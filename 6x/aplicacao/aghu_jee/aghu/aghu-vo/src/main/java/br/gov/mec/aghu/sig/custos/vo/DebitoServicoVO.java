package br.gov.mec.aghu.sig.custos.vo;


public class DebitoServicoVO {
	
	private Integer cctCodigo;
	private Integer cctCodigoAplic;
	private Integer slsNumero;
	private Integer srvCodigo;
	private Integer iafAfnNumero;
	private Integer iafNumero;
	private Double valor;
	
	
	public DebitoServicoVO() {
		
	}
	
	public DebitoServicoVO(Object[] object) {
		
		if(object[0] != null){
			setCctCodigo((Integer) object[0]);
		}
		
		if(object[1] != null){
			setCctCodigoAplic((Integer) object[1]);
		}
		
		if(object[2] != null){
			setSlsNumero((Integer) object[2]);
		}
		
		if(object[3] != null){
			setSrvCodigo((Integer) object[3]);
		}
		
		if(object[4] != null){
			setIafAfnNumero((Integer) object[4]);
		}
		
		//Este campo, no momento, é desnecessário para a contabilização dos contratos, mas será mantido já prevendo futuras alterações
		//para incluir os contratos manuais (que não estão sendo contabilizados ainda)
		if(object[5] != null){
			setIafNumero((Integer) object[5]);
		}
		
		if(object[6] != null){
			setValor(((Double) object[6]).doubleValue());
		}
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Integer getCctCodigoAplic() {
		return cctCodigoAplic;
	}

	public void setCctCodigoAplic(Integer cctCodigoAplic) {
		this.cctCodigoAplic = cctCodigoAplic;
	}

	public Integer getSlsNumero() {
		return slsNumero;
	}

	public void setSlsNumero(Integer slsNumero) {
		this.slsNumero = slsNumero;
	}

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	private void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Double getValor() {
		return valor;
	}

	public void setValor(Double valor) {
		this.valor = valor;
	}

	public Integer getSrvCodigo() {
		return srvCodigo;
	}

	public void setSrvCodigo(Integer srvCodigo) {
		this.srvCodigo = srvCodigo;
	}
	
	
	
	
}
