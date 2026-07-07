# Estoque Inox

Sistema web mobile-first para controle simples de estoque e vendas de uma banca de bijuterias de aço inox.

Nesta etapa inicial, o projeto contém a base da aplicação Spring Boot com Thymeleaf, autenticação em memória, banco H2 para desenvolvimento e modelagem inicial de categorias e produtos.

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

## Usuários de teste

| Usuário | Senha | Perfil |
| --- | --- | --- |
| `admin` | `admin123` | `ADMIN` |
| `vendedora` | `venda123` | `VENDEDORA` |

## Rotas iniciais

- `/`: página inicial pública
- `/login`: login público
- `/dashboard`: painel para usuários autenticados
- `/produtos`: lista temporária de produtos para usuários autenticados
- `/admin`: acesso apenas para `ADMIN`
- `/admin/produtos`: gerenciamento de produtos para `ADMIN`
- `/admin/categorias`: gerenciamento de categorias para `ADMIN`
- `/admin/estoque`: histórico de movimentações de estoque para `ADMIN`
- `/admin/estoque/entrada`: registro de entrada de estoque para `ADMIN`
- `/admin/estoque/ajuste`: ajuste manual de estoque para `ADMIN`
- `/admin/produtos/{id}/movimentacoes`: histórico de estoque de um produto para `ADMIN`
- `/vendas`: acesso para `ADMIN` e `VENDEDORA`

## H2 Console

O console do H2 está habilitado para desenvolvimento em:

```text
http://localhost:8080/h2-console
```

Use os dados abaixo:

```text
JDBC URL: jdbc:h2:mem:estoqueinox
User Name: sa
Password:
```

## Próximas etapas previstas

- Controle de estoque e vendas
- Migração futura para PostgreSQL

## Teste manual do CRUD administrativo

1. Acesse `/login` com `admin/admin123`.
2. Entre em `/admin/produtos` para criar, editar, ativar ou desativar produtos.
3. Entre em `/admin/categorias` para criar, editar, ativar ou desativar categorias.
4. Acesse `/login` com `vendedora/venda123`.
5. Confirme que `/produtos` abre para consulta.
6. Confirme que `/admin/produtos` e `/admin/categorias` retornam acesso negado.

## Teste manual de movimentações de estoque

1. Acesse `/login` com `admin/admin123`.
2. Entre em `/admin/estoque`.
3. Use `/admin/estoque/entrada` para adicionar unidades a um produto.
4. Use `/admin/estoque/ajuste` para ajustar a quantidade total de um produto.
5. Confira o histórico geral em `/admin/estoque`.
6. Confira o histórico de um produto em `/admin/produtos/{id}/movimentacoes`.
7. Acesse `/login` com `vendedora/venda123` e confirme que `/admin/estoque` retorna acesso negado.
