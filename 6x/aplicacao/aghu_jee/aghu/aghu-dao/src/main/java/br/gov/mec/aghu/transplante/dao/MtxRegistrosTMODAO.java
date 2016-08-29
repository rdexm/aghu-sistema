package br.gov.mec.aghu.transplante.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.model.MtxRegistrosTMO;

public class MtxRegistrosTMODAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MtxRegistrosTMO> {

	private static final long serialVersionUID = -2279681396392753273L;
	
	public MtxRegistrosTMO obterRegistroTransplantePorTransplante(Integer transplanteSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MtxRegistrosTMO.class, "MRT");

		criteria.createAlias("MRT."+MtxRegistrosTMO.Fields.MTX_TRANSPLANTE.toString(),"TPR", JoinType.INNER_JOIN);
		criteria.createAlias("MRT."+MtxRegistrosTMO.Fields.SERVIDOR.toString(),"RAP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("MRT." + MtxRegistrosTMO.Fields.MTX_TRANSPLANTE_SEQ.toString(), transplanteSeq));		
		return (MtxRegistrosTMO) executeCriteriaUniqueResult(criteria);
	}
}
