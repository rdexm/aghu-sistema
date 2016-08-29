package br.gov.mec.aghu.model;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.persistence.EntityCompositeId;

/**
 * The primary key class for the sce_item_pacote_materiais database table.
 * 
 */
@Embeddable
public class SceItemPacoteMateriaisId implements EntityCompositeId {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6454464977398966519L;
	
	private Integer seqEstoque; 
   	private Integer codigoCentroCustoProprietarioPacoteMateriais;
	private Integer codigoCentroCustoAplicacaoPacoteMateriais;
	private Integer numeroPacoteMateriais;

	/**
	 * 
	 */
    public SceItemPacoteMateriaisId() {
    }
    
    @Column(name="PMT_CCT_CODIGO_REFERE")
	public Integer getCodigoCentroCustoProprietarioPacoteMateriais() {
		return this.codigoCentroCustoProprietarioPacoteMateriais;
	}
    
    public void setCodigoCentroCustoProprietarioPacoteMateriais(
			Integer codigoCentroCustoProprietarioPacoteMateriais) {
		this.codigoCentroCustoProprietarioPacoteMateriais = codigoCentroCustoProprietarioPacoteMateriais;
	}
	
	@Column(name="PMT_CCT_CODIGO")
	public Integer getCodigoCentroCustoAplicacaoPacoteMateriais() {
		return this.codigoCentroCustoAplicacaoPacoteMateriais;
	}
	
	public void setCodigoCentroCustoAplicacaoPacoteMateriais(
			Integer codigoCentroCustoAplicacaoPacoteMateriais) {
		this.codigoCentroCustoAplicacaoPacoteMateriais = codigoCentroCustoAplicacaoPacoteMateriais;
	}
	
	@Column(name="PMT_NUMERO")
	public Integer getNumeroPacoteMateriais() {
		return this.numeroPacoteMateriais;
	}
	

	public void setNumeroPacoteMateriais(Integer numeroPacoteMateriais) {
		this.numeroPacoteMateriais = numeroPacoteMateriais;
	}
	
	@Column(name = "EAL_SEQ")
	public Integer getSeqEstoque() {
		return this.seqEstoque;
	}

	public void setSeqEstoque(Integer seqEstoque) {
		this.seqEstoque = seqEstoque;
	}
	
    
    /*//bi-directional many-to-one association to ScePacoteMateriais
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="PMT_CCT_CODIGO_REFERE", referencedColumnName="CCT_CODIGO_REFERE"),
		@JoinColumn(name="PMT_CCT_CODIGO", referencedColumnName="CCT_CODIGO"),
		@JoinColumn(name="PMT_NUMERO", referencedColumnName="NUMERO")
	})
	public ScePacoteMateriais getPacoteMaterial() {
		return this.pacoteMaterial;
	}

	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
	}*/

	/*@ManyToOne
	@JoinColumn(name="EAL_SEQ")
	public SceEstoqueAlmoxarifado getEstoque() {
		return estoque;
	}

	public void setEstoque(SceEstoqueAlmoxarifado estoque) {
		this.estoque = estoque;
	}*/
	
	

	

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
		umHashCodeBuilder.append(this.getCodigoCentroCustoProprietarioPacoteMateriais());
		umHashCodeBuilder.append(this.getCodigoCentroCustoAplicacaoPacoteMateriais());
		umHashCodeBuilder.append(this.getNumeroPacoteMateriais());
		umHashCodeBuilder.append(this.getSeqEstoque());
		return umHashCodeBuilder.toHashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SceItemPacoteMateriaisId)) {
			return false;
		}
		SceItemPacoteMateriaisId other = (SceItemPacoteMateriaisId) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
		umEqualsBuilder.append(this.getCodigoCentroCustoProprietarioPacoteMateriais(), other.getCodigoCentroCustoProprietarioPacoteMateriais());
		umEqualsBuilder.append(this.getCodigoCentroCustoAplicacaoPacoteMateriais(), other.getCodigoCentroCustoAplicacaoPacoteMateriais());
		umEqualsBuilder.append(this.getNumeroPacoteMateriais(), other.getNumeroPacoteMateriais());
		umEqualsBuilder.append(this.getSeqEstoque(), other.getSeqEstoque());
		return umEqualsBuilder.isEquals();
	}
	// ##### GeradorEqualsHashCodeMain #####
	
}