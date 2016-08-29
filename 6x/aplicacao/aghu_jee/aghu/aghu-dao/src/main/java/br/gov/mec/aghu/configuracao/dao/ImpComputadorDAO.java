package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.cups.ImpComputador;

public class ImpComputadorDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ImpComputador> {

	private static final long serialVersionUID = -1839083247558276596L;

	/**
	 * Retorna computador pelo IP fornecido.
	 * 
	 * @param ipComputador Endereço de rede do computador, pode
	 * ser tanto o IP quanto o nome do host
	 * @return Computador identificado pelo endereço
	 */
	public ImpComputador buscarComputador(String ipComputador) {
		if (ipComputador == null || ipComputador.isEmpty()) {
			return null;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ImpComputador.class);
		
		criteria.add(Restrictions.or(
				Restrictions.eq(ImpComputador.Fields.IP.toString(), ipComputador),
				Restrictions.eq(ImpComputador.Fields.NOME.toString(), ipComputador).ignoreCase()));
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return (ImpComputador) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * @param paramPesquisa
	 * @return
	 */
	public List<ImpComputador> pesquisarComputador(Object paramPesquisa) {
		String valor = paramPesquisa.toString();

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputador.class);

		if (StringUtils.isNotBlank(valor)) {

			criteria.add(Restrictions.like(
					ImpComputador.Fields.NOME.toString(), valor.toUpperCase(),
					MatchMode.ANYWHERE));
		}

		List<ImpComputador> li = executeCriteria(criteria);

		return li;
	}

	/**
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param ip
	 * @param nome
	 * @param descricao
	 * @return
	 */
	public List<ImpComputador> pesquisarComputador(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, String ip,
			String nome, String descricao) {

		return executeCriteria(criarCriteriaPesquisaComputador(ip, nome,
				descricao), firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Método definido para retornar a quantidade de computadores lidos
	 * 
	 * @param ip
	 *            e/ou nome e/ou descricao
	 * @return Integer
	 */
	public Long pesquisarComputadorCount(String ip, String nome,
			String descricao) {
		return executeCriteriaCount(criarCriteriaPesquisaComputador(ip, nome,
				descricao));
	}

	/**
	 * Método responsável pela definição da Criteria utilizada na pesquisa de
	 * computador
	 * 
	 * @param ip
	 *            e/ou nome e/ou descricao do computador
	 * @return Um DetachedCriteria pronto para pesquisa
	 */
	private DetachedCriteria criarCriteriaPesquisaComputador(String ip,
			String nome, String descricao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputador.class);

		if (!StringUtils.isBlank(ip)) {
			criteria.add(Restrictions.like("ipComputador", ip,
					MatchMode.ANYWHERE));
		}

		if (!StringUtils.isBlank(nome)) {
			criteria.add(Restrictions.like("nomeComputador", nome,
					MatchMode.ANYWHERE));
		}

		if (!StringUtils.isBlank(descricao)) {
			criteria.add(Restrictions.like("descricao", descricao,
					MatchMode.ANYWHERE));
		}

		return criteria;
	}

	/**
	 * Método definido para verificar a existência de um computador
	 * 
	 * @param idComputador
	 *            , ipComputador e nomeComputador
	 * @return boolean
	 */
	public boolean isComputadorExistente(Integer idComputador,
			String ipComputador, String nomeComputador) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputador.class);

		Criterion restrictionIp = Restrictions.eq(ImpComputador.Fields.IP
				.toString(), ipComputador);
		Criterion restrictionNome = Restrictions.eq(ImpComputador.Fields.NOME
				.toString(), nomeComputador);

		criteria.add(Restrictions.or(restrictionIp, restrictionNome));

		ImpComputador impComputador = (ImpComputador) executeCriteriaUniqueResult(criteria);

		// validacao para alteracao
		if (impComputador != null && idComputador != null) {
			if (impComputador.getSeq().equals(idComputador)) {
				return false;
			}
			return true;
		}
		return impComputador == null ? false : true;
	}

	public ImpComputador obterComputadorPorNome(String nomePc) {
		if (nomePc == null || nomePc.isEmpty()) {
			return null;
		}
		
		DetachedCriteria criteria = DetachedCriteria.forClass(ImpComputador.class);
		
		criteria.add(Restrictions.eq(ImpComputador.Fields.NOME.toString(), nomePc).ignoreCase());
		
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return (ImpComputador) executeCriteriaUniqueResult(criteria);
	}

}
