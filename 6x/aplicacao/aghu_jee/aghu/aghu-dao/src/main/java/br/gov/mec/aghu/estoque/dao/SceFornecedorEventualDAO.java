package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.model.SceFornecedorEventual;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SceFornecedorEventualDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceFornecedorEventual>{
	
	private static final long serialVersionUID = -4560240605319685604L;

	public List<SceFornecedorEventual> obterFornecedorEventualPorSeqDescricao(Object param) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceFornecedorEventual.class);

		String strPesquisa = (String) param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroInteger(strPesquisa)) {
				criteria.add(Restrictions.eq(SceFornecedorEventual.Fields.SEQ.toString(), Integer.parseInt(strPesquisa)));
			} else {
				criteria.add(Restrictions.or(Restrictions.ilike(SceFornecedorEventual.Fields.RAZAO_SOCIAL.toString(), strPesquisa, MatchMode.ANYWHERE),
						Restrictions.ilike(SceFornecedorEventual.Fields.NOME_FANTASIA.toString(), strPesquisa, MatchMode.ANYWHERE)));
			}
		}

		return executeCriteria(criteria, 0, 100, SceFornecedorEventual.Fields.SEQ.toString(), true);
	}

}
