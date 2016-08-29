package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class CursorCurPreVO implements Serializable {

	private static final long serialVersionUID = -2420340100305110252L;
	private String nome;	
	
	public enum Fields {
		NOME("nome"),
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

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}