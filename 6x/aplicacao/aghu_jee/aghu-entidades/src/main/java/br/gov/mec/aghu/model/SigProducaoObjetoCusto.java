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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPjcSq1", sequenceName = "SIG_PJC_SQ1", allocationSize = 1)
@Table(name = "sig_producao_objeto_custos", schema = "agh")
public class SigProducaoObjetoCusto extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 3830527645419571344L;
	private Integer seq;
	private SigDetalheProducao sigDetalheProducoes;
	private SigCalculoObjetoCusto sigCalculoObjetoCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	public SigProducaoObjetoCusto() {
	}

	public SigProducaoObjetoCusto(Integer seq, Date criadoEm, RapServidores rapServidores) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
	}

	public SigProducaoObjetoCusto(Integer seq, SigDetalheProducao sigDetalheProducoes, SigCalculoObjetoCusto sigCalculoObjetoCustos, Date criadoEm,
			RapServidores rapServidores) {
		this.seq = seq;
		this.sigDetalheProducoes = sigDetalheProducoes;
		this.sigCalculoObjetoCustos = sigCalculoObjetoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPjcSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "dhp_seq", referencedColumnName = "seq")
	public SigDetalheProducao getSigDetalheProducoes() {
		return this.sigDetalheProducoes;
	}

	public void setSigDetalheProducoes(SigDetalheProducao sigDetalheProducoes) {
		this.sigDetalheProducoes = sigDetalheProducoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq", referencedColumnName = "seq")
	public SigCalculoObjetoCusto getSigCalculoObjetoCustos() {
		return this.sigCalculoObjetoCustos;
	}

	public void setSigCalculoObjetoCustos(SigCalculoObjetoCusto sigCalculoObjetoCustos) {
		this.sigCalculoObjetoCustos = sigCalculoObjetoCustos;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "criado_em", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
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

	public enum Fields {

		SEQ("seq"),
		DETALHE_PRODUCAO("sigDetalheProducoes"),
		CALCULO_OBJETO_CUSTOS("sigCalculoObjetoCustos"),
		CRIADO_EM("criadoEm"),
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

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigProducaoObjetoCusto)) {
			return false;
		}
		SigProducaoObjetoCusto castOther = (SigProducaoObjetoCusto) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
