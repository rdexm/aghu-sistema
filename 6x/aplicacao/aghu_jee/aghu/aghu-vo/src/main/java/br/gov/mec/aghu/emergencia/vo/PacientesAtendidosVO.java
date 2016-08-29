package br.gov.mec.aghu.emergencia.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioTipoMovimento;

public class PacientesAtendidosVO implements Serializable {

	private static final long serialVersionUID = -3719423092534467385L;

	private Long trgSeq;
	private Integer pacCodigo;
	private Short segSeq;
	private DominioPacAtendimento indPacAtendimento;
	private Short unfSeq;
	private DominioTipoMovimento ultTipoMvt;
	private Date dthrUltMvto;
	private Integer conNumero;
	private String tooltipUltimoAtendimento;
	private Short seqp;
	
	public Long getTrgSeq() {
		return trgSeq;
	}
	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Short getSegSeq() {
		return segSeq;
	}
	public void setSegSeq(Short segSeq) {
		this.segSeq = segSeq;
	}
	public DominioPacAtendimento getIndPacAtendimento() {
		return indPacAtendimento;
	}
	public void setIndPacAtendimento(DominioPacAtendimento indPacAtendimento) {
		this.indPacAtendimento = indPacAtendimento;
	}
	
	public Short getUnfSeq() {
		return unfSeq;
	}
	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}
	public DominioTipoMovimento getUltTipoMvt() {
		return ultTipoMvt;
	}
	public void setUltTipoMvt(DominioTipoMovimento ultTipoMvt) {
		this.ultTipoMvt = ultTipoMvt;
	}
	public Date getDthrUltMvto() {
		return dthrUltMvto;
	}
	public void setDthrUltMvto(Date dthrUltMvto) {
		this.dthrUltMvto = dthrUltMvto;
	}
	
	public Integer getConNumero() {
		return conNumero;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public String getTooltipUltimoAtendimento() {
		return tooltipUltimoAtendimento;
	}

	public void setTooltipUltimoAtendimento(String tooltipUltimoAtendimento) {
		this.tooltipUltimoAtendimento = tooltipUltimoAtendimento;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public enum Fields {
		TRG_SEQ("trgSeq"),
		PAC_CODIGO("pacCodigo"),
		SEG_SEQ("segSeq"),
		IND_PAC_ATENDIMENTO("indPacAtendimento"),
		UNF_SEQ("unfSeq"),
		ULT_TIPO_MVT("ultTipoMvt"),
		DTHR_ULT_MVTO("dthrUltMvto"),
		CON_NUMERO("conNumero"), 
		ATD_SEQ("atdSeq"),
		SEQP("seqp");

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
