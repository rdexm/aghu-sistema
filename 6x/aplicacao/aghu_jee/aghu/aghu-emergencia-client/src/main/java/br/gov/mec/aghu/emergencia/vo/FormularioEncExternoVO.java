package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public class FormularioEncExternoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5001580464590785155L;

	private String nome;
	private Short dddTelefone;
	private Long telefone;
	private String logradouro;
	private String complLogradouro;
	private Integer nroLogradouro;
	private BigDecimal cep;
	private String bairro;
	private String cidade;
	private String unidadeFederacao;
	private String municioUnidSaudeExt;
	private String unidSaudeExt;
	private String protocolo;
	private String classificacaoGravidade;
	
	private String queixaPrinipal;
	private Date dataQueixa;
	private Date horaQueixa;
	private String fluxograma;
	private String grauGravidade;
	private String municipio;
	private String labelNome;
	private String labelEndereco;
	private String labelUnidadeReferencia;
	private String labelClassificacaoRisco;
	private String labelFluxograma;
	private String labelAtencaoPrimaria;
	private String labelPacienteSemReferencia;
	private String labelCidadeOrigem;
	private String labelCidadeTrabalha1;
	private String labelCidadeTrabalha2;
	private String labelPacientesClassificacao;
	private String labelPacientesClassificacao1;
	private String labelPacientesClassificacao2;
	
	private List<SubFormularioSinaisVitaisVO> sinaisAferidos;
	
	
	public String getLabelNome() {
		return labelNome;
	}

	public void setLabelNome(String labelNome) {
		this.labelNome = labelNome;
	}

	public String getLabelEndereco() {
		return labelEndereco;
	}

	public void setLabelEndereco(String labelEndereco) {
		this.labelEndereco = labelEndereco;
	}

	public String getLabelUnidadeReferencia() {
		return labelUnidadeReferencia;
	}

	public void setLabelUnidadeReferencia(String labelUnidadeReferencia) {
		this.labelUnidadeReferencia = labelUnidadeReferencia;
	}

	public String getLabelClassificacaoRisco() {
		return labelClassificacaoRisco;
	}

	public void setLabelClassificacaoRisco(String labelClassificacaoRisco) {
		this.labelClassificacaoRisco = labelClassificacaoRisco;
	}

	public String getLabelFluxograma() {
		return labelFluxograma;
	}

	public void setLabelFluxograma(String labelFluxograma) {
		this.labelFluxograma = labelFluxograma;
	}

	public String getLabelAtencaoPrimaria() {
		return labelAtencaoPrimaria;
	}

	public void setLabelAtencaoPrimaria(String labelAtencaoPrimaria) {
		this.labelAtencaoPrimaria = labelAtencaoPrimaria;
	}

	public String getLabelPacienteSemReferencia() {
		return labelPacienteSemReferencia;
	}

	public void setLabelPacienteSemReferencia(String labelPacienteSemReferencia) {
		this.labelPacienteSemReferencia = labelPacienteSemReferencia;
	}

	public String getLabelCidadeOrigem() {
		return labelCidadeOrigem;
	}

	public void setLabelCidadeOrigem(String labelCidadeOrigem) {
		this.labelCidadeOrigem = labelCidadeOrigem;
	}

	public String getLabelCidadeTrabalha1() {
		return labelCidadeTrabalha1;
	}

	public void setLabelCidadeTrabalha1(String labelCidadeTrabalha1) {
		this.labelCidadeTrabalha1 = labelCidadeTrabalha1;
	}

	public String getLabelCidadeTrabalha2() {
		return labelCidadeTrabalha2;
	}

	public void setLabelCidadeTrabalha2(String labelCidadeTrabalha2) {
		this.labelCidadeTrabalha2 = labelCidadeTrabalha2;
	}

	public String getLabelPacientesClassificacao() {
		return labelPacientesClassificacao;
	}

	public void setLabelPacientesClassificacao(String labelPacientesClassificacao) {
		this.labelPacientesClassificacao = labelPacientesClassificacao;
	}

	public String getLabelPacientesClassificacao1() {
		return labelPacientesClassificacao1;
	}

	public void setLabelPacientesClassificacao1(String labelPacientesClassificacao1) {
		this.labelPacientesClassificacao1 = labelPacientesClassificacao1;
	}

	public String getLabelPacientesClassificacao2() {
		return labelPacientesClassificacao2;
	}

	public void setLabelPacientesClassificacao2(String labelPacientesClassificacao2) {
		this.labelPacientesClassificacao2 = labelPacientesClassificacao2;
	}
	
	public String getBairro() {
		return bairro;
	}
	
	public void setBairro(String bairro) {
		this.bairro = bairro;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Short getDddTelefone() {
		return dddTelefone;
	}

	public void setDddTelefone(Short dddTelefone) {
		this.dddTelefone = dddTelefone;
	}

	public Long getTelefone() {
		return telefone;
	}

	public void setTelefone(Long telefone) {
		this.telefone = telefone;
	}

	public String getLogradouro() {
		return logradouro;
	}

	public void setLogradouro(String logradouro) {
		this.logradouro = logradouro;
	}

	public String getComplLogradouro() {
		return complLogradouro;
	}

	public void setComplLogradouro(String complLogradouro) {
		this.complLogradouro = complLogradouro;
	}

	public Integer getNroLogradouro() {
		return nroLogradouro;
	}

	public void setNroLogradouro(Integer nroLogradouro) {
		this.nroLogradouro = nroLogradouro;
	}

	public BigDecimal getCep() {
		return cep;
	}

	public void setCep(BigDecimal cep) {
		this.cep = cep;
	}

	public String getCidade() {
		return cidade;
	}

	public void setCidade(String cidade) {
		this.cidade = cidade;
	}

	public String getUnidadeFederacao() {
		return unidadeFederacao;
	}

	public void setUnidadeFederacao(String unidadeFederacao) {
		this.unidadeFederacao = unidadeFederacao;
	}

	public String getMunicioUnidSaudeExt() {
		return municioUnidSaudeExt;
	}

	public void setMunicioUnidSaudeExt(String municioUnidSaudeExt) {
		this.municioUnidSaudeExt = municioUnidSaudeExt;
	}

	public String getUnidSaudeExt() {
		return unidSaudeExt;
	}

	public void setUnidSaudeExt(String unidSaudeExt) {
		this.unidSaudeExt = unidSaudeExt;
	}

	public String getProtocolo() {
		return protocolo;
	}

	public void setProtocolo(String protocolo) {
		this.protocolo = protocolo;
	}

	public String getClassificacaoGravidade() {
		return classificacaoGravidade;
	}

	public void setClassificacaoGravidade(String classificacaoGravidade) {
		this.classificacaoGravidade = classificacaoGravidade;
	}

	public String getQueixaPrinipal() {
		return queixaPrinipal;
	}

	public void setQueixaPrinipal(String queixaPrinipal) {
		this.queixaPrinipal = queixaPrinipal;
	}

	public Date getDataQueixa() {
		return dataQueixa;
	}

	public void setDataQueixa(Date dataQueixa) {
		this.dataQueixa = dataQueixa;
	}

	public Date getHoraQueixa() {
		return horaQueixa;
	}

	public void setHoraQueixa(Date horaQueixa) {
		this.horaQueixa = horaQueixa;
	}

	public String getFluxograma() {
		return fluxograma;
	}

	public void setFluxograma(String fluxograma) {
		this.fluxograma = fluxograma;
	}

	public String getGrauGravidade() {
		return grauGravidade;
	}

	public void setGrauGravidade(String grauGravidade) {
		this.grauGravidade = grauGravidade;
	}

	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}
	public List<SubFormularioSinaisVitaisVO> getSinaisAferidos() {
		return sinaisAferidos;
	}

	public void setSinaisAferidos(List<SubFormularioSinaisVitaisVO> sinaisAferidos) {
		this.sinaisAferidos = sinaisAferidos;
	}
}
