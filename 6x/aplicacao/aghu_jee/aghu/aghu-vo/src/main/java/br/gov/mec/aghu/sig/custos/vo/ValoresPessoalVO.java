package br.gov.mec.aghu.sig.custos.vo;

import java.math.BigDecimal;

public class ValoresPessoalVO implements java.io.Serializable{

	private static final long serialVersionUID = -8904560894506915087L;
	
	private Integer seqCentroCusto;
	private Integer seqGrupoOcupacao;
	private Long quantidadeHoras;
	private BigDecimal quantidadeFolhasPessoal;
	
	public ValoresPessoalVO(){
		
	}
	
	
	public ValoresPessoalVO(Object[] obj){
		if(obj[0] != null){
			this.setSeqCentroCusto((Integer)obj[0]);
		}
		if(obj[1] != null){
			this.setSeqGrupoOcupacao((Integer)obj[1]);
		}
		if(obj[2] != null){
			this.setQuantidadeHoras((Long)obj[2]);
		}
		
		if(obj[3] != null){
			this.setQuantidadeFolhasPessoal((BigDecimal)obj[3]);
		}
	}

	public Integer getSeqCentroCusto() {
		return seqCentroCusto;
	}

	public void setSeqCentroCusto(Integer seqCentroCusto) {
		this.seqCentroCusto = seqCentroCusto;
	}

	public Integer getSeqGrupoOcupacao() {
		return seqGrupoOcupacao;
	}
	
	public Integer getSeqGrupoOcupacaoNormalizado(){
		return seqGrupoOcupacao != null ? seqGrupoOcupacao : -1;
	}

	public void setSeqGrupoOcupacao(Integer seqGrupoOcupacao) {
		this.seqGrupoOcupacao = seqGrupoOcupacao;
	}

	public Long getQuantidadeHoras() {
		return quantidadeHoras;
	}

	public void setQuantidadeHoras(Long quantidadeHoras) {
		this.quantidadeHoras = quantidadeHoras;
	}

	public BigDecimal getQuantidadeFolhasPessoal() {
		return quantidadeFolhasPessoal;
	}

	public void setQuantidadeFolhasPessoal(BigDecimal quantidadeFolhasPessoal) {
		this.quantidadeFolhasPessoal = quantidadeFolhasPessoal;
	}
}
