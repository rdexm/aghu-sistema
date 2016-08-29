package br.gov.mec.aghu.exames.dao;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Property;
import org.hibernate.criterion.Restrictions;

import br.gov.mec.aghu.exames.questionario.vo.QuestionarioVO;
import br.gov.mec.aghu.exames.questionario.vo.RespostaQuestaoVO;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelRespostaQuestaoHist;

public class AelRespostaQuestaoHistDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRespostaQuestaoHist>{

	private static final long serialVersionUID = -8827994496531445844L;
	
	
	public List<QuestionarioVO> pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(Integer soeSeq,Short seqp){
		List<QuestionarioVO> questionarios = new ArrayList<QuestionarioVO>();
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestaoHist.class);
		criteria.createAlias(AelRespostaQuestaoHist.Fields.QUESTIONARIO.toString(), AelRespostaQuestaoHist.Fields.QUESTIONARIO.toString());
		criteria.add(Restrictions.eq(AelRespostaQuestaoHist.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelRespostaQuestaoHist.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.distinct(Projections.property(AelRespostaQuestaoHist.Fields.QUESTIONARIO.toString()+"."+AelQuestionarios.Fields.SEQ.toString())));
			projectionList.add(Property.forName(AelRespostaQuestaoHist.Fields.QUESTIONARIO.toString()+"."+AelQuestionarios.Fields.DESCRICAO.toString()));
		criteria.setProjection(projectionList);
		List<Object[]> resultadoPesquisa = executeCriteria(criteria);
		for (Object[] record : resultadoPesquisa) {
			QuestionarioVO questionario = new QuestionarioVO();
			questionario.setSeq((Integer)record[0]); 
			questionario.setDescricao((String)record[1]);
			questionarios.add(questionario);
		}
		return questionarios;
	}
	
	public List<RespostaQuestaoVO> pesquisarRespostasPorQuestionarioEItemSolicitacaoExame(Integer qtnSeq, Integer soeSeq,Short seqp){
		List<RespostaQuestaoVO> respostasQuestao = new ArrayList<RespostaQuestaoVO>();
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestaoHist.class);
		criteria.createAlias(AelRespostaQuestaoHist.Fields.QUESTAO_QUESTIONARIO.toString(), "questaoQuestionario");
		criteria.createAlias("questaoQuestionario"+"."+AelQuestoesQuestionario.Fields.QUESTAO.toString(), "questao");
		criteria.createAlias("questaoQuestionario"+"."+AelQuestoesQuestionario.Fields.GRUPO_QUESTAO.toString(), "grupoQuestao", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(AelRespostaQuestaoHist.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelRespostaQuestaoHist.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelRespostaQuestaoHist.Fields.QQU_QTN_SEQ.toString(), qtnSeq));
		criteria.addOrder(Order.asc("questao"+"."+AelQuestao.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("questaoQuestionario"+"."+AelQuestoesQuestionario.Fields.ORDEM.toString()));
		
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("questao"+"."+AelQuestao.Fields.DESCRICAO.toString()))
		.add(Projections.property(AelRespostaQuestaoHist.Fields.RESPOSTA.toString()))
		.add(Projections.property(AelRespostaQuestaoHist.Fields.QQU_QAO_SEQ.toString()))
		.add(Projections.property(AelRespostaQuestaoHist.Fields.QQU_QTN_SEQ.toString()))
		.add(Projections.property("grupoQuestao"+"."+AelGrupoQuestao.Fields.DESCRICAO.toString()));
		criteria.setProjection(projectionList);

		List<Object[]> resultadoPesquisa = executeCriteria(criteria);
		for (Object[] record : resultadoPesquisa) {
			RespostaQuestaoVO respostaQuestaoVO = new RespostaQuestaoVO();
			respostaQuestaoVO.setQuestao((String)record[0]);
			respostaQuestaoVO.setResposta((String)record[1]);
			respostaQuestaoVO.setQaoSeq((Integer)record[2]);
			respostaQuestaoVO.setQtnSeq((Integer)record[3]);
			respostaQuestaoVO.setGrupo((String)record[4]);
			respostasQuestao.add(respostaQuestaoVO);
		}
		return respostasQuestao;
	}

}
