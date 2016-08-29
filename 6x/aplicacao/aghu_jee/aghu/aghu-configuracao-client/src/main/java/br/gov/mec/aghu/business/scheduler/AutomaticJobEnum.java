package br.gov.mec.aghu.business.scheduler;

import java.util.Date;

import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.model.RapServidores;

public enum AutomaticJobEnum implements IAutomaticJobEnum {
	SCHEDULERINIT_VERIFICACAOESCALAPROFISSIONAL("internacaoFacade.verificarEscalaProfissional")// Cron: " + SchedulerInit.ALL_DAY_22_45_CRON)
	, SCHEDULERINIT_CARGAINDICADORES_GERAR("indicadoresFacade.gerarIndicadoresHospitalares")// Cron: " + SchedulerInit.ONCE_A_MONTH_CRON)
	, SCHEDULERINIT_CARGAINDICADORES_GRAVAR("indicadoresFacade.gravarIndicadoresResumidos")// Cron: " + SchedulerInit.ONCE_A_MONTH_CRON)
	/**
	 * PARAM_SYS: P_AGHU_GERA_SUMARIO_ALTA_EXECUCAO
	 */
	, SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOMEDICA("prescricaoMedicaFacade.agendarGerarDadosSumarioPrescricaoMedica")
	, SCHEDULERINIT_GERARDADOSSUMARIOPRESCRICAOENFERMAGEM("prescricaoEnfermagemFacade.agendarGerarDadosSumarioPrescricaoEnfermagem")
	
	/**
	 * PARAM_SYS: P_AGHU_HORA_ENCERRAMENTO_AUTOMATICO_CTH
	 */
	, SCHEDULERINIT_ENCERRAMENTOAUTOMATICOCONTASHOSPITALARES("agendarEncerramentoAutomaticoContaHospitalar", false)
	, SCHED_INIT_REM_PERFISACESSOSISTEMACOMPENDENCIASBLOQUEANTES("cascaFacade.removerPerfisAcessoSistemaComPendenciasBloqueantes")// Cron: " + SchedulerInit.ALL_DAY_22_45_CRON, SchedulerInit.ALL_DAY_22_45_CRON)
	/**
	 * PARAM_SYS: P_COMPETENCIA, P_HORA_ATUAL_FECHAMENTO_ESTOQUE
	 */
	, SCHEDULERINIT_FECHAMENTOESTOQUEMENSAL("estoqueFacade.gerarFechamentoEstoqueMensal")// Cron: PARAM_SYS")
	, SCHEDULERINIT_REMOVERLOGSAPLICACAO("aghuFacade.removerLogsAplicacao")// Cron: " + SchedulerInit.EACH_ONE_MINUTE_CRON)
	
	, SCHEDULERINIT_EFETIVARREQUISICAOMATERIALAUTOMATICA("estoqueFacade.efetivarRequisicaoMaterialAutomatica")
	
	, SCHEDULERINIT_PROCESSARCUSTODIARIOAUTOMATIZADO("processarCustoDiarioAutomatizado", false)
		
	, SCHEDULERINIT_PROCESSARGESTAOAMOSTRA("agendarGestaoAmostra", false, true)
	
	, SCHEDULERINIT_PARAMETROSISTEMA("agendarParametroSistema", false)
	
	/**
	 * PARAM_SYS: P_HORA_AGENDA_SC
	 */
	, SCHEDULERINIT_GERACAO_AUTOMATICA_SOL_COMPRAS_MAT_ESTOCAVEL("solicitacaoComprasFacade.gerarSolicitacaoComprasMaterialEstocavel")
	, SCHEDULERINIT_VALIDAPRONTUARIOSCHEDULER("pacienteFacade.validaProntuarioScheduler")
	, SCHEDULERINIT_PROCESSAREXAMENAOREALIZADOSCHEDULER("enviarExamesNaoRealizadosFila", false)
	, SCHEDULERINIT_PROCESSAR_EXAME_INTERNET_SCHEDULER("processarExameInternet",true)
	, SCHEDULERINIT_PROCESSAR_PEDIDOS_PAPELARIA("papelariaFacade.buscarPedidosPapelaria")
	, SCHEDULERINIT_IMPORTACAOARQRETORNOPREGAOBBSCHEDULER("comprasFacade.importarArqPregaoBBRetorno")
	, SCHEDULERINIT_PROCESSAR_OPME("processarAcionamentoProcessoAutorizacaoOPME", true)
	, SCHEDULERINIT_NOTIF_GMR_AMBUL("pacienteFacade.enviarEmailPacienteGMR")
	, SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA("patrimonioFacade.enviarPendenciaEmailTicket")
	,SCHEDULERINIT_FREQUENCIA_ENVIO_PENDENCIA_SUMARIZADA("patrimonioFacade.enviarEmailPendeciasAceiteTec")
	;

	private String nome;
	private String cron;
	private RapServidores servidor;
	private boolean permiteVariosAgendamentos;
	private boolean ignorarLimiteMinIntervaloAgendamento;

	private AutomaticJobEnum(String nomeProcesso) {
		this.setNome(nomeProcesso);
		this.setPermiteVariosAgendamentos(false);
	}
	
	private AutomaticJobEnum(String nomeProcesso, boolean variosAgendamentos) {
		this.setNome(nomeProcesso);
		this.setPermiteVariosAgendamentos(variosAgendamentos);
	}
	
	private AutomaticJobEnum(String nomeProcesso, boolean variosAgendamentos, boolean ignorarLimiteMinIntervaloAgendamento) {
		this.setNome(nomeProcesso);
		this.setPermiteVariosAgendamentos(variosAgendamentos);
		this.setIgnorarLimiteMinIntervaloAgendamento(ignorarLimiteMinIntervaloAgendamento);
	}
	
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#getCodigo()
	 */
	@Override
	public int getCodigo() {
		return this.ordinal();
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#getDescricao()
	 */
	@Override
	public String getDescricao() {
		return this.getNome();
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#getTriggerName()
	 */
	@Override
	public String getTriggerName() {
		String triggerName = this.getNome();
		if (this.isPermiteVariosAgendamentos()) {
			String dataFormatada = DateFormatUtil.formataTimeStamp(new Date());
			triggerName = triggerName.concat("-").concat(dataFormatada);
		}
		return triggerName;
	}

	private String getNome() {
		return nome;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#setNome(java.lang.String)
	 */
	private void setNome(String n) {
		this.nome = n;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#getCron()
	 */
	@Override
	public String getCron() {
		return cron;
	}

	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#setCron(java.lang.String)
	 */
	@Override
	public void setCron(String c) {
		this.cron = c;
	}



	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#setServidor(br.gov.mec.aghu.model.RapServidores)
	 */
	@Override
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}



	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#getServidor()
	 */
	@Override
	public RapServidores getServidor() {
		return servidor;
	}



	private void setPermiteVariosAgendamentos(boolean permiteVariosAgendamentos) {
		this.permiteVariosAgendamentos = permiteVariosAgendamentos;
	}



	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#isPermiteVariosAgendamentos()
	 */
	@Override
	public boolean isPermiteVariosAgendamentos() {
		return permiteVariosAgendamentos;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#setIgnorarLimiteMinIntervaloAgendamento(boolean)
	 */
	@Override
	public void setIgnorarLimiteMinIntervaloAgendamento(boolean ignorarLimiteMinIntervaloAgendamento) {
		this.ignorarLimiteMinIntervaloAgendamento = ignorarLimiteMinIntervaloAgendamento;
	}
	
	/* (non-Javadoc)
	 * @see br.gov.mec.aghu.business.scheduler.IAutomaticJobEnum#isIgnorarLimiteMinIntervaloAgendamento()
	 */
	@Override
	public boolean isIgnorarLimiteMinIntervaloAgendamento() {
		return ignorarLimiteMinIntervaloAgendamento;
	}

}
