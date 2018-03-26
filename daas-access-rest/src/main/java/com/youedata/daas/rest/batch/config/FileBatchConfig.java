package com.youedata.daas.rest.batch.config;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.youedata.daas.rest.batch.listener.DaasJobExecutionListener;
import com.youedata.daas.rest.batch.reader.FileItemReader;
import com.youedata.daas.rest.batch.reader.MultiFieldSetMapper;
import com.youedata.daas.rest.batch.reader.WrappedJsonLineMapper;
import com.youedata.daas.rest.batch.resource.DaasResource;
import com.youedata.daas.rest.batch.writer.TableCurserWriter;
import com.youedata.daas.rest.common.DateUtil;
import com.youedata.daas.rest.common.JedisClient;
import com.youedata.daas.rest.common.enums.FileType;
import com.youedata.daas.rest.exception.BizExceptionEnum;
import com.youedata.daas.rest.exception.BussinessException;
import com.youedata.daas.rest.modular.controller.TaskController;
import com.youedata.daas.rest.modular.model.vo.FtpDsInfoVo;
import com.youedata.ftppool.FtpClientPoolManager;
import com.youedata.ftppool.client.ApachePoolFTPClientImpl;
import com.youedata.ftppool.client.FtpInfo;
import com.youedata.ftppool.client.IFTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.separator.JsonRecordSeparatorPolicy;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by cdyoue on 2018/1/8.
 */
@Configuration
public class FileBatchConfig {
    protected static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    @Autowired
    private DaasJobExecutionListener jobListener;
    @Value("${batch_file_chunk}")
    private Integer chunk;
    private JobBuilderFactory jobBuilderFactory;
    private StepBuilderFactory stepBuilderFactory;
    @Autowired
    private FtpClientPoolManager ftpClientPoolManager;

    @Autowired
    private JedisClient jedisClient;

    public FileBatchConfig(JobBuilderFactory jobBuilderFactory, StepBuilderFactory stepBuilderFactory) {
        this.jobBuilderFactory = jobBuilderFactory;
        this.stepBuilderFactory = stepBuilderFactory;
    }

    @Bean
    public Job fileJob(Step fileStep) {
        return jobBuilderFactory.get("fileJob")
                .listener(jobListener)
                .start(fileStep)
                .build();
    }

    @Bean
    @JobScope
    public Step fileStep(
            @Value("#{jobParameters['dsInfo']}") String dsInfo,
            @Value("#{jobParameters['fromColumn']}") String fromColumn,
            @Value("#{jobParameters['resName']}") String resName,
            @Value("#{jobParameters['tableName']}") String tableName,
            @Value("#{jobParameters['to']}") String to,
            @Value("#{jobParameters['accessType']}") String accessType,
            @Value("#{jobParameters['taskId']}") String taskId
    ) {
        return stepBuilderFactory.get("ftpStep")
                .<JSONObject, JSONObject>chunk(chunk)
                .reader(multiFlatFileReader(null, null, null, null, null))
                //.processor()
                .writer(ftpItemWriter(tableName, to, accessType, taskId))
                .allowStartIfComplete(true)
                .build();
    }

    @Bean
    @StepScope
    public MultiResourceItemReader<JSONObject> multiFlatFileReader(
            @Value("#{jobParameters['dsInfo']}") String dsInfo,
            @Value("#{jobParameters['fromColumn']}") String fromColumn,
            @Value("#{jobParameters['taskId']}") String taskId,
            @Value("#{jobParameters['fileType']}") Integer fileType,
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        MultiResourceItemReader<JSONObject> itemReader = new MultiResourceItemReader<JSONObject>();
        IFTPClient ftpClient = null;
        try {
            FtpDsInfoVo ds = JSON.parseObject(dsInfo, FtpDsInfoVo.class);
            if (ds == null) {
                throw new BussinessException(BizExceptionEnum.FTP_DS_ERROR);
            }
            FtpInfo info = new FtpInfo(ds.getUrl(), ds.getPort(), ds.getUserName(), ds.getPassword());
            try {
                logger.info("开始获取ftpClient。。。。。。");
                ftpClient = new ApachePoolFTPClientImpl(ftpClientPoolManager.getFtpClient(info));
                ftpClient.setFtpClientManager(ftpClientPoolManager);
                ftpClient.setInfo(info);
                logger.info("获取的ftpClient = " + ftpClient);
            } catch (Exception e) {
                throw new BussinessException(BizExceptionEnum.TASK_START_ERROR);
            }

            FTPFile[] ftpFiles = ftpClient.listFiles(filePath);
            Resource[] resources = new Resource[ftpFiles.length];
            for (int i = 0; i < ftpFiles.length; i++) {
                if (ftpFiles[i].isFile()) {
                    try {
                        String name = ftpFiles[i].getName();
                        InputStream inputStream = ftpClient.read(filePath + "/" + name);
                        Resource resource = new DaasResource(inputStream, "", name);
                        resources[i] = resource;
                        logger.info("开始调用ftpClient.completeTransFile(), beginTime=" + DateUtil.getTime());
                        ftpClient.completeTransFile();
                        logger.info("结束调用ftpClient.completeTransFile(), endTime=" + DateUtil.getTime());
                    } catch (Exception e) {
                        throw new BussinessException(BizExceptionEnum.TASK_START_ERROR);
                    }
                }
            }

            itemReader.setResources(resources);
            if (FileType.XML.getType().equals(fileType)) {
                //itemReader.setDelegate(xmlFileTtemReader(null));
            } else {
                itemReader.setDelegate(flatFileItemReader(null, null, null, null, null, null));
            }
        } catch (Exception e) {
            logger.error("读取异常", e);
        } finally {
            if (itemReader != null) {
                itemReader.close();
            }
            if (ftpClient != null) {
                try {
                    ftpClient.close();
                } catch (IOException e) {
                    logger.error("关闭ftp客户端失败:", e);
                }
            }
        }
        return itemReader;
    }

    @Bean
    @StepScope
    public FileItemReader flatFileItemReader(
            @Value("#{jobParameters['dsInfo']}") String dsInfo,
            @Value("#{jobParameters['fromColumn']}") String fromColumn,
            @Value("#{jobParameters['header']}") Long header,
            @Value("#{jobParameters['separator']}") String separator,
            @Value("#{jobParameters['fileType']}") Integer fileType,
            @Value("#{jobParameters['filePath']}") String filePath
    ) {
        FileItemReader itemReader = new FileItemReader();
        try {
            if (fileType.equals(FileType.CSV.getType()) || fileType.equals(FileType.TXT.getType())) {
                logger.info("读取csv或txt文件....,path=" + filePath);
                DefaultLineMapper<JSONObject> lineMapper = new DefaultLineMapper<>();
                DelimitedLineTokenizer delimitedLineTokenizer = new DelimitedLineTokenizer();
                delimitedLineTokenizer.setDelimiter(separator);//设置分隔符
                lineMapper.setLineTokenizer(delimitedLineTokenizer);
                lineMapper.setFieldSetMapper(new MultiFieldSetMapper(fromColumn));
                itemReader.setLineMapper(lineMapper);
                if (1L == header) {
                    itemReader.setLinesToSkip(1);
                }
                return itemReader;
            } else if (fileType.equals(FileType.JSON.getType())) {
                logger.info("读取json文件....,path=" + filePath);
                itemReader.setRecordSeparatorPolicy(new JsonRecordSeparatorPolicy());
                itemReader.setLineMapper(new WrappedJsonLineMapper(fromColumn));
                return itemReader;
            }
        } finally {
            if (itemReader != null) {
                itemReader.close();
            }
        }
        return null;
    }

    @Bean
    @StepScope
    public TableCurserWriter ftpItemWriter(
            @Value("#{jobParameters['mtableName']}") String tableName,
            @Value("#{jobParameters['to']}") String to,
            @Value("#{jobParameters['accessType']}") String accessType,
            @Value("#{jobParameters['taskId']}") String taskId
    ) {
        return new TableCurserWriter(null, tableName, 0, to, accessType, taskId);
    }

    @Bean
    public DaasJobExecutionListener jobListener1() {
        return new DaasJobExecutionListener();
    }
}