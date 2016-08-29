package br.gov.mec.aghu.estoque.vo;

import java.util.Date;
import java.util.List;

import br.gov.mec.aghu.model.SceDocumentoFiscalEntrada;
import br.gov.mec.aghu.model.ScoFornecedor;

/**
 * Quantidade total de itens de uma ou mais notas de recebimento provisório.
 * 
 * @author mlcruz
 */
public class QtdeRpVO {
	/** Número máximo de notas de recebimento provisório. */
	public static final int MAX_RPS = 5;
	
	/** Quantidade */
	private Long quantidade;
	
	/** Notas de Recebimento */
	private List<NotaRecebimentoProvisorio> notasRecebimento;
	
	private Boolean mostrarAlerta;
	
	// Getters/Setters
	
	public Long getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Long quantidade) {
		this.quantidade = quantidade;
	}

	public List<NotaRecebimentoProvisorio> getNotasRecebimento() {
		return notasRecebimento;
	}

	public void setNotasRecebimento(List<NotaRecebimentoProvisorio> notasRecebimento) {
		this.notasRecebimento = notasRecebimento;
	}

	public Boolean getMostrarAlerta() {
		return mostrarAlerta;
	}

	public void setMostrarAlerta(Boolean mostrarAlerta) {
		this.mostrarAlerta = mostrarAlerta;
	}

	/** Nota de Recebimento */
	public static class NotaRecebimentoProvisorio {
		/** ID */
		private Integer id;
		
		/** Data de Geração */
		private Date dataGeracao;
		
		private Boolean indEstorno;
		
		private SceDocumentoFiscalEntrada documentoFiscalEntrada;
		
		private ScoFornecedor fornecedor;
		
		// Getters/Setters

		public Integer getId() {
			return id;
		}

		public void setId(Integer id) {
			this.id = id;
		}

		public Date getDataGeracao() {
			return dataGeracao;
		}

		public void setDataGeracao(Date dataGeracao) {
			this.dataGeracao = dataGeracao;
		}
		
		public Boolean getIndEstorno() {
			return indEstorno;
		}

		public void setIndEstorno(Boolean indEstorno) {
			this.indEstorno = indEstorno;
		}
		
		public ScoFornecedor getFornecedor() {
			return fornecedor;
		}

		public void setFornecedor(ScoFornecedor fornecedor) {
			this.fornecedor = fornecedor;
		}

		public SceDocumentoFiscalEntrada getDocumentoFiscalEntrada() {
			return documentoFiscalEntrada;
		}

		public void setDocumentoFiscalEntrada(
				SceDocumentoFiscalEntrada documentoFiscalEntrada) {
			this.documentoFiscalEntrada = documentoFiscalEntrada;
		}
		
		
		/** Campos */
		public static enum Field {
			ID("id"), DATA_GERACAO("dataGeracao"), IND_ESTORNO("indEstorno"), DOCUMENTO_FISCAL_ENTRADA("documentoFiscalEntrada"), FORNECEDOR("fornecedor");
			
			private String field;
			
			Field(String field) {
				this.field = field;
			}
			
			@Override
			public String toString() {
				return field;
			}
		}
	}
}