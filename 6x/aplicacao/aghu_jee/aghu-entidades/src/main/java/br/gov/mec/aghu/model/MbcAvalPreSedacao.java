package br.gov.mec.aghu.model;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import org.hibernate.annotations.Parameter;

import br.gov.mec.aghu.dominio.DominioAsa;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_AVAL_PRE_SEDACAO", schema = "AGH")
public class MbcAvalPreSedacao extends BaseEntityId<MbcAvalPreSedacaoId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4496216293309912316L;
	
	private MbcAvalPreSedacaoId id;
	private MbcDescricaoCirurgica mbcDescricaoCirurgica;
	private PdtViaAereas viaAereas;
	private Boolean indParticAvalCli;
	private String avaliacaoClinica;
	private RapServidores rapServidores;
	private DominioAsa asa;
	private Short tempoJejum;
	private String exameFisico;
	private String comorbidades;
	private Date criadoEm;
	private Integer version;

	public MbcAvalPreSedacao(){		
	} 

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "dcgCrgSeq", column = @Column(name = "DCG_CRG_SEQ", nullable = false)),
			@AttributeOverride(name = "dcgSeqp", column = @Column(name = "DCG_SEQP", nullable = false)) })
	
	public MbcAvalPreSedacaoId getId() {
		return id;
	}

	public void setId(MbcAvalPreSedacaoId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "DCG_CRG_SEQ", referencedColumnName = "CRG_SEQ",insertable=false, updatable=false),
			@JoinColumn(name = "DCG_SEQP", referencedColumnName = "SEQP",insertable=false, updatable=false)})
	public MbcDescricaoCirurgica getMbcDescricaoCirurgica() {
		return mbcDescricaoCirurgica;
	}

	public void setMbcDescricaoCirurgica(MbcDescricaoCirurgica mbcDescricaoCirurgica) {
		this.mbcDescricaoCirurgica = mbcDescricaoCirurgica;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "DVA_SEQ")
	public PdtViaAereas getViaAereas() {
		return viaAereas;
	}

	public void setViaAereas(PdtViaAereas viaAereas) {
		this.viaAereas = viaAereas;
	} 

	@Column(name = "IND_PARTIC_AVAL_CLI", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndParticAvalCli() {
		return indParticAvalCli;
	}

	public void setIndParticAvalCli(Boolean indParticAvalCli) {
		this.indParticAvalCli = indParticAvalCli;
	}
	
	
	@Column(name = "AVALIACAO_CLINICA", length = 500)
	public String getAvaliacaoClinica() {
		return this.avaliacaoClinica;
	}

	public void setAvaliacaoClinica(String avaliacaoClinica) {
		this.avaliacaoClinica = avaliacaoClinica;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getRapServidores() {
		return this.rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}
	
	@Column(name = "ASA")
	@org.hibernate.annotations.Type(parameters = { @Parameter(name = "enumClassName", value = "br.gov.mec.aghu.dominio.DominioAsa") }, type = "br.gov.mec.aghu.core.model.jpa.DominioUserType")
	public DominioAsa getAsa() {
		return this.asa;
	}

	public void setAsa(DominioAsa asa) {
		this.asa = asa;
	}	
	
	@Column(name = "TEMPO_JEJUM", length = 1)
	public Short getTempoJejum() {
		return tempoJejum;
	}

	public void setTempoJejum(Short tempoJejum) {
		this.tempoJejum = tempoJejum;
	}
	

	@Column(name = "COMORBIDADES", length = 500)
	public String getComorbidades() {
		return this.comorbidades;
	}

	public void setComorbidades(String comorbidades) {
		this.comorbidades = comorbidades;
	}

	
	@Column(name = "EXAME_FISICO", length = 500)
	public String getExameFisico() {
		return this.exameFisico;
	}

	public void setExameFisico(String exameFisico) {
		this.exameFisico = exameFisico;
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
		ID("id"),
		CRG_SEQ("id.dcgCrgSeq"),
		DGC_SEQP("id.dcgSeqp"),
		VERSION("version"),
		PDT_DESCRICAO("pdtDescricao"),
		IND_PARTIC_AVAL_CLI("indParticAvalCli"),
		RAP_SERVIDORES("rapServidores"),
		ASA("asa"),
		TEMPO_JEJUM("tempoJejum"),
		EXAME_FISICO("exameFisico"),
		COMORBIDADES("comorbidades"),
		VIAS_AEREAS("viaAereas"),
		CRIADO_EM("criadoEm");


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
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
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
		if (!(obj instanceof MbcAvalPreSedacao)) {
			return false;
		}
		MbcAvalPreSedacao other = (MbcAvalPreSedacao) obj;
		if (getId() == null) {
			if (other.getId() != null) {
				return false;
			}
		} else if (!getId().equals(other.getId())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}

