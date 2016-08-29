package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.vo.TotaisAIHGeralVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class TotaisAIHGerarlQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 6985872310663085785L;
	
	private static final String ALIAS 	   = "{alias}";
	private static final String ALIAS_EAI  = "EAI";		// FAT_ESPELHOS_AIH				EAI
	private static final String ALIAS_CTH  = "CTH";		// FAT_CONTAS_HOSPITALARES		CTH
	private static final String ALIAS_IPH  = "IPH";		// FAT_ITENS_PROCED_HOSPITALAR	IPH
	private static final String ALIAS_CLC  = "CLC";		// AGH_CLINICAS CLC
	private static final String PONTO = ".";
	private static final String SUM = " SUM ";
	private static final String AS = " AS ";
	
	private DetachedCriteria criteria;

	private DominioModuloCompetencia modulo;
	private Byte mes;
	private Short ano;
	private Date dtHrInicio;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatEspelhoAih.class, ALIAS_EAI);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
	}
	
	private void setJoin() {
		// EAI.IPH_PHO_SEQ_REALIZ = IPH.PHO_SEQ        
		// EAI.IPH_SEQ_REALIZ     = IPH.SEQ
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.ITEM_PROCEDIMENTO_HOSPITALAR_REALIZADO.toString(), ALIAS_IPH, JoinType.INNER_JOIN);
		// EAI.CTH_SEQ            = CTH.SEQ
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), ALIAS_CTH, JoinType.INNER_JOIN);
		// EAI.ESPECIALIDADE_AIH  = CLC.CODIGO  //AghClinicas
		criteria.add(Subqueries.exists(getSubQuery()));
	}
	
	private DetachedCriteria getSubQuery(){
		final DetachedCriteria subQuery = DetachedCriteria.forClass(AghClinicas.class, ALIAS_CLC);
		// EAI.ESPECIALIDADE_AIH = CLC.CODIGO
		subQuery.setProjection(Projections.property(ALIAS_CLC  + PONTO + AghClinicas.Fields.CODIGO.toString()));
		subQuery.add(Restrictions.eqProperty(ALIAS_CLC  + PONTO + AghClinicas.Fields.CODIGO.toString(), ALIAS_EAI + PONTO + FatEspelhoAih.Fields.ESPECIALIDADE_AIH.toString()));
		
		return subQuery;
	}
	
	private void setFiltro() {
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), dtHrInicio));
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), modulo));
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_MES.toString(), mes));
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), ano));
		criteria.add(Restrictions.isNull(ALIAS_CTH + PONTO + FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString()));		  
	}
	
	private void setProjecao() {
		
		String[] columnAliases = {TotaisAIHGeralVO.Fields.TOTAL.toString()};
		Type[] types = {BigDecimalType.INSTANCE};
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.count(ALIAS_EAI+ PONTO + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString()), TotaisAIHGeralVO.Fields.QUANT_AIH.toString())
				.add(Projections.sum(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.VALOR_SADT_REALIZ.toString()), TotaisAIHGeralVO.Fields.SADT_AIH.toString())
				.add(Projections.sum(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.VALOR_SH_REALIZ.toString()), TotaisAIHGeralVO.Fields.HOSP_AIH.toString())
				.add(Projections.sum(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.VALOR_SP_REALIZ.toString()), TotaisAIHGeralVO.Fields.PROF_AIH.toString())
				
				//SUM(EAI.VALOR_SADT_REALIZ) + SUM(EAI.VALOR_SH_REALIZ) + SUM(EAI.VALOR_SP_REALIZ)  TOTAL
				.add(Projections.sqlProjection(obterProjectionSumTotal(), columnAliases, types), TotaisAIHGeralVO.Fields.TOTAL.toString()));		

		criteria.setResultTransformer(Transformers.aliasToBean(TotaisAIHGeralVO.class));
	}
	
	private String obterProjectionSumTotal() {
		
		StringBuilder sqlProjectionTotal =  new StringBuilder(500);	

		sqlProjectionTotal
			.append(SUM).append('(').append(ALIAS).append(PONTO).append(FatEspelhoAih.Fields.VALOR_SADT_REALIZ.name()).append(')').append(" + ")
			.append(SUM).append('(').append(ALIAS).append(PONTO).append(FatEspelhoAih.Fields.VALOR_SH_REALIZ.name()).append(')').append(" + ")
			.append(SUM).append('(').append(ALIAS).append(PONTO).append(FatEspelhoAih.Fields.VALOR_SP_REALIZ.name()).append(')').append(AS)
			.append(TotaisAIHGeralVO.Fields.TOTAL.toString());
		
		return sqlProjectionTotal.toString();
	}
	
	public DetachedCriteria build(DominioModuloCompetencia modulo, Byte mes, Short ano, Date dtHrInicio) {
		this.modulo = modulo;
		this.mes = mes;
		this.ano = ano;
		this.dtHrInicio = dtHrInicio;
		return super.build();
	}

}
