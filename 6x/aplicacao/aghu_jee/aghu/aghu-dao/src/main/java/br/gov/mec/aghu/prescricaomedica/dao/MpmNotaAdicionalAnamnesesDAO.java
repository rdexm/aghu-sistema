package br.gov.mec.aghu.prescricaomedica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MpmAnamneses;
import br.gov.mec.aghu.model.MpmNotaAdicionalAnamneses;
import br.gov.mec.aghu.model.RapServidores;

public class MpmNotaAdicionalAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpmNotaAdicionalAnamneses> {

	private static final long serialVersionUID = 14567898909090L;

	public List<MpmNotaAdicionalAnamneses> listarNotasAdicionaisAnamnesesPorSeqAnamneses(Long seqAnamneses) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MpmNotaAdicionalAnamneses.class,"MNAA");
		criteria.createAlias("MNAA." + MpmAnamneses.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(MpmNotaAdicionalAnamneses.Fields.ANAMNESES_SEQ.toString(),seqAnamneses));
		criteria.addOrder(Order.desc(MpmNotaAdicionalAnamneses.Fields.DTHR_CRIACAO.toString()));
		return executeCriteria(criteria);
	}
	
	
	public boolean verificarNotasAdicionaisAnamnese(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(
		                MpmNotaAdicionalAnamneses.class, "NAA");
		criteria.createAlias(
		                "NAA." + MpmNotaAdicionalAnamneses.Fields.ANAMNESE.toString(),
		                "ANA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(
		                "ANA." + MpmAnamneses.Fields.SEQ.toString(), anaSeq));
		return executeCriteriaExists(criteria);
	}

}
