package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.ScoSiasgIdentificacaoBasica;

public class ScoSiasgIdentificacaoBasicaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoSiasgIdentificacaoBasica>  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2679510154834225792L;
	
	public List<ScoSiasgIdentificacaoBasica> obterCatMat(Object catMat) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(ScoSiasgIdentificacaoBasica.class);

		if(!catMat.toString().isEmpty()){
			criteria.add(Restrictions.ilike(ScoSiasgIdentificacaoBasica.Fields.IT_NO_BASICO.toString(), catMat.toString(), MatchMode.ANYWHERE));
		}
		
		criteria.addOrder(Order.asc(ScoSiasgIdentificacaoBasica.Fields.IT_NO_BASICO.toString()));
		return executeCriteria(criteria);
	}

}
