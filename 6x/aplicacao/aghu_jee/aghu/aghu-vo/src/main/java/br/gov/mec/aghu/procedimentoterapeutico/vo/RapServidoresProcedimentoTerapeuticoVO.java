package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;

public class RapServidoresProcedimentoTerapeuticoVO implements Serializable {

	private static final long serialVersionUID = 4431252211046246326L;
	
	private Integer matricula;
	private Short vinculo;
	private String nome;

	public enum Fields {

		MATRICULA("matricula"), 
		VIN_CODIGO("vinculo"),
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
	
	public String getMatriculaVinculo(){
		return this.matricula.toString() + " " + this.vinculo.toString();
	}
}
