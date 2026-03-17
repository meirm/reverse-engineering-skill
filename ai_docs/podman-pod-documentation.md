# Podman Pod Documentation

## Overview

Podman pods are a set of subcommands that manage pods, or groups of containers. A pod is a collection of one or more containers that share the same network namespace and storage volumes.

## Main Command

### `podman pod`

**Synopsis:** `podman pod` _subcommand_

**Description:** podman pod is a set of subcommands that manage pods, or groups of containers.

## Available Subcommands

| Command | Man Page | Description |
| --- | --- | --- |
| clone | [podman-pod-clone(1)] | Create a copy of an existing pod. |
| create | [podman-pod-create(1)] | Create a new pod. |
| exists | [podman-pod-exists(1)] | Check if a pod exists in local storage. |
| inspect | [podman-pod-inspect(1)] | Display information describing a pod. |
| kill | [podman-pod-kill(1)] | Kill the main process of each container in one or more pods. |
| logs | [podman-pod-logs(1)] | Display logs for pod with one or more containers. |
| pause | [podman-pod-pause(1)] | Pause one or more pods. |
| prune | [podman-pod-prune(1)] | Remove all stopped pods and their containers. |
| ps | [podman-pod-ps(1)] | Print out information about pods. |
| restart | [podman-pod-restart(1)] | Restart one or more pods. |
| rm | [podman-pod-rm(1)] | Remove one or more stopped pods and containers. |
| start | [podman-pod-start(1)] | Start one or more pods. |
| stats | [podman-pod-stats(1)] | Display a live stream of resource usage stats for containers in one or more pods. |
| stop | [podman-pod-stop(1)] | Stop one or more pods. |
| top | [podman-pod-top(1)] | Display the running processes of containers in a pod. |
| unpause | [podman-pod-unpause(1)] | Unpause one or more pods. |

## Pod Creation

### `podman pod create`

**Synopsis:** `podman pod create` [ _options_ ] [ _name_ ]

**Description:** Creates an empty pod, or unit of multiple containers, and prepares it to have containers added to it. The pod can be created with a specific name. If a name is not given a random name is generated. The pod ID is printed to STDOUT. You can then use `podman create --pod <pod_id|pod_name>` … to add containers to the pod, and `podman pod start <pod_id|pod_name>` to start the pod.

**Pod Identification:**
- UUID long identifier ("f78375b1c487e03c9438c729345e54db9d20cfa2ac1fc3494b6eb60872e74778")
- UUID short identifier ("f78375b1c487")
- Name ("jonah")

**Note:** Podman generates a UUID for each pod, and if a name is not assigned to the container with `--name` then a random string name is generated for it. This name is useful to identify a pod.

## Resource Management

Note: Resource limit related flags work by setting the limits explicitly in the pod's cgroup parent for all containers joining the pod. A container can override the resource limits when joining a pod. For example, if a pod was created via `podman pod create --cpus=5`, specifying `podman container create --pod=<pod_id|pod_name> --cpus=4` causes the container to use the smaller limit. Also, containers which specify their own cgroup, such as `--cgroupns=host`, do NOT get the assigned pod level cgroup resources.

## Related Commands

- **[podman(1)]** - Main Podman command reference

## History

July 2018, Originally compiled by Peter Hunt [pehunt@redhat.com]

## Source

- Original URL: https://docs.podman.io/en/latest/Commands/podman-pod.html
- Documentation retrieved from: https://docs.podman.io/en/latest/markdown/podman-pod.1.html
- Commands list from: https://docs.podman.io/en/latest/Commands.html