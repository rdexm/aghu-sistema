package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.model.RapServidores;

/**
 * DAO para a entidade AfaTipoApresentacaoMedicamento
 * 
 * @author lcmoura
 * 
 */
public class AfaTipoApresentacaoMedicamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTipoApresentacaoMedicamento> {

	

	private static final long serialVersionUID = 2846716491456395477L;

	/**
	 * Busca na base uma lista de AfaTipoApresentacaoMedicamento pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param tipoApresentacaoMedicamento
	 * @return
	 */
	public List<AfaTipoApresentacaoMedicamento> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento) {
		DetachedCriteria criteria = this
				.createCriteria(tipoApresentacaoMedicamento);
		criteria.createAlias(AfaTipoApresentacaoMedicamento.Fields.SERVIDOR.toString(), "servidor"		, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica"	, JoinType.LEFT_OUTER_JOIN);
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	/**
	 * Busca na base o número de elementos da lista de
	 * AfaTipoApresentacaoMedicamento pelo filtro
	 * 
	 * @param TipoApresentacaoMedicamento
	 * @return
	 */
	public Long pesquisarCount(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento) {
		DetachedCriteria criteria = this
				.createCriteria(tipoApresentacaoMedicamento);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Cria a criteria do filtro
	 * 
	 * @param TipoApresentacaoMedicamento
	 * @return
	 */
	private DetachedCriteria createCriteria(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaTipoApresentacaoMedicamento.class);

		if (tipoApresentacaoMedicamento != null) {
			if (tipoApresentacaoMedicamento.getSigla() != null) {
				criteria.add(Restrictions.ilike(
						AfaTipoApresentacaoMedicamento.Fields.SIGLA.toString(),
						tipoApresentacaoMedicamento.getSigla(),
						MatchMode.ANYWHERE));
			}
			if (tipoApresentacaoMedicamento.getDescricao() != null
					&& !tipoApresentacaoMedicamento.getDescricao().trim()
							.isEmpty()) {
				criteria.add(Restrictions.ilike(
						AfaTipoApresentacaoMedicamento.Fields.DESCRICAO
								.toString(), tipoApresentacaoMedicamento
								.getDescricao(), MatchMode.ANYWHERE));
			}
			if (tipoApresentacaoMedicamento.getSituacao() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoApresentacaoMedicamento.Fields.SITUACAO
								.toString(), tipoApresentacaoMedicamento
								.getSituacao()));
			}
		}

		return criteria;
	}

	/**
	 * Pesquisa as tipos de apresentação de medicamentos ativos
	 * 
	 * @param siglaOuDescricao
	 * @return
	 */
	public List<AfaTipoApresentacaoMedicamento> pesquisaTipoApresentacaoMedicamentosAtivos(Object siglaOuDescricao) {
		
		DetachedCriteria criteria = criteriaTipoApresentacaoMedicamentosAtivos(siglaOuDescricao);
		criteria.addOrder(Order.asc(AfaTipoApresentacaoMedicamento.Fields.DESCRICAO.toString()));
		return executeCriteria(criteria, 0, 100, null, true);
	}
	
	/**
	 * Count pesquisa as tipos de apresentação de medicamentos ativos
	 * 
	 * @param siglaOuDescricao
	 * @return
	 */
	public Long pesquisaTipoApresentacaoMedicamentosAtivosCount(Object siglaOuDescricao) {
		
		DetachedCriteria criteria = criteriaTipoApresentacaoMedicamentosAtivos(siglaOuDescricao);

		return executeCriteriaCount(criteria);
	}
	
	public DetachedCriteria criteriaTipoApresentacaoMedicamentosAtivos(Object siglaOuDescricao) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaTipoApresentacaoMedicamento.class);

		if (siglaOuDescricao != null && StringUtils.isNotBlank(siglaOuDescricao.toString())){

			criteria.add(Restrictions.ilike(AfaTipoApresentacaoMedicamento.Fields.SIGLA.toString(), siglaOuDescricao.toString(), MatchMode.ANYWHERE));
			criteria.add(Restrictions.eq(AfaTipoApresentacaoMedicamento.Fields.SITUACAO.toString(),	DominioSituacao.A));
		
			List<AfaTipoApresentacaoMedicamento> listAfaTipoApresentacaoMedicamento = executeCriteria(criteria);

			if (listAfaTipoApresentacaoMedicamento != null && listAfaTipoApresentacaoMedicamento.size() > 0) {
				return criteria;
			} else {

				criteria = DetachedCriteria.forClass(AfaTipoApresentacaoMedicamento.class);
				criteria.add(Restrictions.ilike(AfaTipoApresentacaoMedicamento.Fields.DESCRICAO.toString(), siglaOuDescricao.toString(),MatchMode.ANYWHERE));
				criteria.add(Restrictions.eq(AfaTipoApresentacaoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));

				return criteria;
			}
		}
		return criteria;
	}
}
