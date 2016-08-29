package br.gov.mec.aghu.compras.contaspagar.vo;

import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import br.gov.mec.aghu.core.utils.AghuNumberFormat;

/**
 * @author rafael.silvestre
 */
public class SolicitacaoTituloVO {

	private String tipo;
	private Integer solicitacao;
	private String descricao;
	private String aplicacao;
	private Integer vbgSeq;
	private String vbgDescricao;
	private Integer codigo;
	private String nome;
	private Integer grupoNaturezaDespesa;
	private String descricaoGrupoNaturezaDespesa;
	private Byte ntdCodigo;
	private String ntdDescricao;
	private Date dataGeracao;
	private Number qtdeSolicitada;
	private BigDecimal valorUnitPrevisto;
	
	private String servicoMaterial;
	private String naturezaDespesa;
	private BigDecimal valor;
	
	private boolean selecionado;
	
	//46302
	private Integer seqTituloSolicitacao;
	private Double valorTitulSolicitacao;
	

	public SolicitacaoTituloVO() {
	}
	
	public SolicitacaoTituloVO(Integer solicitacao,
			String descricao, String aplicacao, Integer vbgSeq,
			String vbgDescricao, Integer codigo, String nome,
			Integer grupoNaturezaDespesa, String descricaoGrupoNaturezaDespesa,
			Byte ntdCodigo, String ntdDescricao, Date dataGeracao,
			Integer qtdeSolicitada, BigDecimal valorUnitPrevisto) {
		this.solicitacao = solicitacao;
		this.descricao = descricao;
		this.aplicacao = aplicacao;
		this.vbgSeq = vbgSeq;
		this.vbgDescricao = vbgDescricao;
		this.codigo = codigo;
		this.nome = nome;
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
		this.descricaoGrupoNaturezaDespesa = descricaoGrupoNaturezaDespesa;
		this.ntdCodigo = ntdCodigo;
		this.ntdDescricao = ntdDescricao;
		this.dataGeracao = dataGeracao;
		this.qtdeSolicitada = qtdeSolicitada;
		this.valorUnitPrevisto = valorUnitPrevisto;
	}

	public String getTipo() {
		return tipo;
	}

	public void setTipo(String tipo) {
		this.tipo = tipo;
	}

	public Integer getSolicitacao() {
		return solicitacao;
	}

	public void setSolicitacao(Integer solicitacao) {
		this.solicitacao = solicitacao;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}

	public Integer getGrupoNaturezaDespesa() {
		return grupoNaturezaDespesa;
	}

	public void setGrupoNaturezaDespesa(Integer grupoNaturezaDespesa) {
		this.grupoNaturezaDespesa = grupoNaturezaDespesa;
	}

	public String getDescricaoGrupoNaturezaDespesa() {
		return descricaoGrupoNaturezaDespesa;
	}

	public void setDescricaoGrupoNaturezaDespesa(String descricaoGrupoNaturezaDespesa) {
		this.descricaoGrupoNaturezaDespesa = descricaoGrupoNaturezaDespesa;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Integer getVbgSeq() {
		return vbgSeq;
	}

	public void setVbgSeq(Integer vbgSeq) {
		this.vbgSeq = vbgSeq;
	}

	public String getVbgDescricao() {
		return vbgDescricao;
	}

	public void setVbgDescricao(String vbgDescricao) {
		this.vbgDescricao = vbgDescricao;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public Byte getNtdCodigo() {
		return ntdCodigo;
	}

	public void setNtdCodigo(Byte ntdCodigo) {
		this.ntdCodigo = ntdCodigo;
	}

	public String getNtdDescricao() {
		return ntdDescricao;
	}

	public void setNtdDescricao(String ntdDescricao) {
		this.ntdDescricao = ntdDescricao;
	}

	public Number getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Number qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public BigDecimal getValorUnitPrevisto() {
		return valorUnitPrevisto;
	}

	public void setValorUnitPrevisto(BigDecimal valorUnitPrevisto) {
		this.valorUnitPrevisto = valorUnitPrevisto;
	}
	
	//	vbgSeq - vbgDescricao
	public String getVerbaGestao() {
		if (vbgSeq != null) {
			if (vbgDescricao != null) {
				return vbgSeq.toString() + " - " + vbgDescricao;
			} else {
				return vbgSeq.toString();
			}
		} else {
			if (vbgDescricao != null) {
				return vbgDescricao;
			} else {
				return StringUtils.EMPTY;
			}
		}
	}
	
	//	codigo - nome
	public String getServicoMaterial() {
		if (codigo != null) {
			if (nome != null) {
				servicoMaterial = codigo.toString() + " - " + nome;
			} else {
				servicoMaterial = codigo.toString();
			}
		} else {
			if (nome != null) {
				servicoMaterial = nome;
			} else {
				servicoMaterial = StringUtils.EMPTY;
			}
		}
		return servicoMaterial;
	}
	
	public void setServicoMaterial(String servicoMaterial) {
		this.servicoMaterial = servicoMaterial;
	}
	
	//	ntdCodigo - ntdDescricao
	public String getNaturezaDespesa() {
		if (ntdCodigo != null) {
			if (ntdDescricao != null) {
				naturezaDespesa = ntdCodigo.toString() + " - " + ntdDescricao;
			} else {
				naturezaDespesa = ntdCodigo.toString();
			}
		} else {
			if (ntdDescricao != null) {
				naturezaDespesa = ntdDescricao;
			} else {
				naturezaDespesa = StringUtils.EMPTY;
			}
		}
		return naturezaDespesa;
	}
	
	public void setNaturezaDespesa(String naturezaDespesa) {
		this.naturezaDespesa = naturezaDespesa;
	}
	
	public String getValorFormatado() {
		return AghuNumberFormat.formatarNumeroMoeda(getValor());
	}
	
	//	qtdeSolicitada * valorUnitPrevisto
	public BigDecimal getValor() {
		valor = BigDecimal.ZERO;
		if (valorUnitPrevisto != null && qtdeSolicitada != null) {
			if (qtdeSolicitada instanceof Integer) {
				valor = valorUnitPrevisto.multiply(new BigDecimal((Integer) qtdeSolicitada));
			} else if (qtdeSolicitada instanceof Long) {
				valor = valorUnitPrevisto.multiply(new BigDecimal((Long) qtdeSolicitada));
			} 
		}
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public Integer getSeqTituloSolicitacao() {
		return seqTituloSolicitacao;
	}

	public void setSeqTituloSolicitacao(Integer seqTituloSolicitacao) {
		this.seqTituloSolicitacao = seqTituloSolicitacao;
	}

	public Double getValorTitulSolicitacao() {
		return valorTitulSolicitacao;
	}

	public void setValorTitulSolicitacao(Double valorTitulSolicitacao) {
		this.valorTitulSolicitacao = valorTitulSolicitacao;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}
	
	public enum Fields {
		TIPO("tipo"),
		SOLICITACAO("solicitacao"),
		DESCRICAO("descricao"),
		APLICACAO("aplicacao"),
		VBG_SEQ("vbgSeq"),
		VBG_DESCRICAO("vbgDescricao"),
		CODIGO("codigo"),
		NOME("nome"),
		GRUPO_NATUREZA_DESPESA("grupoNaturezaDespesa"),
		DESCRICAO_GRUPO_NATUREZA_DESPESA("descricaoGrupoNaturezaDespesa"),
		NTD_CODIGO("ntdCodigo"),
		NTD_DESCRICAO("ntdDescricao"),
		DATA_GERACAO("dataGeracao"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		VALOR_UNIT_PREVISTO("valorUnitPrevisto"),
		SERVICO_MATERIAL("servicoMaterial"),
		SEQ_TITULO_SOLICITACAO("seqTituloSolicitacao"),
		VALOR_TITULO_SOLICITACAO("valorTitulSolicitacao"),
		NATUREZA_DESPESA("naturezaDespesa");
		
		private String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}
	
	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getTipo());
        umHashCodeBuilder.append(this.getSolicitacao());
        umHashCodeBuilder.append(this.getDescricao());
        umHashCodeBuilder.append(this.getAplicacao());
        umHashCodeBuilder.append(this.getVbgSeq());
        umHashCodeBuilder.append(this.getVbgDescricao());
        umHashCodeBuilder.append(this.getCodigo());
        umHashCodeBuilder.append(this.getNome());
        umHashCodeBuilder.append(this.getGrupoNaturezaDespesa());
        umHashCodeBuilder.append(this.getDescricaoGrupoNaturezaDespesa());
        umHashCodeBuilder.append(this.getNtdCodigo());
        umHashCodeBuilder.append(this.getNtdDescricao());
        umHashCodeBuilder.append(this.getDataGeracao());
        umHashCodeBuilder.append(this.getQtdeSolicitada());
        umHashCodeBuilder.append(this.getValorUnitPrevisto());
        return umHashCodeBuilder.toHashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		SolicitacaoTituloVO other = (SolicitacaoTituloVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getTipo(), other.getTipo());
        umEqualsBuilder.append(this.getSolicitacao(), other.getSolicitacao());
        umEqualsBuilder.append(this.getDescricao(), other.getDescricao());
        umEqualsBuilder.append(this.getAplicacao(), other.getAplicacao());
        umEqualsBuilder.append(this.getVbgSeq(), other.getVbgSeq());
        umEqualsBuilder.append(this.getVbgDescricao(), other.getVbgDescricao());
        umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
        umEqualsBuilder.append(this.getNome(), other.getNome());
        umEqualsBuilder.append(this.getGrupoNaturezaDespesa(), other.getGrupoNaturezaDespesa());
        umEqualsBuilder.append(this.getDescricaoGrupoNaturezaDespesa(), other.getDescricaoGrupoNaturezaDespesa());
        umEqualsBuilder.append(this.getNtdCodigo(), other.getNtdCodigo());
        umEqualsBuilder.append(this.getNtdDescricao(), other.getNtdDescricao());
        umEqualsBuilder.append(this.getDataGeracao(), other.getDataGeracao());
        umEqualsBuilder.append(this.getQtdeSolicitada(), other.getQtdeSolicitada());
        umEqualsBuilder.append(this.getValorUnitPrevisto(), other.getValorUnitPrevisto());
        return umEqualsBuilder.isEquals();
	}
}
