package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.PdtCidPorProc;
import br.gov.mec.aghu.model.PdtComplementoPorCid;
import br.gov.mec.aghu.model.PdtProc;
import br.gov.mec.aghu.model.PdtProcDiagTerap;

public class PdtComplementoPorCidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtComplementoPorCid> {
	
	private static final long serialVersionUID = 6698786129066467798L;

	public List<PdtComplementoPorCid> listarPdtComplementoPorCids(Integer dptSeq, Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtComplementoPorCid.class);

		criteria.add(Restrictions.eq(PdtComplementoPorCid.Fields.ID_CXP_DPT_SEQ.toString(), dptSeq));
		criteria.add(Restrictions.eq(PdtComplementoPorCid.Fields.ID_CXP_CID_SEQ.toString(), cidSeq));
		
		criteria.addOrder(Order.asc(PdtComplementoPorCid.Fields.ID_SEQP.toString()));
		criteria.addOrder(Order.asc(PdtComplementoPorCid.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Short obterMaxSeqP(Integer dptSeq, Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtComplementoPorCid.class);
		criteria.setProjection(Projections.max(PdtComplementoPorCid.Fields.ID_SEQP.toString()));
		
		criteria.add(Restrictions.eq(PdtComplementoPorCid.Fields.ID_CXP_DPT_SEQ.toString(), dptSeq));
		criteria.add(Restrictions.eq(PdtComplementoPorCid.Fields.ID_CXP_CID_SEQ.toString(), cidSeq));
		
		Object maxSeqp = executeCriteriaUniqueResult(criteria);
		
		if(maxSeqp == null){
			return 0;
		}else{
			return (Short) maxSeqp;
		}
	}
	
	public List<PdtComplementoPorCid> obterListaComplementoCidAtivos(Integer ddtSeq, Integer cidSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(PdtComplementoPorCid.class, "CMPL_CID");
		criteria.createAlias("CMPL_CID." + PdtComplementoPorCid.Fields.PDT_CID_POR_PROCS.toString(), "CPROCS", Criteria.INNER_JOIN);
		criteria.createAlias("CPROCS." + PdtCidPorProc.Fields.PDT_PROC_DIAG_TERAPS.toString(), "DIAGT", Criteria.INNER_JOIN);
		criteria.createAlias("DIAGT." + PdtProcDiagTerap.Fields.PDT_PROCES.toString(), "PROC", Criteria.INNER_JOIN);

		criteria.add(Restrictions.eq("PROC." + PdtProc.Fields.DDT_SEQ.toString(), ddtSeq));
		criteria.add(Restrictions.eq("CMPL_CID." + PdtComplementoPorCid.Fields.ID_CXP_CID_SEQ.toString(), cidSeq));
		criteria.add(Restrictions.eq("CMPL_CID." + PdtComplementoPorCid.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.addOrder(Order.asc("CMPL_CID." + PdtComplementoPorCid.Fields.DESCRICAO.toString()));
	
		return executeCriteria(criteria);
	}

}
