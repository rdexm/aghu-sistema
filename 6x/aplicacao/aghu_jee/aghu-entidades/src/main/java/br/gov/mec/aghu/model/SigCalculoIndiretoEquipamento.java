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
@SequenceGenerator(name = "sigCieSq1", sequenceName = "SIG_CIE_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_ind_equipamentos", schema = "agh")
public class SigCalculoIndiretoEquipamento extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 1491834913494419853L;
	
	
	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigCalculoObjetoCusto sigCalculoObjetoCusto;
	private SigCalculoObjetoCusto sigCalculoObjetoCustoDebita;
	private BigDecimal qtde;
	private BigDecimal vlrEquipamentos;
	private BigDecimal peso;
	private Integer version;
	private Integer iteracao;

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCieSq1")
	@Column(name = "seq", unique = true, nullable = false)
	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq", referencedColumnName = "seq", nullable = false)
	public SigCalculoObjetoCusto getSigCalculoObjetoCusto() {
		return sigCalculoObjetoCusto;
	}

	public void setSigCalculoObjetoCusto(SigCalculoObjetoCusto sigCalculoObjetoCusto) {
		this.sigCalculoObjetoCusto = sigCalculoObjetoCusto;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "cbj_seq_debita", referencedColumnName = "seq", nullable = false)
	public SigCalculoObjetoCusto getSigCalculoObjetoCustoDebita() {
		return sigCalculoObjetoCustoDebita;
	}

	public void setSigCalculoObjetoCustoDebita(SigCalculoObjetoCusto sigCalculoObjetoCustoDebita) {
		this.sigCalculoObjetoCustoDebita = sigCalculoObjetoCustoDebita;
	}

	@Column(name = "qtde", precision = 12, scale = 4, nullable = false)
	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}

	@Column(name = "vlr_equipamentos", precision = 20, scale = 4, nullable = false)
	public BigDecimal getVlrEquipamentos() {
		return vlrEquipamentos;
	}

	public void setVlrEquipamentos(BigDecimal vlrEquipamentos) {
		this.vlrEquipamentos = vlrEquipamentos;
	}
	
	@Column(name = "peso", precision = 14, scale = 5, nullable = false)
	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	@Column(name = "VERSION", length = 7)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "ITERACAO", precision = 3)
	public Integer getIteracao(){
		return this.iteracao;
	}
	
	public void setIteracao(Integer iteracao){
		this.iteracao = iteracao;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigCalculoIndiretoEquipamento)) {
			return false;
		}
		SigCalculoIndiretoEquipamento castOther = (SigCalculoIndiretoEquipamento) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"),
		SERVIDOR("rapServidores"),
		CRIADO_EM("criadoEm"),
		CALCULO_OBJETO_CUSTO("sigCalculoObjetoCusto"),
		CALCULO_OBJETO_CUSTO_DEBITA("sigCalculoObjetoCustoDebita"),
		QTDE("qtde"),
		VALOR_EQUIPAMENTOS("vlrEquipamentos"),
		PESO("peso"),
		ITERACAO("iteracao");

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
