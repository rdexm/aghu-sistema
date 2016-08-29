package br.gov.mec.aghu.controlepaciente.vo;

import java.io.Serializable;
import java.util.Date;

public class RegistroControlePacienteServiceVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6992116186379434471L;
	
	private Long horarioSeq;
	private Date dataHoraMedicao;
	private String anotacoesHorario;
	private Integer atdSeq;
	private Long trgSeq;
	private Integer pacCodigo;
	private Short unfSeq;
	private String localAtendimento;	
	private String[] valor;
	private Boolean[] limite;
	private boolean renderizarAnotacoes;
	private String anotacoes;
	
	public RegistroControlePacienteServiceVO() {

	}

	public RegistroControlePacienteServiceVO(Long horarioSeq, Date dataHoraMedicao,
			String anotacoesHorario, Integer atdSeq, Long trgSeq, String[] valor,
			Boolean[] limite, String localAtendimento, Integer pacCodigo, Short unfSeq, boolean renderizarAnotacoes) {
		super();
		this.horarioSeq = horarioSeq;
		this.dataHoraMedicao = dataHoraMedicao;
		this.anotacoesHorario = anotacoesHorario;
		this.valor = valor;
		this.limite = limite;
		this.atdSeq = atdSeq;
		this.trgSeq = trgSeq;
		this.localAtendimento = localAtendimento;
		this.pacCodigo = pacCodigo;
		this.unfSeq = unfSeq;
		this.setRenderizarAnotacoes(renderizarAnotacoes);
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public RegistroControlePacienteServiceVO(Long horarioSeq, Date dataHoraMedicao,
			String anotacoesHorario, Integer atdSeq, Long trgSeq, String[] valor,
			Boolean[] limite, String localAtendimento, Integer pacCodigo, Short unfSeq,
			boolean renderizarAnotacoes, String anotacoes) {
		super();
		this.horarioSeq = horarioSeq;
		this.dataHoraMedicao = dataHoraMedicao;
		this.anotacoesHorario = anotacoesHorario;
		this.valor = valor;
		this.limite = limite;
		this.atdSeq = atdSeq;
		this.trgSeq = trgSeq;
		this.localAtendimento = localAtendimento;
		this.pacCodigo = pacCodigo;
		this.unfSeq = unfSeq;
		this.setRenderizarAnotacoes(renderizarAnotacoes);
		this.anotacoes = anotacoes;
	}

	public Long getHorarioSeq() {
		return horarioSeq;
	}

	public void setHorarioSeq(Long horarioSeq) {
		this.horarioSeq = horarioSeq;
	}

	public Date getDataHoraMedicao() {
		return dataHoraMedicao;
	}

	public void setDataHoraMedicao(Date dataHoraMedicao) {
		this.dataHoraMedicao = dataHoraMedicao;
	}

	public String getAnotacoesHorario() {
		return anotacoesHorario;
	}

	public void setAnotacoesHorario(String anotacoesHorario) {
		this.anotacoesHorario = anotacoesHorario;
	}

	public String[] getValor() {
		return valor;
	}

	public void setValor(String[] valor) {
		this.valor = valor;
	}

	public Boolean[] getLimite() {
		return limite;
	}

	public void setLimite(Boolean[] limite) {
		this.limite = limite;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public String getLocalAtendimento() {
		return localAtendimento;
	}

	public void setLocalAtendimento(String localAtendimento) {
		this.localAtendimento = localAtendimento;
	}

	public boolean isRenderizarAnotacoes() {
		return renderizarAnotacoes;
	}

	public void setRenderizarAnotacoes(boolean renderizarAnotacoes) {
		this.renderizarAnotacoes = renderizarAnotacoes;
	}

	public String getAnotacoes() {
		return anotacoes;
	}

	public void setAnotacoes(String anotacoes) {
		this.anotacoes = anotacoes;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

}
