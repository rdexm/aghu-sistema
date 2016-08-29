package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.util.Date;


public class QuantidadePrescricoesDispensacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1685819790037019873L;

	private String dataEmissaoInicio;
	
	private String dataEmissaoFim;
	
	private Date dataEmissao;
	
	private Long quantidadePrescricoes;
	
	private Long quantidadeItensDispensados;

	
	public enum Fields {
		DATA_EMISSAO_INICIO("dataEmissaoInicio"), 
		DATA_EMISSAO_FIM("dataEmissaoFim"), 
		DATA_EMISSAO("dataEmissao"),
		QUANTIDADE_PRESCRICOES("quantidadePrescricoes"),	
		QUANTIDADE_ITENS_DISPENSADOS("quantidadeItensDispensados"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	public String getDataEmissaoInicio() {
		return dataEmissaoInicio;
	}


	public void setDataEmissaoInicio(String dataEmissaoInicio) {
		this.dataEmissaoInicio = dataEmissaoInicio;
	}


	public String getDataEmissaoFim() {
		return dataEmissaoFim;
	}


	public void setDataEmissaoFim(String dataEmissaoFim) {
		this.dataEmissaoFim = dataEmissaoFim;
	}


	public Date getDataEmissao() {
		return dataEmissao;
	}


	public void setDataEmissao(Date dataEmissao) {
		this.dataEmissao = dataEmissao;
	}


	public Long getQuantidadePrescricoes() {
		return quantidadePrescricoes;
	}


	public void setQuantidadePrescricoes(Long quantidadePrescricoes) {
		this.quantidadePrescricoes = quantidadePrescricoes;
	}


	public Long getQuantidadeItensDispensados() {
		return quantidadeItensDispensados;
	}


	public void setQuantidadeItensDispensados(Long quantidadeItensDispensados) {
		this.quantidadeItensDispensados = quantidadeItensDispensados;
	}
	
}
