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
