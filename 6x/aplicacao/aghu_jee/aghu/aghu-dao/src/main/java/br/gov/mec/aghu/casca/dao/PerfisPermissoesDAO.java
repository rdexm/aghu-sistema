/**
 * 
 */
package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Perfil;
import br.gov.mec.aghu.casca.model.PerfisPermissoes;


/**
 * @author rafael
 *
 */
public class PerfisPermissoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PerfisPermissoes> {

	private static final long serialVersionUID = 310774047442275607L;

	public List<PerfisPermissoes> pesquisarPerfisPermissoes(Perfil perfil) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(PerfisPermissoes.class);
		criteria.add(Restrictions.eq("perfil", perfil));
		return executeCriteria(criteria,true);
	}
}
