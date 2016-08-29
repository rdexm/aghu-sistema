package br.gov.mec.aghu.registrocolaborador.vo;

import java.io.Serializable;

public class OcupacaoCargoVO implements Serializable {
	
	private static final long serialVersionUID = -3882249791451861745L;

	private Integer codigoOcupacao;
	private String descricaoOcupacao;
	private String cargoCodigo;

	
	public Integer getCodigoOcupacao() {
		return codigoOcupacao;
	}
	
	public void setCodigoOcupacao(Integer codigoOcupacao) {
		this.codigoOcupacao = codigoOcupacao;
	}
	
	public String getDescricaoOcupacao() {
		return descricaoOcupacao;
	}
	
	public void setDescricaoOcupacao(String descricaoOcupacao) {
		this.descricaoOcupacao = descricaoOcupacao;
	}
	
	public String getCargoCodigo() {
		return cargoCodigo;
	}
	
	public void setCargoCodigo(String cargoCodigo) {
		this.cargoCodigo = cargoCodigo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((cargoCodigo == null) ? 0 : cargoCodigo.hashCode());
		result = prime * result
				+ ((codigoOcupacao == null) ? 0 : codigoOcupacao.hashCode());
		result = prime
				* result
				+ ((descricaoOcupacao == null) ? 0 : descricaoOcupacao
						.hashCode());
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
		OcupacaoCargoVO other = (OcupacaoCargoVO) obj;
		if (cargoCodigo == null) {
			if (other.cargoCodigo != null){
				return false;
			}
		} else if (!cargoCodigo.equals(other.cargoCodigo)){
			return false;
		}
		if (codigoOcupacao == null) {
			if (other.codigoOcupacao != null){
				return false;
			}
		} else if (!codigoOcupacao.equals(other.codigoOcupacao)){
			return false;
		}
		if (descricaoOcupacao == null) {
			if (other.descricaoOcupacao != null){
				return false;
			}
		} else if (!descricaoOcupacao.equals(other.descricaoOcupacao)){
			return false;
		}
		return true;
	}

}