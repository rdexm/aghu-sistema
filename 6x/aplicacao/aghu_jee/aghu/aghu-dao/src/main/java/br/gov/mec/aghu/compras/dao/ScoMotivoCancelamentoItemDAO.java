package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMotivoCancelamentoItem;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class ScoMotivoCancelamentoItemDAO extends BaseDao<ScoMotivoCancelamentoItem>{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1895572813611870816L;
	
	public List<ScoMotivoCancelamentoItem> pesquisaMotivoCancelamentoItem(Integer firstResult,
			Integer maxResults, String orderProperty, boolean asc,
			String codigo, String descricao,
			DominioSituacao situacao) {
		
		if (StringUtils.isBlank(orderProperty)) {
			orderProperty = ScoMotivoCancelamentoItem.Fields.DESCRICAO.toString();
		}

		DetachedCriteria criteria = createPesquisaMotivoCancelamentoItemCriteria(
				orderProperty, asc, codigo, descricao, situacao);

		return executeCriteria(criteria, firstResult, maxResults,
				orderProperty, asc);
	}

	private DetachedCriteria createPesquisaMotivoCancelamentoItemCriteria(
			String orderProperty, Boolean asc, String codigo, String descricao,
			DominioSituacao situacao) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMotivoCancelamentoItem.class);

		if (StringUtils.isNotBlank(codigo)) {
			criteria.add(Restrictions.ilike(ScoMotivoCancelamentoItem.Fields.CODIGO
					.toString(), codigo, MatchMode.ANYWHERE));
		}

		if (StringUtils.isNotBlank(descricao)) {
			criteria.add(Restrictions.ilike(ScoMotivoCancelamentoItem.Fields.DESCRICAO
					.toString(), descricao, MatchMode.ANYWHERE));
		}

		if (situacao != null) {
			criteria.add(Restrictions.eq(ScoMotivoCancelamentoItem.Fields.IND_ATIVO
					.toString(), situacao));
		}

		if (StringUtils.isNotBlank(orderProperty)) {
			criteria.addOrder((asc || asc == null) ? Order.asc(orderProperty) : Order
					.desc(orderProperty));
		}

		return criteria;
	}

	public Long pesquisaMotivoCancelamentoItemCount(String codigo,

			String descricao, DominioSituacao situacao) {
		return executeCriteriaCount(createPesquisaMotivoCancelamentoItemCriteria(null,
				null, codigo, descricao, situacao));
	}
	
	public List<ScoMotivoCancelamentoItem> getMotivoCancelamentoItemComMesmoCodigo(
			ScoMotivoCancelamentoItem motivo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMotivoCancelamentoItem.class);

		criteria.add(Restrictions.eq(
				ScoMotivoCancelamentoItem.Fields.CODIGO.toString(), motivo
						.getCodigo()));

		return this.executeCriteria(criteria);
	}

	public Long pesquisarCountPorCodigoNome(Object paramPesquisa) {

		DetachedCriteria cri = DetachedCriteria
				.forClass(ScoMotivoCancelamentoItem.class);

		// Pesquisar por motivos de cancelamento que estejam em situacao ativa.
		cri.add(Restrictions.eq(ScoMotivoCancelamentoItem.Fields.IND_ATIVO.toString(),
				DominioSituacao.A));

		if (StringUtils.isNotBlank((String) paramPesquisa)) {
			
			String strPesquisa = (String) paramPesquisa;
			cri.add(Restrictions.or(Restrictions.like(
					ScoMotivoCancelamentoItem.Fields.CODIGO.toString(),
					((String) strPesquisa).toUpperCase(),
					MatchMode.ANYWHERE), Restrictions.like(
					ScoMotivoCancelamentoItem.Fields.DESCRICAO.toString(),
					((String) strPesquisa).toUpperCase(),
					MatchMode.ANYWHERE)));
			}

		return executeCriteriaCount(cri);
	}
	
	public boolean verificarCodigo(ScoMotivoCancelamentoItem motivocancel) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMotivoCancelamentoItem.class);

		if (motivocancel.getCodigo() != null) {
			criteria.add(Restrictions.eq(
					ScoMotivoCancelamentoItem.Fields.CODIGO.toString(),motivocancel.getCodigo()));
		}
		if (this.executeCriteriaCount(criteria)> 0 ){
			return false;
		}
		return true;
	}
	
	public List<ScoMotivoCancelamentoItem> getMotivoCancelamentoItemComMesmaDescricao(
			ScoMotivoCancelamentoItem motivo) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMotivoCancelamentoItem.class);

		criteria.add(Restrictions.ilike(ScoMotivoCancelamentoItem.Fields.DESCRICAO
				.toString(), motivo.getDescricao(), MatchMode.EXACT));

		// Garante que não será comparada a descricao do motivo de cancelamento que está sendo
		// editada
		if (motivo.getCodigo() != null) {
			criteria.add(Restrictions.ne(ScoMotivoCancelamentoItem.Fields.CODIGO
					.toString(), motivo.getCodigo()));
		}

		return this.executeCriteria(criteria);
	}
	
	public boolean verificarDescricao(ScoMotivoCancelamentoItem motcan) {

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMotivoCancelamentoItem.class);

		if(motcan.getCodigo()!=null){
			criteria.add(Restrictions.ne(
					ScoUnidadeMedida.Fields.CODIGO.toString(),motcan.getCodigo().toUpperCase()));
		}
		
		if (motcan.getDescricao() != null) {
			criteria.add(Restrictions.ilike(
					ScoUnidadeMedida.Fields.DESCRICAO.toString(),motcan.getDescricao().toUpperCase(),MatchMode.EXACT));
		}
		if (this.executeCriteriaCount(criteria)> 0 ){
			return false;
		}
		return true;
		
	}
	
	public ScoMotivoCancelamentoItem obterMotivoCancelamentoItemPorCodigo(String codigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(ScoMotivoCancelamentoItem.class);

		// Pesquisar por motivos que estejam em situacao ativa.
		// criteria.add(Restrictions.eq(ScoMotivoCancelamentoItem.Fields.IND_ATIVO.toString(),
		// 		DominioSituacao.A));
		criteria.add(Restrictions.eq(ScoMotivoCancelamentoItem.Fields.CODIGO
				.toString(), codigo));

		return (ScoMotivoCancelamentoItem)executeCriteriaUniqueResult(criteria);
	}
	
	

}