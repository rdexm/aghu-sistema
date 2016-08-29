package br.gov.mec.aghu.controleinfeccao.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;

import br.gov.mec.aghu.controleinfeccao.vo.NotificacaoMedidasPreventivasVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.dominio.DominioConfirmacaoCCI;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AhdHospitaisDia;
import br.gov.mec.aghu.model.AinAtendimentosUrgencia;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MciEtiologiaInfeccao;
import br.gov.mec.aghu.model.MciMvtoInfeccaoTopografias;
import br.gov.mec.aghu.model.MciMvtoMedidaPreventivas;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.model.MciTopografiaProcedimento;

public class MciMvtoMedidaPreventivasDAO extends
		br.gov.mec.aghu.core.persistence.dao.BaseDao<MciMvtoMedidaPreventivas> {

	private static final long serialVersionUID = 4609325674141066897L;

	public List<MciMvtoMedidaPreventivas> pesquisarMedidaPreventiva(
			Integer codigoPaciente) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		criteria.createAlias(MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO.toString(), MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO.toString(),	Criteria.INNER_JOIN);
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString(),codigoPaciente));
		criteria.add(Restrictions.isNull(MciMvtoMedidaPreventivas.Fields.DATA_FIM.toString()));
		criteria.add(Restrictions.isNotNull(MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO.toString()));
		criteria.add(Restrictions.eq(
				MciMvtoMedidaPreventivas.Fields.PATOLOGIA_QUARTO_PRIVATIVO
						.toString(), true));

		return executeCriteria(criteria);
	}

	public List<MciMvtoMedidaPreventivas> listarFatoresPredisponentes(Integer codigoPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString(),codigoPaciente));
		criteria.add(Restrictions.isNull(MciMvtoMedidaPreventivas.Fields.DATA_FIM.toString()));
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.ISOLAMENTO.toString(), true));

		return executeCriteria(criteria);
		
	}
	
	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);

		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString(), pacCodigo));

		return executeCriteria(criteria);
	}
	
	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivas(Integer pacCodigo, AghAtendimentos atendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString(), pacCodigo));
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.ATENDIMENTO.toString(), atendimento));
		
		return executeCriteria(criteria);
	}

	public List<Integer> listarMvtosMedidasPreventivasPorUnfSeqTipoOrigem(Short unfSeq, String codigoOrigem) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		
		criteria.setProjection(Projections.projectionList().add(
				Projections.property(MciMvtoMedidaPreventivas.Fields.SEQ.toString())));
		
		Criterion critUnfSeq = Restrictions.eq(MciMvtoMedidaPreventivas.Fields.UNF_SEQ.toString(), unfSeq);
		Criterion critUnfSeqNotificado = Restrictions.eq(MciMvtoMedidaPreventivas.Fields.UNF_SEQ_NOTIFICADO.toString(), unfSeq);
		
		criteria.add(Restrictions.or(critUnfSeq, critUnfSeqNotificado));
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.EIN_TIPO.toString(), codigoOrigem));
		
		return executeCriteria(criteria);
	}
	
	public List<MciMvtoMedidaPreventivas> pesquisarMciMvtoMedidaPreventivasPorPatologia(final Integer codigoPatologia){
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		criteria.createAlias(MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO.toString(), "MPI", JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("MPI." + MciPatologiaInfeccao.Fields.SEQ.toString(), codigoPatologia));
		return executeCriteria(criteria);
	}	

	//#1297 - C1 
	public List<NotificacaoMedidasPreventivasVO> buscarNotificacoesMedidasPreventivasPorCodigoPaciente(final Integer codigoPaciente) {
		DetachedCriteria criteria = criarCriteriaNotificacaoPorPaciente();
		criarJoinsNotificacaoMedidasPreventivas(criteria);
		criarFiltroCriteriaNotificacaoPorPaciente(codigoPaciente, criteria);
		criarProjectionNotificacaoPorPaciente(criteria);
		return executeCriteria(criteria);
	}
	
	//#1297 - C1 - Parse dos dados dos pojos para o vo
	private void criarProjectionNotificacaoPorPaciente(DetachedCriteria criteria) {
		ProjectionList projection =	Projections.projectionList()
				// MMP.SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.SEQ.toString())
				//	  MMP.SER_MATRICULA,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SER_MATRICULA.toString()), NotificacaoMedidasPreventivasVO.Fields.SER_MATRICULA.toString())
				//	  MMP.SER_VIN_CODIGO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SER_VIN_CODIGO.toString()), NotificacaoMedidasPreventivasVO.Fields.SER_VIN_CODIGO.toString())
				//	  MMP.UNF_SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.UNF_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.UNF_SEQ.toString())
				//	  MMP.UNF_SEQ_NOTIFICADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.UNF_SEQ_NOTIFICADO.toString()), NotificacaoMedidasPreventivasVO.Fields.UNF_SEQ_NOTIFICADO.toString())
				//	  MMP.PAC_CODIGO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString()), NotificacaoMedidasPreventivasVO.Fields.PAC_CODIGO.toString())				
				//	  MMP.IND_CONFIRMACAO_CCI,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.IND_CONFIRMACAO_CCI.toString()), NotificacaoMedidasPreventivasVO.Fields.IND_CONFIRMACAO_CCI.toString())
				//	  MMP.DT_INICIO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.DT_INICIO.toString()), NotificacaoMedidasPreventivasVO.Fields.DT_INICIO.toString())
				//	  MMP.CRIADO_EM,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.CRIADO_EM.toString()), NotificacaoMedidasPreventivasVO.Fields.CRIADO_EM.toString())
				//	  MMP.EIN_TIPO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.EIN_TIPO.toString()), NotificacaoMedidasPreventivasVO.Fields.EIN_TIPO.toString())
				//	  MMP.PAI_SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.PAI_SEQ.toString())
				//	  MMP.MIT_SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.MVTO_INFECCAO_TOPOGRAFIAS_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.MIT_SEQ.toString())
				//	  MMP.SER_MATRICULA_CONFIRMADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SER_MATRICULA_CONFIRMADO.toString()), NotificacaoMedidasPreventivasVO.Fields.SER_MATRICULA_CONFIRMADO.toString())
				//	MMP.SER_VIN_CODIGO_CONFIRMADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SER_VIN_CODIGO_CONFIRMADO.toString()), NotificacaoMedidasPreventivasVO.Fields.SER_VIN_CODIGO_CONFIRMADO.toString())
				//	  MMP.ATD_SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.ATENDIMENTO_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.ATD_SEQ.toString())
				//	  MMP.IHO_SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.IHO_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.IHO_SEQ.toString())
				//	  MMP.QRT_NUMERO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.QRT_NUMERO.toString()), NotificacaoMedidasPreventivasVO.Fields.QRT_NUMERO.toString())
				//	  MMP.QRT_NUMERO_NOTIFICADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.QRT_NUMERO_NOTIFICADO.toString()), NotificacaoMedidasPreventivasVO.Fields.QRT_NUMERO_NOTIFICADO.toString())
				//	  MMP.LTO_LTO_ID,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.LTO_LTO_ID.toString()), NotificacaoMedidasPreventivasVO.Fields.LTO_LTO_ID.toString())
				//	  MMP.LTO_LTO_ID_NOTIFICADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.LTO_LTO_ID_NOTIFICADO.toString()), NotificacaoMedidasPreventivasVO.Fields.LTO_LTO_ID_NOTIFICADO.toString())
				//	  MMP.SER_MATRICULA_ENCERRADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SER_MATRICULA_ENCERRADO.toString()), NotificacaoMedidasPreventivasVO.Fields.SER_MATRICULA_ENCERRADO.toString())
				//	  MMP.SER_VIN_CODIGO_ENCERRADO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.SER_VIN_CODIGO_ENCERRADO.toString()), NotificacaoMedidasPreventivasVO.Fields.SER_VIN_CODIGO_ENCERRADO.toString())
				//	  MMP.DT_FIM,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.DATA_FIM.toString()), NotificacaoMedidasPreventivasVO.Fields.DT_FIM.toString())
				//	  MMP.MOTIVO_ENCERRAMENTO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.MOTIVO_ENCERRAMENTO.toString()), NotificacaoMedidasPreventivasVO.Fields.MOTIVO_ENCERRAMENTO.toString())
				//	  MMP.IND_IMPRESSAO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.IND_IMPRESSAO.toString()), NotificacaoMedidasPreventivasVO.Fields.IND_IMPRESSAO.toString())
				//	  MMP.CIF_SEQ,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.CIF_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.CIF_SEQ.toString())
				//	  MMP.IND_GMR,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.IND_GMR.toString()), NotificacaoMedidasPreventivasVO.Fields.IND_GMR.toString())
				//	  MMP.IND_ISOLAMENTO,
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.IND_ISOLAMENTO.toString()), NotificacaoMedidasPreventivasVO.Fields.IND_ISOLAMENTO.toString())
				//	  MIT.top_seq,
				.add(Projections.property("MIT." + MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO_SEQ.toString()), NotificacaoMedidasPreventivasVO.Fields.TOP_SEQ.toString())
				//	  TOP.descricao,
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString()), NotificacaoMedidasPreventivasVO.Fields.TOP_DESCRICAO.toString())
				//	  EIN.descricao
				.add(Projections.property("EIN." + MciEtiologiaInfeccao.Fields.DESCRICAO.toString()), NotificacaoMedidasPreventivasVO.Fields.EIN_DESCRICAO.toString())
				// 	pai.ind_uso_quarto_privativo
				.add(Projections.property("PAI." + MciPatologiaInfeccao.Fields.IND_QUARTO_USO_PRIVATIVO.toString()), NotificacaoMedidasPreventivasVO.Fields.USO_QUARTO_PRIVATIVO.toString())
				.add(Projections.property("PAI." + MciPatologiaInfeccao.Fields.DESCRICAO.toString()), NotificacaoMedidasPreventivasVO.Fields.DESCRICAO_PATOLOGIA.toString())
				
				//# Serviço 39814
				.add(Projections.property("ATD." + AghAtendimentos.Fields.DTHR_INICIO.toString()), NotificacaoMedidasPreventivasVO.Fields.DT_ATENDIMENTO.toString())
				.add(Projections.property("LTO." + AinLeitos.Fields.LTO_ID.toString()), NotificacaoMedidasPreventivasVO.Fields.LEITO_ATENDIMENTO.toString())
				.add(Projections.property("QRT." + AinQuartos.Fields.NUMERO.toString()), NotificacaoMedidasPreventivasVO.Fields.QUARTO_ATENDIMENTO.toString())
				.add(Projections.property("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()), NotificacaoMedidasPreventivasVO.Fields.UNF_ATENDIMENTO.toString())
				.add(Projections.property("AAC." + AacConsultas.Fields.NUMERO.toString()), NotificacaoMedidasPreventivasVO.Fields.CONSULTA.toString())
				.add(Projections.property("AAC." + AacConsultas.Fields.DATA_CONSULTA.toString()), NotificacaoMedidasPreventivasVO.Fields.ATENDIMENTO_DATA_CONSULTA.toString())
				.add(Projections.property("ATU." + AinAtendimentosUrgencia.Fields.DT_ATENDIMENTO.toString()), NotificacaoMedidasPreventivasVO.Fields.URGENCIA_DATA_ATENDIMENTO.toString())
				.add(Projections.property("HOD." + AhdHospitaisDia.Fields.DTHR_INICIO .toString()), NotificacaoMedidasPreventivasVO.Fields.HOSPITAL_DIA_DATA_INICIO.toString())
				.add(Projections.property("INT." + AinInternacao.Fields.DT_INTERNACAO.toString()), NotificacaoMedidasPreventivasVO.Fields.INTERNACAO_DATA_INICIO.toString());
		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacaoMedidasPreventivasVO.class));
		
	}
	
	//#1297 - C1 - Cria os left joins
	private void criarJoinsNotificacaoMedidasPreventivas(DetachedCriteria criteria) {
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO.toString(), "PAI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.MIT_SEQ.toString(), "MIT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MIT." + MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO.toString(), "TOP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.MCI_ETIOLOGIA_INFECCAO.toString(), "EIN", JoinType.LEFT_OUTER_JOIN);
		// Joins para trazer os dados relativos ao serviço 39814
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.ATENDIMENTO.toString(), "ATD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.LTO_LTO.toString(), "LTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "AAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.QUARTO.toString(), "QRT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.ATENDIMENTO_URGENCIA.toString(), "ATU", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.HOD.toString(), "HOD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.INTERNACAO.toString(), "INT", JoinType.LEFT_OUTER_JOIN);
		
	}
	
	//#1297 - C1 - Adiciona o filtro
	private void criarFiltroCriteriaNotificacaoPorPaciente(final Integer codigoPaciente, DetachedCriteria criteria) {
		if(codigoPaciente != null) {
			criteria.add(Restrictions.eq("MMP." + MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString(), codigoPaciente));
		}
	}

	private DetachedCriteria criarCriteriaNotificacaoPorPaciente() {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class, "MMP");
		return criteria;
	} 
	
	/**
	 * 
	 * Retorna Movimentos de Medidas Preventivas associados com instituição hospitalar
	 * 
	 * @return Internacao
	 */
	public List<MciMvtoMedidaPreventivas> pesquisarMovimentosMedidasPreventivasInstituicaoHospitalar(final Integer ihoSeq) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.INSTITUICAO_HOSPITALAR_SEQ.toString(), ihoSeq));
		return executeCriteria(criteria);
	}
	
	public List<MciMvtoMedidaPreventivas> listarMvtosMedidasPreventivasPorseqNotificacaoTopografia(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.MVTO_INFECCAO_TOPOGRAFIAS_SEQ.toString(), seq));
		return executeCriteria(criteria);
	}

	// #1297 - c8
	public boolean verificarMedidaPreventivaNaoEncerradaPorPatologiaAtendimento(Integer seq, Integer seqPatologia, Integer seqAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);		
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO_SEQ.toString(), seqPatologia));
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.ATENDIMENTO_SEQ.toString(), seqAtendimento));
		criteria.add(Restrictions.isNull(MciMvtoMedidaPreventivas.Fields.DATA_FIM.toString()));
		if(seq != null) {
			criteria.add(Restrictions.ne(MciMvtoMedidaPreventivas.Fields.SEQ.toString(), seq));
		}
		return executeCriteriaCount(criteria) > 0;
	}
	
	public Boolean existeNotificacaoNaoConferidaListarPacientesCCIH(final Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class);
		criteria.createAlias(MciMvtoMedidaPreventivas.Fields.PACIENTE.toString(), "PAC");
		criteria.add(Restrictions.eq(MciMvtoMedidaPreventivas.Fields.IND_CONFIRMACAO_CCI.toString(), DominioConfirmacaoCCI.N));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		return executeCriteriaExists(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesDoencasCondicoes(final Integer codigoPaciente) {
		DetachedCriteria criteria = montarCriteriaNotificacoes(codigoPaciente);
		criteria.addOrder(Order.desc("MMP." + MciMvtoMedidaPreventivas.Fields.DT_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public List<NotificacoesGeraisVO> listarNotificacoesDoencasCondicoesBuscaAtiva(final Integer codigoPaciente, final Integer atdSeq) {
		DetachedCriteria criteria = montarCriteriaNotificacoes(codigoPaciente);
		
		criteria.add(Restrictions.eq("MMP." + MciMvtoMedidaPreventivas.Fields.ATENDIMENTO_SEQ.toString(), atdSeq));

		criteria.addOrder(Order.desc("MMP." + MciMvtoMedidaPreventivas.Fields.DT_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(NotificacoesGeraisVO.class));
		
		return executeCriteria(criteria);
	}
	
	public DetachedCriteria montarCriteriaNotificacoes(Integer codigoPaciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MciMvtoMedidaPreventivas.class, "MMP");
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.PATOLOGIA_INFECCAO.toString(), "PAI", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.MVTO_INFECCAO_TOPOGRAFIAS.toString(), "MIT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MIT." + MciMvtoInfeccaoTopografias.Fields.TOPOGRAFIA_PROCEDIMENTO.toString(), "TOP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MMP." + MciMvtoMedidaPreventivas.Fields.MCI_ETIOLOGIA_INFECCAO.toString(), "EIN", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("PAI." + MciPatologiaInfeccao.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.PAT_SEQ.toString())
				.add(Projections.property("PAI." + MciPatologiaInfeccao.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.PAT_DESCRICAO.toString())
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.SEQ.toString())
						, NotificacoesGeraisVO.Fields.TOP_SEQ.toString())
				.add(Projections.property("TOP." + MciTopografiaProcedimento.Fields.DESCRICAO.toString())
						, NotificacoesGeraisVO.Fields.TOP_DESCRICAO.toString())
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.DT_INICIO.toString())
						, NotificacoesGeraisVO.Fields.DT_INICIO.toString())
				.add(Projections.property("MMP." + MciMvtoMedidaPreventivas.Fields.DATA_FIM.toString())
						, NotificacoesGeraisVO.Fields.DT_FIM.toString()));
		
		criteria.add(Restrictions.eq("MMP." + MciMvtoMedidaPreventivas.Fields.COD_PACIENTE.toString(), codigoPaciente));
		
		return criteria;
	}
}
