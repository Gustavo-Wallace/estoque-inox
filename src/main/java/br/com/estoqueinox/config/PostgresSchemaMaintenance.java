package br.com.estoqueinox.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("postgres")
public class PostgresSchemaMaintenance implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public PostgresSchemaMaintenance(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        ajustarVendaItensLegados();
        ajustarStatusLegados();
    }

    private void ajustarVendaItensLegados() {
        jdbcTemplate.execute("""
                alter table venda_itens
                add column if not exists quantidade_cancelada integer default 0
                """);
        jdbcTemplate.update("""
                update venda_itens
                set quantidade_cancelada = case
                    when status = 'CANCELADO' then quantidade
                    else 0
                end
                where quantidade_cancelada is null
                """);
        jdbcTemplate.execute("""
                alter table venda_itens
                alter column quantidade_cancelada set default 0
                """);
        jdbcTemplate.execute("""
                alter table venda_itens
                alter column quantidade_cancelada set not null
                """);
    }

    private void ajustarStatusLegados() {
        jdbcTemplate.execute("""
                alter table vendas
                alter column status type varchar(30)
                """);
        jdbcTemplate.execute("""
                alter table venda_itens
                alter column status type varchar(30)
                """);
        recriarCheckConstraintStatusVendas();
        recriarCheckConstraintStatusVendaItens();
    }

    private void recriarCheckConstraintStatusVendas() {
        jdbcTemplate.execute("""
                do $$
                declare constraint_record record;
                begin
                    for constraint_record in
                        select conname
                        from pg_constraint
                        where conrelid = 'vendas'::regclass
                          and contype = 'c'
                          and pg_get_constraintdef(oid) ilike '%status%'
                    loop
                        execute format('alter table vendas drop constraint if exists %I', constraint_record.conname);
                    end loop;
                end $$;
                """);
        jdbcTemplate.execute("""
                alter table vendas
                add constraint ck_vendas_status
                check (status in ('CONCLUIDA', 'PARCIALMENTE_CANCELADA', 'CANCELADA'))
                """);
    }

    private void recriarCheckConstraintStatusVendaItens() {
        jdbcTemplate.execute("""
                do $$
                declare constraint_record record;
                begin
                    for constraint_record in
                        select conname
                        from pg_constraint
                        where conrelid = 'venda_itens'::regclass
                          and contype = 'c'
                          and pg_get_constraintdef(oid) ilike '%status%'
                    loop
                        execute format('alter table venda_itens drop constraint if exists %I', constraint_record.conname);
                    end loop;
                end $$;
                """);
        jdbcTemplate.execute("""
                alter table venda_itens
                add constraint ck_venda_itens_status
                check (status in ('CONCLUIDO', 'PARCIALMENTE_CANCELADO', 'CANCELADO'))
                """);
    }
}
