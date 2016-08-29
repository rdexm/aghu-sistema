package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

public class ConsultaDadosAPACVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 506476651620784338L;

	
	private byte capSeq;
	private Long atmNumero;
	private Date dtInicioTratamento;
	private Date dtInicioValidade;
	private Date dtFimValidade;
	private Byte cpeMes;
	private Short cpeAno;
	private Short ciclos;
	private Date dtFimTratamento;
	
	public enum Fields {
		CAP_SEQ("capSeq"),
		ATM_NUMERO("atmNumero"),
		DT_INICIO_TRATAMENTO("dtInicioTratamento"),
		DT_INICIO_VALIDADE("dtInicioValidade"),
		DT_FIM_VALIDADE("dtFimValidade"),
		MES("cpeMes"),
		ANO("cpeAno"),
		CICLOS("ciclos");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
	public Date getDtInicioTratamento() {
		return dtInicioTratamento;
	}

	public void setDtInicioTratamento(Date dtInicioTratamento) {
		this.dtInicioTratamento = dtInicioTratamento;
	}

	public Date getDtInicioValidade() {
		return dtInicioValidade;
	}

	public void setDtInicioValidade(Date dtInicioValidade) {
		this.dtInicioValidade = dtInicioValidade;
	}

	public Date getDtFimValidade() {
		return dtFimValidade;
	}

	public void setDtFimValidade(Date dtFimValidade) {
		this.dtFimValidade = dtFimValidade;
	}

	public Byte getCpeMes() {
		return cpeMes;
	}

	public void setCpeMes(Byte cpeMes) {
		this.cpeMes = cpeMes;
	}

	public Short getCpeAno() {
		return cpeAno;
	}

	public void setCpeAno(Short cpeAno) {
		this.cpeAno = cpeAno;
	}

	public Short getCiclos() {
		if(ciclos==null){
			return 0;
		}else{
			return ciclos;
		}
		
	}

	public void setCiclos(Short ciclos) {
		this.ciclos = ciclos;
	}

	public Date getDtFimTratamento() {
	      
		if(dtFimTratamento==null){
			if(ciclos==null){
				dtFimTratamento = dtInicioTratamento;
			}else{
				 Calendar data = Calendar.getInstance(); 
				 data.setTime(dtInicioTratamento);
				 data.add(Calendar.MONTH, ciclos.intValue()); 
				 dtFimTratamento = data.getTime();
			}
		}
		return dtFimTratamento;
	}

	public void setDtFimTratamento(Date dtFimTratamento) {
		this.dtFimTratamento = dtFimTratamento;
	}

	public byte getCapSeq() {
		return capSeq;
	}

	public void setTeste(byte capSeq) {
		this.capSeq = capSeq;
	}

	public Long getAtmNumero() {
		return atmNumero;
	}

	public void setAtmNumero(Long atmNumero) {
		this.atmNumero = atmNumero;
	}

}
