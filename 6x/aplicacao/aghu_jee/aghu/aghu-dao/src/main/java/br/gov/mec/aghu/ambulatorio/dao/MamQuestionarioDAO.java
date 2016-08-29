package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.QuestionarioRespostaAnamneseVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamValorValidoQuestao;
import br.gov.mec.aghu.core.persistence.dao.BaseDao;

public class MamQuestionarioDAO extends BaseDao<MamQuestionario> {
	
	private static final String QUT = "QUT";
	private static final String QUT_PONTO = "QUT.";
	private static final String QUS = "QUS";
	private static final String QUS_PONTO = "QUS.";
	private static final String VVQ = "VVQ";
	private static final String VVQ_PONTO = "VVQ.";
	private static final String REA = "REA";
	private static final String REA_PONTO = "REA.";

	/**
	 * #52053
	 * @param questionarioSeq
	 * @return String
	 */
	public String obterIndSituacaoQuestionarioPorSeq(Integer questionarioSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class);
		criteria.add(Restrictions.eq(MamQuestionario.Fields.SEQ.toString(), questionarioSeq));
		criteria.setProjection(Projections.property(MamQuestionario.Fields.IND_SITUACAO.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	public String obterSituacaoQuestionarioPorSeq(Integer questionarioSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class);
		criteria.add(Restrictions.eq(MamQuestionario.Fields.SEQ.toString(), questionarioSeq));
		criteria.setProjection(Projections.property(MamQuestionario.Fields.IND_SITUACAO.toString()));
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #50937
	 * Cursor: cur_nega
	 * @param anaSeq
	 * @param tinSeq
	 * @return
	 */
	public List<QuestionarioRespostaAnamneseVO> obterNegaSimplesPorAnaSeqTinSeq(Long anaSeq, Integer tinSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class, QUT);
		
		criteria.createAlias(QUT_PONTO + MamQuestionario.Fields.MAM_QUESTAOES.toString(), QUS, JoinType.INNER_JOIN);
		criteria.createAlias(QUS_PONTO + MamQuestao.Fields.MAM_VALOR_VALIDO_QUESTAOES.toString(), VVQ, JoinType.INNER_JOIN);
		criteria.createAlias(VVQ_PONTO + MamValorValidoQuestao.Fields.MAM_RESPOSTA_ANAMNESESES.toString(), REA, JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(VVQ_PONTO + MamValorValidoQuestao.Fields.VALOR.toString()), QuestionarioRespostaAnamneseVO.Fields.VALOR_VALIDO_QUESTAO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_ANTES_RESPOSTA.toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_ANTES_RESPOSTA.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_DEPOIS_RESPOSTA .toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_DEPOIS_RESPOSTA.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.DESCRICAO.toString()), QuestionarioRespostaAnamneseVO.Fields.DESCRICAO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()), QuestionarioRespostaAnamneseVO.Fields.ORDEM_VISUALIZACAO.toString())
		);
		
		criteria.add(Restrictions.eq(REA_PONTO + MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		criteria.add(Restrictions.eq(QUT_PONTO + MamQuestionario.Fields.TIN_SEQ.toString(), tinSeq));
		criteria.add(Restrictions.eq(QUT_PONTO + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), DominioSimNao.N));
		
		criteria.add(Restrictions.or(Restrictions.eqProperty(QUS_PONTO + MamQuestao.Fields.TEXTO_FORMATADO.toString(), REA_PONTO + MamRespostaAnamneses.Fields.RESPOSTA.toString()),
									 Restrictions.isNull(REA_PONTO + MamRespostaAnamneses.Fields.RESPOSTA.toString())));
		
		criteria.addOrder(Order.asc(VVQ_PONTO + MamValorValidoQuestao.Fields.VALOR.toString()));		
		criteria.addOrder(Order.asc(QUS_PONTO + MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QuestionarioRespostaAnamneseVO.class));	
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #50937
	 * Cursor: cur_nega_resp
	 * @param anaSeq
	 * @param tinSeq
	 * @return
	 */
	public List<QuestionarioRespostaAnamneseVO> obterNegaComRespostaPorAnaSeqTinSeq(Long anaSeq, Integer tinSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class, QUT);
		
		criteria.createAlias(QUT_PONTO + MamQuestionario.Fields.MAM_QUESTAOES.toString(), QUS, JoinType.INNER_JOIN);
		criteria.createAlias(QUS_PONTO + MamQuestao.Fields.MAM_VALOR_VALIDO_QUESTAOES.toString(), VVQ, JoinType.INNER_JOIN);
		criteria.createAlias(VVQ_PONTO + MamValorValidoQuestao.Fields.MAM_RESPOSTA_ANAMNESESES.toString(), REA, JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(VVQ_PONTO + MamValorValidoQuestao.Fields.VALOR.toString()), QuestionarioRespostaAnamneseVO.Fields.VALOR_VALIDO_QUESTAO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_ANTES_RESPOSTA.toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_ANTES_RESPOSTA.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_DEPOIS_RESPOSTA .toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_DEPOIS_RESPOSTA.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.DESCRICAO.toString()), QuestionarioRespostaAnamneseVO.Fields.DESCRICAO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()), QuestionarioRespostaAnamneseVO.Fields.ORDEM_VISUALIZACAO.toString())
				.add(Projections.property(REA_PONTO + MamRespostaAnamneses.Fields.RESPOSTA.toString()), QuestionarioRespostaAnamneseVO.Fields.RESPOSTA.toString())
		);
		
		criteria.add(Restrictions.eq(REA_PONTO + MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		criteria.add(Restrictions.isNotNull(REA_PONTO + MamRespostaAnamneses.Fields.RESPOSTA.toString()));
		
		criteria.add(Restrictions.or(Restrictions.isNull(QUS_PONTO + MamQuestao.Fields.TEXTO_FORMATADO.toString()),
									 Restrictions.and(Restrictions.isNotNull(QUS_PONTO + MamQuestao.Fields.TEXTO_FORMATADO.toString()), 
											 		  Restrictions.neProperty(QUS_PONTO + MamQuestao.Fields.TEXTO_FORMATADO.toString(), REA_PONTO + MamRespostaAnamneses.Fields.RESPOSTA.toString()))
		));

		criteria.add(Restrictions.eq(QUT_PONTO + MamQuestionario.Fields.TIN_SEQ.toString(), tinSeq));
		criteria.add(Restrictions.eq(QUT_PONTO + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), DominioSimNao.N));
		
		criteria.addOrder(Order.asc(VVQ_PONTO + MamValorValidoQuestao.Fields.VALOR.toString()));		
		criteria.addOrder(Order.asc(QUS_PONTO + MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.addOrder(Order.asc(QUS_PONTO + MamQuestao.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QuestionarioRespostaAnamneseVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #50937
	 * Cursor: cur_resp
	 * @param anaSeq
	 * @param tinSeq
	 * @return
	 */
	public List<QuestionarioRespostaAnamneseVO> obterRespostaPorAnaSeqTinSeq(Long anaSeq, Integer tinSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestionario.class, QUT);
		
		criteria.createAlias(QUT_PONTO + MamQuestionario.Fields.MAM_QUESTAOES.toString(), QUS, JoinType.INNER_JOIN);
		criteria.createAlias(QUS_PONTO + MamQuestao.Fields.MAM_RESPOSTA_ANAMNESESES.toString(), REA, JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.QUT_SEQ.toString()), QuestionarioRespostaAnamneseVO.Fields.QUT_SEQ.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.SEQP.toString()), QuestionarioRespostaAnamneseVO.Fields.SEQP_QUESTAO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.DESCRICAO.toString()), QuestionarioRespostaAnamneseVO.Fields.DESCRICAO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_ANTES_RESPOSTA.toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_ANTES_RESPOSTA.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_DEPOIS_RESPOSTA.toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_DEPOIS_RESPOSTA.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.TEXTO_FORMATADO.toString()), QuestionarioRespostaAnamneseVO.Fields.TEXTO_FORMATADO.toString())
				.add(Projections.property(QUS_PONTO + MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()), QuestionarioRespostaAnamneseVO.Fields.ORDEM_VISUALIZACAO.toString())
				.add(Projections.property(REA_PONTO + MamRespostaAnamneses.Fields.RESPOSTA.toString()), QuestionarioRespostaAnamneseVO.Fields.RESPOSTA.toString())
		);
		
		criteria.add(Restrictions.eq(REA_PONTO + MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		criteria.add(Restrictions.isNull(REA_PONTO + MamRespostaAnamneses.Fields.VVQ_SEQP.toString()));
		criteria.add(Restrictions.isNull(REA_PONTO + MamRespostaAnamneses.Fields.VVQ_QUS_SEQP.toString()));
		criteria.add(Restrictions.isNull(REA_PONTO + MamRespostaAnamneses.Fields.VVQ_QUS_QUT_SEQ.toString()));
		
		criteria.add(Restrictions.eq(QUT_PONTO + MamQuestionario.Fields.TIN_SEQ.toString(), tinSeq));
		criteria.add(Restrictions.eq(QUT_PONTO + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), DominioSimNao.N));
		
		criteria.addOrder(Order.asc(QUS_PONTO + MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()));
		criteria.addOrder(Order.asc(QUS_PONTO + MamQuestao.Fields.DESCRICAO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(QuestionarioRespostaAnamneseVO.class));
		
		return executeCriteria(criteria);
	}
}
