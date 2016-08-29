package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.commons.CoreUtil;

/**
 * DAO para a entidade AfaGrupoUsoMedicamento
 * 
 * @author lcmoura
 * 
 */
public class AfaGrupoUsoMedicamentoDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaGrupoUsoMedicamento> {

	private static final long serialVersionUID = 7101986639113754148L;

	/**
	 * Busca na base uma lista de AfaGrupoUsoMedicamento pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param grupoUsoMedicamento
	 * @return
	 */
	public List<AfaGrupoUsoMedicamento> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaGrupoUsoMedicamento grupoUsoMedicamento) {
		DetachedCriteria criteria = this.createCriteria(grupoUsoMedicamento);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	/**
	 * Busca na base o número de elementos da lista de AfaGrupoUsoMedicamento
	 * pelo filtro
	 * 
	 * @param grupoUsoMedicamento
	 * @return
	 */
	public Long pesquisarCount(AfaGrupoUsoMedicamento grupoUsoMedicamento) {
		DetachedCriteria criteria = this.createCriteria(grupoUsoMedicamento);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Cria a criteria do filtro
	 * 
	 * @param grupoUsoMedicamento
	 * @return
	 */
	private DetachedCriteria createCriteria(
			AfaGrupoUsoMedicamento grupoUsoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaGrupoUsoMedicamento.class);
		
		criteria.createAlias(AfaGrupoUsoMedicamento.Fields.SERVIDOR.toString(), "servidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servidor."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoa", JoinType.LEFT_OUTER_JOIN);

		if (grupoUsoMedicamento != null) {
			if (grupoUsoMedicamento.getSeq() != null) {
				criteria.add(Restrictions.eq(AfaGrupoUsoMedicamento.Fields.SEQ
						.toString(), grupoUsoMedicamento.getSeq()));
			}
			if (grupoUsoMedicamento.getDescricao() != null
					&& !grupoUsoMedicamento.getDescricao().trim().isEmpty()) {
				criteria
						.add(Restrictions.ilike(
								AfaGrupoUsoMedicamento.Fields.DESCRICAO
										.toString(), grupoUsoMedicamento
										.getDescricao(), MatchMode.ANYWHERE));
			}
			if (grupoUsoMedicamento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(
						AfaGrupoUsoMedicamento.Fields.SITUACAO.toString(),
						grupoUsoMedicamento.getIndSituacao()));
			}
		}

		return criteria;
	}

	public List<AfaGrupoUsoMedicamento> findAll() {
		return super.executeCriteria(DetachedCriteria
				.forClass(AfaGrupoUsoMedicamento.class));
	}
	
	
	// 1291
	public List<AfaGrupoUsoMedicamento> pesquisarGrupoUsoMedicamentoAtivo(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaGrupoUsoMedicamento.class);
		processarCriteriaPesquisarTodosGrupoUsoMedicamentoII(cri, strPesquisa);
		cri.add(Restrictions.eq(AfaGrupoUsoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		return  executeCriteria(cri, 0, 100, AfaGrupoUsoMedicamento.Fields.DESCRICAO.toString(), true);
	}
	
	// 1291
	public Integer pesquisarGrupoUsoMedicamentoAtivoCount(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaGrupoUsoMedicamento.class);
		processarCriteriaPesquisarTodosGrupoUsoMedicamentoII(cri, strPesquisa);
		cri.add(Restrictions.eq(AfaGrupoUsoMedicamento.Fields.SITUACAO.toString(), DominioSituacao.A));
		return  executeCriteria(cri).size();
	}
	
	// 1291
	private void processarCriteriaPesquisarTodosGrupoUsoMedicamentoII(DetachedCriteria cri,
			Object strPesquisa) {
		
		
		
		Disjunction or = Restrictions.disjunction();
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			if(CoreUtil.isNumeroInteger(strPesquisa)){
				or.add(Restrictions.eq(AfaGrupoUsoMedicamento.Fields.SEQ.toString(), Integer.valueOf((String) strPesquisa)));
			}
			or.add(Restrictions.or(Restrictions.ilike(AfaGrupoUsoMedicamento.Fields.DESCRICAO.toString(), (String) strPesquisa,MatchMode.ANYWHERE)));
		}
		
		cri.add(or);

	}
	
	// 5713
	public List<AfaGrupoUsoMedicamento> pesquisarTodosGrupoUsoMedicamento(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaGrupoUsoMedicamento.class);
		processarCriteriaPesquisarTodosGrupoUsoMedicamento(cri, strPesquisa);
		return this.executeCriteria(cri);
	}

	// 5713
	private void processarCriteriaPesquisarTodosGrupoUsoMedicamento(DetachedCriteria cri,
			Object strPesquisa) {

		if (CoreUtil.isNumeroInteger(strPesquisa)) {
			cri.add(Restrictions.eq(
					AfaGrupoUsoMedicamento.Fields.SEQ.toString(), Integer.valueOf(
							(String) strPesquisa)));
		} else {
			cri.add(Restrictions.ilike(AfaGrupoUsoMedicamento.Fields.DESCRICAO
					.toString(), (String) strPesquisa, MatchMode.ANYWHERE));
		}
		cri.addOrder(Order.asc(AfaGrupoUsoMedicamento.Fields.DESCRICAO.toString()));
	}
	
	// 5713
	public Integer pesquisarTodosGrupoUsoMedicamentoCount(Object strPesquisa) {
		return pesquisarTodosGrupoUsoMedicamento(strPesquisa).size();
	}
}
