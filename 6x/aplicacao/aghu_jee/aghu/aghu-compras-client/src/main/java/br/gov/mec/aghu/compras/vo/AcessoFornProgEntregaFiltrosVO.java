package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfpPublicado;
import br.gov.mec.aghu.model.VScoFornecedor;

public class AcessoFornProgEntregaFiltrosVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;

	private Integer numeroAF;
	private Short complemento;
	private Integer numeroAFP;
	private DominioAfpPublicado publicacao;
	private VScoFornecedor fornecedor;
	private Date dataPublicacaoInicial;
	private Date dataPublicacaoFinal;
	private Date dataAcessoInicial;
	private Date dataAcessoFinal;

	public Integer getNumeroAF() {
		return numeroAF;
	}
	public void setNumeroAF(Integer numeroAF) {
		this.numeroAF = numeroAF;
	}
	public Short getComplemento() {
		return complemento;
	}
	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}
	public Integer getNumeroAFP() {
		return numeroAFP;
	}
	public void setNumeroAFP(Integer numeroAFP) {
		this.numeroAFP = numeroAFP;
	}
	public DominioAfpPublicado getPublicacao() {
		return publicacao;
	}
	public void setPublicacao(DominioAfpPublicado publicacao) {
		this.publicacao = publicacao;
	}
	public VScoFornecedor getFornecedor() {
		return fornecedor;
	}
	public void setFornecedor(VScoFornecedor fornecedor) {
		this.fornecedor = fornecedor;
	}
	public Date getDataPublicacaoInicial() {
		return dataPublicacaoInicial;
	}
	public void setDataPublicacaoInicial(Date dataPublicacaoInicial) {
		this.dataPublicacaoInicial = dataPublicacaoInicial;
	}
	public Date getDataPublicacaoFinal() {
		return dataPublicacaoFinal;
	}
	public void setDataPublicacaoFinal(Date dataPublicacaoFinal) {
		this.dataPublicacaoFinal = dataPublicacaoFinal;
	}
	public Date getDataAcessoInicial() {
		return dataAcessoInicial;
	}
	public void setDataAcessoInicial(Date dataAcessoInicial) {
		this.dataAcessoInicial = dataAcessoInicial;
	}
	public Date getDataAcessoFinal() {
		return dataAcessoFinal;
	}
	public void setDataAcessoFinal(Date dataAcessoFinal) {
		this.dataAcessoFinal = dataAcessoFinal;
	}

}
