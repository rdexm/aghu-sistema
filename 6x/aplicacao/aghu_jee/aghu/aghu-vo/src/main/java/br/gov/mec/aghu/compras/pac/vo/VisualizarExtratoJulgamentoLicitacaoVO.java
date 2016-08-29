package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;
import java.math.BigDecimal;

public class VisualizarExtratoJulgamentoLicitacaoVO implements Serializable {

	private static final long serialVersionUID = 6311943154533755902L;

	private Integer pfrFrnNumero;
	private String razaoSocial;
	private Long cgc;
	private Short itemSolicitacaoNumero;
	private String umdCodigo;
	private Integer fatorConversao;
	private String criterio;
	private String condPagamento;
	private BigDecimal valorUnitario;

	private Integer codigoMaterialServico;
	private String nomeMaterialServico;

	private String unidadeMedica;

	private String email;
	
	private String fornecedor;

	public enum Fields {

		PFR_FRN_NUMERO("pfrFrnNumero"),
		RAZAO_SOCIAL("razaoSocial"),
		CGC("cgc"),
		ITEM_SOLICITACAO_NUMERO("descricitemSolicitacaoNumeroao"),
		UMD_CODIGO("umdCodigo"),
		FATOR_CONVERSAO("fatorConversao"),
		CRITERIO("criterio"),
		COND_PAGAMENTO("condPagamento"),
		VALOR_UNITARIO("valorUnitario"),
		CODIGO_MATERIAL_SERVICO("codigoMaterialServico"),
		NOME_MATERIAL_SERVICO("nomeMaterialServico"),
		EMAIL("email");

		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}

	public void setPfrFrnNumero(Integer pfrFrnNumero) {
		this.pfrFrnNumero = pfrFrnNumero;
	}

	public Integer getPfrFrnNumero() {
		return pfrFrnNumero;
	}

	public void setRazaoSocial(String razaoSocial) {
		this.razaoSocial = razaoSocial;
	}

	public String getRazaoSocial() {
		return razaoSocial;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setItemSolicitacaoNumero(Short itemSolicitacaoNumero) {
		this.itemSolicitacaoNumero = itemSolicitacaoNumero;
	}

	public Short getItemSolicitacaoNumero() {
		return itemSolicitacaoNumero;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setCondPagamento(String condPagamento) {
		this.condPagamento = condPagamento;
	}

	public String getCondPagamento() {
		return condPagamento;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public String getCriterio() {
		return criterio;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public Integer getCodigoMaterialServico() {
		return codigoMaterialServico;
	}

	public void setCodigoMaterialServico(Integer codigoMaterialServico) {
		this.codigoMaterialServico = codigoMaterialServico;
	}

	public String getNomeMaterialServico() {
		return nomeMaterialServico;
	}

	public void setNomeMaterialServico(String nomeMaterialServico) {
		this.nomeMaterialServico = nomeMaterialServico;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getEmail() {
		return email;
	}

	public void setUnidadeMedica(String unidadeMedica) {
		this.unidadeMedica = unidadeMedica;
	}

	public String getUnidadeMedica() {
		return unidadeMedica;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((cgc == null) ? 0 : cgc.hashCode());
		result = prime
				* result
				+ ((itemSolicitacaoNumero == null) ? 0 : itemSolicitacaoNumero
						.hashCode());
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
		if (!(obj instanceof VisualizarExtratoJulgamentoLicitacaoVO)){
			return false;
		}
		VisualizarExtratoJulgamentoLicitacaoVO other = (VisualizarExtratoJulgamentoLicitacaoVO) obj;
		if (cgc == null) {
			if (other.cgc != null){
				return false;
			}
		} else if (!cgc.equals(other.cgc)){
			return false;
		}
		if (itemSolicitacaoNumero == null) {
			if (other.itemSolicitacaoNumero != null){
				return false;
			}
		} else if (!itemSolicitacaoNumero.equals(other.itemSolicitacaoNumero)){
			return false;
		}
		
		return true;
	}

}
