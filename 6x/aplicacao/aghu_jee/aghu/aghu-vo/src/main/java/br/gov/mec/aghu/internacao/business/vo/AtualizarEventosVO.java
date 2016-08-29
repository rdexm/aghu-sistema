package br.gov.mec.aghu.internacao.business.vo;

import java.util.Date;

public class AtualizarEventosVO {

	private Date dthrPrimeiroEvento;
	private Date dthrUltimoEvento;

	public AtualizarEventosVO() {
	}

	public AtualizarEventosVO(Date dthrPrimeiroEvento, Date dthrUltimoEvento) {
		this.dthrPrimeiroEvento = dthrPrimeiroEvento;
		this.dthrUltimoEvento = dthrUltimoEvento;
	}

	public Date getDthrPrimeiroEvento() {
		return this.dthrPrimeiroEvento;
	}

	public void setDthrPrimeiroEvento(Date dthrPrimeiroEvento) {
		this.dthrPrimeiroEvento = dthrPrimeiroEvento;
	}

	public Date getDthrUltimoEvento() {
		return this.dthrUltimoEvento;
	}

	public void setDthrUltimoEvento(Date dthrUltimoEvento) {
		this.dthrUltimoEvento = dthrUltimoEvento;
	}

}