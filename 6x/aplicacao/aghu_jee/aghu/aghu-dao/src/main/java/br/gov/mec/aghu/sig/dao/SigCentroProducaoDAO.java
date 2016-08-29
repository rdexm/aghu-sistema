package br.gov.mec.aghu.sig.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoCentroProducaoCustos;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SigCentroProducao;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SigCentroProducaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<SigCentroProducao> {

	private static final long serialVersionUID = -605872042062054812L;

	public List<SigCentroProducao> pesquisarCentroProducao() {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCentroProducao.class);
		criteria.addOrder(Order.asc(SigCentroProducao.Fields.NOME.toString()));
		return executeCriteria(criteria);
	}

	public List<SigCentroProducao> pesquisarCentroProducaoCentroTipoSituacao(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			String nomeCentroProducao, DominioTipoCentroProducaoCustos tipoCentroProducao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigCentroProducao.class);

		if (StringUtils.isNotBlank(nomeCentroProducao)) {
			criteria.add(Restrictions.ilike(SigCentroProducao.Fields.NOME.toString(), nomeCentroProducao.trim(), MatchMode.ANYWHERE));
		}

		if (tipoCentroProducao != null) {
			criteria.add(Restrictions.eq(SigCentroProducao.Fields.IND_TIPO.toString(), tipoCentroProducao));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(SigCentroProducao.Fields.IND_SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc(SigCentroProducao.Fields.NOME.toString()));

		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarCentroProducaoCount(String nomeCentroProducao, DominioTipoCentroProducaoCustos tipoCentroProducao, DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria.forClass(SigCentroProducao.class);

		if (StringUtils.isNotBlank(nomeCentroProducao)) {
			criteria.add(Restrictions.ilike(SigCentroProducao.Fields.NOME.toString(), nomeCentroProducao.trim(), MatchMode.ANYWHERE));
		}

		if (tipoCentroProducao != null) {
			criteria.add(Restrictions.eq(SigCentroProducao.Fields.IND_TIPO.toString(), tipoCentroProducao));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(SigCentroProducao.Fields.IND_SITUACAO.toString(), situacao));
		}

		return this.executeCriteriaCount(criteria);
	}

	public List<SigCentroProducao> pesquisarCentroProducao(Object paramPesquisa, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCentroProducao.class);

		String srtPesquisa = (String) paramPesquisa;

		if (StringUtils.isNotBlank(srtPesquisa)) {
			if (CoreUtil.isNumeroInteger(paramPesquisa)) {
				criteria.add(Restrictions.eq(SigCentroProducao.Fields.SEQ.toString(), Integer.valueOf(srtPesquisa)));
			} else {
				criteria.add(Restrictions.ilike(SigCentroProducao.Fields.NOME.toString(), srtPesquisa, MatchMode.ANYWHERE));
			}
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(SigCentroProducao.Fields.IND_SITUACAO.toString(), situacao));
		}

		criteria.addOrder(Order.asc(SigCentroProducao.Fields.NOME.toString()));

		return executeCriteria(criteria);
	}

	public SigCentroProducao obterPorNome(String nome) {
		DetachedCriteria criteria = DetachedCriteria.forClass(SigCentroProducao.class);
		criteria.add(Restrictions.ilike(SigCentroProducao.Fields.NOME.toString(), nome.trim(), MatchMode.EXACT));
		return (SigCentroProducao) this.executeCriteriaUniqueResult(criteria);
	}

	public List<FccCentroCustos> pesquisaCentroCustoAtivoHierarquia(FccCentroCustos princiapl) {
		DetachedCriteria criteria = DetachedCriteria.forClass(FccCentroCustos.class);

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.CENTRO_CUSTO.toString(), princiapl));

		criteria.add(Restrictions.eq(FccCentroCustos.Fields.SITUACAO.toString(), DominioSituacao.A));

		return executeCriteria(criteria);
	}

}