package com.youedata.daas.rest.batch.listener;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.youedata.daas.rest.common.enums.WarnningType;
import com.youedata.daas.rest.modular.model.DO.CurserDO;
import com.youedata.daas.rest.modular.service.ICurserService;
import com.youedata.daas.rest.modular.service.IWarnningService;
import com.youedata.daas.rest.util.SpringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;

/**
 * Created by cdyoue on 2018/1/8.
 */
public class TableItemListener extends StepExecutionListenerSupport {
    protected Logger logger = LoggerFactory.getLogger(TableItemListener.class);
    private ICurserService curserService = (ICurserService) SpringUtil.getBean("curserServiceImpl");
    private IWarnningService warnningService = (IWarnningService) SpringUtil.getBean("warnningServiceImpl");
    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        String tableName = stepExecution.getJobParameters().getString("tableName");
        Long taskId = stepExecution.getJobParameters().getLong("taskId");
        int writeCount = stepExecution.getWriteCount();
        try {
            CurserDO curserDO = curserService.selectOne(new EntityWrapper<CurserDO>().eq("tableName", tableName)
                    .eq("taskId", taskId));
            if (curserDO!=null){
                curserDO.setRowNum(curserDO.getRowNum()+writeCount);
            }else {
                curserDO = new CurserDO();
                curserDO.setRowNum(writeCount);
                curserDO.setTaskId(taskId.intValue());
                curserDO.setTableName(tableName);
                curserDO.setRowNum(writeCount);
            }
            curserService.insertOrUpdate(curserDO);
            if (writeCount == 0){
                Long did = stepExecution.getJobParameters().getLong("taskId");
                warnningService.insertWarning(did.intValue(), WarnningType.NOT_IMPLEMENTED_DURING_CYCLE.getType());
            }
        } catch (Exception e) {
            logger.error("{}", e);
        }

        return stepExecution.getExitStatus();
    }
}
