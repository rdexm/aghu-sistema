package br.gov.mec.aghu.model;

// Generated 15/03/2011 13:46:58 by Hibernate Tools 3.2.5.Beta

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
@Table(name = "FAT_CONTA_PAC_TRANSPLANTES", schema = "AGH")
public class FatContaPacTransplante extends BaseEntityId<FatContaPacTransplanteId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8635552253344480322L;
	private FatContaPacTransplanteId id;

	public FatContaPacTransplante() {
	}

	public FatContaPacTransplante(FatContaPacTransplanteId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "ptrPacCodigo", column = @Column(name = "PTR_PAC_CODIGO", nullable = false, precision = 8, scale = 0)),
			@AttributeOverride(name = "ptrTtrCodigo", column = @Column(name = "PTR_TTR_CODIGO", nullable = false, length = 10)),
			@AttributeOverride(name = "ptrDtInscricaoTransplante", column = @Column(name = "PTR_DT_INSCRICAO_TRANSPLANTE", nullable = false, length = 7)),
			@AttributeOverride(name = "cthSeq", column = @Column(name = "CTH_SEQ", nullable = false, precision = 8, scale = 0)) })
	public FatContaPacTransplanteId getId() {
		return this.id;
	}

	public void setId(FatContaPacTransplanteId id) {
		this.id = id;
	}

	public enum Fields {

		ID("id");

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
		if (!(obj instanceof FatContaPacTransplante)) {
			return false;
		}
		FatContaPacTransplante other = (FatContaPacTransplante) obj;
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
