package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

public class HistoricoAlteracaoAlocacaoSalaVO {
	
	private String inicio;
	private String fim;
	private String especialidade;
	private String percReserva;
	private String equipe;
	private String horaInicio;
	private String horaFim;
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
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getPercReserva() {
		return percReserva;
	}
	public void setPercReserva(String percReserva) {
		this.percReserva = percReserva;
	}
	public String getEquipe() {
		return equipe;
	}
	public void setEquipe(String equipe) {
		this.equipe = equipe;
	}
	public String getHoraInicio() {
		return horaInicio;
	}
	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}
	public String getHoraFim() {
		return horaFim;
	}
	public void setHoraFim(String horaFim) {
		this.horaFim = horaFim;
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