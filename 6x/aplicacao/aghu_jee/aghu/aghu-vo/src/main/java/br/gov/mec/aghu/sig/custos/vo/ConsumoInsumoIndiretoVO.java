package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ConsumoInsumoIndiretoVO implements java.io.Serializable{

	private static final long serialVersionUID = -4874639802322882087L;
	
	private Integer cctCodigo;
	private Integer gmtCodigo;
	private Long qtdeConsumoInsumo;
	private BigDecimal valorConsumoInsumo;
	
	public ConsumoInsumoIndiretoVO(){
		
	}

	public ConsumoInsumoIndiretoVO(Object[] obj){
		
		if(obj[0] != null){
			this.setCctCodigo((Integer)obj[0]);
		}
		if(obj[1] != null){
			this.setGmtCodigo((Integer)obj[1]);
		}
		if(obj[2] != null){
			this.setQtdeConsumoInsumo((Long)obj[2]);
		}
		if(obj[3] != null){
			this.setValorConsumoInsumo((BigDecimal)obj[3]);
		}
	}
	
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	public Integer getGmtCodigo() {
		return gmtCodigo;
	}
	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}
	public Long getQtdeConsumoInsumo() {
		return qtdeConsumoInsumo;
	}
	public void setQtdeConsumoInsumo(Long qtdeConsumoInsumo) {
		this.qtdeConsumoInsumo = qtdeConsumoInsumo;
	}
	public BigDecimal getValorConsumoInsumo() {
		return valorConsumoInsumo;
	}
	public void setValorConsumoInsumo(BigDecimal valorConsumoInsumo) {
		this.valorConsumoInsumo = valorConsumoInsumo;
	}
	
	

}
