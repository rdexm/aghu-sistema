package br.gov.mec.aghu.perinatologia.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.view.VMcoExames;

public class VMcoExamesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<VMcoExames> {
	
	
	private static final long serialVersionUID = 3029538343587812231L;

	public VMcoExames obterVMcoExamesPorChave(String chave) {
		DetachedCriteria criteria = DetachedCriteria.forClass(VMcoExames.class, "vex");
	    criteria.add(Restrictions.eq("vex." + VMcoExames.Fields.CHAVE.toString(), chave));	        
	    return (VMcoExames) executeCriteriaUniqueResult(criteria);
	}

}
