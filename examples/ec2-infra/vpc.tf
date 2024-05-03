resource "aws_vpc_endpoint" "ssm-access" {
  vpc_id            = "vpc-86ae9dfc"
  service_name      = "com.amazonaws.us-east-1.ssm"
  vpc_endpoint_type = "Interface"

  security_group_ids = [
    "sg-98c165b2",
  ]
  subnet_ids = [
    "subnet-a69e28eb",
    "subnet-f6ff1ca9"

  ]
  private_dns_enabled = true
}