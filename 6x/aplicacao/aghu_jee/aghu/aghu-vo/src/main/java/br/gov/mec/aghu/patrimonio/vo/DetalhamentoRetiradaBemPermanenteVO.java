package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;

import org.apache.commons.lang3.StringUtils;

public class DetalhamentoRetiradaBemPermanenteVO implements Serializable {

	private static final long serialVersionUID = 8287799852600414713L;

	private Integer recebimento;
	private Integer itemRecebimento;
	private Integer esl;
	private Integer codigo;
	private String material;
	private String numeroSerie;
	private Long numeroBem;
	private AceiteTecnicoParaSerRealizadoVO aceiteVO;
	
	public enum Fields {
		
		RECEBIMENTO("recebimento"),
		ITEM_RECEBIMENTO("itemRecebimento"),
		ESL("esl"),
		CODIGO("codigo"),
		MATERIAL("material"),
		ACEITE_VO("aceiteVO"),
		NUMERO_SERIE("numeroSerie"),
		NUMERO_BEM("numeroBem");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}
	
	/**
	 * Formata o numero Recebimento e o Item Recebimento para o formato desejado.
	 */
	public String obterRecebItem() {

		String recebItem = StringUtils.EMPTY;

		if (recebimento != null && itemRecebimento != null) {
			recebItem = recebimento.toString() + "/" + itemRecebimento.toString();
		} else if (recebimento != null && itemRecebimento == null) {
			recebItem = recebimento.toString();
		} else if (recebimento == null && itemRecebimento != null) {
			recebItem = itemRecebimento.toString();
		}
		return recebItem;
	}
	
	/**
	 * Formata o Código e a Descrição do Material para o formato desejado. 
	 */
	public String obterCodigoMaterial() {
		String codigoMaterial = "";
		
		if (codigo != null && material != null) {
			codigoMaterial = codigo.toString() + "/" + material;
		} else if (codigo != null && material == null) {
			codigoMaterial = codigo.toString();
		} else if (codigo == null && material != null) {
			codigoMaterial = material;
		}

		return codigoMaterial;
	}

	public Integer getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(Integer recebimento) {
		this.recebimento = recebimento;
	}

	public Integer getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public Integer getEsl() {
		return esl;
	}

	public void setEsl(Integer esl) {
		this.esl = esl;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}

	public String getMaterial() {
		return material;
	}

	public void setMaterial(String material) {
		this.material = material;
	}

	public AceiteTecnicoParaSerRealizadoVO getAceiteVO() {
		return aceiteVO;
	}

	public void setAceiteVO(AceiteTecnicoParaSerRealizadoVO aceiteVO) {
		this.aceiteVO = aceiteVO;
	}

	public String getNumeroSerie() {
		return numeroSerie;
	}

	public void setNumeroSerie(String numeroSerie) {
		this.numeroSerie = numeroSerie;
	}

	public Long getNumeroBem() {
		return numeroBem;
	}

	public void setNumeroBem(Long numeroBem) {
		this.numeroBem = numeroBem;
	}
	
	public String getCodigoMaterial(){
		return obterCodigoMaterial();
	}
}
