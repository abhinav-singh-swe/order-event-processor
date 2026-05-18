# Glean Generated Code
output "orders_queue_url" {
  value = aws_sqs_queue.orders.url
}

output "audit_bucket" {
  value = aws_s3_bucket.audit.bucket
}

output "mysql_endpoint" {
  value = aws_db_instance.mysql.endpoint
}

output "consumer_function" {
  value = aws_lambda_function.consumer.function_name
}
