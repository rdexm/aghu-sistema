package br.gov.mec.aghu.model;

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
import javax.validation.constraints.NotNull;

import br.gov.mec.aghu.core.model.BaseJournal;


@Entity
@SequenceGenerator(name="mbcAaoJnSeq", sequenceName="AGH.MBC_AAO_JN_SEQ", allocationSize = 1)
@Table(name = "MBC_ALCADA_AVAL_OPMS_JN", schema = "AGH")
public class MbcAlcadaAvalOpmsJn extends BaseJournal implements java.io.Serializable {

	private static final long serialVersionUID = -8492806268907944587L;
	private Short seq;
	private MbcGrupoAlcadaAvalOpms grupoAlcada;
	private Integer nivelAlcada;
	private String descricao;
	private BigDecimal valorMinimo;
	private BigDecimal valorMaximo;
	private Date criadoEm;
	private Date modificadoEm;
	private RapServidores rapServidores;
	private RapServidores rapServidoresModificacao;	
	
	public MbcAlcadaAvalOpmsJn() {
	}

	@Id
	@Column(name = "SEQ_JN", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "mbcAaoJnSeq")
	@NotNull
	@Override
	public Integer getSeqJn() {
		return super.getSeqJn();
	}
	
	@Column(name = "SEQ", unique = true, nullable = false)
	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "GAO_SEQ",referencedColumnName="SEQ")
	@NotNull
	public MbcGrupoAlcadaAvalOpms getGrupoAlcada() {
		return grupoAlcada;
	}
	
	public void setGrupoAlcada(MbcGrupoAlcadaAvalOpms grupoAlcada) {
		this.grupoAlcada = grupoAlcada;
	}

	@Column(name = "NIVEL_ALCADA", nullable = false)
	public Integer getNivelAlcada() {
		return this.nivelAlcada;
	}

	public void setNivelAlcada(Integer nivelAlcada) {
		this.nivelAlcada = nivelAlcada;
	}
	
	@Column(name = "DESCRICAO", nullable = false, length = 60)
	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	@Column(name = "VALOR_MINIMO", precision=14, scale = 2)
	public BigDecimal getValorMinimo() {
		return valorMinimo;
	}

	public void setValorMinimo(BigDecimal valorMinimo) {
		this.valorMinimo = valorMinimo;
	}
	
	@Column(name = "VALOR_MAXIMO", precision=14, scale = 2)
	public BigDecimal getValorMaximo() {
		return valorMaximo;
	}

	public void setValorMaximo(BigDecimal valorMaximo) {
		this.valorMaximo = valorMaximo;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "MODIFICADO_EM", length = 29)
	public Date getModificadoEm() {
		return modificadoEm;
	}


	public void setModificadoEm(Date modificadoEm) {
		this.modificadoEm = modificadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_MODIFICACAO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_MODIFICACAO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getRapServidoresModificacao() {
		return rapServidoresModificacao;
	}


	public void setRapServidoresModificacao(RapServidores rapServidoresModificacao) {
		this.rapServidoresModificacao = rapServidoresModificacao;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA_CRIACAO", referencedColumnName = "MATRICULA", nullable = false),
				   @JoinColumn(name = "SER_VIN_CODIGO_CRIACAO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	@NotNull
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public enum Fields {
		
		ID("seq"),
		GRUPO_ALCADA_SEQ("grupoAlcada.seq"),
		GRUPO_ALCADA("grupoAlcada"),
		NIVEL_ALCADA("nivelAlcada"),
		DESCRICAO("descricao"),
		VALOR_MINIMO("valorMinimo"),
		VALOR_MAXIMO("valorMaximo"),
		CRIADO_EM("criadoEm"),
		MODIFICADO_EM("modificadoEm"),
		RAP_SERVIDORES("rapServidores"),
		RAP_SERVIDORES_MODIFICACAO("rapServidoresModificacao"),
		SERVIDORES_AVAL("mbcServidoresAvalOpms");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}


	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MbcAlcadaAvalOpmsJn)) {
			return false;
		}
		MbcAlcadaAvalOpmsJn other = (MbcAlcadaAvalOpmsJn) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}