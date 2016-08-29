package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.ArrayList;
import java.util.List;


/**
 * Itens de recebimento com valor excedido a serem confirmado.
 * 
 * @author matheus
 */
public class ItemRecebimentoValorExcedido extends Exception {
	private static final long serialVersionUID = 6512572471860940758L;
	
	/** Itens Excedidos */
	private List<String> excedidos = new ArrayList<String>();
	
	/**
	 * Constrói objeto sem status.
	 */
	public ItemRecebimentoValorExcedido() {
	}
	
	/**
	 * Constrói objeto a partir do status de um item de recebimento.
	 * 
	 * @param itemStatus Status
	 */
	public ItemRecebimentoValorExcedido(String itemStatus) {
		excedidos.add(itemStatus);
	}
	
	/**
	 * Complementa status.
	 * 
	 * @param status Status Complementar
	 */
	public void add(ItemRecebimentoValorExcedido status) {
		excedidos.addAll(status.getExcedidos());
	}
	
	/**
	 * Lança status se não estiver vázio.
	 * 
	 * @throws ItemRecebimentoValorExcedido
	 */
	public void throwIfExcedeu() throws ItemRecebimentoValorExcedido {
		if (!excedidos.isEmpty()) {
			throw this;
		}
	}

	/** Obtem status dos itens. */
	public List<String> getExcedidos() {
		return excedidos;
	}
}