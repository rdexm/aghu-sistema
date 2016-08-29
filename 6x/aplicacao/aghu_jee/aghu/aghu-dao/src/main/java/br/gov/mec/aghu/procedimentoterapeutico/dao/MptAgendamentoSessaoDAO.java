package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AlterarHorariosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioAcomodacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.HorarioReservadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MapaDisponibilidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PercentualOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ReservasVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MptAgendamentoSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptAgendamentoSessao> {

	private static final long serialVersionUID = 1963016939129368832L;

	private static final String HRS = "HRS";
	private static final String HRS_DOT = "HRS.";
	private static final String AGS = "AGS";
	private static final String AGS_DOT = "AGS.";
	private static final String PAC = "PAC";
	private static final String PAC_DOT = "PAC.";
	private static final String PTA = "PTA";
	private static final String PTA_DOT = "PTA.";
	private static final String VPA = "VPA";
	private static final String VPA_DOT = "VPA.";

	/**
	 * #41730 - C7 - Busca pacientes extras (sem acomodação)
	 * @param salaSeq
	 * @param dataInicio
	 * @param turno
	 * @return
	 */
	public Long obterQuantidadePacientesExtras(Short salaSeq, Date dataInicio, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN,
				Restrictions.eqProperty("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL." + MptSalas.Fields.SEQ.toString()));
		
		if (salaSeq != null) {
			criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salaSeq));
		}
		criteria.add(Restrictions.isNull("HRS." + MptHorarioSessao.Fields.LOA_SEQ.toString()));
		if (dataInicio != null) {
			criteria.add(Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), 
					DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataInicio)));
		}
		if (turno != null) {
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TURNO.toString(), turno));
		}
		return executeCriteriaCountDistinct(criteria, "AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), false);
	}
	
	/**
	 * #41730 - C9 - Minutos Disponiveis
	 * @param salaSeq
	 * @param dataInicio
	 * @param turno
	 * @param subSelect
	 * @return
	 */
	public Object[] obterMinutosDisponiveis(Short salaSeq, Date dataInicio, DominioTurno turno, String subSelect) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN,
				Restrictions.eqProperty("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL." + MptSalas.Fields.SEQ.toString()));
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection(subSelect + " AS somaDifHoras ", 
				new String[]{"somaDifHoras"}, new Type[]{IntegerType.INSTANCE}), "somaDifHoras");
		projection.add(Projections.sqlProjection(" sum((DATA_FIM - DATA_INICIO) * 1440) AS somaDifDatas ", 
				new String[]{"somaDifDatas"}, new Type[]{IntegerType.INSTANCE}), "somaDifDatas");
		projection.add(Projections.groupProperty("SAL." + MptSalas.Fields.DESCRICAO.toString()));
		criteria.setProjection(projection);
		
		if (salaSeq != null) {
			criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salaSeq));
		}
		
		if (dataInicio != null) {
			criteria.add(Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), 
					DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataInicio)));
		}
		
		if (turno != null) {
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TURNO.toString(), turno));
		}
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #41730 - C10 - Número de pacientes e disponibilidade
	 * @param salSeq
	 * @param dataInicio
	 * @param turno
	 * @param subSelect
	 * @return
	 */
	public Object[] obterNumeroPacientesDisponibilidade(Short salSeq, Date dataInicio, DominioTurno turno, String subSelect) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN, 
				Restrictions.eqProperty("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL." + MptSalas.Fields.SEQ.toString()));
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection(" (sum((DATA_FIM - DATA_INICIO) * 1440) * 100) AS somaDifDatas ", 
				new String[]{"somaDifDatas"}, new Type[]{IntegerType.INSTANCE}), "somaDifDatas");
		projection.add(Projections.sqlProjection(subSelect + " AS somaDifHoras ", 
				new String[]{"somaDifHoras"}, new Type[]{IntegerType.INSTANCE}), "somaDifHoras");
		projection.add(Projections.sqlProjection(" count({alias}.PAC_CODIGO) AS numeroPacientes ", 
				new String[]{MapaDisponibilidadeVO.Fields.NUMERO_PACIENTES.toString()}, new Type[]{IntegerType.INSTANCE}), 
				MapaDisponibilidadeVO.Fields.NUMERO_PACIENTES.toString());
		projection.add(Projections.groupProperty("SAL." + MptSalas.Fields.DESCRICAO.toString()));
		criteria.setProjection(projection);
		
		if (salSeq != null) {
			criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salSeq));
		}
		
		if (dataInicio != null) {
			criteria.add(Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), 
					DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataInicio)));
		}
		
		if (turno != null) {
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TURNO.toString(), turno));
		}
		
		return (Object[]) executeCriteriaUniqueResult(criteria);
	}
	
	public PercentualOcupacaoVO obterPercentualOcupacaoNoDia(Short salSeq, Date dataInicio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN);
		
		StringBuilder sb1 = new StringBuilder(1500);
		if (isOracle()) {
			sb1.append(" CAST((SUM((DATA_FIM - DATA_INICIO) * 1440) * 100) / (SELECT SUM((HR_FIM - HR_INICIO) * 1440) ")
			.append(" FROM MPT_TURNO_TIPO_SESSAO TTS ")
			.append(" INNER JOIN AGH.MPT_TIPO_SESSAO TSE ON TSE.SEQ = TTS.TPS_SEQ ")
			.append(" INNER JOIN AGH.MPT_SALAS SAL ON SAL.TPS_SEQ = TSE.SEQ ")
			.append(" INNER JOIN AGH.MPT_LOCAL_ATENDIMENTO LAT ON LAT.SEQ_SAL =  SAL.SEQ ")
			.append(" WHERE SAL.SEQ = ").append(salSeq).append(" ) AS INTEGER) AS percentual");
			
		} else {
			sb1.append(" ((SELECT EXTRACT (EPOCH FROM (SUM((DATA_FIM - DATA_INICIO) / 60)) * 100)) ")
			.append(" / (SELECT EXTRACT (EPOCH FROM (SELECT SUM((HR_FIM - HR_INICIO) / 60) ")
			.append(" FROM AGH.MPT_TURNO_TIPO_SESSAO TTS ")
			.append(" INNER JOIN AGH.MPT_TIPO_SESSAO TSE ON TSE.SEQ = TTS.TPS_SEQ ")
			.append(" INNER JOIN AGH.MPT_SALAS SAL ON SAL.TPS_SEQ = TSE.SEQ ")
			.append(" INNER JOIN AGH.MPT_LOCAL_ATENDIMENTO LAT ON LAT.SEQ_SAL =  SAL.SEQ ")
			.append(" WHERE SAL.SEQ = ").append(salSeq).append(" ))))::INTEGER AS percentual");
		}
		
		StringBuilder sb2 = new StringBuilder(1500);
		if (isOracle()) {
			sb2.append(" CAST((SELECT SUM((HR_FIM - HR_INICIO) * 1440) ")
			.append(" FROM MPT_TURNO_TIPO_SESSAO TTS ")
			.append(" INNER JOIN AGH.MPT_TIPO_SESSAO TSE ON TSE.SEQ = TTS.TPS_SEQ ")
			.append(" INNER JOIN AGH.MPT_SALAS SAL ON SAL.TPS_SEQ = TSE.SEQ ")
			.append(" INNER JOIN AGH.MPT_LOCAL_ATENDIMENTO LAT ON LAT.SEQ_SAL =  SAL.SEQ ")
			.append(" WHERE SAL.SEQ = ").append(salSeq).append(" ) ")
			.append(" - SUM((DATA_FIM - DATA_INICIO) * 1440) AS INTEGER) AS minutosDisponiveis ");
			
		} else {
			sb2.append(" ((SELECT EXTRACT(EPOCH FROM (SELECT SUM((TTS.HR_FIM - TTS.HR_INICIO) / 60) ")
			.append(" INNER JOIN AGH.MPT_TIPO_SESSAO TSE ON TSE.SEQ = TTS.TPS_SEQ ")
			.append(" INNER JOIN AGH.MPT_SALAS SAL ON SAL.TPS_SEQ = TSE.SEQ ")
			.append(" INNER JOIN AGH.MPT_LOCAL_ATENDIMENTO LAT ON LAT.SEQ_SAL =  SAL.SEQ ")
			.append(" WHERE SAL.SEQ = ").append(salSeq).append(" ))) ")
			.append(" - (SELECT EXTRACT (EPOCH FROM SUM((DATA_FIM - DATA_INICIO) / 60))))::INTEGER AS minutosDisponiveis ");
		}
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlGroupProjection("TO_CHAR(DATA_INICIO, 'DD/MM/YYYY') AS dataInicio",
				"TO_CHAR(DATA_INICIO, 'DD/MM/YYYY')", new String[]{PercentualOcupacaoVO.Fields.DATA_INICIO.toString()}, new Type[]{StringType.INSTANCE}));
		projection.add(Projections.sqlProjection(sb1.toString(), new String[]{PercentualOcupacaoVO.Fields.PERCENTUAL.toString()},
				new Type[]{IntegerType.INSTANCE}));
		
		if (isOracle()) {
			projection.add(Projections.sqlProjection(" CAST(COUNT({alias}.PAC_CODIGO) AS INTEGER) AS numeroPacientes ",
					new String[]{PercentualOcupacaoVO.Fields.NUMERO_PACIENTES.toString()}, new Type[]{IntegerType.INSTANCE}));
			
		} else {
			projection.add(Projections.sqlProjection(" COUNT({alias}.PAC_CODIGO)::INTEGER AS numeroPacientes ",
					new String[]{PercentualOcupacaoVO.Fields.NUMERO_PACIENTES.toString()}, new Type[]{IntegerType.INSTANCE}));
		}
		
		projection.add(Projections.sqlProjection(sb2.toString(),
				new String[]{PercentualOcupacaoVO.Fields.MINUTOS_DISPONIVEIS.toString()}, new Type[]{IntegerType.INSTANCE}));
		
		criteria.setProjection(projection);
		
		criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salSeq));
		criteria.add(Restrictions.sqlRestriction(" TO_CHAR(DATA_INICIO, 'DD/MM/YYYY') =  '" + DateUtil.obterDataFormatada(dataInicio, "dd/MM/yyyy") + "'"));
		
		criteria.setResultTransformer(Transformers.aliasToBean(PercentualOcupacaoVO.class));
		
		return (PercentualOcupacaoVO) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AlterarHorariosVO> pesquisarPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(final Integer codigoPaciente, final int diasValidadePrecricaoQuimio) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class);

		// Aliases padrão da C1
		criteria.createAlias(MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES1", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SER_VAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER_VAL." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES2", JoinType.LEFT_OUTER_JOIN);

		// Alias extras da C1
		criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.PRESCRICAO_CICLO.toString(), "CLO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ATE." + AghAtendimentos.Fields.PACIENTE.toString(), "PAC");
		

		// Projections
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.groupProperty("CLO." + MptPrescricaoCiclo.Fields.SEQ.toString()), AlterarHorariosVO.Fields.CICLO.toString());
		pList.add(Projections.groupProperty("SER." + RapServidores.Fields.MATRICULA.toString()), AlterarHorariosVO.Fields.SER_MATRICULA.toString());
		pList.add(Projections.groupProperty("SER." + RapServidores.Fields.VIN_CODIGO.toString()), AlterarHorariosVO.Fields.SER_VIN_CODIGO.toString());
		pList.add(Projections.groupProperty("SER_VAL." + RapServidores.Fields.MATRICULA.toString()), AlterarHorariosVO.Fields.SER_MATRICULA_VALIDA.toString());
		pList.add(Projections.groupProperty("SER_VAL." + RapServidores.Fields.VIN_CODIGO.toString()), AlterarHorariosVO.Fields.SER_VIN_CODIGO_VALIDA.toString());
		pList.add(Projections.groupProperty("PES1." + RapPessoasFisicas.Fields.NOME.toString()), AlterarHorariosVO.Fields.RESPONSAVEL_2.toString());
		pList.add(Projections.groupProperty("PES2." + RapPessoasFisicas.Fields.NOME.toString()), AlterarHorariosVO.Fields.RESPONSAVEL_1.toString());
		pList.add(Projections.groupProperty("SES." + MptSessao.Fields.CICLO.toString()), AlterarHorariosVO.Fields.CLO_SEQ.toString());
		pList.add(Projections.min("PTE." + MptPrescricaoPaciente.Fields.DATA_PREVISAO_EXECUCAO.toString()), AlterarHorariosVO.Fields.DATA_SUGERIDA.toString());
		pList.add(Projections.groupProperty(MptAgendamentoSessao.Fields.SEQ.toString()), AlterarHorariosVO.Fields.SEQ_AGENDAMENTO.toString());
		pList.add(Projections.groupProperty("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString()));
		criteria.setProjection(Projections.distinct(pList)); // DISTINCT
		
		
		// Condicionais
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.eq("SES." + MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.A));

		// Validade (P_AGHU_VALIDADE_PRECRICAO_QUIMIO)
		Calendar validade = Calendar.getInstance();
		validade.setTime(new Date()); // SYSDATE
		validade.add(Calendar.DAY_OF_YEAR, -diasValidadePrecricaoQuimio);
		criteria.add(Restrictions.ge("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString(), validade.getTime()));
		
		
		criteria.setResultTransformer(Transformers.aliasToBean(AlterarHorariosVO.class));
		criteria.addOrder(Order.desc("PTE." + MptPrescricaoPaciente.Fields.CRIADO_EM.toString()));
		
		return executeCriteria(criteria);
	}
	
	public List<HorarioAcomodacaoVO> obterListaHorariosReservadosMarcados(Short salSeq, Short loaSeq, Date inicioTurno, Date fimTurno, boolean reservados) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
		criteria.createAlias(MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "LOA", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		
		if (reservados) {
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R));
			
		} else {
			criteria.createAlias("HRS." + MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);
			criteria.createAlias("CLO." + MptPrescricaoCiclo.Fields.PROTOCOLO_CICLO.toString(), "PTC", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("PTC." + MptProtocoloCiclo.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
			criteria.add(Restrictions.or(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M),Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R)));
		}
		
		ProjectionList pList = Projections.projectionList();
		
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString())
						, HorarioAcomodacaoVO.Fields.DATA_INICIO.toString());
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString())
						, HorarioAcomodacaoVO.Fields.DATA_FIM.toString());
		pList.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString())
				, HorarioAcomodacaoVO.Fields.PAC_CODIGO.toString());
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.CICLO.toString())
				, HorarioAcomodacaoVO.Fields.CICLO.toString());
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DIA.toString())
				, HorarioAcomodacaoVO.Fields.DIA.toString());
		pList.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.SEQ.toString())
				, HorarioAcomodacaoVO.Fields.AGS_SEQ.toString());
		
		
		if (reservados) {
			pList.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString())
					, HorarioAcomodacaoVO.Fields.PROTOCOLO.toString());
			
		} else {
			pList.add(Projections.sqlProjection(" COALESCE(PTA8_.TITULO, ptc6_.DESCRICAO) as protocolo",
					new String[]{HorarioAcomodacaoVO.Fields.PROTOCOLO.toString()}, new Type[]{StringType.INSTANCE}));
		}
		
		criteria.setProjection(pList);
		
		criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salSeq));
		criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.SEQ.toString(), loaSeq));
		criteria.add(Restrictions.or(Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), inicioTurno, fimTurno),
				Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), inicioTurno, fimTurno)));
		
		criteria.addOrder(Order.asc("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(HorarioAcomodacaoVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * Obtém horários reservados para o Paciente informado.
	 * 
	 * @param pacCodigo - Código do Paciente
	 * @return Lista de horários reservados
	 */
	public List<HorarioReservadoVO> obterReservasPacienteParaConfirmacaoCancelamento(Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, HRS);

		ProjectionList projections = Projections.projectionList();

		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.SEQ.toString()), HorarioReservadoVO.Fields.SEQ.toString());
		projections.add(Projections.property(AGS_DOT + MptAgendamentoSessao.Fields.SEQ.toString()), HorarioReservadoVO.Fields.AGS_SEQ.toString());
		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.DIA.toString()), HorarioReservadoVO.Fields.DIA.toString());
		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.DATA_INICIO.toString()), HorarioReservadoVO.Fields.DATA_INICIO.toString());
		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.DATA_FIM.toString()), HorarioReservadoVO.Fields.DATA_FIM.toString());
		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.TEMPO.toString()), HorarioReservadoVO.Fields.TEMPO.toString());
		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.CICLO.toString()), HorarioReservadoVO.Fields.CICLO.toString());
		projections.add(Projections.property(HRS_DOT + MptHorarioSessao.Fields.CONSULTAS_AMB.toString()), HorarioReservadoVO.Fields.CONSULTAS_AMB.toString());
		projections.add(Projections.property(PAC_DOT + AipPacientes.Fields.CODIGO.toString()), HorarioReservadoVO.Fields.PAC_CODIGO.toString());
		projections.add(Projections.property(PTA_DOT + MpaProtocoloAssistencial.Fields.TITULO.toString()), HorarioReservadoVO.Fields.PROTOCOLO.toString());

		criteria.setProjection(projections);

		criteria.createAlias(HRS_DOT + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), AGS);
		criteria.createAlias(AGS_DOT + MptAgendamentoSessao.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), VPA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(VPA_DOT + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), PTA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AGS_DOT + MptAgendamentoSessao.Fields.PACIENTE.toString(), PAC, JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.eq(HRS_DOT + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R));
		criteria.add(Restrictions.ge(HRS_DOT + MptHorarioSessao.Fields.DATA_INICIO.toString(), new Date()));
		criteria.add(Restrictions.eq(PAC_DOT + AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HSS");

		subCriteria.setProjection(Projections.property("ASS." + MptAgendamentoSessao.Fields.SEQ.toString()));

		subCriteria.createAlias("HSS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "ASS");
		subCriteria.createAlias("ASS." + MptAgendamentoSessao.Fields.PACIENTE.toString(), "PCS");

		subCriteria.add(Restrictions.eq("HSS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R));
		subCriteria.add(Restrictions.ge("HSS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), new Date()));
		subCriteria.add(Restrictions.eq("PCS." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));

		criteria.add(Subqueries.propertyIn(AGS_DOT + MptAgendamentoSessao.Fields.SEQ.toString(), subCriteria));

		criteria.addOrder(Order.asc(HRS_DOT + MptHorarioSessao.Fields.DATA_INICIO.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(HorarioReservadoVO.class));
		return executeCriteria(criteria);
	}

	/**
	 * #42292 - C7 Consulta carrega responsável pelo agendamento
	 * @param agsSeq
	 * @return
	 */
	public String obterResponsavelPeloAgendamento(Short agsSeq) {
		DetachedCriteria criteria = montarCriteriaResponsavelPeloAgendamento(agsSeq);
		return (String) executeCriteriaUniqueResult(criteria);
	}

	private DetachedCriteria montarCriteriaResponsavelPeloAgendamento(Short agsSeq) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()))));
		
		if (agsSeq != null) {
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.SEQ.toString(), agsSeq));
		}
		return criteria;
	}
	
	/**
	 * Verificar se o paciente está atrasado para a sessão
	 * #41704
	 * @param seqHorario
	 * @param codPaciente
	 * @return
	 */
	public Boolean verificarPacienteAtrasadoSessao(Short seqHorario, Integer codPaciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, AGS);

		criteria.setProjection(Projections.property(AGS_DOT +MptAgendamentoSessao.Fields.SEQ.toString()));

		criteria.createAlias(AGS_DOT + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString() , HRS);
		criteria.createAlias(HRS_DOT + MptHorarioSessao.Fields.SESSAO.toString() , "SES");
		
		criteria.add(Restrictions.eq(HRS_DOT+MptHorarioSessao.Fields.SEQ.toString(), seqHorario));
		criteria.add(Restrictions.eq(AGS_DOT+MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.lt(HRS_DOT+MptHorarioSessao.Fields.DATA_INICIO.toString(), new Date()));
		criteria.add(Restrictions.eq("SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SSO));
		
		return executeCriteriaExists(criteria);
	}
	
	//C13 Implementação padrão
	public List<AlterarHorariosVO> pesquisarReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Integer codigoPaciente) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");

			// Aliases padrão da C13
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

			// Projections
			ProjectionList pList = Projections.projectionList();
			pList.add(Projections.groupProperty("HRS." + MptHorarioSessao.Fields.CICLO.toString()), AlterarHorariosVO.Fields.CICLO_RESERVA.toString());
			pList.add(Projections.groupProperty("SER." + RapServidores.Fields.MATRICULA.toString()), AlterarHorariosVO.Fields.SER_MATRICULA.toString());
			pList.add(Projections.groupProperty("SER." + RapServidores.Fields.VIN_CODIGO.toString()), AlterarHorariosVO.Fields.SER_VIN_CODIGO.toString());
			pList.add(Projections.groupProperty("PES." + RapPessoasFisicas.Fields.NOME.toString()), AlterarHorariosVO.Fields.RESPONSAVEL_1.toString());
			pList.add(Projections.groupProperty("AGS." + MptAgendamentoSessao.Fields.SEQ.toString()), AlterarHorariosVO.Fields.SEQ_AGENDAMENTO.toString());
			pList.add(Projections.groupProperty("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()));
			criteria.setProjection(Projections.distinct(pList)); // DISTINCT
			
			
			// Condicionais
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), codigoPaciente));
			
			criteria.setResultTransformer(Transformers.aliasToBean(AlterarHorariosVO.class));
			criteria.addOrder(Order.desc("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()));
			
		return executeCriteria(criteria);
	}
	
	//C13 Verificar existencia 50762
	public Boolean existeReservasPrescricoesPacienteManutencaoAgendamentoSessaoTerapeutica(Short seqAtd, Integer codigoPaciente) {
			DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");

			// Aliases padrão da C13
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

			// Projections
			ProjectionList pList = Projections.projectionList();
			pList.add(Projections.groupProperty("HRS." + MptHorarioSessao.Fields.CICLO.toString()), AlterarHorariosVO.Fields.CICLO_RESERVA.toString());
			pList.add(Projections.groupProperty("SER." + RapServidores.Fields.MATRICULA.toString()), AlterarHorariosVO.Fields.SER_MATRICULA.toString());
			pList.add(Projections.groupProperty("SER." + RapServidores.Fields.VIN_CODIGO.toString()), AlterarHorariosVO.Fields.SER_VIN_CODIGO.toString());
			pList.add(Projections.groupProperty("PES." + RapPessoasFisicas.Fields.NOME.toString()), AlterarHorariosVO.Fields.RESPONSAVEL_1.toString());
			pList.add(Projections.groupProperty("AGS." + MptAgendamentoSessao.Fields.SEQ.toString()), AlterarHorariosVO.Fields.SEQ_AGENDAMENTO.toString());
			pList.add(Projections.groupProperty("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()));
			criteria.setProjection(Projections.distinct(pList)); // DISTINCT
			
			
			// Condicionais
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), codigoPaciente));
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.SEQ.toString(), seqAtd));
			
			criteria.setResultTransformer(Transformers.aliasToBean(AlterarHorariosVO.class));
			criteria.addOrder(Order.desc("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()));
			
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * Consulta as Reservas (C8 e C9).
	 * @param codigoPaciente
	 * @return List<ReservasVO>
	 */
	public List<ReservasVO> pesquisarConsultasReservas(Integer codigoPaciente, Integer consulta) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptAgendamentoSessao.class, "AGS");
				
		// Projections
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.SEQ.toString()), ReservasVO.Fields.SEQ.toString());
		pList.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), ReservasVO.Fields.SER_MATRICULA.toString());
		pList.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), ReservasVO.Fields.SER_VIN_CODIGO.toString());
		pList.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ReservasVO.Fields.RESPONSAVEL.toString());
		pList.add(Projections.property("TPS." + MptTipoSessao.Fields.DESCRICAO.toString()), ReservasVO.Fields.DESC_SESSAO.toString());
		pList.add(Projections.property("SAL." + MptSalas.Fields.DESCRICAO.toString()), ReservasVO.Fields.DESC_SALA.toString());
		pList.add(Projections.property("TPS." + MptTipoSessao.Fields.AVISO.toString()), ReservasVO.Fields.AVISO.toString());
		pList.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), ReservasVO.Fields.NOME.toString());
		pList.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), ReservasVO.Fields.PRONTUARIO.toString());
		pList.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()), ReservasVO.Fields.CRIADO_EM.toString());
		pList.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()), ReservasVO.Fields.CRIADO_EM.toString());
		pList.add(Projections.property("PTA." + MpaProtocoloAssistencial.Fields.TITULO.toString()), ReservasVO.Fields.TITULO.toString());
		
		if (consulta.equals(9)){
			pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DIA.toString()), ReservasVO.Fields.DIA.toString());
			pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()), ReservasVO.Fields.DATA_INICIO.toString());
			pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString()), ReservasVO.Fields.DATA_FIM.toString());
			pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.CICLO.toString()), ReservasVO.Fields.CICLO.toString());			
		}		
		criteria.setProjection(Projections.distinct(pList)); // DISTINCT
				
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		if (consulta.equals(9)){
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.HORARIOS_SESSAO.toString(), "HRS", JoinType.INNER_JOIN);			
		}
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.MPA_VERSAO_PROT_ASSISTENCIAL.toString(), "VPA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("VPA." + MpaVersaoProtAssistencial.Fields.MPA_PROTOCOLO_ASSISTENCIAIS.toString(), "PTA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.addOrder(Order.asc("AGS." + MptAgendamentoSessao.Fields.CRIADO_EM.toString()));

		criteria.setResultTransformer(Transformers.aliasToBean(ReservasVO.class));
		return executeCriteria(criteria);
	}
}