package br.gov.mec.aghu.faturamento.vo;

import java.io.Serializable;
import java.util.Date;

public class MenorDataValidacaoApacVO implements Serializable {

	private static final long serialVersionUID = 4350138799349480160L;

	private Date dthrRealizado;

	public Date getDthrRealizado() {
		return dthrRealizado;
	}

	public void setDthrRealizado(Date dthrRealizado) {
		this.dthrRealizado = dthrRealizado;
	}

	public enum Fields {
		DTHR_REALIZADO("dthrRealizado");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
