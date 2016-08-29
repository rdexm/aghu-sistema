package br.gov.mec.aghu.controleinfeccao.vo;

import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class RelatorioBuscaAtivaUnidadePacientesVO implements BaseBean {

	private static final long serialVersionUID = -5036322323573019899L;
	
	private String unidadeDescricao;
	
	private List<RelatorioBuscaAtivaPacientesVO> listaPacientes;

	public List<RelatorioBuscaAtivaPacientesVO> getListaPacientes() {
		return listaPacientes;
	}

	public void setListaPacientes(
			List<RelatorioBuscaAtivaPacientesVO> listaPacientes) {
		this.listaPacientes = listaPacientes;
	}

	public String getUnidadeDescricao() {
		return unidadeDescricao;
	}

	public void setUnidadeDescricao(String unidadeDescricao) {
		this.unidadeDescricao = unidadeDescricao;
	}
}
