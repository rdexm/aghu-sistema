package br.gov.mec.aghu.ambulatorio.dao;

import java.util.Date;
import java.util.List;

import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.ambulatorio.vo.PreGeraItemQuestVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MamQuestao;
import br.gov.mec.aghu.model.MamQuestionario;
import br.gov.mec.aghu.model.MamRespostaAnamneses;
import br.gov.mec.aghu.model.MamRespostaEvolucoes;
import br.gov.mec.aghu.model.MamTipoItemAnamneses;
import br.gov.mec.aghu.model.MamTipoItemEvolucao;

public class MamRespostaEvolucoesDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MamRespostaEvolucoes> {

	private static final long serialVersionUID = 8407672687020979084L;

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPip(Integer pipPacCodigo, Short pipSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);

		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PIP_PAC_CODIGO.toString(), pipPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PIP_SEQP.toString(), pipSeqp));

		return executeCriteria(criteria);
	}

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPdp(Integer pdpPacCodigo, Short pdpSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);

		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PDP_PAC_CODIGO.toString(), pdpPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PDP_SEQP.toString(), pdpSeqp));

		return executeCriteria(criteria);
	}

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPlp(Integer plpPacCodigo, Short plpSeqp) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);

		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PLP_PAC_CODIGO.toString(), plpPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PLP_SEQP.toString(), plpSeqp));

		return executeCriteria(criteria);
	}

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorAtpPacCodigo(Integer atpPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);

		criteria.add(Restrictions.eq(MamRespostaAnamneses.Fields.ATP_PAC_CODIGO.toString(), atpPacCodigo));

		return executeCriteria(criteria);
	}

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorAtpPacCodigoEAtpCriadoEm(Integer atpPacCodigo, Date atpCriadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);

		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.ATP_PAC_CODIGO.toString(), atpPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.ATP_CRIADO_EM.toString(), atpCriadoEm));

		return executeCriteria(criteria);
	}

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorPepPacCodigo(Integer pepPacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);
		
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PEP_PAC_CODIGO.toString(), pepPacCodigo));

		return executeCriteria(criteria);
	}

	public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorPepPacCodigoEPepCriadoEm(Integer pepPacCodigo, Date pepCriadoEm) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);
		
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PEP_PAC_CODIGO.toString(), pepPacCodigo));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.PEP_CRIADO_EM.toString(), pepCriadoEm));

		return executeCriteria(criteria);
	}
	
	public List<MamRespostaEvolucoes> pesquisarRespostaEvolucoesPorEvolucaoQuestaoModo(Long evoSeq, Integer qusQutSeq, Short qusSeq, String modo, Integer tieSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class,"REE");
		
		criteria.createAlias("REE." + MamRespostaAnamneses.Fields.MAM_QUESTAO.toString(), "QUS");
		criteria.createAlias("QUS." + MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		
		criteria.add(Restrictions.eq("REE." + MamRespostaEvolucoes.Fields.EVO_SEQ.toString(),evoSeq));
		criteria.add(Restrictions.isNull("REE." + MamRespostaEvolucoes.Fields.VALOR_VALIDO.toString()));
		
		if(modo.equals("I")){
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S));
		}
		
		if(tieSeq != null){
			criteria.createAlias("QUT." + MamQuestionario.Fields.MAM_TIPO_ITEM_EVOLUCAO.toString(), "TIE");
			criteria.add(Restrictions.eq("TIE." + MamTipoItemEvolucao.Fields.SEQ.toString(), tieSeq));
		}
		
		criteria.add(Restrictions.eq("QUT." + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), DominioSimNao.N));
		
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
	
	public List<MamRespostaEvolucoes> pesquisarRespostaEvolucoesPorEvolucaoQuestaoModo(Long evoSeq, Integer qusQutSeq, Short qusSeqp,
			String modo, DominioSimNao customizacao, Integer tieSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class, "REE");
		criteria.createAlias("REE." + MamRespostaAnamneses.Fields.MAM_QUESTAO.toString(), "QUS");
		criteria.createAlias("QUS." + MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
		criteria.add(Restrictions.eq("REE." + MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		criteria.add(Restrictions.isNull("REE." + MamRespostaEvolucoes.Fields.VALOR_VALIDO.toString()));
		if (modo != null && modo.equals("I")) {
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.IND_MOSTRA_IMPRESSAO.toString(), DominioSimNao.S));
		}
		if (customizacao != null) {
			criteria.add(Restrictions.eq("QUT." + MamQuestionario.Fields.IND_CUSTOMIZACAO.toString(), customizacao));
		} 
		if (tieSeq != null) {
			criteria.createAlias("QUT." + MamQuestionario.Fields.MAM_TIPO_ITEM_EVOLUCAO.toString(), "TIE");
			criteria.add(Restrictions.eq("TIE." + MamTipoItemAnamneses.Fields.SEQ.toString(), tieSeq));
		}
		if (qusQutSeq != null) {
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.QUT_SEQ.toString(), qusQutSeq));
		}
		if (qusSeqp != null) {
			criteria.add(Restrictions.eq("QUS." + MamQuestao.Fields.SEQP.toString(), qusSeqp));
		}

		criteria.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.ORDEM_VISUALIZACAO.toString())));
		criteria.addOrder(Order.asc("QUS.".concat(MamQuestao.Fields.DESCRICAO.toString())));

		return executeCriteria(criteria);
	}
		
	public List<MamRespostaEvolucoes> pesquisarRespostaEvolucoesPorEvolucao(Long evoSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		return executeCriteria(criteria);
	}

	/**
	 * Verifica existência de registro de MamRespostaEvolucoes com o campo Resposta ou os campos de Valor Válido preenchidos.
	 * 
	 * @param evoSeq
	 * @param qusQutSeq
	 * @param qusSeqp
	 * 
	 * @return Flag indicando existência de registro para os filtros informados
	 */
	public boolean verificarExistenciaRespostaEvolucaoComRespostaOuValorValido(Long evoSeq, Integer qusQutSeq, Short qusSeqp) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);

		criteria.setProjection(Projections.property(MamRespostaEvolucoes.Fields.EVO_SEQ.toString()));

		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.QUS_QUT_SEQ.toString(), qusQutSeq));
		criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.QUS_SEQP.toString(), qusSeqp));

		Disjunction or = Restrictions.disjunction();

		or.add(Restrictions.isNotNull(MamRespostaEvolucoes.Fields.RESPOSTA.toString()));

		Conjunction and = Restrictions.and();

		and.add(Restrictions.isNotNull(MamRespostaEvolucoes.Fields.VVQ_QUS_QUT_SEQ.toString()));
		and.add(Restrictions.isNotNull(MamRespostaEvolucoes.Fields.VVQ_QUS_SEQP.toString()));
		and.add(Restrictions.isNotNull(MamRespostaEvolucoes.Fields.VVQ_SEQP.toString()));

		or.add(and);

		criteria.add(or);

		return executeCriteriaExists(criteria);
	}

		public List<MamRespostaEvolucoes> listarRespostasEvolucoesPorEvolucao(Long evoSeq) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);
			criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
			return executeCriteria(criteria);
		}		
		
		
		/**
		 * #49992 Consulta utilizada em P1 para obter CURSOR CUR_REA
		 * DEVE SER MIGRADA POSTERIORMENTE PARA BRANCH AMBULATORIO QUANDO CRIADO E POSTERIORMENTE EXCLUIDO DESTE BRANCH	
		 * @param evoSeq
		 * @param qusQutSeq
		 * @param qusSeqp
		 * @return
		 */
		public Boolean obterBooleanCursorCurRea(Long evoSeq, Integer qusQutSeq, Short qusSeqp) {
				
			DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);
				
				criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
				criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.QUS_QUT_SEQ.toString(), qusQutSeq+0));
				criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.QUS_SEQP.toString(), Short.valueOf(String.valueOf(qusSeqp+0))));

				return executeCriteriaExists(criteria);
		}
		
		/**
		 * #49992 Consulta utilizada em P1 para obter CURSOR cur_max_rea
		 * @param evoSeq
		 * @param qusQutSeq
		 * @param qusSeqp
		 * @return
		 */
		public Short obterMaxShortPMaisUm(Long evoSeq, Integer qusQutSeq, Short qusSeqp){
			
			DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class);
			
			criteria.setProjection(Projections.max(MamRespostaEvolucoes.Fields.SEQ_P.toString()));
			
			criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
			criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.QUS_QUT_SEQ.toString(), qusQutSeq));
			criteria.add(Restrictions.eq(MamRespostaEvolucoes.Fields.QUS_SEQP.toString(), qusSeqp));

			Short maxShortP = (Short) executeCriteriaUniqueResult(criteria);
			
			return (short) ((maxShortP == null ? 0 : maxShortP)+1);
		}
		
		/**
		 * #49992 - CONSULTA C3
		 * @param tieSeq
		 * @param indTipoPac
		 * @return
		 */
		public List<PreGeraItemQuestVO> obterListaPreGeraItemQuestVO(Long evoSeq, Integer tieSeq, String indTipoPac){
			
			final String QUT = "QUT.";
			final String QUS = "QUS.";
			final String RES = "RES.";
			
			DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class, "RES"); 
			criteria.createAlias(RES+MamRespostaEvolucoes.Fields.MAM_QUESTAO.toString(), "QUS");
			criteria.createAlias(QUS+MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
			
			criteria.setProjection(Projections.projectionList()
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.RESPOSTA.toString()), PreGeraItemQuestVO.Fields.RESPOSTA.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.EVO_SEQ.toString()), PreGeraItemQuestVO.Fields.P_EVO_SEQ.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.QUS_QUT_SEQ.toString()), PreGeraItemQuestVO.Fields.QUS_QUT_SEQ.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.QUS_SEQP.toString()), PreGeraItemQuestVO.Fields.QUS_SEQP.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.ESP_SEQ.toString()), PreGeraItemQuestVO.Fields.V_ESP_SEQ.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.SEQ_P.toString()), PreGeraItemQuestVO.Fields.SEQP.toString())
					.add(Projections.property(QUS+MamQuestao.Fields.DESCRICAO.toString()), PreGeraItemQuestVO.Fields.PERGUNTA.toString())
					.add(Projections.property(QUS+MamQuestao.Fields.ORDEM_VISUALIZACAO.toString()), PreGeraItemQuestVO.Fields.ORDEM_VISUALIZACAO.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.VVQ_QUS_QUT_SEQ.toString()), PreGeraItemQuestVO.Fields.VVQ_QUS_QUT_SEQ.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.VVQ_QUS_SEQP.toString()), PreGeraItemQuestVO.Fields.VVQ_QUS_SEQP.toString())
					.add(Projections.property(RES+MamRespostaEvolucoes.Fields.VVQ_SEQP.toString()), PreGeraItemQuestVO.Fields.VVQ_SEQP.toString())
					);
			
			criteria.setResultTransformer(Transformers.aliasToBean(PreGeraItemQuestVO.class));
			
			
			criteria.add(Restrictions.eq(RES+MamRespostaEvolucoes.Fields.EVO_SEQ, evoSeq));
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
		
		/**
		 *  #49992 - Consulta para obter cursor CUR_TOT_REA P2
		 * @param evoSeq
		 * @param tieSeq
		 * @return
		 */
		public Long obterCurTotRea(Long evoSeq, Integer tieSeq){
			final String REA = "REA.";
			final String QUS = "QUS.";
			final String QUT = "QUT.";
			
			
			DetachedCriteria criteria = DetachedCriteria.forClass(MamRespostaEvolucoes.class, "REA");
			criteria.createAlias(REA+MamRespostaEvolucoes.Fields.MAM_QUESTAO.toString(), "QUS");
			criteria.createAlias(QUS+MamQuestao.Fields.MAM_QUESTIONARIOS.toString(), "QUT");
			
			criteria.add(Restrictions.eq(REA+MamRespostaEvolucoes.Fields.EVO_SEQ.toString(), evoSeq));
			criteria.add(Restrictions.eq(QUT+MamQuestionario.Fields.TIE_SEQ.toString(), tieSeq));
			
			return executeCriteriaCount(criteria);
		}

		@Override
		public void persistir(MamRespostaEvolucoes entity) {
			MamRespostaEvolucoes resposta = entity;
			super.persistir(resposta);
		}
}
