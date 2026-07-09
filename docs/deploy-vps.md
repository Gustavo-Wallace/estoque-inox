# Deploy em VPS

Este guia organiza os passos para subir o Estoque Inox em uma VPS Linux.

O ambiente previsto usa:

- Docker
- Docker Compose
- PostgreSQL
- aplicacao Spring Boot containerizada
- profile `prod`
- arquivo `.env`
- backups locais em `backups/`

Ainda nao faz parte desta etapa configurar dominio, HTTPS, Nginx ou backup externo.

Antes de executar este guia em uma VPS, rode a [simulacao de producao local](simulacao-producao-local.md). Ela valida o profile `prod`, PostgreSQL limpo, seed essencial e dados demonstrativos desabilitados.

## Pre-requisitos da VPS

A VPS precisa ter:

- Linux Ubuntu/Debian ou similar;
- acesso SSH;
- Docker instalado;
- Docker Compose plugin instalado;
- Git instalado;
- porta `22` liberada para SSH;
- porta `8080` liberada temporariamente para teste inicial;
- portas `80` e `443` reservadas futuramente para Nginx/HTTPS.

## Passo 1 - Acessar a VPS

```bash
ssh usuario@IP_DA_VPS
```

## Passo 2 - Atualizar pacotes

```bash
sudo apt update && sudo apt upgrade -y
```

## Passo 3 - Instalar dependencias

Instale utilitarios basicos:

```bash
sudo apt install -y git curl ca-certificates
```

Instale Docker e o plugin do Docker Compose seguindo a documentacao oficial da Docker para Ubuntu/Debian.

Depois confirme:

```bash
docker --version
docker compose version
git --version
```

Se necessario, adicione seu usuario ao grupo `docker` e reconecte no SSH:

```bash
sudo usermod -aG docker $USER
```

## Passo 4 - Clonar o repositorio

```bash
git clone URL_DO_REPOSITORIO
cd estoque-inox
```

## Passo 5 - Criar `.env`

```bash
cp .env.example .env
nano .env
```

Variaveis importantes:

```text
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_HOST=postgres
DB_PORT=5432
DB_NAME=estoque_inox
DB_USER=estoque_user
DB_PASSWORD=senha_forte_aqui
APP_SEED_ENABLED=true
APP_DEMO_DATA_ENABLED=false
BACKUP_KEEP_LAST=14
```

Cuidados:

- use uma senha forte em `DB_PASSWORD`;
- nao commite o `.env`;
- use `APP_SEED_ENABLED=true` apenas no primeiro setup, se precisar criar o admin inicial;
- mantenha `APP_DEMO_DATA_ENABLED=false` em producao real;
- depois do primeiro setup, considere mudar para `APP_SEED_ENABLED=false`;
- troque a senha padrao do admin logo depois do primeiro login;
- se `APP_SEED_ENABLED=false` e nao houver admin no banco, sera necessario criar o admin manualmente ou reabilitar o seed essencial.

## Passo 6 - Subir containers

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

## Passo 7 - Verificar containers

```bash
docker ps
docker compose -f docker-compose.prod.yml --env-file .env logs -f app
```

Se o app nao subir, confira tambem o PostgreSQL:

```bash
docker compose -f docker-compose.prod.yml --env-file .env logs -f postgres
```

## Passo 8 - Acessar temporariamente por IP

```text
http://IP_DA_VPS:8080
```

Esse acesso por IP e HTTP puro e apenas para teste inicial. Para uso real, configure Nginx + HTTPS em etapa futura. Nao use HTTP puro com senhas em producao final.

## Passo 9 - Primeiro login e setup

Se o seed essencial estiver habilitado, acesse com o usuario admin inicial.

Depois:

- troque a senha do admin;
- crie usuarias reais;
- desative usuarios de teste;
- confirme que `APP_DEMO_DATA_ENABLED=false` em producao real;
- remova produtos fake se eles tiverem sido criados em um ambiente de teste;
- cadastre produtos reais;
- confira estoque inicial;
- teste uma venda;
- teste cancelamento parcial;
- confira relatorios;
- confira exportacoes;
- gere um backup manual.

## Passo 10 - Backup

De permissao de execucao ao script:

```bash
chmod +x scripts/backup-postgres.sh scripts/restore-postgres.sh
```

Gere um backup manual:

```bash
./scripts/backup-postgres.sh
```

Os backups ficam em:

```text
backups/
```

Teste restore em ambiente seguro antes do uso real. Backup local no mesmo servidor nao substitui backup externo.

## Passo 11 - Atualizar aplicacao no futuro

Antes de atualizar, gere backup:

```bash
./scripts/backup-postgres.sh
```

Depois atualize:

```bash
git pull
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
docker compose -f docker-compose.prod.yml --env-file .env logs -f app
```

Nao use `docker compose down -v` durante atualizacao. Esse comando remove volumes e pode apagar os dados do PostgreSQL.

## Passo 12 - Parar aplicacao

Para parar sem apagar dados:

```bash
docker compose -f docker-compose.prod.yml --env-file .env down
```

Evite:

```bash
docker compose -f docker-compose.prod.yml --env-file .env down -v
```

Use `down -v` apenas se tiver certeza de que deseja apagar os volumes.

## Proximas etapas de infraestrutura

Ainda faltam para producao real:

- configurar dominio;
- configurar Nginx;
- configurar HTTPS;
- configurar backup externo;
- testar restore periodicamente.
