package br.gov.mec.aghu.configuracao.dao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghArquivoProcessamento;
import br.gov.mec.aghu.model.AghSistemas;


public class AghArquivoProcessamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AghArquivoProcessamento> {

	private static final long serialVersionUID = 1921709389106196497L;

	private DetachedCriteria criarCriteria() {
		return DetachedCriteria.forClass(AghArquivoProcessamento.class);
	}

	private DetachedCriteria criarCriteriaArquivosNaoConcluidos() {
		DetachedCriteria criteria = criarCriteria();
		criteria.add(Restrictions.isNull(AghArquivoProcessamento.Fields.DT_FIM_PROCESSAMENTO.toString()));
		return criteria;
	}

	private DetachedCriteria criarCriteriaArquivosNaoConcluidosPorSistema(AghSistemas sistema) {
		DetachedCriteria criteria = criarCriteriaArquivosNaoConcluidos();
		criteria.add(Restrictions.eq(AghArquivoProcessamento.Fields.SISTEMA.toString(), sistema));

		return criteria;
	}

	private DetachedCriteria criarArquivoNaoProcessado(AghSistemas sistema, String nome) {
		DetachedCriteria criteria = criarCriteriaArquivosNaoConcluidosPorSistema(sistema);
		criteria.add(Restrictions.eq(AghArquivoProcessamento.Fields.NOME.toString(), nome));
		criteria.addOrder(Order.desc(AghArquivoProcessamento.Fields.DT_CRIACAO.toString()));
		return criteria;
	}

	private DetachedCriteria criarArquivosNaoConcluidosIniciadosPorSistema(String sigla, List<Integer> arquivos) {
		DetachedCriteria criteria = criarCriteria();
		criteria.add(Restrictions.eq(AghArquivoProcessamento.Fields.SISTEMA.toString(), obterAghSistema(sigla)));
		criteria.add(Restrictions.in(AghArquivoProcessamento.Fields.ID.toString(), arquivos));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghArquivoProcessamento.Fields.ID.toString()))
				.add(Projections.property(AghArquivoProcessamento.Fields.PERCENTUAL_PROCESSADO.toString()))
				.add(Projections.property(AghArquivoProcessamento.Fields.DT_FIM_PROCESSAMENTO.toString())));
		criteria.addOrder(Order.asc(AghArquivoProcessamento.Fields.ORDEM.toString()));

		return criteria;
	}

	private DetachedCriteria criarCriteriaArquivosAbortadeos(AghSistemas sistema, Date limite) {
		DetachedCriteria criteria = criarCriteriaArquivosNaoConcluidosPorSistema(sistema);
		criteria.add(Restrictions.isNull(AghArquivoProcessamento.Fields.DT_FIM_PROCESSAMENTO.toString()));
		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.isNull(AghArquivoProcessamento.Fields.DT_INICIO_PROCESSAMENTO.toString()),
						Restrictions.lt(AghArquivoProcessamento.Fields.DT_CRIACAO.toString(), limite)),
				Restrictions.lt(AghArquivoProcessamento.Fields.DT_ULTIMO_PROCESSAMENTO.toString(), limite)));
		return criteria;
	}

	public AghArquivoProcessamento obterArquivoNaoProcessado(AghSistemas sistema, String nome) {
		DetachedCriteria criteria = criarArquivoNaoProcessado(sistema, nome);
		List<AghArquivoProcessamento> result = this.executeCriteria(criteria);
		if (result == null || result.isEmpty()) {
			return null;
		}
		return result.get(0);
	}

	public AghArquivoProcessamento obterArquivoNaoProcessadoVO(AghSistemas sistema, String nome) {
		DetachedCriteria criteria = criarArquivoNaoProcessado(sistema, nome);
		criteria.setProjection(Projections.projectionList().add(Projections.property(AghArquivoProcessamento.Fields.ID.toString()))
				.add(Projections.property(AghArquivoProcessamento.Fields.PERCENTUAL_PROCESSADO.toString()))
				.add(Projections.property(AghArquivoProcessamento.Fields.DT_FIM_PROCESSAMENTO.toString()))
				.add(Projections.property(AghArquivoProcessamento.Fields.NOME.toString())));
		List<Object[]> result = this.executeCriteria(criteria, 0, 1, null, true);
		if (result == null || result.isEmpty()) {
			return null;
		}
		AghArquivoProcessamento aghArquivoProcessamento = new AghArquivoProcessamento();
		aghArquivoProcessamento.setSeq((Integer) result.get(0)[0]);
		if (result.get(0)[1] != null) {
			aghArquivoProcessamento.setPercentualProcessado((Integer) result.get(0)[1]);
		}
		if (result.get(0)[2] != null) {
			aghArquivoProcessamento.setDthrFimProcessamento((Date) result.get(0)[2]);
		}
		if (result.get(0)[3] != null) {
			aghArquivoProcessamento.setNome((String) result.get(0)[3]);
		}
		return aghArquivoProcessamento;
	}
	
	
	public AghArquivoProcessamento obterArquivoNaoProcessado(String sigla, String nome) {
		return obterArquivoNaoProcessado(obterAghSistema(sigla), nome);
	}

	public List<AghArquivoProcessamento> pesquisarArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(String sigla, List<Integer> arquivos) {
		List<Object[]> result = executeCriteria(criarArquivosNaoConcluidosIniciadosPorSistema(sigla, arquivos));
		return obterValoresArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(result);
	}

	private List<AghArquivoProcessamento> obterValoresArquivosNaoConcluidosIniciadosPorSistemaDtFimProcessamento(List<Object[]> result) {
		if (result == null || result.isEmpty()) {
			return null;
		}
		List<AghArquivoProcessamento> retorno = new ArrayList<AghArquivoProcessamento>();
		for (Object[] ret : result) {
			AghArquivoProcessamento aghArquivoProcessamento = new AghArquivoProcessamento();
			aghArquivoProcessamento.setSeq((Integer) ret[0]);
			if (ret[1] != null) {
				aghArquivoProcessamento.setPercentualProcessado((Integer) ret[1]);
			}
			if (ret[2] != null) {
				aghArquivoProcessamento.setDthrFimProcessamento((Date) ret[2]);
			}
			retorno.add(aghArquivoProcessamento);
		}
		return retorno;
	}

	/**
	 * Retorna o sistema referente a uma sigla.
	 * 
	 * @param sigla
	 * @return
	 */
	public AghSistemas obterAghSistema(String sigla) {
		return this.find(AghSistemas.class, sigla);
	}

	public List<AghArquivoProcessamento> pesquisarArquivosAbortados(AghSistemas sistema, Integer minutosVencimento) {
		Date limite = new Date();
		limite.setTime(limite.getTime() - (minutosVencimento * 60000));
		DetachedCriteria criteria = criarCriteriaArquivosAbortadeos(sistema, limite);
		return executeCriteria(criteria);
	}
	
	public List<Integer> pesquisarIdsArquivosAbortados(AghSistemas sistema, Integer minutosVencimento) {
		Date limite = new Date();
		limite.setTime(limite.getTime() - (minutosVencimento * 60000));
		DetachedCriteria criteria = criarCriteriaArquivosAbortadeos(sistema, limite);
		criteria.setProjection(Projections.property(AghArquivoProcessamento.Fields.ID.toString()));
		return executeCriteria(criteria);
	}

	public void finalizarAghArquivoProcessamento(List<Integer> seqs){
		StringBuffer hql = new StringBuffer("UPDATE ").append(AghArquivoProcessamento.class.getName())
				.append(" SET ").append(AghArquivoProcessamento.Fields.DT_FIM_PROCESSAMENTO.toString()).append(" = :data")
				.append(" WHERE ").append(AghArquivoProcessamento.Fields.ID.toString()).append(" IN (:seqs)");
		Query query = createHibernateQuery(hql.toString());
		query.setDate("data", new Date());
		query.setParameterList("seqs", seqs);
		query.executeUpdate();
	}

	public void atualizarAghArquivoProcessamento(final Integer seq, final Date date, final Integer percent, final Date fimProcessamento){
		StringBuffer hql = new StringBuffer("UPDATE ").append(AghArquivoProcessamento.class.getName())
				.append(" SET ").append(AghArquivoProcessamento.Fields.DT_ULTIMO_PROCESSAMENTO.toString()).append(" = :data")
				.append(", ").append(AghArquivoProcessamento.Fields.PERCENTUAL_PROCESSADO.toString()).append(" = :percent");
		if(fimProcessamento != null){
			hql.append(", ").append(AghArquivoProcessamento.Fields.DT_FIM_PROCESSAMENTO.toString()).append(" = :fimProcessamento");
		}
		hql.append(" WHERE ").append(AghArquivoProcessamento.Fields.ID.toString()).append(" = :seq");
		Query query = createHibernateQuery(hql.toString());
		query.setDate("data", date);
		query.setInteger("percent", percent);
		if(fimProcessamento != null){
			query.setDate("fimProcessamento", fimProcessamento);
		}
		query.setInteger("seq", seq);
		query.executeUpdate();
	}
	
}
