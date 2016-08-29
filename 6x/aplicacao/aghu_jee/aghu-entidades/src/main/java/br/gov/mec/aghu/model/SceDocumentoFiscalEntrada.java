package br.gov.mec.aghu.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
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
import javax.persistence.Transient;
import javax.persistence.Version;


import org.hibernate.annotations.Type;
import org.hibernate.validator.constraints.Length;

import br.gov.mec.aghu.dominio.DominioSituacaoDocumentoFiscalEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.dominio.DominioTipoDocumentoFiscalEntrada;
import br.gov.mec.aghu.core.persistence.BaseEntitySeq;

/**
 * The persistent class for the sce_documento_fiscal_entradas database table.
 * 
 */
@Entity
@Table(name = "SCE_DOCUMENTO_FISCAL_ENTRADAS")
@SequenceGenerator(name = "sceDfeSq1", sequenceName = "AGH.SCE_DFE_SQ1", allocationSize = 1)
public class SceDocumentoFiscalEntrada extends BaseEntitySeq<Integer> implements Serializable {
		/**
	 * 
	 */
	private static final long serialVersionUID = 885424518920409485L;
	private Integer seq;
	private Float aliquotaIcms;
	private Float aliquotaIpi;
	private String chaveCodigoBarras;
	private DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada;
	private Date dtAutorizada;
	private Date dtEmissao;
	private Date dtEntrada;
	private Date dtGeracao;
	private Date dtLibSapiens;
	private Date dtVerifDanfe1;
	private Date dtVerifDanfe2;
	private Boolean indLibSapiens;
	private String indOperacao;
	private String indOrigemNf;
	private DominioSituacaoDocumentoFiscalEntrada indSituacao;
	private Integer indVerifDanfe1;
	private Integer indVerifDanfe2;
	private Integer nopCodigo;
	private Long numero;
	private String observacao;
	private String observacaoIcms;
	private String observacaoIpi;
	private RapServidores servidor;
	private RapServidores servidorLibSapiens;
	private String serie;
	private String subSerie;
	private DominioTipoDocumentoEntrada tipo;
	private Double valorBaseIcms;
	private Double valorBaseIcmsSubst;
	private Double valorBaseImpostoImport;
	private Double valorDar;
	private Double valorDesconto;
	private Double valorDespAcessorias;
	private Double valorDivs;
	private Double valorFrete;
	private Double valorIcms;
	private Double valorIcmsSubst;
	private Double valorImpostoImport;
	private Double valorInss;
	private Double valorIpi;
	private Double valorSeguro;
	private Double valorTotalItens;
	private Double valorTotalNf;
	private Double valorTotalNfServ;
	private Integer version;
	private ScoFornecedor fornecedor;
	private SceFornecedorEventual fornecedorEventual;
	private Set<SceNotaRecebimento> notasRecebimento;
	private Set<SceNotaRecebimentoHist> notasRecebimentoHist;
	private Set<SceEntradaSaidaSemLicitacao> entradasSaidasSemLicitacao;
	private Set<SceDevolucaoFornecedor> devolucoesFornecedores;
	private List<SceNotaRecebProvisorio> notaRecebProvisorios;


	public SceDocumentoFiscalEntrada() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO, generator = "sceDfeSq1")
	public Integer getSeq() {
		return this.seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	@Column(name = "ALIQUOTA_ICMS", precision = 5)
	public Float getAliquotaIcms() {
		return this.aliquotaIcms;
	}

	public void setAliquotaIcms(Float aliquotaIcms) {
		this.aliquotaIcms = aliquotaIcms;
	}

	@Column(name = "ALIQUOTA_IPI", precision = 5)
	public Float getAliquotaIpi() {
		return this.aliquotaIpi;
	}

	public void setAliquotaIpi(Float aliquotaIpi) {
		this.aliquotaIpi = aliquotaIpi;
	}

	@Column(name = "CHAVE_CODIGO_BARRAS", length = 50)
	@Length(max = 50)
	public String getChaveCodigoBarras() {
		return this.chaveCodigoBarras;
	}

	public void setChaveCodigoBarras(String chaveCodigoBarras) {
		this.chaveCodigoBarras = chaveCodigoBarras;
	}

	@Column(name = "DFE_TYPE", nullable = false)
	@Enumerated(EnumType.STRING)
	public DominioTipoDocumentoFiscalEntrada getTipoDocumentoFiscalEntrada() {
		return tipoDocumentoFiscalEntrada;
	}
	
	public void setTipoDocumentoFiscalEntrada(DominioTipoDocumentoFiscalEntrada tipoDocumentoFiscalEntrada) {
		this.tipoDocumentoFiscalEntrada = tipoDocumentoFiscalEntrada;
	}


	@Column(name = "DT_AUTORIZADA")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtAutorizada() {
		return this.dtAutorizada;
	}

	public void setDtAutorizada(Date dtAutorizada) {
		this.dtAutorizada = dtAutorizada;
	}

	@Column(name = "DT_EMISSAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEmissao() {
		return this.dtEmissao;
	}

	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	@Column(name = "DT_ENTRADA", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtEntrada() {
		return this.dtEntrada;
	}

	public void setDtEntrada(Date dtEntrada) {
		this.dtEntrada = dtEntrada;
	}

	@Column(name = "DT_GERACAO", nullable = false)
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtGeracao() {
		return this.dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	@Column(name = "DT_LIB_SAPIENS")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtLibSapiens() {
		return this.dtLibSapiens;
	}

	public void setDtLibSapiens(Date dtLibSapiens) {
		this.dtLibSapiens = dtLibSapiens;
	}

	@Column(name = "DT_VERIF_DANFE_1")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtVerifDanfe1() {
		return this.dtVerifDanfe1;
	}

	public void setDtVerifDanfe1(Date dtVerifDanfe1) {
		this.dtVerifDanfe1 = dtVerifDanfe1;
	}

	@Column(name = "DT_VERIF_DANFE_2")
	@Temporal(TemporalType.TIMESTAMP)
	public Date getDtVerifDanfe2() {
		return dtVerifDanfe2;
	}

	public void setDtVerifDanfe2(Date dtVerifDanfe2) {
		this.dtVerifDanfe2 = dtVerifDanfe2;
	}

	@Column(name = "IND_LIB_SAPIENS")
	@Type(type = "br.gov.mec.aghu.core.persistence.type.BooleanUserType")
	public Boolean getIndLibSapiens() {
		return this.indLibSapiens;
	}

	public void setIndLibSapiens(Boolean indLibSapiens) {
		this.indLibSapiens = indLibSapiens;
	}

	@Column(name = "IND_OPERACAO")
	public String getIndOperacao() {
		return this.indOperacao;
	}

	public void setIndOperacao(String indOperacao) {
		this.indOperacao = indOperacao;
	}

	@Column(name = "IND_ORIGEM_NF")
	public String getIndOrigemNf() {
		return this.indOrigemNf;
	}

	public void setIndOrigemNf(String indOrigemNf) {
		this.indOrigemNf = indOrigemNf;
	}


	@Column(name = "IND_SITUACAO")
	@Enumerated(EnumType.STRING)
	public DominioSituacaoDocumentoFiscalEntrada getIndSituacao() {
		return this.indSituacao;
	}

	public void setIndSituacao(DominioSituacaoDocumentoFiscalEntrada indSituacao) {
		this.indSituacao = indSituacao;
	}

	@Column(name = "IND_VERIF_DANFE_1")
	public Integer getIndVerifDanfe1() {
		return this.indVerifDanfe1;
	}

	public void setIndVerifDanfe1(Integer indVerifDanfe1) {
		this.indVerifDanfe1 = indVerifDanfe1;
	}

	@Column(name = "IND_VERIF_DANFE_2")
	public Integer getIndVerifDanfe2() {
		return this.indVerifDanfe2;
	}

	public void setIndVerifDanfe2(Integer indVerifDanfe2) {
		this.indVerifDanfe2 = indVerifDanfe2;
	}

	@Column(name = "NOP_CODIGO")
	public Integer getNopCodigo() {
		return this.nopCodigo;
	}

	public void setNopCodigo(Integer nopCodigo) {
		this.nopCodigo = nopCodigo;
	}
	
	@Column(name = "NUMERO", precision = 10, scale = 0)
	public Long getNumero() {
		return this.numero;
	}

	public void setNumero(Long numero) {
		this.numero = numero;
	}
	
	@Column(name = "OBSERVACAO", length = 120)
	@Length(max = 120)
	public String getObservacao() {
		return this.observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	@Column(name = "OBSERVACAO_ICMS")
	public String getObservacaoIcms() {
		return this.observacaoIcms;
	}

	public void setObservacaoIcms(String observacaoIcms) {
		this.observacaoIcms = observacaoIcms;
	}

	@Column(name = "OBSERVACAO_IPI")
	public String getObservacaoIpi() {
		return this.observacaoIpi;
	}

	public void setObservacaoIpi(String observacaoIpi) {
		this.observacaoIpi = observacaoIpi;
	}
	

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA_LIB_SAPIENS", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO_LIB_SAPIENS", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidorLibSapiens() {
		return servidorLibSapiens;
	}

	public void setServidorLibSapiens(RapServidores servidorLibSapiens) {
		this.servidorLibSapiens = servidorLibSapiens;
	}

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumns({
			@JoinColumn(name = "SER_MATRICULA", referencedColumnName = "MATRICULA"),
			@JoinColumn(name = "SER_VIN_CODIGO", referencedColumnName = "VIN_CODIGO") })
	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}
	
	@Column(name = "SERIE", length = 3)
	@Length(max = 3)
	public String getSerie() {
		return this.serie;
	}

	public void setSerie(String serie) {
		this.serie = serie;
	}

	@Column(name = "SUB_SERIE", precision = 7, scale = 0)
	public String getSubSerie() {
		return this.subSerie;
	}

	public void setSubSerie(String subSerie) {
		this.subSerie = subSerie;
	}

	@Column(name = "TIPO")
	@Enumerated(EnumType.STRING)
	public DominioTipoDocumentoEntrada getTipo() {
		return this.tipo;
	}

	public void setTipo(DominioTipoDocumentoEntrada tipo) {
		this.tipo = tipo;
	}

	@Column(name = "VALOR_BASE_ICMS")
	public Double getValorBaseIcms() {
		return this.valorBaseIcms;
	}

	public void setValorBaseIcms(Double valorBaseIcms) {
		this.valorBaseIcms = valorBaseIcms;
	}

	@Column(name = "VALOR_BASE_ICMS_SUBST")
	public Double getValorBaseIcmsSubst() {
		return this.valorBaseIcmsSubst;
	}

	public void setValorBaseIcmsSubst(Double valorBaseIcmsSubst) {
		this.valorBaseIcmsSubst = valorBaseIcmsSubst;
	}

	@Column(name = "VALOR_BASE_IMPOSTO_IMPORT")
	public Double getValorBaseImpostoImport() {
		return this.valorBaseImpostoImport;
	}

	public void setValorBaseImpostoImport(Double valorBaseImpostoImport) {
		this.valorBaseImpostoImport = valorBaseImpostoImport;
	}

	@Column(name = "VALOR_DAR")
	public Double getValorDar() {
		return this.valorDar;
	}

	public void setValorDar(Double valorDar) {
		this.valorDar = valorDar;
	}

	@Column(name = "VALOR_DESCONTO")
	public Double getValorDesconto() {
		return this.valorDesconto;
	}

	public void setValorDesconto(Double valorDesconto) {
		this.valorDesconto = valorDesconto;
	}

	@Column(name = "VALOR_DESP_ACESSORIAS")
	public Double getValorDespAcessorias() {
		return this.valorDespAcessorias;
	}

	public void setValorDespAcessorias(Double valorDespAcessorias) {
		this.valorDespAcessorias = valorDespAcessorias;
	}

	@Column(name = "VALOR_DIVS")
	public Double getValorDivs() {
		return this.valorDivs;
	}

	public void setValorDivs(Double valorDivs) {
		this.valorDivs = valorDivs;
	}

	@Column(name = "VALOR_FRETE")
	public Double getValorFrete() {
		return this.valorFrete;
	}

	public void setValorFrete(Double valorFrete) {
		this.valorFrete = valorFrete;
	}

	@Column(name = "VALOR_ICMS")
	public Double getValorIcms() {
		return this.valorIcms;
	}

	public void setValorIcms(Double valorIcms) {
		this.valorIcms = valorIcms;
	}

	@Column(name = "VALOR_ICMS_SUBST")
	public Double getValorIcmsSubst() {
		return this.valorIcmsSubst;
	}

	public void setValorIcmsSubst(Double valorIcmsSubst) {
		this.valorIcmsSubst = valorIcmsSubst;
	}

	@Column(name = "VALOR_IMPOSTO_IMPORT")
	public Double getValorImpostoImport() {
		return this.valorImpostoImport;
	}

	public void setValorImpostoImport(Double valorImpostoImport) {
		this.valorImpostoImport = valorImpostoImport;
	}

	@Column(name = "VALOR_INSS")
	public Double getValorInss() {
		return this.valorInss;
	}

	public void setValorInss(Double valorInss) {
		this.valorInss = valorInss;
	}

	@Column(name = "VALOR_IPI", precision = 15)
	public Double getValorIpi() {
		return this.valorIpi;
	}

	public void setValorIpi(Double valorIpi) {
		this.valorIpi = valorIpi;
	}

	@Column(name = "VALOR_SEGURO")
	public Double getValorSeguro() {
		return this.valorSeguro;
	}

	public void setValorSeguro(Double valorSeguro) {
		this.valorSeguro = valorSeguro;
	}

	@Column(name = "VALOR_TOTAL_ITENS")
	public Double getValorTotalItens() {
		return this.valorTotalItens;
	}

	public void setValorTotalItens(Double valorTotalItens) {
		this.valorTotalItens = valorTotalItens;
	}

	@Column(name = "VALOR_TOTAL_NF")
	public Double getValorTotalNf() {
		return this.valorTotalNf;
	}

	public void setValorTotalNf(Double valorTotalNf) {
		this.valorTotalNf = valorTotalNf;
	}

	@Column(name = "VALOR_TOTAL_NF_SERV")
	public Double getValorTotalNfServ() {
		return this.valorTotalNfServ;
	}

	public void setValorTotalNfServ(Double valorTotalNfServ) {
		this.valorTotalNfServ = valorTotalNfServ;
	}

	@Version
	@Column(name = "VERSION", nullable = false)
	public Integer getVersion() {
		return this.version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	// bi-directional many-to-one association to SceFornecedorEventual
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FEV_SEQ")
	public SceFornecedorEventual getFornecedorEventual() {
		return this.fornecedorEventual;
	}

	public void setFornecedorEventual(SceFornecedorEventual fornecedorEventual) {
		this.fornecedorEventual = fornecedorEventual;
	}

	// bi-directional many-to-one association to ScoFornecedore
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "FRN_NUMERO")
	public ScoFornecedor getFornecedor() {
		return this.fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	@OneToMany(mappedBy = "documentoFiscalEntrada")
	public Set<SceNotaRecebimento> getNotasRecebimento() {
		return notasRecebimento;
	}

	public void setNotasRecebimento(Set<SceNotaRecebimento> notasRecebimento) {
		this.notasRecebimento = notasRecebimento;
	}
	
	@OneToMany(mappedBy = "documentoFiscalEntrada")
	public Set<SceNotaRecebimentoHist> getNotasRecebimentoHist() {
		return notasRecebimentoHist;
	}

	public void setNotasRecebimentoHist(Set<SceNotaRecebimentoHist> notasRecebimentoHist) {
		this.notasRecebimentoHist = notasRecebimentoHist;
	}
	//bi-directional many-to-one association to SceEntrSaidSemLicitacoe
	@OneToMany(mappedBy="sceDocumentoFiscalEntrada")
	public Set<SceEntradaSaidaSemLicitacao> getEntradasSaidasSemLicitacao() {
		return this.entradasSaidasSemLicitacao;
	}

	public void setEntradasSaidasSemLicitacao(Set<SceEntradaSaidaSemLicitacao> entradasSaidasSemLicitacao) {
		this.entradasSaidasSemLicitacao = entradasSaidasSemLicitacao;
	}
	
	//bi-directional many-to-one association to SceDevolucaoFornecedore
	@OneToMany(mappedBy="sceDocumentoFiscalEntrada")
	public Set<SceDevolucaoFornecedor> getDevolucoesFornecedores() {
		return this.devolucoesFornecedores;
	}

	public void setDevolucoesFornecedores(Set<SceDevolucaoFornecedor> devolucoesFornecedores) {
		this.devolucoesFornecedores = devolucoesFornecedores;
	}

	@OneToMany(mappedBy = "documentoFiscalEntrada")
	public List<SceNotaRecebProvisorio> getNotaRecebProvisorios() {
		return notaRecebProvisorios;
	}

	public void setNotaRecebProvisorios(List<SceNotaRecebProvisorio> notaRecebProvisorios) {
		this.notaRecebProvisorios = notaRecebProvisorios;
	}

	@Transient
	public String getSerieTipo() {
		String result = "";
		if (this.serie != null && this.tipo != null) {
			result = this.serie + " " + this.tipo.toString();
		} else {
			if(this.serie != null ){
				result = this.serie;
			}
			else{
				result = this.tipo.toString();
			}
		}
		return result;
	}
	
	
	public enum Fields {

		NUMERO("numero"), TIPO_DOCUMENTO_FISCAL_ENTRADA("tipoDocumentoFiscalEntrada"), DT_GERACAO("dtGeracao"), 
		DT_EMISSAO("dtEmissao"), DT_ENTRADA("dtEntrada"), 
		DT_AUTORIZADA("dtAutorizada"), FORNECEDOR("fornecedor"), SEQ("seq"), 
		SERIE("serie"), FORNECEDOR_EVENTUAL("fornecedorEventual"),
		TIPO("tipo"), VALOR_TOTAL_NF("valorTotalNf"),
		DEVOLUCAO_FORNECEDORES("devolucoesFornecedores")
		, CHAVE_CODIGO_BARRAS("chaveCodigoBarras")
		, FORNECEDOR_NUMERO("fornecedor.numero")
		,NOTA_RECEBIMENTO("notasRecebimento")
		,NOTA_RECEBIMENTO_HIST("notasRecebimentoHist");

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
		if (!(obj instanceof SceDocumentoFiscalEntrada)) {
			return false;
		}
		SceDocumentoFiscalEntrada other = (SceDocumentoFiscalEntrada) obj;
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