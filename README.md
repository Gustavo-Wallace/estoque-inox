# Estoque Inox

Sistema web mobile-first para controle simples de estoque e vendas de uma banca de bijuterias de aço inox.

Nesta etapa inicial, o projeto contém a base da aplicação Spring Boot com Thymeleaf, página inicial pública e autenticação em memória para validação dos perfis.

## Tecnologias

- Java 21
- Spring Boot
- Maven
- Thymeleaf
- Spring Security
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
- `/admin`: acesso apenas para `ADMIN`
- `/vendas`: acesso para `ADMIN` e `VENDEDORA`

## Próximas etapas previstas

- Persistência de dados
- Cadastro de produtos
- Controle de estoque e vendas
