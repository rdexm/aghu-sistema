package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.math.BigDecimal;


public class MaterialOpmeVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -816888232356132160L;
	private Integer matCodigo;
	private String matNome;
	private String matUmd;
	private Long iphCodSus;
	private BigDecimal iphValorUnit;
	private Double iafValorUnit;
	private Integer matMarcaCod;
	private String matMarca;
	
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getMatNome() {
		return matNome;
	}
	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}
	public String getMatUmd() {
		return matUmd;
	}
	public void setMatUmd(String matUmd) {
		this.matUmd = matUmd;
	}
	public Long getIphCodSus() {
		return iphCodSus;
	}
	public void setIphCodSus(Long iphCodSus) {
		this.iphCodSus = iphCodSus;
	}
	public BigDecimal getIphValorUnit() {
		return iphValorUnit;
	}
	public void setIphValorUnit(BigDecimal iphValorUnit) {
		this.iphValorUnit = iphValorUnit;
	}
	public Integer getMatMarcaCod() {
		return matMarcaCod;
	}
	public void setMatMarcaCod(Integer matMarcaCod) {
		this.matMarcaCod = matMarcaCod;
	}
	public String getMatMarca() {
		return matMarca;
	}
	public void setMatMarca(String matMarca) {
		this.matMarca = matMarca;
	}
	public Double getIafValorUnit() {
		return iafValorUnit;
	}
	public void setIafValorUnit(Double iafValorUnit) {
		this.iafValorUnit = iafValorUnit;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matCodigo == null) ? 0 : matCodigo.hashCode());
		result = prime * result
				+ ((matMarcaCod == null) ? 0 : matMarcaCod.hashCode());
		result = prime * result + ((matNome == null) ? 0 : matNome.hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MaterialOpmeVO other = (MaterialOpmeVO) obj;
		if (matCodigo == null) {
			if (other.matCodigo != null){
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)){
			return false;
		}
		if (matMarcaCod == null) {
			if (other.matMarcaCod != null){
				return false;
			}
		} else if (!matMarcaCod.equals(other.matMarcaCod)){
			return false;
		}
		if (matNome == null) {
			if (other.matNome != null){
				return false;
			}
		} else if (!matNome.equals(other.matNome)){
			return false;
		}
		return true;
	}

	
}
