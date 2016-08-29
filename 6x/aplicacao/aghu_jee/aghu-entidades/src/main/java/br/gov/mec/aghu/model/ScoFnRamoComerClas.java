package br.gov.mec.aghu.model;

// Generated 09/04/2012 16:32:53 by Hibernate Tools 3.4.0.CR1

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
import javax.persistence.Version;

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
@Table(name = "SCO_FN_RAMO_COMER_CLAS", schema = "AGH")
public class ScoFnRamoComerClas extends BaseEntityId<ScoFnRamoComerClasId> implements java.io.Serializable {

	private static final long serialVersionUID = -6322440440002714789L;
	private ScoFnRamoComerClasId id;
	private Integer version;
	private ScoFornRamoComercial scoFornRamoComercial;
	private ScoClassifMatNiv5 scoClassifMatNiv5;

	public ScoFnRamoComerClas() {
	}

	public ScoFnRamoComerClas(ScoFnRamoComerClasId id, ScoFornRamoComercial scoFornRamoComercial,
			ScoClassifMatNiv5 scoClassifMatNiv5) {
		this.id = id;
		this.scoFornRamoComercial = scoFornRamoComercial;
		this.scoClassifMatNiv5 = scoClassifMatNiv5;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "cn5Numero", column = @Column(name = "CN5_NUMERO", nullable = false)),
			@AttributeOverride(name = "frmFrnNumero", column = @Column(name = "FRM_FRN_NUMERO", nullable = false)),
			@AttributeOverride(name = "frmRcmCodigo", column = @Column(name = "FRM_RCM_CODIGO", nullable = false)) })
	public ScoFnRamoComerClasId getId() {
		return this.id;
	}

	public void setId(ScoFnRamoComerClasId id) {
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
	@JoinColumns({
			@JoinColumn(name = "FRM_FRN_NUMERO", referencedColumnName = "FRN_NUMERO", nullable = false, insertable = false, updatable = false),
			@JoinColumn(name = "FRM_RCM_CODIGO", referencedColumnName = "RCM_CODIGO", nullable = false, insertable = false, updatable = false) })
	public ScoFornRamoComercial getScoFornRamoComercial() {
		return this.scoFornRamoComercial;
	}

	public void setScoFornRamoComercial(ScoFornRamoComercial scoFornRamoComercial) {
		this.scoFornRamoComercial = scoFornRamoComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CN5_NUMERO", nullable = false, insertable = false, updatable = false)
	public ScoClassifMatNiv5 getScoClassifMatNiv5() {
		return this.scoClassifMatNiv5;
	}

	public void setScoClassifMatNiv5(ScoClassifMatNiv5 scoClassifMatNiv5) {
		this.scoClassifMatNiv5 = scoClassifMatNiv5;
	}

	public enum Fields {

		ID("id"),
		ID_FRMFRNNUMERO("id.frmFrnNumero"),
		ID_FRMRCMCODIGO("id.frmRcmCodigo"),
		ID_CN5NUMERO("id.cn5Numero"),
		VERSION("version"),
		SCO_FORN_RAMOS_COMERCIAIS("scoFornRamoComercial"),
		SCO_NUM_FORN("id.frmFrnNumero"),
		SCO_RCM_CODIGO("id.frmRcmCodigo"),
		SCO_CLASSIF_MAT_NIV5("scoClassifMatNiv5");

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
		if (!(obj instanceof ScoFnRamoComerClas)) {
			return false;
		}
		ScoFnRamoComerClas other = (ScoFnRamoComerClas) obj;
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
