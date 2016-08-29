package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTipoSolProcedimento;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamTipoSolicitacaoProcedimentoDAO extends BaseDao<MamTipoSolProcedimento> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4207896233919763714L;
	
	public List<MamTipoSolProcedimento> pesquisarTipoSolicitacaoProcedimentosPaginado(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		return this.pesquisarListaTipoSolicitacaoProcedimentosPaginado(firstResult, maxResult, orderProperty, asc, tipoSolicitacaoProcedimento);
	}
	
	public Long countTipoSolicitacaoProcedimentosPaginado(MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoSolProcedimento.class);
		criteria = this.obterCriteriaPesquisaTipoSolicitacaoProcedimentosPaginado(tipoSolicitacaoProcedimento, criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	public Long countPesquisarTipoSolicitacaoProcedimentosPaginado(MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoSolProcedimento.class);
		criteria = this.obterCriteriaPesquisaTipoSolicitacaoProcedimentosPaginado(tipoSolicitacaoProcedimento, criteria);
		
		return executeCriteriaCount(criteria);
	}
	
	public List<MamTipoSolProcedimento> pesquisarListaTipoSolicitacaoProcedimentosPaginado(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc, MamTipoSolProcedimento tipoSolicitacaoProcedimento) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTipoSolProcedimento.class);
		criteria = this.obterCriteriaPesquisaTipoSolicitacaoProcedimentosPaginado(tipoSolicitacaoProcedimento, criteria);
		
		criteria.addOrder(Order.asc(MamTipoSolProcedimento.Fields.SEQ.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	protected DetachedCriteria obterCriteriaPesquisaTipoSolicitacaoProcedimentosPaginado(MamTipoSolProcedimento tipoSolicitacaoProcedimento,
			DetachedCriteria criteria) {
		
		if (tipoSolicitacaoProcedimento.getSeq() != null) {
			criteria.add(Restrictions.eq(MamTipoSolProcedimento.Fields.SEQ.toString(), tipoSolicitacaoProcedimento.getSeq()));
		}
		if (tipoSolicitacaoProcedimento.getDescricao() != null) {
			criteria.add(Restrictions.ilike(MamTipoSolProcedimento.Fields.DESCRICAO.toString(), tipoSolicitacaoProcedimento.getDescricao(), MatchMode.ANYWHERE));
		}
		if (tipoSolicitacaoProcedimento.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(MamTipoSolProcedimento.Fields.IND_SITUACAO.toString(), tipoSolicitacaoProcedimento.getIndSituacao()));
		}
		if (tipoSolicitacaoProcedimento.getIndDigitaJustif() != null) {
			criteria.add(Restrictions.eq(MamTipoSolProcedimento.Fields.IND_DIGITA_JUSTIF.toString(), tipoSolicitacaoProcedimento.getIndDigitaJustif()));
		}
		return criteria;
	}
	
}
