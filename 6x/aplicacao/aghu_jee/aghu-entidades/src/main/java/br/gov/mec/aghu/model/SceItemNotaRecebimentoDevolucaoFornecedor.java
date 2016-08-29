package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_inr_idf database table.
 * 
 */
@Entity
@Table(name="SCE_INR_IDF")
public class SceItemNotaRecebimentoDevolucaoFornecedor extends BaseEntityId<SceItemNotaRecebimentoDevolucaoFornecedorId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 2715461755636547079L;
	private SceItemNotaRecebimentoDevolucaoFornecedorId id;
	private Integer quantidade;
	private Integer version;
	private SceItemNotaRecebimento sceItemNr;
	private SceItemDevolucaoFornecedor sceItemDf;
	
	public enum Fields{
		ITEM_DEVOLUCAO_FORNECEDOR("sceItemDf"),
		ITEM_NOTA_RECEBIMENTO("sceItemNr"),
		QUANTIDADE("quantidade"),
		AFN_NUMERO("id.iafAfnNumero"),
		IAF_NUMERO("id.iafNumero"),
		IDF_DFS_SEQ("sceItemDf.id.dfsSeq"),
		IDF_NUMERO("sceItemDf.id.numero"),
		INR_NRS_SEQ("sceItemNr.id.nrsSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public SceItemNotaRecebimentoDevolucaoFornecedor() {
    }

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "nrsSeq", column = @Column(name = "INR_NRS_SEQ", nullable = false)),
		@AttributeOverride(name = "iafAfnNumero", column = @Column(name = "INR_IAF_AFN_NUMERO", nullable = false)),
		@AttributeOverride(name = "iafNumero", column = @Column(name = "INR_IAF_NUMERO", nullable = false)),
		@AttributeOverride(name = "idfNumero", column = @Column(name = "IDF_NUMERO", nullable = false)),
		@AttributeOverride(name = "dfsSeq", column = @Column(name = "IDF_DFS_SEQ", nullable = false)) })
	public SceItemNotaRecebimentoDevolucaoFornecedorId getId() {
		return this.id;
	}

	public void setId(SceItemNotaRecebimentoDevolucaoFornecedorId id) {
		this.id = id;
	}
	

	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	 //bi-directional many-to-one association to SceItemNr
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="INR_IAF_AFN_NUMERO", referencedColumnName="IAF_AFN_NUMERO", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="INR_IAF_NUMERO", referencedColumnName="IAF_NUMERO", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="INR_NRS_SEQ", referencedColumnName="NRS_SEQ", nullable=false, insertable=false, updatable=false)
		})
	public SceItemNotaRecebimento getSceItemNr() {
		return this.sceItemNr;
	}

	public void setSceItemNr(SceItemNotaRecebimento sceItemNr) {
		this.sceItemNr = sceItemNr;
	}
	
	//bi-directional many-to-one association to SceItemDf
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="IDF_DFS_SEQ", referencedColumnName="DFS_SEQ", nullable=false, insertable=false, updatable=false),
		@JoinColumn(name="IDF_NUMERO", referencedColumnName="NUMERO", nullable=false, insertable=false, updatable=false)
		})
	public SceItemDevolucaoFornecedor getSceItemDf() {
		return this.sceItemDf;
	}

	public void setSceItemDf(SceItemDevolucaoFornecedor sceItemDf) {
		this.sceItemDf = sceItemDf;
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
		if (!(obj instanceof SceItemNotaRecebimentoDevolucaoFornecedor)) {
			return false;
		}
		SceItemNotaRecebimentoDevolucaoFornecedor other = (SceItemNotaRecebimentoDevolucaoFornecedor) obj;
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