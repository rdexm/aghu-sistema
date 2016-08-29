package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class RelatorioEtiquetasIdentificacaoVO implements Serializable {

	private static final long serialVersionUID = 1244715559543905273L;

	private Integer prontuario;
	private Integer pacCodigo;
	private String nome;
	private Integer atendimento;
	private String ltoLtoId;
	private Integer numeroSubConsulta;
	private Short unfSeq;

	public RelatorioEtiquetasIdentificacaoVO() {
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(Integer atendimento) {
		this.atendimento = atendimento;
	}

	public String getLtoLtoId() {
		return ltoLtoId;
	}

	public void setLtoLtoId(String ltoLtoId) {
		this.ltoLtoId = ltoLtoId;
	}

	public Integer getNumeroSubConsulta() {
		return numeroSubConsulta;
	}

	public void setNumeroSubConsulta(Integer numeroSubConsulta) {
		this.numeroSubConsulta = numeroSubConsulta;
	}

	public enum Fields {
		PRONTUARIO("prontuario"), 
		PAC_CODIGO("pacCodigo"), 
		NOME("nome"), 
		ATENDIMENTO("atendimento"), 
		LTO_LTO_ID("ltoLtoId"), 
		NUMERO_SUB_CONSULTA("numeroSubConsulta");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

}