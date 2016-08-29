package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class SceFornecedorMaterialId implements EntityCompositeId {

	/**
	 * serialVersionUID auto-generation
	 */
	private static final long serialVersionUID = -6600641489750600403L;
	private Integer numero;
	private Integer codigo;

	public SceFornecedorMaterialId() {
	}

	public SceFornecedorMaterialId(Integer numero, Integer codigo) {
		this.codigo = codigo;
		this.numero = numero;
	}

	@Column(name = "MAT_CODIGO", nullable = false)
	public Integer getCodigo() {
		return this.codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	@Column(name = "FRN_NUMERO", nullable = false)
	public Integer getNumero() {
		return this.numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	@Override
	public boolean equals(Object other) {
		if ((this == other)) {
			return true;
		}
		if ((other == null)) {
			return false;
		}
		if (!(other instanceof SceFornecedorMaterialId)) {
			return false;
		}
		SceFornecedorMaterialId castOther = (SceFornecedorMaterialId) other;

		return (this.getCodigo() == castOther.getCodigo())
				&& (this.getNumero() == castOther.getNumero());
	}

	@Override
	public int hashCode() {
		int result = 17;

		result = 37 * result + this.getCodigo();
		result = 37 * result + this.getNumero();
		return result;
	}

}
