package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sco_caminhos_solicitacoes database table.
 * 
 */
@Embeddable
public class ScoCaminhoSolicitacaoID implements EntityCompositeId {

	private static final long serialVersionUID = -4750848353967457676L;

	private Short ppsCodigoInicio;
	private Short ppsCodigo;

	public ScoCaminhoSolicitacaoID() {
	}

	@Column(name = "PPS_CODIGO_INICIO", nullable = false)
	public Short getPpsCodigoInicio() {
		return this.ppsCodigoInicio;
	}

	public void setPpsCodigoInicio(Short ppsCodigoInicio) {
		this.ppsCodigoInicio = ppsCodigoInicio;
	}

	@Column(name = "PPS_CODIGO", nullable = false)
	public Short getPpsCodigo() {
		return this.ppsCodigo;
	}

	public void setPpsCodigo(Short ppsCodigo) {
		this.ppsCodigo = ppsCodigo;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((ppsCodigo == null) ? 0 : ppsCodigo.hashCode());
		result = prime * result
				+ ((ppsCodigoInicio == null) ? 0 : ppsCodigoInicio.hashCode());
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
		ScoCaminhoSolicitacaoID other = (ScoCaminhoSolicitacaoID) obj;
		if (ppsCodigo == null) {
			if (other.ppsCodigo != null) {
				return false;
			}
		} else if (!ppsCodigo.equals(other.ppsCodigo)) {
			return false;
		}
		if (ppsCodigoInicio == null) {
			if (other.ppsCodigoInicio != null) {
				return false;
			}
		} else if (!ppsCodigoInicio.equals(other.ppsCodigoInicio)) {
			return false;
		}
		return true;
	}	
	public enum Fields {

		PPS_CODIGO_INICIO("ppsCodigoInicio"),
		PPS_CODIGO("ppsCodigo");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}