package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.LwsComAntibiograma;
import br.gov.mec.aghu.model.LwsComResultado;

public class LwsComAntibiogramaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<LwsComAntibiograma> {
	
	
	private static final long serialVersionUID = -1106143476482026932L;

	public List<LwsComAntibiograma> pesquisarLwsComAntibiogramaPorIdResultado(Long idResultado) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(LwsComAntibiograma.class);
		
		criteria.createAlias(LwsComAntibiograma.Fields.LWS_COM_RESULTADOS.toString(), "CRS", DetachedCriteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq("CRS." + LwsComResultado.Fields.ID.toString(), idResultado));
			
		return executeCriteria(criteria);
		
	}




}