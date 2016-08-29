package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.model.RapServidores;

public class AfaTipoUsoMdtoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaTipoUsoMdto> {

	

	private static final long serialVersionUID = -3867235012094589140L;

	public Long listaTipoUsoMedicamentoPorMedicamentoEGupSeqCount(
			Integer codigoMedicamento, Integer gupSeq) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaTipoUsoMdto.class);

		criteria.createAlias(AfaTipoUsoMdto.Fields.AFA_MEDICAMENTO.toString(),
				AfaTipoUsoMdto.Fields.AFA_MEDICAMENTO.toString());

		criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.AFA_MEDICAMENTO
				.toString()
				+ "." + AfaMedicamento.Fields.MAT_CODIGO.toString(),
				codigoMedicamento));

		criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.GUP_SEQ.toString(),
				gupSeq));

		criteria.setProjection(Projections.distinct(Projections
				.property(AfaTipoUsoMdto.Fields.GUP_SEQ.toString())));

		return executeCriteriaCount(criteria);
	}

	/**
	 * Busca na base uma lista de AfaTipoUsoMdto pelo filtro
	 * 
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param tipoApresentacaoMedicamento
	 * @return
	 */
	public List<AfaTipoUsoMdto> pesquisar(Integer firstResult,
			Integer maxResult, String orderProperty, boolean asc,
			AfaTipoUsoMdto elemento) {
		DetachedCriteria criteria = this.createCriteria(elemento);
		return executeCriteria(criteria, firstResult, maxResult, orderProperty,
				asc);
	}

	/**
	 * Busca na base o número de elementos da lista de AfaTipoUsoMdto pelo
	 * filtro
	 * 
	 * @param TipoApresentacaoMedicamento
	 * @return
	 */
	public Long pesquisarCount(AfaTipoUsoMdto elemento) {
		DetachedCriteria criteria = this.createCriteria(elemento);
		return executeCriteriaCount(criteria);
	}

	/**
	 * Cria a criteria do filtro
	 * 
	 * @param AfaTipoUsoMdto
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private DetachedCriteria createCriteria(AfaTipoUsoMdto elemento) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaTipoUsoMdto.class);
		
		criteria.createAlias(AfaTipoUsoMdto.Fields.RAP_SERVIDORES.toString(), "rapServidores", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rapServidores."+RapServidores.Fields.PESSOA_FISICA.toString(), "pessoaFisica", JoinType.LEFT_OUTER_JOIN);
		
		if (elemento != null) {

			// Sigla
			if (elemento.getSigla() != null
					&& !elemento.getSigla().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AfaTipoUsoMdto.Fields.SIGLA
						.toString(), elemento.getSigla(), MatchMode.ANYWHERE));
			}

			// Descrição
			if (elemento.getDescricao() != null
					&& !elemento.getDescricao().trim().isEmpty()) {
				criteria.add(Restrictions.ilike(AfaTipoUsoMdto.Fields.DESCRICAO
						.toString(), elemento.getDescricao(),
						MatchMode.ANYWHERE));
			}

			// Grupo
			if (elemento.getGrupoUsoMedicamento() != null) {
				criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.GRUPO_USO
						.toString(), elemento.getGrupoUsoMedicamento()));
			}

			// IndAntimicrobiano
			if (elemento.getIndAntimicrobiano() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoUsoMdto.Fields.IND_ANTIMICROBIANO.toString(),
						elemento.getIndAntimicrobiano()));
			}

			// IndExigeJustificativa
			if (elemento.getIndExigeJustificativa() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoUsoMdto.Fields.IND_EXIGE_JUSTIFICATIVA
								.toString(), elemento
								.getIndExigeJustificativa()));
			}

			// IndAvaliacao
			if (elemento.getIndAvaliacao() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoUsoMdto.Fields.IND_AVALIACAO.toString(),
						elemento.getIndAvaliacao()));
			}

			// IndExigeDuracaoSolicitada
			if (elemento.getIndExigeDuracaoSolicitada() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoUsoMdto.Fields.IND_EXIGE_DURACAO_SOLICITADA
								.toString(), elemento
								.getIndExigeDuracaoSolicitada()));
			}

			// IndControlado
			if (elemento.getIndControlado() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoUsoMdto.Fields.IND_CONTROLADO.toString(),
						elemento.getIndControlado()));
			}

			// IndQuimioterapico
			if (elemento.getIndQuimioterapico() != null) {
				criteria.add(Restrictions.eq(
						AfaTipoUsoMdto.Fields.IND_QUIMIOTERAPICO.toString(),
						elemento.getIndQuimioterapico()));
			}

			// IndSituacao
			if (elemento.getIndSituacao() != null) {
				criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.IND_SITUACAO
						.toString(), elemento.getIndSituacao()));
			}
		}

		return criteria;
	}

	/**
	 * Pesquisa as tipos de uso de medicamentos ativos
	 * 
	 * @param siglaOuDescricao
	 * @return
	 */
	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivos(
			Object siglaOuDescricao) {

		DetachedCriteria criteria = null;

		if (siglaOuDescricao != null
				&& StringUtils.isNotBlank((String) siglaOuDescricao)) {

			criteria = DetachedCriteria.forClass(AfaTipoUsoMdto.class);

			criteria.add(Restrictions.ilike(AfaTipoUsoMdto.Fields.SIGLA
					.toString(), (String) siglaOuDescricao, MatchMode.EXACT));

			criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.IND_SITUACAO
					.toString(), DominioSituacao.A));

			criteria.addOrder(Order.asc(AfaTipoUsoMdto.Fields.DESCRICAO
					.toString()));

			List<AfaTipoUsoMdto> result = executeCriteria(criteria);

			if (result != null && !result.isEmpty()) {
				return result;

			} else {
				criteria = DetachedCriteria.forClass(AfaTipoUsoMdto.class);

				criteria.add(Restrictions.ilike(AfaTipoUsoMdto.Fields.DESCRICAO
						.toString(), (String) siglaOuDescricao,
						MatchMode.ANYWHERE));

				criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.IND_SITUACAO
						.toString(), DominioSituacao.A));

				criteria.addOrder(Order.asc(AfaTipoUsoMdto.Fields.DESCRICAO
						.toString()));

				return executeCriteria(criteria);
			}
		}

		criteria = DetachedCriteria.forClass(AfaTipoUsoMdto.class);

		criteria.add(Restrictions.eq(AfaTipoUsoMdto.Fields.IND_SITUACAO
				.toString(), DominioSituacao.A));

		criteria
				.addOrder(Order.asc(AfaTipoUsoMdto.Fields.DESCRICAO.toString()));

		return executeCriteria(criteria);
	}
	
	public List<AfaTipoUsoMdto> pesquisaTipoUsoMdtoAtivosII(Object strPesquisa) {
		
		DetachedCriteria cri = DetachedCriteria.forClass(AfaTipoUsoMdto.class);
		Disjunction or = Restrictions.disjunction();
		if (strPesquisa != null && StringUtils.isNotBlank(strPesquisa.toString())) {
			or.add(Restrictions.ilike(AfaTipoUsoMdto.Fields.DESCRICAO.toString(), (String) strPesquisa,MatchMode.ANYWHERE));
			or.add(Restrictions.or(Restrictions.ilike(AfaTipoUsoMdto.Fields.SIGLA.toString(), (String) strPesquisa,MatchMode.ANYWHERE)));
		}
		
		cri.add(or);
		
		cri.add(Restrictions.eq(AfaTipoUsoMdto.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		cri.addOrder(Order.asc(AfaTipoUsoMdto.Fields.DESCRICAO.toString()));
		
		return executeCriteria(cri);
	}
	
	//1291
	public Integer pesquisarTipoUsoMDTOCount(Object strPesquisa) {
		return pesquisaTipoUsoMdtoAtivosII(strPesquisa).size();
	}
	
	// 5713
	public List<AfaTipoUsoMdto> pesquisarTodosTipoUsoMdto(Object strPesquisa) {
		DetachedCriteria cri = DetachedCriteria.forClass(AfaTipoUsoMdto.class);
		processarCriteriaPesquisarTodosTipoUsoMdto(cri, strPesquisa);
		return this.executeCriteria(cri);
	}

	// 5713
	private void processarCriteriaPesquisarTodosTipoUsoMdto(DetachedCriteria cri,
			Object strPesquisa) {

		if (!"".equals(strPesquisa)){
			cri.add(Restrictions.or(
					Restrictions.eq(
							AfaTipoUsoMdto.Fields.SIGLA.toString(), ((String) strPesquisa).toUpperCase())
					, 
					Restrictions.ilike(AfaTipoUsoMdto.Fields.DESCRICAO
							.toString(), (String) strPesquisa, MatchMode.ANYWHERE)));
		} 
		cri.addOrder(Order.asc(AfaTipoUsoMdto.Fields.DESCRICAO.toString()));
	}

	// 5713
	public Integer pesquisarTodosTipoUsoMdtoCount(Object strPesquisa) {
		return pesquisarTodosTipoUsoMdto(strPesquisa).size();
	}
}
