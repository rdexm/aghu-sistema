package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.model.MbcProcEspPorCirurgiasId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;

public class SumarioAltaProcedimentosCrgVO extends SumarioAltaProcedimentosVO {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4515319514202192060L;
	private Integer seqProcedimentoCirurgico;
	private MbcProcEspPorCirurgiasId procEspPorCirurgiasId;

	public SumarioAltaProcedimentosCrgVO(){
		super(new MpmAltaSumarioId());
	}
	
	public SumarioAltaProcedimentosCrgVO(MpmAltaSumarioId id) {
		super(id);
	}

	/**
	 * As comparações abaixo simulam a eliminação de registros duplicados,
	 * encontrada no arquivo MPMF_SUMARIO_ALTA.pll, na procedure MPMP_LISTA_CIRG_REALIZADAS,
	 * cursor cur_acr, feita através do operador minus da consulta SQL.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SumarioAltaProcedimentosCrgVO)) {
			return false;
		}

		SumarioAltaProcedimentosCrgVO other = (SumarioAltaProcedimentosCrgVO) obj;

		return this.getData().compareTo(other.getData()) == 0 
		&& (this.getProcEspPorCirurgiasId() != null && this.getProcEspPorCirurgiasId().equals(other.getProcEspPorCirurgiasId()))
		&& (this.getDescricao().equalsIgnoreCase(other.getDescricao()));
	}

	@Override
	public int hashCode() {
		return this.getProcEspPorCirurgiasId() == null ? 0 : this.getProcEspPorCirurgiasId().hashCode();
	}

	public void setSeqProcedimentoCirurgico(Integer seqProcedimentoCirurgico) {
		this.seqProcedimentoCirurgico = seqProcedimentoCirurgico;
	}

	public Integer getSeqProcedimentoCirurgico() {
		return this.seqProcedimentoCirurgico;
	}

	public void setProcEspPorCirurgiasId(MbcProcEspPorCirurgiasId procEspPorCirurgiasId) {
		this.procEspPorCirurgiasId = procEspPorCirurgiasId;
	}

	public MbcProcEspPorCirurgiasId getProcEspPorCirurgiasId() {
		return this.procEspPorCirurgiasId;
	}

}