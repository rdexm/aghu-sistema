package br.gov.mec.aghu.exames.dao;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAgrpPesquisas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;

public class AelAgrpPesquisasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAgrpPesquisas> {
	
	private static final long serialVersionUID = -8270822335051684123L;


	/**
	 * Executa busca por descricao. Pesquisa exata.
	 * Filtra apenas por descricao.
	 * 
	 * @param descricao
	 * @return
	 */
	public AelAgrpPesquisas obterAelAgrpPesquisasPorDescricao(String descricao) {
		if (descricao == null) {
			throw new IllegalArgumentException("Criterio obrigatorio de pesquisa nao informado!");
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AelAgrpPesquisas.class);
		
		criteria.add(Restrictions.eq(AelAgrpPesquisas.Fields.DESCRICAO.toString(), descricao));
		
		return (AelAgrpPesquisas) executeCriteriaUniqueResult(criteria);
	}


	public List<AelAgrpPesquisas> obterAelAgrpPesquisasList(
			AelAgrpPesquisas filtros, Integer firstResult, Integer maxResults,
			String orderProperty, boolean asc) {
		
		final DetachedCriteria criteria = obterCriteriaAelAgrpPesquisasList(filtros);
		
		if(orderProperty == null){
			orderProperty = AelAgrpPesquisas.Fields.DESCRICAO.toString();
			asc=true;
		}
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	private DetachedCriteria obterCriteriaAelAgrpPesquisasList(final AelAgrpPesquisas filtros) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAgrpPesquisas.class);
		
		criteria.createAlias(AelAgrpPesquisas.Fields.RAP_SERVIDOR.toString(), "ser");
		
		if(filtros != null){
			if(filtros.getSeq() != null){
				criteria.add(Restrictions.eq(AelAgrpPesquisas.Fields.SEQ.toString(), filtros.getSeq()));
			}
			
			if(StringUtils.isNotEmpty(filtros.getDescricao())){
				criteria.add(Restrictions.ilike(AelAgrpPesquisas.Fields.DESCRICAO.toString(), filtros.getDescricao(), MatchMode.ANYWHERE));
			}
			
			if(filtros.getIndSituacao() != null){
				criteria.add(Restrictions.eq(AelAgrpPesquisas.Fields.IND_SITUACAO.toString(), filtros.getIndSituacao()));
			}
		}
		
		return criteria;
	}

	public Long obterAelAgrpPesquisasListCount(AelAgrpPesquisas filtros) {
		return executeCriteriaCount(obterCriteriaAelAgrpPesquisasList(filtros));
	}

	public BigDecimal obterValorExame(String exaSigla, Integer manSeq,
			AghUnidadesFuncionais unidadeFuncional, Date dthrLiberada,
			FatConvenioSaudePlano convenioSaudePlano, Boolean isOracle) {
		
		StringBuilder sqlSelect = new StringBuilder();
		
		montarSqlValorExame(sqlSelect);
		
		SQLQuery query = this.createSQLQuery(sqlSelect.toString());
		setarParametrosValorExame(exaSigla, manSeq, unidadeFuncional,
				dthrLiberada, convenioSaudePlano, query);
	 	
	 	@SuppressWarnings("unchecked")
	 	List<Object[]> lista = query.list();
	 	Iterator<Object[]> it = lista.listIterator();
	 	if(!it.hasNext()){
	 		return BigDecimal.ZERO;
	 	}
	 	BigDecimal valor = BigDecimal.ZERO;
	 	while (it.hasNext()) {
	 		Object[] obj = it.next();
	 		String xIndDiscDsc = (String) obj[0];
	 		BigDecimal xPercDsct = (BigDecimal) obj[1];
	 		BigDecimal zQtdeCsh = (BigDecimal) obj[2];
	 		BigDecimal bQtdeCshProf = (BigDecimal) obj[3];
	 		BigDecimal cValor = (BigDecimal) obj[4];
	 		BigDecimal flmValor = (BigDecimal) obj[5];
	 		BigDecimal bQtdeM2 = (BigDecimal) obj[6];
	 		BigDecimal zQtdeChMat = (BigDecimal) obj[7];
	 		BigDecimal bQtdeAprs = (BigDecimal) obj[8];
//	 		Integer aCodHcpa = (Integer) obj[9];
//	 		Integer xTpitCod = (Integer) obj[10];
	 		
	 		if(xIndDiscDsc == null){
	 			xIndDiscDsc = "N";
	 		}
	 		BigDecimal parte1 = BigDecimal.ZERO;
	 		BigDecimal parte2 = BigDecimal.ZERO;
	 		BigDecimal parte3 = BigDecimal.ZERO;
	 		BigDecimal parte4 = BigDecimal.ZERO;
	 		BigDecimal parte5 = BigDecimal.ZERO;
	 		parte1 = calcularParte1ValorExame(xIndDiscDsc, xPercDsct, parte1);
	 		
	 		parte2 = calcularParte2ValorExame(zQtdeCsh, bQtdeCshProf);
	 		parte3 = cValor;
	 		parte4 = calcularParte4ValorExame(flmValor, bQtdeM2);
	 		parte5 = calcularParte5ValorExame(zQtdeChMat, bQtdeAprs);
	 		valor = calcularValorTotal(parte1, parte2, parte3, parte4, parte5);
	 		valor = valor.setScale(2, RoundingMode.HALF_UP);
	 	}
	 	
		return valor;
	}


	private BigDecimal calcularValorTotal(BigDecimal parte1, BigDecimal parte2,
			BigDecimal parte3, BigDecimal parte4, BigDecimal parte5) {
		return parte1.multiply(parte2).multiply(parte3).add(parte4).add(parte5);
	}


	private BigDecimal calcularParte5ValorExame(BigDecimal zQtdeChMat,
			BigDecimal bQtdeAprs) {
		return (zQtdeChMat == null ? BigDecimal.ZERO : zQtdeChMat).divide(bQtdeAprs == null ? BigDecimal.ONE : bQtdeAprs);
	}


	private BigDecimal calcularParte4ValorExame(BigDecimal flmValor,
			BigDecimal bQtdeM2) {
		return (flmValor == null ? BigDecimal.ZERO : flmValor).multiply(bQtdeM2 == null ? BigDecimal.ZERO : bQtdeM2);
	}


	private BigDecimal calcularParte2ValorExame(BigDecimal zQtdeCsh,
			BigDecimal bQtdeCshProf) {
		return zQtdeCsh.add(bQtdeCshProf != null ? bQtdeCshProf : BigDecimal.ZERO);
	}


	private BigDecimal calcularParte1ValorExame(String xIndDiscDsc,
			BigDecimal xPercDsct, BigDecimal parte1) {
		if(xIndDiscDsc.equals("S")){
			parte1 = parte1.add(BigDecimal.ONE);
		}else{
			parte1 = parte1.add(BigDecimal.valueOf(100)).subtract(BigDecimal.valueOf(xPercDsct == null ? 0 : xPercDsct.longValue()));
			parte1 = parte1.divide(BigDecimal.valueOf(100));
		}
		return parte1;
	}


	private void setarParametrosValorExame(String exaSigla, Integer manSeq,
			AghUnidadesFuncionais unidadeFuncional, Date dthrLiberada,
			FatConvenioSaudePlano convenioSaudePlano, SQLQuery query) {
		query.setParameter("dthrLiberada", dthrLiberada);
		query.setParameter("dthrAtual", new Date());
		query.setParameter("exaSigla", exaSigla);
		query.setParameter("manSeq", manSeq);
		query.setParameter("cnvCodigo", convenioSaudePlano.getId().getCnvCodigo());
		query.setParameter("seq", convenioSaudePlano.getId().getSeq());
		query.setParameter("unfSeq", unidadeFuncional.getSeq());
	}


	private void montarSqlValorExame(StringBuilder sqlSelect) {
		sqlSelect.append(" select ");
		sqlSelect.append(" x.IND_DISC_DSCT,");
		sqlSelect.append(" x.perc_dsct,");
		sqlSelect.append(" z.QTDE_CSH,");
		sqlSelect.append(" b.qtde_csh_prof,");
		sqlSelect.append(" c.valor,");
		sqlSelect.append(" flm.valor,");
		sqlSelect.append(" B.QTDE_M2,");
		sqlSelect.append(" z.QTDE_CH_MAT,");
		sqlSelect.append(" b.qtde_aprs,");
		sqlSelect.append(" A.cOD_HCPA,");
		sqlSelect.append(" x.tpit_Cod");
		sqlSelect.append(" from agh.ael_tmp_bull_int ael");
		sqlSelect.append(" inner join conv.PROC_HCPA_X_CONV A1 on a1.tabpag_Cod = ael.cod_exme");
		sqlSelect.append(" inner join conv.PROC_HCPA_X_CNV A on A.COD_HCPA = a1.prhc_cod_hcpa");
		sqlSelect.append(" inner join conv.tipo_x_cch x on");
		sqlSelect.append("    X.TPIT_COD = A.TPIT_COD and");
		sqlSelect.append("    ((x.mae_csp_cnv_Codigo is not null and x.mae_csp_cnv_Codigo = a.csp_cnv_Codigo) OR");
		sqlSelect.append("    (x.mae_csp_cnv_Codigo is null and x.csp_cnv_Codigo = a.csp_cnv_Codigo)) and");
		sqlSelect.append("    ((x.mae_csp_seq is not null and x.mae_csp_seq = a.csp_seq) OR");
		sqlSelect.append("    (x.mae_csp_seq is null and x.csp_seq = a.csp_seq)) and");
		sqlSelect.append("    X.TPTAB_COD = A.TPTAB_COD");
		sqlSelect.append(" inner join conv.TAB_PGTO B on");
		sqlSelect.append("    B.TPTAB_COD = X.TPTAB_COD and");
		sqlSelect.append("    B.COD = A.TBl_COD");
		sqlSelect.append(" inner join conv.tipo_tab_pgto y on y.cod = b.tptab_cod");
		sqlSelect.append(" inner join conv.comp_tab_pgto t on t.tptab_cod = b.tptab_cod");
		sqlSelect.append(" inner join conv.valr_tab_pgto_x_comp z on");
		sqlSelect.append("    z.tptab_cod = t.tptab_cod and");
		sqlSelect.append("    z.tabpag_cod = b.cod and");
		sqlSelect.append("    z.cppgto_nro = t.nro");
		sqlSelect.append(" inner join conv.VALOR_CH_CONV_PLANO C on");
		sqlSelect.append("    C.ccp_csp_cnv_Codigo = X.ccp_csp_cnv_Codigo and");
		sqlSelect.append("    C.ccp_csp_seq = X.ccp_csp_seq and");
		sqlSelect.append("    C.ccp_Codigo = X.ccp_Codigo");
		sqlSelect.append(" left outer join conv.VALOR_CH_CONV_PLANO flm on");
		sqlSelect.append("    flm.ccp_csp_cnv_Codigo = X.ccp_flm_csp_cnv_Codigo and");
		sqlSelect.append("    flm.ccp_csp_seq = X.ccp_flm_csp_seq and");
		sqlSelect.append("    flm.ccp_Codigo = X.ccp_flm_Codigo and");
		sqlSelect.append("    ((flm.dt_fim is not null and :dthrLiberada between flm.dt_inicio and flm.dt_fim) OR"); //restricao 0
		sqlSelect.append("    (flm.dt_fim is null and :dthrLiberada between flm.dt_inicio and :dthrAtual))"); //restricao 1,2
		sqlSelect.append(" WHERE");
		sqlSelect.append(" a1.tabpag_tptab_cod in (9,13,71,73,75,77,79)");
		sqlSelect.append(" and a1.tpit_Cod in (13,17,14,15,16,22,89,48)");
		sqlSelect.append(" AND X.TPTAB_COD  NOT  IN  (9,11,13,15,22,71,73,75,77,79)");
		sqlSelect.append(" and z.QTDE_CSH   > 0");
		sqlSelect.append(" and a1.conv_Cod = 99");
		sqlSelect.append(" and    ael.exa_sigla = :exaSigla"); //restricao 3
		sqlSelect.append(" and ael.man_seq = :manSeq"); //restricao 4
		sqlSelect.append(" and ((t.data_finl is not null and :dthrLiberada between t.data_inic and t.data_finl) OR"); //restricao 5
		sqlSelect.append(" (t.data_finl is null and :dthrLiberada between t.data_inic and :dthrAtual))"); //restricao 6,7
		sqlSelect.append(" and ((c.dt_fim is not null and :dthrLiberada between c.dt_inicio and c.dt_fim) OR"); //restricao 8
		sqlSelect.append(" (c.dt_fim is null and :dthrLiberada between c.dt_inicio and :dthrAtual))"); //restricao 9,10
		sqlSelect.append(" and X.csp_cnv_codigo+0 = :cnvCodigo"); //restricao 11
		sqlSelect.append(" and x.csp_seq = :seq"); //restricao 12
		sqlSelect.append(" and ael.unf_seq = :unfSeq"); //restricao 13
	}
}
