locals {
  instance_tags = {
    instance1 = {
      Name      = "infra-guarded-app-server"
      nonuke    = true
      CreatedOn = "2024-04-26"
      CreatedBy = "vidya.kharje"
    }
    instance2 = {
      Name      = "infra-guarded-db-server"
      nonuke    = true
      CreatedOn = "2024-04-26"
      CreatedBy = "vidya.kharje"
    }
  }
}

resource "aws_instance" "example" {
  count                = 2
  ami                  = "ami-04e5276ebb8451442"
  instance_type        = "t3.micro"
  tags                 = local.instance_tags["instance${count.index + 1}"]
  volume_tags          = local.instance_tags["instance${count.index + 1}"]
  iam_instance_profile = aws_iam_instance_profile.example_instance_profile.name

}

resource "aws_iam_instance_profile" "example_instance_profile" {
  name = "example-instance-profile"
  role = aws_iam_role.ssm_role.name
}
