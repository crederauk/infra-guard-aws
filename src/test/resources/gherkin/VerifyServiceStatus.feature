Feature: Service status on a specified hosts

  Scenario Outline: Services are running on the specified hosts
  hosts
    When service status on host <Host> is active for service <ServiceName> with timeout 9 seconds
    Then the service is running

    Examples:
      | Host                | ServiceName       |
      | 'i-0ef8af428ece8e86d' | 'sshd' |
      | 'i-040d60025bde0fe7b' | 'sshd' |
