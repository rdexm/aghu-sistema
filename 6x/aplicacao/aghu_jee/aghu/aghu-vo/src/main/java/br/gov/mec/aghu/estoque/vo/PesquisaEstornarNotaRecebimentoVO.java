package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoAutorizacaoForn;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class PesquisaEstornarNotaRecebimentoVO implements BaseBean {

	private static final long serialVersionUID = 5078198922525449735L;
	
	private Integer seq;
	private ScoAutorizacaoForn autorizacaoFornecimento; 
	private SceDocumentoFiscalEntrada documentoFiscalEntrada;
	private Date dtGeracao;
	private Boolean debitoNotaRecebimento;
	private Boolean estorno;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public ScoAutorizacaoForn getAutorizacaoFornecimento() {
		return autorizacaoFornecimento;
	}

	public void setAutorizacaoFornecimento(
			ScoAutorizacaoForn autorizacaoFornecimento) {
		this.autorizacaoFornecimento = autorizacaoFornecimento;
	}

	public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
		return documentoFiscalEntrada;
	}

	public void setDocumentoFiscalEntrada(
			SceDocumentoFiscalEntrada documentoFiscalEntrada) {
		this.documentoFiscalEntrada = documentoFiscalEntrada;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public Boolean getDebitoNotaRecebimento() {
		return debitoNotaRecebimento;
	}

	public void setDebitoNotaRecebimento(Boolean debitoNotaRecebimento) {
		this.debitoNotaRecebimento = debitoNotaRecebimento;
	}
	
	public Boolean getEstorno() {
		return estorno;
	}
	
	public void setEstorno(Boolean estorno) {
		this.estorno = estorno;
	}
	
	/**
	 * Gera uma descrição do débito da nota de recebimento
	 * @return
	 */
	public String getDebitoNotaRecebimentoDescricao() {
		if(getDebitoNotaRecebimento() == null){
			return null;
		}
		return getDebitoNotaRecebimento() ? "Sim" : "Não";
	}
	
	/**
	 * Gera uma descrição do fornecedor através do número e razão social
	 * @return
	 */
	public String getFornecedorDescricao() {
		
		if (getAutorizacaoFornecimento() != null && getAutorizacaoFornecimento().getPropostaFornecedor() != null &&
				getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor() != null){
			ScoFornecedor fornecedor  = getAutorizacaoFornecimento().getPropostaFornecedor().getFornecedor();
			return fornecedor.getNumero() + " - " + fornecedor.getRazaoSocial();
		}
		return "";
	}
	
	/**
	 * Gera uma descrição da AF através do número e complemento
	 * @return
	 */
	public String getAfDescricao() {
		
		if (getAutorizacaoFornecimento() != null && getAutorizacaoFornecimento().getNumero() != null &&
				getAutorizacaoFornecimento().getNroComplemento() != null){
			return getAutorizacaoFornecimento().getNumero() + "/" + getAutorizacaoFornecimento().getNroComplemento();
		}
		return "";
	}
	
}
