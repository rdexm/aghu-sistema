package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MptAgendaSessao;
import br.gov.mec.aghu.model.MptControleFreqSessao;
import br.gov.mec.aghu.model.MptItemPrcrModalidade;

public class MptControleFreqSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptControleFreqSessao> {
	
	private static final long serialVersionUID = 5177906318357603568L;

	public List<MptControleFreqSessao> listarSessoesFisioterapia(Integer trpSeq, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		DetachedCriteria criteria = criteriaListarSessoesFisioterapia(trpSeq);
		criteria.addOrder(Order.desc("CFS." + MptControleFreqSessao.Fields.DTHR_AGENDA.toString()));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long listarSessoesFisioterapiaCount(Integer trpSeq) {
		DetachedCriteria criteria = criteriaListarSessoesFisioterapia(trpSeq);
		
		return executeCriteriaCount(criteria);
	}
	
	protected DetachedCriteria criteriaListarSessoesFisioterapia(Integer trpSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptControleFreqSessao.class , "CFS");
		criteria.createAlias("CFS." + MptControleFreqSessao.Fields.MPT_TIPO_OCORRENCIA_SESSOES.toString(), "TOS", Criteria.LEFT_JOIN);
		criteria.createAlias("CFS." + MptControleFreqSessao.Fields.MPT_AGENDA_SESSOES.toString(), "AGE");
		criteria.createAlias("AGE." + MptAgendaSessao.Fields.MPT_ITEM_PRCR_MODALIDADES.toString(), "ITM");
		criteria.createAlias("ITM." + MptItemPrcrModalidade.Fields.MPT_TIPO_MODALIDADES.toString(), "TMD");
		criteria.createAlias("ITM." + MptItemPrcrModalidade.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("AGE." + MptAgendaSessao.Fields.MPT_HORARIO_GRADE_SESSOES.toString(), "HGS");
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.TRP_SEQ.toString(), trpSeq));
		
		return criteria;
	}
}
