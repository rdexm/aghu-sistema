package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * VO que representa o valor e descrição do exame
 * 
 * @author luismoura
 * 
 */
public class InformacaoExame implements Serializable {
	private static final long serialVersionUID = -92569821747730258L;

	private BigDecimal valor;
	private String descricao;

	public InformacaoExame() {

	}

	public InformacaoExame(BigDecimal valor, String descricao) {
		this.valor = valor;
		this.descricao = descricao;
	}

	public BigDecimal getValor() {
		return valor;
	}

	public void setValor(BigDecimal valor) {
		this.valor = valor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
}