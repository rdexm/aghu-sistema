package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigOdrSq1", sequenceName = "sig_odr_sq1", allocationSize = 1)
@Table(name = "sig_objeto_custo_dir_rateios", schema = "agh")
public class SigObjetoCustoDirRateios extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -6586604013029669298L;
	
	private Integer seq;
	private SigObjetoCustoVersoes objetoCustoVersoes;
	private SigDirecionadores direcionadores;
	private BigDecimal percentual;
	private Date criadoEm;
	private RapServidores servidor;
	private DominioSituacao situacao;
	private Boolean emEdicao = Boolean.FALSE;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigOdrSq1")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "OCV_SEQ", referencedColumnName = "SEQ")
	public SigObjetoCustoVersoes getObjetoCustoVersoes() {
		return objetoCustoVersoes;
	}

	public void setObjetoCustoVersoes(SigObjetoCustoVersoes objetoCustoVersoes) {
		this.objetoCustoVersoes = objetoCustoVersoes;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getDirecionadores() {
		return direcionadores;
	}

	public void setDirecionadores(SigDirecionadores direcionadores) {
		this.direcionadores = direcionadores;
	}

	@Column(name = "PERCENTUAL", nullable = false, precision = 5, scale = 2)
	public BigDecimal getPercentual() {
		return percentual;
	}

	public void setPercentual(BigDecimal percentual) {
		this.percentual = percentual;
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

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Transient
	public Boolean getEmEdicao() {
		return emEdicao;
	}

	public void setEmEdicao(Boolean emEdicao) {
		this.emEdicao = emEdicao;
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
		OBJETO_CUSTO_VERSAO("objetoCustoVersoes"),
		OBJETO_CUSTO_VERSAO_SEQ("objetoCustoVersoes.seq"),
		DIRECIONADORES("direcionadores"),
		PERCENTUAL("percentual"),
		CRIADO_EM("criadoEm"),
		SERVIDOR("servidor"),
		SITUACAO("situacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigObjetoCustoDirRateios)) {
			return false;
		}
		SigObjetoCustoDirRateios castOther = (SigObjetoCustoDirRateios) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
