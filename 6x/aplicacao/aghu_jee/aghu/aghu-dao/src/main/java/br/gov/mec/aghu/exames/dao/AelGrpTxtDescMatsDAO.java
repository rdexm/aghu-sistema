package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrpTxtDescMats;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelGrpTxtDescMatsDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelGrpTxtDescMats> {
	private static final long serialVersionUID = 7364447360812667935L;

	public List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(Short codigo, String descricao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpTxtDescMats.class);

		if (codigo != null) {
			criteria.add(Restrictions.eq(AelGrpTxtDescMats.Fields.SEQ.toString(), codigo));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(AelGrpTxtDescMats.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(AelGrpTxtDescMats.Fields.IND_SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc(AelGrpTxtDescMats.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);

	}

	public List<AelGrpTxtDescMats> pesquisarGrupoTextoPadraoDescMats(String filtro, DominioSituacao situacao) {
		final DetachedCriteria criteria = obterCriteria(filtro, situacao);
		criteria.addOrder(Order.asc(AelGrpTxtDescMats.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}

	public Long pesquisarGrupoTextoPadraoDescMatsCount(String filtro, DominioSituacao situacao) {
		return executeCriteriaCount(obterCriteria(filtro, situacao));
	}

	private DetachedCriteria obterCriteria(String filtro, DominioSituacao situacao) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelGrpTxtDescMats.class);

		if (situacao != null) {
			criteria.add(Restrictions.eq(AelGrpTxtDescMats.Fields.IND_SITUACAO.toString(), situacao));
		}

		if (StringUtils.isNotBlank(filtro)) {
			if (CoreUtil.isNumeroShort(filtro)) {
				criteria.add(Restrictions.eq(AelGrpTxtDescMats.Fields.SEQ.toString(), Short.valueOf(filtro)));

			} else {
				criteria.add(Restrictions.ilike(AelGrpTxtDescMats.Fields.DESCRICAO.toString(), filtro, MatchMode.ANYWHERE));
			}
		}

		return criteria;
	}
}
