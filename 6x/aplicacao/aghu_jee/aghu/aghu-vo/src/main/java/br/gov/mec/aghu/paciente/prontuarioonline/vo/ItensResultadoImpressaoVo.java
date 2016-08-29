package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.io.Serializable;

public class ItensResultadoImpressaoVo implements Serializable  {

	
	private static final long serialVersionUID = -7000246033281145391L;
	Short seq;
	Integer iseSoeSeq;
	Short iseSeq;
	Integer nroVias;
	Integer ordemImpressao;
	
	public ItensResultadoImpressaoVo() {
	}
	
	public ItensResultadoImpressaoVo(Short seq, Integer iseSoeSeq, Short iseSeq,
			Integer nroVias, Integer ordemImpressao) {
		super();
		this.seq = seq;
		this.iseSoeSeq = iseSoeSeq;
		this.iseSeq = iseSeq;
		this.nroVias = nroVias;
		this.ordemImpressao = ordemImpressao;
	}
	
	public enum Fields {
		
		SEQ("seq"),
		ISE_SOE_SEQ("iseSoeSeq"),
		ISE_SEQ("iseSeq"),
		NRO_VIAS("nroVias"),
		ORDEM_IMPRESSAO("ordemImpressao");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	public Short getSeq() {
		return seq;
	}
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	public Integer getIseSoeSeq() {
		return iseSoeSeq;
	}
	public void setIseSoeSeq(Integer iseSoeSeq) {
		this.iseSoeSeq = iseSoeSeq;
	}
	public Short getIseSeq() {
		return iseSeq;
	}
	public void setIseSeq(Short iseSeq) {
		this.iseSeq = iseSeq;
	}
	public Integer getNroVias() {
		return nroVias;
	}
	public void setNroVias(Integer nroVias) {
		this.nroVias = nroVias;
	}
	public Integer getOrdemImpressao() {
		return ordemImpressao;
	}
	public void setOrdemImpressao(Integer ordemImpressao) {
		this.ordemImpressao = ordemImpressao;
	}
	
	

}
