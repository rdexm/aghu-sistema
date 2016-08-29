package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.LongType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatValorContaHospitalar;
import br.gov.mec.aghu.core.persistence.dao.QueryBuilder;

public class TotalGeralClinicaPorProcedimentoQueryBuilder extends QueryBuilder<DetachedCriteria> {

	private static final long serialVersionUID = 6985872310663085785L;
	
	private static final String ALIAS_FVCH = "V";   // FAT_VALORES_CONTA_HOSPITALAR V
	private static final String ALIAS_FCH  = "C";   // FAT_CONTAS_HOSPITALARES C
	private static final String ALIAS_FCH_HIB  = "c1_";
	private static final String ALIAS_FDC  = "FDC"; // FAT_DOCUMENTO_COBRANCA_AIHS						
	private static final String PONTO = ".";
	
	private DetachedCriteria criteria;

	private DominioModuloCompetencia modulo;
	private Integer mes;
	private Integer ano;
	private Date dtHrInicio;

	@Override
	protected DetachedCriteria createProduct() {
		return DetachedCriteria.forClass(FatValorContaHospitalar.class, ALIAS_FVCH);
	}

	@Override
	protected void doBuild(DetachedCriteria criteria) {
		this.criteria = criteria;
		setJoin();
		setFiltro();
		setProjecao();
	}
	
	private void setJoin() {
		criteria.createAlias(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.CONTA_HOSPITALAR.toString(), ALIAS_FCH, JoinType.INNER_JOIN);
	}

	private void setFiltro() {
		criteria.add(Restrictions.isNull(ALIAS_FCH + PONTO + FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString()));
		criteria.add(Subqueries.propertyIn(ALIAS_FCH + PONTO + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), getSubQuery()));
	}
	
	private DetachedCriteria getSubQuery(){
		final DetachedCriteria subQuery = DetachedCriteria.forClass(FatDocumentoCobrancaAihs.class, ALIAS_FDC);
		
		subQuery.setProjection(Projections.property(ALIAS_FDC + PONTO + FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString()));
		
		subQuery.add(Restrictions.eq(ALIAS_FDC + PONTO + FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), dtHrInicio));
		subQuery.add(Restrictions.eq(ALIAS_FDC + PONTO + FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), modulo));
		subQuery.add(Restrictions.eq(ALIAS_FDC + PONTO + FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), mes));
		subQuery.add(Restrictions.eq(ALIAS_FDC + PONTO + FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), ano));
		
		return subQuery;
	}

	private void setProjecao() {
		// SUM(NVL(DIAS_UTI_MES_ALTA,0) + NVL(DIAS_UTI_MES_ANTERIOR,0) + NVL(DIAS_UTI_MES_INICIAL,0)) DIAS_UTI,
		String sql = "SUM(" + ALIAS_FCH_HIB + PONTO + FatContasHospitalares.Fields.DIAS_UTI_MES_ALTA.name() + ") + "
					+ "SUM(" + ALIAS_FCH_HIB + PONTO + FatContasHospitalares.Fields.DIAS_UTI_MES_ANTERIOR.name() + ") + "
					+ "SUM(" + ALIAS_FCH_HIB + PONTO + FatContasHospitalares.Fields.DIAS_UTI_MES_INICIAL.name() + ") AS "
					+ TotalGeralClinicaPorProcedimentoVO.Fields.DIAS_UTI.toString();
		
		String[] columnAliases = {TotalGeralClinicaPorProcedimentoVO.Fields.DIAS_UTI.toString()};
		Type[] types = {LongType.INSTANCE};
		
		criteria.setProjection(Projections.projectionList()
			.add(Projections.sum(ALIAS_FCH + PONTO + FatContasHospitalares.Fields.VALOR_UTI.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_UTI.toString())
			.add(Projections.sum(ALIAS_FCH + PONTO + FatContasHospitalares.Fields.VALOR_ACOMP.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_ACOMP.toString())
			
			.add(Projections.sum(ALIAS_FCH + PONTO + FatContasHospitalares.Fields.DIARIAS_ACOMPANHANTE.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.DIAS_ACOMP.toString())
			
			.add(Projections.sqlProjection(sql, columnAliases, types), TotalGeralClinicaPorProcedimentoVO.Fields.DIAS_UTI.toString())
			
			.add(Projections.sum(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.VALOR_SH_UTI.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_SH_UTI.toString())
			.add(Projections.sum(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.VALOR_SP_UTI.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_SP_UTI.toString())
			.add(Projections.sum(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.VALOR_SADT_UTI.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_SADT_UTI.toString())
			.add(Projections.sum(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.VALOR_SH_ACOMP.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_SH_ACOMP.toString())
			.add(Projections.sum(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.VALOR_SP_ACOMP.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_SP_ACOMP.toString())
			.add(Projections.sum(ALIAS_FVCH + PONTO + FatValorContaHospitalar.Fields.VALOR_SADT_ACOMP.toString()), TotalGeralClinicaPorProcedimentoVO.Fields.VALOR_SADT_ACOMP.toString())
		);
		
		criteria.setResultTransformer(Transformers.aliasToBean(TotalGeralClinicaPorProcedimentoVO.class));
	}
	
	public DetachedCriteria build(DominioModuloCompetencia modulo, Integer mes, Integer ano, Date dtHrInicio) {
		this.modulo = modulo;
		this.mes = mes;
		this.ano = ano;
		this.dtHrInicio = dtHrInicio;
		return super.build();
	}

}
