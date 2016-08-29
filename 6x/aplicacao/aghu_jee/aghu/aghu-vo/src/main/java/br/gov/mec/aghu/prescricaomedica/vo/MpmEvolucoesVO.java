package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.MpmEvolucoes;

public class MpmEvolucoesVO implements Serializable {

	private static final long serialVersionUID = 6097247410619909814L;

	private MpmEvolucoes evolucao;

	private Boolean selecionado;

	public MpmEvolucoes getEvolucao() {

		return evolucao;

	}

	public void setEvolucao(MpmEvolucoes evolucao) {

		this.evolucao = evolucao;

	}

	public Boolean getSelecionado() {

		return selecionado;

	}

	public void setSelecionado(Boolean selecionado) {

		this.selecionado = selecionado;

	}

}
