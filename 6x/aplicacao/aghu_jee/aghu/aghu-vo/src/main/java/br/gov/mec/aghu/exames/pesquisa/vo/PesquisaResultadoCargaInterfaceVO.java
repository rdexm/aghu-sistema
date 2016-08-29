package br.gov.mec.aghu.exames.pesquisa.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioLwsOrigem;
import br.gov.mec.aghu.dominio.DominioLwsTipoComunicacao;
import br.gov.mec.aghu.dominio.DominioSimNao;


public class PesquisaResultadoCargaInterfaceVO implements Serializable {

	private static final long serialVersionUID = 1680569849545084209L;
	private Date dataHoraInicial;
	private Date dataHoraFinal;
	private DominioSimNao indMostraComErro;
	private Integer solicitacao;
	private Short amostra;
	private DominioLwsTipoComunicacao tipoComunicao;
	private DominioLwsOrigem origem;
	
	public Date getDataHoraInicial() {
		return dataHoraInicial;
	}
	public void setDataHoraInicial(Date dataHoraInicial) {
		this.dataHoraInicial = dataHoraInicial;
	}
	public Date getDataHoraFinal() {
		return dataHoraFinal;
	}
	public void setDataHoraFinal(Date dataHoraFinal) {
		this.dataHoraFinal = dataHoraFinal;
	}
	public DominioSimNao getIndMostraComErro() {
		return indMostraComErro;
	}
	public void setIndMostraComErro(DominioSimNao indMostraComErro) {
		this.indMostraComErro = indMostraComErro;
	}
	public Integer getSolicitacao() {
		return solicitacao;
	}
	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}
	public Short getAmostra() {
		return amostra;
	}
	public void setAmostra(Short amostra) {
		this.amostra = amostra;
	}
	public DominioLwsTipoComunicacao getTipoComunicao() {
		return tipoComunicao;
	}
	public void setTipoComunicao(DominioLwsTipoComunicacao tipoComunicao) {
		this.tipoComunicao = tipoComunicao;
	}
	public DominioLwsOrigem getOrigem() {
		return origem;
	}
	public void setOrigem(DominioLwsOrigem origem) {
		this.origem = origem;
	}
	
	
}