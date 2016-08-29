package br.gov.mec.aghu.patrimonio.vo;


public class TecnicoItemRecebimentoVO {
	
	private Integer matricula;
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
			return fields;
		}

	}
	
	public Integer getMatricula() {
		return matricula;
	}
	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	
}
