package br.gov.mec.aghu.perinatologia.vo;

import java.util.Date;

public class DataNascimentoVO {
	private Date dtNascimento;

	public DataNascimentoVO(){}
	
	public DataNascimentoVO(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}

	public Date getDtNascimento() {
		return dtNascimento;
	}

	public void setDtNascimento(Date dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
}
