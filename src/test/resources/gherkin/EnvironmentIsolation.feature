Feature: Development hosts are isolated from production hosts

  Scenario Outline: Hosts within development environment can
  successfully ping each other
    When host <HostA> pings host <HostB> with timeout 9 seconds
    Then the ping is successful

    Examples:
      | HostA                 | HostB           |
      | 'i-0cc281de2237fec29' | '192.168.0.23'  |
      | 'i-00447101c780099fe' | '192.168.0.247' |

  Scenario Outline: Development hosts cannot ping production
  hosts
    When host <HostA> pings host <HostB> with timeout 9 seconds
    Then the ping is failed

    Examples:
      | HostA                 | HostB           |
      | 'i-0cc281de2237fec29' | '34.243.888.5'  |
      | 'i-00447101c780099fe' | '34.243.888.6'  |
