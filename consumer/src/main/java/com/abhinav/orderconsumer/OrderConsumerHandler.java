// Glean Generated Code
package com.abhinav.orderconsumer;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.MetricDatum;
import software.amazon.awssdk.services.cloudwatch.model.PutMetricDataRequest;
import software.amazon.awssdk.services.cloudwatch.model.StandardUnit;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

public class OrderConsumerHandler implements RequestHandler<SQSEvent, Void> {

    private static final ObjectMapper MAPPER = new ObjectMapper().registerModule(new JavaTimeModule());
    private static final String NAMESPACE = "OrderEventProcessor";

    private final CloudWatchClient cw = CloudWatchClient.builder()
            .region(Region.of(System.getenv().getOrDefault("AWS_REGION", "ap-south-1")))
            .build();

    @Override
    public Void handleRequest(SQSEvent event, Context context) {
        for (SQSEvent.SQSMessage msg : event.getRecords()) {
            try {
                OrderRecord rec = MAPPER.readValue(msg.getBody(), OrderRecord.class);
                persist(rec);
                publishMetric("OrdersPersisted", 1.0);
                context.getLogger().log("persisted order " + rec.orderId);
            } catch (Exception ex) {
                publishMetric("OrderProcessingFailures", 1.0);
                context.getLogger().log("failed to persist order: " + ex.getMessage());
                throw new RuntimeException(ex);  // lets SQS retry / DLQ
            }
        }
        return null;
    }

    private void persist(OrderRecord rec) throws Exception {
        String url = System.getenv("DB_URL");
        String user = System.getenv("DB_USER");
        String pass = System.getenv("DB_PASSWORD");
        try (Connection conn = DriverManager.getConnection(url, user, pass);
             PreparedStatement ps = conn.prepareStatement(
                "INSERT INTO orders(order_id, customer_id, sku, quantity, price_cents, total_cents, created_at) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)")) {
            ps.setString(1, rec.orderId);
            ps.setString(2, rec.customerId);
            ps.setString(3, rec.sku);
            ps.setInt(4, rec.quantity);
            ps.setLong(5, rec.priceCents);
            ps.setLong(6, rec.totalCents);
            ps.setTimestamp(7, Timestamp.from(rec.createdAt));
            ps.executeUpdate();
        }
    }

    private void publishMetric(String name, double value) {
        cw.putMetricData(PutMetricDataRequest.builder()
                .namespace(NAMESPACE)
                .metricData(MetricDatum.builder()
                        .metricName(name)
                        .unit(StandardUnit.COUNT)
                        .value(value)
                        .build())
                .build());
    }
}
