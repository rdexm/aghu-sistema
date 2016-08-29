package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class InsumosAtividadeRateioPesoVO {
	
	private Integer seqSigAtividade;
	private Integer seqSigAtividadeInsumo;
	private Integer seqSigCalculoComponente;
	private Integer seqFccCentroCusto;
	private Integer seqScoMaterial;
	private Integer seqSigDirecionador;
	private BigDecimal peso;
	private Integer seqSigCalculoObjetoCusto;
	
	public InsumosAtividadeRateioPesoVO(){
		
	}
	
	public InsumosAtividadeRateioPesoVO(Object[] object) {
	
		if (object[0] != null) {
			this.setSeqSigAtividade((Integer) object[0]);
		}
		if (object[1] != null) {
			this.setSeqSigAtividadeInsumo(((Long) object[1]).intValue());
		}
		if (object[2] != null) {
			this.setSeqSigCalculoComponente((Integer) object[2]);
		}
		if (object[3] != null) {
			this.setSeqFccCentroCusto((Integer) object[3]);
		}
		if (object[4] != null) {
			this.setSeqScoMaterial((Integer) object[4]);
		}
		if (object[5] != null) {
			this.setSeqSigDirecionador((Integer) object[5]);
		}
		if (object[6] != null) {
			this.setPeso((BigDecimal) object[6]);
		}
	}

	public Integer getSeqSigAtividade() {
		return seqSigAtividade;
	}

	public void setSeqSigAtividade(Integer seqSigAtividade) {
		this.seqSigAtividade = seqSigAtividade;
	}

	public Integer getSeqSigAtividadeInsumo() {
		return seqSigAtividadeInsumo;
	}

	public void setSeqSigAtividadeInsumo(Integer seqSigAtividadeInsumo) {
		this.seqSigAtividadeInsumo = seqSigAtividadeInsumo;
	}

	public Integer getSeqSigCalculoComponente() {
		return seqSigCalculoComponente;
	}

	public void setSeqSigCalculoComponente(Integer seqSigCalculoComponente) {
		this.seqSigCalculoComponente = seqSigCalculoComponente;
	}

	public Integer getSeqFccCentroCusto() {
		return seqFccCentroCusto;
	}

	public void setSeqFccCentroCusto(Integer seqFccCentroCusto) {
		this.seqFccCentroCusto = seqFccCentroCusto;
	}

	public Integer getSeqScoMaterial() {
		return seqScoMaterial;
	}

	public void setSeqScoMaterial(Integer seqScoMaterial) {
		this.seqScoMaterial = seqScoMaterial;
	}

	public Integer getSeqSigDirecionador() {
		return seqSigDirecionador;
	}

	public void setSeqSigDirecionador(Integer seqSigDirecionador) {
		this.seqSigDirecionador = seqSigDirecionador;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}

	public Integer getSeqSigCalculoObjetoCusto() {
		return seqSigCalculoObjetoCusto;
	}

	public void setSeqSigCalculoObjetoCusto(Integer seqSigCalculoObjetoCusto) {
		this.seqSigCalculoObjetoCusto = seqSigCalculoObjetoCusto;
	}
	
	
}
