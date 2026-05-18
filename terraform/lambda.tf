# Glean Generated Code
# Build the consumer jar with:  cd ../consumer && mvn -q clean package
# The shaded jar is at consumer/target/order-consumer.jar
resource "aws_lambda_function" "consumer" {
  function_name = "${var.project}-consumer"
  role          = aws_iam_role.consumer.arn
  runtime       = "java17"
  handler       = "com.abhinav.orderconsumer.OrderConsumerHandler::handleRequest"
  filename      = "${path.module}/../consumer/target/order-consumer.jar"
  source_code_hash = filebase64sha256("${path.module}/../consumer/target/order-consumer.jar")
  memory_size   = 512
  timeout       = 30

  environment {
    variables = {
      DB_URL      = var.db_url
      DB_USER     = var.db_user
      DB_PASSWORD = var.db_password
    }
  }

  tags = local.tags
}

resource "aws_lambda_event_source_mapping" "sqs_to_lambda" {
  event_source_arn = aws_sqs_queue.orders.arn
  function_name    = aws_lambda_function.consumer.arn
  batch_size       = 10
}

# ----- CloudWatch alarm: surface processing failures fast -----
resource "aws_cloudwatch_metric_alarm" "failures" {
  alarm_name          = "${var.project}-order-processing-failures"
  comparison_operator = "GreaterThanThreshold"
  evaluation_periods  = 1
  period              = 60
  statistic           = "Sum"
  threshold           = 0
  namespace           = "OrderEventProcessor"
  metric_name         = "OrderProcessingFailures"
  treat_missing_data  = "notBreaching"
  alarm_description   = "Order consumer Lambda reported at least one failure in the last minute"
}
