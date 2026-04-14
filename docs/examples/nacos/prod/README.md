# Nacos Production Templates

These files are sanitized examples derived from the local private `nacos-configs/prod/` folder.

## Mapping

| Local Example File | Recommended Namespace | Nacos Data ID |
| --- | --- | --- |
| `weblog-springboot.example.yml` | `prod` | `weblog-springboot.yml` |
| `weblog-gateway.example.yml` | `prod` | `weblog-gateway.yml` |
| `zyz-ai-robot.example.yml` | `prod` | `zyz-ai-robot.yml` |

## What Was Desensitized

- database hosts, usernames, passwords
- object storage endpoint and credentials
- JWT secret
- model provider API keys
- private search service address
- private hostnames and storage paths

## Usage

1. Copy the example content into your own private file.
2. Replace every placeholder wrapped in `<...>`.
3. Upload the result to Nacos under the matching namespace and dataId.
4. Do not commit the filled values back into Git.
