package com.yuxin.messaging.configuration;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.services.cloudwatch.AmazonCloudWatch;
import com.amazonaws.services.cloudwatch.AmazonCloudWatchClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CloudWatchConfiguration {

    @Autowired
    private AWSCredentialsProvider credentialsProvider;

    @Value("${cloud.aws.region.static}")
    private String awsRegion;

    @Bean
    public AmazonCloudWatch amazonCloudWatch() {
        return AmazonCloudWatchClientBuilder.standard()
                .withCredentials(this.credentialsProvider)
                .withRegion(this.awsRegion)
                .build();
    }
}
