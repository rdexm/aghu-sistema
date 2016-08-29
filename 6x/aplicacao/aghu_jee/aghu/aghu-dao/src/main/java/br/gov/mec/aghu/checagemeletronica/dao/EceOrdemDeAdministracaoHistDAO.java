package br.gov.mec.aghu.checagemeletronica.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.EceOrdemDeAdministracaoHist;

public class EceOrdemDeAdministracaoHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<EceOrdemDeAdministracaoHist>  {
	
	private static final long serialVersionUID = -7725083966614971044L;

	public List<EceOrdemDeAdministracaoHist> buscarOrdemAdminHist(Integer atdSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(EceOrdemDeAdministracaoHist.class);
		criteria.add(Restrictions.eq(EceOrdemDeAdministracaoHist.Fields.PME_ATD_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc(EceOrdemDeAdministracaoHist.Fields.DATA_REFERENCIA.toString()));
		
		return executeCriteria(criteria);
	}
}
