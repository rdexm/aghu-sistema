package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.FccCentroCustos;

public class ObjetoCustoProducaoExamesVO implements java.io.Serializable {

	private static final long serialVersionUID = -5485160171797479633L;
	private Integer seq;
	private Integer dhpSeq;
	private FccCentroCustos fccCentroCustos;
	private BigDecimal qtde;
	private Long qtdeInteira;
	private AacPagador pagador;

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public FccCentroCustos getFccCentroCustos() {
		return fccCentroCustos;
	}

	public void setFccCentroCustos(FccCentroCustos fccCentroCustos) {
		this.fccCentroCustos = fccCentroCustos;
	}

	public BigDecimal getQtde() {
		return qtde;
	}

	public void setQtde(BigDecimal qtde) {
		this.qtde = qtde;
	}
	
	public Long getQtdeInteira() {
		return qtdeInteira;
	}

	public void setQtdeInteira(Long qtdeInteira) {
		this.qtdeInteira = qtdeInteira;
	}


	public Integer getDhpSeq() {
		return dhpSeq;
	}

	public void setDhpSeq(Integer dhpSeq) {
		this.dhpSeq = dhpSeq;
	}

	public AacPagador getPagador() {
		return pagador;
	}

	public void setPagador(AacPagador pagador) {
		this.pagador = pagador;
	}

	public enum Fields {

		SEQ("seq"), 
		CENTRO_CUSTO("fccCentroCustos"), 
		QTDE("qtde"), 
		QTDE_INTEIRA("qtdeInteira"),
		DHP_SEQ("dhpSeq"),
		PAGADOR("pagador");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
