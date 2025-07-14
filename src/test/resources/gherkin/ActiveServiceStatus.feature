@aws
Feature: Verify service status is active on specified hosts

  Background:
    Given a timeout of 9 seconds

  Scenario Outline: Check if the service is active on target hosts
    Given the service <ServiceName> on host <Host>
    When the service status is retrieved
    Then the service status must be active

    Examples:
      | ServiceName | Host                  |
      | 'sshd'      | 'i-0ef8af428ece8e86d' |
      | 'sshd'      | 'i-040d60025bde0fe7b' |
