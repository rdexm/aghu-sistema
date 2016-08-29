package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamRespQuestAnamneses;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamRespQuestAnamnesesDAO extends BaseDao<MamRespQuestAnamneses> {

	/**
	 * SERIAL ID
	 */
	private static final long serialVersionUID = 1556844534074454430L;

	/**
	 * #50745 - P2 - Consulta as respostas estruturadas das questões de anmanese
	 */
	public List<MamRespQuestAnamneses> obterListaRespQuestAnamnesePorAnaSeq(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespQuestAnamneses.class);
		criteria.add(Restrictions.eq(MamRespQuestAnamneses.Fields.REA_ANA_SEQ.toString(), anaSeq));
		return executeCriteria(criteria);
	}
}
