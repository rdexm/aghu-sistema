package br.gov.mec.aghu.model;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


@Entity
@Table(name = "AGH_CARACT_UNID_FUNCIONAIS", schema = "AGH")
public class AghCaractUnidFuncionais extends BaseEntityId<AghCaractUnidFuncionaisId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2269764584022817020L;

	/**
	 * 
	 */
	private AghCaractUnidFuncionaisId id;

	/**
	 * 
	 */
	private RapServidores servidor;
	/**
	 * 
	 */
	private AghUnidadesFuncionais unidadeFuncional;
	
	private Integer version;
	
	public AghCaractUnidFuncionais() {
	}

	public AghCaractUnidFuncionais(AghCaractUnidFuncionaisId id,
			Integer serMatricula, Short serVinCodigo) {
		this.id = id;

	}

	public AghCaractUnidFuncionais(
			AghCaractUnidFuncionaisId aghCaractUnidFuncionaisId) {
		this.id = aghCaractUnidFuncionaisId;
	}
	
	public AghCaractUnidFuncionais(AghCaractUnidFuncionaisId id, AghUnidadesFuncionais unidadeFuncional,
			RapServidores servidor) {
		super();
		this.id = id;
		this.servidor = servidor;
		this.unidadeFuncional = unidadeFuncional;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "unfSeq", column = @Column(name = "UNF_SEQ", nullable = false, precision = 4, scale = 0)),
			@AttributeOverride(name = "caracteristica", column = @Column(name = "CARACTERISTICA", nullable = false, length = 30)) })
	public AghCaractUnidFuncionaisId getId() {
		return this.id;
	}

	public void setId(AghCaractUnidFuncionaisId id) {
		this.id = id;
		// Necessário para a operação de insert, quando o objeto unidade funcional é atualizado mas não o unfSeq do id.
		if(id != null && id.getUnfSeq() == null && unidadeFuncional != null && unidadeFuncional.getSeq() != null){
			this.id.setUnfSeq(unidadeFuncional.getSeq());
		}
	}

	/**
	 * @return the unidadeFuncional
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UNF_SEQ", nullable = false, insertable = false, updatable = false)
	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	/**
	 * @param unidadeFuncional
	 *            the unidadeFuncional to set
	 */
	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	/**
	 * @return the servidor
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	/**
	 * @param servidor
	 *            the servidor to set
	 */
	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Transient
	public String getSiglaUnidadeFuncional() {
		return unidadeFuncional.getSigla();
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}		
	

	public enum Fields {
		ID("id"),
		UNF_SEQ("id.unfSeq"), 
		DESCRICAO_CARACTERISTICA("id.caracteristica"), 
		UNIDADE_FUNCIONAL("unidadeFuncional");

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
		if (!(obj instanceof AghCaractUnidFuncionais)) {
			return false;
		}
		AghCaractUnidFuncionais other = (AghCaractUnidFuncionais) obj;
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