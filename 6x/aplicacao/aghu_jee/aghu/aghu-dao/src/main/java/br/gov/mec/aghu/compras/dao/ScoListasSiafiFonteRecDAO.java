/**
 * 
 */
package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoListasSiafiFonteRec;

/**
 * @author silvia
 *
 */
public class ScoListasSiafiFonteRecDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoListasSiafiFonteRec>{

	private static final long serialVersionUID = 7560943552026609160L;
	
	//consulta 5
	public List<ScoListasSiafiFonteRec> pesquisarListasSiafiFonteRecAtivos(){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoListasSiafiFonteRec.class);
		criteria.add(Restrictions.eq(ScoListasSiafiFonteRec.Fields.SITUACAO.toString(),DominioSituacao.A));
		return this.executeCriteria(criteria);
	}
	
	
}
