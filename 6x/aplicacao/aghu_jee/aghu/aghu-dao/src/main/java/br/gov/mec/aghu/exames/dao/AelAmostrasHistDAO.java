package br.gov.mec.aghu.exames.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.Query;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelAmostraItemExamesHist;
import br.gov.mec.aghu.model.AelAmostrasHist;
import br.gov.mec.aghu.model.AelItemHorarioAgendadoHist;
import br.gov.mec.aghu.model.AelSolicitacaoExamesHist;

public class AelAmostrasHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAmostrasHist> {
	
	private static final long serialVersionUID = 2153207798197831251L;

	/**
	 * Lista todas as amostras por agendamento
	 * 
	 * @param hedGaeUnfSeq, hedGaeSeqp, hedDthrAgenda
	 * @return lista de amostras
	 */
	public List<AelAmostrasHist> listarAmostrasPorAgendamento(final Short hedGaeUnfSeq, final Integer hedGaeSeqp, final Date hedDthrAgenda) {
		StringBuffer hql = new StringBuffer(300);
		hql.append(" select amo from " + AelAmostrasHist.class.getSimpleName() + " amo ");
		hql.append(" where (amo." + AelAmostrasHist.Fields.SOE_SEQ.toString() + " , amo." + AelAmostrasHist.Fields.SEQP.toString() + ") ");
		hql.append(" 	in ( ");
		hql.append(" select aie." + AelAmostraItemExamesHist.Fields.AMO_SOE_SEQ.toString() + ", aie."+AelAmostraItemExamesHist.Fields.AMO_SEQP);
		hql.append(" from " + AelAmostraItemExamesHist.class.getSimpleName() + " aie ");
		hql.append(" where (aie." + AelAmostraItemExamesHist.Fields.ISE_SOE_SEQ.toString() + " , aie." + AelAmostraItemExamesHist.Fields.ISE_SEQP.toString() + ") ");
		hql.append(" 	in ( ");
		hql.append(" select iha." + AelItemHorarioAgendadoHist.Fields.ISE_SOE_SEQ.toString() + ", iha."+AelItemHorarioAgendadoHist.Fields.ISE_SEQP);
		hql.append(" from " + AelItemHorarioAgendadoHist.class.getSimpleName() + " iha ");
		hql.append(" where iha.").append(AelItemHorarioAgendadoHist.Fields.HED_GAE_UNF_SEQ.toString()).append(" = :hedGaeUnfSeq ");
		hql.append(" and iha.").append(AelItemHorarioAgendadoHist.Fields.HED_GAE_SEQP.toString()).append(" = :hedGaeSeqp ");
		hql.append(" and iha.").append(AelItemHorarioAgendadoHist.Fields.HED_DTHR_AGENDA.toString()).append(" = :hedDthrAgenda ))");
		hql.append(" order by amo.").append(AelAmostrasHist.Fields.SOE_SEQ.toString()).append(", amo.").append(AelAmostrasHist.Fields.SEQP.toString());

		Query query = createHibernateQuery(hql.toString());
		query.setParameter("hedGaeUnfSeq", hedGaeUnfSeq);
		query.setParameter("hedGaeSeqp", hedGaeSeqp);
		query.setParameter("hedDthrAgenda", hedDthrAgenda);

		return query.list();
	}
	
	public List<AelAmostrasHist> buscarAmostrasPorSolicitacaoExameEItemSolicitacao(
			final AelSolicitacaoExamesHist solicitacaoExame,
			final Short amostraSeqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAmostrasHist.class);
		
		//criteria.createCriteria(AelAmostrasHist.Fields.AMOSTRA_ITEM_EXAMES.toString(), "aie", Criteria.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AelAmostrasHist.Fields.SOE_SEQ.toString(), solicitacaoExame.getSeq()));
		//if(iseSoeSeq != null){
		//	criteria.add(Restrictions.eq("aie."+AelAmostraItemExamesHist.Fields.SEQP.toString(), iseSoeSeq));
		//}
		
		if(amostraSeqp != null){
			criteria.add(Restrictions.eq(AelAmostrasHist.Fields.SEQP.toString(), amostraSeqp));
		}
		
		return executeCriteria(criteria);	
	}
	
}
