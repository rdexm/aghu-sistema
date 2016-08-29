package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecedor;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class ExcluirProgramacaoEntregaItemAFVO implements Serializable {
	
	private static final String HIFEN_ESPACADO = " - ";

	private static final long serialVersionUID = 2802331515830019587L;

	private Integer numeroItem;
	private DominioSituacaoAutorizacaoFornecedor situacao;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer codigoServico;
	private String nomeServico;
	private Double valor;
	private ScoUnidadeMedida unidade;
	private Integer quantidadeRecebida;
	private Integer quantidadeSolicitada;
	private String colunaDescricao;
	
	private Boolean selecionado = false;
	
	public Integer getNumeroItem() {
		return numeroItem;
	}
	
	public void setNumeroItem(Integer numeroItem) {
		this.numeroItem = numeroItem;
	}
	
	public DominioSituacaoAutorizacaoFornecedor getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacaoAutorizacaoFornecedor situacao) {
		this.situacao = situacao;
	}
	
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	
	public Integer getCodigoServico() {
		return codigoServico;
	}
	
	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}
	
	public String getNomeServico() {
		return nomeServico;
	}
	
	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}
	
	public Double getValor() {
		return valor;
	}
	
	public void setValor(Double valor) {
		this.valor = valor;
	}

	public String getValorFormatado() {
		return obterValorFormatado(valor);
	}

	
	public ScoUnidadeMedida getUnidade() {
		return unidade;
	}
	
	public void setUnidade(ScoUnidadeMedida unidade) {
		this.unidade = unidade;
	}
	
	public Integer getQuantidadeRecebida() {
		return quantidadeRecebida;
	}
	
	public void setQuantidadeRecebida(Integer quantidadeRecebida) {
		this.quantidadeRecebida = quantidadeRecebida;
	}
	
	public Integer getQuantidadeSolicitada() {
		return quantidadeSolicitada;
	}
	
	public void setQuantidadeSolicitada(Integer quantidadeSolicitada) {
		this.quantidadeSolicitada = quantidadeSolicitada;
	}
	
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Boolean getSelecionado() {
		return selecionado;
	}

	public void setColunaDescricao(String colunaDescricao) {
		this.colunaDescricao = colunaDescricao;
	}

	public String getColunaDescricao() {
		colunaDescricao = this.codigoMaterial + HIFEN_ESPACADO + this.nomeMaterial;
		if(this.codigoMaterial == null){
			colunaDescricao = this.codigoServico + HIFEN_ESPACADO + this.nomeServico;
		}
		
		if(colunaDescricao.length() > 35) {
			colunaDescricao= colunaDescricao.substring(0, 34) + "...";
		}
		
		return colunaDescricao;
	}

	public String getColunaDescricaoMat() {
		colunaDescricao = this.codigoMaterial + HIFEN_ESPACADO + this.nomeMaterial;
		if(this.codigoMaterial == null){
			colunaDescricao = this.codigoServico + HIFEN_ESPACADO + this.nomeServico;
		}
		
		return colunaDescricao;
	}
	
	public enum Fields {
		 
		NUMERO_ITEM("numeroItem"),            
		SITUACAO("situacao"),               
		CODIGO_MATERIAL("codigoMaterial"),        
		NOME_MATERIAL("nomeMaterial"),           
		CODIGO_SERVICO("codigoServico"),         
		NOME_SERVICO("nomeServico"),           
		VALOR("valor"),              
		UNIDADE("unidade"),                
		QUANTIDADE_SOLICITADA("quantidadeSolicitada"),  
		QUANTIDADE_RECEBIDA("quantidadeRecebida");    
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}	
	}

	private String obterValorFormatado(double valorUnitario) {
		BigDecimal vlUnitarioEmbalagem = new BigDecimal(valorUnitario);
		vlUnitarioEmbalagem = vlUnitarioEmbalagem.setScale(4, RoundingMode.HALF_UP);
		Locale locBR = new Locale("pt", "BR");
		DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
		dfSymbols.setDecimalSeparator(',');
		DecimalFormat format = new DecimalFormat("#,###,###,###,###,##0.0000####", dfSymbols);
		format.format(vlUnitarioEmbalagem);
		return format.format(vlUnitarioEmbalagem);
	}
}
