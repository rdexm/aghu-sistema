package br.gov.mec.aghu.model;

/*
 * 
 * Created by: sgralha
 */

import java.text.SimpleDateFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;


@Embeddable
public class ScoAutTempSolicitaId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3387845374279065578L;

	
	private RapServidores servidor;
	private FccCentroCustos fccCentroCustos;
	private Date dtInicio;
	
	public ScoAutTempSolicitaId() {
	}

	public ScoAutTempSolicitaId(RapServidores servidor, FccCentroCustos fccCentroCustos, Date dtInicio) {
		this.servidor = servidor;
		this.fccCentroCustos = fccCentroCustos;
		this.dtInicio = dtInicio;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return this.fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@Column(name = "DT_INICIO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtInicio() {
		return this.dtInicio;
	}

	public void setDtInicio(Date dtInicio) {
		this.dtInicio = dtInicio;
	}
	
	@Transient
	public String getData() {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		return sdf.format(dtInicio);
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getServidor());
		umHashCodeBuilder.append(this.getFccCentroCustos());
		umHashCodeBuilder.append(this.getDtInicio());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ScoAutTempSolicitaId)) {
			return false;
		}
		ScoAutTempSolicitaId other = (ScoAutTempSolicitaId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getServidor(), other.getServidor());
		umEqualsBuilder.append(this.getFccCentroCustos(), other.getFccCentroCustos());
		umEqualsBuilder.append(this.getDtInicio(), other.getDtInicio());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}
