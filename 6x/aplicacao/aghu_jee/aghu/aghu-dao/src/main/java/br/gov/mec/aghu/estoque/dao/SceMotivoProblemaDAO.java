package br.gov.mec.aghu.estoque.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SceMotivoProblemaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SceMotivoProblema> {

	
	private static final long serialVersionUID = -6642278387285149742L;

	public List<SceMotivoProblema> pesquisaMotivosProblemasPorSeqDescricao(String param, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SceMotivoProblema.class);

		String strPesquisa = param;

		if (StringUtils.isNotBlank(strPesquisa)) {

			if (CoreUtil.isNumeroShort(strPesquisa)) {
				criteria.add(Restrictions.eq(SceMotivoProblema.Fields.SEQ.toString(), Short.parseShort(strPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SceMotivoProblema.Fields.DESCRICAO.toString(), StringUtils.trim(strPesquisa), MatchMode.ANYWHERE));
			}
		}
		
		if (situacao != null){
			criteria.add(Restrictions.eq(SceMotivoProblema.Fields.IND_SITUACAO.toString(), situacao));
		}

		return executeCriteria(criteria, 0, 100, null, false);
	}
	

}
