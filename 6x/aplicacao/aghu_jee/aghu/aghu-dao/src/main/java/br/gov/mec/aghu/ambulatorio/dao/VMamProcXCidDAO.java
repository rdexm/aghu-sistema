package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.VMamProcXCid;

public class VMamProcXCidDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMamProcXCid> {
	
	private static final long serialVersionUID = -5211680829699096005L;

	private DetachedCriteria montarCriteriaPesquisarCidsParaProcedimentoAtendimentoMedico(String parametro, Integer prdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMamProcXCid.class);
		criteria.add(Restrictions.or(Restrictions.isNull(VMamProcXCid.Fields.TPC_IND_VALIDADE.toString()), 
				Restrictions.eq(VMamProcXCid.Fields.TPC_IND_VALIDADE.toString(), DominioSituacao.A)));
		criteria.add(Restrictions.eq(VMamProcXCid.Fields.PRD_SEQ.toString(), prdSeq));
		if (StringUtils.isNotEmpty(parametro)) {
			Criterion lhs = Restrictions.ilike(VMamProcXCid.Fields.CID_CODIGO.toString(),
					parametro, MatchMode.ANYWHERE);
			Criterion rhs = Restrictions.ilike(VMamProcXCid.Fields.CID_DESC_EDIT.toString(),
					parametro, MatchMode.ANYWHERE);
			criteria.add(Restrictions.or(lhs, rhs));			
		}
		return criteria;
	}
	
	public List<VMamProcXCid> pesquisarCidsParaProcedimentoAtendimentoMedico(String parametro, Integer prdSeq) {
		DetachedCriteria criteria = montarCriteriaPesquisarCidsParaProcedimentoAtendimentoMedico(parametro, prdSeq);
		return executeCriteria(criteria);
	}
	
	public Long pesquisarCidsParaProcedimentoAtendimentoMedicoCount(String parametro, Integer prdSeq) {
		DetachedCriteria criteria = montarCriteriaPesquisarCidsParaProcedimentoAtendimentoMedico(parametro, prdSeq);
		return executeCriteriaCount(criteria);
	}	
	
	public List<VMamProcXCid> pesquisarCidsPorPrdSeqCidSeq(Integer prdSeq, Integer cidSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMamProcXCid.class);
		criteria.add(Restrictions.eq(VMamProcXCid.Fields.PRD_SEQ.toString(), prdSeq));
		if (cidSeq != null) {
			criteria.add(Restrictions.eq(VMamProcXCid.Fields.CID_SEQ.toString(), cidSeq));
		}
		return executeCriteria(criteria);
	}

}
