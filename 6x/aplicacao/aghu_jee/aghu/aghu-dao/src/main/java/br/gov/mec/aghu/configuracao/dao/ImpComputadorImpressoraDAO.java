package br.gov.mec.aghu.configuracao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.FetchMode;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioTipoCups;
import br.gov.mec.aghu.model.cups.ImpClasseImpressao;
import br.gov.mec.aghu.model.cups.ImpComputador;
import br.gov.mec.aghu.model.cups.ImpComputadorImpressora;

public class ImpComputadorImpressoraDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<ImpComputadorImpressora> {

	private static final long serialVersionUID = 3181894523803299677L;

	/**
	 * Prepara o criteria para a re-impressão de etiquetas.
	 * 
	 * @param prontuarios
	 * @param dataInicial
	 * @param dataFinal
	 * @return
	 */
	public ImpComputadorImpressora buscarImpressora(Integer idComputador,
			Integer idClasseImpressao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputadorImpressora.class);
		criteria.add(Restrictions.eq(
				ImpComputadorImpressora.Fields.ID_COMPUTADOR.toString(),
				idComputador));
		criteria.add(Restrictions.eq(
				ImpComputadorImpressora.Fields.ID_CLASSE_IMPRESSORA.toString(),
				idClasseImpressao));

		return (ImpComputadorImpressora) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Retorna impressora para o tipo de documento fornecido.
	 * 
	 * @param idComputador
	 * @param tipoCups
	 * @return
	 */
	public ImpComputadorImpressora buscarImpressora(Integer idComputador,
			DominioTipoCups tipoCups) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputadorImpressora.class);
		criteria.add(Restrictions.eq(
				ImpComputadorImpressora.Fields.ID_COMPUTADOR.toString(),
				idComputador));

		criteria.createCriteria(
				ImpComputadorImpressora.Fields.IMP_CLASSE_IMPRESSAO.toString())
				.add(
						Restrictions.eq(ImpClasseImpressao.Fields.TIPOCUPS
								.toString(), tipoCups));

		List<ImpComputadorImpressora> list = executeCriteria(criteria);

		if (list.isEmpty()) {
			return null;
		}

		return list.get(0);
	}

	/**
	 * @param impComputadorImpressora
	 * @return
	 */
	public Long pesquisarComputadorImpressoraCount(
			ImpComputadorImpressora impComputadorImpressora) {
		return executeCriteriaCount(criarCriteriaPesquisaComputadorImpressora(impComputadorImpressora));
	}

	/**
	 * Metodo definido para retornar uma lista de computadores
	 * 
	 * @param impComputadorImpressora
	 * @param impClasseImpressao
	 * @param idComputador
	 *            ,String classeImpressao,Integer idImpressora
	 * @return List<ImpComputadorImpressora>
	 */
	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ImpComputadorImpressora impComputadorImpressora) {

		return executeCriteria(
				criarCriteriaPesquisaComputadorImpressora(impComputadorImpressora),
				firstResult, maxResult, orderProperty, asc);
	}

	/**
	 * Metodo definido para verificar a existencia de uma classeImpressao.
	 * 
	 * @param id
	 *            , classeImpressao e tipoImpressora
	 * @return boolean
	 */
	public boolean isComputadorImpressoraExistente(
			Integer idComputadorImpressora,
			ImpComputadorImpressora impComputadorImpressora) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputadorImpressora.class);

		Criterion restrictionComputador = Restrictions.eq(
				ImpComputadorImpressora.Fields.ID_COMPUTADOR.toString(),
				impComputadorImpressora.getImpComputador().getSeq());

		Criterion restrictionClasseImpressao = Restrictions.eq(
				ImpComputadorImpressora.Fields.ID_CLASSE_IMPRESSORA.toString(),
				impComputadorImpressora.getImpClasseImpressao().getId());

		criteria.add(Restrictions.and(restrictionComputador,
				restrictionClasseImpressao));

		ImpComputadorImpressora objImpComputadorImpressao = (ImpComputadorImpressora) executeCriteriaUniqueResult(criteria);

		// validacao para alteracao
		if (objImpComputadorImpressao != null && idComputadorImpressora != null) {
			if (objImpComputadorImpressao.getId()
					.equals(idComputadorImpressora)) {
				return false;
			}
			return true;
		}
		return objImpComputadorImpressao == null ? false : true;
	}

	/**
	 * Metodo responsavel pela definido da Criteria utilizada na pesquisa de
	 * computador
	 * 
	 * @param string
	 * @param asc
	 * @param maxResult
	 * @param firstResult
	 */
	private DetachedCriteria criarCriteriaPesquisaComputadorImpressora(
			ImpComputadorImpressora impComputadorImpressora) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputadorImpressora.class);

		if (impComputadorImpressora.getImpComputador() != null) {
			criteria.add(Restrictions.eq("impComputador.seq",
					impComputadorImpressora.getImpComputador().getSeq()));
		}

		if (impComputadorImpressora.getImpClasseImpressao() != null) {
			criteria.add(Restrictions.eq("impClasseImpressao.id",
					impComputadorImpressora.getImpClasseImpressao().getId()));
		}

		if (impComputadorImpressora.getImpImpressora() != null) {
			criteria.add(Restrictions.eq("impImpressora.id",
					impComputadorImpressora.getImpImpressora().getId()));
		}
		
		criteria.createAlias(ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString(), "com", JoinType.INNER_JOIN);
		criteria.createAlias(ImpComputadorImpressora.Fields.IMP_CLASSE_IMPRESSAO.toString(), "cls", JoinType.INNER_JOIN);
		criteria.createAlias(ImpComputadorImpressora.Fields.IMP_IMPRESSORA.toString(), "imp", JoinType.INNER_JOIN);
		

		return criteria;
	}
	
	/**
	 * Busca impressora cadastrada no computador
	 * 
	 * @param idComputador
	 * @return
	 */
	public ImpComputadorImpressora buscarImpressoraPorIdComputador(Integer idComputador) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ImpComputadorImpressora.class);
		criteria.add(Restrictions.eq(ImpComputadorImpressora.Fields.ID_COMPUTADOR.toString(),
				idComputador));

		return (ImpComputadorImpressora) executeCriteriaUniqueResult(criteria);
	}

	public Long pesquisarComputadorImpressoraCount(String ipAddress) {
		DetachedCriteria criteria = criarCriteriaPesquisaComputadorImpressora(ipAddress);
		return executeCriteriaCount(criteria);
	}

	public List<ImpComputadorImpressora> pesquisarComputadorImpressora(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, String ipAddress) {
		DetachedCriteria criteria = criarCriteriaPesquisaComputadorImpressora(ipAddress);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	private DetachedCriteria criarCriteriaPesquisaComputadorImpressora(
			String ipAddress) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ImpComputadorImpressora.class);

		if (StringUtils.isNotBlank(ipAddress)) {
			criteria.createAlias(
					ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString(),
					ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString());

			criteria.createAlias(
					ImpComputadorImpressora.Fields.IMP_IMPRESSORA.toString(),
					ImpComputadorImpressora.Fields.IMP_IMPRESSORA.toString());

			criteria.add(Restrictions.or(Restrictions.ilike(
					ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString()
							+ "." + ImpComputador.Fields.IP.toString(),
					ipAddress, MatchMode.EXACT), Restrictions.ilike(
					ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString()
							+ "." + ImpComputador.Fields.NOME.toString(),
					ipAddress, MatchMode.EXACT)));
		}
		
		criteria.setFetchMode(ImpComputadorImpressora.Fields.IMP_COMPUTADOR.toString(), FetchMode.JOIN);
		criteria.setFetchMode(ImpComputadorImpressora.Fields.IMP_CLASSE_IMPRESSAO .toString(), FetchMode.JOIN);
		criteria.setFetchMode(ImpComputadorImpressora.Fields.IMP_IMPRESSORA.toString(), FetchMode.JOIN);

		return criteria;
	}

}
