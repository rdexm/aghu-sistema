/**
 * 
 */
package br.gov.mec.aghu.suprimentos.vo;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * @author bruno.mourao
 *
 */

public class RelUltimasComprasPACVO implements Serializable, Comparable<RelUltimasComprasPACVO> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1967919648813096511L;
	public enum Fields{
		DT_ABERTURA_PROPOSTA("dtAberturaProposta"),
		DT_GERACAO("dtGeracao"),
		FORMA_PAGTO("formaPag"),
		NRO_COMPLEMENTO("nroComplemento"),
		NRO_NF("nroNf"),
		NRO_SEQ("nrsSeq"),
		NRO_LICITACAO("nroLicit"),
		QUANTIDADE("quantidade"),
		VALOR("valor"),
		NRO_SOLICITACAO("nroSolicitacao"),
		NRO_ITEM("nroItem"),
		DESC_FORNECEDOR("descFornecedor"),
		COD_MATERIAL("codMaterial"),
		DESC_MATERIAL("descMaterial"),
		DESC_UNIDADE("descUnidade"),
		FONE_FORNECEDOR("foneFornecedor"),
		DDD_FORNECEDOR("dddFornecedor"),
		DESC_MARCA("descMarca"),
		DESC_MODALIDADE_LICITACAO("descModalidadeLicitacao"),
		TIPO_MODALIDADE_LICITACAO("tpModLicitacao"),
		INCISO("inciso");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	private Timestamp dtAberturaProposta;
	private Timestamp dtGeracao;
	private String formaPag;
	private Integer nroComplemento;
	private Integer nroNf;
	private Integer nrsSeq;
	private Integer pfrLctNumero;
	private Integer quantidade;
	private double valor;
	private Short nroItem;
	private Integer codMaterial;
	private String descMaterial;
	private String descUnidade;
	private Integer nroSolicitacao;
	private Integer nroLicit;
	private String descFornecedor;
	private Long foneFornecedor;
	private Short dddFornecedor;
	private String descMarca;
	private String descModalidadeLicitacao;
	private String tpModLicitacao;
	private String inciso;
	
	@Override  
    public int compareTo(RelUltimasComprasPACVO otherRelUltimasComprasPACVO) {	
		if (this.getNroSolicitacao() !=null){
		   return this.getNroSolicitacao().compareTo(otherRelUltimasComprasPACVO.getNroSolicitacao());
		}
		return 1;
        		
    }  
	
	/**
	 * @return the dtAberturaProposta
	 */
	public Timestamp getDtAberturaProposta() {
		return dtAberturaProposta;
	}
	/**
	 * @param dtAberturaProposta the dtAberturaProposta to set
	 */
	public void setDtAberturaProposta(Timestamp dtAberturaProposta) {
		this.dtAberturaProposta = dtAberturaProposta;
	}
	/**
	 * @return the dtGeracao
	 */
	public Timestamp getDtGeracao() {
		return dtGeracao;
	}
	/**
	 * @param dtGeracao the dtGeracao to set
	 */
	public void setDtGeracao(Timestamp dtGeracao) {
		this.dtGeracao = dtGeracao;
	}
	/**
	 * @return the formaPag
	 */
	public String getFormaPag() {
		return formaPag;
	}
	/**
	 * @param formaPag the formaPag to set
	 */
	public void setFormaPag(String formaPag) {
		this.formaPag = formaPag;
	}
	/**
	 * @return the nroComplemento
	 */
	public Integer getNroComplemento() {
		return nroComplemento;
	}
	/**
	 * @param nroComplemento the nroComplemento to set
	 */
	public void setNroComplemento(Integer nroComplemento) {
		this.nroComplemento = nroComplemento;
	}
	/**
	 * @return the nroNf
	 */
	public Integer getNroNf() {
		return nroNf;
	}
	/**
	 * @param nroNf the nroNf to set
	 */
	public void setNroNf(Integer nroNf) {
		this.nroNf = nroNf;
	}
	/**
	 * @return the nrsSeq
	 */
	public Integer getNrsSeq() {
		return nrsSeq;
	}
	/**
	 * @param nrsSeq the nrsSeq to set
	 */
	public void setNrsSeq(Integer nrsSeq) {
		this.nrsSeq = nrsSeq;
	}
	/**
	 * @return the pfrLctNumero
	 */
	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}
	/**
	 * @param pfrLctNumero the pfrLctNumero to set
	 */
	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}
	/**
	 * @return the quantidade
	 */
	public Integer getQuantidade() {
		return quantidade;
	}
	/**
	 * @param quantidade the quantidade to set
	 */
	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}
	/**
	 * @return the valor
	 */
	public double getValor() {
		return valor;
	}
	/**
	 * @param valor the valor to set
	 */
	public void setValor(double valor) {
		this.valor = valor;
	}
	
	public String getNumeroAF(){
		return nroLicit + "/" + nroComplemento; 
	}
	public Short getNroItem() {
		return nroItem;
	}
	public void setNroItem(Short nroItem) {
		this.nroItem = nroItem;
	}
	public Integer getCodMaterial() {
		return codMaterial;
	}
	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}
	public String getDescMaterial() {
		return descMaterial;
	}
	public void setDescMaterial(String descMaterial) {
		this.descMaterial = descMaterial;
	}
	public String getDescUnidade() {
		return descUnidade;
	}
	public void setDescUnidade(String descUnidade) {
		this.descUnidade = descUnidade;
	}
	public Integer getNroSolicitacao() {
		return nroSolicitacao;
	}
	public void setNroSolicitacao(Integer nroSolicitacao) {
		this.nroSolicitacao = nroSolicitacao;
	}
	public Integer getNroLicit() {
		return nroLicit;
	}
	public void setNroLicit(Integer nroLicit) {
		this.nroLicit = nroLicit;
	}
	public String getDescFornecedor() {
		return descFornecedor;
	}
	public void setDescFornecedor(String descFornecedor) {
		this.descFornecedor = descFornecedor;
	}
	public Long getFoneFornecedor() {
		return foneFornecedor;
	}
	public void setFoneFornecedor(Long foneFornecedor) {
		this.foneFornecedor = foneFornecedor;
	}
	public String getDescMarca() {
		return descMarca;
	}
	public void setDescMarca(String descMarca) {
		this.descMarca = descMarca;
	}
	public String getDescModalidadeLicitacao() {
		return descModalidadeLicitacao;
	}
	public void setDescModalidadeLicitacao(String descModalidadeLicitacao) {
		this.descModalidadeLicitacao = descModalidadeLicitacao;
	}

	public String getTpModLicitacao() {
		return tpModLicitacao;
	}

	public void setTpModLicitacao(String tpModLicitacao) {
		this.tpModLicitacao = tpModLicitacao;
	}
	
	public Short getDddFornecedor() {
			return dddFornecedor;
	}
		
	public void setDddFornecedor(Short dddFornecedor) {
		this.dddFornecedor = dddFornecedor;
	}
	
	public void setInciso(String inciso) {
		this.inciso = inciso;
	}

	public String getInciso() {
		return inciso;
	}

}
