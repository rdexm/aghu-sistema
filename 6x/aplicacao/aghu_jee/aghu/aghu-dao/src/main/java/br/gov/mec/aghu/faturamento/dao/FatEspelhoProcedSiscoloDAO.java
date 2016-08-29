package br.gov.mec.aghu.faturamento.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.model.FatEspelhoProcedSiscolo;

public class FatEspelhoProcedSiscoloDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatEspelhoProcedSiscolo> {

	private static final long serialVersionUID = -4844506171101007506L;

	/*
	 SELECT DISTINCT DATA_PREVIA
		FROM FAT_ESPELHO_PROCED_SISCOLOS
		WHERE 
		CPE_MODULO = 'SIS' 
		AND CPE_MES = :P_CPE_MES 
		AND CPE_ANO = :P_CPE_ANO
		AND CPE_DT_HR_INICIO = :P_CPE_DT_HR_INICIO 
		ORDER BY 1 DESC;
	 */
	public List<Date> obterDataPreviaModuloSIS(FatCompetencia competencia){

		final DetachedCriteria criteria = DetachedCriteria.forClass(FatEspelhoProcedSiscolo.class);

		criteria.setProjection(Projections.distinct(Projections.property(FatEspelhoProcedSiscolo.Fields.DATA_PREVIA.toString())));
		criteria.add(Restrictions.eq(FatEspelhoProcedSiscolo.Fields.CPE_MODULO.toString(), DominioModuloCompetencia.SIS));
		criteria.add(Restrictions.eq(FatEspelhoProcedSiscolo.Fields.CPE_MES.toString(), competencia.getId().getMes().byteValue()));
		criteria.add(Restrictions.eq(FatEspelhoProcedSiscolo.Fields.CPE_ANO.toString(), competencia.getId().getAno().shortValue()));
		criteria.add(Restrictions.eq(FatEspelhoProcedSiscolo.Fields.CPE_DT_HR_INICIO.toString(), competencia.getId().getDtHrInicio()));

		criteria.addOrder(Order.desc(FatEspelhoProcedSiscolo.Fields.DATA_PREVIA.toString()));
		
		return executeCriteria(criteria);
	}
}
