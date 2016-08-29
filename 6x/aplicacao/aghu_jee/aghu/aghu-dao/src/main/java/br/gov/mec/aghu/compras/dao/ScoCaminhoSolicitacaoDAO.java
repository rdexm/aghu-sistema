package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.ScoCaminhoSolicitacao;
import br.gov.mec.aghu.model.ScoPontoParadaSolicitacao;

public class ScoCaminhoSolicitacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoCaminhoSolicitacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2261955322391131839L;

	public List<ScoCaminhoSolicitacao> pesquisarCaminhoSolicitacao(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {

		DetachedCriteria criteria = this.obterCriteriaBasica(origemParada, destinoParada);
		criteria.createAlias(ScoCaminhoSolicitacao.Fields.PONTO_PARADA_ORIGEM.toString(), "PPO", JoinType.INNER_JOIN);
		criteria.createAlias(ScoCaminhoSolicitacao.Fields.PONTO_PARADA_DESTINO.toString(), "PPD", JoinType.INNER_JOIN);

		criteria.addOrder(Order.asc(ScoCaminhoSolicitacao.Fields.PPS_CODIGO_INICIO.toString()));
		criteria.addOrder(Order.asc(ScoCaminhoSolicitacao.Fields.PPS_CODIGO.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	public Long pesquisarCaminhoSolicitacaoCount(
			ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {

		final DetachedCriteria criteria = this
				.obterCriteriaBasica(origemParada, destinoParada);

		return this.executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaBasica(
			ScoPontoParadaSolicitacao origemParada, ScoPontoParadaSolicitacao destinoParada) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoCaminhoSolicitacao.class);

		if (origemParada != null) {
			if (origemParada.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoCaminhoSolicitacao.Fields.PPS_CODIGO_INICIO.toString(), origemParada.getCodigo()));
			}
		}

		if (destinoParada != null) {
			if (destinoParada.getCodigo() != null) {
				criteria.add(Restrictions.eq(
						ScoCaminhoSolicitacao.Fields.PPS_CODIGO.toString(), destinoParada.getCodigo()));
			}
		}
		
		return criteria;
	}
	
	/**
	 * Pesquisa caminho solicitação com PPS_CODIGO igual ao informado.
	 * @param codigo
	 * @author dilceia.alves
	 * @since 31/10/2012
	 */
	public Long pesquisarCaminhoSolicitacaoPorPpsCodigoCount(Short codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaminhoSolicitacao.class);

		criteria.add(Restrictions.eq(ScoCaminhoSolicitacao.Fields.PPS_CODIGO.toString(), codigo));

		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Pesquisa caminho solicitação com PPS_CODIGO igual ao informado.
	 * @param codigo
	 * @author dilceia.alves
	 * @since 31/10/2012
	 */
	public Long pesquisarCaminhoSolicitacaoPorPpsCodigoInicioCount(Short codigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoCaminhoSolicitacao.class);

		criteria.add(Restrictions.eq(ScoCaminhoSolicitacao.Fields.PPS_CODIGO_INICIO.toString(), codigo));

		return this.executeCriteriaCount(criteria);
	}
}
