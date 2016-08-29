package br.gov.mec.aghu.prescricaomedica.modelobasico.vo;

import java.text.Collator;
import java.util.Locale;

public class ItensModeloBasicoVO implements Comparable<ItensModeloBasicoVO> {
	private Tipo tipo;
	private Integer modeloBasicoPrescricaoSeq; // Seq do modelo basico
	private Integer itemSeq; // Seq do item de um modelo básico
	private String descricao;
	private Boolean itemEscolhidoCheckBox;

	public ItensModeloBasicoVO() {
		super();
	}

	public ItensModeloBasicoVO(Tipo tipo, String descricao) {
		super();
		this.tipo = tipo;
		this.descricao = descricao;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public Integer getModeloBasicoPrescricaoSeq() {
		return modeloBasicoPrescricaoSeq;
	}

	public void setModeloBasicoPrescricaoSeq(Integer modeloBasicoPrescricaoSeq) {
		this.modeloBasicoPrescricaoSeq = modeloBasicoPrescricaoSeq;
	}

	public Integer getItemSeq() {
		return itemSeq;
	}

	public void setItemSeq(Integer itemSeq) {
		this.itemSeq = itemSeq;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public void setItemEscolhidoCheckBox(Boolean itemEscolhidoCheckBox) {
		this.itemEscolhidoCheckBox = itemEscolhidoCheckBox;
	}

	public Boolean getItemEscolhidoCheckBox() {
		return itemEscolhidoCheckBox;
	}

	
	/**
	 * Representa o tipo do item.
	 * 
	 */
	public enum Tipo {
		/**
		 * 
		 */
		DIETA,
		/**
		 * 
		 */
		CUIDADO,
		/**
		 * 
		 */
		MEDICAMENTO,
		/**
		 * 
		 */
		SOLUCAO,
		/**
		 * 
		 */
		PROCEDIMENTO;
	}

	/**
	 * Compara este objeto com o objeto fornecido para ordenação.<br>
	 * A ordem natural implementada por este método é tipo(ordem natural do
	 * enum) e descrição.
	 */
	@Override
	public int compareTo(ItensModeloBasicoVO other) {
		int result = this.tipo.compareTo(other.getTipo());
		if (result == 0) {
			if(this.getDescricao() != null && other.getDescricao() != null){
				 final Collator collator = Collator.getInstance(new Locale("pt", "BR"));
                 collator.setStrength(Collator.PRIMARY);
                 result = collator.compare(this.getDescricao(), other.getDescricao());
//               result = this.getDescricao().compareTo(other.getDescricao());

			}				
		}
		return result;
	}

}
