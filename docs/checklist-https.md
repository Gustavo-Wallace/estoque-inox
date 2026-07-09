# Checklist HTTPS

## Antes

- [ ] App rodando na VPS.
- [ ] Dominio comprado ou subdominio disponivel.
- [ ] Registro DNS tipo `A` apontando para o IP da VPS.
- [ ] Porta `80` liberada.
- [ ] Porta `443` liberada.
- [ ] Porta `8080` revisada para nao ficar publica depois do Nginx.
- [ ] Backup recente feito.
- [ ] Senhas padrao trocadas ou plano claro para trocar antes do uso real.

## Durante

- [ ] Nginx instalado.
- [ ] Certbot instalado.
- [ ] Configuracao copiada de `deploy/nginx/estoque-inox.conf.example`.
- [ ] Dominio substituido no arquivo.
- [ ] `nginx -t` sem erro.
- [ ] Site habilitado em `sites-enabled`.
- [ ] Nginx recarregado.
- [ ] Certbot executado.
- [ ] HTTPS acessivel.
- [ ] HTTP redireciona para HTTPS.

## Depois

- [ ] Login testado.
- [ ] Venda testada.
- [ ] Cancelamento parcial testado.
- [ ] Relatorios testados.
- [ ] Exportacoes testadas.
- [ ] Backup testado.
- [ ] Acesso pelo celular testado.
- [ ] Senhas padrao trocadas.
- [ ] Usuarios de teste desativados.
- [ ] Renovacao do Certbot validada com `sudo certbot renew --dry-run`.
- [ ] Logs do Nginx sem erros recorrentes.
