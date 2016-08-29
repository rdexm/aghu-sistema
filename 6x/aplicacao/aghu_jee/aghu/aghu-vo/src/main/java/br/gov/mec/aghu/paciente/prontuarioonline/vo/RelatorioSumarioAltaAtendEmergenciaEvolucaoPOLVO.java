package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.prescricaomedica.vo.LinhaReportVO;


public class RelatorioSumarioAltaAtendEmergenciaEvolucaoPOLVO implements Serializable  {

	private static final long serialVersionUID = -3915623418600575969L;
	
	private String assinatura;
	private Boolean indNoConsultorio;
	
//	Medicamentos
	private List<LinhaReportVO> descMedicamento;
//	Consultorias
	private List<LinhaReportVO> consultorias;
//	Exames Realizados
	private List<LinhaReportVO> descExame;
//	Evolução
	private List<LinhaReportVO> descEvolucao;
//	Impressão Diagnóstica na Alta
	private List<LinhaReportVO> descDiagnostica;
//	Situação de Saída do Paciente na Alta
	private List<LinhaReportVO> estado;
//	Encaminhamento
	private List<LinhaReportVO> plano;
//	Recomendações na Alta
	private List<LinhaReportVO> recomendacao;
//	Medicamentos prescritos na alta
	private List<LinhaReportVO> descricaoQuantidade;	
	//	Getters and Setters
	public List<LinhaReportVO> getConsultorias() {
		return consultorias;
	}
	public void setConsultorias(List<LinhaReportVO> consultorias) {
		this.consultorias = consultorias;
	}
	public List<LinhaReportVO> getDescExame() {
		return descExame;
	}
	public void setDescExame(List<LinhaReportVO> descExame) {
		this.descExame = descExame;
	}
	public List<LinhaReportVO> getDescEvolucao() {
		return descEvolucao;
	}
	public void setDescEvolucao(List<LinhaReportVO> descEvolucao) {
		this.descEvolucao = descEvolucao;
	}
	public List<LinhaReportVO> getDescDiagnostica() {
		return descDiagnostica;
	}
	public void setDescDiagnostica(List<LinhaReportVO> descDiagnostica) {
		this.descDiagnostica = descDiagnostica;
	}
	public List<LinhaReportVO> getEstado() {
		return estado;
	}
	public void setEstado(List<LinhaReportVO> estado) {
		this.estado = estado;
	}
	public List<LinhaReportVO> getPlano() {
		return plano;
	}
	public void setPlano(List<LinhaReportVO> plano) {
		this.plano = plano;
	}
	public List<LinhaReportVO> getRecomendacao() {
		return recomendacao;
	}
	public void setRecomendacao(List<LinhaReportVO> recomendacao) {
		this.recomendacao = recomendacao;
	}
	public List<LinhaReportVO> getDescricaoQuantidade() {
		return descricaoQuantidade;
	}
	public void setDescricaoQuantidade(List<LinhaReportVO> descricaoQuantidade) {
		this.descricaoQuantidade = descricaoQuantidade;
	}
	public void setIndNoConsultorio(Boolean indNoConsultorio) {
		this.indNoConsultorio = indNoConsultorio;
	}
	public Boolean getIndNoConsultorio() {
		return indNoConsultorio;
	}
	public void setAssinatura(String assinatura) {
		this.assinatura = assinatura;
	}
	public String getAssinatura() {
		return assinatura;
	}
	public void setDescMedicamento(List<LinhaReportVO> descMedicamento) {
		this.descMedicamento = descMedicamento;
	}
	public List<LinhaReportVO> getDescMedicamento() {
		return descMedicamento;
	}

}