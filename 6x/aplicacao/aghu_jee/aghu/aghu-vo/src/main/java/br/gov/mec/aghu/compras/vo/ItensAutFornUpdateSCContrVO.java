package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioClassifABC;

/**
 * Os dados armazenados nesse objeto representam os Itens de uma Autorizacao de Fornecimento
 * 
 * @author flavio rutkowski
 */
public class ItensAutFornUpdateSCContrVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1094758748075863219L;

	
	private Integer slcNumeroSC;
	private Integer slcCodigoMaterial;
	private Integer slcCctCodigo;
	private DominioClassifABC egrClassificacaoAbc;
	private Short   estAlAlmoxSeq;
	private Integer   estAlAlmoxFornecedorNumero;
	private Short   matAlmoxSeq;
	private Integer estAlseq;
	private Integer matGrupoCodigo;
	
	
	// GETs and SETs	
	
	public Integer getSlcNumeroSC() {
		return slcNumeroSC;
	}
	public void setSlcNumeroSC(Integer slcNumeroSC) {
		this.slcNumeroSC = slcNumeroSC;
	}
	public Integer getSlcCodigoMaterial() {
		return slcCodigoMaterial;
	}
	public void setSlcCodigoMaterial(Integer slcCodigoMaterial) {
		this.slcCodigoMaterial = slcCodigoMaterial;
	}
	public Integer getSlcCctCodigo() {
		return slcCctCodigo;
	}
	public void setSlcCctCodigo(Integer slcCctCodigo) {
		this.slcCctCodigo = slcCctCodigo;
	}
	public DominioClassifABC getEgrClassificacaoAbc() {
		return egrClassificacaoAbc;
	}
	public void setEgrClassificacaoAbc(DominioClassifABC egrClassificacaoAbc) {
		this.egrClassificacaoAbc = egrClassificacaoAbc;
	}
	public Short getEstAlAlmoxSeq() {
		return estAlAlmoxSeq;
	}
	public void setEstAlAlmoxSeq(Short estAlAlmoxSeq) {		
		this.estAlAlmoxSeq = estAlAlmoxSeq;
	}
	public Integer getEstAlAlmoxFornecedorNumero() {
		return estAlAlmoxFornecedorNumero;
	}
	public void setEstAlAlmoxFornecedorNumero(Integer estAlAlmoxFornecedorNumero) {
		this.estAlAlmoxFornecedorNumero = estAlAlmoxFornecedorNumero;
	}
	
	public Short getMatAlmoxSeq() {
		return matAlmoxSeq;
	}
	public void setMatAlmoxSeq(Short matAlmoxSeq) {
		this.matAlmoxSeq = matAlmoxSeq;
	}

	public Integer getEstAlseq() {
		return estAlseq;
	}
	public void setEstAlseq(Integer estAlseq) {
		this.estAlseq = estAlseq;
	}

	public Integer getMatGrupoCodigo() {
		return matGrupoCodigo;
	}
	public void setMatGrupoCodigo(Integer matGrupoCodigo) {
		this.matGrupoCodigo = matGrupoCodigo;
	}

	public enum Fields {
		SLC_NUMERO_SC("slcNumeroSC"),
		SLC_CODIGO_MATERIAL("slcCodigoMaterial"),
		SLC_CCT_CODIGO("slcCctCodigo"),
		EGR_CLASSIF_ABC("egrClassificacaoAbc"),
		EST_AL_ALMOX_SEQ("estAlAlmoxSeq"),
		EST_AL_ALMOX_FORNECEDOR_NUMERO("estAlAlmoxFornecedorNumero"),
		EST_AL_SEQ("estAlseq"),
		MAT_ALMOX_SEQ("matAlmoxSeq"),
		MAT_GRUPO_CODIGO("matGrupoCodigo");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}
	
			
}
