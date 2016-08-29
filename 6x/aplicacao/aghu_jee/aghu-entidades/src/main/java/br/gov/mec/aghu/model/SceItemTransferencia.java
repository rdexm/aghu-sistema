package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_item_trs database table.
 * 
 */
@Entity
@Table(name="SCE_ITEM_TRS")
public class SceItemTransferencia extends BaseEntityId<SceItemTransferenciaId> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8860846493976692615L;
	private SceItemTransferenciaId id;
	private Integer eslSeqConse;
	private Integer eslSeqConss;
	private Boolean faturadoConss;
	private Boolean matBloqueado;
	private Integer qtdeEnviada;
	private Integer quantidade;
	private Integer version;
	private SceTransferencia transferencia;
	private SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem;
	private SceEstoqueAlmoxarifado estoqueAlmoxarifado;
	private ScoUnidadeMedida unidadeMedida;

    public SceItemTransferencia() {
    }


	@EmbeddedId
	public SceItemTransferenciaId getId() {
		return this.id;
	}

	public void setId(SceItemTransferenciaId id) {
		this.id = id;
	}
	
	@Column(name="ESL_SEQ_CONSE")
	public Integer getEslSeqConse() {
		return this.eslSeqConse;
	}

	public void setEslSeqConse(Integer eslSeqConse) {
		this.eslSeqConse = eslSeqConse;
	}


	@Column(name="ESL_SEQ_CONSS")
	public Integer getEslSeqConss() {
		return this.eslSeqConss;
	}

	public void setEslSeqConss(Integer eslSeqConss) {
		this.eslSeqConss = eslSeqConss;
	}


	@Column(name="IND_FATURADO_CONSS")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getFaturadoConss() {
		return faturadoConss;
	}
	
	public void setFaturadoConss(Boolean faturadoConss) {
		this.faturadoConss = faturadoConss;
	}

	@Column(name="IND_MAT_BLOQUEADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getMatBloqueado() {
		return matBloqueado;
	}
	
	public void setMatBloqueado(Boolean matBloqueado) {
		this.matBloqueado = matBloqueado;
	}

	@Column(name="QTDE_ENVIADA")
	public Integer getQtdeEnviada() {
		return this.qtdeEnviada;
	}

	public void setQtdeEnviada(Integer qtdeEnviada) {
		this.qtdeEnviada = qtdeEnviada;
	}

	@Column(name="QUANTIDADE", nullable = false)
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EAL_SEQ", nullable = false, insertable = false, updatable = false)
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifado() {
		return this.estoqueAlmoxarifado;
	}
	
	public void setEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmoxarifado) {
		this.estoqueAlmoxarifado = estoqueAlmoxarifado;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "EAL_SEQ_ORIG")
	public SceEstoqueAlmoxarifado getEstoqueAlmoxarifadoOrigem() {
		return this.estoqueAlmoxarifadoOrigem;
	}
	
	public void setEstoqueAlmoxarifadoOrigem(SceEstoqueAlmoxarifado estoqueAlmoxarifadoOrigem) {
		this.estoqueAlmoxarifadoOrigem = estoqueAlmoxarifadoOrigem;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", nullable = false, referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.unidadeMedida = scoUnidadeMedida;
	}

	 @ManyToOne
	@JoinColumn(name="TRF_SEQ", nullable = false, insertable=false, updatable=false)
	public SceTransferencia getTransferencia() {
		return this.transferencia;
	}
    
	public void setTransferencia(SceTransferencia sceTransferencia) {
		this.transferencia = sceTransferencia;
	}
	
	@Transient
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public enum Fields {
		
		ID("id"),
		TRF_SEQ("id.trfSeq"),
		EAL_SEQ("id.ealSeq"),
		ESL_SEQ_CONSE("eslSeqConse"),
		ESL_SEQ_CONSS("eslSeqConss"),
		FATURADO_CONSS("faturadoConss"),
		QUANTIDADE("quantidade"),
		MAT_BLOQUEADO("matBloqueado"),
		UNIDADE_MEDIDA("unidadeMedida"),
		QTDE_ENVIADA("qtdeEnviada"),
		TRANSFERENCIA("transferencia"),
		ESTOQUE_ALMOXARIFADO_ORIGEM("estoqueAlmoxarifadoOrigem"),
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
		if (!(obj instanceof SceItemTransferencia)) {
			return false;
		}
		SceItemTransferencia other = (SceItemTransferencia) obj;
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