package br.gov.mec.aghu.blococirurgico.vo;

import java.util.List;
import java.util.Map;

public class MbcOpmesListaGrupoExcludente {

	private List<MbcOpmesVO> listaPesquisada;
	private List<GrupoExcludenteVO> listaGrupoExcludente;
	private Map<Long, List<MbcOpmesVO>> mapTabelaMateriais;


	public List<MbcOpmesVO> getListaPesquisada() {
		return listaPesquisada;
	}

	public void setListaPesquisada(List<MbcOpmesVO> listaPesquisada) {
		this.listaPesquisada = listaPesquisada;
	}

	public List<GrupoExcludenteVO> getListaGrupoExcludente() {
		return listaGrupoExcludente;
	}

	public void setListaGrupoExcludente(
			List<GrupoExcludenteVO> listaGrupoExcludente) {
		this.listaGrupoExcludente = listaGrupoExcludente;
	}
	
	public Map<Long, List<MbcOpmesVO>> getMapTabelaMateriais() {
		return mapTabelaMateriais;
	}

	public void setMapTabelaMateriais(Map<Long, List<MbcOpmesVO>> mapTabelaMateriais) {
		this.mapTabelaMateriais = mapTabelaMateriais;
	}
	
}
