package br.gov.mec.aghu.compras.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoMotivoAlteracaoAf;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * Conjunto de regras de negócio para estória
 * "#5605 - Cadastro de Motivo Alteração AF"
 * 
 * @author rogeriovieira
 * 
 */

public class ScoMotivoAlteracaoAfDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<ScoMotivoAlteracaoAf> {
	
	private static final long serialVersionUID = 9027558003938018174L;

	/**
	 * Este método determina a quantidade de objetos da tabela ScoMotivoAlteracaoAf de acordo com os filtros informados
	 * @param motivoAlteracao
	 * @return
	 */
	public Long pesquisarCount(ScoMotivoAlteracaoAf motivoAlteracao){
		DetachedCriteria criteria = criarCriteriaPesquisa(motivoAlteracao);
		return this.executeCriteriaCount(criteria);
	}
	
	/**
	 * Este método busca os objetos da tabela ScoMotivoAlteracaoAf de acordo com os filtros informados
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param motivoAlteracao		
	 * @return
	 */
	public List<ScoMotivoAlteracaoAf> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc, ScoMotivoAlteracaoAf motivoAlteracao){
		
		DetachedCriteria criteria = this.criarCriteriaPesquisa(motivoAlteracao);
		criteria.addOrder(Order.asc(ScoMotivoAlteracaoAf.Fields.DESCRICAO.toString()));
		return this.executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	/**
	 * Este método retorna, se existir, um registro com a mesma descrição informada 
	 * @param descricao
	 * @return
	 */
	public ScoMotivoAlteracaoAf obterPelaDescricao(String descricao){
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMotivoAlteracaoAf.class);
		//criteria.add(Restrictions.eq( ScoMotivoAlteracaoAf.Fields.DESCRICAO.toString(), descricao));
		criteria.add(Restrictions.ilike( ScoMotivoAlteracaoAf.Fields.DESCRICAO.toString(), descricao, MatchMode.EXACT));
		return (ScoMotivoAlteracaoAf)this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Este método cria uma criteria que realizará a pesquisa
	 * @param motivoAlteracao
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisa(ScoMotivoAlteracaoAf motivoAlteracao){
	
		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMotivoAlteracaoAf.class);
		
		if(motivoAlteracao.getCodigo() != null){
			criteria.add(Restrictions.eq(ScoMotivoAlteracaoAf.Fields.CODIGO.toString(), motivoAlteracao.getCodigo()));
		}
		
		if ((motivoAlteracao.getDescricao() != null) && (StringUtils.isNotBlank(motivoAlteracao.getDescricao()))) {
			criteria.add(Restrictions.ilike( ScoMotivoAlteracaoAf.Fields.DESCRICAO.toString(), motivoAlteracao.getDescricao(), MatchMode.ANYWHERE));
		}
		
		if (motivoAlteracao.getSituacao() != null) {
			criteria.add(Restrictions.eq( ScoMotivoAlteracaoAf.Fields.SITUACAO.toString(), motivoAlteracao.getSituacao()));
		}
		
		return criteria;
	}
	
	public List<ScoMotivoAlteracaoAf> listarScoMotivoAlteracaoAf(Object objPesquisa) {
		String strPesquisa = (String) objPesquisa;

		DetachedCriteria criteria = DetachedCriteria.forClass(ScoMotivoAlteracaoAf.class);
		
		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			criteria.add(Restrictions.eq(
					ScoMotivoAlteracaoAf.Fields.CODIGO.toString(),
					Short.valueOf(strPesquisa)));

		} else if (StringUtils.isNotBlank(strPesquisa)) {
			criteria.add(Restrictions.ilike(
					ScoMotivoAlteracaoAf.Fields.DESCRICAO.toString(),
					strPesquisa, MatchMode.ANYWHERE));
		}

		criteria.add(Restrictions.eq(ScoMotivoAlteracaoAf.Fields.SITUACAO.toString(), DominioSituacao.A));
		return executeCriteria(criteria.addOrder(Order.asc(ScoMotivoAlteracaoAf.Fields.CODIGO.toString())));
	}

}
