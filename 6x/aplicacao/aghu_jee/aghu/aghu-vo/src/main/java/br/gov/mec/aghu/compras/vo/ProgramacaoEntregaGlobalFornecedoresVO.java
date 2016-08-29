package br.gov.mec.aghu.compras.vo;




public class ProgramacaoEntregaGlobalFornecedoresVO extends ProgramacaoEntregaGlobalVO {
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	
	
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}
	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}
	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}
}
