package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.RapServidores;

public class ResponsaveisStatusTicketsVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6380798975171814074L;

	private RapServidores matricula;	
	private String nome;

	public enum Fields {
		
		MATRICULA("matricula"),
		NOME("nome");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public RapServidores getMatricula() {
		return matricula;
	}

	public void setMatricula(RapServidores matricula) {
		this.matricula = matricula;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
}