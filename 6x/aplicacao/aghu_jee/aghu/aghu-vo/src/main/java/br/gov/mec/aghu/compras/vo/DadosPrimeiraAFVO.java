package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;

public class DadosPrimeiraAFVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;
	
	private Integer af;
	private Short cp;
	private Date vencimentoContrato;
	private DominioSituacaoAutorizacaoFornecimento situacao;
	private Date dataGeracao;
	
	public Integer getAf() {
		return af;
	}
	public void setAf(Integer af) {
		this.af = af;
	}
	public Short getCp() {
		return cp;
	}
	public void setCp(Short cp) {
		this.cp = cp;
	}
	public Date getVencimentoContrato() {
		return vencimentoContrato;
	}
	public void setVencimentoContrato(Date vencimentoContrato) {
		this.vencimentoContrato = vencimentoContrato;
	}
	public DominioSituacaoAutorizacaoFornecimento getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacaoAutorizacaoFornecimento situacao) {
		this.situacao = situacao;
	}
	public Date getDataGeracao() {
		return dataGeracao;
	}
	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}
	
}
