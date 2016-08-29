package br.gov.mec.aghu.exames.dao;

import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelExQuestionarioOrigens;
import br.gov.mec.aghu.model.AelExamesQuestionario;
import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelQuestionariosConvUnid;
import br.gov.mec.aghu.model.AelQuestoesQuestionario;

public class AelExQuestionarioOrigensDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AelExQuestionarioOrigens> {

	private static final long serialVersionUID = -5338838876216486602L;

	/**
	 * Estoria 2234 - AEL_EX_QUESTIONARIO_ORIGENS
	 * 
	 * @param eqeEmaExaSigla
	 * @param eqeEmaManSeq
	 * @param eqeQtnSeq
	 * @return
	 */
	public List<AelExQuestionarioOrigens> pesquisarAelExQuestionarioOrigens(String eqeEmaExaSigla, Integer eqeEmaManSeq, Integer eqeQtnSeq) {
		return executeCriteria(montarCriteria(eqeEmaExaSigla, eqeEmaManSeq, eqeQtnSeq));
	}

	/**
	 * 
	 * @param eqeEmaExaSigla
	 * @param eqeEmaManSeq
	 * @param eqeQtnSeq
	 * @return
	 */
	private DetachedCriteria montarCriteria(String eqeEmaExaSigla, Integer eqeEmaManSeq, Integer eqeQtnSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AelExQuestionarioOrigens.class);
		criteria.createAlias(AelExQuestionarioOrigens.Fields.EXAME_QUESTIONARIO.toString(), "EXQ", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AelExQuestionarioOrigens.Fields.EQE_EMA_EXA_SIGLA.toString(), eqeEmaExaSigla));
		criteria.add(Restrictions.eq(AelExQuestionarioOrigens.Fields.EQE_EMA_MAN_SEQ.toString(), eqeEmaManSeq));
		criteria.add(Restrictions.eq(AelExQuestionarioOrigens.Fields.EQE_QTN_SEQ.toString(), eqeQtnSeq));
		return criteria;
	}
	
	/**
     * #43044 Cursor c_reposta - F1
     * @param exaSigla
     * @param manSeq
     * @param cspCnvCodigo
     * @param origemAtend
     * @return Boolean
     */
     public Boolean obterCursorResposta(String exaSigla, Integer manSeq, Short cspCnvCodigo, DominioOrigemAtendimento origemAtend) {
           
           DetachedCriteria criteria = DetachedCriteria.forClass(AelExQuestionarioOrigens.class, "EQO");
           
           criteria.createAlias("EQO." + AelExQuestionarioOrigens.Fields.EXAME_QUESTIONARIO.toString(), "EQE", JoinType.INNER_JOIN);
           criteria.createAlias("EQE." + AelExamesQuestionario.Fields.QUESTIONARIOS.toString(), "QTN", JoinType.INNER_JOIN);
           criteria.createAlias("QTN." + AelQuestionarios.Fields.QUESTOES_QUESTIONARIOS.toString(), "QQU", JoinType.INNER_JOIN);
           criteria.createAlias("QTN." + AelQuestionarios.Fields.QUESTIONARIOS_CONV_UNIDS.toString(), "QCU", JoinType.INNER_JOIN);
           
           criteria.add(Restrictions.eq("EQE."+AelExamesQuestionario.Fields.EMA_EXA_SIGLA.toString(), exaSigla)); 
           criteria.add(Restrictions.eq("EQE."+AelExamesQuestionario.Fields.EMA_MAN_SEQ.toString(), manSeq));
           criteria.add(Restrictions.eq("EQE."+AelExamesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
           criteria.add(Restrictions.eq("QTN."+AelQuestionarios.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
           criteria.add(Restrictions.eq("QQU."+AelQuestoesQuestionario.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
           
           Criterion restricao1 = Restrictions.isNotNull("QCU."+AelQuestionariosConvUnid.Fields.UNFSEQ.toString());
           Criterion restricao2 = Restrictions.isNotNull("QCU."+AelQuestionariosConvUnid.Fields.CNVCODIGO.toString()); 
           Criterion restricao3 = Restrictions.eq("QCU."+AelQuestionariosConvUnid.Fields.CNVCODIGO.toString(), cspCnvCodigo);
           Conjunction conjuction = Restrictions.conjunction();
           conjuction.add(restricao2);
           conjuction.add(restricao3);
           Disjunction disjunction = Restrictions.disjunction();
           disjunction.add(restricao1);
           disjunction.add(conjuction);
           criteria.add(disjunction);
                         
           Disjunction disjunction2 = Restrictions.disjunction();
           Criterion restricao4 = Restrictions.eq("EQO."+AelExQuestionarioOrigens.Fields.ORIGEM_QUESTIONARIO.toString(), DominioOrigemAtendimento.T);
           Criterion restricao5 = Restrictions.eq("EQO."+AelExQuestionarioOrigens.Fields.ORIGEM_QUESTIONARIO.toString(), origemAtend);
           disjunction2.add(restricao4);
           disjunction2.add(restricao5);
           criteria.add(disjunction2);
           
           if (isOracle()) {
                  criteria.add(Restrictions.sqlRestriction("NVL({alias}.nro_vias,0) > 0"));
           } else {
                  criteria.add(Restrictions.sqlRestriction("COALESCE({alias}.nro_vias,0) > 0"));
           }
           
           return executeCriteriaExists(criteria);
           
     }

}