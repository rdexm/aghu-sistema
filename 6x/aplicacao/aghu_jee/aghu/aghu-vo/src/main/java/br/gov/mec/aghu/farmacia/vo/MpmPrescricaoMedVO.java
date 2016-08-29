package br.gov.mec.aghu.farmacia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.core.utils.DateUtil;

public class MpmPrescricaoMedVO implements Serializable {

	private static final long serialVersionUID = -1386293010232528505L;
	
	private Date dtReferencia;
	private Date dthrInicio;
	private Date dthrFim;
	private Integer atdSeq;
	private Integer seq;
	
	//UTILIZADO PARA PREENCHER SB COM PATTERN CORRETO
	private String dataReferenciaS;
	private String dataInicioS;
	private String dataFimS;

	public enum Fields {
		DT_REFERENCIA("dtReferencia"),
		DTHR_INICIO("dthrInicio"),
		DTHR_FIM("dthrFim"),
		ATD_SEQ("atdSeq"),
		SEQ("seq"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Date getDtReferencia() {
		return dtReferencia;
	}

	public void setDtReferencia(Date dtReferencia) {
		this.dtReferencia = dtReferencia;
	}

	public Date getDthrInicio() {
		return dthrInicio;
	}

	public void setDthrInicio(Date dthrInicio) {
		this.dthrInicio = dthrInicio;
	}

	public Date getDthrFim() {
		return dthrFim;
	}

	public void setDthrFim(Date dthrFim) {
		this.dthrFim = dthrFim;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getDataReferenciaS() {
		
		dataReferenciaS = DateUtil.obterDataFormatada(dtReferencia, "dd/MM/yyyy");		
		return dataReferenciaS;
	}

	public void setDataReferenciaS(String dataReferenciaS) {
		this.dataReferenciaS = dataReferenciaS;
	}

	public String getDataInicioS() {
		
		dataInicioS = DateUtil.obterDataFormatada(dthrInicio, "dd/MM/yyyy HH:mm");
		return dataInicioS;
	}

	public void setDataInicioS(String dataInicioS) {
		this.dataInicioS = dataInicioS;
	}

	public String getDataFimS() {
		
		dataFimS = DateUtil.obterDataFormatada(dthrFim, "dd/MM/yyyy HH:mm");
		return dataFimS;
	}

	public void setDataFimS(String dataFimS) {
		this.dataFimS = dataFimS;
	}
}
