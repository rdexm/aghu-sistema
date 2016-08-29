package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.model.SceMotivoProblema;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PendenciasDevolucaoVO {

	private Integer bocSeq;
	private Short nroItem;
	private Integer matCodigo;
	private String nomeMaterial;
	private String descricaoMaterial;
	private String unidadeMaterial;
	private Integer qtdEntrada;
	private Integer qtdSaldo;
	private Long qtdeSaida;
	private BigDecimal valorUnitario;
	private BigDecimal valorTotal;
	private String descricao;
	private Long qtdeBo;
	private Integer qtdeNr;	
	private Double valorTotalItemNrOriginal;	
	private Integer matCodigoBo;
	private Integer fatorConversao;
	private Boolean isMarked;
	private SceMotivoProblema motivoProblema;
	private Double valorTotalItemNr;
	
	public PendenciasDevolucaoVO() {
		
	}
	
	public Integer getBocSeq() {
		return bocSeq;
	}


	public void setBocSeq(Integer bocSeq) {
		this.bocSeq = bocSeq;
	}


	public Short getNroItem() {
		return nroItem;
	}


	public void setNroItem(Short nroItem) {
		this.nroItem = nroItem;
	}


	public Integer getMatCodigo() {
		return matCodigo;
	}


	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}


	public String getNomeMaterial() {
		return nomeMaterial;
	}


	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}


	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}


	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}


	public String getUnidadeMaterial() {
		return unidadeMaterial;
	}


	public void setUnidadeMaterial(String unidadeMaterial) {
		this.unidadeMaterial = unidadeMaterial;
	}


	public Integer getQtdEntrada() {
		return qtdEntrada;
	}


	public void setQtdEntrada(Integer qtdEntrada) {
		this.qtdEntrada = qtdEntrada;
	}


	public Integer getQtdSaldo() {
		return qtdSaldo;
	}


	public void setQtdSaldo(Integer qtdSaldo) {
		this.qtdSaldo = qtdSaldo;
	}


	public Long getQtdeSaida() {
		return qtdeSaida;
	}


	public void setQtdeSaida(Long qtdeSaida) {
		this.qtdeSaida = qtdeSaida;
	}


	public BigDecimal getValorTotal() {
		return valorTotal;
	}


	public void setValorTotal(BigDecimal valorTotal) {
		this.valorTotal = valorTotal;
	}


	public String getDescricao() {
		return descricao;
	}


	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}


	public Long getQtdeBo() {
		return qtdeBo;
	}


	public void setQtdeBo(Long qtdeBo) {
		this.qtdeBo = qtdeBo;
	}
	
		
	public Integer getQtdeNr() {
		return qtdeNr;
	}

	public void setQtdeNr(Integer qtdeNr) {
		this.qtdeNr = qtdeNr;
	}

	public Double getValorTotalItemNr() {
		return valorTotalItemNr;
	}

	public void setValorTotalItemNr(Double valorTotalItemNr) {
		this.valorTotalItemNr = valorTotalItemNr;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((bocSeq == null) ? 0 : bocSeq.hashCode());
		result = prime * result + ((nroItem == null) ? 0 : nroItem.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		PendenciasDevolucaoVO other = (PendenciasDevolucaoVO) obj;
		if (bocSeq == null || nroItem == null) {
			if (other.bocSeq != null || other.nroItem != null){
				return false;
			}
		} else if (!bocSeq.equals(other.bocSeq) || !nroItem.equals(other.nroItem)) {
			return false;
		}
		
		return true;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}	
	
	
	public Integer getMatCodigoBo() {
		return matCodigoBo;
	}

	public void setMatCodigoBo(Integer matCodigoBo) {
		this.matCodigoBo = matCodigoBo;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Boolean getIsMarked() {
		return isMarked;
	}

	public void setIsMarked(Boolean isMarked) {
		this.isMarked = isMarked;
	}

	/**** Metodos de Calculo ***/
	public Integer getQtdSaldoCalculado(){
		if (this.qtdeBo == null &&
			this.qtdeNr != null){
			return this.qtdeNr;
		}
		return this.qtdeNr != null && this.qtdeBo != null ? this.qtdeNr - this.qtdeBo.intValue() : 0;	
		
	}
	
	public Double getValorUnitarioItemCalculado(){
		Double  vlrTotalItemNr = (Double) CoreUtil.nvl(this.getValorTotalItemNrOriginal(),0);
		Integer qtde =  (Integer) CoreUtil.nvl(this.getQtdeNr(),0);
		Integer fatorConversao =  (Integer) CoreUtil.nvl(this.getFatorConversao(),0);
		
		if (fatorConversao != 0 && ((qtde/fatorConversao) != 0)){			
			return vlrTotalItemNr / (qtde/fatorConversao);
		}
		return new Double(0);
	} 
	
	public SceMotivoProblema getMotivoProblema() {
		return motivoProblema;
	}

	public void setMotivoProblema(SceMotivoProblema motivoProblema) {
		this.motivoProblema = motivoProblema;
	}

	public Double getValorTotalItemNrOriginal() {
		return valorTotalItemNrOriginal;
	}

	public void setValorTotalItemNrOriginal(Double valorTotalItemNrOriginal) {
		this.valorTotalItemNrOriginal = valorTotalItemNrOriginal;
	}

	public enum Fields {
		NUMERO_ITEM("nroItem"), 
		MAT_CODIGO("matCodigo"), 
		MAT_NOME("nomeMaterial"),
		MAT_DESCRICAO("descricaoMaterial"),
		MAT_UNIDADE_MEDIDA("unidadeMaterial"), 
		QTDE_ENTRADA("qtdEntrada"), 
		QTDE_SALDO("qtdSaldo"), 
		QTDE_SAIDA("qtdeSaida"), 
		QTDE_BO("qtdeBo"),
		QTDE_NR("qtdeNr"),
		VLR_UNITARIO("valorUnitario"), 
		VLR_TOTAL("valorTotal"),		
		VLR_TOTAL_ITEM_NR("valorTotalItemNr"),
		VLR_TOTAL_ITEM_NR_ORIGINAL("valorTotalItemNrOriginal"),
		MAT_CODIGO_BO("matCodigoBo"),
		FATOR_CONVERSAO("fatorConversao");

		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
}
}
