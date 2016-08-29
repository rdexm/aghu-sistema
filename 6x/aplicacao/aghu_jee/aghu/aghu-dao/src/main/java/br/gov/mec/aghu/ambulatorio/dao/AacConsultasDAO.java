package br.gov.mec.aghu.ambulatorio.dao;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.math.BigDecimal;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.Query;
import javax.persistence.Table;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.SQLQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.jdbc.Work;
import org.hibernate.sql.JoinType;
import org.hibernate.transform.AliasToBeanConstructorResultTransformer;
import org.hibernate.transform.Transformers;
import org.hibernate.type.IntegerType;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.type.StringType;
import org.hibernate.type.TimestampType;
import org.hibernate.type.Type;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.exceptioncode.AGHUBaseBusinessExceptionCode;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.pesquisa.vo.ConsultasAgendaVO;
import br.gov.mec.aghu.ambulatorio.vo.CabecalhoRelatorioAgendaConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaAmbulatorioVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaDisponibilidadeHorarioVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaGuiaAtendimentoUnimedVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasDeOutrosConveniosVO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultasGradeVO;
import br.gov.mec.aghu.ambulatorio.vo.CurEspecialidadeVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.EspecialidadeRelacionadaVO;
import br.gov.mec.aghu.ambulatorio.vo.FiltroGradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.GradeConsultasVO;
import br.gov.mec.aghu.ambulatorio.vo.LaudoSolicitacaoAutorizacaoProcedAmbVO;
import br.gov.mec.aghu.ambulatorio.vo.PesquisarConsultasPendentesVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.persistence.dialect.OrderBySql;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dao.EsquemasOracleEnum;
import br.gov.mec.aghu.dao.ObjetosBancoOracleEnum;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioIndPendenteAmbulatorio;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOrigemConsulta;
import br.gov.mec.aghu.dominio.DominioPrioridadeCid;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoInterconsultas;
import br.gov.mec.aghu.dominio.DominioSituacaoProcedimentoAmbulatorio;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.dominio.StatusPacienteAgendado;
import br.gov.mec.aghu.faturamento.vo.FatConsultaPrhVO;
import br.gov.mec.aghu.faturamento.vo.FatProcedHospInternosVO;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AacAtendimentoApacs;
import br.gov.mec.aghu.model.AacCidAtendimentos;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacConsultaProcedHospitalar;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacGradeSituacao;
import br.gov.mec.aghu.model.AacMotivos;
import br.gov.mec.aghu.model.AacNivelBusca;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacProcedHospEspecialidades;
import br.gov.mec.aghu.model.AacRetornos;
import br.gov.mec.aghu.model.AacSituacaoConsultas;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AacUnidFuncionalSalas;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractEspecialidades;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipEnderecosPacientes;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitantesProntuario;
import br.gov.mec.aghu.model.FatAtendimentoApacProcHosp;
import br.gov.mec.aghu.model.FatCandidatosApacOtorrino;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.FatEspecialidadeTratamento;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatListaPacApac;
import br.gov.mec.aghu.model.FatProcedAmbRealizado;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.FatProcedHospInternosPai;
import br.gov.mec.aghu.model.FatTipoTratamentos;
import br.gov.mec.aghu.model.FatTransplanteEspecialidade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MamAltaSumario;
import br.gov.mec.aghu.model.MamAnamneses;
import br.gov.mec.aghu.model.MamControles;
import br.gov.mec.aghu.model.MamEvolucoes;
import br.gov.mec.aghu.model.MamExtratoControles;
import br.gov.mec.aghu.model.MamSituacaoAtendimentos;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacFormaAgendamentos;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.model.VFatAssociacaoProcedimento;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AltasAmbulatoriasPolVO;
import br.gov.mec.aghu.prescricaomedica.vo.CursorPacVO;

@SuppressWarnings({ "PMD.ExcessiveClassLength", "PMD.NcssTypeCount"})
public class AacConsultasDAO extends br.gov.mec.aghu.core.persistence.dao.BaseDao<AacConsultas> {

	private static final Log LOG = LogFactory.getLog(AacConsultasDAO.class);

	private static final String AND_CONSULTA = " and consulta.";
	private static final String SIT_ANTEND = "sitAntend.";
	private static final String ALIAS_ESPECIALIDADE = "aliasEspecialidade";
	private static final String ALIAS_RETORNO = "aliasRetorno";
	private static final String VUSL = "vusl";
	private static final String ZONA_SALA2 = "zonaSala.";
	private static final String GRD2 = "GRD";
	private static final String GRD = "GRD.";
	private static final String PAC = "PAC";
	private static final String GRADE_ESP2 = "gradeEsp.";
	private static final String GRADE2 = "grade.";
	private static final String ZONA_SALA = "zonaSala";
	private static final String GRADE_ESP = "gradeEsp";
	private static final String GRADE = "grade";
	private static final String UNF = "unf";
	private static final String ESP_ESP = "espEsp";
	private static final String SITUACAO_CONSULTA = "situacaoConsulta";
	private static final String PACIENTE = "paciente";
	private static final long serialVersionUID = 4026598078798815561L;
	private static final String ALIAS_C_PONTO = "C.";

	private static final String GRADE_ESP_GENERICA = "gradeEspGenerica";
	private static final String SERVIDOR_CONSULTA = "servidorConsulta";
	private static final String SERVIDOR_CONSULTA2 = "servidorConsulta.";
	private static final String PESSOA_FISICA_CONSULTA = "pessoaFisicaConsulta";
	@Inject
    private IParametroFacade parametroFacade;

	public AacConsultas obterConsulta(Integer numero) {
		return this.obterPorChavePrimaria(numero);
	}
	
	public AacConsultas obterAacConsultasJoinGradeEEspecialidade(final Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), GRADE_ESP);
		criteria.createAlias(GRADE_ESP2 + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), ESP_ESP, JoinType.LEFT_OUTER_JOIN);
	
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}	
	
	/**
	 * TODO PENDÊNCIA ARQUITETURA - REMOVER APÓS MIGRAÇÃO
	 * @param numero
	 * @return
	 */
	public AacConsultas obterAacConsultasAtenderPacientesAgendados(final Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), PACIENTE);
		criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), SITUACAO_CONSULTA);
		criteria.createAlias(AacConsultas.Fields.CONVENIO_SAUDE_PLANO.toString(), "convenio");
		
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), ZONA_SALA);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), GRADE_ESP);
		criteria.createAlias(GRADE_ESP2 + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), ESP_ESP,JoinType.LEFT_OUTER_JOIN);
		
//		criteria.createAlias(AacConsultas.Fields.CONTROLE.toString(), "controle", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("controle." + MamControles.Fields.EXTRATO_CONTROLES.toString(), "extrato", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), "retorno", JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "condicaoAtendimento", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "gradeEquipe", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "gradeProfEsp", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gradeProfEsp." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "gradeProfEspRapServidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gradeProfEspRapServidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "gradeProfEspRapServidorPessoaFisica", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gradeEquipe." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "gradeEquipeProfResp");
		criteria.createAlias("gradeEquipeProfResp." + RapServidores.Fields.PESSOA_FISICA.toString(), "gradeEquipeProfRespPesFisica");

		criteria.add(Restrictions.isNotNull(AacConsultas.Fields.PACIENTE.toString()));
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numero));
		
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}

	public List<AacConsultas> pesquisarConsultacPorConsultaNumeroAnterior(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.CON_NUMERO.toString(), numero));
		return executeCriteria(criteria);
	}

	private DetachedCriteria montarQueryConsultasPaciente(Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral,
			Integer filtroCodConsultaAnterior, Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio,
			Date filtroDtFim, boolean utilizarDataFim) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), AacConsultas.Fields.SITUACAO_CONSULTA.toString());
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), PAC, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), "TP_AGENDAMENTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.SERVIDOR_ALTERADO.toString(), "SERV_ALTERADO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.ATENDIMENTO.toString(), "ATENDIMENTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_ALTERADO." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_FIS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.CONSULTA.toString(), "CONSULTA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "COND_ATENDIMENTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRD2, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRD + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRD + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), "SIGLA_UNF_SALA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRD + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQUIPE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRD + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "PROF_ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROF_ESP." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "SERV_PROF_ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERV_PROF_ESP." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES_FIS_PROF_ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), "RETORNO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.PAGADOR.toString(), "PAG", JoinType.LEFT_OUTER_JOIN);
	
		String separador = ".";

		// where ind_sit_consulta = 'M'
		criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA.toString() + separador + AacSituacaoConsultas.Fields.SITUACAO.toString(),
				"M"));

		if (filtroPacCodigo != null) {
			criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), filtroPacCodigo));
		}

		if (filtroNumeroConsulta != null) {
			criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), filtroNumeroConsulta));
		}

		if (filtroCodCentral != null) {
			criteria.add(Restrictions.eq(AacConsultas.Fields.COD_CENTRAL.toString(), filtroCodCentral));
		}

		if (filtroCodConsultaAnterior != null) {
			criteria.add(Restrictions.eq("consulta" + separador + AacConsultas.Fields.NUMERO.toString(), filtroCodConsultaAnterior));
		}

		if (filtroCondAtendSeq != null) {
			//criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString());
			criteria.add(Restrictions.eq(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString() + separador + AacCondicaoAtendimento.Fields.SEQ,
					filtroCondAtendSeq));
		}

		if (filtroGradeSeq != null || filtroEspSeq != null) {
			//criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());

			if (filtroGradeSeq != null) {
				criteria.add(Restrictions.eq(GRD2 + separador + AacGradeAgendamenConsultas.Fields.SEQ, filtroGradeSeq));
			}

			if (filtroEspSeq != null) {
				criteria.add(Restrictions.eq(GRD2 + separador
						+ AacGradeAgendamenConsultas.Fields.ESP_SEQ, filtroEspSeq));
			}
		}

		if (filtroDtInicio != null) {
			Date dtInicio = DateUtil.truncaData(filtroDtInicio);
			if (filtroDtFim != null) {
				Date dtFim = DateUtil.obterDataComHoraFinal(filtroDtFim);
				criteria.add(Restrictions.or(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio),
						Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio)));
				criteria.add(Restrictions.or(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim),
						Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim)));
			} else if (utilizarDataFim) {
				// dtFim é a data de inicio com hora final (23:59:59)
				Date dtFim = DateUtil.obterDataComHoraFinal(filtroDtInicio);

				// A condição a seguir foi adaptada para usar o indice da coluna
				// dt_consulta
				// e para que não seja necessario reescrever a consulta em HQL
				// para o uso do trunc(<data>)
				// Condição: and trunc(dt_consulta) = trunc(:dtInicio)
				criteria.add(Restrictions.or(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio),
						Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio)));
				criteria.add(Restrictions.or(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim),
						Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim)));
			} else {
				criteria.add(Restrictions.or(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio),
						Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio)));
			}
		}

		return criteria;
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	private Query montarQueryConsultasAgenda(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetornos, Boolean count, FatProcedHospInternosVO filtroProcedimento) {

		Date dtInicio = DateUtil.truncaData(filtroDtInicio);
		Date dtFim = DateUtil.truncaDataFim(filtroDtInicio);
		if (filtroDtFim != null) {
			dtFim = DateUtil.truncaDataFim(filtroDtFim);
		}

		Query query = this.createQuery(
				montarHQLConsultasAgenda(filtroServico, filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe, filtroPagador,
						filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana, filtroHoraConsulta, filtroDtInicio, filtroDtFim,
						filtroProfissional, filtroRetornos, count, filtroProcedimento));

		query.setParameter("dtInicio", dtInicio);
		query.setParameter("dtFim", dtFim);
		
		if (filtroHoraConsulta != null) {
			Calendar data = Calendar.getInstance();
			data.setTime(filtroHoraConsulta);
			query.setParameter("hora", data.get(Calendar.HOUR_OF_DAY));
			query.setParameter("minuto", data.get(Calendar.MINUTE));
		}

		if (filtroDiaSemana != null) {
			query.setParameter("diaSemana", filtroDiaSemana);
		}
		if (filtroGrdSeq != null) {
			query.setParameter("filtroGrdSeq", filtroGrdSeq);
		}
		if (filtroEspecialidade != null) {
			query.setParameter("filtroEspecialidadeSeq", filtroEspecialidade.getSeq());
		}
		if (filtroServico != null) {
			query.setParameter("filtroServico", filtroServico);
		}
		if (filtroUslUnfSeq != null) {
			query.setParameter("filtroUslUnfSeq", filtroUslUnfSeq);
		}
		if (filtroEquipe != null) {
			query.setParameter("filtroEquipeSeq", filtroEquipe.getSeq());
		}
		if (filtroPagador != null) {
			query.setParameter("filtroPagadorSeq", filtroPagador.getSeq());
		}
		if (filtroAutorizacao != null) {
			query.setParameter("filtroAutorizacaoSeq", filtroAutorizacao.getSeq());
		}
		if (filtroCondicao != null) {
			query.setParameter("filtroCondicaoSeq", filtroCondicao.getSeq());
		}
		if (filtroSituacao != null) {
			query.setParameter("filtroSituacao", filtroSituacao);
		}
		if (filtroProfissional != null) {
			query.setParameter("profissional", filtroProfissional);
		}
		
		if (filtroRetornos != null) {
			query.setParameter("filtroRetornosSeq", filtroRetornos.getSeq());
		}
		
		if(filtroProcedimento != null){
			query.setParameter("filtroProcedimentoDescricao", filtroProcedimento.getDescricao());
		}
		
		return query;
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	private String montarHQLConsultasAgenda(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetornos, Boolean count, 
			FatProcedHospInternosVO filtroProcedimento) {

		StringBuilder sb = new StringBuilder(1085);
		if (count) {
			sb.append("select count(distinct consulta.numero) from AacConsultas consulta ");
		} else {
			sb.append("select distinct consulta from AacConsultas consulta ");
		}
		sb.append(" inner join consulta.gradeAgendamenConsulta grade ")
		.append(" left join grade.profServidor profServidor ")
		.append(" left join profServidor.pessoaFisica profPessoaFisica ")
		.append(" inner join grade.equipe equipe ")
		.append(" left join grade.especialidade especialidade ")			
		.append(" left join grade." ).append( AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString() ).append( " profEspecialidade ")
		.append(" left join consulta.paciente paciente ")
		.append(" join grade.especialidade.centroCusto centroCusto ")		
		.append(" left join consulta.retorno retorno ")
		.append(" left join consulta.controles controle ")
		.append(" left join consulta.procedimentosHospitalares cph ")
		.append(" left join cph.procedHospInterno phi ");

		/*if (filtroEspecialidade != null || filtroServico != null
				|| filtroProfissional != null || filtroGrdSeq != null
				|| filtroUslUnfSeq != null || filtroEquipe != null
				|| filtroProfissional != null) {
			sb.append("inner join consulta.gradeAgendamenConsulta grade ");
		}

		if (filtroEspecialidade != null || filtroServico != null) {
			sb.append("inner join grade.especialidade especialidade ");
		}
		
		if (filtroRetornos != null) {
			sb.append("left join consulta.retorno retorno ");
		}
		
		if (filtroProfissional != null) {
			sb.append("left join grade." + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString() + " profEspecialidade ");
		}
	   */
		
		sb.append("where ")
		.append("consulta." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( " >= :dtInicio ")
		.append("and consulta." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( " <= :dtFim ");

		if (filtroHoraConsulta != null) {
			sb.append("and hour(consulta.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(") = :hora ");
			sb.append("and minute(consulta.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(") = :minuto ");
		}

		if (filtroDiaSemana != null) {
			sb.append("and consulta." ).append( AacConsultas.Fields.DIA_SEMANA.toString()).append(" = :diaSemana ");
		}
		if (filtroGrdSeq != null) {
			sb.append("and grade.seq = :filtroGrdSeq ");
		}
		if (filtroEspecialidade != null) {
			sb.append("and especialidade.seq = :filtroEspecialidadeSeq ");
		}
		if (filtroServico != null) {
			sb.append("and centroCusto = :filtroServico ");
		}
		if (filtroUslUnfSeq != null) {
			sb.append("and grade." ).append( AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString()).append(" = :filtroUslUnfSeq ");
		}
		if (filtroEquipe != null) {
			sb.append("and grade.").append(AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString()).append(" = :filtroEquipeSeq ");
		}
		if (filtroPagador != null) {
			sb.append("and consulta.").append(AacConsultas.Fields.FAG_PGD_SEQ.toString()).append(" = :filtroPagadorSeq ");
		}
		if (filtroAutorizacao != null) {
			sb.append("and consulta.").append(AacConsultas.Fields.FAG_TAG_SEQ.toString()).append(" = :filtroAutorizacaoSeq ");
		}
		if (filtroCondicao != null) {
			sb.append("and consulta.").append(AacConsultas.Fields.FAG_CAA_SEQ.toString()).append(" = :filtroCondicaoSeq ");
		}
		if (filtroSituacao != null) {
			sb.append("and consulta.").append(AacConsultas.Fields.SITUACAO_CONSULTA.toString()).append(" = :filtroSituacao ");
		}
		// filtroMedico
		if (filtroProfissional != null) {
			sb.append("and grade.").append("profEspecialidade.").append(AghProfEspecialidades.Fields.RAP_SERVIDOR.toString()).append(" = :profissional ");
		}
		if (filtroRetornos != null) {
			sb.append("and retorno.").append(AacRetornos.Fields.SEQ.toString()).append(" = :filtroRetornosSeq ");
		}
		//#43342
		if(filtroProcedimento != null){
			sb.append("and phi.descricao = :filtroProcedimentoDescricao");
		}
		if (!count) {
			sb.append(" order by year(consulta."+AacConsultas.Fields.DATA_CONSULTA+")," 
					+ "month(consulta."+AacConsultas.Fields.DATA_CONSULTA+"), "
					+ "day(consulta."+AacConsultas.Fields.DATA_CONSULTA+"), "
					+ " especialidade." + AghEspecialidades.Fields.NOME_ESPECIALIDADE + ", grade."+ AacGradeAgendamenConsultas.Fields.SEQ
					+ ", profEspecialidade." + AghProfEspecialidades.Fields.SER_MATRICULA + ", paciente." + AipPacientes.Fields.PRONTUARIO);
		}
		return sb.toString();
	}
	
	public List<AacConsultas> listarConsultasPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral, Integer filtroCodConsultaAnterior,
			Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio, Date filtroDtFim) {
		return listarConsultasPaciente(firstResult, maxResult, orderProperty, asc, filtroPacCodigo, filtroNumeroConsulta,
				filtroCodCentral, filtroCodConsultaAnterior, filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq, filtroDtInicio,
				filtroDtFim, true);
	}

	public List<AacConsultas> listarConsultasPaciente(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral, Integer filtroCodConsultaAnterior,
			Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio, Date filtroDtFim,
			Boolean utilizarDataFim) {

		DetachedCriteria criteria = montarQueryConsultasPaciente(filtroPacCodigo, filtroNumeroConsulta, filtroCodCentral, filtroCodConsultaAnterior,
				filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq, filtroDtInicio, filtroDtFim, utilizarDataFim);
		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}

	public Long listarConsultasPacienteCount(Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral,
			Integer filtroCodConsultaAnterior, Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio,
			Date filtroDtFim) {
		return listarConsultasPacienteCount(filtroPacCodigo, filtroNumeroConsulta, filtroCodCentral, filtroCodConsultaAnterior, filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq, filtroDtInicio, filtroDtFim, true);
	}

	public Long listarConsultasPacienteCount(Integer filtroPacCodigo, Integer filtroNumeroConsulta, Long filtroCodCentral,
			Integer filtroCodConsultaAnterior, Short filtroCondAtendSeq, Integer filtroGradeSeq, Short filtroEspSeq, Date filtroDtInicio,
			Date filtroDtFim, Boolean utilizarDataFim) {

		DetachedCriteria criteria = montarQueryConsultasPaciente(filtroPacCodigo, filtroNumeroConsulta, filtroCodCentral, filtroCodConsultaAnterior,
				filtroCondAtendSeq, filtroGradeSeq, filtroEspSeq, filtroDtInicio, filtroDtFim, utilizarDataFim);

		return executeCriteriaCount(criteria);
	}

	public List<AacConsultas> listarConsultasMarcadasNovoPeriodo(Integer grdSeq, Date dtInicioSituacao) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), AacConsultas.Fields.SITUACAO_CONSULTA.toString());
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());
		criteria.add(Restrictions.eq(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + "." + AacGradeAgendamenConsultas.Fields.SEQ.toString(),
				grdSeq));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA.toString() + "." + AacSituacaoConsultas.Fields.IND_BLOQUEIO.toString(),
				false));
		criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicioSituacao));
		return executeCriteria(criteria);
	}

	public List<AacConsultas> listarConsultas(Integer grdSeq, Integer pacCodigo, Date dataConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grdSeq));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dataConsulta));
		return executeCriteria(criteria);
	}

	public List<AacConsultas> listarConsultasPorGrade(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grdSeq));
		return executeCriteria(criteria);
	}

	public boolean verificarGradeAgendamento(Integer seq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.setProjection(Projections.property(AacConsultas.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), seq));
		return executeCriteriaExists(criteria);
	}

	public List<AacConsultas> listarConsultaCodigoCentral(Integer codigoPaciente, Integer numeroConsulta, Date dtConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString());
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), codigoPaciente));
		criteria.add(Restrictions.isNotNull(AacConsultas.Fields.COD_CENTRAL.toString()));
		dtConsulta = DateUtil.truncaData(dtConsulta);
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);
	}

	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AacConsultas> listarConsultasNaoRealizadasPorGrade(
			AacGradeAgendamenConsultas grade, Short pgdSeq, Short tagSeq,
			Short caaSeq, Boolean emergencia, Date dtConsulta, Date mesInicio, Date mesFim,
			Date horaConsulta, DominioDiaSemana diaSemana, boolean mostrarTodosHorarios, boolean excluirPrimeiraConsulta, boolean visualizarPrimeirasConsultasSMS)
			throws ApplicationBusinessException {

		StringBuilder hql = new StringBuilder(447);
		hql.append("select ")
		.append(AacConsultas.Fields.CONSULTA.toString())
		.append(", pag, con, ta, ca, pac, gac ")


		.append(" from ")
		.append(AacConsultas.class.getName())
		.append(" as consulta ")

		.append(" left outer join fetch consulta.").append(AacConsultas.Fields.PAGADOR.toString()).append(" pag ")
		.append(" left outer join fetch consulta.").append(AacConsultas.Fields.CONTROLE.toString()).append(" con ")
		.append(" left outer join fetch consulta.").append(AacConsultas.Fields.TIPO_AGENDAMENTO.toString()).append(" ta ")
		.append(" left outer join fetch consulta.").append(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString()).append(" ca ")
		.append(" left outer join fetch consulta.").append(AacConsultas.Fields.PACIENTE.toString()).append(" pac ")
		.append(" left outer join fetch consulta.").append(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()).append(" gac ")
		
		.append(" where")
		.append(" consulta.").append(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString()).append(" = :seqGrade ");

		/*
		 * criteria.add(Restrictions.or(Restrictions.and(Restrictions.ne(
		 * AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(), forma),
		 * Restrictions
		 * .and(Restrictions.ne(AacConsultas.Fields.FORMA_AGENDAMENTO_ID
		 * .toString(), formaSe), Restrictions.and(
		 * Restrictions.ne(AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(),
		 * formaCampanha),
		 * Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(),
		 * dataMaisTrinta.getTime())))), Restrictions.and( Restrictions.or(
		 * Restrictions.eq(AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(),
		 * forma),
		 * Restrictions.or(Restrictions.eq(AacConsultas.Fields.FORMA_AGENDAMENTO_ID
		 * .toString(), formaSe),
		 * Restrictions.eq(AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(),
		 * formaCampanha))),
		 * Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(),
		 * DateUtil.truncaData(new Date())))));
		 */

		hql.append(AND_CONSULTA).append(AacConsultas.Fields.PACIENTE.toString()).append(" is null ");
		hql.append(AND_CONSULTA).append(AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString()).append(" = :situacaoL ");

		/*
		 * Somente aparecem primeiras consultas sus para funcionário com perfil
		 * de agendamento que faz a marcação das consultas vindas da PMPA
		 * 
		 * if (!getPacienteFacade().verificarAcaoQualificacaoMatricula(
		 * "MOSTRAR PRIM CONS SUS MARCACAO",
		 * servidorLogado.getId().getVinCodigo(),
		 * servidorLogado.getId().getMatricula()).isSim()) {
		 * criteria.add(
		 * Restrictions.ne(AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(),
		 * forma)); }
		 */
		/*
		 * for (AghCaractEspecialidades caracteristica :
		 * grade.getEspecialidade().getCaracteristicas()) { if
		 * (caracteristica.getId().getCaracteristica() ==
		 * DominioCaracEspecialidade.FUNCIONARIOS) {
		 * criteria.add(Restrictions.ne
		 * (AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(), formaHc));
		 * criteria
		 * .add(Restrictions.ne(AacConsultas.Fields.FORMA_AGENDAMENTO_ID.
		 * toString(), formaUf)); break; } }
		 */

		if (emergencia){
			hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" <= :dataMaisUmDia ");
			hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :dataMenosMeiaHora ");			
		}else{
			if (dtConsulta != null){
				hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :dtConsulta ");
				hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" <= :dataMaisUmDia ");	
			}
			/*else if (mes != null){
				hql.append(" and consulta.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" <= :ultimoDiaMes ");
				hql.append(" and consulta.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :primeiroDiaMes ");
			}*/
			else if (mesInicio!=null || mesFim != null) {
				if (mesInicio!=null) {
					hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString());
					hql.append(" >= :mesInicio ");
				}
				if (mesFim!=null) {
					hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString());
					hql.append(" <= :mesFim ");
				}
			}
			
			if (horaConsulta != null){
				hql.append(" and To_char(consulta.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(", 'hh24:mi')").append(" = :dataFormatada ");
			}

			if(!mostrarTodosHorarios) {
				hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" > :dataAtual ");
			} else {
				hql.append(AND_CONSULTA).append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" > :dataAtualTrunc ");
			}
			if (diaSemana != null){
				hql.append(AND_CONSULTA).append(AacConsultas.Fields.DIA_SEMANA.toString()).append(" = :diaSemana ");
			}
			//#52061 - Caso não possua permissão específica não lista Primeiras Consultas da Secretaria Municipal da Saúde
			if (!visualizarPrimeirasConsultasSMS){
				hql.append("and (consulta.").append(AacConsultas.Fields.CAA_SEQ.toString()).append(" <> :seqCondPrimeiraConsulta ");
				hql.append("or consulta.").append(AacConsultas.Fields.TAG_SEQ.toString()).append(" <> :seqTipoAgendamentoSMS)");
			}
		}

		if (pgdSeq != null) {
			hql.append(" and pag.").append(AacPagador.Fields.SEQ.toString()).append(" = :pgdSeq ");
		}
		if (tagSeq != null) {
			hql.append(" and ta.").append(AacTipoAgendamento.Fields.SEQ.toString()).append(" = :tagSeq ");
		}
		if (caaSeq != null) {
			hql.append(" and ca.").append(AacCondicaoAtendimento.Fields.SEQ.toString()).append(" = :caaSeq ");
		}
		if (excluirPrimeiraConsulta){
			hql.append(" and ca.").append(AacCondicaoAtendimento.Fields.DESCRICAO.toString()).append(" <> 'PRIMEIRA CONSULTA' ");
		}

		hql.append(" order by consulta.").append(AacConsultas.Fields.DATA_CONSULTA.toString());
		hql.append(" asc,");
		hql.append(" numero ");
		hql.append(" asc");

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("seqGrade", grade.getSeq());
		query.setString("situacaoL", "L");
		if (caaSeq != null) {
			query.setParameter("caaSeq", caaSeq);
		}
		if (tagSeq != null) {
			query.setParameter("tagSeq", tagSeq);
		}
		if (pgdSeq != null) {
			query.setParameter("pgdSeq", pgdSeq);
		}

		if (emergencia){
			Calendar dataMaisUmDia = Calendar.getInstance();
			dataMaisUmDia.add(Calendar.DAY_OF_MONTH, 1);
			Calendar dataMenosMeiaHora = Calendar.getInstance();
			dataMenosMeiaHora.add(Calendar.MINUTE, -30);
			query.setParameter("dataMaisUmDia", dataMaisUmDia.getTime());
			query.setParameter("dataMenosMeiaHora", dataMenosMeiaHora.getTime());			
		}else{
			if (dtConsulta != null){
				Calendar dataMaisUmDia = Calendar.getInstance();
				dataMaisUmDia.setTime(dtConsulta);
				dataMaisUmDia.add(Calendar.DAY_OF_MONTH, 1);
				query.setParameter("dtConsulta", dtConsulta);
				query.setParameter("dataMaisUmDia", dataMaisUmDia.getTime());
			}
			/*else if (mes != null){
				Date primeiroDiaMes = DateUtil.obterDataInicioCompetencia(mes);
				Date ultimoDiaMes = DateUtil.obterDataFimCompetencia(mes);
				query.setParameter("ultimoDiaMes", ultimoDiaMes);
				query.setParameter("primeiroDiaMes", primeiroDiaMes);	
			}*/
			else if (mesInicio!=null || mesFim!=null) {
				if (mesInicio!=null){
					query.setParameter("mesInicio", mesInicio);
				}
				if (mesFim!=null){
					query.setParameter("mesFim", mesFim);
				}
			}

			if (horaConsulta != null){
				SimpleDateFormat formatador = new SimpleDateFormat("HH:mm");
				query.setParameter("dataFormatada", formatador.format(horaConsulta));
			}
			
			if(!mostrarTodosHorarios) {
				query.setParameter("dataAtual", new Date());
			} else {
				query.setParameter("dataAtualTrunc", DateUtil.truncaData(new Date()));
			}
			if (diaSemana != null){
				query.setParameter("diaSemana", diaSemana);
			}
			//#52061
			if (!visualizarPrimeirasConsultasSMS){
				Short seqTipoAgendamentoSMS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TAG_SMS).getVlrNumerico().shortValue();
				Short seqCondPrimeiraConsulta = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CAA_CONSULTA).getVlrNumerico().shortValue();
				query.setParameter("seqTipoAgendamentoSMS", seqTipoAgendamentoSMS);
				query.setParameter("seqCondPrimeiraConsulta", seqCondPrimeiraConsulta);
			}
		}
		
		query.setReadOnly(true); 
		return query.list();
	}

	public List<Integer> listarHorariosConsultasSeqs(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia, Date filtroHoraConsulta,
			AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio,
			Date filtroDtFim, Integer grdSeq) {

		DetachedCriteria criteria = montarQueryHorariosConsulta(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta, filtroPagador,
																filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);

		criteria.add(Restrictions.ne(AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), "M"));
		
		
		List<AacConsultas> lista = executeCriteria(criteria);
		
		if (filtroDia != null || filtroHoraConsulta != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDia, filtroHoraConsulta, lista);
		}
		
		List<Integer> result = new ArrayList<>(lista.size());
		for (AacConsultas cns : lista) {
			result.add(cns.getNumero());
		}
		
		return result;
	}

	public List<AacConsultas> listarHorariosConsultas(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia, Date filtroHoraConsulta,
			AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio,
			Date filtroDtFim, Integer grdSeq) {

		DetachedCriteria criteria = montarQueryHorariosConsulta(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta, filtroPagador,
																filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);

		List<AacConsultas> lista = executeCriteria(criteria);

		if (filtroDia != null || filtroHoraConsulta != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDia, filtroHoraConsulta, lista);
		}

		if (!lista.isEmpty()) {
			Comparator<AacConsultas> decrescente = Collections.reverseOrder(new AacConsultasComparator());
			Collections.sort(lista, decrescente);
			int indPrimeiro = firstResult;
			int indUltimo = firstResult + maxResult;
			int tamLista = lista.size();
			if (indUltimo > tamLista) {
				indUltimo = tamLista;
			}
			return lista.subList(indPrimeiro, indUltimo);
		}

		return lista;
	}

	public Long listarHorariosConsultasCount(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia,
			Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao,
			Date filtroDtInicio, Date filtroDtFim, Integer grdSeq) {

		DetachedCriteria criteria = montarQueryHorariosConsulta(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta, filtroPagador,
			filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);

		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grdSeq));

		List<AacConsultas> lista = executeCriteria(criteria);

		if (filtroDia != null || filtroHoraConsulta != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDia, filtroHoraConsulta, lista);
		}

		return Long.valueOf(lista.size());
	}

	public AacConsultas obterConsultasMarcada(Integer nroConsulta, Boolean relacionaFormaAgendamento) {

		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.createAlias(AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), "FA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.PROJETO_PESQUISA.toString(), "PP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.SERVIDOR_CONSULTADO.toString(), "SC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.SERVIDOR_ATENDIDO.toString(), "SA", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AacConsultas.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), PAC, JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "CAC", JoinType.INNER_JOIN);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA_ESPECIALIDADE.toString(), "CAC_ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), "SITUACAO", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias("SA."+RapServidores.Fields.PESSOA_FISICA.toString(), "SERV_ATEND_PF", JoinType.LEFT_OUTER_JOIN);


		if (relacionaFormaAgendamento){
			criteria.createAlias(AacConsultas.Fields.PAGADOR.toString(), "PGD", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), "TAG", JoinType.LEFT_OUTER_JOIN);
			criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA", JoinType.LEFT_OUTER_JOIN);
		}
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), nroConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), "M"));

		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}

	public List<AacConsultas> listarHorariosConsultasNaoMarcadas(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta,
			DominioDiaSemana filtroDia, Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, Date filtroDtInicio, Date filtroDtFim, Integer grdSeq) {

		DetachedCriteria criteria = montarQueryHorariosConsulta(filtroSituacao, filtroNroConsulta, filtroDia, filtroHoraConsulta, filtroPagador,
				filtroAutorizacao, filtroCondicao, filtroDtInicio, filtroDtFim, grdSeq);

		// Filtra por consultas geradas
		criteria.add(Restrictions.ne(AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), "M"));

		List<AacConsultas> lista = executeCriteria(criteria);

		if (filtroDia != null || filtroHoraConsulta != null) {
			lista = filtrarListaPorDiaSemanaHora(filtroDia, filtroHoraConsulta, lista);
		}

		return lista;
	}

	private DetachedCriteria montarQueryHorariosConsulta(AacSituacaoConsultas filtroSituacao, Integer filtroNroConsulta, DominioDiaSemana filtroDia,
			Date filtroHoraConsulta, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao, AacCondicaoAtendimento filtroCondicao,
			Date filtroDtInicio, Date filtroDtFim, Integer grdSeq) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grdSeq));
		criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), SITUACAO_CONSULTA);
		criteria.createAlias(AacConsultas.Fields.PAGADOR.toString(), "PAG", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), "TP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CA", JoinType.LEFT_OUTER_JOIN);
		
		if (filtroNroConsulta != null) {
			criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), filtroNroConsulta));
		} else {
			if (filtroSituacao != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), filtroSituacao));
			}
			if (filtroPagador != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), filtroPagador.getSeq()));
			}
			if (filtroAutorizacao != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), filtroAutorizacao.getSeq()));
			}
			if (filtroCondicao != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_CAA_SEQ.toString(), filtroCondicao.getSeq()));
			}
			if (filtroDtInicio != null) {
				Date dtInicio = DateUtil.truncaData(filtroDtInicio);
				if (filtroDtFim != null) {
					Date dtFim = DateUtil.truncaDataFim(filtroDtFim);
					criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio));
					criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim));
				} else {
					Calendar calendarioTemp = Calendar.getInstance();
					calendarioTemp.setTime(dtInicio);
					calendarioTemp.add(Calendar.DAY_OF_MONTH, 1);
					Date dtInicioDiaSeguinte = DateUtil.truncaData(calendarioTemp.getTime());

					criteria.add(Restrictions.or(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio),
							Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio)));
					criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicioDiaSeguinte));
				}
			}
		}

		return criteria;
	}

	/**
	 * 
	 * @param filtroDia
	 * @param filtroHoraConsulta
	 * @param lista
	 * @return Lista de resultados da pesquisa filtrada pelo dia da semana e
	 *         hora (hh:mm) informados
	 */
	private List<AacConsultas> filtrarListaPorDiaSemanaHora(DominioDiaSemana filtroDia, Date filtroHoraConsulta, List<AacConsultas> lista) {
		List<AacConsultas> listaFiltrada = new ArrayList<AacConsultas>();

		for (AacConsultas aacConsulta : lista) {
			Date dataHoraConsulta = aacConsulta.getDtConsulta();
			Calendar calConsulta = Calendar.getInstance();
			Calendar calFiltroHora = Calendar.getInstance();

			calConsulta.setTime(dataHoraConsulta);

			if (filtroHoraConsulta != null) {
				calFiltroHora.setTime(filtroHoraConsulta);
			}

			int consultaHora = calConsulta.get(Calendar.HOUR_OF_DAY);
			int consultaMinuto = calConsulta.get(Calendar.MINUTE);
			int filtroHora = calFiltroHora.get(Calendar.HOUR_OF_DAY);
			int filtroMinuto = calFiltroHora.get(Calendar.MINUTE);

			if (filtroDia != null && filtroHoraConsulta != null && filtroDia.equals(CoreUtil.retornaDiaSemana(dataHoraConsulta))
					&& consultaHora == filtroHora && consultaMinuto == filtroMinuto) {
				listaFiltrada.add(aacConsulta);
			} else if (filtroDia != null && filtroHoraConsulta == null && filtroDia.equals(CoreUtil.retornaDiaSemana(dataHoraConsulta))) {
				listaFiltrada.add(aacConsulta);
			} else if (filtroDia == null && filtroHoraConsulta != null && consultaHora == filtroHora && consultaMinuto == filtroMinuto) {
				listaFiltrada.add(aacConsulta);
			}
		}
		return listaFiltrada;
	}

	/**
	 * Comparator para ordenar pesquisa das grades pela zona e sala.
	 * 
	 * @author diego.pacheco
	 * 
	 */
	class AacConsultasComparator implements Comparator<AacConsultas> {
		@Override
		public int compare(AacConsultas a1, AacConsultas a2) {
			return a1.getDtConsulta().compareTo(a2.getDtConsulta());
		}
	}

	public Long listarConsultasAgendaCount(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetornos, FatProcedHospInternosVO filtroProcedimento) {

		Query query = montarQueryConsultasAgenda(filtroServico, filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe, filtroPagador,
				filtroAutorizacao, filtroCondicao, filtroSituacao, filtroDiaSemana, filtroHoraConsulta, filtroDtInicio, filtroDtFim,
				filtroProfissional, filtroRetornos,  true, filtroProcedimento);

		return (Long) query.getSingleResult();
	}

	public List<AacConsultas> listarConsultasAgenda(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetornos, Integer firstResult, Integer maxResult,
			FatProcedHospInternosVO filtroProcedimento, Boolean ordenarNome) {
		
		String alias = "CON";
		
		DetachedCriteria criteria = consultasAgenda(filtroServico,
				filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade,
				filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao,
				filtroSituacao, filtroDiaSemana, filtroHoraConsulta,
				filtroDtInicio, filtroDtFim, filtroProfissional,
				filtroRetornos, filtroProcedimento,alias);

		createAliasConsultaAgenda(criteria, alias);

		if (ordenarNome){
			criteria.addOrder(Order.asc("paciente." + AipPacientes.Fields.NOME.toString()));
		}
		criteria.addOrder(Order.asc("paciente." + AipPacientes.Fields.PRONTUARIO.toString()));

		criteria.addOrder(Order.asc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);

		return executeCriteria(criteria);
	}
		
	private void createAliasConsultaAgenda(DetachedCriteria criteria, String aliasConsulta) {
		criteria.createAlias(AacConsultas.Fields.SERVIDOR.toString(), SERVIDOR_CONSULTA, JoinType.INNER_JOIN);
		criteria.createAlias(SERVIDOR_CONSULTA2 + RapServidores.Fields.PESSOA_FISICA.toString(), PESSOA_FISICA_CONSULTA, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE_ESP2 + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), GRADE_ESP_GENERICA , JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("profEspecialidade." + AghProfEspecialidades.Fields.SERVIDOR.toString(), "rapServidor", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("rapServidor." + RapServidores.Fields.PESSOA_FISICA.toString(), "serv_pf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "grade_equipe", JoinType.INNER_JOIN);
		criteria.createAlias("grade_equipe." + AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(), "prof_resp", JoinType.INNER_JOIN);
		criteria.createAlias("prof_resp." + RapServidores.Fields.PESSOA_FISICA.toString(), "pes_fis", JoinType.INNER_JOIN);
		criteria.createAlias("pes_fis." + RapPessoasFisicas.Fields.QUALIFICACOES.toString(), "pes_fis_quali", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.PAGADOR.toString(), "pagador", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), "tp_agend", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(aliasConsulta + "." + AacConsultas.Fields.PROCEDIMENTOS_HOSPITALARES.toString(), "CPH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CPH."+AacConsultaProcedHospitalar.Fields.PROCED_HOSP_INTERNO.toString(), "PHI", JoinType.LEFT_OUTER_JOIN);

		criteria.createAlias("paciente." + AipPacientes.Fields.ENDERECOS.toString(), "enderecos", JoinType.LEFT_OUTER_JOIN);
	}
	
	public List<AacConsultas> listarConsultasAgendaVO(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetornos, Integer firstResult, Integer maxResult, 
			FatProcedHospInternosVO filtroProcedimento) {

		DetachedCriteria criteria = consultasAgenda(filtroServico,
				filtroGrdSeq, filtroUslUnfSeq, filtroEspecialidade,
				filtroEquipe, filtroPagador, filtroAutorizacao, filtroCondicao,
				filtroSituacao, filtroDiaSemana, filtroHoraConsulta,
				filtroDtInicio, filtroDtFim, filtroProfissional,
				filtroRetornos, filtroProcedimento, "CON");
		
		criteria.addOrder(Order.asc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		
		return executeCriteria(criteria, firstResult.intValue(), maxResult.intValue(), null, false);
	}
	
	public List<ConsultasAgendaVO> listarConsultasAgendaScrooler(
			FccCentroCustos filtroServico, Integer filtroGrdSeq,
			Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, AacPagador filtroPagador,
			AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao,
			AacSituacaoConsultas filtroSituacao,
			DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim,
			RapServidores filtroProfissional, AacRetornos filtroRetornos,
			Integer firstResult, Integer maxResult, FatProcedHospInternosVO filtroProcedimento) {

		List<AacConsultas> result =  listarConsultasAgendaVO(filtroServico, filtroGrdSeq,
				filtroUslUnfSeq, filtroEspecialidade, filtroEquipe,
				filtroPagador, filtroAutorizacao, filtroCondicao,
				filtroSituacao, filtroDiaSemana, filtroHoraConsulta,
				filtroDtInicio, filtroDtFim, filtroProfissional,
				filtroRetornos, firstResult, maxResult, filtroProcedimento);
		return converterConsultasAgenda(result);
	}

	private List<ConsultasAgendaVO> converterConsultasAgenda(List<AacConsultas> consultas) {
		List<ConsultasAgendaVO> retorno = new ArrayList<ConsultasAgendaVO>(
				consultas.size());
		ConsultasAgendaVO consultasAgenda = null;
		for (AacConsultas consulta : consultas) {
			consultasAgenda = new ConsultasAgendaVO();
			if(consulta.getGradeAgendamenConsulta() != null && consulta.getGradeAgendamenConsulta().getEspecialidade() != null){
			consultasAgenda.setEspecialidadeSigla(consulta
					.getGradeAgendamenConsulta().getEspecialidade().getSigla());
			consultasAgenda.setEspecialidadeNome(consulta
					.getGradeAgendamenConsulta().getEspecialidade()
					.getNomeEspecialidade());			
			consultasAgenda.setGradeAgendamentoSeq(consulta
					.getGradeAgendamenConsulta().getSeq());
			}
			consultasAgenda.setDataConsulta(consulta.getDtConsulta());
			
			if(consulta.getCondicaoAtendimento() != null){
				consultasAgenda.setCondicaoAtendimentoDescricao(consulta
						.getCondicaoAtendimento().getDescricao());
			}
			
			if(consulta.getSituacaoConsulta() != null){
				consultasAgenda.setSitucaoConsultaDescricao(consulta
						.getSituacaoConsulta().getDescricao());
			}
			if (consulta.getPaciente() != null) {
				consultasAgenda.setPacienteProntuario(consulta.getPaciente()
						.getProntuario());
				consultasAgenda.setPacienteNome(consulta.getPaciente()
						.getNome());
				AipEnderecosPacientes endereco = consulta.getPaciente().getEnderecoPadrao(); 
				if(endereco != null){
					if( endereco.getAipCidade() != null){
					consultasAgenda.setPacienteEnderecoPadraoCidade(endereco.getAipCidade()
							.getNome());				
					consultasAgenda
							.setPacienteEnderecoPadraoUfSigla(endereco.getAipCidade().getAipUf() == null ? null
									: endereco.getAipCidade().getAipUf().getSigla());
					} else if(endereco.getAipBairrosCepLogradouro() != null){
						consultasAgenda.setNomeCidade(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getNome());
						consultasAgenda.setCidadeUfSigla(endereco.getAipBairrosCepLogradouro().getCepLogradouro().getLogradouro().getAipCidade().getAipUf().getSigla());
					}
				} 
			}
			if(consulta.getRetorno() != null){
				consultasAgenda.setRetornoSeq(consulta.getRetorno().getSeq());
				consultasAgenda.setRetornoDescricao(consulta.getRetorno()
						.getDescricao());
			}
			

			retorno.add(consultasAgenda);
		}
		return retorno;
	}

	@SuppressWarnings({"PMD.NPathComplexity"})
	private DetachedCriteria consultasAgenda(FccCentroCustos filtroServico, Integer filtroGrdSeq, Short filtroUslUnfSeq,
			AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, AacPagador filtroPagador, AacTipoAgendamento filtroAutorizacao,
			AacCondicaoAtendimento filtroCondicao, AacSituacaoConsultas filtroSituacao, DominioDiaSemana filtroDiaSemana, Date filtroHoraConsulta,
			Date filtroDtInicio, Date filtroDtFim, RapServidores filtroProfissional, AacRetornos filtroRetornos, FatProcedHospInternosVO filtroProcedimento, String aliasConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, aliasConsulta);
		criteria.createAlias(aliasConsulta + "." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE, JoinType.INNER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL, UNF , JoinType.INNER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), GRADE_ESP, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasConsulta + "." + AacConsultas.Fields.PACIENTE.toString(), PACIENTE, JoinType.LEFT_OUTER_JOIN);		
		criteria.createAlias(aliasConsulta + "." + AacConsultas.Fields.RETORNO.toString(), "retorno", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "profEspecialidade", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasConsulta + "." + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "condicaoAtendimento", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(aliasConsulta + "." + AacConsultas.Fields.SITUACAO_CONSULTA.toString(), "sitConsulta", JoinType.LEFT_OUTER_JOIN);

		
		if (filtroHoraConsulta != null) {
			Calendar data = Calendar.getInstance();
			data.setTime(filtroHoraConsulta);
			criteria.add(Restrictions.sqlRestriction("EXTRACT(HOUR FROM {alias}.DT_CONSULTA) = ?", data.get(Calendar.HOUR_OF_DAY), StandardBasicTypes.INTEGER));
			criteria.add(Restrictions.sqlRestriction("EXTRACT(MINUTE FROM {alias}.DT_CONSULTA) = ?", data.get(Calendar.MINUTE), StandardBasicTypes.INTEGER));
		}
		
		Date dtConsulta1 = DateUtil.truncaData(filtroDtInicio);
		Date dtConsulta2 = DateUtil.truncaDataFim(filtroDtInicio);
		if(filtroDtFim != null){
			dtConsulta2 = DateUtil.truncaDataFim(filtroDtFim);
		}
		
		criteria.add(Restrictions.between(aliasConsulta + "." + AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta1, dtConsulta2));
		
		if (filtroDiaSemana != null) {
			criteria.add(Restrictions.eq(aliasConsulta + "." + AacConsultas.Fields.DIA_SEMANA.toString(), filtroDiaSemana));
		}
		
		if (filtroGrdSeq != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.SEQ.toString(), filtroGrdSeq));
		}
		
		if (filtroEspecialidade != null) {
			criteria.add(Restrictions.eq(GRADE_ESP2 + AghEspecialidades.Fields.SEQ.toString(), filtroEspecialidade.getSeq()));
		}
		if (filtroServico != null) {
			criteria.add(Restrictions.eq(GRADE_ESP2 + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), filtroServico));
		}
		if (filtroUslUnfSeq != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), filtroUslUnfSeq));
		}
		if (filtroEquipe != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), filtroEquipe.getSeq()));
		}
		if (filtroPagador != null) {
			criteria.add(Restrictions.eq(aliasConsulta + "." + AacConsultas.Fields.FAG_PGD_SEQ.toString(), filtroPagador.getSeq()));
		}
		if (filtroAutorizacao != null) {
			criteria.add(Restrictions.eq(aliasConsulta + "." + AacConsultas.Fields.FAG_TAG_SEQ.toString(), filtroAutorizacao.getSeq()));
		}
		if (filtroCondicao != null) {
			criteria.add(Restrictions.eq(aliasConsulta + "." + AacConsultas.Fields.FAG_CAA_SEQ.toString(), filtroCondicao.getSeq()));
		}
		if (filtroSituacao != null) {
			criteria.add(Restrictions.eq(aliasConsulta + "." + AacConsultas.Fields.SITUACAO_CONSULTA.toString(), filtroSituacao));
		}
		// filtroMedico
		if (filtroProfissional != null) {
			criteria.add(Restrictions.eq("profEspecialidade." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), filtroProfissional));
		}
		if (filtroRetornos != null) {
			criteria.add(Restrictions.eq("retorno." + AacRetornos.Fields.SEQ.toString(), filtroRetornos.getSeq()));
		}
		//#43342
		if(filtroProcedimento != null){
			criteria.add(Restrictions.like("PHI."+FatProcedHospInternos.Fields.DESCRICAO.toString(), filtroProcedimento.getDescricao()));
		}
		return criteria;
	}	

	public Date obterConsultaUltimaDataPaciente(Integer pacCodigo, Date data) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		data = DateUtil.truncaData(data);
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), data));
		criteria.setProjection(Projections.max(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return (Date) executeCriteriaUniqueResult(criteria);
	}

	public Date obterDataUltimaConsultaNaoExcedente(Integer grdSeq, Date horaInicio, Date horaFim, DominioDiaSemana diaSemana, Short faaCaaSeq,
			Short fagTagSeq, Short fagPgdSeq, Date dtUltimaGeracao) {
		Calendar calDtUltimaGeracaoPassada = Calendar.getInstance();
		calDtUltimaGeracaoPassada.setTime(dtUltimaGeracao);
		calDtUltimaGeracaoPassada.add(Calendar.DAY_OF_MONTH, -360);
		Date dtUltimaGeracaoPassada = calDtUltimaGeracaoPassada.getTime();

		Calendar calHoraInicio = Calendar.getInstance();
		calHoraInicio.setTime(horaInicio);
		int numHoraInicio = calHoraInicio.get(Calendar.HOUR_OF_DAY);
		int numMinInicio = calHoraInicio.get(Calendar.MINUTE);

		Calendar calHoraFim = Calendar.getInstance();
		calHoraFim.setTime(horaFim);
		int numHoraFim = calHoraFim.get(Calendar.HOUR_OF_DAY);
		int numMinFim = calHoraFim.get(Calendar.MINUTE);

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.setProjection(Projections.max(AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grdSeq));
		criteria.add(Restrictions.between(AacConsultas.Fields.DATA_CONSULTA.toString(), dtUltimaGeracaoPassada, dtUltimaGeracao));
		criteria.add(Restrictions.eq(AacConsultas.Fields.DIA_SEMANA.toString(), diaSemana));
		criteria.add(Restrictions.disjunction()
				.add(Restrictions.and(
						Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'HH24') = '"+ numHoraInicio+"'"),
						Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'MI') >= '"+ numMinInicio+"'")))
				.add(Restrictions.and(
						Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'HH24') > '"+ numHoraInicio+"'"),
						Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'HH24') < '"+ numHoraFim+"'")))
				.add(Restrictions.conjunction()
						.add(Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'HH24') > '"+numHoraInicio+"'"))
						.add(Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'HH24') = '"+numHoraFim+"'"))
						.add(Restrictions.sqlRestriction("to_char(cast (DT_CONSULTA  as TIMESTAMP), 'MI') <= '"+numMinFim+"'"))));
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_CAA_SEQ.toString(), faaCaaSeq));
		criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), fagTagSeq));
		criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), fagPgdSeq));
		criteria.add(Restrictions.eq(AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString(), Boolean.FALSE));
		
		return (Date) executeCriteriaUniqueResult(criteria);
	}
	
	public MamAltaSumario pesquisarAltasSumariosPorNumeroConsulta(Integer numeroConsulta) {

		DetachedCriteria criteria = DetachedCriteria.forClass(MamAltaSumario.class);
		criteria.add(Restrictions.eq(MamAltaSumario.Fields.CON_NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.or(
				Restrictions.in(MamAltaSumario.Fields.IND_PENDENTE.toString(),
						Arrays.asList(DominioIndPendenteDiagnosticos.A,
						DominioIndPendenteDiagnosticos.E,
						DominioIndPendenteDiagnosticos.V)), 
				Restrictions.and(
						Restrictions.eq(MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.P), 
						Restrictions.isNull(MamAltaSumario.Fields.MAM_ALTA_SUMARIO.toString()))));
		criteria.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));

		return (MamAltaSumario) executeCriteriaUniqueResult(criteria);
	}


	public Date obterDataUltConsultaNaoExcedentePorGradeDataInicio(Integer grdSeq, Date dtHrInicio) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(DateUtil.obterDataComHoraInical(dtHrInicio));
		cal.add(Calendar.DAY_OF_MONTH, -32);
		Date dtHrInicioZero = cal.getTime();
		Date dtHrInicioUltHora = DateUtil.obterDataComHoraFinal(dtHrInicio);

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grdSeq));
		criteria.add(Restrictions.or(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtHrInicioZero),
				Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtHrInicioZero)));
		criteria.add(Restrictions.or(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtHrInicioUltHora),
				Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dtHrInicioUltHora)));
		criteria.add(Restrictions.eq(AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString(), Boolean.FALSE));
		criteria.setProjection(Projections.max(AacConsultas.Fields.DATA_CONSULTA.toString()));

		return (Date) executeCriteriaUniqueResult(criteria);
	}

//	protected IAmbulatorioFacade getAmbulatorioFacade() {
//		return this.aIAmbulatorioFacade;
//	}

	/**
	 * ORADB: Function AACC_VER_SIT_GRADE
	 * 
	 * @param grade
	 * @param data
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public String verificaSituacaoGrade(final AacGradeAgendamenConsultas grade, final Date data) throws ApplicationBusinessException {

		if (!isOracle()) {
			return "I";
		}
		final StringBuilder resultado = new StringBuilder();
		final String nomeObjeto = EsquemasOracleEnum.AGH + "." + ObjetosBancoOracleEnum.AACC_VER_SIT_GRADE;
		try {
			this.doWork(new Work() {
				public void execute(Connection connection) throws SQLException {
					CallableStatement cs = null;
					try {
						cs = connection.prepareCall("{? = call " + nomeObjeto + "(?,?)}");

						CoreUtil.configurarParametroCallableStatement(cs, 2, Types.INTEGER, grade == null ? null : grade.getSeq());
						CoreUtil.configurarParametroCallableStatement(cs, 3, Types.DATE, data);

						// Registro de parametro OUT
						cs.registerOutParameter(1, Types.VARCHAR);

						cs.execute();

						String retorno = cs.getString(1);
						resultado.append(retorno);
					} finally {
						if(cs != null){
							cs.close();
						}
					}
				}
			});
		} catch (Exception e) {
			String valores = CoreUtil.configurarValoresParametrosCallableStatement(grade == null ? null : grade.getSeq(), data);
			LOG.error(CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, true, valores), e);
			throw new ApplicationBusinessException(AGHUBaseBusinessExceptionCode.ERRO_EXECUCAO_FUNCAO_PROCEDURE_BD, nomeObjeto, valores,
					CoreUtil.configurarMensagemUsuarioLogCallableStatement(nomeObjeto, e, false, valores));
		}

		return resultado.toString();
	}

	public List<AacConsultas> obterAgendamentoConsulta(Integer nroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class,"CONSULTA");
		criteria.createAlias("CONSULTA." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), nroConsulta));

		return executeCriteria(criteria);
	}

	
	private String sqlDtExtrato(String indField, String alias, String minMax){
		String schema = MamExtratoControles.class.getAnnotation(Table.class).schema();
		String extratoTB = schema + "." + MamExtratoControles.class.getAnnotation(Table.class).name() + " ext";
		String ctrlTB = schema + "." + MamControles.class.getAnnotation(Table.class).name() + " ctrl";
		String sitTB = schema + "." + MamSituacaoAtendimentos.class.getAnnotation(Table.class).name() + " sit";
		
		String extDt = "ext.DTHR_MOVIMENTO";
		String extSat = "ext.SAT_SEQ";
		String extCtrl = "ext.CTL_SEQ";
		String ctrlSeq = "ctrl.SEQ";
		String ctrlCon = "ctrl.CON_NUMERO";
		String sitSeq = "sit.SEQ";
		String sitInd = "sit." + indField;

		
		return "(select "+minMax+"(" + extDt + ") from " + extratoTB + "," + ctrlTB + "," + sitTB 
				+ " where " + ctrlCon + "= {alias}.NUMERO and " 
				+ extCtrl + "=" + ctrlSeq + " and "
				+ extSat + "=" + sitSeq + " and "
				+ sitInd + "='S') as " + alias;
	}
	
	
	public List<ConsultaAmbulatorioVO> consultaPacientesAgendados(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, 
			Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas, VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, 
			EspCrmVO espCrmVO, AghEspecialidades especialidade, RapServidores profissional, StatusPacienteAgendado status, List<Short> sitPend) {
		DetachedCriteria criteria =  consultaPacientesAgendadosVO(dtPesquisa, zonaUnfSeq, zonaSalas, zonaSala, turno, equipe, espCrmVO,
				especialidade, profissional, status, sitPend);
		
		if (StringUtils.isEmpty(orderProperty)) {
			if (StatusPacienteAgendado.AGUARDANDO.equals(status)) {
				criteria.addOrder(Order.asc(AacConsultas.Fields.DATA_CONSULTA.toString()));
				criteria.addOrder(Order.asc("paciente." + AipPacientes.Fields.NOME.toString()));
			} else {
				criteria.addOrder(Order.asc("paciente." + AipPacientes.Fields.NOME.toString()));
				criteria.addOrder(Order.asc(AacConsultas.Fields.DATA_CONSULTA.toString()));
			}
		}

		if (firstResult == null) {
			return executeCriteria(criteria);
		}
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	
	public Long consultaPacientesAgendadosCount(Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas, VAacSiglaUnfSala zonaSala, DataInicioFimVO turno, AghEquipes equipe, 
			EspCrmVO espCrmVO, AghEspecialidades especialidade, RapServidores profissional, StatusPacienteAgendado status, List<Short> sitPend) {
		DetachedCriteria criteria = consultaPacientesAgendadosVO(dtPesquisa, zonaUnfSeq, zonaSalas, zonaSala, turno, equipe, espCrmVO,
				especialidade, profissional, status, sitPend);
		return executeCriteriaCount(criteria);
	}
	
	
	
	//---[CONSULTA LISTA DE PACIENTES AGENDADOS]
	@SuppressWarnings({"PMD.NPathComplexity"})
	public DetachedCriteria consultaPacientesAgendadosVO(Date dtPesquisa, Short zonaUnfSeq, List<Byte> zonaSalas, VAacSiglaUnfSala zonaSala,	
			DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, 
			RapServidores profissional, StatusPacienteAgendado status, List<Short> seqSitPendentes){		

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), PACIENTE);
		criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), SITUACAO_CONSULTA,JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.CONVENIO_SAUDE_PLANO.toString(), "convenio", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("convenio." + FatConvenioSaudePlano.Fields.CONVENIO_SAUDE.toString(), "convenioSaude");
		
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), ZONA_SALA);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), GRADE_ESP);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA.toString(), "gradeunidFunc", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "equipe", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "gradeProf", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(GRADE_ESP2 + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA.toString(), ESP_ESP, JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AacConsultas.Fields.CONTROLE.toString(), "controle", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("controle." + MamControles.Fields.SITUACAO_ATENDIMENTOS.toString(), "sitAntend", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("controle." + MamControles.Fields.SERVIDOR_RESPONSAVEL.toString(), "servRespons", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("servRespons." + RapServidores.Fields.PESSOA_FISICA.toString(), "servResponsPF", JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), "retorno", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "condicaoAtendimento", JoinType.LEFT_OUTER_JOIN);

		criteria.add(Restrictions.isNotNull(AacConsultas.Fields.PACIENTE.toString()));
		criteria.add(Restrictions.eq("situacaoConsulta." + AacSituacaoConsultas.Fields.SITUACAO.toString(), "M"));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AacConsultas.Fields.NUMERO.toString()), "numero")
				.add(Projections.property(AacConsultas.Fields.DATA_CONSULTA.toString()), "dtConsulta")
				.add(Projections.property(AacConsultas.Fields.ORIGEM.toString()), "origem")
				.add(Projections.property(AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString()), "excedeProgramacao")
				.add(Projections.property(AacConsultas.Fields.COD_CENTRAL.toString()), "codCentral")
				.add(Projections.property(AacConsultas.Fields.CAA_SEQ.toString()), "caaSeq")
				.add(Projections.property(AacConsultas.Fields.TAG_SEQ.toString()), "tagSeq")
				.add(Projections.property(AacConsultas.Fields.PGD_SEQ.toString()), "pgdSeq")
				.add(Projections.property("paciente." + AipPacientes.Fields.CODIGO.toString()), "pacienteCodigo")
				.add(Projections.property("paciente." + AipPacientes.Fields.NOME.toString()), "pacienteNome")
				.add(Projections.property("paciente." + AipPacientes.Fields.NOME_SOCIAL.toString()), "nomeSocial")
				.add(Projections.property("paciente." + AipPacientes.Fields.NRO_CARTAO_SAUDE.toString()), "pacienteNroCartaoSaude")
				.add(Projections.property("paciente." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
				.add(Projections.property("paciente." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), "pacienteDtNasc")
				.add(Projections.property("paciente." + AipPacientes.Fields.DT_RECADASTRO.toString()), "pacienteDtRecadastro")
				.add(Projections.property("convenio." + FatConvenioSaudePlano.Fields.DESCRICAO.toString()), "convenioDescricao")
				.add(Projections.property("convenioSaude." + FatConvenioSaude.Fields.DESCRICAO.toString()), "convenioSaudeDescricao")
				.add(Projections.property(GRADE2 + AacGradeAgendamenConsultas.Fields.SEQ.toString()), "gradeSeq")
				.add(Projections.property("equipe." + AghEquipes.Fields.NOME.toString()), "gradeEquipeNome")
				.add(Projections.property("gradeProf." + AghProfEspecialidades.Fields.SER_MATRICULA.toString()), "gradeProfmatricula")
				.add(Projections.property("gradeProf." + AghProfEspecialidades.Fields.SER_VIN_CODIGO), "gradeProfvinCodigo")
				.add(Projections.property(GRADE_ESP2 + AghEspecialidades.Fields.SEQ.toString()), "gradeEspSeq")
				.add(Projections.property(GRADE_ESP2 + AghEspecialidades.Fields.SIGLA.toString()), "gradeEspSigla")
				.add(Projections.property(GRADE_ESP2 + AghEspecialidades.Fields.NOME.toString()), "gradeEspNome")
				.add(Projections.property("gradeunidFunc." + AacUnidFuncionalSalas.Fields.SALA.toString()), "gradeUnidIdsala")
				.add(Projections.property("gradeunidFunc." + AacUnidFuncionalSalas.Fields.UNF_SEQ.toString()), "gradeUnidSeq")
				.add(Projections.property("zonaSala." + VAacSiglaUnfSala.Fields.SALA.toString()), "gradeSiglaUnfIdsala")
				.add(Projections.property("zonaSala." + VAacSiglaUnfSala.Fields.SIGLA.toString()), "gradeUnidadeSigla")
				.add(Projections.property("condicaoAtendimento." + AacCondicaoAtendimento.Fields.COR_EXIBICAO.toString()), "condicaoCorExibica")
				.add(Projections.property("condicaoAtendimento." + AacCondicaoAtendimento.Fields.DESCRICAO.toString()), "condicaoDescricao")				
				.add(Projections.property("situacaoConsulta." + AacSituacaoConsultas.Fields.SITUACAO.toString()), SITUACAO_CONSULTA)	
				.add(Projections.property("retorno." + AacRetornos.Fields.SEQ.toString()), "retornoSeq")
				.add(Projections.property("retorno." + AacRetornos.Fields.DESCRICAO.toString()), "retornoDescricao")
				.add(Projections.property("controle." + MamControles.Fields.IND_SITUACAO.toString()), "controleSituacao")
				.add(Projections.property("servRespons." + RapServidores.Fields.MATRICULA.toString()), "controleSerMatricula")
				.add(Projections.property("servRespons." + RapServidores.Fields.VIN_CODIGO.toString()), "controleServinCodigo")
				.add(Projections.property("servResponsPF." + RapPessoasFisicas.Fields.NOME.toString()), "controleServNome")
				
				.add(Projections.property(SIT_ANTEND + MamSituacaoAtendimentos.Fields.SEQ.toString()), "controleSituacaoAtendimentoSeq")
				.add(Projections.property(SIT_ANTEND + MamSituacaoAtendimentos.Fields.AGENDADO.toString()), "controleSituacaoAtendimentoAgendado")
				.add(Projections.sqlProjection(sqlDtExtrato("IND_AGUARDANDO", "controledthrChegada", "max"), new String[]{"controledthrChegada"}, new Type[]{TimestampType.INSTANCE}),"controledthrChegada")
				.add(Projections.sqlProjection(sqlDtExtrato("IND_PAC_ATEND", "controledtHrInicio", "max"), new String[]{"controledtHrInicio"}, new Type[]{TimestampType.INSTANCE}))
				.add(Projections.sqlProjection(sqlDtExtrato("IND_ATEND_CONCLUIDO", "controledtHrFim", "min"), new String[]{"controledtHrFim"}, new Type[]{TimestampType.INSTANCE}))
			);
		
		Date dtConsulta1 = DateUtil.truncaData(dtPesquisa);
		Date dtConsulta2 = DateUtil.truncaDataFim(dtPesquisa);
		if (turno != null) {
			dtConsulta1 = DateUtil.comporDiaHora(dtConsulta1, turno.getDataInicial());
			dtConsulta2 = DateUtil.comporDiaHora(dtConsulta2, turno.getDataFinal());
		}
		criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta1));
		criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta2));

		if (zonaSala != null) {
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.SALA.toString(), zonaSala.getId().getSala()));
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), zonaSala.getId().getUnfSeq()));
		} else if (zonaUnfSeq != null && zonaSalas != null && !zonaSalas.isEmpty()) {
			criteria.add(Restrictions.in(ZONA_SALA2 + VAacSiglaUnfSala.Fields.SALA.toString(), zonaSalas));
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), zonaUnfSeq));
		}

		if (equipe != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), equipe.getSeq()));
		}
		if (espCrmVO != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_ESP_SEQ.toString(), espCrmVO.getEspSeq()));
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), espCrmVO.getMatricula()));
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), espCrmVO.getVinCodigo()));
		}
		if (especialidade != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		if (profissional != null) {
			criteria.add(Restrictions.eq("gradeProf." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), profissional));
		}
		
		// Filtro para grupo de atendimento não será utilizado por enquanto,
		// caso seja implementado, deverá ser validado contra a classe
		// DominioGrupoAtendimentoAmbulatorio antes de chamar essa criteria		
		
		if (status != null) {
			switch (status) {
			case AGENDADO:
				break;
			case AGUARDANDO:
				criteria.add(Restrictions.eq(SIT_ANTEND + MamSituacaoAtendimentos.Fields.AGUARDANDO.toString(), Boolean.TRUE));
				break;
			case EM_ATENDIMENTO:

				if (seqSitPendentes != null && !seqSitPendentes.isEmpty()) {
					criteria.add(Restrictions.or(
							Restrictions.eq(SIT_ANTEND + MamSituacaoAtendimentos.Fields.IND_PAC_ATEND.toString(), Boolean.TRUE), Restrictions.or(
									Restrictions.eq(SIT_ANTEND + MamSituacaoAtendimentos.Fields.ATEND_REABERTO.toString(), Boolean.TRUE),
									Restrictions.in(SIT_ANTEND + MamSituacaoAtendimentos.Fields.SEQ.toString(), seqSitPendentes))));
				} else {
					criteria.add(Restrictions.or(
							Restrictions.eq(SIT_ANTEND + MamSituacaoAtendimentos.Fields.IND_PAC_ATEND.toString(), Boolean.TRUE),
							Restrictions.eq(SIT_ANTEND + MamSituacaoAtendimentos.Fields.ATEND_REABERTO.toString(), Boolean.TRUE)));
				}
				break;
			case ATENDIDO:
				criteria.add(Restrictions.eq(SIT_ANTEND + MamSituacaoAtendimentos.Fields.ATEND_CONCLUIDO.toString(), Boolean.TRUE));
				break;
			}
		}
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaAmbulatorioVO.class));
		
		return criteria;
	}
	
	
	
	
	public List<AacConsultas> pesquisarPacientesConsultasAgendadas(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala,	
			DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, 
			RapServidores profissional, AacRetornos retorno) throws ApplicationBusinessException{

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias("con." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
		criteria.createAlias("con." + AacConsultas.Fields.PACIENTE.toString(), PACIENTE);
		criteria.createAlias("con." + AacConsultas.Fields.SITUACAO_CONSULTA.toString(), SITUACAO_CONSULTA);

		criteria.add(Restrictions.isNotNull("con." + AacConsultas.Fields.PACIENTE.toString()));
		criteria.add(Restrictions.eq("situacaoConsulta." + AacSituacaoConsultas.Fields.SITUACAO.toString(), "M"));

		Date dtConsulta1 = DateUtil.truncaData(dtPesquisa);
		Date dtConsulta2 = DateUtil.truncaDataFim(dtPesquisa);
		if (turno != null) {
			dtConsulta1 = DateUtil.comporDiaHora(dtConsulta1, turno.getDataInicial());
			dtConsulta2 = DateUtil.comporDiaHora(dtConsulta2, turno.getDataFinal());
		}
		criteria.add(Restrictions.ge("con." + AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta1));
		criteria.add(Restrictions.le("con." + AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta2));

		if (zonaSala != null) {
			criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), ZONA_SALA);
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.SALA.toString(), zonaSala.getId().getSala()));
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), zonaSala.getId().getUnfSeq()));

		} else if (zonaSalas != null && !zonaSalas.isEmpty()) {
			criteria.add(Restrictions.in(GRADE2 + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), zonaSalas));
		}
		if (equipe != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), equipe.getSeq()));
		}
		if (espCrmVO != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_ESP_SEQ.toString(), espCrmVO.getEspSeq()));
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), espCrmVO.getMatricula()));
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), espCrmVO.getVinCodigo()));
		}
		if (especialidade != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		if (profissional != null) {
			criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "profEspecialidade");
			criteria.add(Restrictions.eq("profEspecialidade." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), profissional));
		}

		// Filtro para grupo de atendimento não será utilizado por enquanto,
		// caso seja implementado, deverá ser validado contra a classe
		// DominioGrupoAtendimentoAmbulatorio antes de chamar essa criteria
		
		if (retorno != null) {
			
			Criterion cRetornoAgendados = Restrictions.eq("con." + AacConsultas.Fields.RETORNO.toString() + "." + AacRetornos.Fields.SEQ.toString(), retorno.getSeq());
			Criterion cRetornoAtendidos = Restrictions.eq("con." + AacConsultas.Fields.RETORNO.toString() + "." + AacRetornos.Fields.SEQ.toString(), DominioSituacaoAtendimento.PACIENTE_ATENDIDO.getCodigo());
			criteria.add(Restrictions.or(cRetornoAgendados, cRetornoAtendidos));
			
			DetachedCriteria subCriteriaNaoExiste = DetachedCriteria.forClass(MamControles.class, "mam");
			subCriteriaNaoExiste.setProjection(Projections.projectionList().add(Projections.property(MamControles.Fields.SEQ.toString())));
			subCriteriaNaoExiste.add(Restrictions.eqProperty("con." + AacConsultas.Fields.NUMERO.toString(), "mam." + MamControles.Fields.CON_NUMERO.toString()));
			
			
			DetachedCriteria subCriteriaExiste = DetachedCriteria.forClass(MamControles.class, "mam2");
			subCriteriaExiste.setProjection(Projections.projectionList().add(Projections.property(MamControles.Fields.SEQ.toString())));
			subCriteriaExiste.createAlias("mam2." + MamControles.Fields.SITUACAO_ATENDIMENTOS, "situacao_atendimento", JoinType.LEFT_OUTER_JOIN);
			subCriteriaExiste.add(Restrictions.eqProperty("con." + AacConsultas.Fields.NUMERO.toString(), "mam2." + MamControles.Fields.CON_NUMERO.toString()));
			subCriteriaExiste.add(Restrictions.eq("situacao_atendimento." + MamSituacaoAtendimentos.Fields.AGENDADO.toString(), true));
			
			Criterion cNaoExiste = Subqueries.notExists(subCriteriaNaoExiste);
			Criterion cExiste = Subqueries.exists(subCriteriaExiste);
			criteria.add(Restrictions.or(cNaoExiste, cExiste));
		}

		criteria.addOrder(Order.asc("paciente." + AipPacientes.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("con." + AacConsultas.Fields.DATA_CONSULTA.toString()));

		criteria.setResultTransformer(Criteria.DISTINCT_ROOT_ENTITY);
		
		List<AacConsultas> lista = executeCriteria(criteria);
		return lista;
	}

	public Boolean existeConsultaPacientesAgendados(Date dtPesquisa, List<VAacSiglaUnfSala> zonaSalas, VAacSiglaUnfSala zonaSala,	
			DataInicioFimVO turno, AghEquipes equipe, EspCrmVO espCrmVO, AghEspecialidades especialidade, 
			RapServidores profissional){

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), PACIENTE);
		criteria.add(Restrictions.isNotNull(AacConsultas.Fields.PACIENTE.toString()));

		Date dtConsulta1 = DateUtil.truncaData(dtPesquisa);
		Date dtConsulta2 = DateUtil.truncaDataFim(dtPesquisa);
		if (turno != null) {
			dtConsulta1 = DateUtil.comporDiaHora(dtConsulta1, turno.getDataInicial());
			dtConsulta2 = DateUtil.comporDiaHora(dtConsulta2, turno.getDataFinal());
		}
		criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta1));
		criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta2));

		if (zonaSala != null) {
			criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), ZONA_SALA);
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.SALA.toString(), zonaSala.getId().getSala()));
			criteria.add(Restrictions.eq(ZONA_SALA2 + VAacSiglaUnfSala.Fields.UNF_SEQ.toString(), zonaSala.getId().getUnfSeq()));

		} else if (zonaSalas != null && !zonaSalas.isEmpty()) {
			criteria.add(Restrictions.in(GRADE2 + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), zonaSalas));
		}
		if (equipe != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), equipe.getSeq()));
		}
		if (espCrmVO != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_ESP_SEQ.toString(), espCrmVO.getEspSeq()));
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), espCrmVO.getMatricula()));
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), espCrmVO.getVinCodigo()));
		}
		if (especialidade != null) {
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		if (profissional != null) {
			criteria.createAlias(GRADE2 + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "profEspecialidade");
			criteria.add(Restrictions.eq("profEspecialidade." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), profissional));
		}
		
		// Filtro para grupo de atendimento não será utilizado por enquanto,
		// caso seja implementado, deverá ser validado contra a classe
		// DominioGrupoAtendimentoAmbulatorio antes de chamar essa criteria	
		
		return executeCriteriaCount(criteria) > 0;
	}

	public List<AacConsultas> pesquisarConsultaMesmaEspecialidade(Integer numero, Integer pacCodigo, Date dtConsulta, Date dataLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
		criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
		criteria.add(Restrictions.ne(AacConsultas.Fields.CON_NUMERO.toString(), numero));
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");

		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));

		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(
				Restrictions.eqProperty(AacNivelBusca.Fields.CLC_CODIGO.toString(),
						"aliasEspecialidade." + AghEspecialidades.Fields.CLINICA_CODIGO.toString()),
				Restrictions.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));

		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);

	}

	// cur_con1
	public List<AacConsultas> pesquisarConsultaAnteriorMesmaEspecialidade(Integer pacCodigo, Date dtConsulta, Date dtLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
		criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), "aliasPaciente");
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtLimite));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");

		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));

		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(
				Restrictions.eqProperty(AacNivelBusca.Fields.CLC_CODIGO.toString(),
						"aliasEspecialidade." + AghEspecialidades.Fields.CLINICA_CODIGO.toString()),
				Restrictions.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		return executeCriteria(criteria);
	}

	// cur_con4
	public List<AacConsultas> pesquisarConsultaAnteriorMesmaGrade(Integer pacCodigo, Date dtConsulta, Integer grdSeq, Short caaSeq, Short tagSeq,
			Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), "aliasPaciente");

		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.eq("aliasPaciente" + "." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.SEQ.toString(), grdSeq));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");

		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));

		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		return executeCriteria(criteria);
	}

	public List<AacConsultas> pesquisarConsultaAnteriorMesmaEspecialidadeServidor(Integer pacCodigo, Date dtConsulta, Date dtLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq, Integer matriculaCon, Short vinCodigoCon) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
		criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), AacConsultas.Fields.RETORNO.toString());
		criteria.createAlias(AacConsultas.Fields.SERVIDOR_CONSULTADO.toString(), AacConsultas.Fields.SERVIDOR_CONSULTADO.toString());
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtLimite));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SERVIDOR_CONSULTADO_MATRICULA.toString(), matriculaCon));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SERVIDOR_CONSULTADO_VIN_CODIGO.toString(), vinCodigoCon));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(
				Restrictions.eqProperty(AacNivelBusca.Fields.CLC_CODIGO.toString(),
						"aliasEspecialidade." + AghEspecialidades.Fields.CLINICA_CODIGO.toString()),
				Restrictions.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		return executeCriteria(criteria);
	}

	// cur_con5
	public List<AacConsultas> pesquisarConsultaAnteriorMesmaGradeSemNivelBusca(Integer pacCodigo, Date dtConsulta, Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), AacConsultas.Fields.PACIENTE.toString());
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.eq(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + "." + AacGradeAgendamenConsultas.Fields.SEQ.toString(),
				grdSeq));
		return executeCriteria(criteria);
	}

	public List<AacConsultas> pesquisarConsultaEspecialidadeDiferente(Integer pacCodigo, Date dtConsulta, Short espSeq, Date dataLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
		criteria.createAlias(GRADE + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "especialidade");
		criteria.createAlias("especialidade" + "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA, "especialidadeGenerica");
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), "retorno");
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));

		criteria.add(Restrictions.or(
				Restrictions.and(Restrictions.isNotNull("especialidadeGenerica" + "." + AghEspecialidades.Fields.SEQ.toString()),
						Restrictions.ne("especialidadeGenerica" + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq)),
				Restrictions.and(Restrictions.isNull("especialidadeGenerica" + "." + AghEspecialidades.Fields.SEQ.toString()),
						Restrictions.ne("especialidade" + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq))));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(
				Restrictions.eqProperty(AacNivelBusca.Fields.CLC_CODIGO.toString(), "especialidade" + "."
						+ AghEspecialidades.Fields.CLINICA_CODIGO.toString()), Restrictions.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));

		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);
	}

	// cur_con2
	public List<AacConsultas> pesquisarConsultaAnteriorEspecialidadeDiferente(Integer pacCodigo, Date dtConsulta, Short espSeqGen, Date dataLimite,
			Short caaSeq, Short tagSeq, Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
		criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.or(Restrictions.and(
				Restrictions.isNotNull(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
				Restrictions.ne(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString(), espSeqGen)),
				Restrictions.and(Restrictions.isNull(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()),
						Restrictions.ne(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.SEQ.toString(), espSeqGen))));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(
				Restrictions.eqProperty(AacNivelBusca.Fields.CLC_CODIGO.toString(),
						"aliasEspecialidade." + AghEspecialidades.Fields.CLINICA_CODIGO.toString()),
				Restrictions.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));

		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);
	}

	public List<AacConsultas> pesquisarConsultaIndependenteEspecialidade(Integer pacCodigo, Date dtConsulta, Date dataLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq) {
		String aliasEspecialidade = AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + "."
				+ AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString();
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),
				aliasEspecialidade);
		criteria.createAlias(aliasEspecialidade + "." + AghEspecialidades.Fields.CLINICA.toString(),"aliasClinica");

		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(Restrictions.eqProperty(
				AacNivelBusca.Fields.CLC_CODIGO.toString(), "aliasClinica."
						+ AghClinicas.Fields.CODIGO.toString()), Restrictions
				.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));

		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);
	}

	// cur_con3
	public List<AacConsultas> pesquisarConsultaIndependenteEspecialidadeAnterior(Integer pacCodigo, Date dtConsulta, Date dataLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
		criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);

		criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta));
		criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacNivelBusca.class, "niv");
		subCriteria.setProjection(Projections.projectionList().add(Projections.property(AacNivelBusca.Fields.SEQP.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_CAA_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_PGD_SEQ.toString()))
				.add(Projections.property(AacNivelBusca.Fields.FAG_TAG_SEQ.toString())));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_CAA_SEQ.toString(), caaSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		subCriteria.add(Restrictions.eq(AacNivelBusca.Fields.FAG_PGD_SEQ.toString(), pgdSeq));

		subCriteria.add(Restrictions.or(
				Restrictions.eqProperty(AacNivelBusca.Fields.CLC_CODIGO.toString(),
						"aliasEspecialidade." + AghEspecialidades.Fields.CLINICA_CODIGO.toString()),
				Restrictions.isNull(AacNivelBusca.Fields.CLC_CODIGO.toString())));

		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.CAA_SEQ.toString(), "con." + AacConsultas.Fields.FAG_CAA_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.TAG_SEQ.toString(), "con." + AacConsultas.Fields.FAG_TAG_SEQ.toString()));
		subCriteria.add(Restrictions.eqProperty(AacNivelBusca.Fields.PGD_SEQ.toString(), "con." + AacConsultas.Fields.FAG_PGD_SEQ.toString()));

		criteria.add(Subqueries.exists(subCriteria));

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);
	}

	public Long pesquisarConsultaPosAltaCount(Integer numero, Integer pacCodigo, Date dtConsulta, Short espSeq, Date dataLimite, Short caaSeq,
			Short tagSeq, Short pgdSeq, Date dtAltaMedica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		if (dtAltaMedica == null) {
			criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
			criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
			criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
			criteria.createAlias(AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), "aliasFormaAgendamento");
			criteria.add(Restrictions.ne(AacConsultas.Fields.CON_NUMERO.toString(), numero));
			criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));
			criteria.add(Restrictions.eq(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
			criteria.add(Restrictions.eq(ALIAS_RETORNO + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_CAA_SEQ.toString(), caaSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), pgdSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		} else if (dtAltaMedica != null) {
			criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
			criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
			criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
			criteria.createAlias(AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), "aliasFormaAgendamento");
			criteria.add(Restrictions.ne(AacConsultas.Fields.CON_NUMERO.toString(), numero));
			criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));
			criteria.add(Restrictions.eq(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
			criteria.add(Restrictions.eq(ALIAS_RETORNO + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_CAA_SEQ.toString(), caaSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), pgdSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq));
			criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtAltaMedica));
		}
		return executeCriteriaCount(criteria);
	}

	public Long pesquisarConsultaAnteriorPosAltaCount(Integer numero, Integer pacCodigo, Date dtConsulta, Short espSeq, Date dataLimite,
			Short caaSeq, Short tagSeq, Short pgdSeq, Date dtAltaMedica) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		if (dtAltaMedica == null) {
			criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
			criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
			criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
			criteria.createAlias(AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), "aliasFormaAgendamento");
			criteria.add(Restrictions.ne(AacConsultas.Fields.CON_NUMERO.toString(), numero));
			criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));
			criteria.add(Restrictions.eq(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
			criteria.add(Restrictions.eq(ALIAS_RETORNO + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_CAA_SEQ.toString(), caaSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), pgdSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq));
		} else if (dtAltaMedica != null) {
			criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGrade");
			criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
			criteria.createAlias("aliasGrade" + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), ALIAS_ESPECIALIDADE);
			criteria.createAlias(AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), "aliasFormaAgendamento");
			criteria.add(Restrictions.ne(AacConsultas.Fields.CON_NUMERO.toString(), numero));
			criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
			criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dataLimite));
			criteria.add(Restrictions.eq(ALIAS_ESPECIALIDADE + "." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
			criteria.add(Restrictions.eq(ALIAS_RETORNO + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_CAA_SEQ.toString(), caaSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), pgdSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq));
			criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_TAG_SEQ.toString(), tagSeq));
			criteria.add(Restrictions.gt(AacConsultas.Fields.DATA_CONSULTA.toString(), dtAltaMedica));
		}
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria criteriaPesquisarConsultaAnterior(AacConsultas consulta, Short espSeq, Boolean emergencia) {
		

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gradeAgenda");
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), "retorno");
		criteria.createAlias("gradeAgenda." + AacGradeAgendamenConsultas.Fields.AAC_GRADE_SITUACAO.toString(), "gradeSituacao");

		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), consulta.getPaciente().getCodigo()));
		if (emergencia) {
			criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), consulta.getDtConsulta()));
		} else {
			criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), consulta.getDtConsulta()));
		}
		criteria.add(Restrictions.ne(AacConsultas.Fields.NUMERO.toString(), consulta.getNumero()));
		criteria.add(Restrictions.eq("retorno." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));

		criteria.add(Restrictions.geProperty(AacConsultas.Fields.DATA_CONSULTA.toString(), "gradeSituacao."
				+ AacGradeSituacao.Fields.DT_INICIO_SITUACAO.toString()));
		criteria.add(Restrictions.or(
				Restrictions.isNull("gradeSituacao." + AacGradeSituacao.Fields.DT_FIM_SITUACAO),
				(Restrictions.leProperty(AacConsultas.Fields.DATA_CONSULTA.toString(),
						"gradeSituacao." + AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()))));
		criteria.add(Restrictions.eq("gradeSituacao." + AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));

		if (espSeq != null) {
			criteria.add(Restrictions.eq("gradeAgenda." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		}
		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		return criteria;
	}
	
	public List<AacConsultas> pesquisarConsultaAnterior(AacConsultas consulta, Short espSeq, Boolean emergencia){
		DetachedCriteria criteria = this.criteriaPesquisarConsultaAnterior(consulta, espSeq, emergencia);
		return executeCriteria(criteria);
	}
	
	public AacConsultas primeiraConsultaAnterior(AacConsultas consulta, Short espSeq, Boolean emergencia){
		DetachedCriteria criteria = this.criteriaPesquisarConsultaAnterior(consulta, espSeq, emergencia);
		List<AacConsultas> consultas = executeCriteria(criteria, 0, 1, null, true);
		if(consultas!=null && !consultas.isEmpty()){
			return consultas.get(0);
		}
		return null;
	}



	public Boolean existeConsultasAgendadas(AacGradeAgendamenConsultas grade) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.setProjection(Projections.rowCount());
		criteria.add(Restrictions.eq(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), grade));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA.toString() + ".situacao", "M"));
		Long result = (Long) executeCriteriaUniqueResult(criteria);
		return result != null && result > 0l;
	}

	public List<AacConsultas> pesquisarConsultaGradeEspecialidade(Integer numeroConsulta) {
		String separador = ".";
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());
		criteria.createAlias(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + separador + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + separador + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString());
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		return executeCriteria(criteria);
	}

	public List<AacConsultas> obterConsultasPorPacienteUnfSalaData(Integer codPaciente, Date dtConsulta, AacGradeAgendamenConsultas grade,
			DataInicioFimVO turno) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), codPaciente));
		if (grade.getAacUnidFuncionalSala() != null) {
			criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRADE);
			criteria.add(Restrictions.eq(GRADE2 + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString(), grade
					.getAacUnidFuncionalSala().getId().getUnfSeq()));
		}
		Date dtConsulta1 = DateUtil.truncaData(dtConsulta);
		Date dtConsulta2 = DateUtil.truncaDataFim(dtConsulta);
		if (turno != null) {
			dtConsulta1 = DateUtil.comporDiaHora(dtConsulta1, turno.getDataInicial());
			dtConsulta2 = DateUtil.comporDiaHora(dtConsulta2, turno.getDataFinal());
		}
		criteria.add(Restrictions.between(AacConsultas.Fields.DATA_CONSULTA.toString(), dtConsulta1, dtConsulta2));
		criteria.addOrder(Order.asc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public Boolean verificarExisteConsultasNaoExcedentes(Date dtConsulta, Short caaSeq) {
		StringBuilder hql = new StringBuilder(210);
		hql.append("select ac")
		.append(" from AacConsultas ac ")
		.append(" where")
		.append(" ac.")
		.append(AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString())
		.append(" = :excedeProgramacao ")
		.append(" and ac.")
		.append(AacConsultas.Fields.FAG_CAA_SEQ.toString())
		.append(" = :caaSeq ")
		.append(" and month(ac." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( ") = :mes ");

		org.hibernate.Query query = createHibernateQuery(hql.toString());

		query.setParameter("caaSeq", caaSeq);
		Calendar data = Calendar.getInstance();
		data.setTime(dtConsulta);
		query.setParameter("mes", data.get(Calendar.MONTH) + 1);
		query.setParameter("excedeProgramacao", Boolean.FALSE);

		query.setMaxResults(1);

		return !query.list().isEmpty();
	}

	public Long pesquisarRetornosAbsenteismoCount(Integer numeroConsulta) {
		String separador = ".";
		String aliasRetorno = "retorno";
		String aliasConsulta = "cons";

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, aliasConsulta);
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), aliasRetorno);
		criteria.add(Restrictions.eq(aliasConsulta + separador + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		criteria.add(Restrictions.or(
				Restrictions.eq(aliasRetorno + separador + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R), 
				Restrictions.eq(aliasRetorno + separador + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.N))
				);

		return executeCriteriaCount(criteria);
	}

	public Date ultimaDataConsultaPaciente(Integer codPaciente, Date limit) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.setProjection(Projections.max(AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), codPaciente));
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), "retorno");
		criteria.add(Restrictions.lt("retorno." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
		if (limit != null) {
			criteria.add(Restrictions.lt(AacConsultas.Fields.DATA_CONSULTA.toString(), limit));

		}

		return (Date) executeCriteriaUniqueResult(criteria);

	}

	public Long listarConsultasCount(AacFormaAgendamento formaAgendamento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.add(Restrictions.eq(AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), formaAgendamento));

		return executeCriteriaCount(criteria);
	}

	public Long listarConsultasComMotivoCount(AacMotivos motivo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.add(Restrictions.eq(AacConsultas.Fields.MOTIVO.toString(), motivo));

		return executeCriteriaCount(criteria);
	}
	
	public List<AacConsultas> listarConsultasParaLiberar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc,
			Integer prontuarioPaciente, Integer codigoPaciente, Integer grade, AghEspecialidades especialidade, Date dataConsulta,
			String situacaoConsulta, Integer nroConsulta) {
		DetachedCriteria criteria = createListarConsultasParaLiberarCriteria(prontuarioPaciente, codigoPaciente, grade, especialidade, dataConsulta,
				situacaoConsulta, nroConsulta);

		if (StringUtils.isNotBlank(orderProperty)) {
			criteria.addOrder(asc ? Order.asc(orderProperty) : Order.desc(orderProperty));
		}

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		return executeCriteria(criteria, firstResult, maxResult, null, false);
	}

	public Long listarConsultasParaLiberarCount(Integer prontuarioPaciente, Integer codigoPaciente, Integer grade,
			AghEspecialidades especialidade, Date dataConsulta, String situacaoConsulta, Integer nroConsulta) {
		DetachedCriteria criteria = createListarConsultasParaLiberarCriteria(prontuarioPaciente, codigoPaciente, grade, especialidade, dataConsulta,
				situacaoConsulta, nroConsulta);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria createListarConsultasParaLiberarCriteria(Integer prontuarioPaciente, Integer codigoPaciente, Integer grade,
			AghEspecialidades especialidade, Date dataConsulta, String situacaoConsulta, Integer nroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.createAlias(AacConsultas.Fields.PAGADOR.toString(), AacConsultas.Fields.PAGADOR.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), JoinType.LEFT_OUTER_JOIN);

		String gradeAgendaConsulta = AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString();
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), gradeAgendaConsulta);
		
		String unidFuncSala = AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA.toString();
		criteria.createAlias(gradeAgendaConsulta + "." + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA.toString()
				, unidFuncSala, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(unidFuncSala + "." + AacUnidFuncionalSalas.Fields.UNIDADE_FUNCIONAL.toString()
				, AacUnidFuncionalSalas.Fields.UNIDADE_FUNCIONAL.toString(), JoinType.LEFT_OUTER_JOIN);
		
		//_consulta.gradeAgendamenConsulta.profEspecialidade.rapServidor.pessoaFisica.nome
		String profEspecialidade = AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString();
		String servidor = AghProfEspecialidades.Fields.SERVIDOR.toString();
		criteria.createAlias(gradeAgendaConsulta + "." + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString()
				, profEspecialidade, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(profEspecialidade + "." + AghProfEspecialidades.Fields.SERVIDOR.toString()
				, servidor, JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(servidor + "." + RapServidores.Fields.PESSOA_FISICA.toString()
				, RapServidores.Fields.PESSOA_FISICA.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(gradeAgendaConsulta + "." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString()
				, AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), JoinType.LEFT_OUTER_JOIN);
		
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), AacConsultas.Fields.PACIENTE.toString());

		criteria.createAlias(gradeAgendaConsulta + "." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),
				AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString());
		
		if (prontuarioPaciente != null || codigoPaciente != null) {
			if (prontuarioPaciente != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_PRONTUARIO.toString(), prontuarioPaciente));
			}

			if (codigoPaciente != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), codigoPaciente));
			}
		}

		if (grade != null || especialidade != null) {
			
			if (grade != null) {
				criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grade));
			}

			if (especialidade != null) {
				criteria.add(Restrictions.eq(gradeAgendaConsulta + "."
						+ AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), especialidade));
			}
		}
		
		if (dataConsulta != null) {
			Calendar calIni = Calendar.getInstance();
			calIni.setTime(dataConsulta);
			calIni.set(Calendar.HOUR_OF_DAY, 0);
			calIni.set(Calendar.MINUTE, 0);
			calIni.set(Calendar.SECOND, 0);
			calIni.set(Calendar.MILLISECOND, 0);
			
			Calendar calFim = Calendar.getInstance();
			calFim.setTime(dataConsulta);
			calFim.set(Calendar.HOUR_OF_DAY, 23);
			calFim.set(Calendar.MINUTE, 59);
			calFim.set(Calendar.SECOND, 59);
			calFim.set(Calendar.MILLISECOND, 999);

			criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), calIni.getTime()));
			criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), calFim.getTime()));
		}
		
		if (StringUtils.isNotBlank(situacaoConsulta)) {
			criteria.createAlias(AacConsultas.Fields.SITUACAO_CONSULTA.toString(), AacConsultas.Fields.SITUACAO_CONSULTA.toString());

			criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA.toString() + "." + AacSituacaoConsultas.Fields.SITUACAO.toString(),
					situacaoConsulta));
		}

		if (nroConsulta != null) {
			criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), nroConsulta));
		}

		return criteria;
	}

	/**
	 * Cursor para retornar as consultas pelo conNumero
	 * 
	 * @param cConNumero
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	public List<AacConsultas> pesquisaConsultasNumero(Integer cConNumero) throws ApplicationBusinessException {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), AacConsultas.Fields.PACIENTE.toString());
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());

		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), cConNumero)); // restricao

		List<AacConsultas> lista = executeCriteria(criteria);	

		return lista;
	}
	
	
	public List<Object[]> pesquisarConsultaMesmaData(AacConsultas aacConsultas){
		
		StringBuilder hql = new StringBuilder(210);

		hql.append("select")
		.append(" con1.")
		.append(AacConsultas.Fields.NUMERO.toString())
		.append(", con1.")
		.append(AacConsultas.Fields.DATA_CONSULTA.toString())
		.append(" from ")
		.append(AacConsultas.class.getName())
		.append(" as con1 ")

		.append(" where ")
		.append(" con1.")
		.append(AacConsultas.Fields.NUMERO.toString()).append(" <> :cConNumero")
		.append(" and")

		.append(" con1.")
		.append(AacConsultas.Fields.GRD_SEQ.toString()).append(" = :grdSeq")
		.append(" and ")

		.append(" con1.")
		.append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" = :dtConsulta");

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("cConNumero", aacConsultas.getNumero());
		query.setParameter("grdSeq", aacConsultas.getGradeAgendamenConsulta().getSeq());
		query.setParameter("dtConsulta", DateUtil.truncaData(aacConsultas.getDtConsulta()));

		@SuppressWarnings("unchecked")
		List<Object[]> listaObjetos = query.list();
		
		return listaObjetos;
	}
	
	/*public Object[] obterDataInicioFimGrade(Integer grdSeq){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "consulta");
		
		String toCharDataConsulta = "to_char(this_.DT_CONSULTA,'HH24:MI:SS')";

		String sqlFragmentMin = "min(" + toCharDataConsulta + ") as dataInicio";
		String sqlFragmentMax = "max(" + toCharDataConsulta + ") as dataFim"; 
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.sqlProjection(sqlFragmentMin, new String[] {"dataInicio"},new Type[] {TimestampType.INSTANCE}))
				.add(Projections.sqlProjection(sqlFragmentMax, new String[] {"dataFim"},new Type[] {TimestampType.INSTANCE})));
		
		criteria.add(Restrictions.eq("consulta."+AacConsultas.Fields.GRD_SEQ.toString(), grdSeq));
		criteria.add(Restrictions.eq("consulta."+AacConsultas.Fields.IND_SIT_CONSULTA_SIT.toString(), "L"));
		criteria.add(Restrictions.gt("consulta."+AacConsultas.Fields.DATA_CONSULTA.toString(), new Date()));
				
		return (Object[]) executeCriteriaUniqueResult(criteria);		

	}*/

	/**
	 * Pesquisa AacConsultas por codigo do paciente e data
	 * 
	 * @param pacCodigo
	 * @param data
	 * @return
	 */
	public List<AacConsultas> pesquisarAacConsultasPorCodigoEData(Integer pacCodigo, Date data) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		if (pacCodigo != null){
			criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		}
		if (data != null){
			criteria.add(Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), data));
		}
		return executeCriteria(criteria);
	}

	@SuppressWarnings("PMD.NPathComplexity")
	public List<ConsultaDisponibilidadeHorarioVO> listarConsultaDisponibilidadeHorariosVO(
			Integer grdSeq, AacPagador pagador,
			AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta,
			Date mesInicio, Date mesFim, DominioDiaSemana dia,
			Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {
		Date dtInicio = null;
		Date dtFim = null;

		StringBuilder hql = new StringBuilder(300);
		hql.append(" select new br.gov.mec.aghu.ambulatorio.vo.ConsultaDisponibilidadeHorarioVO (")
		.append(" 	aac.")
		.append(AacConsultas.Fields.DATA_CONSULTA.toString())
		.append(", ")
		.append(" 	aac.")
		.append(AacConsultas.Fields.SITUACAO_CONSULTA.toString())
		.append(", ")
		.append(" 	aac.")
		.append(AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString())
		.append(" ) ")
		.append(" from AacConsultas aac ")
		.append(" 	join aac.")
		.append(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString())
		.append(" 	gac")
		.append(" where gac.")
		.append(AacGradeAgendamenConsultas.Fields.SEQ.toString())
		.append(" = :grdSeq ");
		if (pagador != null && pagador.getSeq() != null) {
			hql.append(" and aac.")
			.append(AacConsultas.Fields.FAG_PGD_SEQ.toString())
			.append(" = :pgdSeq ");
		}
		if (tipoAgendamento != null && tipoAgendamento.getSeq() != null) {
			hql.append(" and aac.")
			.append(AacConsultas.Fields.FAG_TAG_SEQ.toString())
			.append(" = :tagSeq ");
		}

		if (condicaoAtendimento != null && condicaoAtendimento.getSeq() != null) {
			hql.append(" and aac.")
			.append(AacConsultas.Fields.FAG_CAA_SEQ.toString())
			.append(" = :caaSeq ");
		}
		if (dia != null) {
			hql.append(" and aac.");
			hql.append(AacConsultas.Fields.DIA_SEMANA.toString());
			hql.append(" = :dia ");
		}
		if (dtConsulta != null) {
			hql.append(" and aac." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( " >= :dtInicio ");
			hql.append(" and aac." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( " <= :dtFim ");
		}
		/*if (mes != null) {
			hql.append("and month(aac." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( ") = :mes ");
			hql.append("and year(aac." ).append( AacConsultas.Fields.DATA_CONSULTA.toString() ).append( ") = :ano ");
		}*/
		if(mesInicio!= null){
			hql.append("and aac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :mesInicio " );
		}
		if(mesFim!=null){
			hql.append("and aac.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" <= :mesFim " );
		}
		
		//#52061 - Caso não possua permissão específica não lista Primeiras Consultas da Secretaria Municipal da Saúde
		if (!visualizarPrimeirasConsultasSMS){
			hql.append("and (aac.").append(AacConsultas.Fields.CAA_SEQ.toString()).append(" <> :seqCondPrimeiraConsulta ");
			hql.append("or aac.").append(AacConsultas.Fields.TAG_SEQ.toString()).append(" <> :seqTipoAgendamentoSMS)");
		}

		org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("grdSeq", grdSeq);
		if (pagador != null && pagador.getSeq() != null) {
			query.setParameter("pgdSeq", pagador.getSeq());
		}

		if (tipoAgendamento != null && tipoAgendamento.getSeq() != null) {
			query.setParameter("tagSeq", tipoAgendamento.getSeq());
		}

		if (condicaoAtendimento != null && condicaoAtendimento.getSeq() != null) {
			query.setParameter("caaSeq", condicaoAtendimento.getSeq());
		}

		if (dia != null) {
			query.setParameter("dia", dia);
		}
		if (dtConsulta != null) {
			dtInicio = DateUtil.truncaData(dtConsulta);
			dtFim = DateUtil.truncaDataFim(dtConsulta);
			query.setParameter("dtInicio", dtInicio);
			query.setParameter("dtFim", dtFim);
		}

		/*if (mes != null) {
			Calendar mesAux = Calendar.getInstance();
			mesAux.setTime(mes);
			query.setParameter("mes", mesAux.get(Calendar.MONTH)+1);
			query.setParameter("ano", mesAux.get(Calendar.YEAR));
		}*/
		if (mesInicio!=null){
			query.setParameter("mesInicio", mesInicio);
		}
		if (mesFim!=null){
			query.setParameter("mesFim", mesFim);
		}
		
		//#52061
		if (!visualizarPrimeirasConsultasSMS){
			Short seqTipoAgendamentoSMS = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TAG_SMS).getVlrNumerico().shortValue();
			Short seqCondPrimeiraConsulta = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_CAA_CONSULTA).getVlrNumerico().shortValue();
			query.setParameter("seqCondPrimeiraConsulta", seqCondPrimeiraConsulta);
			query.setParameter("seqTipoAgendamentoSMS", seqTipoAgendamentoSMS);
		}

		return query.list();
	}

	public List<AacConsultas> pesquisarMapaDesarquivamento(Date dtReferencia) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		Date dtRefereciaFinal = DateUtil.obterDataComHoraFinal(dtReferencia);
		
		criteria.createAlias("con." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");
		criteria.createAlias("grd." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("con." + AacConsultas.Fields.PACIENTE.toString(), "pac");
		criteria.createAlias("grd." + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA.toString(), "usl");
		criteria.createAlias("usl." + AacUnidFuncionalSalas.Fields.UNIDADE_FUNCIONAL.toString(), "unfUsl");

		criteria.createAlias("con." + AacConsultas.Fields.SITUACAO_CONSULTA.toString(), "sco");


		criteria.add(Restrictions.between("con."+AacConsultas.Fields.DATA_CONSULTA.toString(), dtReferencia, dtRefereciaFinal));


		criteria.add(Restrictions.eq("sco." + AacSituacaoConsultas.Fields.SITUACAO.toString(), "M"));
		criteria.add(Restrictions.eq("grd." + AacGradeAgendamenConsultas.Fields.IND_ENVIA_SAMIS.toString(), Boolean.TRUE));
		criteria.add(Restrictions.isNotNull("pac." + AipPacientes.Fields.PRONTUARIO.toString()));
		criteria.add(Restrictions.lt("pac." + AipPacientes.Fields.PRONTUARIO.toString(), VALOR_MAXIMO_PRONTUARIO));
		criteria.add(Restrictions.isNull("pac." + AipPacientes.Fields.DT_OBITO.toString()));
		criteria.add(Restrictions.isNull("pac." + AipPacientes.Fields.TIPO_DATA_OBITO.toString()));

		DetachedCriteria subMovimentacao = DetachedCriteria.forClass(AipMovimentacaoProntuarios.class, "mvp");
		subMovimentacao.setProjection(Projections.property("mvp." + AipMovimentacaoProntuarios.Fields.SEQ.toString()));
		subMovimentacao.createAlias("mvp." + AipMovimentacaoProntuarios.Fields.SOLICITANTE.toString(), "sop");

		subMovimentacao.add(Restrictions.eqProperty("mvp." + AipMovimentacaoProntuarios.Fields.PAC_CODIGO.toString(), "con."
				+ AacConsultas.Fields.PAC_CODIGO.toString()));
		subMovimentacao.add(Restrictions.eqProperty("mvp." + AipMovimentacaoProntuarios.Fields.DATA_MOVIMENTO.toString(), "con."
				+ AacConsultas.Fields.DATA_CONSULTA.toString()));
		subMovimentacao.add(Restrictions.eqProperty("sop." + AipSolicitantesProntuario.Fields.UNIDADES_FUNCIONAIS_SEQ.toString(), "grd."
				+ AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()));

		subMovimentacao.add(Restrictions.isNotNull("mvp." + AipMovimentacaoProntuarios.Fields.OBSERVACOES.toString()));

		criteria.add(Subqueries.notExists(subMovimentacao));

		return executeCriteria(criteria);
	}

	public List<Object[]> obterPacConsultas(Date dtFrom, Date dtTo, Boolean isReprint, String stringSeparator) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");

		// JOINs
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");

		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), "pac");

		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + stringSeparator
				+ AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), VUSL);

		// Restrictions
		criteria.add(Restrictions.between("con" + stringSeparator + AacConsultas.Fields.DATA_CONSULTA.toString(), dtFrom, dtTo));

		criteria.add(Restrictions.eq("grd" + stringSeparator + AacGradeAgendamenConsultas.Fields.IND_ENVIA_SAMIS.toString(), true));

		criteria.add(Restrictions.isNotNull("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString()));

		criteria.add(Restrictions.le("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString(), VALOR_MAXIMO_PRONTUARIO));

		criteria.add(Restrictions.isNull("pac" + stringSeparator + AipPacientes.Fields.DT_OBITO.toString()));

		criteria.add(Restrictions.isNull("pac" + stringSeparator + AipPacientes.Fields.TIPO_DATA_OBITO.toString()));

		if (isReprint) {
			criteria.add(Restrictions.isNotNull("pac" + stringSeparator + AipPacientes.Fields.VOLUMES.toString()));
		} else {
			criteria.add(Restrictions.isNull("pac" + stringSeparator + AipPacientes.Fields.VOLUMES.toString()));
		}

		// Projections
		criteria.setProjection(Projections.projectionList()
				.add(Projections.distinct(Projections.property("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString())))

				.add(Projections.property("con" + stringSeparator + AacConsultas.Fields.DATA_CONSULTA.toString()))

				.add(Projections.property("grd" + stringSeparator + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA_UNF_SEQ.toString()))

				.add(Projections.property("grd" + stringSeparator + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()))

				.add(Projections.property("grd" + stringSeparator + AacGradeAgendamenConsultas.Fields.SEQ.toString()))

				.add(Projections.property(AacConsultas.Fields.PAC_CODIGO.toString())));

		// Order by

		criteria.addOrder(Order.asc("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString()));

		criteria.addOrder(Order.asc("con" + stringSeparator + AacConsultas.Fields.DATA_CONSULTA.toString()));

		return executeCriteria(criteria);
	}

	public List<Object[]> obterPacientes(Date dtFrom, Date dtTo, Boolean isReprint, String stringSeparator) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");

		// JOINs
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");

		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), "pac");

		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() + stringSeparator
				+ AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), VUSL);

		// Restrictions
		criteria.add(Restrictions.between("con" + stringSeparator + AacConsultas.Fields.DATA_CONSULTA.toString(), dtFrom, dtTo));

		criteria.add(Restrictions.eq("grd" + stringSeparator + AacGradeAgendamenConsultas.Fields.IND_ENVIA_SAMIS.toString(), true));

		criteria.add(Restrictions.isNotNull("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString()));

		criteria.add(Restrictions.le("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString(), VALOR_MAXIMO_PRONTUARIO));

		criteria.add(Restrictions.isNull("pac" + stringSeparator + AipPacientes.Fields.DT_OBITO.toString()));

		criteria.add(Restrictions.isNull("pac" + stringSeparator + AipPacientes.Fields.TIPO_DATA_OBITO.toString()));

		if (isReprint) {
			criteria.add(Restrictions.isNotNull("pac" + stringSeparator + AipPacientes.Fields.VOLUMES.toString()));
		} else {
			criteria.add(Restrictions.isNull("pac" + stringSeparator + AipPacientes.Fields.VOLUMES.toString()));
		}

		// Projections
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("con" + stringSeparator + AacConsultas.Fields.DATA_CONSULTA.toString())).add( // Date

						Projections.property(VUSL + stringSeparator + VAacSiglaUnfSala.Fields.SIGLA.toString())).add( // String

						Projections.property(VUSL + stringSeparator + VAacSiglaUnfSala.Fields.SALA.toString())).add( // Byte

						Projections.property("pac" + stringSeparator + AipPacientes.Fields.PRONTUARIO.toString())).add( // Int

						Projections.property("pac" + stringSeparator + AipPacientes.Fields.NOME.toString())).add( // Int

						Projections.property("pac" + stringSeparator + AipPacientes.Fields.CODIGO.toString()))); // String

		return executeCriteria(criteria, true);
	}

	public List<Object[]> iberarConsultasPacientesParaPassivarProntuario(Integer pacCodigo, Calendar dthr) {
		// Qualquer dúvida sobre a forma de implementação desta consulta,
		// verificar a function AACC_BUSCA_DT_CONJ
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), dthr.getTime()));
		criteria.add(Restrictions.ne(AacConsultas.Fields.FAG_CAA_SEQ.toString(), (short) 6));

		criteria.setProjection(Projections.projectionList().add(Projections.property(AacConsultas.Fields.DATA_CONSULTA.toString()))
				.add(Projections.property(AacConsultas.Fields.NUMERO.toString())));

		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		return executeCriteria(criteria);
	}

	public List<AacConsultas> pesquisaConsultasPorPacienteDataConsulta(AipPacientes paciente, Date dataUltimaConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class)
				.add(Restrictions.eq(AacConsultas.Fields.PACIENTE.toString(), paciente))
				.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), dataUltimaConsulta))
				.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		return executeCriteria(criteria);
	}

	@SuppressWarnings("unchecked")
	public List<AacConsultas> buscarApacAssociacao(final Integer codPaciente, final Date dtInicio, final Date dtFim, final Short cnvCodigo, // P_CONVENIO_SUS_PADRAO
			final Integer codigoTratamento) {
		/*
		 * CURSOR c_itens_consulta (p_dt_ini DATE, p_dt_fim DATE, p_pac_codigo
		 * NUMBER, p_tratamento NUMBER) SELECT con.numero FROM aac_retornos ret,
		 * aac_consultas con, aac_grade_agendamen_consultas grd,
		 * agh_especialidades esp, fat_especialidade_tratamentos etr,
		 * fat_tipo_tratamentos tpt WHERE con.dt_consulta BETWEEN p_dt_ini AND
		 * p_dt_fim AND con.pac_codigo = p_pac_codigo AND con.ret_seq = ret.seq
		 * AND con.csp_cnv_codigo = 1 -- SUS AND ret.ind_absenteismo = 'R' AND
		 * con.grd_seq = grd.seq AND grd.esp_seq = esp.seq AND esp.seq =
		 * etr.esp_seq AND etr.tpt_seq = tpt.seq AND tpt.codigo = p_tratamento
		 * AND etr.ind_valida_apac = 'S';
		 */
		final StringBuilder hql = new StringBuilder(600).append("select con from AacConsultas con, FatEspecialidadeTratamento etr where con.")
				.append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" between :dtInicio and :dtFim and con.")
				.append(AacConsultas.Fields.PAC_CODIGO.toString()).append(" = :codPaciente and con.")
				.append(AacConsultas.Fields.CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo and con.")
				.append(AacConsultas.Fields.RETORNO.toString()).append('.').append(AacRetornos.Fields.IND_ABSENTEISMO.toString())
				.append(" = :indAbsenteismo and con.").append(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()).append('.')
				.append(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()).append(" = etr.").append(FatEspecialidadeTratamento.Fields.ESP_SEQ)
				.append(" and etr.").append(FatEspecialidadeTratamento.Fields.IND_VALIDA_APAC.toString()).append(" = :indValidaApac and etr.")
				.append(FatEspecialidadeTratamento.Fields.COD_TIPO_TRATAMENTO.toString()).append(" = :codigoTratamento ");
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtInicio", dtInicio);
		query.setParameter("dtFim", dtFim);
		query.setParameter("codPaciente", codPaciente);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("indAbsenteismo", DominioIndAbsenteismo.R);
		query.setParameter("indValidaApac", true);
		query.setParameter("codigoTratamento", codigoTratamento);

		return query.list();
	}

	@SuppressWarnings("unchecked")
	public List<AacConsultas> buscarConsultaPorProcedAmbRealizadoEspecialidade(final Integer codPaciente, final Date dtInicioCompetencia,
			final Date dtInicio, final Date dtFim, final Short cnvCodigo, // P_CONVENIO_SUS_PADRAO
			final Integer codigoTratamento, final Byte cpeMes, final Short cpeAno) {
		/*
		 * SELECT con.numero FROM aac_consultas con, fat_tipo_tratamentos tpt,
		 * fat_especialidade_tratamentos etr, FAT_PROCED_AMB_REALIZADOS PMR
		 * WHERE pmr.cpe_dt_hr_inicio = p_cpe_dt_hr_inicio AND pmr.cpe_modulo =
		 * 'AMB' AND PMR.cpe_mes = p_cpe_mes AND PMR.cpe_ano = p_cpe_ano AND
		 * PMR.dtHR_REALIZADO BETWEEN p_dt_ini AND p_dt_fim AND PMR.pac_codigo =
		 * p_pac_codigo AND PMR.IND_SITUACAO = 'A' AND PMR.csp_cnv_codigo = 1 --
		 * SUS AND ETR.ESP_SEQ = PMR.esp_seq AND etr.tpt_seq = tpt.seq AND
		 * tpt.codigo = 27 AND etr.ind_valida_apac = 'S' AND CON.NUMERO =
		 * PMR.PRH_CON_NUMERO;
		 */
		final StringBuilder hql = new StringBuilder("select pmr.consulta from FatProcedAmbRealizado pmr, FatEspecialidadeTratamento etr where pmr.")

		.append(FatProcedAmbRealizado.Fields.CPE_DT_HR_INICIO.toString()).append(" = :dtInicioCompetencia and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MODULO.toString()).append(" = :cpeModulo and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_MES.toString()).append(" = :cpeMes and pmr.")
				.append(FatProcedAmbRealizado.Fields.CPE_ANO.toString()).append(" = :cpeAno and pmr.")
				.append(FatProcedAmbRealizado.Fields.DTHR_REALIZADO.toString()).append(" between :dtInicio and :dtFim and pmr.")
				.append(FatProcedAmbRealizado.Fields.PAC_CODIGO.toString()).append(" = :codPaciente and pmr.")
				.append(FatProcedAmbRealizado.Fields.SITUACAO.toString()).append(" = :situacao and pmr.")
				.append(FatProcedAmbRealizado.Fields.CSP_CNV_CODIGO.toString()).append(" = :cnvCodigo and pmr.")
				.append(FatProcedAmbRealizado.Fields.ESP_SEQ.toString()).append(" = etr.").append(FatEspecialidadeTratamento.Fields.ESP_SEQ)
				.append(" and etr.").append(FatEspecialidadeTratamento.Fields.IND_VALIDA_APAC.toString()).append(" = :indValidaApac and etr.")
				.append(FatEspecialidadeTratamento.Fields.COD_TIPO_TRATAMENTO.toString()).append(" = :codigoTratamento ");
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("dtInicioCompetencia", dtInicioCompetencia);
		query.setParameter("cpeModulo", DominioModuloCompetencia.AMB);
		query.setParameter("cpeMes", cpeMes.intValue());
		query.setParameter("cpeAno", cpeAno.intValue());
		query.setParameter("dtInicio", dtInicio);
		query.setParameter("dtFim", dtFim);
		query.setParameter("codPaciente", codPaciente);
		query.setParameter("situacao", DominioSituacaoProcedimentoAmbulatorio.ABERTO);
		query.setParameter("cnvCodigo", cnvCodigo);
		query.setParameter("indValidaApac", true);
		// 27 - P_CODIGO_NEFROLOGIA
		query.setParameter("codigoTratamento", codigoTratamento);

		return query.list();
	}

	public List<FatConsultaPrhVO> buscarFatConsultaPrhVO(final Integer codPaciente, final Long numeroAtm, final Integer codigoTratamento,
			final Date dtInicio, final Date dtFim) {
		/*
		 * CURSOR c_consultas IS SELECT con.numero, aap.phi_seq, CIA.CID_SEQ
		 * FROM AAC_CID_ATENDIMENTOS CIA, aac_retornos ret, aac_consultas con,
		 * aac_grade_agendamen_consultas grd, fat_especialidade_tratamentos etr,
		 * FAT_TIPO_TRATAMENTOS tpt, fat_atendimento_apac_proc_hosp aap,
		 * aac_atendimento_apacs atm WHERE atm.numero = p_atm_numero AND
		 * aap.atm_numero = atm.numero AND tpt.CODIGO = p_tipo_tratamento AND
		 * etr.tpt_seq = tpt.seq AND etr.ind_valida_apac = 'S' AND grd.esp_seq =
		 * etr.esp_seq AND con.grd_seq = grd.seq AND con.pac_codigo =
		 * p_pac_codigo AND con.atd_seq = atm.atd_seq AND con.dt_consulta
		 * BETWEEN p_dt_ini_conta AND p_dt_fim_conta AND ret.seq = con.ret_seq
		 * AND ret.ind_absenteismo = 'R' -- Milena 04/2009 AND CIA.ATD_SEQ =
		 * ATM.ATD_SEQ AND CIA.IND_PRIORIDADE = 'P';
		 */
		final StringBuilder hql = new StringBuilder(600).append(
				"select con.numero as numeroConsulta, aap.id.phiSeq as phiSeq, cia.aghCid.seq as cidSeq from AacConsultas con, AacCidAtendimentos cia, FatAtendimentoApacProcHosp aap, FatEspecialidadeTratamento etr where aap.")
				.append(FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString()).append('.')
				.append(AacAtendimentoApacs.Fields.NUMERO.toString()).append(" = :numeroAtm and etr.")
				.append(FatEspecialidadeTratamento.Fields.TIPO_TRATAMENTO.toString()).append('.').append(FatTipoTratamentos.Fields.CODIGO.toString())
				.append(" = :codigoTratamento and etr.").append(FatEspecialidadeTratamento.Fields.IND_VALIDA_APAC.toString())
				.append(" = :invValidaApac and con.").append(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()).append('.')
				.append(AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString()).append('.').append(AghEspecialidades.Fields.SEQ.toString()).append(" = etr.")
				.append(FatEspecialidadeTratamento.Fields.ESPECIALIDADE).append('.').append(AghEspecialidades.Fields.SEQ.toString()).append(" and con.")
				.append(AacConsultas.Fields.PACIENTE.toString()).append('.').append(AipPacientes.Fields.CODIGO.toString()).append(" = :codPaciente and con.")
				.append(AacConsultas.Fields.ATENDIMENTO.toString()).append('.').append(AghAtendimentos.Fields.SEQ.toString()).append(" = aap.")
				.append(FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString()).append('.')
				.append(AacAtendimentoApacs.Fields.ATENDIMENTO.toString()).append('.').append(AghAtendimentos.Fields.SEQ.toString())
				.append(" and con.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" between :dtInicio and :dtFim and con.")
				.append(AacConsultas.Fields.RETORNO.toString()).append('.').append(AacRetornos.Fields.IND_ABSENTEISMO.toString())
				.append(" = :indAbsenteismo and cia.").append(AacCidAtendimentos.Fields.AGH_ATENDIMENTOS.toString()).append('.').append(AghAtendimentos.Fields.SEQ.toString()).append(" = con.")
				.append(AacConsultas.Fields.ATENDIMENTO.toString()).append('.').append(AghAtendimentos.Fields.SEQ.toString()).append(" and cia.")
				.append(AacCidAtendimentos.Fields.IND_PRIODRIDADE.toString()).append(" = :indPriodridade ");
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("numeroAtm", numeroAtm);
		query.setParameter("codigoTratamento", codigoTratamento);
		query.setParameter("invValidaApac", true);
		query.setParameter("codPaciente", codPaciente);
		query.setParameter("dtInicio", dtInicio);
		query.setParameter("dtFim", dtFim);
		query.setParameter("indAbsenteismo", DominioIndAbsenteismo.R);
		query.setParameter("indPriodridade", DominioPrioridadeCid.P);

		return processaProjecaoFatConsultaPrhVO(query.list());
	}
	
	public List<AacConsultas> pesquisarConsultasDoPaciente(AipPacientes paciente){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.PACIENTE.toString(), paciente));
		return executeCriteria(criteria);
	}
	
	public AacConsultas obterConsultaPorCpfGrade(RapServidores servidor, AacFormaAgendamento formaAgendamento, Date dataHora, String situacaoConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias("gradeAgendamenConsulta", "grd");		
		criteria.createAlias("grd.gradeSituacao", "gst");
		
		criteria.add(Restrictions.eq("grd."+ AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), 
				servidor.getId().getMatricula()));
		criteria.add(Restrictions.eq("grd."+ AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), 
				servidor.getId().getVinCodigo()));
		criteria.add(Restrictions.eq("gst."+AacGradeSituacao.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		
		// #35540 (A grade pode ter data_fim, mas estar ativa por não ter chego na data ainda)
		criteria.add(Restrictions.or(Restrictions.isNull("gst."+AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()),
				Restrictions.ge("gst."+AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString(), new Date())));
		//criteria.add(Restrictions.isNull("gst."+AacGradeSituacao.Fields.DT_FIM_SITUACAO.toString()));
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.DATA_CONSULTA.toString(), dataHora));
		criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), situacaoConsulta));
		criteria.add(Restrictions.eq(AacConsultas.Fields.FORMA_AGENDAMENTO_ID.toString(), formaAgendamento.getId()));
		
		List<AacConsultas> consultas = executeCriteria(criteria);
		AacConsultas consulta = null;
		if (consultas!= null && !consultas.isEmpty()){
			consulta = consultas.get(0);
		}
		
		return consulta;
	}

	public List<FatConsultaPrhVO> buscarFatConsultaPrhVOAcompanhamento(final Integer codPaciente, final Long numeroAtm, final Date dtInicio,
			final Date dtFim) {
		/*
		 * CURSOR c_cons_acomp IS SELECT con.numero, aap.phi_seq FROM
		 * fat_atendimento_apac_proc_hosp aap, aac_atendimento_apacs atm,
		 * fat_transplante_especialidades trs, aac_grade_agendamen_consultas
		 * grd, aac_retornos ret, aac_consultas con WHERE con.pac_codigo =
		 * p_pac_codigo AND ret.seq = con.ret_seq AND ret.ind_absenteismo = 'R'
		 * AND con.dt_consulta BETWEEN p_dt_ini_conta AND
		 * NVL(p_dt_fim_conta,SYSDATE) AND grd.seq = con.grd_seq AND trs.esp_seq
		 * = grd.esp_seq AND trs.ttr_codigo = atm.ptr_ttr_codigo AND atm.numero
		 * = p_atm_numero AND aap.atm_numero = atm.numero AND aap.ind_prioridade
		 * = 'P';
		 */
		final StringBuilder hql = new StringBuilder(600).append(
				"select con.numero as numeroConsulta, aap.id.phiSeq as phiSeq from AacConsultas con, FatAtendimentoApacProcHosp aap, FatTransplanteEspecialidade trs where con.")
				.append(AacConsultas.Fields.PACIENTE.toString()).append('.').append(AipPacientes.Fields.CODIGO.toString()).append(" = :codPaciente and con.")
				.append(AacConsultas.Fields.RETORNO.toString()).append('.').append(AacRetornos.Fields.IND_ABSENTEISMO.toString())
				.append(" = :indAbsenteismo and con.").append(AacConsultas.Fields.DATA_CONSULTA.toString())
				.append(" between :dtInicio and :dtFim and con.").append(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()).append('.')
				.append(AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString()).append('.').append(AghEspecialidades.Fields.SEQ.toString()).append(" = trs.")
				.append(FatTransplanteEspecialidade.Fields.ESPECIALIDADES.toString()).append('.').append(AghEspecialidades.Fields.SEQ.toString())
				.append(" and trs.").append(FatTransplanteEspecialidade.Fields.TTR_CODIGO.toString()).append(" = aap.")
				.append(FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString()).append('.')
				.append(AacAtendimentoApacs.Fields.PTR_TTR_CODIGO.toString()).append(" and aap.")
				.append(FatAtendimentoApacProcHosp.Fields.ATENDIMENTO_APAC.toString()).append('.').append(AacAtendimentoApacs.Fields.NUMERO.toString())
				.append(" = :numeroAtm and aap.").append(FatAtendimentoApacProcHosp.Fields.IND_PRIORIDADE.toString()).append(" = :indPrioridade")

		;
		final org.hibernate.Query query = createHibernateQuery(hql.toString());
		query.setParameter("codPaciente", codPaciente);
		query.setParameter("indAbsenteismo", DominioIndAbsenteismo.R);
		query.setParameter("dtInicio", dtInicio);
		query.setParameter("dtFim", dtFim == null ? new Date() : dtFim);
		query.setParameter("numeroAtm", numeroAtm);
		query.setParameter("indPrioridade", DominioPrioridadeCid.P);

		return processaProjecaoFatConsultaPrhVO(query.list());
	}

	private List<FatConsultaPrhVO> processaProjecaoFatConsultaPrhVO(@SuppressWarnings("rawtypes") List list) {
		if (list == null) {
			return new ArrayList<FatConsultaPrhVO>(0);
		}
		List<FatConsultaPrhVO> retorno = new ArrayList<FatConsultaPrhVO>(list.size());
		for (Object obj : list) {
			FatConsultaPrhVO fatCondultaPrhVO = new FatConsultaPrhVO();
			Object[] res = (Object[]) obj;
			int i = 0;
			if (res[i] != null && res.length >= i) {
				fatCondultaPrhVO.setNumeroConsulta((Integer) res[i]);
			}
			i++;
			if (res[i] != null && res.length >= i) {
				fatCondultaPrhVO.setPhiSeq((Integer) res[i]);
			}
			i++;
			if (res[i] != null && res.length >= i) {
				fatCondultaPrhVO.setCidSeq((Integer) res[i]);
			}
			retorno.add(fatCondultaPrhVO);
		}
		return retorno;
	}

	public List<AacConsultas> listarConsultasPorCodigoPaciente(Integer pacCodigo) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));

		return executeCriteria(criteria);
	}

	/**
	 * Realiza a busca por consultas de paciente com óbito.
	 * 
	 * @param pacCodigo
	 *            - Código do Paciente
	 * @return Lista de consultas
	 */
	public List<AacConsultas> pesquisarConsultasPorCodigoPacienteObito(
			Integer pacCodigo) {

		DetachedCriteria criteria = DetachedCriteria
				.forClass(AacConsultas.class);

		criteria.createAlias(AacConsultas.Fields.PACIENTE.toString(), "pac");

		criteria.add(Restrictions.eq(
				"pac." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.gtProperty(
				AacConsultas.Fields.DATA_CONSULTA.toString(), "pac."
						+ AipPacientes.Fields.DT_OBITO.toString()));
		criteria.add(Restrictions.gt(
				AacConsultas.Fields.DATA_CONSULTA.toString(), new Date()));

		return executeCriteria(criteria);
	}

	public List<AacConsultas> pesquisarConsultasPorPaciente(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class)
				.add(Restrictions.eq(AacConsultas.Fields.PACIENTE.toString(), paciente))
				.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		criteria.createCriteria(AacConsultas.Fields.RETORNO.toString()).add(
				Restrictions.eq(AacRetornos.Fields.IND_ABSENTEISMO.toString(),
						DominioIndAbsenteismo.R));

		List<AacConsultas> listaConsultasVolta = executeCriteria(criteria);

		return listaConsultasVolta;
	}
	
	public Long pesquisarConsultasPorPacienteCount(AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.PACIENTE.toString(), paciente));
		//criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		criteria.createCriteria(AacConsultas.Fields.RETORNO.toString()).add(
				Restrictions.eq(AacRetornos.Fields.IND_ABSENTEISMO.toString(),
						DominioIndAbsenteismo.R));

		return executeCriteriaCount(criteria);
	}
	
	public boolean possuiConsultasPorGradeAgendamento(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(),
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());

		criteria.add(Restrictions.eq(AacConsultas.Fields.GRD_SEQ.toString(), grdSeq));

		return executeCriteriaCount(criteria) > 0;
	}
	
	public List<AacConsultas> pesquisarConsultasPorGrade(Integer grdSeq, Date dataConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.GRD_SEQ.toString(), grdSeq));
		if (dataConsulta != null){
			Date dtInicio = DateUtil.obterDataComHoraInical(dataConsulta);
			Date dtFim = DateUtil.obterDataComHoraFinal(dataConsulta);
			criteria.add(Restrictions.between(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio, dtFim));			
		}
		if (dataConsulta != null){
			Date dtInicio = DateUtil.obterDataComHoraInical(dataConsulta);
			Date dtFim = DateUtil.obterDataComHoraFinal(dataConsulta);
			criteria.add(Restrictions.between(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio, dtFim));			
		}
		if (dataConsulta != null){
			Date dtInicio = DateUtil.obterDataComHoraInical(dataConsulta);
			Date dtFim = DateUtil.obterDataComHoraFinal(dataConsulta);
			criteria.add(Restrictions.between(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio, dtFim));			
		}
		return executeCriteria(criteria);
	}
	
	public boolean verificaGradeTipoSisreg(Integer grdSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);

		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(),
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString());

		criteria.add(Restrictions.eq(AacConsultas.Fields.GRD_SEQ.toString(), grdSeq));

		criteria.add(Restrictions.or(Restrictions.eq(
				AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), "U"),
				Restrictions.eq(AacConsultas.Fields.ORIGEM.toString(),
						DominioOrigemConsulta.S)));
		return executeCriteriaCount(criteria) > 0;
	}

	public List<AltasAmbulatoriasPolVO> pesquisarAltasAmbulatoriaisPol(Integer pacCodigo, Integer firstResult, Integer maxResult,

			String orderProperty, boolean asc) {
		DetachedCriteria criteria = getCriteriaPesquisarAltasAmbulatoriaisPol(pacCodigo);
		
		criteria.addOrder(Order.desc("atd." + AghAtendimentos.Fields.DATA_HORA_INICIO.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(AltasAmbulatoriasPolVO.class));
		
		return executeCriteria(criteria, firstResult, maxResult, orderProperty, asc);
	}
	
	public Long pesquisarAltasAmbulatoriaisPolCount(Integer pacCodigo) {
		DetachedCriteria criteria = getCriteriaPesquisarAltasAmbulatoriaisPol(pacCodigo);
		return executeCriteriaCount(criteria);
	}

	private DetachedCriteria getCriteriaPesquisarAltasAmbulatoriaisPol(Integer pacCodigo) {

		DetachedCriteria criteria = getCriteriaPesquisarAltasAmbulatoriaisPol();
		criteria.add(Restrictions.eq("aac." + AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(AghAtendimentos.Fields.SEQ.toString()), AltasAmbulatoriasPolVO.Fields.SEQ.toString())
				.add(Projections.property(AghAtendimentos.Fields.PRONTUARIO.toString()), AltasAmbulatoriasPolVO.Fields.PRONTUARIO.toString())
				.add(Projections.property(AghAtendimentos.Fields.DATA_HORA_INICIO.toString()), AltasAmbulatoriasPolVO.Fields.DTHR_INICIO.toString())
				.add(Projections.property(AghAtendimentos.Fields.INT_SEQ.toString()), AltasAmbulatoriasPolVO.Fields.INT_SEQ.toString())
				.add(Projections.property(AghAtendimentos.Fields.APE_SEQ.toString()), AltasAmbulatoriasPolVO.Fields.APE_SEQ.toString())
				
				.add(Projections.property("aac." + AacConsultas.Fields.NUMERO.toString()), AltasAmbulatoriasPolVO.Fields.NUMERO.toString())
				.add(Projections.property("aac." + AacConsultas.Fields.PAC_CODIGO.toString()), AltasAmbulatoriasPolVO.Fields.PAC_CODIGO.toString())
				.add(Projections.property("aac." + AacConsultas.Fields.DATA_CONSULTA.toString()), AltasAmbulatoriasPolVO.Fields.DT_CONSULTA.toString())
				
				.add(Projections.property("grd." + AacGradeAgendamenConsultas.Fields.SEQ.toString()), AltasAmbulatoriasPolVO.Fields.GRD_SEQ.toString())
				.add(Projections.property("grd." + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()), AltasAmbulatoriasPolVO.Fields.GRD_SER_VIN_CODIGO.toString())
				.add(Projections.property("grd." + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()), AltasAmbulatoriasPolVO.Fields.GRD_SER_MATRICULA.toString())
				
				.add(Projections.property("eqp." + AghEquipes.Fields.CODIGO.toString()), AltasAmbulatoriasPolVO.Fields.EQP_SEQ.toString())
				.add(Projections.property("eqp." + AghEquipes.Fields.RAP_SERVIDORES_ID_VIN_CODIGO.toString()), AltasAmbulatoriasPolVO.Fields.EQP_SER_VIN_CODIGO.toString())
				.add(Projections.property("eqp." + AghEquipes.Fields.RAP_SERVIDORES_ID_MATRICULA.toString()), AltasAmbulatoriasPolVO.Fields.EQP_SER_MATRICULA.toString())
				
				.add(Projections.property("esp." + AghEspecialidades.Fields.SEQ.toString()), AltasAmbulatoriasPolVO.Fields.ESP_SEQ.toString())
				
				//.add(Projections.property(""), AltasAmbulatoriasPolVO.Fields.ESP_NOME_ESPECIALIDADE.toString())//, mpmc_minusculo(esp1.nome_especialidade, 2)
				//.add(Projections.property(""), AltasAmbulatoriasPolVO.Fields.DATA_ORD.toString())//, trunc(atd.dthr_inicio)
				.add(Projections.property("esp." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), AltasAmbulatoriasPolVO.Fields.ESP_NOME_AGENDA.toString())//, mpmc_minusculo(esp.nome_especialidade, 2)
				.add(Projections.property("esp." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString()), AltasAmbulatoriasPolVO.Fields.ESP_SEQ_PAI.toString())//, esp1.seq
		);
		
		DetachedCriteria altaSum = DetachedCriteria.forClass(MamAltaSumario.class, "mam");
		altaSum.setProjection(Projections.property(MamAltaSumario.Fields.SEQ.toString()));
		altaSum.add(Restrictions.eqProperty(MamAltaSumario.Fields.CON_NUMERO.toString(), "aac." + AacConsultas.Fields.NUMERO.toString()));
		altaSum.add(Restrictions.in(MamAltaSumario.Fields.IND_PENDENTE.toString(), 
					Arrays.asList(DominioIndPendenteDiagnosticos.A,
							DominioIndPendenteDiagnosticos.E,
							DominioIndPendenteDiagnosticos.V)));
		altaSum.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));

		DetachedCriteria altaSum1 = DetachedCriteria.forClass(MamAltaSumario.class, "mam1");
		altaSum1.setProjection(Projections.property(MamAltaSumario.Fields.SEQ.toString()));
		altaSum1.add(Restrictions.eqProperty(MamAltaSumario.Fields.CON_NUMERO.toString(), "aac." + AacConsultas.Fields.NUMERO.toString()));
		altaSum1.add(Restrictions.eq(MamAltaSumario.Fields.IND_PENDENTE.toString(), DominioIndPendenteDiagnosticos.P));
		altaSum1.add(Restrictions.isNull(MamAltaSumario.Fields.DTHR_VALIDA_MVTO.toString()));
		altaSum1.add(Restrictions.isNull(MamAltaSumario.Fields.MAM_ALTA_SUMARIO.toString()));
		
		criteria.add(Restrictions.or(Subqueries.exists(altaSum), Subqueries.exists(altaSum1)));
		return criteria;
	}

	/**
	 * ORADB AGH.V_AIP_POL_AMBUL
	 * o JOIN 	and esp1.seq = decode(esp.esp_seq, null, esp.seq, esp.esp_seq)
	 * deve ser acessado via lazy. atd.especialidade.getEspecialidade()
	 * @return
	 */
	private DetachedCriteria getCriteriaPesquisarAltasAmbulatoriaisPol() {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "atd");
		
		criteria.createAlias(AghAtendimentos.Fields.CONSULTA.toString(), "aac");
		criteria.createAlias("aac." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");
		criteria.createAlias(AghAtendimentos.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("grd." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "eqp");
		
		return criteria;
	}
	
	public List<Object[]> listarAtendimentosPacienteAmbulatorioPorCodigo(Integer pacCodigo, Date inicio, Date fim) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.createAlias(AacConsultas.Fields.RETORNO.toString(), ALIAS_RETORNO);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "aliasGradeAgendamenConsultas");
		criteria.createAlias(AacConsultas.Fields.ATENDIMENTOS.toString(), "aliasAtendimentos");
		criteria.createAlias("aliasAtendimentos." + AghAtendimentos.Fields.UNIDADE_FUNCIONAL.toString(), "unidadeFuncional", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.between(AacConsultas.Fields.DATA_CONSULTA.toString(), inicio, fim));
		criteria.add(Restrictions.eq(ALIAS_RETORNO + "." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
		
		ProjectionList p = Projections.projectionList();
		p.add(Projections.property("aliasAtendimentos" + "." + AghAtendimentos.Fields.SEQ.toString()));
		p.add(Projections.property(AacConsultas.Fields.DATA_CONSULTA.toString()));
		p.add(Projections.property("aliasGradeAgendamenConsultas" + "." + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()));

		criteria.setProjection(p);
		
		return executeCriteria(criteria);
	}

	
	/**
	 * se paciente tem alguma consulta x dias para trás ou x dias para frente
	 * P_PAC_EM_ATENDIMENTO
	 * CURSOR c_consulta
	 * 
	 * @param pacCodigo
	 * @return Lista AacConsultas
	 * 
	 */
	public List<AacConsultas> verificarSePacienteTemConsulta(Integer pacCodigo, Integer numDiasPassado, 
			Integer numDiasFuturo, Integer paramReteronoConsAgendada ) {

		if(numDiasFuturo == null && numDiasPassado == null) {
			numDiasFuturo = 0;
			numDiasPassado = 0;
		}
		if (paramReteronoConsAgendada == null){
			paramReteronoConsAgendada = -1;
		}
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias("con.".concat(AacConsultas.Fields.RETORNO.toString()), "ret");
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(new Date()), (-1)*numDiasPassado)));
		criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.adicionaDias(DateUtil.obterDataComHoraFinal(new Date()), numDiasFuturo)));
		criteria.add(Restrictions.or(Restrictions.eq("ret.".concat(AacRetornos.Fields.IND_ABSENTEISMO.toString()), DominioIndAbsenteismo.R),
									 Restrictions.eq("ret.".concat(AacRetornos.Fields.SEQ.toString()), paramReteronoConsAgendada)));
		
		return executeCriteria(criteria);
	}

	/**
	 * DAOTest: AacConsultasDAOTest.obterNumeroDasConsultas (Retestar qd alterado) eSchweigert 17/09/2012
	 */
	public List<Integer> obterNumeroDasConsultas(final Integer pacCodigo, final Date dtTransplante, final Date dtInicio, final Date dtFim, final String ttrCodigo, final DominioIndAbsenteismo absenteismo){
		final StringBuilder hql = new StringBuilder(400);
		
		hql.append(" SELECT CON.").append(AacConsultas.Fields.NUMERO.toString())
		
		   .append("  FROM ")
		   .append(     FatTransplanteEspecialidade.class.getName()).append(" as TRS, ")
		   .append(     AacGradeAgendamenConsultas.class.getName()).append(" as GRD, ")
		   .append(     AacRetornos.class.getName()).append(" as RET, ")
		   .append(     AacConsultas.class.getName()).append(" as CON ")
		   
		   .append(" WHERE 1=1 " )
		   
		   .append("   AND RET.").append(AacRetornos.Fields.SEQ.toString()).append(" = CON.").append(AacConsultas.Fields.RETORNO_SEQ.toString())
		   .append("   AND GRD.").append(AacGradeAgendamenConsultas.Fields.SEQ.toString()).append(" = CON.").append(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString())
		   .append("   AND TRS.").append(FatTransplanteEspecialidade.Fields.ESP_SEQ.toString()).append(" = GRD.").append(AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString())

		   .append("   AND CON.").append(AacConsultas.Fields.PAC_CODIGO.toString()).append(" = :PRM_PAC_CODIGO ")
		   .append("   AND CON.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" >= :PRM_DT_TRANSPLANTE ")
		   .append("   AND CON.").append(AacConsultas.Fields.DATA_CONSULTA.toString()).append(" BETWEEN :PRM_DT_INICIO AND :PRM_DT_FIM ")
		   .append("   AND RET.").append(AacRetornos.Fields.IND_ABSENTEISMO.toString()).append(" = :PRM_IND_ABSENTEISMO ")
		   .append("   AND TRS.").append(FatTransplanteEspecialidade.Fields.TTR_CODIGO.toString()).append(" = :PRM_TTR_CODIGO ");
		
		final org.hibernate.Query query = createHibernateQuery(hql.toString());

		query.setParameter("PRM_PAC_CODIGO", pacCodigo);
		query.setParameter("PRM_DT_TRANSPLANTE", dtTransplante);
		query.setParameter("PRM_DT_INICIO", dtInicio);
		query.setParameter("PRM_DT_FIM", dtFim);
		query.setParameter("PRM_TTR_CODIGO", ttrCodigo);
		query.setParameter("PRM_IND_ABSENTEISMO", absenteismo);
		
		return query.list();
	}

	/**
	 * ORADB P_IMPRIME_AVAL_ANESTESIA
	 * 
	 * @return retorna quey principal dos cursos da procedure
	 */
	private DetachedCriteria obterConsultasDaGradeAgendPorPacientePorEsp(final Integer paciente, final Short especiliada){

		final String aliasCon = "CON";
		final String aliasGrd = GRD2;
		final String aliasEsp = "ESP";
		final String ponto = ".";
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, aliasCon);
		criteria.createAlias(aliasCon + ponto + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), aliasGrd);
		criteria.createAlias(aliasGrd + ponto + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), aliasEsp);
		
		if(paciente != null){
			criteria.add(Restrictions.eq(aliasCon + ponto + AacConsultas.Fields.PAC_CODIGO.toString(), paciente));
		}
		
		if(especiliada != null){
			criteria.add(Restrictions.eq(aliasEsp + ponto + AghEspecialidades.Fields.SEQ.toString(), especiliada));
		}
		
		criteria.addOrder(Order.desc(aliasCon + ponto + AacConsultas.Fields.DATA_CONSULTA.toString()));
		
		return criteria;
	}
	
	/**
	 * ORADB P_IMPRIME_AVAL_ANESTESIA
	 * CURSOR c_ana 
	 * @param (c_pac_codigo aac_consultas.pac_codigo%type)
	 * @return List<AacConsultas>
	 */
	
	public List<AacConsultas> obterConsultaAnamnesesPorDataConsPacEspIndPendente(
			final Date dataCorrente, 
			final Integer paciente,
			final Short especiliada, 
			final DominioIndPendenteAmbulatorio indPendente) {
		
		final String aliasCon = "CON";
		final String aliasAna = "ANA";
		final String ponto = ".";

		DetachedCriteria criteria = obterConsultasDaGradeAgendPorPacientePorEsp(paciente, especiliada);

		criteria.createAlias(aliasCon + ponto + AacConsultas.Fields.ANAMNESES.toString(), aliasAna);
		
		criteria.add(Restrictions.eq(aliasAna + ponto + MamAnamneses.Fields.IND_PENDENTE.toString(), indPendente));
		criteria.add(Restrictions.isNull(aliasAna + ponto + MamAnamneses.Fields.DTHR_VALIDA_MVTO.toString()));

		if(dataCorrente != null){
			criteria.add(Restrictions.lt(aliasCon + ponto + AacConsultas.Fields.DATA_CONSULTA.toString(), dataCorrente));
		}
		
		return executeCriteria(criteria);
	}
	
	/**
	 * ORADB P_IMPRIME_AVAL_ANESTESIA
	 * CURSOR c_evo
	 * @param (c_pac_codigo aac_consultas.pac_codigo%type, c_dt_consulta aac_consultas.dt_consulta%type) 
	 * @return List<AacConsultas>
	 */
	public List<AacConsultas> obterConsultaEvolucoesPorDataConsPacEspIndPendente(
			final Date dataConsulta, 
			final Integer paciente,
			final Short especiliada, 
			final DominioIndPendenteAmbulatorio indPendente) {
		
		final String aliasCon = "CON";
		final String aliasEvo = "EVO";
		final String ponto = ".";

		DetachedCriteria criteria = obterConsultasDaGradeAgendPorPacientePorEsp(paciente, especiliada);
		criteria.createAlias(aliasCon + ponto + AacConsultas.Fields.EVOLUCOES.toString(), aliasEvo);

		criteria.add(Restrictions.eq(aliasEvo + ponto + MamEvolucoes.Fields.IND_PENDENTE.toString(), indPendente));
		criteria.add(Restrictions.isNull(aliasEvo + ponto + MamEvolucoes.Fields.DTHR_VALIDA_MVTO.toString()));

		if (dataConsulta != null) {
			criteria.add(Restrictions.gt(aliasCon + ponto + AacConsultas.Fields.DATA_CONSULTA.toString(), dataConsulta));
		}

		return executeCriteria(criteria);
	}
	
	public void tirarPermissaoLeitura(AacConsultas consulta) {
		this.setReadOnly(consulta, false);
	}

	public List<AacConsultas> pesquisarConsultasPorPacientePOL(
			AipPacientes paciente) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class)
				.add(Restrictions.eq(AacConsultas.Fields.PACIENTE.toString(), paciente))
				.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));

		criteria.createCriteria(AacConsultas.Fields.RETORNO.toString())
				.add(Restrictions.or(
						Restrictions.eq(AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R),
						Restrictions.eq(AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.P)));
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(),
				FetchMode.JOIN);
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()
						+ "."
						+ AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE
								.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()
						+ "."
						+ AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE
								.toString() + "."
						+ AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(),
				FetchMode.JOIN);
		
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()
						+ "."
						+ AacGradeAgendamenConsultas.Fields.EQUIPE
								.toString(), FetchMode.JOIN);
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()
						+ "."
						+ AacGradeAgendamenConsultas.Fields.EQUIPE
								.toString() + "."
						+ AghEquipes.Fields.PROFISSIONAL_RESPONSAVEL.toString(),
				FetchMode.JOIN);		
		
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()
						+ "."
						+ AacGradeAgendamenConsultas.Fields.ESPECIALIDADE
								.toString(), FetchMode.JOIN);
		
		
		criteria.setFetchMode(
				AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString()
						+ "."
						+ AacGradeAgendamenConsultas.Fields.ESPECIALIDADE
								.toString() + "." + AghEspecialidades.Fields.CENTRO_CUSTO.toString(), FetchMode.JOIN);

		List<AacConsultas> listaConsultasVolta = executeCriteria(criteria);

		return listaConsultasVolta;
	}
	
	public Long pesquisarConsultasPorEspecialidade(Short espSeq, List<Integer> consultasPacientesEmAtendimento){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CONSUL");
		criteria.createAlias("CONSUL." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA , "GRADE_CONSUL");
		criteria.add(Restrictions.eq("GRADE_CONSUL." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		criteria.add(Restrictions.in("CONSUL." + AacConsultas.Fields.NUMERO.toString() , consultasPacientesEmAtendimento));
		criteria.setProjection(Projections.property(AacConsultas.Fields.NUMERO.toString()));

		return this.executeCriteriaCount(criteria);
	}
	
	public List<AacConsultas> obterConsultasPorNumero(List<Integer> numeros) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.in(AacConsultas.Fields.NUMERO.toString(), numeros));
		
		return executeCriteria(criteria);
	}
	
	
	/**
	 * Pesquisar consulta por número de consulta e especialidade
	 * @param conNumero
	 * @param espSeq
	 * @return
	 */
	
	public List<AacConsultas> pesquisarPorConsultaPorNumeroConsultaEspecialidade(List<Integer> conNumero, Short espSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.add(Restrictions.in("CON."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		if (espSeq != null){
			criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString() , GRD2);
			criteria.add(Restrictions.eq(GRD+AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		}
		return executeCriteria(criteria);
	}
	
	/**
	 * Buscar consulta do paciente tem consulta no CO
	 * 
	 * Web Service #36972
	 * 
	 * @param pacCodigo
	 * @param unfSeq1
	 * @param unfSeq2
	 * @return
	 */
	public List<Integer> pesquisarPorPacienteUnidadeFuncional(final Integer pacCodigo, final Short unfSeq1, final Short unfSeq2) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRD2);		
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), PAC);
		criteria.add(
				Restrictions.or(
						Restrictions.eq(GRD + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString(), unfSeq1),
						Restrictions.eq(GRD + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString(), unfSeq2)
				)
		);
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.le("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.adicionaDiasFracao(new Date(), 0.21f)));
		criteria.addOrder(Order.desc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.setProjection(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()));
		return executeCriteria(criteria);
	}
	
	/**
	 * Buscar a data da consulta anterior a consulta atual, sendo informado o código do paciente e o sequencial da gestação.
	 * 
	 * Web Service #37687
	 * 
	 * @param pacCodigo
	 * @param conNumero
	 * @param gsoSeqp
	 * @return
	 */
	public List<Date> pesquisarPorPacienteConsultaGestacao(Integer pacCodigo, Integer conNumero, Short gsoSeqp) {

		// FROM
		DetachedCriteria criteria = DetachedCriteria.forClass(AghAtendimentos.class, "ATD");

		// JOIN
		criteria.createAlias("ATD." + AghAtendimentos.Fields.CONSULTA.toString(), "CON");
		criteria.createAlias("ATD." + AghAtendimentos.Fields.PACIENTE.toString(), PAC);

		// WHERE
		criteria.add(Restrictions.eqProperty("ATD." + AghAtendimentos.Fields.GSO_PAC_CODIGO.toString(), "PAC." + AipPacientes.Fields.CODIGO.toString()));
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.ne("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq("ATD." + AghAtendimentos.Fields.GSO_SEQP.toString(), gsoSeqp));

		// ORDER BY
		criteria.addOrder(Order.desc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));

		// SELECT
		criteria.setProjection(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));

		return super.executeCriteria(criteria);
	}
	
	public Short obtemCodigoEspecialidadeGradePeloNumeroConsulta(Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRD2);
		
		criteria.setProjection(Projections.property(GRD + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		return (Short) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AacConsultas> obterCodCentralPorPacEsp(Integer pacCodigo, Short espSeq) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.RETORNO.toString(), "RET");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), GRD2);
		criteria.createAlias(GRD + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.isNotNull("CON." + AacConsultas.Fields.COD_CENTRAL.toString()));
		criteria.add(Restrictions.eq("RET." + AacRetornos.Fields.IND_ABSENTEISMO.toString(), DominioIndAbsenteismo.R));
		
		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghEspecialidades.class, "ESP_INF");
		subCriteria.add(Restrictions.eq("ESP_INF." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		subCriteria.add(Restrictions.or(
				Restrictions.eq(GRD + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq),
				Restrictions.eqProperty("ESP." + AghEspecialidades.Fields.ESP_SEQ.toString(),
						"ESP_INF." + AghEspecialidades.Fields.ESPECIALIDADE_GENERICA_SEQ.toString())));
		
		subCriteria.setProjection(Projections.property("ESP_INF." + AghEspecialidades.Fields.SEQ.toString()));
		
		criteria.add(Subqueries.exists(subCriteria));
		
		return executeCriteria(criteria);
	}
	
	public int existeDependenciaComRetorno(AacRetornos retornoConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class); 
		criteria.add(Restrictions.eq(AacConsultas.Fields.RETORNO.toString(), retornoConsulta));
		return executeCriteria(criteria).size();
	}	

	public List<AacConsultas> pesquisarConsultasAnterioresPacienteByEspecialidade(Integer consultaAtual, Integer codPaciente, Date dtInicio, Date dtFim, Short espSeq, Integer retSeq){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "gradeAgenda",JoinType.INNER_JOIN);
		criteria.createAlias("gradeAgenda."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "especialidade", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("gradeAgenda."+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "equipe", JoinType.LEFT_OUTER_JOIN);
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), codPaciente));
		if(espSeq!=null){
			criteria.add(Restrictions.eq("gradeAgenda." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), espSeq));
		}
		if(dtFim==null){
			dtFim = DateUtil.truncaDataFim(new Date());
		}else{
			dtFim = DateUtil.truncaDataFim(dtFim);
			criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim));
		}
		
		if(dtInicio!=null){
			criteria.add(Restrictions.ge(AacConsultas.Fields.DATA_CONSULTA.toString(), dtInicio));
		}else{
			criteria.add(Restrictions.le(AacConsultas.Fields.DATA_CONSULTA.toString(), dtFim));
		}
		if(consultaAtual!=null){
			criteria.add(Restrictions.ne(AacConsultas.Fields.NUMERO.toString(), consultaAtual));
		}
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(),"M"));
		criteria.add(Restrictions.eq(AacConsultas.Fields.RETORNO_SEQ.toString(),retSeq));
		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		return executeCriteria(criteria);
	}
	/**
	 * Web Service #37234
	 * Utilizado na estória #26325
	 * @param numeroConsulta
	 * @return atd_seq
	 */
	public Integer obterAtdSeqPorNumeroConsulta(Integer numeroConsulta){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.setProjection(Projections.property(AacConsultas.Fields.ATD_SEQ.toString()));
		criteria.add(Restrictions.eq(AacConsultas.Fields.CON_NUMERO.toString(), numeroConsulta));
		
		return (Integer) executeCriteriaUniqueResult(criteria);
	}
	
	
	/**
	 * Pesquisa consultas pelo Mês e Ano
	 *  
	 * @param firstResult
	 * @param maxResult
	 * @param orderProperty
	 * @param asc
	 * @param aacConsultas
	 * @return
	 */
	public List<ConsultasDeOutrosConveniosVO> pesquisarConsultasDeOutrosConvenios(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, Date mesAno) {
		DetachedCriteria criteria = getCriteriaProntuarioOuCodigo(mesAno, false);

		if (orderProperty == null) {
			criteria.addOrder(Order.desc("AAC." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		}

		return this.executeCriteria(criteria, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * 
	 * @param aacConsultas
	 * @return
	 */
	public Long pesquisarConsultasDeOutrosConveniosCount(Date mesAno) {
		DetachedCriteria criteria = getCriteriaProntuarioOuCodigo(mesAno, true);
		return (Long) this.executeCriteriaUniqueResult(criteria);
	}

	/**
	 * Criteria para Pesquisa Pesquisar Consultas de Outros Convênvios pelo Mês e Ano
	 * 
	 * @param filtro
	 * @return
	 */
	private DetachedCriteria getCriteriaProntuarioOuCodigo(Date mesAno, boolean rowCount) {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "AAC");
		

		criteria.createAlias("AAC." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA_ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.RETORNO.toString(), "RET", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.FAT_CONVENIO_SAUDE.toString(), "CNV", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.CONVENIO_SAUDE_PLANO.toString(), "CSP", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.SERVIDOR.toString(), "SER", JoinType.INNER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.VINCULO.toString(), "VIN", JoinType.INNER_JOIN);
		criteria.createAlias("AAC." + AacConsultas.Fields.SERVIDOR_PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		ProjectionList projList = Projections.projectionList();
        projList.add(Projections.property("AAC." + AacConsultas.Fields.DATA_CONSULTA.toString()), ConsultasDeOutrosConveniosVO.Fields.DATA_CONSULTA.toString());
        projList.add(Projections.property("AAC." + AacConsultas.Fields.NUMERO.toString()), ConsultasDeOutrosConveniosVO.Fields.NUMERO.toString());
        projList.add(Projections.property("ESP." + AghEspecialidades.Fields.SIGLA.toString()), ConsultasDeOutrosConveniosVO.Fields.ESP_SIGLA.toString());
        projList.add(Projections.property("RET." + AacRetornos.Fields.DESCRICAO.toString()), ConsultasDeOutrosConveniosVO.Fields.RET_DESCRICAO.toString());
        projList.add(Projections.property("PAC." + AipPacientes.Fields.CODIGO.toString()), ConsultasDeOutrosConveniosVO.Fields.PAC_CODIGO.toString());
        projList.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), ConsultasDeOutrosConveniosVO.Fields.PAC_PRONTUARIO.toString());
        projList.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), ConsultasDeOutrosConveniosVO.Fields.PAC_NOME.toString());
        projList.add(Projections.property("CSP." + FatConvenioSaudePlano.Fields.CNV_CODIGO.toString()), ConsultasDeOutrosConveniosVO.Fields.CSP_CNV_CODIGO.toString());
        projList.add(Projections.property("CNV." + FatConvenioSaude.Fields.DESCRICAO.toString()), ConsultasDeOutrosConveniosVO.Fields.CNV_DESCRICAO.toString());
        projList.add(Projections.property("CSP." + FatConvenioSaudePlano.Fields.SEQ.toString()), ConsultasDeOutrosConveniosVO.Fields.CSP_SEQ.toString());
        projList.add(Projections.property("CSP." + FatConvenioSaudePlano.Fields.DESCRICAO.toString()), ConsultasDeOutrosConveniosVO.Fields.CSP_DESCRICAO.toString());
        projList.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), ConsultasDeOutrosConveniosVO.Fields.SER_VIN_CODIGO.toString());
        projList.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), ConsultasDeOutrosConveniosVO.Fields.SER_MATRICULA.toString());
        projList.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ConsultasDeOutrosConveniosVO.Fields.PEC_NOME.toString());
		
        criteria.setProjection(projList);
        if (rowCount) {
        	criteria.setProjection(Projections.count("AAC." + AacConsultas.Fields.NUMERO.toString()));
        }
		criteria.add(Restrictions.eq("CSP." + FatConvenioSaudePlano.Fields.IND_TIPO_PLANO.toString(), DominioSituacaoInterconsultas.A));

		DetachedCriteria subquery = DetachedCriteria.forClass(
				AghParametros.class, "PAR");
		subquery.add(Restrictions.in(AghParametros.Fields.NOME.toString(),
				new Object[] { AghuParametrosEnum.P_CONV_PADRAO.toString(),
						AghuParametrosEnum.P_CONVENIO_UFRGS.toString(),
						AghuParametrosEnum.P_CONVENIO_HCPA.toString() }));
		subquery.setProjection(Projections
				.property(AghParametros.Fields.VLR_NUMERICO.toString()));
		
		if (mesAno != null) {
			
			Date primeiroDiaMes = DateUtil.obterDataInicioCompetencia(mesAno);
			Date ultimoDiaMes = DateUtil.obterDataFimCompetencia(mesAno);
			criteria.add(Restrictions.between("AAC." + AacConsultas.Fields.DATA_CONSULTA.toString(), primeiroDiaMes, ultimoDiaMes));
		}
		
		criteria.add(Restrictions.eq("AAC." + AacConsultas.Fields.SITUACAO_CONSULTA_SIT.toString(), DominioSituacaoInterconsultas.M.toString()));
		
		criteria.add(Subqueries.propertyNotIn("AAC." + AacConsultas.Fields.CSP_CNV_CODIGO.toString(), subquery));
		
		if (!rowCount) {
			criteria.setResultTransformer(Transformers.aliasToBean(ConsultasDeOutrosConveniosVO.class));
		}
		
		return criteria;
	}

	public AacConsultas obterConsultaPorAgendamentoSeq(Integer codAtendimento) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "AAC");
		
		criteria.add(Restrictions.eq("AAC." + AacConsultas.Fields.ATD_SEQ.toString(), codAtendimento));
		
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}
	
	public List<AacConsultas> pesquisarPorNumeroEProntuario(Integer numero, Integer prontuario) {
        
       final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
       criteria.createAlias("C."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G", JoinType.INNER_JOIN);
       criteria.createAlias("G."+AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "U", JoinType.INNER_JOIN);
       criteria.createAlias("G."+AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "E", JoinType.INNER_JOIN);
       criteria.createAlias("G."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ES", JoinType.LEFT_OUTER_JOIN);
       criteria.createAlias("C."+AacConsultas.Fields.PACIENTE.toString(), "P", JoinType.INNER_JOIN);
       
       criteria.add(Restrictions.eq("C."+AacConsultas.Fields.NUMERO.toString(), numero));
       criteria.add(Restrictions.eq("P."+AipPacientes.Fields.PRONTUARIO.toString(), prontuario));
       
       return this.executeCriteria(criteria);
       
	}
	
	/**
	 * #27521 #8236 #8233 C1
	 * Query para obter dados do cabeçalho do relatório de Agenda de Consultas.
	 * @param dataInicio 
	 * @param dataFim
	 * @param grade
	 * @param especialidade
	 * @param unidadeFuncional
	 * @param turno
	 * @return Lista de objetos com as informações para o cabeçalho do relatório.
	 * @throws ApplicationBusinessException 
	 */
	public List<CabecalhoRelatorioAgendaConsultasVO> obterDadosCabecalhoRelatorioAgenda (Date dataInicio, Date dataFim, Integer grade, Short especialidade, Short unidadeFuncional, Integer turno) throws ApplicationBusinessException {
		final DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		//JOIN
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.IND_SIT_CONSULTA.toString(), "SIT", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA, "UND", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

		// SELEÇÃO DOS CAMPOS QUE SERÃO RETORNADOS NA CONSULTA
		criteria.setProjection(Projections
				.projectionList()
				.add(Projections.groupProperty("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.DATA_CONSULTA.toString()))
				.add(Projections.groupProperty("UND." + VAacSiglaUnfSala.Fields.DESCRICAO.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.DESCRICAO_SETOR.toString()))
				.add(Projections.groupProperty("UND." + VAacSiglaUnfSala.Fields.SALA.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.SALA.toString()))
				.add(Projections.groupProperty("ESP." + AghEspecialidades.Fields.SEQ.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.SEQ_ESPECIALIDADE.toString()))
				.add(Projections.groupProperty("ESP." + AghEspecialidades.Fields.NOME.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.NOME_ESPECIALIDADE.toString()))
				.add(Projections.groupProperty("EQP." + AghEquipes.Fields.NOME.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.NOME_EQUIPE.toString()))
				.add(Projections.groupProperty("PES." + RapPessoasFisicas.Fields.NOME.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.NOME_MEDICO.toString()))
				.add(Projections.groupProperty("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()).
						as(CabecalhoRelatorioAgendaConsultasVO.Fields.SEQ_GRADE.toString()))
				.add(Projections.groupProperty("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString()).
								as(CabecalhoRelatorioAgendaConsultasVO.Fields.SEQ_UND_FUNCIONAL.toString()))
				);
		
						
		//WHERE
		criteria.add(Restrictions.in("SIT." + AacSituacaoConsultas.Fields.SITUACAO.toString(), new String[]{"M","L"}));
		
		if (dataInicio != null){ 
			if(dataFim != null){
				criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), 
						DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataFim)));
			} else {
				criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), 
						DateUtil.obterDataComHoraInical(dataInicio), DateUtil.obterDataComHoraFinal(dataInicio)));
			}
		}
		
		if (grade != null){
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString(), grade));
		}
		
		if (especialidade != null){
			criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SEQ.toString(), especialidade));
		}
		
		if(unidadeFuncional != null){
			criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeFuncional));
		}
		
		if(turno != null){
			String parametroTurno = StringUtils.EMPTY;
			
			if(turno == DominioTurno.M.getCodigo()){
				
				parametroTurno = "'00:00:00' and '11:59:59'";
			}else if (turno == DominioTurno.T.getCodigo()){
				
				parametroTurno = "'12:00:00' and '15:59:59'";
				
			}else if (turno == DominioTurno.N.getCodigo()){
				parametroTurno = "'16:00:00' and '23:59:59'";
				
			}
			
			criteria.add(Restrictions.sqlRestriction("to_char(this_.dt_consulta,'HH24:MI:SS' ) between " + parametroTurno));
		}
		
		//ORDER BY
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.addOrder(Order.asc("UND." + VAacSiglaUnfSala.Fields.SALA.toString()));
		criteria.addOrder(Order.asc("ESP." + AghEspecialidades.Fields.NOME.toString()));
		criteria.addOrder(Order.asc("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()));
		
		try {
			criteria.setResultTransformer(new AliasToBeanConstructorResultTransformer(CabecalhoRelatorioAgendaConsultasVO.class.getConstructor(
						Date.class, String.class,Byte.class,Short.class,String.class,String.class,String.class,Integer.class, Short.class)));
		} catch (NoSuchMethodException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		} catch (SecurityException e) {
			throw new ApplicationBusinessException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e);
		}
						
		return this.executeCriteria(criteria);
	}
	
	//Querys da estória 40299------------------------------------------------------------------------------------------
	
	/**
	 * 
	 * @param numero
	 * @param seq_pesquisa
	 * @return
	 * /**
	 * 
	 Estoria:40299 Procedure: c5
	 */
	public boolean verificaConsultaPorNumeroFormaAgendamento(Integer numero,Integer seqPesquisa){
		
		DetachedCriteria criteria =  DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numero));
		criteria.add(Restrictions.eq(AacConsultas.Fields.FAG_PGD_SEQ.toString(), seqPesquisa.shortValue()));
		
		if(executeCriteria(criteria).isEmpty()){
			return false;			
		}
		
		return true;
		
	}


	/**
	 * 
	 * @param numero
	 * @return
	 /**
	 * 
	 Estoria:40299 Procedure: c6
	 */
	public Date obterAacConsultasDataConsultaPorNumero(Integer numero){
		
		DetachedCriteria criteria =  DetachedCriteria
				.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(),numero));
		criteria.setProjection(Projections.property(AacConsultas.Fields.DATA_CONSULTA.toString()));
		Date aacConsultasData = (Date) executeCriteriaUniqueResult(criteria);
		
		return aacConsultasData;		
	}

	/**
	 * 
	 * @param numero
	 * @return
	 * /**
	 * 
	 Estoria:40299 Procedure: c8
	 */
	public Integer obterNumeroConsultaPacCodigoPorNumero(Integer numero){
				
		DetachedCriteria criteria =  DetachedCriteria
				.forClass(AacConsultas.class);
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(),numero));
		criteria.setProjection(Projections.property(AacConsultas.Fields.PAC_CODIGO.toString()));
		
		Integer aacConsultasCodigo =  (Integer) executeCriteriaUniqueResult(criteria);
		
		return aacConsultasCodigo;
	}

	/**
	 * 
	 * @param esp_seq
	 * @return
	 * Estoria: 40229:  Consulta c16
	 */
	public String obterDescricaoCidCapitalizada(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.INNER_JOIN);
		
		//Projeções-
		criteria.setProjection(Projections.projectionList().add(Projections.property("PAC."+AipPacientes.Fields.NOME.toString()))
				.add(Projections.property("PAC."+AipPacientes.Fields.PRONTUARIO.toString()))
				.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()))
				.add(Projections.property("ESP."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()))
				.add(Projections.property("EQP."+AghEquipes.Fields.NOME.toString())));
				
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));				
				
		Object[]  aacConsultas = (Object[]) executeCriteriaUniqueResult(criteria);
		
		//concatenar resultado como String
		String descricaoCidCapitalizada ="paciente "+((String) aacConsultas[0]).toLowerCase()
				+", prontuário "+aacConsultas[1]+", "
				+((String) aacConsultas[2]).toLowerCase()+", "
				+((String) aacConsultas[3]).toLowerCase()+", Equipe "
				+((String) aacConsultas[4]).toLowerCase();
		
		return descricaoCidCapitalizada;
	}

	/**
	 * Realiza a busca por consultas pendentes.
	 * 
	 * @param usuario - Usuário logado
	 * @param data - Data pesquisada
	 * @param especialidade - Especialidade
	 * @param equipe - Equipe
	 * @param zona - Zona
	 * @param sala - Sala da consulta
	 * @param profissional - Profissional que realizou a consulta
	 * @param turno - Turno da consulta
	 * @param paramSeqSituacaoAtendimento - Valor do parâmetro P_SEQ_SIT_EM_ATEND
	 * @param paramDiasReabrirPendente - Valor do parâmetro P_DIAS_REABRIR_PENDENTE
	 * 
	 * @return Lista de consultas pendentes
	 */
	public List<PesquisarConsultasPendentesVO> pesquisarConsultasPendentes(RapServidores usuario, Date data, AghEspecialidades especialidade, AghEquipes equipe,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional, DataInicioFimVO turno, Short paramSeqSituacaoAtendimento,
			Integer paramDiasReabrirPendente) {

		List<PesquisarConsultasPendentesVO> retorno = new ArrayList<PesquisarConsultasPendentesVO>();
		
		retorno.addAll(pesquisarConsultasPendentesPacienteEmAtendimento(usuario, especialidade, equipe, zona, sala, profissional, turno,
				paramSeqSituacaoAtendimento, paramDiasReabrirPendente));
		retorno.addAll(pesquisarConsultasPendentesPacienteAguardandoOuPendenciaUsuario(data, especialidade, equipe, zona, sala, profissional, turno,
				paramSeqSituacaoAtendimento));
		retorno.addAll(pesquisarConsultasPendentesPendenciaOutroUsuario(usuario, data, especialidade, equipe, zona, sala, profissional, turno,
				paramSeqSituacaoAtendimento));

		Comparator<PesquisarConsultasPendentesVO> voComparator = new Comparator<PesquisarConsultasPendentesVO>() {
			
			@Override
			public int compare(PesquisarConsultasPendentesVO o1, PesquisarConsultasPendentesVO o2) {

					if (o1 == null) {
						if (o2 == null) {
							return 0;
						}
						return -1;
					} else if (o2 == null) {
						return 1;
					} else if (o1.getNome() == null) {
						if (o2.getNome() == null) {
							return 0;
						}
						return -1;
					} else if (o2.getNome() == null) {
						return 1;
					}

					return o1.getNome().compareToIgnoreCase(o2.getNome());
			}
		};
		
		Collections.sort(retorno, voComparator);
		
		return retorno;
	}

	/**
	 * Realiza a primeira parte da busca por consultas pendentes. Busca por consultas em atendimento.
	 * 
	 * @param usuario - Usuário logado
	 * @param data - Data pesquisada
	 * @param especialidade - Especialidade
	 * @param equipe - Equipe
	 * @param zona - Zona
	 * @param sala - Sala da consulta
	 * @param profissional - Profissional que realizou a consulta
	 * @param turno - Turno da consulta
	 * @param paramSeqSituacaoAtendimento - Valor do parâmetro P_SEQ_SIT_EM_ATEND
	 * @param paramDiasReabrirPendente - Valor do parâmetro P_DIAS_REABRIR_PENDENTE
	 * 
	 * @return Lista de consultas pendentes em atendimento
	 */
	private List<PesquisarConsultasPendentesVO> pesquisarConsultasPendentesPacienteEmAtendimento(RapServidores usuario, AghEspecialidades especialidade,
			AghEquipes equipe, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional, DataInicioFimVO turno,
			Short paramSeqSituacaoAtendimento, Integer paramDiasReabrirPendente) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");

		ProjectionList projections = Projections.projectionList();
		
		projections.add(Projections.sqlProjection("2 as prioridade", new String[]{"prioridade"}, new Type[]{IntegerType.INSTANCE}));
		projections.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()), "numero");
		projections.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()), "dtConsulta");
		projections.add(Projections.property("CON." + AacConsultas.Fields.DTHR_INICIO.toString()), "dtHrInicioConsulta");
		projections.add(Projections.property("CAA." + AacCondicaoAtendimento.Fields.SEQ.toString()), "caaSeq");
		projections.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "nome");
		projections.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
		projections.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), "serVinCodigo");
		projections.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), "serMatricula");
		projections.add(Projections.property("CTL." + MamControles.Fields.MIC_NOME.toString()), "micNome");
		projections.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()), "grdSeq");
		projections.add(Projections.property("EXC." + MamExtratoControles.Fields.DTHR_MOVIMENTO.toString()), "hrInicioAtendimento");
		projections.add(Projections.property("USL." + AacUnidFuncionalSalas.Fields.SALA.toString()), "salaMicro");
		projections.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel");
		projections.add(Projections.property("PPF." + RapPessoasFisicas.Fields.NOME.toString()), "profissional");
		projections.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade");
		projections.add(Projections.property("EQP." + AghEquipes.Fields.NOME.toString()), "equipe");

		criteria.setProjection(projections);

		criteria.createAlias("CON." + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA");
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("CON." + AacConsultas.Fields.CONTROLE.toString(), "CTL");
		criteria.createAlias("CTL." + MamControles.Fields.SERVIDOR_RESPONSAVEL.toString(), "SER");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "PRE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRE." + RapServidores.Fields.PESSOA_FISICA.toString(), "PPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.SITUACAO_ATENDIMENTOS.toString(), "SAT");
		criteria.createAlias("CTL." + MamControles.Fields.EXTRATO_CONTROLES.toString(), "EXC", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_SALA_MICRO
		criteria.createAlias("CTL." + MamControles.Fields.MICROCOMPUTADOR.toString(), "MIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MIC." + AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_RESPONSAVEL
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_HR_INI_ATD
		DetachedCriteria subHrIniAtd = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subHrIniAtd.setProjection(Projections.max("SEC." + MamExtratoControles.Fields.SEQP.toString()));
		
		subHrIniAtd.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT", JoinType.LEFT_OUTER_JOIN);
		subHrIniAtd.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA", JoinType.LEFT_OUTER_JOIN,
				Restrictions.and(Restrictions.eq("SSA." + MamSituacaoAtendimentos.Fields.SEQ.toString(), paramSeqSituacaoAtendimento)));
		
		subHrIniAtd.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));

		criteria.add(Restrictions.or(Restrictions.isNull("EXC." + MamExtratoControles.Fields.SEQP.toString()),
				Subqueries.propertyEq("EXC." + MamExtratoControles.Fields.SEQP.toString(), subHrIniAtd)));

		criteria.add(Restrictions.in("SAT." + MamSituacaoAtendimentos.Fields.SEQ.toString(),
				new Object[]{(short) 2, (short) 3, (short) 4, (short) 5, (short) 6, (short) 13}));
		criteria.add(Restrictions.eq("SAT." + MamSituacaoAtendimentos.Fields.SITUACAO.toString(), DominioSituacao.A));
		criteria.add(Restrictions.eq("SAT." + MamSituacaoAtendimentos.Fields.ORIGEM.toString(), DominioSituacao.A));

		Date dataMovimento = DateUtil.adicionaDias(DateUtil.obterDataComHoraInical(null), -1 * paramDiasReabrirPendente);
		
		criteria.add(Restrictions.gt("CTL." + MamControles.Fields.DTHR_MOVIMENTO.toString(), dataMovimento));
		
		if (usuario != null && usuario != null) {
			if (usuario.getId().getVinCodigo() != null) {
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), usuario.getId().getVinCodigo()));
			}
			
			if (usuario.getId().getMatricula() != null) {
				criteria.add(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), usuario.getId().getMatricula()));
			}
		}
		
		criteria.add(Restrictions.gt("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataMovimento));

		// @ORADB MAMC_GET_ATEND_PEND
		DetachedCriteria subGetAtendPend = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subGetAtendPend.setProjection(Projections.property("SSA." + MamSituacaoAtendimentos.Fields.ATEND_PEND.toString()));
		
		subGetAtendPend.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT");
		subGetAtendPend.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA");
		
		subGetAtendPend.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));
		
		DetachedCriteria subSubGetAtendPend = DetachedCriteria.forClass(MamExtratoControles.class, "SSE");
		
		subSubGetAtendPend.setProjection(Projections.max("SSE." + MamExtratoControles.Fields.SEQP.toString()));
		
		subSubGetAtendPend.createAlias("SSE." + MamExtratoControles.Fields.CONTROLE.toString(), "SSC");

		subSubGetAtendPend.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "SSC." + MamControles.Fields.SEQ.toString()));
		
		subGetAtendPend.add(Subqueries.propertyEq("SEC." + MamExtratoControles.Fields.SEQP.toString(), subSubGetAtendPend));
		criteria.add(Subqueries.eq(DominioSimNao.S.isSim(), subGetAtendPend));

		// @ORADB AACC_DEFINE_TURNO
		if (turno != null && turno.getDataInicial() != null && turno.getDataFinal() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String dataInicioTurno = sdf.format(turno.getDataInicial());
			String dataFimTurno = sdf.format(turno.getDataFinal());
			
			criteria.add(Restrictions.sqlRestriction("to_char({alias}.DT_CONSULTA, 'hh24:mi:ss') between ? and ?", new Object[] {dataInicioTurno, dataFimTurno},
					new Type[] {StringType.INSTANCE, StringType.INSTANCE}));
		}

		adicionarSubqueryGradeAgendamentoPesquisarConsultasPendentes(criteria, especialidade, equipe, zona, sala, profissional);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisarConsultasPendentesVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * Realiza a segunda parte da busca por consultas pendentes. Busca por consultas aguadando atendimento ou pendende por usuário.
	 * 
	 * @param data - Data pesquisada
	 * @param especialidade - Especialidade
	 * @param equipe - Equipe
	 * @param zona - Zona
	 * @param sala - Sala da consulta
	 * @param profissional - Profissional que realizou a consulta
	 * @param turno - Turno da consulta
	 * @param paramSeqSituacaoAtendimento - Valor do parâmetro P_SEQ_SIT_EM_ATEND
	 * 
	 * @return Lista de consultas aguardando atendimento e com pendências do usuário
	 */
	private List<PesquisarConsultasPendentesVO> pesquisarConsultasPendentesPacienteAguardandoOuPendenciaUsuario(Date data, AghEspecialidades especialidade,
			AghEquipes equipe, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional, DataInicioFimVO turno,
			Short paramSeqSituacaoAtendimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");

		ProjectionList projections = Projections.projectionList();
		
		projections.add(Projections.sqlProjection("case sa2x11_.IND_AGUARDANDO when 'S' then 1 else 4 end as prioridade", new String[]{"prioridade"},
				new Type[]{IntegerType.INSTANCE}));
		projections.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()), "numero");
		projections.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()), "dtConsulta");
		projections.add(Projections.property("CON." + AacConsultas.Fields.DTHR_INICIO.toString()), "dtHrInicioConsulta");
		projections.add(Projections.property("CAA." + AacCondicaoAtendimento.Fields.SEQ.toString()), "caaSeq");
		projections.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "nome");
		projections.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
		projections.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), "serVinCodigo");
		projections.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), "serMatricula");
		projections.add(Projections.property("CTL." + MamControles.Fields.MIC_NOME.toString()), "micNome");
		projections.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()), "grdSeq");
		projections.add(Projections.property("EXC." + MamExtratoControles.Fields.DTHR_MOVIMENTO.toString()), "hrInicioAtendimento");
		projections.add(Projections.property("USL." + AacUnidFuncionalSalas.Fields.SALA.toString()), "salaMicro");
		projections.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel");
		projections.add(Projections.property("PPF." + RapPessoasFisicas.Fields.NOME.toString()), "profissional");
		projections.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade");
		projections.add(Projections.property("EQP." + AghEquipes.Fields.NOME.toString()), "equipe");

		criteria.setProjection(projections);

		criteria.createAlias("CON." + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA");
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC");

		// @ORADB MAMC_GET_VIN_RESP, @ORADB MAMC_GET_MAT_RESP, @ORADB MAMC_GET_MICRO
		criteria.createAlias("CON." + AacConsultas.Fields.CONTROLE.toString(), "CTL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.SERVIDOR_RESPONSAVEL.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "PRE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRE." + RapServidores.Fields.PESSOA_FISICA.toString(), "PPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.EXTRATO_CONTROLES_2.toString(), "EC2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("EC2." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SA2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.EXTRATO_CONTROLES.toString(), "EXC", JoinType.LEFT_OUTER_JOIN);
		
		// @ORADB MAMC_GET_SALA_MICRO
		criteria.createAlias("CTL." + MamControles.Fields.MICROCOMPUTADOR.toString(), "MIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MIC." + AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_RESPONSAVEL
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_AGUARDANDO
		DetachedCriteria subGetAguardando = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subGetAguardando.setProjection(Projections.max("SEC." + MamExtratoControles.Fields.SEQP.toString()));
		
		subGetAguardando.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT");
		
		subGetAguardando.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));

		criteria.add(Restrictions.or(Restrictions.isNull("EC2." + MamExtratoControles.Fields.SEQP.toString()),
				Subqueries.propertyEq("EC2." + MamExtratoControles.Fields.SEQP.toString(), subGetAguardando)));

		// @ORADB MAMC_GET_HR_INI_ATD
		DetachedCriteria subHrIniAtd = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subHrIniAtd.setProjection(Projections.max("SEC." + MamExtratoControles.Fields.SEQP.toString()));
		
		subHrIniAtd.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT", JoinType.LEFT_OUTER_JOIN);
		subHrIniAtd.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA", JoinType.LEFT_OUTER_JOIN,
				Restrictions.and(Restrictions.eq("SSA." + MamSituacaoAtendimentos.Fields.SEQ.toString(), paramSeqSituacaoAtendimento)));
		
		subHrIniAtd.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));

		criteria.add(Restrictions.or(Restrictions.isNull("EXC." + MamExtratoControles.Fields.SEQP.toString()),
				Subqueries.propertyEq("EXC." + MamExtratoControles.Fields.SEQP.toString(), subHrIniAtd)));

		if (data != null) {
			criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraInical(data),
					DateUtil.obterDataComHoraFinal(data)));
		}
		
		criteria.add(Restrictions.isNotNull("PAC." + AipPacientes.Fields.CODIGO.toString()));

		// @ORADB MAMC_GET_AGUARD_ATEN
		DetachedCriteria subGetAguardAten = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");

		subGetAguardAten.setProjection(Projections.property("SEC." + MamExtratoControles.Fields.SEQP.toString()));

		subGetAguardAten.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT");
		subGetAguardAten.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA");
		
		subGetAguardAten.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));
		subGetAguardAten.add(Restrictions.eq("SSA." + MamSituacaoAtendimentos.Fields.ATEND_CONCLUIDO.toString(), DominioSimNao.N.isSim()));
		subGetAguardAten.add(Restrictions.eq("SSA." + MamSituacaoAtendimentos.Fields.ATEND_PEND.toString(), DominioSimNao.N.isSim()));
		
		DetachedCriteria subOrGetAguardAten = DetachedCriteria.forClass(MamExtratoControles.class, "SSE");
		
		subOrGetAguardAten.setProjection(Projections.max("SSE." + MamExtratoControles.Fields.SEQP.toString()));
		
		subOrGetAguardAten.createAlias("SSE." + MamExtratoControles.Fields.CONTROLE.toString(), "SSC");
		subOrGetAguardAten.createAlias("SSE." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA2");
		
		subOrGetAguardAten.add(Restrictions.eqProperty("CTL." + MamControles.Fields.SEQ.toString(), "SSC." + MamControles.Fields.SEQ.toString()));
		
		subOrGetAguardAten.add(Restrictions.or(Restrictions.eq("SSA2." + MamSituacaoAtendimentos.Fields.AGUARDANDO.toString(), DominioSimNao.S.isSim()),
				Restrictions.eq("SSA2." + MamSituacaoAtendimentos.Fields.AGENDADO.toString(), DominioSimNao.S.isSim())));

		subGetAguardAten.add(Subqueries.exists(subOrGetAguardAten));
		
		DetachedCriteria subSubGetAguardAten = DetachedCriteria.forClass(MamExtratoControles.class, "SSE");
		
		subSubGetAguardAten.setProjection(Projections.max("SSE." + MamExtratoControles.Fields.SEQP.toString()));
		
		subSubGetAguardAten.createAlias("SSE." + MamExtratoControles.Fields.CONTROLE.toString(), "SSC");

		subSubGetAguardAten.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "SSC." + MamControles.Fields.SEQ.toString()));

		subGetAguardAten.add(Subqueries.propertyEq("SEC." + MamExtratoControles.Fields.SEQP.toString(), subSubGetAguardAten));
		criteria.add(Subqueries.exists(subGetAguardAten));

		// @ORADB MAMC_GET_AGENDADO
		DetachedCriteria subGetAgendado = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subGetAgendado.setProjection(Projections.property("SSA." + MamSituacaoAtendimentos.Fields.AGENDADO.toString()));
		
		subGetAgendado.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT");
		subGetAgendado.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA");
		
		subGetAgendado.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));
		
		DetachedCriteria subSubGetAgendado = DetachedCriteria.forClass(MamExtratoControles.class, "SSE");
		
		subSubGetAgendado.setProjection(Projections.max("SSE." + MamExtratoControles.Fields.SEQP.toString()));
		
		subSubGetAgendado.createAlias("SSE." + MamExtratoControles.Fields.CONTROLE.toString(), "SSC");

		subSubGetAgendado.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "SSC." + MamControles.Fields.SEQ.toString()));
		
		subGetAgendado.add(Subqueries.propertyEq("SEC." + MamExtratoControles.Fields.SEQP.toString(), subSubGetAgendado));
		criteria.add(Subqueries.eq(DominioSimNao.N.isSim(), subGetAgendado));
		
		// @ORADB AACC_DEFINE_TURNO
		if (turno != null && turno.getDataInicial() != null && turno.getDataFinal() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String dataInicioTurno = sdf.format(turno.getDataInicial());
			String dataFimTurno = sdf.format(turno.getDataFinal());
			
			criteria.add(Restrictions.sqlRestriction("to_char({alias}.DT_CONSULTA, 'hh24:mi:ss') between ? and ?", new Object[] {dataInicioTurno, dataFimTurno},
					new Type[] {StringType.INSTANCE, StringType.INSTANCE}));
		}

		adicionarSubqueryGradeAgendamentoPesquisarConsultasPendentes(criteria, especialidade, equipe, zona, sala, profissional);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisarConsultasPendentesVO.class));
		
		return executeCriteria(criteria);
	}

	/**
	 * Realiza a terceira parte da busca por consultas pendentes. Busca por consultas com pendências de outros usuários.
	 * 
	 * @param usuario - Usuário logado
	 * @param data - Data pesquisada
	 * @param especialidade - Especialidade
	 * @param equipe - Equipe
	 * @param zona - Zona
	 * @param sala - Sala da consulta
	 * @param profissional - Profissional que realizou a consulta
	 * @param turno - Turno da consulta
	 * @param paramSeqSituacaoAtendimento - Valor do parâmetro P_SEQ_SIT_EM_ATEND
	 * 
	 * @return Lista de consultas pendentes em atendimento
	 */
	private List<PesquisarConsultasPendentesVO> pesquisarConsultasPendentesPendenciaOutroUsuario(RapServidores usuario, Date data, AghEspecialidades especialidade,
			AghEquipes equipe, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional, DataInicioFimVO turno,
			Short paramSeqSituacaoAtendimento) {

		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");

		ProjectionList projections = Projections.projectionList();
		
		projections.add(Projections.sqlProjection("3 as prioridade", new String[]{"prioridade"}, new Type[]{IntegerType.INSTANCE}));
		projections.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()), "numero");
		projections.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()), "dtConsulta");
		projections.add(Projections.property("CON." + AacConsultas.Fields.DTHR_INICIO.toString()), "dtHrInicioConsulta");
		projections.add(Projections.property("CAA." + AacCondicaoAtendimento.Fields.SEQ.toString()), "caaSeq");
		projections.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "nome");
		projections.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario");
		projections.add(Projections.property("SER." + RapServidores.Fields.VIN_CODIGO.toString()), "serVinCodigo");
		projections.add(Projections.property("SER." + RapServidores.Fields.MATRICULA.toString()), "serMatricula");
		projections.add(Projections.property("CTL." + MamControles.Fields.MIC_NOME.toString()), "micNome");
		projections.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()), "grdSeq");
		projections.add(Projections.property("EXC." + MamExtratoControles.Fields.DTHR_MOVIMENTO.toString()), "hrInicioAtendimento");
		projections.add(Projections.property("USL." + AacUnidFuncionalSalas.Fields.SALA.toString()), "salaMicro");
		projections.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), "responsavel");
		projections.add(Projections.property("PPF." + RapPessoasFisicas.Fields.NOME.toString()), "profissional");
		projections.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "especialidade");
		projections.add(Projections.property("EQP." + AghEquipes.Fields.NOME.toString()), "equipe");

		criteria.setProjection(projections);

		criteria.createAlias("CON." + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA");
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC");

		// @ORADB MAMC_GET_VIN_RESP, @ORADB MAMC_GET_MAT_RESP, @ORADB MAMC_GET_MICRO
		criteria.createAlias("CON." + AacConsultas.Fields.CONTROLE.toString(), "CTL", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.SERVIDOR_RESPONSAVEL.toString(), "SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "PRE", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PRE." + RapServidores.Fields.PESSOA_FISICA.toString(), "PPF", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.EXTRATO_CONTROLES_2.toString(), "EC2", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CTL." + MamControles.Fields.EXTRATO_CONTROLES.toString(), "EXC", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_SALA_MICRO
		criteria.createAlias("CTL." + MamControles.Fields.MICROCOMPUTADOR.toString(), "MIC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("MIC." + AghMicrocomputador.Fields.UNIDFUNCIONALSALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_RESPONSAVEL
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.LEFT_OUTER_JOIN);

		// @ORADB MAMC_GET_AGUARDANDO
		DetachedCriteria subGetAguardando = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subGetAguardando.setProjection(Projections.max("SEC." + MamExtratoControles.Fields.SEQP.toString()));
		
		subGetAguardando.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT");
		
		subGetAguardando.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));

		criteria.add(Restrictions.or(Restrictions.isNull("EC2." + MamExtratoControles.Fields.SEQP.toString()),
				Subqueries.propertyEq("EC2." + MamExtratoControles.Fields.SEQP.toString(), subGetAguardando)));

		// @ORADB MAMC_GET_HR_INI_ATD
		DetachedCriteria subHrIniAtd = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subHrIniAtd.setProjection(Projections.max("SEC." + MamExtratoControles.Fields.SEQP.toString()));
		
		subHrIniAtd.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT", JoinType.LEFT_OUTER_JOIN);
		subHrIniAtd.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA", JoinType.LEFT_OUTER_JOIN,
				Restrictions.and(Restrictions.eq("SSA." + MamSituacaoAtendimentos.Fields.SEQ.toString(), paramSeqSituacaoAtendimento)));
		
		subHrIniAtd.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));

		criteria.add(Restrictions.or(Restrictions.isNull("EXC." + MamExtratoControles.Fields.SEQP.toString()),
				Subqueries.propertyEq("EXC." + MamExtratoControles.Fields.SEQP.toString(), subHrIniAtd)));

		if (data != null) {
			criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraInical(data),
					DateUtil.obterDataComHoraFinal(data)));
		}
		
		criteria.add(Restrictions.isNotNull("PAC." + AipPacientes.Fields.CODIGO.toString()));

		// @ORADB MAMC_GET_ATEND_PEND
		DetachedCriteria subGetAtendPend = DetachedCriteria.forClass(MamExtratoControles.class, "SEC");
		
		subGetAtendPend.setProjection(Projections.property("SSA." + MamSituacaoAtendimentos.Fields.ATEND_PEND.toString()));
		
		subGetAtendPend.createAlias("SEC." + MamExtratoControles.Fields.CONTROLE.toString(), "SCT");
		subGetAtendPend.createAlias("SEC." + MamExtratoControles.Fields.SITUACAO_ATENDIMENTO.toString(), "SSA");
		
		subGetAtendPend.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "CTL." + MamControles.Fields.SEQ.toString()));
		
		DetachedCriteria subSubGetAtendPend = DetachedCriteria.forClass(MamExtratoControles.class, "SSE");
		
		subSubGetAtendPend.setProjection(Projections.max("SSE." + MamExtratoControles.Fields.SEQP.toString()));
		
		subSubGetAtendPend.createAlias("SSE." + MamExtratoControles.Fields.CONTROLE.toString(), "SSC");

		subSubGetAtendPend.add(Restrictions.eqProperty("SCT." + MamControles.Fields.SEQ.toString(), "SSC." + MamControles.Fields.SEQ.toString()));
		
		subGetAtendPend.add(Subqueries.propertyEq("SEC." + MamExtratoControles.Fields.SEQP.toString(), subSubGetAtendPend));
		criteria.add(Subqueries.eq(DominioSimNao.S.isSim(), subGetAtendPend));

		// @ORADB MAMC_GET_VIN_RESP, @ORADB MAMC_GET_MAT_RESP
		if (usuario != null && usuario != null && usuario.getId().getVinCodigo() != null && usuario.getId().getMatricula() != null) {
			criteria.add(Restrictions.or(Restrictions.not(Restrictions.eq("SER." + RapServidores.Fields.VIN_CODIGO.toString(), usuario.getId().getVinCodigo())),
					Restrictions.not(Restrictions.eq("SER." + RapServidores.Fields.MATRICULA.toString(), usuario.getId().getMatricula()))));
		}

		// @ORADB AACC_DEFINE_TURNO
		if (turno != null && turno.getDataInicial() != null && turno.getDataFinal() != null) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
			String dataInicioTurno = sdf.format(turno.getDataInicial());
			String dataFimTurno = sdf.format(turno.getDataFinal());
			
			criteria.add(Restrictions.sqlRestriction("to_char({alias}.DT_CONSULTA, 'hh24:mi:ss') between ? and ?", new Object[] {dataInicioTurno, dataFimTurno},
					new Type[] {StringType.INSTANCE, StringType.INSTANCE}));
		}

		adicionarSubqueryGradeAgendamentoPesquisarConsultasPendentes(criteria, especialidade, equipe, zona, sala, profissional);
		
		criteria.setResultTransformer(Transformers.aliasToBean(PesquisarConsultasPendentesVO.class));
		
		return executeCriteria(criteria);
	}

	/** 
	 * Adiciona à criteria informada, a subquery de GradeAgendamentoConsultas.
	 * 
	 * @param criteria - Criteria da consulta principal
	 * @param especialidade - Especialidade
	 * @param equipe - Equipe
	 * @param zona - Zona
	 * @param sala - Sala da consulta
	 * @param profissional - Profissional que realizou a consulta
	 */
	private void adicionarSubqueryGradeAgendamentoPesquisarConsultasPendentes(DetachedCriteria criteria, AghEspecialidades especialidade, AghEquipes equipe,
			VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala sala, RapServidores profissional) {

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AacGradeAgendamenConsultas.class, "SGR");

		subCriteria.setProjection(Projections.property("SGR." + AacGradeAgendamenConsultas.Fields.SEQ.toString()));
		
		subCriteria.createAlias("SGR." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "SES", JoinType.LEFT_OUTER_JOIN);
		subCriteria.createAlias("SGR." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "SEQ", JoinType.LEFT_OUTER_JOIN);
		subCriteria.createAlias("SGR." + AacGradeAgendamenConsultas.Fields.AAC_UND_FUNC_SALA.toString(), "USL", JoinType.LEFT_OUTER_JOIN);
		subCriteria.createAlias("SGR." + AacGradeAgendamenConsultas.Fields.PROF_SERVIDOR.toString(), "SSR", JoinType.LEFT_OUTER_JOIN);

		subCriteria.add(Restrictions.eqProperty("SGR." + AacGradeAgendamenConsultas.Fields.SEQ.toString(),
				"GRD." + AacGradeAgendamenConsultas.Fields.SEQ.toString()));

		if (especialidade != null && especialidade.getSeq() != null) {
			subCriteria.add(Restrictions.eq("SES." + AghEspecialidades.Fields.SEQ.toString(), especialidade.getSeq()));
		}

		if (equipe != null && equipe.getSeq() != null) {
			subCriteria.add(Restrictions.eq("SEQ." + AghEquipes.Fields.SEQ.toString(), equipe.getSeq()));
		}

		if (zona != null && zona.getUnfSeq() != null) {
			subCriteria.add(Restrictions.eq("USL." + AacUnidFuncionalSalas.Fields.UNF_SEQ.toString(), zona.getUnfSeq()));
		}

		if (sala != null && sala.getId() != null && sala.getId().getSala() != null) {
			subCriteria.add(Restrictions.eq("USL." + AacUnidFuncionalSalas.Fields.SALA.toString(), sala.getId().getSala()));
		}

		if (profissional != null && profissional.getId() != null) {
			if (profissional.getId().getVinCodigo() != null) {
				subCriteria.add(Restrictions.eq("SSR." + RapServidores.Fields.VIN_CODIGO.toString(), profissional.getId().getVinCodigo()));
			}

			if (profissional.getId().getMatricula() != null) {
				subCriteria.add(Restrictions.eq("SSR." + RapServidores.Fields.MATRICULA.toString(), profissional.getId().getMatricula()));
			}
		}

		criteria.add(Subqueries.exists(subCriteria));
	}
	
	/**
	 * 
	 * @param esp_seq
	 * @return
	 * Estoria: 40229:  Consulta c18
	 */
	
	public String mensagemInterconsultaDataEspecialidade(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD",JoinType.INNER_JOIN);
		criteria.createAlias("GRD."+ AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF", JoinType.INNER_JOIN);
		criteria.createAlias("GRD."+ AacGradeAgendamenConsultas.Fields.EQUIPE.toString(),"EQP", JoinType.INNER_JOIN);
		criteria.createAlias("GRD."+ AacGradeAgendamenConsultas.Fields.SERVIDOR.toString(),"SER", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER."+ RapServidores.Fields.PESSOA_FISICA, "PES",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD."+ AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(),"ESP", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.CON_NUMERO.toString(),conNumero));
		
		//Projeções-
		criteria.setProjection(Projections.projectionList().add(Projections.property(AacConsultas.Fields.DATA_CONSULTA.toString()))
				.add(Projections.property("UNF."+AghUnidadesFuncionais.Fields.DESCRICAO.toString()))
				.add(Projections.property("GRD."+AacGradeAgendamenConsultas.Fields.USL_SALA.toString()))
				.add(Projections.property("ESP."+AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()))
				.add(Projections.property("EQP."+AghEquipes.Fields.NOME.toString()))
				.add(Projections.property("PES."+RapPessoasFisicas.Fields.NOME.toString())));
				
		String mensagemInterconsultaDataEspecialidade;
		
		
		Object[]  aacConsultas = (Object[]) executeCriteriaUniqueResult(criteria);						
		
		mensagemInterconsultaDataEspecialidade = "Interconsulta marcada para "+aacConsultas[0]+", "
				+((String) aacConsultas[1]).toLowerCase()+", Sala "
				+aacConsultas[2]+", " 
				+((String) aacConsultas[3]).toLowerCase()+", Equipe "
				+((String) aacConsultas[4]).toLowerCase();
		
				return mensagemInterconsultaDataEspecialidade;
	}

	//Método que realiza a consulta da aba ausentes da tela lista de pacientes agendados
	public List<ConsultaAmbulatorioVO> consultaAbaPacientesAusentes(Date dtPesquisa, Short zonaUnfSeq, VAacSiglaUnfSala zonaSala, AghEspecialidades especialidade, AghEquipes equipe,
			RapServidores profissional){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		Date dataInicial = DateUtil.truncaData(dtPesquisa);
		Date dataFinal = DateUtil.truncaDataFim(dtPesquisa);
		
		//Relacionamentos da consulta
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD",JoinType.INNER_JOIN);
		criteria.createAlias("GRD."+ AacGradeAgendamenConsultas.Fields.ESPECIALIDADE, "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("GRD."+ AacGradeAgendamenConsultas.Fields.EQUIPE, "EQP", JoinType.INNER_JOIN);
		criteria.createAlias("CON."+ AacConsultas.Fields.RETORNO.toString(), "RET",JoinType.INNER_JOIN);
		criteria.createAlias("CON."+ AacConsultas.Fields.PACIENTE.toString(), "PAC",JoinType.INNER_JOIN);
		criteria.createAlias("CON."+ AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA",JoinType.INNER_JOIN);
		
		//WHERE da consulta
		criteria.add(Restrictions.eq("RET." + AacRetornos.Fields.IND_AUSENTE_AMBU.toString(), Boolean.TRUE));		
		criteria.add(Restrictions.isNotNull("PAC." + AipPacientes.Fields.CODIGO.toString()));
		criteria.add(Restrictions.between("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataInicial, dataFinal));
		criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString(), zonaUnfSeq));
		if(zonaSala != null){
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.USL_SALA.toString(), zonaSala.getId().getSala()));
		}
		if(especialidade != null){
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), especialidade.getSeq()));
		}
		if(equipe != null){
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.EQP_SEQ.toString(), equipe.getSeq()));
		}
		if(profissional != null){
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString(), profissional.getId().getMatricula()));	
			criteria.add(Restrictions.eq("GRD." + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString(), profissional.getId().getVinCodigo()));			
		}		
		
		//Projeções
		criteria.setProjection(Projections.projectionList().add(Projections.property("CON." + AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString()), "excedeProgramacao")
															//campo faz a seguinte conversão to_char (cons.dt_consulta, 'DD/MM/YYYY HH24:MI')
															.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()), "dtConsulta")
															.add(Projections.property("PAC." + AipPacientes.Fields.NOME.toString()), "pacienteNome")
															.add(Projections.property("PAC." + AipPacientes.Fields.PRONTUARIO.toString()), "prontuario")
															.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()), "numero")
															//campo faz a seguinte conversão TRUNC(MONTHS_BETWEEN(SYSDATE,pac.dt_nascimento)/12) idade realizado no VO
															.add(Projections.property("PAC." + AipPacientes.Fields.DATA_NASCIMENTO.toString()), "pacienteDtNasc")
															//campo faz a seguinte conversão SUBSTR(esp.NOME_ESPECIALIDADE,1,30) especialidade
															.add(Projections.property("ESP." + AghEspecialidades.Fields.NOME_ESPECIALIDADE.toString()), "nomeEspecialidade")
															//campo faz a seguinte conversão SUBSTR(eqp.nome,1,30) equipe
															.add(Projections.property("EQP." + AghEquipes.Fields.NOME.toString()), "gradeEquipeNome")
															.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.PRE_SER_MATRICULA.toString()), "controleSerMatricula")
															.add(Projections.property("GRD." + AacGradeAgendamenConsultas.Fields.PRE_SER_VIN_CODIGO.toString()), "controleServinCodigo")															
															.add(Projections.property("CON." + AacConsultas.Fields.FAG_CAA_SEQ.toString()), "caaSeq")
															.add(Projections.property("CAA." + AacCondicaoAtendimento.Fields.COR_EXIBICAO.toString()), "condiaoCorExibica"));		
		
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultaAmbulatorioVO.class));
		
		return executeCriteria(criteria);
	}
	
	/**
	 * #42011
	 * @param conNumero 
	 * @param dataConsulta 
	 * @param cTransp 
	 */
	public AacConsultas consulta (Integer conNumero,String cTransp, Date dataConsulta) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.INNER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.INNER_JOIN);
		criteria.createAlias("ESP." + AghEspecialidades.Fields.TRANSPLANTE_ESPECIALIDADES.toString(), "TRS", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.add(Restrictions.ge("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), dataConsulta));
		
		criteria.add(Restrictions.eqProperty("TRS." + FatTransplanteEspecialidade.Fields.ESP_SEQ.toString(), "GRD." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString()));
		criteria.add(Restrictions.eq("TRS." + FatTransplanteEspecialidade.Fields.TTR_CODIGO.toString(), cTransp));

		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}
	
	public AacConsultas listarConsulta(Integer conNumero) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		criteria.createAlias("C." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq("C." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}
	
	//C2 #42012
	public List<AacConsultas> verificarNecessidadeSinalizacaoLaudoFoto(Integer numeroConsulta, Integer parametroSistema){
		
		DetachedCriteria criteria = getCriteriaPorNumeroConsulta(numeroConsulta);
		
		DetachedCriteria subcriteria = obterSubCriteria(parametroSistema);
		
		criteria.add(Subqueries.notExists(subcriteria));
		
		return executeCriteria(criteria);
	}

	//C3 #42012
	public List<AacConsultas> verificarNecessidadeSinalizacaoControleFrequenciaFoto(Integer numeroConsulta, Integer parametroSistema){
		
		DetachedCriteria criteria = getCriteriaPorNumeroConsulta(numeroConsulta);
		
		DetachedCriteria subcriteria = obterSubCriteria(parametroSistema);
		
		criteria.add(Subqueries.exists(subcriteria));
		
		return executeCriteria(criteria);
	}

	private DetachedCriteria getCriteriaPorNumeroConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		criteria.createAlias("C." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G");
		
		criteria.add(Restrictions.eq("C." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		return criteria;
	}
	

	private DetachedCriteria obterSubCriteria(Integer parametroSistema) {
		DetachedCriteria subcriteria = DetachedCriteria.forClass(FatListaPacApac.class, "LPP");
		subcriteria.add(Restrictions.eqProperty("LPP." + FatListaPacApac.Fields.PAC_CODIGO.toString(), "C." + AacConsultas.Fields.PAC_CODIGO.toString()));
		subcriteria.add(Restrictions.isNotNull("LPP." + FatListaPacApac.Fields.DT_CONFIRMADO.toString()));		
		subcriteria.add(Restrictions.eq("LPP." + FatListaPacApac.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		subcriteria.add(Restrictions.eq("LPP." + FatListaPacApac.Fields.TPT_SEQ.toString(), parametroSistema));
		
		subcriteria.setProjection(Projections.projectionList().add(Projections.property("LPP." + FatListaPacApac.Fields.SEQ )));
		return subcriteria;
	}
	
	/**
	 * Consulta c_ctrl_freq
	 * estoria 42013
	 * @param conNumero
	 * @return
	 */
	public boolean verificaConsultaGradeAgendamenEspecialidadeTratamentosPorConNumero(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias("con." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");
		criteria.createAlias("grd." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "esp");
		criteria.createAlias("esp." + AghEspecialidades.Fields.ESPECIALIDADE_TRATAMENTO.toString(), "etr");
		
		criteria.add(Restrictions.eq("con."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		criteria.add(Restrictions.eq("etr."+FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.toString(),"S" ));
		
		criteria.setProjection(Projections.property("con."+AacConsultas.Fields.NUMERO.toString()));
		
		return executeCriteriaExists(criteria);
		
	}
	
	public List<AacConsultas> listarTodos(){
		DetachedCriteria criteria = DetachedCriteria.forClass(VAacFormaAgendamentos.class);
		
		return executeCriteria(criteria);
	}
	
	//Select para auxiliar no update dos pagadores, autorizações e condições.
	public AacConsultas obterAacConsultasParaAuxiliarUpdate(){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.createAlias(AacConsultas.Fields.FAG_PGD_SEQ.toString(), "Pagador");
		criteria.createAlias(AacConsultas.Fields.FAG_TAG_SEQ.toString(), "Autorização");
		criteria.createAlias(AacConsultas.Fields.FAG_CAA_SEQ.toString(), "Condição");
		
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #42229 P3 - CURSOR c_saldo
	 * Conta consultas marcadas com situacao Ativa e não bloqueada
	 */
	public Long pesquisaConsultasMarcadasAtivasSemBloqueio(Integer numero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.SITUACAO_CONSULTA.toString(), "SIT",JoinType.INNER_JOIN);
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.PJQ_SEQ.toString(), numero));
		criteria.add(Restrictions.eq("SIT." + AacSituacaoConsultas.Fields.SITUACAO, DominioSituacaoInterconsultas.M.toString() ));
		criteria.add(Restrictions.eq("SIT." + AacSituacaoConsultas.Fields.IND_BLOQUEIO, false));
		criteria.add(Restrictions.eq("SIT." + AacSituacaoConsultas.Fields.IND_SITUACAO, DominioSituacao.A));
		return executeCriteriaCount(criteria);
	}
	
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterDadosDeclaracao(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.createAlias("ESP." + AghEspecialidades.Fields.ESPECIALIDADE_TRATAMENTO.toString(), "ETR");
		criteria.createAlias("ETR." + FatEspecialidadeTratamento.Fields.TIPO_TRATAMENTO.toString(), "TPT");
		criteria.createAlias("TPT." + FatTipoTratamentos.Fields.ITEM_PROCED_HOSPITALAR.toString(), "IPH");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.COD_TABELA.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CODIGO_TABELA.toString())
				.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DATA_DECLARACAO.toString())
				.add(Projections.property("IPH." + FatItensProcedHospitalar.Fields.DESCRICAO.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO.toString()));		

		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(),numeroConsulta));
		criteria.add(Restrictions.eq("ETR." + FatEspecialidadeTratamento.Fields.IND_LISTA_CANDIDATO.toString(),"S" ));

		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		
		return (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
	}
	
	public LaudoSolicitacaoAutorizacaoProcedAmbVO obterDadosDeclaracaoConsulta(Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "P", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("P." + AipPacientes.Fields.FAT_CANDIDATOS_APAC_OTORRINO.toString(), "OTO", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("OTO." + FatCandidatosApacOtorrino.Fields.PRODCEDIMENTO_HOSPITALAR_SUGERIDO.toString(), "psug", JoinType.INNER_JOIN);
		criteria.createAlias("psug." + FatProcedHospInternosPai.Fields.V_FAT_ASSOCIACAO_PROCD_HOSP.toString(), "V", JoinType.INNER_JOIN);
		
		criteria.setProjection(Projections.distinct(Projections.projectionList()
				.add(Projections.property("V." + VFatAssociacaoProcedimento.Fields.COD_TABELA.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.CODIGO_TABELA.toString())
				.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DATA_DECLARACAO.toString())
				.add(Projections.property("V." + VFatAssociacaoProcedimento.Fields.IPH_DESCRICAO.toString()), LaudoSolicitacaoAutorizacaoProcedAmbVO.Fields.DESCRICAO.toString())));		
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(),numeroConsulta));
		criteria.add(Restrictions.eqProperty("OTO." + FatCandidatosApacOtorrino.Fields.DT_EVENTO.toString(),"CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.add(Restrictions.eq("V." + VFatAssociacaoProcedimento.Fields.IPH_PHO_SEQ.toString(), Short.valueOf((short) 12) ));
		
		criteria.setResultTransformer(Transformers.aliasToBean(LaudoSolicitacaoAutorizacaoProcedAmbVO.class));
		
		return (LaudoSolicitacaoAutorizacaoProcedAmbVO) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * 42010 melhoria - 44508
	 * Modificado 27/02/15 - melhoria #47180
	 * verificar a sinalização na etapa de Diagnóstico
	 */
	public List<AacConsultas> obterSinalizacaoDiagnostico(Integer nroConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G",JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), nroConsulta));
		if(isOracle()){
		criteria.add(Restrictions.sqlRestriction(" not exists ( select c2.dt_fim_validade from agh.v_fat_atend_apac_pacientes a"
				+ "    inner join agh.fat_conta_apacs c2 on a.atm_numero = c2.atm_numero    where"
				+ "      a.pac_prontuario = this_.pac_codigo and add_months (c2.dt_fim_validade, 12) > current_date )"));
		}else if(isPostgreSQL()){
			criteria.add(Restrictions.sqlRestriction(" not exists ( select c2.dt_fim_validade from agh.v_fat_atend_apac_pacientes a"
					+ "    inner join agh.fat_conta_apacs c2 on a.atm_numero = c2.atm_numero    where"
					+ "      a.pac_prontuario = this_.pac_codigo and (c2.dt_fim_validade + interval '12 month') > current_date )"));
		}
		return executeCriteria(criteria);
	}
	

	public AacConsultas obterAgendamentoConsultaPorFiltros(Integer consultaNumero, Short unidadeNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC");
		criteria.createAlias("CON." + AacConsultas.Fields.PAGADOR.toString(), "PGD");
		criteria.createAlias("CON." + AacConsultas.Fields.SERVIDOR_CONSULTADO.toString(), "SER",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.SERVIDOR_MARCACAO.toString(), "SERMARC",JoinType.LEFT_OUTER_JOIN);
		
		//parte F02
		criteria.createAlias("CON." + AacConsultas.Fields.SERVIDOR.toString(), "SERMAR",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SERMAR." + RapServidores.Fields.CSE_USUARIO.toString(), "CUSU",JoinType.LEFT_OUTER_JOIN);
		//
	
		criteria.createAlias("CON." + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CAA");
		criteria.createAlias("SER." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("SER." + RapServidores.Fields.CENTRO_CUSTO_LOTACAO.toString(), "CCT", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "UNF",JoinType.LEFT_OUTER_JOIN);
		
		//PARTE DA FUNCTION F01
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.PROF_ESPECIALIDADE.toString(), "PROFESP",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROFESP." + AghProfEspecialidades.Fields.RAP_SERVIDOR.toString(), "PROFESPSERV",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("PROFESPSERV." + RapServidores.Fields.PESSOA_FISICA.toString(), "PROFESPSERVPES",JoinType.LEFT_OUTER_JOIN);
		//FIM DA FUNCITON F01
		
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "EQP");
		criteria.createAlias("CON." + AacConsultas.Fields.TIPO_AGENDAMENTO.toString(), "TAG");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.V_AAC_SIGLA_UNF_SALA.toString(), "VUSL");	
		
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), consultaNumero));
		if(unidadeNumero!=null){
			criteria.add(Restrictions.eq("UNF." + AghUnidadesFuncionais.Fields.SEQUENCIAL.toString(), unidadeNumero));
		}

		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}
	
	
	public boolean obterAlaPorNumeroConsulta(Integer consultaNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias("con." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grd");
		criteria.createAlias("grd." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "unf");
		criteria.add(Restrictions.eq("con." + AacConsultas.Fields.NUMERO.toString(), consultaNumero));
		criteria.setProjection(Projections.property("unf."+ AghUnidadesFuncionais.Fields.IND_ALA.toString()));
		return executeCriteriaExists(criteria);
	}
	
	/**
	 * C7 #6807
	 * Consulta para popular a listagem de Consultas por Grade.
	 * @return
	 */
	public List<GradeConsultasVO> pesquisaConsultasPorGrade(Integer grade, FiltroGradeConsultasVO filtro,
			Integer firstResult, Integer maxResult, String orderProperty, boolean asc){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class,"C");
		String aliasCPonto = "C.";
		aliasPesquisaConsultasPorGrade(criteria);
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property(aliasCPonto+AacConsultas.Fields.DATA_CONSULTA.toString()), GradeConsultasVO.Fields.DATA_CONSULTA.toString())
				.add(Projections.property("CA."+AacCondicaoAtendimento.Fields.DESCRICAO.toString()), GradeConsultasVO.Fields.DESCRICAO_COND_ATENDIMENTO.toString())
				.add(Projections.property("S."+AacSituacaoConsultas.Fields.DESCRICAO.toString()), GradeConsultasVO.Fields.SITUACAO_CONSULTA.toString())
				.add(Projections.property(aliasCPonto+AacConsultas.Fields.NUMERO.toString()), GradeConsultasVO.Fields.NUMERO_CONSULTA.toString())
				.add(Projections.property("P."+AipPacientes.Fields.PRONTUARIO.toString()), GradeConsultasVO.Fields.PRONTUARIO_PAC.toString())
				.add(Projections.property("P."+AipPacientes.Fields.CODIGO.toString()), GradeConsultasVO.Fields.CODIGO_PAC.toString())
				.add(Projections.property("P."+AipPacientes.Fields.NOME.toString()), GradeConsultasVO.Fields.NOME_PAC.toString())
				.add(Projections.property("R."+AacRetornos.Fields.DESCRICAO.toString()), GradeConsultasVO.Fields.DESCRICAO_RETORNO.toString())
				.add(Projections.property(aliasCPonto+AacConsultas.Fields.ALTERADO_EM.toString()), GradeConsultasVO.Fields.DATA_ALTERADA_CONSULTA.toString())
				.add(Projections.property("PF."+RapPessoasFisicas.Fields.NOME.toString()), GradeConsultasVO.Fields.NOME_SERVIDOR.toString()));
		
		filtroPesquisaConsultasPorGrade(grade, filtro, criteria, false);
		
		if(filtro.getQuantidade() != null) {
			if (filtro.getQuantidade() > maxResult) {
				int x = filtro.getQuantidade() - firstResult;
				if (x < maxResult) {
					maxResult = x;
			}
			} else {
				maxResult = filtro.getQuantidade();
			}
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(GradeConsultasVO.class));
		return executeCriteria(criteria,firstResult,maxResult,orderProperty,asc);
	}
	
	public Long pesquisaConsultasPorGradeCount(Integer grade, FiltroGradeConsultasVO filtro){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class,"C");
		aliasPesquisaConsultasPorGrade(criteria);
		filtroPesquisaConsultasPorGrade(grade, filtro, criteria, true);
		return executeCriteriaCount(criteria);
	}

	private void aliasPesquisaConsultasPorGrade(DetachedCriteria criteria) {
		criteria.createAlias(ALIAS_C_PONTO+AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CA",JoinType.LEFT_OUTER_JOIN)
				.createAlias(ALIAS_C_PONTO+AacConsultas.Fields.SITUACAO_CONSULTA.toString(), "S",JoinType.LEFT_OUTER_JOIN)
				.createAlias(ALIAS_C_PONTO+AacConsultas.Fields.PACIENTE.toString(),"P",JoinType.LEFT_OUTER_JOIN)
				.createAlias(ALIAS_C_PONTO+AacConsultas.Fields.SERVIDOR_ALTERADO.toString(),"RS",JoinType.LEFT_OUTER_JOIN)
				.createAlias("RS."+RapServidores.Fields.PESSOA_FISICA.toString(),"PF",JoinType.LEFT_OUTER_JOIN)
				.createAlias(ALIAS_C_PONTO+AacConsultas.Fields.RETORNO.toString(),"R",JoinType.LEFT_OUTER_JOIN);
	}

	private void filtroPesquisaConsultasPorGrade(Integer grade,
			FiltroGradeConsultasVO filtro, DetachedCriteria criteria, Boolean isCount) {
		if(grade != null){
			criteria.add(Restrictions.eq(ALIAS_C_PONTO + AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grade));
		}
		
		if(filtro.getSituacao() != null){
			criteria.add(Restrictions.eq("S." + AacSituacaoConsultas.Fields.SITUACAO.toString(), filtro.getSituacao()));
		}

		if(filtro.getApartirDe() == null){
//			<A partir de> is null and c.dt_consulta > to_date(to_char(sysdate,'ddmmyy')||'000000','ddmmyyhh24miss')
			criteria.add(Restrictions.gt(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.truncaData(new Date())));
		}
		else{
//			<A partir de> is not null and c.dt_consulta >= to_date(to_char(<A partir de>,'ddmmyyyy')||'000000','ddmmyyyyhh24miss')
			criteria.add(Restrictions.ge(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.truncaData(filtro.getApartirDe())));
		}
		
        //<Dia> is not null and <Dia> = decode(to_char(c.dt_consulta,'DY'),'SUN',1, 'MON',2,'TUE',3,'WED',4,'THU',5,'FRI',6,'SAT',7,8) 
		if(filtro.getDia() != null){
			StringBuilder sqlRestrictions = new StringBuilder();
			
			sqlRestrictions.append(" case when to_char(this_.dt_consulta, 'DY') = 'DOM' then 1 ");
			sqlRestrictions.append(" when to_char(this_.dt_consulta, 'DY') = 'SEG' then 2 ");						 
			sqlRestrictions.append(" when to_char(this_.dt_consulta, 'DY') = 'TER' then 3 ");
			sqlRestrictions.append(" when to_char(this_.dt_consulta, 'DY') = 'QUA' then 4 ");
			sqlRestrictions.append(" when to_char(this_.dt_consulta, 'DY') = 'QUI' then 5 ");
			sqlRestrictions.append(" when to_char(this_.dt_consulta, 'DY') = 'SEX' then 6");
			sqlRestrictions.append(" when to_char(this_.dt_consulta, 'DY') = 'SAB' then 7 ");
			sqlRestrictions.append(" else 8 END = ? ");
			
			criteria.add(Restrictions.sqlRestriction(sqlRestrictions.toString(), filtro.getDia(), StandardBasicTypes.INTEGER));
		}
		
//		<Horário> is not null and to_char(to_date(<Horário>,'dd/mon/yyyy hh24:mi:ss'),'hh24:mi')= to_char(c.dt_consulta,'hh24:mi')
		if(filtro.getHorario() != null){
			String horarioformatado = DateUtil.obterDataFormatada(filtro.getHorario(), "HH:mm");
			String sqlRestrictions = "to_char({alias}.dt_consulta,'hh24:mi') = ?";
			criteria.add(Restrictions.sqlRestriction(sqlRestrictions,horarioformatado,StandardBasicTypes.STRING));
		}
		
		if(filtro.getQuantidade() != null && !isCount){
			if (isOracle()) {
				criteria.add(Restrictions.sqlRestriction(" rownum <= " + filtro.getQuantidade()));
			} else {
				criteria.addOrder(OrderBySql.sql("this_.dt_consulta ASC"));
			}
		}
	}
	
	public AacConsultas buscarPorNumEGrade(Integer numero, Integer grade){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.SEQ_GRADE_AGENDA_CONSULTA.toString(), grade));
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numero));
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}
	

	/**
	 * Obter Codigo da central pelo numero da consulta
	 * 42803
	 * C13 
	 * @return
	 */
	public Long obterCMCE(Integer nroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("con." + AacConsultas.Fields.COD_CENTRAL.toString())));
		criteria.add(Restrictions.eq("con."  + AacConsultas.Fields.NUMERO.toString(), nroConsulta));
		
		return (Long) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #1378 - Cursor
	 * @author marcelo.deus 
	 */
	public CursorPacVO buscarCursorPac(Integer cConNumero){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE.toString(), "PAC")
				.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD")
				.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), cConNumero));
		criteria.setResultTransformer(Transformers.aliasToBean(CursorPacVO.class));
		return (CursorPacVO) executeCriteriaUniqueResult(criteria);
	}
	/**
	 * 6171 - Obtém consultas por grade.
	 * @param filtro
	 * @param firstResult
	 * @param maxResults
	 * @param orderProperty
	 * @param asc
	 * @return List<ConsultasGradeVO>
	 */
	public List<ConsultasGradeVO> pesquisarConsultasPorGrade(ConsultasGradeVO filtro, Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {		
		DetachedCriteria criteria = criarCriteriaConsultasPorGrade(filtro, true);		
		return executeCriteria(criteria, firstResult, maxResults, orderProperty, asc);
	}
	
	/**
	 * 6171 - Usada na grid com paginação - C1
	 * @param filtro
	 * @return Quantidade
	 */
	public Long contarPesquisarConsultasPorGrade(ConsultasGradeVO filtro) {		
		DetachedCriteria criteria = criarCriteriaConsultasPorGrade(filtro, false);		
		return executeCriteriaCount(criteria);
	}
	
	/**
	 * 6171 - Cria criteria de C1.
	 * @param filtro
	 * @return DetachedCriteria
	 */
	private DetachedCriteria criarCriteriaConsultasPorGrade(ConsultasGradeVO filtro, boolean order) {
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		
		ProjectionList projList = Projections.projectionList();
		projList.add(Projections.property(ALIAS_C_PONTO + AacConsultas.Fields.STCSITUACAO.toString()), ConsultasGradeVO.Fields.STC_SITUACAO_CONSULTA.toString());
		projList.add(Projections.property("FA." + AacFormaAgendamento.Fields.EXIGE_PRONTUARIO.toString()), ConsultasGradeVO.Fields.EXIGE_PRONTUARIO.toString());
		projList.add(Projections.property("P." + AipPacientes.Fields.PRONTUARIO.toString()), ConsultasGradeVO.Fields.PRONTUARIO.toString());
		projList.add(Projections.property(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString()), ConsultasGradeVO.Fields.DATA_CONSULTA.toString());
		projList.add(Projections.property("CA." + AacCondicaoAtendimento.Fields.DESCRICAO.toString()), ConsultasGradeVO.Fields.DESCRICAO_CONDICAO_ATENDIMENTO.toString());
		projList.add(Projections.property("SC." + AacSituacaoConsultas.Fields.DESCRICAO.toString()), ConsultasGradeVO.Fields.DESCRICAO_SITUACAO_CONSULTA.toString());
		projList.add(Projections.property(ALIAS_C_PONTO + AacConsultas.Fields.NUMERO.toString()), ConsultasGradeVO.Fields.NUMERO_CONSULTA.toString());
		projList.add(Projections.property("PA." + AacPagador.Fields.DESCRICAO.toString()), ConsultasGradeVO.Fields.DESCRICAO_PAGADOR.toString());
		projList.add(Projections.property("TA." + AacTipoAgendamento.Fields.DESCRICAO.toString()), ConsultasGradeVO.Fields.DESCRICAO_AUTORIZACAO.toString());
		projList.add(Projections.property("R." + AacRetornos.Fields.SEQ.toString()), ConsultasGradeVO.Fields.SEQ_RETORNO.toString());
		projList.add(Projections.property("R." + AacRetornos.Fields.DESCRICAO.toString()), ConsultasGradeVO.Fields.DESCRICAO_RETORNO.toString());
		projList.add(Projections.property("M." + AacMotivos.Fields.CODIGO.toString()), ConsultasGradeVO.Fields.SEQ_MOTIVO.toString());
		projList.add(Projections.property("M." + AacMotivos.Fields.DESCRICAO.toString()), ConsultasGradeVO.Fields.DESCRICAO_MOTIVO.toString());
		projList.add(Projections.property("P." + AipPacientes.Fields.CODIGO.toString()), ConsultasGradeVO.Fields.CODIGO_PACIENTE.toString());
		projList.add(Projections.property("P." + AipPacientes.Fields.NOME.toString()), ConsultasGradeVO.Fields.NOME_PACIENTE.toString());
		projList.add(Projections.property("C." + AacConsultas.Fields.ALTERADO_EM.toString()), ConsultasGradeVO.Fields.ALTERADO_EM.toString());
		projList.add(Projections.property("PES." + RapPessoasFisicas.Fields.NOME.toString()), ConsultasGradeVO.Fields.NOME_ALTERADO_EM.toString());
		projList.add(Projections.property(ALIAS_C_PONTO + AacConsultas.Fields.IND_EXCEDE_PROGRAMACAO.toString()), ConsultasGradeVO.Fields.CONSULTA_EXCEDENTE.toString());
		criteria.setProjection(projList);
		
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.CONDICAO_ATENDIMENTO.toString(), "CA", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.SITUACAO_CONSULTA.toString(), "SC", JoinType.INNER_JOIN);
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.PACIENTE.toString(), "P", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.FORMA_AGENDAMENTO.toString(), "FA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FA." + AacFormaAgendamento.Fields.PAGADOR.toString(), "PA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("FA." + AacFormaAgendamento.Fields.TIPO_AGENDAMENTO.toString(), "TA", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.RETORNO.toString(), "R", JoinType.LEFT_OUTER_JOIN, Restrictions.eq("R." + AacRetornos.Fields.IND_SITUACAO.toString(), DominioSituacao.A));
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.MOTIVO.toString(), "M", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.SERVIDOR_ALTERADO.toString(), "RPS", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("RPS." + RapServidores.Fields.PESSOA_FISICA.toString(), "PES", JoinType.INNER_JOIN);
		
		criteria.add(Restrictions.eq(ALIAS_C_PONTO + AacConsultas.Fields.GRDSEQ, filtro.getSeqGrade()));
		
		if (filtro.getDataInicial() != null && filtro.getDataFinal() != null) {
			criteria.add(Restrictions.between(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraInical(filtro.getDataInicial()), DateUtil.obterDataComHoraFinal(filtro.getDataFinal())));
		} else if (filtro.getDataInicial() != null && filtro.getDataFinal() == null) {
			criteria.add(Restrictions.gt(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraInical(filtro.getDataInicial())));
		} else if (filtro.getDataInicial() == null && filtro.getDataFinal() != null) {
			criteria.add(Restrictions.lt(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(), DateUtil.obterDataComHoraFinal(filtro.getDataFinal())));
		}	
		if (filtro.getSituacaoConsulta() != null) {
			criteria.add(Restrictions.eq("SC." + AacSituacaoConsultas.Fields.SITUACAO.toString(), filtro.getSituacaoConsulta().toString()));
		}		
		if (filtro.getNumeroConsulta() != null) {
			criteria.add(Restrictions.eq(ALIAS_C_PONTO + AacConsultas.Fields.NUMERO.toString(), filtro.getNumeroConsulta()));
		}	
		if (filtro.getProntuario() != null) {
			criteria.add(Restrictions.eq("P." + AipPacientes.Fields.PRONTUARIO.toString(), filtro.getProntuario()));
		}
		
		if (order) {
			criteria.addOrder(Order.asc(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString()));
			criteria.addOrder(Order.asc(ALIAS_C_PONTO + AacConsultas.Fields.NUMERO.toString()));
		}
		
		criteria.setResultTransformer(Transformers.aliasToBean(ConsultasGradeVO.class));
		
		return criteria;
		
	}
	
	/**
	 * SQL da pesquisa principal (C1) do relatório #43088 Imprimir Guia
	 * deatendimento da Unimed
	 * 
	 * @param numero
	 * @return
	 */
	private String obterSqlGuiaAtendimentoUnimed(final Integer numero) {

		StringBuilder sql = new StringBuilder(3000);
		String sp = StringUtils.SPACE;

		// Projections
		sql.append("SELECT CONV.COD_ANS,A.NUMERO_GUIA,A.DATA,A.QTDE,A.QTDE_CSH,A.IND_DISCRIMINA_DESCONTO,A.VALOR_DESCONTO,A.VIAS_ACCS,A.PRHC_COD_HCPA,A.TPCONV_TPIT_COD,").append(sp);
		sql.append("CON.SER_MATRICULA_CONSULTADO,CON.SER_VIN_CODIGO_CONSULTADO,PAC.PRONTUARIO,PAC.NOME NOME_PAC,INTD.COD_PRNT,INTD.SGLA_ESP,").append(sp);
		sql.append("CTA.ATD_SEQ,CTA.MEX_SEQ,CTA.CSP_CNV_CODIGO,CTA.CSP_SEQ,CTA.NRO,TPTAB.TIPO_TAB,B.COD,B.DSCR BDSCR,B.QTDE_CSH_PROF,").append(sp);
		sql.append("B.QTDE_M2,Y.QTDE_CH_MAT,Y.QTDE_CSH YQTDE_CSH,FLM.VALOR,H.VALOR HVALOR,PLANO.DESCRICAO,").append(sp);

		sql.append("CASE WHEN SUBSTR(TPTAB.DSCR,1,10) = 'GEAP CBHPM' THEN '06'").append(sp);
		sql.append("WHEN SUBSTR(TPTAB.DSCR,1,6) = 'AMB 90' THEN '01'").append(sp);
		sql.append("WHEN SUBSTR(TPTAB.DSCR,1,6) = 'AMB 92' THEN '02'").append(sp);
		sql.append("WHEN SUBSTR(TPTAB.DSCR,1,6) = 'AMB 96' THEN '03'").append(sp);
		sql.append("WHEN SUBSTR(TPTAB.DSCR,1,6) = 'AMB 99' THEN '04'").append(sp);
		sql.append("WHEN SUBSTR(TPTAB.DSCR,1,6) = 'CBHPM ' THEN '06'").append(sp);
		sql.append("WHEN SUBSTR(TPTAB.DSCR,1,6) = 'CIEFAS' THEN '08'").append(sp);
		sql.append("ELSE '22' END TABELA,").append(sp);
		sql.append("CNOPER.COD_ANS CNOPER_COD_ANS").append(sp);

		// From
		sql.append("FROM AGH.AAC_CONSULTAS CON INNER JOIN AGH.AIP_PACIENTES PAC ON PAC.CODIGO = CON.PAC_CODIGO").append(sp);
		sql.append("INNER JOIN CONV.PROC_EFET A ON CON.NUMERO = A.CON_NUMERO").append(sp);
		sql.append("INNER JOIN CONV.CNTA_CONV CTA ON CTA.NRO = A.CTACV_NRO").append(sp);
		sql.append("INNER JOIN CONV.TIPO_X_CCH E ON E.CSP_CNV_CODIGO = CTA.CSP_CNV_CODIGO AND E.CSP_SEQ = CTA.CSP_SEQ ").append(sp);
		sql.append("INNER JOIN CONV.PROC_HCPA_X_CNV D ON E.TPIT_COD = D.TPIT_COD").append(sp);
		sql.append("INNER JOIN CONV.TAB_PGTO B ON B.TPTAB_COD = D.TPTAB_COD AND B.COD = D.TBL_COD").append(sp);
		sql.append("INNER JOIN CONV.COMP_TAB_PGTO X ON X.TPTAB_COD = B.TPTAB_COD").append(sp);
		sql.append("INNER JOIN CONV.VALR_TAB_PGTO_X_COMP Y ON Y.TPTAB_COD = X.TPTAB_COD AND Y.CPPGTO_NRO = X.NRO").append(sp);
		sql.append("INNER JOIN CONV.PROC_HCPA F ON F.COD_HCPA = A.PRHC_COD_HCPA").append(sp);

		sql.append("INNER JOIN CONV.VALOR_CH_CONV_PLANO H ON H.CCP_CSP_CNV_CODIGO = E.CCP_CSP_CNV_CODIGO").append(sp);
		sql.append("AND H.CCP_CSP_SEQ = E.CCP_CSP_SEQ AND H.CCP_CODIGO = E.CCP_CODIGO").append(sp);
		sql.append("INNER JOIN CONV CNOPER ON CNOPER.COD = 99").append(sp);

		/*
		 * H DATAS
		 */
		if (isOracle()) {
			sql.append("AND TRUNC(SYSDATE) BETWEEN H.DT_INICIO AND COALESCE(H.DT_FIM,SYSDATE)").append(sp);
		} else {
			sql.append("AND DATE_TRUNC('DAY', NOW()) BETWEEN H.DT_INICIO AND COALESCE(H.DT_FIM,CURRENT_DATE)").append(sp);
		}

		sql.append("LEFT JOIN CONV.VALOR_CH_CONV_PLANO FLM ON FLM.CCP_CSP_CNV_CODIGO = E.CCP_FLM_CSP_CNV_CODIGO AND FLM.CCP_CSP_SEQ = E.CCP_FLM_CSP_SEQ  AND FLM.CCP_CODIGO = E.CCP_FLM_CODIGO").append(sp);

		/*
		 * FLM DATAS
		 */
		if (isOracle()) {
			sql.append("AND TRUNC(SYSDATE) BETWEEN FLM.DT_INICIO AND COALESCE(FLM.DT_FIM,SYSDATE)").append(sp);
		} else {
			sql.append("AND DATE_TRUNC('DAY', NOW()) BETWEEN FLM.DT_INICIO AND COALESCE(FLM.DT_FIM,CURRENT_DATE)").append(sp);
		}

		sql.append("INNER JOIN AGH.FAT_CONVENIOS_SAUDE CNV ON CNV.CODIGO = CTA.CSP_CNV_CODIGO").append(sp);
		sql.append("INNER JOIN CONV.CONV CONV ON CONV.COD = CNV.CODIGO").append(sp);
		sql.append("INNER JOIN CONV.PAC_INTD_CONV INTD ON INTD.COD_PRNT = CTA.INTD_COD_PRNT AND INTD.DATA_INT = CTA.INTD_DATA_INT AND INTD.SEQ = CTA.PACC_SEQ").append(sp);
		sql.append("LEFT JOIN AGH.AGH_ATENDIMENTOS ATD1 ON CTA.ATD_SEQ = ATD1.SEQ").append(sp);
		sql.append("INNER JOIN CONV.TIPO_TAB_PGTO TPTAB ON TPTAB.COD = B.TPTAB_COD").append(sp);
		sql.append("INNER JOIN AGH.FAT_CONV_SAUDE_PLANOS PLANO ON PLANO.CNV_CODIGO = CTA.CSP_CNV_CODIGO AND PLANO.SEQ = CTA.CSP_SEQ").append(sp);

		// Condicionais
		sql.append("WHERE A.CON_NUMERO  =").append(numero).append(sp);
		sql.append("AND E.TPIT_COD NOT IN (5,50,51)").append(sp);

		sql.append("AND D.COD_HCPA = A.PRHC_COD_HCPA").append(sp);

		sql.append("AND D.TPIT_COD = (CASE WHEN A.PRHC_COD_HCPA = 990604 THEN 90 ELSE E.TPIT_COD END) ").append(sp);
		sql.append("AND D.TPTAB_COD = (CASE WHEN A.PRHC_COD_HCPA = 990604 THEN 50 ELSE E.TPTAB_COD END)").append(sp);

		sql.append("AND D.CSP_CNV_CODIGO = COALESCE(E.MAE_CSP_CNV_CODIGO,E.CSP_CNV_CODIGO)").append(sp);
		sql.append("AND D.CSP_SEQ = COALESCE(E.MAE_CSP_SEQ,E.CSP_SEQ)").append(sp);

		if (isOracle()) {
		sql.append("AND ((A.DATA BETWEEN X.DATA_INIC AND COALESCE(X.DATA_FINL,SYSDATE)").append(sp);
		} else {
			sql.append("AND ((A.DATA BETWEEN X.DATA_INIC AND COALESCE(X.DATA_FINL,CURRENT_DATE)").append(sp);
		}

		sql.append("AND E.NRO_EDICAO_BRDI  IS NULL)").append(sp);
		sql.append("OR (E.NRO_EDICAO_BRDI IS NOT NULL AND E.NRO_EDICAO_BRDI = X.NRO_EDICAO_BRDI))").append(sp);
		sql.append("AND Y.TABPAG_COD = B.COD AND CTA.CONV_COD = CONV.COD").append(sp);

		// Ordenação
		sql.append("ORDER BY A.DATA,TABELA").append(sp);

		return sql.toString();
	}

	/**
	 * Pesquisa principal (C1) do relatório #43088 Imprimir Guia de atendimento
	 * da Unimed
	 * 
	 * @param numero
	 * @return
	 */
	public List<ConsultaGuiaAtendimentoUnimedVO> pesquisarGuiaAtendimentoUnimed(final Integer numero) {
		List<ConsultaGuiaAtendimentoUnimedVO> resultado = new ArrayList<ConsultaGuiaAtendimentoUnimedVO>();

		javax.persistence.Query query = this.createNativeQuery(obterSqlGuiaAtendimentoUnimed(numero));
		List<Object[]> resultadoConsulta = query.getResultList();

		for (Object[] o : resultadoConsulta) {
			ConsultaGuiaAtendimentoUnimedVO vo = new ConsultaGuiaAtendimentoUnimedVO();

			if (o[0] != null) {
				vo.setCodAns(((Number) o[0]).intValue());
			}
			if (o[1] != null) {
				vo.setNumeroGuia(((Number) o[1]).longValue());
			}
			if (o[2] != null) {
				vo.setData((Date) o[2]);
			}
			if (o[3] != null) {
				vo.setQtde((BigDecimal) o[3]);
			}
			if (o[4] != null) {
				vo.setQtdeCsh((BigDecimal) o[4]);
			}
			if (o[5] != null) {
				vo.setIndDiscriminaDesconto(String.valueOf(o[5]));
			}
			if (o[6] != null) {
				vo.setValorDesconto(((Number) o[6]).floatValue());
			}

			// Evita violações de PMD
			continuacaoPopularVOGuiaAtendimentoUnimedParte1(vo, o);
			continuacaoPopularVOGuiaAtendimentoUnimedParte2(vo, o);
			continuacaoPopularVOGuiaAtendimentoUnimedParte3(vo, o);
			resultado.add(vo);
		}
		return resultado;
	}

	/**
	 * Popular VO principal (C1) do relatório #43088 Imprimir Guia de
	 * atendimento. Evita violações de PMD
	 * 
	 * @param vo
	 * @param o
	 */
	private void continuacaoPopularVOGuiaAtendimentoUnimedParte1(ConsultaGuiaAtendimentoUnimedVO vo, Object[] o) {
		
		if (o[7] != null) {
			vo.setViasAccs(((Number) o[7]).shortValue());
		}
		if (o[8] != null) {
			vo.setPrhcCodHcpa(((Number) o[8]).intValue());
		}
		if (o[9] != null) {
			vo.setTpconvTpitCod(((Number) o[9]).shortValue());
		}
		if (o[10] != null) {
			vo.setMatriculaConsultado(((Number) o[10]).intValue());
		}
		if (o[11] != null) {
			vo.setVinCodigoConsultado(((Number) o[11]).shortValue());
		}
		if (o[12] != null) {
			vo.setProntuario(((Number) o[12]).intValue());
		}
		if (o[13] != null) {
			vo.setNomePaciente(String.valueOf(o[13]));
		}
		if (o[14] != null) {
			vo.setCodPrnt(((Number) o[14]).intValue());
		}
		if (o[15] != null) {
			vo.setSiglaEsp(String.valueOf(o[15]));
		}
	}

	private void continuacaoPopularVOGuiaAtendimentoUnimedParte2(ConsultaGuiaAtendimentoUnimedVO vo, Object[] o) {
		if (o[16] != null) {
			vo.setAtdSeq(((Number) o[16]).intValue());
		}
		if (o[17] != null) {
			vo.setMexSeq(((Number) o[17]).intValue());
		}
		if (o[18] != null) {
			vo.setCspCnvCodigo(((Number) o[18]).shortValue());
		}
		if (o[19] != null) {
			vo.setCspSeq(((Number) o[19]).byteValue());			
		}
		if (o[20] != null) {
			vo.setNro(((Number) o[20]).intValue());
		}
		if (o[21] != null) {
			vo.setTipoTab(String.valueOf(o[21]));
		}
		if (o[22] != null) {
			vo.setCodTabPgto(((Number) o[22]).longValue());
		}
		if (o[23] != null) {
			vo.setDscrTabPgto(String.valueOf(o[23]));
		}
	}

	private void continuacaoPopularVOGuiaAtendimentoUnimedParte3(ConsultaGuiaAtendimentoUnimedVO vo, Object[] o) {
		
		if (o[24] != null) {
			vo.setQtdeCshProf((BigDecimal) o[24]);
		}
		if (o[25] != null) {
			vo.setQtdeM2((BigDecimal) o[25]);
		}
		if (o[26] != null) {
			vo.setQtdeChMatTabPgtoComp((BigDecimal) o[26]);
		}
		if (o[27] != null) {
			vo.setQtdeCshTabPgtoComp((BigDecimal) o[27]);
		}
		if (o[28] != null) {
			vo.setFlmValorConvPlano((BigDecimal) o[28]);
		}
		if (o[29] != null) {
			vo.sethValorConvPlano((BigDecimal) o[29]);
		}
		if (o[30] != null) {
			vo.setDescricaoPlano(String.valueOf(o[30]));
		}
		if (o[31] != null) {
			vo.setTabela(String.valueOf(o[31]));
	}
		if (o[32] != null) {
			vo.setCodCnesExec(((Number) o[32]).intValue());
		}
	}
	
	/**
	 * Defeito em Homologação #47786 Estoria #43029
	 *  Obtém a especialidade de uma consulta, recebendo como parâmetro o SEQ.
	 * 
	 * @param numeroConsulta
	 * @return
	 */
	public String obterEspecialidadePorConsulta(final Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		
		criteria.setProjection(Projections.projectionList().add(Projections.property("ESP." + AghEspecialidades.Fields.NOME.toString())));
		
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		List<String> list = executeCriteria(criteria);
		if (list != null && !list.isEmpty()) {
			return list.get(0);
		}
		return null;
	}	
	
	public List<AacConsultas> obterDataPrevisaoInterconsulta(Short espSeq, Short caaSeq){
		Date data =  DateUtil.truncaData(new Date());
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()).as(AacConsultas.Fields.DATA_CONSULTA.toString())));

		
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC", JoinType.INNER_JOIN);
		criteria.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE, "PAC", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.IND_SIT_CONSULTA, "ISC", JoinType.LEFT_OUTER_JOIN);
		
		criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.add(Restrictions.gt("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), data));
		criteria.add(Restrictions.isNull("PAC." + AipPacientes.Fields.CODIGO.toString()));
		criteria.add(Restrictions.in("ISC." + AacSituacaoConsultas.Fields.SITUACAO.toString(), new Object[] {"L", "G"}));
		criteria.add(Restrictions.eq("CON." + AacConsultas.Fields.CAA_SEQ.toString(), caaSeq));
				
		criteria.addOrder(Order.asc("CON." + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AacConsultas.class));
		
		return executeCriteria(criteria);
	}
	
	@SuppressWarnings("deprecation")
	public List<AacConsultas> obterInterconsulta(Short espSeq, Integer codPaciente){
		
		Date novaData = new Date();
		novaData.setDate(novaData.getDate() + 1);
		Date data =  DateUtil.truncaData(novaData);
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("CON." + AacConsultas.Fields.NUMERO.toString()).as(AacConsultas.Fields.NUMERO.toString())));
		
		criteria.createAlias("CON." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GAC", JoinType.INNER_JOIN);
		criteria.createAlias("GAC." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("CON." + AacConsultas.Fields.PACIENTE, "PAC", JoinType.LEFT_OUTER_JOIN);
	
		
		criteria.add(Restrictions.eq("PAC." + AipPacientes.Fields.CODIGO.toString(), codPaciente));
		criteria.add(Restrictions.eq("ESP." + AghEspecialidades.Fields.SEQ.toString(), espSeq));
		criteria.add(Restrictions.gt("CON." + AacConsultas.Fields.DATA_CONSULTA.toString(), data));
			
		criteria.setResultTransformer(Transformers.aliasToBean(AacConsultas.class));
		
		return executeCriteria(criteria);
	}

	public Long consultarCMCEpaciente(final Integer pacCodigo,final Integer conNumero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grade");
		criteria.createAlias("grade." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "gradeEsp");
	
		criteria.add(Restrictions.eq(AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));

		DetachedCriteria subCriteria = DetachedCriteria.forClass(AghEspecialidades.class,"esp1");
		
		subCriteria.setProjection(Projections.projectionList()
				.add(Projections.property("esp1." + AghEspecialidades.Fields.SEQ.toString()), "esp1_seq"));
		
		DetachedCriteria subSubCriteria = DetachedCriteria.forClass(AacConsultas.class,"con3");
		
		subSubCriteria.createAlias("con3."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grade3");
		subSubCriteria.createAlias("grade3." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "gradeEsp3");
		subSubCriteria.setProjection(Projections.projectionList()
				.add(Projections.property("gradeEsp3." + AghEspecialidades.Fields.ESP_SEQ.toString()), "gradeEsp3_esp_seq"));
		
		criteria.add(Restrictions.isNotNull(AacConsultas.Fields.COD_CENTRAL.toString()));
		subSubCriteria.add(Restrictions.eq("con3."+AacConsultas.Fields.NUMERO.toString(),conNumero));
		
		criteria.addOrder(Order.desc(AacConsultas.Fields.DATA_CONSULTA.toString()));
		
		criteria.add(Subqueries.propertyIn("gradeEsp." + AghEspecialidades.Fields.SEQ.toString(),subCriteria));
		subCriteria.add(Subqueries.propertyEq("esp1." + AghEspecialidades.Fields.ESP_SEQ.toString(),subSubCriteria));
		
		List<AacConsultas> consultas = executeCriteria(criteria, 0, 1, null, true);
		if(consultas!=null && !consultas.isEmpty()){
			return consultas.get(0).getCodCentral();
		}
		return null;
		
	}
	
	/**
	 * consulta para verificar sinalização "selecao" ou "implante coclear"
	 * #42010M - C5
	 */
	public List<String> obterEspecialidadeConsultaPorEtapa(Integer parametroPhiSeq, Integer numConsulta){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		criteria.createAlias("C." + AacConsultas.Fields.PROCEDIMENTOS_HOSPITALARES.toString(), "CP");
		criteria.createAlias("C." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G");
		criteria.createAlias("G." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "E");		
		
		DetachedCriteria subquery = DetachedCriteria.forClass(AacProcedHospEspecialidades.class, "PHP1");
		subquery.createAlias("PHP1." + AacProcedHospEspecialidades.Fields.PROCED_HOSP_INTERNO.toString(), "PHI1", JoinType.INNER_JOIN, Restrictions.eq("PHP1." + AacProcedHospEspecialidades.Fields.PHI_SEQ.toString(), parametroPhiSeq));
		subquery.setProjection(Projections.property("PHP1." + AacProcedHospEspecialidades.Fields.ESPECIALIDADE_SEQ));

		criteria.add(Restrictions.or(Subqueries.propertyIn("E." + AghEspecialidades.Fields.SEQ.toString(),subquery), Subqueries.propertyIn("E." + AghEspecialidades.Fields.ESP_SEQ.toString(), subquery)));
		criteria.add(Restrictions.eq("C." + AacConsultas.Fields.NUMERO.toString(), numConsulta));
		criteria.add(Restrictions.le("C." + AacConsultas.Fields.DATA_CONSULTA.toString(), new Date()));
		criteria.setProjection(Projections.property("E." + AghEspecialidades.Fields.SIGLA.toString()));

		return executeCriteria(criteria);
	}
	
		
		
		
	/**
	 * C17 #47668
	 * @param numeroConsulta
	 * @return
	 */
	public AacConsultas obternomeEspecialidadeDataConsulta(final Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		
		criteria.createAlias("C." + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "G");
		criteria.createAlias("G." + AacGradeAgendamenConsultas.Fields.EQUIPE.toString(), "E");
		criteria.add(Restrictions.eq("C." + AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		List<AacConsultas> listConsulta = executeCriteria(criteria);
		if (listConsulta != null && !listConsulta.isEmpty()) {
			return listConsulta.get(0);
		}
		return null;
	}	
	/**
	 * C7/ Obtem a lista de consultas daquele paciente para 'Cand Apac Otorrino' ou 'AIC'(ESP_SEQ=842)
	 * @param dtInicio
	 * @param dtFim
	 * @param pacCodigo
	 * @return
	 */
	public List<AacConsultas> obterConsultasPorPeriodoPorPacCodigo(Date dtInicio, Date dtFim, Integer pacCodigo){
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "C");
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.createAlias(ALIAS_C_PONTO + AacConsultas.Fields.PROCEDIMENTOS_HOSPITALARES.toString(), "CPH", JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("ESP." + AghEspecialidades.Fields.CARACTERISTICAS.toString(), "CAR");
		
		criteria.add(Restrictions.eq(ALIAS_C_PONTO + AacConsultas.Fields.PAC_CODIGO.toString(), pacCodigo));
		criteria.add(Restrictions.gt(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(),dtInicio));
		criteria.add(Restrictions.le(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString(),dtFim));
		criteria.add(Restrictions.or(Restrictions.like("CAR." + AghCaractEspecialidades.Fields.CARACTERISTICA.toString(), DominioCaracEspecialidade.CAND_APAC_OTORRINO), 
				Restrictions.and(Restrictions.eq("CAR." + AacGradeAgendamenConsultas.Fields.ESP_SEQ.toString(), Short.valueOf((short) 842)), Restrictions.isNotNull("CPH." + AacConsultaProcedHospitalar.Fields.CON_NUMERO.toString()))));
		
		criteria.addOrder(Order.asc(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.setProjection(Projections.projectionList().add(Projections.distinct(Projections.property(ALIAS_C_PONTO + AacConsultas.Fields.NUMERO.toString())), AacConsultas.Fields.NUMERO.toString()).add(Projections.property(ALIAS_C_PONTO + AacConsultas.Fields.DATA_CONSULTA.toString()), AacConsultas.Fields.DATA_CONSULTA.toString()));
		criteria.setResultTransformer(Transformers.aliasToBean(AacConsultas.class));
		return executeCriteria(criteria);
	}
	
	public boolean verificarSeConsultaPossuiConvenioSaudePlano(Integer conNumero, Short convenio, Byte plano) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "con");
		criteria.createAlias("con." + AacConsultas.Fields.CONVENIO_SAUDE_PLANO.toString(), "convenio", JoinType.INNER_JOIN);
	
		criteria.setProjection(Projections.property("con." + AacConsultas.Fields.NUMERO.toString()));
		criteria.add(Restrictions.eq("con." + AacConsultas.Fields.NUMERO.toString(), conNumero));
		criteria.add(Restrictions.eq("con." + AacConsultas.Fields.CSP_CNV_CODIGO.toString(), convenio));
		criteria.add(Restrictions.eq("con." + AacConsultas.Fields.CSP_SEQ.toString(), plano));
		
		return executeCriteriaExists(criteria);
	}
	
	public AacConsultas obterAacConsultasJoinGradeEEspecialidadeUnidadeFuncional(final Integer numeroConsulta) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		
		criteria.createAlias(AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "grade");
		criteria.createAlias(AacConsultas.Fields.CONTROLE.toString(), "controle",JoinType.LEFT_OUTER_JOIN);
//		criteria.createAlias("controle."+MamControles.Fields.SITUACAO_ATENDIMENTOS.toString(), "situacao",JoinType.LEFT_OUTER_JOIN);
		criteria.createAlias("grade." + AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "gradeEsp");
		criteria.createAlias("grade." + AacGradeAgendamenConsultas.Fields.UNIDADE_FUNCIONAL.toString(), "unidfunc");
	
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numeroConsulta));
		
		return (AacConsultas) executeCriteriaUniqueResult(criteria);
	}	
	
	public List<Object[]> obterHorarioConsultas(String dataInicio, String dataFim, Integer grade){
		
		final StringBuilder query = new StringBuilder(2000);
		
		query.append(" SELECT DISTINCT");
		
		query.append(" (CASE WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '1' THEN 'DOM'");
		query.append(" WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '2' THEN 'SEG'");
		query.append(" WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '3' THEN 'TER'");
		query.append(" WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '4' THEN 'QUA'");
		query.append(" WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '5' THEN 'QUI'");
		query.append(" WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '6' THEN 'SEX'");
		query.append(" WHEN TO_CHAR(this_.DT_CONSULTA,'D') = '7' THEN 'SAB'");
		query.append(" ELSE '' END) ");
		query.append(" ,TO_CHAR(THIS_.DT_CONSULTA,'D') DIA");
		query.append(" ,TO_CHAR(THIS_.DT_CONSULTA,'hh24:mi') HORA");
		query.append(" ,CAA1_.SIGLA TIPO");
		query.append(" ,COUNT(TO_CHAR(this_.DT_CONSULTA,'DDMMYYYY'))");
		query.append(" from AGH.AAC_CONSULTAS this_ ");
		
		query.append(" inner join AGH.AAC_CONDICAO_ATENDIMENTOS caa1_ on caa1_.SEQ = ");
		query.append("(SELECT DISTINCT (CASE WHEN conj.fag_caa_seq is null THEN this_.FAG_CAA_SEQ "); 
				query.append(" ELSE conj.fag_caa_seq END) ");  
        query.append(" FROM aac_consultas_jn conj ");  
        query.append(" WHERE ((conj.numero = THIS_.NUMERO AND conj.ind_sit_consulta = 'G' ");
        query.append(" and conj.jn_date_time = (SELECT min(conj2.jn_date_time) FROM aac_consultas_jn conj2 ");
        							query.append(" WHERE conj2.numero = conj.numero AND conj2.ind_sit_consulta = 'G') ) ");
		query.append(" or (conj.numero = (SELECT MIN(NUMERO) FROM aac_consultas_jn) and not exists (SELECT distinct ");
		query.append(" (CASE WHEN conj3.fag_caa_seq is null THEN this_.FAG_CAA_SEQ ELSE conj3.fag_caa_seq END) ");
		query.append(" FROM aac_consultas_jn conj3 ");
		query.append(" WHERE ((conj3.numero = this_.numero AND conj3.ind_sit_consulta = 'G' ");
		query.append(" and conj3.jn_date_time = ( ");
		query.append(" SELECT min(conj4.jn_date_time) FROM aac_consultas_jn conj4 ");
		query.append(" WHERE conj4.numero = conj3.numero AND conj4.ind_sit_consulta = 'G') ) ) ) ) ) ) ");
		
        query.append(" WHERE THIS_.GRD_SEQ= ").append(grade);
		query.append(" AND THIS_.DT_CONSULTA BETWEEN TO_DATE('" + dataInicio + "','dd/MM/yyyy HH24:mi:ss') AND TO_DATE('" + dataFim + "','dd/MM/yyyy HH24:mi:ss')");
		query.append(" AND THIS_.IND_EXCEDE_PROGRAMACAO = 'N'");
		
		query.append(" GROUP BY ");
		query.append(" TO_CHAR(this_.dt_consulta,'hh24:mi') ");
		query.append(" ,TO_CHAR(this_.DT_CONSULTA,'D') ");
		query.append(" ,CAA1_.SIGLA ");
		query.append(" ,TO_CHAR(this_.DT_CONSULTA,'DDMMYYYY') ");
		
		query.append(" ORDER BY HORA, DIA ");
		
		final SQLQuery sql = createSQLQuery(query.toString());
		
        return sql.list();
	}
	
	public AacSituacaoConsultas obterSituacaoConsulta(Integer numero) {
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class);
		criteria.add(Restrictions.eq(AacConsultas.Fields.NUMERO.toString(), numero));
		criteria.setProjection(Projections.projectionList().add(Projections.property(AacConsultas.Fields.SITUACAO_CONSULTA.toString())));
		return (AacSituacaoConsultas) executeCriteriaUniqueResult(criteria);
	}
	
	/**
	 * #49992 P2 - CONSULTA PARA OBTER CUR_ESP]
	 */
	public CurEspecialidadeVO obterCurEspPorNumeroConsulta(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ESP."+AghEspecialidades.Fields.CLINICA_CODIGO.toString()), CurEspecialidadeVO.Fields.CLC_CODIGO.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.IND_ESP_PEDIATRICA.toString()), CurEspecialidadeVO.Fields.IND_ESP_PEDIATRICA.toString()));
		
		criteria.setResultTransformer(Transformers.aliasToBean(CurEspecialidadeVO.class));
		
		criteria.createAlias("CON."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.add(Restrictions.eq("CON."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		return (CurEspecialidadeVO) executeCriteriaUniqueResult(criteria);
	}

	/**
	 * #49992 C2
	 */
	public EspecialidadeRelacionadaVO obterDadosEspecialidadesRelacionadoAConsulta(Integer conNumero){
		
		DetachedCriteria criteria = DetachedCriteria.forClass(AacConsultas.class, "CON");
		
		criteria.setProjection(Projections.projectionList()
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SEQ.toString()), EspecialidadeRelacionadaVO.Fields.SEQ.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.ESP_SEQ.toString()), EspecialidadeRelacionadaVO.Fields.ESP_SEQ.toString())
				.add(Projections.property("GRD."+AacGradeAgendamenConsultas.Fields.USL_UNF_SEQ.toString()), EspecialidadeRelacionadaVO.Fields.USL_UNF_SEQ.toString())
				.add(Projections.property("ESP."+AghEspecialidades.Fields.SIGLA.toString()), EspecialidadeRelacionadaVO.Fields.SIGLA.toString())
				);
		
		criteria.setResultTransformer(Transformers.aliasToBean(EspecialidadeRelacionadaVO.class));
		
		criteria.createAlias("CON."+AacConsultas.Fields.GRADE_AGENDA_CONSULTA.toString(), "GRD");
		criteria.createAlias("GRD."+AacGradeAgendamenConsultas.Fields.ESPECIALIDADE.toString(), "ESP");
		criteria.add(Restrictions.eq("CON."+AacConsultas.Fields.NUMERO.toString(), conNumero));
		
		return (EspecialidadeRelacionadaVO) executeCriteriaUniqueResult(criteria);
	}
}