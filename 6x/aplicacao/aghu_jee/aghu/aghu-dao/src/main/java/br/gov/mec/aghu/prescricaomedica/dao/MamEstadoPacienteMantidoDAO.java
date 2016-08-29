package br.gov.mec.aghu.prescricaomedica.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamEstadoPacienteMantido;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamEstadoPacienteMantidoDAO extends BaseDao<MamEstadoPacienteMantido> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5880687868791015237L;

	/**
	 * #44197 - CURSOR - cur_epm
	 * @author marcelo.deus
	 */
	public String obterCursorMamEstadoPacienteMantido(Long cRgtSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamEstadoPacienteMantido.class, "EPM");
		criteria.add(Restrictions.eq("EPM." + MamEstadoPacienteMantido.Fields.MAM_REGISTRO_SEQ.toString(), cRgtSeq));
		Boolean  existe = executeCriteriaExists(criteria);
		
		if(existe){
			return "S";
		} else {
			return null;
		}
	}
}
