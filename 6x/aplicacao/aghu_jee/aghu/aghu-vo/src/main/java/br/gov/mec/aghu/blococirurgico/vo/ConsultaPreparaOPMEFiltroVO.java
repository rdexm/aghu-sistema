package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioSimNao;

public class ConsultaPreparaOPMEFiltroVO implements Serializable {

	private static final long serialVersionUID = 9069371379271622796L;

	private Short requisicaoSelecionada;
	private DominioSimNao licitado;

	public Short getRequisicaoSelecionada() {
		return requisicaoSelecionada;
	}

	public void setRequisicaoSelecionada(Short requisicaoSelecionada) {
		this.requisicaoSelecionada = requisicaoSelecionada;
	}

	public DominioSimNao getLicitado() {
		return licitado;
	}

	public void setLicitado(DominioSimNao licitado) {
		this.licitado = licitado;
	}

	@Override
	public String toString() {
		return "ConsultaPreparaOPMEFiltroVO [requisicaoSelecionada=" + requisicaoSelecionada + ", licitado=" + licitado + "]";
	}

}
