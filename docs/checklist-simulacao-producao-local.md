# Checklist de simulacao de producao local

## 1. Preparacao

- [ ] `.env` criado a partir de `.env.example`.
- [ ] `SPRING_PROFILES_ACTIVE=prod`.
- [ ] `APP_SEED_ENABLED=true`.
- [ ] `APP_DEMO_DATA_ENABLED=false`.
- [ ] Senha do banco configurada com valor forte de teste.
- [ ] Docker Desktop rodando.
- [ ] Ambiente anterior resetado apenas se for teste local.
- [ ] Entendido que `down -v` apaga volumes e dados locais.

## 2. Subida

- [ ] Rodar `docker compose -f docker-compose.prod.yml --env-file .env up -d --build`.
- [ ] Verificar containers com `docker ps`.
- [ ] Verificar logs da aplicacao.
- [ ] Acessar `http://localhost:8080`.
- [ ] Confirmar que o profile ativo e `prod` pelos logs/configuracao.

## 3. Primeiro acesso

- [ ] Login com admin inicial.
- [ ] Trocar senha do admin, se o sistema ja permitir.
- [ ] Criar uma vendedora real de teste.
- [ ] Desativar usuarios de teste, se existirem.
- [ ] Avaliar mudar `APP_SEED_ENABLED=false` depois do primeiro setup.

## 4. Dados reais manuais

- [ ] Confirmar que produtos demonstrativos nao foram criados.
- [ ] Criar categorias reais manualmente.
- [ ] Criar produtos reais manualmente.
- [ ] Adicionar estoque inicial.

## 5. Fluxo de venda

- [ ] Login como vendedora.
- [ ] Criar venda com multiplos itens.
- [ ] Criar venda com desconto por item.
- [ ] Criar venda com 2 unidades do mesmo produto.
- [ ] Cancelar apenas 1 unidade.
- [ ] Cancelar item individual.
- [ ] Cancelar venda inteira.

## 6. Conferencias

- [ ] Conferir estoque.
- [ ] Conferir movimentacoes.
- [ ] Conferir relatorios.
- [ ] Conferir exportacoes CSV.
- [ ] Conferir permissoes da vendedora.
- [ ] Conferir que `/admin/**` bloqueia vendedora.
- [ ] Conferir que `/h2-console` nao esta disponivel.

## 7. Backup

- [ ] Gerar backup com script.
- [ ] Conferir arquivo em `backups/`.
- [ ] Conferir que backup nao aparece no Git.
- [ ] Testar restore em ambiente local seguro.

## 8. Persistencia

- [ ] Parar containers sem `-v`.
- [ ] Subir containers novamente.
- [ ] Confirmar que dados persistiram.

## 9. Resultado

- [ ] Ambiente local limpo aprovado.
- [ ] Pontos encontrados anotados.
- [ ] Pronto para testar deploy em VPS.
