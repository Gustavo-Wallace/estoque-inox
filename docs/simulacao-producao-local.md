# Simulacao de producao local

Este roteiro simula um primeiro setup real de producao no ambiente local, usando Docker Compose de producao e PostgreSQL.

O objetivo e validar:

- profile `prod`;
- banco PostgreSQL limpo;
- seed essencial;
- dados demonstrativos desligados;
- login admin inicial;
- criacao de usuarias reais;
- cadastro manual de produtos;
- vendas;
- cancelamentos;
- relatorios;
- exportacoes;
- backup;
- restore.

## Ambiente recomendado

Crie um `.env` a partir do `.env.example`:

```cmd
copy .env.example .env
```

No Linux/macOS:

```bash
cp .env.example .env
```

Para esta simulacao, use valores como:

```text
SPRING_PROFILES_ACTIVE=prod
SERVER_PORT=8080
DB_HOST=postgres
DB_PORT=5432
DB_NAME=estoque_inox
DB_USER=estoque_user
DB_PASSWORD=uma_senha_de_teste_forte
APP_SEED_ENABLED=true
APP_DEMO_DATA_ENABLED=false
BACKUP_KEEP_LAST=14
```

`APP_SEED_ENABLED=true` cria o admin inicial se ele ainda nao existir.

`APP_DEMO_DATA_ENABLED=false` impede a criacao de produtos, categorias e usuarios demonstrativos.

Apos o primeiro setup real, o recomendado e mudar `APP_SEED_ENABLED=false`.

## Atencao: reset apaga dados locais

O comando abaixo apaga volumes e dados do ambiente local de simulacao. Use apenas quando quiser recriar o banco do zero.

Nao use este comando em producao real.

```bash
docker compose -f docker-compose.prod.yml --env-file .env down -v
```

Depois suba novamente:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

Isso recria o banco local do ambiente `prod` simulado.

## Subir ambiente

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

Verifique containers:

```bash
docker ps
```

Veja logs da aplicacao:

```bash
docker compose -f docker-compose.prod.yml --env-file .env logs -f app
```

Acesse:

```text
http://localhost:8080
```

No Windows, o script abaixo mostra status, containers e logs recentes:

```cmd
scripts\status-prod-local.bat
```

## Primeiro acesso

Com `APP_SEED_ENABLED=true`, entre com o admin inicial:

```text
Usuario: admin
Senha: admin123
```

Depois:

- troque a senha do admin, se o sistema ja permitir;
- crie uma vendedora real de teste;
- confirme que nao existe vendedora demonstrativa, a menos que `APP_DEMO_DATA_ENABLED=true` tenha sido usado;
- confirme que nao existem produtos demonstrativos.

## Dados manuais

Cadastre manualmente:

- categorias reais;
- produtos reais;
- estoque inicial.

Isso valida o fluxo esperado para producao real, sem depender de massa fake.

## Fluxo funcional

Teste:

- venda com multiplos itens;
- venda com desconto por item;
- venda com 2 unidades do mesmo produto;
- cancelamento de apenas 1 unidade;
- cancelamento de item individual;
- cancelamento da venda inteira.

Depois confira:

- estoque;
- movimentacoes;
- relatorios;
- exportacoes CSV;
- permissoes da vendedora;
- bloqueio de `/admin/**` para vendedora;
- H2 Console indisponivel em `prod`.

## Backup e restore

Gere backup:

```cmd
scripts\backup-postgres.bat
```

No Linux:

```bash
./scripts/backup-postgres.sh
```

Confira o arquivo em:

```text
backups/
```

Confira tambem que o backup nao aparece para commit:

```bash
git status --short
```

Teste restore apenas em ambiente local seguro.

## Persistencia

Pare sem apagar volumes:

```bash
docker compose -f docker-compose.prod.yml --env-file .env down
```

Suba novamente:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d
```

Confirme que usuarios, produtos, vendas, movimentacoes e relatorios continuam com os dados criados manualmente.

## Ordem recomendada antes da VPS

1. Simular producao local limpa.
2. Corrigir qualquer problema encontrado.
3. Fazer deploy na VPS.
4. Configurar Nginx/HTTPS.
5. Rodar piloto real.
