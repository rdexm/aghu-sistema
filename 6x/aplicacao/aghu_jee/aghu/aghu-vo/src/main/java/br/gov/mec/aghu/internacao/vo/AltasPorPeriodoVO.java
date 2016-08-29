package br.gov.mec.aghu.internacao.vo;

import java.util.Date;

public class AltasPorPeriodoVO {
	
	private String 	prontuario;
	private String 	obito;
	private String 	nomePaciente;
	private String 	leito;
	private String 	siglaEspecialidade;
	private String 	crm;
	private String 	nomeMedico;
	private String 	dataInt;
	private String	dataAnt;
	private Date	dataSaidaPaciente;
	private String 	senha;
	private String 	codigoConvenio;
	private String 	descricaoConvenioData;
	
	public AltasPorPeriodoVO(	String prontuario, 
								String obito, 
								String nomePaciente, 
								String leito, 
								String siglaEspecialidade, 
								String crm,
								String nomeMedico, 
								String dataInternacao, 
								String dataAltaMedica, 
								Date dataSaidaPaciente,
								String senha,
								String codigoConvenio,
								String descricaoConvenioData) {
		super();
		this.prontuario = prontuario;
		this.setObito(obito);
		this.nomePaciente = nomePaciente;
		this.leito = leito;
		this.siglaEspecialidade = siglaEspecialidade;
		this.crm = crm;
		this.nomeMedico = nomeMedico;
		this.dataInt = dataInternacao;
		this.dataAnt = dataAltaMedica;
		this.dataSaidaPaciente = dataSaidaPaciente;
		this.senha = senha;
		this.codigoConvenio = codigoConvenio;
		this.descricaoConvenioData = descricaoConvenioData;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getObito() {
		return obito;
	}

	public void setObito(String obito) {
		if (obito.equals("C") || obito.equals("D")) {
			this.obito = "O";
		} else {
			this.obito = "";
		}
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public String getSiglaEspecialidade() {
		return siglaEspecialidade;
	}

	public void setSiglaEspecialidade(String siglaEspecialidade) {
		this.siglaEspecialidade = siglaEspecialidade;
	}

	public String getCrm() {
		return crm;
	}

	public void setCrm(String crm) {
		this.crm = crm;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getDataInt() {
		return dataInt;
	}

	public void setDataInt(String dataInternacao) {
		this.dataInt = dataInternacao;
	}

	public String getDataAnt() {
		return dataAnt;
	}

	public void setDataAnt(String dataAltaMedica) {
		this.dataAnt = dataAltaMedica;
	}

	public Date getDataSaidaPaciente() {
		return dataSaidaPaciente;
	}

	public void setDataSaidaPaciente(Date dataSaidaPaciente) {
		this.dataSaidaPaciente = dataSaidaPaciente;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

	public String getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(String codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getDescricaoConvenioData() {
		return descricaoConvenioData;
	}

	public void setDescricaoConvenioData(String descricaoConvenioData) {
		this.descricaoConvenioData = descricaoConvenioData;
	}
	
	

}
