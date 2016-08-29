package br.gov.mec.aghu.sig.custos.vo;

public class GrupoOcupacaoAlocacaoExcedenteVO {
	
	private Integer cctCodigo;
	private Integer gocSeq; 
	private Long qtdeDisponivel;
	private Double qtdePrevista;
	
	
	public GrupoOcupacaoAlocacaoExcedenteVO(Object[] obj) {
		if (obj[0] != null) {
			this.setCctCodigo((Integer) obj[0]);
		}
		
		if (obj[1] != null) {
			this.setGocSeq((Integer) obj[1]);
		}
		
		if (obj[2] != null) {
			this.setQtdeDisponivel((Long) obj[2]);
		}
		
		if (obj[3] != null) {
			this.setQtdePrevista((Double) obj[3]);
		}
	}
	
	public GrupoOcupacaoAlocacaoExcedenteVO(){}
	
	public Integer getCctCodigo() {
		return cctCodigo;
	}
	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}
	public Integer getGocSeq() {
		return gocSeq;
	}
	public void setGocSeq(Integer gocSeq) {
		this.gocSeq = gocSeq;
	}
	public Long getQtdeDisponivel() {
		return qtdeDisponivel;
	}
	public void setQtdeDisponivel(Long qtdeDisponivel) {
		this.qtdeDisponivel = qtdeDisponivel;
	}
	public Double getQtdePrevista() {
		return qtdePrevista;
	}
	public void setQtdePrevista(Double qtdePrevista) {
		this.qtdePrevista = qtdePrevista;
	}
	
	

}
