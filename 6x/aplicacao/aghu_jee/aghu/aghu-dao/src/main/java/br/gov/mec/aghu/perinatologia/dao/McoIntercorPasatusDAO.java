package br.gov.mec.aghu.perinatologia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.McoIntercorPasatus;

public class McoIntercorPasatusDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<McoIntercorPasatus>{

	private static final long serialVersionUID = 742913619743480826L;

	/***
	 * @param short ingOpaSeq
	 * 
	 * @return list<McoIntercorPasatus>
	 * 
	 * @see Q_OPA1
	 */
   
	public List<McoIntercorPasatus> listarIntercorPasatusPorSeq(Integer ingOpaSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(McoIntercorPasatus.class);
		
		criteria.add(Restrictions.eq(McoIntercorPasatus.Fields.SEQ.toString(), ingOpaSeq));

		return executeCriteria(criteria);
	}

}
