Feature: Service status on a specified hosts

  Scenario Outline: Services are running on the specified hosts
  hosts
    When host <Host> checks  service status <ServiceCommand> with timeout 9 seconds
    Then the service is running

    Examples:
      | Host                | ServiceCommand         |
      | 'i-0ef8af428ece8e86d' | 'service sshd status' |
      | 'i-040d60025bde0fe7b' | 'service sshd status' |
