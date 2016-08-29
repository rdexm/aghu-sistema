package br.gov.mec.aghu.compras.vo;

import br.gov.mec.aghu.core.commons.BaseBean;

public class SociosFornecedoresVO implements BaseBean {

	private static final long serialVersionUID = 1693618429925255443L;
	private Integer seq;
	private String nome;
	private String rg;
	private Long cpf;
	private Long qtdFornecedor;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public String getRg() {
		return rg;
	}

	public void setRg(String rg) {
		this.rg = rg;
	}

	public Long getCpf() {
		return cpf;
	}

	public void setCpf(Long cpf) {
		this.cpf = cpf;
	}

	public Long getQtdFornecedor() {
		return qtdFornecedor;
	}

	public void setQtdFornecedor(Long qtdFornecedor) {
		this.qtdFornecedor = qtdFornecedor;
	}

}
