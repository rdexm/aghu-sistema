package br.gov.mec.aghu.compras.contaspagar.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoTitulo;
import br.gov.mec.aghu.core.commons.BaseBean;

public class TituloPendenteVO implements BaseBean, Serializable  {
	/*-*-*-* Constante de Inicializacao *-*-*-*/
	private static final long serialVersionUID = -2963363107018831169L;

	
	/*-*-*-* Variaveis e Constantes *-*-*-*/
	private String numeroSerie;
	private String licitacao;
	private Long cgcFornecedor;
	private Long cpfFornecedor;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private Integer titulo;
	private Short nroParcela;
	private Date dtEmissao;
	private Date dtVencimento;
	private Double valor;
	private Integer numeroNF;
	private String indDocumentacao;
	private DominioSituacaoTitulo indSituacao;
	
	// Variaveis especificas para o iReport
	private String cgcCpfFornecedor;
	private String situacao;
	

	/*-*-*-* Getters e Setters *-*-*-*/
	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	public String getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(String licitacao) {
		this.licitacao = licitacao;
	}

	public Long getCgcFornecedor() {
		return cgcFornecedor;
	}

	public void setCgcFornecedor(Long cgcFornecedor) {
		setCgcCpfFornecedor(cgcFornecedor);
		this.cgcFornecedor = cgcFornecedor;
	}

	public Long getCpfFornecedor() {
		return cpfFornecedor;
	}

	public void setCpfFornecedor(Long cpfFornecedor) {
		setCgcCpfFornecedor(cpfFornecedor);
		this.cpfFornecedor = cpfFornecedor;
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

	public Integer getTitulo() {
		return titulo;
	}

	public void setTitulo(Integer titulo) {
		this.titulo = titulo;
	}

	public Short getNroParcela() {
		return nroParcela;
	}

	public void setNroParcela(Short nroParcela) {
		this.nroParcela = nroParcela;
	}

	public Date getDtEmissao() {
		return dtEmissao;
	}

	public void setDtEmissao(Date dtEmissao) {
		this.dtEmissao = dtEmissao;
	}

	public Date getDtVencimento() {
		return dtVencimento;
	}

	public void setDtVencimento(Date dtVencimento) {
		this.dtVencimento = dtVencimento;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getNumeroNF() {
		return numeroNF;
	}

	public void setNumeroNF(Integer numeroNF) {
		this.numeroNF = numeroNF;
	}

	public String getIndDocumentacao() {
		return indDocumentacao;
	}

	public void setIndDocumentacao(String indDocumentacao) {
		this.indDocumentacao = indDocumentacao;
	}

	public DominioSituacaoTitulo getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoTitulo indSituacao) {
		setSituacao(indSituacao);
		this.indSituacao = indSituacao;
	}

	public String getCgcCpfFornecedor() {
		return cgcCpfFornecedor;
	}

	public void setCgcCpfFornecedor(Long cgcCpfFornecedor) {
		if(cgcCpfFornecedor != null && cgcCpfFornecedor > 0) {
			this.cgcCpfFornecedor = String.valueOf(cgcCpfFornecedor);
		}
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoTitulo situacao) {		
		this.situacao = situacao.name();
	}
}
