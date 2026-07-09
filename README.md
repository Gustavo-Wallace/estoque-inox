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

## Roteiros de validacao

- [Roteiro de teste manual](docs/roteiro-teste-manual.md)
- [Checklist pre-piloto](docs/checklist-pre-piloto.md)

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

## Backup manual do PostgreSQL

Suba o PostgreSQL:

```bash
docker compose up -d postgres
```

Gere um backup manual no Windows:

```cmd
scripts\backup-postgres.bat
```

O arquivo sera salvo em:

```text
backups/
```

Os backups locais nao devem ser commitados. A pasta `backups/` esta no `.gitignore`.

Para restaurar um backup em ambiente de desenvolvimento:

```cmd
scripts\restore-postgres.bat backups\NOME_DO_ARQUIVO.sql
```

O restore pode sobrescrever dados do banco local. Para producao real, ainda sera necessario configurar backup automatico e armazenamento externo seguro.

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
