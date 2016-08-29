package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioTipoAltaUTI;


public class RnCthcVerUtimesesVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5495000077350426063L;

	private Byte diariasUtiMesIni;
	 
	private Byte diariasUtiMesAnt;
	
	private Byte diariasUtiMesAlta;
	
	private DominioTipoAltaUTI tipoUti;
	
	private Boolean utiNeo;
	
	private Boolean retorno;

	public Byte getDiariasUtiMesIni() {
		return diariasUtiMesIni;
	}

	public void setDiariasUtiMesIni(Byte diariasUtiMesIni) {
		this.diariasUtiMesIni = diariasUtiMesIni;
	}

	public Byte getDiariasUtiMesAnt() {
		return diariasUtiMesAnt;
	}

	public void setDiariasUtiMesAnt(Byte diariasUtiMesAnt) {
		this.diariasUtiMesAnt = diariasUtiMesAnt;
	}

	public Byte getDiariasUtiMesAlta() {
		return diariasUtiMesAlta;
	}

	public void setDiariasUtiMesAlta(Byte diariasUtiMesAlta) {
		this.diariasUtiMesAlta = diariasUtiMesAlta;
	}

	public DominioTipoAltaUTI getTipoUti() {
		return tipoUti;
	}

	public void setTipoUti(DominioTipoAltaUTI tipoUti) {
		this.tipoUti = tipoUti;
	}

	public Boolean getUtiNeo() {
		return utiNeo;
	}

	public void setUtiNeo(Boolean utiNeo) {
		this.utiNeo = utiNeo;
	}

	public Boolean getRetorno() {
		return retorno;
	}

	public void setRetorno(Boolean retorno) {
		this.retorno = retorno;
	}

}
