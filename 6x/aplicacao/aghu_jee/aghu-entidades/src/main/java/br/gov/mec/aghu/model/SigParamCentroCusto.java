package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioTipoRateio;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPntSq1", sequenceName = "SIG_PNT_SQ1", allocationSize = 1)
@Table(name = "SIG_PARAM_CENTRO_CUSTOS", schema = "AGH")
@org.hibernate.annotations.Cache(usage = org.hibernate.annotations.CacheConcurrencyStrategy.TRANSACTIONAL)
public class SigParamCentroCusto extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6808085580297016823L;
	private Integer seq;
	private FccCentroCustos fccCentroCustos;
	private SigDirecionadores sigDirecionadores;
	private DominioTipoRateio tipoRateio;
	private Date criadoEm;
	private RapServidores servidor;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPntSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "CCT_CODIGO", referencedColumnName = "CODIGO") })
	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ") })
	public SigDirecionadores getSigDirecionadores() {
		return sigDirecionadores;
	}

	public void setSigDirecionadores(SigDirecionadores sigDirecionadores) {
		this.sigDirecionadores = sigDirecionadores;
	}

	@Column(name = "TIPO_RATEIO", nullable = true, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioTipoRateio getTipoRateio() {
		return tipoRateio;
	}

	public void setTipoRateio(DominioTipoRateio tipoRateio) {
		this.tipoRateio = tipoRateio;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		SEQ("seq"), CENTRO_CUSTO("fccCentroCustos"), DIRECIONADOR("sigDirecionadores"), TIPO_RATEIO("tipoRateio"), CRIADO_EM("criadoEm"), SERVIDOR("servidor");

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
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigParamCentroCusto)) {
			return false;
		}
		SigParamCentroCusto other = (SigParamCentroCusto) obj;
		return new EqualsBuilder().append(this.seq, other.seq).isEquals();
	}
}
