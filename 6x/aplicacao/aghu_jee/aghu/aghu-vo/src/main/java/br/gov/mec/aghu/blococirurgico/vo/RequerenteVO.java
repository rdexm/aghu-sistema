package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;


public class RequerenteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4051046974830550667L;
	
	private Integer codigoPessoa;
	private Integer matricula;
	private Short vinculo;
	private String nome;
	private String vinculoMatricula;

	public Integer getCodigoPessoa() {
		return codigoPessoa;
	}

	public void setCodigoPessoa(Integer codigoPessoa) {
		this.codigoPessoa = codigoPessoa;
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

	public String getVinculoMatricula() {
		vinculoMatricula = this.vinculo.toString() + " " + this.matricula.toString();
		return vinculoMatricula;
	}

	public void setVinculoMatricula(String vinculoMatricula) {
		this.vinculoMatricula = vinculoMatricula;
	}
	
	
	public String getMatriculaVinculoNome() {
		return this.getVinculoMatricula() + " - " + this.nome;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
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
		if (!(obj instanceof RequerenteVO)) {
			return false;
		}
		RequerenteVO other = (RequerenteVO) obj;
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
