# Neo4j Snapshot

This folder contains the `system-shell-graph` Neo4j dataset exported from the reduced repository state.

- `system-shell-graph-export.json`: structured JSON snapshot of all `SystemShellGraphNode` nodes and relationships.
- `system-shell-graph-restore.cypher`: restorable Cypher script for loading the same dataset into Neo4j.

Restore into the local Docker Neo4j container:

```bash
cat database/neo4j/system-shell-graph-restore.cypher | docker exec -i design-hub-neo4j cypher-shell -u neo4j -p password
```

The snapshot was generated after reseeding the backend graph so it matches the current `system-shell-graph` app surface.
