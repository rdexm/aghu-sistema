package br.gov.mec.aghu.sig.custos.vo;

public class AtividadeValorRealizadoCalculadoVO {
	
	
	private Integer ocvSeq;
	private Integer cbjSeq;
	private Integer cmtSeq;
	private Integer cctCodigo;
	private Integer gocSeq;
	private Double qtdeRealizada;
	private Double vlrGrupoOcupacao;
	private Integer tvdSeq;
	private String nomeAtividade;
	private Short pgdSeq;

	public AtividadeValorRealizadoCalculadoVO() {
	}
	
	public AtividadeValorRealizadoCalculadoVO(Object[] obj) {
		
		if (obj[0] != null) {
			this.setOcvSeq((Integer) obj[0]);
		}
		if (obj[1] != null) {
			this.setCbjSeq((Integer) obj[1]);
		}
		if(obj[2] != null){
			this.setCmtSeq((Integer) obj[2]);
		}
		if(obj[3] != null) {
			this.setCctCodigo((Integer) obj[3]);
		}
		if(obj[4] != null){
		  	this.setGocSeq((Integer) obj[4]);
		}
		if(obj[5] != null){
			this.setQtdeRealizada((Double) obj[5]);
		}
		if(obj[6] != null){
			this.setVlrGrupoOcupacao((Double) obj[6]);
		}
		if(obj[7] != null){
			this.setTvdSeq((Integer) obj[7]);
		}
		if(obj[8] != null){
			this.setNomeAtividade((String) obj[8]);
		}
		if(obj[9] != null){
			this.setPgdSeq((Short) obj[9]);
		}
	}

	public Integer getOcvSeq() {
		return ocvSeq;
	}

	public void setOcvSeq(Integer ocvSeq) {
		this.ocvSeq = ocvSeq;
	}
	
	public Integer getCbjSeq() {
		return cbjSeq;
	}
	
	public void setCbjSeq(Integer cbjSeq) {
		this.cbjSeq = cbjSeq;
	}
	
	public Integer getCmtSeq() {
		return cmtSeq;
	}
	
	public void setCmtSeq(Integer cmtSeq) {
		this.cmtSeq = cmtSeq;
	}
	
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
	
	public Double getQtdeRealizada() {
		return qtdeRealizada;
	}
	
	public void setQtdeRealizada(Double qtdeRealizada) {
		this.qtdeRealizada = qtdeRealizada;
	}
	
	public Double getVlrGrupoOcupacao() {
		return vlrGrupoOcupacao;
	}
	
	public void setVlrGrupoOcupacao(Double vlrGrupoOcupacao) {
		this.vlrGrupoOcupacao = vlrGrupoOcupacao;
	}

	public String getNomeAtividade() {
		return nomeAtividade;
	}

	public void setNomeAtividade(String nomeAtividade) {
		this.nomeAtividade = nomeAtividade;
	}
	
	public Integer getTvdSeq() {
		return tvdSeq;
	}

	public void setTvdSeq(Integer tvdSeq) {
		this.tvdSeq = tvdSeq;
	}
	
	public Short getPgdSeq() {
		return pgdSeq;
	}

	public void setPgdSeq(Short pgdSeq) {
		this.pgdSeq = pgdSeq;
	}
}
