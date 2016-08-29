package br.gov.mec.aghu.sig.custos.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

public class CalculoProcedimentoVO implements Serializable {
	
	private static final long serialVersionUID = -732847051529688047L;

	private Short iphPhoSeq;
	private Integer iphSeq;
	private Long codTabela;
	private String descricao;
	private Integer atdSeq;
	private BigDecimal custoTotal;
	private BigDecimal receitaTotal;
	private Long quantidade;
	
	private Short iphPhoSeqSecundario;
	private Integer iphSeqSecundario;
	private Long codTabelaSecundario;
	private String descricaoSecundario;
	private Integer atdSeqSecundario;
	private BigDecimal custoTotalSecundario;
	private BigDecimal receitaTotalSecundario;
	private Long quantidadeSecundario;
	private Boolean segundoNivel;
	private Boolean terceiroNivel;
	private List<Integer> listaAtdSeq;
	private Integer prontuario;
	
	private String pacNome;
	private Integer conta;
	
	public enum Fields{
		IPH_PHO_SEQ ("iphPhoSeq"),
		IPH_SEQ ("iphSeq"),
		COD_TABELA ("codTabela"),
		DESCRICAO ("descricao"),
		ATD_SEQ("atdSeq"),
		CUSTO_TOTAL("custoTotal"),
		RECEITA_TOTAL("receitaTotal"),
		QUANTIDADE("quantidade"),
		
		IPH_PHO_SEQ_SECUNDARIO ("iphPhoSeqSecundario"),
		IPH_SEQ_SECUNDARIO ("iphSeqSecundario"),
		COD_TABELA_SECUNDARIO ("codTabelaSecundario"),
		DESCRICAO_SECUNDARIO ("descricaoSecundario"),
		ATD_SEQ_SECUNDARIO("atdSeqSecundario"),
		CUSTO_TOTAL_SECUNDARIO ("custoTotalSecundario"),
		RECEITA_TOTAL_SECUNDARIO ("receitaTotalSecundario"),
		QUANTIDADE_SECUNDARIO ("quantidadeSecundario"),
		NOME_PACIENTE("pacNome"),
		PRONTUARIO("prontuario"),
		CONTA("conta")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

	public Short getIphPhoSeq() {
		return iphPhoSeq;
	}

	public void setIphPhoSeq(Short iphPhoSeq) {
		this.iphPhoSeq = iphPhoSeq;
	}

	public Integer getIphSeq() {
		return iphSeq;
	}

	public void setIphSeq(Integer iphSeq) {
		this.iphSeq = iphSeq;
	}

	public Long getCodTabela() {
		return codTabela;
	}

	public void setCodTabela(Long codTabela) {
		this.codTabela = codTabela;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public BigDecimal getCustoTotal() {
		return custoTotal;
	}

	public void setCustoTotal(BigDecimal custoTotal) {
		this.custoTotal = custoTotal;
	}

	public BigDecimal getReceitaTotal() {
		return receitaTotal;
	}

	public void setReceitaTotal(BigDecimal receitaTotal) {
		this.receitaTotal = receitaTotal;
	}

	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}	
	
	public Short getIphPhoSeqSecundario() {
		return iphPhoSeqSecundario;
	}

	public void setIphPhoSeqSecundario(Short iphPhoSeqSecundario) {
		this.iphPhoSeqSecundario = iphPhoSeqSecundario;
	}

	public Integer getIphSeqSecundario() {
		return iphSeqSecundario;
	}

	public void setIphSeqSecundario(Integer iphSeqSecundario) {
		this.iphSeqSecundario = iphSeqSecundario;
	}

	public Long getCodTabelaSecundario() {
		return codTabelaSecundario;
	}

	public void setCodTabelaSecundario(Long codTabelaSecundario) {
		this.codTabelaSecundario = codTabelaSecundario;
	}

	public String getDescricaoSecundario() {
		return descricaoSecundario;
	}

	public void setDescricaoSecundario(String descricaoSecundario) {
		this.descricaoSecundario = descricaoSecundario;
	}

	public BigDecimal getCustoTotalSecundario() {
		return custoTotalSecundario;
	}

	public void setCustoTotalSecundario(BigDecimal custoTotalSecundario) {
		this.custoTotalSecundario = custoTotalSecundario;
	}

	public BigDecimal getReceitaTotalSecundario() {
		return receitaTotalSecundario;
	}

	public void setReceitaTotalSecundario(BigDecimal receitaTotalSecundario) {
		this.receitaTotalSecundario = receitaTotalSecundario;
	}

	public Long getQuantidadeSecundario() {
		return quantidadeSecundario;
	}

	public void setQuantidadeSecundario(Long quantidadeSecundario) {
		this.quantidadeSecundario = quantidadeSecundario;
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Integer getAtdSeqSecundario() {
		return atdSeqSecundario;
	}

	public void setAtdSeqSecundario(Integer atdSeqSecundario) {
		this.atdSeqSecundario = atdSeqSecundario;
	}

	public Boolean getSegundoNivel() {
		return segundoNivel;
	}

	public void setSegundoNivel(Boolean segundoNivel) {
		this.segundoNivel = segundoNivel;
	}

	public Boolean getTerceiroNivel() {
		return terceiroNivel;
	}

	public void setTerceiroNivel(Boolean terceiroNivel) {
		this.terceiroNivel = terceiroNivel;
	}

	public List<Integer> getListaAtdSeq() {
		return listaAtdSeq;
	}

	public void setListaAtdSeq(List<Integer> listaAtdSeq) {
		this.listaAtdSeq = listaAtdSeq;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getConta() {
		return conta;
	}

	public void setConta(Integer conta) {
		this.conta = conta;
	}
}
