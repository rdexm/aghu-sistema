package br.gov.mec.aghu.controleinfeccao.dao;



import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciTipoGrupoProcedRisco;

public class MciTipoGrupoProcedRiscoDAO  extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciTipoGrupoProcedRisco> {


	private static final long serialVersionUID = -3486588475559128256L;

	
	//#3796 - C1
	public List<MciTipoGrupoProcedRisco> pesquisarMciTipoGrupoProcedRisco(Integer firstResult, Integer maxResults, String orderProperty, boolean asc, Short seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = criarCriteriaPesquisaMciTipoGrupoProcedRisco(seq, descricao, indSituacao);
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}

	private DetachedCriteria criarCriteriaPesquisaMciTipoGrupoProcedRisco(Short seq, String descricao, DominioSituacao indSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciTipoGrupoProcedRisco.class);
		if(seq != null) {
			criteria.add(Restrictions.eq(MciTipoGrupoProcedRisco.Fields.SEQ.toString(), seq));
		}
		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(MciTipoGrupoProcedRisco.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (indSituacao != null) {
			criteria.add(Restrictions.eq(MciTipoGrupoProcedRisco.Fields.IND_SITUACAO .toString(), indSituacao));
		}
		return criteria;
	}	
	

	public Long pesquisarMciTipoGrupoProcedRiscoCount(Short codigo, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = criarCriteriaPesquisaMciTipoGrupoProcedRisco(codigo, descricao, situacao);
		return (Long) executeCriteriaCount(criteria);
	}
}
