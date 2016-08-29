package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class ConsumoMedioMensalVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -8157714928296917229L;

	private String mesAno;
	private Integer consumo;
	private Boolean media = false;
	
	public String getMesAno() {
		return mesAno;
	}
	
	public void setMesAno(String mesAno) {
		this.mesAno = mesAno;
	}
	
	public Integer getConsumo() {
		return consumo;
	}
	
	public void setConsumo(Integer consumo) {
		this.consumo = consumo;
	}

	public Boolean getMedia() {
		return media;
	}

	public void setMedia(Boolean media) {
		this.media = media;
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(mesAno).toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		ConsumoMedioMensalVO other = (ConsumoMedioMensalVO) obj;
		if (other != null) {
			return new EqualsBuilder().append(this.mesAno, other.mesAno).isEquals();
		}
	
		return false;
	}
	
}
