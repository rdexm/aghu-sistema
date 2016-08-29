package br.gov.mec.aghu.paciente.vo;

import java.io.Serializable;

public class DescricaoCodigoComplementoCidVO implements Serializable {

	private static final long serialVersionUID = 8702837332939324328L;
	
	private Integer seqAtd;
	private Integer seqPte;
	private Integer pacCodigo;
	private String cidDescricao;
	private String cidCodigo;
	private String complementoCidTratTerapeutico;	
	private String cidDescricaoFormatado;
		
	public DescricaoCodigoComplementoCidVO() {
		super();
	}

	public DescricaoCodigoComplementoCidVO(Integer seqAtd, Integer seqPte,
			Integer pacCodigo, String cidDescricao, String cidCodigo,
			String complementoCidTratTerapeutico) {
		super();
		this.seqAtd = seqAtd;
		this.seqPte = seqPte;
		this.pacCodigo = pacCodigo;
		this.cidDescricao = cidDescricao;
		this.cidCodigo = cidCodigo;
		this.complementoCidTratTerapeutico = complementoCidTratTerapeutico;
	}
	
	public Integer getSeqAtd() {
		return seqAtd;
	}
	public void setSeqAtd(Integer seqAtd) {
		this.seqAtd = seqAtd;
	}
	public Integer getSeqPte() {
		return seqPte;
	}
	public void setSeqPte(Integer seqPte) {
		this.seqPte = seqPte;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public String getCidDescricao() {
		return cidDescricao;
	}
	public void setCidDescricao(String cidDescricao) {
		this.cidDescricao = cidDescricao;
	}	
	public String getCidCodigo() {
		return cidCodigo;
	}
	public void setCidCodigo(String cidCodigo) {
		this.cidCodigo = cidCodigo;
	}
	public String getComplementoCidTratTerapeutico() {
		return complementoCidTratTerapeutico;
	}
	public void setComplementoCidTratTerapeutico(
			String complementoCidTratTerapeutico) {
		this.complementoCidTratTerapeutico = complementoCidTratTerapeutico;
	}
	public String getCidDescricaoFormatado() {
		return cidDescricaoFormatado;
	}
	public void setCidDescricaoFormatado(String cidDescricaoFormatado) {
		this.cidDescricaoFormatado = cidDescricaoFormatado;
	}		
}
