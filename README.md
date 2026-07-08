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
- Bootstrap via CDN

## Como rodar localmente

```bash
mvn spring-boot:run
```

Depois, acesse:

```text
http://localhost:8080
```

## Usuarios de teste

| Usuario | Senha | Perfil |
| --- | --- | --- |
| `admin` | `admin123` | `ADMIN` |
| `vendedora` | `venda123` | `VENDEDORA` |

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
