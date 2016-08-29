package br.gov.mec.aghu.business.scheduler;

import br.gov.mec.aghu.model.RapServidores;

public enum NotificacaoJobEnum implements IAutomaticJobEnum {
	SCHEDULERINIT_WHATSAPP_INTERNACOES_EXCEDENTES("enviarWhatsappDeInternacoesExcedentes", true);

	private String nome;
	private String cron;
	private RapServidores servidor;
	private boolean permiteVariosAgendamentos;
	private boolean ignorarLimiteMinIntervaloAgendamento;
	private String triggerName;

	private NotificacaoJobEnum(String nomeProcesso) {
		this.setNome(nomeProcesso);
		this.setPermiteVariosAgendamentos(false);
	}
	
	private NotificacaoJobEnum(String nomeProcesso, boolean variosAgendamentos) {
		this.setNome(nomeProcesso);
		this.setPermiteVariosAgendamentos(variosAgendamentos);
	}
	
	private NotificacaoJobEnum(String nomeProcesso, boolean variosAgendamentos, boolean ignorarLimiteMinIntervaloAgendamento) {
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
		return triggerName;
	}
	
	public void setTriggerName(String triggerName) {
		this.triggerName = triggerName;
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
