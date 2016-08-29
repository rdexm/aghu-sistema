package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import br.gov.mec.aghu.core.persistence.BaseEntitySeq;


/**
 * The persistent class for the sce_entr_said_sem_licitacoes database table.
 * 
 */
@Entity
@SequenceGenerator(name="sceEslSq1", sequenceName="AGH.SCE_ESL_SQ1", allocationSize = 1)
@Table(name="SCE_ENTR_SAID_SEM_LICITACOES") 
public class SceEntradaSaidaSemLicitacao extends BaseEntitySeq<Integer> implements Serializable {	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7775821669643982393L;
	
	private Integer seq;
	private SceAlmoxarifado almoxarifados;
	private FccCentroCustos fccCentroCustosAplica;
	private FccCentroCustos cctCodigoSolic;
	private String contato;
	private String dadosComplementares;
	private Date dtEfetivacao;
	private Date dtEncerramento;
	private Date dtEstorno;
	private Date dtGeracao;
	private Date dtPrevDevolucao;
	private Date dtPrevEntrega;
	private Integer eslSeq;
	private SceFornecedorEventual sceFornecedorEventual;
	private Long foneContato;
	private ScoFornecedor scoFornecedor;
	private Integer frnNumeroTransp;
	private Integer frnNumero2;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private String indAdiantamentoAf;
	private String indEfetivado;
	private Boolean indEncerrado;
	private Boolean indEstorno;
	private String indFormaDevolucao;
	private String indRecebimento;
	private String localEntrega;
	private ScoMarcaComercial scoMarcaComercial;
	private ScoNomeComercial scoNomeComercial;
	private Integer nroControleFisico;
	private Integer nroPatrimonio;
	private Integer nroProjeto;
	private String observacao;
	private Integer qtdeConsumida;
	private Integer qtdeDevolvida;
	private Integer quantidade;
	private RapServidores servidor;
	private ScoSolicitacaoDeCompra scoSolicitacoesCompras;
	private ScoSolicitacaoServico scoSolicitacoesServico;
	private Double valor;
	private Integer version;
	private SceTipoMovimento sceTipoMovimento;
	private ScoMaterial scoMaterial;
	private ScoUnidadeMedida scoUnidadeMedida;
	private SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada;
	private SceItemBoc sceItemBoc;
	private SceItemRmps sceItemRmps;
	private List<SceItemRecebProvisorio> sceItensRecebProvisorio;
	

    public SceEntradaSaidaSemLicitacao() {
    }

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceEslSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "ALM_SEQ", referencedColumnName = "SEQ", nullable = false)
	public SceAlmoxarifado getAlmoxarifados() {
		return this.almoxarifados;
	}

	public void setAlmoxarifados(SceAlmoxarifado almoxarifados) {
		this.almoxarifados = almoxarifados;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_APLIC", nullable = false)
	public FccCentroCustos getFccCentroCustosAplica() {
		return this.fccCentroCustosAplica;
	}

	public void setFccCentroCustosAplica(FccCentroCustos fccCentroCustosAplica) {
		this.fccCentroCustosAplica = fccCentroCustosAplica;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "CCT_CODIGO_SOLIC", nullable = false)
	public FccCentroCustos getCctCodigoSolic() {
		return this.cctCodigoSolic;
	}

	public void setCctCodigoSolic(FccCentroCustos cctCodigoSolic) {
		this.cctCodigoSolic = cctCodigoSolic;
	}


	public String getContato() {
		return this.contato;
	}

	public void setContato(String contato) {
		this.contato = contato;
	}


	@Column(name="DADOS_COMPLEMENTARES")
	public String getDadosComplementares() {
		return this.dadosComplementares;
	}

	public void setDadosComplementares(String dadosComplementares) {
		this.dadosComplementares = dadosComplementares;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_EFETIVACAO", length = 7)
	public Date getDtEfetivacao() {
		return this.dtEfetivacao;
	}

	public void setDtEfetivacao(Date dtEfetivacao) {
		this.dtEfetivacao = dtEfetivacao;
	}


	@Temporal(TemporalType.DATE)
	@Column(name = "DT_ENCERRAMENTO", length = 7)
	public Date getDtEncerramento() {
		return this.dtEncerramento;
	}

	public void setDtEncerramento(Date dtEncerramento) {
		this.dtEncerramento = dtEncerramento;
	}


	@Temporal(TemporalType.DATE)
	@Column(name = "DT_ESTORNO", length = 7)
	public Date getDtEstorno() {
		return this.dtEstorno;
	}

	public void setDtEstorno(Date dtEstorno) {
		this.dtEstorno = dtEstorno;
	}


	@Temporal(TemporalType.DATE)
	@Column(name = "DT_GERACAO", nullable = false, length = 7)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_PREV_DEVOLUCAO", length = 7)
	public Date getDtPrevDevolucao() {
		return this.dtPrevDevolucao;
	}

	public void setDtPrevDevolucao(Date dtPrevDevolucao) {
		this.dtPrevDevolucao = dtPrevDevolucao;
	}

	@Temporal(TemporalType.DATE)
	@Column(name = "DT_PREV_ENTREGA", length = 7)
	public Date getDtPrevEntrega() {
		return this.dtPrevEntrega;
	}

	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}


	@Column(name = "ESL_SEQ", nullable = true,precision = 7, scale = 0)
	public Integer getEslSeq() {
		return this.eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FEV_SEQ", referencedColumnName = "SEQ")
	public SceFornecedorEventual getSceFornecedorEventual() {
		return this.sceFornecedorEventual;
	}

	public void setSceFornecedorEventual(SceFornecedorEventual sceFornecedorEventual) {
		this.sceFornecedorEventual = sceFornecedorEventual;
	}


	@Column(name="FONE_CONTATO")
	public Long getFoneContato() {
		return this.foneContato;
	}

	public void setFoneContato(Long foneContato) {
		this.foneContato = foneContato;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="FRN_NUMERO", referencedColumnName = "NUMERO")
	public ScoFornecedor getScoFornecedor() {
		return this.scoFornecedor;
	}

	public void setScoFornecedor(ScoFornecedor scoFornecedor) {
		this.scoFornecedor = scoFornecedor;
	}

	@Column(name="FRN_NUMERO_TRANSP")
	public Integer getFrnNumeroTransp() {
		return this.frnNumeroTransp;
	}

	public void setFrnNumeroTransp(Integer frnNumeroTransp) {
		this.frnNumeroTransp = frnNumeroTransp;
	}


	@Column(name="FRN_NUMERO2")
	public Integer getFrnNumero2() {
		return this.frnNumero2;
	}

	public void setFrnNumero2(Integer frnNumero2) {
		this.frnNumero2 = frnNumero2;
	}


	@Column(name="IAF_AFN_NUMERO")
	public Integer getIafAfnNumero() {
		return this.iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}


	@Column(name="IAF_NUMERO")
	public Integer getIafNumero() {
		return this.iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}


	@Column(name="IND_ADIANTAMENTO_AF")
	public String getIndAdiantamentoAf() {
		return this.indAdiantamentoAf;
	}

	public void setIndAdiantamentoAf(String indAdiantamentoAf) {
		this.indAdiantamentoAf = indAdiantamentoAf;
	}


	@Column(name="IND_EFETIVADO")
	public String getIndEfetivado() {
		return this.indEfetivado;
	}

	public void setIndEfetivado(String indEfetivado) {
		this.indEfetivado = indEfetivado;
	}


	@Column(name="IND_ENCERRADO")
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEncerrado() {
		return this.indEncerrado;
	}

	public void setIndEncerrado(Boolean indEncerrado) {
		this.indEncerrado = indEncerrado;
	}


	@Column(name = "IND_ESTORNO", nullable = false, length = 1)
	@org.hibernate.annotations.Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndEstorno() {
		return this.indEstorno;
	}

	public void setIndEstorno(Boolean indEstorno) {
		this.indEstorno = indEstorno;
	}


	@Column(name="IND_FORMA_DEVOLUCAO")
	public String getIndFormaDevolucao() {
		return this.indFormaDevolucao;
	}

	public void setIndFormaDevolucao(String indFormaDevolucao) {
		this.indFormaDevolucao = indFormaDevolucao;
	}


	@Column(name="IND_RECEBIMENTO")
	public String getIndRecebimento() {
		return this.indRecebimento;
	}

	public void setIndRecebimento(String indRecebimento) {
		this.indRecebimento = indRecebimento;
	}


	@Column(name="LOCAL_ENTREGA")
	public String getLocalEntrega() {
		return this.localEntrega;
	}

	public void setLocalEntrega(String localEntrega) {
		this.localEntrega = localEntrega;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="MCM_CODIGO", referencedColumnName = "CODIGO")
	public ScoMarcaComercial getScoMarcaComercial() {
		return this.scoMarcaComercial;
	}

	public void setScoMarcaComercial(ScoMarcaComercial scoMarcaComercial) {
		this.scoMarcaComercial = scoMarcaComercial;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "NC_MCM_CODIGO", referencedColumnName = "MCM_CODIGO"),
	@JoinColumn(name = "NC_NUMERO", referencedColumnName = "NUMERO") })
	public ScoNomeComercial getScoNomeComercial() {
		return this.scoNomeComercial;
	}

	public void setScoNomeComercial(ScoNomeComercial scoNomeComercial) {
		this.scoNomeComercial = scoNomeComercial;
	}

	@Column(name="NRO_CONTROLE_FISICO")
	public Integer getNroControleFisico() {
		return this.nroControleFisico;
	}

	public void setNroControleFisico(Integer nroControleFisico) {
		this.nroControleFisico = nroControleFisico;
	}


	@Column(name="NRO_PATRIMONIO")
	public Integer getNroPatrimonio() {
		return this.nroPatrimonio;
	}

	public void setNroPatrimonio(Integer nroPatrimonio) {
		this.nroPatrimonio = nroPatrimonio;
	}


	@Column(name="NRO_PROJETO")
	public Integer getNroProjeto() {
		return this.nroProjeto;
	}

	public void setNroProjeto(Integer nroProjeto) {
		this.nroProjeto = nroProjeto;
	}


	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}


	@Column(name="QTDE_CONSUMIDA")
	public Integer getQtdeConsumida() {
		return this.qtdeConsumida;
	}

	public void setQtdeConsumida(Integer qtdeConsumida) {
		this.qtdeConsumida = qtdeConsumida;
	}


	@Column(name="QTDE_DEVOLVIDA")
	public Integer getQtdeDevolvida() {
		return this.qtdeDevolvida;
	}

	public void setQtdeDevolvida(Integer qtdeDevolvida) {
		this.qtdeDevolvida = qtdeDevolvida;
	}

	@Column(name="QUANTIDADE")
	public Integer getQuantidade() {
		return this.quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}


	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA", nullable = false),
	@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO", nullable = false) })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLC_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoDeCompra getScoSolicitacoesCompras() {
		return scoSolicitacoesCompras;
	}

	public void setScoSolicitacoesCompras(
			ScoSolicitacaoDeCompra scoSolicitacoesCompras) {
		this.scoSolicitacoesCompras = scoSolicitacoesCompras;
	}
	
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IBO_BOC_SEQ", referencedColumnName = "BOC_SEQ"),
	@JoinColumn(name = "IBO_NRO_ITEM", referencedColumnName = "NRO_ITEM") })
	public SceItemBoc getSceItemBoc() {
		return this.sceItemBoc;
	}

	public void setSceItemBoc(SceItemBoc sceItemBoc) {
		this.sceItemBoc = sceItemBoc;
	}


	//bi-directional many-to-one association to ScoSolicitacaoServico
    @ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="SLS_NUMERO", referencedColumnName = "NUMERO")
	public ScoSolicitacaoServico getScoSolicitacoesServico() {
		return this.scoSolicitacoesServico;
	}

	public void setScoSolicitacoesServico(ScoSolicitacaoServico scoSolicitacoesServico) {
		this.scoSolicitacoesServico = scoSolicitacoesServico;
	}


	public Double getValor() {
		return this.valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}


	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	@ManyToOne(fetch = FetchType.LAZY)  
	@JoinColumns({ @JoinColumn(name = "TMV_SEQ", referencedColumnName = "SEQ", nullable = false, updatable=false), 
	@JoinColumn(name = "TMV_COMPLEMENTO", referencedColumnName = "COMPLEMENTO", nullable = false, updatable=false)})
	public SceTipoMovimento getSceTipoMovimento() {
		return this.sceTipoMovimento;
	}

	public void setSceTipoMovimento(SceTipoMovimento sceTipoMovimento) {
		this.sceTipoMovimento = sceTipoMovimento;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "MAT_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public ScoMaterial getScoMaterial() {
		return this.scoMaterial;
	}

	public void setScoMaterial(ScoMaterial scoMaterial) {
		this.scoMaterial = scoMaterial;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "UMD_CODIGO", referencedColumnName = "CODIGO", nullable = false)
	public ScoUnidadeMedida getScoUnidadeMedida() {
		return this.scoUnidadeMedida;
	}

	public void setScoUnidadeMedida(ScoUnidadeMedida scoUnidadeMedida) {
		this.scoUnidadeMedida = scoUnidadeMedida;
	}
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name="DFE_SEQ", referencedColumnName = "SEQ",insertable=false, updatable=false)
	public SceDocumentoFiscalEntrada getSceDocumentoFiscalEntrada() {
		return this.sceDocumentoFiscalEntrada;
	}

	public void setSceDocumentoFiscalEntrada(SceDocumentoFiscalEntrada sceDocumentoFiscalEntrada) {
		this.sceDocumentoFiscalEntrada = sceDocumentoFiscalEntrada;
		
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({ @JoinColumn(name = "IPS_RMP_SEQ", referencedColumnName = "RMP_SEQ"),
	@JoinColumn(name = "IPS_NUMERO", referencedColumnName = "NUMERO") })
	public SceItemRmps getSceItemRmps() {
		return this.sceItemRmps;
	}

	public void setSceItemRmps(SceItemRmps sceItemRmps) {
		this.sceItemRmps = sceItemRmps;
	}
	
	@OneToMany(fetch = FetchType.LAZY)
	@JoinColumn(name = "ESL_SEQ", referencedColumnName = "SEQ", insertable = false, updatable = false)
	public List<SceItemRecebProvisorio> getSceItensRecebProvisorio() {
		return sceItensRecebProvisorio;
	}

	public void setSceItensRecebProvisorio(List<SceItemRecebProvisorio> sceItensRecebProvisorio) {
		this.sceItensRecebProvisorio = sceItensRecebProvisorio;
	}

	public enum Fields {
		
		SEQ("seq"),
		MATERIAL("scoMaterial"),
		CODIGO_MATERIAL("scoMaterial.codigo"),
		DOCUMENTO_FISCAL_ENTRADA("sceDocumentoFiscalEntrada"),
		DOCUMENTO_FISCAL_ENTRADA_SEQ("sceDocumentoFiscalEntrada.seq"),
		DATA_GERACAO("dtGeracao"),
		IND_ESTORNO("indEstorno"),
		UNIDADE_MEDIDA("scoUnidadeMedida"),
		CODIGO_UNIDADE_MEDIDA("scoUnidadeMedida.codigo"),
		TIPO_MOVIMENTO("sceTipoMovimento"),
		FORNECEDOR("scoFornecedor"),
		TIPO_MOVIMENTO_SEQ("sceTipoMovimento.id.seq"),
		TIPO_MOVIMENTO_COMPLEMENTO("sceTipoMovimento.id.complemento"),
		MARCA_COMERCIAL("scoMarcaComercial"),
		IND_ENCERRADO("indEncerrado"),
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
		if (!(obj instanceof SceEntradaSaidaSemLicitacao)) {
			return false;
		}
		SceEntradaSaidaSemLicitacao other = (SceEntradaSaidaSemLicitacao) obj;
		if (getSeq() == null) {
			if (other.getSeq() != null) {
				return false;
			}
		} else if (!getSeq().equals(other.getSeq())) {
			return false;
		}
		return true;
	}
	// ##### GeradorEqualsHashCodeMain #####

}