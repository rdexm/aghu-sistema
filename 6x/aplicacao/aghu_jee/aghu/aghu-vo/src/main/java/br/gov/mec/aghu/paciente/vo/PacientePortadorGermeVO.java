package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class PacientePortadorGermeVO implements Serializable {

	private static final long serialVersionUID = -5550576133547069203L;
	
	private String unidade;
	
	private String horaConsulta;
	
	private Integer consulta;
	
	private Short sala;
	
	private String agenda;
	
	private Integer prontuario;
	
	private String nomePaciente;
	
	private Integer codBacteria;

	public String getUnidade() {
		return unidade;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getHoraConsulta() {
		return horaConsulta;
	}

	public void setHoraConsulta(String horaConsulta) {
		this.horaConsulta = horaConsulta;
	}

	public Integer getConsulta() {
		return consulta;
	}

	public void setConsulta(Integer consulta) {
		this.consulta = consulta;
	}

	public Short getSala() {
		return sala;
	}

	public void setSala(Short sala) {
		this.sala = sala;
	}

	public String getAgenda() {
		return agenda;
	}

	public void setAgenda(String agenda) {
		this.agenda = agenda;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public Integer getCodBacteria() {
		return codBacteria;
	}

	public void setCodBacteria(Integer codBacteria) {
		this.codBacteria = codBacteria;
	}
	
	public enum Fields {
		UNIDADE("unidade"),
		HORA_CONSULTA("horaConsulta"),
		CONSULTA("consulta"),
		SALA("sala"),
		AGENDA("agenda"),
		PRONTUARIO("prontuario"),
		NOME_PACIENTE("nomePaciente"),
		COD_BACTERIA("codBacteria");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

}
