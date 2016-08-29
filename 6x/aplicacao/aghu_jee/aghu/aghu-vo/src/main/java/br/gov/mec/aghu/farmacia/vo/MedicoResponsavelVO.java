package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;


public class MedicoResponsavelVO implements Serializable {


	private static final long serialVersionUID = 2044812655030152478L;
	
	private String nome;
	private String nrgRegConselho;
	private Integer matricula;
	private Short vinCodigo;
	private String sigla;
	
	public MedicoResponsavelVO() {
		
	}
	
	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}
	
	public void setNrgRegConselho(String nrgRegConselho) {
		this.nrgRegConselho = nrgRegConselho;
	}

	public String getNrgRegConselho() {
		return nrgRegConselho;
	}

	public enum Fields {
		NOME("nome"),
		NRG_REG_CONSELHO("nrgRegConselho"),
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
}