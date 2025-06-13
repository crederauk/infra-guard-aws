@aws
Feature: Development hosts are isolated from production hosts

  Scenario Outline: Hosts within development environment can
  successfully ping each other
    When host <HostA> pings host <HostB> with timeout 9 seconds
    Then the ping is successful

    Examples:
      | HostA                 | HostB           |
      | 'i-0ef8af428ece8e86d' | '172.31.24.6'   |
      | 'i-040d60025bde0fe7b' | '172.31.27.178' |

  Scenario Outline: Development hosts cannot ping production
  hosts
    When host <HostA> pings host <HostB> with timeout 9 seconds
    Then the ping is failed

    Examples:
      | HostA                 | HostB          |
      | 'i-0ef8af428ece8e86d' | '34.243.888.5' |
      | 'i-040d60025bde0fe7b' | '34.243.888.6' |
