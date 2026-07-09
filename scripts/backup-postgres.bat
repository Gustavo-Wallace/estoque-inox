@echo off
setlocal

set COMPOSE_FILE=docker-compose.prod.yml
set ENV_FILE=.env
set BACKUP_DIR=backups
set BACKUP_KEEP_LAST=14

if not exist "%ENV_FILE%" (
    echo Arquivo %ENV_FILE% nao encontrado.
    echo Crie um .env a partir do .env.example antes de gerar backup.
    exit /b 1
)

for /f "tokens=1,* delims==" %%A in ('findstr /B "BACKUP_KEEP_LAST=" "%ENV_FILE%" 2^>nul') do set BACKUP_KEEP_LAST=%%B

if not exist "%BACKUP_DIR%" mkdir "%BACKUP_DIR%"

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HH-mm-ss"') do set TIMESTAMP=%%i
set BACKUP_FILE=%BACKUP_DIR%\estoque_inox_%TIMESTAMP%.dump

echo Gerando backup em %BACKUP_FILE%...

docker compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" exec -T postgres sh -c "pg_dump -U $POSTGRES_USER -d $POSTGRES_DB -Fc" > "%BACKUP_FILE%"

if errorlevel 1 (
    echo Erro ao gerar backup. Verifique se os containers de producao estao rodando.
    if exist "%BACKUP_FILE%" del /q "%BACKUP_FILE%"
    exit /b 1
)

if not exist "%BACKUP_FILE%" (
    echo Erro ao gerar backup: arquivo nao foi criado.
    exit /b 1
)

for %%A in ("%BACKUP_FILE%") do if %%~zA EQU 0 (
    echo Erro ao gerar backup: arquivo ficou vazio.
    del /q "%BACKUP_FILE%"
    exit /b 1
)

echo Backup gerado com sucesso: %BACKUP_FILE%
echo Mantendo os ultimos %BACKUP_KEEP_LAST% backups locais.

for /f "skip=%BACKUP_KEEP_LAST% delims=" %%F in ('dir /b /a-d /o-d "%BACKUP_DIR%\estoque_inox_*.dump" 2^>nul') do (
    echo Removendo backup antigo: %BACKUP_DIR%\%%F
    del /q "%BACKUP_DIR%\%%F"
)

exit /b 0
