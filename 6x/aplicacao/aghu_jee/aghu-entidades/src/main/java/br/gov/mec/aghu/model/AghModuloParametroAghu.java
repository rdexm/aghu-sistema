package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
@Table(name = "AGH_MODULOS_PARAMETROS_AGHU", schema = "AGH")
public class AghModuloParametroAghu extends BaseEntityId<AghModuloParametroAghuId> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8428597833991732306L;

	private AghModuloParametroAghuId id;
	
	private AghParametros parametro;
	
	private AghModuloAghu modulo;
	
	public AghModuloParametroAghu() {
		
	}
	
	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "seqParametro", column = @Column(name = "SEQ_PARAMETRO", nullable = false)),
			@AttributeOverride(name = "seqModuloAghu", column = @Column(name = "SEQ_MODULO_AGHU", nullable = false)) })
	public AghModuloParametroAghuId getId() {
		return this.id;
	}

	public void setId(AghModuloParametroAghuId id) {
		this.id = id;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_PARAMETRO", nullable = false, insertable=false, updatable=false)
	public AghParametros getParametro() {
		return parametro;
	}

	public void setParametro(AghParametros parametro) {
		this.parametro = parametro;
	}
		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SEQ_MODULO_AGHU", nullable = false, insertable=false, updatable=false)
	public AghModuloAghu getModulo() {
		return modulo;
	}

	public void setModulo(AghModuloAghu modulo) {
		this.modulo = modulo;
	}


	public enum Fields {
		CODIGO_PARAMETRO("id.seqParametro"),
		CODIGO_MODULO("id.seqModuloAghu"),
		PARAMETRO("parametro"),
		MODULO("modulo");
		
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
		if (!(obj instanceof AghModuloParametroAghu)) {
			return false;
		}
		AghModuloParametroAghu other = (AghModuloParametroAghu) obj;
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
