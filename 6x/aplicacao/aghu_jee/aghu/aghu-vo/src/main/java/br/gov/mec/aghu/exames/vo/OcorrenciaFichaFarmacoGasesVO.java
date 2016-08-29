package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoOcorrenciaFichaFarmaco;

public class OcorrenciaFichaFarmacoGasesVO {

	private Date dthrOcorrencia;
	private Integer seqOcorrencia;
	private Integer ffaSeq;
	private DominioTipoOcorrenciaFichaFarmaco tipoOcorrencia;
	private Float fluxo;

	public Date getDthrOcorrencia() {
		return dthrOcorrencia;
	}

	public void setDthrOcorrencia(Date dthrOcorrencia) {
		this.dthrOcorrencia = dthrOcorrencia;
	}

	public Integer getSeqOcorrencia() {
		return seqOcorrencia;
	}

	public void setSeqOcorrencia(Integer seqOcorrencia) {
		this.seqOcorrencia = seqOcorrencia;
	}

	public Integer getFfaSeq() {
		return ffaSeq;
	}

	public void setFfaSeq(Integer ffaSeq) {
		this.ffaSeq = ffaSeq;
	}

	public DominioTipoOcorrenciaFichaFarmaco getTipoOcorrencia() {
		return tipoOcorrencia;
	}

	public void setTipoOcorrencia(
			DominioTipoOcorrenciaFichaFarmaco tipoOcorrencia) {
		this.tipoOcorrencia = tipoOcorrencia;
	}

	public Float getFluxo() {
		return fluxo;
	}

	public void setFluxo(Float fluxo) {
		this.fluxo = fluxo;
	}

}
