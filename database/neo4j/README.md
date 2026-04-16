# Neo4j Snapshot

The canonical `system-shell-graph` seed data now lives in:

- `backend/src/main/resources/system-shell-graph-restore.cypher`

The backend reseed flow loads that resource directly into Neo4j.

To restore it manually into the local Docker Neo4j container:

```bash
cat backend/src/main/resources/system-shell-graph-restore.cypher | docker exec -i design-hub-neo4j cypher-shell -u neo4j -p password
```
