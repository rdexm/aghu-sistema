package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;


public class RnCthcVerDiariasVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5669784312233855879L;

	private Integer diasUti;
	
	private Date retorno;

	public Integer getDiasUti() {
		return diasUti;
	}

	public void setDiasUti(Integer diasUti) {
		this.diasUti = diasUti;
	}

	public Date getRetorno() {
		return retorno;
	}

	public void setRetorno(Date retorno) {
		this.retorno = retorno;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((diasUti == null) ? 0 : diasUti.hashCode());
		result = prime * result + ((retorno == null) ? 0 : retorno.hashCode());
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
		RnCthcVerDiariasVO other = (RnCthcVerDiariasVO) obj;
		if (diasUti == null) {
			if (other.diasUti != null){
				return false;
			}
		} else if (!diasUti.equals(other.diasUti)){
			return false;
		}
		if (retorno == null) {
			if (other.retorno != null){
				return false;
			}
		} else if (!retorno.equals(other.retorno)){
			return false;
		}
		return true;
	}
	
}
