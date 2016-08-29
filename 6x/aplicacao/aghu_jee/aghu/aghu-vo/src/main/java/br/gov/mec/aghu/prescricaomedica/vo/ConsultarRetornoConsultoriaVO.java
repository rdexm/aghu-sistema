package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class ConsultarRetornoConsultoriaVO implements Serializable {


	private static final long serialVersionUID = -3938070666714847773L;
	
	private String nomeEspecialidade;
	private String sigla;
	private Integer pacCodigo;
	private Integer codProntuario;
	private String nomePaciente;
	private Short espSeq;
	private String descricao;
	
	public enum Fields {

		NOME_ESPECIALIDADE("nomeEspecialidade"),
		SIGLA("sigla"),
		PAC_CODIGO("pacCodigo"),
		COD_PRONTUARIO("codProntuario"),
		NOME_PACIENTE("nomePaciente"),
		ESP_SEQ("espSeq"),
		DESCRICAO("descricao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Integer getCodProntuario() {
		return codProntuario;
	}
	public void setCodProntuario(Integer codProntuario) {
		this.codProntuario = codProntuario;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public Short getEspSeq() {
		return espSeq;
	}
	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

}
