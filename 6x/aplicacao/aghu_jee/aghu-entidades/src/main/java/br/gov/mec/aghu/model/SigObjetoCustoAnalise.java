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
import org.hibernate.annotations.Type;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigOcsSq1", sequenceName = "SIG_OCS_SQ1", allocationSize = 1)
@Table(name = "SIG_OBJETO_CUSTO_ANALISES", schema = "agh")
public class SigObjetoCustoAnalise extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 5135448546529152224L;

	private Integer seq;
	private Boolean principal;
	private SigObjetoCustoVersoes sigObjetoCustoVersoes;
	private SigCalculoAtdConsumo calculoAtividadeConsumo;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigOcsSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ocv_seq", nullable = false, referencedColumnName = "seq")
	public SigObjetoCustoVersoes getSigObjetoCustoVersoes() {
		return this.sigObjetoCustoVersoes;
	}

	public void setSigObjetoCustoVersoes(SigObjetoCustoVersoes sigObjetoCustoVersoes) {
		this.sigObjetoCustoVersoes = sigObjetoCustoVersoes;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "IND_PRINCIPAL", nullable = false, length = 1)
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getPrincipal() {
		return principal;
	}

	public void setPrincipal(Boolean principal) {
		this.principal = principal;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCA_SEQ")
	public SigCalculoAtdConsumo getCalculoAtividadeConsumo() {
		return calculoAtividadeConsumo;
	}

	public void setCalculoAtividadeConsumo(SigCalculoAtdConsumo calculoAtividadeConsumo) {
		this.calculoAtividadeConsumo = calculoAtividadeConsumo;
	}

	public enum Fields {

		SEQ("seq"),
		PRINCIPAL("principal"),
		CRIADO_EM("criadoEm"),
		OBJETO_CUSTO_VERSAO("sigObjetoCustoVersoes"),
		CALCULO_ATIVIDADE_CONSUMO("calculoAtividadeConsumo"),
		SERVIDOR("rapServidores");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoAnalise)) {
			return false;
		}
		SigObjetoCustoAnalise castOther = (SigObjetoCustoAnalise) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
