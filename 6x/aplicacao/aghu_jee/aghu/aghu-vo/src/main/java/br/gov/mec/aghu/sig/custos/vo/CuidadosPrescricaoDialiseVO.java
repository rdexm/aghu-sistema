package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.util.Date;

public class CuidadosPrescricaoDialiseVO implements Serializable {

	private static final long serialVersionUID = 1094758748075863219L;

	private Integer atdPaciente;
	private Integer codPaciente;
	private Integer tratamentosTerapeuticosSeq;
	private Integer unidadeAtendimentoSeq;
	private Integer unidadeAgendaSeq;
	private Integer codCentroCusto;
	private Integer phiSeq;
	private Integer ocvSeq;
	private Date dataPrevisaoExecucao;
	private Integer cuidadoSeq;
	private Short frequenciaAprazamentoSeq;
	private Short frequencia;
	private Integer quantidadeUnidade;
	private Integer unidadeMedidaSeq;
	private Integer quantidadeCuidados;

	public Integer getAtdPaciente() {
		return atdPaciente;
	}
	public void setAtdPaciente(Integer atdPaciente) {
		this.atdPaciente = atdPaciente;
	}
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public Integer getTratamentosTerapeuticosSeq() {
		return tratamentosTerapeuticosSeq;
	}
	public void setTratamentosTerapeuticosSeq(Integer tratamentosTerapeuticosSeq) {
		this.tratamentosTerapeuticosSeq = tratamentosTerapeuticosSeq;
	}
	public Integer getUnidadeAtendimentoSeq() {
		return unidadeAtendimentoSeq;
	}
	public void setUnidadeAtendimentoSeq(Integer unidadeAtendimentoSeq) {
		this.unidadeAtendimentoSeq = unidadeAtendimentoSeq;
	}
	public Integer getUnidadeAgendaSeq() {
		return unidadeAgendaSeq;
	}
	public void setUnidadeAgendaSeq(Integer unidadeAgendaSeq) {
		this.unidadeAgendaSeq = unidadeAgendaSeq;
	}
	public Integer getCodCentroCusto() {
		return codCentroCusto;
	}
	public void setCodCentroCusto(Integer codCentroCusto) {
		this.codCentroCusto = codCentroCusto;
	}
	public Integer getPhiSeq() {
		return phiSeq;
	}
	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}
	public Integer getOcvSeq() {
		return ocvSeq;
	}
	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}
	public Date getDataPrevisaoExecucao() {
		return dataPrevisaoExecucao;
	}
	public void setDataPrevisaoExecucao(Date dataPrevisaoExecucao) {
		this.dataPrevisaoExecucao = dataPrevisaoExecucao;
	}
	public Integer getCuidadoSeq() {
		return cuidadoSeq;
	}
	public void setCuidadoSeq(Integer cuidadoSeq) {
		this.cuidadoSeq = cuidadoSeq;
	}
	public Short getFrequenciaAprazamentoSeq() {
		return frequenciaAprazamentoSeq;
	}
	public void setFrequenciaAprazamentoSeq(Short frequenciaAprazamentoSeq) {
		this.frequenciaAprazamentoSeq = frequenciaAprazamentoSeq;
	}
	public Short getFrequencia() {
		return frequencia;
	}
	public void setFrequencia(Short frequencia) {
		this.frequencia = frequencia;
	}
	public Integer getQuantidadeUnidade() {
		return quantidadeUnidade;
	}
	public void setQuantidadeUnidade(Integer quantidadeUnidade) {
		this.quantidadeUnidade = quantidadeUnidade;
	}
	public Integer getUnidadeMedidaSeq() {
		return unidadeMedidaSeq;
	}
	public void setUnidadeMedidaSeq(Integer unidadeMedidaSeq) {
		this.unidadeMedidaSeq = unidadeMedidaSeq;
	}
	public Integer getQuantidadeCuidados() {
		return quantidadeCuidados;
	}
	public void setQuantidadeCuidados(Integer quantidadeCuidados) {
		this.quantidadeCuidados = quantidadeCuidados;
	}
}
