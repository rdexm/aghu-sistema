package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.MpaCadOrdItemHemoter;



public class MpaCadOrdItemHemoterDao extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaCadOrdItemHemoter> {

	private static final long serialVersionUID = -4984912779740443607L;

	public List<MpaCadOrdItemHemoter> pesquisarMpaCadOrdItemHemoterPorCodigoProcedimentoHemoterapico(String pheCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"COIH");
		dc.createAlias("COIH.".concat(MpaCadOrdItemHemoter.Fields.ABS_PROCED_HEMOTERAPICO.toString()), "APH");
		dc.add(Restrictions.ilike("APH.".concat(AbsProcedHemoterapico.Fields.CODIGO.toString()), pheCodigo, MatchMode.EXACT));
		return executeCriteria(dc);		
	}
	
}