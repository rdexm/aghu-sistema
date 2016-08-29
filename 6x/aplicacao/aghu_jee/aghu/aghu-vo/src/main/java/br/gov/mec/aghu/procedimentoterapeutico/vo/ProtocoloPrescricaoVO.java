package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;

public class ProtocoloPrescricaoVO implements Serializable {

	private static final long serialVersionUID = 5281516217983448392L;

	private String descricao;
	private String titulo;
	
	public enum Fields {

		TITULO("titulo"), 
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}
}
