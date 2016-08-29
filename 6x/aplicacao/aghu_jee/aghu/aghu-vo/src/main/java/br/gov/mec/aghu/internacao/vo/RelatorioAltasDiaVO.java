package br.gov.mec.aghu.internacao.vo;


/**
 * Classe responsável por agrupar informações a serem exibidos no relatório
 * de altas do dia.
 * 
 * 
 */
public class RelatorioAltasDiaVO {
	
	//Paciente
	private String prontuario;
	private String obito;
	private String nomePaciente;
	private String leito;
	private String siglaEspecialidade;
	private String crm;
	private String nomeMedico;
	private String dataInt;
	private String dataAnt;
	private String senha;
	private String codigoConvenio;
	private String descricaoConvenioData;

	
	// GETs e SETs
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
		this.obito = obito;
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
	public void setDataInt(String dataInt) {
		this.dataInt = dataInt;
	}
	public String getDataAnt() {
		return dataAnt;
	}
	public void setDataAnt(String dataAnt) {
		this.dataAnt = dataAnt;
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
