package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;


import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_item_nrs database table.
 * 
 */
@Entity
@Table(name="SCE_ITEM_NRS")
public class SceItemNotaRecebimento extends BaseEntityId<SceItemNotaRecebimentoId> implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2693058700441132420L;
	private SceItemNotaRecebimentoId id;
	private Date dtDebitoNrIaf;
	private Boolean indDebitoNrIaf;
	private Boolean indTributacao;
	private Boolean indUsoMaterial;
	private ScoMaterial material;
	private Integer quantidade;
	private RapServidores servidorDebitado;
	private ScoServico servico;
	private Double valor;
	private Integer version;
	private Set<SceItemNotaRecebimentoDevolucaoFornecedor> inrIdfs;
	private ScoUnidadeMedida unidadeMedida;

	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private SceNotaRecebimento notaRecebimento;
	//private ScoFaseSolicitacao faseSolicitacao;

	public enum Fields{
		ITEM_AUTORIZACAO_FORN("itemAutorizacaoForn"),
		IAF_AFN_NUMERO("itemAutorizacaoForn.id.afnNumero"),
		IAF_NUMERO("itemAutorizacaoForn.id.numero"),
		MATERIAL("material"),
		MAT_CODIGO("material.codigo"),
		SERVICO("servico"),
		NOTA_RECEBIMENTO("notaRecebimento"),
		NOTA_RECEBIMENTO_SEQ("id.nrsSeq"),
		QUANTIDADE("quantidade"),
		VALOR("valor"),
		INR_IDF("inrIdfs"),
		IND_USO_MATERIAL("indUsoMaterial"),
		AFN_NUMERO("id.afnNumero"),
		ITEM_NUMERO("id.itemNumero"),
		IND_DEBITO_NF("indDebitoNrIaf"),
		UNIDADE_MEDIDA("unidadeMedida"),
		IND_TRIBUTACAO("indTributacao");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public SceItemNotaRecebimento() {
	}

	@EmbeddedId
	/*@AttributeOverrides({
		@AttributeOverride(name = "nrsSeq", column = @Column(name = "NRS_SEQ", nullable = false)),
		@AttributeOverride(name = "itemNumero", column = @Column(name = "NUMERO", nullable = false)), 
		@AttributeOverride(name = "afnNumero", column = @Column(name = "AFN_NUMERO", nullable = false))
	})*/
	public SceItemNotaRecebimentoId getId() {
		return this.id;
	}

	public void setId(SceItemNotaRecebimentoId id) {
		this.id = id;
	}


	@ManyToOne
	@JoinColumn(name="SRV_CODIGO")
	public ScoServico getServico() {
		return servico;
	}


	public void setServico(ScoServico servico) {
		this.servico = servico;
	}

	@Column(name="DT_DEBITO_NR_IAF")
	public Date getDtDebitoNrIaf() {
		return this.dtDebitoNrIaf;
	}

	public void setDtDebitoNrIaf(Date dtDebitoNrIaf) {
		this.dtDebitoNrIaf = dtDebitoNrIaf;
	}

	@Column(name="IND_DEBITO_NR_IAF")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndDebitoNrIaf() {
		return this.indDebitoNrIaf;
	}

	public void setIndDebitoNrIaf(Boolean indDebitoNrIaf) {
		this.indDebitoNrIaf = indDebitoNrIaf;
	}


	@Column(name="IND_TRIBUTACAO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTributacao() {
		return this.indTributacao;
	}

	public void setIndTributacao(Boolean indTributacao) {
		this.indTributacao = indTributacao;
	}


	@Column(name="IND_USO_MATERIAL")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndUsoMaterial() {
		return this.indUsoMaterial;
	}

	public void setIndUsoMaterial(Boolean indUsoMaterial) {
		this.indUsoMaterial = indUsoMaterial;
	}

	@ManyToOne
	@JoinColumn(name="MAT_CODIGO")
	public ScoMaterial getMaterial() {
		return material;
	}


	public void setMaterial(ScoMaterial material) {
		this.material = material;
	}

	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name = "SER_MATRICULA_DEBITADO", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_DEBITADO", referencedColumnName = "VIN_CODIGO") })
		public RapServidores getServidorDebitado() {
		return servidorDebitado;
	}


	public void setServidorDebitado(RapServidores servidorDebitado) {
		this.servidorDebitado = servidorDebitado;
	}
	
	@ManyToOne
	@JoinColumn(name="UMD_CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

	@Column(name="VALOR")
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	@Transient
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	//bi-directional many-to-one association to SceNotaRecebimento
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="NRS_SEQ", insertable=false, updatable=false)
	public SceNotaRecebimento getNotaRecebimento() {
		return notaRecebimento;
	}

	public void setNotaRecebimento(SceNotaRecebimento notaRecebimento) {
		this.notaRecebimento = notaRecebimento;
	}

	//bi-directional many-to-one association to ScoItensAutorizacaoForn
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="AFN_NUMERO", insertable=false, updatable=false),
		@JoinColumn(name="IAF_NUMERO", referencedColumnName="NUMERO", insertable=false, updatable=false)
	})
	public ScoItemAutorizacaoForn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}

	public void setItemAutorizacaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}

	//bi-directional many-to-one association to SceInrIdf
	@OneToMany(mappedBy="sceItemNr")
	public Set<SceItemNotaRecebimentoDevolucaoFornecedor> getInrIdfs() {
		return inrIdfs;
	}

	public void setInrIdfs(Set<SceItemNotaRecebimentoDevolucaoFornecedor> inrIdfs) {
		this.inrIdfs = inrIdfs;
	}

//	@ManyToOne
//	@JoinColumns({
//		@JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="IAF_AFN_NUMERO", insertable=false, updatable=false),
//		@JoinColumn(name="IAF_NUMERO", referencedColumnName="IAF_NUMERO", insertable=false, updatable=false)
//		})
//	public ScoFaseSolicitacao getFaseSolicitacao() {
//		return faseSolicitacao;
//	}
//
//	public void setFaseSolicitacao(ScoFaseSolicitacao faseSolicitacao) {
//		this.faseSolicitacao = faseSolicitacao;
//	}

	/*@Transient
	public Integer getAfnNumero(){
		return getId().getAfnNumero();
	}

	@Transient
	public Integer getItemNumero(){
		return getId().getItemNumero();
	}

	public void setAfnNumero(Integer afnNumero) {
		getId().setAfnNumero(afnNumero);
	}


	public void setItemNumero(Integer itemNumero) {
		getId().setItemNumero(itemNumero);
	}*/
	
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
		SceItemNotaRecebimento other = (SceItemNotaRecebimento) obj;
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