# Checklist pre-piloto

Use este checklist antes de testar o sistema com dados reais da banca.

- [ ] Trocar as senhas padrao `admin123` e `venda123`.
- [ ] Criar usuarias reais.
- [ ] Desativar usuarios de teste que nao serao usados.
- [ ] Cadastrar ou revisar os produtos reais.
- [ ] Conferir categorias reais.
- [ ] Conferir estoque inicial de cada produto.
- [ ] Testar venda no celular.
- [ ] Testar venda com desconto.
- [ ] Testar cancelamento parcial de item.
- [ ] Testar cancelamento de venda inteira.
- [ ] Confirmar que vendedora nao acessa `/admin/**`.
- [ ] Confirmar que PostgreSQL esta rodando no ambiente de teste real.
- [ ] Confirmar que os dados persistem apos reiniciar a aplicacao.
- [ ] Testar backup manual do PostgreSQL.
- [ ] Fazer backup antes de inserir ou alterar dados reais.
- [ ] Nao usar `docker compose down -v` em ambiente com dados importantes.
- [ ] Guardar o arquivo de backup em local seguro.

