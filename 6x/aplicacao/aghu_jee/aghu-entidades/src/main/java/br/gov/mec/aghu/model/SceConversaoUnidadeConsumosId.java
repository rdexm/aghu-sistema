package br.gov.mec.aghu.model;



import javax.persistence.Column;
import javax.persistence.Embeddable;


import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class SceConversaoUnidadeConsumosId implements EntityCompositeId {

	private static final long serialVersionUID = 6240669548601187952L;
	
	private Integer matCodigo;
	private String umdCodigo;

	public SceConversaoUnidadeConsumosId() {
	}

	public SceConversaoUnidadeConsumosId(Integer matCodigo, String umdCodigo) {
		this.matCodigo = matCodigo;
		this.umdCodigo = umdCodigo;
	}

	@Column(name = "MAT_CODIGO", nullable = false, precision = 6, scale = 0)
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	@Column(name = "UMD_CODIGO", length = 3, nullable = false)
	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matCodigo == null) ? 0 : matCodigo.hashCode());
		result = prime * result
				+ ((umdCodigo == null) ? 0 : umdCodigo.hashCode());
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
		
		SceConversaoUnidadeConsumosId other = (SceConversaoUnidadeConsumosId) obj;
		
		if (matCodigo == null) {
			if (other.matCodigo != null){
				return false;
			}
		} else if (!matCodigo.equals(other.matCodigo)){
			return false;
		}
		if (umdCodigo == null) {
			if (other.umdCodigo != null){
				return false;
			}
		} else if (!umdCodigo.equals(other.umdCodigo)){
			return false;
		}
		return true;
	}

	

}
