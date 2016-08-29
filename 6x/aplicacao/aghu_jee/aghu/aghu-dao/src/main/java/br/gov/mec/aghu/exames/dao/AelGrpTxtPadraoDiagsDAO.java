package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpTxtPadraoDiags;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelGrpTxtPadraoDiagsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpTxtPadraoDiags> {

	private static final long serialVersionUID = 5087300470840788126L;
	
	public List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final Short seq, final String descricao, final DominioSituacao situacao) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(new AelGrpTxtPadraoDiags(seq, descricao, situacao, null, null));
		criteria.addOrder(Order.asc(AelGrpTxtPadraoDiags.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AelGrpTxtPadraoDiags> pesquisarAelGrpTxtPadraoDiags(final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = this.obterCriteriaBasica(filtro, situacao);
		criteria.addOrder(Order.asc(AelGrpTxtPadraoDiags.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public Long pesquisarAelGrpTxtPadraoDiagsCount(final String filtro, final DominioSituacao situacao) {
		return executeCriteriaCount(obterCriteriaBasica(filtro, situacao));
	}

	private DetachedCriteria obterCriteriaBasica(final AelGrpTxtPadraoDiags aelGrpTxtPadraoDiags) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpTxtPadraoDiags.class);

		if (aelGrpTxtPadraoDiags != null) {
			if (aelGrpTxtPadraoDiags.getSeq() != null) {
				criteria.add(Restrictions.eq(AelGrpTxtPadraoDiags.Fields.SEQ.toString(), aelGrpTxtPadraoDiags.getSeq()));
			}

			if (StringUtils.isNotBlank(aelGrpTxtPadraoDiags.getDescricao())) {
				criteria.add(Restrictions.ilike(AelGrpTxtPadraoDiags.Fields.DESCRICAO.toString(), aelGrpTxtPadraoDiags.getDescricao(), MatchMode.ANYWHERE));
			}

			if (aelGrpTxtPadraoDiags.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AelGrpTxtPadraoDiags.Fields.IND_SITUACAO.toString(), aelGrpTxtPadraoDiags.getIndSituacao()));
			}
		}

		return criteria;
	}
	
	
	private DetachedCriteria obterCriteriaBasica(final String filtro, final DominioSituacao situacao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpTxtPadraoDiags.class);

		if (situacao != null) {
			criteria.add(Restrictions.eq(AelGrpTxtPadraoDiags.Fields.IND_SITUACAO.toString(), situacao));
		}
		
		if (StringUtils.isNotBlank(filtro)) {
			if(CoreUtil.isNumeroShort(filtro)){
				criteria.add(Restrictions.eq(AelGrpTxtPadraoDiags.Fields.SEQ.toString(), Short.valueOf(filtro)));
				
			} else {
				criteria.add(Restrictions.ilike(AelGrpTxtPadraoDiags.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}
	
}
