# Estoque Inox

Sistema web mobile-first para controle simples de estoque e vendas de uma banca de bijuterias de aco inox.

O projeto ja possui base Spring Boot com Thymeleaf, autenticacao persistida em banco, H2 para desenvolvimento, CRUD administrativo de produtos/categorias/usuarios, movimentacoes de estoque, venda com multiplos itens, desconto por item e relatorios administrativos.

## Tecnologias

- Java 21
- Spring Boot
- Maven
- Thymeleaf
- Spring Security
- Spring Data JPA
- H2 Database
- PostgreSQL
- Bootstrap via CDN
- Docker Compose

## Profiles disponiveis

- `dev`: profile padrao, usa H2 em memoria e habilita o H2 Console.
- `postgres`: usa PostgreSQL local, com valores padrao de desenvolvimento por variaveis de ambiente.
- `prod`: usa PostgreSQL obrigatorio, sem H2 Console, sem SQL verboso e sem senhas padrao no arquivo de configuracao.

## Como rodar localmente com H2

No Windows:

```cmd
mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
./mvnw spring-boot:run
```

Depois, acesse:

```text
http://localhost:8080
```

O profile padrao e `dev`, usando H2 em memoria. Os dados somem ao reiniciar a aplicacao.

## Usuarios de teste

| Usuario | Senha | Perfil |
| --- | --- | --- |
| `admin` | `admin123` | `ADMIN` |
| `vendedora` | `venda123` | `VENDEDORA` |

Essas senhas sao apenas para desenvolvimento. Troque antes de qualquer uso real.

## Rodar com PostgreSQL via Docker Compose

Suba apenas o banco:

```bash
docker compose up -d postgres
```

Depois rode a aplicacao com o profile `postgres`.

No Windows CMD:

```cmd
set SPRING_PROFILES_ACTIVE=postgres && mvnw.cmd spring-boot:run
```

No PowerShell:

```powershell
$env:SPRING_PROFILES_ACTIVE="postgres"; .\mvnw.cmd spring-boot:run
```

No Linux/macOS:

```bash
SPRING_PROFILES_ACTIVE=postgres ./mvnw spring-boot:run
```

Por padrao, o profile `postgres` usa:

```text
DB_HOST=localhost
DB_PORT=5432
DB_NAME=estoque_inox
DB_USER=estoque_user
DB_PASSWORD=estoque_pass
```

Esses valores sao apenas para desenvolvimento local. Nao use `estoque_pass` em producao.

Tambem e possivel subir banco e aplicacao juntos:

```bash
docker compose up --build
```

Nesse caso, acesse:

```text
http://localhost:8080
```

para finalizar:
```bash
netstat -ano | findstr "8080"
taskkill /F /PID 7864
```

O volume `postgres_data` mantem os dados entre reinicios do container.

## Preparacao para producao

Esta etapa prepara o projeto para rodar com profile `prod`, Docker e PostgreSQL persistente. O fluxo recomendado e subir primeiro por IP/porta `8080` para teste inicial e depois configurar Nginx/HTTPS para acesso final pelo dominio.

Crie um arquivo `.env` a partir do exemplo:

```bash
cp .env.example .env
```

No Windows, se preferir:

```cmd
copy .env.example .env
```

Edite o `.env` e troque principalmente:

```text
DB_PASSWORD=troque_por_uma_senha_forte
```

Para subir localmente simulando producao:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d --build
```

Depois acesse:

```text
http://localhost:8080
```

Em uma VPS, o primeiro teste pode ser feito temporariamente por:

```text
http://IP_DA_VPS:8080
```

Para uso real, o acesso final deve ser pelo dominio com HTTPS, usando Nginx na frente da aplicacao.

Para acompanhar logs da aplicacao:

```bash
docker compose -f docker-compose.prod.yml logs -f app
```

Para parar sem apagar dados:

```bash
docker compose -f docker-compose.prod.yml down
```

Nao use `docker compose down -v` a menos que queira apagar o volume do PostgreSQL e perder os dados locais desse ambiente.

No compose de producao local, o banco nao expoe a porta `5432` para fora. Em producao real, o ideal tambem e nao expor o PostgreSQL diretamente para a internet.

O profile `prod` usa:

```text
SPRING_PROFILES_ACTIVE=prod
DB_HOST
DB_PORT
DB_NAME
DB_USER
DB_PASSWORD
SERVER_PORT
APP_SEED_ENABLED
BACKUP_KEEP_LAST
```

`APP_SEED_ENABLED=true` permite criar os dados iniciais caso ainda nao existam. Depois do primeiro setup real, deixe `APP_SEED_ENABLED=false`. O seed nao sobrescreve usuarios existentes, mas as senhas padrao (`admin/admin123` e `vendedora/venda123`) devem ser trocadas antes de qualquer uso real.

O `ddl-auto=update` continua ativo por enquanto para facilitar esta fase. Antes de producao definitiva, o ideal e migrar para Flyway ou Liquibase.

Antes de producao real:

- trocar todas as senhas padrao;
- criar usuarias reais;
- conferir produtos e estoque inicial;
- configurar HTTPS, dominio e Nginx;
- configurar backup automatico;
- testar restore de backup;
- confirmar que `/h2-console` nao abre em `prod`;
- fazer backup antes de testes reais.

## Rotas principais

- `/`: pagina inicial publica
- `/login`: login publico
- `/dashboard`: painel para usuarios autenticados
- `/produtos`: consulta de produtos para `ADMIN` e `VENDEDORA`
- `/vendas`: listagem de vendas para `ADMIN` e `VENDEDORA`
- `/vendas/nova`: registro de venda com um ou mais itens
- `/vendas/{id}`: detalhe da venda
- `/vendas/{id}/cancelar`: cancelamento da venda inteira
- `/vendas/{vendaId}/itens/{itemId}/cancelar`: cancelamento de um item da venda
- `/admin`: acesso apenas para `ADMIN`
- `/admin/produtos`: gerenciamento de produtos para `ADMIN`
- `/admin/categorias`: gerenciamento de categorias para `ADMIN`
- `/admin/estoque`: historico de movimentacoes de estoque para `ADMIN`
- `/admin/estoque/entrada`: registro de entrada de estoque para `ADMIN`
- `/admin/estoque/ajuste`: ajuste manual de estoque para `ADMIN`
- `/admin/produtos/{id}/movimentacoes`: historico de estoque de um produto para `ADMIN`
- `/admin/relatorios`: resumo do dia e links dos relatorios para `ADMIN`
- `/admin/relatorios/vendas`: vendas por periodo para `ADMIN`
- `/admin/relatorios/produtos-mais-vendidos`: produtos mais vendidos para `ADMIN`
- `/admin/relatorios/estoque-baixo`: produtos com estoque baixo para `ADMIN`
- `/admin/relatorios/vendedoras`: vendas agrupadas por vendedora para `ADMIN`
- `/admin/exportacoes`: exportacoes CSV para `ADMIN`
- `/admin/exportacoes/produtos`: CSV de produtos para `ADMIN`
- `/admin/exportacoes/estoque-baixo`: CSV de estoque baixo para `ADMIN`
- `/admin/exportacoes/vendas`: CSV de vendas por periodo para `ADMIN`
- `/admin/exportacoes/movimentacoes-estoque`: CSV de movimentacoes por periodo para `ADMIN`
- `/admin/exportacoes/produtos-mais-vendidos`: CSV de produtos mais vendidos por periodo para `ADMIN`
- `/admin/usuarios`: gerenciamento de usuarios para `ADMIN`
- `/admin/usuarios/novo`: cadastro de usuario para `ADMIN`
- `/admin/usuarios/{id}/editar`: edicao de usuario para `ADMIN`
- `/admin/usuarios/{id}/senha`: alteracao de senha para `ADMIN`
- `/admin/usuarios/{id}/alternar-status`: ativar ou desativar usuario para `ADMIN`

## H2 Console

O console do H2 esta habilitado para desenvolvimento em:

```text
http://localhost:8080/h2-console
```

Use os dados abaixo:

```text
JDBC URL: jdbc:h2:mem:estoqueinox
User Name: sa
Password:
```

O H2 Console fica desabilitado no profile `postgres`.

## Dados simulados de desenvolvimento

Ao iniciar a aplicacao, o seed cria dados apenas quando ainda nao existem.

Categorias simuladas:

- Brincos
- Aneis
- Colares
- Pulseiras
- Correntes
- Conjuntos
- Piercings
- Tornozeleiras

Produtos simulados:

- `BRI-001` - Brinco argola inox dourado pequeno
- `BRI-002` - Brinco ponto de luz inox prata
- `BRI-003` - Brinco coracao inox dourado
- `ANE-001` - Anel inox liso
- `ANE-002` - Anel inox pedra preta
- `COL-001` - Colar inox ponto de luz
- `COL-002` - Colar inox corrente veneziana
- `PUL-001` - Pulseira inox elo portugues
- `PUL-002` - Pulseira inox dourada fina
- `COR-001` - Corrente inox masculina
- `CON-001` - Conjunto colar e brinco inox
- `TOR-001` - Tornozeleira inox coracao

O seed nao cria vendas automaticamente para evitar duplicacao de movimentos e relatórios.

## Documentacao operacional

- [Roteiro de teste manual](docs/roteiro-teste-manual.md)
- [Checklist pre-piloto](docs/checklist-pre-piloto.md)
- [Deploy em VPS](docs/deploy-vps.md)
- [Checklist de deploy](docs/checklist-deploy.md)
- [Nginx, dominio e HTTPS](docs/nginx-https.md)
- [Checklist HTTPS](docs/checklist-https.md)
- [Backup automatico](docs/backup-automatico.md)

## Exportacoes CSV

As exportacoes administrativas ficam em:

```text
http://localhost:8080/admin/exportacoes
```

Apenas usuarias `ADMIN` podem acessar. Os arquivos usam CSV em UTF-8, separador `;` e podem ser abertos no Excel ou Google Sheets.

Exportacoes disponiveis:

- Produtos
- Estoque baixo
- Vendas por periodo
- Movimentacoes de estoque por periodo
- Produtos mais vendidos por periodo

Quando o periodo nao for informado, o sistema usa o dia atual.

## Revisao de seguranca e regras criticas

A revisao atual reforcou pontos importantes do backend:

- `/admin/**` continua restrito a `ADMIN`, com pagina amigavel de acesso negado.
- `/h2-console/**` continua liberado apenas no profile `dev`; no profile `postgres` fica bloqueado.
- Usuarios inativos nao autenticam, senhas sao armazenadas com BCrypt e usernames sao normalizados em minusculo.
- O ultimo `ADMIN` ativo nao pode ser removido ou desativado, e o admin logado nao remove o proprio acesso.
- Venda, cancelamento e estoque validam quantidade, estoque suficiente, desconto e permissao no backend.
- Movimentacoes que alteram produto usam lock pessimista para reduzir risco de estoque negativo em acessos simultaneos.
- Relatorios usam quantidade ativa dos itens, ignorando itens totalmente cancelados no total liquido.

## Backups do PostgreSQL

Os scripts de backup usam o Docker Compose de producao (`docker-compose.prod.yml`) e o arquivo `.env`.

Antes de gerar backup, confirme que os containers estao rodando:

```bash
docker compose -f docker-compose.prod.yml --env-file .env up -d
```

Backup manual no Windows:

```cmd
scripts\backup-postgres.bat
```

Backup manual no Linux/VPS:

```bash
chmod +x scripts/backup-postgres.sh
./scripts/backup-postgres.sh
```

Os backups ficam em:

```text
backups/
```

O formato padrao e `.dump`, gerado com `pg_dump -Fc`. A rotacao mantem apenas os ultimos backups locais conforme `BACKUP_KEEP_LAST` no `.env`:

```text
BACKUP_KEEP_LAST=14
```

A rotacao so roda depois que o backup atual e gerado com sucesso.

Restaurar no Windows:

```cmd
scripts\restore-postgres.bat backups\estoque_inox_2026-07-09_02-00-00.dump
```

Restaurar no Linux/VPS:

```bash
chmod +x scripts/restore-postgres.sh
./scripts/restore-postgres.sh backups/estoque_inox_2026-07-09_02-00-00.dump
```

O restore de `.dump` usa `pg_restore --clean --if-exists --no-owner` e pode sobrescrever dados do banco. Os scripts tambem aceitam `.sql` antigo usando `psql`.

Avisos importantes:

- backups nao devem ser commitados;
- backup local no mesmo servidor nao substitui backup externo;
- antes de producao real, copie backups para fora da VPS;
- nunca use `docker compose down -v` sem backup recente;
- teste restore antes do piloto real.

Mais detalhes sobre agendamento futuro: [Backup automatico](docs/backup-automatico.md).

## Teste manual de venda com multiplos itens

1. Acesse `/login` com `admin/admin123`.
2. Entre em `/vendas/nova`.
3. Adicione dois ou mais produtos diferentes na mesma venda.
4. Registre a venda e confira a listagem em `/vendas`.
5. Abra `/vendas/{id}` e confirme os itens, totais e status.
6. Cancele um item pelo detalhe da venda.
7. Confirme que a venda ficou `PARCIALMENTE_CANCELADA`.
8. Confirme em `/admin/estoque` que houve movimentacao `CANCELAMENTO`.
9. Registre outra venda e use `/vendas/{id}/cancelar`.
10. Confirme que a venda ficou `CANCELADA` e que cada item foi estornado.

## Teste manual de desconto por item

1. Acesse `/login` com `admin/admin123` ou `vendedora/venda123`.
2. Entre em `/vendas/nova`.
3. Adicione um produto sem desconto.
4. Adicione outro produto com desconto unitario em reais.
5. Confirme no resumo: total original, desconto total e total final.
6. Registre a venda e abra `/vendas/{id}`.
7. Confirme por item: preco original, desconto unitario, preco final e subtotal final.
8. Tente desconto negativo e desconto maior que o preco do produto.
9. Confirme que a venda invalida nao e salva.
10. Confira em `/admin/relatorios` que o desconto total aparece e que produtos vendidos usam valor final.

## Teste manual de cancelamento parcial de item

1. Acesse `/login` com `admin/admin123`.
2. Crie uma venda com 2 unidades do mesmo produto.
3. Abra `/vendas/{id}`.
4. Clique em cancelar item.
5. Informe quantidade `1` e confirme.
6. Confira que o item ficou `PARCIALMENTE_CANCELADO`.
7. Confira quantidade vendida, cancelada e ativa no detalhe da venda.
8. Confira em `/admin/estoque` uma movimentacao `CANCELAMENTO` com quantidade `1`.
9. Cancele a quantidade restante e confirme que o item ficou `CANCELADO`.
10. Crie outra venda, cancele parcialmente um item e depois cancele a venda inteira para confirmar que apenas as quantidades ativas sao estornadas.

## Teste manual de permissoes

1. Acesse `/login` com `vendedora/venda123`.
2. Confirme que `/produtos`, `/vendas` e `/vendas/nova` funcionam.
3. Confirme que `/admin/produtos`, `/admin/categorias` e `/admin/estoque` retornam acesso negado.
4. Registre uma venda como vendedora.
5. Confirme que a vendedora ve e cancela apenas vendas proprias.

## Teste manual de relatorios

1. Acesse `/login` com `admin/admin123`.
2. Crie uma venda com multiplos itens.
3. Crie outra venda e cancele apenas um item.
4. Crie outra venda e cancele a venda inteira.
5. Acesse `/admin/relatorios` e confira o resumo do dia.
6. Acesse `/admin/relatorios/vendas` e verifique vendas concluidas, parciais e canceladas.
7. Acesse `/admin/relatorios/produtos-mais-vendidos` e confirme que itens cancelados nao entram na quantidade.
8. Acesse `/admin/relatorios/estoque-baixo` e confira produtos ativos com estoque menor ou igual ao minimo.
9. Acesse `/admin/relatorios/vendedoras` e confira o agrupamento por usuario responsavel.
10. Entre como `vendedora/venda123` e confirme que `/admin/relatorios` retorna acesso negado.

## Teste manual de usuarios

1. Acesse `/login` com `admin/admin123`.
2. Entre em `/admin/usuarios`.
3. Confirme que os usuarios iniciais `admin` e `vendedora` foram criados.
4. Cadastre uma nova vendedora em `/admin/usuarios/novo`.
5. Faca logout e entre com a nova vendedora.
6. Confirme que ela acessa `/vendas`, `/vendas/nova` e `/produtos`.
7. Confirme que ela nao acessa `/admin/usuarios` nem outras rotas `/admin/**`.
8. Entre novamente como admin e altere a senha da nova vendedora.
9. Confirme login com a nova senha.
10. Desative a nova vendedora e confirme que ela nao consegue mais logar.

## Proximas etapas previstas

- Filtros e melhorias operacionais
- Migracao futura para PostgreSQL
