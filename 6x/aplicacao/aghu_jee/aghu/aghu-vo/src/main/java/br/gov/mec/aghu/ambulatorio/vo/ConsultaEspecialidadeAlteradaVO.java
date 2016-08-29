package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Filtro para busca de consulta com especialidade alterada AacConsultasJn e
 * AacGradeAgendamenConsultas
 * 
 * @author felipe.rocha
 * 
 */
public class ConsultaEspecialidadeAlteradaVO implements Serializable {

	private static final long serialVersionUID = -4195817103912553934L;

	private Integer grdSeq;
	private Date jnDateTime;
	private Short espSeq;

	public Integer getGrdSeq() {
		return grdSeq;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	public Date getJnDateTime() {
		return jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	
	public enum Fields {

		GRD_SEQ("grdSeq"),
		JN_DATE_TIME("jnDateTime"),
		ESP_SEQ("espSeq");
		
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
