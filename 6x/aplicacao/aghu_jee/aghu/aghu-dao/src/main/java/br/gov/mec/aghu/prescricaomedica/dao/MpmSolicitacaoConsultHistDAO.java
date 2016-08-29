package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MpmSolicitacaoConsultHist;

/**
 * @author rcorvalao
 * 
 */
public class MpmSolicitacaoConsultHistDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmSolicitacaoConsultHist> {

	
	private static final long serialVersionUID = -801466865920114254L;

	public List<MpmSolicitacaoConsultHist> obterHistoricoConsultoria(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultHist.class);
		
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultHist.Fields.ID_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultHist.Fields.ID_DTHR_DESATIVACAO.toString()));
		
		return executeCriteria(criteria);
	}
	
	public Long obterHistoricoConsultoriaCount(Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmSolicitacaoConsultHist.class);
		
		criteria.add(Restrictions.eq(MpmSolicitacaoConsultHist.Fields.ID_ATD_SEQ.toString(), atdSeq));
		criteria.add(Restrictions.isNull(MpmSolicitacaoConsultHist.Fields.ID_DTHR_DESATIVACAO.toString()));
		
		return executeCriteriaCount(criteria);
	}
}
