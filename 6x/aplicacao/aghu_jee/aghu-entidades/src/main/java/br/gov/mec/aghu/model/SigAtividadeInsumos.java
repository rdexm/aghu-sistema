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
import org.apache.commons.lang3.builder.ToStringBuilder;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name = "SIG_ATIVIDADE_INSUMOS", schema = "AGH")
@SequenceGenerator(name = "sigAisSq1", sequenceName = "AGH.SIG_AIS_SQ1", allocationSize = 1)
public class SigAtividadeInsumos extends BaseEntitySeq<Long> implements Serializable, Cloneable {

	private static final long serialVersionUID = -103366656125178956L;
	private Long seq;
	private SigAtividades sigAtividades;
	private ScoMaterial material;
	private BigDecimal qtdeUso;
	private ScoUnidadeMedida unidadeMedida;
	private SigDirecionadores direcionadores;
	private Integer vidaUtilQtde;
	private Integer vidaUtilTempo;
	private Date criadoEm;
	private RapServidores rapServidores;
	private DominioSituacao indSituacao;
	private Integer version;
	
	
	private Boolean selected = Boolean.FALSE;
	
	public SigAtividadeInsumos(){		
	}
	
	public SigAtividadeInsumos(Long seq){		
		this.seq = seq;
	}

	// getters & setters
	@Id
	@Column(name = "SEQ", nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sigAisSq1")
	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TVD_SEQ", referencedColumnName = "SEQ")
	public SigAtividades getSigAtividades() {
		return sigAtividades;
	}

	public void setSigAtividades(SigAtividades sigAtividades) {
		this.sigAtividades = sigAtividades;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}

	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@Column(name = "QTDE_USO", nullable = true, precision = 15, scale = 5)
	public BigDecimal getQtdeUso() {
		return qtdeUso;
	}

	public void setQtdeUso(BigDecimal qtdeUso) {
		this.qtdeUso = qtdeUso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DIR_SEQ", referencedColumnName = "SEQ")
	public SigDirecionadores getDirecionadores() {
		return direcionadores;
	}

	public void setDirecionadores(SigDirecionadores direcionadores) {
		this.direcionadores = direcionadores;
	}

	@Column(name = "VIDA_UTIL_TEMPO", nullable = true)
	public Integer getVidaUtilTempo() {
		return vidaUtilTempo;
	}

	public void setVidaUtilTempo(Integer vidaUtilTempo) {
		this.vidaUtilTempo = vidaUtilTempo;
	}

	@Column(name = "VIDA_UTIL_QTDE", nullable = true)
	public Integer getVidaUtilQtde() {
		return vidaUtilQtde;
	}

	public void setVidaUtilQtde(Integer vidaUtilQtde) {
		this.vidaUtilQtde = vidaUtilQtde;
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
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Column(name = "IND_SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "VERSION", nullable = false)
	@Version
	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@Transient
	public Boolean getSelected() {
		return selected;
	}

	public void setSelected(Boolean selected) {
		this.selected = selected;
	}

	@Override
	public String toString() {
		return new ToStringBuilder(this).append("seq", this.seq).toString();
	}

	@Override
	public boolean equals(Object other) {
		if (!(other instanceof SigAtividadeInsumos)) {
			return false;
		}
		SigAtividadeInsumos castOther = (SigAtividadeInsumos) other;
		return new EqualsBuilder().append(this.seq, castOther.getSeq()).isEquals();
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(this.seq).toHashCode();
	}

	public enum Fields {
		SEQ("seq"),
		SIG_ATIVIDADES("sigAtividades"),
		MATERIAL("material"),
		MATERIAL_CODIGO("material.codigo"),
		QTD_EUSO("qtdeUso"),
		UNIDADE_MEDIDA("unidadeMedida"),
		DIRECIONADORES("direcionadores"),
		VIDA_UTIL_QTDE("vidaUtilQtde"),
		VIDA_UTIL_TEMPO("vidaUtilTempo"),
		CRIADO_EM("criadoEm"),
		RAP_SERVIDORES("rapServidores"),
		IND_SITUACAO("indSituacao"),
		SIG_ATIVIDADES_SEQ("sigAtividades.seq");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}

}