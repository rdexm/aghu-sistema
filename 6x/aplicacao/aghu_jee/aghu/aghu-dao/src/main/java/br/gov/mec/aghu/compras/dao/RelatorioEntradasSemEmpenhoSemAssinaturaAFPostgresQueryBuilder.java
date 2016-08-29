package br.gov.mec.aghu.compras.dao;

import org.hibernate.Query;

import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class RelatorioEntradasSemEmpenhoSemAssinaturaAFPostgresQueryBuilder extends QueryBuilder<Query>{

	private static final long serialVersionUID = -5100359735051179478L;

	@Override
	protected Query createProduct() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void doBuild(Query aProduct) {
		// TODO Auto-generated method stub
		
	}
	
	public String montarQueryListarRelatorioEntradasSemEmpenhoSemAssinaturaAFPostgres(){
		StringBuilder query = new StringBuilder(4000);
		
			selectSemAssinaturaAlguma(query);
			query.append("    UNION");
			selectSemAssinaturaManual(query);
			query.append("    UNION");
			selectSemEmpenhoAlgum(query);
			query.append("    UNION");
			selectSemEmpenhoVigente(query);
			query.append("    UNION");
			selectSemEmpenhoSuficiente(query);
			query.append("    UNION");
			selectEntrAntEmisNF(query);
			query.append("    ORDER  BY");
			query.append("        1,");
			query.append("        2;");
			
		return query.toString();
	}

	private void selectEntrAntEmisNF(StringBuilder query) {
		query.append("    select distinct 'Entr Ant Emis NF' SITUACAO,");
		query.append("        afn.numero afn_numero,");
		query.append("        frn.razao_social FORNECEDOR, ");
		query.append("        numero_lista CAMPO021,");
		query.append("        afn.pfr_lct_numero||'/'||AFN.nro_complemento AF,");
		query.append("        afn.ind_situacao SIT_AF, ");
		query.append("        nrs.seq NR,");
		query.append("        nrs.dt_geracao DTENTRADA,");
		query.append("        dfe.dt_emissao DTEMISNF,");
		query.append("        lct.mlc_codigo MODLLICT,");
		query.append("        lct.artigo_licitacao ARTIGO,");
		query.append("        lct.inciso_artigo_licitacao INCISO ");
		query.append("    FROM agh.sce_nota_recebimentos nrs ");
		query.append("    left outer join agh.sco_listas_siafi lst on lst.iaf_afn_numero = nrs.afn_numero ");
		query.append("    inner join agh.sce_documento_fiscal_entradas dfe on dfe.seq = nrs.dfe_seq ");
		query.append("    inner join agh.sco_autorizacoes_forn afn on afn.numero = nrs.afn_numero  ");
		query.append("    inner join agh.sco_licitacoes lct on lct.numero = afn.pfr_lct_numero  ");
		query.append("    inner join agh.sco_fornecedores frn on frn.numero = afn.pfr_frn_numero  ");
		query.append("    inner join agh.sco_listas_siafi_fonte_rec lfr on Substr(lst.numero_lista, 1, 7) = To_char(now() + interval '1 day','yyyy') ||'LI' ||lfr.seq_lista ");
		query.append("    WHERE");
		query.append("        nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND Coalesce(lfr.dt_final_empenho, now() + interval '1 day') ");
		query.append("        AND lfr.ind_situacao = 'A' ");
		query.append("        AND nrs.dt_geracao < dfe.dt_emissao ");
	}

	private void selectSemEmpenhoSuficiente(StringBuilder query) {
		query.append("    select 'Sem Empenho Suficiente' SITUACAO,");
		query.append("        afn.numero afn_numero,");
		query.append("        frn.razao_social FORNECEDOR, ");
		query.append("        numero_lista CAMPO021,");
		query.append("        afn.pfr_lct_numero||'/'||AFN.nro_complemento AF,");
		query.append("        afn.ind_situacao SIT_AF, ");
		query.append("        nrs.seq NR,");
		query.append("        nrs.dt_geracao DTENTRADA,");
		query.append("        dfe.dt_emissao DTEMISNF,");
		query.append("        lct.mlc_codigo MODLLICT,");
		query.append("        lct.artigo_licitacao ARTIGO,");
		query.append("        lct.inciso_artigo_licitacao INCISO ");
		query.append("    FROM");
		query.append("        agh.sce_nota_recebimentos nrs left outer join agh.sco_listas_siafi lst on lst.iaf_afn_numero = nrs.afn_numero ");
		query.append("    inner join agh.sce_documento_fiscal_entradas dfe on dfe.seq = nrs.dfe_seq ");
		query.append("    inner join agh.sco_autorizacoes_forn afn on afn.numero = nrs.afn_numero  ");
		query.append("    inner join agh.sco_licitacoes lct on lct.numero = afn.pfr_lct_numero  ");
		query.append("    inner join agh.sco_fornecedores frn on frn.numero = afn.pfr_frn_numero  ");
		query.append("    inner join agh.sco_listas_siafi_fonte_rec lfr on Substr(lst.numero_lista,1, 7) = To_char(now() + interval '1 day','yyyy') ||'LI' ||lfr.seq_lista ");  
		query.append("    WHERE");
		query.append("        nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND Coalesce(lfr.dt_final_empenho, now() + interval '1 day') ");
		query.append("        AND lfr.ind_situacao = 'A'  ");
		query.append("        AND EXISTS (SELECT");
		query.append("                afn_numero      ");
		query.append("            FROM");
		query.append("                agh.sco_af_empenhos     ");  
		query.append("            WHERE");
		query.append("                afn_numero = nrs.afn_numero       ");
		query.append("                AND CAST(nro_lista_itens_af_siafi AS TEXT) = CAST(lfr.seq_lista AS TEXT) || CAST(afn_numero AS TEXT)    ");   
		query.append("                AND numero <> 'PEND') ");
		query.append("                and (SELECT SUM(valor_efetivado) ");
		query.append("                FROM   agh.sco_itens_autorizacao_forn ");
		query.append("                WHERE  afn_numero = afn.numero) >=");
		query.append("                (SELECT Coalesce(SUM(valor),0)"); 
		query.append("                FROM   agh.sco_af_empenhos ");
		query.append("                WHERE  afn_numero = afn.numero)");
	}

	private void selectSemEmpenhoVigente(StringBuilder query) {
		query.append("    select 'Sem Empenho Vigente' SITUACAO,");
		query.append("        afn.numero afn_numero,");
		query.append("        frn.razao_social FORNECEDOR, ");
		query.append("        numero_lista CAMPO021,");
		query.append("        afn.pfr_lct_numero||'/'||AFN.nro_complemento AF,");
		query.append("        afn.ind_situacao SIT_AF, ");
		query.append("        nrs.seq NR,");
		query.append("        nrs.dt_geracao DTENTRADA,");
		query.append("        dfe.dt_emissao DTEMISNF,");
		query.append("        lct.mlc_codigo MODLLICT,");
		query.append("        lct.artigo_licitacao ARTIGO,");
		query.append("        lct.inciso_artigo_licitacao INCISO ");
		query.append("    FROM agh.sce_nota_recebimentos nrs ");
		query.append("    left outer join agh.sco_listas_siafi lst on lst.iaf_afn_numero = nrs.afn_numero ");
		query.append("    inner join agh.sce_documento_fiscal_entradas dfe on dfe.seq = nrs.dfe_seq ");
		query.append("    inner join agh.sco_autorizacoes_forn afn on afn.numero = nrs.afn_numero  ");
		query.append("    inner join agh.sco_licitacoes lct on lct.numero = afn.pfr_lct_numero  ");
		query.append("    inner join agh.sco_fornecedores frn on frn.numero = afn.pfr_frn_numero  ");
		query.append("    inner join agh.sco_listas_siafi_fonte_rec lfr on Substr(lst.numero_lista, 1,7) = To_char(now() + interval '1 day','yyyy') ||'LI' ||lfr.seq_lista");
		query.append("    WHERE");
		query.append("        nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND Coalesce(lfr.dt_final_empenho, now() + interval '1 day')"); 
		query.append("        AND lfr.ind_situacao = 'A' ");
		query.append("        AND NOT EXISTS ( SELECT afn_numero ");
		query.append("            FROM");
		query.append("                agh.sco_af_empenhos       ");
		query.append("            WHERE");
		query.append("                afn_numero = nrs.afn_numero        ");
		query.append("                AND CAST(nro_lista_itens_af_siafi AS TEXT) = CAST(lfr.seq_lista AS TEXT) || CAST(afn_numero AS TEXT)   ");    
		query.append("                AND numero <> 'PEND') ");
		query.append("        AND EXISTS (SELECT afn_numero    ");
		query.append("            FROM");
		query.append("                agh.sco_af_empenhos      ");
		query.append("            WHERE");
		query.append("                afn_numero = nrs.afn_numero      "); 
		query.append("                AND numero <> 'PEND') ");
	}

	private void selectSemEmpenhoAlgum(StringBuilder query) {
		query.append("    select distinct 'Sem Empenho algum'  SITUACAO,");
		query.append("        afn.numero afn_numero,");
		query.append("        frn.razao_social FORNECEDOR, ");
		query.append("        numero_lista CAMPO021,");
		query.append("        afn.pfr_lct_numero||'/'||AFN.nro_complemento AF,");
		query.append("        afn.ind_situacao SIT_AF, ");
		query.append("        nrs.seq NR,");
		query.append("        nrs.dt_geracao DTENTRADA,");
		query.append("        dfe.dt_emissao DTEMISNF,");
		query.append("        lct.mlc_codigo MODLLICT,");
		query.append("        lct.artigo_licitacao ARTIGO,");
		query.append("        lct.inciso_artigo_licitacao INCISO ");
		query.append("    FROM agh.sce_nota_recebimentos nrs ");
		query.append("    left outer join agh.sco_listas_siafi lst on lst.iaf_afn_numero = nrs.afn_numero ");
		query.append("    inner join agh.sce_documento_fiscal_entradas dfe on dfe.seq = nrs.dfe_seq ");
		query.append("    inner join agh.sco_autorizacoes_forn afn on afn.numero = nrs.afn_numero  ");
		query.append("    inner join agh.sco_licitacoes lct on lct.numero = afn.pfr_lct_numero  ");
		query.append("    inner join agh.sco_fornecedores frn on frn.numero = afn.pfr_frn_numero  ");
		query.append("    inner join agh.sco_listas_siafi_fonte_rec lfr on Substr(lst.numero_lista, 1, 7) = To_char(now() + interval '1 day','yyyy') ||'LI' ||lfr.seq_lista ");
		query.append("    WHERE");
		query.append("        nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND Coalesce(lfr.dt_final_empenho, now() + interval '1 day') ");
		query.append("        AND lfr.ind_situacao = 'A'  ");
		query.append("        AND not exists ( SELECT afn_numero      ");
		query.append("            FROM  agh.sco_af_empenhos      "); 
		query.append("            WHERE");
		query.append("                afn_numero = nrs.afn_numero     ");  
		query.append("                AND numero <> 'PEND') ");
	}

	private void selectSemAssinaturaManual(StringBuilder query) {
		query.append("    select distinct 'Sem Assinatura manual' SITUACAO,");
		query.append("        afn.numero afn_numero,");
		query.append("        frn.razao_social FORNECEDOR, ");
		query.append("        numero_lista CAMPO021,");
		query.append("        afn.pfr_lct_numero||'/'||AFN.nro_complemento AF,");
		query.append("        afn.ind_situacao SIT_AF, ");
		query.append("        nrs.seq NR,");
		query.append("        nrs.dt_geracao DTENTRADA,");
		query.append("        dfe.dt_emissao DTEMISNF,");
		query.append("        lct.mlc_codigo MODLLICT,");
		query.append("        lct.artigo_licitacao ARTIGO,");
		query.append("        lct.inciso_artigo_licitacao INCISO ");
		query.append("    FROM   agh.sce_nota_recebimentos nrs ");
		query.append("    left outer join agh.sco_listas_siafi lst on lst.iaf_afn_numero = nrs.afn_numero ");
		query.append("    inner join agh.sce_documento_fiscal_entradas dfe on dfe.seq = nrs.dfe_seq ");
		query.append("    inner join agh.sco_autorizacoes_forn afn on afn.numero = nrs.afn_numero  ");
		query.append("    inner join agh.sco_licitacoes lct on lct.numero = afn.pfr_lct_numero  ");
		query.append("    inner join agh.sco_fornecedores frn on frn.numero = afn.pfr_frn_numero  ");
		query.append("    inner join agh.sco_listas_siafi_fonte_rec lfr on Substr(lst.numero_lista, 1, 7) = To_char(now() + interval '1 day','yyyy') ||'LI' ||lfr.seq_lista ");
		query.append("    WHERE nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND Coalesce(lfr.dt_final_empenho, now() + interval '1 day') ");
		query.append("        AND lfr.ind_situacao = 'A'  ");
		query.append("        AND NOT EXISTS ( SELECT numero  ");
		query.append("            FROM agh.sco_af_jn   ");
		query.append("            WHERE");
		query.append("                numero = nrs.afn_numero     ");
		query.append("                AND ind_aprovada = 'A' ");
		query.append("                OR ind_aprovada = 'E'    ");
		query.append("                AND ser_matricula_assina_coord IS NOT NULL    ");
		query.append("                AND dt_assinatura_coord IS NOT NULL) ");
		query.append("        AND EXISTS (SELECT numero   ");
		query.append("            FROM");
		query.append("                agh.sco_af_jn ");
		query.append("            WHERE");
		query.append("                numero = nrs.afn_numero     ");
		query.append("                AND ind_aprovada = 'A' ");
		query.append("                OR ind_aprovada = 'E') ");
	}

	private void selectSemAssinaturaAlguma(StringBuilder query) {
		query.append("select  distinct 'Sem Assinatura alguma' SITUACAO,");
		query.append("        afn.numero afn_numero,");
		query.append("        frn.razao_social FORNECEDOR, ");
		query.append("        numero_lista CAMPO021,");
		query.append("        afn.pfr_lct_numero||'/'||AFN.nro_complemento AF,");
		query.append("        afn.ind_situacao SIT_AF, ");
		query.append("        nrs.seq NR,");
		query.append("        nrs.dt_geracao DTENTRADA,");
		query.append("        dfe.dt_emissao DTEMISNF,");
		query.append("        lct.mlc_codigo MODLLICT,");
		query.append("        lct.artigo_licitacao ARTIGO,");
		query.append("        lct.inciso_artigo_licitacao INCISO ");
		query.append("    FROM   agh.sce_nota_recebimentos nrs ");
		query.append("    left outer join agh.sco_listas_siafi lst on lst.iaf_afn_numero = nrs.afn_numero ");
		query.append("    inner join agh.sce_documento_fiscal_entradas dfe on dfe.seq = nrs.dfe_seq ");
		query.append("    inner join agh.sco_autorizacoes_forn afn on afn.numero = nrs.afn_numero  ");
		query.append("    inner join agh.sco_licitacoes lct on lct.numero = afn.pfr_lct_numero  ");
		query.append("    inner join agh.sco_fornecedores frn on frn.numero = afn.pfr_frn_numero  ");
		query.append("    inner join agh.sco_listas_siafi_fonte_rec lfr on Substr(lst.numero_lista, 1,7) = To_char(now() + interval '1 day','yyyy') ||'LI' ||lfr.seq_lista");
		query.append("    WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND Coalesce(lfr.dt_final_empenho, now() + interval '1 day') ");
		query.append("        AND lfr.ind_situacao = 'A' ");
		query.append("        AND NOT EXISTS (SELECT  numero  ");
		query.append("            FROM agh.sco_af_jn ");
		query.append("            WHERE numero = nrs.afn_numero   ");
		query.append("                AND ind_aprovada = 'A' ");
		query.append("                OR  ind_aprovada = 'E') ");
	}

}
