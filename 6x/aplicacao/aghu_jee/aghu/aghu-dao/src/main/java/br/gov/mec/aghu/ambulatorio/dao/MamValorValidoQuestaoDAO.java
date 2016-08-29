package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.PropertyExpression;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamCustomQuestao;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;
import br.gov.mec.aghu.model.MamValorValidoQuestao;

public class MamValorValidoQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamValorValidoQuestao>{
	
	private static final long serialVersionUID = -8543589939103940536L;

	public List<MamValorValidoQuestao> pesquisarValorValidoQuestaoPorAnamneseQuestaoNegaSimples(Long anaSeq, Integer qusQutSeq, Short qusSeq, String modo, Integer tinSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamValorValidoQuestao.class,"VVQ");
		
		criteria.createAlias("VVQ." + MamValorValidoQuestao.Fields.MAM_RESPOSTA_ANAMNESESES.toString(), "REA");
		criteria.createAlias("VVQ." + MamValorValidoQuestao.Fields.MAM_QUESTOES.toString(), "QUS");
		criteria.createAlias("QUS." + MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		criteria.createAlias("QUT." + MamQuestionario.Fields.MAM_TIPO_ITEM_ANAMNESES.toString(), "TIA");
		
		criteria.add(Restrictions.eq("REA." + MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		PropertyExpression eqProperty = Restrictions.eqProperty("QUS." + MamQuestao.Fields.TEXTO_FORMATADO.toString(), "REA." + MamRespostaAnamneses.Fields.RESPOSTA.toString());
		criteria.add(Restrictions.or(eqProperty, Restrictions.isNull("REA." + MamRespostaAnamneses.Fields.RESPOSTA.toString())));
		
		if(modo.equals("I")){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S.toString()));
		}
		
		if(tinSeq != null){
			criteria.add(Restrictions.eq("TIA." + MamTipoItemAnamneses.Fields.SEQ.toString(), tinSeq));
		}
		
		criteria.add(Restrictions.eq("QUT." + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), DominioSimNao.S.toString()));
		
		if(qusSeq != null){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.SEQP.toString(), qusSeq));
		}
		
		if(qusQutSeq != null){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.QUT_SEQ.toString(), qusQutSeq));
		}
		
		return this.executeCriteria(criteria);
		
	}
	
	public List<MamValorValidoQuestao> pesquisarValorValidoQuestaoPorAnamneseQuestaoNegaComResposta(Long anaSeq, Integer qusQutSeq, Short qusSeq, String modo, String customizacao, Integer tinSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamValorValidoQuestao.class,"VVQ");
		
		criteria.createAlias("VVQ." + MamValorValidoQuestao.Fields.MAM_RESPOSTA_ANAMNESESES.toString(), "REA");
		criteria.createAlias("VVQ." + MamValorValidoQuestao.Fields.MAM_QUESTOES.toString(), "QUS");
		criteria.createAlias("QUS." + MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		criteria.createAlias("QUT." + MamQuestionario.Fields.MAM_TIPO_ITEM_ANAMNESES.toString(), "TIA");
		criteria.add(Restrictions.eq("REA." + MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		criteria.add(Restrictions.isNotNull("REA." + MamRespostaAnamneses.Fields.RESPOSTA.toString()));
		
		Criterion criterion = Restrictions.isNull("QUS." + MamQuestao.Fields.TEXTO_FORMATADO.toString());
		
		Criterion criterionNot = Restrictions.isNotNull("QUS." + MamQuestao.Fields.TEXTO_FORMATADO.toString());
		PropertyExpression neProperty = Restrictions.neProperty("QUS." + MamQuestao.Fields.TEXTO_FORMATADO.toString(), "REA." + MamRespostaAnamneses.Fields.RESPOSTA.toString());
		
		criteria.add(Restrictions.or(criterion, Restrictions.and(criterionNot, neProperty)));
		
		if(modo != null && modo.equals("I")){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S.toString()));
		}
		
		if(customizacao != null){
			criteria.add(Restrictions.eq("QUT." + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), customizacao));
		}
		
		if(tinSeq != null){
			criteria.add(Restrictions.eq("TIA." + MamTipoItemAnamneses.Fields.SEQ.toString(), tinSeq));
		}
		
		criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.SEQP.toString(), qusSeq));
		criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.QUT_SEQ.toString(), qusQutSeq));
		
		return this.executeCriteria(criteria);
	}
	
	/**
	 * @author daniel.silva
	 * @param evoSeq
	 * @param tieSeq
	 * @param comResposta
	 * @param qutSeq
	 * @param seqp
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisarValorValidoQuestaoPorEvolucaoQuestao(Long evoSeq, Integer tieSeq, Integer qutSeq, Short seqp, Boolean comResposta, String modo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "VVQ");
		
		dc.createAlias("VVQ.".concat(MamValorValidoQuestao.Fields.MAM_QUESTOES.toString()), "QUS");
		dc.createAlias("VVQ.".concat(MamValorValidoQuestao.Fields.MAM_RESPOSTA_EVOLUCOESES.toString()), "REV");
		dc.createAlias("QUS.".concat(MamQuestao.Fields.MAM_QUESTIONARIOS.toString()), "QUT");
		
		dc.add(Restrictions.eq("REV.".concat(MamRespostaEvolucoes.Fields.EVO_SEQ.toString()), evoSeq));
		
		dc.addOrder(Order.asc("VVQ.".concat(MamValorValidoQuestao.Fields.VALOR.toString())));
		dc.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.ORDEM_VISUALIZACAO.toString())));
		
		if (qutSeq != null || seqp != null) {
			dc.createAlias("QUT.".concat(MamQuestao.Fields.MAM_CUSTOM_QUESTAOES.toString()), "CQU");
			dc.add(Restrictions.eq("QUT.".concat(MamQuestionario.Fields.IND_CUSTOMIZACAO.toString()), DominioSimNao.S));
			if (qutSeq != null) {
				dc.add(Restrictions.eq("CQU.".concat(MamCustomQuestao.Fields.QUS_QUT_SEQ.toString()), qutSeq));			
			}
			if (seqp != null) {
				dc.add(Restrictions.eq("CQU.".concat(MamCustomQuestao.Fields.QUS_SEQP.toString()), seqp));			
			}			
		}
		if (StringUtils.equals(modo, "I")) {
			dc.add(Restrictions.eq("QUS.".concat(MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString()), DominioSimNao.S));
		}
			
		if (tieSeq != null) {
			dc.add(Restrictions.eq("QUT.".concat(MamQuestionario.Fields.IND_CUSTOMIZACAO.toString()), DominioSimNao.N));
			dc.createAlias("QUT.".concat(MamQuestionario.Fields.MAM_TIPO_ITEM_EVOLUCAO.toString()), "TIE");
			dc.add(Restrictions.eq("TIE.".concat(MamTipoItemEvolucao.Fields.SEQ.toString()), tieSeq));			
		}
		
		if (comResposta) {
			Criterion restrict1 = Restrictions.isNull("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()));
			Criterion restrict2 = Restrictions.isNotNull("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()));
			Criterion restrict3 = Restrictions.neProperty("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()),
					"REV.".concat(MamRespostaEvolucoes.Fields.RESPOSTA.toString()));
			Criterion restrict4 = Restrictions.and(restrict2, restrict3);
			
			dc.add(Restrictions.isNotNull("REV.".concat(MamRespostaEvolucoes.Fields.RESPOSTA.toString())));
			dc.add(Restrictions.or(restrict1, restrict4));
			
			dc.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.DESCRICAO.toString())));
		} else {
			Criterion restrict1 = Restrictions.eqProperty("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()),
					"REV.".concat(MamRespostaEvolucoes.Fields.RESPOSTA.toString()));
			Criterion restrict2 = Restrictions.isNull("REV.".concat(MamRespostaEvolucoes.Fields.RESPOSTA.toString()));			
			dc.add(Restrictions.or(restrict1, restrict2));
		}
		
		return dc;
	}
		
	/**
	 * @author daniel.silva
	 * @param anaSeq
	 * @param tinSeq
	 * @param comResposta
	 * @param qutSeq
	 * @param seqp
	 * @return
	 */
	private DetachedCriteria criarCriteriaPesquisarValorValidoQuestaoPorAnamneseQuestao(Long anaSeq, Integer tinSeq, Integer qutSeq, Short seqp, Boolean comResposta, String modo) {
		DetachedCriteria dc = DetachedCriteria.forClass(getClazz(), "VVQ");
		
		dc.createAlias("VVQ.".concat(MamValorValidoQuestao.Fields.MAM_QUESTOES.toString()), "QUS");
		dc.createAlias("VVQ.".concat(MamValorValidoQuestao.Fields.MAM_RESPOSTA_ANAMNESESES.toString()), "REA");
		dc.createAlias("QUS.".concat(MamQuestao.Fields.MAM_QUESTIONARIOS.toString()), "QUT");
		
		dc.add(Restrictions.eq("REA.".concat(MamRespostaAnamneses.Fields.ANA_SEQ.toString()), anaSeq));
		
		dc.addOrder(Order.asc("VVQ.".concat(MamValorValidoQuestao.Fields.VALOR.toString())));
		dc.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.ORDEM_VISUALIZACAO.toString())));
		
		if (qutSeq != null || seqp != null) {
			dc.createAlias("QUT.".concat(MamQuestao.Fields.MAM_CUSTOM_QUESTAOES.toString()), "CQU");
			dc.add(Restrictions.eq("QUT.".concat(MamQuestionario.Fields.IND_CUSTOMIZACAO.toString()), DominioSimNao.S));
			if (qutSeq != null) {
				dc.add(Restrictions.eq("CQU.".concat(MamCustomQuestao.Fields.QUS_QUT_SEQ.toString()), qutSeq));			
			}
			if (seqp != null) {
				dc.add(Restrictions.eq("CQU.".concat(MamCustomQuestao.Fields.QUS_SEQP.toString()), seqp));			
			}			
		}
		if (StringUtils.equals(modo, "I")) {
			dc.add(Restrictions.eq("QUS.".concat(MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString()), DominioSimNao.S));
		}
		
		if (tinSeq != null) {
			dc.add(Restrictions.eq("QUT.".concat(MamQuestionario.Fields.IND_CUSTOMIZACAO.toString()), DominioSimNao.N));
			dc.createAlias("QUT.".concat(MamQuestionario.Fields.MAM_TIPO_ITEM_ANAMNESES.toString()), "TIN");
			dc.add(Restrictions.eq("TIN.".concat(MamTipoItemEvolucao.Fields.SEQ.toString()), tinSeq));			
		}
		
		if (comResposta) {
			Criterion restrict1 = Restrictions.isNull("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()));
			Criterion restrict2 = Restrictions.isNotNull("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()));
			Criterion restrict3 = Restrictions.neProperty("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()),
					"REA.".concat(MamRespostaAnamneses.Fields.RESPOSTA.toString()));
			Criterion restrict4 = Restrictions.and(restrict2, restrict3);
			
			dc.add(Restrictions.isNotNull("REA.".concat(MamRespostaAnamneses.Fields.RESPOSTA.toString())));
			dc.add(Restrictions.or(restrict1, restrict4));
			
			dc.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.DESCRICAO.toString())));
		} else {
			Criterion restrict1 = Restrictions.eqProperty("QUS.".concat(MamQuestao.Fields.TEXTO_FORMATADO.toString()),
					"REA.".concat(MamRespostaAnamneses.Fields.RESPOSTA.toString()));
			Criterion restrict2 = Restrictions.isNull("REA.".concat(MamRespostaAnamneses.Fields.RESPOSTA.toString()));			
			dc.add(Restrictions.or(restrict1, restrict2));
		}
		
		return dc;
	}
	
	
	/**
	 * @author daniel.silva
	 * @param evoSeq
	 * @param tieSeq
	 * @param qutSeq
	 * @param seqp
	 * @return
	 */
	public List<MamValorValidoQuestao> pesquisarValorValidoQuestaoPorEvolucaoQuestaoNegaSimples(Long evoSeq, Integer tieSeq, Integer qutSeq, Short seqp, String modo) {
		return executeCriteria(criarCriteriaPesquisarValorValidoQuestaoPorEvolucaoQuestao(evoSeq, tieSeq, qutSeq, seqp, Boolean.FALSE, modo));
	}
	
	/**
	 * @author daniel.silva
	 * @param evoSeq
	 * @param tieSeq
	 * @param qutSeq
	 * @param seqp
	 * @return
	 */
	public List<MamValorValidoQuestao> pesquisarValorValidoQuestaoPorEvolucaoQuestaoNegaComResposta(Long evoSeq, Integer tieSeq, Integer qutSeq, Short seqp, String modo) {
		return executeCriteria(criarCriteriaPesquisarValorValidoQuestaoPorEvolucaoQuestao(evoSeq, tieSeq, qutSeq, seqp, Boolean.TRUE, modo));
	}
	
	
	/**
	 * @author daniel.silva
	 * @param anaSeq
	 * @param tinSeq
	 * @param qutSeq
	 * @param seqp
	 * @return
	 */
	public List<MamValorValidoQuestao> pesquisarValorValidoQuestaoPorAnamneseQuestaoNega(Long anaSeq, Integer tinSeq, Integer qutSeq, Short seqp, String modo) {
		return executeCriteria(criarCriteriaPesquisarValorValidoQuestaoPorAnamneseQuestao(anaSeq, tinSeq, qutSeq, seqp, Boolean.FALSE, modo));
	}
	
	/**
	 * @author daniel.silva
	 * @param anaSeq
	 * @param tinSeq
	 * @param qutSeq
	 * @param seqp
	 * @return
	 */
	public List<MamValorValidoQuestao> pesquisarValorValidoQuestaoPorAnamneseQuestaoNegaComResposta(Long anaSeq, Integer tinSeq, Integer qutSeq, Short seqp, String modo) {
		return executeCriteria(criarCriteriaPesquisarValorValidoQuestaoPorAnamneseQuestao(anaSeq, tinSeq, qutSeq, seqp, Boolean.TRUE, modo));
	}

	/**
	 * #52053
	 * @param qusQutSeq
	 * @param qusSeqP
	 * @param seqP
	 * @return
	 */
	public String obterSituacaoValorValidoQuestaoPorSeq(Integer qusQutSeq, Short qusSeqP, Short seqP){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamValorValidoQuestao.class);
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.QUS_QUT_SEQ.toString(), qusQutSeq));
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.QUS_SEQP.toString(), qusSeqP));
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.SEQP.toString(), seqP));
		criteria.setProjection(Projections.property(MamValorValidoQuestao.Fields.IND_SITUACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamValorValidoQuestao.class));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	//#51886 - CUR_VVT
	public String obterSituacaoPorSeq(Integer qusQutSeq, Short qusSeqP, Short seqP){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamValorValidoQuestao.class);
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.QUS_QUT_SEQ.toString(), qusQutSeq));
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.QUS_SEQP.toString(), qusSeqP));
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.SEQP.toString(), seqP));
		criteria.setProjection(Projections.property(MamValorValidoQuestao.Fields.IND_SITUACAO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(MamValorValidoQuestao.class));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #49992 C1
	 * @param qusQutSeq
	 * @param qusSeqP
	 * @return
	 */
	public MamValorValidoQuestao obterSeqValorSituacaoMamValValidoQuestao(Integer qusQutSeq, Short qusSeqP){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamValorValidoQuestao.class);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(MamValorValidoQuestao.Fields.ID.toString()).as(MamValorValidoQuestao.Fields.ID.toString()))
				.add(Projections.property(MamValorValidoQuestao.Fields.VALOR.toString()).as(MamValorValidoQuestao.Fields.VALOR.toString()))
				.add(Projections.property(MamValorValidoQuestao.Fields.IND_SITUACAO.toString()).as(MamValorValidoQuestao.Fields.IND_SITUACAO.toString())));
		
		criteria.setResultTransformer(Transformers.aliasToBean(MamValorValidoQuestao.class));
		
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.IND_SITUACAO.toString(), "A"));
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.QUS_QUT_SEQ.toString(), qusQutSeq));
		criteria.add(Restrictions.eq(MamValorValidoQuestao.Fields.QUS_SEQP.toString(), qusSeqP));
		
		return (MamValorValidoQuestao) executeCriteriaUniqueResult(criteria);
	}
}
