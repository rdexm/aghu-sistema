package br.gov.mec.aghu.sig.custos.vo;

public class AtividadeAlocacaoExcedenteVO {
	
	private Integer capSeq;
	private Double qtdePrevistaAtividade;
	private Double vlrGrupoOcupacao;
	
	public AtividadeAlocacaoExcedenteVO() {
		
	}
	
	public AtividadeAlocacaoExcedenteVO(Object[] obj) {
		if (obj[0] != null) {
			this.setCapSeq((Integer) obj[0]);
		}
		
		if(obj[1] != null){
			this.setQtdePrevistaAtividade((Double) obj[1]);
		}
		
		if(obj[2] != null){
			this.setVlrGrupoOcupacao((Double) obj[2]);
		}
	}

	public Integer getCapSeq() {
		return capSeq;
	}
	
	public void setCapSeq(Integer capSeq) {
		this.capSeq = capSeq;
	}
	
	public Double getQtdePrevistaAtividade() {
		return qtdePrevistaAtividade;
	}
	
	public void setQtdePrevistaAtividade(Double qtdePrevistaAtividade) {
		this.qtdePrevistaAtividade = qtdePrevistaAtividade;
	}
	
	public Double getVlrGrupoOcupacao() {
		return vlrGrupoOcupacao;
	}

	public void setVlrGrupoOcupacao(Double vlrGrupoOcupacao) {
		this.vlrGrupoOcupacao = vlrGrupoOcupacao;
	} 
	
	
}
