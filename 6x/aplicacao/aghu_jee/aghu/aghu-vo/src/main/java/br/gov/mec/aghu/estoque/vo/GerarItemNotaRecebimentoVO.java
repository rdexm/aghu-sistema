package br.gov.mec.aghu.estoque.vo;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import br.gov.mec.aghu.dominio.DominioTipoItemNotaRecebimento;
import br.gov.mec.aghu.model.ScoUnidadeMedida;

public class GerarItemNotaRecebimentoVO {
	
	private Integer afnNumero;
	private Integer numero;
	
	private DominioTipoItemNotaRecebimento tipo;
	private Integer codigo;
	private String nome;
	private String marcaNome;
	private Integer qtdeSolicitada;
	private Integer qtdeRecebida;
	private Integer fatorConversao;
	private Double valorUnitIaf;
	private String unidSc;
	private String descrMatSrv;
	private ScoUnidadeMedida unidadeMedidaMaterialServico;

	private Integer quantidadeReceber;
	private Integer quantidadeConvertida;

	private BigDecimal valor;

	
	public Integer getAfnNumero() {
		return afnNumero;
	}
	
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}
	
	public Integer getNumero() {
		return numero;
	}
	
	public void setNumero(Integer numero) {
		this.numero = numero;
	}
	
	public DominioTipoItemNotaRecebimento getTipo() {
		return tipo;
	}
	public void setTipo(DominioTipoItemNotaRecebimento tipo) {
		this.tipo = tipo;
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
	public String getMarcaNome() {
		return marcaNome;
	}
	public void setMarcaNome(String marcaNome) {
		this.marcaNome = marcaNome;
	}
	public Integer getQtdeSolicitada() {
		this.qtdeSolicitada = qtdeSolicitada != null ? qtdeSolicitada : 0;
		return qtdeSolicitada;
	}
	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}
	public Integer getQtdeRecebida() {
		this.qtdeRecebida = qtdeRecebida != null ? qtdeRecebida : 0;
		return qtdeRecebida;
	}
	public void setQtdeRecebida(Integer qtdeRecebida) {
		this.qtdeRecebida = qtdeRecebida;
	}
	public Integer getFatorConversao() {
		return fatorConversao;
	}
	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}
	public Double getValorUnitIaf() {
		return valorUnitIaf;
	}
	public void setValorUnitIaf(Double valorUnitIaf) {
		this.valorUnitIaf = valorUnitIaf;
	}
	public String getUnidSc() {
		return unidSc;
	}
	public void setUnidSc(String unidSc) {
		this.unidSc = unidSc;
	}
	
	public Integer getQuantidadeConvertida() {
		return quantidadeConvertida;
	}
	
	public void setQuantidadeConvertida(Integer quantidadeConvertida) {
		this.quantidadeConvertida = quantidadeConvertida;
	}
	
	
	public BigDecimal getValor() {
		return valor;
	}
	
	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}
	
	public String getDescrMatSrv() {
		return descrMatSrv;
	}
	
	public void setDescrMatSrv(String descrMatSrv) {
		this.descrMatSrv = descrMatSrv;
	}
	
	public ScoUnidadeMedida getUnidadeMedidaMaterialServico() {
		return unidadeMedidaMaterialServico;
	}
	
	public void setUnidadeMedidaMaterialServico(ScoUnidadeMedida unidadeMedidaMaterialServico) {
		this.unidadeMedidaMaterialServico = unidadeMedidaMaterialServico;
	}
	
	public Integer getQuantidadeReceber() {
		return quantidadeReceber;
	}
	
	public void setQuantidadeReceber(Integer quantidadeReceber) {
		this.quantidadeReceber = quantidadeReceber;
	}
	
	/**
	 * Gera uma descrição sucinta do item de nota de recebimento
	 * @return
	 */
	public String getDescricaoItemNota() {
		
		StringBuffer descricao = new StringBuffer(55);
		final String separador = " | ";
		
		descricao.append("Item: ").append(getNumero()).append(separador)
		.append("Tipo: ").append(getTipo()).append(separador)
		.append("Material/Serviço: ").append(getNome()).append(separador);
		
		Locale locBR = new Locale("pt", "BR");//Brasil 
        DecimalFormatSymbols dfSymbols = new DecimalFormatSymbols(locBR);
        dfSymbols.setDecimalSeparator(',');
        DecimalFormat format = new DecimalFormat("#,###,###,###,##0.00", dfSymbols);
		
		descricao.append("Valor Unit. Líquido: ").append(format.format(this.getValorUnitIaf()));
		
		return descricao.toString();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((afnNumero == null) ? 0 : afnNumero.hashCode());
		result = prime * result + ((numero == null) ? 0 : numero.hashCode());
		return result;
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
		GerarItemNotaRecebimentoVO other = (GerarItemNotaRecebimentoVO) obj;
		if (afnNumero == null) {
			if (other.afnNumero != null) {
				return false;
			}
		} else if (!afnNumero.equals(other.afnNumero)) {
			return false;
		}
		if (numero == null) {
			if (other.numero != null) {
				return false;
			}
		} else if (!numero.equals(other.numero)) {
			return false;
		}
		return true;
	}

}	
	