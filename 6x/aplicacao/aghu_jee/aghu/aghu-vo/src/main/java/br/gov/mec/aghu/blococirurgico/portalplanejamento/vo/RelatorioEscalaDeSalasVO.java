package br.gov.mec.aghu.blococirurgico.portalplanejamento.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class RelatorioEscalaDeSalasVO implements Serializable {

	private static final long serialVersionUID = -7482889145371378355L;
	
	private String nomeHospital;
	private Date dataAtual;
	private String descricaoUnidade;
	private String sala;
	private List<RelatorioEscalaDeSalasLinhaVO> linha;
	
	public String getNomeHospital() {
		return nomeHospital;
	}
	public void setNomeHospital(String nomeHospital) {
		this.nomeHospital = nomeHospital;
	}
	public Date getDataAtual() {
		return dataAtual;
	}
	public void setDataAtual(Date dataAtual) {
		this.dataAtual = dataAtual;
	}
	public String getDescricaoUnidade() {
		return descricaoUnidade;
	}
	public void setDescricaoUnidade(String descricaoUnidade) {
		this.descricaoUnidade = descricaoUnidade;
	}
	public String getSala() {
		return sala;
	}
	public void setSala(String sala) {
		this.sala = sala;
	}
	public void setLinha(List<RelatorioEscalaDeSalasLinhaVO> linha) {
		this.linha = linha;
	}
	public List<RelatorioEscalaDeSalasLinhaVO> getLinha() {
		return linha;
	}
}