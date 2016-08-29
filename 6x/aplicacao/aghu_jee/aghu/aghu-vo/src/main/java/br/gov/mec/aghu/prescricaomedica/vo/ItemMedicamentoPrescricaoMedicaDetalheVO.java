
package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

public class ItemMedicamentoPrescricaoMedicaDetalheVO implements Serializable {

	private static final long serialVersionUID = -9040847305045848947L;
	
	private String descricao;
	private String dthrInicio;
	private String dthrFim;
	private String duracaoTratSolicitado;
	private String indOrientacaoAvaliador;
	private String tipoInfeccao;
	private String nomeGerme;
	private String indUsoAntiMicrobiano;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(String dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public String getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(String dthrFim) {
		this.dthrFim = dthrFim;
	}

	public String getDuracaoTratSolicitado() {
		return duracaoTratSolicitado;
	}

	public void setDuracaoTratSolicitado(String duracaoTratSolicitado) {
		this.duracaoTratSolicitado = duracaoTratSolicitado;
	}

	public String getIndOrientacaoAvaliador() {
		return indOrientacaoAvaliador;
	}

	public void setIndOrientacaoAvaliador(String indOrientacaoAvaliador) {
		this.indOrientacaoAvaliador = indOrientacaoAvaliador;
	}

	public String getTipoInfeccao() {
		return tipoInfeccao;
	}

	public void setTipoInfeccao(String tipoInfeccao) {
		this.tipoInfeccao = tipoInfeccao;
	}

	public String getNomeGerme() {
		return nomeGerme;
	}

	public void setNomeGerme(String nomeGerme) {
		this.nomeGerme = nomeGerme;
	}

	public String getIndUsoAntiMicrobiano() {
		return indUsoAntiMicrobiano;
	}

	public void setIndUsoAntiMicrobiano(String indUsoAntiMicrobiano) {
		this.indUsoAntiMicrobiano = indUsoAntiMicrobiano;
	}
}