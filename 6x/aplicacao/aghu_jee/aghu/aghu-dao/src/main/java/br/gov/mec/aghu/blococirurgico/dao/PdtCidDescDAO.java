package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoPlano;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.FatProcedHospIntCid;
import br.gov.mec.aghu.model.PdtCidDesc;
import br.gov.mec.aghu.model.PdtDescricao;

public class PdtCidDescDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<PdtCidDesc> {

	private static final long serialVersionUID = 3361287980977644448L;

	public List<PdtCidDesc> pesquisarPdtCidDescPorDdtSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtCidDesc.class);	
		criteria.add(Restrictions.eq(PdtCidDesc.Fields.DDT_SEQ.toString(), seq));	
		return executeCriteria(criteria);
	}

	public List<PdtCidDesc> pesquisarPdtCidDescPorDdtSeqComCidAtivo(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(PdtCidDesc.class);	
		criteria.createAlias(PdtCidDesc.Fields.AGH_CID.toString(), "cid");
		criteria.createAlias(PdtCidDesc.Fields.PDT_COMPLEMENTO_POR_CIDS.toString(), PdtCidDesc.Fields.PDT_COMPLEMENTO_POR_CIDS.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(PdtCidDesc.Fields.DDT_SEQ.toString(), seq));
		criteria.add(Restrictions.eq("cid."+AghCid.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria);
	}

	public Short obterMaxSeqpPdtCidDesc(final Integer ddtSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtCidDesc.class);
	
		criteria.setProjection(Projections.max(PdtCidDesc.Fields.SEQP.toString()));  
		criteria.add(Restrictions.eq(PdtCidDesc.Fields.DDT_SEQ.toString(), ddtSeq));
		
		final Short maxSeq = (Short)this.executeCriteriaUniqueResult(criteria);
		
		return maxSeq;
	}
	
	public Integer buscarCidSeqPdtCidDesc(Integer crgSeq, Integer phiSeq, DominioTipoPlano validade) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(PdtCidDesc.class, "PID");
		criteria.createAlias("PID." + PdtCidDesc.Fields.PDT_DESCRICAO.toString(), "DDT");
		criteria.setProjection(Projections.property("PID." + PdtCidDesc.Fields.CID_SEQ.toString()));
		
		DetachedCriteria subCriteriaTpc = DetachedCriteria.forClass(FatProcedHospIntCid.class, "TPC");
		subCriteriaTpc.setProjection(Projections.property("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString()));
		subCriteriaTpc.add(Restrictions.eqProperty("TPC." + FatProcedHospIntCid.Fields.CID_SEQ.toString(), 
				"PID." + PdtCidDesc.Fields.CID_SEQ.toString()));
		subCriteriaTpc.add(Restrictions.eq("TPC." + FatProcedHospIntCid.Fields.PHI_SEQ.toString(), phiSeq));
		subCriteriaTpc.add(Restrictions.or(Restrictions.isNull("TPC." + FatProcedHospIntCid.Fields.VALIDADE.toString()), 
				Restrictions.eq("TPC." + FatProcedHospIntCid.Fields.VALIDADE.toString(), validade)));
		
		criteria.add(Restrictions.eq("DDT." + PdtDescricao.Fields.CRG_SEQ.toString(), crgSeq));
		criteria.add(Subqueries.exists(subCriteriaTpc));
		criteria.addOrder(Order.asc("PID." + PdtCidDesc.Fields.SEQP.toString()));
		
		/*criteria.getExecutableCriteria(getSession()).setFirstResult(0);
		criteria.getExecutableCriteria(getSession()).setMaxResults(1);*/
		
		List<Object> listResult = executeCriteria(criteria, 0, 1, null, false);
		
		return (Integer) listResult.get(0);
	}
}
