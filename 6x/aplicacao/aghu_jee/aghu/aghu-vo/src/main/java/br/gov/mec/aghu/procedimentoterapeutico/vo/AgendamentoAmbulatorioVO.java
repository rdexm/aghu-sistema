package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.util.Date;

import br.gov.mec.aghu.core.commons.BaseBean;

public class AgendamentoAmbulatorioVO implements BaseBean {

	private static final long serialVersionUID = 2825983462327968912L;
	
	private Integer grdSeq;
	private Integer conNumero;
	private Date dataConsulta;
	
	public Integer getGrdSeq() {
		return grdSeq;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	public Integer getConNumero() {
		return conNumero;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Date getDataConsulta() {
		return dataConsulta;
	}

	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}

	public enum Fields {

		GRD_SEQ("grdSeq"), 
		CON_NUMERO("conNumero"),
		DATA_CONSULTA("dataConsulta");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
}
