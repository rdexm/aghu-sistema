package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import br.gov.mec.aghu.model.AfaComponenteNpt;
import br.gov.mec.aghu.model.AfaItemProducaoNpt;

public class AfaItemProducaoNptDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaItemProducaoNpt> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3969746408890873894L;

	public List<AfaItemProducaoNpt> listarPorMatCodigo(Integer cod) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaItemProducaoNpt.class,"AIP");
		criteria.createAlias("AIP."+AfaItemProducaoNpt.Fields.AFA_COMPONENTE_NPT.toString(), "ACN", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("ACN."+AfaComponenteNpt.Fields.MED_MAT_CODIGO.toString(), cod));

		return this.executeCriteria(criteria,0,1,null,false);
	}

	
}
