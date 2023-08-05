package com.yuxin.messaging.scheduler;

import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.sun.management.OperatingSystemMXBean;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;

@Component
@Log4j2
public class ResourceUtilizationMetricMonitor {
    @Autowired
    private AmazonCloudWatch amazonCloudWatch;

    @Scheduled(fixedDelay = 5000)
    public void monitor() {
        OperatingSystemMXBean operatingSystemMXBean = (OperatingSystemMXBean) ManagementFactory.getOperatingSystemMXBean();
        log.info("Process CPU load: {}", operatingSystemMXBean.getProcessCpuLoad());
    }
}
