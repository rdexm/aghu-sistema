package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
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
import javax.persistence.Version;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sce_item_receb_provisorios database table.
 * 
 */
@Entity
@Table(name="SCE_ITEM_RECEB_PROVISORIOS")
public class SceItemRecebProvisorio extends BaseEntityId<SceItemRecebProvisorioId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = 7388645649231035511L;
	private SceItemRecebProvisorioId id;
	private Integer eslSeq;
	private Integer quantidade;
	private Double valor;
	private Integer version;
	private SceNotaRecebProvisorio notaRecebimentoProvisorio;
	private ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf;
	private ScoItemAutorizacaoForn scoItensAutorizacaoForn;
	private SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao;
	private SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao;
	private Set<SceItemRecbXProgrEntrega> sceItensRecbXProgrEntrega;
	private Set<PtmItemRecebProvisorios> ptmItensRecebProvisorios;
	private ScoItemAutorizacaoForn scoItemAutorizacaoForn;
	private ScoItemAutorizacaoForn itemAutorizacaoForn;
	private ScoAutorizacaoForn scoAutorizacaoForn;
	private ScoFaseSolicitacao scoFaseSolicitacao;
	private SceEntrSaidSemLicitacao sceEntrSaidSemLicitacao;
	
	public enum Fields{
		
		NOTA_RECEBIMENTO_PROVISORIO("notaRecebimentoProvisorio"),
		PROG_ENTREGA_IAF("progEntregaItemAf"),
		NRP_SEQ("id.nrpSeq"),
		NRO_ITEM("id.nroItem"),
		QUANTIDADE("quantidade"),
		VALOR("valor"),
		PEA_IAF_AFN_NUMERO("progEntregaItemAf.id.iafAfnNumero"),
		PEA_IAF_NUMERO("progEntregaItemAf.id.iafNumero"),
		ITEM_AUTORIZACAO_FORNECIMENTO("scoItensAutorizacaoForn"),
		PEA_PARCELA("progEntregaItemAf.id.parcela"),
		PEA_SEQ("progEntregaItemAf.id.seq"),
		ESL_SEQ("eslSeq"), 
		ITEM_ENTR_SAID_SEM_LICITACAO("sceItemEntrSaidSemLicitacao"), 
		ITEM_ENTR_SAID_SEM_LICITACAO_SEQ("sceItemEntrSaidSemLicitacao.seq"),
		SCE_ITENS_RECB_X_PROGR_ENTREGA("sceItensRecbXProgrEntrega"),
		ENTRADA_SAIDA_SEM_LICITACAO("entradaSaidaSemLicitacao"),
		PTM_ITENS_RECEB_PROVISORIOS("ptmItensRecebProvisorios"),
		SCO_ITEM_AUTORIZACAO_FORN("scoItemAutorizacaoForn"),
		ITEM_AUTORIZACAO_FORN("itemAutorizacaoForn"),
		SCO_AUTORIZACAO_FORN("scoAutorizacaoForn"),
		SCO_FASE_SOLICITACAO("scoFaseSolicitacao"),
		SCE_ENTR_SAID_SEM_LICITACAO("sceEntrSaidSemLicitacao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}


    public SceItemRecebProvisorio() {
    }


	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "nrpSeq", column = @Column(name = "NRP_SEQ", nullable = false)),
		@AttributeOverride(name = "nroItem", column = @Column(name = "NRO_ITEM", nullable = false)) 
		})
	public SceItemRecebProvisorioId getId() {
		return this.id;
	}

	public void setId(SceItemRecebProvisorioId id) {
		this.id = id;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IAF_AFN_NUMERO", referencedColumnName = "AFN_NUMERO"),
			@JoinColumn(name = "IAF_NUMERO", referencedColumnName = "NUMERO") })
	public ScoItemAutorizacaoForn getScoItensAutorizacaoForn() {
		return this.scoItensAutorizacaoForn;
	}

	public void setScoItensAutorizacaoForn(final ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		this.scoItensAutorizacaoForn = scoItensAutorizacaoForn;
	}

	@Column(name="ESL_SEQ")
	public Integer getEslSeq() {
		return this.eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	
	/*
	 * Valores já utilizados com mapiamento não realizado adequadamente.
	 */
	@Transient
	public Integer getIafAfnNumero() {
		if (this.scoItensAutorizacaoForn == null) {
			return null;
		}
		return this.scoItensAutorizacaoForn.getId().getAfnNumero();
	}

	@Transient
	public Integer getIafNumero() {
		if (this.scoItensAutorizacaoForn == null) {
			return null;
		}
		return this.scoItensAutorizacaoForn.getId().getNumero();
	}

	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

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

	@ManyToOne
	@JoinColumn(name="NRP_SEQ", insertable=false, updatable=false)
	public SceNotaRecebProvisorio getNotaRecebimentoProvisorio() {
		return notaRecebimentoProvisorio;
	}

	
	public void setNotaRecebimentoProvisorio(
			SceNotaRecebProvisorio notaRecebimentoProvisorio) {
		this.notaRecebimentoProvisorio = notaRecebimentoProvisorio;
	}


	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="PEA_IAF_AFN_NUMERO", referencedColumnName="IAF_AFN_NUMERO"),
		@JoinColumn(name="PEA_IAF_NUMERO", referencedColumnName="IAF_NUMERO"),
		@JoinColumn(name="PEA_PARCELA", referencedColumnName="PARCELA"),
		@JoinColumn(name="PEA_SEQ", referencedColumnName="SEQ")
		})
	public ScoProgEntregaItemAutorizacaoFornecimento getProgEntregaItemAf() {
		return progEntregaItemAf;
	}


	public void setProgEntregaItemAf(
			ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf) {
		this.progEntregaItemAf = progEntregaItemAf;
	}
	

	@ManyToOne
	@JoinColumn(name = "ISL_SEQ", referencedColumnName = "SEQ")
	public SceItemEntrSaidSemLicitacao getSceItemEntrSaidSemLicitacao() {
		return sceItemEntrSaidSemLicitacao;
	}

	public void setSceItemEntrSaidSemLicitacao(SceItemEntrSaidSemLicitacao sceItemEntrSaidSemLicitacao) {
		this.sceItemEntrSaidSemLicitacao = sceItemEntrSaidSemLicitacao;
	}

	@ManyToOne
	@JoinColumn(name="ESL_SEQ", insertable = false, updatable = false)
	public SceEntradaSaidaSemLicitacao getEntradaSaidaSemLicitacao() {
		return entradaSaidaSemLicitacao;
	}

	public void setEntradaSaidaSemLicitacao(
			SceEntradaSaidaSemLicitacao entradaSaidaSemLicitacao) {
		this.entradaSaidaSemLicitacao = entradaSaidaSemLicitacao;
	}
	
	@OneToMany(mappedBy="sceItemRecebProvisorio", fetch = FetchType.LAZY)
	public Set<SceItemRecbXProgrEntrega> getSceItensRecbXProgrEntrega() {
		return sceItensRecbXProgrEntrega;
	}

	public void setSceItensRecbXProgrEntrega(Set<SceItemRecbXProgrEntrega> sceItensRecbXProgrEntrega) {
		this.sceItensRecbXProgrEntrega = sceItensRecbXProgrEntrega;
	}

	@OneToMany(mappedBy="sceItemRecebProvisorio", fetch = FetchType.LAZY)
	public Set<PtmItemRecebProvisorios> getPtmItensRecebProvisorios() {
		return ptmItensRecebProvisorios;
	}

	public void setPtmItensRecebProvisorios(Set<PtmItemRecebProvisorios> ptmItensRecebProvisorios) {
		this.ptmItensRecebProvisorios = ptmItensRecebProvisorios;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "PEA_IAF_AFN_NUMERO", referencedColumnName = "AFN_NUMERO", insertable = false, updatable = false),
			@JoinColumn(name = "PEA_IAF_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false) })
	public ScoItemAutorizacaoForn getScoItemAutorizacaoForn() {
		return scoItemAutorizacaoForn;
	}

	public void setScoItemAutorizacaoForn(ScoItemAutorizacaoForn scoItemAutorizacaoForn) {
		this.scoItemAutorizacaoForn = scoItemAutorizacaoForn;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "PEA_IAF_AFN_NUMERO", referencedColumnName = "AFN_NUMERO", insertable = false, updatable = false),
			@JoinColumn(name = "PEA_IAF_NUMERO", referencedColumnName = "NUMERO", insertable = false, updatable = false) })
	public ScoItemAutorizacaoForn getItemAutorizacaoForn() {
		return itemAutorizacaoForn;
	}


	public void setItemAutorizacaoForn(ScoItemAutorizacaoForn itemAutorizacaoForn) {
		this.itemAutorizacaoForn = itemAutorizacaoForn;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "PEA_IAF_AFN_NUMERO", insertable = false, updatable = false)
	public ScoAutorizacaoForn getScoAutorizacaoForn() {
		return scoAutorizacaoForn;
	}

	public void setScoAutorizacaoForn(ScoAutorizacaoForn scoAutorizacaoForn) {
		this.scoAutorizacaoForn = scoAutorizacaoForn;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "PEA_IAF_AFN_NUMERO", referencedColumnName = "IAF_AFN_NUMERO", insertable = false, updatable = false),
			@JoinColumn(name = "PEA_IAF_NUMERO", referencedColumnName = "IAF_NUMERO", insertable = false, updatable = false) })
	public ScoFaseSolicitacao getScoFaseSolicitacao() {
		return scoFaseSolicitacao;
	}


	public void setScoFaseSolicitacao(ScoFaseSolicitacao scoFaseSolicitacao) {
		this.scoFaseSolicitacao = scoFaseSolicitacao;
	}

	@ManyToOne
	@JoinColumn(name="ESL_SEQ", insertable = false, updatable = false)
	public SceEntrSaidSemLicitacao getSceEntrSaidSemLicitacao() {
		return sceEntrSaidSemLicitacao;
	}

	public void setSceEntrSaidSemLicitacao(
			SceEntrSaidSemLicitacao sceEntrSaidSemLicitacao) {
		this.sceEntrSaidSemLicitacao = sceEntrSaidSemLicitacao;
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
		if (!(obj instanceof SceItemRecebProvisorio)) {
			return false;
		}
		SceItemRecebProvisorio other = (SceItemRecebProvisorio) obj;
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