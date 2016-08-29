package br.gov.mec.aghu.prescricaoenfermagem.vo;

import java.io.Serializable;
import java.util.List;

public class SelecaoCuidadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7259096608880796066L;
	private Integer penSeq;
	private Integer atdSeq;
	private Short dgnSnbGnbSeq;
	private Short dgnSnbSequencia;
	private Short dgnSequencia;
	private List<Short> etiologias;

	public SelecaoCuidadoVO(){
		
	}
	
	public Integer getPenSeq() {
		return penSeq;
	}

	public void setPenSeq(Integer penSeq) {
		this.penSeq = penSeq;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Short getDgnSnbGnbSeq() {
		return dgnSnbGnbSeq;
	}

	public void setDgnSnbGnbSeq(Short dgnSnbGnbSeq) {
		this.dgnSnbGnbSeq = dgnSnbGnbSeq;
	}

	public Short getDgnSnbSequencia() {
		return dgnSnbSequencia;
	}

	public void setDgnSnbSequencia(Short dgnSnbSequencia) {
		this.dgnSnbSequencia = dgnSnbSequencia;
	}

	public Short getDgnSequencia() {
		return dgnSequencia;
	}

	public void setDgnSequencia(Short dgnSequencia) {
		this.dgnSequencia = dgnSequencia;
	}

	public List<Short> getEtiologias() {
		return etiologias;
	}

	public void setEtiologias(List<Short> etiologias) {
		this.etiologias = etiologias;
	}

}
