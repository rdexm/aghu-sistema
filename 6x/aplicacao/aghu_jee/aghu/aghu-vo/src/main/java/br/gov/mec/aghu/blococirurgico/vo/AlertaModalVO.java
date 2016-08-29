package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

/**
 * VO que controla o fluxo dos alertas (Vide: SHOW_ALERT do FORMS) como na estória #22460 – Agendar procedimentos eletivo, urgência ou emergência
 * 
 * @author aghu
 * 
 */
public class AlertaModalVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2202425471285486331L;
	
	/*
	 * Atributos gerais
	 */
	private String titulo;
	private String alerta;
	private boolean cancelarAlertaContinuaProcesso; // Determina se ao cancelar o alerta o processamento deverá continuar
	
	/*
	 * Quando solicitado o controle de convênio
	 */
	private Short convenioEncontradoCodigo;
	private Byte convenioEncontradoCodigoSeq;
	
	/*
	 * Quando solicitado sangue
	 */
	private boolean sangueSolicitado; 
	
	/*
	 * Quando necessária validação de O2 e AZOT
	 */
	private boolean tempoUtlzO2;
	private boolean tempoUtlzProAzot;
	
	private boolean exibirModalAlertaGravar = false; // Flag que controla a exibição da modal de alerta
	private boolean exibirModalValidacaoTempoMinimoPrevisto = false; // Flag que controla a exibição da modal de validação de tempo mínimo


	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getAlerta() {
		return alerta;
	}

	public void setAlerta(String alerta) {
		this.alerta = alerta;
	}

	public Short getConvenioEncontradoCodigo() {
		return convenioEncontradoCodigo;
	}

	public void setConvenioEncontradoCodigo(Short convenioEncontradoCodigo) {
		this.convenioEncontradoCodigo = convenioEncontradoCodigo;
	}

	public Byte getConvenioEncontradoCodigoSeq() {
		return convenioEncontradoCodigoSeq;
	}

	public void setConvenioEncontradoCodigoSeq(Byte convenioEncontradoCodigoSeq) {
		this.convenioEncontradoCodigoSeq = convenioEncontradoCodigoSeq;
	}

	public boolean isCancelarAlertaContinuaProcesso() {
		return cancelarAlertaContinuaProcesso;
	}
	
	public void setCancelarAlertaContinuaProcesso(boolean cancelarAlertaContinuaProcesso) {
		this.cancelarAlertaContinuaProcesso = cancelarAlertaContinuaProcesso;
	}

	public boolean isSangueSolicitado() {
		return sangueSolicitado;
	}

	public void setSangueSolicitado(boolean sangueSolicitado) {
		this.sangueSolicitado = sangueSolicitado;
	}

	public boolean isTempoUtlzO2() {
		return tempoUtlzO2;
	}

	public void setTempoUtlzO2(boolean tempoUtlzO2) {
		this.tempoUtlzO2 = tempoUtlzO2;
	}

	public boolean isTempoUtlzProAzot() {
		return tempoUtlzProAzot;
	}

	public void setTempoUtlzProAzot(boolean tempoUtlzProAzot) {
		this.tempoUtlzProAzot = tempoUtlzProAzot;
	}
	
	public boolean isExibirModalAlertaGravar() {
		return exibirModalAlertaGravar;
	}

	public void setExibirModalAlertaGravar(boolean exibirModalAlertaGravar) {
		this.exibirModalAlertaGravar = exibirModalAlertaGravar;
	}

	public boolean isExibirModalValidacaoTempoMinimoPrevisto() {
		return exibirModalValidacaoTempoMinimoPrevisto;
	}

	public void setExibirModalValidacaoTempoMinimoPrevisto(
			boolean exibirModalValidacaoTempoMinimoPrevisto) {
		this.exibirModalValidacaoTempoMinimoPrevisto = exibirModalValidacaoTempoMinimoPrevisto;
	}

}
