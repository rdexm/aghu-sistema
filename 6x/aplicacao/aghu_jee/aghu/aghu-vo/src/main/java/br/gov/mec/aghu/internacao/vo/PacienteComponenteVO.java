package br.gov.mec.aghu.internacao.vo;

public class PacienteComponenteVO {
	
	private Integer codigo;
	
	private Integer prontuario;
	
	private String  nome;

	
	public PacienteComponenteVO() {}
	

	public PacienteComponenteVO(final Integer codigo, final Integer prontuario, final String nome) {
		this.codigo = codigo;
		this.prontuario = prontuario;
		this.nome = nome;
	}
	
	public enum Fields {
		NOME("nome"), 
		CODIGO("codigo"), 
		PRONTUARIO("prontuario");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	
	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}
	
	
}
