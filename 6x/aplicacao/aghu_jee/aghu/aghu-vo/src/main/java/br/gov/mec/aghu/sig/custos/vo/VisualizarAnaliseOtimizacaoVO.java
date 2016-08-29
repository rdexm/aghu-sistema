package br.gov.mec.aghu.sig.custos.vo;

import java.util.ArrayList;
import java.util.List;

import br.gov.mec.aghu.core.commons.BaseBean;

public class VisualizarAnaliseOtimizacaoVO implements BaseBean{

	private static final long serialVersionUID = 2817819991635923418L;
	
	private List<VisualizarAnaliseCustosObjetosCustoVO> lista = new ArrayList<VisualizarAnaliseCustosObjetosCustoVO>();
	
	private SomatorioAnaliseCustosObjetosCustoVO somatorio = new SomatorioAnaliseCustosObjetosCustoVO();

	public List<VisualizarAnaliseCustosObjetosCustoVO> getLista() {
		return lista;
	}
	
	public void setLista(List<VisualizarAnaliseCustosObjetosCustoVO> lista) {
		this.lista = lista;
	}
	
	public SomatorioAnaliseCustosObjetosCustoVO getSomatorio() {
		return somatorio;
	}
	
	public void setSomatorio(SomatorioAnaliseCustosObjetosCustoVO somatorio) {
		this.somatorio = somatorio;
	}
	
	public List<VisualizarAnaliseCustosObjetosCustoVO> paginar(Integer firstResult, Integer maxResult){
		
		if(lista.size() == 1){
			return this.lista;
		}
		else{
			return lista.subList(firstResult, (firstResult + maxResult) <= lista.size() ?  firstResult + maxResult : lista.size());
		}	
	}
	
	public Long contar() {
		if(lista == null){
			return 0l;
		}
		return (long)lista.size();
	}
}