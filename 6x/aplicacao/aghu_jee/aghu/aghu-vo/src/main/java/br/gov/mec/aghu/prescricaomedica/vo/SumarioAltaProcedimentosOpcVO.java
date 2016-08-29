package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.MbcProcEspPorCirurgias;
import br.gov.mec.aghu.model.MpmAltaCirgRealizadaId;

public class SumarioAltaProcedimentosOpcVO implements Serializable {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3879339950579380625L;
	private MpmAltaCirgRealizadaId id;
	private String descCirurgia;
	private Date dthrCirurgia;
	private MbcProcEspPorCirurgias procedimentoEspCirurgia;
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SumarioAltaProcedimentosOpcVO)) {
			return false;
		}
		
		SumarioAltaProcedimentosOpcVO other = (SumarioAltaProcedimentosOpcVO) obj;
		return (getProcedimentoEspCirurgia() != null && getProcedimentoEspCirurgia().equals(other.getProcedimentoEspCirurgia())); 
	}
	
	@Override
	public int hashCode() {
		return this.getProcedimentoEspCirurgia() == null ? 0 : this.getProcedimentoEspCirurgia().hashCode();
	}

	public MpmAltaCirgRealizadaId getId() {
		return this.id;
	}

	public void setId(MpmAltaCirgRealizadaId id) {
		this.id = id;
	}

	public String getDescCirurgia() {
		return this.descCirurgia;
	}

	public void setDescCirurgia(String descCirurgia) {
		this.descCirurgia = descCirurgia;
	}

	public Date getDthrCirurgia() {
		return this.dthrCirurgia;
	}

	public void setDthrCirurgia(Date dthrCirurgia) {
		this.dthrCirurgia = dthrCirurgia;
	}

	public MbcProcEspPorCirurgias getProcedimentoEspCirurgia() {
		return this.procedimentoEspCirurgia;
	}

	public void setProcedimentoEspCirurgia(MbcProcEspPorCirurgias procedimentoEspCirurgia) {
		this.procedimentoEspCirurgia = procedimentoEspCirurgia;
	}
	
}