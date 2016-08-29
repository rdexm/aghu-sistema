package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

public class RelatorioImprimirTicketRetornoVO implements Serializable {

	private static final long serialVersionUID = 6017637487491235398L;

	private String nomeHospital;
	private String descricao;
	private String identificacao;
	private String nomeMedico;
	
	public String getNomeHospital() {
		return nomeHospital;
	}
	public void setNomeHospital(String nomeHospital) {
		this.nomeHospital = nomeHospital;
	}
	public String getDescricao() {
		return descricao;
	}
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	public String getIdentificacao() {
		return identificacao;
	}
	public void setIdentificacao(String identificacao) {
		this.identificacao = identificacao;
	}
	public String getNomeMedico() {
		return nomeMedico;
	}
	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}
}