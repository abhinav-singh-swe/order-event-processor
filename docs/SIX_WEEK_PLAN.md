# Six-Week Build Plan

The repo is scoped so the whole thing fits in ~30-40 hours of weekend work.
Don't rush ahead — finishing each week and pushing a Git tag matters more
than feature breadth.

## Week 1 — AWS account + EC2 baseline

**Goal:** Spring Boot producer running on a public EC2, reachable via curl.

- Create a fresh AWS account just for this project.
- Set up an **AWS Budget alarm at $5/month** — single most important step.
- Install AWS CLI locally, `aws configure` with a least-privilege IAM user.
- Provision one EC2 t2.micro manually from the console.
- Build the producer jar (`cd producer && mvn package`).
- SCP the jar onto the EC2, run `java -jar`, and `curl` against the public IP.

**Tag:** `v0.1-ec2-baseline`

## Week 2 — S3 + IAM hardening

**Goal:** Producer writes order payloads to S3 using an EC2 instance role,
not access keys.

- Create the S3 audit bucket from the console.
- Create the producer IAM role with a least-privilege policy (S3 PutObject
  on `bucket/orders/*` only).
- Attach the role to the EC2 instance.
- Update producer code to write to S3 (already in this repo's `OrderService`).
- Verify a posted order shows up in S3 within seconds.

**Tag:** `v0.2-s3-audit`

## Week 3 — RDS MySQL

**Goal:** Replace local MySQL with managed RDS, connect the producer.

- Provision an RDS MySQL `db.t3.micro` from the console.
- Lock down its security group so only the EC2's SG can reach 3306.
- Run `terraform/schema.sql` against the instance to create the `orders` table.
- Update producer config to point at the RDS endpoint.

**Tag:** `v0.3-rds`

## Week 4 — SQS + Lambda consumer

**Goal:** Producer publishes to SQS; Lambda consumes and writes to RDS.

- Create the SQS main queue + DLQ from the console.
- Build the consumer Lambda jar (`cd consumer && mvn package`).
- Upload to a Lambda function manually; wire SQS as the event source.
- Test end-to-end: `curl` the producer → see a row in RDS within ~5s.

**Tag:** `v0.4-sqs-lambda`

## Week 5 — CloudWatch metrics + alarm

**Goal:** Observability — custom metrics from Lambda + one alarm.

- Confirm the `OrdersPersisted` and `OrderProcessingFailures` metrics show
  up in CloudWatch.
- Create one alarm: failures > 0 in any 1-minute window.
- Hook the alarm to your email via SNS topic.
- Write a "Force a failure" test in the README to prove the alarm fires.

**Tag:** `v0.5-observability`

## Week 6 — Terraform-ize everything

**Goal:** Tear it all down, rebuild from `terraform apply` in one command.

- Run `terraform destroy` on each manually-created resource (or just delete
  them from the console).
- `cp terraform/terraform.tfvars.example terraform.tfvars`, set the
  DB password.
- `cd terraform && terraform init && terraform apply`.
- Verify the same end-to-end test works on the Terraform-provisioned stack.
- Push the final repo + a polished README.

**Tag:** `v1.0`

## After v1.0 — optional stretch goals

- Add a GitHub Actions workflow that builds, tests and deploys on every PR
  to `main` (already scaffolded in `.github/workflows/`).
- Replace EC2 with **AWS Lambda** for the producer too (fully serverless).
- Add **API Gateway** in front of the producer for HTTPS + throttling.
- Add a **dead-letter handler Lambda** that pushes DLQ items to S3 +
  triggers an alert.
- Migrate to **AWS CDK** instead of Terraform as a second IaC reference point.

## How to talk about this in interviews

Pick **one design decision per week** and rehearse a 60-second explanation:

- *Why SQS and not Kafka?* — managed, no broker to operate, perfect for
  request-decoupling at this scale; Kafka would be over-engineered.
- *Why Lambda and not ECS?* — pure event-driven workload, no persistent
  connection state, Free Tier covers it.
- *Why a DLQ with maxReceiveCount=3?* — bounded retries, then poison-pill
  isolation so one bad message doesn't block the queue.
- *Why per-service IAM roles?* — blast-radius containment; if the producer
  is compromised, attacker can't touch the DB or CloudWatch.
- *Why Terraform and not CloudFormation?* — provider-agnostic, cleaner
  state model, more transferable across employers.

That's the kind of crisp reasoning that separates "built a tutorial" from
"engineered a system."
