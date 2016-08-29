package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.core.commons.BaseBean;

public class ScoProgrCodAcessoFornVO implements BaseBean {
	private static final long serialVersionUID = 1154589656761678695L;
	private Integer seq;
	private ScoFornecedor scoFornecedor;
	private Date dtGeracao;
	private Date dtEnvioSenha;
	private Date dtEnvioContato;
	private String pendencia;
	private String color;
	private String codAcesso;
	private String colorEnvioContatos;
	
	public ScoProgrCodAcessoFornVO() {}
	
	public ScoProgrCodAcessoFornVO(Integer seq, ScoFornecedor fornecedor,
			Date dtGeracao, Date dtEnvioSenha, Date dtEnvioContato,
			String pendencia, String color) {
		super();
		this.seq = seq;
		this.scoFornecedor = fornecedor;
		this.dtGeracao = dtGeracao;
		this.dtEnvioSenha = dtEnvioSenha;
		this.dtEnvioContato = dtEnvioContato;
		this.pendencia = pendencia;
		this.color = color;
	}

	public Integer getSeq() {
		return seq;
	}

	public ScoFornecedor getScoFornecedor() {
		return scoFornecedor;
	}

	public Date getDtGeracao() {
		return dtGeracao;
	}

	public Date getDtEnvioSenha() {
		return dtEnvioSenha;
	}

	public Date getDtEnvioContato() {
		return dtEnvioContato;
	}

	public String getPendencia() {
		return pendencia;
	}

	public String getColor() {
		return color;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public void setScoFornecedor(ScoFornecedor fornecedor) {
		this.scoFornecedor = fornecedor;
	}

	public void setDtGeracao(Date dtGeracao) {
		this.dtGeracao = dtGeracao;
	}

	public void setDtEnvioSenha(Date dtEnvioSenha) {
		this.dtEnvioSenha = dtEnvioSenha;
	}

	public void setDtEnvioContato(Date dtEnvioContato) {
		this.dtEnvioContato = dtEnvioContato;
	}

	public void setPendencia(String pendencia) {
		this.pendencia = pendencia;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getCodAcesso() {
		return codAcesso;
	}

	public void setCodAcesso(String codAcesso) {
		this.codAcesso = codAcesso;
	}

	public String getColorEnvioContatos() {
		return colorEnvioContatos;
	}

	public void setColorEnvioContatos(String colorEnvioContatos) {
		this.colorEnvioContatos = colorEnvioContatos;
	}
}
