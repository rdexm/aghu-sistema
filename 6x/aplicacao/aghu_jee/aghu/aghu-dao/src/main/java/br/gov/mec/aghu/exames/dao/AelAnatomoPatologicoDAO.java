package br.gov.mec.aghu.exames.dao;

import java.util.Date;

import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSituacaoExamePatologia;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelAnatomoPatologico;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelConfigExLaudoUnico;
import br.gov.mec.aghu.model.AelExameAp;
import br.gov.mec.aghu.model.AelExameApItemSolic;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;

public class AelAnatomoPatologicoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelAnatomoPatologico> {

	private static final long serialVersionUID = -4146743203415046272L;

	private DetachedCriteria obterDetachedCriteriaAelAnatomoPatologicoByNumeroAp(
			Integer configExame, final Long numeroAp) {
		final DetachedCriteria criteria = DetachedCriteria
				.forClass(AelAnatomoPatologico.class);

		criteria.add(Restrictions.eq(
				AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), configExame));

		criteria.add(Restrictions.eq(
				AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));

		return criteria;
	}

	/**
	 * Traz um registro único de AEL_ANATOMO_PATOLOGICOS a partir de um número de exame (antigo "número de AP")
	 * @param numeroAp
	 * 		número do Exame para pesquisa.
	 * @return
	 * 		objeto contendo o registro de AelAnatomoPatologico filtrado.
	 */
	public AelAnatomoPatologico obterAelAnatomoPatologicoPorNumeroAp (Long numeroAp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class);

		criteria.add(Restrictions.eq(AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		
		return (AelAnatomoPatologico) executeCriteriaUniqueResult(criteria);
	}

	public boolean verificarAelAnatomoPatologicoByNumeroAp(
			AelConfigExLaudoUnico configExameOrigem, Long numeroApOrigem) {
		assert numeroApOrigem != null : "Número do exame de origem não definido";
		
		final DetachedCriteria criteria = this
				.obterDetachedCriteriaAelAnatomoPatologicoByNumeroAp(
						configExameOrigem.getSeq(), numeroApOrigem);
		
		return executeCriteriaCount(criteria) > 0;
	}

	public AelAnatomoPatologico obterAelAnatomoPatologicoPorSeqNumeroAp(
			Long seq, AelConfigExLaudoUnico aelConfigExLaudoUnico, Long numeroAp) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class);

		criteria.add(Restrictions.eq(AelAnatomoPatologico.Fields.SEQ.toString(), seq));
		criteria.add(Restrictions.eq(AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), aelConfigExLaudoUnico));
		criteria.add(Restrictions.eq(AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		
		return (AelAnatomoPatologico) executeCriteriaUniqueResult(criteria);
	}

	public AelAnatomoPatologico obterAelAnatomoPatologicoByNumeroAp(
			Long numeroAp, Integer lu2Seq) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class);
		
		criteria.add(Restrictions.eq(AelAnatomoPatologico.Fields.NUMERO_AP.toString(), numeroAp));
		criteria.add(Restrictions.eq(AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		return (AelAnatomoPatologico) executeCriteriaUniqueResult(criteria);
	}
	
	public AelAnatomoPatologico obterAelAnatomoPatologicoPorItemSolic(
			Integer soeSeq, Short seqp) {
		
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class, "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.AEL_EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), seqp));

		return (AelAnatomoPatologico) executeCriteriaUniqueResult(criteria);
	}
	
	
	//#22049 - C6
	public AelAnatomoPatologico obterNumeroExameSelecionado() {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class, "lum");
		
		ProjectionList projection = Projections.projectionList()
		.add(Projections.property("lum." + AelAnatomoPatologico.Fields.NUMERO_AP.toString()), "numeroAp");
		criteria.setProjection(projection);
		
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.AEL_EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lu1");
		criteria.createAlias("lu1." + AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "lu2");
		
		//@TODO passar parametro
		/*criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SOE_SEQ.toString(), 3282323));
		criteria.add(Restrictions.eq("ise." + AelItemSolicitacaoExames.Fields.SEQP.toString(), new Short("1")));*/
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(AelAnatomoPatologico.class));
		
		Object result = executeCriteriaUniqueResult(criteria);
		
		return result != null ? (AelAnatomoPatologico)result : null;
	}
	
	public AelAnatomoPatologico obterAelAnatomoPatologicoPorItemAmostra(Integer soeSeq, Short seqp) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class, "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.AEL_EXAME_AP.toString(), "lux");
		criteria.createAlias("lux." + AelExameAp.Fields.AEL_EXAME_AP_ITEM_SOLICS.toString(), "lul");
		criteria.createAlias("lul." + AelExameApItemSolic.Fields.ITEM_SOLICITACAO_EXAMES.toString(), "ise");
		criteria.createAlias("ise." + AelItemSolicitacaoExames.Fields.AEL_AMOSTRA_ITEM_EXAMES.toString(), "iae");

		criteria.add(Restrictions.eq("iae." + AelAmostraItemExames.Fields.AMO_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq("iae." + AelAmostraItemExames.Fields.AMO_SEQP.toString(), seqp.intValue()));

		return (AelAnatomoPatologico) executeCriteriaUniqueResult(criteria);
	}	
	
	public Long obterUltimoAelAnatomoPatologicoPorPacCodigo(Integer pacCodigo, Integer lu2Seq, Date numeroDiasUteis) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class, "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.ATD_SEQ.toString(), "atd", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.ATV_SEQ.toString(), "atv", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.AEL_EXAME_AP.toString(), "lux");
		
		Criterion rest1 = Restrictions.eq("atd." + AghAtendimentos.Fields.PAC_CODIGO.toString(), pacCodigo);
		Criterion rest2 = Restrictions.eq("atv." + AelAtendimentoDiversos.Fields.PAC_CODIGO.toString(), pacCodigo);
		
		criteria.add(Restrictions.or(rest1, rest2));
		
		criteria.add(Restrictions.isNull("lux." + AelExameAp.Fields.AEL_ANATOMO_PATOLOGICOS_ORIGEM.toString()));
		
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME_SEQ.toString(), lu2Seq));
		
		criteria.add(Restrictions.ge("lum." + AelAnatomoPatologico.Fields.CRIADO_EM.toString(), numeroDiasUteis));
		
		criteria.add(Restrictions.eq("lux." + AelExameAp.Fields.ETAPAS_LAUDO.toString(), DominioSituacaoExamePatologia.LA));
		
		criteria.setProjection(Projections.projectionList().add(Projections.max("lum." + AelAnatomoPatologico.Fields.SEQ.toString())));		

		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	public AelAnatomoPatologico obterAelAnatomoPatologicoPorId(Long seq) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AelAnatomoPatologico.class, "lum");
		criteria.createAlias("lum." + AelAnatomoPatologico.Fields.CONFIG_EXAME.toString(), "conf", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("lum." + AelAnatomoPatologico.Fields.SEQ.toString(), seq));
		return (AelAnatomoPatologico) executeCriteriaUniqueResult(criteria);
		
	}
}
