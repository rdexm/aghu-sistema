package br.gov.mec.aghu.bancosangue.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.AbsProcedHemoterapico;
import br.gov.mec.aghu.model.AbsValidAmostraProced;

public class AbsValidAmostraProcedDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AbsValidAmostraProced> {

	private static final long serialVersionUID = -1611236129996709748L;

	public List<AbsValidAmostraProced> pesquisarAbsValidAmostraProcedPorPheCodigo(String pheCodigo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(),"VAP");
		dc.createAlias("VAP.".concat(AbsValidAmostraProced.Fields.ABS_PROCED_HEMOTERAPICO.toString()), "APH");
		dc.add(Restrictions.ilike("APH.".concat(AbsProcedHemoterapico.Fields.CODIGO.toString()), pheCodigo, MatchMode.EXACT));
		return executeCriteria(dc);		
	}
	
	public List<AbsValidAmostraProced> pesquisarAbsValidAmostraProcedPorPheCodigoPeriodo(String pheCodigo, Short vMeses) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AbsValidAmostraProced.class);
		criteria.add(Restrictions.eq(AbsValidAmostraProced.Fields.PHE_CODIGO.toString(), pheCodigo));
		criteria.add(Restrictions.le(AbsValidAmostraProced.Fields.IDADE_MES_INICIAL.toString(), vMeses));
		criteria.add(Restrictions.ge(AbsValidAmostraProced.Fields.IDADE_MES_FINAL.toString(), vMeses));
		return this.executeCriteria(criteria);
	}
}
