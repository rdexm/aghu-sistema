package br.gov.mec.aghu.model;

// Generated 19/03/2010 17:25:07 by Hibernate Tools 3.2.5.Beta

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "MBC_PROF_CIRURGIAS", schema = "AGH")
public class MbcProfCirurgias extends BaseEntityId<MbcProfCirurgiasId> implements java.io.Serializable {

	private static final long serialVersionUID = 3541710452172453572L;
	private MbcProfCirurgiasId id;
	private Boolean indResponsavel;
	private Boolean indRealizou;
	private Boolean indInclEscala;
	private Date criadoEm;
	private RapServidores servidor;
	private RapServidores servidorPuc;
	private DominioFuncaoProfissional funcaoProfissional;
	private MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs;
	private MbcCirurgias cirurgia;
	private AghUnidadesFuncionais unidadeFuncional;
	private MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgsVinc;
	private Integer version;

	public MbcProfCirurgias() {
	}

	public MbcProfCirurgias(MbcProfCirurgiasId id, Boolean indResponsavel,
			Boolean indRealizou, Boolean indInclEscala) {
		this.id = id;
		this.indResponsavel = indResponsavel;
		this.indRealizou = indRealizou;
		this.indInclEscala = indInclEscala;
	}

	public MbcProfCirurgias(MbcProfCirurgiasId id, Boolean indResponsavel,
			Boolean indRealizou, Boolean indInclEscala, Date criadoEm,
			Integer serMatricula, Short serVinCodigo) {
		this.id = id;
		this.indResponsavel = indResponsavel;
		this.indRealizou = indRealizou;
		this.indInclEscala = indInclEscala;
		this.criadoEm = criadoEm;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "crgSeq", column = @Column(name = "CRG_SEQ", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "pucSerMatricula", column = @Column(name = "PUC_SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "pucSerVinCodigo", column = @Column(name = "PUC_SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "pucUnfSeq", column = @Column(name = "PUC_UNF_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "pucIndFuncaoProf", column = @Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3)) })
	public MbcProfCirurgiasId getId() {
		return this.id;
	}

	public void setId(MbcProfCirurgiasId id) {
		this.id = id;
	}

	@Column(name = "IND_RESPONSAVEL", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndResponsavel() {
		return this.indResponsavel;
	}

	public void setIndResponsavel(Boolean indResponsavel) {
		this.indResponsavel = indResponsavel;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "CRIADO_EM", length = 7)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PUC_SER_MATRICULA", referencedColumnName = "MATRICULA", insertable = false, updatable = false, nullable = false),
			@JoinColumn(name = "PUC_SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", insertable = false, updatable = false, nullable = false) })	
	public RapServidores getServidorPuc() {
		return servidorPuc;
	}

	public void setServidorPuc(RapServidores servidorPuc) {
		this.servidorPuc = servidorPuc;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PUC_SER_MATRICULA", referencedColumnName = "SER_MATRICULA", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUC_SER_VIN_CODIGO", referencedColumnName = "SER_VIN_CODIGO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUC_UNF_SEQ", referencedColumnName = "UNF_SEQ", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "PUC_IND_FUNCAO_PROF", referencedColumnName = "IND_FUNCAO_PROF", nullable = false, insertable = false, updatable = false) })
	public MbcProfAtuaUnidCirgs getMbcProfAtuaUnidCirgs() {
		return this.mbcProfAtuaUnidCirgs;
	}

	public void setMbcProfAtuaUnidCirgs(
			MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) {
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
	}
	
	@Column(name = "PUC_IND_FUNCAO_PROF", nullable = false, length = 3, insertable = false, updatable = false)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getFuncaoProfissional() {
		return this.funcaoProfissional;
	}
	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CRG_SEQ", referencedColumnName="SEQ",nullable = false, insertable = false, updatable = false)	
	public MbcCirurgias getCirurgia() {
		return cirurgia;
	}
	
	public void setCirurgia(MbcCirurgias cirurgia) {
		this.cirurgia = cirurgia;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PUC_UNF_SEQ", referencedColumnName="SEQ",nullable = false, insertable = false, updatable = false)	
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}
	
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PUC_SER_MATRICULA_VINC", referencedColumnName = "SER_MATRICULA"),
			@JoinColumn(name = "PUC_SER_VIN_CODIGO_VINC", referencedColumnName = "SER_VIN_CODIGO"),
			@JoinColumn(name = "PUC_UNF_SEQ_VINC", referencedColumnName = "UNF_SEQ"),
			@JoinColumn(name = "PUC_IND_FUNCAO_PROF_VINC", referencedColumnName = "IND_FUNCAO_PROF") })
	public MbcProfAtuaUnidCirgs getMbcProfAtuaUnidCirgsVinc() {
		return mbcProfAtuaUnidCirgsVinc;
	}
	
	public void setMbcProfAtuaUnidCirgsVinc(MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgsVinc) {
		this.mbcProfAtuaUnidCirgsVinc = mbcProfAtuaUnidCirgsVinc;
	}

	public enum Fields {
		CIRURGIA("cirurgia"), 
		IND_RESPONSAVEL("indResponsavel"), 
		ID("id"), 
		UNID_CIRG("mbcProfAtuaUnidCirgs"), 
		PUC_SER_MATRICULA("id.pucSerMatricula"),
		PUC_SER_VIN_CODIGO("id.pucSerVinCodigo"),
		PUC_UNF_SEQ("id.pucUnfSeq"),
		PUC_IND_FUNCAO_PROF("id.pucIndFuncaoProf"),
		FUNCAO_PROFISSIONAL("funcaoProfissional"),
		SERVIDOR("servidor"),
		SERVIDOR_PUC("servidorPuc"),
		CRG_SEQ("id.crgSeq"), 
		IND_INCLUI_ESCALA("indInclEscala"),
		MBC_PROF_ATUA_UNID_CIRGS_VINC("mbcProfAtuaUnidCirgsVinc"),
		IND_REALIZOU("indRealizou"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		MBC_PROF_ATUA_UNID_CIRGS("mbcProfAtuaUnidCirgs");

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
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		MbcProfCirurgias other = (MbcProfCirurgias) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}

	@Column(name = "IND_REALIZOU", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRealizou() {
		return indRealizou;
	}

	public void setIndRealizou(Boolean indRealizou) {
		this.indRealizou = indRealizou;
	}

	@Column(name = "IND_INCL_ESCALA", length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndInclEscala() {
		return indInclEscala;
	}

	public void setIndInclEscala(Boolean indInclEscala) {
		this.indInclEscala = indInclEscala;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}	
	
}
