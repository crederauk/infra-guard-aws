
## Test tool use cases

1. User-Reported Network Issue: Conduct a sanity check to validate normal system operation in response to reported network problems.
2. Infrastructure Outage Assessment: Evaluate the extent of infrastructure unavailability during outages, identifying failing feature files and quantifying the impact.
3. Network Configuration Change: Re-execute a sanity check after modifying network configurations to ensure continued proper functionality.
4. Network Configuration Experimentation: Iteratively perform sanity checks to validate the functionality of various network configurations during experimentation.
5. Department Onboarding to Landing Zone: Validate existing network integrity and ensure the intended functionality of new networking configurations when onboarding a new department to a Landing Zone.
6. Deployment of New Host (e.g., Bastion): Automatically confirm the correct configuration of public keys for SSH access upon deploying a new host.
7. Security Auditing: Scrutinize feature files to ensure adherence to the principle of least privilege in granting access, and update and rerun features if necessary during security audits.

## Categorisation of testing scenarios

- Network access from HostA to HostB
- Host metrics
- Host attributes

### Network access from HostA to HostB

- Ping (ICMP)
- Access a port (TCP)
- Max number of hopes (ICMP)
- Max latency (ICMP)

Example scenarios:

- Development instances should not be able to access production instances directly and vice versa
- Development instances of one department should be able to access other development instances in the same department but not in other departments
- Hop count should not exceed 3 and latency should not exceed 2 seconds when application server connects to the database server

### Host metrics

- Disk
- Network
- I/O
- CPU
- RAM
- Process
- Temperature and Fan Speed

Example scenarios:

- Production instances must have at least 10GB free disk storage available

### Host attributes

- Presence/absence of a file
- Presence/absence of a package and its version
- Presence/absence of file content
- File checksum

Example scenarios:

* SSH public key is configured for SSH access
