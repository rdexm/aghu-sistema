package br.gov.mec.aghu.prescricaomedica.vo;

import java.util.Date;

import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoriaId;

public class SumarioAltaProcedimentosConsultoriasVO extends SumarioAltaProcedimentosVO {

	

	/**
	 * 
	 */
	private static final long serialVersionUID = -4688560469823219610L;
	private MpmSolicitacaoConsultoriaId solicitacaoConsultoriaId;
	private Date dthrResposta;
	private Short seqEspecialidade;
	private String nomeEspecialidade;

	public SumarioAltaProcedimentosConsultoriasVO(MpmAltaSumarioId id) {
		super(id);
	}

	/**
	 * As comparações abaixo simulam a eliminação de registros duplicados,
	 * encontrada no arquivo MPMF_SUMARIO_ALTA.pll, na procedure MPMP_LISTA_CONSULTORIA,
	 * cursor cur_acn, feita através do operador minus da consulta SQL.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SumarioAltaProcedimentosConsultoriasVO)) {
			return false;
		}

		SumarioAltaProcedimentosConsultoriasVO other = (SumarioAltaProcedimentosConsultoriasVO) obj;

		return ((this.getSolicitacaoConsultoriaId() != null) && this.getSolicitacaoConsultoriaId().equals(other.getSolicitacaoConsultoriaId()))
					&& (((this.getDthrResposta() != null) && this.getDthrResposta().equals(other.getDthrResposta())) ||
							((this.getData() != null) && this.getData().equals(other.getData())))
					&& ((this.getSeqEspecialidade() != null) && this.getSeqEspecialidade().equals(other.getSeqEspecialidade()))
					&& ((this.getNomeEspecialidade() != null) && this.getNomeEspecialidade().equalsIgnoreCase(other.getNomeEspecialidade()));
	}

	@Override
	public int hashCode() {
		return (this.getSolicitacaoConsultoriaId() == null ? 0 : this.getSolicitacaoConsultoriaId().hashCode())
				+ (this.getDthrResposta() == null ? 0 : this.getDthrResposta().hashCode())
				+ (this.getSeqEspecialidade() == null ? 0 : this.getSeqEspecialidade().hashCode())
				+ (this.getNomeEspecialidade() == null ? 0 : this.getNomeEspecialidade().hashCode());
	}

	public void setSolicitacaoConsultoriaId(MpmSolicitacaoConsultoriaId solicitacaoConsultoriaId) {
		this.solicitacaoConsultoriaId = solicitacaoConsultoriaId;
	}

	public MpmSolicitacaoConsultoriaId getSolicitacaoConsultoriaId() {
		return this.solicitacaoConsultoriaId;
	}

	public Date getDthrResposta() {
		return this.dthrResposta;
	}

	public void setDthrResposta(Date dthrResposta) {
		this.dthrResposta = dthrResposta;
	}

	public Short getSeqEspecialidade() {
		return this.seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}

	public String getNomeEspecialidade() {
		return this.nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}


}