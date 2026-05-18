# Order Event Processor

> A small, production-style event-driven order processing service on AWS.
> Built to demonstrate end-to-end ownership of a cloud-native backend system:
> REST ingestion → async queue → serverless processing → durable storage →
> observability and least-privilege IAM, all provisioned via Terraform.

![Architecture](docs/architecture.png)

## Why this project exists

Most "AWS hello world" projects (single Lambda + S3) don't show the engineering
that matters in real systems: how requests are accepted, validated, queued,
processed asynchronously, persisted, monitored, and secured. This repo is
deliberately scoped to look like a smaller version of a real production
service — small enough to finish in ~6 weekends, complete enough to defend in
an interview.

## Architecture
