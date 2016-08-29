package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class FiltroConsultaBloqueioConsultaVO implements Serializable {

	private static final long serialVersionUID = -4666980402783255431L;
	
	private String situacao;
	private String descricao;
	private DominioSituacao indSituacao;
	private DominioSimNao dominioSimNao;

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public String getSituacao() {
		return situacao;
	}

	public void setSituacao(String situacao) {
		this.situacao = situacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}

	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}

}
