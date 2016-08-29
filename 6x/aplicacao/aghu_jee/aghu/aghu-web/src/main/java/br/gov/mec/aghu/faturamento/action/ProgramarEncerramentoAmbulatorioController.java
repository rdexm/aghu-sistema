package br.gov.mec.aghu.faturamento.action;

import java.net.UnknownHostException;
import java.util.Calendar;
import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioModuloCompetencia;
import br.gov.mec.aghu.dominio.DominioOpcaoEncerramentoAmbulatorio;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatCompetencia;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;


public class ProgramarEncerramentoAmbulatorioController extends ActionController {

	private static final Log LOG = LogFactory.getLog(ProgramarEncerramentoAmbulatorioController.class);

	private static final long serialVersionUID = -3874595583768095810L;
	private Date dtExecucao;
	private Date dtFimCompetencia;
	private Date dtFimProximaCompetencia;

	private Boolean previa;
	private Boolean exibirModal;

	private DominioOpcaoEncerramentoAmbulatorio opcao;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;	

	@EJB
	private IParametroFacade parametroFacade;

//	@EJB
//	private ISchedulerFacade schedulerFacade;

//	@EJB
//	private IRegistroColaboradorFacade registroColaboradorFacade;
	
//	@Inject
//	private ProgramarEncerramentoScheduler programarEncerramentoScheduler;	// TODO pendencia de migracao, corvalao ficou de migrar isso
	

	

	private enum ProgramarEncerramentoAmbulatorioControllerExceptionCode implements BusinessExceptionCode {
		MSG_CONFIRMACAO_AGENDAMENTO_PREVIA_AMBULATORIO, PROCEDIMENTO_AMBULATORIAL_AGENDADO_SUCESSO, ;
	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
		previa = true;
	}

	public void iniciar() {
	 

		try {
			previa = true;
			
			/* eschweigert 06/08/2013
			 * 
			 * Solucao temporaria para deixar checado a opcao TODOS para o HCPA,
			 * os options serao ativados quando da completa migracao. Apos isso, 
			 * este trecho de codigo nao sera mais necessario. No HCPA demais chamadas
			 * serao chamadas nativas.
			 */
			if(aghuFacade.isHCPA()){
				opcao = DominioOpcaoEncerramentoAmbulatorio.TODOS;
				
			} else {
				opcao = DominioOpcaoEncerramentoAmbulatorio.AMB;
			}

			dtExecucao = getDataHoraExecucao();
			dtFimCompetencia = DateUtil.obterDataFimCompetencia(new Date());
			dtFimProximaCompetencia = null; // DateUtil.obterDataFimCompetencia(DateUtil.adicionaMeses(new
											// Date(), 1));
			exibirModal = false;
		} catch (ApplicationBusinessException e) {
			LOG.debug("Excecao ignorada. " + e.getMessage(), e);
		}
	
	}
	
	public void extornarCompetencia(){
		try {
			faturamentoFacade.extornaCompetenciaAmbulatorio(obterLoginUsuarioLogado());
			//faturamentoFacade.commit();
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_ESTORNO_COMPETENCIA_SUCESSO");
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"MENSAGEM_ESTORNO_COMPETENCIA_ERRO", e.getMessage());
		}
	}

	public void ajustaDataExecucao() {
		try {
			dtExecucao = getDataHoraExecucao();
			if (previa) {
				dtFimProximaCompetencia = null;
			} else {
				dtFimProximaCompetencia = DateUtil.obterDataFimCompetencia(DateUtil.adicionaMeses(new Date(), 1));
			}
		} catch (ApplicationBusinessException e) {
			LOG.debug("Excecao ignorada. " + e.getMessage(), e);
		}
	}

	private Date getDataHoraExecucao() throws ApplicationBusinessException {
		Date data = null;
		Calendar calendar = Calendar.getInstance();
		String horaParametro;
		int hora;
		int minuto;
		if (previa) {
			calendar.setTime(DateUtil.adicionaDias(new Date(), 1));

			horaParametro = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HORA_EXECUCAO_ROTINA_PREVIA_ENCERRAMENTO_AMBULATORIO);

			hora = Integer.valueOf(horaParametro.substring(0, 2)); // 02:00
			minuto = Integer.valueOf(horaParametro.substring(3, 5)); // 02:00
		} else {
			calendar.setTime(new Date());
			horaParametro = parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HORA_EXECUCAO_ROTINA_ENCERRAMENTO_AMBULATORIO);

			hora = Integer.valueOf(horaParametro.substring(0, 2)); // 22:00
			minuto = Integer.valueOf(horaParametro.substring(3, 5)); // 22:00
		}

		data = DateUtil.obterData(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), hora, minuto);

		return data;
	}

	public void validaCampos() {
		exibirModal = true;
		if (dtExecucao == null || dtFimCompetencia == null) {
			exibirModal = false;
		}
		try {
			faturamentoFacade.validarCamposProgramarEncerramento(criarNomeJob(getDtExecucao()), opcao, dtExecucao, dtFimCompetencia,
					dtFimProximaCompetencia, previa);
		} catch (BaseException e) {
			exibirModal = false;
			apresentarExcecaoNegocio(e);
		}
	}

	public void agendar() {
		exibirModal = false;
		
		
		LOG.info("AghJobDetail inserido.");	// TODO pendencia de migracao, corvalao ficou de migrar isso
		
		/*
		final String triggerName = criarNomeJob(getDtExecucao());
		
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Excecao caputada:", e);
		}

		// Agendamento no Quartz
		try {
			final AghJobDetail jobDetail = schedulerFacade.persistirAghJobDetail(new AghJobDetail(triggerName, pessoa.getRapServidores(), null));
			LOG.info("AghJobDetail inserido.");
			
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(dtExecucao);
			final String cron = "0 " + calendar.get(Calendar.MINUTE) 
			+ " " + calendar.get(Calendar.HOUR_OF_DAY) + " " + calendar.get(Calendar.DAY_OF_MONTH) 
			+ " " + (calendar.get(Calendar.MONTH)+1) + " ?" 
			+ " " + calendar.get(Calendar.YEAR)
			;
			
			LOG.info("# IntervalCron: " + cron);
			Boolean isPrevia = getPrevia();
			Date dtFimCompetencia = getDtFimCompetencia();
			final Date dataFimVinculoServidor = new Date();
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), dataFimVinculoServidor);
			QuartzTriggerHandle trigger = programarEncerramentoScheduler.agendar(dtExecucao, cron, pessoa, triggerName, opcao, isPrevia, dtFimCompetencia, nomeMicrocomputador, servidorLogado);
			jobDetail.setTrigger(trigger.getTrigger());
			
			LOG.info("Quartz Trigger: " + jobDetail.getTrigger());
			schedulerFacade.atualizarAghJobDetail(jobDetail);
			this.apresentarMsgNegocio(Severity.INFO,
					ProgramarEncerramentoAmbulatorioControllerExceptionCode.PROCEDIMENTO_AMBULATORIAL_AGENDADO_SUCESSO.toString());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		} catch (SchedulerException e) {
			this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}*/
	}

	public void acionaRnFatpExecFatNew() {
		LOG.info("AghJobDetail inserido.");	// TODO pendencia de migracao, corvalao ficou de migrar isso
		/*String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Excecao caputada:", e);
		}
		try {
			final String triggerName = criarNomeJob(getDtExecucao());
			final AghJobDetail jobDetail = schedulerFacade.persistirAghJobDetail(new AghJobDetail(triggerName, pessoa.getRapServidores(), null));
			LOG.info("AghJobDetail inserido.");

			final Date dataFimVinculoServidor = new Date();
			faturamentoFacade.rnFatpExecFatNew(opcao, getPrevia(), dtFimCompetencia, 
											   jobDetail, nomeMicrocomputador, dataFimVinculoServidor);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}*/
	}
	
	public void acionaSeparaBpaBpi() {
		String nomeMicrocomputador = null;
		try {
			nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
		} catch (UnknownHostException e) {
			LOG.error("Excecao caputada:", e);
		}
		try {
			
			FatCompetencia competenciaAmb  = faturamentoFacade.obterCompetenciaModuloMesAnoDtHoraInicioSemHora(DominioModuloCompetencia.AMB, 06, 2012, DateUtil.obterData(2012, 4, 31, 22, 00));
			LOG.info(super.getEnderecoIPv4HostRemoto());
			LOG.info(super.getEnderecoRedeHostRemoto());

			final Date dataFimVinculoServidor = new Date();
			faturamentoFacade.fatpAgruBpaBpi(getPrevia(), competenciaAmb, nomeMicrocomputador, dataFimVinculoServidor);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error("Excecao caputada:", e);
		}
	}
	
	public void naoAgendar() {
		exibirModal = false;
	}

	private String criarNomeJob(final Date dtExecucao) {
		return IFaturamentoFacade.NOME_JOB_ENCERRAMENTO_AMBULATORIO + " - " + (getPrevia() ? "prévia" : "encerramento") + " - " +
		   																DateUtil.obterDataFormatadaHoraMinutoSegundo(getDtExecucao());
	}

	public String getMensagemConfirmacao() {
		if(getDtExecucao() == null){
			return StringUtils.EMPTY;
		}
		return getMensagem(ProgramarEncerramentoAmbulatorioControllerExceptionCode.MSG_CONFIRMACAO_AGENDAMENTO_PREVIA_AMBULATORIO.toString(),
							(getPrevia() ? "prévia" : "encerramento"),
							DateUtil.obterDataFormatadaHoraMinutoSegundo(getDtExecucao())
						   );
	}

	protected String getMensagem(final String key, final Object... args) {
		String val = super.getBundle().getString(key);
		if (args != null && args.length > 0) {
			for (int i = 0; i < args.length; i++) {
				val = val.replaceAll("\\{" + i + "\\}", args[i].toString());
			}
		}
		return val;
	}

	public void limpar() {
		iniciar();
	}

	/* getters and setters */

	public Date getDtExecucao() {
		return dtExecucao;
	}

	public void setDtExecucao(Date dtExecucao) {
		this.dtExecucao = dtExecucao;
	}

	public Date getDtFimCompetencia() {
		return dtFimCompetencia;
	}

	public void setDtFimCompetencia(Date dtFimCompetencia) {
		this.dtFimCompetencia = dtFimCompetencia;
	}

	public Date getDtFimProximaCompetencia() {
		return dtFimProximaCompetencia;
	}

	public void setDtFimProximaCompetencia(Date dtFimProximaCompetencia) {
		this.dtFimProximaCompetencia = dtFimProximaCompetencia;
	}

	public Boolean getPrevia() {
		return previa;
	}

	public void setPrevia(Boolean previa) {
		this.previa = previa;
	}

	public DominioOpcaoEncerramentoAmbulatorio getOpcao() {
		return opcao;
	}

	public void setOpcao(DominioOpcaoEncerramentoAmbulatorio opcao) {
		this.opcao = opcao;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}
}