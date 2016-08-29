package br.gov.mec.aghu.casca.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.casca.model.Aplicacao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class AplicacaoDAO extends BaseDao<Aplicacao> {
	
	private static final long serialVersionUID = -1867920253369664868L;
	
	
	public List<Aplicacao> pesquisarAplicacaoPorNome(Object nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
		String _nome = nome.toString();
		if (StringUtils.isNotBlank(_nome)) {
			criteria.add(Restrictions.ilike(Aplicacao.Fields.NOME.toString(),
					_nome, MatchMode.ANYWHERE));
		}
		return executeCriteria(criteria);
	}

	
	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo,
			Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		
		DetachedCriteria criteria = montarConsulta(servidor, porta, contexto, nome, externo);
		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	
	public Long pesquisarAplicacoesCount(String servidor, Integer porta,
			String contexto, String nome, Boolean externo) {

		DetachedCriteria criteria = montarConsulta(servidor, porta, contexto, nome, externo);

		return executeCriteriaCount(criteria);
	}

	
	public List<Aplicacao> pesquisarAplicacoes(String servidor, Integer porta,
			String contexto, String nome, Boolean externo) {

		DetachedCriteria criteria = montarConsulta(servidor, porta, contexto,
				nome, externo);
		return executeCriteria(criteria,true);
	}

	public Aplicacao obterAplicacaoPorContexto(final String nomeContexto, final String servidor) {
		String contextoFiltro = nomeContexto;
		if (nomeContexto.contains("/")) {
			contextoFiltro = contextoFiltro.replace('/', ' ').trim();
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);
		criteria.add(Restrictions.ilike(Aplicacao.Fields.CONTEXTO.toString(), contextoFiltro, MatchMode.EXACT));
		
		List<Aplicacao> aplicacoes = executeCriteria(criteria);
		
		if (aplicacoes != null && aplicacoes.size() > 1 && StringUtils.isNotBlank(servidor)) {
			for (Aplicacao aplicacao : aplicacoes) {
				if (servidor.equalsIgnoreCase(aplicacao.getServidor())) {
					return aplicacao;
				}
			}
		}
		
		return aplicacoes != null && !aplicacoes.isEmpty() ? aplicacoes.get(0) : null;
	}
	
	
	private DetachedCriteria montarConsulta(String servidor, Integer porta,	String contexto, String nome, Boolean externo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(Aplicacao.class);

		if (StringUtils.isNotBlank(servidor)) {
			criteria.add(Restrictions.ilike(Aplicacao.Fields.SERVIDOR.toString(), servidor, MatchMode.ANYWHERE));
		}
		if (porta != null && porta != 0) {
			criteria.add(Restrictions.eq(Aplicacao.Fields.PORTA.toString(),	porta));
		}
		if (StringUtils.isNotBlank(contexto)) {
			criteria.add(Restrictions.ilike(Aplicacao.Fields.CONTEXTO.toString(), contexto));
		}
		if (StringUtils.isNotBlank(nome)) {
			criteria.add(Restrictions.ilike(Aplicacao.Fields.NOME.toString(), nome, MatchMode.ANYWHERE));
		}
		if (externo != null) {
			criteria.add(Restrictions.eq(Aplicacao.Fields.EXTERNO.toString(), externo));
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
		List<Aplicacao> aplicacoes = executeCriteria(criteria,true);
		return aplicacoes;
	}
}