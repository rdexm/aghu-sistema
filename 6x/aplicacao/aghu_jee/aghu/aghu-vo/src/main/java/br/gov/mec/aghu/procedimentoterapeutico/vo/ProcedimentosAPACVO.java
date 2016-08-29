package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioPrioridadeCid;

public class ProcedimentosAPACVO implements Serializable {

	/**
	 * C3
	 */
	private static final long serialVersionUID = -506476651620784338L;
	
	private Long numero;
		private String codTabela;
	private DominioPrioridadeCid indPrioridade;
	
	
	public enum Fields {
		NUMERO("numero"),
		COD_TABELA("codTabela"),
		IND_PRIORIDADE("indPrioridade");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}
		
		@Override
		public String toString() {
			return fields;
		}
	}
	
	public Long getNumero() {
		return numero;
	}


	public void setNumero(Long numero) {
		this.numero = numero;
	}


	public String getCodTabela() {
		return codTabela;
	}


	public void setCodTabela(String codTabela) {
		this.codTabela = codTabela;
	}


	public DominioPrioridadeCid getIndPrioridade() {
		return indPrioridade;
	}


	public void setIndPrioridade(DominioPrioridadeCid indPrioridade) {
		this.indPrioridade = indPrioridade;
	}



}
