package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.MamTrgMedicacoes;

public class MamTrgMedicacoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamTrgMedicacoes> {

	private static final long serialVersionUID = 5066927549916399044L;

	/**
	 * CURSOR c_med
	 * @param trgSeq
	 * @return List<MamTrgMedicacoes>
	 * 
	 */
	public List<MamTrgMedicacoes> pesquisarMamTrgMedicacoesComItem(Long trgSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamTrgMedicacoes.class, "mdm");
		criteria.createAlias("mdm."+MamTrgMedicacoes.Fields.ITEM_MEDICACAO.toString(), "imd");
		
		criteria.add(Restrictions.eq(MamTrgMedicacoes.Fields.TRG_SEQ.toString(), trgSeq));
		
		return executeCriteria(criteria);
	}
	
}
