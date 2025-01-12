package com.fit2cloud.quartz;

import com.fit2cloud.base.service.IBaseSystemParameterService;
import com.fit2cloud.common.constants.ParamConstants;
import com.fit2cloud.common.log.utils.LogUtil;
import com.fit2cloud.es.entity.OperatedLog;
import com.fit2cloud.es.entity.SystemLog;
import com.fit2cloud.service.ILogService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @author jianneng
 * @date 2022/12/11 17:25
 **/
@Component
public class LogCleanJob {
    @Resource
    private IBaseSystemParameterService baseSystemParameterService;
    @Resource
    private ILogService logService;

    @Scheduled(cron = "0 30 1 * * ?")
    public void cleanApiLog() {
        String operatedLogValue = baseSystemParameterService.getValue(ParamConstants.Log.KEEP_API_MONTHS.getValue());
        String loginLogValue = baseSystemParameterService.getValue(ParamConstants.Log.KEEP_LOGIN_MONTHS.getValue());
        String systemLogValue = baseSystemParameterService.getValue(ParamConstants.Log.KEEP_SYSTEM_MONTHS.getValue());
        cleanOperatedLog(operatedLogValue, "操作", "ce-file-api-logs", OperatedLog.class);
        cleanOperatedLog(loginLogValue, "登录", "ce-file-api-logs", OperatedLog.class);
        cleanOperatedLog(systemLogValue, "系统", "ce-file-system-logs", SystemLog.class);
    }

    private void cleanOperatedLog(String m, String logType, String index, Class<?> clazz) {
        int months = 3;
        if (StringUtils.isBlank(m)) {
            LogUtil.info("未设置{}日志保存月数，使用默认值: {}", logType, months);
        } else {
            months = Integer.valueOf(m);
        }
        if (months < 1) {
            // if 0
            months = 3;
        }
        if (LogUtil.getLogger().isDebugEnabled()) {
            LogUtil.getLogger().debug("开始清理{}个月前的{}日志.", months, logType);
        }
        logService.deleteEsData(index, months, clazz);
        if (LogUtil.getLogger().isDebugEnabled()) {
            LogUtil.getLogger().debug("{}日志清理完成.", logType);
        }
    }


}
