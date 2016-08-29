package br.gov.mec.aghu.blococirurgico.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTipoSala;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MbcTipoSalaDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MbcTipoSala> {
	
	private static final long serialVersionUID = -3406083812393801025L;

	public List<MbcTipoSala> pesquisarTipoSalas(Short seq, String descricao, DominioSituacao situacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoSala.class, "tps");
		if(seq != null) {
			criteria.add(Restrictions.eq("tps.".concat(MbcTipoSala.Fields.SEQ.toString()), seq));
		}
		if(descricao != null) {
			criteria.add(Restrictions.ilike("tps.".concat(MbcTipoSala.Fields.DESCRICAO.toString()), descricao, MatchMode.ANYWHERE));
		}
		if(situacao != null) {
			criteria.add(Restrictions.eq("tps.".concat(MbcTipoSala.Fields.SITUACAO.toString()), situacao));
		}
		criteria.addOrder(Order.asc("tps.".concat(MbcTipoSala.Fields.DESCRICAO.toString())));
		return executeCriteria(criteria);
	}
	
	public List<MbcTipoSala> buscarTipoSalaAtivasPorCodigoOuDescricao(Object objPesquisa) {
		List<MbcTipoSala> retorno = null;

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			retorno = executeCriteria(this.criarCriteriaBuscarMbcTipoSalaPorCodigo(Short.valueOf(objPesquisa.toString())));
		}
		if (retorno == null || retorno.isEmpty()) {
			final DetachedCriteria criteria = this.criarCriteriaBuscarMbcTipoSalaPorDescricao(objPesquisa);
			criteria.addOrder(Order.asc(MbcTipoSala.Fields.DESCRICAO.toString()));
			retorno = executeCriteria(criteria, 0, 100, null, true);
		}
		return retorno;
	}
	
	public Long contarTipoSalaAtivasPorCodigoOuDescricao(Object objPesquisa) {
		Long retorno = Long.valueOf(0);

		if (CoreUtil.isNumeroShort(objPesquisa)) {
			retorno = executeCriteriaCount(this.criarCriteriaBuscarMbcTipoSalaPorCodigo(Short.valueOf(objPesquisa.toString())));
		}
		if (retorno == 0) {
			retorno = executeCriteriaCount(this.criarCriteriaBuscarMbcTipoSalaPorDescricao(objPesquisa));
		}
		return retorno;
	}
	
	public Boolean verificarExistenciaSalaAtivaComMesmaDescricao(String descricao) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoSala.class);
		criteria.add(Restrictions.eq(MbcTipoSala.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.like(MbcTipoSala.Fields.DESCRICAO.toString(), descricao));
		List<MbcTipoSala> result = executeCriteria(criteria); 
		return result.size() > 0 ? true : false;
	}
	
	private DetachedCriteria criarCriteriaBuscarMbcTipoSalaPorCodigo(Short objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoSala.class);
		criteria.add(Restrictions.eq(MbcTipoSala.Fields.SEQ.toString(), objPesquisa));
		criteria.add(Restrictions.eq(MbcTipoSala.Fields.SITUACAO.toString(), DominioSituacao.A));
		return criteria;
	}
	
	private DetachedCriteria criarCriteriaBuscarMbcTipoSalaPorDescricao(final Object objPesquisa) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MbcTipoSala.class);
		criteria.add(Restrictions.ilike(MbcTipoSala.Fields.DESCRICAO.toString(), objPesquisa == null ? "" : objPesquisa.toString(), MatchMode.ANYWHERE));
		criteria.add(Restrictions.eq(MbcTipoSala.Fields.SITUACAO.toString(), DominioSituacao.A));

		return criteria;
	}
	
}