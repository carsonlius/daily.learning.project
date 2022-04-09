package com.carsonlius.handler;


import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class XxlJobExecutorHandler {

    private static Logger logger = LoggerFactory.getLogger(XxlJobExecutorHandler.class);


    @XxlJob("requestUserJobHandler")
    public void requestUserJobHandler(){
        logger.info("xxl-job request user list");
        XxlJobHelper.log("hello xxl-job request user list");
    }
}
