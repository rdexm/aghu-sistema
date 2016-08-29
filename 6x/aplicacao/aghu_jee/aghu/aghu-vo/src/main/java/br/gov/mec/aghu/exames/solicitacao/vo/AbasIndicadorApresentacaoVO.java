package br.gov.mec.aghu.exames.solicitacao.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.model.AelQuestionarios;
import br.gov.mec.aghu.model.AelRecomendacaoExame;
import br.gov.mec.aghu.model.AghCid;


public class AbasIndicadorApresentacaoVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7706845294261905009L;
	//Flags para aparecerem ou não as abas internas dependendo do exame selecionado
	private Boolean mostrarAbaTipoTransporte = Boolean.FALSE;
	private Boolean mostrarAbaIntervColeta = Boolean.FALSE;
	private Boolean mostrarAbaNoAmostras = Boolean.FALSE;
	private Boolean mostrarAbaConcentO2 = Boolean.FALSE;
	private Boolean mostrarAbaRegMatAnalise = Boolean.FALSE;
	private Boolean mostrarAbaRecomendacoes = Boolean.FALSE;
	private Boolean mostrarAbaQuestionario = Boolean.FALSE;
	private Boolean mostrarAbaQuestionarioSismama = Boolean.FALSE;
	private Boolean mostrarAbaQuestionarioSismamaBiopsia = Boolean.FALSE;
	private Boolean mostrarAbaSituacao = Boolean.TRUE;
	
	private List<AelRecomendacaoExame> recomendacaoExameList;
	private List<AelQuestionarios> questionarios;
	private List<AghCid> aghCids;
	
	public Boolean getMostrarAbaTipoTransporte() {
		return mostrarAbaTipoTransporte;
	}
	public void setMostrarAbaTipoTransporte(Boolean mostrarAbaTipoTransporte) {
		this.mostrarAbaTipoTransporte = mostrarAbaTipoTransporte;
	}
	public Boolean getMostrarAbaIntervColeta() {
		return mostrarAbaIntervColeta;
	}
	public void setMostrarAbaIntervColeta(Boolean mostrarAbaIntervColeta) {
		this.mostrarAbaIntervColeta = mostrarAbaIntervColeta;
	}
	public Boolean getMostrarAbaNoAmostras() {
		return mostrarAbaNoAmostras;
	}
	public void setMostrarAbaNoAmostras(Boolean mostrarAbaNoAmostras) {
		this.mostrarAbaNoAmostras = mostrarAbaNoAmostras;
	}
	public Boolean getMostrarAbaConcentO2() {
		return mostrarAbaConcentO2;
	}
	public void setMostrarAbaConcentO2(Boolean mostrarAbaConcentO2) {
		this.mostrarAbaConcentO2 = mostrarAbaConcentO2;
	}
	public Boolean getMostrarAbaRegMatAnalise() {
		return mostrarAbaRegMatAnalise;
	}
	public void setMostrarAbaRegMatAnalise(Boolean mostrarAbaRegMatAnalise) {
		this.mostrarAbaRegMatAnalise = mostrarAbaRegMatAnalise;
	}
	public Boolean getMostrarAbaRecomendacoes() {
		return mostrarAbaRecomendacoes;
	}
	public void setMostrarAbaRecomendacoes(Boolean mostrarAbaRecomendacoes) {
		this.mostrarAbaRecomendacoes = mostrarAbaRecomendacoes;
	}
	public List<AelRecomendacaoExame> getRecomendacaoExameList() {
		return recomendacaoExameList;
	}
	public void setRecomendacaoExameList(
			List<AelRecomendacaoExame> recomendacaoExameList) {
		this.recomendacaoExameList = recomendacaoExameList;
	}
	public void setMostrarAbaQuestionario(Boolean mostrarAbaQuestionario) {
		this.mostrarAbaQuestionario = mostrarAbaQuestionario;
	}
	public Boolean getMostrarAbaQuestionario() {
		return mostrarAbaQuestionario;
	}
	public Boolean getMostrarAbaQuestionarioSismama() {
		return mostrarAbaQuestionarioSismama;
	}
	public void setMostrarAbaQuestionarioSismama(
			Boolean mostrarAbaQuestionarioSismama) {
		this.mostrarAbaQuestionarioSismama = mostrarAbaQuestionarioSismama;
	}
	public void setQuestionarios(List<AelQuestionarios> questionarios) {
		this.questionarios = questionarios;
	}
	public List<AelQuestionarios> getQuestionarios() {
		return questionarios;
	}
	public void setAghCids(List<AghCid> aghCids) {
		this.aghCids = aghCids;
	}
	public List<AghCid> getAghCids() {
		return aghCids;
	}
	public Boolean getMostrarAbaQuestionarioSismamaBiopsia() {
		return mostrarAbaQuestionarioSismamaBiopsia;
	}
	public void setMostrarAbaQuestionarioSismamaBiopsia(
			Boolean mostrarAbaQuestionarioSismamaBiopsia) {
		this.mostrarAbaQuestionarioSismamaBiopsia = mostrarAbaQuestionarioSismamaBiopsia;
	}
	public Boolean getMostrarAbaSituacao() {
		return mostrarAbaSituacao;
	}
	public void setMostrarAbaSituacao(Boolean mostrarAbaSituacao) {
		this.mostrarAbaSituacao = mostrarAbaSituacao;
	}
	
	

}
