package br.gov.mec.aghu.faturamento.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.persistence.Query;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.type.BigDecimalType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.faturamento.vo.TotalGeralClinicaPorProcedimentoVO;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatDocumentoCobrancaAihs;
import br.gov.mec.aghu.model.FatValorContaHospitalar;

public class FatValorContaHospitalarDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<FatValorContaHospitalar> {

	private static final long serialVersionUID = 8805541935876802412L;
	
	public Integer removerPorCthSeqs(List<Integer> cthSeqs) {
		Query query = this.createQuery("delete " + FatValorContaHospitalar.class.getName() + 
				 " where " + FatValorContaHospitalar.Fields.CTH_SEQ.toString() + " in (:cthSeqs) ");
		query.setParameter("cthSeqs", cthSeqs);
		return query.executeUpdate();
	}
	
	public TotalGeralClinicaPorProcedimentoVO obterTotalGeralClinicaPorProcedimento(
			final DominioModuloCompetencia modulo, final Integer mes,
			final Integer ano, final Date dtHrInicio) {
		
		TotalGeralClinicaPorProcedimentoQueryBuilder builder =  new TotalGeralClinicaPorProcedimentoQueryBuilder();
		return (TotalGeralClinicaPorProcedimentoVO) executeCriteriaUniqueResult(builder.build(modulo, mes, ano, dtHrInicio));
	}

	public BigDecimal obterFatorMultiplicacaoParaValorRateado(Date dtHrInicio, Integer ano, Integer mes) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatValorContaHospitalar.class, "VCT");

		criteria.createAlias(FatValorContaHospitalar.Fields.CONTA_HOSPITALAR.toString(), "CTH");
		criteria.createAlias("CTH." + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), "DCI");
		
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.INT));
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_MES.toString(), mes));
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_ANO.toString(), ano));
		criteria.add(Restrictions.eq("DCI." + FatDocumentoCobrancaAihs.Fields.CPE_DT_HR_INICIO.toString(), dtHrInicio));

		criteria.add(Restrictions.eqProperty("CTH." + FatContasHospitalares.Fields.DCI_CODIGO_DCIH.toString(), "DCI." + FatDocumentoCobrancaAihs.Fields.CODIGO_DCIH.toString()));
		criteria.add(Restrictions.isNull("CTH." + FatContasHospitalares.Fields.CTH_SEQ_REAPRESENTADA.toString()));
		
		criteria.add(Restrictions.eqProperty("VCT." + FatValorContaHospitalar.Fields.CTH_SEQ.toString(), "CTH." + FatContasHospitalares.Fields.SEQ.toString()));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(" SUM(CTH1_.VALOR_SADT + VALOR_SADT_UTI + VALOR_SADT_UTIE + VALOR_SADT_ACOMP + VALOR_SADT_RN + VALOR_SADT_TRANSP) AS IND_RN5 ", 
					new String[] {"IND_RN5"}, new Type[] {BigDecimalType.INSTANCE})));

		return (BigDecimal) executeCriteriaUniqueResult(criteria);
	}
}
