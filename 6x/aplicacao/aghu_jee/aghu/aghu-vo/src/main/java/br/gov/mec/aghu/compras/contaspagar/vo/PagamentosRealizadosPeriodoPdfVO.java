package br.gov.mec.aghu.compras.contaspagar.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PagamentosRealizadosPeriodoPdfVO implements BaseBean {

	/*-*-*-* Constante de Inicializacao *-*-*-*/
	private static final long serialVersionUID = 3483227679069715917L;
	
	
	/*-*-*-* Variaveis *-*-*-*/
	private Date dtPagamento;
	private Long cnpj;
	private Long cpf;
	private String fornecedor;
	private String doc;
	private Integer numero;
	private Integer titulo;
	private Integer nr;
	private Double valorTitulo;
	private Double desconto;
	private Double vlrAcresc;
	private Double tributos;
	private Double vlrDesc;
	private Double vlrDft;
	private Integer licitacao;
	private Short complemento;
	private Integer codVerba;
	private String  descVerba;
	private Integer codGrupoNatureza;
	private Byte codNatureza;
	private String  ntdDescricao;
	private Double valorPagamento;

	// Variavel para ser utilizada no iReport
	private String cgcCpfFornecedor;
	
	// Subreports
	private List<PagamentosRealizadosPeriodoPdfSubDocsVO> subRelatorioDocs;
	private List<PagamentosRealizadosPeriodoPdfSubLicitacoesVO> subRelatorioLicitacoes;
	
	
	/*-*-*-* Construtores *-*-*-*/
	public PagamentosRealizadosPeriodoPdfVO() {
		super();
	}
	

	/*-*-*-* Enum *-*-*-*/
	public enum Fields {
		DT_PAGAMENTO("dtPagamento"), CNPJ("cnpj"), CPF("cpf"), RAZAO_SOCIAL("fornecedor"), 
		TDP_DESCRICAO("doc"), NRO_DOCUMENTO("numero"),
		TITULO("titulo"), NRS_SEQ("nr"), TTL_VALOR("valorTitulo"), TRIBUTOS("tributos"),
		VLR_DESCONTO("desconto"), VLR_ACRESCIMO("vlrAcresc"), DFT_VALOR("vlrDft"), PFT_LCT_NUMERO("licitacao"), 
		NRO_COMPLEMENTO("complemento"), VBG_SEQ("codVerba"), VBG_DESCRICAO("descVerba"), NTD_GND_CODIGO("codGrupoNatureza"),
		NTD_CODIGO("codNatureza"), NTD_DESCRICAO("ntdDescricao"), VLR_PAGAMENTO("valorPagamento");
		
		String field;

		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}

	}

	
	/*-*-*-* Metodos Publicos *-*-*-*/
	private void setCgcCpfFornecedorFormatado()
  	{
  		if(cnpj != null && cnpj > 0)		{ setCgcCpfFornecedor(CoreUtil.formatarCNPJ(cnpj)); }
  		else if(cpf != null && cpf > 0)		{ setCgcCpfFornecedor(CoreUtil.formataCPF(cpf)); }
  	}
	
	private void setValorPagamentoCorreto(){
		if(valorPagamento != null && tributos != null) {
			valorPagamento -= tributos;
		}
	}

	
	/*-*-*-* Getters e Setters *-*-*-*/
	public Date getDtPagamento() {
		return dtPagamento;
	}

	public void setDtPagamento(Date dtPagamento) {
		this.dtPagamento = dtPagamento;
	}

	public Long getCnpj() {
		return cnpj;
	}

	public void setCnpj(Long cnpj) {
		setCgcCpfFornecedorFormatado();
		this.cnpj = cnpj;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		setCgcCpfFornecedorFormatado();
		this.cpf = cpf;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getDoc() {
		return doc;
	}

	public void setDoc(String doc) {
		this.doc = doc;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getTitulo() {
		return titulo;
	}

	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}

	public Integer getNr() {
		return nr;
	}

	public void setNr(Integer nr) {
		this.nr = nr;
	}

	public Double getValorTitulo() {
		return valorTitulo;
	}

	public void setValorTitulo(Double valorTitulo) {
		this.valorTitulo = valorTitulo;
	}

	public Double getDesconto() {
		return desconto;
	}

	public void setDesconto(Double desconto) {
		this.desconto = desconto;
	}

	public Double getVlrAcresc() {
		return vlrAcresc;
	}

	public void setVlrAcresc(Double vlrAcresc) {
		this.vlrAcresc = vlrAcresc;
	}

	public Double getTributos() {
		return tributos;
	}

	public void setTributos(Double tributos) {
		this.tributos = tributos;
		setValorPagamentoCorreto();
	}

	public Double getVlrDesc() {
		return vlrDesc;
	}

	public void setVlrDesc(Double vlrDesc) {
		this.vlrDesc = vlrDesc;
	}

	public Double getVlrDft() {
		return vlrDft;
	}

	public void setVlrDft(Double vlrDft) {
		this.vlrDft = vlrDft;
	}

	public Integer getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(Integer licitacao) {
		this.licitacao = licitacao;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getCodVerba() {
		return codVerba;
	}

	public void setCodVerba(Integer codVerba) {
		this.codVerba = codVerba;
	}

	public String getDescVerba() {
		return descVerba;
	}

	public void setDescVerba(String descVerba) {
		this.descVerba = descVerba;
	}

	public Integer getCodGrupoNatureza() {
		return codGrupoNatureza;
	}

	public void setCodGrupoNatureza(Integer codGrupoNatureza) {
		this.codGrupoNatureza = codGrupoNatureza;
	}

	public Byte getCodNatureza() {
		return codNatureza;
	}

	public void setCodNatureza(Byte codNatureza) {
		this.codNatureza = codNatureza;
	}

	public String getNtdDescricao() {
		return ntdDescricao;
	}

	public void setNtdDescricao(String ntdDescricao) {
		this.ntdDescricao = ntdDescricao;
	}

	public Double getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(Double valorPagamento) {
		this.valorPagamento = valorPagamento;
		setValorPagamentoCorreto();
	}

	public String getCgcCpfFornecedor() {
		return cgcCpfFornecedor;
	}

	public void setCgcCpfFornecedor(String cgcCpfFornecedor) {
		this.cgcCpfFornecedor = cgcCpfFornecedor;
	}

	public List<PagamentosRealizadosPeriodoPdfSubDocsVO> getSubRelatorioDocs() {
		return subRelatorioDocs;
	}

	public void setSubRelatorioDocs(
			List<PagamentosRealizadosPeriodoPdfSubDocsVO> subRelatorioDocs) {
		this.subRelatorioDocs = subRelatorioDocs;
	}

	public List<PagamentosRealizadosPeriodoPdfSubLicitacoesVO> getSubRelatorioLicitacoes() {
		return subRelatorioLicitacoes;
	}

	public void setSubRelatorioLicitacoes(
			List<PagamentosRealizadosPeriodoPdfSubLicitacoesVO> subRelatorioLicitacoes) {
		this.subRelatorioLicitacoes = subRelatorioLicitacoes;
	}
}
