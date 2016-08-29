package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.exames.patologia.vo.ConsultaConfigExamesVO;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelItemConfigExame;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class AelConfigExLaudoUnicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelConfigExLaudoUnico> {

	private static final long serialVersionUID = 1531979667876238483L;

	public List<AelConfigExLaudoUnico> listarConfigExames(
			ConsultaConfigExamesVO consulta) {
		DetachedCriteria criteria = getCriteriaListaConfigExames(consulta);

		return executeCriteria(criteria, consulta.getFirstResult(),
				consulta.getMaxResult(), null, false);
	}

	public Long listarConfigExamesCount(
			ConsultaConfigExamesVO consulta) {
		DetachedCriteria criteria = getCriteriaListaConfigExames(consulta);
		return executeCriteriaCount(criteria);
	}
	
	private DetachedCriteria getCriteriaListaConfigExames(ConsultaConfigExamesVO consulta) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(AelConfigExLaudoUnico.class);

		restrictNonNullEquals(criteria,
				AelConfigExLaudoUnico.Fields.SEQ.toString(), consulta.getSeq());

		restrictNonEmptyIlike(criteria,
				AelConfigExLaudoUnico.Fields.NOME.toString(),
				consulta.getNome());

		restrictNonEmptyIlike(criteria,
				AelConfigExLaudoUnico.Fields.SIGLA.toString(),
				consulta.getSigla());

		restrictNonNullEquals(criteria,
				AelConfigExLaudoUnico.Fields.SITUACAO.toString(),
				consulta.getSituacao());
		
		restrictNonNullEquals(criteria,
				AelConfigExLaudoUnico.Fields.LAUDO_ANTERIOR.toString(),
				consulta.getLaudoAnterior());
		
		return criteria;
	}
	
	private void restrictNonNullEquals(DetachedCriteria criteria, String field, Object val) {
		if (val != null) {
			criteria.add(Restrictions.eq(field, val));
		}
	}
	
	private void restrictNonEmptyIlike(DetachedCriteria criteria, String field,
			String val) {
		if (!StringUtils.isBlank(val)) {
			criteria.add(Restrictions.ilike(field, val, MatchMode.ANYWHERE));
		}
	}

	public List<AelConfigExLaudoUnico> pesquisarAelConfigExLaudoUnico(final String orderProperty, final String filtro) {
		final DetachedCriteria criteria = obterCriteriaAelConfigExLaudoUnico(filtro);
		criteria.addOrder(Order.asc(AelConfigExLaudoUnico.Fields.NOME.toString()));
		
		return executeCriteria(criteria, 0, 1000, orderProperty, true);
	}

	public Long pesquisarAelConfigExLaudoUnicoCount(final String orderProperty, final String filtro) {
		final DetachedCriteria criteria = obterCriteriaAelConfigExLaudoUnico(filtro);
		return executeCriteriaCount(criteria);
	}
	
	public Long pesquisarAelConfigExLaudoUnicoCount(final String filtro) {
		final DetachedCriteria criteria = obterCriteriaAelConfigExLaudoUnico(filtro);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria obterCriteriaAelConfigExLaudoUnico(
			final String filtro) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(AelConfigExLaudoUnico.class);

		if (!StringUtils.isEmpty(filtro)) {
			if (CoreUtil.isNumeroInteger(filtro)) {
				criteria.add(Restrictions.eq(
						AelConfigExLaudoUnico.Fields.SEQ.toString(),
						Integer.valueOf(filtro)));

			} else {
				criteria.add(Restrictions.or(Restrictions.ilike(
						AelConfigExLaudoUnico.Fields.NOME.toString(), filtro,
						MatchMode.ANYWHERE), Restrictions.ilike(
						AelConfigExLaudoUnico.Fields.SIGLA.toString(), filtro,
						MatchMode.EXACT)));
			}
		}

		return criteria;
	}

	/**
	 * Obtem configuração de exames com base num item de solicitação de exames.
	 * 
	 * AelItemSolicitacaoExames.aelUnfExecutaExames = AelItemConfigExame.unidadeExecutaExame
	 * 
	 * @param item Item de Solicitação de Exames
	 * @return Configuração de Exames
	 */
	public AelConfigExLaudoUnico obterConfigExLaudoUnico(
			AelItemSolicitacaoExames item) {
		final String CLU = "clu", ICE = "ice";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(
				AelConfigExLaudoUnico.class, CLU);
		
		criteria.createAlias(CLU + '.'
				+ AelConfigExLaudoUnico.Fields.ITENS_CONFIG_EXAME.toString(),
				ICE);

		criteria.add(Restrictions.eq(ICE + '.'
				+ AelItemConfigExame.Fields.UNIDADE_EX_EXAME.toString(),
				item.getAelUnfExecutaExames()));

		return (AelConfigExLaudoUnico) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Obtem configuração de exames por sigla.
	 * 
	 * @param sigla Sigla
	 * @return Configuração de Exame
	 */
	public AelConfigExLaudoUnico obterPorSigla(String sigla) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigExLaudoUnico.class);
		criteria.add(Restrictions.eq(AelConfigExLaudoUnico.Fields.SIGLA.toString(), sigla));
		return (AelConfigExLaudoUnico) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Consulta tipo de exame de uma amostra
	 * C4 #22049
	 * 
	 * @param solicitacaoNumero numero de solicitacao do exame
	 * @param numeroAmostra numero da amostra consultada
	 */
	public AelConfigExLaudoUnico obterTipoExameAmostra(Integer solicitacaoNumero, Integer numeroAmostra) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigExLaudoUnico.class, "lu2");
		criteria.createAlias("lu2." + AelConfigExLaudoUnico.Fields.ITENS_CONFIG_EXAME.toString(), "ice");
		
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("lu2." + AelConfigExLaudoUnico.Fields.SEQ.toString()), "seq")
			.add(Projections.property("lu2." + AelConfigExLaudoUnico.Fields.SIGLA.toString()), "sigla");
		criteria.setProjection(projection);
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelAmostraItemExames.class, "aie");
		subCriteria.createAlias("aie." + AelAmostraItemExames.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		

		//		subCriteria.add(Restrictions.eqProperty("aie." + AelAmostraItemExames.Fields.ISE_SOE_SEQ.toString(),
		//				"ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString()));
		//		subCriteria.add(Restrictions.eqProperty("aie." + AelAmostraItemExames.Fields.ISE_SEQP.toString(),
		//				"ise." + AelItemSolicitacaoExames.Fields.SEQP.toString()));
		
		subCriteria.add(Restrictions.eqProperty("ice." + AelItemConfigExame.Fields.UFE_EMA_EXA_SILGA.toString(), "ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		subCriteria.add(Restrictions.eqProperty("ice." + AelItemConfigExame.Fields.UFE_EMA_MAN_SEQ.toString(), "ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		
		ProjectionList projectionSubCriteria = Projections.projectionList()
		.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()), "ufe_ema_exa_sigla");
		
		
		subCriteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), solicitacaoNumero));
		subCriteria.add(Restrictions.eq("aie." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), numeroAmostra.intValue()));
		
		subCriteria.setProjection(projectionSubCriteria);
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelConfigExLaudoUnico.class));
		
		Object result = executeCriteriaUniqueResult(criteria);
		
		return result != null ? (AelConfigExLaudoUnico)result : null;
	}
	
	/**
	 * Consulta tipo de exame de um exame
	 * C5 #22049
	 * 
	 * @param solicitacaoNumero numero de solicitacao do exame
	 * @param numeroAmostra numero da amostra consultada
	 */
	public AelConfigExLaudoUnico pesquisarTipoExameSelecionado(Integer solicitacaoNumero, Short numeroAmostra) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelConfigExLaudoUnico.class, "lu2");
		criteria.createAlias("lu2." + AelConfigExLaudoUnico.Fields.ITENS_CONFIG_EXAME.toString(), "ice");
		
		ProjectionList projection = Projections.projectionList()
			.add(Projections.property("lu2." + AelConfigExLaudoUnico.Fields.SEQ.toString()), "seq")
			.add(Projections.property("lu2." + AelConfigExLaudoUnico.Fields.SIGLA.toString()), "sigla");
		criteria.setProjection(projection);
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AelItemSolicitacaoExames.class, "ise");
		
		subCriteria.add(Restrictions.eqProperty("ice." + AelItemConfigExame.Fields.UFE_EMA_EXA_SILGA.toString(), "ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()));
		subCriteria.add(Restrictions.eqProperty("ice." + AelItemConfigExame.Fields.UFE_EMA_MAN_SEQ.toString(), "ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_MAN_SEQ.toString()));
		
		ProjectionList projectionSubCriteria = Projections.projectionList()
		.add(Projections.property("ise." + AelItemSolicitacaoExames.Fields.UFE_EMA_EXA_SIGLA.toString()), "ufe_ema_exa_sigla");
		
		//@TODO: passar parametro
		subCriteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), solicitacaoNumero));
		subCriteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), numeroAmostra));
		
		
		subCriteria.setProjection(projectionSubCriteria);
		criteria.add(Subqueries.exists(subCriteria));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelConfigExLaudoUnico.class));
		
		Object result = executeCriteriaUniqueResult(criteria);
		
		return result != null ? (AelConfigExLaudoUnico)result : null;
	}
	
}