# Glean Generated Code
# Small Free-Tier-friendly MySQL instance.
# Destroy this with `terraform destroy` when not in use to stay inside Free Tier.
resource "aws_db_instance" "mysql" {
  identifier             = "${var.project}-mysql"
  engine                 = "mysql"
  engine_version         = "8.0"
  instance_class         = "db.t3.micro"
  allocated_storage      = 20
  storage_type           = "gp2"
  username               = var.db_user
  password               = var.db_password
  db_name                = "orders"
  skip_final_snapshot    = true
  publicly_accessible    = false
  deletion_protection    = false
  apply_immediately      = true

  tags = local.tags
}
