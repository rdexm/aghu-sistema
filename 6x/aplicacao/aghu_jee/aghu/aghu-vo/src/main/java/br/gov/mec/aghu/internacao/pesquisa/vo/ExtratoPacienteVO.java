package br.gov.mec.aghu.internacao.pesquisa.vo;

import br.gov.mec.aghu.core.commons.BaseBean;



public class ExtratoPacienteVO implements BaseBean {
	private String dataLancamento;
	private String tipoMovimento;
	private String especialidade;
	private String descEspecialidade;
	private String leito;
	private String quarto;
	private String unidade;
	private String nomeMedico;
	private String crmMedico;
	private String nomeInformante;
	private String criadoEm;
	
	
	
	public String getDataLancamento() {
		return dataLancamento;
	}
	public void setDataLancamento(String dataLancamento) {
		this.dataLancamento = dataLancamento;
	}
	public String getTipoMovimento() {
		return tipoMovimento;
	}
	public void setTipoMovimento(String tipoMovimento) {
		this.tipoMovimento = tipoMovimento;
	}
	public String getEspecialidade() {
		return especialidade;
	}
	public void setEspecialidade(String especialidade) {
		this.especialidade = especialidade;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getQuarto() {
		return quarto;
	}
	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}
	public String getUnidade() {
		return unidade;
	}
	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}
	public String getNomeMedico() {
		return nomeMedico;
	}
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
	public String getCrmMedico() {
		return crmMedico;
	}
	public void setCrmMedico(String crmMedico) {
		this.crmMedico = crmMedico;
	}
	public String getNomeInformante() {
		return nomeInformante;
	}
	public void setNomeInformante(String nomeInformante) {
		this.nomeInformante = nomeInformante;
	}
	public void setCriadoEm(String criadoEm) {
		this.criadoEm = criadoEm;
	}
	public String getCriadoEm() {
		return criadoEm;
	}
	public String getDescEspecialidade() {
		return descEspecialidade;
	}
	public void setDescEspecialidade(String descEspecialidade) {
		this.descEspecialidade = descEspecialidade;
	}
}
