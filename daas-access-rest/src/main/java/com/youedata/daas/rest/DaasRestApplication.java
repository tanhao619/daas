package com.youedata.daas.rest;

import cn.aofeng.threadpool4j.ThreadPoolManager;
import com.youedata.ftppool.FtpClientPoolManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.web.client.RestTemplate;

@EnableBatchProcessing
@EnableRetry
@SpringBootApplication(scanBasePackages = {"com.youedata.daas"})
public class DaasRestApplication  implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(DaasRestApplication.class);
    @Autowired
    FtpClientPoolManager ftpClientPoolManager;
    @Bean
    public RestTemplate template(){
        return new RestTemplate();
    }
    public static void main(String[] args){
        SpringApplication app = new SpringApplication(DaasRestApplication.class);
        Environment env = app.run(args).getEnvironment();
        logger.info(
                "\n----------------------------------------------------------\n\t"
                        + "Application is running! Access URLs:\n\t" + "Local: \t\thttp://127.0.0.1:{}/{}\n\t"
                        + "\n----------------------------------------------------------",
                env.getProperty("server.port"),"swagger-ui.html");
    }
    @Override
    public void run(String... strings) throws Exception {
        ThreadPoolManager tpm = ThreadPoolManager.getSingleton();
        tpm.init();
        ftpClientPoolManager.init();
    }
}
