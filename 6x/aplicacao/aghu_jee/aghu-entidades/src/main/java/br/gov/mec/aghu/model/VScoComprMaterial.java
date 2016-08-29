package br.gov.mec.aghu.model;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "V_SCO_COMPR_MATERIAL", schema = "AGH")
@Immutable
public class VScoComprMaterial extends BaseEntityId<VScoComprMaterialId> implements java.io.Serializable {

	private static final long serialVersionUID = 5331032322776441593L;

	private VScoComprMaterialId id;
	
	public VScoComprMaterial() {
	}

	public VScoComprMaterial(VScoComprMaterialId id) {
		super();
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides( {
			@AttributeOverride(name = "codigoMaterial", column = @Column(name = "MAT_CODIGO")),
			@AttributeOverride(name = "dtGeracaoMovto", column = @Column(name = "DT_GERACAO_MOVTO")),
			@AttributeOverride(name = "numeroFrn", column = @Column(name = "FRN_NUMERO")),
			@AttributeOverride(name = "numeroAfn", column = @Column(name = "AFN_NUMERO")),
			@AttributeOverride(name = "dtGeracaoNr", column = @Column(name = "DT_GERACAO_NR")),
			@AttributeOverride(name = "numeroSlc", column = @Column(name = "SLC_NUMERO")),
			@AttributeOverride(name = "dtAberturaProposta", column = @Column(name = "DT_ABERTURA_PROPOSTA")),
			@AttributeOverride(name = "nrsSeq", column = @Column(name = "NRS_SEQ")),
			@AttributeOverride(name = "numeroLct", column = @Column(name = "LCT_NUMERO")),
			@AttributeOverride(name = "valor", column = @Column(name = "VALOR")),
			@AttributeOverride(name = "quantidade", column = @Column(name = "QUANTIDADE")),
			@AttributeOverride(name = "formaPagamento", column = @Column(name = "FORMA_PAG")),
			@AttributeOverride(name = "custoUnitario", column = @Column(name = "CUSTO_UNITARIO")),
			@AttributeOverride(name = "dfeSeq", column = @Column(name = "DFE_SEQ")),
			@AttributeOverride(name = "numeroComplemento", column = @Column(name = "NRO_COMPLEMENTO")),
			@AttributeOverride(name = "numeroDfe", column = @Column(name = "DFE_NUMERO")),
			@AttributeOverride(name = "mcmCodigo", column = @Column(name = "MCM_CODIGO")),
			@AttributeOverride(name = "ncMcmCodigo", column = @Column(name = "NC_MCM_CODIGO")),
			@AttributeOverride(name = "ncNumero", column = @Column(name = "NC_NUMERO")) })
	public VScoComprMaterialId getId() {
		return this.id;
	}

	public void setId(VScoComprMaterialId id) {
		this.id = id;
	}
	
public enum Fields {
		
		ID("id"),
		MAT_CODIGO ("id.codigoMaterial"),
		DT_GERACAO_MOVTO ("id.dtGeracaoMovto"),
		FRN_NUMERO ("id.numeroFrn"),
		AFN_NUMERO ("id.numeroAfn"),
		DT_GERACAO_NR ("id.dtGeracaoNr"),
		SLC_NUMERO ("id.numeroSlc"),
		DT_ABERTURA_PROPOSTA ("id.dtAberturaProposta"),
		NRS_SEQ ("id.nrsSeq"),
		LCT_NUMERO ("id.numeroLct"),
		VALOR ("id.valor"),
		QUANTIDADE ("id.quantidade"),
		FORMA_PAG ("id.formaPagamento"),
		CUSTO_UNITARIO ("id.custoUnitario"),
		DFE_SEQ ("id.dfeSeq"),
		NRO_COMPLEMENTO ("id.numeroComplemento"),
		DFE_NUMERO ("id.numeroDfe"),
		MCM_CODIGO ("id.mcmCodigo"),
		NC_MCM_CODIGO ("id.ncMcmCodigo"),
		NC_NUMERO ("id.ncNumero");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
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
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof VScoComprMaterial)) {
			return false;
		}
		VScoComprMaterial other = (VScoComprMaterial) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

}
