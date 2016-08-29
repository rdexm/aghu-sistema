package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MbcAreaTricProcCirg;
import br.gov.mec.aghu.model.MbcAreaTricotomia;

public class MbcAreaTricProcCirgDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcAreaTricProcCirg> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3144497357459621315L;

	
	public List<MbcAreaTricProcCirg> buscarAreaTricPeloSeqProcedimento(Integer pciSeq) {
		DetachedCriteria criteria = criarCriteriaAreaTricPeloSeqProcedimento(pciSeq);
		
		criteria.addOrder(Order.asc("ATC."+MbcAreaTricotomia.Fields.DESCRICAO.toString()));
		
		return super.executeCriteria(criteria);
	}

	private DetachedCriteria criarCriteriaAreaTricPeloSeqProcedimento(
			Integer pciSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcAreaTricProcCirg.class);
		criteria.createAlias(MbcAreaTricProcCirg.Fields.MBC_AREA_TRICOTOMIAS.toString(), "ATC");
		criteria.add(Restrictions.eq(MbcAreaTricProcCirg.Fields.PCI_SEQ.toString(), pciSeq));
		return criteria;
	}

}
