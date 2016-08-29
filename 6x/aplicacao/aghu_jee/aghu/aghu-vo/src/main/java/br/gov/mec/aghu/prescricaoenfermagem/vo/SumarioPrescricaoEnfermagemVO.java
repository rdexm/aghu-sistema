package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.Date;

@SuppressWarnings({"PMD.NPathComplexity"})
public class SumarioPrescricaoEnfermagemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5261900545791971585L;

	private Integer codPaciente;
	private Integer atdSeq;
	private Integer atdPac;
	private Date dthrInicio;
	private Date dthrFim;
	private int idx;
	private boolean selected;
	private byte [] pdfFile;
	private byte [] pdfFileNaoProtegido;

	
	public SumarioPrescricaoEnfermagemVO() {
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((atdPac == null) ? 0 : atdPac.hashCode());
		result = prime * result + ((atdSeq == null) ? 0 : atdSeq.hashCode());
		result = prime * result + ((codPaciente == null) ? 0 : codPaciente.hashCode());
		result = prime * result + idx;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SumarioPrescricaoEnfermagemVO other = (SumarioPrescricaoEnfermagemVO) obj;
		if (atdPac == null) {
			if (other.atdPac != null) {
				return false;
			}
		} else if (!atdPac.equals(other.atdPac)) {
			return false;
		}
		if (atdSeq == null) {
			if (other.atdSeq != null) {
				return false;
			}
		} else if (!atdSeq.equals(other.atdSeq)) {
			return false;
		}
		if (codPaciente == null) {
			if (other.codPaciente != null) {
				return false;
			}
		} else if (!codPaciente.equals(other.codPaciente)) {
			return false;
		}
		if (idx != other.idx) {
			return false;
		}
		return true;
	}

	public byte[] getPdfFileNaoProtegido() {
		return pdfFileNaoProtegido;
	}

	public void setPdfFileNaoProtegido(byte[] pdfFileNaoProtegido) {
		this.pdfFileNaoProtegido = pdfFileNaoProtegido;
	}

}
