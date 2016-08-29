package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class RapPessoalServidorVO implements Serializable{
	
	private static final long serialVersionUID = -681077016553101627L;
	
	private Integer serMatricula;
	private Short serVinCodigo;
	private String nome;
	
	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public enum Fields {
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		NOME("nome"),
	
		;
		
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
