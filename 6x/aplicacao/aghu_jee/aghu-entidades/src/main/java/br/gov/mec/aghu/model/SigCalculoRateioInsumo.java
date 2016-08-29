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
@SequenceGenerator(name = "sigCriSq1", sequenceName = "SIG_CRI_SQ1", allocationSize = 1)
@Table(name = "sig_calculo_rateio_insumos", schema = "agh")
public class SigCalculoRateioInsumo extends BaseEntitySeq<Integer> implements Serializable {

	private static final long serialVersionUID = 8504230990089519943L;

	private Integer seq;
	private SigCalculoObjetoCusto sigCalculoObjetoCustos;
	private Date criadoEm;
	private RapServidores rapServidores;
	private ScoMaterial scoMaterial;
	private Double qtde;
	private BigDecimal vlrInsumo;
	private Integer version;
	private ScoGrupoMaterial grupoMaterial;	
	private BigDecimal peso;

	public SigCalculoRateioInsumo() {
	}

	public SigCalculoRateioInsumo(Integer seq, SigCalculoObjetoCusto sigCalculoObjetoCustos, Date criadoEm, RapServidores rapServidores,
			ScoMaterial scoMaterial, Double qtde, BigDecimal vlrInsumo, ScoGrupoMaterial grupoMaterial) {
		this.seq = seq;
		this.sigCalculoObjetoCustos = sigCalculoObjetoCustos;
		this.criadoEm = criadoEm;
		this.rapServidores = rapServidores;
		this.scoMaterial = scoMaterial;
		this.qtde = qtde;
		this.vlrInsumo = vlrInsumo;
		this.grupoMaterial = grupoMaterial;
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
	@JoinColumn(name = "mat_codigo", referencedColumnName = "codigo")
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@Column(name = "qtde", precision = 12, scale = 4)
	public Double getQtde() {
		return this.qtde;
	}

	public void setQtde(Double qtde) {
		this.qtde = qtde;
	}

	@Column(name = "vlr_insumo", nullable = false, precision = 20, scale = 4)
	public BigDecimal getVlrInsumo() {
		return this.vlrInsumo;
	}

	public void setVlrInsumo(BigDecimal vlrInsumo) {
		this.vlrInsumo = vlrInsumo;
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

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "gmt_codigo", referencedColumnName = "codigo")
	public ScoGrupoMaterial getGrupoMaterial() {
		return grupoMaterial;
	}

	public void setGrupoMaterial(ScoGrupoMaterial grupoMaterial) {
		this.grupoMaterial = grupoMaterial;
	}

	public enum Fields {

		SEQ("seq"),
		CALCULO_OBJETO_CUSTO("sigCalculoObjetoCustos"),
		CRIADO_EM("criadoEm"),
		SERVIDOR_RESPONSAVEL("rapServidores"),
		MATERIAL("scoMaterial"),
		QUANTIDADE("qtde"),
		VALOR_INSUMO("vlrInsumo"),
		GRUPO_MATERIAL("grupoMaterial");

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
		if (!(other instanceof SigCalculoRateioInsumo)) {
			return false;
		}
		SigCalculoRateioInsumo castOther = (SigCalculoRateioInsumo) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

}
