package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamItemSinalVital;
import br.gov.mec.aghu.model.MamTrgSinalVital;

public class MamTrgSinalVitalDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgSinalVital> {

	private static final long serialVersionUID = 198520599018022946L;

	/**
	 * CURSOR c_sinais
	 * @param trgSeq
	 * @return List<MamTrgSinalVital>
	 * 
	 */
	public List<MamTrgSinalVital> pesquisarMamTrgSinalVitalComItem(Long trgSeq) {

		DetachedCriteria dc = DetachedCriteria.forClass(MamTrgSinalVital.class, "SVI");
		dc.createAlias("SVI.".concat(MamTrgSinalVital.Fields.MAM_ITEM_SINAL_VITAL.toString()), "ISV");
		
		dc.add(Restrictions.eq("SVI.".concat(MamTrgSinalVital.Fields.TRG_SEQ.toString()), trgSeq));
		dc.add(Restrictions.eq("SVI.".concat(MamTrgSinalVital.Fields.IND_USO.toString()), DominioSimNao.S));

		dc.addOrder(Order.asc("ISV.".concat(MamItemSinalVital.Fields.ORDEM.toString())));
		dc.addOrder(Order.asc("SVI.".concat(MamTrgSinalVital.Fields.TRG_SEQ.toString())));
		
		return executeCriteria(dc);
	}
	
}
