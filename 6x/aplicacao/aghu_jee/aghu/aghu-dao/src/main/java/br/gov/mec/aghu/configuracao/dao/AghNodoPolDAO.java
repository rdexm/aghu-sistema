package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghNodoPol;

public class AghNodoPolDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghNodoPol> {

	private static final long serialVersionUID = -3423686012097688096L;
	
	public AghNodoPol obterAghNodoPolPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNodoPol.class);
		
		criteria.add(Restrictions.eq(AghNodoPol.Fields.NOME.toString(), nome));
		
		return (AghNodoPol) executeCriteriaUniqueResult(criteria);
	}

	public List<AghNodoPol> recuperarAghNodoPolPorOrdem() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNodoPol.class);

		criteria.addOrder(Order.asc(AghNodoPol.Fields.ORDEM.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AghNodoPol> recuperarAghNodoPolPorOrdem(String [] tipos) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghNodoPol.class);

		criteria.add(Restrictions.in(AghNodoPol.Fields.NOME.toString(), tipos));
		criteria.addOrder(Order.asc(AghNodoPol.Fields.ORDEM.toString()));

		return executeCriteria(criteria);
	}	

}
