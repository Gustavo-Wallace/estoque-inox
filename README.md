# Estoque Inox

Sistema web mobile-first para controle simples de estoque e vendas de uma banca de bijuterias de aço inox.

Nesta etapa inicial, o projeto contém apenas a base da aplicação Spring Boot com Thymeleaf e uma página inicial em `/`.

## Tecnologias

- Java 21
- Spring Boot
- Maven
- Thymeleaf
- Bootstrap via CDN

## Como rodar localmente

```bash
mvn spring-boot:run
```

Depois, acesse:

```text
http://localhost:8080
```

## Próximas etapas previstas

- Autenticação básica
- Perfis de acesso para `ADMIN` e `VENDEDORA`
- Persistência de dados
- Cadastro de produtos
- Controle de estoque e vendas
