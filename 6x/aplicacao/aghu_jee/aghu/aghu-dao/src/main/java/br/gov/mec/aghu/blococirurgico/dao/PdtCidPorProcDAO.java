package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;

public class PdtCidPorProcDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtCidPorProc> {
	
	private static final long serialVersionUID = 6698786129066467798L;

	private DetachedCriteria montaCriteriaPdtCidPorProcPorCidProcedimento(PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtCidPorProc.class);
		criteria.createAlias(PdtCidPorProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "TER");
		criteria.createAlias(PdtCidPorProc.Fields.AGH_CID.toString(), "CID");
		if(cid != null) {
			criteria.add(Restrictions.eq(PdtCidPorProc.Fields.AGH_CID.toString(),cid));
		}
		if(procedimento != null) {
			criteria.add(Restrictions.eq(PdtCidPorProc.Fields.PDT_PROC_DIAG_TERAPS.toString(),procedimento));
		}
		if(situacao != null){
			criteria.add(Restrictions.eq(PdtCidPorProc.Fields.IND_SITUACAO.toString(), situacao));
		}
		return criteria;
	}
	
	public List<PdtCidPorProc> listarPdtCidPorProcPorProcedimentoSituacaoCid(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid) {
		DetachedCriteria criteria = montaCriteriaPdtCidPorProcPorCidProcedimento(procedimento,situacao,cid);
		criteria.addOrder(Order.asc(PdtCidPorProc.Fields.DPT_SEQ.toString()));
		criteria.addOrder(Order.asc(PdtCidPorProc.Fields.CID_SEQ.toString()));
		return executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}

	public Long listarPdtCidPorProcPorProcedimentoSituacaoCidCount(PdtProcDiagTerap procedimento, DominioSituacao situacao, AghCid cid) {
		DetachedCriteria criteria = montaCriteriaPdtCidPorProcPorCidProcedimento(procedimento,situacao,cid);
		return executeCriteriaCount(criteria);
	}
	
}
