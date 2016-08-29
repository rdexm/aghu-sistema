package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Version;


import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

@Entity
@Table(name="SCO_SOLIC_PROGR_ENTREGA")
public class ScoSolicitacaoProgramacaoEntrega extends BaseEntitySeq<Long> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -6867750868616838312L;
	private Long seq;
	private ScoItemAutorizacaoForn scoItensAutorizacaoForn;	
	private ScoItemAutorizacaoForn itemAfOrigem;	
	private ScoSolicitacaoDeCompra solicitacaoCompra;
	private ScoSolicitacaoServico solicitacaoServico;
	private ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf;
	private Integer qtde;
	private Double valor;
	private Short indPrioridade;
	private Integer qtdeEntregue;
	private Double valorEfetivado;
	private Integer version;

	public enum Fields{
		SEQ("seq"),
		ITEM_AUTORIZACAO_FORN("scoItensAutorizacaoForn"),
		ITEM_AUTORIZACAO_AFN_NUMERO("scoItensAutorizacaoForn.id.afnNumero"),
		ITEM_AUTORIZACAO_NUMERO("scoItensAutorizacaoForn.id.numero"),
		SOLICITACAO_COMPRA("solicitacaoCompra"),
		NUMERO_SOLICITACAO_COMPRA("solicitacaoCompra.numero"),
		SOLICITACAO_SERVICO("solicitacaoServico"),
		QTDE_ENTREGUE("qtdeEntregue"),
		QTDE("qtde"),
		VALOR("valor"),
		VALOR_EFETIVADO("valorEfetivado"),
		IND_PRIORIDADE("indPrioridade"),
		IND_GERACAO_AUTOMATICA("indGeracaoAutomatica"),
		PROG_ENTREGA_ITEM_AF("progEntregaItemAf"),
		PEA_IAF_AFN_NUMERO("progEntregaItemAf.id.iafAfnNumero"),
		PEA_IAF_NUMERO("progEntregaItemAf.id.iafNumero"),
		PEA_SEQ("progEntregaItemAf.id.seq"),
		PEA_PARCELA("progEntregaItemAf.id.parcela"),
		ITEM_AF_ORIGEM("itemAfOrigem")
		;				
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

    public ScoSolicitacaoProgramacaoEntrega() {
    }

    @Id
	@SequenceGenerator(name="SCO_SPE_PEA_SEQ_GENERATOR", sequenceName="SCO_SPE_PEA_SQ1", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="SCO_SPE_PEA_SEQ_GENERATOR")
	@Column(name="seq")
	public Long getSeq() {
		return this.seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}
    
	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="PEA_IAF_AFN_NUMERO", referencedColumnName="AFN_NUMERO", insertable=false, updatable=false),
		@JoinColumn(name="PEA_IAF_NUMERO", referencedColumnName="NUMERO", insertable=false, updatable=false)
		})
	public ScoItemAutorizacaoForn getScoItensAutorizacaoForn() {
		return this.scoItensAutorizacaoForn;
	}

	public void setScoItensAutorizacaoForn(ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		this.scoItensAutorizacaoForn = scoItensAutorizacaoForn;
	}
	
	@ManyToOne
	@JoinColumns({
			@JoinColumn(name = "iaf_afn_numero_orig", referencedColumnName = "AFN_NUMERO"),
			@JoinColumn(name = "iaf_numero_orig", referencedColumnName = "NUMERO") })
	public ScoItemAutorizacaoForn getItemAfOrigem() {
		return itemAfOrigem;
	}

	public void setItemAfOrigem(ScoItemAutorizacaoForn itemAfOrigem) {
		this.itemAfOrigem = itemAfOrigem;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLS_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoServico getSolicitacaoServico() {
		return solicitacaoServico;
	}

	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
		this.solicitacaoServico = solicitacaoServico;
	}

	//bi-directional many-to-one association to ScoSolicitacaoServico
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
		return solicitacaoCompra;
	}

	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
		this.solicitacaoCompra = solicitacaoCompra;
	}

	@ManyToOne
	@JoinColumns({
		@JoinColumn(name="PEA_IAF_AFN_NUMERO", referencedColumnName="IAF_AFN_NUMERO"),
		@JoinColumn(name="PEA_IAF_NUMERO", referencedColumnName="IAF_NUMERO"),
		@JoinColumn(name="PEA_SEQ", referencedColumnName="SEQ"),
		@JoinColumn(name="PEA_PARCELA", referencedColumnName="PARCELA")
		})
	public ScoProgEntregaItemAutorizacaoFornecimento getProgEntregaItemAf() {
		return this.progEntregaItemAf;
	}

	public void setProgEntregaItemAf(ScoProgEntregaItemAutorizacaoFornecimento progEntregaItemAf) {
		this.progEntregaItemAf = progEntregaItemAf;
	}
	
	@Column(name="QTDE")
	public Integer getQtde() {
		return this.qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}


	@Column(name="QTDE_ENTREGUE")
	public Integer getQtdeEntregue() {
		return this.qtdeEntregue;
	}

	public void setQtdeEntregue(Integer qtdeEntregue) {
		this.qtdeEntregue = qtdeEntregue;
	}

	@Column(name="VALOR_EFETIVADO")
	public Double getValorEfetivado() {
		return this.valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}


	@Column(name="VALOR")
	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor= valor;
	}

	@Column(name="IND_PRIORIDADE")	
	public Short getIndPrioridade() {
		return this.indPrioridade;
	}

	public void setIndPrioridade(Short indPrioridade) {
		this.indPrioridade = indPrioridade;
	}
      
	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// ##### GeradorEqualsHashCodeMain #####
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((getSeq() == null) ? 0 : getSeq().hashCode());
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
		if (!(obj instanceof ScoSolicitacaoProgramacaoEntrega)) {
			return false;
		}
		ScoSolicitacaoProgramacaoEntrega other = (ScoSolicitacaoProgramacaoEntrega) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
}