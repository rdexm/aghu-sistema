package br.gov.mec.aghu.estoque.vo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;



public class NotaRecebimentoVO {
	
	private String tipoDocumento;
	private String razaoSocialFornecedor;
	private String numeroConta;
	private String numeroEmpenho;
	private String anoListaItens;
	private String afnNumero;
	private String naturezaDespesa;
	private String incisoArtigoLicitacao;
	private String cgcFornecedor;
	private String codigoModalidadeLicitacao;
	private String recebimento;
	private String confirmacao;
	
	private Integer modalidadeEmpenho;
	private Long numeroDocumento;
	private Integer numeroFornecedor;
	private Integer numeroBanco;
	private Integer numeroAgencia;
	private Integer numeroNotaReceb;
	private Long frfCodigo;
	private Integer codigoConvenio;
	private Integer artigoLicitacao;
	private Integer numeroTitulo;
	
	private Date dtGeracaoNota;
	private Date dtEmissaoDocumento;
	private Date dtGeracaoAf;
	private Date dtCompetencia;
	private Date dtVencimento;
	private Date dtRecebimento;

	private Double valorEmpenho;
	
	
	private List<NotaRecebimentoItensVO> itensNotaRecebimento = new ArrayList<NotaRecebimentoItensVO>();

	public String getTipoDocumento() {
		return tipoDocumento;
	}

	public void setTipoDocumento(String tipoDocumento) {
		this.tipoDocumento = tipoDocumento;
	}

	public Long getNumeroDocumento() {
		return numeroDocumento;
	}

	public void setNumeroDocumento(Long numeroDocumento) {
		this.numeroDocumento = numeroDocumento;
	}

	public Date getDtEmissaoDocumento() {
		return dtEmissaoDocumento;
	}

	public void setDtEmissaoDocumento(Date dtEmissaoDocumento) {
		this.dtEmissaoDocumento = dtEmissaoDocumento;
	}

	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public String getCgcFornecedor() {
		return cgcFornecedor;
	}

	public void setCgcFornecedor(String cgcFornecedor) {
		this.cgcFornecedor = cgcFornecedor;
	}

	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	public Integer getNumeroBanco() {
		return numeroBanco;
	}

	public void setNumeroBanco(Integer numeroBanco) {
		this.numeroBanco = numeroBanco;
	}

	public Integer getNumeroAgencia() {
		return numeroAgencia;
	}

	public void setNumeroAgencia(Integer numeroAgencia) {
		this.numeroAgencia = numeroAgencia;
	}

	public String getNumeroConta() {
		return numeroConta;
	}

	public void setNumeroConta(String numeroConta) {
		this.numeroConta = numeroConta;
	}

	public Integer getNumeroNotaReceb() {
		return numeroNotaReceb;
	}

	public void setNumeroNotaReceb(Integer numeroNotaReceb) {
		this.numeroNotaReceb = numeroNotaReceb;
	}

	public Date getDtGeracaoNota() {
		return dtGeracaoNota;
	}

	public void setDtGeracaoNota(Date dtGeracaoNota) {
		this.dtGeracaoNota = dtGeracaoNota;
	}

	public String getNumeroEmpenho() {
		return numeroEmpenho;
	}

	public void setNumeroEmpenho(String numeroEmpenho) {
		this.numeroEmpenho = numeroEmpenho;
	}

	public String getAnoListaItens() {
		return anoListaItens;
	}

	public void setAnoListaItens(String anoListaItens) {
		this.anoListaItens = anoListaItens;
	}

	public Long getFrfCodigo() {
		return frfCodigo;
	}

	public void setFrfCodigo(Long frfCodigo) {
		this.frfCodigo = frfCodigo;
	}

	public String getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(String afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(Integer codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public Integer getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(Integer modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public Double getValorEmpenho() {
		return valorEmpenho;
	}

	public void setValorEmpenho(Double valorEmpenho) {
		this.valorEmpenho = valorEmpenho;
	}

	public String getNaturezaDespesa() {
		return naturezaDespesa;
	}

	public void setNaturezaDespesa(String naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}

	public Date getDtGeracaoAf() {
		return dtGeracaoAf;
	}

	public void setDtGeracaoAf(Date dtGeracaoAf) {
		this.dtGeracaoAf = dtGeracaoAf;
	}

	public String getCodigoModalidadeLicitacao() {
		return codigoModalidadeLicitacao;
	}

	public void setCodigoModalidadeLicitacao(String codigoModalidadeLicitacao) {
		this.codigoModalidadeLicitacao = codigoModalidadeLicitacao;
	}

	public Integer getArtigoLicitacao() {
		return artigoLicitacao;
	}

	public void setArtigoLicitacao(Integer artigoLicitacao) {
		this.artigoLicitacao = artigoLicitacao;
	}

	public String getIncisoArtigoLicitacao() {
		return incisoArtigoLicitacao;
	}

	public void setIncisoArtigoLicitacao(String incisoArtigoLicitacao) {
		this.incisoArtigoLicitacao = incisoArtigoLicitacao;
	}

	public Date getDtCompetencia() {
		return dtCompetencia;
	}

	public void setDtCompetencia(Date dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}
	
	public List<NotaRecebimentoItensVO> getItensNotaRecebimento() {
		return itensNotaRecebimento;
	}

	public void setItensNotaRecebimento(List<NotaRecebimentoItensVO> itensNotaRecebimento) {
		this.itensNotaRecebimento = itensNotaRecebimento;
	}

	public Integer getNumeroTitulo() {
		return numeroTitulo;
	}

	public void setNumeroTitulo(Integer numeroTitulo) {
		this.numeroTitulo = numeroTitulo;
	}

	public Date getDtVencimento() {
		return dtVencimento;
	}

	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}

	public String getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(String recebimento) {
		this.recebimento = recebimento;
	}

	public String getConfirmacao() {
		return confirmacao;
	}

	public void setConfirmacao(String confirmacao) {
		this.confirmacao = confirmacao;
	}

	public Date getDtRecebimento() {
		return dtRecebimento;
	}

	public void setDtRecebimento(Date dtRecebimento) {
		this.dtRecebimento = dtRecebimento;
	}
}
