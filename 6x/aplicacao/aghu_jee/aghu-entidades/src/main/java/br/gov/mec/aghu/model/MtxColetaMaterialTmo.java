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
import javax.persistence.UniqueConstraint;
import javax.persistence.Version;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the mtx_coleta_materiais_tmo database table.
 * 
 */
@Entity
@SequenceGenerator(name="MTX_COLETA_MATERIAIS_TMO_SEQ_GENERATOR", sequenceName="AGH.MTX_CMA_SQ1", allocationSize=1)
@Table(name="mtx_coleta_materiais_tmo", schema = "AGH", uniqueConstraints = @UniqueConstraint(columnNames = "SEQ"))
public class MtxColetaMaterialTmo extends BaseEntitySeq<Integer> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7569020883404080594L;
	private Integer seq;
	private BigDecimal cd34;
	private BigDecimal celulaNucleada;
	private Integer codMaterial;
	private Date criadoEm;
	private String exameCultural;
	private Integer leucocitos;
	private AipPacientes pacCodigo;
	private BigDecimal peso;
	private RapServidores servidor;
	private Integer version;
	private Integer volume;
	private MtxTransplantes transplante;

	public MtxColetaMaterialTmo() {
	}


	@Id
	@GeneratedValue(strategy=GenerationType.AUTO, generator="MTX_COLETA_MATERIAIS_TMO_SEQ_GENERATOR")
	@Column(name = "SEQ", unique = true, nullable = false)
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "CD34")
	public BigDecimal getCd34() {
		return this.cd34;
	}

	public void setCd34(BigDecimal cd34) {
		this.cd34 = cd34;
	}

	@Column(name="CELULA_NUCLEADA")
	public BigDecimal getCelulaNucleada() {
		return this.celulaNucleada;
	}

	public void setCelulaNucleada(BigDecimal celulaNucleada) {
		this.celulaNucleada = celulaNucleada;
	}

	@Column(name="COD_MATERIAL", nullable = false)
	public Integer getCodMaterial() {
		return this.codMaterial;
	}

	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}

	@Column(name="CRIADO_EM")
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@Column(name="EXAME_CULTURAL")
	public String getExameCultural() {
		return this.exameCultural;
	}

	public void setExameCultural(String exameCultural) {
		this.exameCultural = exameCultural;
	}

	@Column(name = "LEUCOCITOS")
	public Integer getLeucocitos() {
		return this.leucocitos;
	}

	public void setLeucocitos(Integer leucocitos) {
		this.leucocitos = leucocitos;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="PAC_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public AipPacientes getPacCodigo() {
		return this.pacCodigo;
	}

	public void setPacCodigo(AipPacientes pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	@Column(name = "PESO")
	public BigDecimal getPeso() {
		return this.peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@Version
	@Column(name = "VERSION")
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@Column(name = "VOLUME")
	public Integer getVolume() {
		return this.volume;
	}

	public void setVolume(Integer volume) {
		this.volume = volume;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="TRP_SEQ", referencedColumnName = "SEQ", nullable = false)
	public MtxTransplantes getTransplante() {
		return transplante;
	}


	public void setTransplante(MtxTransplantes transplante) {
		this.transplante = transplante;
	}


	public enum Fields {

		SEQ("seq"),
		CD34("cd34"),
		CELULA_NUCLEADA("celulaNucleada"),
		AEL_MATERIAL_ANALISE("codMaterial"),
		AEL_MATERIAL_ANALISE_SEQ("codMaterial.seq"),
		CRIADO_EM("criadoEm"),
		EXAME_CULTURAL("exameCultural"),
		LEUCOCITOS("leucocitos"),
		AIP_PACIENTES("pacCodigo"),
		AIP_PACIENTES_CODIGO("pacCodigo.codigo"),
		PESO("peso"),
		SERVIDOR("servidor"),
		VOLUME("volume"),
		TRANSPLANTE_SEQ("transplante.seq");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
    @Override
    public int hashCode() {
          HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
          umHashCodeBuilder.append(this.getSeq());
          umHashCodeBuilder.append(this.getPacCodigo());
          return umHashCodeBuilder.toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
          if (this == obj) {
                 return true;
          }
          if (obj == null) {
                 return false;
          }
          if (!(obj instanceof MtxColetaMaterialTmo)) {
                 return false;
          }
          MtxColetaMaterialTmo other = (MtxColetaMaterialTmo) obj;
          EqualsBuilder umEqualsBuilder = new EqualsBuilder();
          umEqualsBuilder.append(this.getSeq(), other.getSeq());
          umEqualsBuilder.append(this.getPacCodigo(), other.getPacCodigo());
          return umEqualsBuilder.isEquals();
    }

}