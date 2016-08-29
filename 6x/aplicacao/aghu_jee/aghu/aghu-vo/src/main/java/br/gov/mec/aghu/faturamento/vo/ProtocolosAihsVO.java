package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;


public class ProtocolosAihsVO {

	private Integer prontuario;
	private String paciente;
	private String pacienteTruncado;
	private String leito;
	private Integer conta;
	private Date datainternacao;
	private Date dataalta;
	private Date dataenviosms;
	private String envio;
	private boolean envioBoolean;
	private boolean update;
	
	
	public Integer getProntuario() {
		return prontuario;
	}
	
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPaciente() {
		return paciente;
	}

	public void setPaciente(String paciente) {
		this.paciente = paciente;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}

	public Date getDatainternacao() {
		return datainternacao;
	}

	public void setDatainternacao(Date datainternacao) {
		this.datainternacao = datainternacao;
	}

	public Date getDataalta() {
		return dataalta;
	}

	public void setDataalta(Date dataalta) {
		this.dataalta = dataalta;
	}

	public Date getDataenviosms() {
		return dataenviosms;
	}

	public void setDataenviosms(Date dataenviosms) {
		this.dataenviosms = dataenviosms;
	}

	public String getEnvio() {
		return envio;
	}

	public void setEnvio(String envio) {
		this.envio = envio;
		setEnvioBoolean(false);
		if("S".equals(envio)){
			setEnvioBoolean(true);
		}
	}
	
	public boolean isEnvioBoolean() {
		return envioBoolean;
	}

	public void setEnvioBoolean(boolean envioBoolean) {
		this.envioBoolean = envioBoolean;
	}

	public boolean isUpdate() {
		return update;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public String getPacienteTruncado() {
		return pacienteTruncado;
	}

	public void setPacienteTruncado(String pacienteTruncado) {
		this.pacienteTruncado = pacienteTruncado;
	}

	public enum Fields {
		PRONTUARIO("prontuario"),
		PACIENTE("paciente"),
		LEITO("leito"),
		CONTA("conta"),
		DATA_INTERNACAO("datainternacao"),
		DATA_ALTA("dataalta"),
		DATA_ENVIO("dataenviosms"),
		ENVIO("envio");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	
	
}
