package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

public class CirurgiaComPacEmTransOperatorioVO implements Serializable {

	private static final long serialVersionUID = -2061766678146074932L;

	private Integer codPaciente;
	private String nomePaciente;
	private Integer prontuario;
	private Integer crgSeq;
	private Boolean panelAberto;
	private Date dataExtratoTransOperatorio;
	
	public CirurgiaComPacEmTransOperatorioVO(Integer crgSeq) {
		super();
		this.crgSeq = crgSeq;
	}
	
	public CirurgiaComPacEmTransOperatorioVO() {
	}
	
	public Integer getCodPaciente() {
		return codPaciente;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public Integer getCrgSeq() {
		return crgSeq;
	}
	public Boolean getPanelAberto() {
		return panelAberto;
	}
	public void setCodPaciente(Integer codPaciente) {
		this.codPaciente = codPaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}
	public void setPanelAberto(Boolean panelAberto) {
		this.panelAberto = panelAberto;
	}
	
	public Date getDataExtratoTransOperatorio() {
		return dataExtratoTransOperatorio;
	}

	public void setDataExtratoTransOperatorio(Date dataExtratoTransOperatorio) {
		this.dataExtratoTransOperatorio = dataExtratoTransOperatorio;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((crgSeq == null) ? 0 : crgSeq.hashCode());
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
		CirurgiaComPacEmTransOperatorioVO other = (CirurgiaComPacEmTransOperatorioVO) obj;
		if (crgSeq == null) {
			if (other.crgSeq != null) {
				return false;
			}
		} else if (!crgSeq.equals(other.crgSeq)) {
			return false;
		}
		return true;
	}

}
