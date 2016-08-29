package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.MbcFichaGas;

public class MbcFichaGasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcFichaGas> {
	
	private static final long serialVersionUID = -1135764821442850303L;
	public final String MBC_FICHA_ANESTESIAS = "fan";
	

	private DetachedCriteria getCriteriaJoinFichaAnestesias() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcFichaGas.class);
		criteria.createAlias(MbcFichaGas.Fields.MBC_FICHA_ANESTESIAS.toString(), MBC_FICHA_ANESTESIAS);
		return criteria;
	}

	
	public List<MbcFichaGas> listarMbcFichaGasesComMaterial(
			Long seqMbcFichaAnestesia) {
		
		DetachedCriteria criteria = getCriteriaJoinFichaAnestesias();
		
		criteria.createAlias(MbcFichaGas.Fields.SCO_MATERIAL.toString(), "sco");
		
		criteria.add(Restrictions.eq(MbcFichaGas.Fields.MBC_FICHA_ANESTESIAS.toString()  +"."+ MbcFichaAnestesias.Fields.SEQ.toString(), seqMbcFichaAnestesia));
		
		criteria.addOrder(Order.asc(MbcFichaGas.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
		
	}


}
