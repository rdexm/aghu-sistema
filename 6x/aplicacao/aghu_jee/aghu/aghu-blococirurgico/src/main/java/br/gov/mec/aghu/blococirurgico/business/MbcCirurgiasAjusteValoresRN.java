package br.gov.mec.aghu.blococirurgico.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.joda.time.DateTime;
import org.joda.time.Minutes;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.MbcCirurgiasRN.MbcCirurgiasRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCaractSalaEspDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDestinoPacienteDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSubstEscalaSalaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemana;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioNaturezaFichaAnestesia;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemPacienteCirurgia;
import br.gov.mec.aghu.dominio.DominioRegimeProcedimentoCirurgicoSus;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoCirurgia;
import br.gov.mec.aghu.dominio.DominioTipoProcedimentoCirurgico;
import br.gov.mec.aghu.dominio.DominioUtilizacaoSala;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MbcAgendaAnestesia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendaProcedimento;
import br.gov.mec.aghu.model.MbcAgendaSolicEspecial;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcAnestesiaCirurgias;
import br.gov.mec.aghu.model.MbcCaractSalaEsp;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcDestinoPaciente;
import br.gov.mec.aghu.model.MbcEspecialidadeProcCirgs;
import br.gov.mec.aghu.model.MbcMatOrteseProtCirg;
import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.MbcSolicitacaoEspExecCirg;
import br.gov.mec.aghu.model.MbcSubstEscalaSala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.procedimentoterapeutico.business.IProcedimentoTerapeuticoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Classe responsável por prover os métodos de negócio de ajustes de valores de MbcCirurgias.
 * 
 * @autor fwinck
 * 
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength" })
@Stateless
public class MbcCirurgiasAjusteValoresRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcCirurgiasAjusteValoresRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcSubstEscalaSalaDAO mbcSubstEscalaSalaDAO;

	@Inject
	private MbcProfCirurgiasDAO mbcProfCirurgiasDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;

	@Inject
	private MbcDestinoPacienteDAO mbcDestinoPacienteDAO;

	@Inject
	private MbcCaractSalaEspDAO mbcCaractSalaEspDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;


	@EJB
	private MbcAgendaOrtProteseRN mbcAgendaOrtProteseRN;

	@EJB
	private IProcedimentoTerapeuticoFacade iProcedimentoTerapeuticoFacade;

	@EJB
	private MbcAgendasRN mbcAgendasRN;

	@EJB
	private ISolicitacaoExameFacade iSolicitacaoExameFacade;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@EJB
	private MbcAgendaHemoterapiaRN mbcAgendaHemoterapiaRN;

	@EJB
	private IParametroFacade iParametroFacade;

	@EJB
	private MbcAgendaAnestesiaRN mbcAgendaAnestesiaRN;

	@EJB
	private MbcAgendaSolicEspecialRN mbcAgendaSolicEspecialRN;

	@EJB
	private MbcCirurgiasVerificacoesRN mbcCirurgiasVerificacoesRN;

	@EJB
	private MbcAgendaProcedimentoRN mbcAgendaProcedimentoRN;

	@EJB
	private MbcProfCirurgiasRN mbcProfCirurgiasRN;

	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private MbcCirurgiasDAO cirurgiasDAO;
	
	private static final long serialVersionUID = 540558487712295755L;

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_atu_digt_ns
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 */
	public void atualizarDigitoNotaSala(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) {

		// RN 1 Se o valor da coluna dthr_digit_nota_sala mudou de null para not null então
		if (cirurgia.getDataDigitacaoNotaSala() != null && cirurgiaOld.getDataDigitacaoNotaSala() == null) {

			// RN 1.1 Seta ind_digt_nota_sala com o valor ‘S’.
			cirurgia.setDigitaNotaSala(Boolean.TRUE);

			// RN 1.2 Se o valor da coluna situacao for diferente de ‘RZDA’ e ‘CANC’ então seta para ‘RZDA’
			if (!DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao()) && !DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {
				cirurgia.setSituacao(DominioSituacaoCirurgia.RZDA);
			}

			// RN 2 Se o valor da coluna dthr_digit_nota_sala não mudou de de null para not null então
		} else {
			// RN 2.1 Se ind_digt_nota_sala mudou de ‘N’ para ‘S’ então seta dthr_digit_nota_sala com o valor da data e hora do momento.
			if (cirurgia.getDigitaNotaSala() && !cirurgiaOld.getDigitaNotaSala()) {
				cirurgia.setDataDigitacaoNotaSala(new Date());

				// RN 2.2 Se o valor da coluna situacao for diferente de ‘RZDA’ e ‘CANC’ então seta para ‘RZDA’
				if (!DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao()) && !DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {
					cirurgia.setSituacao(DominioSituacaoCirurgia.RZDA);
				}
			} else if (cirurgia.getDataDigitacaoNotaSala() != null
					&& (!DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao()) && !DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()))) {
				
				cirurgia.setSituacao(DominioSituacaoCirurgia.RZDA);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_dtprev_i
	 * 
	 * @param cirurgia
	 */
	public void verificarDataPrevisaoInicio(MbcCirurgias cirurgia) {
		// Se dthr prev inicio > null, atualiza a dthr inicio cirg com a data de previsão
		if (cirurgia.getDataPrevisaoInicio() != null) {
			cirurgia.setDataInicioCirurgia(cirurgia.getDataPrevisaoInicio());
			cirurgia.setDataInicioOrdem(cirurgia.getDataPrevisaoInicio());
		} else {
			cirurgia.setDataInicioCirurgia(null);
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_prev_fim
	 * 
	 * @param cirurgia
	 */
	public void verificarDataPrevisaoFim(MbcCirurgias cirurgia) {
		if(cirurgia.getDataPrevisaoInicio() != null){
			if (DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao())) {
		
				// Pego a data prevista de inicio
				Date previsaoFim = cirurgia.getDataPrevisaoInicio();
		
				// Adiciono horas
				if (cirurgia.getTempoPrevistoHoras() != null) {
					previsaoFim = DateUtil.adicionaHoras(previsaoFim, cirurgia.getTempoPrevistoHoras());
				}
		
				// Adiciono minutos
				if (cirurgia.getTempoPrevistoMinutos() != null) {
					previsaoFim = DateUtil.adicionaMinutos(previsaoFim, cirurgia.getTempoPrevistoMinutos());
				}
				// Atualiza a dthr prev fim e dthr fim cirg com o valor resultante
				cirurgia.setDataPrevisaoFim(previsaoFim);
				cirurgia.setDataFimCirurgia(previsaoFim);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_ultdg_ns
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void atualizarUltDigitacaoNotaSala(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// RN6 do documento de análise
		if (cirurgia.getDigitaNotaSala()) {
			cirurgia.setDataUltimaAtualizacaoNotaSala(new Date());
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_ver_sit_canc
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void verificarSituacaoCancelada(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// RN11.1 verifica se situação é cancelada e se foi informado um motivo para o cancelamento
		if (DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()) && cirurgia.getMotivoCancelamento() == null) {
			throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00287);
		}

		// RN11.2 Se motivo do cancelamento foi informado, seta situação como "CANC"
		if (cirurgia.getMotivoCancelamento() != null) {
			cirurgia.setSituacao(DominioSituacaoCirurgia.CANC);
		}

		// RN11.3 se situação for transferida e o destino do paciente foi a SR=sala de recuperação
		Boolean destinoPacSalaRec = cirurgia.getMotivoCancelamento() != null && cirurgia.getMotivoCancelamento().getDestSr() ? Boolean.TRUE : Boolean.FALSE;
		if (DominioSituacaoCirurgia.TRAN.equals(cirurgia.getSituacao()) && destinoPacSalaRec) {

			// Atualizo o destino do paciente
			AghParametros parametroDestPaciente = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DEST_SALA_REC);
			MbcDestinoPaciente destPaciente = getMbcDestinoPacienteDAO().obterOriginal(parametroDestPaciente.getVlrNumerico());
			cirurgia.setDestinoPaciente(destPaciente);
			// -*-*--*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
			// Atualizo a data de saída da sala
			cirurgia.setDataSaidaSala(new Date());
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_atu_info_sit
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void atualizarInformacaoSituacao(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {

		if (CoreUtil.modificados(cirurgia.getSituacao(), cirurgiaOld.getSituacao())) {
			
			Boolean indTemDescricao = cirurgia.getTemDescricao(); //Criado variável para não alterar valor de MBC_CIRURGIAS.IND_TEM_DESCRICAO
			if(indTemDescricao == null){
				indTemDescricao = Boolean.FALSE;
			}
			
			atualizarInformacaoSituacaoBloco1e2(cirurgia, cirurgiaOld, indTemDescricao);
			
			// BLOCO 3
			// if p_old_situacao = 'TRAN' and p_new_situacao in ('AGND','CHAM','PREP') then
			if (DominioSituacaoCirurgia.TRAN.equals(cirurgiaOld.getSituacao())
					&& (DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao()) || DominioSituacaoCirurgia.CHAM.equals(cirurgia.getSituacao()) || DominioSituacaoCirurgia.PREP
							.equals(cirurgia.getSituacao()))) {
				//(!cirurgia.getTemDescricao())
				if (Boolean.FALSE.equals(indTemDescricao)) {
					cirurgia.setDataInicioCirurgia(cirurgia.getDataPrevisaoInicio());
					cirurgia.setDataFimCirurgia(cirurgia.getDataPrevisaoFim());
				}
				cirurgia.setDataEntradaSala(null);
			}

			// BLOCO 4
			// if p_new_situacao = 'TRAN' then
			if (DominioSituacaoCirurgia.TRAN.equals(cirurgia.getSituacao())) {

				cirurgia.setDataSaidaSala(null);
				//!cirurgia.getTemDescricao()
				if (Boolean.FALSE.equals(indTemDescricao)) {
					cirurgia.setDataEntradaSala(new Date());
					cirurgia.setDataInicioCirurgia(new Date());
					cirurgia.setDataFimCirurgia(null);
				}
			}
		}
	}

	private void atualizarInformacaoSituacaoBloco1e2(MbcCirurgias cirurgia,
			MbcCirurgias cirurgiaOld, Boolean indTemDescricao) {
		// BLOCO 1
		if (DominioSituacaoCirurgia.RZDA.equals(cirurgia.getSituacao()) && !cirurgia.getDigitaNotaSala()) {

			cirurgia.setDataSaidaSala(new Date());

			if (Boolean.FALSE.equals(indTemDescricao)) {
				if (cirurgia.getDataFimCirurgia() == null) {
					cirurgia.setDataFimCirurgia(new Date());
				}
			} else {
				if (cirurgia.getDataEntradaSala() == null) {
					cirurgia.setDataEntradaSala(cirurgia.getDataInicioCirurgia());
				}
			}
		}

		// BLOCO 2
		// if p_old_situacao = 'RZDA' and p_new_situacao in('AGND','CHAM','PREP','TRAN')then
		if (DominioSituacaoCirurgia.RZDA.equals(cirurgiaOld.getSituacao())
				&& (DominioSituacaoCirurgia.AGND.equals(cirurgia.getSituacao()) || DominioSituacaoCirurgia.CHAM.equals(cirurgia.getSituacao())
						|| DominioSituacaoCirurgia.PREP.equals(cirurgia.getSituacao()) || DominioSituacaoCirurgia.TRAN.equals(cirurgia.getSituacao()))) {

			if (cirurgia.getDigitaNotaSala()) {
				/*
				 * p_new_dthr_entrada_sala := null; p_new_dthr_saida_sala :=null; p_new_dthr_entrada_sr := null; p_new_dthr_saida_sr := null; p_new_dpa_seq := null;
				 */
				cirurgia.setDataEntradaSala(null);
				cirurgia.setDataSaidaSala(null);
				cirurgia.setDataEntradaSr(null);
				cirurgia.setDataSaidaSr(null);
				cirurgia.setDestinoPaciente(null);

				if (Boolean.FALSE.equals(indTemDescricao)) {
					cirurgia.setDataInicioCirurgia(cirurgia.getDataPrevisaoInicio());
					cirurgia.setDataFimCirurgia(cirurgia.getDataPrevisaoFim());
				}
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_tempo_o2
	 * 
	 * @param cirurgias
	 * @throws ApplicationBusinessException
	 */
	public void atualizarTempoO2(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// #35882 - atualizar apenas se a utulização de O2 foi indicada
		if (cirurgia.getUtilizaO2()) {
			if (cirurgia.getDataInicioAnestesia() == null) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00328);
			}

			if (cirurgia.getDataFimAnestesia() == null) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00329);
			}
		
			// Calculo a diferença em minutos entre as datas
			Minutes minutes = Minutes.minutesBetween(new DateTime(cirurgia.getDataInicioAnestesia()), new DateTime(cirurgia.getDataFimAnestesia()));
			cirurgia.setTempoUtilizacaoO2((short) minutes.getMinutes());
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_tempo_az
	 * 
	 * @param cirurgias
	 * @throws ApplicationBusinessException
	 */
	public void atualizarTempoAZ(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		// #35882 - atualizar apenas se a utulização de O2 foi indicada
		if (cirurgia.getUtilizaProAzot()) {
			if (cirurgia.getDataInicioAnestesia() == null) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00330);
			}

			if (cirurgia.getDataFimAnestesia() == null) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00331);
			}
	
			// Calculo a diferença em minutos entre as datas
			Minutes minutes = Minutes.minutesBetween(new DateTime(cirurgia.getDataFimAnestesia()), new DateTime(cirurgia.getDataInicioAnestesia()));
			cirurgia.setTempoUtilizacaoProAzot((short) minutes.getMinutes());
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_convenio
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void atualizarConvenio(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		
		Integer atdSeq = null;
		Integer oldAtdSeq = null;
		DominioOrigemPacienteCirurgia oldOrigemPacienteCirurgia = null;
		DominioOrigemPacienteCirurgia origemPacienteCirurgia = cirurgia.getOrigemPacienteCirurgia();
		
		if (cirurgia.getAtendimento() != null) {
			atdSeq = cirurgia.getAtendimento().getSeq();
		}
		
		if (cirurgiaOld != null) {
			oldOrigemPacienteCirurgia = cirurgiaOld.getOrigemPacienteCirurgia();
			if (cirurgiaOld.getAtendimento() != null) {
				oldAtdSeq = cirurgiaOld.getAtendimento().getSeq();
			}
		}
		
		if (atdSeq != null) {
			
			if (oldAtdSeq == null 
					|| (!atdSeq.equals(oldAtdSeq)) 
					|| (atdSeq.equals(oldAtdSeq) && DominioOrigemPacienteCirurgia.I.equals(origemPacienteCirurgia) && !DominioOrigemPacienteCirurgia.I.equals(oldOrigemPacienteCirurgia))) {

				if (cirurgia.getAtendimento() == null) {
					
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00504);
					
				} else {
					
					if (DominioOrigemAtendimento.I.equals(cirurgia.getAtendimento().getOrigem()) && cirurgia.getAtendimento().getInternacao() != null) {
						
						final AinInternacao internacao = cirurgia.getAtendimento().getInternacao();
						cirurgia.setConvenioSaudePlano(internacao.getConvenioSaudePlano());
						
					}
						
				}
				
			}
			
		}
		
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_proc_pac
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void atualizarProcPaciente(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		// Verifico se mudou de 'N' pra 'S'
		if (cirurgia.getDigitaNotaSala() && !cirurgiaOld.getDigitaNotaSala()) {
			// Se a situação é diferente de cancelada
			if (!DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao())) {

				AipPacientes aipPacientes = getPacienteFacade().obterAipPacientesPorChavePrimaria(cirurgia.getPaciente().getCodigo());

				if (aipPacientes == null) {
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00520);
				} else {
					if (DateUtil.validaDataMenor(aipPacientes.getDtUltProcedimento(), cirurgia.getDataInicioCirurgia())) {
						// Atualizo a data da último procedimento do paciente
						aipPacientes.setDtUltProcedimento(cirurgia.getDataInicioCirurgia());
						aipPacientes = getPacienteFacade().atualizarAipPacientes(aipPacientes, false);
						cirurgia.setPaciente(aipPacientes);
					}
				}
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_canc_quimio atualiza a tabela mpt_agenda_prescricoes
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void cancelarQuimio(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		// se mudou a situação da cirurgia pra cancelada, então executa a procedure somente no oracle
		if (DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()) && !DominioSituacaoCirurgia.CANC.equals(cirurgiaOld.getSituacao())) {
			getProcedimentoTerapeuticoFacade().atualizarAgendaPrescricaoPorCirurgiaCallableStatement(cirurgia.getSeq());
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_cancela
	 * 
	 * @param cirurgia
	 * @throws ApplicationBusinessException
	 */
	public void atualizarCancelamento(MbcCirurgias cirurgia) throws ApplicationBusinessException {
		if (DominioSituacaoCirurgia.CANC.equals(cirurgia.getSituacao()) && (cirurgia.getTemDescricao() == null || !cirurgia.getTemDescricao())) {
			cirurgia.setTemDescricao(null);
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_atd_exme
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @param nomeMicrocomputador
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAtendimentoSolicitacaoExames(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, final String nomeMicrocomputador)
	throws ApplicationBusinessException {
		// Verifico se o registro alterado possuí atendimento com solicitações de exames
		List<AelSolicitacaoExames> solicitacoes =  examesFacade.obterSolicitacaoExamesPorAtendimento(cirurgiaOld.getAtendimento().getSeq());
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		// Se tiver então, itera na coleção de solicitações
		if (!solicitacoes.isEmpty()) {

			for (AelSolicitacaoExames aelSolicitacaoExames : solicitacoes) {

				// Verifico se a data de solicitação é maior que a data do atendimento
				if (cirurgia.getAtendimento() != null && DateUtil.validaDataMaior(aelSolicitacaoExames.getCriadoEm(), cirurgia.getAtendimento().getDthrInicio())) {

					// Defino o novo atendimento
					aelSolicitacaoExames.setAtendimento(cirurgia.getAtendimento());

					// Atualização do convenio/plano de cada solicitação
					aelSolicitacaoExames.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());
					try {
						// atualizo no banco passando pelas regras das solicitações.
						getSolicitacaoExameFacade().atualizar(aelSolicitacaoExames, null, nomeMicrocomputador, servidorLogado);
					} catch (BaseException e) {
						logError(e);
						throw new ApplicationBusinessException(e);
					}
				}
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.mbcp_atu_agenda_canc
	 * 
	 * @param cirurgia
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void atualizarAgendaCancelamento(MbcCirurgias cirurgia) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

		// pego os parametros necessários
		AghParametros parametroSusPadrao = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_CONVENIO_SUS_PADRAO);
		AghParametros parametroMotivoDesmarcar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR);
		AghParametros parametroMotivoDesmarcarAdm = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_MOT_DESMARCAR_ADM);

		
		MbcAgendas agenda = cirurgiasDAO.obterCirurgiaPorChavePrimaria(cirurgia.getSeq()).getAgenda();
		
		// se foi informada agenda na cirurgia
		if (agenda != null) {
			boolean v_agd_ind_exclusao = false;

			// Se o convenio for diferente de sus e motivo....
			if (!cirurgia.getConvenioSaudePlano().getId().getCnvCodigo().equals(parametroSusPadrao.getVlrNumerico().shortValue())
					|| cirurgia.getMotivoCancelamento().getSeq().equals(parametroMotivoDesmarcar.getVlrNumerico().shortValue()) || cirurgia.getMotivoCancelamento().getSeq().equals(parametroMotivoDesmarcarAdm.getVlrNumerico().shortValue())) {
				v_agd_ind_exclusao = true;
			}

			// Defino alguns valores null e agenda como cancelada

			
			agenda.setIndSituacao(DominioSituacaoAgendas.CA);
			agenda.setIndExclusao(v_agd_ind_exclusao);
			agenda.setDtAgenda(null);
			agenda.setDthrPrevInicio(null);
			agenda.setDthrPrevFim(null);
			agenda.setSalaCirurgica(null);

			// Atualizo a agenda
			cirurgia.setAgenda(getMbcAgendasRN().persistirAgenda(agenda, servidorLogado));

			// Busco as agendas hemoterapicas
			Set<MbcAgendaHemoterapia> agendasHemo = getMbcAgendaHemoterapiaDAO().listarAgendasHemoterapiaPorAgendaSeq(agenda.getSeq());

			// Excluo as hemoterapias
			for (MbcAgendaHemoterapia mbcAgendaHemoterapia : agendasHemo) {
				getMbcAgendaHemoterapiaRN().excluirAgendaHemoterapia(mbcAgendaHemoterapia);
			}
		} else {
			boolean continuar = true;

			// Se o convenio for diferente de sus e motivo....
			if (!cirurgia.getConvenioSaudePlano().getId().getCnvCodigo().equals(parametroSusPadrao.getVlrNumerico().shortValue())
					|| !DominioNaturezaFichaAnestesia.ELE.equals(cirurgia.getNaturezaAgenda()) || cirurgia.getMotivoCancelamento().equals(parametroMotivoDesmarcar.getSeq())
					|| cirurgia.getMotivoCancelamento().equals(parametroMotivoDesmarcarAdm.getSeq())) {
				continuar = false;
			}

			if (continuar) {

				if (cirurgia.getUnidadeFuncional() == null || cirurgia.getUnidadeFuncional().getIntervaloEscalaCirurgia() == null
						|| cirurgia.getUnidadeFuncional().getIntervaloEscalaProced() == null) {
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00947);
				}

				// Busca equipe
				MbcProfCirurgias profissionalCirur = null;
				if (cirurgia.getProfCirurgias() != null) {
					for (MbcProfCirurgias profCirur : cirurgia.getProfCirurgias()) {
						if (profCirur.getIndResponsavel()) {
							profissionalCirur = profCirur;
							break;
						}
					}
				}
				if (profissionalCirur == null) {
					throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_00976);
				}

				// Busca procedimento principal
				MbcEspecialidadeProcCirgs espProcdCirur = null;
				Byte v_qtde_proc = null;
				DominioTipoProcedimentoCirurgico v_pci_tipo = null;
				getMbcCirurgiasVerificacoesRN().verificarProcedimentoPrincipal(cirurgia, espProcdCirur, v_qtde_proc, v_pci_tipo);

				// Crio novo Objeto MbcAgendas
				agenda = new MbcAgendas();
				agenda.setPaciente(getPacienteFacade().obterAipPacientesPorChavePrimaria(cirurgia.getPaciente().getCodigo()));
				agenda.setUnidadeFuncional(cirurgia.getUnidadeFuncional());
				agenda.setEspecialidade(cirurgia.getEspecialidade());
				agenda.setProfAtuaUnidCirgs(profissionalCirur.getMbcProfAtuaUnidCirgs());

				if (DominioOrigemPacienteCirurgia.A.equals(cirurgia.getOrigemPacienteCirurgia())) {
					agenda.setRegime(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO);
				} else if (DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia())) {
					agenda.setRegime(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO);
				}
				agenda.setIndSituacao(DominioSituacaoAgendas.CA);
				agenda.setIndExclusao(Boolean.FALSE);
				agenda.setEspProcCirgs(espProcdCirur);
				agenda.setTempoSala(calcularTempoSala(cirurgia, v_pci_tipo));
				agenda.setIndPrioridade(Boolean.FALSE);
				agenda.setIndPrecaucaoEspecial(cirurgia.getPrecaucaoEspecial());
				agenda.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());
				agenda.setIndGeradoSistema(Boolean.TRUE);
				agenda.setQtdeProc(v_qtde_proc.shortValue());
				agenda.setServidor(servidorLogado);
				if (DominioTipoProcedimentoCirurgico.CIRURGIA.equals(v_pci_tipo)) {
					agenda.setIntervaloEscala(cirurgia.getUnidadeFuncional().getIntervaloEscalaCirurgia());
				} else {
					agenda.setIntervaloEscala(cirurgia.getUnidadeFuncional().getIntervaloEscalaProced());
				}
				// Insiro a agenda
				getMbcAgendasRN().persistirAgenda(agenda, servidorLogado);

				// Inserir procedimentos secundarios
				inserirProcedimentoSecundario(cirurgia, agenda);

				// Inserir anestesias
				inserirAnestesias(cirurgia, agenda);

				// Inserir solicitações especiais
				inserirSolicitacoesEspeciais(cirurgia, agenda);

				// Insere materiais orteses proteses
				inserirMatOrtesesProteses(cirurgia, agenda);
			}
		}
	}

	/**
	 * Calcula o tempo de utilização da dala, usado somente em MBCP_ATU_AGENDA_CANC
	 * 
	 * @param cirurgia
	 * @param v_pci_tipo
	 * @return Date
	 */
	private Date calcularTempoSala(MbcCirurgias cirurgia, DominioTipoProcedimentoCirurgico v_pci_tipo) {
		Calendar c = Calendar.getInstance();
		c.clear();

		int tempoSalaMinutos = 0;
		if (cirurgia.getTempoPrevistoHoras() != null) {
			tempoSalaMinutos += cirurgia.getTempoPrevistoHoras() * 60;
		}
		if (cirurgia.getTempoPrevistoMinutos() != null) {
			tempoSalaMinutos += cirurgia.getTempoPrevistoMinutos();
		}

		if (DominioTipoProcedimentoCirurgico.CIRURGIA.equals(v_pci_tipo)) {
			tempoSalaMinutos -= cirurgia.getUnidadeFuncional().getIntervaloEscalaCirurgia();
		} else {
			tempoSalaMinutos -= cirurgia.getUnidadeFuncional().getIntervaloEscalaProced();
		}
		c.set(Calendar.MINUTE, tempoSalaMinutos);
		return c.getTime();
	}

	/**
	 * Insere procedimentos secundarios, ou seja, que não é o principal
	 * 
	 * @param cirurgia
	 * @param agenda
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void inserirProcedimentoSecundario(MbcCirurgias cirurgia, MbcAgendas agenda) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Set<MbcProcEspPorCirurgias> procCirurgicos = cirurgia.getProcEspPorCirurgias();
		if (procCirurgicos != null) {
			for (MbcProcEspPorCirurgias procCirurgico : procCirurgicos) {
				if (DominioIndRespProc.AGND.equals(procCirurgico.getId().getIndRespProc()) && !procCirurgico.getIndPrincipal() && procCirurgico.getSituacao().isAtivo()) {

					// construo o objeto
					MbcAgendaProcedimento agendaProc = new MbcAgendaProcedimento();
					agendaProc.setCriadoEm(new Date());
					agendaProc.setRapServidores(servidorLogado);
					agendaProc.setQtde(procCirurgico.getQtd().shortValue());
					agendaProc.setMbcAgendas(agenda);
					agendaProc.setMbcEspecialidadeProcCirgs(procCirurgico.getMbcEspecialidadeProcCirgs());
					getMbcAgendaProcedimentoRN().persistirAgendaProcedimentos(null, agendaProc);
				}
			}
		}
	}

	/**
	 * Insere as anestesias da cirurgia
	 * 
	 * @param cirurgia
	 * @param agenda
	 * @param servidorLogado
	 * @throws BaseException
	 */
	private void inserirAnestesias(MbcCirurgias cirurgia, MbcAgendas agenda) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Set<MbcAnestesiaCirurgias> anestesias = cirurgia.getAnestesiaCirurgicas();
		if (anestesias != null) {
			for (MbcAnestesiaCirurgias anestesia : anestesias) {

				MbcAgendaAnestesia novaAnestesia = new MbcAgendaAnestesia();
				novaAnestesia.setCriadoEm(new Date());
				novaAnestesia.setMbcAgendas(agenda);
				novaAnestesia.setMbcTipoAnestesias(anestesia.getMbcTipoAnestesias());
				novaAnestesia.setRapServidores(servidorLogado);

				getMbcAgendaAnestesiaRN().persistirAgendaAnestesia(novaAnestesia);
			}
		}
	}

	/**
	 * Insere as solicitações especiais da cirurgia
	 * 
	 * @param cirurgia
	 * @param agenda
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void inserirSolicitacoesEspeciais(MbcCirurgias cirurgia, MbcAgendas agenda) throws BaseException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Set<MbcSolicitacaoEspExecCirg> solics = cirurgia.getSolicitacaoEspExecCirurgia();
		if (solics != null) {
			for (MbcSolicitacaoEspExecCirg solicEsp : solics) {

				MbcAgendaSolicEspecial newSolic = new MbcAgendaSolicEspecial();

				newSolic.setCriadoEm(new Date());
				newSolic.setMbcAgendas(agenda);
				newSolic.setMbcNecessidadeCirurgica(solicEsp.getMbcNecessidadeCirurgica());
				newSolic.setRapServidores(servidorLogado);
				newSolic.setDescricao(solicEsp.getDescricao());

				getMbcAgendaSolicEspecialRN().persistirAgendaSolicEspecial(newSolic);
			}
		}
	}

	/**
	 * Insere os materiais de ortese e protese utilizados na cirurgia
	 * 
	 * @param cirurgia
	 * @param agenda
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	private void inserirMatOrtesesProteses(MbcCirurgias cirurgia, MbcAgendas agenda) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		Set<MbcMatOrteseProtCirg> ortProts = cirurgia.getMaterialOrteseProtCirur();
		if (ortProts != null) {
			for (MbcMatOrteseProtCirg ortesesProts : ortProts) {

				MbcAgendaOrtProtese newOrtProt = new MbcAgendaOrtProtese();

				newOrtProt.setMbcAgendas(agenda);
				newOrtProt.setScoMaterial(ortesesProts.getScoMaterial());
				newOrtProt.setCriadoEm(new Date());
				newOrtProt.setQtde(ortesesProts.getQtdSolic());

				newOrtProt.setRapServidores(servidorLogado);

				getMbcAgendaOrtProteseRN().persistirAgendaOrtProtese(newOrtProt);
			}
		}
	}

	/**
	 * ORADB mbck_crg_rn.mbcp_atu_agenda_banc
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @param VALIDA_REGRA_BRU
	 * @throws BaseException
	 */
	public void atualizarAgendaBanc(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, boolean VALIDA_REGRA_BRU) throws BaseException {
		// if mbck_crg_rn.valida_regra_bru = 'N' or p_new_agd_seq is null then return; end if;

		if (VALIDA_REGRA_BRU && cirurgia.getAgenda() != null) {
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			// Se foi modificado o valor de unf_seq ou pac_codigo ou csp_cnv_codigo ou csp_seq
			// ou sci_unf_seq ou sci_seqp ou origem_pac_cirg então atualiza mbc_agendas
			if (CoreUtil.modificados(cirurgia.getUnidadeFuncional().getSeq(), cirurgiaOld.getUnidadeFuncional().getSeq())
					|| CoreUtil.modificados(cirurgia.getPaciente().getCodigo(), cirurgiaOld.getPaciente().getCodigo())
					|| (CoreUtil.modificados(cirurgia.getConvenioSaudePlano().getId().getCnvCodigo(), cirurgiaOld.getConvenioSaudePlano().getId().getCnvCodigo()) && CoreUtil
							.modificados(cirurgia.getConvenioSaudePlano().getId().getSeq(), cirurgiaOld.getConvenioSaudePlano().getId().getSeq()))
							|| (CoreUtil.modificados(cirurgia.getSalaCirurgica().getId().getUnfSeq(), cirurgiaOld.getSalaCirurgica().getId().getUnfSeq()) 
							|| CoreUtil.modificados(cirurgia.getSalaCirurgica().getId().getSeqp(), cirurgiaOld.getSalaCirurgica().getId().getSeqp()))
									|| CoreUtil.modificados(cirurgia.getOrigemPacienteCirurgia(), cirurgiaOld.getOrigemPacienteCirurgia())) {// inicio if

				MbcAgendas agenda = cirurgia.getAgenda();
				// atualizo o paciente
				agenda.setPaciente(getPacienteFacade().obterAipPacientesPorChavePrimaria(cirurgia.getPaciente().getCodigo()));
				// atualizo a unidade funcional
				agenda.setUnidadeFuncional(cirurgia.getUnidadeFuncional());
				// atualizo a sala cirurgica
				agenda.setSalaCirurgica(cirurgia.getSalaCirurgica());
				// atualizo o convênio plano de saúde
				agenda.setConvenioSaudePlano(cirurgia.getConvenioSaudePlano());
				// Atualizo o regime com a origem do paciente... sim, é assim mesmo
				if (DominioOrigemPacienteCirurgia.A.equals(cirurgia.getOrigemPacienteCirurgia())) {
					agenda.setRegime(DominioRegimeProcedimentoCirurgicoSus.AMBULATORIO);
				} else if (DominioOrigemPacienteCirurgia.I.equals(cirurgia.getOrigemPacienteCirurgia())) {
					agenda.setRegime(DominioRegimeProcedimentoCirurgicoSus.INTERNACAO);
				}
				// atualizo a agenda
				getMbcAgendasRN().persistirAgenda(agenda, servidorLogado);
			}// final if
		}
	}

	/**
	 * ORADB mbck_crg_rn.rn_crgp_atu_util_sl
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @throws ApplicationBusinessException
	 */
	public void verificarUtilizacaoSala(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld) throws ApplicationBusinessException {
		MbcProfCirurgias profCirurgias = getMbcProfCirurgiasDAO().retornaResponsavelCirurgia(cirurgia);
		
		// Quando há indicação de responsável
		if (profCirurgias != null) {
			
			// Obtem os valores necessários para processar a utilização da sala
			final Date valorData = cirurgia.getDataInicioCirurgia();
			final MbcSalaCirurgica valorSalaUnidade = cirurgia.getSalaCirurgica(); // v_sala e v_unidade
			final DominioNaturezaFichaAnestesia valorNatureza = cirurgia.getNaturezaAgenda();
			
			
			if (valorData!=null) {
				DominioUtilizacaoSala utilizacao = this.getMbcProfCirurgiasRN().verificarUtilizacaoSala(valorData, valorSalaUnidade.getId().getSeqp(), valorSalaUnidade.getId().getUnfSeq(),
					profCirurgias.getServidorPuc().getId().getMatricula(), profCirurgias.getServidorPuc().getId().getVinCodigo(), valorNatureza);

				cirurgia.setUtilizacaoSala(utilizacao);	
			
			}
			else {
				cirurgia.setUtilizacaoSala(DominioUtilizacaoSala.NPR);	
			}

		}
	}

	/**
	 * Parte da rn rn_crgp_atu_util_sl, esse trecho é usado em 2 "if"s grandes, para não escrever duas vezes coloquei em outro método
	 * 
	 * @param cirurgia
	 * @param caractSalaCir
	 * @return
	 */
	public DominioUtilizacaoSala verificacoesReutilizadas(MbcCirurgias cirurgia, Set<MbcCaracteristicaSalaCirg> caractSalaCir) {
		MbcCaracteristicaSalaCirg caracteristicaSala = null;
		if (caractSalaCir != null && !caractSalaCir.isEmpty()) {
			for (MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg : caractSalaCir) {

				Date DTIni = mbcCaracteristicaSalaCirg.getMbcHorarioTurnoCirg().getHorarioInicial();
				Date DTFim = mbcCaracteristicaSalaCirg.getMbcHorarioTurnoCirg().getHorarioFinal();
				DominioDiaSemana diaSemCaract = mbcCaracteristicaSalaCirg.getDiaSemana();
				DominioDiaSemana diaSemCirur = DominioDiaSemana.getDiaDaSemana(getMbcCirurgiasVerificacoesRN().getDiaSemana(cirurgia.getDataInicioCirurgia()));

				if (((DateUtil.validaDataMenor(DTIni, DTFim) && DateUtil.entre(retirarAnoMesDiaMiliData(cirurgia.getDataInicioCirurgia()), retirarAnoMesDiaMiliData(DTIni),
						retirarAnoMesDiaMiliData(DTFim))) || (!DateUtil.validaDataMenor(DTIni, DTFim) && (DateUtil.entre(
								retirarAnoMesDiaMiliData(cirurgia.getDataInicioCirurgia()), retirarAnoMesDiaMiliData(DTIni), getMbcCirurgiasVerificacoesRN().getHoraSemData(24, 00)) || DateUtil
								.entre(retirarAnoMesDiaMiliData(cirurgia.getDataInicioCirurgia()), getMbcCirurgiasVerificacoesRN().getHoraSemData(00, 00), retirarAnoMesDiaMiliData(DTFim)))))
								&& diaSemCaract.equals(diaSemCirur)) {
					caracteristicaSala = mbcCaracteristicaSalaCirg;
				}
			}
		}
		if (caracteristicaSala == null) {
			return DominioUtilizacaoSala.NPR;
		}
		// Se achou uma caracteristica, segue na função

		// Pesquisa as caracteristicas da sala especial por profissional
		MbcProfAtuaUnidCirgs profAtuaUnidCirgs = null;
		if (cirurgia.getProfCirurgias() != null) {
			for (MbcProfCirurgias profCirur : cirurgia.getProfCirurgias()) {
				if (profCirur.getIndResponsavel()) {
					profAtuaUnidCirgs = profCirur.getMbcProfAtuaUnidCirgs();
					break;
				}
			}
		}

		List<MbcCaractSalaEsp> lstCaractSalaEsp = getMbcCaractSalaEspDAO().pesquisarCaractSalaEspPorCaracProfAtuaUniCirur(caracteristicaSala.getSeq(), profAtuaUnidCirgs);
		if (!lstCaractSalaEsp.isEmpty()) {
			return DominioUtilizacaoSala.PRE;
		}
		// *****************************
		// Se não achou nenhum profissional procura um substituto
		List<MbcSubstEscalaSala> substitutos = getMbcSubstEscalaSalaDAO().pesquisarSubstitutoPorCaracProfAtuaUniCirurData(caracteristicaSala.getSeq(),
				DateUtil.truncaData(cirurgia.getDataInicioCirurgia()), profAtuaUnidCirgs);
		if (!substitutos.isEmpty()) {
			return DominioUtilizacaoSala.PRE;
		} else {
			return DominioUtilizacaoSala.NPR;
		}
	}

	/**
	 * ORADB mbck_crg_rn2.rn_crgp_atu_unid_prf
	 * 
	 * @param cirurgia
	 * @param cirurgiaOld
	 * @param servidorLogado
	 * @param veioGeraEscalaD
	 * @throws ApplicationBusinessException
	 */
	public void atualizarUnidadeProfissional(MbcCirurgias cirurgia, MbcCirurgias cirurgiaOld, Boolean veioGeraEscalaD) throws ApplicationBusinessException {

		// Verifico o servidor da unidade antiga
		MbcProfCirurgias profCirurgia = getMbcProfCirurgiasDAO().obterEquipePorCirurgiaUnidade(cirurgia.getSeq(), cirurgiaOld.getUnidadeFuncional().getSeq());
		if (profCirurgia != null) {
			// Verifico se o servidor pode atuar na unidade destino
			List<MbcProfAtuaUnidCirgs> profsAtu = getMbcProfAtuaUnidCirgsDAO().pesquisarProfissionaisUnidCirgAtivoPorServidorUnfSeq(
					profCirurgia.getMbcProfAtuaUnidCirgs().getRapServidores(), cirurgia.getUnidadeFuncional().getSeq());
			if (profsAtu.isEmpty()) {
				throw new ApplicationBusinessException(MbcCirurgiasRNExceptionCode.MBC_01104);
			} else {
				// Nova unidade
				MbcProfCirurgias profCirurgiaNova = getMbcProfCirurgiasDAO().obterEquipePorCirurgiaUnidade(cirurgia.getSeq(), cirurgia.getUnidadeFuncional().getSeq());
				if(profCirurgiaNova != null){//IF (v_puc_ser_matricula IS NOT NULL OR v_puc_ser_matricula <> 0) THEN
					try {
						veioGeraEscalaD = Boolean.TRUE;
						boolean veioCirurgia = Boolean.TRUE;
						// Excluo o profissional antigo
						getMbcProfCirurgiasRN().removerMbcProfCirurgias(profCirurgia, veioGeraEscalaD);
	
						// Incluo profissional novo
						MbcProfCirurgias profCirur = new MbcProfCirurgias();
						profCirur.setMbcProfAtuaUnidCirgs(profCirurgiaNova.getMbcProfAtuaUnidCirgs());
						profCirur.setServidor(profCirurgia.getServidor());
						profCirur.setIndInclEscala(profCirurgia.getIndInclEscala());
						profCirur.setFuncaoProfissional(profCirurgia.getFuncaoProfissional());
						profCirur.setIndRealizou(profCirurgia.getIndRealizou());
						profCirur.setIndResponsavel(profCirurgia.getIndResponsavel());
						profCirur.setCriadoEm(new Date());
						profCirur.setCirurgia(cirurgia);
						// Insert
						getMbcProfCirurgiasRN().inserirMbcProfCirurgias(profCirur, veioGeraEscalaD, veioCirurgia);
	
						veioGeraEscalaD = Boolean.FALSE;
						veioCirurgia = Boolean.FALSE;
	
					} catch (BaseException e) {
						logError(e);
						throw new ApplicationBusinessException(e);
					}
				}
			}
		}
	}

	/**
	 * Deixa a data somente com a hora e minuto que é o importante
	 * 
	 * @param data
	 * @return
	 */
	private Date retirarAnoMesDiaMiliData(Date data) {
		Calendar c = Calendar.getInstance();
		c.clear();
		c.setTime(data);
		c.set(Calendar.YEAR, 1970);
		c.set(Calendar.MONTH, 1);
		c.set(Calendar.DAY_OF_MONTH, 1);
		c.set(Calendar.MILLISECOND, 0);
		return c.getTime();
	}

	protected IParametroFacade getParametroFacade() {
		return iParametroFacade;
	}

	protected MbcDestinoPacienteDAO getMbcDestinoPacienteDAO() {
		return mbcDestinoPacienteDAO;
	}

	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected IProcedimentoTerapeuticoFacade getProcedimentoTerapeuticoFacade() {
		return this.iProcedimentoTerapeuticoFacade;
	}

	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.iSolicitacaoExameFacade;
	}

	protected MbcAgendasRN getMbcAgendasRN() {
		return mbcAgendasRN;
	}

	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
		return mbcAgendaHemoterapiaDAO;
	}

	protected MbcAgendaHemoterapiaRN getMbcAgendaHemoterapiaRN() {
		return mbcAgendaHemoterapiaRN;
	}

	protected MbcCirurgiasVerificacoesRN getMbcCirurgiasVerificacoesRN() {
		return mbcCirurgiasVerificacoesRN;
	}

	protected MbcAgendaProcedimentoRN getMbcAgendaProcedimentoRN() {
		return mbcAgendaProcedimentoRN;
	}

	protected MbcAgendaAnestesiaRN getMbcAgendaAnestesiaRN() {
		return mbcAgendaAnestesiaRN;
	}

	protected MbcAgendaSolicEspecialRN getMbcAgendaSolicEspecialRN() {
		return mbcAgendaSolicEspecialRN;
	}

	protected MbcAgendaOrtProteseRN getMbcAgendaOrtProteseRN() {
		return mbcAgendaOrtProteseRN;
	}

	protected MbcCaractSalaEspDAO getMbcCaractSalaEspDAO() {
		return mbcCaractSalaEspDAO;
	}

	protected MbcSubstEscalaSalaDAO getMbcSubstEscalaSalaDAO() {
		return mbcSubstEscalaSalaDAO;
	}

	protected MbcProfCirurgiasDAO getMbcProfCirurgiasDAO() {
		return mbcProfCirurgiasDAO;
	}

	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}

	protected MbcProfCirurgiasRN getMbcProfCirurgiasRN() {
		return mbcProfCirurgiasRN;
	}
	
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}


}