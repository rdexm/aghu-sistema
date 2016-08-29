package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.cups.ImpServidorCups;

public class ImpServidorCupsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ImpServidorCups> {

	private static final long serialVersionUID = 8762472475825352071L;

	/**
	 * @param ipServidor
	 * @param nomeCups
	 * @param descricao
	 * @return
	 */
	public Long pesquisarServidorCupsCount(String ipServidor,
			String nomeCups, String descricao) {

		return executeCriteriaCount(criarCriteriaPesquisaServidorCups(
				ipServidor, nomeCups, descricao));
	}

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param ipServidor
	 * @param nomeCups
	 * @param descricao
	 * @return
	 */
	public List<ImpServidorCups> pesquisarServidorCups(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			String ipServidor, String nomeCups, String descricao) {
		return executeCriteria(
				criarCriteriaPesquisaServidorCups(ipServidor, nomeCups,
						descricao), firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Cria criteria para pesquisa de servidor cups
	 * 
	 * @param String
	 *            ipServidor,String nomeCups, String descricao
	 * @return Um DetachedCriteria pronto pra pesquisa.
	 */
	private DetachedCriteria criarCriteriaPesquisaServidorCups(
			String ipServidor, String nomeCups, String descricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpServidorCups.class);

		if (!StringUtils.isBlank(ipServidor)) {
			criteria.add(Restrictions.like("ipServidor", ipServidor,
					MatchMode.ANYWHERE));
		}

		if (!StringUtils.isBlank(nomeCups)) {
			criteria.add(Restrictions.like("nomeCups", nomeCups,
					MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * @param paramPesquisa
	 * @return ImpServidorCups
	 */
	public List<ImpServidorCups> pesquisarServidorCups(Object paramPesquisa) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpServidorCups.class);

		if (paramPesquisa != null && StringUtils.isNotBlank(paramPesquisa.toString())) {

			criteria.add(Restrictions.like(ImpServidorCups.Fields.NOME_CUPS
					.toString(), paramPesquisa.toString().toUpperCase(),
					MatchMode.ANYWHERE));

		}

		List<ImpServidorCups> li = executeCriteria(criteria);

		return li;
	}

	/**
	 * Servidor Cups Existente?
	 * 
	 * @param
	 * @param
	 * @return
	 */
	public boolean isServidorCupsExistente(Integer IdServidorCups,
			String ipServidor) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpServidorCups.class);

		criteria.add(Restrictions.eq(
				ImpServidorCups.Fields.IPSERVIDOR.toString(), ipServidor));

		ImpServidorCups objServidorCups = (ImpServidorCups) executeCriteriaUniqueResult(criteria);

		// validacao para alteracao
		if (objServidorCups != null && IdServidorCups != null) {
			if (objServidorCups.getId().equals(IdServidorCups)) {
				return false;
			}
			return true;
		}
		return objServidorCups == null ? false : true;
	}
}
