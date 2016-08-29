package br.gov.mec.aghu.configuracao.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Usuario;
import br.gov.mec.aghu.model.cups.ImpUsuarioVisualiza;

public class ImpUsuarioVisualizaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ImpUsuarioVisualiza> {

	private static final long serialVersionUID = -4088927970045636336L;

	/**
	 * Prepara o criteria para a buscar situacao ativa modal cups
	 * 
	 * @param usuario
	 * @return
	 */
	public ImpUsuarioVisualiza buscarModalAtivoCups(Usuario usuario) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpUsuarioVisualiza.class);

		criteria.add(Restrictions.eq(
				ImpUsuarioVisualiza.Fields.ID_USUARIO.toString(),
				usuario));

		return (ImpUsuarioVisualiza) executeCriteriaUniqueResult(criteria);
	}
}
