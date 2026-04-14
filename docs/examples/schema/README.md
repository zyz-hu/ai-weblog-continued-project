# Database Schema Examples

These schema files are inferred from the current entity classes and mapper query patterns.

They are intended to be:

- readable
- easy to adapt
- good enough for self-hosted deployment

They are not guaranteed to be a byte-for-byte match of the original private production schema.

## Files

- `blog-mysql.example.sql`
- `ai-robot-postgres.example.sql`

## Notes

- adjust field length, charset, collation, and comments to your own standards
- add migration tooling if you want reproducible deployment
- the AI module also depends on a `t_vector_store` table for pgvector retrieval, which is framework-related and may vary by Spring AI version
