@echo off
setlocal

set COMPOSE_FILE=docker-compose.prod.yml
set ENV_FILE=.env

if "%~1"=="" (
    echo Informe o caminho do arquivo de backup.
    echo Exemplo: scripts\restore-postgres.bat backups\estoque_inox_2026-07-09_02-00-00.dump
    exit /b 1
)

if not exist "%ENV_FILE%" (
    echo Arquivo %ENV_FILE% nao encontrado.
    echo Crie um .env a partir do .env.example antes de restaurar backup.
    exit /b 1
)

if not exist "%~1" (
    echo Arquivo nao encontrado: %~1
    exit /b 1
)

echo ATENCAO: este restore usa --clean --if-exists e pode sobrescrever dados do banco.
echo Arquivo: %~1
set /p CONFIRMA=Digite RESTAURAR para continuar: 

if not "%CONFIRMA%"=="RESTAURAR" (
    echo Restore cancelado.
    exit /b 0
)

set EXT=%~x1
echo Restaurando backup %~1...

if /I "%EXT%"==".dump" (
    type "%~1" | docker compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" exec -T postgres sh -c "pg_restore --clean --if-exists --no-owner -U $POSTGRES_USER -d $POSTGRES_DB"
) else if /I "%EXT%"==".sql" (
    type "%~1" | docker compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" exec -T postgres sh -c "psql -U $POSTGRES_USER -d $POSTGRES_DB"
) else (
    echo Extensao nao suportada: %EXT%
    echo Use arquivos .dump ou .sql.
    exit /b 1
)

if errorlevel 1 (
    echo Erro ao restaurar backup. Verifique se os containers de producao estao rodando.
    exit /b 1
)

echo Restore concluido com sucesso.
exit /b 0
