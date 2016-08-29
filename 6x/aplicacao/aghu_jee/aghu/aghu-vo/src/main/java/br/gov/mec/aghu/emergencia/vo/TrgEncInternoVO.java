package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

public class TrgEncInternoVO implements Serializable {
	private static final long serialVersionUID = -4101227518458831214L;

	private Short seqp;
	private Integer consultaNumero;
	private Date dthrInicio;
	
	
	public enum Fields {
		SEQP("seqp"),
		CON_NUMERO("consultaNumero"),
		DTHR_INICIO("dthrInicio");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getConsultaNumero() {
		return consultaNumero;
	}

	public void setConsultaNumero(Integer consultaNumero) {
		this.consultaNumero = consultaNumero;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}
}
