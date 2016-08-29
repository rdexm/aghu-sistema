package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

public class HistoricoAlteracaoCaractSalaVO {
	
	private String inicio;
	private String fim;
	private String diaSemana;
	private String turno;
	private String particular;
	private String urgencia;
	private String instalada;
	private String operacional;
	private String criadoEm;
	private String alteradoEm;
	
	public String getInicio() {
		return inicio;
	}
	public void setInicio(String inicio) {
		this.inicio = inicio;
	}
	public String getFim() {
		return fim;
	}
	public void setFim(String fim) {
		this.fim = fim;
	}
	public String getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(String diaSemana) {
		this.diaSemana = diaSemana;
	}
	public String getTurno() {
		return turno;
	}
	public void setTurno(String turno) {
		this.turno = turno;
	}
	public String getParticular() {
		return particular;
	}
	public void setParticular(String particular) {
		this.particular = particular;
	}
	public String getUrgencia() {
		return urgencia;
	}
	public void setUrgencia(String urgencia) {
		this.urgencia = urgencia;
	}
	public String getInstalada() {
		return instalada;
	}
	public void setInstalada(String instalada) {
		this.instalada = instalada;
	}
	public String getOperacional() {
		return operacional;
	}
	public void setOperacional(String operacional) {
		this.operacional = operacional;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getAlteradoEm() {
		return alteradoEm;
	}
	public void setAlteradoEm(String alteradoEm) {
		this.alteradoEm = alteradoEm;
	}
}