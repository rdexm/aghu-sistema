package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.RespostaAnamneseVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;

public class MamRespostaAnamnesesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRespostaAnamneses> {

	private static final long serialVersionUID = -4056845623919658793L;

	public List<MamRespostaAnamneses> listarRespostasAnamnesesPip(Integer pipPacCodigo, Short pipSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PIP_PAC_CODIGO.toString(), pipPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PIP_SEQP.toString(), pipSeqp));

		return executeCriteria(criteria);
	}

	public List<MamRespostaAnamneses> listarRespostasAnamnesesPdp(Integer pdpPacCodigo, Short pdpSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PDP_PAC_CODIGO.toString(), pdpPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PDP_SEQP.toString(), pdpSeqp));

		return executeCriteria(criteria);
	}

	public List<MamRespostaAnamneses> listarRespostasAnamnesesPlp(Integer plpPacCodigo, Short plpSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PLP_PAC_CODIGO.toString(), plpPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PLP_SEQP.toString(), plpSeqp));

		return executeCriteria(criteria);
	}

	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorPepPacCodigo(Integer pepPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PEP_PAC_CODIGO.toString(), pepPacCodigo));

		return executeCriteria(criteria);
	}

	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorPepPacCodigoEPepCriadoEm(Integer pepPacCodigo, Date pepCriadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PEP_PAC_CODIGO.toString(), pepPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.PEP_CRIADO_EM.toString(), pepCriadoEm));

		return executeCriteria(criteria);
	}

	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorAtpPacCodigo(Integer atpPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.ATP_PAC_CODIGO.toString(), atpPacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MamRespostaAnamneses> listarRespostasAnamnesesPorAtpPacCodigoEAtpCriadoEm(Integer atpPacCodigo, Date atpCriadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);
		
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.ATP_PAC_CODIGO.toString(), atpPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.ATP_CRIADO_EM.toString(), atpCriadoEm));

		return executeCriteria(criteria);
	}
	
	public List<MamRespostaAnamneses> pesquisarRespostaAnamnesesPorAnamneseQuestaoModo(Long anaSeq, Integer qusQutSeq, Short qusSeq,
			String modo, DominioSimNao customizacao, Integer tinSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class, "REA");
		
		criteria.createAlias("REA." + MamRespostaAnamneses.Fields.MAM_QUESTAO.toString(), "QUS");
		criteria.createAlias("QUS." + MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		
		criteria.add(Restrictions.eq("REA." + MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		criteria.add(Restrictions.isNull("REA." + MamRespostaAnamneses.Fields.VALOR_VALIDO.toString()));
		
		if(modo != null && modo.equals("I")){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S));
		}
		
		if(customizacao != null){
			criteria.add(Restrictions.eq("QUT." + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), customizacao));
		}
		
		if(tinSeq != null){
			criteria.createAlias("QUT." + MamQuestionario.Fields.MAM_TIPO_ITEM_ANAMNESES.toString(), "TIA");
			criteria.add(Restrictions.eq("TIA." + MamTipoItemAnamneses.Fields.SEQ.toString(), tinSeq));
		}
		
		if(qusQutSeq != null){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.QUT_SEQ.toString(), qusQutSeq));
		}
		
		if(qusSeq != null){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.SEQP.toString(), qusSeq));
		}

		criteria.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.ORDEM_VISUALIZACAO.toString())));
		criteria.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.DESCRICAO.toString())));
		
		return executeCriteria(criteria);
	}
	
	public List<MamRespostaAnamneses> pesquisarRespostaAnamnesePorAnamneseQuestaoModo(Long anaSeq, Integer qusQutSeq, Short qusSeq, String modo, Integer tinSeq){

		DetachedCriteria dc = DetachedCriteria.forClass(MamRespostaAnamneses.class,"REA");
		
		dc.createAlias("REA.".concat(MamRespostaAnamneses.Fields.MAM_QUESTAO.toString()), "QUS");
		dc.createAlias("QUS.".concat(MamQuestao.Fields.MAM_QUESTIONARIOS.toString()), "QUT");
		
		dc.add(Restrictions.eq("REA.".concat(MamRespostaAnamneses.Fields.ANA_SEQ.toString()), anaSeq));
		dc.add(Restrictions.isNull("REA.".concat(MamRespostaAnamneses.Fields.VALOR_VALIDO.toString())));
		
		if (StringUtils.equals(modo, "I")) {
			dc.add(Restrictions.eq("QUS." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S));
		}
				
		if(tinSeq != null){
			dc.createAlias("QUT." + MamQuestionario.Fields.MAM_TIPO_ITEM_ANAMNESES.toString(), "TIN");
			dc.add(Restrictions.eq("TIN." + MamTipoItemAnamneses.Fields.SEQ.toString(), tinSeq));
		}
		
		dc.add(Restrictions.eq("QUT." + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), DominioSimNao.N));
		
		if(qusQutSeq != null){
			dc.add(Restrictions.eq("QUS." + MamQuestao.Fields.QUT_SEQ.toString(), qusQutSeq));
		}
		if(qusSeq != null){
			dc.add(Restrictions.eq("QUS." + MamQuestao.Fields.SEQP.toString(), qusSeq));
		}

		dc.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.ORDEM_VISUALIZACAO.toString())));
		dc.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.DESCRICAO.toString())));
		
		return executeCriteria(dc);
}

	/**
	 * #50745 - P1 - Cursor das respostas de anamneses
	 */
	public List<RespostaAnamneseVO> obterRespostaAnamnesePorAnaSeq(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class, "REA");
		criteria.createAlias("REA." + MamRespostaAnamneses.Fields.MAM_QUESTAO.toString(), "QUS");
		criteria.createAlias("REA." + MamRespostaAnamneses.Fields.MAM_ANAMNESES.toString(), "ANA");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ANA." + MamAnamneses.Fields.PAC_CODIGO.toString()), RespostaAnamneseVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("ANA." + MamAnamneses.Fields.CON_NUMERO.toString()), RespostaAnamneseVO.Fields.CON_NUMERO.toString())
				.add(Projections.property("QUS." + MamQuestao.Fields.FUE_SEQ.toString()), RespostaAnamneseVO.Fields.FUE_SEQ.toString())
				.add(Projections.property("QUS." + MamQuestao.Fields.TEXTO_FORMATADO.toString()), RespostaAnamneseVO.Fields.TEXTO_FORMATADO.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.QUS_QUT_SEQ.toString()), RespostaAnamneseVO.Fields.QUS_QUT_SEQ.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.QUS_SEQP.toString()), RespostaAnamneseVO.Fields.QUS_SEQP.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.SEQP.toString()), RespostaAnamneseVO.Fields.SEQP.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.VVQ_QUS_QUT_SEQ.toString()), RespostaAnamneseVO.Fields.VVQ_QUS_QUT_SEQ.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.VVQ_QUS_SEQP.toString()), RespostaAnamneseVO.Fields.VVQ_QUS_SEQP.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.VVQ_SEQP.toString()), RespostaAnamneseVO.Fields.VVQ_SEQP.toString())
				.add(Projections.property("REA." + MamRespostaAnamneses.Fields.RESPOSTA.toString()), RespostaAnamneseVO.Fields.RESPOSTA.toString())
				);
		
		criteria.add(Restrictions.eq("ANA." + MamAnamneses.Fields.SEQ.toString(), anaSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(RespostaAnamneseVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * #50745 - P2 - Consulta as respostas dadas na anamnese
	 */
	public List<MamRespostaAnamneses> obterListaRespostaAnamnesePorAnaSeq(Long anaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaAnamneses.class);
		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.ANA_SEQ.toString(), anaSeq));
		return executeCriteria(criteria);
	}
}
