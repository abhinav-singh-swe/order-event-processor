// Glean Generated Code
package com.abhinav.orderprocessor.service;

import com.abhinav.orderprocessor.model.OrderEvent;
import com.abhinav.orderprocessor.model.OrderRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

@Service
public class OrderService {

    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    private final SqsClient sqs;
    private final S3Client s3;
    private final ObjectMapper mapper;
    private final String queueUrl;
    private final String auditBucket;

    public OrderService(SqsClient sqs,
                        S3Client s3,
                        ObjectMapper mapper,
                        @Value("${app.sqs.queue-url}") String queueUrl,
                        @Value("${app.s3.audit-bucket}") String auditBucket) {
        this.sqs = sqs;
        this.s3 = s3;
        this.mapper = mapper;
        this.queueUrl = queueUrl;
        this.auditBucket = auditBucket;
    }

    public OrderEvent accept(OrderRequest request) {
        OrderEvent event = OrderEvent.from(request);
        try {
            String json = mapper.writeValueAsString(event);
            // 1. Write the raw payload to S3 for audit / replay.
            s3.putObject(PutObjectRequest.builder()
                            .bucket(auditBucket)
                            .key("orders/" + event.getOrderId() + ".json")
                            .build(),
                    RequestBody.fromString(json));
            // 2. Publish the normalized event to SQS.
            sqs.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(json)
                    .build());
        } catch (Exception ex) {
            log.error("failed to publish order {}", event.getOrderId(), ex);
            throw new RuntimeException("publish failed", ex);
        }
        return event;
    }
}
