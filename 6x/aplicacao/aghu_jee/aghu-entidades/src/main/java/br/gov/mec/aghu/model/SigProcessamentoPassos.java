package br.gov.mec.aghu.model;

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

import br.gov.mec.aghu.dominio.DominioSituacaoEtapaProcessamento;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigPrpSq1", sequenceName = "SIG_PRP_SQ1", allocationSize = 1)
@Table(name = "sig_processamento_passos", schema = "agh")
public class SigProcessamentoPassos extends BaseEntitySeq<Integer> implements java.io.Serializable {

	private static final long serialVersionUID = -8314139263779182269L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;
	private SigProcessamentoCusto sigProcessamentoCusto;
	private SigPassos sigPassos;
	private DominioSituacaoEtapaProcessamento situacao;

	public SigProcessamentoPassos() {
	}

	public SigProcessamentoPassos(Integer seq, Date criadoEm, RapServidores rapServidores, SigProcessamentoCusto sigProcessamentoCusto, SigPassos sigPassos,
			DominioSituacaoEtapaProcessamento situacao) {
		this.seq = seq;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.sigProcessamentoCusto = sigProcessamentoCusto;
		this.sigPassos = sigPassos;
		this.situacao = situacao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigPrpSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	@Column(name = "VERSION")
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pmu_seq", referencedColumnName = "seq")
	public SigProcessamentoCusto getSigProcessamentoCusto() {
		return sigProcessamentoCusto;
	}

	public void setSigProcessamentoCusto(SigProcessamentoCusto sigProcessamentoCusto) {
		this.sigProcessamentoCusto = sigProcessamentoCusto;
	}

	@OneToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "pss_seq", referencedColumnName = "seq")
	public SigPassos getSigPassos() {
		return sigPassos;
	}

	public void setSigPassos(SigPassos sigPassos) {
		this.sigPassos = sigPassos;
	}

	@Column(name = "situacao", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacaoEtapaProcessamento getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoEtapaProcessamento situacao) {
		this.situacao = situacao;
	}

	public enum Fields {

		SEQ("seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		PROCESSAMENTO_CUSTO("sigProcessamentoCusto"),
		PASSOS("sigPassos"),
		SITUACAO("situacao");

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
		if (!(other instanceof SigProcessamentoPassos)) {
			return false;
		}
		SigProcessamentoPassos castOther = (SigProcessamentoPassos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
