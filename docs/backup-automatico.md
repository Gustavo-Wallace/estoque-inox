# Backup automatico do PostgreSQL

Este projeto ja possui scripts para gerar backups locais do PostgreSQL usando o Docker Compose de producao.

O script Linux abaixo sera usado futuramente na VPS:

```bash
./scripts/backup-postgres.sh
```

Ele gera arquivos `.dump` em `backups/`, usando `pg_dump -Fc`, e mantem apenas os ultimos backups conforme `BACKUP_KEEP_LAST`.

## Exemplo futuro com cron

Nao configure automaticamente agora. Quando o projeto estiver na VPS, um agendamento diario as 02:00 pode seguir este formato:

```cron
0 2 * * * cd /caminho/estoque-inox && ./scripts/backup-postgres.sh >> backups/backup.log 2>&1
```

Antes de usar em producao real:

- confirme que o caminho do projeto esta correto;
- confirme que `.env` existe no servidor;
- rode o script manualmente pelo menos uma vez;
- confira se o arquivo aparece em `backups/`;
- teste uma restauracao em ambiente seguro.

## Backup externo

Backup local no mesmo servidor ajuda em erros operacionais, mas nao protege contra perda da VPS, disco corrompido ou exclusao acidental do servidor.

Antes do piloto real, o ideal e copiar os backups para armazenamento externo, como outro servidor, Google Drive, S3, Backblaze ou solucao equivalente. Essa integracao ainda nao foi configurada nesta etapa.

## Restauracao periodica

Teste restauracao periodicamente. Um backup so e confiavel depois que o restore foi validado.

Para restaurar um `.dump`:

```bash
./scripts/restore-postgres.sh backups/nome-do-backup.dump
```

O restore de `.dump` usa `pg_restore --clean --if-exists --no-owner`, entao pode sobrescrever objetos existentes no banco.
