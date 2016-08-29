package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.FatAtoObrigatorioProced;

public class FatAtoObrigatorioProcedDAO
		extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatAtoObrigatorioProced> {

	private static final long serialVersionUID = 6921627669322725525L;

	protected DetachedCriteria obterCriteriaPorIph(final Short iphPhoSeq, final Integer iphSeq) {

		DetachedCriteria result = null;

		result = DetachedCriteria.forClass(FatAtoObrigatorioProced.class);
		result.add(Restrictions.eq(FatAtoObrigatorioProced.Fields.IPH_PHO_SEQ.toString(), iphPhoSeq));
		result.add(Restrictions.eq(FatAtoObrigatorioProced.Fields.IPH_SEQ.toString(), iphSeq));

		return result;
	}

	public List<FatAtoObrigatorioProced> obterListaPorIph(final Short iphPhoSeq, final Integer iphSeq) {

		List<FatAtoObrigatorioProced> result = null;
		DetachedCriteria criteria = null;

		criteria = this.obterCriteriaPorIph(iphPhoSeq, iphSeq);
		//criteria.getExecutableCriteria(getSession()).setCacheable(Boolean.TRUE);
		result = this.executeCriteria(criteria, true);

		return result;
	}
}