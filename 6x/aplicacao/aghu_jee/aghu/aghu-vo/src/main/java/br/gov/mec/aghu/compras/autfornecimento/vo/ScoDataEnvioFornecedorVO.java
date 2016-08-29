package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.Date;


/**
 * 
 * 
 * 
 */
public class ScoDataEnvioFornecedorVO implements 	Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7026180078309862752L;

	private Date dataEnvioFornecedor;
	private Date dataEnvio;
	private Date dataEmpenho;
	private String observacao;
	private Integer seqProgEntregaItemAutorizacaoFornecimento;
	
	public ScoDataEnvioFornecedorVO() {}
	
	public ScoDataEnvioFornecedorVO(Date dataEnvioFornecedor, Date dataEnvio,
			Date dataEmpenho) {
		super();
		this.dataEnvioFornecedor = dataEnvioFornecedor;
		this.dataEnvio = dataEnvio;
		this.dataEmpenho = dataEmpenho;
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
	
	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	
	public Integer getSeqProgEntregaItemAutorizacaoFornecimento() {
		return seqProgEntregaItemAutorizacaoFornecimento;
	}

	public void setSeqProgEntregaItemAutorizacaoFornecimento(
			Integer seqProgEntregaItemAutorizacaoFornecimento) {
		this.seqProgEntregaItemAutorizacaoFornecimento = seqProgEntregaItemAutorizacaoFornecimento;
	}

	public enum Fields {
		DATA_ENVIO_FORNECEDOR("dataEnvioFornecedor"),
		DATA_ENVIO("dataEnvio"),
		DATA_EMPENHO("dataEmpenho"),
		OBSERVACAO("observacao"),
		SEQUENCIAL_PROGRAMACAO_ITEM("seqProgEntregaItemAutorizacaoFornecimento");
	
		private String field;
	
		private Fields(String field) {
			this.field = field;
		}
	
		public String toString() {
			return this.field;
		}
	}
}