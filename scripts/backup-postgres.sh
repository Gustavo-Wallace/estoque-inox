#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
COMPOSE_FILE="$PROJECT_ROOT/docker-compose.prod.yml"
ENV_FILE="$PROJECT_ROOT/.env"
BACKUP_DIR="$PROJECT_ROOT/backups"

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Arquivo .env nao encontrado. Crie um .env a partir do .env.example antes de gerar backup." >&2
  exit 1
fi

KEEP_LAST="$(grep -E '^BACKUP_KEEP_LAST=' "$ENV_FILE" 2>/dev/null | tail -n 1 | cut -d '=' -f 2- | tr -d '\r' || true)"
KEEP_LAST="${KEEP_LAST:-14}"
TIMESTAMP="$(date +%Y-%m-%d_%H-%M-%S)"
BACKUP_FILE="$BACKUP_DIR/estoque_inox_${TIMESTAMP}.dump"
TMP_FILE="${BACKUP_FILE}.tmp"

mkdir -p "$BACKUP_DIR"

echo "Gerando backup em $BACKUP_FILE..."

if ! docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T postgres sh -c 'pg_dump -U "$POSTGRES_USER" -d "$POSTGRES_DB" -Fc' > "$TMP_FILE"; then
  rm -f "$TMP_FILE"
  echo "Erro ao gerar backup. Verifique se os containers de producao estao rodando." >&2
  exit 1
fi

if [[ ! -s "$TMP_FILE" ]]; then
  rm -f "$TMP_FILE"
  echo "Erro ao gerar backup: arquivo ficou vazio." >&2
  exit 1
fi

mv "$TMP_FILE" "$BACKUP_FILE"
echo "Backup gerado com sucesso: $BACKUP_FILE"

if [[ "$KEEP_LAST" =~ ^[0-9]+$ && "$KEEP_LAST" -gt 0 ]]; then
  echo "Mantendo os ultimos $KEEP_LAST backups locais."
  find "$BACKUP_DIR" -maxdepth 1 -type f -name 'estoque_inox_*.dump' \
    | sort -r \
    | tail -n +"$((KEEP_LAST + 1))" \
    | xargs -r rm -f
else
  echo "BACKUP_KEEP_LAST invalido ou desativado; rotacao ignorada."
fi
