package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;


public class BuscaDadosInternacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -777230642975934028L;

	private Short codigoConvenioSaude;

	private Byte seqConvenioSaudePlano;

	private Date dataHoraPrimeiroEvento;

	private Date dataHoraUltimoEvento;

	public BuscaDadosInternacaoVO() {
	}

	public BuscaDadosInternacaoVO(Short codigoConvenioSaude,
			Byte seqConvenioSaudePlano, Date dataHoraPrimeiroEvento,
			Date dataHoraUltimoEvento) {
		this.codigoConvenioSaude = codigoConvenioSaude;
		this.seqConvenioSaudePlano = seqConvenioSaudePlano;
		this.dataHoraPrimeiroEvento = dataHoraPrimeiroEvento;
		this.dataHoraUltimoEvento = dataHoraUltimoEvento;
	}

	public Short getCodigoConvenioSaude() {
		return codigoConvenioSaude;
	}

	public void setCodigoConvenioSaude(Short codigoConvenioSaude) {
		this.codigoConvenioSaude = codigoConvenioSaude;
	}

	public Byte getSeqConvenioSaudePlano() {
		return seqConvenioSaudePlano;
	}

	public void setSeqConvenioSaudePlano(Byte seqConvenioSaudePlano) {
		this.seqConvenioSaudePlano = seqConvenioSaudePlano;
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
