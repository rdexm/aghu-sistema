package br.gov.mec.aghu.ambulatorio.dao;

import java.util.List;

import org.hibernate.SQLQuery;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.ShortType;
import org.hibernate.type.StringType;

import br.gov.mec.aghu.ambulatorio.vo.CurCutVO;
import br.gov.mec.aghu.ambulatorio.vo.RespostaEvolucaoVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamCustomQuestao;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class MamQuestaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamQuestao>{

	private static final long serialVersionUID = -7551207729193054359L;

	private static final String QUS = "QUS";
	private static final String QUS_DOT = "QUS.";
	private static final String QUT = "QUT";
	private static final String QUT_DOT = "QUT.";
	private static final String CQU = "CQU";
	private static final String CQU_DOT = "CQU.";

	public MamQuestao obterQuestaoPorIdModo(Integer qutSeq, Short seqp, String modo){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestao.class,"MAM");
		
		criteria.add(Restrictions.eq("MAM." + MamQuestao.Fields.QUT_SEQ.toString(), qutSeq));
		criteria.add(Restrictions.eq("MAM." + MamQuestao.Fields.SEQP.toString(), seqp));
		
		if(modo.equals("I")){
			criteria.add(Restrictions.eq("MAM." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S));
		}
		
		return (MamQuestao) this.executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * Obtém lista de Questões relacionadas a uma Custom a partir do Questionário.
	 * 
	 * @param qutSeq
	 * @param seqp
	 * @return Lista de Questões
	 */
	public List<MamQuestao> listarQuestoesPorCustomQuestaoQuestionario(Integer qutSeq, Short seqp) {
	
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestao.class, QUS);

		criteria.createAlias(QUS_DOT + MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), QUT);
		criteria.createAlias(QUT_DOT + MamQuestionario.Fields.MAM_CUSTOM_QUESTAOES.toString(), CQU);

		criteria.add(Restrictions.eq(CQU_DOT + MamCustomQuestao.Fields.QUS_QUT_SEQ.toString(), qutSeq));
		criteria.add(Restrictions.eq(CQU_DOT + MamCustomQuestao.Fields.QUS_SEQP.toString(), seqp));

		return executeCriteria(criteria);
	}

	public List<RespostaEvolucaoVO> obterQuestaoPorEvolucaoNativo(final Long vEvoSeq) {

		StringBuffer SQL = new StringBuffer(610);
	
		SQL.append(" SELECT evo.pac_codigo pacCodigo, \n");
		SQL.append("  evo.con_numero	   conNumero, \n");
		SQL.append("  qus.fue_seq          fueSeq, \n");
		SQL.append("  qus.texto_formatado  textoFormatado, \n");
		SQL.append("  rev.qus_qut_seq      qusQutSeq, \n");
		SQL.append("  rev.qus_seqp         qusSeqp, \n");
		SQL.append("  rev.seqp             seqp, \n");
		SQL.append("  rev.vvq_qus_qut_seq  vvqQusQutSeq, \n");
		SQL.append("  rev.vvq_qus_seqp     vvqQusSeqp, \n");
		SQL.append("  rev.vvq_seqp         vvqSeqp, \n");
		SQL.append("  rev.resposta         resposta \n");
		SQL.append(" FROM agh.mam_questoes qus, \n");
		SQL.append("  agh.mam_resposta_evolucoes rev, \n");
		SQL.append("  agh.mam_evolucoes evo \n");
		SQL.append(" WHERE evo.seq   = :vEvoSeq \n");
		SQL.append(" AND rev.evo_seq = evo.seq \n");
		SQL.append(" AND qus.qut_seq = rev.qus_qut_seq \n");
		SQL.append(" AND qus.seqp    = rev.qus_seqp ");
	

		SQLQuery q = createSQLQuery(SQL.toString());
		q.setLong("vEvoSeq", vEvoSeq);

		List<RespostaEvolucaoVO> listaVO = q
				.addScalar("pacCodigo", IntegerType.INSTANCE)
				.addScalar("conNumero", IntegerType.INSTANCE)
				.addScalar("fueSeq", ShortType.INSTANCE)
				.addScalar("textoFormatado", StringType.INSTANCE)
				.addScalar("qusQutSeq", IntegerType.INSTANCE)
				.addScalar("qusSeqp", ShortType.INSTANCE)
				.addScalar("seqp", ShortType.INSTANCE)
				.addScalar("vvqQusQutSeq", IntegerType.INSTANCE)
				.addScalar("vvqQusSeqp", ShortType.INSTANCE)
				.addScalar("vvqSeqp", ShortType.INSTANCE)
				.addScalar("resposta", StringType.INSTANCE)
				.setResultTransformer(Transformers.aliasToBean(RespostaEvolucaoVO.class)).list();
		

		return listaVO;
	}

	/**
	 * #52053
	 */
	public String obterTipoDadoQuestaoPorSeq(Integer qutSeq, Short seqP){
		DetachedCriteria criteria = obterCriteriaPorSeq(qutSeq, seqP);
		
		criteria.setProjection(Projections.property(MamQuestao.Fields.TIPO_DADO.toString()));		
		return (String) executeCriteriaUniqueResult(criteria);
	}

	
	/**
	 * #52053
	 */
	private DetachedCriteria obterCriteriaPorSeq(Integer qutSeq, Short seqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestao.class);
		criteria.add(Restrictions.eq(MamQuestao.Fields.QUT_SEQ.toString(), qutSeq));
		criteria.add(Restrictions.eq(MamQuestao.Fields.SEQP.toString(), seqP));
		return criteria;
	}
	
	/**
	 * #52053
	 */
	public Integer obterFgrSeqQuestaoPorSeq(Integer qutSeq, Short seqP){
		Integer retorno = 0;
		DetachedCriteria criteria = obterCriteriaPorSeq(qutSeq, seqP);
		MamQuestao questao = (MamQuestao) executeCriteriaUniqueResult(criteria);
		if(questao.getMamFuncaoGravacao() != null && questao.getMamFuncaoGravacao().getSeq() != null){
			retorno = (Integer) CoreUtil.nvl(questao.getMamFuncaoGravacao().getSeq(), Integer.valueOf("0"));
		}
		return retorno;
	}
	
	//#51886 - CUR_QUS1
	public String obterTipoDadoPorSeq(Integer qutSeq, Short seqP){
		DetachedCriteria criteria = montarCriteriaObterPorSeq(qutSeq, seqP);
		
		criteria.setProjection(Projections.property(MamQuestao.Fields.TIPO_DADO.toString()));		
		return (String) executeCriteriaUniqueResult(criteria);
	}
	
	//#51886 - CUR_QUS2
	public Integer obterFgrSeqPorSeq(Integer qutSeq, Short seqP){
		Integer retorno = 0;
		DetachedCriteria criteria = montarCriteriaObterPorSeq(qutSeq, seqP);
		MamQuestao questao = (MamQuestao) executeCriteriaUniqueResult(criteria);
		if(questao.getMamFuncaoGravacao() != null && questao.getMamFuncaoGravacao().getSeq() != null){
			retorno = (Integer) CoreUtil.nvl(questao.getMamFuncaoGravacao().getSeq(), Integer.valueOf("0"));
		}
		return retorno;
	}
	//#51886 - monta criteria CUR_QUS
	private DetachedCriteria montarCriteriaObterPorSeq(Integer qutSeq, Short seqP) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestao.class);
		criteria.add(Restrictions.eq(MamQuestao.Fields.QUT_SEQ.toString(), qutSeq));
		criteria.add(Restrictions.eq(MamQuestao.Fields.SEQP.toString(), seqP));
		return criteria;
	}
	
	/**
	 * #49992 - CONSULTA DO CURSOR CUR_CUT
	 * CONSULTA A SER MIGRADA PARA BRANCH AMBULATORIO A SER CRIADO
	 * DEPOIS DE MIGRADO EXCLUIR 
	 */
	public List<CurCutVO> obterListaCursorCutVO(Integer tieSeq, String indTipoPac){
		
		final String QUT = "QUT.";
		final String QUS = "QUS.";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamQuestao.class, "QUS"); 
		criteria.createAlias(QUS+MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(QUS+MamQuestao.Fields.QUT_SEQ.toString()), CurCutVO.Fields.QUT_SEQ.toString())
				.add(Projections.property(QUS+MamQuestao.Fields.SEQP.toString()), CurCutVO.Fields.SEQP.toString())
				.add(Projections.property(QUS+MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()), CurCutVO.Fields.ORDEM_VISUALIZACAO.toString())
				.add(Projections.property(QUS+MamQuestao.Fields.SEXO_QUESTAO.toString()), CurCutVO.Fields.SEXO_QUESTAO.toString())
				.add(Projections.property(QUS+MamQuestao.Fields.TEXTO_FORMATADO.toString()), CurCutVO.Fields.TEXTO_FORMATADO.toString())
				.add(Projections.property(QUS+MamQuestao.Fields.FUE_SEQ.toString()), CurCutVO.Fields.FUE_SEQ.toString())
				.add(Projections.property(QUS+MamQuestao.Fields.DESCRICAO.toString()), CurCutVO.Fields.DESCRICAO.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(CurCutVO.class));
		
		
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.TIE_SEQ, tieSeq));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_SITUACAO, "A"));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_ORIGEM, "A"));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_LIBERADO, "S"));
		criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.IND_CUSTOMIZACAO, DominioSimNao.N));
		criteria.add(Restrictions.or(
				Restrictions.eq(QUT+MamQuestionario.Fields.IND_INSTITUCIONAL, "N"),
				Restrictions.and(Restrictions.eq(QUT+MamQuestionario.Fields.IND_INSTITUCIONAL, "S"), 
								 Restrictions.eq(QUT+MamQuestionario.Fields.IND_TIPO_PAC, "Q")),
				Restrictions.and(Restrictions.eq(QUT+MamQuestionario.Fields.IND_INSTITUCIONAL, "S"), 
								 Restrictions.eq(QUT+MamQuestionario.Fields.IND_TIPO_PAC, indTipoPac))
				));
		
		criteria.add(Restrictions.eq(QUS+MamQuestao.Fields.IND_SITUACAO, "A"));
		criteria.addOrder(Order.asc(QUS+MamQuestao.Fields.ORDEM_VISUALIZACAO));
		
		return executeCriteria(criteria);
	}
}
