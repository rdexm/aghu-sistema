package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class BuscaDadosAtendimentoUrgenciaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2801113525283390048L;

	private Short codigoConvenioSaude;

	private Byte cspSeq;

	public BuscaDadosAtendimentoUrgenciaVO() {
	}

	public BuscaDadosAtendimentoUrgenciaVO(Short codigoConvenioSaude,
			Byte cspSeq) {
		this.codigoConvenioSaude = codigoConvenioSaude;
		this.cspSeq = cspSeq;
	}
	
	public Short getCodigoConvenioSaude() {
		return codigoConvenioSaude;
	}

	public void setCodigoConvenioSaude(Short codigoConvenioSaude) {
		this.codigoConvenioSaude = codigoConvenioSaude;
	}

	public Byte getCspSeq() {
		return cspSeq;
	}

	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

}
