package br.gov.mec.aghu.faturamento.vo;

import java.util.Date;

public class DoacaoColetaSangueVO {

	private Date bolData;
	private Long quantidade;
	private Integer serMatricula;
	private Integer serVinCodigo;
	private String indOrigem;
	private Integer pacCodigo;
	private Date dthrMovimento;
	
	public Date getBolData() {
		return bolData;
	}
	public void setBolData(Date bolData) {
		this.bolData = bolData;
	}
	public Long getQuantidade() {
		return quantidade;
	}
	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}
	public Integer getSerMatricula() {
		return serMatricula;
	}
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}
	public Integer getSerVinCodigo() {
		return serVinCodigo;
	}
	public void setSerVinCodigo(Integer serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public String getIndOrigem() {
		return indOrigem;
	}
	public void setIndOrigem(String indOrigem) {
		this.indOrigem = indOrigem;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public Date getDthrMovimento() {
		return dthrMovimento;
	}
	public void setDthrMovimento(Date dthrMovimento) {
		this.dthrMovimento = dthrMovimento;
	}
	
	
}
