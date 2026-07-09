@echo off
setlocal

set COMPOSE_FILE=docker-compose.prod.yml
set ENV_FILE=.env

if not exist "%ENV_FILE%" (
    echo Arquivo %ENV_FILE% nao encontrado.
    echo Crie um .env a partir do .env.example antes de consultar o status.
    exit /b 1
)

echo.
echo === Containers do compose de producao ===
docker compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" ps

echo.
echo === Logs recentes da aplicacao ===
docker compose -f "%COMPOSE_FILE%" --env-file "%ENV_FILE%" logs --tail=80 app

echo.
echo Acesso local:
echo http://localhost:8080
echo.
echo Comandos uteis:
echo docker compose -f %COMPOSE_FILE% --env-file %ENV_FILE% up -d --build
echo docker compose -f %COMPOSE_FILE% --env-file %ENV_FILE% down
echo.
echo Nao use down -v, a menos que queira apagar os dados locais da simulacao.
