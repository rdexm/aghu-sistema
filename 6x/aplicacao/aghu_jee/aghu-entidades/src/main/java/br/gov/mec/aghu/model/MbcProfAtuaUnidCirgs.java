package br.gov.mec.aghu.model;

// Generated 22/03/2010 14:51:58 by Hibernate Tools 3.2.5.Beta

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

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
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * MbcProfAtuaUnidCirgs generated by hbm2java
 */

@Entity
@Table(name = "MBC_PROF_ATUA_UNID_CIRGS", schema = "AGH")
public class MbcProfAtuaUnidCirgs extends BaseEntityId<MbcProfAtuaUnidCirgsId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6743710036148214667L;
	private MbcProfAtuaUnidCirgsId id;
	private MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs;
	private AghUnidadesFuncionais unidadeFuncional;
	private RapServidores servidorCadastrado;
	private RapServidores servidorAlteradoPor;
	private DominioSituacao situacao;
	private Date criadoEm;
	private Set<MbcProfCirurgias> mbcProfCirurgiases = new HashSet<MbcProfCirurgias>(
			0);
	private Set<MbcProfAtuaUnidCirgs> mbcProfAtuaUnidCirgses = new HashSet<MbcProfAtuaUnidCirgs>(
			0);
	
	private Set<MbcCaractSalaEsp> mbcCaractSalaEsp = new HashSet<MbcCaractSalaEsp>(
			0);
	
	private RapServidores rapServidores;
	private DominioFuncaoProfissional indFuncaoProf;
	
	public MbcProfAtuaUnidCirgs() {
	}

	public MbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgsId id,
			AghUnidadesFuncionais unidadeFuncional,
			RapServidores servidorCadastrado, DominioSituacao situacao, Date criadoEm) {
		this.id = id;
		this.unidadeFuncional = unidadeFuncional;
		this.servidorCadastrado = servidorCadastrado;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
	}

	public MbcProfAtuaUnidCirgs(MbcProfAtuaUnidCirgsId id,
			MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs,
			AghUnidadesFuncionais unidadeFuncional,
			RapServidores servidorCadastrado,
			RapServidores servidorAlteradoPor,
			DominioSituacao situacao, Date criadoEm,
			Set<MbcProfCirurgias> mbcProfCirurgiases,
			Set<MbcProfAtuaUnidCirgs> mbcProfAtuaUnidCirgses) {
		this.id = id;
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
		this.unidadeFuncional = unidadeFuncional;
		this.servidorCadastrado = servidorCadastrado;
		this.servidorAlteradoPor = servidorAlteradoPor;
		this.situacao = situacao;
		this.criadoEm = criadoEm;
		this.mbcProfCirurgiases = mbcProfCirurgiases;
		this.mbcProfAtuaUnidCirgses = mbcProfAtuaUnidCirgses;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "serMatricula", column = @Column(name = "SER_MATRICULA", nullable = false, precision = 7, scale = 0)),
			@AttributeOverride(name = "serVinCodigo", column = @Column(name = "SER_VIN_CODIGO", nullable = false, precision = 3, scale = 0)),
			@AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "indFuncaoProf", column = @Column(name = "IND_FUNCAO_PROF", nullable = false, length = 3)) })
	public MbcProfAtuaUnidCirgsId getId() {
		return this.id;
	}

	public void setId(MbcProfAtuaUnidCirgsId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "PUC_SER_MATRICULA", referencedColumnName = "SER_MATRICULA"),
			@JoinColumn(name = "PUC_SER_VIN_CODIGO", referencedColumnName = "SER_VIN_CODIGO"),
			@JoinColumn(name = "PUC_UNF_SEQ", referencedColumnName = "UNF_SEQ"),
			@JoinColumn(name = "PUC_IND_FUNCAO_PROF", referencedColumnName = "IND_FUNCAO_PROF") })
	public MbcProfAtuaUnidCirgs getMbcProfAtuaUnidCirgs() {
		return this.mbcProfAtuaUnidCirgs;
	}

	public void setMbcProfAtuaUnidCirgs(
			MbcProfAtuaUnidCirgs mbcProfAtuaUnidCirgs) {
		this.mbcProfAtuaUnidCirgs = mbcProfAtuaUnidCirgs;
	}
	
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = false, updatable = false, insertable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_E_CADASTRADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_E_CADASTRADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorCadastrado() {
		return servidorCadastrado;
	}
	
	public void setServidorCadastrado(RapServidores servidorCadastrado) {
		this.servidorCadastrado = servidorCadastrado;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA_ALTERADO", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_ALTERADO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorAlteradoPor() {
		return servidorAlteradoPor;
	}
	
	public void setServidorAlteradoPor(RapServidores servidorAlteradoPor) {
		this.servidorAlteradoPor = servidorAlteradoPor;
	}

	@Column(name = "SITUACAO", nullable = false, length = 1)
	@Enumerated(EnumType.STRING)
	public DominioSituacao getSituacao() {
		return this.situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "CRIADO_EM", nullable = false, length = 29)
	public Date getCriadoEm() {
		return this.criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcProfAtuaUnidCirgs")
	public Set<MbcProfCirurgias> getMbcProfCirurgiases() {
		return this.mbcProfCirurgiases;
	}

	public void setMbcProfCirurgiases(Set<MbcProfCirurgias> mbcProfCirurgiases) {
		this.mbcProfCirurgiases = mbcProfCirurgiases;
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcProfAtuaUnidCirgs")
	public Set<MbcProfAtuaUnidCirgs> getMbcProfAtuaUnidCirgses() {
		return this.mbcProfAtuaUnidCirgses;
	}

	public void setMbcProfAtuaUnidCirgses(
			Set<MbcProfAtuaUnidCirgs> mbcProfAtuaUnidCirgses) {
		this.mbcProfAtuaUnidCirgses = mbcProfAtuaUnidCirgses;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( { @JoinColumn(name = "SER_MATRICULA", updatable = false, insertable = false),
			@JoinColumn(name = "SER_VIN_CODIGO", updatable = false, insertable = false) })
	public RapServidores getRapServidores() {
		return rapServidores;
	}

	public void setRapServidores(RapServidores rapServidores) {
		this.rapServidores = rapServidores;
	}
	
	@Column(name = "IND_FUNCAO_PROF", nullable = false, insertable = false, updatable = false, length = 3)
	@Enumerated(EnumType.STRING)
	public DominioFuncaoProfissional getIndFuncaoProf() {
		return this.indFuncaoProf;
	}

	public void setIndFuncaoProf(DominioFuncaoProfissional indFuncaoProf) {
		this.indFuncaoProf = indFuncaoProf;
	}
	
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "mbcProfAtuaUnidCirgs")
	public Set<MbcCaractSalaEsp> getMbcCaractSalaEsp() {
		return mbcCaractSalaEsp;
	}

	public void setMbcCaractSalaEsp(Set<MbcCaractSalaEsp> mbcCaractSalaEsp) {
		this.mbcCaractSalaEsp = mbcCaractSalaEsp;
	}

	public enum Fields {
		IND_FUNCAO_PROF("id.indFuncaoProf"), 
		RAP_SERVIDOR("rapServidores"),
		SER_MATRICULA("id.serMatricula"),
		SER_VIN_CODIGO("id.serVinCodigo"),
		FUNCAO("indFuncaoProf"),
		UNF_SEQ("id.unfSeq"),
		UNIDADE_FUNCIONAL("unidadeFuncional"),
		SITUACAO("situacao"),
		PROF_ATUA_UNID_CIRGS("mbcProfAtuaUnidCirgs"),
		PROF_CIRURGIAS("mbcProfCirurgiases"),
		CARACT_SALA_ESP("mbcCaractSalaEsp");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
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
		if (!(obj instanceof MbcProfAtuaUnidCirgs)) {
			return false;
		}
		MbcProfAtuaUnidCirgs other = (MbcProfAtuaUnidCirgs) obj;
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
	
	@Transient
	public String getFuncaoMatriculaNome() {
		return this.getIndFuncaoProf().getCodigo() + " - " 
			+ (this.getId().getSerMatricula() != null ? this.getId().getSerMatricula() : "") + " - " 
			+ (this.getRapServidores().getPessoaFisica().getNome() != null ? this.getRapServidores().getPessoaFisica().getNome() : "");
	}

	@Override
	public String toString() {
		return "MbcProfAtuaUnidCirgs [getId()=" + getId() + "]";
	}

}
