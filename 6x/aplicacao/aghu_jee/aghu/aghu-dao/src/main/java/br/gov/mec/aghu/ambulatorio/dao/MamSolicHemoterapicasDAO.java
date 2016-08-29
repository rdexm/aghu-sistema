package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamSolicHemoterapica;

/**
 * #42360
 * Classe de acesso a base de dados responsáveis pelas operações relativas a
 * tabela MAM_SOLIC_HEMOTERAPICAS.
 * 
 */
public class MamSolicHemoterapicasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamSolicHemoterapica> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6304487717166735144L;

	/**
	 * #42360
	 * @param numeroConsulta
	 * @return
	 */
	
	public MamSolicHemoterapica buscarSolicHemoterapicasPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamSolicHemoterapica.class);

		criteria.add(Restrictions.eq(MamSolicHemoterapica.Fields.CON_NUMERO.toString(), numeroConsulta));
		List<MamSolicHemoterapica> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}
	
}
