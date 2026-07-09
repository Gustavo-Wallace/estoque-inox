#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
COMPOSE_FILE="$PROJECT_ROOT/docker-compose.prod.yml"
ENV_FILE="$PROJECT_ROOT/.env"

if [[ $# -lt 1 ]]; then
  echo "Informe o caminho do arquivo de backup."
  echo "Exemplo: ./scripts/restore-postgres.sh backups/estoque_inox_2026-07-09_02-00-00.dump"
  exit 1
fi

if [[ ! -f "$ENV_FILE" ]]; then
  echo "Arquivo .env nao encontrado. Crie um .env a partir do .env.example antes de restaurar backup." >&2
  exit 1
fi

INPUT_FILE="$1"
if [[ -f "$INPUT_FILE" ]]; then
  BACKUP_FILE="$INPUT_FILE"
elif [[ -f "$PROJECT_ROOT/$INPUT_FILE" ]]; then
  BACKUP_FILE="$PROJECT_ROOT/$INPUT_FILE"
else
  echo "Arquivo nao encontrado: $INPUT_FILE" >&2
  exit 1
fi

echo "ATENCAO: este restore usa --clean --if-exists para arquivos .dump e pode sobrescrever dados do banco."
echo "Arquivo: $BACKUP_FILE"
read -r -p "Digite RESTAURAR para continuar: " CONFIRMA

if [[ "$CONFIRMA" != "RESTAURAR" ]]; then
  echo "Restore cancelado."
  exit 0
fi

case "${BACKUP_FILE,,}" in
  *.dump)
    echo "Restaurando backup custom com pg_restore..."
    cat "$BACKUP_FILE" | docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T postgres sh -c 'pg_restore --clean --if-exists --no-owner -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
    ;;
  *.sql)
    echo "Restaurando backup SQL com psql..."
    cat "$BACKUP_FILE" | docker compose -f "$COMPOSE_FILE" --env-file "$ENV_FILE" exec -T postgres sh -c 'psql -U "$POSTGRES_USER" -d "$POSTGRES_DB"'
    ;;
  *)
    echo "Extensao nao suportada. Use arquivos .dump ou .sql." >&2
    exit 1
    ;;
esac

echo "Restore concluido com sucesso."
