package com.yuxin.messaging.aspect;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import com.amazonaws.services.cloudwatch.model.Dimension;
import com.amazonaws.services.cloudwatch.model.MetricDatum;
import com.amazonaws.services.cloudwatch.model.PutMetricDataRequest;
import com.amazonaws.services.cloudwatch.model.StandardUnit;
import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Aspect
@Component
@Log4j2
@Order(1)
public class LogAspect {
    @Autowired
    private AmazonCloudWatch cloudWatch;

    @Around("execution(* com.yuxin.messaging.controller.*.*(..))")
    public Object log(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        Date start = new Date();
        boolean exceptionThrown = false;
        String className = proceedingJoinPoint.getSignature().getDeclaringType().getSimpleName();
        String methodName = proceedingJoinPoint.getSignature().getName();
        try {
            return proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            exceptionThrown = true;
            throw throwable;
        } finally {
            Date end = new Date();
            long duration = end.getTime() - start.getTime();
            log.info("Executed {}.{} in {} ms, exception thrown: {}",
                    className, methodName, duration, exceptionThrown);

            List<Dimension> dimensions = List.of(new Dimension()
                                                        .withName("ClassName")
                                                        .withValue(className),
                                                new Dimension()
                                                        .withName("MethodName")
                                                        .withValue(methodName));

            MetricDatum countDatum = new MetricDatum()
                    .withDimensions(dimensions)
                    .withMetricName("Count")
                    .withUnit(StandardUnit.Count)
                    .withValue(1.0);

            MetricDatum latencyDatum = new MetricDatum()
                    .withDimensions(dimensions)
                    .withMetricName("Latency")
                    .withUnit(StandardUnit.Milliseconds)
                    .withValue((double) duration);

            MetricDatum errorDatum = new MetricDatum()
                    .withDimensions(dimensions)
                    .withMetricName("Error")
                    .withUnit(StandardUnit.Count)
                    .withValue(exceptionThrown ? 1.0 : 0.0);

            cloudWatch.putMetricData(new PutMetricDataRequest()
                    .withMetricData(countDatum, latencyDatum, errorDatum)
                    .withNamespace("MessagingService"));
        }
    }
}
