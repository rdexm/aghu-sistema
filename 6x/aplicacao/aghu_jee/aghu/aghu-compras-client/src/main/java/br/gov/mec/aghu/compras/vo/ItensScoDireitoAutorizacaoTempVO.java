package br.gov.mec.aghu.compras.vo;



import br.gov.mec.aghu.model.ScoDireitoAutorizacaoTemp;



public class ItensScoDireitoAutorizacaoTempVO implements Cloneable {
	
	private ScoDireitoAutorizacaoTemp  direitoAutorizacaoTemp;
	
	
	public static enum Operacao {
		SAVE, ADD, EDIT, DELETE
	};
	
	private Operacao operacao;
	
	public ScoDireitoAutorizacaoTemp getDireitoAutorizacaoTemp() {
		return direitoAutorizacaoTemp;
	}
	public void setDireitoAutorizacaoTemp(
			ScoDireitoAutorizacaoTemp direitoAutorizacaoTemp) {
		this.direitoAutorizacaoTemp = direitoAutorizacaoTemp;
	}
				
	public Operacao getOperacao() {
		return operacao;
	}
	public void setOperacao(Operacao add) {
		this.operacao = add;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof ItensScoDireitoAutorizacaoTempVO)){
			return false;
		}
		ItensScoDireitoAutorizacaoTempVO castOther = (ItensScoDireitoAutorizacaoTempVO) other;
		
		if (this.getDireitoAutorizacaoTemp().getServidor() != null){
			return this.getDireitoAutorizacaoTemp().getScoPontoServidor().equals(castOther.getDireitoAutorizacaoTemp().getScoPontoServidor()) &&
					this.getDireitoAutorizacaoTemp().getServidor().equals(castOther.getDireitoAutorizacaoTemp().getServidor()) &&
					this.getDireitoAutorizacaoTemp().getCentroCusto().equals(castOther.getDireitoAutorizacaoTemp().getCentroCusto()) &&
					this.getDireitoAutorizacaoTemp().getDtInicio().equals(castOther.getDireitoAutorizacaoTemp().getDtInicio()) &&
					this.getDireitoAutorizacaoTemp().getDtFim().equals(castOther.getDireitoAutorizacaoTemp().getDtFim());
		}
		else {
			return this.getDireitoAutorizacaoTemp().getScoPontoServidor().equals(castOther.getDireitoAutorizacaoTemp().getScoPontoServidor()) &&					
					this.getDireitoAutorizacaoTemp().getCentroCusto().equals(castOther.getDireitoAutorizacaoTemp().getCentroCusto()) &&
					this.getDireitoAutorizacaoTemp().getDtInicio().equals(castOther.getDireitoAutorizacaoTemp().getDtInicio()) &&
					this.getDireitoAutorizacaoTemp().getDtFim().equals(castOther.getDireitoAutorizacaoTemp().getDtFim());		
		}
					
		
		
	}	
	
	 @Override  
	   public ItensScoDireitoAutorizacaoTempVO clone() throws CloneNotSupportedException {  
		 ItensScoDireitoAutorizacaoTempVO item = null;
		 
		
		item = (ItensScoDireitoAutorizacaoTempVO) super.clone();
		item.direitoAutorizacaoTemp = (ScoDireitoAutorizacaoTemp) this.direitoAutorizacaoTemp.clone();		    
		
		 return item;
	        
	   }  
	
		
}
