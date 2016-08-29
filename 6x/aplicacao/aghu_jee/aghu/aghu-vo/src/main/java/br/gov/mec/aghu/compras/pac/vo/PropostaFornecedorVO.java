package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class PropostaFornecedorVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4551357317035500800L;
	
	private String nomeFornecedor;
	private String cnpjCpf;
	private Integer codigoMarca;
	private String marca;
	private String embalagem;
	private String apresentacao;
	private Integer fator;
	private Integer qtdOfertada;
	private BigDecimal valorEmbalagem;
	private String modelo;
	private String origem;
	private String moeda;
	private Integer qtdConvertida;
	private BigDecimal valorUnitario;
	private String parecer;
	private Integer numeroFornecedor;
	private Short numeroIdItem;
	private String motivoDesclassificacao;
	private String justificativaAutorizacaoUsuario;
	private String tipoSolicitacao;
	
	private List<FormaPagamentoPropostaVO> listaFormaPagamento;
	
	public String getNomeFornecedor() {
		return nomeFornecedor;
	}
	
	public void setNomeFornecedor(String nomeFornecedor) {
		this.nomeFornecedor = nomeFornecedor;
	}
	
	public String getCnpjCpf() {
		return cnpjCpf;
	}
	
	public void setCnpjCpf(String cnpjCpf) {
		this.cnpjCpf = cnpjCpf;
	}
	
	public String getMarca() {
		return marca;
	}
	
	public void setMarca(String marca) {
		this.marca = marca;
	}
	
	public String getEmbalagem() {
		return embalagem;
	}
	
	public void setEmbalagem(String embalagem) {
		this.embalagem = embalagem;
	}
	
	public String getApresentacao() {
		return apresentacao;
	}

	public void setApresentacao(String apresentacao) {
		this.apresentacao = apresentacao;
	}

	public Integer getFator() {
		return fator;
	}
	
	public void setFator(Integer fator) {
		this.fator = fator;
	}
	
	public Integer getQtdOfertada() {
		return qtdOfertada;
	}
	
	public void setQtdOfertada(Integer qtdOfertada) {
		this.qtdOfertada = qtdOfertada;
	}
	
	public BigDecimal getValorEmbalagem() {
		return valorEmbalagem;
	}
	
	public void setValorEmbalagem(BigDecimal valorEmbalagem) {
		this.valorEmbalagem = valorEmbalagem;
	}
	
	public String getModelo() {
		return modelo;
	}
	
	public void setModelo(String modelo) {
		this.modelo = modelo;
	}
	
	public String getOrigem() {
		return origem;
	}
	
	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public String getMoeda() {
		return moeda;
	}
	
	public void setMoeda(String moeda) {
		this.moeda = moeda;
	}
	
	public Integer getQtdConvertida() {
		return qtdConvertida;
	}
	
	public void setQtdConvertida(Integer qtdConvertida) {
		this.qtdConvertida = qtdConvertida;
	}
	
	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}
	
	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}
	
	public String getParecer() {
		return parecer;
	}
	
	public void setParecer(String parecer) {
		this.parecer = parecer;
	}
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	public Short getNumeroIdItem() {
		return numeroIdItem;
	}

	public void setNumeroIdItem(Short numeroIdItem) {
		this.numeroIdItem = numeroIdItem;
	}

	public String getMotivoDesclassificacao() {
		return motivoDesclassificacao;
	}

	public void setMotivoDesclassificacao(String motivoDesclassificacao) {
		this.motivoDesclassificacao = motivoDesclassificacao;
	}

	public String getJustificativaAutorizacaoUsuario() {
		return justificativaAutorizacaoUsuario;
	}

	public void setJustificativaAutorizacaoUsuario(
			String justificativaAutorizacaoUsuario) {
		this.justificativaAutorizacaoUsuario = justificativaAutorizacaoUsuario;
	}

	public List<FormaPagamentoPropostaVO> getListaFormaPagamento() {
		return listaFormaPagamento;
	}

	public void setListaFormaPagamento(
			List<FormaPagamentoPropostaVO> listaFormaPagamento) {
		this.listaFormaPagamento = listaFormaPagamento;
	}	

	public String getTipoSolicitacao() {
		return tipoSolicitacao;
	}

	
	public void setTipoSolicitacao(String tipoSolicitacao) {
		this.tipoSolicitacao = tipoSolicitacao;

	}

	public Integer getCodigoMarca() {
		return codigoMarca;
	}

	public void setCodigoMarca(Integer codigoMarca) {
		this.codigoMarca = codigoMarca;
	}	

}