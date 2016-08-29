package br.gov.mec.aghu.model;

// Generated 30/08/2010 12:38:37 by Hibernate Tools 3.2.6.CR1

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;


import org.hibernate.annotations.Immutable;

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
@Table(name = "AGH_PERFIS_PARAMETRO", schema = "AGH")
public class AghPerfilParametro extends BaseEntityId<AghPerfilParametroId> implements java.io.Serializable {

	private static final long serialVersionUID = -5311338435896081969L;
	
	private AghPerfilParametroId id;

	public AghPerfilParametro() {
	}

	public AghPerfilParametro(AghPerfilParametroId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "psiSeq", column = @Column(name = "PSI_SEQ", nullable = false, precision = 6, scale = 0)),
			@AttributeOverride(name = "perfil", column = @Column(name = "PERFIL", nullable = false, length = 30)) })
	public AghPerfilParametroId getId() {
		return this.id;
	}

	public void setId(AghPerfilParametroId id) {
		this.id = id;
	}
	
	public enum Fields {
		PSI_SEQ("id.psiSeq"), PERFIL("id.perfil");

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
		if (!(obj instanceof AghPerfilParametro)) {
			return false;
		}
		AghPerfilParametro other = (AghPerfilParametro) obj;
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
