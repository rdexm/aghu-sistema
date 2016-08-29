package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class EquipamentoAtividadeObjetoCustoVO {

	private Integer tvdSeq;
	private Integer aveSeq;
	private Integer cmtSeq;
	private Integer cctCodigo;
	private String codPatrimonio;
	private Integer dirSeqAtividade;
	private BigDecimal pesoOc;

	public EquipamentoAtividadeObjetoCustoVO() {
	}

	public EquipamentoAtividadeObjetoCustoVO(Object[] obj) {

		if (obj[0] != null) {
			this.setTvdSeq((Integer) obj[0]);
		}

		if (obj[1] != null) {
			this.setAveSeq((Integer) obj[1]);
		}

		if (obj[2] != null) {
			this.setCmtSeq((Integer) obj[2]);
		}

		if (obj[3] != null) {
			this.setCctCodigo((Integer) obj[3]);
		}

		if (obj[4] != null) {
			this.setCodPatrimonio((String) obj[4]);
		}

		if (obj[5] != null) {
			this.setDirSeqAtividade((Integer) obj[5]);
		}

		if (obj[6] != null) {
			this.setPesoOc((BigDecimal) obj[6]);
		}
	}

	public Integer getTvdSeq() {
		return tvdSeq;
	}

	public void setTvdSeq(Integer tvdSeq) {
		this.tvdSeq = tvdSeq;
	}

	public Integer getAveSeq() {
		return aveSeq;
	}

	public void setAveSeq(Integer aveSeq) {
		this.aveSeq = aveSeq;
	}

	public Integer getCmtSeq() {
		return cmtSeq;
	}

	public void setCmtSeq(Integer cmtSeq) {
		this.cmtSeq = cmtSeq;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public String getCodPatrimonio() {
		return codPatrimonio;
	}

	public void setCodPatrimonio(String codPatrimonio) {
		this.codPatrimonio = codPatrimonio;
	}

	public Integer getDirSeqAtividade() {
		return dirSeqAtividade;
	}

	public void setDirSeqAtividade(Integer dirSeqAtividade) {
		this.dirSeqAtividade = dirSeqAtividade;
	}

	public BigDecimal getPesoOc() {
		return pesoOc;
	}

	public void setPesoOc(BigDecimal pesoOc) {
		this.pesoOc = pesoOc;
	}
}
