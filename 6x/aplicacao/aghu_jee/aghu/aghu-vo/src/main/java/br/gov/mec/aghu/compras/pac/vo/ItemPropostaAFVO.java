package br.gov.mec.aghu.compras.pac.vo;

import java.math.BigDecimal;

import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemPropostaFornecedor;

/**
 * @author silvia
 *
 */
public class ItemPropostaAFVO {
	
	private ScoItemPropostaFornecedor itemPropostaFornecedor;
	
	private DominioTipoFaseSolicitacao tipo;
	private DominioModalidadeEmpenho modalidadeEmpenho;
	
	private Integer slcNumero;
	private Integer slsNumero;
	
	private Integer slcVbgSeq;
	private Integer slsVbgSeq;
	
	private Integer slcNtdGndCodigo;
	private Integer slsNtdGndCodigo;
	
	private Integer matCodigo;
	private Integer frequenciaEntrega;
	private Integer numeroPac;
	
	private Short numeroItemPac;
	private Short codigoFormaPag;

	private Byte slcNtdCodigo;
	private Byte slsNtdCodigo;

	private Boolean slcIndExclusao;
	private Boolean slsIndExclusao;
	private Boolean indEscolhido;
	
	private BigDecimal valorUnitarioPrevisto;
	private BigDecimal valorUnitarioPropForn;
	
	private String descricaoFormaPag;
	private String razaoSocialFornecedor;
	
	// Getters/Setters
	public ScoItemPropostaFornecedor getItemPropostaFornecedor() {
		return itemPropostaFornecedor;
	}

	public void setItemPropostaFornecedor(ScoItemPropostaFornecedor itemPropostaFornecedor) {
		this.itemPropostaFornecedor = itemPropostaFornecedor;
	}

	public DominioTipoFaseSolicitacao getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoFaseSolicitacao tipo) {
		this.tipo = tipo;
	}

	public Integer getSlcNumero() {
		return slcNumero;
	}

	public void setSlcNumero(Integer slcNumero) {
		this.slcNumero = slcNumero;
	}

	public Integer getSlsNumero() {
		return slsNumero;
	}

	public void setSlsNumero(Integer slsNumero) {
		this.slsNumero = slsNumero;
	}

	public Integer getFrequenciaEntrega() {
		return frequenciaEntrega;
	}

	public void setFrequenciaEntrega(Integer frequenciaEntrega) {
		this.frequenciaEntrega = frequenciaEntrega;
	}

	public BigDecimal getValorUnitarioPrevisto() {
		return valorUnitarioPrevisto;
	}

	public void setValorUnitarioPrevisto(BigDecimal valorUnitarioPrevisto) {
		this.valorUnitarioPrevisto = valorUnitarioPrevisto;
	}

	public BigDecimal getValorUnitarioPropForn() {
		return valorUnitarioPropForn;
	}

	public void setValorUnitarioPropForn(BigDecimal valorUnitarioPropForn) {
		this.valorUnitarioPropForn = valorUnitarioPropForn;
	}
	
	public Integer getMatCodigo() {
		return matCodigo;
	}

	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}

	public DominioModalidadeEmpenho getModalidadeEmpenho() {
		return modalidadeEmpenho;
	}

	public void setModalidadeEmpenho(DominioModalidadeEmpenho modalidadeEmpenho) {
		this.modalidadeEmpenho = modalidadeEmpenho;
	}

	public Integer getSlcVbgSeq() {
		return slcVbgSeq;
	}

	public void setSlcVbgSeq(Integer slcVbgSeq) {
		this.slcVbgSeq = slcVbgSeq;
	}

	public Integer getSlcNtdGndCodigo() {
		return slcNtdGndCodigo;
	}

	public void setSlcNtdGndCodigo(Integer slcNtdGndCodigo) {
		this.slcNtdGndCodigo = slcNtdGndCodigo;
	}

	public Byte getSlcNtdCodigo() {
		return slcNtdCodigo;
	}

	public void setSlcNtdCodigo(Byte slcNtdCodigo) {
		this.slcNtdCodigo = slcNtdCodigo;
	}

	public Boolean getSlcIndExclusao() {
		return slcIndExclusao;
	}

	public void setSlcIndExclusao(Boolean slcIndExclusao) {
		this.slcIndExclusao = slcIndExclusao;
	}

	public Integer getSlsVbgSeq() {
		return slsVbgSeq;
	}

	public void setSlsVbgSeq(Integer slsVbgSeq) {
		this.slsVbgSeq = slsVbgSeq;
	}

	public Integer getSlsNtdGndCodigo() {
		return slsNtdGndCodigo;
	}

	public void setSlsNtdGndCodigo(Integer slsNtdGndCodigo) {
		this.slsNtdGndCodigo = slsNtdGndCodigo;
	}

	public Byte getSlsNtdCodigo() {
		return slsNtdCodigo;
	}

	public void setSlsNtdCodigo(Byte slsNtdCodigo) {
		this.slsNtdCodigo = slsNtdCodigo;
	}

	public Boolean getSlsIndExclusao() {
		return slsIndExclusao;
	}

	public void setSlsIndExclusao(Boolean slsIndExclusao) {
		this.slsIndExclusao = slsIndExclusao;
	}
	
	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public Short getNumeroItemPac() {
		return numeroItemPac;
	}

	public void setNumeroItemPac(Short numeroItemPac) {
		this.numeroItemPac = numeroItemPac;
	}

	public Short getCodigoFormaPag() {
		return codigoFormaPag;
	}

	public void setCodigoFormaPag(Short codigoFormaPag) {
		this.codigoFormaPag = codigoFormaPag;
	}

	public String getDescricaoFormaPag() {
		return descricaoFormaPag;
	}

	public void setDescricaoFormaPag(String descricaoFormaPag) {
		this.descricaoFormaPag = descricaoFormaPag;
	}
	
	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	public Boolean getIndEscolhido() {
		return indEscolhido;
	}

	public void setIndEscolhido(Boolean indEscolhido) {
		this.indEscolhido = indEscolhido;
	}
	
	/** Campos */
	public enum Fields {
		
		ITEM_PROPOSTA_FORNECEDOR("itemPropostaFornecedor"),
		NUMERO_PAC("numeroPac"),
		NUMERO_ITEM("numeroItemPac"),
		TIPO("tipo"),
		MODALIDADE_EMPENHO("modalidadeEmpenho"),
		SLC_NUMERO("slcNumero"),
		SLS_NUMERO("slsNumero"),
		SLC_VBG_SEQ("slcVbgSeq"),
		SLS_VBG_SEQ("slsVbgSeq"),
		SLC_NTD_GND_CODIGO("slcNtdGndCodigo"),
		SLC_NTD_CODIGO("slcNtdCodigo"),
		SLS_NTD_GND_CODIGO("slsNtdGndCodigo"),
		SLS_NTD_CODIGO("slsNtdCodigo"),
		MAT_CODIGO("matCodigo"),
		FREQUENCIA_ENTREGA("frequenciaEntrega"),
		SLC_IND_EXCLUSAO("slcIndExclusao"),
		SLS_IND_EXCLUSAO("slsIndExclusao"),
		COD_FORMA_PAGAMENTO("codigoFormaPag"),
		DESCR_FORMA_PAGAMENTO("descricaoFormaPag"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor"),
		VALOR_UNITARIO_PREVISTO("valorUnitarioPrevisto"),
		VALOR_UNITARIO_PROP_FORNECEDOR("valorUnitarioPropForn"),
		IND_ESCOLHIDO("indEscolhido");
		
		/** Propriedade */
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
