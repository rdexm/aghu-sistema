package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;

public class SceEntrSaidSemLicitacaoVO implements Serializable {

	private static final long serialVersionUID = 2330801588564306190L;
	
	private Integer eslSeq;
	private String tmvSigla;
	private Integer matCodigo;
	private Integer quantidade;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private String matNome;
	
	public Integer getEslSeq() {
		return eslSeq;
	}
	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}
	public String getTmvSigla() {
		return tmvSigla;
	}
	public void setTmvSigla(String tmvSigla) {
		this.tmvSigla = tmvSigla;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public Integer getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}	
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
	public String getMatNome() {
		return matNome;
	}
	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}

}
