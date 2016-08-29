package br.gov.mec.aghu.prescricaomedica.vo;

import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class SumarioAltaPrescricaoProcedimentoVO extends SumarioAltaProcedimentosVO {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = 7185761694582415520L;

	// ScoMateriais
	private Integer matCodigo;

	// ScoMateriais.nome
	private String matNome;

	// MbcProcedimentoCirurgicos
	private Integer pciSeq;

	// MbcProcedimentoCirurgicos.descricao
	private String descProcedCirurgico;

	// MpmProcedEspecialDiversos
	private Short pedSeq;

	// MpmProcedEspecialDiversos.descricao
	private String descProcedEspecial;

	public SumarioAltaPrescricaoProcedimentoVO(MpmAltaSumarioId id) {
		super(id);
	}

	public Integer getMatCodigo() {
		return this.matCodigo;
	}


	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}


	public String getMatNome() {
		return this.matNome;
	}


	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}


	public Integer getPciSeq() {
		return this.pciSeq;
	}


	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}


	public String getDescProcedCirurgico() {
		return this.descProcedCirurgico;
	}


	public void setDescProcedCirurgico(String descProcedCirurgico) {
		this.descProcedCirurgico = descProcedCirurgico;
	}


	public Short getPedSeq() {
		return this.pedSeq;
	}


	public void setPedSeq(Short pedSeq) {
		this.pedSeq = pedSeq;
	}


	public String getDescProcedEspecial() {
		return this.descProcedEspecial;
	}


	public void setDescProcedEspecial(String descProcedEspecial) {
		this.descProcedEspecial = descProcedEspecial;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SumarioAltaPrescricaoProcedimentoVO)) {
			return false;
		}

		SumarioAltaPrescricaoProcedimentoVO other = (SumarioAltaPrescricaoProcedimentoVO) obj;

		return !CoreUtil.modificados(this.getMatCodigo(),other.getMatCodigo())
		&& ((this.getMatNome() == null && other.getMatNome() == null) || (this.getMatNome() != null && this.getMatNome().equalsIgnoreCase(other.getMatNome())))
		&& !CoreUtil.modificados(this.getPciSeq(),other.getPciSeq())
		&& !CoreUtil.modificados(this.getPedSeq(),other.getPedSeq())
		&& ((this.getDescProcedCirurgico() == null && other.getDescProcedCirurgico() == null) || (this.getDescProcedCirurgico() != null && this.getDescProcedCirurgico().equalsIgnoreCase(other.getDescProcedCirurgico())))
		&& ((this.getDescProcedEspecial() == null && other.getDescProcedEspecial() == null) || (this.getDescProcedEspecial() != null && this.getDescProcedEspecial().equalsIgnoreCase(other.getDescProcedEspecial())));
	}

	@Override
	@SuppressWarnings("PMD.NPathComplexity")
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
		* result
		+ ((this.descProcedCirurgico == null) ? 0 : this.descProcedCirurgico
				.hashCode());
		result = prime
		* result
		+ ((this.descProcedEspecial == null) ? 0 : this.descProcedEspecial
				.hashCode());
		result = prime * result
		+ ((this.matCodigo == null) ? 0 : this.matCodigo.hashCode());
		result = prime * result + ((this.matNome == null) ? 0 : this.matNome.hashCode());
		result = prime * result + ((this.pciSeq == null) ? 0 : this.pciSeq.hashCode());
		result = prime * result + ((this.pedSeq == null) ? 0 : this.pedSeq.hashCode());
		return result;
	}

}
