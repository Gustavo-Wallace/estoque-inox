@echo off
setlocal

set CONTAINER=estoque-inox-postgres
set DB_NAME=estoque_inox
set DB_USER=estoque_user

if not exist backups mkdir backups

for /f %%i in ('powershell -NoProfile -Command "Get-Date -Format yyyy-MM-dd_HHmm"') do set TIMESTAMP=%%i
set BACKUP_FILE=backups\estoque_inox_%TIMESTAMP%.sql

echo Gerando backup em %BACKUP_FILE%...

docker exec %CONTAINER% pg_dump -U %DB_USER% -d %DB_NAME% > "%BACKUP_FILE%"

if errorlevel 1 (
    echo Erro ao gerar backup. Verifique se o Docker esta rodando e se o container %CONTAINER% esta ativo.
    exit /b 1
)

echo Backup gerado com sucesso: %BACKUP_FILE%
