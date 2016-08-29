package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

public class SumarioPrescricaoMedicaVO implements Serializable {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 3445792697403624668L;
	private Integer codPaciente;
	private Integer atdSeq;
	private Integer atdPac;
	private Date dthrInicio;
	private Date dthrFim;
	private int idx;
	private boolean selected;
	private byte [] pdfFile;
	private byte [] pdfFileNaoProtegido;

	
	public SumarioPrescricaoMedicaVO() {
		super();
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
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

	public Integer getCodPaciente() {
		return codPaciente;
	}

	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}

	public int getIdx() {
		return idx;
	}

	public void setIdx(int idx) {
		this.idx = idx;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	public byte[] getPdfFile() {
		return pdfFile;
	}

	public void setPdfFile(byte[] pdfFile) {
		this.pdfFile = (byte[])pdfFile.clone();
	}

	public Integer getAtdPac() {
		return atdPac;
	}

	public void setAtdPac(Integer atdPac) {
		this.atdPac = atdPac;
	}

	public byte[] getPdfFileNaoProtegido() {
		return pdfFileNaoProtegido;
	}

	public void setPdfFileNaoProtegido(byte[] pdfFileNaoProtegido) {
		this.pdfFileNaoProtegido = pdfFileNaoProtegido;
	}
}