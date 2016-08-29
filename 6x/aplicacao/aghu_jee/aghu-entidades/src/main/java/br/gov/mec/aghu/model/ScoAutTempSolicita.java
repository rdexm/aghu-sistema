package br.gov.mec.aghu.model;

import java.io.Serializable;
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


/*
 * 
 * Created by: sgralha
 */

@Entity
@Table(name = "SCO_AUTORIZACOES_TEMP_SOLICITA", schema = "AGH")
public class ScoAutTempSolicita extends BaseEntityId<ScoAutTempSolicitaId> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4452426971613786417L;
	/**
	 * 
	 */
	
	private ScoAutTempSolicitaId id;
	private Date dtFim;
	private Integer version;

	private RapServidores servidor;
	private FccCentroCustos fccCentroCustos;

	// construtores
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", insertable=false, updatable=false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable=false, updatable=false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", insertable=false, updatable=false)
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}
	
	
	
	public ScoAutTempSolicita() {
	}
	
	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "servidor.id.matricula", column = @Column(name = "SER_MATRICULA", nullable = false)),
		@AttributeOverride(name = "servidor.id.vinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false)),
		@AttributeOverride(name = "fccCentroCustos.codigo", column = @Column(name = "CCT_CODIGO", nullable = false)),
		@AttributeOverride(name = "dtInicio", column = @Column(name = "DT_INICIO", nullable = false))
	})
	public ScoAutTempSolicitaId getId() {
		return this.id;
	}

	public void setId(ScoAutTempSolicitaId id) {
		this.id = id;
	}
	
	
	@Column(name = "DT_FIM")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtFim() {
		return dtFim;
	}


	public void setDtFim(Date dtFim) {
		this.dtFim = dtFim;
	}


	@Version
	@Column(name = "VERSION", length = 3, nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
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
		if (!(obj instanceof ScoListasSiafi)) {
			return false;
		}
		ScoListasSiafi other = (ScoListasSiafi) obj;
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
	
	public enum Fields {
		ID("id"),
		SERVIDOR("servidor"), 
		CENTRO_CUSTO("fccCentroCustos"),
		DT_INICIO("id.dtInicio"),
		DT_FIM("dtFim"), 
		VERSION("version"),
		VINCULO_SERVIDOR("id.servidor.id.vinCodigo"),
		MATRICULA_SERVIDOR("id.servidor.id.matricula");

		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
}	

	