package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoObjetoCusto;

public class InsumoAtividadeObjetoCustoVO {

	private Integer tvdSeq;
	private Integer aisSeq;
	private Integer cmtSeq;
	private Integer cctCodigo;
	private Integer matCodigo;
	private String unUso;
	private String unMaterial;
	private BigDecimal qtdeUso;
	private Integer vidaUtilQtde;
	private Integer vidaUtilTempo;
	private Integer dirSeqTempo;
	private Integer dirSeqAtividade;
	private Integer nroExecucoes;
	private Date procDataInicio;
	private Date procDataFim;
	private Integer sumDhpNroDiasProducao;
	private BigDecimal sumDhpQtde;

	public InsumoAtividadeObjetoCustoVO() {

	}

	public InsumoAtividadeObjetoCustoVO(Object[] obj, DominioTipoObjetoCusto tipoObjetoCusto) {

		if (obj[0] != null) {
			this.setTvdSeq((Integer) obj[0]);
		}
		if (obj[1] != null) {
			this.setAisSeq(((Long) obj[1]).intValue());
			//this.setAisSeq((Integer) (obj[1]));
		}
		if (obj[2] != null) {
			this.setCmtSeq((Integer) obj[2]);
		}
		if (obj[3] != null) {
			this.setCctCodigo((Integer) obj[3]);
		}
		if (obj[4] != null) {
			this.setMatCodigo((Integer) obj[4]);
		}
		if (obj[5] != null) {
			this.setUnUso((String) obj[5]);
		}
		if (obj[6] != null) {
			this.setUnMaterial((String) obj[6]);
		}
		if (obj[7] != null) {
			this.setQtdeUso((BigDecimal) obj[7]);
		}
		if (obj[8] != null) {
			this.setVidaUtilQtde((Integer) obj[8]);
		}
		if (obj[9] != null) {
			this.setVidaUtilTempo((Integer) obj[9]);
		}

		if (obj[10] != null) {
			this.setDirSeqTempo((Integer) obj[10]);
		}

		if (tipoObjetoCusto.equals(DominioTipoObjetoCusto.AS)) {

			if (obj[11] != null) {
				this.setDirSeqAtividade((Integer) obj[11]);
			}
			if (obj[12] != null) {
				this.setNroExecucoes((Integer) obj[12]);
			}
			if (obj[13] != null) {
				this.setProcDataInicio((Date) obj[13]);
			}
			if (obj[14] != null) {
				this.setProcDataFim((Date) obj[14]);
			}
			if (obj[15] != null) {
				this.setSumDhpNroDiasProducao(((Long) obj[15]).intValue());
			}
			if (obj[16] != null) {
				this.setSumDhpQtde((BigDecimal) obj[16]);
			}

		} else if (tipoObjetoCusto.equals(DominioTipoObjetoCusto.AP)) {

			if (obj[11] != null) {
				this.setNroExecucoes((Integer) obj[11]);
			}
			if (obj[12] != null) {
				this.setProcDataInicio((Date) obj[12]);
			}
			if (obj[13] != null) {
				this.setProcDataFim((Date) obj[13]);
			}
		}
	}

	public Integer getTvdSeq() {
		return tvdSeq;
	}

	public void setTvdSeq(Integer tvdSeq) {
		this.tvdSeq = tvdSeq;
	}

	public Integer getAisSeq() {
		return aisSeq;
	}

	public void setAisSeq(Integer aisSeq) {
		this.aisSeq = aisSeq;
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

	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public String getUnUso() {
		return unUso;
	}

	public void setUnUso(String unUso) {
		this.unUso = unUso;
	}

	public String getUnMaterial() {
		return unMaterial;
	}

	public void setUnMaterial(String unMaterial) {
		this.unMaterial = unMaterial;
	}

	public BigDecimal getQtdeUso() {
		return qtdeUso;
	}

	public void setQtdeUso(BigDecimal qtdeUso) {
		this.qtdeUso = qtdeUso;
	}

	public Integer getVidaUtilQtde() {
		return vidaUtilQtde;
	}

	public void setVidaUtilQtde(Integer vidaUtilQtde) {
		this.vidaUtilQtde = vidaUtilQtde;
	}

	public Integer getVidaUtilTempo() {
		return vidaUtilTempo;
	}

	public void setVidaUtilTempo(Integer vidaUtilTempo) {
		this.vidaUtilTempo = vidaUtilTempo;
	}

	public Integer getDirSeqTempo() {
		return dirSeqTempo;
	}

	public void setDirSeqTempo(Integer dirSeqTempo) {
		this.dirSeqTempo = dirSeqTempo;
	}

	public Integer getDirSeqAtividade() {
		return dirSeqAtividade;
	}

	public void setDirSeqAtividade(Integer dirSeqAtividade) {
		this.dirSeqAtividade = dirSeqAtividade;
	}

	public Integer getNroExecucoes() {
		return nroExecucoes;
	}

	public void setNroExecucoes(Integer nroExecucoes) {
		this.nroExecucoes = nroExecucoes;
	}

	public Date getProcDataInicio() {
		return procDataInicio;
	}

	public void setProcDataInicio(Date procDataInicio) {
		this.procDataInicio = procDataInicio;
	}

	public Date getProcDataFim() {
		return procDataFim;
	}

	public void setProcDataFim(Date procDataFim) {
		this.procDataFim = procDataFim;
	}

	public BigDecimal getSumDhpQtde() {
		return sumDhpQtde;
	}

	public void setSumDhpQtde(BigDecimal sumDhpQtde) {
		this.sumDhpQtde = sumDhpQtde;
	}

	public Integer getSumDhpNroDiasProducao() {
		return sumDhpNroDiasProducao;
	}

	public void setSumDhpNroDiasProducao(Integer sumDhpNroDiasProducao) {
		this.sumDhpNroDiasProducao = sumDhpNroDiasProducao;
	}

}
