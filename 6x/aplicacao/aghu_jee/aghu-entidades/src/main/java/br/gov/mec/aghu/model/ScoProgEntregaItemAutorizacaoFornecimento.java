package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;


import org.hibernate.annotations.Type;

import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * The persistent class for the sco_progr_entrega_itens_af database table.
 * 
 */
@Entity
@Table(name="SCO_PROGR_ENTREGA_ITENS_AF")
public class ScoProgEntregaItemAutorizacaoFornecimento extends BaseEntityId<ScoProgEntregaItemAutorizacaoFornecimentoId> implements Serializable {	/**
	 * 
	 */
	private static final long serialVersionUID = -6867750868666838312L;
	private ScoProgEntregaItemAutorizacaoFornecimentoId id;
	private Date dtAlteracao;
	private Date dtAssinatura;
	private Date dtAtualizacao;
	private Date dtCancelamento;
	private Date dtEntrega;
	private Date dtGeracao;
	private Date dtLibPlanejamento;
	private Date dtNecessidadeHcpa;
	private Date dtPrevEntrega;
	private Date dtPrevEntregaAposAtraso;
	private Integer eslSeqFatura;
	private Boolean indAssinatura;
	private Boolean indCancelada;
	private Boolean indConversaoUnidade;
	private Boolean indEfetivada;
	private DominioAfEmpenhada indEmpenhada;
	private Boolean indEntregaImediata;
	private Boolean indEntregaUrgente;
	private Boolean indEnvioFornecedor;
	private Boolean indImpressa;
	private Boolean indPlanejamento;
	private Boolean indPublicado;
	private Boolean indRecalculoAutomatico;
	private Boolean indRecalculoManual;
	private Boolean indTramiteInterno;
	private String observacao;
	private Integer qtde;
	private Integer qtdeEntregue;
	private Integer qtdeEntregueAMais;
	private Integer qtdeEntregueProv;
	private Integer slcNumero;
	private Double valorEfetivado;
	private Double valorTotal;
	private Integer version;
	private RapServidores rapServidorLibPlanej;
	private RapServidores rapServidorCancelamento;
	private RapServidores rapServidorAssinatura;
	private RapServidores rapServidorAlteracao;
	private RapServidores rapServidor;
	private ScoAutorizacaoFornecedorPedido scoAfPedido;
	private ScoItemAutorizacaoForn scoItensAutorizacaoForn;
	private ScoJustificativa scoJustificativa;
	private Set<SceItemRecebProvisorio> itensRecebProvisorio;
	private Set<ScoSolicitacaoProgramacaoEntrega> solicitacoesProgEntrega;
	private Set<SceItemRecbXProgrEntrega> itensRecebProvisorioXProgEntrega;
	
//	private ScoSolicitacaoServico solicitacaoServico;
//	private ScoSolicitacaoDeCompra solicitacaoCompra;
	
	public enum Fields{
		
		ITEM_AUTORIZACAO_FORN("scoItensAutorizacaoForn"),
		IAF_AFN_NUMERO("id.iafAfnNumero"),
		IAF_NUMERO("id.iafNumero"),
		ITEM_AUTORIZACAO_AFN_NUMERO("scoItensAutorizacaoForn.id.afnNumero"),
		ITEM_AUTORIZACAO_NUMERO("scoItensAutorizacaoForn.id.numero"),
		SCO_AF_PEDIDO("scoAfPedido"),
		AFE_NUMERO("scoAfPedido.id.numero"),
		AFE_AFN_NUMERO("scoAfPedido.id.afnNumero"),
		SCO_JUSTIFICATIVA("scoJustificativa"),
		SCO_JUSTIFICATIVA_CODIGO("scoJustificativa.codigo"),
		IND_ASSINATURA("indAssinatura"),
		IND_RECALCULO_MANUAL("indRecalculoManual"),
		IND_RECALCULO_AUTOMATICO("indRecalculoAutomatico"),
		QTDE_ENTREGUE("qtdeEntregue"),
		IND_ENTREGA_IMEDIATA("indEntregaImediata"),
		IND_ENTREGA_URGENTE("indEntregaUrgente"),
		DT_PREV_ENTREGA("dtPrevEntrega"),
		PARCELA("id.parcela"),
		SEQ("id.seq"),
		IND_PLANEJAMENTO("indPlanejamento"),
		IND_CANCELADA("indCancelada"),
		IND_IMPRESSA("indImpressa"),
		IND_TRAMITE_INTERNO("indTramiteInterno"),
		IND_EMPENHADA("indEmpenhada"),
		IND_EFETIVADA("indEfetivada"),
		VALOR_TOTAL("valorTotal"),		
		VALOR_EFETIVADO("valorEfetivado"),		
		QTDE("qtde"),
		DT_NECESSIDADE_HCPA("dtNecessidadeHcpa"),
		DT_ASSINATURA("dtAssinatura"),
		DT_GERACAO("dtGeracao"),
		DT_LIB_PLANEJAMENTO("dtLibPlanejamento"),
		IND_ENVIO_FORNECEDOR("indEnvioFornecedor"),
		OBSERVACAO("observacao"),
		ITENS_RECEBIMENTO_PROVISORIO("itensRecebProvisorio"),
		SOLICITACOES_PROG_ENTREGA("solicitacoesProgEntrega"),
		ITENS_RECEB_PROVISORIO_X_PROG_ENTREGA("itensRecebProvisorioXProgEntrega"),
		DT_ENTREGA("dtEntrega"),
		ID("id");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

    public ScoProgEntregaItemAutorizacaoFornecimento() {
    }

	@EmbeddedId
	@AttributeOverrides({
		@AttributeOverride(name = "iafAfnNumero", column = @Column(name = "IAF_AFN_NUMERO", nullable = false, precision = 7, scale = 0)),
		@AttributeOverride(name = "iafNumero", column = @Column(name = "IAF_NUMERO", nullable = false, precision = 3, scale = 0)),
		@AttributeOverride(name = "seq", column = @Column(name = "SEQ", nullable = false, precision = 7, scale = 0)),
		@AttributeOverride(name = "parcela", column = @Column(name = "PARCELA", nullable = false, precision = 7, scale = 0)) })
	public ScoProgEntregaItemAutorizacaoFornecimentoId getId() {
		return this.id;
	}

	public void setId(ScoProgEntregaItemAutorizacaoFornecimentoId id) {
		this.id = id;
	}

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ALTERACAO")
	public Date getDtAlteracao() {
		return this.dtAlteracao;
	}

	public void setDtAlteracao(Date dtAlteracao) {
		this.dtAlteracao = dtAlteracao;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ASSINATURA")
	public Date getDtAssinatura() {
		return this.dtAssinatura;
	}

	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ATUALIZACAO")
	public Date getDtAtualizacao() {
		return this.dtAtualizacao;
	}

	public void setDtAtualizacao(Date dtAtualizacao) {
		this.dtAtualizacao = dtAtualizacao;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_CANCELAMENTO")
	public Date getDtCancelamento() {
		return this.dtCancelamento;
	}

	public void setDtCancelamento(Date dtCancelamento) {
		this.dtCancelamento = dtCancelamento;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_ENTREGA")
	public Date getDtEntrega() {
		return this.dtEntrega;
	}

	public void setDtEntrega(Date dtEntrega) {
		this.dtEntrega = dtEntrega;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_GERACAO")
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_LIB_PLANEJAMENTO")
	public Date getDtLibPlanejamento() {
		return this.dtLibPlanejamento;
	}

	public void setDtLibPlanejamento(Date dtLibPlanejamento) {
		this.dtLibPlanejamento = dtLibPlanejamento;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_NECESSIDADE_HCPA")
	public Date getDtNecessidadeHcpa() {
		return this.dtNecessidadeHcpa;
	}

	public void setDtNecessidadeHcpa(Date dtNecessidadeHcpa) {
		this.dtNecessidadeHcpa = dtNecessidadeHcpa;
	}


	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="DT_PREV_ENTREGA")
	public Date getDtPrevEntrega() {
		return this.dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}


	@Column(name="ESL_SEQ_FATURA")
	public Integer getEslSeqFatura() {
		return this.eslSeqFatura;
	}

	public void setEslSeqFatura(Integer eslSeqFatura) {
		this.eslSeqFatura = eslSeqFatura;
	}


	@Column(name="IND_ASSINATURA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAssinatura() {
		return this.indAssinatura;
	}

	public void setIndAssinatura(Boolean indAssinatura) {
		this.indAssinatura = indAssinatura;
	}


	@Column(name="IND_CANCELADA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndCancelada() {
		return this.indCancelada;
	}

	public void setIndCancelada(Boolean indCancelada) {
		this.indCancelada = indCancelada;
	}

	@Column(name="IND_CONVERSAO_UNIDADE")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConversaoUnidade() {
		return this.indConversaoUnidade;
	}

	public void setIndConversaoUnidade(Boolean indConversaoUnidade) {
		this.indConversaoUnidade = indConversaoUnidade;
	}


	@Column(name="IND_EFETIVADA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEfetivada() {
		return this.indEfetivada;
	}

	public void setIndEfetivada(Boolean indEfetivada) {
		this.indEfetivada = indEfetivada;
	}


	@Column(name="IND_EMPENHADA")
	@Enumerated(EnumType.STRING)
	public DominioAfEmpenhada getIndEmpenhada() {
		return this.indEmpenhada;
	}

	public void setIndEmpenhada(DominioAfEmpenhada indEmpenhada) {
		this.indEmpenhada = indEmpenhada;
	}


	@Column(name="IND_ENTREGA_IMEDIATA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEntregaImediata() {
		return this.indEntregaImediata;
	}

	public void setIndEntregaImediata(Boolean indEntregaImediata) {
		this.indEntregaImediata = indEntregaImediata;
	}
	
	@Column(name="IND_ENTREGA_URGENTE")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEntregaUrgente() {
		return this.indEntregaUrgente;
	}

	public void setIndEntregaUrgente(Boolean indEntregaUrgente) {
		this.indEntregaUrgente = indEntregaUrgente;
	}

	@Column(name="IND_ENVIO_FORNECEDOR")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEnvioFornecedor() {
		return this.indEnvioFornecedor;
	}

	public void setIndEnvioFornecedor(Boolean indEnvioFornecedor) {
		this.indEnvioFornecedor = indEnvioFornecedor;
	}


	@Column(name="IND_IMPRESSA")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndImpressa() {
		return this.indImpressa;
	}

	public void setIndImpressa(Boolean indImpressa) {
		this.indImpressa = indImpressa;
	}


	@Column(name="IND_PLANEJAMENTO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPlanejamento() {
		return this.indPlanejamento;
	}

	public void setIndPlanejamento(Boolean indPlanejamento) {
		this.indPlanejamento = indPlanejamento;
	}


	@Column(name="IND_PUBLICADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPublicado() {
		return this.indPublicado;
	}

	public void setIndPublicado(Boolean indPublicado) {
		this.indPublicado = indPublicado;
	}


	@Column(name="IND_RECALCULO_AUTOMATICO")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRecalculoAutomatico() {
		return this.indRecalculoAutomatico;
	}

	public void setIndRecalculoAutomatico(Boolean indRecalculoAutomatico) {
		this.indRecalculoAutomatico = indRecalculoAutomatico;
	}


	@Column(name="IND_RECALCULO_MANUAL")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRecalculoManual() {
		return this.indRecalculoManual;
	}

	public void setIndRecalculoManual(Boolean indRecalculoManual) {
		this.indRecalculoManual = indRecalculoManual;
	}


	@Column(name="IND_TRAMITE_INTERNO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndTramiteInterno() {
		return this.indTramiteInterno;
	}

	public void setIndTramiteInterno(Boolean indTramiteInterno) {
		this.indTramiteInterno = indTramiteInterno;
	}


	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
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


	@Column(name="QTDE_ENTREGUE_A_MAIS")
	public Integer getQtdeEntregueAMais() {
		return this.qtdeEntregueAMais;
	}

	public void setQtdeEntregueAMais(Integer qtdeEntregueAMais) {
		this.qtdeEntregueAMais = qtdeEntregueAMais;
	}


	@Column(name="QTDE_ENTREGUE_PROV")
	public Integer getQtdeEntregueProv() {
		return this.qtdeEntregueProv;
	}

	public void setQtdeEntregueProv(Integer qtdeEntregueProv) {
		this.qtdeEntregueProv = qtdeEntregueProv;
	}


	@Column(name="SLC_NUMERO")
	public Integer getSlcNumero() {
		return this.slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}


	@Column(name="VALOR_EFETIVADO")
	public Double getValorEfetivado() {
		return this.valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}


	@Column(name="VALOR_TOTAL")
	public Double getValorTotal() {
		return this.valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}


	@Version
	@Column(name = "VERSION", nullable = false, precision = 3, scale = 0)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}


	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_LIB_PLANEJ", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_LIB_PLANEJ", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getRapServidorLibPlanej() {
		return this.rapServidorLibPlanej;
	}

	public void setRapServidorLibPlanej(RapServidores rapServidore1) {
		this.rapServidorLibPlanej = rapServidore1;
	}
	

	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_CANCELAMENTO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_CANCELAMENTO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getRapServidorCancelamento() {
		return this.rapServidorCancelamento;
	}

	public void setRapServidorCancelamento(RapServidores rapServidore2) {
		this.rapServidorCancelamento = rapServidore2;
	}
	

	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_ASSINATURA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_ASSINATURA", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getRapServidorAssinatura() {
		return this.rapServidorAssinatura;
	}

	public void setRapServidorAssinatura(RapServidores rapServidore3) {
		this.rapServidorAssinatura = rapServidore3;
	}
	

	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA_ALTERACAO", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO_ALTERACAO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getRapServidorAlteracao() {
		return this.rapServidorAlteracao;
	}

	public void setRapServidorAlteracao(RapServidores rapServidore4) {
		this.rapServidorAlteracao = rapServidore4;
	}
	

	//bi-directional many-to-one association to RapServidore
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="SER_MATRICULA", referencedColumnName="MATRICULA"),
		@JoinColumn(name="SER_VIN_CODIGO", referencedColumnName="VIN_CODIGO")
		})
	public RapServidores getRapServidor() {
		return this.rapServidor;
	}

	public void setRapServidor(RapServidores rapServidore5) {
		this.rapServidor = rapServidore5;
	}
	

	//bi-directional many-to-one association to ScoAutorizacaoFornecedorPedido
    @ManyToOne
	@JoinColumns({
		@JoinColumn(name="AFE_AFN_NUMERO", referencedColumnName="AFN_NUMERO"),
		@JoinColumn(name="AFE_NUMERO", referencedColumnName="NUMERO")
		})
	public ScoAutorizacaoFornecedorPedido getScoAfPedido() {
		return this.scoAfPedido;
	}

	public void setScoAfPedido(ScoAutorizacaoFornecedorPedido scoAfPedido) {
		this.scoAfPedido = scoAfPedido;
	}
	

	//bi-directional many-to-one association to ScoItensAutorizacaoForn
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="IAF_AFN_NUMERO", referencedColumnName="AFN_NUMERO", insertable=false, updatable=false),
		@JoinColumn(name="IAF_NUMERO", referencedColumnName="NUMERO", insertable=false, updatable=false)
		})
	public ScoItemAutorizacaoForn getScoItensAutorizacaoForn() {
		return this.scoItensAutorizacaoForn;
	}

	public void setScoItensAutorizacaoForn(ScoItemAutorizacaoForn scoItensAutorizacaoForn) {
		this.scoItensAutorizacaoForn = scoItensAutorizacaoForn;
	}
	

	//bi-directional many-to-one association to ScoJustificativa
    @ManyToOne
	@JoinColumn(name="JST_CODIGO")
	public ScoJustificativa getScoJustificativa() {
		return this.scoJustificativa;
	}

	public void setScoJustificativa(ScoJustificativa scoJustificativa) {
		this.scoJustificativa = scoJustificativa;
	}


	@OneToMany(mappedBy="progEntregaItemAf")
	public Set<SceItemRecebProvisorio> getItensRecebProvisorio() {
		return itensRecebProvisorio;
	}


	public void setItensRecebProvisorio(
			Set<SceItemRecebProvisorio> itensRecebProvisorio) {
		this.itensRecebProvisorio = itensRecebProvisorio;
	}
	
	@OneToMany(mappedBy="progEntregaItemAf")
	public Set<ScoSolicitacaoProgramacaoEntrega> getSolicitacoesProgEntrega() {
		return solicitacoesProgEntrega;
	}


	public void setSolicitacoesProgEntrega(
			Set<ScoSolicitacaoProgramacaoEntrega> solicitacoesProgEntrega) {
		this.solicitacoesProgEntrega = solicitacoesProgEntrega;
	}

	@OneToMany(mappedBy="scoProgEntregaItemAutorizacaoFornecimento")
	public Set<SceItemRecbXProgrEntrega> getItensRecebProvisorioXProgEntrega() {
		return itensRecebProvisorioXProgEntrega;
	}


	public void setItensRecebProvisorioXProgEntrega(
			Set<SceItemRecbXProgrEntrega> itensRecebProvisorioXProgEntrega) {
		this.itensRecebProvisorioXProgEntrega = itensRecebProvisorioXProgEntrega;
	}
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "DT_PREV_ENTREGA_APOS_ATRASO")
	public Date getDtPrevEntregaAposAtraso() {
		return dtPrevEntregaAposAtraso;
	}

	public void setDtPrevEntregaAposAtraso(Date dtPrevEntregaAposAtraso) {
		this.dtPrevEntregaAposAtraso = dtPrevEntregaAposAtraso;
	}
	
	
//	@Transient
//	public ScoSolicitacaoServico getSolicitacaoServico() {
//		return solicitacaoServico;
//	}
//	public void setSolicitacaoServico(ScoSolicitacaoServico solicitacaoServico) {
//		this.solicitacaoServico = solicitacaoServico;
//	}
	
//	@Transient
//	public ScoSolicitacaoDeCompra getSolicitacaoCompra() {
//		return solicitacaoCompra;
//	}
//	public void setSolicitacaoCompra(ScoSolicitacaoDeCompra solicitacaoCompra) {
//		this.solicitacaoCompra = solicitacaoCompra;
//	}

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
		if (!(obj instanceof ScoProgEntregaItemAutorizacaoFornecimento)) {
			return false;
		}
		ScoProgEntregaItemAutorizacaoFornecimento other = (ScoProgEntregaItemAutorizacaoFornecimento) obj;
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