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


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.dominio.DominioTipoDetalheFolha;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigDfpSq1", sequenceName = "SIG_DFP_SQ1", allocationSize = 1)
@Table(name = "SIG_DETALHE_FOLHA_PESSOAS", schema = "AGH")
public class SigDetalheFolhaPessoa extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -1242353924724619823L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigMvtoContaMensal sigMvtoContaMensal;
	private DominioTipoDetalheFolha indTipoDetalheFolha;
	private BigDecimal valor;
	private String ocaCarCodigo;
	private Integer ocaCodigo;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigDfpSq1")
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
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "msl_seq", referencedColumnName = "seq")
	public SigMvtoContaMensal getSigMvtoContaMensal() {
		return sigMvtoContaMensal;
	}

	public void setSigMvtoContaMensal(SigMvtoContaMensal sigMvtoContaMensal) {
		this.sigMvtoContaMensal = sigMvtoContaMensal;
	}

	@Column(name = "ind_tipo_detalhe", nullable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioTipoDetalheFolha getIndTipoDetalheFolha() {
		return indTipoDetalheFolha;
	}

	public void setIndTipoDetalheFolha(DominioTipoDetalheFolha indTipoDetalheFolha) {
		this.indTipoDetalheFolha = indTipoDetalheFolha;
	}

	@Column(name = "valor", nullable = false, precision = 18, scale = 4)
	public BigDecimal getValor() {
		return this.valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	@Column(name = "oca_car_codigo", length = 10)
	public String getOcaCarCodigo() {
		return ocaCarCodigo;
	}

	public void setOcaCarCodigo(String ocaCarCodigo) {
		this.ocaCarCodigo = ocaCarCodigo;
	}

	@Column(name = "oca_codigo")
	public Integer getOcaCodigo() {
		return ocaCodigo;
	}

	public void setOcaCodigo(Integer ocaCodigo) {
		this.ocaCodigo = ocaCodigo;
	}

	public enum Fields {

		SEQ("seq"),
		SIG_MVTO_CONTA_MENSAL("sigMvtoContaMensal"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		TIPO_DETALHE_FOLHA("indTipoDetalheFolha"),
		VALOR("valor"),
		OCA_CAR_CODIGO("ocaCarCodigo"),
		OCA_CODIGO("ocaCodigo");

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
		if (!(other instanceof SigDetalheFolhaPessoa)) {
			return false;
		}
		SigDetalheFolhaPessoa castOther = (SigDetalheFolhaPessoa) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
