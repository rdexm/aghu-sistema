package br.gov.mec.aghu.prescricaomedica.vo;

/**
 * #3855 Prescrição: Emitir relatório de estatísica da produtividade do
 * consultor
 * 
 * @author aghu
 *
 */
public class NumeroHorasUteisVO {

	/*
	 * Atributos utilizados pela função MPMC_NHORAS_UTEIS
	 */

	private int horas;
	private int minutos;

	public int getHoras() {
		return horas;
	}

	public void setHoras(int horas) {
		this.horas = horas;
	}

	public int getMinutos() {
		return minutos;
	}

	public void setMinutos(int minutos) {
		this.minutos = minutos;
	}

	@Override
	public String toString() {
		return horas + ":" + String.format("%02d", minutos);
	}

}
