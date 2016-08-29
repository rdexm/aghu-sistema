package br.gov.mec.aghu.model;

import java.io.Serializable;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinColumns;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.hibernate.annotations.Immutable;

import br.gov.mec.aghu.core.persistence.BaseEntityId;


/**
 * Entidade que representa a view VSCEINRIAF
 * 
 */
@Entity
@Table(name="V_SCE_INR_IAF")
@Immutable
public class VSceItemNotaRecebimentoAutorizacaoFornecimento extends BaseEntityId<VSceItemNotaRecebimentoAutorizacaoFornecimentoId> implements Serializable {	
	/**
	 * 
	 */
	private static final long serialVersionUID = -4641645957158754250L;

	private VSceItemNotaRecebimentoAutorizacaoFornecimentoId id;
	
	private ScoItemAutorizacaoForn itemAutorizacaoFornecimento;
	
    public VSceItemNotaRecebimentoAutorizacaoFornecimento() {
    }

    @EmbeddedId
	public VSceItemNotaRecebimentoAutorizacaoFornecimentoId getId() {
		return id;
	}

	public void setId(VSceItemNotaRecebimentoAutorizacaoFornecimentoId id) {
		this.id = id;
	}
	
	
	 @ManyToOne
	@JoinColumns({
		@JoinColumn(name="AFN_NUMERO", referencedColumnName="AFN_NUMERO", insertable=false, updatable=false),
		@JoinColumn(name="NUMERO", referencedColumnName="NUMERO", insertable=false, updatable=false)
		})
	public ScoItemAutorizacaoForn getItemAutorizacaoFornecimento() {
		return itemAutorizacaoFornecimento;
	}

	public void setItemAutorizacaoFornecimento(
			ScoItemAutorizacaoForn itemAutorizacaoFornecimento) {
		this.itemAutorizacaoFornecimento = itemAutorizacaoFornecimento;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj){
			return true;
		}
		if (obj == null){
			return false;
		}
		if (getClass() != obj.getClass()){
			return false;
		}
		VSceItemNotaRecebimentoAutorizacaoFornecimento other = (VSceItemNotaRecebimentoAutorizacaoFornecimento) obj;
		if (id == null) {
			if (other.id != null){
				return false;
			}
		} else if (!id.equals(other.id)){
			return false;
		}
		return true;
	}	
	
	
	/**
	 *     
	 * @ORADB: Function SCEK_INR_RN.INRC_VER_VAL_LIQ_IAF
	 */
	@Transient
	public Double calcularCustoUnitarioLiquido(Integer qtdeReceber){
		Double valorBruto, 
			   valorLiquido, 
			   valorDescontoItem = 0.0,
			   valorDesconto, 
			   valorAcrescimoItem, 
			   valorAcrescimo, 
			   valorIpi, 
			   custoUnitLiq = 0.0;
		if (getId() != null && getId().getValorUnitarioItemAutorizacaoFornecimento() != null ){
			valorBruto = (qtdeReceber * getId().getValorUnitarioItemAutorizacaoFornecimento());
			valorLiquido = valorBruto;
			if(getId().getPercDescontoItem() != null && getId().getPercDescontoItem() > 0){
				valorDescontoItem = (valorBruto*(valorDescontoItem / 100));
				valorLiquido = (valorLiquido - valorDescontoItem);
			}
			if (getId().getPercDesconto() != null && getId().getPercDesconto() > 0){
				valorDesconto = (valorBruto*(getId().getPercDesconto() / 100));
				valorLiquido = (valorLiquido - valorDesconto);
			}
			if (getId().getPercAcrescimoItem() != null && getId().getPercAcrescimoItem() > 0){
				valorAcrescimoItem = (valorBruto*(getId().getPercAcrescimoItem() / 100));
				valorLiquido = (valorLiquido + valorAcrescimoItem);
			}
			if (getId().getPercAcrescimo() != null && getId().getPercAcrescimo() > 0){
				valorAcrescimo = (valorBruto*(getId().getPercAcrescimo() / 100));
				valorLiquido = (valorLiquido + valorAcrescimo);
			}
			if (getId().getPercIpi() != null && getId().getPercIpi() > 0){
				valorIpi = (valorLiquido * (getId().getPercIpi() / 100));
				valorLiquido = (valorLiquido + valorIpi);
			}
			if (qtdeReceber == 0){
				qtdeReceber = 1;
			}
			custoUnitLiq =(valorLiquido / qtdeReceber );
		}
		return custoUnitLiq;
	}
	
	public enum Fields {
		ITEM_AUTORIZACAO_FORNECIMENTO("itemAutorizacaoFornecimento"),
		PERCENTUAL_DESCONTO("id.percDesconto"),
		PERCENTUAL_DESCONTO_ITEM("id.percDescontoItem"),
		PERCENTUAL_ACRESCIMO("id.percAcrescimo"),
		PERCENTUAL_ACRESCIMO_ITEM("id.percAcrescimoItem"),
		PERCENTUAL_IPI("id.percIpi"),
		VALOR_UNITARIO_ITEM_AUTORIZACAO_FORN("id.valorUnitarioItemAutorizacaoFornecimento"), 
		NUMERO_ITEM_LICITACAO("id.numeroItemLicitacao"), 
		TIPO("id.tipo"), 
		NOME_MATERIAL_SERVICO("id.nome"), 
		NOME_MARCA_COMERCIAL("id.marcaNome"),
		QUANTIDADE_RECEBIDA("id.qtdeRecebida"),
		AFN_NUMERO("id.numeroAutorizacaoFornecimento"),
		NUMERO("id.numero"),
		CODIGO_MARCA("id.codigoMarca"),
		SITUACAO("id.situacao");
		;
		
		private String field;

		private Fields(String field) {
			this.field = field;
		}

		@Override
		public String toString() {
			return this.field;
		}
	}
}