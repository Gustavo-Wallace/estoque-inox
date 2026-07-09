# Roteiro de teste manual

Este roteiro simula um dia de uso real do Estoque Inox em uma banca de bijuterias.

## Preparacao

1. Suba a aplicacao com H2 ou PostgreSQL.
2. Acesse `http://localhost:8080`.
3. Entre com `admin/admin123`.
4. Confira se os produtos simulados aparecem em `/produtos` ou `/admin/produtos`.

## Teste como ADMIN

1. Acesse o dashboard.
2. Abra `Categorias` e confirme as categorias:
   Brincos, Aneis, Colares, Pulseiras, Correntes, Conjuntos, Piercings e Tornozeleiras.
3. Abra `Produtos` e confirme os produtos simulados.
4. Cadastre uma nova vendedora em `Usuarios`.
5. Abra `Estoque`.
6. Registre uma entrada de estoque em um produto.
7. Registre um ajuste de estoque em outro produto.
8. Confira se as movimentacoes aparecem no historico de estoque.

## Teste de vendas

1. Acesse `Nova venda`.
2. Registre uma venda com multiplos produtos sem desconto.
3. Registre outra venda com desconto em um item.
4. Registre uma venda com 2 unidades do mesmo produto.
5. Abra o detalhe dessa venda.
6. Cancele apenas 1 unidade do item.
7. Confirme que a quantidade vendida, cancelada e ativa ficou correta.
8. Registre outra venda e cancele um produto individual.
9. Registre outra venda e cancele a venda inteira.
10. Confira se as quantidades canceladas voltaram ao estoque.

## Teste como VENDEDORA

1. Faca logout.
2. Entre com `vendedora/venda123` ou com a nova vendedora criada.
3. Confira que o dashboard mostra apenas Nova venda, Minhas vendas e Consulta de produtos.
4. Tente acessar `/admin/produtos`.
5. Confirme que o acesso e negado.
6. Registre uma venda.
7. Confirme que a vendedora ve apenas as vendas dela em `Minhas vendas`.

## Relatorios

1. Entre novamente como ADMIN.
2. Abra `Relatorios`.
3. Confira o resumo do dia.
4. Abra `Vendas por periodo`.
5. Confira vendas concluidas, parcialmente canceladas e canceladas.
6. Abra `Produtos mais vendidos`.
7. Confirme que produtos cancelados totalmente nao aumentam a quantidade vendida.
8. Abra `Estoque baixo`.
9. Confira os produtos com estoque menor ou igual ao minimo.
10. Abra `Vendas por vendedora`.
11. Confira os totais por usuaria.

## Backup e persistencia

1. Se estiver usando PostgreSQL, gere backup manual:

```cmd
scripts\backup-postgres.bat
```

2. Reinicie a aplicacao.
3. Confirme que usuarios, produtos, vendas e movimentacoes continuam salvos.
4. Rode `git status` e confirme que arquivos em `backups/` nao aparecem para commit.

