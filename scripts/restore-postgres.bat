@echo off
setlocal

set CONTAINER=estoque-inox-postgres
set DB_NAME=estoque_inox
set DB_USER=estoque_user

if "%~1"=="" (
    echo Informe o caminho do arquivo de backup.
    echo Exemplo: scripts\restore-postgres.bat backups\estoque_inox_2026-07-08_1530.sql
    exit /b 1
)

if not exist "%~1" (
    echo Arquivo nao encontrado: %~1
    exit /b 1
)

echo ATENCAO: este restore pode sobrescrever dados do banco %DB_NAME%.
set /p CONFIRMA=Digite RESTAURAR para continuar: 

if not "%CONFIRMA%"=="RESTAURAR" (
    echo Restore cancelado.
    exit /b 0
)

echo Restaurando backup %~1...

type "%~1" | docker exec -i %CONTAINER% psql -U %DB_USER% -d %DB_NAME%

if errorlevel 1 (
    echo Erro ao restaurar backup. Verifique se o Docker esta rodando e se o container %CONTAINER% esta ativo.
    exit /b 1
)

echo Restore concluido com sucesso.
