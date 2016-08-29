package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.core.persistence.BaseEntityId;

/**
 * The persistent class for the sco_itens_autorizacao_forn database table.
 * 
 */
@Entity
@Table(name="SCO_ITENS_AUTORIZACAO_FORN", schema = "AGH")
public class ScoItemAutorizacaoForn extends BaseEntityId<ScoItemAutorizacaoFornId> implements Serializable {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6427770602023758610L;
	private ScoItemAutorizacaoFornId id;
	private Date dtEstorno;
	private Date dtExclusao;
	private Integer fatorConversao;
	private Integer fatorConversaoForn;
	private Boolean indAnaliseProgrPlanej;
	private Boolean indConsignado;
	private Boolean indContrato;
	private Boolean indEstorno;
	private Boolean indExclusao;
	private Boolean indPreferencialCum;
	private Boolean indProgrEntgAuto;
	private Boolean indProgrEntgBloq;
	private Boolean indRecebimento;
	private DominioSituacaoAutorizacaoFornecedor indSituacao;
	private Double percAcrescimo;
	private Double percAcrescimoItem;
	private Double percDesconto;
	private Double percDescontoItem;
	private Double percIpi;
	private Float percVarPreco;
	private Integer qtdeRecebida;
	private Integer qtdeSolicitada;
	private Integer sequenciaAlteracao;
	private RapServidores servidor;
	private RapServidores servidorEstorno;
	private ScoUnidadeMedida unidadeMedida;	
	private ScoUnidadeMedida umdCodigoForn;
	private Double valorEfetivado;
	private Double valorUnitario;
	private Integer version;
	private ScoAutorizacaoForn autorizacoesForn;
	private ScoItemPropostaFornecedor itemPropostaFornecedor;
	private List<ScoFaseSolicitacao> scoFaseSolicitacao;
	private List<SceItemNotaRecebimento> itemNotaRecebimento;
	private Set<ScoItemAutorizacaoFornJn> itemAutorizacaoFornJn;
	private ScoNomeComercial nomeComercial;
	private ScoMarcaComercial marcaComercial;
	private ScoMarcaModelo modeloComercial;
	private Integer ipfPfrLctNumero;
	private List<SceItemRecebProvisorio> itemRecebProvisorios;
	private List<SceItemEntrSaidSemLicitacao> itemEntrSaidSemLicitacao;
	private List<ScoProgEntregaItemAutorizacaoFornecimento> progEntregas;
	private List<ScoCumXProgrEntrega> scoCumXProgrEntrega;
	private List<SceItemNotaPreRecebimento> itemNotaPreRecebimento;
	private Set<SceItemRecebProvisorio> sceItensRecebProvisorio;
	private ScoFornecedor scoFornecedor;
	
	private Set<ScoProgEntregaItemAutorizacaoFornecimento> scoProgEntregaItemAutorizacaoFornecimento = new HashSet<ScoProgEntregaItemAutorizacaoFornecimento>(0);

    @EmbeddedId
	public ScoItemAutorizacaoFornId getId() {
		return this.id;
	}

	public void setId(ScoItemAutorizacaoFornId id) {
		this.id = id;
	}
	
	@Column(name="DT_ESTORNO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}

	@Column(name="DT_EXCLUSAO")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtExclusao() {
		return this.dtExclusao;
	}

	public void setDtExclusao(Date dtExclusao) {
		this.dtExclusao = dtExclusao;
	}

	@Column(name="FATOR_CONVERSAO")
	public Integer getFatorConversao() {
		return this.fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	@Column(name="FATOR_CONVERSAO_FORN")
	public Integer getFatorConversaoForn() {
		return this.fatorConversaoForn;
	}

	public void setFatorConversaoForn(Integer fatorConversaoForn) {
		this.fatorConversaoForn = fatorConversaoForn;
	}

	@Column(name="IND_ANALISE_PROGR_PLANEJ")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndAnaliseProgrPlanej() {
		return this.indAnaliseProgrPlanej;
	}

	public void setIndAnaliseProgrPlanej(Boolean indAnaliseProgrPlanej) {
		this.indAnaliseProgrPlanej = indAnaliseProgrPlanej;
	}

	@Column(name="IND_CONSIGNADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndConsignado() {
		return this.indConsignado;
	}

	public void setIndConsignado(Boolean indConsignado) {
		this.indConsignado = indConsignado;
	}

	@Column(name="IND_CONTRATO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndContrato() {
		return this.indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	@Column(name="IND_ESTORNO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstorno() {
		return this.indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}

	@Column(name="IND_EXCLUSAO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndExclusao() {
		return this.indExclusao;
	}

	public void setIndExclusao(Boolean indExclusao) {
		this.indExclusao = indExclusao;
	}

	@Column(name="IND_PREFERENCIAL_CUM")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndPreferencialCum() {
		return this.indPreferencialCum;
	}

	public void setIndPreferencialCum(Boolean indPreferencialCum) {
		this.indPreferencialCum = indPreferencialCum;
	}
	
	@Column(name="IND_PROGR_ENTG_AUTO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndProgrEntgAuto() {
		return this.indProgrEntgAuto;
	}

	public void setIndProgrEntgAuto(Boolean indProgrEntgAuto) {
		this.indProgrEntgAuto = indProgrEntgAuto;
	}
	
	@Column(name="IND_PROGR_ENTG_BLOQ")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndProgrEntgBloq() {
		return this.indProgrEntgBloq;
	}

	public void setIndProgrEntgBloq(Boolean indProgrEntgBloq) {
		this.indProgrEntgBloq = indProgrEntgBloq;
	}
	
	@Column(name="IND_RECEBIMENTO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndRecebimento() {
		return this.indRecebimento;
	}

	public void setIndRecebimento(Boolean indRecebimento) {
		this.indRecebimento = indRecebimento;
	}

	@Column(name="IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacaoAutorizacaoFornecedor getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAutorizacaoFornecedor indSituacao) {
		this.indSituacao = indSituacao;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MCM_CODIGO", referencedColumnName = "CODIGO")
	public ScoMarcaComercial getMarcaComercial() {
		return marcaComercial;
	}

	public void setMarcaComercial(ScoMarcaComercial marcaComercial) {
		this.marcaComercial = marcaComercial;
	}
	
	@Column(name="PERC_ACRESCIMO")
	public Double getPercAcrescimo() {
		return this.percAcrescimo;
	}

	public void setPercAcrescimo(Double percAcrescimo) {
		this.percAcrescimo = percAcrescimo;
	}
	
	@Column(name="PERC_ACRESCIMO_ITEM")
	public Double getPercAcrescimoItem() {
		return this.percAcrescimoItem;
	}

	public void setPercAcrescimoItem(Double percAcrescimoItem) {
		this.percAcrescimoItem = percAcrescimoItem;
	}

	@Column(name="PERC_DESCONTO")
	public Double getPercDesconto() {
		return this.percDesconto;
	}

	public void setPercDesconto(Double percDesconto) {
		this.percDesconto = percDesconto;
	}

	@Column(name="PERC_DESCONTO_ITEM")
	public Double getPercDescontoItem() {
		return this.percDescontoItem;
	}

	public void setPercDescontoItem(Double percDescontoItem) {
		this.percDescontoItem = percDescontoItem;
	}
	
	@Column(name="PERC_IPI")
	public Double getPercIpi() {
		return this.percIpi;
	}

	public void setPercIpi(Double percIpi) {
		this.percIpi = percIpi;
	}

	@Column(name="PERC_VAR_PRECO")
	public Float getPercVarPreco() {
		return this.percVarPreco;
	}

	public void setPercVarPreco(Float percVarPreco) {
		this.percVarPreco = percVarPreco;
	}

	@Column(name="QTDE_RECEBIDA")
	public Integer getQtdeRecebida() {
		return this.qtdeRecebida;
	}

	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}
	
	@Column(name="QTDE_SOLICITADA")
	public Integer getQtdeSolicitada() {
		return this.qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	
	@Column(name="SEQUENCIA_ALTERACAO")
	public Integer getSequenciaAlteracao() {
		return this.sequenciaAlteracao;
	}

	public void setSequenciaAlteracao(Integer sequenciaAlteracao) {
		this.sequenciaAlteracao = sequenciaAlteracao;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA_ESTORNO", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO_ESTORNO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorEstorno() {
		return servidorEstorno;
	}
	
	public void setServidorEstorno(RapServidores servidorEstorno) {
		this.servidorEstorno = servidorEstorno;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUnidadeMedida() {
		return unidadeMedida;
	}

	public void setUnidadeMedida(ScoUnidadeMedida unidadeMedida) {
		this.unidadeMedida = unidadeMedida;
	}

		
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO_FORN", referencedColumnName = "CODIGO")
	public ScoUnidadeMedida getUmdCodigoForn() {
		return this.umdCodigoForn;
	}

	public void setUmdCodigoForn(ScoUnidadeMedida umdCodigoForn) {
		this.umdCodigoForn = umdCodigoForn;
	}
	
	@Column(name="VALOR_EFETIVADO")
	public Double getValorEfetivado() {
		return this.valorEfetivado;
	}

	public void setValorEfetivado(Double valorEfetivado) {
		this.valorEfetivado = valorEfetivado;
	}
	
	@Column(name="VALOR_UNITARIO")
	public Double getValorUnitario() {
		return this.valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	@Column(name = "VERSION", length= 7)
	@Version
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	//bi-directional many-to-one association to ScoAutorizacoesForn
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumn(name="AFN_NUMERO",  insertable=false, updatable=false, nullable=false)
	public ScoAutorizacaoForn getAutorizacoesForn() {
		return autorizacoesForn;
	}

	public void setAutorizacoesForn(ScoAutorizacaoForn autorizacoesForn) {
		this.autorizacoesForn = autorizacoesForn;
	}

	//bi-directional many-to-one association to ScoItemPropostasFornecedor
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="IPF_NUMERO", referencedColumnName="NUMERO"),
		@JoinColumn(name="IPF_PFR_FRN_NUMERO", referencedColumnName="PFR_FRN_NUMERO"),
		@JoinColumn(name="IPF_PFR_LCT_NUMERO", referencedColumnName="PFR_LCT_NUMERO")
		})
	public ScoItemPropostaFornecedor getItemPropostaFornecedor() {
		return itemPropostaFornecedor;
	}
	
	public void setItemPropostaFornecedor(
			ScoItemPropostaFornecedor itemPropostaFornecedor) {
		this.itemPropostaFornecedor = itemPropostaFornecedor;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="IPF_PFR_FRN_NUMERO", referencedColumnName="NUMERO", insertable = false, updatable = false)})
	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}
	
	@ManyToOne(fetch=FetchType.LAZY)
	@JoinColumns({
		@JoinColumn(name="NC_MCM_CODIGO", referencedColumnName="MCM_CODIGO"),
		@JoinColumn(name="NC_NUMERO", referencedColumnName="NUMERO")})
	public ScoNomeComercial getNomeComercial() {
		return nomeComercial;
	}

	public void setNomeComercial(ScoNomeComercial nomeComercial) {
		this.nomeComercial = nomeComercial;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns( {
		@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
		@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	public void setItemAutorizacaoFornJn(Set<ScoItemAutorizacaoFornJn> itemAutorizacaoFornJn) {
		this.itemAutorizacaoFornJn = itemAutorizacaoFornJn;
	}

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "SCO_IAF_JN", schema = "AGH", joinColumns = {
			@JoinColumn(name = "AFN_NUMERO", nullable = false, updatable = false),
			@JoinColumn(name = "NUMERO", nullable = false, updatable = false) }, 
			inverseJoinColumns = { @JoinColumn(name = "SEQ_JN", nullable = false, updatable = false)})
	public Set<ScoItemAutorizacaoFornJn> getItemAutorizacaoFornJn() {
		return itemAutorizacaoFornJn;
	}
		
	@OneToMany(mappedBy="itemAutorizacaoForn", fetch = FetchType.LAZY)
	public List<ScoFaseSolicitacao> getScoFaseSolicitacao() {
		return scoFaseSolicitacao;
	}

	public void setScoFaseSolicitacao(List<ScoFaseSolicitacao> scoFaseSolicitacao) {
		this.scoFaseSolicitacao = scoFaseSolicitacao;
	}
	
	@OneToMany(mappedBy="itemAutorizacaoForn", fetch = FetchType.LAZY)
	public List<SceItemNotaRecebimento> getItemNotaRecebimento() {
		return itemNotaRecebimento;
	}

	public void setItemNotaRecebimento(List<SceItemNotaRecebimento> itemNotaRecebimento) {
		this.itemNotaRecebimento = itemNotaRecebimento;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "MOM_MCM_CODIGO", referencedColumnName = "MCM_CODIGO"),
			@JoinColumn(name = "MOM_SEQP", referencedColumnName = "SEQP") })
	public ScoMarcaModelo getModeloComercial() {
		return modeloComercial;
	}

	public void setModeloComercial(ScoMarcaModelo modeloComercial) {
		this.modeloComercial = modeloComercial;
	}

	@Column(name="IPF_PFR_LCT_NUMERO", insertable = false, updatable = false)
	public Integer getIpfPfrLctNumero() {
		return ipfPfrLctNumero;
	}

	public void setIpfPfrLctNumero(Integer ipfPfrLctNumero) {
		this.ipfPfrLctNumero = ipfPfrLctNumero;
	}
	
	@OneToMany(mappedBy = "scoItensAutorizacaoForn", fetch = FetchType.LAZY)
	public List<SceItemRecebProvisorio> getItemRecebProvisorios() {
		return itemRecebProvisorios;
	}

	public void setItemRecebProvisorios(List<SceItemRecebProvisorio> itemRecebProvisorios) {
		this.itemRecebProvisorios = itemRecebProvisorios;
	}

	@OneToMany(mappedBy = "itemAutorizacaoForn")
	public List<SceItemEntrSaidSemLicitacao> getItemEntrSaidSemLicitacao() {
		return itemEntrSaidSemLicitacao;
	}

	public void setItemEntrSaidSemLicitacao(
			List<SceItemEntrSaidSemLicitacao> itemEntrSaidSemLicitacao) {
		this.itemEntrSaidSemLicitacao = itemEntrSaidSemLicitacao;
	}
	
	@OneToMany(mappedBy = "scoItensAutorizacaoForn", fetch = FetchType.LAZY)
	public List<ScoProgEntregaItemAutorizacaoFornecimento> getProgEntregas() {
		return progEntregas;
	}

	public void setProgEntregas(
			List<ScoProgEntregaItemAutorizacaoFornecimento> progEntregas) {
		this.progEntregas = progEntregas;
	}

	@OneToMany(mappedBy = "scoItensAutorizacaoForn", fetch = FetchType.LAZY)
	public List<ScoCumXProgrEntrega> getScoCumXProgrEntrega() {
		return scoCumXProgrEntrega;
	}

	public void setScoCumXProgrEntrega(List<ScoCumXProgrEntrega> scoCumXProgrEntrega) {
		this.scoCumXProgrEntrega = scoCumXProgrEntrega;
	}
	
	@OneToMany(mappedBy="scoItemAutorizacaoForn", fetch = FetchType.LAZY)
	public List<SceItemNotaPreRecebimento> getItemNotaPreRecebimento() {
		return itemNotaPreRecebimento;
	}

	public void setItemNotaPreRecebimento(
			List<SceItemNotaPreRecebimento> itemNotaPreRecebimento) {
		this.itemNotaPreRecebimento = itemNotaPreRecebimento;
	}

	@OneToMany(mappedBy = "scoItensAutorizacaoForn", fetch = FetchType.LAZY)
	public Set<SceItemRecebProvisorio> getSceItensRecebProvisorio() {
		return sceItensRecebProvisorio;
	}

	public void setSceItensRecebProvisorio(Set<SceItemRecebProvisorio> sceItensRecebProvisorio) {
		this.sceItensRecebProvisorio = sceItensRecebProvisorio;
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
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		ScoItemAutorizacaoForn other = (ScoItemAutorizacaoForn) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}
		

	public enum Fields {
		ID("id"), 		
		PROPOSTA_FORNECEDOR_LICITACAO_ID("itemPropostaFornecedor.id.pfrLctNumero"),
		PROPOSTA_FORNECEDOR_FORNECEDOR_ID("itemPropostaFornecedor.id.pfrFrnNumero"),
		NUMERO_PROPOSTA("itemPropostaFornecedor.id.numero"),
		ID_AUT_FORN_NUMERO("id.afnNumero"), 
		NUMERO("id.numero"), 		
		DATA_ESTORNO("dtEstorno"),
		DATA_EXCLUSAO("dtExclusao"),
		FATOR_CONVERSAO("fatorConversao"),
		FATOR_CONVERSAO_FORM("fatorConversaoForn"),
		IND_ANALISE_PROGR_PLANEJ("indAnaliseProgrPlanej"),
		IND_CONSIGNADO("indConsignado"),
		IND_CONTRATO("indContrato"),
		IND_ESTORNO("indEstorno"),
		IND_EXCLUSAO("indExclusao"),
		IND_PREFERENCIAL_CUM("indPreferencialCum"),
		IND_PROGR_ENTG_AUTO("indProgrEntgAuto"),
		IND_PROGR_ENTRG_BLOQ("indProgrEntgBloq"),
		IND_PROGR_ENTG_BLOQ("indProgrEntgBloq"),
		IND_RECEBIMENTO("indRecebimento"),
		IND_SITUACAO("indSituacao"),
		FASES_SOLICITACAO("scoFaseSolicitacao"),
		AUTORIZACAO_FORN("autorizacoesForn"),
		AFN_NUMERO("autorizacoesForn"),
		AUTORIZACAO_FORN_NUMERO("autorizacoesForn.numero"), 
		UNIDADE("unidadeMedida"),
		SEQUENCIA_ALTERACAO("sequenciaAlteracao"),
		VALOR_UNITARIO("valorUnitario"),
		UNIDADE_CODIGO("unidadeMedida.codigo"),
		ITEM_NOTA_RECEBIMENTO("itemNotaRecebimento"),
		ITEM_PROPOSTA_FORNECEDOR("itemPropostaFornecedor"),
		ITEM_ENTR_SAID_SEM_LICITACAO("itemEntrSaidSemLicitacao"),
		ITENS_AF_JN("itemAutorizacaoFornJn"),
		NOME_COMERCIAL("nomeComercial"),
		MARCA_COMERCIAL("marcaComercial"),
		MARCA_COMERCIAL_CODIGO("marcaComercial.codigo"),
		MARCA_MODELO("modeloComercial"),
		MOM_MCM_CODIGO("modeloComercial.id.mcmCodigo"),
		MOM_SEQP("modeloComercial.id.seqp"),
		QUANTIDADE_SOLICITADA("qtdeSolicitada"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		QUANTIDADE_RECEBIDA("qtdeRecebida"),
		SERVIDOR("servidor"),
		SERVIDOR_ESTORNO("servidorEstorno"),
		VALOR_EFETIVADO("valorEfetivado"),
		PERC_VAR_PRECO("percVarPreco"),
		PROG_ENTREGAS("progEntregas"),
		UMD_CODIGO_FORN("umdCodigoForn"),
		UNIDADE_CODIGO_FORN("umdCodigoForn.codigo"),
		SCO_CUM_X_PROG("scoCumXProgrEntrega"),
		IPF_PFR_LCT_NUMERO("ipfPfrLctNumero"),
		ITEM_NOTA_PRE_RECEBIMENTO("itemNotaPreRecebimento"),
		PROG_ENTREGA_AUTORIZACAO_FORNECIMENTO("scoProgEntregaItemAutorizacaoFornecimento"),
		SCO_FORNECEDOR("scoFornecedor"),
		SCE_ITENS_RECEB_PROVISORIO("sceItensRecebProvisorio");
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}	
	}

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "scoItensAutorizacaoForn")
	public Set<ScoProgEntregaItemAutorizacaoFornecimento> getScoProgEntregaItemAutorizacaoFornecimento() {
		return scoProgEntregaItemAutorizacaoFornecimento;
	}

	public void setScoProgEntregaItemAutorizacaoFornecimento(
			Set<ScoProgEntregaItemAutorizacaoFornecimento> scoProgEntregaItemAutorizacaoFornecimento) {
		this.scoProgEntregaItemAutorizacaoFornecimento = scoProgEntregaItemAutorizacaoFornecimento;
	}

}