package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.faturamento.vo.FatSaldoUTIVO;
import br.gov.mec.aghu.model.FatSaldoUti;

public class FatSaldoUtiDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatSaldoUti> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7329629078290187338L;
	

	public List<FatSaldoUTIVO> pesquisarBancosUTI(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI, Integer firstResult, Integer maxResult, 
			String orderProperty, boolean asc) {
		DetachedCriteria criteria = montarCriteria(mes, ano, tipoUTI, true);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);	
	}	
	public Long pesquisarBancosUTICount(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI) {
		DetachedCriteria criteria = montarCriteria(mes, ano, tipoUTI, false);
		return executeCriteriaCount(criteria);	
	}
	
	private DetachedCriteria montarCriteria(Integer mes, Integer ano, DominioTipoIdadeUTI tipoUTI, boolean ordenar) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FatSaldoUti.class, "suti");
			
		criteria.setProjection(Projections.projectionList().add(
				Projections.property("suti." + FatSaldoUti.Fields.MES.toString()), FatSaldoUTIVO.Fields.MES.toString()).add(
				Projections.property("suti." + FatSaldoUti.Fields.ANO.toString()), FatSaldoUTIVO.Fields.ANO.toString()).add(
				Projections.property("suti." + FatSaldoUti.Fields.TIPO_UTI.toString()), FatSaldoUTIVO.Fields.TIPO_UTI.toString()).add(
				Projections.property("suti." + FatSaldoUti.Fields.NRO_LEITOS.toString()), FatSaldoUTIVO.Fields.NRO_LEITOS.toString()).add(
				Projections.property("suti." + FatSaldoUti.Fields.CAPACIDADE.toString()), FatSaldoUTIVO.Fields.CAPACIDADE.toString()).add(
				Projections.property("suti." + FatSaldoUti.Fields.SALDO.toString()), FatSaldoUTIVO.Fields.UTILIZACAO.toString()));
		
		if (mes != null) {
			criteria.add(Restrictions.eq(("suti." + FatSaldoUti.Fields.MES.toString()), mes));
		}
		if (ano != null) {
			criteria.add(Restrictions.eq(("suti." + FatSaldoUti.Fields.ANO.toString()), ano));
		}
		if (tipoUTI != null) {
			criteria.add(Restrictions.eq(("suti." + FatSaldoUti.Fields.TIPO_UTI.toString()), tipoUTI));
		}
		
		if (ordenar) {
			criteria.addOrder(Order.desc("suti." + FatSaldoUti.Fields.ANO.toString()));
			criteria.addOrder(Order.desc("suti." + FatSaldoUti.Fields.MES.toString()));
			criteria.addOrder(Order.asc("suti." + FatSaldoUti.Fields.TIPO_UTI.toString()));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(FatSaldoUTIVO.class));
		
		return criteria;
	}
	
}
