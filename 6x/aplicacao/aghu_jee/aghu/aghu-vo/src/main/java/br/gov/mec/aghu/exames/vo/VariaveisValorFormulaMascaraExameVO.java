package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class VariaveisValorFormulaMascaraExameVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2022015141533304593L;
	private String variavel;
	private BigDecimal valorNumerico;
	private String valorSexo;
	private Integer quantidadeCaracteres;
	private Short quantidadeCasasDecimais;
	
	public VariaveisValorFormulaMascaraExameVO(String variavel, BigDecimal valorNumerico, Integer quantidadeCaracteres, Short quantidadeCasasDecimais) {
		super();
		this.variavel = variavel;
		this.valorNumerico = valorNumerico;
		this.quantidadeCaracteres = quantidadeCaracteres;
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}
	
	public VariaveisValorFormulaMascaraExameVO(String variavel, String valorSexo, Integer quantidadeCaracteres, Short quantidadeCasasDecimais) {
		super();
		this.variavel = variavel;
		this.valorSexo = valorSexo;
		this.quantidadeCaracteres = quantidadeCaracteres;
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}
	
	public String getVariavel() {
		return variavel;
	}
	
	public void setVariavel(String variavel) {
		this.variavel = variavel;
	}
	
	public BigDecimal getValorNumerico() {
		return valorNumerico;
	}

	public void setValorNumerico(BigDecimal valorNumerico) {
		this.valorNumerico = valorNumerico;
	}

	public String getValorSexo() {
		return valorSexo;
	}

	public void setValorSexo(String valorSexo) {
		this.valorSexo = valorSexo;
	}

	public Integer getQuantidadeCaracteres() {
		return quantidadeCaracteres;
	}

	public void setQuantidadeCaracteres(Integer quantidadeCaracteres) {
		this.quantidadeCaracteres = quantidadeCaracteres;
	}

	public Short getQuantidadeCasasDecimais() {
		return quantidadeCasasDecimais;
	}

	public void setQuantidadeCasasDecimais(Short quantidadeCasasDecimais) {
		this.quantidadeCasasDecimais = quantidadeCasasDecimais;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((variavel == null) ? 0 : variavel.hashCode());
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
		if (!(obj instanceof VariaveisValorFormulaMascaraExameVO)) {
			return false;
		}
		VariaveisValorFormulaMascaraExameVO other = (VariaveisValorFormulaMascaraExameVO) obj;
		if (variavel == null) {
			if (other.variavel != null) {
				return false;
			}
		} else if (!variavel.equals(other.variavel)) {
			return false;
		}
		return true;
	}
}