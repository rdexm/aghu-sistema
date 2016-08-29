package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelArquivoIntegracao;

public class AelArquivoIntegracaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelArquivoIntegracao> {

	private static final long serialVersionUID = -5236397110961706219L;

	public List<AelArquivoIntegracao> pesquisarArquivos(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			Date dataInicial, Date dataFinal, String nomeArquivo,
			boolean solicitacaoComErro, boolean solicitacaoSemAgenda) {

		DetachedCriteria criteria = this.criarCriteria(dataInicial, dataFinal,
				nomeArquivo, solicitacaoComErro, solicitacaoSemAgenda);
		criteria.addOrder(Order.desc(AelArquivoIntegracao.Fields.DATA_PROCESSAMENTO.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);

	}

	
	public Long pesquisarArquivosCount(
			Date dataInicial, Date dataFinal, String nomeArquivo,
			boolean solicitacaoComErro, boolean solicitacaoSemAgenda) {

		DetachedCriteria criteria = this.criarCriteria(dataInicial, dataFinal,
				nomeArquivo, solicitacaoComErro, solicitacaoSemAgenda);
		return executeCriteriaCount(criteria);

	}

	private DetachedCriteria criarCriteria(Date dataInicial, Date dataFinal, String nomeArquivo,
			boolean solicitacaoComErro, boolean solicitacaoSemAgenda){
		
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelArquivoIntegracao.class);

		if (dataInicial != null && dataFinal != null) {
			criteria.add(Restrictions.between(
					AelArquivoIntegracao.Fields.DATA_PROCESSAMENTO.toString(),
					dataInicial, dataFinal));
		}

		if (!StringUtils.isEmpty(nomeArquivo)) {
			criteria.add(Restrictions.ilike(
					AelArquivoIntegracao.Fields.NOME_ARQUIVO_ENTRADA.toString(),
					nomeArquivo, MatchMode.ANYWHERE));
		}

		if (solicitacaoComErro) {
			criteria.add(Restrictions.gt(
					AelArquivoIntegracao.Fields.TOTAL_RECUSADA.toString(), 0));
		}

		if (solicitacaoSemAgenda) {
			criteria.add(Restrictions.gt(
					AelArquivoIntegracao.Fields.TOTAL_SEM_AGENDA.toString(), 0));
		}				
		
		return criteria;
	}
	
	public List<AelArquivoIntegracao> pesquisarArquivoIntegracaoPeloNome(
			Object param) {
		String nomeArquivoEntrada = (String) param;

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelArquivoIntegracao.class);

		if (!StringUtils.isEmpty(nomeArquivoEntrada)) {
			criteria.add(Restrictions.ilike(
					AelArquivoIntegracao.Fields.NOME_ARQUIVO_ENTRADA.toString(),
					nomeArquivoEntrada,MatchMode.ANYWHERE));
		}

		criteria.addOrder(Order
				.desc(AelArquivoIntegracao.Fields.DATA_PROCESSAMENTO.toString()));
		return this.executeCriteria(criteria);
	}

}
