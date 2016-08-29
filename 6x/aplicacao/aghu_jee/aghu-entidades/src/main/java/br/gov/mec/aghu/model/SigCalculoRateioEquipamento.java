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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@SequenceGenerator(name = "sigCrqSq1", sequenceName = "SIG_CRQ_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_rat_equipamentos", schema = "agh")
public class SigCalculoRateioEquipamento extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = -4363286041047892408L;

	@Transient
	private final String EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA = "^0*";
	
	private Integer seq;
	private Date criadoEm;
	private RapServidores rapServidores;
	private SigCalculoObjetoCusto sigCalculoObjetoCustos;
	private String codPatrimonio;
	private Double qtde;
	private BigDecimal vlrDepreciacao;
	private Integer version;
	private BigDecimal peso;

	public SigCalculoRateioEquipamento() {
	}

	public SigCalculoRateioEquipamento(Integer seq, Date criadoEm, RapServidores rapServidores, SigCalculoObjetoCusto sigCalculoObjetoCustos,
			String codPatrimonio, Double qtde, BigDecimal vlrDepreciacao) {

		this.seq = seq;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.sigCalculoObjetoCustos = sigCalculoObjetoCustos;
		this.codPatrimonio = codPatrimonio;
		this.qtde = qtde;
		this.vlrDepreciacao = vlrDepreciacao;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigCrqSq1")
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

	@Column(name = "cod_patrimonio", nullable = false, length = 60)
	public String getCodPatrimonio() {
		return this.codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		if (codPatrimonio != null) {
			codPatrimonio = codPatrimonio.replaceFirst(EXPRESAO_REGEN_ELIMINAR_0_ESQUERDA, "");
		}
		this.codPatrimonio = codPatrimonio;
	}

	@Column(name = "qtde", precision = 12, scale = 4)
	public Double getQtde() {
		return this.qtde;
	}

	public void setQtde(Double qtde) {
		this.qtde = qtde;
	}

	@Column(name = "vlr_deprec", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrDepreciacao() {
		return vlrDepreciacao;
	}

	public void setVlrDepreciacao(BigDecimal vlrDepreciacao) {
		this.vlrDepreciacao = vlrDepreciacao;
	}

	@Column(name = "version", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Column(name = "peso", precision = 14, scale = 5)	
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
		QUANTIDADE("qtde"),
		CODIGO_PATRIMONIO("codPatrimonio"),
		PESO("peso"),
		VALOR_DEPRECIACAO("vlrDepreciacao");

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
	public int hashCode() {
		return new HashCodeBuilder().append(seq).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof SigCalculoRateioEquipamento)) {
			return false;
		}
		SigCalculoRateioEquipamento other = (SigCalculoRateioEquipamento) obj;
		return new EqualsBuilder().append(this.seq, other.getSeq()).isEquals();
	}
}
