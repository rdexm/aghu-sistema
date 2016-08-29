package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class InsumosAtividadeRateioPesoQuantidadeVO {
	
	private Integer seqSigObjetoCustos;
	private Integer seqSigObjetoCustoVersoes;
	private Integer seqSigObjetoCustoComposicoes;
	private Integer seqSigAtividades;
	private Long seqSigAtividadeInsumos;
	private Integer seqSigCalculoAtividadeInsumo;
	private Integer seqSigCalculoComponente;
	private Integer seqSigCalculoObjetoCusto;
	private Integer cctCodigo;
	private Integer matCodigo;
	private Double qtdePrevista;
	private Double qtdeRealizada;
	private BigDecimal vlrInsumo;
	
	private BigDecimal peso;
	
	public InsumosAtividadeRateioPesoQuantidadeVO(){
		
	}
	
	public InsumosAtividadeRateioPesoQuantidadeVO(Object[] object) {
	
		if (object[0] != null) {
			this.setSeqSigObjetoCustos((Integer) object[0]);
		}
		if (object[1] != null) {
			this.setSeqSigObjetoCustoVersoes(((Integer) object[1]));
		}
		if (object[2] != null) {
			this.setSeqSigObjetoCustoComposicoes((Integer) object[2]);
		}
		if (object[3] != null) {
			this.setSeqSigAtividades((Integer) object[3]);
		}
		if (object[4] != null) {
			this.setSeqSigAtividadeInsumos((Long) object[4]);
		}
		if (object[5] != null) {
			this.setSeqSigCalculoAtividadeInsumo((Integer) object[5]);
		}
		if (object[6] != null) {
			this.setSeqSigCalculoComponente((Integer) object[6]);
		}
		if (object[7] != null) {
			this.setSeqSigCalculoObjetoCusto((Integer) object[7]);
		}
		if (object[8] != null) {
			this.setCctCodigo((Integer) object[8]);
		}
		if (object[9] != null) {
			this.setMatCodigo((Integer) object[9]);
		}
		if (object[10] != null) {
			this.setQtdePrevista((Double) object[10]);
		}
		if (object[11] != null) {
			this.setQtdeRealizada((Double) object[11]);
		}
		if (object[12] != null) {
			this.setVlrInsumo((BigDecimal) object[12]);
		}
		if (object[13] != null) {
			this.setPeso((BigDecimal) object[13]);
		}
	}
	
	public enum Fields {
		OBJ_SEQ("seqSigObjetoCustos"),
		OCV_SEQ("seqSigObjetoCustoVersoes"),
		CBT_SEQ("seqSigObjetoCustoComposicoes"),
		TVD_SEQ("seqSigAtividades"),
		AIS_SEQ("seqSigAtividadeInsumos"),
		CVN_SEQ("seqSigCalculoAtividadeInsumo"),
		CMT_SEQ("seqSigCalculoComponente"),
		CBJ_SEQ("seqSigCalculoObjetoCusto"),
		CCT_CODIGO("cctCodigo"),
		MAT_CODIGO("matCodigo"),
		QTDE_PREVISTA("qtdePrevista"),
		QTDE_REALIZADA("qtdeRealizada"),
		VLR_INSUMO("vlrInsumo"),
		PESO("peso");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Integer getSeqSigObjetoCustos() {
		return seqSigObjetoCustos;
	}

	public void setSeqSigObjetoCustos(Integer seqSigObjetoCustos) {
		this.seqSigObjetoCustos = seqSigObjetoCustos;
	}

	public Integer getSeqSigObjetoCustoVersoes() {
		return seqSigObjetoCustoVersoes;
	}

	public void setSeqSigObjetoCustoVersoes(Integer seqSigObjetoCustoVersoes) {
		this.seqSigObjetoCustoVersoes = seqSigObjetoCustoVersoes;
	}

	public Integer getSeqSigObjetoCustoComposicoes() {
		return seqSigObjetoCustoComposicoes;
	}

	public void setSeqSigObjetoCustoComposicoes(Integer seqSigObjetoCustoComposicoes) {
		this.seqSigObjetoCustoComposicoes = seqSigObjetoCustoComposicoes;
	}

	public Integer getSeqSigAtividades() {
		return seqSigAtividades;
	}

	public void setSeqSigAtividades(Integer seqSigAtividades) {
		this.seqSigAtividades = seqSigAtividades;
	}

	public Long getSeqSigAtividadeInsumos() {
		return seqSigAtividadeInsumos;
	}

	public void setSeqSigAtividadeInsumos(Long seqSigAtividadeInsumos) {
		this.seqSigAtividadeInsumos = seqSigAtividadeInsumos;
	}

	public Integer getSeqSigCalculoAtividadeInsumo() {
		return seqSigCalculoAtividadeInsumo;
	}

	public void setSeqSigCalculoAtividadeInsumo(Integer seqSigCalculoAtividadeInsumo) {
		this.seqSigCalculoAtividadeInsumo = seqSigCalculoAtividadeInsumo;
	}

	public Integer getSeqSigCalculoComponente() {
		return seqSigCalculoComponente;
	}

	public void setSeqSigCalculoComponente(Integer seqSigCalculoComponente) {
		this.seqSigCalculoComponente = seqSigCalculoComponente;
	}

	public Integer getSeqSigCalculoObjetoCusto() {
		return seqSigCalculoObjetoCusto;
	}

	public void setSeqSigCalculoObjetoCusto(Integer seqSigCalculoObjetoCusto) {
		this.seqSigCalculoObjetoCusto = seqSigCalculoObjetoCusto;
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

	public Double getQtdePrevista() {
		return qtdePrevista;
	}

	public void setQtdePrevista(Double qtdePrevista) {
		this.qtdePrevista = qtdePrevista;
	}

	public Double getQtdeRealizada() {
		return qtdeRealizada;
	}

	public void setQtdeRealizada(Double qtdeRealizada) {
		this.qtdeRealizada = qtdeRealizada;
	}

	public BigDecimal getVlrInsumo() {
		return vlrInsumo;
	}

	public void setVlrInsumo(BigDecimal vlrInsumo) {
		this.vlrInsumo = vlrInsumo;
	}

	public BigDecimal getPeso() {
		return peso;
	}

	public void setPeso(BigDecimal peso) {
		this.peso = peso;
	}


	

		
	
}
