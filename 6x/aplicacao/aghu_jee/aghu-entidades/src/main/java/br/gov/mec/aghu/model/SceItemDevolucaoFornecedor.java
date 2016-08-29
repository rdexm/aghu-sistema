package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_item_dfs database table.
 * 
 */
@Entity
@Table(name="SCE_ITEM_DFS")
public class SceItemDevolucaoFornecedor extends BaseEntityId<SceItemDevolucaoFornecedorId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -2111467563556668110L;
	private SceItemDevolucaoFornecedorId id;
	private Integer ealSeq;
	private Integer quantidade;
	private String umdCodigo;
	private Double valor;
	private Integer version;
	private Set<SceItemNotaRecebimentoDevolucaoFornecedor> sceInrIdfs;
	
	private SceDevolucaoFornecedor sceDevolucaoFornecedor;
	
	public enum Fields{
		INR_DFS("sceInrIdfs"),
		QUANTIDADE("quantidade"),
		VALOR("valor"),
		DFS_SEQ("id.dfsSeq"),
		NUMERO("id.numero"),
		DEVOLUCAO_FORNECEDOR("sceDevolucaoFornecedor");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public SceItemDevolucaoFornecedor() {
    }


	@EmbeddedId
//	@AttributeOverrides({
//		@AttributeOverride(name = "dfsSeq", column = @Column(name = "DFS_SEQ", nullable = false)),
//		@AttributeOverride(name = "numero", column = @Column(name = "NUMERO", nullable = false))
//	})
	public SceItemDevolucaoFornecedorId getId() {
		return this.id;
	}

	public void setId(SceItemDevolucaoFornecedorId id) {
		this.id = id;
	}
	

	@Column(name="EAL_SEQ")
	public Integer getEalSeq() {
		return this.ealSeq;
	}

	public void setEalSeq(Integer ealSeq) {
		this.ealSeq = ealSeq;
	}

	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	@Column(name="UMD_CODIGO")
	public String getUmdCodigo() {
		return this.umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	@Column(name="VALOR")
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}


	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to SceInrIdf
	@OneToMany(mappedBy="sceItemDf")
	public Set<SceItemNotaRecebimentoDevolucaoFornecedor> getSceInrIdfs() {
		return this.sceInrIdfs;
	}

	public void setSceInrIdfs(Set<SceItemNotaRecebimentoDevolucaoFornecedor> sceInrIdfs) {
		this.sceInrIdfs = sceInrIdfs;
	}
	
	//bi-directional many-to-one association to SceDevolucaoFornecedor
    @ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="DFS_SEQ", insertable=false, updatable=false)
	public SceDevolucaoFornecedor getSceDevolucaoFornecedor() {
		return this.sceDevolucaoFornecedor;
	}

	public void setSceDevolucaoFornecedor(SceDevolucaoFornecedor sceDevolucaoFornecedor) {
		this.sceDevolucaoFornecedor = sceDevolucaoFornecedor;
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
		if (!(obj instanceof SceItemDevolucaoFornecedor)) {
			return false;
		}
		SceItemDevolucaoFornecedor other = (SceItemDevolucaoFornecedor) obj;
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