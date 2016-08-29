package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class ProgramaEspecialidadeVO implements Serializable{
	
	private static final long serialVersionUID = -182392180151582L;

	private String nomePrograma;
	private String nomeEspecialidade;
	
	public enum Fields {
		NOME_PROGRAMA("nomePrograma"), 
		NOME_ESPECIALIDADE("nomeEspecialidade")
		;

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

	public String getNomePrograma() {
		return nomePrograma;
	}

	public void setNomePrograma(String nomePrograma) {
		this.nomePrograma = nomePrograma;
	}
}
