package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

public class DadosAdicionaisVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4903055864285134837L;
	
	private Byte apgar1;
	private Byte apgar5;
	private Date dtNascimento;
	private BigDecimal temperatura;
	private Byte igFinal;
	private Byte igAtualSemanas;
	private Date dtHrFim;
	
	
	public DadosAdicionaisVO(){
		super();
	}
	
	public DadosAdicionaisVO(Byte apgar1, Byte apgar5, Date dtNascimento,
			BigDecimal temperatura, Byte igFinal, Byte igAtualSemanas, Date dtHrFim) {
		super();
		this.apgar1 = apgar1;
		this.apgar5 = apgar5;
		this.dtNascimento = dtNascimento;
		this.temperatura = temperatura;
		this.igFinal = igFinal;
		this.igAtualSemanas = igAtualSemanas;
		this.dtHrFim = dtHrFim;
	}

	
	public Byte getApgar1() {
		return apgar1;
	}	
	public void setApgar1(Byte apgar1) {
		this.apgar1 = apgar1;
	}
	public Byte getApgar5() {
		return apgar5;
	}
	public void setApgar5(Byte apgar5) {
		this.apgar5 = apgar5;
	}
	public Date getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public BigDecimal getTemperatura() {
		return temperatura;
	}
	public void setTemperatura(BigDecimal temperatura) {
		this.temperatura = temperatura;
	}
	public Byte getIgFinal() {
		return igFinal;
	}
	public void setIgFinal(Byte igFinal) {
		this.igFinal = igFinal;
	}
	public Byte getIgAtualSemanas() {
		return igAtualSemanas;
	}
	public void setIgAtualSemanas(Byte igAtualSemanas) {
		this.igAtualSemanas = igAtualSemanas;
	}
	public Date getDtHrFim() {
		return dtHrFim;
	}
	public void setDtHrFim(Date dtHrFim) {
		this.dtHrFim = dtHrFim;
	}

}
