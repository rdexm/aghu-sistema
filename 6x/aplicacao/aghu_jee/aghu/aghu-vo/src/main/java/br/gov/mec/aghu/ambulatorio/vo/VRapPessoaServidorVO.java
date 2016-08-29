package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;


public class VRapPessoaServidorVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -759079071003017104L;
	private Short vinculo;
	private Integer matricula;
	private String nome;
	private Integer pesCodigo;
	
	private String vinculoMatricula = StringUtils.EMPTY;
	
	public VRapPessoaServidorVO(Short vinculo, Integer matricula, String nome, Integer pesCodigo) {
		this.vinculo = vinculo;
		this.matricula = matricula;
		this.nome = nome;
		this.pesCodigo = pesCodigo;
		this.vinculoMatricula =StringUtils.EMPTY+ vinculo +"/"+ matricula;
	}
	
	public enum Fields {
		VINCULO("vinculo"),
		NOME("nome"),
		PES_CODIGO("pesCodigo"),
		MATRICULA("matricula"),
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

	public Short getVinculo() {
		return vinculo;
	}

	public void setVinculo(Short vinculo) {
		this.vinculo = vinculo;
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

	public Integer getPesCodigo() {
		return pesCodigo;
	}

	public void setPesCodigo(Integer pesCodigo) {
		this.pesCodigo = pesCodigo;
	}

	public String getVinculoMatricula() {
		return vinculoMatricula;
	}

	public void setVinculoMatricula(String vinculoMatricula) {
		this.vinculoMatricula = vinculoMatricula;
	}
	
}