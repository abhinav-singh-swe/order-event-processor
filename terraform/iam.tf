# Glean Generated Code
# ----- Producer role (used when the producer runs on EC2 / ECS) -----
data "aws_iam_policy_document" "producer_assume" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["ec2.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "producer" {
  name               = "${var.project}-producer-role"
  assume_role_policy = data.aws_iam_policy_document.producer_assume.json
  tags               = local.tags
}

data "aws_iam_policy_document" "producer_perms" {
  statement {
    actions   = ["sqs:SendMessage", "sqs:GetQueueAttributes"]
    resources = [aws_sqs_queue.orders.arn]
  }
  statement {
    actions   = ["s3:PutObject"]
    resources = ["${aws_s3_bucket.audit.arn}/orders/*"]
  }
}

resource "aws_iam_role_policy" "producer" {
  name   = "${var.project}-producer-policy"
  role   = aws_iam_role.producer.id
  policy = data.aws_iam_policy_document.producer_perms.json
}

# ----- Consumer role (Lambda) -----
data "aws_iam_policy_document" "consumer_assume" {
  statement {
    actions = ["sts:AssumeRole"]
    principals {
      type        = "Service"
      identifiers = ["lambda.amazonaws.com"]
    }
  }
}

resource "aws_iam_role" "consumer" {
  name               = "${var.project}-consumer-role"
  assume_role_policy = data.aws_iam_policy_document.consumer_assume.json
  tags               = local.tags
}

resource "aws_iam_role_policy_attachment" "consumer_basic" {
  role       = aws_iam_role.consumer.name
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaBasicExecutionRole"
}

data "aws_iam_policy_document" "consumer_perms" {
  statement {
    actions = [
      "sqs:ReceiveMessage", "sqs:DeleteMessage",
      "sqs:GetQueueAttributes", "sqs:ChangeMessageVisibility"
    ]
    resources = [aws_sqs_queue.orders.arn]
  }
  statement {
    actions   = ["cloudwatch:PutMetricData"]
    resources = ["*"]
  }
}

resource "aws_iam_role_policy" "consumer" {
  name   = "${var.project}-consumer-policy"
  role   = aws_iam_role.consumer.id
  policy = data.aws_iam_policy_document.consumer_perms.json
}
