package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoFatorPredisponenteVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MciFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;

public class MciMvtoFatorPredisponentesDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMvtoFatorPredisponentes> {

	private static final long serialVersionUID = -8187445793972811615L;

	public List<MciMvtoMedidaPreventivas> listaMedidasPreventivaIsolamento(Integer codigoPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class);
		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(),codigoPaciente));		
		criteria.add(Restrictions.isNull(MciMvtoMedidaPreventivas.Fields.DATA_FIM.toString()));
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.ISOLAMENTO.toString(), true));
		return executeCriteria(criteria);
	}
	
	/**
	 * Busca <b>Sequencial de Fator Predisponente</b> pelo codiog do paciente.<br>
	 * Conforme busca indicada abaixo:<br>
	 * <b>Busca 1:</b>
	 * <i>
	 * select distinct mfp.seq<br>
	 * from MciMvtoFatorPredisponentes mfp<br>
	 * where mfp.paciente.codigo = <b>codigoPaciente</b><br>
	 * and mfp.dataFim is null<br>
	 * and mfp.isolamento = true<br>
	 * </i>
	 * 
	 * @param codigoPaciente
	 * @return List of Integer.
	 */
	public List<Integer> pesquisarSequencialFatorPredisponentePorCodigoPaciente(Integer codigoPaciente) {
		DetachedCriteria criteria3 = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class);
		
		criteria3.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("seq"))
		));
		
		criteria3.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria3.add(Restrictions.isNull(MciMvtoFatorPredisponentes.Fields.DATA_FIM.toString()));
		criteria3.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.ISOLAMENTO.toString(), true));

		return this.executeCriteria(criteria3);
	}
	
	
	public MciMvtoFatorPredisponentes obterMciMvtoFatorPredisponentesPorPaciente(
			Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria
				.forClass(MciMvtoFatorPredisponentes.class);
		criteria.add(Restrictions.eq(
				MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(),
				codigoPaciente));
		return (MciMvtoFatorPredisponentes) this.executeCriteriaUniqueResult(criteria);
	}

	public List<MciMvtoFatorPredisponentes> listarMvtosFatorPredisponentesPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class);

		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MciMvtoFatorPredisponentes> listarMvtoFatorPredisponentes(Integer pacCodigo, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class);
		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.ATENDIMENTO.toString(), atendimento));
		
		return executeCriteria(criteria);
	}
	
	public List<NotificacaoFatorPredisponenteVO> listarNotificacoesPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class,"MFP");

		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.createAlias("MFP." + MciMvtoFatorPredisponentes.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "AAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MFP." + MciMvtoFatorPredisponentes.Fields.FATOR_PREDISPONENTE.toString(), "FPD", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_FIM.toString()), NotificacaoFatorPredisponenteVO.Fields.DTHR_FIM.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.SEQ.toString()), NotificacaoFatorPredisponenteVO.Fields.SEQ.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.CRIADO_EM.toString()), NotificacaoFatorPredisponenteVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.LTO_LTO_ID_NOTIFICADO.toString()), NotificacaoFatorPredisponenteVO.Fields.LTO_LTO_ID_NOTIFICADO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.QRT_NUMERO_NOTIFICADO.toString()), NotificacaoFatorPredisponenteVO.Fields.QRT_NUMERO_NOTIFICADO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.UNF_SEQ_NOTIFICADO.toString()), NotificacaoFatorPredisponenteVO.Fields.UNF_SEQ_NOTIFICADO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.LTO_LTO_ID.toString()), NotificacaoFatorPredisponenteVO.Fields.LTO_LTO_ID.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.QRT_NUMERO.toString()), NotificacaoFatorPredisponenteVO.Fields.QRT_NUMERO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.UNF_SEQ.toString()), NotificacaoFatorPredisponenteVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("FPD." + MciFatorPredisponentes.Fields.DESCRICAO.toString()), NotificacaoFatorPredisponenteVO.Fields.DESCRICAO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.FATOR_PREDISPONENTE_SEQ.toString()), NotificacaoFatorPredisponenteVO.Fields.FPD_SEQ.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.ATENDIMENTO_SEQ.toString()), NotificacaoFatorPredisponenteVO.Fields.ATENDIMENTO_SEQ.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString()), NotificacaoFatorPredisponenteVO.Fields.PACIENTE_CODIGO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.DATA_INICIO.toString()), NotificacaoFatorPredisponenteVO.Fields.DATA_INICIO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.DATA_FIM.toString()), NotificacaoFatorPredisponenteVO.Fields.DATA_FIM.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.SERVIDOR_MATRICULA.toString()), NotificacaoFatorPredisponenteVO.Fields.MATRICULA.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.SERVIDOR_VIN_CODIGO.toString()), NotificacaoFatorPredisponenteVO.Fields.CODIGO_VINCULO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.SERVIDOR_MATRICULA_ENCERRADO.toString()), NotificacaoFatorPredisponenteVO.Fields.MATRICULA_ENC.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.SERVIDOR_VIN_CODIGO_ENCERRADO.toString()), NotificacaoFatorPredisponenteVO.Fields.CODIGO_VINCULO_ENC.toString())
				.add(Projections.property("AAT." + AacConsultas.Fields.NUMERO.toString()), NotificacaoFatorPredisponenteVO.Fields.NUMERO.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoFatorPredisponenteVO.class));
		
		return executeCriteria(criteria);
	}
	
	public NotificacaoFatorPredisponenteVO listarNotificacoesPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class,"MFP");

		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.SEQ.toString(), seq));
		criteria.createAlias("MFP." + MciMvtoFatorPredisponentes.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "AAT");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_FIM.toString()), NotificacaoFatorPredisponenteVO.Fields.DTHR_FIM.toString())
				.add(Projections.property("AAT." + AacConsultas.Fields.NUMERO.toString()), NotificacaoFatorPredisponenteVO.Fields.NUMERO.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoFatorPredisponenteVO.class));
		
		return (NotificacaoFatorPredisponenteVO) executeCriteriaUniqueResult(criteria);
	}
	
	public MciMvtoFatorPredisponentes verificaNotificacaoAberta(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class,"MFP");

		criteria.createAlias("MFP." + MciMvtoFatorPredisponentes.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.isNull("MFP."+MciMvtoFatorPredisponentes.Fields.DATA_FIM.toString()));
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		if(seq != null){
			criteria.add(Restrictions.ne("MFP."+MciMvtoFatorPredisponentes.Fields.SEQ.toString(), seq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoFatorPredisponenteVO.class));
		
		return (MciMvtoFatorPredisponentes) executeCriteriaUniqueResult(criteria);
	}
	
	public List<MciMvtoFatorPredisponentes> listarMovimentosFatoresPredisponentes(final Short codigoFatorPredisponente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class, "MFP");
		criteria.add(Restrictions.eq("MFP." + MciMvtoFatorPredisponentes.Fields.FATOR_PREDISPONENTE_SEQ.toString(), codigoFatorPredisponente));
		return executeCriteria(criteria,0,10, MciMvtoFatorPredisponentes.Fields.FATOR_PREDISPONENTE_SEQ.toString(), true);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesFatoresPredisponentes(final Integer codigoPaciente) {
		DetachedCriteria criteria = montarCriteriaFatoresPredisponentes(codigoPaciente);
		
		criteria.addOrder(Order.desc("MFP." + MciMvtoFatorPredisponentes.Fields.DATA_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesFatoresPredisponentesBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		DetachedCriteria criteria = montarCriteriaFatoresPredisponentes(codigoPaciente);
		
		
		criteria.add(Restrictions.eq("MFP." + MciMvtoFatorPredisponentes.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc("MFP." + MciMvtoFatorPredisponentes.Fields.DATA_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria montarCriteriaFatoresPredisponentes(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoFatorPredisponentes.class, "MFP");
		criteria.createAlias("MFP." + MciMvtoFatorPredisponentes.Fields.FATOR_PREDISPONENTE.toString(), "FPD", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("FPD." + MciFatorPredisponentes.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.TOP_SEQ.toString())
				.add(Projections.property("FPD." + MciFatorPredisponentes.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.TOP_DESCRICAO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.DATA_INICIO.toString())
						, NotificacoesGeraisVO.Fields.DT_INICIO.toString())
				.add(Projections.property("MFP." + MciMvtoFatorPredisponentes.Fields.DATA_FIM.toString())
						, NotificacoesGeraisVO.Fields.DT_FIM.toString()));
		
		criteria.add(Restrictions.eq("MFP." + MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(), codigoPaciente));
		
		return criteria;
	}
}
