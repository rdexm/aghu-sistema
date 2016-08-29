package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class CursorConDadosVO implements Serializable {

	
	private static final long serialVersionUID = 320897775349404117L;
	
	private String sigla;
	private String nomeReduzido;
	private String nome;
	private Short preSerVinCodigo;
	private Integer preSerMatricula;
	
	public enum Fields {
		SIGLA("sigla"),
		NOME_REDUZIDO("nomeReduzido"),
		NOME("nome"),
		PRE_SER_VIN_CODIGO("preSerVinCodigo"),
		PRE_SER_MATRICULA("preSerMatricula")
		;

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}


	public String getSigla() {
		return sigla;
	}


	public void setSigla(String sigla) {
		this.sigla = sigla;
	}


	public String getNomeReduzido() {
		return nomeReduzido;
	}


	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}


	public String getNome() {
		return nome;
	}


	public void setNome(String nome) {
		this.nome = nome;
	}


	public Short getPreSerVinCodigo() {
		return preSerVinCodigo;
	}


	public void setPreSerVinCodigo(Short preSerVinCodigo) {
		this.preSerVinCodigo = preSerVinCodigo;
	}


	public Integer getPreSerMatricula() {
		return preSerMatricula;
	}


	public void setPreSerMatricula(Integer preSerMatricula) {
		this.preSerMatricula = preSerMatricula;
	}
}