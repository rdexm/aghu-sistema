package br.gov.mec.aghu.model;

import java.io.Serializable;

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
import javax.persistence.Transient;
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_item_pacote_materiais database table.
 * 
 */
@Entity
@Table(name="SCE_ITEM_PACOTE_MATERIAIS")
public class SceItemPacoteMateriais extends BaseEntityId<SceItemPacoteMateriaisId> implements Serializable {
	
	private static final long serialVersionUID = -4438181555686520829L;
	
	private SceItemPacoteMateriaisId id;
	private Integer quantidade;
	private SceEstoqueAlmoxarifado estoque;
	private ScePacoteMateriais pacoteMaterial;
	private Integer version = Integer.valueOf(0);
	
	//transient
	private Integer codigoMaterialEstoque;

    public SceItemPacoteMateriais() {
    }


	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "pacoteMaterial.id.cctCodigoRefere", column = @Column(name = "PMT_CCT_CODIGO_REFERE", nullable = false)),
		@AttributeOverride(name = "pacoteMaterial.id.cctCodigo", column = @Column(name = "PMT_CCT_CODIGO", nullable = false)),
		@AttributeOverride(name = "pacoteMaterial.id.numero", column = @Column(name = "PMT_NUMERO", nullable = false)),
		@AttributeOverride(name = "ealSeq", column = @Column(name = "EAL_SEQ", nullable = false, length = 3)) })
		
	public SceItemPacoteMateriaisId getId() {
		return this.id;
	}

	public void setId(SceItemPacoteMateriaisId id) {
		this.id = id;
	}
	
	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="EAL_SEQ", nullable = false, insertable = false, updatable = false)
	public SceEstoqueAlmoxarifado getEstoque() {
		return estoque;
	}

	public void setEstoque(SceEstoqueAlmoxarifado estoque) {
		this.estoque = estoque;
	}
	
	
	//bi-directional many-to-one association to ScePacoteMateriais
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="PMT_CCT_CODIGO_REFERE", referencedColumnName="CCT_CODIGO_REFERE", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name="PMT_CCT_CODIGO", referencedColumnName="CCT_CODIGO", nullable = false, insertable = false, updatable = false),
		@JoinColumn(name="PMT_NUMERO", referencedColumnName="NUMERO", nullable = false, insertable = false, updatable = false)
	})
	public ScePacoteMateriais getPacoteMaterial() {
		return this.pacoteMaterial;
	}

	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
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
		if (getClass() != obj.getClass()) {
			return false;
		}
		SceItemPacoteMateriais other = (SceItemPacoteMateriais) obj;
		if (id == null) {
			if (other.id != null) {
				return false;
			}
		} else if (!id.equals(other.id)) {
			return false;
		}
		return true;
	}

	
	@Transient
	public String getChaveMapStr(){
		StringBuffer chaveMapStr = new StringBuffer();
		if(getId() != null){
			if(getPacoteMaterial() != null && getPacoteMaterial().getId() != null)
			{
				chaveMapStr.append(getPacoteMaterial().getId().getCodigoCentroCustoAplicacao());
				chaveMapStr.append('@').append(getPacoteMaterial().getId().getCodigoCentroCustoProprietario());
				chaveMapStr.append('@').append(getPacoteMaterial().getId().getNumero());
			}
			if(chaveMapStr.length() > 0 && getEstoque() != null 
					&& getEstoque().getSeq() != null)
			{
				chaveMapStr.append('@').append(getEstoque().getSeq());
			}
		}
		return chaveMapStr.toString();
	}
	
	@Transient
	public Integer getCodigoMaterialEstoque() {
		return codigoMaterialEstoque;
	}


	public void setCodigoMaterialEstoque(Integer codigoMaterialEstoque) {
		this.codigoMaterialEstoque = codigoMaterialEstoque;
	}

	/**
	 * 
	 *
	 */
	public enum Fields {
				
				ID_ITEM_PACOTE_MATERIAIS("id"),
				PACOTE_MATERIAL("pacoteMaterial"),
				ESTOQUE_ALMOXARIFADO("estoque"),
				QUANTIDADE("quantidade"),
				CODIGO_CENTRO_CUSTO_PROPRIETARIO_PACOTE("id.codigoCentroCustoProprietarioPacoteMateriais"),
				CODIGO_CENTRO_CUSTO_APLICACAO_PACOTE("id.codigoCentroCustoAplicacaoPacoteMateriais"),
				NUMERO_PACOTE("id.numeroPacoteMateriais"),
				CODIGO_ESTOQUE("id.seqEstoque"),
				CODIGO_MATERIAL_ESTOQUE("codigoMaterialEstoque");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
	
}