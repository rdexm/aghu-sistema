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
import br.gov.mec.aghu.model.AelExamesQuestionarioId;
import br.gov.mec.aghu.model.AelGrupoQuestao;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelQuestao;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;
import br.gov.mec.aghu.model.AelQuestoesQuestionarioId;
import br.gov.mec.aghu.model.AelRespostaQuestao;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;

public class AelRespostaQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelRespostaQuestao>{
	
	
	private static final long serialVersionUID = -4412803475081190025L;

	public boolean verificarRespostaQuestao(Integer iseSoeSeq, Short iseSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class);
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SOE_SEQ.toString(), iseSoeSeq));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SEQP.toString(), iseSeqp));
		
		return (executeCriteriaCount(criteria) > 0);
	}

	public Long contarRespostaQuestaoPorExameQuestionario(AelExamesQuestionarioId id) {
		return executeCriteriaCount(this.criarCriteriaRespostaQuestaoPorExameQuestionario(id));
	}

	private DetachedCriteria criarCriteriaRespostaQuestaoPorExameQuestionario(AelExamesQuestionarioId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class);
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.EQE_EMA_EXA_SIGLA.toString(), id.getEmaExaSigla()));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.EQE_EMA_MAN_SEQ.toString(), id.getEmaManSeq()));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.EQE_QTN_SEQ.toString(), id.getQtnSeq()));
		return criteria;
	}
	
	
	
	public List<QuestionarioVO> pesquisarQuestionarioPorRespostaQuestaoEItemSolicitacaoExame(Integer soeSeq,Short seqp){
		List<QuestionarioVO> questionarios = new ArrayList<QuestionarioVO>();
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class);
		criteria.createAlias(AelRespostaQuestao.Fields.QUESTIONARIO.toString(), AelRespostaQuestao.Fields.QUESTIONARIO.toString());
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.distinct(Projections.property(AelRespostaQuestao.Fields.QUESTIONARIO.toString()+"."+AelQuestionarios.Fields.SEQ.toString())));
			projectionList.add(Property.forName(AelRespostaQuestao.Fields.QUESTIONARIO.toString()+"."+AelQuestionarios.Fields.DESCRICAO.toString()));
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
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class);
		criteria.createAlias(AelRespostaQuestao.Fields.QUESTAO_QUESTIONARIO.toString(), "questaoQuestionario");
		criteria.createAlias("questaoQuestionario"+"."+AelQuestoesQuestionario.Fields.QUESTAO.toString(), "questao");
		criteria.createAlias("questaoQuestionario"+"."+AelQuestoesQuestionario.Fields.GRUPO_QUESTAO.toString(), "grupoQuestao", Criteria.LEFT_JOIN);
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SEQP.toString(), seqp));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SOE_SEQ.toString(), soeSeq));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.QQU_QTN_SEQ.toString(), qtnSeq));
		criteria.addOrder(Order.asc("questao"+"."+AelQuestao.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("questaoQuestionario"+"."+AelQuestoesQuestionario.Fields.ORDEM.toString()));
		
		ProjectionList projectionList = Projections.projectionList()
		.add(Projections.property("questao"+"."+AelQuestao.Fields.DESCRICAO.toString()))
		.add(Projections.property(AelRespostaQuestao.Fields.RESPOSTA.toString()))
		.add(Projections.property(AelRespostaQuestao.Fields.QQU_QAO_SEQ.toString()))
		.add(Projections.property(AelRespostaQuestao.Fields.QQU_QTN_SEQ.toString()))
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

	public Long contarRespostaQuestaoPorQuestaoQuestionario(final AelQuestoesQuestionarioId id) {
		return executeCriteriaCount(this.criarCriteriaRespostaQuestaoPorQuestaoQuestionario(id));
	}

	private DetachedCriteria criarCriteriaRespostaQuestaoPorQuestaoQuestionario(final AelQuestoesQuestionarioId id) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class);
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.QQU_QAO_SEQ.toString(), id.getQaoSeq()));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.QQU_QTN_SEQ.toString(), id.getQtnSeq()));
		return criteria;
	}

	public List<AelRespostaQuestao> pesquisarRespostaPorConsultaPendente(
			Integer conNumero, String vSituacaoPendente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class, "req");
		criteria.createAlias("req."+AelRespostaQuestao.Fields.ITEM_SOLICITACAO_EXAME.toString(), "ise");
		criteria.createAlias("ise."+AelItemSolicitacaoExames.Fields.SOLICITACAO_EXAME.toString(), "soe");
		criteria.createAlias("soe."+AelSolicitacaoExames.Fields.ATENDIMENTO.toString(), "atd");
		
		criteria.add(Restrictions.eq("atd."+AghAtendimentos.Fields.NUMERO_CONSULTA.toString(), conNumero));

		criteria.add(Restrictions.ne("ise."
				+ AelItemSolicitacaoExames.Fields.SIT_CODIGO.toString(),
				vSituacaoPendente));
		
		return executeCriteria(criteria);
	}
	
	public Boolean obterRespostaInformada(Integer cSoeSeq, Short seqp){
		DetachedCriteria criteria = DetachedCriteria.forClass(AelRespostaQuestao.class);
		
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SOE_SEQ.toString(), cSoeSeq));
		criteria.add(Restrictions.eq(AelRespostaQuestao.Fields.ISE_SEQP.toString(), seqp));
		
		return executeCriteriaExists(criteria);		
	}

}
