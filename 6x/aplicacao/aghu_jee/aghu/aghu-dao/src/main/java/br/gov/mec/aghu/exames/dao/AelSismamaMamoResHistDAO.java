package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelSismamaMamoResHist;


public class AelSismamaMamoResHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelSismamaMamoResHist> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5806496226057529270L;
	

	public Long obterRespostaMamografiaCountHist(Integer soeSeq, Short seqp) {
		DetachedCriteria dc = DetachedCriteria.forClass(AelSismamaMamoResHist.class, "RES");
		
		dc.add(Restrictions.eq("RES.".concat(AelSismamaMamoResHist.Fields.ISE_SEQP.toString()), seqp));
		dc.add(Restrictions.eq("RES.".concat(AelSismamaMamoResHist.Fields.ISE_SOE_SEQ.toString()), soeSeq));
		
		return executeCriteriaCount(dc);
	}


	public List<AelSismamaMamoResHist> obterRespostasMamoHist(
			Integer soeSeq, Short seqp) {
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp);
		
		dc.createAlias("RES.".concat(AelSismamaMamoResHist.Fields.AEL_SISMAMA_MAMO_CAD.toString()), "CAD");

		return executeCriteria(dc);
	}


	public List<AelSismamaMamoResHist> obterRespostasMamografiaHistRespNull(
			Integer soeSeq, Short seqp) {
		DetachedCriteria dc = criarCriteriaPesquisarRespostaMamografia(soeSeq, seqp);
		
		dc.createAlias("RES.".concat(AelSismamaMamoResHist.Fields.AEL_SISMAMA_MAMO_CAD.toString()), "CAD");

		dc.add(Restrictions.isNull("RES.".concat(AelSismamaMamoResHist.Fields.RESPOSTA.toString())));
		
		return executeCriteria(dc);
	}
	
	private DetachedCriteria criarCriteriaPesquisarRespostaMamografia(Integer soeSeq, Short seqp) {
		DetachedCriteria dc = DetachedCriteria.forClass(AelSismamaMamoResHist.class, "RES");
		
		dc.add(Restrictions.eq("RES.".concat(AelSismamaMamoResHist.Fields.ISE_SEQP.toString()), seqp));
		dc.add(Restrictions.eq("RES.".concat(AelSismamaMamoResHist.Fields.ISE_SOE_SEQ.toString()), soeSeq));
		return dc;
	}
	
	

}
