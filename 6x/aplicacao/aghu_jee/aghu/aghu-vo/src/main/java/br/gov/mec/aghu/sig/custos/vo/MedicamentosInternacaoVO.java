package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.Date;

public class MedicamentosInternacaoVO {

	private Integer phiSeq;
	private String vadSigla;
	private Date criadoEm;
	private Short tfdSeq;
	private Short frequecia;
	private Date dataHoraInicio;
	private Date dataHoraFim;
	private BigDecimal qtdeSolicitada;
	private BigDecimal qtdeDispensada;

	public static MedicamentosInternacaoVO create(Object[] object) {
		MedicamentosInternacaoVO vo = new MedicamentosInternacaoVO();

		if (object[0] != null) {
			vo.setPhiSeq(Integer.parseInt(object[0].toString()));
		}

		if (object[1] != null) {
			vo.setVadSigla(object[1].toString());
		}

		if (object[2] != null) {
			vo.setCriadoEm((Date) object[2]);
		}

		if (object[3] != null) {
			vo.setTfdSeq(Short.parseShort(object[3].toString()));
		}

		if (object[4] != null) {
			vo.setFrequecia(Short.parseShort(object[4].toString()));
		}

		if (object[5] != null) {
			vo.setDataHoraInicio((Date)object[5]);
		}
		
		if (object[6] != null) {
			vo.setDataHoraFim(((Date)object[6]));
		}
		
		if (object[7] != null) {
			vo.setQtdeSolicitada(new BigDecimal(object[7].toString()));
		}

		if (object[8] != null) {
			vo.setQtdeDispensada(new BigDecimal(object[8].toString()));
		}
		return vo;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public String getVadSigla() {
		return vadSigla;
	}

	public void setVadSigla(String vadSigla) {
		this.vadSigla = vadSigla;
	}

	public Date getCriadoEm() {
		return criadoEm;
	}

	public void setCriadoEm(Date criadoEm) {
		this.criadoEm = criadoEm;
	}

	public Short getTfdSeq() {
		return tfdSeq;
	}

	public void setTfdSeq(Short tfdSeq) {
		this.tfdSeq = tfdSeq;
	}

	public Short getFrequecia() {
		return frequecia;
	}

	public void setFrequecia(Short frequecia) {
		this.frequecia = frequecia;
	}


	public BigDecimal getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(BigDecimal qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public BigDecimal getQtdeDispensada() {
		return qtdeDispensada;
	}

	public void setQtdeDispensada(BigDecimal qtdeDispensada) {
		this.qtdeDispensada = qtdeDispensada;
	}

	public Date getDataHoraInicio() {
		return dataHoraInicio;
	}

	public void setDataHoraInicio(Date dataHoraInicio) {
		this.dataHoraInicio = dataHoraInicio;
	}

	public Date getDataHoraFim() {
		return dataHoraFim;
	}

	public void setDataHoraFim(Date dataHoraFim) {
		this.dataHoraFim = dataHoraFim;
	}

}
