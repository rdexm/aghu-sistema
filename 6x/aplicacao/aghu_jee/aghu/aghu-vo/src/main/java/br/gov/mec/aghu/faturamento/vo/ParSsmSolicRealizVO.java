package br.gov.mec.aghu.faturamento.vo;

public class ParSsmSolicRealizVO {

	private Integer cthSeq = null;
	private String ssmStrSolicitado = null;
	private String ssmStrRealizado = null;

	public ParSsmSolicRealizVO() {
		
		super();
	}

	public Integer getCthSeq() {

		return this.cthSeq;
	}

	public void setCthSeq(final Integer cthSeq) {

		this.cthSeq = cthSeq;
	}

	public String getSsmStrSolicitado() {

		return this.ssmStrSolicitado;
	}

	public void setSsmStrSolicitado(final String ssmStrSolicitado) {

		this.ssmStrSolicitado = ssmStrSolicitado;
	}

	public String getSsmStrRealizado() {

		return this.ssmStrRealizado;
	}

	public void setSsmStrRealizado(final String ssmStrRealizado) {

		this.ssmStrRealizado = ssmStrRealizado;
	}
	
	@Override
	public String toString() {
	
		return "[c: " + this.cthSeq + " s: " + this.ssmStrSolicitado + " r: " + this.ssmStrRealizado + "]";
	}
}
