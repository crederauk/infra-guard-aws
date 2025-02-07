Feature: Ensure specified services are running on specified hosts

  Scenario Outline: Desired services are running on specified hosts
    Given a list of specified hosts and services
    When checking service status on host <Host> for service <ServiceName> with timeout of 9 seconds

    Examples:
      | Host                | ServiceName       |
      | 'i-0ef8af428ece8e86d' | 'sshd' |
      | 'i-040d60025bde0fe7b' | 'sshd' |
