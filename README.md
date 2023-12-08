
# infra-guard-aws

Test Automation Tool for AWS Infrastructure

## Configure AWS CLI profile

Install the AWS Command Line Interface (AWS CLI)

    brew install awscli # macOS
    sudo pacman -S aws-cli-v2 # Arch Linux

Verify the version of AWS CLI

    aws --version

Configure CLI profile in `~/.aws/config`

    aws configure sso

Verify the above profile

    aws s3 ls --profile AdministratorAccess-100000000000

If you get the following error

    Error when retrieving token from sso: Token has expired and refresh failed

Then, login to SSO account and try again

    aws sso login --profile AdministratorAccess-100000000000
