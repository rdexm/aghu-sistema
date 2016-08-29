package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.List;

/**
 * Utilizado para validações de um item de af é válido para programação de entrega
 * 
 * @author ueslei.rosado
 * 
 */
public class EntradaMateriasDiaVO implements Serializable {
	private static final long serialVersionUID = 3758522936860979868L;

	private List<DadosEntradaMateriasDiaVO> listaDadosEntradaMateriasDia;
	private List<GrupoEntradaMateriasDiaVO> listaGrupoEntradaMateriasDia;
	

	public EntradaMateriasDiaVO() {

	}

	public EntradaMateriasDiaVO(List<DadosEntradaMateriasDiaVO> listaDadosEntradaMateriasDia, List<GrupoEntradaMateriasDiaVO> listaGrupoEntradaMateriasDia) {
		this.listaDadosEntradaMateriasDia = listaDadosEntradaMateriasDia;
		this.listaGrupoEntradaMateriasDia = listaGrupoEntradaMateriasDia;
	}
	

	public List<DadosEntradaMateriasDiaVO> getListaDadosEntradaMateriasDia() {
		return listaDadosEntradaMateriasDia;
	}

	public void setListaDadosEntradaMateriasDia(
			List<DadosEntradaMateriasDiaVO> listaDadosEntradaMateriasDia) {
		this.listaDadosEntradaMateriasDia = listaDadosEntradaMateriasDia;
	}

	public List<GrupoEntradaMateriasDiaVO> getListaGrupoEntradaMateriasDia() {
		return listaGrupoEntradaMateriasDia;
	}

	public void setListaGrupoEntradaMateriasDia(
			List<GrupoEntradaMateriasDiaVO> listaGrupoEntradaMateriasDia) {
		this.listaGrupoEntradaMateriasDia = listaGrupoEntradaMateriasDia;
	}

	
}
