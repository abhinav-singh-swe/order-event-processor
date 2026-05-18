# Glean Generated Code
terraform {
  required_version = ">= 1.6"
  required_providers {
    aws = {
      source  = "hashicorp/aws"
      version = "~> 5.40"
    }
  }
}

provider "aws" {
  region = var.region
}

# ----- S3 audit bucket -----
resource "aws_s3_bucket" "audit" {
  bucket        = "${var.project}-audit-${random_id.suffix.hex}"
  force_destroy = true
  tags          = local.tags
}

resource "aws_s3_bucket_public_access_block" "audit" {
  bucket                  = aws_s3_bucket.audit.id
  block_public_acls       = true
  block_public_policy     = true
  ignore_public_acls      = true
  restrict_public_buckets = true
}

resource "random_id" "suffix" {
  byte_length = 4
}

# ----- SQS main queue + DLQ -----
resource "aws_sqs_queue" "orders_dlq" {
  name                       = "${var.project}-orders-dlq"
  message_retention_seconds  = 1209600  # 14 days
  tags                       = local.tags
}

resource "aws_sqs_queue" "orders" {
  name                       = "${var.project}-orders"
  visibility_timeout_seconds = 60
  message_retention_seconds  = 345600   # 4 days
  redrive_policy = jsonencode({
    deadLetterTargetArn = aws_sqs_queue.orders_dlq.arn
    maxReceiveCount     = 3
  })
  tags = local.tags
}

locals {
  tags = {
    Project = var.project
    Owner   = "abhinav"
  }
}
