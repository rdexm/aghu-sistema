package br.gov.mec.aghu.compras.dao;

import org.hibernate.Query;

import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;
@SuppressWarnings({"PMD.AvoidDuplicateLiterals","PMD.ConsecutiveLiteralAppends","PMD.ConsecutiveAppendsShouldReuse"})
public class RelatorioEntradasSemEmpenhoSemAssinaturaAFOracleQueryBuilder extends QueryBuilder<Query>{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5770000518584772891L;

	
	@Override
	protected Query createProduct() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	protected void doBuild(Query aProduct) {
		// TODO Auto-generated method stub
		
	}
	
	public String montarQueryListarRelatorioEntradasSemEmpenhoSemAssinaturaAFOracle(){
		
		StringBuilder query = new StringBuilder(4000);	
						selectSemAssinaturaAlguma(query); 
						query.append("		UNION"); 
						selectSemAssinaturaManual(query); 
						query.append("		UNION ");
						selectSemEmpenhoAlgum(query);
						query.append("		UNION ");
						selectSemEmpenhoVigente(query);
						query.append("		UNION ");
						selectSemEmpenhoSuficiente(query);
						query.append("		UNION ");
						selectEntreAntEmisNF(query);
					    query.append("ORDER  BY 1, ");
						query.append("2 ");
		return query.toString();
	}
	private void selectEntreAntEmisNF(StringBuilder query) {
		query.append("		SELECT 'Entr Ant Emis NF'                  SITUACAO, ");
		query.append("		       afn.numero                          afn_numero, ");
		query.append("		       frn.razao_social                    FORNECEDOR, ");
		query.append("		       numero_lista                        CAMPO021, ");
		query.append("				AFN.pfr_lct_numero ");
		query.append(" ||'/' ");
		query.append(" ||AFN.nro_complemento               AF, ");
		query.append(" afn.ind_situacao                    SIT_AF, ");
		query.append(" nrs.seq                             NR, ");
		query.append(" nrs.dt_geracao DTENTRADA, ");
		query.append(" dfe.dt_emissao DTEMISNF, ");
		query.append(" lct.mlc_codigo                      MODLLICT, ");
		query.append(" lct.artigo_licitacao                ARTIGO, ");
		query.append(" lct.inciso_artigo_licitacao         INCISO ");
		query.append(" FROM   sco_fornecedores frn, ");
		query.append(" sco_licitacoes lct, ");
		query.append(" sce_documento_fiscal_entradas dfe, ");
		query.append(" sco_listas_siafi_fonte_rec lfr, ");
		query.append(" sco_listas_siafi lst, ");
		query.append(" sco_autorizacoes_forn afn, ");
		query.append(" sce_nota_recebimentos nrs ");
		query.append(" WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND"); 
		query.append("                                 Coalesce(lfr.dt_final_empenho, SYSDATE + 1)"); 
		query.append("	  AND dfe.seq = nrs.dfe_seq ");
		query.append("    AND afn.numero = afn_numero ");
		query.append("   AND frn.numero = afn.pfr_frn_numero"); 
		query.append("   AND lct.numero = afn.pfr_lct_numero ");
		query.append("   AND lfr.ind_situacao = 'A' ");
		query.append("   AND Substr(lst.numero_lista, 1, 7) = To_char(SYSDATE, 'yyyy')"); 
		query.append("                                           ||'LI' ");
		query.append("                                   ||lfr.seq_lista"); 
		query.append("  AND lst.iaf_afn_numero (+) = nrs.afn_numero ");
		query.append(" AND nrs.dt_geracao < dfe.dt_emissao ");
	}
	private void selectSemEmpenhoSuficiente(StringBuilder query) {
		query.append("		SELECT 'Sem Empenho Suficiente'            SITUACAO, ");
		query.append("		       afn.numero                          afn_numero, ");
		query.append("		       frn.razao_social                    FORNECEDOR, ");
		query.append("		       numero_lista                        CAMPO021, ");
		query.append("		       AFN.pfr_lct_numero ||'/' ||AFN.nro_complemento AF, ");
		query.append("		       afn.ind_situacao                    SIT_AF, ");
		query.append("		       nrs.seq                             NR, ");
		query.append("		       nrs.dt_geracao DTENTRADA, ");
		query.append("		       dfe.dt_emissao DTEMISNF, ");
		query.append("		       lct.mlc_codigo                      MODLLICT, ");
		query.append("		       lct.artigo_licitacao                ARTIGO, ");
		query.append("		       lct.inciso_artigo_licitacao         INCISO ");
		query.append("		FROM   sco_fornecedores frn, ");
		query.append("		       sco_licitacoes lct, ");
		query.append("		       sce_documento_fiscal_entradas dfe, ");
		query.append("		       sco_listas_siafi_fonte_rec lfr, ");
		query.append("		       sco_listas_siafi lst, ");
		query.append("		       sco_autorizacoes_forn afn, ");
		query.append("		       sce_nota_recebimentos nrs ");
		query.append("		WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND ");
		query.append("		                                     Coalesce(lfr.dt_final_empenho, SYSDATE + 1) ");
		query.append("		       AND dfe.seq = nrs.dfe_seq ");
		query.append("		       AND afn.numero = afn_numero ");
		query.append("		       AND frn.numero = afn.pfr_frn_numero ");
		query.append("		       AND lct.numero = afn.pfr_lct_numero ");
		query.append("		       AND lfr.ind_situacao = 'A' ");
		query.append("		       AND Substr(lst.numero_lista, 1, 7) = To_char(SYSDATE, 'yyyy') ");
		query.append("		                                            ||'LI' ");
		query.append("		                                            ||lfr.seq_lista ");
		query.append("		       AND lst.iaf_afn_numero (+) = nrs.afn_numero ");
		query.append("		       AND afn_numero IN (SELECT afn_numero ");
		query.append("		                          FROM   sco_af_empenhos ");
		query.append("		                          WHERE  afn_numero = nrs.afn_numero ");
		query.append("		                                 AND nro_lista_itens_af_siafi = lfr.seq_lista ");
		query.append("		                                                                ||afn_numero ");
		query.append("		                                 AND numero <> 'PEND') ");
		query.append("             and (SELECT SUM(valor_efetivado) ");
		query.append("             FROM   sco_itens_autorizacao_forn ");
		query.append("             WHERE  afn_numero = afn.numero) >=");
		query.append("             (SELECT Coalesce(SUM(valor),0)"); 
		query.append("             FROM   sco_af_empenhos ");
		query.append("             WHERE  afn_numero = afn.numero)");
	}
	private void selectSemEmpenhoVigente(StringBuilder query) {
		query.append("		SELECT 'Sem Empenho Vigente'               SITUACAO, ");
		query.append("		       afn.numero                          afn_numero, ");
		query.append("		       frn.razao_social                    FORNECEDOR, ");
		query.append("		       numero_lista                        CAMPO021, ");
		query.append("		       AFN.pfr_lct_numero ");
		query.append("		       ||'/' ");
		query.append("		       ||AFN.nro_complemento               AF, ");
		query.append("		       afn.ind_situacao                    SIT_AF, ");
		query.append("		       nrs.seq                             NR, ");
		query.append("		       nrs.dt_geracao DTENTRADA, ");
		query.append("		       dfe.dt_emissao DTEMISNF, ");
		query.append("		       lct.mlc_codigo                      MODLLICT, ");
		query.append("		       lct.artigo_licitacao                ARTIGO, ");
		query.append("		       lct.inciso_artigo_licitacao         INCISO ");
		query.append("		FROM   sco_fornecedores frn, ");
		query.append("		       sco_licitacoes lct, ");
		query.append("		       sce_documento_fiscal_entradas dfe, ");
		query.append("		       sco_listas_siafi_fonte_rec lfr, ");
		query.append("		       sco_listas_siafi lst, ");
		query.append("		       sco_autorizacoes_forn afn, ");
		query.append("		       sce_nota_recebimentos nrs ");
		query.append("		WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND ");
		query.append("		                                     Coalesce(lfr.dt_final_empenho, SYSDATE + 1) ");
		//-- usar essa linha para colocar no documento 
		query.append("		       AND dfe.seq = nrs.dfe_seq ");
		query.append("		       AND afn.numero = afn_numero ");
		query.append("		       AND frn.numero = afn.pfr_frn_numero ");
		query.append("		       AND lct.numero = afn.pfr_lct_numero ");
		query.append("		       AND lfr.ind_situacao = 'A' ");
		query.append("		       AND Substr(lst.numero_lista, 1, 7) = To_char(SYSDATE, 'yyyy') ");
		query.append("		                                            ||'LI' ");
		query.append("		                                            ||lfr.seq_lista ");
		query.append("		       AND lst.iaf_afn_numero (+) = nrs.afn_numero ");
		query.append("		       AND afn_numero NOT IN (SELECT afn_numero ");
		query.append("		                              FROM   sco_af_empenhos ");
		query.append("		                              WHERE  afn_numero = nrs.afn_numero ");
		query.append("		                                     AND nro_lista_itens_af_siafi = ");
		query.append("		                                         lfr.seq_lista ");
		query.append("		                                         ||afn_numero ");
		query.append("		                                     AND numero <> 'PEND') ");
		query.append("		       AND afn_numero IN (SELECT afn_numero ");
		query.append("		                          FROM   sco_af_empenhos ");
		query.append("		                          WHERE  afn_numero = nrs.afn_numero ");
		query.append("		                                 AND numero <> 'PEND') ");
	}
	private void selectSemEmpenhoAlgum(StringBuilder query) {
		query.append("		SELECT 'Sem Empenho algum'                 SITUACAO,"); 
		query.append("		       afn.numero                          afn_numero,"); 
		query.append("		       frn.razao_social                    FORNECEDOR, ");
		query.append("		       numero_lista                        CAMPO021, ");
		query.append("		       AFN.pfr_lct_numero ");
		query.append("		       ||'/' ");
		query.append("		       ||AFN.nro_complemento               AF,"); 
		query.append("		       afn.ind_situacao                    SIT_AF,"); 
		query.append("		       nrs.seq                             NR, ");
		query.append("		       nrs.dt_geracao DTENTRADA,"); 
		query.append("		       dfe.dt_emissao DTEMISNF,"); 
		query.append("		       lct.mlc_codigo                      MODLLICT, ");
		query.append("		       lct.artigo_licitacao                ARTIGO, ");
		query.append("		       lct.inciso_artigo_licitacao         INCISO ");
		query.append("		FROM   sco_fornecedores frn, ");
		query.append("		       sco_licitacoes lct, ");
		query.append("		       sce_documento_fiscal_entradas dfe, ");
		query.append("		       sco_listas_siafi_fonte_rec lfr, ");
		query.append("		       sco_listas_siafi lst, ");
		query.append("		       sco_autorizacoes_forn afn, ");
		query.append("		       sce_nota_recebimentos nrs ");
		query.append("		WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND ");
		query.append("		                                     Coalesce(lfr.dt_final_empenho, SYSDATE + 1) ");
		query.append("		       AND dfe.seq = nrs.dfe_seq ");
		query.append("		       AND afn.numero = afn_numero ");
		query.append("		       AND frn.numero = afn.pfr_frn_numero ");
		query.append("		       AND lct.numero = afn.pfr_lct_numero ");
		query.append("		       AND lfr.ind_situacao = 'A' ");
		query.append("		       AND Substr(lst.numero_lista, 1, 7) = To_char(SYSDATE, 'yyyy') ");
		query.append("		                                            ||'LI' ");
		query.append("		                                            ||lfr.seq_lista ");
		query.append("		       AND lst.iaf_afn_numero (+) = nrs.afn_numero ");
		query.append("		       AND afn_numero NOT IN (SELECT afn_numero ");
		query.append("		                              FROM   sco_af_empenhos ");
		query.append("		                              WHERE  afn_numero = nrs.afn_numero ");
		query.append("		                                     AND numero <> 'PEND') ");
	}
	private void selectSemAssinaturaManual(StringBuilder query) {
		query.append("		SELECT 'Sem Assinatura manual'             SITUACAO,"); 
		query.append("		       afn.numero                          afn_numero, ");
		query.append("		       frn.razao_social                    FORNECEDOR, ");
		query.append("		       numero_lista                        CAMPO021, ");
		query.append("		       AFN.pfr_lct_numero ");
		query.append("		       ||'/' ");
		query.append("		       ||AFN.nro_complemento               AF,"); 
		query.append("		       afn.ind_situacao                    SIT_AF,"); 
		query.append("		       nrs.seq                             NR,"); 
		query.append("		       nrs.dt_geracao DTENTRADA,"); 
		query.append("		       dfe.dt_emissao DTEMISNF,"); 
		query.append("		       lct.mlc_codigo                      MODLLICT, ");
		query.append("		       lct.artigo_licitacao                ARTIGO, ");
		query.append("		       lct.inciso_artigo_licitacao         INCISO ");
		query.append("		FROM   sco_fornecedores frn, ");
		query.append("		       sco_licitacoes lct, ");
		query.append("		       sce_documento_fiscal_entradas dfe,"); 
		query.append("		       sco_listas_siafi_fonte_rec lfr, ");
		query.append("		       sco_listas_siafi lst, ");
		query.append("		       sco_autorizacoes_forn afn,"); 
		query.append("		       sce_nota_recebimentos nrs ");
		query.append("		WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND"); 
		query.append("		                                     Coalesce(lfr.dt_final_empenho, SYSDATE + 1)"); 
		query.append("		       AND dfe.seq = nrs.dfe_seq ");
		query.append("		       AND afn.numero = afn_numero ");
		query.append("		       AND frn.numero = afn.pfr_frn_numero"); 
		query.append("		       AND lct.numero = afn.pfr_lct_numero ");
		query.append("		       AND lfr.ind_situacao = 'A' ");
		query.append("		       AND Substr(lst.numero_lista, 1, 7) = To_char(SYSDATE, 'yyyy')"); 
		query.append("		                                            ||'LI' ");
		query.append("		                                            ||lfr.seq_lista"); 
		query.append("		       AND lst.iaf_afn_numero (+) = nrs.afn_numero ");
		query.append("		       AND afn_numero NOT IN (SELECT numero"); 
		query.append("		                              FROM   sco_af_jn"); 
		query.append("		                              WHERE  numero = afn_numero"); 
		query.append("		                                     AND ind_aprovada IN ( 'A', 'E' )"); 
		query.append("		                                     AND ser_matricula_assina_coord IS NOT NULL"); 
		query.append("		                                     AND dt_assinatura_coord IS NOT NULL)"); 
		query.append("		       AND afn_numero IN (SELECT numero ");
		query.append("		                          FROM   sco_af_jn ");
		query.append("		                          WHERE  numero = afn_numero"); 
		query.append("		                                 AND ind_aprovada IN ( 'A', 'E' ))");
	}
	private void selectSemAssinaturaAlguma(StringBuilder query) {
		query.append(" SELECT 'Sem Assinatura alguma'             SITUACAO,"); 
		query.append(" afn.numero                          afn_numero,"); 
		query.append(" frn.razao_social                    FORNECEDOR,"); 
		query.append(" numero_lista                        CAMPO021,"); 
		query.append(" AFN.pfr_lct_numero"); 
		query.append(" ||'/'"); 
		query.append(" ||AFN.nro_complemento               AF,"); 
		query.append(" afn.ind_situacao                    SIT_AF,"); 
		query.append(" nrs.seq                             NR,"); 
		query.append(" nrs.dt_geracao DTENTRADA,"); 
		query.append(" dfe.dt_emissao DTEMISNF,"); 
		query.append(" lct.mlc_codigo                      MODLLICT,"); 
		query.append(" lct.artigo_licitacao                ARTIGO,"); 
		query.append(" lct.inciso_artigo_licitacao         INCISO"); 
		query.append(" FROM   sco_fornecedores frn,"); 
		query.append(" sco_licitacoes lct,"); 
		query.append("		       sce_documento_fiscal_entradas dfe, ");
		query.append("		       sco_listas_siafi_fonte_rec lfr, ");
		query.append("		       sco_listas_siafi lst, ");
		query.append("		       sco_autorizacoes_forn afn, ");
		query.append("		       sce_nota_recebimentos nrs ");
		query.append("		WHERE  nrs.dt_geracao BETWEEN lfr.dt_inicial_empenho AND ");
		query.append("		                                     Coalesce(lfr.dt_final_empenho, SYSDATE + 1) ");
		query.append("		       AND dfe.seq = nrs.dfe_seq ");
		query.append("		       AND afn.numero = afn_numero ");
		query.append("		       AND frn.numero = afn.pfr_frn_numero"); 
		query.append("		       AND lct.numero = afn.pfr_lct_numero ");
		query.append("		       AND lfr.ind_situacao = 'A' ");
		query.append("		       AND Substr(lst.numero_lista, 1, 7) = To_char(SYSDATE, 'yyyy')"); 
		query.append("		                                            ||'LI' ");
		query.append("		                                            ||lfr.seq_lista"); 
		query.append("		       AND lst.iaf_afn_numero (+) = nrs.afn_numero ");
		query.append("		       AND afn_numero NOT IN (SELECT numero ");
		query.append("		                              FROM   sco_af_jn ");
		query.append("		                              WHERE  numero = afn_numero"); 
		query.append("		                                     AND ind_aprovada IN ( 'A', 'E' ))");
	}
}
