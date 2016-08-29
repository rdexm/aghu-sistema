package br.gov.mec.aghu.exames.dao;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AelPacAgrpPesqExames;

public class AelPacAgrpPesqExameDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelPacAgrpPesqExames> {

	private static final long serialVersionUID = 4875729358977761030L;

	private DetachedCriteria obterCriteria() {
		return DetachedCriteria.forClass(AelPacAgrpPesqExames.class);
    }
	
	public AelPacAgrpPesqExames obterPorId(Integer axeSeq, Integer pacCodigo) {
		DetachedCriteria criteria = obterCriteria();
		criteria.add(Restrictions.eq(AelPacAgrpPesqExames.Fields.AXE_SEQ.toString(), axeSeq));
		criteria.add(Restrictions.eq(AelPacAgrpPesqExames.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		return (AelPacAgrpPesqExames) executeCriteriaUniqueResult(criteria);
	}	
	
}
