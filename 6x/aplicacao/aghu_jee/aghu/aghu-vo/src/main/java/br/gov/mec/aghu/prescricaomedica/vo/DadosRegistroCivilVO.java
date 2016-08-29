package br.gov.mec.aghu.prescricaomedica.vo;

import java.math.BigInteger;

public class DadosRegistroCivilVO {
	
	private Long numeroDn;
	private BigInteger regNascimento;
	
	public enum Fields {
		REG_NASCIMENTO("regNascimento"),
		NUMERO_DN("numeroDn");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Long getNumeroDn() {
		return numeroDn;
	}
	public void setNumeroDn(Long numeroDn) {
		this.numeroDn = numeroDn;
	}
	public BigInteger getRegNascimento() {
		return regNascimento;
	}
	public void setRegNascimento(BigInteger regNascimento) {
		this.regNascimento = regNascimento;
	}	
	
}
