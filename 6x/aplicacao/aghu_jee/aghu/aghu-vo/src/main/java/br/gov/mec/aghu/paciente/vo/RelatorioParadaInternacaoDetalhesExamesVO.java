package br.gov.mec.aghu.paciente.vo;

import java.util.Date;

public class RelatorioParadaInternacaoDetalhesExamesVO {
	private Date dataEvento; //DTHR_EVENTO_LIB de Q_SEM
	private String manDescricao; //man_descricao de Q_SEM 
	private String exaDescricao; //exa_descricao de Q_SEM
	private Integer ordemRelatorio; //ordem_relatorio de Q_SEM
	private String descricaoMascLinha; // DESCRICAO1 de Q_MASC_LINHA
	private String descricaolongMasc;// Retorno de CF_LONG_MASCFormula.sql
	
	//parametros para relatorio
	private Integer atdSeq;
	private Date dthrCriacao;
	
	public Date getDataEvento() {
		return dataEvento;
	}
	public void setDataEvento(Date dataEvento) {
		this.dataEvento = dataEvento;
	}
	public String getManDescricao() {
		return manDescricao;
	}
	public void setManDescricao(String manDescricao) {
		this.manDescricao = manDescricao;
	}
	public String getExaDescricao() {
		return exaDescricao;
	}
	public void setExaDescricao(String exaDescricao) {
		this.exaDescricao = exaDescricao;
	}
	public Integer getOrdemRelatorio() {
		return ordemRelatorio;
	}
	public void setOrdemRelatorio(Integer ordemRelatorio) {
		this.ordemRelatorio = ordemRelatorio;
	}
	
	public String getDescricaoMascLinha() {
		return descricaoMascLinha;
	}
	public void setDescricaoMascLinha(String descricaoMascLinha) {
		this.descricaoMascLinha = descricaoMascLinha;
	}
	public String getDescricaolongMasc() {
		return descricaolongMasc;
	}
	public void setDescricaolongMasc(String descricaolongMasc) {
		this.descricaolongMasc = descricaolongMasc;
	}
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public Date getDthrCriacao() {
		return dthrCriacao;
	}
	public void setDthrCriacao(Date dthrCriacao) {
		this.dthrCriacao = dthrCriacao;
	}

}
