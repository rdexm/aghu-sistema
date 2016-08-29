package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioTipoIdadeUTI;
import br.gov.mec.aghu.model.FatSaldoUti;

public class FatSaltoUtiDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatSaldoUti> {

	private static final long serialVersionUID = -2220592794835577132L;

	private DetachedCriteria obterDetachedFatSaldoUti(){
		
		return  DetachedCriteria.forClass(FatSaldoUti.class);
	}
	
	public List<Integer> obterListNroLeitosPorTipoEmOrdemDecrData(DominioTipoIdadeUTI tipo) {
		DetachedCriteria criteria = this.obterDetachedFatSaldoUti();
		criteria.setProjection(Projections.property(FatSaldoUti.Fields.NRO_LEITOS.toString()));
		criteria.add(Restrictions.eq(FatSaldoUti.Fields.TIPO_UTI.toString(), tipo));
		criteria.addOrder(Order.desc(FatSaldoUti.Fields.ANO.toString()));
		criteria.addOrder(Order.desc(FatSaldoUti.Fields.MES.toString()));
		return executeCriteria(criteria);
	}
}
