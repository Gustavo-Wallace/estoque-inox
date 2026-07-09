# Nginx, dominio e HTTPS

Este guia prepara o Estoque Inox para rodar atras de Nginx com dominio e HTTPS em uma VPS.

O Nginx fica na frente da aplicacao. A aplicacao Spring Boot continua rodando na porta `8080`, enquanto as usuarias acessam o sistema pelo dominio HTTPS.

Fluxo esperado:

```text
Cliente/celular
-> https://estoque.seudominio.com
-> Nginx
-> Estoque Inox em http://127.0.0.1:8080
-> PostgreSQL
```

O PostgreSQL nao deve ser exposto publicamente.

## 1. Pre-requisitos

- VPS com a aplicacao rodando.
- Dominio ou subdominio apontando para o IP da VPS.
- Portas `80` e `443` liberadas.
- Nginx instalado.
- Certbot instalado.
- Backup recente antes de alterar configuracoes.

## 2. Apontar dominio

Crie um registro DNS do tipo `A` apontando para o IP da VPS:

```text
estoque.seudominio.com -> IP_DA_VPS
```

A propagacao pode levar alguns minutos ou horas, dependendo do provedor de DNS.

## 3. Instalar Nginx

Em Ubuntu/Debian:

```bash
sudo apt update
sudo apt install nginx -y
```

Confira o status:

```bash
sudo systemctl status nginx
```

## 4. Instalar Certbot

```bash
sudo apt install certbot python3-certbot-nginx -y
```

## 5. Criar configuracao do Nginx

Copie o template do projeto:

```bash
sudo cp deploy/nginx/estoque-inox.conf.example /etc/nginx/sites-available/estoque-inox
```

Edite o arquivo:

```bash
sudo nano /etc/nginx/sites-available/estoque-inox
```

Troque `SEU_DOMINIO_AQUI` pelo dominio real, por exemplo:

```text
estoque.seudominio.com
```

O proxy aponta para:

```text
http://127.0.0.1:8080
```

## 6. Habilitar site

```bash
sudo ln -s /etc/nginx/sites-available/estoque-inox /etc/nginx/sites-enabled/
sudo nginx -t
sudo systemctl reload nginx
```

Se o `nginx -t` falhar, corrija a configuracao antes de recarregar.

## 7. Emitir certificado HTTPS

Com o dominio ja apontando para a VPS:

```bash
sudo certbot --nginx -d estoque.seudominio.com
```

Use o dominio real no lugar de `estoque.seudominio.com`.

## 8. Testar acesso

Acesse:

```text
https://estoque.seudominio.com
```

Teste tambem o HTTP:

```text
http://estoque.seudominio.com
```

Ele deve redirecionar para HTTPS.

## 9. Renovacao automatica

O Certbot normalmente instala renovacao automatica. Valide com:

```bash
sudo certbot renew --dry-run
```

## 10. Cuidados

- Nao exponha PostgreSQL para internet.
- Nao use HTTP puro em producao final.
- Troque senhas padrao antes do uso real.
- Faca backup antes de mudar Nginx, containers ou `.env`.
- Teste acesso pelo celular.
- Se houver erro, confira logs do Nginx e da aplicacao:

```bash
sudo tail -f /var/log/nginx/error.log
docker compose -f docker-compose.prod.yml --env-file .env logs -f app
```

## Observacao sobre a porta 8080

Durante o teste inicial, o compose de producao publica a porta `8080`. Depois que Nginx/HTTPS estiver funcionando, avalie restringir esse acesso no firewall ou ajustar o compose para publicar a porta apenas localmente.
