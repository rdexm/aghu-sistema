package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoPrevAtende;

public class OrdenacaoPorGravidadesVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5709179150326067049L;
	
	private Long trgSeq;
	private Short agrSeq;
	private Short agrOrdem;
	private DominioTipoPrevAtende indPrevAtend;
	private Integer conNumero;
	private Date dthrPrevAtend;
	
	
	public Date getDthrPrevAtend() {
		return dthrPrevAtend;
	}


	public void setDthrPrevAtend(Date dthrPrevAtend) {
		this.dthrPrevAtend = dthrPrevAtend;
	}


	public Integer getConNumero() {
		return conNumero;
	}


	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}


	public Long getTrgSeq() {
		return trgSeq;
	}


	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}


	public Short getAgrSeq() {
		return agrSeq;
	}


	public void setAgrSeq(Short agrSeq) {
		this.agrSeq = agrSeq;
	}


	public Short getAgrOrdem() {
		return agrOrdem;
	}


	public void setAgrOrdem(Short agrOrdem) {
		this.agrOrdem = agrOrdem;
	}


	public DominioTipoPrevAtende getIndPrevAtend() {
		return indPrevAtend;
	}


	public void setIndPrevAtend(DominioTipoPrevAtende indPrevAtend) {
		this.indPrevAtend = indPrevAtend;
	}


	public enum Fields {
		TRG_SEQ("trgSeq"),
		AGR_SEQ("agrSeq"),
		AGR_ORDEM("agrOrdem"),
		CON_NUMERO("conNumero"),
		DTHR_PREV_ATEND("dthrPrevAtend"),
		IND_PREV_ATEND("indPrevAtend");

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
