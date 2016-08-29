package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
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
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigOpeSq1", sequenceName = "SIG_OPE_SQ1", allocationSize = 1)
@Table(name = "sig_objeto_custo_pesos", schema = "agh")
public class SigObjetoCustoPesos extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -7899323029489378118L;
	private Integer seq;
	private SigObjetoCustos sigObjetoCustos;
	private BigDecimal valor;
	private BigDecimal valorAjustado;
	private Date criadoEm;
	private RapServidores rapServidores;
	private Integer version;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigOpeSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@OneToOne
	@JoinColumn(name = "obj_seq", referencedColumnName = "seq")
	public SigObjetoCustos getSigObjetoCustos() {
		return sigObjetoCustos;
	}

	public void setSigObjetoCustos(SigObjetoCustos sigObjetoCustos) {
		this.sigObjetoCustos = sigObjetoCustos;
	}

	@Column(name = "valor", precision = 20, scale = 4)
	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Column(name = "valor_ajustado", nullable = true, precision = 20, scale = 4)
	public BigDecimal getValorAjustado() {
		return valorAjustado;
	}

	public void setValorAjustado(BigDecimal valorAjustado) {
		this.valorAjustado = valorAjustado;
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

//	@Transient
//	public BigInteger getValorAjustadoInteiro() {
//		if (this.getValorAjustado() == null) {
//			return null;
//		}
//		return this.getValorAjustado().toBigInteger();
//	}
//
//	public void setValorAjustadoInteiro(BigInteger valorAjustadoInteiro) {
//		if (valorAjustadoInteiro == null) {
//			this.setValorAjustado(null);
//		} else {
//			this.setValorAjustado(new BigDecimal(valorAjustadoInteiro));
//		}
//	}

	@Transient
	public BigInteger getValorInteiro() {
		if (this.getValor() == null) {
			return null;
		}
		return this.getValor().toBigInteger();
	}

	public void setValorInteiro(BigInteger valorAjustadoInteiro) {
		if (valorAjustadoInteiro == null) {
			this.setValor(null);
		} else {
			this.setValor(new BigDecimal(valorAjustadoInteiro));
		}
	}

	public enum Fields {

		SEQ("seq"),
		OBJETO_CUSTO("sigObjetoCustos"),
		VALOR("valor"),
		VALOR_AJUSTADO("valorAjustado"),
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
		if (!(other instanceof SigObjetoCustoPesos)) {
			return false;
		}
		SigObjetoCustoPesos castOther = (SigObjetoCustoPesos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
