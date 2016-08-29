package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

// ORADB: MPMR_IMP_SLTC_HEMO

public class RelSolHemoterapicaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6772179435228233126L;
	private Integer seq;
	private String tipo; // "SOLICITAÇÃO" ou "REIMPRESSÃO" - F_REIMPRESSAO
	private String solicitante; // F_NOME1
	private String conselho; // F_NRO_REG_CONSELHO
	private Date dataHora; // F_CRIADO_EM
	private Integer prescricao; // F_SEQ3
	private Integer pacienteProntuario; // F_SEQ5
	private String pacienteNome; // F_SEQ5
	private String pacienteSexo; // F_SEQ6
	private Date dataNascimento; // F_SEQ7
	private String labelLocalizacao; // F_SEQ8
	private String localizacao; // F_SEQ8
	private String convenio; //F_SEQ9
	private String siglaConselho; // F_NRO_REG_CONSELHO
	private String numeroRegistroConselho; // F_NRO_REG_CONSELHO
	
	private List<RelItemComponenteSangSolHemoterapicaVO> listaComponentesSanguineos; //Lista dos componentes sanguíneos
	private List<RelItemProcedimentoHemoSolHemoterapicaVO> listaProcedimentosHemoterapicos; //Lista dos procedimentos
	
	private Boolean transfusoesUltimos3Dias; 
	private Boolean pacienteTransplantado;
	private String situacaoAmostra;
	private String observacao;
	private String urgente;
	private String diagnostico;
	
	private List<RelAtendimentoCidSolHemoterapicaVO> listaCidsAtendimento;

	
	public Integer getSeq() {
		return seq;
	}
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getSolicitante() {
		return solicitante;
	}
	public void setSolicitante(String solicitante) {
		this.solicitante = solicitante;
	}
	public String getConselho() {
		return conselho;
	}
	public void setConselho(String conselho) {
		this.conselho = conselho;
	}
	public Date getDataHora() {
		return dataHora;
	}
	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}
	public Integer getPrescricao() {
		return prescricao;
	}
	public void setPrescricao(Integer prescricao) {
		this.prescricao = prescricao;
	}
	public Integer getPacienteProntuario() {
		return pacienteProntuario;
	}
	public void setPacienteProntuario(Integer pacienteProntuario) {
		this.pacienteProntuario = pacienteProntuario;
	}
	public String getPacienteNome() {
		return pacienteNome;
	}
	public void setPacienteNome(String pacienteNome) {
		this.pacienteNome = pacienteNome;
	}
	public String getPacienteSexo() {
		return pacienteSexo;
	}
	public void setPacienteSexo(String pacienteSexo) {
		this.pacienteSexo = pacienteSexo;
	}
	public Date getDataNascimento() {
		return dataNascimento;
	}
	public void setDataNascimento(Date dataNascimento) {
		this.dataNascimento = dataNascimento;
	}
	
	public String getLabelLocalizacao() {
		return labelLocalizacao;
	}
	public void setLabelLocalizacao(String labelLocalizacao) {
		this.labelLocalizacao = labelLocalizacao;
	}
	public String getLocalizacao() {
		return localizacao;
	}
	public void setLocalizacao(String localizacao) {
		this.localizacao = localizacao;
	}
	public String getConvenio() {
		return convenio;
	}
	public void setConvenio(String convenio) {
		this.convenio = convenio;
	}
	public List<RelItemComponenteSangSolHemoterapicaVO> getListaComponentesSanguineos() {
		return listaComponentesSanguineos;
	}
	public void setListaComponentesSanguineos(
			List<RelItemComponenteSangSolHemoterapicaVO> listaComponentesSanguineos) {
		this.listaComponentesSanguineos = listaComponentesSanguineos;
	}
	public List<RelItemProcedimentoHemoSolHemoterapicaVO> getListaProcedimentosHemoterapicos() {
		return listaProcedimentosHemoterapicos;
	}
	public void setListaProcedimentosHemoterapicos(
			List<RelItemProcedimentoHemoSolHemoterapicaVO> listaProcedimentosHemoterapicos) {
		this.listaProcedimentosHemoterapicos = listaProcedimentosHemoterapicos;
	}
	
	public Boolean getTransfusoesUltimos3Dias() {
		return transfusoesUltimos3Dias;
	}
	public void setTransfusoesUltimos3Dias(Boolean transfusoesUltimos3Dias) {
		this.transfusoesUltimos3Dias = transfusoesUltimos3Dias;
	}
	public Boolean getPacienteTransplantado() {
		return pacienteTransplantado;
	}
	public void setPacienteTransplantado(Boolean pacienteTransplantado) {
		this.pacienteTransplantado = pacienteTransplantado;
	}
	public String getSituacaoAmostra() {
		return situacaoAmostra;
	}
	public void setSituacaoAmostra(String situacaoAmostra) {
		this.situacaoAmostra = situacaoAmostra;
	}
	public String getObservacao() {
		return observacao;
	}
	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}
	public String getDiagnostico() {
		return diagnostico;
	}
	public void setDiagnostico(String diagnostico) {
		this.diagnostico = diagnostico;
	}
	public String getUrgente() {
		return urgente;
	}
	public void setUrgente(String urgente) {
		this.urgente = urgente;
	}
	
	public List<RelAtendimentoCidSolHemoterapicaVO> getListaCidsAtendimento() {
		return listaCidsAtendimento;
	}
	public void setListaCidsAtendimento(
			List<RelAtendimentoCidSolHemoterapicaVO> listaCidsAtendimento) {
		this.listaCidsAtendimento = listaCidsAtendimento;
	}
	public String getSiglaConselho() {
		return siglaConselho;
	}
	public void setSiglaConselho(String siglaConselho) {
		this.siglaConselho = siglaConselho;
	}
	public String getNumeroRegistroConselho() {
		return numeroRegistroConselho;
	}
	public void setNumeroRegistroConselho(String numeroRegistroConselho) {
		this.numeroRegistroConselho = numeroRegistroConselho;
	}
	
	
	
}
