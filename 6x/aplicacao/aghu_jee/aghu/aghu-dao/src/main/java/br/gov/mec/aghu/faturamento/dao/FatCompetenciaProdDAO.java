package br.gov.mec.aghu.faturamento.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacaoCompProd;
import br.gov.mec.aghu.model.FatCompetenciaProd;

public class FatCompetenciaProdDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<FatCompetenciaProd> {

	private static final long serialVersionUID = 8618870516464406909L;

	public List<FatCompetenciaProd> pesquisarFatCompetenciaProd(
			final FatCompetenciaProd competenciaProducao,
			final Integer firstResult, final Integer maxResult,
			final String orderProperty, final boolean asc) {

		return executeCriteria(
				getCriteriaListarCompetencia(competenciaProducao), firstResult,
				maxResult, orderProperty, asc);

	}

	public Long pesquisarFatCompetenciaProdCount(
			final FatCompetenciaProd competenciaProducao) {

		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCompetenciaProd.class);

		if (competenciaProducao.getMes() != null) {
			criteria.add(Restrictions.eq(
					FatCompetenciaProd.Fields.MES.toString(),
					competenciaProducao.getMes()));
		}

		if (competenciaProducao.getAno() != null) {
			criteria.add(Restrictions.eq(
					FatCompetenciaProd.Fields.ANO.toString(),
					competenciaProducao.getAno()));
		}

		if (competenciaProducao.getDthrInicioProd() != null) {
			criteria.add(Restrictions.ge(
					FatCompetenciaProd.Fields.DTHR_INICIO_PROD.toString(), competenciaProducao.getDthrInicioProd()));
		}

		if (competenciaProducao.getDthrFimProd() != null) {
			criteria.add(Restrictions.le(
					FatCompetenciaProd.Fields.DTHR_FIM_PROD.toString(),competenciaProducao.getDthrFimProd()));
		}

		if (competenciaProducao.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					FatCompetenciaProd.Fields.IND_SITUACAO.toString(),
					competenciaProducao.getIndSituacao()));
		}

		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaListarCompetencia(
			final FatCompetenciaProd competenciaProd) {

		final DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCompetenciaProd.class);

		criteria.addOrder(Order.desc(FatCompetenciaProd.Fields.ANO.toString()));
		criteria.addOrder(Order.desc(FatCompetenciaProd.Fields.MES.toString()));
		criteria.addOrder(Order.desc(FatCompetenciaProd.Fields.DTHR_INICIO_PROD
				.toString()));

		if (competenciaProd.getMes() != null) {
			criteria.add(Restrictions.eq(
					FatCompetenciaProd.Fields.MES.toString(),
					competenciaProd.getMes()));
		}

		if (competenciaProd.getAno() != null) {
			criteria.add(Restrictions.eq(
					FatCompetenciaProd.Fields.ANO.toString(),
					competenciaProd.getAno()));
		}

		if (competenciaProd.getDthrInicioProd() != null) {
			criteria.add(Restrictions.ge(
					FatCompetenciaProd.Fields.DTHR_INICIO_PROD.toString(), competenciaProd.getDthrInicioProd()));
		}

		if (competenciaProd.getDthrFimProd() != null) {
			criteria.add(Restrictions.le(
					FatCompetenciaProd.Fields.DTHR_FIM_PROD.toString(),competenciaProd.getDthrFimProd()));
		}

		if (competenciaProd.getIndSituacao() != null) {
			criteria.add(Restrictions.eq(
					FatCompetenciaProd.Fields.IND_SITUACAO.toString(),
					competenciaProd.getIndSituacao()));
		}
		return criteria;
	}

	public FatCompetenciaProd obtemCompetenciaAtiva() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCompetenciaProd.class);

		criteria.add(Restrictions.eq(
				FatCompetenciaProd.Fields.IND_SITUACAO.toString(),
				DominioSituacaoCompProd.A));

		return (FatCompetenciaProd) this.executeCriteriaUniqueResult(criteria);
	}

	public List<FatCompetenciaProd> obtemCompetenciaAnterior() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(FatCompetenciaProd.class);

		criteria.add(Restrictions.eq(
				FatCompetenciaProd.Fields.IND_SITUACAO.toString(),
				DominioSituacaoCompProd.F));
		criteria.addOrder(Order.asc(FatCompetenciaProd.Fields.DTHR_FIM_PROD
				.toString()));

		return executeCriteria(criteria);
	}

}
