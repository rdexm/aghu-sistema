package br.gov.mec.aghu.prescricaoenfermagem.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EpeUnidPacAtendimento;

public class EpeUnidPacAtendimentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EpeUnidPacAtendimento> {

	private static final long serialVersionUID = 6270056352978599140L;

	public List<EpeUnidPacAtendimento> obterEpeUnidPacAtendimentoPorApaAtdSeqApaSeq(Integer apaAtdSeq, Integer apaSeq) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "UPA");
		
		dc.add(Restrictions.eq("UPA.".concat(EpeUnidPacAtendimento.Fields.APA_ATD_SEQ.toString()), apaAtdSeq));
		dc.add(Restrictions.eq("UPA.".concat(EpeUnidPacAtendimento.Fields.APA_SEQ.toString()), apaSeq));
		
		return executeCriteria(dc);
	}

}
