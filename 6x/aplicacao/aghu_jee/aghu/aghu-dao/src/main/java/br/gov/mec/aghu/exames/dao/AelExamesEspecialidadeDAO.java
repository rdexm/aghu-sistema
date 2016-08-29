package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.AelExamesEspecialidade;

public class AelExamesEspecialidadeDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesEspecialidade> {

	private static final long serialVersionUID = -3924107819469823342L;

	private DetachedCriteria obterCriteria() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesEspecialidade.class);
		return criteria;
    }
	

	public List<AelExamesEspecialidade> buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeq(
			String emaExaSigla, Integer emaManSeq, Short unfSeq) {
		
		DetachedCriteria dc = obterCriteria();

		dc.createAlias(AelExamesEspecialidade.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.UFE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.UFE_EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		
		return executeCriteria(dc);
	}
	
	public AelExamesEspecialidade buscaListaAelExamesEspecialidadePorEmaExaSiglaEmaManSeqUnfSeqEspSeq(
			String emaExaSigla, Integer emaManSeq, Short unfSeq, Short espSeq) {
		
		DetachedCriteria dc = obterCriteria();
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.UFE_EMA_EXA_SIGLA.toString(), emaExaSigla));
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.UFE_EMA_MAN_SEQ.toString(), emaManSeq));
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.UFE_UNF_SEQ.toString(), unfSeq));
		dc.add(Restrictions.eq(AelExamesEspecialidade.Fields.ESP_SEQ.toString(), espSeq));
		
		return (AelExamesEspecialidade)executeCriteriaUniqueResult(dc);
	}
}