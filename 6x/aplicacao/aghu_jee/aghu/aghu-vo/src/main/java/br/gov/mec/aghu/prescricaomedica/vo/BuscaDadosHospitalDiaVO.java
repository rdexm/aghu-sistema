package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;


public class BuscaDadosHospitalDiaVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -208182517279765642L;

	private Short codigoConvenioSaude;

	private Byte cspSeq;

	private Date dataHoraPrimeiroEvento;

	private Date dataHoraUltimoEvento;

	public BuscaDadosHospitalDiaVO() {
	}

	public BuscaDadosHospitalDiaVO(Short codigoConvenioSaude, Byte cspSeq,
			Date dataHoraPrimeiroEvento, Date dataHoraUltimoEvento) {
		this.codigoConvenioSaude = codigoConvenioSaude;
		this.cspSeq = cspSeq;
		this.dataHoraPrimeiroEvento = dataHoraPrimeiroEvento;
		this.dataHoraUltimoEvento = dataHoraUltimoEvento;
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

	public Date getDataHoraPrimeiroEvento() {
		return dataHoraPrimeiroEvento;
	}

	public void setDataHoraPrimeiroEvento(Date dataHoraPrimeiroEvento) {
		this.dataHoraPrimeiroEvento = dataHoraPrimeiroEvento;
	}

	public Date getDataHoraUltimoEvento() {
		return dataHoraUltimoEvento;
	}

	public void setDataHoraUltimoEvento(Date dataHoraUltimoEvento) {
		this.dataHoraUltimoEvento = dataHoraUltimoEvento;
	}

}
