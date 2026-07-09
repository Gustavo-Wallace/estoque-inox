# Checklist de deploy

## Antes do deploy

- [ ] Docker Compose de producao testado localmente.
- [ ] Backup manual funcionando.
- [ ] Restore testado em ambiente seguro.
- [ ] `.env.example` atualizado.
- [ ] `.env` ignorado no Git.
- [ ] Senhas padrao identificadas.
- [ ] Politica de seed revisada.
- [ ] `APP_DEMO_DATA_ENABLED=false` definido para producao real.
- [ ] H2 Console desabilitado em `prod`.
- [ ] Profile `prod` testado localmente.
- [ ] Exportacoes funcionando.
- [ ] Relatorios funcionando.
- [ ] Backups em `backups/` ignorados no Git.
- [ ] Comando `docker compose -f docker-compose.prod.yml --env-file .env config` validado.

## Durante o deploy

- [ ] VPS criada.
- [ ] Acesso SSH funcionando.
- [ ] Docker instalado.
- [ ] Docker Compose plugin instalado.
- [ ] Git instalado.
- [ ] Repositorio clonado.
- [ ] `.env` criado a partir do `.env.example`.
- [ ] Senha forte configurada em `DB_PASSWORD`.
- [ ] `SPRING_PROFILES_ACTIVE=prod` configurado.
- [ ] `APP_SEED_ENABLED` revisado.
- [ ] `APP_DEMO_DATA_ENABLED=false` confirmado.
- [ ] Containers subiram.
- [ ] Logs do app sem erro.
- [ ] Logs do PostgreSQL sem erro.
- [ ] Sistema acessivel por IP na porta `8080`.
- [ ] Login admin funcionando.
- [ ] H2 Console indisponivel em `prod`.
- [ ] Backup funcionando na VPS.

## Antes do uso real

- [ ] Senha admin trocada.
- [ ] Vendedoras reais criadas.
- [ ] Usuarios de teste desativados.
- [ ] Produtos fake removidos, se existirem.
- [ ] Produtos reais cadastrados.
- [ ] Estoque inicial conferido.
- [ ] Teste no celular realizado.
- [ ] Venda teste realizada.
- [ ] Venda com desconto testada.
- [ ] Venda com duas unidades do mesmo produto testada.
- [ ] Cancelamento parcial testado.
- [ ] Cancelamento total testado.
- [ ] Relatorios conferidos.
- [ ] Exportacoes CSV conferidas.
- [ ] Backup gerado.
- [ ] Restore testado em ambiente seguro.
- [ ] `APP_SEED_ENABLED=false` avaliado depois do setup inicial.
- [ ] `APP_DEMO_DATA_ENABLED=false` mantido em producao real.
- [ ] Backup externo planejado.
- [ ] Nginx/HTTPS planejado para etapa futura.

## Nao fazer sem backup recente

- [ ] Nao rodar `docker compose down -v`.
- [ ] Nao apagar volumes do Docker.
- [ ] Nao alterar `.env` em producao sem registrar o que mudou.
- [ ] Nao atualizar a aplicacao sem gerar backup antes.
