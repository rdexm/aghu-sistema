package br.gov.mec.aghu.estoque.vo;

import java.util.Date;

public class DocumentoValidadeVO {
	private Integer documentoEalSeq;
	private Date documentoData;
	private String loteCodigo;
	private Integer loteMaterialCodigo;
	private Integer loteMarcaComercialCodigo;
	private Integer loteFornecedor;
	private Date loteDataValidade;
	
	
	public DocumentoValidadeVO(Integer documentoEalSeq, Date documentoData, String loteCodigo,
			Integer loteMaterialCodigo, Integer loteMarcaComercialCodigo, Integer loteFornecedor,
			Date loteDataValidade) {
		this.documentoEalSeq = documentoEalSeq;
		this.documentoData = documentoData;
		this.loteCodigo = loteCodigo;
		this.loteMaterialCodigo = loteMaterialCodigo;
		this.loteMarcaComercialCodigo = loteMarcaComercialCodigo;
		this.loteFornecedor = loteFornecedor;
		this.loteDataValidade = loteDataValidade;
	}
	
	
	
	public Integer getDocumentoEalSeq() {
		return documentoEalSeq;
	}
	public void setDocumentoEalSeq(Integer documentoEalSeq) {
		this.documentoEalSeq = documentoEalSeq;
	}
	public Date getDocumentoData() {
		return documentoData;
	}
	public void setDocumentoData(Date documentoData) {
		this.documentoData = documentoData;
	}
	public String getLoteCodigo() {
		return loteCodigo;
	}
	public void setLoteCodigo(String loteCodigo) {
		this.loteCodigo = loteCodigo;
	}
	public Integer getLoteMaterialCodigo() {
		return loteMaterialCodigo;
	}
	public void setLoteMaterialCodigo(Integer loteMaterialCodigo) {
		this.loteMaterialCodigo = loteMaterialCodigo;
	}
	public Integer getLoteMarcaComercialCodigo() {
		return loteMarcaComercialCodigo;
	}
	public void setLoteMarcaComercialCodigo(Integer loteMarcaComercialCodigo) {
		this.loteMarcaComercialCodigo = loteMarcaComercialCodigo;
	}
	public Integer getLoteFornecedor() {
		return loteFornecedor;
	}
	public void setLoteFornecedor(Integer loteFornecedor) {
		this.loteFornecedor = loteFornecedor;
	}
	public Date getLoteDataValidade() {
		return loteDataValidade;
	}
	public void setLoteDataValidade(Date loteDataValidade) {
		this.loteDataValidade = loteDataValidade;
	}
}
