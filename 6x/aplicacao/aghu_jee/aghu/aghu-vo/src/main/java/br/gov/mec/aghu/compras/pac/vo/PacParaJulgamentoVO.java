package br.gov.mec.aghu.compras.pac.vo;

import br.gov.mec.aghu.dominio.DominioSituacaoLicitacao;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * PAC para Julgamento
 * 
 * @author mlcruz
 */
public class PacParaJulgamentoVO implements BaseBean {
	
	private static final long serialVersionUID = -8128413633364289401L;

	/** Número */
	private Integer numero;
	
	/** Descrição */
	private String descricao;
	
	/** Modalidade */
	private String modalidade;
	
	/** Situação */
	private DominioSituacaoLicitacao situacao;
	
	/** Quantidade de Itens */
	private Integer qtdeItens;
	
	/** Quantidade de Itens Julgados */
	private Integer qtdeItensJulgados;
	
	// Getters/Setters

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public String getModalidade() {
		return modalidade;
	}

	public void setModalidade(String modalidade) {
		this.modalidade = modalidade;
	}

	public DominioSituacaoLicitacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoLicitacao situacao) {
		this.situacao = situacao;
	}

	public Integer getQtdeItens() {
		return qtdeItens;
	}

	public void setQtdeItens(Integer qtdeItens) {
		this.qtdeItens = qtdeItens;
	}

	public Integer getQtdeItensJulgados() {
		return qtdeItensJulgados;
	}

	public void setQtdeItensJulgados(Integer qtdeItensJulgados) {
		this.qtdeItensJulgados = qtdeItensJulgados;
	}
	
	// Fields Constants
	public enum Field {
		NUMERO("numero"),
		DESCRICAO("descricao"),
		MODALIDADE("modalidade"),
		SITUACAO("situacao");
		
		/** Property Correspondente do VO */
		private String prop;
		
		private Field(String prop) {
			this.prop = prop;
		}
		
		public String toString() {
			return prop;
		}
	}
}