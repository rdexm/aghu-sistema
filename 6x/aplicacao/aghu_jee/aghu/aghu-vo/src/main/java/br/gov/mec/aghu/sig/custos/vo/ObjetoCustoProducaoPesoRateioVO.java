package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ObjetoCustoProducaoPesoRateioVO implements java.io.Serializable{

	private static final long serialVersionUID = -546345546645315087L;
	
	private Integer seqCalculoObjetoCusto;
	private BigDecimal pesoAcumulado;
	private BigDecimal pesoObjetoCusto;
	
	public ObjetoCustoProducaoPesoRateioVO(){
		
	}
	
	
	public ObjetoCustoProducaoPesoRateioVO(Object[] obj){
		
		if(obj[0] != null){
			this.setSeqCalculoObjetoCusto(((Integer)obj[0]));
		}
		if(obj[1] != null){
			this.setPesoAcumulado((BigDecimal)obj[1]);
		}
		if(obj[2] != null){
			this.setPesoObjetoCusto((BigDecimal)obj[2]);
		}
	}
	

	public Integer getSeqCalculoObjetoCusto() {
		return seqCalculoObjetoCusto;
	}

	public void setSeqCalculoObjetoCusto(Integer seqCalculoObjetoCusto) {
		this.seqCalculoObjetoCusto = seqCalculoObjetoCusto;
	}

	public BigDecimal getPesoAcumulado() {
		return pesoAcumulado;
	}

	public void setPesoAcumulado(BigDecimal pesoAcumulado) {
		this.pesoAcumulado = pesoAcumulado;
	}

	public BigDecimal getPesoObjetoCusto() {
		return pesoObjetoCusto;
	}

	public void setPesoObjetoCusto(BigDecimal pesoObjetoCusto) {
		this.pesoObjetoCusto = pesoObjetoCusto;
	}
}
