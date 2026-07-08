# Estoque Inox

Sistema web mobile-first para controle simples de estoque e vendas de uma banca de bijuterias de aco inox.

O projeto ja possui base Spring Boot com Thymeleaf, autenticacao em memoria, H2 para desenvolvimento, CRUD administrativo de produtos/categorias, movimentacoes de estoque e venda com multiplos itens.

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

## Teste manual de permissoes

1. Acesse `/login` com `vendedora/venda123`.
2. Confirme que `/produtos`, `/vendas` e `/vendas/nova` funcionam.
3. Confirme que `/admin/produtos`, `/admin/categorias` e `/admin/estoque` retornam acesso negado.
4. Registre uma venda como vendedora.
5. Confirme que a vendedora ve e cancela apenas vendas proprias.

## Proximas etapas previstas

- Relatorios e filtros melhores
- Migracao futura para PostgreSQL
