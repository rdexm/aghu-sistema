package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPanSq1", sequenceName = "SIG_PAN_SQ1", allocationSize = 1)
@Table(name = "SIG_PROCESSAMENTO_ANALISES", schema = "AGH")
public class SigProcessamentoAnalises extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -7332872186307888328L;
	private Integer seq;
	private String parecer;
	private Date dtParecer;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	private FccCentroCustos fccCentroCustos;
	private SigProcessamentoCusto sigProcessamentoCustos;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPanSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "PARECER", nullable = true, length = 1000)
	public String getParecer() {
		return this.parecer;
	}

	public void setParecer(String parecer) {
		this.parecer = parecer;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PARECER", nullable = true)
	public Date getDtParecer() {
		return dtParecer;
	}

	public void setDtParecer(Date dtParecer) {
		this.dtParecer = dtParecer;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO")
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmu_seq", nullable = false, referencedColumnName = "seq")
	public SigProcessamentoCusto getSigProcessamentoCustos() {
		return this.sigProcessamentoCustos;
	}

	public void setSigProcessamentoCustos(SigProcessamentoCusto sigProcessamentoCustos) {
		this.sigProcessamentoCustos = sigProcessamentoCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigProcessamentoAnalises)) {
			return false;
		}
		SigProcessamentoAnalises castOther = (SigProcessamentoAnalises) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {

		SEQ("seq"),
		PARECER("parecer"),
		DT_PARECER("dtParecer"),
		CRIADO_EM("criadoEm"),
		CENTRO_CUSTO("fccCentroCustos"),
		PROCESSAMENTO_CUSTO("sigProcessamentoCustos"),
		SERVIDOR_RESPONSAVEL("rapServidores");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

}
