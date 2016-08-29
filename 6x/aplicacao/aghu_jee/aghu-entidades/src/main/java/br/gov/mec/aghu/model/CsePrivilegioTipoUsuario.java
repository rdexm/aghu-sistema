package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;


import org.hibernate.annotations.Immutable;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * ================================================================================
 *   ####   #####    ####   ######  #####   ##  ##   ####    ####    ####    #### 
 *  ##  ##  ##  ##  ##      ##      ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##  ##
 *  ##  ##  #####    ####   ####    #####   ##  ##  ######  ##      ######  ##  ##
 *  ##  ##  ##  ##      ##  ##      ##  ##   ####   ##  ##  ##  ##  ##  ##  ##  ##
 *   ####   #####    ####   ######  ##  ##    ##    ##  ##   ####   ##  ##   #### 
 * ================================================================================
 *
 * A partir de uma análise originada pela tarefa #19993
 * esta model foi escolhida para ser apenas de leitura
 * no AGHU e por isso foi anotada como Immutable.
 *
 * Entretanto, caso esta entidade seja necessária na construção
 * de uma estória que necessite escrever dados no banco, este
 * comentário e esta anotação pode ser retirada desta model.
 */
@Immutable

@Entity
@Table(name = "CSE_PRIVILEGIOS_TIPO_USUARIO", schema = "AGH")
public class CsePrivilegioTipoUsuario extends BaseEntityId<CsePrivilegiosTipoUsuarioId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7192415787208525047L;
	private CsePrivilegiosTipoUsuarioId id;
	private Integer version;
	private CsePrivilegio csePrivilegio;
	private CseTipoUsuario cseTipoUsuario;
	private String administrador;

	public CsePrivilegioTipoUsuario() {
	}

	public CsePrivilegioTipoUsuario(CsePrivilegiosTipoUsuarioId id, CsePrivilegio csePrivilegio, CseTipoUsuario cseTipoUsuario,
			String administrador) {
		this.id = id;
		this.csePrivilegio = csePrivilegio;
		this.cseTipoUsuario = cseTipoUsuario;
		this.administrador = administrador;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "prvSeq", column = @Column(name = "PRV_SEQ", nullable = false)),
			@AttributeOverride(name = "tpuCodigo", column = @Column(name = "TPU_CODIGO", nullable = false, length = 2)) })
	public CsePrivilegiosTipoUsuarioId getId() {
		return this.id;
	}

	public void setId(CsePrivilegiosTipoUsuarioId id) {
		this.id = id;
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
	@JoinColumn(name = "PRV_SEQ", nullable = false, insertable = false, updatable = false)
	public CsePrivilegio getCsePrivilegio() {
		return this.csePrivilegio;
	}

	public void setCsePrivilegio(CsePrivilegio csePrivilegio) {
		this.csePrivilegio = csePrivilegio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "TPU_CODIGO", nullable = false, insertable = false, updatable = false)
	public CseTipoUsuario getCseTipoUsuario() {
		return this.cseTipoUsuario;
	}

	public void setCseTipoUsuario(CseTipoUsuario cseTipoUsuario) {
		this.cseTipoUsuario = cseTipoUsuario;
	}

	@Column(name = "ADMINISTRADOR", nullable = false, length = 1)
	@Length(max = 1)
	public String getAdministrador() {
		return this.administrador;
	}

	public void setAdministrador(String administrador) {
		this.administrador = administrador;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		CSE_PRIVILEGIOS("csePrivilegio"),
		CSE_TIPOS_USUARIO("cseTipoUsuario"),
		ADMINISTRADOR("administrador");

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
		if (!(obj instanceof CsePrivilegioTipoUsuario)) {
			return false;
		}
		CsePrivilegioTipoUsuario other = (CsePrivilegioTipoUsuario) obj;
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
