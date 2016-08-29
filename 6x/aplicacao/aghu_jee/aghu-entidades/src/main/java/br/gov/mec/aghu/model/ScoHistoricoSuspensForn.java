package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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


import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCO_HISTORICOS_SUSPENS_FORN", schema = "AGH")
public class ScoHistoricoSuspensForn extends BaseEntityId<ScoHistoricoSuspensFornId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -284710521830963969L;
	private ScoHistoricoSuspensFornId id;
	private Integer version;
	private ScoFornecedor scoFornecedor;
	private RapServidores rapServidores;
	private Date dtInicio;
	private Date dtFim;
	private String justificativa;

	public ScoHistoricoSuspensForn() {
	}

	public ScoHistoricoSuspensForn(ScoHistoricoSuspensFornId id, ScoFornecedor scoFornecedor, RapServidores rapServidores,
			Date dtInicio, String justificativa) {
		this.id = id;
		this.scoFornecedor = scoFornecedor;
		this.rapServidores = rapServidores;
		this.dtInicio = dtInicio;
		this.justificativa = justificativa;
	}

	public ScoHistoricoSuspensForn(ScoHistoricoSuspensFornId id, ScoFornecedor scoFornecedor, RapServidores rapServidores,
			Date dtInicio, Date dtFim, String justificativa) {
		this.id = id;
		this.scoFornecedor = scoFornecedor;
		this.rapServidores = rapServidores;
		this.dtInicio = dtInicio;
		this.dtFim = dtFim;
		this.justificativa = justificativa;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "frnNumero", column = @Column(name = "FRN_NUMERO", nullable = false)),
			@AttributeOverride(name = "nroProcesso", column = @Column(name = "NRO_PROCESSO", nullable = false)) })
	public ScoHistoricoSuspensFornId getId() {
		return this.id;
	}

	public void setId(ScoHistoricoSuspensFornId id) {
		this.id = id;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", nullable = false, insertable = false, updatable = false)
	public ScoFornecedor getScoFornecedor() {
		return this.scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_INICIO", nullable = false, length = 29)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_FIM", length = 29)
	public Date getDtFim() {
		return this.dtFim;
	}

	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}

	@Column(name = "JUSTIFICATIVA", nullable = false, length = 500)
	@Length(max = 500)
	public String getJustificativa() {
		return this.justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SCO_FORNECEDOR("scoFornecedor"),
		FRN_NUMERO("scoFornecedor.numero"),
		NUMERO("id.nroProcesso"),
		RAP_SERVIDORES("rapServidores"),
		DT_INICIO("dtInicio"),
		DT_FIM("dtFim"),
		JUSTIFICATIVA("justificativa");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (!(obj instanceof ScoHistoricoSuspensForn)) {
			return false;
		}
		ScoHistoricoSuspensForn other = (ScoHistoricoSuspensForn) obj;
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
