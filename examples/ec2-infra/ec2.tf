locals {
  tags = {
    Name      = "infra-guarded-app-server"
    nonuke    = true
    CreatedOn = "2024-04-26"
    CreatedBy = "vidya.kharje"
  }
}

resource "aws_instance" "example" {
  ami           = "ami-04e5276ebb8451442"
  instance_type = "t3.micro"
  tags          = local.tags
  volume_tags   = local.tags
}
