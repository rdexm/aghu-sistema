package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.Date;

public class RelatorioEvolucoesNotasAdicionaisPacienteVO implements
		Serializable {

	/**
         * 
         */
	private static final long serialVersionUID = -5199258298038861275L;

	private Date dataCriacao;
	private String atendimento;
	private String nomeResponsavel;
	private Date dataReferencia;
	private Date dataFim;
	private String descricao;
	private String notasAdicionais;

	public Date getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(Date dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(String atendimento) {
		this.atendimento = atendimento;
	}

	public String getNomeResponsavel() {
		return nomeResponsavel;
	}

	public void setNomeResponsavel(String nomeResponsavel) {
		this.nomeResponsavel = nomeResponsavel;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getNotasAdicionais() {
		return notasAdicionais;
	}

	public void setNotasAdicionais(String notasAdicionais) {
		this.notasAdicionais = notasAdicionais;
	}

}
