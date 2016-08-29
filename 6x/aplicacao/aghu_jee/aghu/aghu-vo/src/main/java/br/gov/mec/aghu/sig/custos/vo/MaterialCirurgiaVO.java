package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.math.BigInteger;


public class MaterialCirurgiaVO implements java.io.Serializable {
	
	private static final long serialVersionUID = 3844840805812023432L;
	private Integer matCodigo;
	private Integer phi;
	private Double soma;
	private BigDecimal fatorConversao;
	
	public MaterialCirurgiaVO(){
		
	}
	
	public MaterialCirurgiaVO(Object[] material){
		if(material[0] != null ){
			Object o = material[0];
			if(o instanceof Integer){
				
				this.setMatCodigo((Integer) o);
			}if(o instanceof BigInteger){
				this.setMatCodigo(((BigInteger) o).intValue());
			}
		}
		if(material[1] != null ){
			Object o = material[1];
			if(o instanceof Integer){
				this.setPhi((Integer) o);
			}if(o instanceof BigInteger){
				this.setPhi(((BigInteger) o).intValue());
			}
		}
		
		if(material[2] != null ){
			Object o = material[2];
			if(o instanceof Double){
				this.setSoma((Double) o);
			}
		}
		if(material[3] != null ){
			Object o = material[3];
			if(o instanceof BigDecimal){
				this.setFatorConversao((BigDecimal) o);
			}
		}
	}
	
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	
	public Double getSoma() {
		if(this.getFatorConversao() != null ){
			Double valor = soma / this.getFatorConversao().doubleValue();
			return valor;
		}		
		return soma;
	}
	public void setSoma(Double soma) {
		this.soma = soma;
	}
	public Integer getPhi() {
		return phi;
	}

	public void setPhi(Integer phi) {
		this.phi = phi;
	}
	
	public BigDecimal getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(BigDecimal fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public enum Fields {
		MATERIAL_CODIGO("matCodigo"),
		PHI("phi"),
		FATOR_CONVERSAO("fatorConversao"),
		SOMATORIO("soma");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
}
