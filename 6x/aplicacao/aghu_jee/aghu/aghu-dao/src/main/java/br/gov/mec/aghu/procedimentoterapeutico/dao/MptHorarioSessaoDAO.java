package br.gov.mec.aghu.procedimentoterapeutico.dao;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import org.apache.commons.lang3.time.DateUtils;
import org.hibernate.Hibernate;
import org.hibernate.Query;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.LongType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoHorarioSessao;
import br.gov.mec.aghu.dominio.DominioSituacaoSessao;
import br.gov.mec.aghu.dominio.DominioTipoAcomodacao;
import br.gov.mec.aghu.dominio.DominioTipoLocal;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContaApac;
import br.gov.mec.aghu.model.MciNotificacaoGmr;
import br.gov.mec.aghu.model.MpaProtocoloAssistencial;
import br.gov.mec.aghu.model.MpaVersaoProtAssistencial;
import br.gov.mec.aghu.model.MptAgendamentoSessao;
import br.gov.mec.aghu.model.MptExtratoSessao;
import br.gov.mec.aghu.model.MptHorarioSessao;
import br.gov.mec.aghu.model.MptLocalAtendimento;
import br.gov.mec.aghu.model.MptPrescricaoCiclo;
import br.gov.mec.aghu.model.MptPrescricaoPaciente;
import br.gov.mec.aghu.model.MptProtocoloCiclo;
import br.gov.mec.aghu.model.MptSalas;
import br.gov.mec.aghu.model.MptSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.model.MptTurnoTipoSessao;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentoSessaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.AgendamentosPacienteVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ConsultaDadosAPACVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.DiasAgendadosVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ImpressaoTicketAgendamentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAgendadoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteEmAtendimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.MapaDisponibilidadeVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteAcolhimentoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.PacienteConcluidoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.TaxaOcupacaoVO;
import br.gov.mec.aghu.procedimentoterapeutico.vo.ReservasVO;
import br.gov.mec.aghu.core.utils.DateUtil;

public class MptHorarioSessaoDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<MptHorarioSessao> {
	private static final String PARENTESE = ")";
	private static final long serialVersionUID = 7887694433254251380L;

	public List<AgendamentoSessaoVO> obterListaHorariosSessaoMarcados(Short salaSeq, Short tpsSeq, Short loaSeq, DominioTipoLocal acomodacao,
			Date dataInicio, Date dataFim) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "LOA");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL");
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("LOA." + MptLocalAtendimento.Fields.SEQ.toString())
						, AgendamentoSessaoVO.Fields.LOA_SEQ.toString())
				.add(Projections.property("SAL." + MptSalas.Fields.DESCRICAO.toString())
						, AgendamentoSessaoVO.Fields.DESCRICAO_SALA.toString())
				.add(Projections.property("LOA." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString())
						, AgendamentoSessaoVO.Fields.TIPO_LOCAL.toString())
				.add(Projections.property("LOA." + MptLocalAtendimento.Fields.DESCRICAO.toString())
						, AgendamentoSessaoVO.Fields.DESCRICAO_LOCAL.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString())
						, AgendamentoSessaoVO.Fields.DATA_INICIO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString())
						, AgendamentoSessaoVO.Fields.DATA_FIM.toString()));
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), salaSeq));
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.SEQ.toString(), loaSeq));
		if (!acomodacao.equals(DominioTipoLocal.T)) {
			criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), acomodacao));
		}
		criteria.add(Restrictions.ge("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), dataInicio));
		criteria.add(Restrictions.le("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), dataFim));
		criteria.addOrder(Order.asc("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()))
			.addOrder(Order.asc("LOA." + MptLocalAtendimento.Fields.SEQ.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AgendamentoSessaoVO.class));
		return executeCriteria(criteria);
	}	
	public boolean possuiHorarioAgendado(Short salaSeq, Short tpsSeq, Short loaSeq, DominioTipoLocal acomodacao,
			Date dataInicio, Date dataFim) {		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "LOA");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL");
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), salaSeq));
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TPS_SEQ.toString(), tpsSeq));
		criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.SEQ.toString(), loaSeq));
		if (!acomodacao.equals(DominioTipoLocal.T)) {
			criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), acomodacao));
		}
		criteria.add(Restrictions.le("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), dataInicio));
		Criterion crit1 = Restrictions.gt("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), dataInicio);
		Criterion crit2 = Restrictions.le("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), dataFim);
		Criterion crit3 = Restrictions.ge("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), dataFim);
		criteria.add(Restrictions.or(Restrictions.and(crit1, crit2), crit3));
		return executeCriteriaExists(criteria);
	}	
	public boolean verificarExisteRegistroHorarioSessaoParaLocalAtendimento(Short loaSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class);
		criteria.setProjection(Projections.property(MptHorarioSessao.Fields.SEQ.toString()));
		if(loaSeq != null){
			criteria.add(Restrictions.eq(MptHorarioSessao.Fields.LOA_SEQ.toString(), loaSeq));	
		}
		return executeCriteriaExists(criteria);
	}	
	/**
	 * #41730 - C1 - Consulta dias do Mapa
	 */
	public List<String> consultarDiasMapa(Short salSeq, Date dataInicio, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN,
				Restrictions.eqProperty("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL." + MptSalas.Fields.SEQ.toString()));
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.sqlProjection(" TO_CHAR({alias}.data_inicio, 'DD/MM/YYYY') AS dataInicio ",
				new String[]{MapaDisponibilidadeVO.Fields.DATA_INICIO.toString()}, new Type[]{StringType.INSTANCE}),
				MapaDisponibilidadeVO.Fields.DATA_INICIO.toString());
		criteria.setProjection(Projections.distinct(projection));
		if (salSeq != null) {
			criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), salSeq));
		}
		if (dataInicio != null) {
			Date dataFim = DateUtil.obterUltimoDiaDoMes(dataInicio);
			criteria.add(Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString(), 
					DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataFim)));
			criteria.add(Restrictions.between("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString(), 
					DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataFim)));
		}
		if (turno != null) {
			criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.TURNO.toString(), turno));
		}
		criteria.addOrder(Order.asc(MapaDisponibilidadeVO.Fields.DATA_INICIO.toString()));
		return executeCriteria(criteria);
	}	
	/**
	 * #41730 - C5 - Horarios agendados no dia 
	 */
	public List<MptHorarioSessao> obterHorariosAgendadosNoDia(Short salaSeq, Date dataInicio, DominioTurno turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN,
				Restrictions.eqProperty("AGS." + MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL." + MptSalas.Fields.SEQ.toString()));
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
		criteria.addOrder(Order.asc("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()));
		return executeCriteria(criteria);
	}	
	/**
	 * Consulta principal, lista de pacientes agendados. ()
	 * @param parametro
	 * @param codigoSala
	 * @return List<MptHorarioSessao>
	 */
	public List<ListaPacienteAgendadoVO> pesquisarListaPacientes(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico) {
		DetachedCriteria criteria = montarCriteriaListaPacientes(dataInicio,horario, tipoSessao, sala, 
				acomodacao, tipoAcomodacao, mpaProtocoloAssistencial, valorNumerico);
		criteria.addOrder(Order.asc("HOR." + MptHorarioSessao.Fields.DATA_INICIO.toString()));
		return executeCriteria(criteria);
	}	
	/**
	 * Consulta principal, lista de pacientes agendados. ()
	 * @param parametro
	 * @param codigoSala
	 * @return Long
	 */
	public Long pesquisarListaPacientesCount(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico) {
		DetachedCriteria criteria = montarCriteriaListaPacientes(dataInicio,horario, tipoSessao, sala, 
				acomodacao, tipoAcomodacao, mpaProtocoloAssistencial, valorNumerico);
		return executeCriteriaCount(criteria);
	}
	/**
	 * Consulta principal, lista de pacientes agendados. ()
	 * @param parametro
	 * @param codigoSala
	 * @return DetachedCriteria
	 */
	public DetachedCriteria montarCriteriaListaPacientes(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico){		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HOR");
		Date dataFinal = new Date();
		Date dataInicial = DateUtils.addDays(dataFinal, -valorNumerico.intValue());
		StringBuilder countPrimeiraConsulta = new StringBuilder(500);
		countPrimeiraConsulta.append(" (SELECT COUNT(*) FROM AGH.MPT_HORARIO_SESSAO CHS ");
		countPrimeiraConsulta.append("    INNER JOIN AGH.MPT_AGENDAMENTO_SESSAO CAG ON CAG.SEQ = CHS.AGS_SEQ ");
		countPrimeiraConsulta.append("    WHERE CAG.PAC_CODIGO = PAC2_.CODIGO ");
		countPrimeiraConsulta.append("    AND TO_CHAR(CHS.DATA_INICIO, 'YYYYMMDD') BETWEEN "+"'"+ DateUtil.obterDataFormatada(dataInicial, "YYYYMMdd") +"'"+" AND  "+"'"+ DateUtil.obterDataFormatada(dataFinal, "YYYYMMdd") +"'"+") AS primeiraconsulta ");		
		StringBuilder countSituacaoHorario = new StringBuilder(500);
		countSituacaoHorario.append(" (SELECT COUNT(*) FROM AGH.MCI_NOTIFICACAO_GMR ");
		countSituacaoHorario.append("    WHERE PAC_CODIGO = PAC2_.CODIGO AND IND_NOTIFICACAO_ATIVA = 'S') AS indgmr ");
		StringBuilder obterApacNumDtFim = new StringBuilder(1000);
		obterApacNumDtFim.append(" (SELECT AAP.NUMERO ||'-'|| TO_CHAR(AAP.DT_FIM, 'DD/MM/YYYY') ||'-'|| count(*) FROM AGH.AAC_ATENDIMENTO_APACS AAP INNER JOIN AGH.AGH_ATENDIMENTOS ATD ON ATD.SEQ = AAP.ATD_SEQ");
		obterApacNumDtFim.append(" INNER JOIN AGH.MPT_PRESCRICAO_CICLO PCI ON PCI.ATD_SEQ = ATD.SEQ");
		obterApacNumDtFim.append(" INNER JOIN AGH.MPT_SESSAO SES ON SES.CLO_SEQ = PCI.SEQ");
		obterApacNumDtFim.append(" INNER JOIN AGH.MPT_HORARIO_SESSAO MHS ON MHS.SES_SEQ = SES.SEQ");
		obterApacNumDtFim.append(" WHERE MHS.SEQ = this_.SEQ AND AAP.DT_INICIO = ");
		obterApacNumDtFim.append(" (SELECT MAX(AAP.DT_INICIO) FROM AGH.AAC_ATENDIMENTO_APACS AAP INNER JOIN AGH.AGH_ATENDIMENTOS ATD ON ATD.SEQ = AAP.ATD_SEQ");
		obterApacNumDtFim.append(" INNER JOIN AGH.MPT_PRESCRICAO_CICLO PCI ON PCI.ATD_SEQ = ATD.SEQ");
		obterApacNumDtFim.append(" INNER JOIN AGH.MPT_SESSAO SES ON SES.CLO_SEQ = PCI.SEQ");
		obterApacNumDtFim.append(" INNER JOIN AGH.MPT_HORARIO_SESSAO MHS ON MHS.SES_SEQ = SES.SEQ");
		obterApacNumDtFim.append(" WHERE MHS.SEQ = this_.SEQ) GROUP BY AAP.NUMERO, AAP.DT_FIM) AS apacDataFimNumero");
		ProjectionList projectionList = Projections.projectionList();
		projectionList			
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.SESSAO_CODIGO.toString()), ListaPacienteAgendadoVO.Fields.CODIGO.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.DATA_INICIO.toString()), ListaPacienteAgendadoVO.Fields.DATA.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.DATA_INICIO.toString()), ListaPacienteAgendadoVO.Fields.HORA.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), ListaPacienteAgendadoVO.Fields.PACIENTE.toString())
		.add(Projections.property("SES." + MptSessao.Fields.IND_SITUACAOPRESCRICAOSESSAO.toString()), ListaPacienteAgendadoVO.Fields.SITUACAO_LM.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO), ListaPacienteAgendadoVO.Fields.COD_PACIENTE.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), ListaPacienteAgendadoVO.Fields.DATANASCIMENTO.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), ListaPacienteAgendadoVO.Fields.PRONTUARIO.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.TEMPO.toString()), ListaPacienteAgendadoVO.Fields.TEMPO.toString())
		.add(Projections.property("ACO." + MptLocalAtendimento.Fields.DESCRICAO.toString()), ListaPacienteAgendadoVO.Fields.ACOMODACAO.toString())
		.add(Projections.property("SES." + MptSessao.Fields.DTHR_CHEGADA.toString()), ListaPacienteAgendadoVO.Fields.CHEGADA.toString())
		.add(Projections.property("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString()), ListaPacienteAgendadoVO.Fields.SITUACAO_SESSAO.toString())
		.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString()), ListaPacienteAgendadoVO.Fields.CICLO.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.IND_SITUACAO.toString()), ListaPacienteAgendadoVO.Fields.SITUACAO_HORARIO.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.SEQ.toString()), ListaPacienteAgendadoVO.Fields.HORARIO_SESSAO_SEQ.toString())
		.add(Projections.property("AGE." + MptAgendamentoSessao.Fields.SEQ.toString()), ListaPacienteAgendadoVO.Fields.AGENDAMENTO_SEQ.toString())
		.add(Projections.sqlProjection(countSituacaoHorario.toString(), new String [] {"indgmr"}, new Type[] {new IntegerType()}))
		.add(Projections.sqlProjection(countPrimeiraConsulta.toString(), new String [] {"primeiraconsulta"}, new Type[] {new IntegerType()}))
		.add(Projections.sqlProjection(obterApacNumDtFim.toString(), new String [] {"apacDataFimNumero"}, new Type[] {new StringType()}));
		
		criteria.createAlias("HOR."+MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGE", JoinType.INNER_JOIN);
		criteria.createAlias("AGE."+MptAgendamentoSessao.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("AGE."+MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.createAlias("HOR."+MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "ACO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("AGE."+MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TIP", JoinType.INNER_JOIN);
		//criteria.createAlias("TIP."+MptTipoSessao.Fields.TURNO_TIPO_SESSAO.toString(), "TUR", JoinType.INNER_JOIN);
		criteria.createAlias("HOR."+MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SES."+MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "PEC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PEC."+MptPrescricaoCiclo.Fields.PROTOCOLO_CICLO.toString(), "PRC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.ne("HOR."+MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.C));
		
		criteria.add(Restrictions.or(
				Restrictions.ne("SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SNC), 
				Restrictions.isNull("SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString()))
				);
		
//		criteria.add(Restrictions.sqlRestriction("(TO_CHAR(THIS_.DATA_INICIO, 'HH24:MI') BETWEEN TO_CHAR(TUR6_.HR_INICIO, 'HH24:MI') AND TO_CHAR(TUR6_.HR_FIM, 'HH24:MI'))"));
		if (dataInicio  != null) {			 
			 String formataData = " (TO_CHAR(THIS_.DATA_INICIO, 'DD/MM/YYYY') = ?) ";	
			criteria.add(Restrictions.sqlRestriction(formataData, DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY") , StandardBasicTypes.STRING));	
			//#41735
			StringBuilder getControleFrequencia = new StringBuilder(500);
			getControleFrequencia.append("(SELECT IND_SITUACAO FROM AGH.MPT_CONTROLE_FREQUENCIA MCF ");
			getControleFrequencia.append(" WHERE MCF.SEQ= (SELECT MAX(SEQ) FROM AGH.MPT_CONTROLE_FREQUENCIA WHERE PAC_CODIGO=PAC2_.CODIGO AND MES_REFERENCIA= ");
			getControleFrequencia.append(DateUtil.obterDataFormatada(dataInicio, "MM"));
			getControleFrequencia.append(" AND ANO_REFERENCIA="+DateUtil.obterDataFormatada(dataInicio, "YYYY") +" )) AS controleFreqSituacao");
			projectionList.add(Projections.sqlProjection(getControleFrequencia.toString(), new String [] {"controleFreqSituacao"}, new Type[] {new StringType()}));
		}
		criteria.setProjection(projectionList);
		if (horario  != null) {
			String horaFormatada = " (TO_CHAR(THIS_.DATA_INICIO, 'HH24:MI')) ";
			criteria.add(Restrictions.sqlRestriction(horaFormatada + " BETWEEN '" + DateUtil.obterDataFormatada(horario.getHoraInicio(), "HH:mm") + "' AND '" + DateUtil.obterDataFormatada(horario.getHoraFim(), "HH:mm") + "'"));			
		}
		if (tipoSessao  != null) {
			criteria.add(Restrictions.eq("TIP." + MptTipoSessao.Fields.SEQ.toString(), tipoSessao));			
		}
		if (sala  != null) {
			criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), sala));			
		}
		if (acomodacao  != null && acomodacao.getSeq() != null) {
			criteria.add(Restrictions.eq("ACO." + MptLocalAtendimento.Fields.SEQ.toString(), acomodacao.getSeq()));			
		}
		if (tipoAcomodacao  != null) {
			criteria.add(Restrictions.eq("ACO." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), tipoAcomodacao));			
		}
		if(mpaProtocoloAssistencial != null){
			criteria.add(Restrictions.eq("PRC." + MptProtocoloCiclo.Fields.VPATPASEQP.toString(), mpaProtocoloAssistencial.getSeq()));
			List<Short> tpaSeqList = new ArrayList<Short>();
			Hibernate.initialize(mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales());
			for (MpaVersaoProtAssistencial item : mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales()) {
				tpaSeqList.add(item.getId().getSeqp());
			}
			if (!tpaSeqList.isEmpty()) {
				criteria.add(Restrictions.in("PRC." + MptProtocoloCiclo.Fields.VPASEQP.toString(), tpaSeqList));
			}
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ListaPacienteAgendadoVO.class));
		return criteria;
	}
	@Override
	public void persistir(MptHorarioSessao entity) {
		MptHorarioSessao horarioSessao = entity;
		super.persistir(horarioSessao);
	}	
	public List<DiasAgendadosVO> pesquisarDiasPrescricaoManutencaoAgendamentoSessaoTerapeutica(final Short agsSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class);
		// Aliases padrão da C7
		criteria.createAlias(MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.createAlias(MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "LOA", JoinType.INNER_JOIN);
		// Aliases adicionais
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.LEFT_OUTER_JOIN);
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property(MptHorarioSessao.Fields.DIA.toString()), DiasAgendadosVO.Fields.DIA.toString());
		pList.add(Projections.property(MptHorarioSessao.Fields.TEMPO.toString()), DiasAgendadosVO.Fields.TEMPO.toString());
		pList.add(Projections.property(MptHorarioSessao.Fields.DATA_INICIO.toString()), DiasAgendadosVO.Fields.DATA_INICIO.toString());
		pList.add(Projections.property(MptHorarioSessao.Fields.DATA_FIM.toString()), DiasAgendadosVO.Fields.DATA_FIM.toString());
		pList.add(Projections.property("SAL." + MptSalas.Fields.DESCRICAO.toString()), DiasAgendadosVO.Fields.SALA.toString());
		pList.add(Projections.property("LOA." + MptLocalAtendimento.Fields.DESCRICAO.toString()), DiasAgendadosVO.Fields.ACOMODACAO.toString());
		pList.add(Projections.property(MptHorarioSessao.Fields.SES_SEQ.toString()), DiasAgendadosVO.Fields.SES_SEQ.toString());
		pList.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.SEQ.toString()), DiasAgendadosVO.Fields.SEQ_AGENDAMENTO.toString());
		pList.add(Projections.property("SAL." + MptSalas.Fields.SEQ.toString()), DiasAgendadosVO.Fields.SEQ_SALA.toString());
		pList.add(Projections.property("LOA." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString()), DiasAgendadosVO.Fields.TIPO_LOCAL.toString());
		pList.add(Projections.property("LOA." + MptLocalAtendimento.Fields.SEQ.toString()), DiasAgendadosVO.Fields.SEQ_ACOMODACAO.toString());
		pList.add(Projections.property(MptHorarioSessao.Fields.SEQ.toString()), DiasAgendadosVO.Fields.SEQ_HORARIO.toString());
		pList.add(Projections.property(MptHorarioSessao.Fields.CICLO.toString()), DiasAgendadosVO.Fields.SEQ_CICLO.toString());
		pList.add(Projections.property("TPS." + MptTipoSessao.Fields.SEQ.toString()), DiasAgendadosVO.Fields.TPS_SEQ.toString());
		criteria.setProjection(Projections.distinct(pList)); // DISTINCT

		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.SEQ.toString(), agsSeq));
		criteria.add(Restrictions.or(Restrictions.eq(MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M), Restrictions.eq(MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.R)));
		criteria.addOrder(Order.asc(MptHorarioSessao.Fields.DIA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(DiasAgendadosVO.class));
		return executeCriteria(criteria);
	}

	public List<MptHorarioSessao> pesquisarAcomodacoesAtivasManutencaoAgendamentoSessaoTerapeutica(final Short sala, final Short tipoSessao, final DominioTipoAcomodacao acomodacao, final Short seqAcomodacao, final Date dataInicial, final Date dataFinal) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class);
		criteria.createAlias(MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.createAlias(MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "LOA", JoinType.INNER_JOIN);
		// Alias adicionais
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), sala));
		criteria.add(Restrictions.eq("TPS." + MptTipoSessao.Fields.SEQ.toString(), tipoSessao));
		criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), acomodacao));
		criteria.add(Restrictions.eq("LOA." + MptLocalAtendimento.Fields.SEQ.toString(), seqAcomodacao));
		// TODO REVISAR COM O ANALISTA
		criteria.add(Restrictions.ge(MptHorarioSessao.Fields.DATA_INICIO.toString(), dataInicial));
		criteria.add(Restrictions.ge(MptHorarioSessao.Fields.DATA_FIM.toString(), dataFinal));
		criteria.add(Restrictions.eq(MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M)); // Marcado
		criteria.addOrder(Order.asc(MptHorarioSessao.Fields.DATA_INICIO.toString()));
		criteria.addOrder(Order.asc("LOA." + MptLocalAtendimento.Fields.SEQ.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * #42292 - C1 - Consulta carrega todos os registro de horários de sessão por paciente com prescrição.
	 * 	 //   - C4 - Consulta carrega todos os registro de horários de sessão por paciente com prescrição, filtrados por ciclo
	 * 	 //   - C6 - Consulta carrega dias do agendamento
	 * @param codPaciente
	 * @return
	 */
	public List<ImpressaoTicketAgendamentoVO> obterHorariosSessaoPorPacientePrescricao(Integer codPaciente, List<Integer> codCiclo, Integer consulta) {
		DetachedCriteria criteria = montarCriteriaHorarioSessaoPorPacientePrescricao(codPaciente, codCiclo, consulta);
		return executeCriteria(criteria);
	}
	private DetachedCriteria montarCriteriaHorarioSessaoPorPacientePrescricao(
			Integer codPaciente, List<Integer> codCiclo, Integer consulta) {
		//Metodo adaptado para gerar três tipo de consulta C1, C4 e C6
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("HRS." + MptHorarioSessao.Fields.DIA.toString()), ImpressaoTicketAgendamentoVO.Fields.HRS_DIA.toString());
		projection.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()), ImpressaoTicketAgendamentoVO.Fields.HRS_DATA_INICIO.toString());
		if (consulta.equals(1) || consulta.equals(6)) {
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);
		}
		
		if (consulta.equals(1) || consulta.equals(4)) {
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
			projection.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString()), ImpressaoTicketAgendamentoVO.Fields.SES_CLO_SEQ.toString());
			if (codPaciente != null) {
				criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
			}
			if (consulta.equals(1)) {
				projection.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.CICLO.toString()), ImpressaoTicketAgendamentoVO.Fields.CLO_CICLO.toString());
				projection.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString()), ImpressaoTicketAgendamentoVO.Fields.HRS_DATA_FIM.toString());
				criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M));
				String truncarData = isOracle() ? "TRUNC(SYSDATE)" : "DATE_TRUNC('day', now())";
				criteria.add(Restrictions.sqlRestriction(" EXISTS (SELECT 1 FROM AGH.MPT_HORARIO_SESSAO FUTURO WHERE FUTURO.DATA_INICIO >= " + truncarData + " and FUTURO.AGS_SEQ = this_.AGS_SEQ ) "));
				criteria.addOrder(Order.desc("CLO." + MptPrescricaoCiclo.Fields.DTPREVISTA.toString()));
				criteria.addOrder(Order.asc("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()));
			}
		}
		
		if (consulta.equals(6) && codCiclo != null) {
			criteria.add(Restrictions.in("CLO." + MptPrescricaoCiclo.Fields.SEQ.toString(), codCiclo));
		}
		if (consulta.equals(4) && codCiclo != null) {
			criteria.add(Restrictions.in("SES." + MptSessao.Fields.CICLO.toString(), codCiclo));
		}	
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ImpressaoTicketAgendamentoVO.class));
		return criteria;
	}
	
	/**
	 * #42292 - C1 - Consulta carrega todos os registro de horários de sessão por paciente com prescrição Reservas.
	 * 	 //   - C4 - Consulta carrega todos os registro de horários de sessão por paciente com prescrição, filtrados por ciclo Reservas.
	 * 	 //   - C6 - Consulta carrega dias do agendamento Reservas.
	 * @param codPaciente
	 * @return
	 */
	public List<ReservasVO> obterHorariosSessaoPorPacientePrescricaoReservas(Integer codPaciente, List<Integer> codCiclo, Integer consulta) {
		DetachedCriteria criteria = montarCriteriaHorarioSessaoPorPacientePrescricaoReservadas(codPaciente, codCiclo, consulta);
		return executeCriteria(criteria);
	}
	private DetachedCriteria montarCriteriaHorarioSessaoPorPacientePrescricaoReservadas(
			Integer codPaciente, List<Integer> codCiclo, Integer consulta) {
		
		//Metodo adaptado para gerar três tipo de consulta C1, C4 e C6
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		
		ProjectionList projection = Projections.projectionList();
		projection.add(Projections.property("HRS." + MptHorarioSessao.Fields.DIA.toString()), ReservasVO.Fields.HRS_DIA.toString());
		projection.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()), ReservasVO.Fields.HRS_DATA_INICIO.toString());
		if (consulta.equals(1) || consulta.equals(6)) {
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);
		}
		
		if (consulta.equals(1) || consulta.equals(4)) {
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
			
			projection.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString()), ReservasVO.Fields.SES_CLO_SEQ.toString());
			
			if (codPaciente != null) {
				criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
			}
			if (consulta.equals(1)) {
				projection.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.CICLO.toString()), ReservasVO.Fields.CLO_CICLO.toString());
				projection.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString()), ReservasVO.Fields.HRS_DATA_FIM.toString());
				
				criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M));
				String truncarData = isOracle() ? "TRUNC(SYSDATE)" : "DATE_TRUNC('day', now())";
				
				criteria.add(Restrictions.sqlRestriction(" EXISTS (SELECT 1 FROM AGH.MPT_HORARIO_SESSAO FUTURO WHERE FUTURO.DATA_INICIO >= " + truncarData + " and FUTURO.AGS_SEQ = this_.AGS_SEQ ) "));
				
				criteria.addOrder(Order.desc("CLO." + MptPrescricaoCiclo.Fields.DTPREVISTA.toString()));
				criteria.addOrder(Order.asc("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()));
			}
		}
		
		if (consulta.equals(6) && codCiclo != null) {
			criteria.add(Restrictions.in("CLO." + MptPrescricaoCiclo.Fields.SEQ.toString(), codCiclo));
		}
		if (consulta.equals(4) && codCiclo != null) {
			criteria.add(Restrictions.in("SES." + MptSessao.Fields.CICLO.toString(), codCiclo));
		}	
		
		criteria.setProjection(projection);
		criteria.setResultTransformer(Transformers.aliasToBean(ReservasVO.class));
		return criteria;
	}
	

	/**
	 * #42292 - C2 - Carrega  todos os ciclos para o paciente informado
	 *          C5 - Carrega informações para impressão do ticket
	 * @param codPaciente
	 * @return
	 */
	public List<ImpressaoTicketAgendamentoVO> obterTodosCiclosParaPacienteInformado(Integer codPaciente, List<Integer> codCiclo, Integer consulta) {
		DetachedCriteria criteria = montarCriteriaTodosCiclosParaPacienteInformado(codPaciente, codCiclo, consulta);
		return executeCriteria(criteria);
	}
	private DetachedCriteria montarCriteriaTodosCiclosParaPacienteInformado(Integer codPaciente, List<Integer> codCiclo, Integer consulta) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		if (consulta.equals(2)) {
			criteria.createAlias("SES." + MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);
			criteria.createAlias("CLO." + MptPrescricaoCiclo.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.INNER_JOIN);
			criteria.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.SEQ.toString()), ImpressaoTicketAgendamentoVO.Fields.CLO_SEQ.toString())
					.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.CICLO.toString()), ImpressaoTicketAgendamentoVO.Fields.CLO_CICLO.toString())
					.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.DTPREVISTA.toString()), ImpressaoTicketAgendamentoVO.Fields.CLO_DATA_PRESVISTA.toString())));
			
			criteria.add(Restrictions.eq("SES." + MptSessao.Fields.TIPO_ATENDIMENTO.toString(), DominioSituacao.A));
			criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M));
			String truncarData = isOracle() ? "TRUNC(SYSDATE)" : "DATE_TRUNC('day', now())";
			criteria.add(Restrictions.sqlRestriction(" EXISTS (SELECT 1 FROM AGH.MPT_HORARIO_SESSAO FUTURO WHERE FUTURO.DATA_INICIO >= " + truncarData + " and FUTURO.AGS_SEQ = AGS3_.SEQ ) "));
			criteria.addOrder(Order.desc("CLO." + MptPrescricaoCiclo.Fields.DTPREVISTA.toString()));
		}
		else if (consulta.equals(5)){
			criteria.createAlias("PTE." + MptPrescricaoPaciente.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TPS", JoinType.INNER_JOIN);
			criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
			criteria.createAlias("ATD." + AghAtendimentos.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
			criteria.setProjection(Projections.distinct(Projections.projectionList()
					.add(Projections.property("TPS." + MptTipoSessao.Fields.DESCRICAO.toString()), ImpressaoTicketAgendamentoVO.Fields.TPS_DESCRICAO.toString())
					.add(Projections.property("SAL." + MptSalas.Fields.DESCRICAO.toString()), ImpressaoTicketAgendamentoVO.Fields.SAL_DESCRICAO.toString())
					.add(Projections.property("PTE." + MptPrescricaoPaciente.Fields.CICLO.toString()), ImpressaoTicketAgendamentoVO.Fields.PTE_CICLO.toString())
					.add(Projections.property("SES." + MptSessao.Fields.CICLO.toString()), ImpressaoTicketAgendamentoVO.Fields.SES_CLO_SEQ.toString())
					.add(Projections.property("TPS." + MptTipoSessao.Fields.AVISO.toString()), ImpressaoTicketAgendamentoVO.Fields.TPS_AVISO.toString())
					.add(Projections.property("ESP." + AghEspecialidades.Fields.SEQ.toString()), ImpressaoTicketAgendamentoVO.Fields.ESP_SEQ.toString())
					.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_REDUZIDO.toString()), ImpressaoTicketAgendamentoVO.Fields.ESP_NOME_REDUZIDO.toString())
					.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.SEQ.toString()), ImpressaoTicketAgendamentoVO.Fields.AGS_SEQ.toString())));
			if (codCiclo != null) {
				criteria.add(Restrictions.in("SES." + MptSessao.Fields.CICLO.toString(), codCiclo));
			}
		}
		if (codPaciente != null) {
			criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.PAC_CODIGO.toString(), codPaciente));
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ImpressaoTicketAgendamentoVO.class));
		return criteria;
	}
	/**
	 * #41729
	 * C1
	 */
	public List<AgendamentosPacienteVO> obterAgendamentosPaciente(Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("SES." + MptSessao.Fields.SEQ.toString()) , AgendamentosPacienteVO.Fields.SEQ_SESSAO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.CICLO.toString()), AgendamentosPacienteVO.Fields.CICLO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DIA.toString()), AgendamentosPacienteVO.Fields.DIA.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.TEMPO.toString()), AgendamentosPacienteVO.Fields.TEMPO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()), AgendamentosPacienteVO.Fields.DATA_INICIO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString()), AgendamentosPacienteVO.Fields.DATA_FIM.toString())
				.add(Projections.property("AGS." + MptAgendamentoSessao.Fields.TURNO.toString()), AgendamentosPacienteVO.Fields.TURNO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString()), AgendamentosPacienteVO.Fields.IND_SITUACAO.toString())
				.add(Projections.property("SES." + MptSessao.Fields.CLO_SEQ.toString()), AgendamentosPacienteVO.Fields.SEQ_CLO.toString())
		);
		criteria.add(Restrictions.eq("AGS." + MptAgendamentoSessao.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.IND_SITUACAO.toString(), DominioSituacaoHorarioSessao.M)); 
		criteria.setResultTransformer(Transformers.aliasToBean(AgendamentosPacienteVO.class));
		return executeCriteria(criteria);
	}
	
	/** #41736-  C2
	 * Consulta para gerar os dados do paciente.
	 */
	private DetachedCriteria montarConsultaDadosAPAC(Date data, Integer horSessaoSeq) {
		Calendar mesAno = Calendar.getInstance(); 
		int seqHora = horSessaoSeq.intValue();
		mesAno.setTime(data);
		int mes =  (mesAno.get(Calendar.MONTH) + 1);
		int ano =  mesAno.get(Calendar.YEAR);
		DetachedCriteria criteria = DetachedCriteria.forClass(FatContaApac.class, "CAP");
		criteria.createAlias("CAP." + FatContaApac.Fields.AAC_ATENDIMENTO_APACS.toString(), "AAP", JoinType.INNER_JOIN);
		criteria.createAlias("AAP." + AacAtendimentoApacs.Fields.ATENDIMENTO.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PESCRICAO_CICLO.toString(), "PCI", JoinType.INNER_JOIN);
		criteria.createAlias("PCI." + MptPrescricaoCiclo.Fields.MPT_SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		criteria.createAlias("SES." + MptSessao.Fields.HORARIO_SESSAO.toString(), "HOR", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CAP." + FatContaApac.Fields.ATM_CODIGO.toString()) ,ConsultaDadosAPACVO.Fields.ATM_NUMERO.toString())
				.add(Projections.property("CAP." + FatContaApac.Fields.SEQP.toString()) , ConsultaDadosAPACVO.Fields.CAP_SEQ.toString())
				.add(Projections.property("AAP." + AacAtendimentoApacs.Fields.DT_INICIO.toString()), ConsultaDadosAPACVO.Fields.DT_INICIO_TRATAMENTO.toString())				
				.add(Projections.property("CAP." + FatContaApac.Fields.DT_INICIO_VALIDADE.toString()) , ConsultaDadosAPACVO.Fields.DT_INICIO_VALIDADE.toString())
				.add(Projections.property("CAP." + FatContaApac.Fields.DT_FIM_VALIDADE.toString()) , ConsultaDadosAPACVO.Fields.DT_FIM_VALIDADE.toString())
				.add(Projections.property("CAP." + FatContaApac.Fields.CPE_MES.toString()) , ConsultaDadosAPACVO.Fields.MES.toString())
				.add(Projections.property("CAP." + FatContaApac.Fields.CPE_ANO.toString()) , ConsultaDadosAPACVO.Fields.ANO.toString())
				.add(Projections.property("CAP." + FatContaApac.Fields.CICLOS_PREVISTOS.toString()), ConsultaDadosAPACVO.Fields.CICLOS.toString())
		);		
		criteria.add(Restrictions.eq("HOR." + MptHorarioSessao.Fields.SEQ.toString(), (short) seqHora));
		criteria.add(Restrictions.eq("CAP." + FatContaApac.Fields.CPE_MES.toString(),(byte) mes));
		criteria.add(Restrictions.eq("CAP." + FatContaApac.Fields.CPE_ANO.toString(),(short)ano));
		criteria.addOrder(Order.desc("AAP." + AacAtendimentoApacs.Fields.DT_INICIO));		
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaDadosAPACVO.class));
		return criteria;
	}
	
	public ConsultaDadosAPACVO pesquisarAPAC(Date data, Integer horSessaoSeq) {
		DetachedCriteria criteria = montarConsultaDadosAPAC(data,horSessaoSeq);
		return (ConsultaDadosAPACVO) executeCriteriaUniqueResult(criteria);
	}
	/**
	 * Consulta principal, Lista de Pacientes Aguardando Atendimento. (C1)
	 */
	public List<ListaPacienteAguardandoAtendimentoVO> pesquisarListaPacientesAguardandoAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico){
		
		Date dataFinal = new Date();
		Date dataInicial = DateUtils.addDays(dataFinal, -valorNumerico.intValue());
		StringBuffer primeiraconsulta = new StringBuffer(500);		
		primeiraconsulta.append(" (SELECT COUNT(*) ")
		.append("   FROM ").append(MptHorarioSessao.class.getSimpleName()).append(" CHS, ")
		.append(MptAgendamentoSessao.class.getSimpleName()).append(" CAG ")
		.append("   WHERE ").append("   CAG."+MptAgendamentoSessao.Fields.SEQ.toString()+ " = CHS."+MptHorarioSessao.Fields.AGS_SEQ.toString())
		.append("   AND CAG."+MptAgendamentoSessao.Fields.PAC_CODIGO.toString()+ " = PAC."+AipPacientes.Fields.CODIGO.toString())
		.append("   AND CHS."+MptHorarioSessao.Fields.DATA_INICIO.toString() + "  BETWEEN  "+" '"+ DateUtil.obterDataFormatada(dataInicial, "dd/MM/yyyy") +"' "+" AND "+"'"+ DateUtil.obterDataFormatada(dataFinal, "dd/MM/yyyy") +"' "+") AS primeiraconsulta ");
		
		StringBuffer indgmr = new StringBuffer(500);		
		indgmr.append(" (SELECT COUNT(*) ").append("   FROM ").append(MciNotificacaoGmr.class.getSimpleName()).append(" CNG ")
		.append("   WHERE ").append("   CNG."+MciNotificacaoGmr.Fields.PAC_CODIGO.toString()+ " = PAC."+AipPacientes.Fields.CODIGO.toString())
		.append("   AND CNG."+MciNotificacaoGmr.Fields.IND_NOTIFICACAO_ATIVA.toString()+ " = 'S')");
		
		StringBuffer triagem = new StringBuffer(500);		
		triagem.append(" (SELECT MIN(CES.").append(MptExtratoSessao.Fields.CRIADO_EM.toString())
		.append(PARENTESE).append("   FROM ").append(MptExtratoSessao.class.getSimpleName()).append(" CES ")
		.append("   WHERE ").append("   CES."+MptExtratoSessao.Fields.MPTSESSAO_SEQ.toString()+ " = HOR."+MptHorarioSessao.Fields.SES_SEQ.toString())
		.append("   AND CES."+MptExtratoSessao.Fields.IND_SITUACAO.toString()+ " = '" + DominioSituacaoSessao.SAA + "' )");
		
		StringBuffer hql = new StringBuffer(750);		
		hql.append(" SELECT new br.gov.mec.aghu.procedimentoterapeutico.vo.ListaPacienteAguardandoAtendimentoVO (HOR." + MptHorarioSessao.Fields.DATA_INICIO + ", HOR." + MptHorarioSessao.Fields.DATA_INICIO + ", SES." + MptSessao.Fields.DTHR_CHEGADA + " , " +triagem.toString()+" , ")
		   .append("PAC." + AipPacientes.Fields.NOME + ", PAC." + AipPacientes.Fields.SEXO + ", PAC." + AipPacientes.Fields.DATA_NASCIMENTO + ", PAC." + AipPacientes.Fields.PRONTUARIO + ", CLO." + MptPrescricaoCiclo.Fields.CICLO + ", ")
		   .append("CLO." + MptPrescricaoCiclo.Fields.SEQ + ", PES." + RapPessoasFisicas.Fields.NOME + ", SES." + MptSessao.Fields.IND_SITUACAOSESSAO + ", SES." + MptSessao.Fields.IND_SITUACAOADMINISTRACAO + ", SES." + MptSessao.Fields.SEQ + ", ATD." + AghAtendimentos.Fields.SEQ + ", "+primeiraconsulta.toString() + " , ")
		   .append(indgmr.toString()+", SES." + MptSessao.Fields.IND_SITUACAOPRESCRICAOSESSAO +", PAC." + AipPacientes.Fields.CODIGO + ", PAC." + AipPacientes.Fields.CODIGO +") ");
		createFrom(hql);
		createWhere(hql);
		filtros(horario, sala, acomodacao, tipoAcomodacao,	mpaProtocoloAssistencial, hql);
		Query q = createHibernateQuery(hql.toString());
		parametros(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial, q);
		return  q.list();
	}
	private void parametros(Date dataInicio, MptTurnoTipoSessao horario,
			Short tipoSessao, Short sala, MptLocalAtendimento acomodacao,
			DominioTipoAcomodacao tipoAcomodacao,
			MpaProtocoloAssistencial mpaProtocoloAssistencial, Query q) {
		q.setString("dataInicio", DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY"));		
		if (horario != null){
			//q.setMptTurnoTipoSessao("horario", horario);//Rever//		
			q.setString("horarioInicio", DateUtil.obterDataFormatada(horario.getHoraInicio(), "HH:mm"));
			q.setString("horarioFim", DateUtil.obterDataFormatada(horario.getHoraFim(), "HH:mm"));
		}		
		q.setShort("tipoSessao", tipoSessao);
		if (sala != null){
			q.setShort("sala", sala);			
		}
		if (acomodacao != null && acomodacao.getSeq() != null){
			q.setShort("acomodacao", acomodacao.getSeq());			
		}		
		if (tipoAcomodacao != null){
			q.setString("tipoAcomodacao", tipoAcomodacao.toString());			
		}
		if(mpaProtocoloAssistencial != null){			
			q.setShort("mpaProtocoloAssistencial", mpaProtocoloAssistencial.getSeq());
			List<Short> tpaSeqList = new ArrayList<Short>();
			Hibernate.initialize(mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales());
			for (MpaVersaoProtAssistencial item : mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales()) {
				tpaSeqList.add(item.getId().getSeqp());
			}
			if (!tpaSeqList.isEmpty()) {
				q.setParameterList("tpaSeqList", tpaSeqList);
			}
		}
	}
	private void filtros(MptTurnoTipoSessao horario, Short sala,
			MptLocalAtendimento acomodacao,
			DominioTipoAcomodacao tipoAcomodacao,
			MpaProtocoloAssistencial mpaProtocoloAssistencial, StringBuffer hql) {
		if (horario != null){
			   //.append("        AND TUR."+MptTurnoTipoSessao.Fields.TURNO.toString()+" IN ('M', 'T', 'N') ")		   
			   hql.append("        AND (TO_CHAR(HOR."+MptHorarioSessao.Fields.DATA_INICIO.toString()+", 'HH24:MI')) BETWEEN :horarioInicio AND :horarioFim ");			   
		   }
			hql.append("        AND TIP."+MptTipoSessao.Fields.SEQ.toString()+" = :tipoSessao ");
			
		   if (sala != null){
			   hql.append("        AND SAL."+MptSalas.Fields.SEQ.toString()+" = :sala ");			   
		   }		   
		   if (acomodacao != null && acomodacao.getSeq() != null){
			   hql.append("        AND ACO."+MptLocalAtendimento.Fields.SEQ.toString()+" = :acomodacao ");			   
		   }
		   if (tipoAcomodacao != null){
			   hql.append("        AND ACO."+MptLocalAtendimento.Fields.TIPO_LOCAL.toString()+" = :tipoAcomodacao ");			   
		   }
		   if(mpaProtocoloAssistencial != null){
			   hql.append("        AND PRC."+MptProtocoloCiclo.Fields.VPATPASEQP.toString()+" = :mpaProtocoloAssistencial ")
			   .append("        AND PRC."+MptProtocoloCiclo.Fields.VPASEQP.toString()+" IN (:tpaSeqList) ");			   
		   }
	}
	private void createWhere(StringBuffer hql) {
		hql.append("   WHERE ")
		   .append("            HOR."+MptHorarioSessao.Fields.AGS_SEQ.toString()+ " = AGE."+MptAgendamentoSessao.Fields.SEQ.toString())
		   .append("        AND AGE."+MptAgendamentoSessao.Fields.PAC_CODIGO.toString()+" = PAC."+AipPacientes.Fields.CODIGO.toString())
		   .append("        AND AGE."+MptAgendamentoSessao.Fields.SAL_SEQ.toString()+" = SAL."+MptSalas.Fields.SEQ.toString())
		   .append("        AND HOR."+MptHorarioSessao.Fields.LOA_SEQ.toString()+" = ACO."+MptLocalAtendimento.Fields.SEQ.toString())
		   .append("        AND AGE."+MptAgendamentoSessao.Fields.TPS_SEQ.toString()+" = TIP."+MptTipoSessao.Fields.SEQ.toString())
		   //.append("        AND TIP."+MptTipoSessao.Fields.SEQ.toString()+" = TUR."+MptTurnoTipoSessao.Fields.TPS_SEQ.toString())  
		   //.append("        AND TO_CHAR(HOR."+MptHorarioSessao.Fields.DATA_INICIO.toString()+", 'HH24:MI') BETWEEN TO_CHAR(TUR."+MptTurnoTipoSessao.Fields.HR_INICIO.toString()+ ", 'HH24:MI') AND TO_CHAR(TUR."+MptTurnoTipoSessao.Fields.HR_FIM.toString()+ ", 'HH24:MI')") 
		   .append("        AND HOR."+MptHorarioSessao.Fields.SES_SEQ.toString()+" = SES."+MptSessao.Fields.SEQ.toString())
		   .append("        AND SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE_ATD_SEQ.toString()+" = PTE."+MptPrescricaoPaciente.Fields.ATD_SEQ.toString())
		   .append("        AND SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE_SEQ.toString()+" = PTE."+MptPrescricaoPaciente.Fields.SEQ.toString())	   
		   .append("        AND ( (PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()+" IS NULL AND ")
		   .append("         	  SER."+RapServidores.Fields.MATRICULA.toString()+" = PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA.toString())
		   .append("              ) OR (PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString()+" IS NOT NULL AND SER."+RapServidores.Fields.MATRICULA.toString()+" = PTE."+MptPrescricaoPaciente.Fields.SER_MATRICULA_VALIDA.toString())		   
		   .append("              )) ")
		   .append("        AND ( (PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()+" IS NULL AND ")
		   .append("         	  SER."+RapServidores.Fields.CODIGO_VINCULO.toString()+" = PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO.toString())
		   .append("              ) OR ( PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString()+" IS NOT NULL AND SER."+RapServidores.Fields.CODIGO_VINCULO.toString()+" = PTE."+MptPrescricaoPaciente.Fields.SER_VIN_CODIGO_VALIDA.toString())		   
		   .append("              ) )")  
		   .append("        AND PES."+RapPessoasFisicas.Fields.CODIGO.toString()+ " = SER."+RapServidores.Fields.PES_CODIGO)		   
		   .append("        AND CLO."+MptPrescricaoCiclo.Fields.SEQ.toString()+ " = SES."+MptSessao.Fields.CLO_SEQ)		   
		   .append("        AND CLO."+MptPrescricaoCiclo.Fields.ATENDIMENTOS.toString()+" = ATD."+AghAtendimentos.Fields.SEQ.toString())
		   .append("        AND PEC."+MptPrescricaoCiclo.Fields.SEQ.toString()+ " = SES."+MptSessao.Fields.CLO_SEQ)
		   .append("        AND SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString()+" = 'SAA' ") /* SITUACAO AGUARDANDO ATENDIMENTO */
		   .append("        AND TO_CHAR(HOR."+MptHorarioSessao.Fields.DATA_INICIO.toString()+", 'DD/MM/YYYY') = :dataInicio ");
	}
	private void createFrom(StringBuffer hql) {
		hql.append("   FROM ").append(MptHorarioSessao.class.getSimpleName()).append(" HOR, ")
		   .append(MptAgendamentoSessao.class.getSimpleName()).append(" AGE, ")
		   .append(AipPacientes.class.getSimpleName()).append(" PAC, ")
		   .append(MptSalas.class.getSimpleName()).append(" SAL, ") 
		   .append(MptLocalAtendimento.class.getSimpleName()).append(" ACO, ") 
		   .append(MptTipoSessao.class.getSimpleName()).append(" TIP, ")
		   //.append(MptTurnoTipoSessao.class.getSimpleName()).append(" TUR, ")
		   .append(MptSessao.class.getSimpleName()).append(" SES, ")		   
		   .append(MptPrescricaoPaciente.class.getSimpleName()).append(" PTE, ")
		   .append(RapServidores.class.getSimpleName()).append(" SER, ")
		   .append(RapPessoasFisicas.class.getSimpleName()).append(" PES, ")
		   .append(MptPrescricaoCiclo.class.getSimpleName()).append(" CLO, ")
		   .append(AghAtendimentos.class.getSimpleName()).append(" ATD, ")
		   .append(MptPrescricaoCiclo.class.getSimpleName()).append(" PEC LEFT JOIN ")
		   .append("PEC." + MptPrescricaoCiclo.Fields.PROTOCOLO_CICLO.toString() + " PRC ");
	}	
	/**
	 * #46834 - C3
	 */
	public List<TaxaOcupacaoVO> consultarTotalOcupacaoPorDiaPeriodoTipoSessao(Date dataInicio, Date dataFim, String horaInicio, String horaFim, Short tpsSeq, List<Short> sequenciaisSala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS", JoinType.INNER_JOIN);
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN, Restrictions.eqProperty("AGS."+MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL."+MptSalas.Fields.SEQ.toString()));
		criteria.add(Restrictions.eq("SAL."+MptSalas.Fields.TPSSEQ.toString(), tpsSeq));
		if (sequenciaisSala != null && !sequenciaisSala.isEmpty()) {
			criteria.add(Restrictions.in("SAL."+MptSalas.Fields.SEQ.toString(), sequenciaisSala));
		}
		criteria.add(Restrictions.between("HRS."+ MptHorarioSessao.Fields.DATA_INICIO.toString(), DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataFim)));
		if (!horaInicio.isEmpty() && !horaFim.isEmpty()) {
			criteria.add(Restrictions.sqlRestriction("TO_CHAR(this_.DATA_INICIO, 'HH24:MI') BETWEEN ? AND ?", new String[] {horaInicio, horaFim}, new Type[] {new StringType(), new StringType()} ));
		}
		criteria.addOrder(Order.asc("SAL."+MptSalas.Fields.SEQ.toString()));
		criteria.addOrder(Order.asc("HRS."+MptHorarioSessao.Fields.DATA_INICIO.toString()));
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.property("HRS."+MptHorarioSessao.Fields.DATA_INICIO.toString()), TaxaOcupacaoVO.Fields.DATA_INICIO.toString());
		projectionList.add(Projections.property("HRS."+MptHorarioSessao.Fields.DATA_FIM.toString()), TaxaOcupacaoVO.Fields.DATA_FIM.toString());
		projectionList.add(Projections.property("SAL."+MptSalas.Fields.SEQ.toString()), TaxaOcupacaoVO.Fields.SAL_SEQ.toString());
		criteria.setProjection(projectionList);
		criteria.setResultTransformer(Transformers.aliasToBean(TaxaOcupacaoVO.class));
		return executeCriteria(criteria);	
	} 	
	/**
	 * #46834 - C5
	 */
	public List<TaxaOcupacaoVO> consultarTotalOcupacaoSalaPorDiaPeriodoTipoSessao(Date dataInicio, Date dataFim, Short sala, String horaInicio, String horaFim) {
				
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS2");
		String projectionData = "TO_CHAR(this_.DATA_INICIO, 'YYYY-MM-DD') ";
		String projectionContador = "(SELECT COUNT(*) " 
				.concat("FROM AGH.MPT_HORARIO_SESSAO HRS ") 
				.concat("WHERE (TO_CHAR(HRS.DATA_INICIO, 'YYYY-MM-DD') = TO_CHAR(this_.DATA_INICIO, 'YYYY-MM-DD')) ")
				.concat("AND TO_CHAR(this_.DATA_INICIO, 'YYYY-MM-DD') BETWEEN '"+DateUtil.obterDataFormatada(dataInicio, "yyyy-MM-dd")+"' AND '"+DateUtil.obterDataFormatada(dataFim, "yyyy-MM-dd")+"' ");					
		if (!horaInicio.isEmpty() && !horaFim.isEmpty()) {
			projectionContador = projectionContador.concat("AND TO_CHAR(this_.DATA_INICIO, 'HH24:MI') BETWEEN '"+horaInicio+"' AND '"+horaFim+"' ");
		}
		projectionContador = projectionContador.concat(") ");
		ProjectionList projectionList = Projections.projectionList()
				.add(Projections.sqlProjection(projectionData + TaxaOcupacaoVO.Fields.DIA.toString(), new String [] {TaxaOcupacaoVO.Fields.DIA.toString()}, new Type[] {new StringType()}))
				.add(Projections.sqlProjection(projectionContador + " " + TaxaOcupacaoVO.Fields.CONTADOR.toString(), new String [] {TaxaOcupacaoVO.Fields.CONTADOR.toString()}, new Type[] {new LongType()}));
		criteria.setProjection(Projections.distinct(projectionList));	
		criteria.createAlias("HRS2." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.SALA.toString(), "SAL");
		criteria.add(Restrictions.eq("SAL."+MptSalas.Fields.SEQ.toString(), sala));
		criteria.add(Restrictions.sqlRestriction(projectionContador + " > 0"));
		criteria.add(Restrictions.between("HRS2."+ MptHorarioSessao.Fields.DATA_INICIO.toString(), dataInicio, DateUtil.obterDataComHoraFinal(dataFim)));	
		if (!horaInicio.isEmpty() && !horaFim.isEmpty()) {
			criteria.add(Restrictions.sqlRestriction("TO_CHAR(this_.DATA_INICIO, 'HH24:MI') BETWEEN '"+horaInicio+"' AND '"+horaFim+"'"));
		}		
		criteria.setResultTransformer(Transformers.aliasToBean(TaxaOcupacaoVO.class));		
		return executeCriteria(criteria);		
	}	
	/**
	 * #46834 - C8
	 */
	public List<TaxaOcupacaoVO> consultarTotalOcupacaoPoltronaPorDiaPeriodoSala(Date dataInicio, Date dataFim, Short sala, String horaInicio, String horaFim, List<Short> sequenciaisSala) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");		
		criteria.createAlias("HRS." + MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGS");
		criteria.createAlias("AGS." + MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TSE", JoinType.INNER_JOIN);
		criteria.createAlias("TSE." + MptTipoSessao.Fields.SALAS.toString(), "SAL", JoinType.INNER_JOIN, Restrictions.eqProperty("AGS."+MptAgendamentoSessao.Fields.SAL_SEQ.toString(), "SAL."+MptSalas.Fields.SEQ.toString()));
		criteria.createAlias("SAL." + MptSalas.Fields.LOCAIS_ATENDIMENTO.toString(), "LOA");
		criteria.add(Restrictions.eq("SAL."+MptSalas.Fields.SEQ.toString(), sala));
		if (sequenciaisSala != null && !sequenciaisSala.isEmpty()) {
			criteria.add(Restrictions.in("LOA."+MptLocalAtendimento.Fields.SEQ.toString(), sequenciaisSala));
		}
		criteria.add(Restrictions.between("HRS."+ MptHorarioSessao.Fields.DATA_INICIO.toString(), DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataFim)));
		if (!horaInicio.isEmpty() && !horaFim.isEmpty()) {
			criteria.add(Restrictions.sqlRestriction("TO_CHAR(this_.DATA_INICIO, 'HH24:MI') BETWEEN ? AND ?", new String[] {horaInicio, horaFim}, new Type[] {new StringType(), new StringType()}));
		}				
		ProjectionList projectionList = Projections.projectionList()				
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()), TaxaOcupacaoVO.Fields.DATA_INICIO.toString())
				.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString()), TaxaOcupacaoVO.Fields.DATA_FIM.toString())
				.add(Projections.property("LOA."+MptLocalAtendimento.Fields.DESCRICAO.toString()), TaxaOcupacaoVO.Fields.DESCRICAO.toString());
		criteria.setProjection(projectionList);		
		criteria.setResultTransformer(Transformers.aliasToBean(TaxaOcupacaoVO.class));		
		criteria.addOrder(Order.asc("LOA."+MptLocalAtendimento.Fields.DESCRICAO.toString()));
		criteria.addOrder(Order.asc("HRS."+MptHorarioSessao.Fields.DATA_INICIO.toString()));		
		return executeCriteria(criteria);
	}		
	/**
	 * Obtem a lista de pacientes para acolhimento (triagem)
	 * #41706
	 */
	public List<PacienteAcolhimentoVO> obterPacientesParaAcolhimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HOR");
		criteria.createAlias("HOR."+MptHorarioSessao.Fields.AGENDAMENTO_SESSAO.toString(), "AGE", JoinType.INNER_JOIN);
		criteria.createAlias("AGE."+MptAgendamentoSessao.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("AGE."+MptAgendamentoSessao.Fields.SALA.toString(), "SAL", JoinType.INNER_JOIN);
		criteria.createAlias("HOR."+MptHorarioSessao.Fields.LOCAL_ATENDIMENTO.toString(), "ACO", JoinType.INNER_JOIN);
		criteria.createAlias("AGE."+MptAgendamentoSessao.Fields.TIPO_SESSAO.toString(), "TIP", JoinType.INNER_JOIN);
		criteria.createAlias("HOR."+MptHorarioSessao.Fields.SESSAO.toString(), "SES", JoinType.INNER_JOIN);
		criteria.createAlias("SES."+MptSessao.Fields.MPT_PRESCRICAO_PACIENTE.toString(), "PTE", JoinType.INNER_JOIN);
		criteria.createAlias("PTE."+MptPrescricaoPaciente.Fields.SERVIDOR_VALIDO.toString(), "SERVAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERVAL."+RapServidores.Fields.PESSOA_FISICA.toString(), "PESVAL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PTE."+MptPrescricaoPaciente.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER."+RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SES."+MptSessao.Fields.MPT_PRESCRICAO_CICLO.toString(), "CLO", JoinType.INNER_JOIN);
		criteria.createAlias("CLO."+MptPrescricaoCiclo.Fields.ATENDIMENTOS.toString(), "ATD", JoinType.INNER_JOIN);
		criteria.createAlias("CLO."+MptPrescricaoCiclo.Fields.PROTOCOLO_CICLO.toString(), "PRC", JoinType.LEFT_OUTER_JOIN);
		Date dataFinal = new Date();
		Date dataInicial = DateUtils.addDays(dataFinal, -valorNumerico.intValue());
		StringBuilder countPrimeiraConsulta = new StringBuilder(500);
		countPrimeiraConsulta.append(" (SELECT COUNT(*) FROM AGH.MPT_HORARIO_SESSAO CHS ");
		countPrimeiraConsulta.append("    INNER JOIN AGH.MPT_AGENDAMENTO_SESSAO CAG ON CAG.SEQ = CHS.AGS_SEQ ");
		countPrimeiraConsulta.append("    WHERE CAG.PAC_CODIGO = PAC2_.CODIGO ");
		countPrimeiraConsulta.append("    AND TO_CHAR(CHS.DATA_INICIO, 'YYYYMMDD') BETWEEN '"+ DateUtil.obterDataFormatada(dataInicial, "YYYYMMdd") +"' AND '"+ DateUtil.obterDataFormatada(dataFinal, "YYYYMMdd") +"') AS primeiraconsulta ");
		StringBuilder countSituacaoHorario = new StringBuilder(500);
		countSituacaoHorario.append(" (SELECT COUNT(*) FROM AGH.MCI_NOTIFICACAO_GMR ");
		countSituacaoHorario.append("    WHERE PAC_CODIGO = PAC2_.CODIGO AND IND_NOTIFICACAO_ATIVA = 'S') AS indgmr ");		
		criteria.setProjection(Projections.distinct((Projections.projectionList()				
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.SESSAO_CODIGO.toString()), PacienteAcolhimentoVO.Fields.CODIGO.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.DATA_INICIO.toString()), PacienteAcolhimentoVO.Fields.DATA_INICIO.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), PacienteAcolhimentoVO.Fields.PACIENTE.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.SEXO.toString()), PacienteAcolhimentoVO.Fields.SEXO.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()), PacienteAcolhimentoVO.Fields.CODIGO_PACIENTE.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), PacienteAcolhimentoVO.Fields.DATANASCIMENTO.toString())
		.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), PacienteAcolhimentoVO.Fields.PRONTUARIO.toString())
		.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.CICLO.toString()), PacienteAcolhimentoVO.Fields.NUMERO_CICLO.toString())
		.add(Projections.property("CLO." + MptPrescricaoCiclo.Fields.SEQ.toString()), PacienteAcolhimentoVO.Fields.CODIGO_CICLO.toString())
		.add(Projections.property("PESVAL." + RapPessoasFisicas.Fields.NOME.toString()), PacienteAcolhimentoVO.Fields.NOME_SERVIDOR_VALIDA.toString())
		.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), PacienteAcolhimentoVO.Fields.NOME_SERVIDOR.toString())
		.add(Projections.property("SES." + MptSessao.Fields.IND_SITUACAOSESSAO.toString()), PacienteAcolhimentoVO.Fields.SITUACAO_SESSAO.toString())
		.add(Projections.property("SES." + MptSessao.Fields.IND_MEDICAMENTO_DOMICILIAR.toString()), PacienteAcolhimentoVO.Fields.MEDICAMENTO_DOMICILIAR.toString())
		.add(Projections.property("SES." + MptSessao.Fields.IND_IMPRESSAO_PULSEIRA.toString()), PacienteAcolhimentoVO.Fields.IMPRESSAO_PULSEIRA.toString())
		.add(Projections.property("SES." + MptSessao.Fields.IND_SITUACAOADMINISTRACAO.toString()), PacienteAcolhimentoVO.Fields.SITUACAO_ADMINISTRACAO.toString())
		.add(Projections.property("ATD." + AghAtendimentos.Fields.SEQ.toString()), PacienteAcolhimentoVO.Fields.CODIGO_ATENDIMENTO.toString())
		.add(Projections.property("HOR." + MptHorarioSessao.Fields.IND_SITUACAO.toString()), PacienteAcolhimentoVO.Fields.SITUACAO_HORARIO.toString())
		.add(Projections.property("TIP." + MptTipoSessao.Fields.SEQ.toString()), PacienteAcolhimentoVO.Fields.TIPO_SESSAO_SEQ.toString())
		.add(Projections.sqlProjection(countSituacaoHorario.toString(), new String [] {PacienteAcolhimentoVO.Fields.INDGMR.toString()}, new Type[] {new IntegerType()}))
		.add(Projections.sqlProjection(countPrimeiraConsulta.toString(), new String [] {PacienteAcolhimentoVO.Fields.PRIMEIRA_CONSULTA.toString()}, new Type[] {new IntegerType()})))));
		criteria.add(Restrictions.eq("SES."+MptSessao.Fields.IND_SITUACAOSESSAO.toString(), DominioSituacaoSessao.SEA));
		if (dataInicio  != null) {			 
			 String formataData = " (TO_CHAR(THIS_.DATA_INICIO, 'DD/MM/YYYY') = ?) ";			
			criteria.add(Restrictions.sqlRestriction(formataData, DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY") , StandardBasicTypes.STRING));			
		}
		if (horario  != null) {
			String horaFormatada = " (TO_CHAR(THIS_.DATA_INICIO, 'HH24:MI')) ";
			criteria.add(Restrictions.sqlRestriction(horaFormatada + " BETWEEN '" + DateUtil.obterDataFormatada(horario.getHoraInicio(), "HH:mm") + "' AND '" + DateUtil.obterDataFormatada(horario.getHoraFim(), "HH:mm") + "'"));			
		}
		if (tipoSessao  != null) {
			criteria.add(Restrictions.eq("TIP." + MptTipoSessao.Fields.SEQ.toString(), tipoSessao));			
		}
		if (sala  != null) {
			criteria.add(Restrictions.eq("SAL." + MptSalas.Fields.SEQ.toString(), sala));			
		}
		if (acomodacao  != null && acomodacao.getSeq() != null) {
			criteria.add(Restrictions.eq("ACO." + MptLocalAtendimento.Fields.SEQ.toString(), acomodacao.getSeq()));			
		}
		if (tipoAcomodacao  != null) {
			criteria.add(Restrictions.eq("ACO." + MptLocalAtendimento.Fields.TIPO_LOCAL.toString(), tipoAcomodacao));			
		}
		if(mpaProtocoloAssistencial != null){
			criteria.add(Restrictions.eq("PRC." + MptProtocoloCiclo.Fields.VPATPASEQP.toString(), mpaProtocoloAssistencial.getSeq()));
			List<Short> tpaSeqList = new ArrayList<Short>();
			Hibernate.initialize(mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales());
			for (MpaVersaoProtAssistencial item : mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales()) {
				tpaSeqList.add(item.getId().getSeqp());
			}
			if (!tpaSeqList.isEmpty()) {
				criteria.add(Restrictions.in("PRC." + MptProtocoloCiclo.Fields.VPASEQP.toString(), tpaSeqList));
			}
		}
		criteria.setResultTransformer(Transformers.aliasToBean(PacienteAcolhimentoVO.class));
		criteria.addOrder(Order.asc("HOR." + MptHorarioSessao.Fields.DATA_INICIO.toString()));
		return executeCriteria(criteria);
	}	
	/**
	 * Consulta principal, Lista de Pacientes Em Atendimento. (C1)
	 */
	public List<ListaPacienteEmAtendimentoVO> listarPacientesEmAtendimento(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico) {
        GerarListaPacientesEmAtendimentoQueryBuilder builderConsulta = new GerarListaPacientesEmAtendimentoQueryBuilder();
        String retorno = builderConsulta.consultarListaPacientesEmAtendimento(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial, valorNumerico);
        Query q = createHibernateQuery(retorno);
        parametrosEmAtendimento(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial, q);        
        return q.list();
    }	
	private void parametrosEmAtendimento(Date dataInicio, MptTurnoTipoSessao horario,
			Short tipoSessao, Short sala, MptLocalAtendimento acomodacao,
			DominioTipoAcomodacao tipoAcomodacao,
			MpaProtocoloAssistencial mpaProtocoloAssistencial, Query q) {
		q.setString("dataInicio", DateUtil.obterDataFormatada(dataInicio, "dd/MM/YYYY"));
		if (horario != null){
			//q.setMptTurnoTipoSessao("horario", horario);//Rever//		
			q.setString("horarioInicio", DateUtil.obterDataFormatada(horario.getHoraInicio(), "HH:mm"));
			q.setString("horarioFim", DateUtil.obterDataFormatada(horario.getHoraFim(), "HH:mm"));
		}
		q.setShort("tipoSessao", tipoSessao);
		if (sala != null){
			q.setShort("sala", sala);			
		}
		if (acomodacao != null && acomodacao.getSeq() != null){
			q.setShort("acomodacao", acomodacao.getSeq());			
		}
		if (tipoAcomodacao != null){
			q.setString("tipoAcomodacao", tipoAcomodacao.toString());			
		}
		if(mpaProtocoloAssistencial != null){			
			q.setShort("mpaProtocoloAssistencial", mpaProtocoloAssistencial.getSeq());
			List<Short> tpaSeqList = new ArrayList<Short>();
			Hibernate.initialize(mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales());
			for (MpaVersaoProtAssistencial item : mpaProtocoloAssistencial.getMpaVersaoProtAssistenciales()) {
				tpaSeqList.add(item.getId().getSeqp());
			}
			if (!tpaSeqList.isEmpty()) {
				q.setParameterList("tpaSeqList", tpaSeqList);
			}
		}
	}	
	/**
	 * Obtem a lista de pacientes com atendimento concluído
	 * #41709
	 */
	public List<PacienteConcluidoVO> obterPacientesAtendimentoConcluido(Date dataInicio, MptTurnoTipoSessao horario, Short tipoSessao, Short sala, 
			MptLocalAtendimento acomodacao, DominioTipoAcomodacao tipoAcomodacao, MpaProtocoloAssistencial mpaProtocoloAssistencial, BigDecimal valorNumerico) {
		Date dataFinal = new Date();
		Date dataInicial = DateUtils.addDays(dataFinal, -valorNumerico.intValue());	
		PacientesAtendimentoConcluidoQueryBuilder builder = new PacientesAtendimentoConcluidoQueryBuilder();
		DetachedCriteria criteria = builder.build(dataInicio, horario, tipoSessao, sala, acomodacao, tipoAcomodacao, mpaProtocoloAssistencial, dataInicial, dataFinal);
		return executeCriteria(criteria);
	}	
	/*
	 * Consulta C8 46834
	 */
	public TaxaOcupacaoVO obterNomeSala(Short salSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(MptSalas.class);
		criteria.setProjection(Projections.projectionList().add(Projections.property(MptSalas.Fields.DESCRICAO.toString()),(TaxaOcupacaoVO.Fields.NOME_SALA.toString())));
		criteria.add(Restrictions.eq(MptSalas.Fields.SEQ.toString(), salSeq));
		criteria.setResultTransformer(Transformers.aliasToBean(TaxaOcupacaoVO.class));
		return  (TaxaOcupacaoVO) executeCriteriaUniqueResult(criteria);		
	}	
	/**
	 * Consulta dias da Reserva (09).
	 * @param codigoAgsSessao
	 * @return List<MptHorarioSessao>
	 */
	public List<ReservasVO> pesquisarConsultarDiasReservas(Short codigoAgsSessao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(MptHorarioSessao.class, "HRS");		
		ProjectionList pList = Projections.projectionList();
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DIA.toString()), ReservasVO.Fields.DIA.toString());
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()), ReservasVO.Fields.DATA_INICIO.toString());
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.DATA_FIM.toString()), ReservasVO.Fields.DATA_FIM.toString());
		pList.add(Projections.property("HRS." + MptHorarioSessao.Fields.CICLO.toString()), ReservasVO.Fields.CICLO.toString());			
		pList.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ReservasVO.Fields.RESPONSAVEL.toString());
		
		criteria.createAlias("HRS." + MptAgendamentoSessao.Fields.SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);
		criteria.setProjection(pList); 		
		criteria.add(Restrictions.eq("HRS." + MptHorarioSessao.Fields.AGS_SEQ.toString(), codigoAgsSessao));
		criteria.addOrder(Order.asc("HRS." + MptHorarioSessao.Fields.DATA_INICIO.toString()));		
		criteria.setResultTransformer(Transformers.aliasToBean(ReservasVO.class));
		return executeCriteria(criteria);
	}	
}