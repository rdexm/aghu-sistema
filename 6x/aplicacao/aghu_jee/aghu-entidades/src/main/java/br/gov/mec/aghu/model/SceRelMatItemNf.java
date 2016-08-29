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
@Table(name = "SCE_REL_MAT_ITENS_NF", schema = "AGH")
public class SceRelMatItemNf extends BaseEntityId<SceRelMatItemNfId> implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4852920807999409946L;
	private SceRelMatItemNfId id;
	private Integer version;
	private ScoFornecedor scoFornecedor;
	private ScoMaterial scoMaterial;
	private String observacao;

	public SceRelMatItemNf() {
	}

	public SceRelMatItemNf(SceRelMatItemNfId id, ScoFornecedor scoFornecedor, ScoMaterial scoMaterial) {
		this.id = id;
		this.scoFornecedor = scoFornecedor;
		this.scoMaterial = scoMaterial;
	}

	public SceRelMatItemNf(SceRelMatItemNfId id, ScoFornecedor scoFornecedor, ScoMaterial scoMaterial, String observacao) {
		this.id = id;
		this.scoFornecedor = scoFornecedor;
		this.scoMaterial = scoMaterial;
		this.observacao = observacao;
	}

	@EmbeddedId
	@AttributeOverrides({ @AttributeOverride(name = "frnNumero", column = @Column(name = "FRN_NUMERO", nullable = false)),
			@AttributeOverride(name = "matCodigo", column = @Column(name = "MAT_CODIGO", nullable = false)),
			@AttributeOverride(name = "codigoMatNf", column = @Column(name = "CODIGO_MAT_NF", nullable = false, length = 14)) })
	public SceRelMatItemNfId getId() {
		return this.id;
	}

	public void setId(SceRelMatItemNfId id) {
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
	@JoinColumn(name = "FRN_NUMERO", nullable = false, insertable = false, updatable = false)
	public ScoFornecedor getScoFornecedor() {
		return this.scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", nullable = false, insertable = false, updatable = false)
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}

	@Column(name = "OBSERVACAO", length = 240)
	@Length(max = 240)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public enum Fields {

		ID("id"),
		VERSION("version"),
		SCO_FORNECEDOR("scoFornecedor"),
		SCO_MATERIAL("scoMaterial"),
		OBSERVACAO("observacao");

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
		if (!(obj instanceof SceRelMatItemNf)) {
			return false;
		}
		SceRelMatItemNf other = (SceRelMatItemNf) obj;
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
