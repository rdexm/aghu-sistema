package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.GrupoReportRotinaCciVO;
import br.gov.mec.aghu.dominio.DominioPeriodicidade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MciGrupoReportRotinaCci;

public class MciGrupoReportRotinaCciDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciGrupoReportRotinaCci> {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7767649429410883437L;
	
	public List<GrupoReportRotinaCciVO> pesquisarGrupoReportRotinaCci(Short codigo, String descricao, DominioSituacao situacao, DominioPeriodicidade periodicidade) {
		DetachedCriteria criteria = montarCriteriaGrupoReportRotinaCci(codigo, descricao, situacao, periodicidade);
		criteria.addOrder(Order.asc(MciGrupoReportRotinaCci.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria);
	}
	private DetachedCriteria montarCriteriaGrupoReportRotinaCci(Short codigo,String descricao, DominioSituacao situacao, DominioPeriodicidade periodicidade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciGrupoReportRotinaCci.class, "grr");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MciGrupoReportRotinaCci.Fields.SEQ.toString()), GrupoReportRotinaCciVO.Fields.SEQ.toString())
				.add(Projections.property(MciGrupoReportRotinaCci.Fields.DESCRICAO.toString()), GrupoReportRotinaCciVO.Fields.DESCRICAO.toString())
				.add(Projections.property(MciGrupoReportRotinaCci.Fields.IND_SITUACAO.toString()), GrupoReportRotinaCciVO.Fields.SITUACAO.toString())
				.add(Projections.property(MciGrupoReportRotinaCci.Fields.IND_MENSAL.toString()), GrupoReportRotinaCciVO.Fields.MENSAL.toString())
				.add(Projections.property(MciGrupoReportRotinaCci.Fields.IND_SEMANAL.toString()), GrupoReportRotinaCciVO.Fields.SEMANAL.toString())
				);
		if (codigo != null) {
			criteria.add(Restrictions.eq(MciGrupoReportRotinaCci.Fields.SEQ.toString(), codigo));
		}
		if (descricao != null && StringUtils.isNotEmpty(descricao)) {
			criteria.add(Restrictions.ilike(MciGrupoReportRotinaCci.Fields.DESCRICAO.toString(), descricao, MatchMode.ANYWHERE));
		}
		if (situacao != null) {
			criteria.add(Restrictions.eq(MciGrupoReportRotinaCci.Fields.IND_SITUACAO.toString(), situacao));
		}
		if (periodicidade != null) {
			if (periodicidade.compareTo(DominioPeriodicidade.M) == 0) {
				criteria.add(Restrictions.eq(MciGrupoReportRotinaCci.Fields.IND_MENSAL.toString(), true));
			} else if (periodicidade.compareTo(DominioPeriodicidade.S) == 0) {
				criteria.add(Restrictions.eq(MciGrupoReportRotinaCci.Fields.IND_SEMANAL.toString(), true));
			}
		}
    	criteria.setResultTransformer(Transformers.aliasToBean(GrupoReportRotinaCciVO.class));
		return criteria;
	}
}
