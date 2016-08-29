package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModoCobranca;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioTipoItemConta;
import br.gov.mec.aghu.faturamento.vo.TotaisProcedEspHemotGeralVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.FatCampoMedicoAuditAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class TotaisProcedEspHemotGeralConsultaUmQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 6985872310663085785L;

	private static final String ALIAS_EAI  = "EAI";	// FAT_ESPELHOS_AIH				EAI
	private static final String ALIAS_CTH  = "CTH";	// FAT_CONTAS_HOSPITALARES		CTH
	private static final String ALIAS_IPH  = "IPH";	// FAT_ITENS_PROCED_HOSPITALAR	IPH
	private static final String ALIAS_CLC  = "CLC";	// AGH_CLINICAS 				CLC
	private static final String ALIAS_CAH  = "CAH"; // FAT_CAMPOS_MEDICO_AUDIT_AIH	CAH
	private static final String ALIAS_CAH_HIB  = "cah1_";
	private static final String SUM = " SUM ";
	private static final String AS = " AS ";
	private static final String PONTO = ".";
	private static final String COALESCE = " COALESCE ";
	private static final Integer EAI_SEQP = 1;
	
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
		// EAI.CTH_SEQ = CAH.EAI_CTH_SEQ
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.FAT_CAMPO_MEDICO_AUDIT_AIH.toString(), ALIAS_CAH, JoinType.INNER_JOIN);
		// CAH.IPH_SEQ = IPH.SEQ 
		// CAH.IPH_PHO_SEQ = IPH.PHO_SEQ  
		criteria.createAlias(ALIAS_CAH + PONTO + FatCampoMedicoAuditAih.Fields.IPH.toString(), ALIAS_IPH, JoinType.INNER_JOIN);
		// EAI.CTH_SEQ = CTH.SEQ
		criteria.createAlias(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.CONTA_HOSPITALAR.toString(), ALIAS_CTH, JoinType.INNER_JOIN);
		// EAI.ESPECIALIDADE_AIH  = CLC.CODIGO
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
		
		// CAH.IND_MODO_COBRANCA  = 'V'	
		criteria.add(Restrictions.eq(ALIAS_CAH + PONTO + FatCampoMedicoAuditAih.Fields.IND_MODO_COBRANCA.toString(), DominioModoCobranca.V));
		// EAI.IPH_COD_SUS_REALIZ <> CAH.IPH_COD_SUS
		criteria.add(Restrictions.neProperty(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.IPH_COD_SUS_REALIZ.toString(), ALIAS_CAH + PONTO + FatCampoMedicoAuditAih.Fields.IPH_COD_SUS.toString()));
		// NVL(CAH.IND_CONSISTENTE, 'D') <> 'R'
        criteria.add(obterRestrictionCahIndConsistente());	
		// EAI.SEQP = 1
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.SEQP.toString(), EAI_SEQP));
		// EAI.DCI_CPE_DT_HR_INICIO = <P_DT_HR_INICIO>
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_DT_HR_INICIO.toString(), dtHrInicio));
		// EAI.DCI_CPE_MODULO = 'INT'
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_MODULO.toString(), modulo));
		// EAI.DCI_CPE_MES = <P_MES>
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_MES.toString(), mes));
		// EAI.DCI_CPE_ANO = <P_ANO>
		criteria.add(Restrictions.eq(ALIAS_EAI + PONTO + FatEspelhoAih.Fields.DCI_CPE_ANO.toString(), ano));
		// CTH.CTH_SEQ_REAPRESENTADA IS NULL
		criteria.add(Restrictions.isNull(ALIAS_CTH + PONTO + FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString()));				  
	}
	
	private Criterion obterRestrictionCahIndConsistente() {

		StringBuilder sqlRestriction =  new StringBuilder(500);	
		
		//NVL(CAH.IND_CONSISTENTE, 'D') <> 'R'
		sqlRestriction.append(COALESCE)
			.append('(')
				.append(ALIAS_CAH_HIB).append('.').append(FatCampoMedicoAuditAih.Fields.IND_CONSISTENTE.name()).append(", '").append(DominioTipoItemConta.D.name()).append("' ) <>  ? ");
		
		Object[] values = { DominioTipoItemConta.R.name() };
		Type[] types = { StringType.INSTANCE };

		return Restrictions.sqlRestriction(sqlRestriction.toString(), values, types);
	}
	
	private void setProjecao() {

		criteria.setProjection(Projections.projectionList()
				// COUNT(*) AS QUANT
				.add(Projections.rowCount(), TotaisProcedEspHemotGeralVO.Fields.QUANT.toString())
				// SUM(NVL(CAH.VALOR_SADT,0)) as v_sadt 
				.add(Projections.sum(ALIAS_CAH + PONTO + FatCampoMedicoAuditAih.Fields.VALOR_SADT.toString()), TotaisProcedEspHemotGeralVO.Fields.V_SADT.toString())
				// SUM(NVL(CAH.VALOR_SERV_HOSP,0)) as v_hosp
				.add(Projections.sum(ALIAS_CAH + PONTO + FatCampoMedicoAuditAih.Fields.VALOR_SERV_HOSP.toString()), TotaisProcedEspHemotGeralVO.Fields.V_HOSP.toString())
				//SUM(NVL(CAH.VALOR_SERV_PROF,0)) as v_prof
				.add(Projections.sum(ALIAS_CAH + PONTO + FatCampoMedicoAuditAih.Fields.VALOR_SERV_PROF.toString()), TotaisProcedEspHemotGeralVO.Fields.V_PROF.toString())
				//SUM(CAH.VALOR_SADT) + SUM(CAH.VALOR_SERV_HOSP) + SUM(CAH.VALOR_SERV_PROF)  TOTAL		
				.add(obterProjectionSumTotal(), TotaisProcedEspHemotGeralVO.Fields.TOTAL.toString()));
				
		criteria.setResultTransformer(Transformers.aliasToBean(TotaisProcedEspHemotGeralVO.class));
	}
	
	private Projection obterProjectionSumTotal() {

		StringBuilder sqlProjection =  new StringBuilder(500);	

		sqlProjection
			.append(SUM).append('(' ).append(ALIAS_CAH_HIB).append(PONTO).append(FatCampoMedicoAuditAih.Fields.VALOR_SADT.name()).append(") ").append(" + ")
			.append(SUM).append('(' ).append(ALIAS_CAH_HIB).append(PONTO).append(FatCampoMedicoAuditAih.Fields.VALOR_SERV_HOSP.name()).append(") ").append(" + ")
			.append(SUM).append('(' ).append(ALIAS_CAH_HIB).append(PONTO).append(FatCampoMedicoAuditAih.Fields.VALOR_SERV_PROF.name()).append(") ") 
			.append(AS).append(TotaisProcedEspHemotGeralVO.Fields.TOTAL.toString()); 
	
		String[] columnAliases = {TotaisProcedEspHemotGeralVO.Fields.TOTAL.toString()};
		Type[] types = {BigDecimalType.INSTANCE};
		
		return Projections.sqlProjection(sqlProjection.toString(), columnAliases, types);
	}
		
	public DetachedCriteria build(DominioModuloCompetencia modulo, Byte mes, Short ano, Date dtHrInicio) {
		this.modulo = modulo;
		this.mes = mes;
		this.ano = ano;
		this.dtHrInicio = dtHrInicio;
		return super.build();
	}

}
