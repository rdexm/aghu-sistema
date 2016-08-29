package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoProcedimentoRiscoVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciMvtoFatorPredisponentes;
import br.gov.mec.aghu.model.MciMvtoProcedimentoRiscos;
import br.gov.mec.aghu.model.MciProcedimentoRisco;

public class MciMvtoProcedimentoRiscosDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMvtoProcedimentoRiscos> {

	private static final long serialVersionUID = -4097360280852457829L;

	public List<MciMvtoProcedimentoRiscos> listarMvtosProcedimentosRiscosPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class);

		criteria.add(Restrictions.eq(MciMvtoProcedimentoRiscos.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	public List<MciMvtoProcedimentoRiscos> listarMvtosProcedimentosRiscos(Integer pacCodigo, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class);
		criteria.add(Restrictions.eq(MciMvtoProcedimentoRiscos.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO.toString(), atendimento));

		return executeCriteria(criteria);
	}
	
	public List<NotificacaoProcedimentoRiscoVO> listarNotificacoesPorPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class,"MPR");

		criteria.add(Restrictions.eq(MciMvtoFatorPredisponentes.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.createAlias("MPR." + MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "AAT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MPR." + MciMvtoProcedimentoRiscos.Fields.PROCEDIMENTO_RISCO.toString(), "PDR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				//ajustado apra mostrar a data de início do atendimento.
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString()), NotificacaoProcedimentoRiscoVO.Fields.DTHR_FIM.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.SEQ.toString()), NotificacaoProcedimentoRiscoVO.Fields.SEQ.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.CRIADO_EM.toString()), NotificacaoProcedimentoRiscoVO.Fields.CRIADO_EM.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.CONFIRMADO_CCIH.toString()), NotificacaoProcedimentoRiscoVO.Fields.CONFIRMADO_CCIH.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.LTO_LTO_ID_NOTIFICADO.toString()), NotificacaoProcedimentoRiscoVO.Fields.LTO_LTO_ID_NOTIFICADO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.QRT_NUMERO_NOTIFICADO.toString()), NotificacaoProcedimentoRiscoVO.Fields.QRT_NUMERO_NOTIFICADO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.UNF_SEQ_NOTIFICADO.toString()), NotificacaoProcedimentoRiscoVO.Fields.UNF_SEQ_NOTIFICADO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.LTO_LTO_ID.toString()), NotificacaoProcedimentoRiscoVO.Fields.LTO_LTO_ID.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.QRT_NUMERO.toString()), NotificacaoProcedimentoRiscoVO.Fields.QRT_NUMERO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.UNF_SEQ.toString()), NotificacaoProcedimentoRiscoVO.Fields.UNF_SEQ.toString())
				.add(Projections.property("PDR." + MciProcedimentoRisco.Fields.DESCRICAO.toString()), NotificacaoProcedimentoRiscoVO.Fields.DESCRICAO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.POR_SEQ.toString()), NotificacaoProcedimentoRiscoVO.Fields.POR_SEQ.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO_SEQ.toString()), NotificacaoProcedimentoRiscoVO.Fields.ATENDIMENTO_SEQ.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.COD_PACIENTE.toString()), NotificacaoProcedimentoRiscoVO.Fields.PACIENTE_CODIGO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.DATA_INICIO.toString()), NotificacaoProcedimentoRiscoVO.Fields.DATA_INICIO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.DATA_FIM.toString()), NotificacaoProcedimentoRiscoVO.Fields.DATA_FIM.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.SERVIDOR_MATRICULA.toString()), NotificacaoProcedimentoRiscoVO.Fields.MATRICULA.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.SERVIDOR_VIN_CODIGO.toString()), NotificacaoProcedimentoRiscoVO.Fields.CODIGO_VINCULO.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.SERVIDOR_MATRICULA_ENCERRADO.toString()), NotificacaoProcedimentoRiscoVO.Fields.MATRICULA_ENC.toString())
				.add(Projections.property("MPR." + MciMvtoProcedimentoRiscos.Fields.SERVIDOR_VIN_CODIGO_ENCERRADO.toString()), NotificacaoProcedimentoRiscoVO.Fields.CODIGO_VINCULO_ENC.toString())
				.add(Projections.property("AAT." + AacConsultas.Fields.NUMERO.toString()), NotificacaoProcedimentoRiscoVO.Fields.NUMERO.toString())
		);
		criteria.addOrder(Order.desc("MPR." + MciMvtoProcedimentoRiscos.Fields.DATA_INICIO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoProcedimentoRiscoVO.class));
		
		return executeCriteria(criteria);
	}
	
	public NotificacaoProcedimentoRiscoVO listarNotificacoesPorSeq(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class,"MPR");

		criteria.add(Restrictions.eq(MciMvtoProcedimentoRiscos.Fields.SEQ.toString(), seq));
		criteria.createAlias("MPR." + MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "AAT");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_FIM.toString()), NotificacaoProcedimentoRiscoVO.Fields.DTHR_FIM.toString())
				.add(Projections.property("AAT." + AacConsultas.Fields.NUMERO.toString()), NotificacaoProcedimentoRiscoVO.Fields.NUMERO.toString())
		);
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoProcedimentoRiscoVO.class));
		
		return (NotificacaoProcedimentoRiscoVO) executeCriteriaUniqueResult(criteria);
	}
	
	public MciMvtoProcedimentoRiscos verificaNotificacaoAberta(Integer atdSeq, Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class,"MPR");

		criteria.createAlias("MPR." + MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO.toString(), "ATD");
		criteria.add(Restrictions.isNull("MPR."+MciMvtoProcedimentoRiscos.Fields.DATA_FIM.toString()));
		criteria.add(Restrictions.eq("ATD."+AghAtendimentos.Fields.SEQ.toString(), atdSeq));
		if(seq != null){
			criteria.add(Restrictions.ne("MPR."+MciMvtoProcedimentoRiscos.Fields.SEQ.toString(), seq));
		}
		//criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoFatorPredisponenteVO.class));
		
		return (MciMvtoProcedimentoRiscos) executeCriteriaUniqueResult(criteria);
	}
	
	public Boolean existeNotificacaoNaoConferidaListarPacientesCCIH(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class);
		criteria.createAlias(MciMvtoProcedimentoRiscos.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(MciMvtoProcedimentoRiscos.Fields.CONFIRMADO_CCIH.toString(), DominioConfirmacaoCCI.N));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		return executeCriteriaExists(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesProcedimentosRisco(final Integer codigoPaciente) {
		DetachedCriteria criteria = montarCriteriaNotificacoes();
		
		criteria.add(Restrictions.eq("MRI." + MciMvtoProcedimentoRiscos.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.addOrder(Order.desc("MRI." + MciMvtoProcedimentoRiscos.Fields.DATA_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesProcedimentosRiscoBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		DetachedCriteria criteria = montarCriteriaNotificacoes();
		criteria.add(Restrictions.eq("MRI." + MciMvtoProcedimentoRiscos.Fields.COD_PACIENTE.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("MRI." + MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));
		criteria.addOrder(Order.desc("MRI." + MciMvtoProcedimentoRiscos.Fields.DATA_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria montarCriteriaNotificacoes() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class, "MRI");
		criteria.createAlias("MRI." + MciMvtoProcedimentoRiscos.Fields.PROCEDIMENTO_RISCO.toString(), "POR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("POR." + MciProcedimentoRisco.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.TOP_SEQ.toString())
				.add(Projections.property("POR." + MciProcedimentoRisco.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.TOP_DESCRICAO.toString())
				.add(Projections.property("MRI." + MciMvtoProcedimentoRiscos.Fields.DATA_INICIO.toString())
						, NotificacoesGeraisVO.Fields.DT_INICIO.toString())
				.add(Projections.property("MRI." + MciMvtoProcedimentoRiscos.Fields.DATA_FIM.toString())
						, NotificacoesGeraisVO.Fields.DT_FIM.toString()));

		return criteria;
	}
	
	
	public List<MciMvtoProcedimentoRiscos> listarNotificacoesPorPacienteATendimento(Integer pacCodigo, Integer atdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoProcedimentoRiscos.class,"MRI");
		criteria.createAlias("MRI." + MciMvtoProcedimentoRiscos.Fields.PROCEDIMENTO_RISCO.toString(), "POR", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("MRI." + MciMvtoProcedimentoRiscos.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq("MRI." + MciMvtoProcedimentoRiscos.Fields.ATENDIMENTO_SEQ.toString(), pacCodigo));
		criteria.addOrder(Order.desc("MPR." + MciMvtoProcedimentoRiscos.Fields.DATA_INICIO.toString()));
		
		return executeCriteria(criteria);
	}
}
