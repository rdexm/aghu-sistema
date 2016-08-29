package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.List;

public class SumarioAltaProcedimentosCrgListasVO implements Serializable{
	
	private static final long serialVersionUID = 4691545640543786108L;
	
	private List<SumarioAltaProcedimentosCrgVO> listaComboCirurgias;
	private List<SumarioAltaProcedimentosCrgVO> listaGridCirurgias;
	
	public List<SumarioAltaProcedimentosCrgVO> getListaComboCirurgias() {
		return listaComboCirurgias;
	}
	public void setListaComboCirurgias(
			List<SumarioAltaProcedimentosCrgVO> listaComboCirurgias) {
		this.listaComboCirurgias = listaComboCirurgias;
	}
	public List<SumarioAltaProcedimentosCrgVO> getListaGridCirurgias() {
		return listaGridCirurgias;
	}
	public void setListaGridCirurgias(
			List<SumarioAltaProcedimentosCrgVO> listaGridCirurgias) {
		this.listaGridCirurgias = listaGridCirurgias;
	}

}
