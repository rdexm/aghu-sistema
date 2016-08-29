package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.ScoFornecedor;

public class ParcelaAfPendenteEntregaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2409972604859818525L;
	
	private Integer seq;
	private Integer numeroAf;
	private Integer numeroCp;
	private Integer numeroAfp;
	private Integer numeroItem;
	private Integer parcela;
	private Integer quantidade;
	private Integer quantidadeEntregue;
	private Integer contagemTemRecebimento;
	private Integer jstCodigo;
	private Date previsaoEntrega;
	private Date dataEntrega;
	private Integer saldo;
	private Integer fatorConversao;
	private String codigoUnidade;
	private String material;
	private String jstDescricao;
	private String indCancelada;
	private String indPlanejamento;
	private String indAssinatura;
	private String indEntregaImediata;
	private String indEnvioFornecedor;
	private String indTramiteInterno;
	private String corPrevisaoEntrega;
	private String corEntrega;
	private String indEmpenhada;
	private Date dataEnvioFornecedor;
	private Date dataEnvio;
	private Date dataEmpenho;
	private String corDataEnvioFornecedor;
	private String corDataEmpenho;
	private String observacao;
	private String corObsParcelaPendente;
	private String obsParcelaPendente;
	private Integer seqProgEntregaItemAutorizacaoFornecimento;
	private Integer codigoMaterial;
	private Date dataPrevEntregaAposAtraso;
	private String nomeMaterial;
	private Integer codigoGrupoMaterial;
	private String descricaoGrupoMaterial;
	private Integer numeroIaf;
	private Integer numeroAfeAfn;
	private Integer numeroIafAfnNumero;
	private Integer numeroFrn;
	private ScoFornecedor fornecedor;
	private String descricaoCodigoUnidade;
	
	public String getDescricaoCodigoUnidade() {
		return descricaoCodigoUnidade;
	}

	public void setDescricaoCodigoUnidade(String descricaoCodigoUnidade) {
		this.descricaoCodigoUnidade = descricaoCodigoUnidade;
	}

	public ParcelaAfPendenteEntregaVO() {
		
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public Integer getNumeroCp() {
		return numeroCp;
	}

	public Integer getNumeroAfp() {
		return numeroAfp;
	}

	public Integer getNumeroItem() {
		return numeroItem;
	}

	public Integer getParcela() {
		return parcela;
	}

	public Date getPrevisaoEntrega() {
		return previsaoEntrega;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public Integer getQuantidadeEntregue() {
		return quantidadeEntregue;
	}

	public Integer getSaldo() {
		return saldo;
	}

	public String getCodigoUnidade() {
		return codigoUnidade;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public String getMaterial() {
		return material;
	}


	public Integer getContagemTemRecebimento() {
		return contagemTemRecebimento;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public void setNumeroCp(Integer numeroCp) {
		this.numeroCp = numeroCp;
	}

	public void setNumeroAfp(Integer numeroAfp) {
		this.numeroAfp = numeroAfp;
	}

	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public void setPrevisaoEntrega(Date previsaoEntrega) {
		this.previsaoEntrega = previsaoEntrega;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public void setQuantidadeEntregue(Integer quantidadeEntregue) {
		this.quantidadeEntregue = quantidadeEntregue;
	}

	public void setSaldo(Integer saldo) {
		this.saldo = saldo;
	}

	public void setCodigoUnidade(String codigoUnidade) {
		this.codigoUnidade = codigoUnidade;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public void setContagemTemRecebimento(Integer contagemTemRecebimento) {
		this.contagemTemRecebimento = contagemTemRecebimento;
	}

	public String getIndEmpenhada() {
		return indEmpenhada;
	}

	public void setIndEmpenhada(String indEmpenhada) {
		this.indEmpenhada = indEmpenhada;
	}

	public Integer getJstCodigo() {
		return jstCodigo;
	}

	public void setJstCodigo(Integer jstCodigo) {
		this.jstCodigo = jstCodigo;
	}

	public String getCorPrevisaoEntrega() {
		return corPrevisaoEntrega;
	}

	public void setCorPrevisaoEntrega(String corPrevisaoEntrega) {
		this.corPrevisaoEntrega = corPrevisaoEntrega;
	}

	public Date getDataEntrega() {
		return dataEntrega;
	}

	public void setDataEntrega(Date dataEntrega) {
		this.dataEntrega = dataEntrega;
	}

	public String getCorEntrega() {
		return corEntrega;
	}

	public void setCorEntrega(String corEntrega) {
		this.corEntrega = corEntrega;
	}

	public Date getDataEnvioFornecedor() {
		return dataEnvioFornecedor;
	}

	public void setDataEnvioFornecedor(Date dataEnvioFornecedor) {
		this.dataEnvioFornecedor = dataEnvioFornecedor;
	}

	public Date getDataEnvio() {
		return dataEnvio;
	}

	public void setDataEnvio(Date dataEnvio) {
		this.dataEnvio = dataEnvio;
	}

	public Date getDataEmpenho() {
		return dataEmpenho;
	}

	public void setDataEmpenho(Date dataEmpenho) {
		this.dataEmpenho = dataEmpenho;
	}

	public String getCorDataEnvioFornecedor() {
		return corDataEnvioFornecedor;
	}

	public void setCorDataEnvioFornecedor(String corDataEnvioFornecedor) {
		this.corDataEnvioFornecedor = corDataEnvioFornecedor;
	}

	public String getCorDataEmpenho() {
		return corDataEmpenho;
	}

	public void setCorDataEmpenho(String corDataEmpenho) {
		this.corDataEmpenho = corDataEmpenho;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getCorObsParcelaPendente() {
		return corObsParcelaPendente;
	}

	public void setCorObsParcelaPendente(String corObsParcelaPendente) {
		this.corObsParcelaPendente = corObsParcelaPendente;
	}

	public String getIndCancelada() {
		return indCancelada;
	}

	public void setIndCancelada(String indCancelada) {
		this.indCancelada = indCancelada;
	}

	public String getIndPlanejamento() {
		return indPlanejamento;
	}

	public void setIndPlanejamento(String indPlanejamento) {
		this.indPlanejamento = indPlanejamento;
	}

	public String getIndAssinatura() {
		return indAssinatura;
	}

	public void setIndAssinatura(String indAssinatura) {
		this.indAssinatura = indAssinatura;
	}

	public String getIndEntregaImediata() {
		return indEntregaImediata;
	}

	public void setIndEntregaImediata(String indEntregaImediata) {
		this.indEntregaImediata = indEntregaImediata;
	}

	public String getIndEnvioFornecedor() {
		return indEnvioFornecedor;
	}

	public void setIndEnvioFornecedor(String indEnvioFornecedor) {
		this.indEnvioFornecedor = indEnvioFornecedor;
	}

	public String getIndTramiteInterno() {
		return indTramiteInterno;
	}

	public void setIndTramiteInterno(String indTramiteInterno) {
		this.indTramiteInterno = indTramiteInterno;
	}

	public String getJstDescricao() {
		return jstDescricao;
	}

	public void setJstDescricao(String jstDescricao) {
		this.jstDescricao = jstDescricao;
	}

	public String getObsParcelaPendente() {
		return obsParcelaPendente;
	}

	public void setObsParcelaPendente(String obsParcelaPendente) {
		this.obsParcelaPendente = obsParcelaPendente;
	}

	public Integer getSeqProgEntregaItemAutorizacaoFornecimento() {
		return seqProgEntregaItemAutorizacaoFornecimento;
	}

	public void setSeqProgEntregaItemAutorizacaoFornecimento(Integer seqProgEntregaItemAutorizacaoFornecimento) {
		this.seqProgEntregaItemAutorizacaoFornecimento = seqProgEntregaItemAutorizacaoFornecimento;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Date getDataPrevEntregaAposAtraso() {
		return dataPrevEntregaAposAtraso;
	}

	public void setDataPrevEntregaAposAtraso(Date dataPrevEntregaAposAtraso) {
		this.dataPrevEntregaAposAtraso = dataPrevEntregaAposAtraso;
	}

	public String getNomeMaterial() {
		return nomeMaterial;
	}

	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	public Integer getCodigoGrupoMaterial() {
		return codigoGrupoMaterial;
	}

	public void setCodigoGrupoMaterial(Integer codigoGrupoMaterial) {
		this.codigoGrupoMaterial = codigoGrupoMaterial;
	}

	public String getDescricaoGrupoMaterial() {
		return descricaoGrupoMaterial;
	}

	public void setDescricaoGrupoMaterial(String descricaoGrupoMaterial) {
		this.descricaoGrupoMaterial = descricaoGrupoMaterial;
	}

	public Integer getNumeroIaf() {
		return numeroIaf;
	}

	public void setNumeroIaf(Integer numeroIaf) {
		this.numeroIaf = numeroIaf;
	}

	public Integer getNumeroAfeAfn() {
		return numeroAfeAfn;
	}

	public void setNumeroAfeAfn(Integer numeroAfeAfn) {
		this.numeroAfeAfn = numeroAfeAfn;
	}

	public Integer getNumeroIafAfnNumero() {
		return numeroIafAfnNumero;
	}

	public void setNumeroIafAfnNumero(Integer numeroIafAfnNumero) {
		this.numeroIafAfnNumero = numeroIafAfnNumero;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public ScoFornecedor getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(ScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}

	
}
