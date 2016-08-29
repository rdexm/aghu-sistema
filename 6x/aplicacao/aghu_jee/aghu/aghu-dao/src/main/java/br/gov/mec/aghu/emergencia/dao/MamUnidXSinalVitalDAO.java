package br.gov.mec.aghu.emergencia.dao;



import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamUnidAtendem;
import br.gov.mec.aghu.model.MamUnidXSinalVital;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamUnidXSinalVitalDAO extends BaseDao<MamUnidXSinalVital> {

	private static final long serialVersionUID = -4634735138837105644L;

	public Boolean existeUnidXSinalPorUnidadeAtendem(MamUnidAtendem mamUnidAtendem)	
	{
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(MamUnidXSinalVital.class, "MamUnidXSinalVital");
		
		criteria.add(Restrictions.eq(MamUnidXSinalVital.Fields.MAM_UNID_ATENDEM.toString(), mamUnidAtendem));
		
		return (this.executeCriteriaCount(criteria) > 0);
		
	}	
	
	

}