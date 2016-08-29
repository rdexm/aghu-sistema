package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelExameResuNotificacao;
import br.gov.mec.aghu.model.AelExameResuNotificacaoId;
import br.gov.mec.aghu.model.AelExamesNotificacao;


public class AelExameResuNotificacaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExameResuNotificacao> {
	
	private static final long serialVersionUID = 4598613874912515726L;

	private DetachedCriteria obterCriteriaAelExameResuNotificacao(final String exnEmaExaSigla, final Integer exnEmaManSeq, final Integer exnCalSeq, final Boolean order) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelExameResuNotificacao.class);
		criteria.add(Restrictions.eq(AelExameResuNotificacao.Fields.EXN_EMA_EXA_SIGLA.toString(), exnEmaExaSigla));
		criteria.add(Restrictions.eq(AelExameResuNotificacao.Fields.EXN_EMA_MAN_SEQ.toString(), exnEmaManSeq));
		criteria.add(Restrictions.eq(AelExameResuNotificacao.Fields.EXN_CAL_SEQ.toString(), exnCalSeq));
		if(order){
			criteria.addOrder(Order.asc(AelExameResuNotificacao.Fields.SEQP.toString()));	
		}
		return criteria;
	}
	
	public Short obterExameResultadoNotificacaoNextSeqp(AelExameResuNotificacaoId id, Boolean order) {
		final DetachedCriteria criteria = obterCriteriaAelExameResuNotificacao(id.getExnEmaExaSigla(), id.getExnEmaManSeq(), id.getExnCalSeq(), false);
		criteria.setProjection(Projections.max(AelExameResuNotificacao.Fields.SEQP.toString()));
		Object res = executeCriteriaUniqueResult(criteria);
		if (res == null) {
			return 1;
		}
		return (short) (((Number)res).intValue() + 1);
	}
	
	public List<AelExameResuNotificacao> pesquisarExameResultadoNotificacao(String sigla, Integer manSeq, Integer calSeq){
		DetachedCriteria criteria = obterCriteriaAelExameResuNotificacao(sigla, manSeq, calSeq, true);
		return executeCriteria(criteria);
	}
	
	public Boolean existeDependenciaExameNotificacao(AelExamesNotificacao exameNotificacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExameResuNotificacao.class);
		criteria.createAlias(AelExameResuNotificacao.Fields.AEL_EXAMES_NOTIFICACAO.toString(), "exameNotificacao");
		criteria.add(Restrictions.eq("exameNotificacao."+AelExamesNotificacao.Fields.EMA_EXA_SIGLA.toString(), exameNotificacao.getId().getEmaExaSigla()));
		criteria.add(Restrictions.eq("exameNotificacao."+AelExamesNotificacao.Fields.EMA_MAN_SEQ.toString(), exameNotificacao.getId().getEmaManSeq()));
		criteria.add(Restrictions.eq("exameNotificacao."+AelExamesNotificacao.Fields.CAL_SEQ.toString(), exameNotificacao.getId().getCalSeq()));
		return executeCriteriaCount(criteria) > 0;
		
	}
	
}