package br.gov.mec.aghu.compras.pac.vo;

import java.math.BigDecimal;

/**
 * VO responsável pelas informações de proposta/item de fornecedor.
 * 
 * @author mlcruz
 */
public class ScoPropostaItemFornecedorVO {
	/** ID do PAC */
	private Integer pacId;
	
	/** Descrição do PAC */
	private String pacDescricao;
	
	/** ID do Item do PAC */
	private Short pacItemId;
	
	/** Fornecedor */
	private String fornecedor;
	
	/** Total */
	private BigDecimal total;
	
	// Getters/Setters

	public Integer getPacId() {
		return pacId;
	}

	public void setPacId(Integer pacId) {
		this.pacId = pacId;
	}

	public String getPacDescricao() {
		return pacDescricao;
	}

	public void setPacDescricao(String pacDescricao) {
		this.pacDescricao = pacDescricao;
	}

	public Short getPacItemId() {
		return pacItemId;
	}

	public void setPacItemId(Short pacItemId) {
		this.pacItemId = pacItemId;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public BigDecimal getTotal() {
		return total;
	}

	public void setTotal(BigDecimal total) {
		this.total = total;
	}
}