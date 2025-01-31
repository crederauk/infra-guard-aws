
# Infra Guard AWS

Test Automation Tool for AWS Infrastructure

## Configure AWS CLI profile

Install the AWS Command Line Interface (AWS CLI)

    brew install awscli # macOS
    sudo pacman -S aws-cli-v2 # Arch Linux

Verify the version of AWS CLI

    aws --version

Configure CLI profile in `~/.aws/config` (for non-SSO profile options refer to [1])

    aws configure sso

Verify the above profile

    aws s3 ls --profile AdministratorAccess-100000000000

If you get the following error

    Error when retrieving token from sso: Token has expired and refresh failed

Then, login to SSO account and try again

    aws sso login --profile AdministratorAccess-100000000000

## Configure application

Add the profile configured in [Configure AWS CLI profile](#configure-aws-cli-profile) and the AWS Region to `application.yml`, for example:

```yaml
aws:
  profile: AdministratorAccess-100000000000
  region: eu-west-1
```

## Add Gherkin feature files

Gherkin feature files are located within the `src/test/resources/gherkin` directory. Using the existing feature files for reference, create feature files to describe your own AWS infrastructure.

Refer to [Test tool use cases](use-cases.md) for further example scenarios.

## Usage

* Test your AWS infrastructure by typing

      AWS_PROFILE=AdministratorAccess-100000000000 ./gradlew clean test --info

* Test your AWS infra with container

Use the following Docker commands if you don't want to set up Gradle and Java but still want to execute test cases locally. Run these commands from the project's root directory, where the Dockerfile exists. Set the EXTENT_REPORT_PATH environment variable to the path on your local machine where you want to store test reports. Ensure that your AWS account access is set up locally and that the credentials file exists in the .aws folder.

    docker build -t infra-guard .
    docker run -it -v $EXTENT_REPORT_PATH/extent-report:/app/reports/extent-report  -v ~/.aws:/root/.aws:ro infra-guard:latest

First volume mount is to make sure reports are available in host machine for your reference.

Second volume mount is to use aws sso token generated in step **Configure AWS CLI profile** for executing test cases. 

## Use Cases

[Test tool use cases](use-cases.md)

## References

1. For instructions on how to setup an AWS Profile refer to https://docs.aws.amazon.com/cli/latest/userguide/cli-configure-files.html#cli-configure-files-format-profile