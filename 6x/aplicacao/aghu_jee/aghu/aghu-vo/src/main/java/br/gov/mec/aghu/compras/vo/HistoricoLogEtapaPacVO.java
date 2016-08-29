package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;

public class HistoricoLogEtapaPacVO {

	private String descricaoLocProcesso;
	private String descricaoEtapa;
	private DominioSituacaoEtapaPac situacaoLog;
	private Short tempoPrevisto;
	private String apontamentoUsuario;
	private String nomePessoaFisica;
	private Date dataApontamento;
	private Date dataAlteracao;

	public String getDescricaoLocProcesso() {
		return descricaoLocProcesso;
	}

	public void setDescricaoLocProcesso(String descricaoLocProcesso) {
		this.descricaoLocProcesso = descricaoLocProcesso;
	}

	public String getDescricaoEtapa() {
		return descricaoEtapa;
	}

	public void setDescricaoEtapa(String descricaoEtapa) {
		this.descricaoEtapa = descricaoEtapa;
	}

	public DominioSituacaoEtapaPac getSituacaoLog() {
		return situacaoLog;
	}

	public void setSituacaoLog(DominioSituacaoEtapaPac situacaoLog) {
		this.situacaoLog = situacaoLog;
	}

	public Short getTempoPrevisto() {
		return tempoPrevisto;
	}

	public void setTempoPrevisto(Short tempoPrevisto) {
		this.tempoPrevisto = tempoPrevisto;
	}

	public String getApontamentoUsuario() {
		return apontamentoUsuario;
	}

	public void setApontamentoUsuario(String apontamentoUsuario) {
		this.apontamentoUsuario = apontamentoUsuario;
	}

	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}

	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}

	public Date getDataApontamento() {
		return dataApontamento;
	}

	public void setDataApontamento(Date dataApontamento) {
		this.dataApontamento = dataApontamento;
	}

	public Date getDataAlteracao() {
		return dataAlteracao;
	}

	public void setDataAlteracao(Date dataAlteracao) {
		this.dataAlteracao = dataAlteracao;
	}

}
