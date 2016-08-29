package br.gov.mec.aghu.paciente.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class PacienteGrupoFamiliarVO implements BaseBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3201766240352447812L;
	
	private Integer prontuario;
	private Integer codigo;
	private String nome;
	private String logradouro;
	private String complLogradouro;
	private String bairro;
	private String ufSigla;
	private Integer nroLogradouro;
	private Integer agfSeq;
	private String cep;
	private String cidade;
	
	public String getCep() {
		return cep;
	}
	public void setCep(String cep) {
		this.cep = cep;
	}
	public Integer getAgfSeq() {
		return agfSeq;
	}
	public void setAgfSeq(Integer agfSeq) {
		this.agfSeq = agfSeq;
	}
	public Integer getNroLogradouro() {
		return nroLogradouro;
	}
	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public Integer getCodigo() {
		return codigo;
	}
	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLogradouro() {
		return logradouro;
	}
	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}
	public String getBairro() {
		return bairro;
	}
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}
	public String getComplLogradouro() {
		return complLogradouro;
	}
	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}
	public String getUfSigla() {
		return ufSigla;
	}
	public void setUfSigla(String ufSigla) {
		this.ufSigla = ufSigla;
	}
	
	public String getCidade() {
		return cidade;
	}
	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public enum Fields {
		PRONTUARIO("prontuario"),
		CODIGO("codigo"),
		NOME("nome"),
		LOGRADOURO("logradouro"),
		COMPLLOGRADOURO("complLogradouro"),
		BAIRRO("bairro"),
		UF("ufSigla"),
		NRO_LOGRADOURO("nroLogradouro"),
		GRUPO_FAMILIAR_SEQ("agfSeq"),
		CIDADE("cidade");
		
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
