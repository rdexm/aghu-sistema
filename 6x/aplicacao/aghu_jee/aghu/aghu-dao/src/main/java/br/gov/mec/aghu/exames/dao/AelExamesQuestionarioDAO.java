package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;

public class AelExamesQuestionarioDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExamesQuestionario>{

	
	private static final long serialVersionUID = -3885790898696458634L;

	public Boolean verificarExistenciaRespostasExamesQuestionario(String cExaSigla, Integer cManSeq, Short cCspCnvCodigo, DominioOrigemAtendimento cOrigemAtend) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesQuestionario.class);
		
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString(), cExaSigla));
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString(), cManSeq));
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.createAlias(AelExamesQuestionario.Fields.QUESTIONARIOS.toString(), "qtn");
		criteria.add(Restrictions.eq("qtn." +AelQuestionarios.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.createAlias("qtn." + AelQuestionarios.Fields.QUESTOES_QUESTIONARIOS.toString(), "qqu");
		criteria.add(Restrictions.eq("qqu." + AelQuestoesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		criteria.createAlias("qtn." + AelQuestionarios.Fields.QUESTIONARIOS_CONV_UNIDS.toString(), "qcu");
		criteria.add(Restrictions.or(Restrictions.isNotNull("qcu." + AelQuestionariosConvUnid.Fields.UNIDADE_FUNCIONAL.toString()), Restrictions.and(Restrictions.isNotNull("qcu." + AelQuestionariosConvUnid.Fields.CODIGO_CONVENIO_SAUDE.toString()), Restrictions.eq("qcu." + AelQuestionariosConvUnid.Fields.CODIGO_CONVENIO_SAUDE.toString(), cCspCnvCodigo))));
		
		criteria.createAlias(AelExamesQuestionario.Fields.QUESTIONARIO_ORIGENS.toString(), "eqo");
		criteria.add(Restrictions.eqProperty("eqo." + AelExQuestionarioOrigens.Fields.EQE_EMA_EXA_SIGLA.toString(), AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString()));
		criteria.add(Restrictions.eqProperty("eqo." + AelExQuestionarioOrigens.Fields.EQE_EMA_MAN_SEQ.toString(), AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString()));
		criteria.add(Restrictions.eqProperty("eqo." + AelExQuestionarioOrigens.Fields.EQE_QTN_SEQ.toString() , AelExamesQuestionario.Fields.QTN_SEQ.toString()));
		criteria.add(Restrictions.or(Restrictions.eq("eqo." + AelExQuestionarioOrigens.Fields.ORIGEM_QUESTIONARIO.toString(), DominioOrigemAtendimento.T), Restrictions.eq("eqo." + AelExQuestionarioOrigens.Fields.ORIGEM_QUESTIONARIO.toString(), cOrigemAtend)));	

		return (executeCriteriaCount(criteria) > 0);
	}

	public List<AelExamesQuestionario> buscarAelExamesQuestionario(final String emaExaSigla, 
																   final Integer emaManSeq,
																   final Integer firstResult, 
																   final Integer maxResult,
																   final String orderProperty, 
																   final boolean asc) {
		return executeCriteria(criarCriteriaBuscarAelExamesQuestionario(emaExaSigla, emaManSeq), firstResult, maxResult, orderProperty, asc);
	}

	private DetachedCriteria criarCriteriaBuscarAelExamesQuestionario(final String emaExaSigla, 
																	  final Integer emaManSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesQuestionario.class);
		
		criteria.createAlias(AelExamesQuestionario.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.EXAME.toString()), "EXA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EMA.".concat(AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString()), "MAT", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AelExamesQuestionario.Fields.QUESTIONARIOS.toString(), "QTN", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AelExamesQuestionario.Fields.QUESTIONARIO_ORIGENS.toString(), "QTO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		return criteria;
	}

	public AelExamesQuestionario obterAelExamesQuestionario( final String emaExaSigla,  final Integer emaManSeq, final Integer qtnSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesQuestionario.class);
		criteria.createAlias(AelExamesQuestionario.Fields.QUESTIONARIOS.toString(), "questionarios");
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString(), emaExaSigla));
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString(), emaManSeq));
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.QTN_SEQ.toString(), qtnSeq));

		return (AelExamesQuestionario) executeCriteriaUniqueResult(criteria);

	}
	
	private DetachedCriteria criarCriteriaBuscarAelExamesQuestionarioPorQuestionario(final Integer qtnSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExamesQuestionario.class);
		criteria.createAlias(AelExamesQuestionario.Fields.EXAME_MATERIAL_ANALISE.toString(), "EMA", JoinType.INNER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.MATERIAL_ANALISE.toString(), "EMA_MA", JoinType.INNER_JOIN);
		criteria.createAlias("EMA."+AelExamesMaterialAnalise.Fields.EXAME.toString(), "EMA_EXA", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq(AelExamesQuestionario.Fields.QTN_SEQ.toString(), qtnSeq));
		return criteria;
	}

	public List<AelExamesQuestionario> buscarAelExamesQuestionarioPorQuestionario(final Integer qtnSeq) {
		return executeCriteria(criarCriteriaBuscarAelExamesQuestionarioPorQuestionario(qtnSeq));
	}
	
	public Long buscarAelExamesQuestionarioCount(final String emaExaSigla, final Integer emaManSeq) {
		return executeCriteriaCount(criarCriteriaBuscarAelExamesQuestionario(emaExaSigla, emaManSeq));
	}
}
