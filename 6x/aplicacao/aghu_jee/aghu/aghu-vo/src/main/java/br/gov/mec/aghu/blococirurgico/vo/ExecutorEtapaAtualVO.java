package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

public class ExecutorEtapaAtualVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6143872849311449943L;
	
	private Integer matricula;
	private Short vinculo;
	private String nome;
	private Integer codigoPessoa;
	private String vinculoMatricula;
	private Boolean autorizadoExecutar;

	public enum Fields {

		MATRICULA("matricula"), 
		VINCULO("vinculo"), 
		NOME("nome"),
		CODIGO_PESSOA("codigoPessoa"),
		AUTORIZADO_EXECUTAR("autorizadoExecutar");


		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
	}

	public String getVinculoMatricula() {
		vinculoMatricula = this.getVinculo().toString() + " " + this.getMatricula().toString();
		return vinculoMatricula;
	}

	public void setVinculoMatricula(String vinculoMatricula) {
		this.vinculoMatricula = vinculoMatricula;
	}
	
	public Boolean getAutorizadoExecutar() {

		return autorizadoExecutar;

	}

	public void setAutorizadoExecutar(Boolean autorizadoExecutar) {

		this.autorizadoExecutar = autorizadoExecutar;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(autorizadoExecutar == null){
			result = prime * result + 0;
		}else{
			result = prime * result + autorizadoExecutar.hashCode();
		}
		if(codigoPessoa == null){
			result = prime * result + 0;
		}else{
			result = prime * result + codigoPessoa.hashCode();
		}
		if(codigoPessoa == null){
			result = prime * result + 0;
		}else{
			result = prime * result + codigoPessoa.hashCode();
		}
		if(matricula == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + matricula.hashCode();
		}
		if(nome == null){
			result = prime * result + 0;
		}else{
			result = prime * result + nome.hashCode();
		}
		if(vinculo == null){
			result = prime * result + 0;
		}else{
			result = prime * result + vinculo.hashCode();
		}
		if(vinculoMatricula == null){
			result = prime * result + 0;	
		}else{
			result = prime * result + vinculoMatricula.hashCode();
		}	
		
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ExecutorEtapaAtualVO)) {
			return false;
		}
		ExecutorEtapaAtualVO other = (ExecutorEtapaAtualVO) obj;
		
		if (codigoPessoa == null) {
			if (other.codigoPessoa != null) {
				return false;
			}
		} else if (!codigoPessoa.equals(other.codigoPessoa)) {
			return false;
		}
		if (nome == null) {
			if (other.nome != null) {
				return false;
			}
		} else if (!nome.equals(other.nome)) {
			return false;
		}
		
		return true;
	}
	
}
