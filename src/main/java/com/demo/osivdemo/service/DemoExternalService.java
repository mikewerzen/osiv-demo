package com.demo.osivdemo.service;

import com.demo.osivdemo.util.SessionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DemoExternalService {

    private static final Logger logger = LoggerFactory.getLogger(DemoExternalService.class);

    private final SessionUtils sessionUtils;

    public DemoExternalService(SessionUtils sessionUtils) {
        this.sessionUtils = sessionUtils;
    }

    public void pretendToMakeRestCall() {
        logger.info("Making external call!");

        int activeConnections = sessionUtils.getActiveConnections();
        if (activeConnections > 0) {
            logger.warn("USING MIXED IOs! Bad!");
        }
        sessionUtils.logActiveConnections();

        try {
            logger.info("Sleeping to simulate HTTP call");
            Thread.sleep(500);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        logger.info("Done making external call!");
    }

}
