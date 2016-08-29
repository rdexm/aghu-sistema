package br.gov.mec.aghu.model;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "AIP_PERIM_CEFALICO_PACIENTES", schema = "AGH")
public class AipPerimCefalicoPacientes extends BaseEntityId<AipPerimCefalicoPacientesId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1426889625315777396L;
	private AipPerimCefalicoPacientesId id;
	private BigDecimal perimetroCefalico;
	private Date criadoEm;
	private RapServidores rapServidor;
	private Integer version;

	public AipPerimCefalicoPacientes() {
	}

	public AipPerimCefalicoPacientes(AipPerimCefalicoPacientesId id,
			BigDecimal perimetroCefalico, Date criadoEm, RapServidores rapServidor) {
		this.id = id;
		this.perimetroCefalico = perimetroCefalico;
		this.criadoEm = criadoEm;
		this.rapServidor = rapServidor;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "pacCodigo", column = @Column(name = "PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "seqp", column = @Column(name = "SEQP", nullable = false, precision = 4, scale = 0)) })
	public AipPerimCefalicoPacientesId getId() {
		return this.id;
	}

	public void setId(AipPerimCefalicoPacientesId id) {
		this.id = id;
	}

	@Column(name = "PERIMETRO_CEFALICO", nullable = false, precision = 5)
	public BigDecimal getPerimetroCefalico() {
		return this.perimetroCefalico;
	}

	public void setPerimetroCefalico(BigDecimal perimetroCefalico) {
		this.perimetroCefalico = perimetroCefalico;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", nullable = false, length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@JoinColumns({
        @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
        @JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO")})
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
	public RapServidores getRapServidor() {
		return rapServidor;
	}

	public void setRapServidor(RapServidores rapServidor) {
		this.rapServidor = rapServidor;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		PAC_CODIGO("id.pacCodigo"), SEQP("id.seqp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof AipPerimCefalicoPacientes)) {
			return false;
		}
		AipPerimCefalicoPacientes other = (AipPerimCefalicoPacientes) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}
