package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_validades database table.
 * 
 */
@Entity
@Table(name="SCE_VALIDADES")
public class SceValidade extends BaseEntityId<SceValidadeId> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5487015994179466447L;
	private SceValidadeId id;
	private Integer qtdeConsumida;
	private Integer qtdeDisponivel;
	private Integer qtdeEntrada;
	private Integer version;
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;
	
    public SceValidade() {
    }


	@EmbeddedId
	public SceValidadeId getId() {
		return this.id;
	}

	public void setId(SceValidadeId id) {
		this.id = id;
	}
	

	@Column(name="QTDE_CONSUMIDA",nullable = false)
	public Integer getQtdeConsumida() {
		return this.qtdeConsumida;
	}

	public void setQtdeConsumida(Integer qtdeConsumida) {
		this.qtdeConsumida = qtdeConsumida;
	}


	@Column(name="QTDE_DISPONIVEL",nullable = false)
	public Integer getQtdeDisponivel() {
		return this.qtdeDisponivel;
	}

	public void setQtdeDisponivel(Integer qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}


	@Column(name="QTDE_ENTRADA",nullable = false)
	public Integer getQtdeEntrada() {
		return this.qtdeEntrada;
	}

	public void setQtdeEntrada(Integer qtdeEntrada) {
		this.qtdeEntrada = qtdeEntrada;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	@JoinColumn(name="EAL_SEQ", insertable=false, updatable=false)
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return estoqueAlmoxarifado;
	}

	public void setEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}
	
	public enum Fields{
		EAL_SEQ("id.ealSeq"),
		DATA("id.data"),
		QTDE_DISPONIVEL("qtdeDisponivel"),
		ESTOQUE_ALMOXARIFADO("estoqueAlmoxarifado");
		
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
		if (!(obj instanceof SceValidade)) {
			return false;
		}
		SceValidade other = (SceValidade) obj;
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