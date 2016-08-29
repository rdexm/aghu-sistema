package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RelatorioBuscaAtivaPacientesCCIHVO implements BaseBean {

	private static final long serialVersionUID = -9161754707987373810L;

	private List<RelatorioBuscaAtivaUnidadePacientesVO> unidades;
	private String dataGeracao;

	public String getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(String dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public List<RelatorioBuscaAtivaUnidadePacientesVO> getUnidades() {
		return unidades;
	}

	public void setUnidades(List<RelatorioBuscaAtivaUnidadePacientesVO> unidades) {
		this.unidades = unidades;
	}
}