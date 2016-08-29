package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class RelatorioAtestadoVO implements Serializable {

	private static final long serialVersionUID = 5752528943119568926L;

	private String tipoAtestado;
	private String conselhoRegional;
	private String siglaConselhoRegional;
	private String nomePaciente;
	private String nomeAcompanhante;
	private String dataCriacao;
	private String dataInicial;
	private String dataFinal;
	private String horarioInicial;
	private String horarioFinal;
	private String turno;
	private String numeroDiasAtestado;
	private String nomeMedico;
	private String observacao;
	private boolean imprimiu;
	private String cid;

	public String getTipoAtestado() {
		return tipoAtestado;
	}

	public void setTipoAtestado(String tipoAtestado) {
		this.tipoAtestado = tipoAtestado;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getNomeAcompanhante() {
		return nomeAcompanhante;
	}

	public void setNomeAcompanhante(String nomeAcompanhante) {
		this.nomeAcompanhante = nomeAcompanhante;
	}

	public String getHorarioInicial() {
		return horarioInicial;
	}

	public void setHorarioInicial(String horarioInicial) {
		this.horarioInicial = horarioInicial;
	}

	public String getHorarioFinal() {
		return horarioFinal;
	}

	public void setHorarioFinal(String horarioFinal) {
		this.horarioFinal = horarioFinal;
	}

	public String getTurno() {
		return turno;
	}

	public void setTurno(String turno) {
		this.turno = turno;
	}

	public String getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(String dataInicial) {
		this.dataInicial = dataInicial;
	}

	public String getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(String dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String getNumeroDiasAtestado() {
		return numeroDiasAtestado;
	}

	public void setNumeroDiasAtestado(String numeroDiasAtestado) {
		this.numeroDiasAtestado = numeroDiasAtestado;
	}

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public String getCid() {
		return cid;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public boolean isImprimiu() {
		return imprimiu;
	}

	public void setImprimiu(boolean imprimiu) {
		this.imprimiu = imprimiu;
	}

	public String getConselhoRegional() {
		return conselhoRegional;
	}

	public void setConselhoRegional(String conselhoRegional) {
		this.conselhoRegional = conselhoRegional;
	}

	public String getDataCriacao() {
		return dataCriacao;
	}

	public void setDataCriacao(String dataCriacao) {
		this.dataCriacao = dataCriacao;
	}

	public String getSiglaConselhoRegional() {
		return siglaConselhoRegional;
	}

	public void setSiglaConselhoRegional(String siglaConselhoRegional) {
		this.siglaConselhoRegional = siglaConselhoRegional;
	}
}