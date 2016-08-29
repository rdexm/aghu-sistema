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
import javax.persistence.Version;


import org.apache.commons.lang3.builder.EqualsBuilder;

import br.gov.mec.aghu.core.persistence.BaseEntityId;

@Entity
@Table(name = "SCO_SOCIOS_FORNECEDORES", schema = "AGH")
public class ScoSociosFornecedores  extends BaseEntityId<ScoSociosFornecedoresId>  implements Serializable {
	private static final long serialVersionUID = 14567343567534564L;

	private ScoSociosFornecedoresId id;
	private ScoSocios socio;
	private ScoFornecedor fornecedor;
	private Integer version;
	
	public ScoSociosFornecedores(){	
	}
	
	public ScoSociosFornecedores(ScoSociosFornecedoresId id){
		this.id = id;
	}
	
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return version;
	}
	
	public void setVersion(Integer version) {
		this.version = version;
	}
	
	public enum Fields {
		ID("id"),
		SOCIO_SEQ("id.socioSeq"), 
		NUMERO_FORNECEDOR("id.fornecedorNumero"),
		SOCIO("socio"),
		FORNECEDOR("fornecedor"),
		FORNECEDOR_RAZAO_SOCIAL("fornecedor.razaoSocial");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public void setId(ScoSociosFornecedoresId id) {
		this.id = id;
	}

	@EmbeddedId
	@AttributeOverrides({ 
		@AttributeOverride(name = "socioSeq", column = @Column(name = "SOC_SEQ", nullable = false)),
		@AttributeOverride(name = "fornecedorNumero", column = @Column(name = "FRN_NUMERO", nullable = false)) })
	public ScoSociosFornecedoresId getId() {
		return id;
	}
	
	public void setSocio(ScoSocios socio) {
		this.socio = socio;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "SOC_SEQ", insertable = false, updatable = false)
	public ScoSocios getSocio() {
		return socio;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO", insertable = false, updatable = false)
	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
		return result;
	}
	
	@Override
	public boolean equals(Object other) {
		if (other instanceof ScoSociosFornecedores) {
			ScoSociosFornecedores castOther = (ScoSociosFornecedores) other;
			return new EqualsBuilder().append(
					this.getSocio().getSeq(),
					castOther.getSocio().getSeq())
					.isEquals()
					&& new EqualsBuilder().append(
							this.getFornecedor().getNumero(),
							castOther.getFornecedor().getNumero())
							.isEquals();
		} else {
			return false;
		}
	}
	
}
