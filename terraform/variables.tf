# Glean Generated Code
variable "region" {
  type    = string
  default = "ap-south-1"
}

variable "project" {
  type    = string
  default = "order-event-processor"
}

variable "db_user" {
  type    = string
  default = "orderapp"
}

variable "db_password" {
  type      = string
  sensitive = true
}

variable "db_url" {
  type        = string
  description = "JDBC URL passed to the consumer Lambda. Set after RDS is up."
  default     = ""
}
