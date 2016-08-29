package br.gov.mec.casca.app.dao;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.casca.model.Aplicacao;
import br.gov.mec.seam.dao.GenericDAO;

public class AplicacaoDAO extends GenericDAO<Aplicacao> {

	public List<Aplicacao> pesquisarAplicacaoPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
		
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(Aplicacao.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		
		return executeCriteria(criteria);
	}

	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo,
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		DetachedCriteria criteria = montarConsulta(servidor, porta, contexto,
				nome, externo);
		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	public Integer pesquisarAplicacoesCount(String servidor, Integer porta,
			String contexto, String nome, DominioSimNao externo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);

		if (StringUtils.isNotBlank(servidor)) {
			criteria.add(Restrictions.like(
					Aplicacao.Fields.SERVIDOR.toString(), servidor,
					MatchMode.ANYWHERE));
		}

		return executeCriteriaCount(criteria);
	}

	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo) {

		DetachedCriteria criteria = montarConsulta(servidor, porta, contexto,
				nome, externo);
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarConsulta(String servidor, Integer porta,
			String contexto, String nome, Boolean externo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);

		if (StringUtils.isNotBlank(servidor)) {
			criteria.add(Restrictions.like(
					Aplicacao.Fields.SERVIDOR.toString(), servidor,
					MatchMode.ANYWHERE));
		}

		if (porta != null) {
			criteria.add(Restrictions.eq(Aplicacao.Fields.PORTA.toString(),
					porta));
		}
		if (StringUtils.isNotBlank(contexto)) {
			criteria.add(Restrictions.eq(Aplicacao.Fields.CONTEXTO.toString(),
					contexto));
		}
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.like(Aplicacao.Fields.NOME.toString(),
					nome, MatchMode.ANYWHERE));
		}
		if (externo != null) {
			criteria.add(Restrictions.eq(Aplicacao.Fields.EXTERNO.toString(),
					externo));
		}

		// criteria.addOrder(Order.asc(Menu.Fields.NOME.toString()));

		return criteria;
	}
	
	/**
	 * Recupera todas as aplicações cadastradas.
	 * 
	 * @return
	 */
	public List<Aplicacao> recuperarAplicacoes() {
		DetachedCriteria criteria = criarDetachedCriteria();
		criteria.addOrder(Order.asc(Aplicacao.Fields.NOME.toString()));
		List<Aplicacao> aplicacoes = executeCriteria(criteria);
		return aplicacoes;
	}
}