--###########################################################
-- SQL para buscar logs da pesquisa de funcionalidade do Aghu.
-- Executa a busca de forma recursiva e monta o caminho de Menu para acessar a tela que foi pesquisa.
-- Faz joins com as tabelas de Servidor de Pessoa Fisica para pegar dados do operador do sistema.
-- Ordena por data de criaÃ§Ã£o do registro de forma ascendente.

WITH RECURSIVE search_moves(id, parent_id, nome, level, path, checkmate, url) as (
	select m.id, m.parent_id, m.nome, 1 as level
	, coalesce(m.nome, '') || ' > ' as path
	, false as checkmate
	, m.url
	from casca.csc_menu m
	where m.id in (select parent_id from casca.csc_menu)
    union all
	select m1.id, m1.parent_id, m1.nome, sm.level + 1 as level
	, sm.path || coalesce(m1.nome) || CASE WHEN m1.url is null THEN ' > ' ELSE '.' END as path
	, true as checkmate
	, m1.url
	from casca.csc_menu m1, search_moves sm 
	where m1.parent_id = sm.id
) SELECT pesq_func.criado_em
, pesq_func.menu_pesquisado
, pesq_func.url
, serv.matricula
, serv.vin_codigo
, serv.usuario
, pes.codigo
, pes.nome
--, menu.id, menu.path, menu.level, menu.url
, (select path from search_moves where url = pesq_func.url and level = (select max(level) from search_moves where url = pesq_func.url)) as path
, (select max(level) from search_moves where url = pesq_func.url) as level
FROM AGH.AGH_PESQUISA_MENU_LOG pesq_func
    left outer join agh.rap_servidores serv 
        on serv.matricula = pesq_func.ser_matricula 
        and serv.vin_codigo = pesq_func.ser_vin_codigo 
        left outer join agh.rap_pessoas_fisicas pes on pes.codigo = serv.pes_codigo
    --left outer join search_moves menu on menu.url = pesq_func.url
where pesq_func.criado_em between (current_date - integer '30') and current_date
order by pesq_func.criado_em asc

