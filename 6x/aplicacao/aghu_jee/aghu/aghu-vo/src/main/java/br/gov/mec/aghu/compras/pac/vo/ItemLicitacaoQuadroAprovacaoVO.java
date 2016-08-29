package br.gov.mec.aghu.compras.pac.vo;

import java.math.BigDecimal;
import java.math.RoundingMode;

import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioSituacaoJulgamento;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoCondicaoPagamentoPropos;
import br.gov.mec.aghu.model.ScoModalidadeLicitacao;

/**
 * Item de Licitação do Quadro de Aprovação
 * 
 * @author mlcruz
 */
public class ItemLicitacaoQuadroAprovacaoVO {
	/** Modalidade */
	private ScoModalidadeLicitacao modalidade;
	
	/** Número do PAC */
	private Integer numeroPac;
	
	/** Descrição */
	private String descricaoPac;
	
	/** Número do Item */
	private Short numeroItem;
	
	/** Nome Membro #1 Comite Licitação */
	private String nomeAssinatura1, deptoAssinatura1;
	
	/** Nome Membro #2 Comite Licitação */
	private String nomeAssinatura2, deptoAssinatura2;
	
	/** Nome Membro #3 Comite Licitação */
	private String nomeAssinatura3, deptoAssinatura3;
	
	/** Nome Membro #4 Comite Licitação */
	private String nomeAssinatura4, deptoAssinatura4;
	
	/** Nome Membro #5 Comite Licitação */
	private String nomeAssinatura5, deptoAssinatura5;
	
	/** Nome Membro #6 Comite Licitação */
	private String nomeAssinatura6, deptoAssinatura6;
	
	/** Tipo */
	private DominioTipoFaseSolicitacao tipo;
	
	/** Material */
	private String material;
	
	/** Serviço */
	private String servico;
	
	/** Excluído */
	private Boolean excluido;
	
	/** Motivo Cancelamento */
	private DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento;
	
	/** Motivo */
	private String motivo;
	
	/** Situação Julgamento */
	private DominioSituacaoJulgamento situacaoJulgamento;
	
	/** Fornecedor */
	private String fornecedor;
	
	/** Critério de Escolha */
	private String criterio;
	
	/** Condição de Pagamento */
	private ScoCondicaoPagamentoPropos condicao;
	
	/** Forma de Pagamento */
	private String formaPagamento;
	
	/** Valor Unitário */
	private BigDecimal valorUnitario;
	
	/** Fator de Conversão */
	private Integer fatorConversao;
	
	/** Parcelas */
	private Short parcelas;
	
	/** Sem Proposta */
	private Boolean semProposta;
	
	/** Pendente */
	private Boolean pendente;
		
	private Integer codigoMaterial;
	private Integer codigoServico;
	
	/** Obtem descrição. */
	public String getDescricaoItem() {
		return DominioTipoFaseSolicitacao.C.equals(tipo) ? codigoMaterial.toString() + " - " + material : codigoServico.toString() + " - " + servico;
	}
	
	/** Indica se está cancelado. */
	public Boolean getCancelado() {
		return motivoCancelamento != null;
	}
	
	/** Obtem valor unitário convertido. */
	public BigDecimal getValorUnitarioConvertido() {
		if (valorUnitario != null && fatorConversao != null) {
			return valorUnitario.divide(new BigDecimal(fatorConversao), 4, RoundingMode.HALF_UP);
		} else {
			return null;
		}
	}
	
	// Getters/Setters
	
	public ScoModalidadeLicitacao getModalidade() {
		return modalidade;
	}

	public Integer getNumeroPac() {
		return numeroPac;
	}

	public void setNumeroPac(Integer numeroPac) {
		this.numeroPac = numeroPac;
	}

	public String getDescricaoPac() {
		return descricaoPac;
	}

	public void setDescricaoPac(String descricaoPac) {
		this.descricaoPac = descricaoPac;
	}

	public Short getNumeroItem() {
		return numeroItem;
	}

	public void setNumeroItem(Short numeroItem) {
		this.numeroItem = numeroItem;
	}

	public void setModalidade(ScoModalidadeLicitacao modalidade) {
		this.modalidade = modalidade;
	}

	public String getNomeAssinatura1() {
		return nomeAssinatura1;
	}

	public void setNomeAssinatura1(String nomeAssinatura1) {
		this.nomeAssinatura1 = nomeAssinatura1;
	}

	public String getDeptoAssinatura1() {
		return deptoAssinatura1;
	}

	public void setDeptoAssinatura1(String deptoAssinatura1) {
		this.deptoAssinatura1 = deptoAssinatura1;
	}

	public String getNomeAssinatura2() {
		return nomeAssinatura2;
	}

	public void setNomeAssinatura2(String nomeAssinatura2) {
		this.nomeAssinatura2 = nomeAssinatura2;
	}

	public String getDeptoAssinatura2() {
		return deptoAssinatura2;
	}

	public void setDeptoAssinatura2(String deptoAssinatura2) {
		this.deptoAssinatura2 = deptoAssinatura2;
	}

	public String getNomeAssinatura3() {
		return nomeAssinatura3;
	}

	public void setNomeAssinatura3(String nomeAssinatura3) {
		this.nomeAssinatura3 = nomeAssinatura3;
	}

	public String getDeptoAssinatura3() {
		return deptoAssinatura3;
	}

	public void setDeptoAssinatura3(String deptoAssinatura3) {
		this.deptoAssinatura3 = deptoAssinatura3;
	}

	public String getNomeAssinatura4() {
		return nomeAssinatura4;
	}

	public void setNomeAssinatura4(String nomeAssinatura4) {
		this.nomeAssinatura4 = nomeAssinatura4;
	}

	public String getDeptoAssinatura4() {
		return deptoAssinatura4;
	}

	public void setDeptoAssinatura4(String deptoAssinatura4) {
		this.deptoAssinatura4 = deptoAssinatura4;
	}

	public String getNomeAssinatura5() {
		return nomeAssinatura5;
	}

	public void setNomeAssinatura5(String nomeAssinatura5) {
		this.nomeAssinatura5 = nomeAssinatura5;
	}

	public String getDeptoAssinatura5() {
		return deptoAssinatura5;
	}

	public void setDeptoAssinatura5(String deptoAssinatura5) {
		this.deptoAssinatura5 = deptoAssinatura5;
	}

	public String getNomeAssinatura6() {
		return nomeAssinatura6;
	}

	public void setNomeAssinatura6(String nomeAssinatura6) {
		this.nomeAssinatura6 = nomeAssinatura6;
	}

	public String getDeptoAssinatura6() {
		return deptoAssinatura6;
	}

	public void setDeptoAssinatura6(String deptoAssinatura6) {
		this.deptoAssinatura6 = deptoAssinatura6;
	}

	public DominioTipoFaseSolicitacao getTipo() {
		return tipo;
	}

	public void setTipo(DominioTipoFaseSolicitacao tipo) {
		this.tipo = tipo;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public String getServico() {
		return servico;
	}

	public void setServico(String servico) {
		this.servico = servico;
	}

	public Boolean getExcluido() {
		return excluido;
	}

	public void setExcluido(Boolean excluido) {
		this.excluido = excluido;
	}

	public DominioMotivoCancelamentoComissaoLicitacao getMotivoCancelamento() {
		return motivoCancelamento;
	}

	public void setMotivoCancelamento(DominioMotivoCancelamentoComissaoLicitacao motivoCancelamento) {
		this.motivoCancelamento = motivoCancelamento;
	}

	public String getMotivo() {
		return motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public DominioSituacaoJulgamento getSituacaoJulgamento() {
		return situacaoJulgamento;
	}

	public void setSituacaoJulgamento(DominioSituacaoJulgamento situacaoJulgamento) {
		this.situacaoJulgamento = situacaoJulgamento;
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public String getCriterio() {
		return criterio;
	}

	public void setCriterio(String criterio) {
		this.criterio = criterio;
	}

	public ScoCondicaoPagamentoPropos getCondicao() {
		return condicao;
	}

	public void setCondicao(ScoCondicaoPagamentoPropos condicao) {
		this.condicao = condicao;
	}

	public String getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(String formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public BigDecimal getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(BigDecimal valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Short getParcelas() {
		return parcelas;
	}

	public void setParcelas(Short parcelas) {
		this.parcelas = parcelas;
	}

	public Boolean getPendente() {
		return pendente;
	}

	public void setPendente(Boolean pendente) {
		this.pendente = pendente;
	}

	public Boolean getSemProposta() {
		return semProposta;
	}

	public void setSemProposta(Boolean semProposta) {
		this.semProposta = semProposta;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public Integer getCodigoServico() {
		return codigoServico;
	}

	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}

	/** Campos */
	public enum Field {
		MODALIDADE("modalidade"),
		NUMERO_PAC("numeroPac"),
		DESCRICAO_PAC("descricaoPac"),
		GESTOR("nomeAssinatura1"),
		NUMERO_ITEM("numeroItem"),
		TIPO("tipo"),
		CODIGO_MATERIAL("codigoMaterial"),
		MATERIAL("material"),
		CODIGO_SERVICO("codigoServico"),
		SERVICO("servico"),
		EXCLUIDO("excluido"),
		MOTIVO_CANCELAMENTO("motivoCancelamento"),
		SITUACAO_JULGAMENTO("situacaoJulgamento"),
		FORNECEDOR("fornecedor"),
		CRITERIO("criterio"),
		CONDICAO("condicao"),
		FORMA_PAGAMENTO("formaPagamento"),
		VALOR_UNITARIO("valorUnitario"),
		FATOR_CONVERSAO("fatorConversao");
		
		/** Propriedade */
		private String prop;
		
		private Field(String prop) {
			this.prop = prop;
		}
		
		@Override
		public String toString() {
			return prop;
		}
	}
}
