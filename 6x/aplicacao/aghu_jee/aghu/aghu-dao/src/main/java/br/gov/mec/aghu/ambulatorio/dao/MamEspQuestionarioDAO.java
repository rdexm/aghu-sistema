package br.gov.mec.aghu.ambulatorio.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamEspQuestionario;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamQuestionario;

public class MamEspQuestionarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamQuestao>{

	private static final long serialVersionUID = -7551207729193054359L;

	/**
	 * #49992 P1 - CONSULTA PARA OBTER CUR_ROT_ESPEC
	 * @param qutSeq
	 * @param espSeq
	 * @return
	 */
	public Boolean obterCursorCurRotEspec(Short espSeq, String indTipoPac, Integer tieSeq){
		
		final String QUT = "QUT.";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEspQuestionario.class, "ESQ"); 
		criteria.createAlias("ESQ."+MamEspQuestionario.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.TIE_SEQ, tieSeq));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_SITUACAO, "A"));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_LIBERADO, "S"));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_ORIGEM, "A"));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_CUSTOMIZACAO, DominioSimNao.N));
		criteria.add(Restrictions.or(
				Restrictions.eq(QUT+MamQuestionario.Fields.IND_INSTITUCIONAL, "N"),
				Restrictions.and(Restrictions.eq(QUT+MamQuestionario.Fields.IND_INSTITUCIONAL, "S"), 
								 Restrictions.eq(QUT+MamQuestionario.Fields.IND_TIPO_PAC, "Q")),
				Restrictions.and(Restrictions.eq(QUT+MamQuestionario.Fields.IND_INSTITUCIONAL, "S"), 
								 Restrictions.eq(QUT+MamQuestionario.Fields.IND_TIPO_PAC, indTipoPac))
				));
		
		criteria.add(Restrictions.eq("ESQ."+MamEspQuestionario.Fields.IND_SITUACAO, "A"));
		criteria.add(Restrictions.eq("ESQ."+MamEspQuestionario.Fields.ESP_SEQ, espSeq));
		
		return executeCriteriaExists(criteria);
	}
}
