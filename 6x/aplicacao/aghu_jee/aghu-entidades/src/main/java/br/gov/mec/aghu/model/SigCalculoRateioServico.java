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

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCrsSq1", sequenceName = "sig_crs_sq1", allocationSize = 1)
@Table(name = "sig_calculo_rateio_servicos", schema = "agh")
public class SigCalculoRateioServico extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 8504230990089619943L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigCalculoObjetoCusto sigCalculoObjetoCustos;
	private ScoItensContrato scoItensContrato;
	private ScoAfContrato scoAfContrato;
	private Double qtde;
	private BigDecimal vlrItemContrato;
	private Integer version;
	private ScoServico servico;
	private ScoGrupoServico grupoServico;
	private ScoAutorizacaoForn autorizacaoForn;
	private BigDecimal peso;

	public SigCalculoRateioServico() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCrsSq1")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq", nullable = false, referencedColumnName = "seq")
	public SigCalculoObjetoCusto getSigCalculoObjetoCustos() {
		return this.sigCalculoObjetoCustos;
	}

	public void setSigCalculoObjetoCustos(SigCalculoObjetoCusto sigCalculoObjetoCustos) {
		this.sigCalculoObjetoCustos = sigCalculoObjetoCustos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "icon_seq", referencedColumnName = "seq")
	public ScoItensContrato getScoItensContrato() {
		return scoItensContrato;
	}

	public void setScoItensContrato(ScoItensContrato scoItensContrato) {
		this.scoItensContrato = scoItensContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "afco_seq", referencedColumnName = "seq")
	public ScoAfContrato getScoAfContrato() {
		return this.scoAfContrato;
	}

	public void setScoAfContrato(ScoAfContrato scoAfContrato) {
		this.scoAfContrato = scoAfContrato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SRV_CODIGO")
	public ScoServico getServico() {
		return servico;
	}

	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "AFN_NUMERO", referencedColumnName = "NUMERO")
	public ScoAutorizacaoForn getAutorizacaoForn() {
		return autorizacaoForn;
	}

	public void setAutorizacaoForn(ScoAutorizacaoForn autorizacaoForn) {
		this.autorizacaoForn = autorizacaoForn;
	}

	@JoinColumn(name = "GSV_CODIGO", referencedColumnName = "CODIGO")
	@ManyToOne(fetch = FetchType.LAZY)
	public ScoGrupoServico getGrupoServico() {
		return grupoServico;
	}

	public void setGrupoServico(ScoGrupoServico grupoServico) {
		this.grupoServico = grupoServico;
	}

	@Column(name = "qtde", nullable = false, precision = 12, scale = 4)
	public Double getQtde() {
		return this.qtde;
	}

	public void setQtde(Double qtde) {
		this.qtde = qtde;
	}

	@Column(name = "vlr_item_contrato", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrItemContrato() {
		return this.vlrItemContrato;
	}

	public void setVlrItemContrato(BigDecimal vlrItemContrato) {
		this.vlrItemContrato = vlrItemContrato;
	}
	
	@Column(name = "peso", nullable = false, precision = 14, scale = 5)
	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
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
		CALCULO_OBJETO_CUSTO("sigCalculoObjetoCustos"),
		CALCULO_OBJETO_CUSTO_SEQ("sigCalculoObjetoCustos.seq"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		QUANTIDADE("qtde"),
		ITENS_CONTRATO("scoItensContrato"),
		ITENS_CONTRATO_SEQ("scoItensContrato.seq"),
		AF_CONTRATO("scoAfContrato"),
		AF_CONTRATO_SEQ("scoAfContrato.seq"),
		AUTORIZACAO_FORNECEDOR("autorizacaoForn"),
		VALOR_ITEM_CONTRATO("vlrItemContrato"),
		PESO("peso"),
		SERVICO("servico");

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
		if (!(other instanceof SigCalculoRateioServico)) {
			return false;
		}
		SigCalculoRateioServico castOther = (SigCalculoRateioServico) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}
}
