package br.gov.mec.aghu.ambulatorio.dao;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamCaractSitEmerg;

public class MamCaractSitEmergDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamCaractSitEmerg> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4736233716726129009L;

	/**
	 * @ORADB c_csi 
	 * #44179 
	 * @param segSeq, caracteristica
	 * @return 'S'
	 */
	public String cCsi(Short segSeq,String caracteristica){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamCaractSitEmerg.class);
		criteria.add(Restrictions.eq(MamCaractSitEmerg.Fields.SEQ.toString(), segSeq));
		criteria.add(Restrictions.eq(MamCaractSitEmerg.Fields.CARACTERISTICA.toString(), caracteristica));
		if(executeCriteriaExists(criteria)){
			return "S";
		}
		return StringUtils.EMPTY;
	}

}
