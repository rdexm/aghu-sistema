package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
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
@SequenceGenerator(name = "sigCmcSq1", sequenceName = "AGH.SIG_CCL_SQ1", allocationSize = 1)
@Table(name = "SIG_CALCULO_CLIENTES", schema = "AGH")
public class SigCalculoCliente extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -234879589345983453L;

	private Integer seq;
	private SigDirecionadores direcionador;
	private SigCalculoObjetoCusto calculoObjetoCusto;
	private FccCentroCustos centroCusto;
	private RapServidores servidor;
	private Date criadoEm;
	private BigDecimal valor;
	private Integer version;

	public SigCalculoCliente() {
	}
	
	public SigCalculoCliente(Integer seq) {
		this.seq = seq;
	}
	
	public SigCalculoCliente(Integer seq, BigDecimal valor, FccCentroCustos centroCusto) {
		this(seq);
		this.valor = valor;
		this.centroCusto = centroCusto;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCmcSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ")
	public SigDirecionadores getDirecionador() {
		return direcionador;
	}

	public void setDirecionador(SigDirecionadores direcionador) {
		this.direcionador = direcionador;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CBJ_SEQ")
	public SigCalculoObjetoCusto getCalculoObjetoCusto() {
		return calculoObjetoCusto;
	}

	public void setCalculoObjetoCusto(SigCalculoObjetoCusto calculoObjetoCusto) {
		this.calculoObjetoCusto = calculoObjetoCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO")
	public FccCentroCustos getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(FccCentroCustos centroCusto) {
		this.centroCusto = centroCusto;
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

	@Column(name = "CRIADO_EM", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name = "VALOR", nullable = false, precision = 14, scale = 4)
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigCalculoCliente)) {
			return false;
		}
		SigCalculoCliente castOther = (SigCalculoCliente) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"),
		DIRECIONADOR("direcionador"),
		CALCULO_OBJETO_CUSTO("calculoObjetoCusto"),
		CENTRO_CUSTO("centroCusto"),
		SERVIDOR("servidor"),
		CRIADO_EM("criadoEm"),
		VALOR("valor");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

}