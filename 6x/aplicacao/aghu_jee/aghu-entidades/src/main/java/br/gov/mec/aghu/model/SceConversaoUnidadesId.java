package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class SceConversaoUnidadesId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3994841052694383960L;
	private String umdCodigo;
	private String umdCodigoDestino;
	private Double fatorConversao;

	public SceConversaoUnidadesId() {
	}

	public SceConversaoUnidadesId(String umdCodigo, String umdCodigoDestino, Double fatorConversao) {
		this.umdCodigo = umdCodigo;
		this.umdCodigoDestino =  umdCodigoDestino;
		this.fatorConversao = fatorConversao;
	}

	@Column(name = "UMD_CODIGO", nullable = false)
	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	@Column(name = "UMD_CODIGO_DESTINO", nullable = false)
	public String getUmdCodigoDestino() {
		return umdCodigoDestino;
	}

	public void setUmdCodigoDestino(String umdCodigoDestino) {
		this.umdCodigoDestino = umdCodigoDestino;
	}

	@Column(name = "FATOR_CONVERSAO", nullable = false)
	public Double getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Double fatorConversao) {
		this.fatorConversao = fatorConversao;
	}	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((umdCodigo == null) ? 0 : umdCodigo.hashCode());
		result = prime * result
				+ ((umdCodigoDestino == null) ? 0 : umdCodigoDestino.hashCode());
		result = prime * result + ((fatorConversao == null) ? 0 : fatorConversao.hashCode());
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		SceConversaoUnidadesId other = (SceConversaoUnidadesId) obj;
		if (umdCodigo == null) {
			if (other.umdCodigo != null) {
				return false;
			}
		} else if (!umdCodigo.equals(other.umdCodigo)) {
			return false;
		}
		if (umdCodigoDestino == null) {
			if (other.umdCodigoDestino != null) {
				return false;
			}
		} else if (!umdCodigoDestino.equals(other.umdCodigoDestino)) {
			return false;
		}
		if (fatorConversao == null) {
			if (other.fatorConversao != null) {
				return false;
			}
		} else if (!fatorConversao.equals(other.fatorConversao)) {
			return false;
		}
		return true;
	}
}
