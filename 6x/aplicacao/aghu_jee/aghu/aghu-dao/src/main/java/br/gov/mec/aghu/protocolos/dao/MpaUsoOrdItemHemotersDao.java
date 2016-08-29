package br.gov.mec.aghu.protocolos.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.MpaUsoOrdItemHemoters;

public class MpaUsoOrdItemHemotersDao extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MpaUsoOrdItemHemoters> {

	private static final long serialVersionUID = 821841259798557626L;

	public List<MpaUsoOrdItemHemoters> pesquisarMpaUsoOrdItemHemotersPorPheCodigo(String pheCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"UOIH");
		dc.createAlias("UOIH.".concat(MpaUsoOrdItemHemoters.Fields.PROCEDIMENTO_HEMOTERAPICO.toString()), "APH");
		dc.add(Restrictions.ilike("APH.".concat(AbsProcedHemoterapico.Fields.CODIGO.toString()), pheCodigo, MatchMode.EXACT));
		return executeCriteria(dc);		
	}

}
