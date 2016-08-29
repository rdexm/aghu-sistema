package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import java.util.Date;

public class RelatorioAnamneseNotasAdicionaisPacienteVO implements Serializable {

	/**

     * 

     */

	private static final long serialVersionUID = 1303815442655324578L;

	private String atendimento;

	private String nomeResponsavel;

	private Date dataConfirmacao;

	private String descricao;

	private String notasAdicionais;

	private Date dataCriacao;

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

	public Date getDataConfirmacao() {

		return dataConfirmacao;

	}

	public void setDataConfirmacao(Date dataConfirmacao) {

		this.dataConfirmacao = dataConfirmacao;

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

	public Date getDataCriacao() {

		return dataCriacao;

	}

	public void setDataCriacao(Date dataCriacao) {

		this.dataCriacao = dataCriacao;

	}

}
