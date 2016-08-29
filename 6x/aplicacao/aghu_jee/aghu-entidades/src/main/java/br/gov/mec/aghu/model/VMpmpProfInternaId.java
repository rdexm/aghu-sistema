package br.gov.mec.aghu.model;

import javax.persistence.Embeddable;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

@Embeddable
public class VMpmpProfInternaId  implements EntityCompositeId{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5488067755347042817L;

	private Integer matricula;
	
	private Integer vinCodigo;
	
	private String indSituacao;
	
	private String responsavel;
	
	public enum Fields {
		MATRICULA("matricula"),
		VIN_CODIGO("vinCodigo"),
		IND_SITUACAO("indSituacao"),
		RESPONSAVEL("responsavel");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Integer getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Integer vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(String indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((matricula == null) ? 0 : matricula.hashCode());
		result = prime * result
				+ ((vinCodigo == null) ? 0 : vinCodigo.hashCode());
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
		if (!(obj instanceof VMpmpProfInternaId)) {
			return false;
		}
		VMpmpProfInternaId other = (VMpmpProfInternaId) obj;
		if (matricula == null) {
			if (other.matricula != null) {
				return false;
			}
		} else if (!matricula.equals(other.matricula)) {
			return false;
		}
		if (vinCodigo == null) {
			if (other.vinCodigo != null) {
				return false;
			}
		} else if (!vinCodigo.equals(other.vinCodigo)) {
			return false;
		}
		return true;
	}

}
