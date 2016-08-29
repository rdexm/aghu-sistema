package br.gov.mec.aghu.patrimonio.vo;

import br.gov.mec.aghu.model.RapServidores;


public class NomeUsuarioVO {
	
	private RapServidores matricula;
	private String nome;

	private String responsavelTecnico;
	
	public enum Fields {
		MATRICULA("matricula"),
		NOME("nome");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
	public String getResponsavelTecnico() {
		return responsavelTecnico;
	}
	public void setResponsavelTecnico(String responsavelTecnico) {
		this.responsavelTecnico = responsavelTecnico;
	}
	
}
