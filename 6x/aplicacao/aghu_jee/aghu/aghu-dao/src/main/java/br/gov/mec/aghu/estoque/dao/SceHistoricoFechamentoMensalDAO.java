package br.gov.mec.aghu.estoque.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceHistoricoFechamentoMensal;

public class SceHistoricoFechamentoMensalDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceHistoricoFechamentoMensal>{
	
	
	private static final long serialVersionUID = 7639035402837906115L;

	/**
	 * Pesquisa histórico de fechamento mensal através da data de competência e ordenado por etapa
	 * @param dataCompetencia
	 * @return
	 */
	public List<SceHistoricoFechamentoMensal> pesquisarHistoricoFechamentoMensalPorDataCompetencia(Date dataCompetencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoFechamentoMensal.class);
		criteria.add(Restrictions.eq(SceHistoricoFechamentoMensal.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		criteria.addOrder(Order.asc(SceHistoricoFechamentoMensal.Fields.ETAPA.toString()));
		return executeCriteria(criteria);
	}


	/**
	 * Pesquisa histórico de fechamento mensal através da data de competência e com fechamento mensal concluído e sem ocorrência
	 * @param dataCompetencia
	 * @return
	 */
	public List<SceHistoricoFechamentoMensal> pesquisartHistoricoFechamentoMensalConcluidoSemOcorrenciaPorDataCompetencia(Date dataCompetencia){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoFechamentoMensal.class);
		
		criteria.add(Restrictions.eq(SceHistoricoFechamentoMensal.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		criteria.add(Restrictions.eq(SceHistoricoFechamentoMensal.Fields.FECHAMENTO_CONCLUIDO.toString(), Boolean.TRUE));
		criteria.add(Restrictions.isNull(SceHistoricoFechamentoMensal.Fields.OCORRENCIA.toString()));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * Obtém a criteria para pesquisa de histórico de fechamento mensal através da data de competência e com fechamento mensal concluído
	 * @param dataCompetencia
	 * @return
	 */
	private DetachedCriteria obterCriteriaHistoricoFechamentoMensalConcluidoPorDataCompetencia(Date dataCompetencia){
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoFechamentoMensal.class);
		criteria.add(Restrictions.eq(SceHistoricoFechamentoMensal.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		criteria.add(Restrictions.eq(SceHistoricoFechamentoMensal.Fields.FECHAMENTO_CONCLUIDO.toString(), Boolean.TRUE));
		return criteria;
	}
	
	/**
	 * Verifica a existência de histórico de fechamento mensal através da data de competência e com fechamento mensal concluído
	 * @param dataCompetencia
	 * @return
	 */
	public Boolean existeHistoricoFechamentoMensalConcluidoPorDataCompetencia(Date dataCompetencia) {
		return executeCriteriaCount(this.obterCriteriaHistoricoFechamentoMensalConcluidoPorDataCompetencia(dataCompetencia)) > 0;
	}
	
	/**
	 * Busca o valor da etapa de fechamento mensal
	 * @param dataCompetencia
	 * @return
	 */
	public Short obterValorEtapaHistoricoFechamentoMensal(Date dataCompetencia){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(SceHistoricoFechamentoMensal.class);
		criteria.setProjection(Projections.max(SceHistoricoFechamentoMensal.Fields.ETAPA.toString()));
		criteria.add(Restrictions.eq(SceHistoricoFechamentoMensal.Fields.DATA_COMPETENCIA.toString(), dataCompetencia));
		
		final Short max = (Short) executeCriteriaUniqueResult(criteria);
		
		if (max == null) {
			return 0;
		}
		return max;
	}
	

}
