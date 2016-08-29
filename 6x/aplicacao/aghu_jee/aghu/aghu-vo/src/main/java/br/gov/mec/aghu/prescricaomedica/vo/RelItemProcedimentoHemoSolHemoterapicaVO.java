package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.List;

public class RelItemProcedimentoHemoSolHemoterapicaVO implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6468915219002074065L;

	private String descricao; // F_DESCRICAO
	
	//private String indIrradiado; //F_IND_IRRADIADO
	//private String indDesleucocitado;
	//private String indLavado; //F_IND_LAVADO
	
	private List<RelSolHemoterapicasJustificativaVO> listaJustificativas;

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	/*
	public String getIndIrradiado() {
		return indIrradiado;
	}

	public void setIndIrradiado(String indIrradiado) {
		this.indIrradiado = indIrradiado;
	}

	public String getIndDesleucocitado() {
		return indDesleucocitado;
	}

	public void setIndDesleucocitado(String indDesleucocitado) {
		this.indDesleucocitado = indDesleucocitado;
	}

	public String getIndLavado() {
		return indLavado;
	}

	public void setIndLavado(String indLavado) {
		this.indLavado = indLavado;
	}
	*/

	public void setListaJustificativas(
			List<RelSolHemoterapicasJustificativaVO> listaJustificativas) {
		this.listaJustificativas = listaJustificativas;
	}

	public List<RelSolHemoterapicasJustificativaVO> getListaJustificativas() {
		return listaJustificativas;
	}
	
}
