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
@SequenceGenerator(name = "sigCriSq1", sequenceName = "SIG_CRP_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_rateio_pessoas", schema = "agh")
public class SigCalculoRateioPessoa extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -6240040634156631465L;

	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigCalculoObjetoCusto sigCalculoObjetoCustos;
	private SigGrupoOcupacoes sigGrupoOcupacoes;
	private Double qtde;
	private BigDecimal vlrPessoal;
	private Integer version;
	private BigDecimal peso;

	public SigCalculoRateioPessoa() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCriSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq", nullable = false, referencedColumnName = "seq")
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GOC_SEQ", referencedColumnName = "SEQ")
	public SigGrupoOcupacoes getSigGrupoOcupacoes() {
		return sigGrupoOcupacoes;
	}

	public void setSigGrupoOcupacoes(SigGrupoOcupacoes sigGrupoOcupacoes) {
		this.sigGrupoOcupacoes = sigGrupoOcupacoes;
	}

	@Column(name = "qtde", precision = 12, scale = 4)
	public Double getQtde() {
		return this.qtde;
	}

	public void setQtde(Double qtde) {
		this.qtde = qtde;
	}

	@Column(name = "vlr_pessoal", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrPessoal() {
		return this.vlrPessoal;
	}

	public void setVlrPessoal(BigDecimal vlrPessoal) {
		this.vlrPessoal = vlrPessoal;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "peso", nullable = false, precision = 14, scale = 5)
	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public enum Fields {

		SEQ("seq"),
		CALCULO_OBJETO_CUSTO("sigCalculoObjetoCustos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		GRUPO_OCUPACAO("sigGrupoOcupacoes"),
		QUANTIDADE("qtde"),
		PESO("peso"),
		VALOR_PESSOAL("vlrPessoal");

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
		if (!(other instanceof SigCalculoRateioPessoa)) {
			return false;
		}
		SigCalculoRateioPessoa castOther = (SigCalculoRateioPessoa) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
