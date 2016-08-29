package br.gov.mec.aghu.model;

import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


@Entity
@Table(name = "AGH_WF_TEMPLATE_FLUXOS", schema = "AGH")
@SequenceGenerator(name = "AghWFTemplateFluxoSequence", sequenceName = "AGH.AGH_WTF_SEQ", allocationSize = 1)
public class AghWFTemplateFluxo  extends BaseEntitySeq<Integer> implements java.io.Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1590036114791084883L;
	private Integer seq;
	private String codigoModulo;
	private Integer version;
	private List<AghWFTemplateEtapa> etapas;	

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "AghWFTemplateFluxoSequence")
	@Column(name = "SEQ", nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "COD_MODULO", length = 10)
	public String getCodigoModulo() {
		return codigoModulo;
	}

	public void setCodigoModulo(String codigoModulo) {
		this.codigoModulo = codigoModulo;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "templateFluxo")
	public List<AghWFTemplateEtapa> getEtapas() {
		return etapas;
	}

	public void setEtapas(List<AghWFTemplateEtapa> etapas) {
		this.etapas = etapas;
	}
	

	public enum Fields {
		SEQ("seq"), 
		VERSION("version"), 
		COD_MODULO("codigoModulo"), 
		ETAPAS("etapas");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((seq == null) ? 0 : seq.hashCode());
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
		AghWFTemplateFluxo other = (AghWFTemplateFluxo) obj;
		if (seq == null) {
			if (other.seq != null) {
				return false;
			}
		} else if (!seq.equals(other.seq)) {
			return false;
		}
		return true;
	}
}