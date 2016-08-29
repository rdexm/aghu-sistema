package br.gov.mec.aghu.farmacia.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaGrupoMedicamento;
import br.gov.mec.aghu.model.AfaItemGrupoMedicamento;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AfaGrupoMedicamentoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AfaGrupoMedicamento> {
	
	private static final long serialVersionUID = 8038019088610916396L;
	private static final Integer TAMANHO_MAXIMO_CONSULTA = 20;
	
	public List<AfaGrupoMedicamento> pesquisaGruposMedicamentos(Object filtro) {
		if (filtro != null || StringUtils.isNotBlank(filtro.toString())) {
			if (CoreUtil.isNumeroShort(filtro)) {
				DetachedCriteria criteria = criaPesquisaGruposMedicamentosCriteria();

				criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.SEQ
						.toString(), Short.parseShort(filtro.toString())));

				criteria.addOrder(Order
						.asc(AfaGrupoMedicamento.Fields.DESCRICAO.toString()));

				List<AfaGrupoMedicamento> lista = executeCriteria(criteria, 0,
						TAMANHO_MAXIMO_CONSULTA, null, false);

				if (lista != null && !lista.isEmpty()) {
					return lista;
				}
			}

			DetachedCriteria criteria = criaPesquisaGruposMedicamentosCriteria();
			criteria.add(Restrictions.ilike(
					AfaGrupoMedicamento.Fields.DESCRICAO.toString(), filtro.toString(),
					MatchMode.ANYWHERE));

			criteria.addOrder(Order.asc(AfaGrupoMedicamento.Fields.DESCRICAO
					.toString()));

			return executeCriteria(criteria, 0, TAMANHO_MAXIMO_CONSULTA, null, false);
		} else {
			DetachedCriteria criteria = criaPesquisaGruposMedicamentosCriteria();

			criteria.addOrder(Order.asc(AfaGrupoMedicamento.Fields.DESCRICAO
					.toString()));

			return executeCriteria(criteria, 0, TAMANHO_MAXIMO_CONSULTA, null, false);
		}
	}

	public Long pesquisaGruposMedicamentosCount(String filtro) {
		if (StringUtils.isNotBlank(filtro)) {
			if (CoreUtil.isNumeroShort(filtro)) {
				DetachedCriteria criteria = criaPesquisaGruposMedicamentosCriteria();

				criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.SEQ
						.toString(), Short.parseShort(filtro)));

				Long count = executeCriteriaCount(criteria);

				if (count != null && count > 0) {
					return count;
				}
			}

			DetachedCriteria criteria = criaPesquisaGruposMedicamentosCriteria();
			criteria.add(Restrictions.ilike(
					AfaGrupoMedicamento.Fields.DESCRICAO.toString(), filtro,
					MatchMode.ANYWHERE));

			return executeCriteriaCount(criteria);
		} else {
			DetachedCriteria criteria = criaPesquisaGruposMedicamentosCriteria();

			return executeCriteriaCount(criteria);
		}
	}

	private DetachedCriteria criaPesquisaGruposMedicamentosCriteria() {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaGrupoMedicamento.class);

		criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.SITUACAO
				.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq(
				AfaGrupoMedicamento.Fields.USO_SUMARIO_PRESCRICAO.toString(),
				Boolean.FALSE));

		return criteria;
	}

	public List<AfaGrupoMedicamento> pesquisaAfaGrupoMedicamento(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc, Short filtroSeq, String filtroDescricao,
			Boolean filtroMedicamentoMesmoSal, Boolean filtroUsoFichaAnestesia,
			DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = createCriteriaPesquisaAfaGrupoMedicamento(
				filtroSeq, filtroDescricao, filtroMedicamentoMesmoSal,
				filtroUsoFichaAnestesia, filtroSituacao);
		
		return executeCriteria(criteria, firstResult, maxResults,
				AfaGrupoMedicamento.Fields.DESCRICAO.toString(), true);
	}

	public Long pesquisaAfaGrupoMedicamentoCount(Short filtroSeq,
			String filtroDescricao, Boolean filtroMedicamentoMesmoSal,
			Boolean filtroUsoFichaAnestesia, DominioSituacao filtroSituacao) {
		return executeCriteriaCount(createCriteriaPesquisaAfaGrupoMedicamento(
				filtroSeq, filtroDescricao, filtroMedicamentoMesmoSal,
				filtroUsoFichaAnestesia, filtroSituacao));
	}

	private DetachedCriteria createCriteriaPesquisaAfaGrupoMedicamento(
			Short filtroSeq, String filtroDescricao,
			Boolean filtroMedicamentoMesmoSal, Boolean filtroUsoFichaAnestesia,
			DominioSituacao filtroSituacao) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AfaGrupoMedicamento.class);
		
		if (filtroSeq != null) {
			criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.SEQ
					.toString(), filtroSeq));
		}

		if (StringUtils.isNotBlank(filtroDescricao)) {
			criteria.add(Restrictions.ilike(
					AfaGrupoMedicamento.Fields.DESCRICAO.toString(),
					filtroDescricao, MatchMode.ANYWHERE));
		}

		if (filtroMedicamentoMesmoSal != null) {
			criteria.add(Restrictions.eq(
					AfaGrupoMedicamento.Fields.MEDICAMENTOS_MESMO_SAL
							.toString(), filtroMedicamentoMesmoSal));
		}

		if (filtroUsoFichaAnestesia != null) {
			criteria.add(Restrictions.eq(
					AfaGrupoMedicamento.Fields.USO_FICHA_ANESTESIA.toString(),
					filtroUsoFichaAnestesia));
		}

		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.SITUACAO
					.toString(), filtroSituacao));
		}

		criteria.add(Restrictions.eq(
				AfaGrupoMedicamento.Fields.USO_SUMARIO_PRESCRICAO.toString(),
				Boolean.FALSE));

		return criteria;
	}

	public AfaGrupoMedicamento obterAfaGrupoMedicamentoComItemGrupoMdto(
			Integer matCodigo, Boolean usoFichaAnestesia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoMedicamento.class);

		criteria.createAlias(AfaGrupoMedicamento.Fields.ITENS_GRUPOS_MEDICAMENTO.toString(), "grupoMdto");
		criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.USO_FICHA_ANESTESIA.toString(), usoFichaAnestesia));
		criteria.add(Restrictions.eq("grupoMdto." + AfaItemGrupoMedicamento.Fields.MED_MAT_CODIGO.toString(), matCodigo));
		
		return (AfaGrupoMedicamento) executeCriteriaUniqueResult(criteria);
	}
	
	public AfaGrupoMedicamento obterAfaGrupoMedicamentoComItemGrupoMdto(Short seqAfaGrupoMedicamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AfaGrupoMedicamento.class);
		criteria.createAlias(AfaGrupoMedicamento.Fields.ITENS_GRUPOS_MEDICAMENTO.toString(), "igm", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("igm."+AfaItemGrupoMedicamento.Fields.MEDICAMENTO.toString(), "mdto", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AfaGrupoMedicamento.Fields.SEQ.toString(), seqAfaGrupoMedicamento));
		return (AfaGrupoMedicamento) executeCriteriaUniqueResult(criteria);
	}
}
