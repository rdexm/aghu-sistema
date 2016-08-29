package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.List;

public class ReImprimirAceiteTecnicoParaSerRealizadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3131999192237260940L;
	
	private List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList;

	public List<AceiteTecnicoParaSerRealizadoVO> getItemRetiradaList() {
		return itemRetiradaList;
	}

	public void setItemRetiradaList(List<AceiteTecnicoParaSerRealizadoVO> itemRetiradaList) {
		this.itemRetiradaList = itemRetiradaList;
	}
	
}
