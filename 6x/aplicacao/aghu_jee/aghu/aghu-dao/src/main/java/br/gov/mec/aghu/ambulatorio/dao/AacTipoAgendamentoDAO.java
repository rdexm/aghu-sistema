package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * 
 * 
 */
public class AacTipoAgendamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AacTipoAgendamento> {

	
	
	private static final long serialVersionUID = -6297227783000891096L;

	/**
	 * Retorna a lista de autorizações ativas.
	 * 
	 * @return
	 */
	public List<AacTipoAgendamento> obterListaAutorizacoesAtivas() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoAgendamento.class);
		criteria.add(Restrictions.eq(AacTipoAgendamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public List<AacTipoAgendamento> pesquisarTiposAgendamento(String filtro) {
		DetachedCriteria criteria = createPesquisaTiposAgendamentoCriteria(filtro);
		return executeCriteria(criteria, 0, 25, null, false);
	}
	
	/**
	 * Ordenação por descrição e quantidade máxima de registros 100
	 * @param filtro
	 * @return
	 */
	public List<AacTipoAgendamento> obterListaTiposAgendamento(String filtro) {
		DetachedCriteria criteria = createPesquisaTiposAgendamentoCriteria(filtro);
		return executeCriteria(criteria, 0, 100, AacTipoAgendamento.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisarTiposAgendamentoCount(String filtro) {
		DetachedCriteria criteria = createPesquisaTiposAgendamentoCriteria(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createPesquisaTiposAgendamentoCriteria(String filtro) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoAgendamento.class);

		if (StringUtils.isNotBlank(filtro)) {
			Criterion descricaoCriterion = Restrictions.ilike(AacTipoAgendamento.Fields.DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE);

			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.or(Restrictions.eq(AacTipoAgendamento.Fields.SEQ.toString(), Short.valueOf(filtro)),
						descricaoCriterion));
			} else {
				criteria.add(descricaoCriterion);
			}
		}

		return criteria;
	}

	public DetachedCriteria obterCriteriaPesquisaTipoAgendamentoPaginado(Short filtroSeq, String filtroDescricao, 
            DominioSituacao filtroSituacaoCondicaoAtendimento, DetachedCriteria criteria) {

        if (filtroSeq != null) {
            criteria.add(Restrictions.eq(AacTipoAgendamento.Fields.SEQ.toString(), filtroSeq));
        }
        if (filtroDescricao != null) {
            criteria.add(Restrictions.ilike(AacTipoAgendamento.Fields.DESCRICAO.toString(), filtroDescricao, MatchMode.ANYWHERE));
        }
        if (filtroSituacaoCondicaoAtendimento != null) {
            criteria.add(Restrictions.eq(AacTipoAgendamento.Fields.SITUACAO.toString(), filtroSituacaoCondicaoAtendimento));
        }
        return criteria;

    }

    public List<AacTipoAgendamento> pesquisarTipoAgendamentoPaginado(Integer firstResult, Integer maxResult,
            String orderProperty, boolean asc, Short filtroSeq, String filtroDescricao,
            DominioSituacao filtroSituacaoCondicaoAtendimento) {

        DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoAgendamento.class);
        criteria = this.obterCriteriaPesquisaTipoAgendamentoPaginado(filtroSeq, filtroDescricao,
                filtroSituacaoCondicaoAtendimento, criteria);

        criteria.addOrder(Order.asc(AacTipoAgendamento.Fields.SEQ.toString()));

        return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
    }
    
    public Long countPesquisarTipoAgendamentoPaginado(Short filtroSeq, String filtroDescricao,
            DominioSituacao filtroSituacaoCondicaoAtendimento) {
        
        DetachedCriteria criteria = DetachedCriteria.forClass(AacTipoAgendamento.class);
        criteria = this.obterCriteriaPesquisaTipoAgendamentoPaginado(filtroSeq, filtroDescricao, 
		filtroSituacaoCondicaoAtendimento, criteria);
			
        return executeCriteriaCount(criteria);
    }
}