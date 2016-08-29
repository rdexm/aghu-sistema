package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;
import java.util.List;

import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.hibernate.transform.ResultTransformer;

public class ItemPrescricaoNptVO implements ResultTransformer  {

	private static final long serialVersionUID = 5998689893113760845L;
	
	private Integer pnpSeq;
    private Integer cnpMedMatCodigo;
    private BigDecimal qtdePrescrita;
    private Integer phiSeq;
    private BigDecimal fatorConversaoUp;
    
    public ItemPrescricaoNptVO(){}

	public Integer getPnpSeq() {
		return pnpSeq;
	}

	public void setPnpSeq(Integer pnpSeq) {
		this.pnpSeq = pnpSeq;
	}

	public Integer getCnpMedMatCodigo() {
		return cnpMedMatCodigo;
	}

	public void setCnpMedMatCodigo(Integer cnpMedMatCodigo) {
		this.cnpMedMatCodigo = cnpMedMatCodigo;
	}

	public BigDecimal getQtdePrescrita() {
		return qtdePrescrita;
	}

	public void setQtdePrescrita(BigDecimal qtdePrescrita) {
		if(qtdePrescrita != null){
			this.qtdePrescrita = qtdePrescrita;
		} 
		else{
			this.qtdePrescrita = BigDecimal.ZERO; //Esse valor pode ser nulo já que a coluna permite isso, mas ele é utilizado em um cálculo
		}
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public BigDecimal getFatorConversaoUp() {
		return fatorConversaoUp;
	}

	public void setFatorConversaoUp(BigDecimal fatorConversaoUp) {
		this.fatorConversaoUp = fatorConversaoUp;
	}

	public enum Fields{
		
		PNP_SEQ("pnpSeq"),
	    CNP_MED_MAT_CODIGO("cnpMedMatCodigo"),
	    QTDE_PRESCRITA("qtdePrescrita"),
	    PHI_SEQ("phiSeq"),
	    FATOR_CONVERSAO("fatorConversaoUp");

	    private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		ItemPrescricaoNptVO vo = new ItemPrescricaoNptVO();
		
		if(tuple[0] != null){
			vo.setPnpSeq((Integer) tuple[0] );
		}
		if(tuple[1] != null){
			vo.setCnpMedMatCodigo((Integer) tuple[1]);
		}
		if(tuple[2] != null){
			vo.setQtdePrescrita((BigDecimal) tuple[2]);
		}
		if(tuple[3] != null){
			vo.setPhiSeq((Integer) tuple[3]);
		}
		if(tuple[4] != null){
			vo.setFatorConversaoUp((BigDecimal)tuple[4]);
		}
		return vo;
	}

	@Override
	public List transformList(List collection) {
		return collection;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if (!(obj instanceof ItemPrescricaoNptVO)) {
			return false;
		}
		
		ItemPrescricaoNptVO other = (ItemPrescricaoNptVO)obj;
		return     this.getCnpMedMatCodigo().equals(other.getCnpMedMatCodigo()) 
				&& this.getFatorConversaoUp().equals(other.getFatorConversaoUp())
				&& this.getPhiSeq().equals(other.getPhiSeq())
				&& this.getPnpSeq().equals(other.getPnpSeq())
				&& this.qtdePrescrita.equals(other.getQtdePrescrita())
				;
	}
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(pnpSeq).append(cnpMedMatCodigo).append(qtdePrescrita).append(phiSeq).append(fatorConversaoUp).toHashCode();
	}
}
