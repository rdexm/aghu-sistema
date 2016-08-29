package br.gov.mec.aghu.patrimonio.vo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

public class AceiteTecnicoParaSerRealizadoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5600207631143863591L;


	private Integer recebimento;
	private Integer esl;
	private Integer itemRecebimento;
	private Integer af;
	private Short complemento;
	private Integer nroSolicCompras;
	private Integer codigo;
	private String material;
	private Integer areaTecAvaliacao;
	private Integer centroCustoAutTec;
	private Integer tecnicoResponsavel;
	private Short codigoTecnicoResponsavel;
	private Integer status;
	private boolean selecionado;
	private Long notaFiscal;
	private Integer quantidade;
	private Long quantidadeDisponivel;
	private Long quantidadeRetirada;
	private boolean inst;
	private Long seqItemPatrimonio;
	private Integer centroCusto;
	private List<DetalhamentoRetiradaBemPermanenteVO> itens = new ArrayList<DetalhamentoRetiradaBemPermanenteVO>();
	private String fornecedor;
	private Long cgc;
	private AceiteTecnicoParaSerRealizadoVO aceiteVO;
	private String nomeTecnicoResp;
	private String nomeCentroCusto;
	private String nomeArea;
	private Integer tecnicoResponsavelItem;
	private Short codigoTecnicoResponsavelItem;
	
	public AceiteTecnicoParaSerRealizadoVO(){
		
	};
	
	public AceiteTecnicoParaSerRealizadoVO(Integer recebimento, Integer esl,
			Integer itemRecebimento, Integer af, Short complemento,
			Integer nroSolicCompras, Integer codigo, String material,
			Integer areaTecAvaliacao, Integer tecnicoResponsavel,
			Short codigoTecnicoResponsavel, Long notaFiscal,
			Integer quantidade, Long seqItemPatrimonio, Integer centroCusto,
			String fornecedor) {
		super();
		this.recebimento = recebimento;
		this.esl = esl;
		this.itemRecebimento = itemRecebimento;
		this.af = af;
		this.complemento = complemento;
		this.nroSolicCompras = nroSolicCompras;
		this.codigo = codigo;
		this.material = material;
		this.areaTecAvaliacao = areaTecAvaliacao;
		this.tecnicoResponsavel = tecnicoResponsavel;
		this.codigoTecnicoResponsavel = codigoTecnicoResponsavel;
		this.notaFiscal = notaFiscal;
		this.quantidade = quantidade;
		this.seqItemPatrimonio = seqItemPatrimonio;
		this.centroCusto = centroCusto;
		this.fornecedor = fornecedor;		
	}
	
	public enum Fields {
		
		RECEBIMENTO("recebimento"),
		ESL("esl"),
		ITEM_RECEBIMENTO("itemRecebimento"),
		AF("af"),
		COMPLEMENTO("complemento"),
		NRO_SOLIC_COMPRAS("nroSolicCompras"),
		CODIGO("codigo"),
		MATERIAL("material"),
		AREA_TEC_AVALIACAO("areaTecAvaliacao"),
		CENTRO_CUSTO_AUT_TEC("centroCustoAutTec"),
		TECNICO_RESPONSAVEL("tecnicoResponsavel"),
		CODIGO_TECNICO_RESPONSAVEL("codigoTecnicoResponsavel"),
		STATUS("status"),
		SELECIONADO("selecionado"),
		NOTA_FISCAL("notaFiscal"),
		QUANTIDADE("quantidade"),
		QUANTIDADE_DISPONIVEL("quantidadeDisponivel"),
		QUANTIDADE_RETIRADA("quantidadeRetirada"),
		INST("inst"),
		SEQ_ITEM_PATRIMONIO("seqItemPatrimonio"),
		CENTRO_CUSTO("centroCusto"),
		ITENS("itens"),
		FORNECEDOR("fornecedor"),
		CGC("cgc"),
		TECNICO_RESPONSAVEL_ITEM("tecnicoResponsavelItem"),
		CODIGO_TECNICO_RESPONSAVEL_ITEM("codigoTecnicoResponsavelItem");
		
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
	 * Formata o numero Af e o complemento Af para o formato desejado.
	 */
	public String obterAfComplemento() {
		
		String afComplemento = "";
		
		if (af != null && complemento != null) {
			afComplemento = Integer.toString(af) + "/" + Short.toString(complemento);
		} else if (af != null && complemento == null) {
			afComplemento = Integer.toString(af);
		} else if (af == null && complemento != null) {
			afComplemento = Short.toString(complemento);
		}
		return afComplemento;
	}
	
	/**
	 * Formata o Código e a Descrição do Material para o formato desejado. 
	 */
	public String obterCodigoMaterial(Integer tamanho) {
		String codigoMaterial = "";
		
		if (codigo != null && material != null) {
			codigoMaterial = codigo.toString() + "/" + material;
		} else if (codigo != null && material == null) {
			codigoMaterial = codigo.toString();
		} else if (codigo == null && material != null) {
			codigoMaterial = material;
		}
		if (codigoMaterial.length() > tamanho) {
			codigoMaterial = StringUtils.abbreviate(codigoMaterial, tamanho);
		}
		return codigoMaterial;
	}

	public Integer getRecebimento() {
		return recebimento;
	}

	public void setRecebimento(Integer recebimento) {
		this.recebimento = recebimento;
	}

	public Integer getEsl() {
		return esl;
	}

	public void setEsl(Integer esl) {
		this.esl = esl;
	}

	public Integer getItemRecebimento() {
		return itemRecebimento;
	}

	public void setItemRecebimento(Integer itemRecebimento) {
		this.itemRecebimento = itemRecebimento;
	}

	public Integer getAf() {
		return af;
	}

	public void setAf(Integer af) {
		this.af = af;
	}

	public Short getComplemento() {
		return complemento;
	}

	public void setComplemento(Short complemento) {
		this.complemento = complemento;
	}

	public Integer getNroSolicCompras() {
		return nroSolicCompras;
	}

	public void setNroSolicCompras(Integer nroSolicCompras) {
		this.nroSolicCompras = nroSolicCompras;
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

	public Integer getAreaTecAvaliacao() {
		return areaTecAvaliacao;
	}

	public void setAreaTecAvaliacao(Integer areaTecAvaliacao) {
		this.areaTecAvaliacao = areaTecAvaliacao;
	}

	public Integer getCentroCustoAutTec() {
		return centroCustoAutTec;
	}

	public void setCentroCustoAutTec(Integer centroCustoAutTec) {
		this.centroCustoAutTec = centroCustoAutTec;
	}

	public Integer getTecnicoResponsavel() {
		return tecnicoResponsavel;
	}

	public void setTecnicoResponsavel(Integer tecnicoResponsavel) {
		this.tecnicoResponsavel = tecnicoResponsavel;
	}

	public Short getCodigoTecnicoResponsavel() {
		return codigoTecnicoResponsavel;
	}

	public void setCodigoTecnicoResponsavel(Short codigoTecnicoResponsavel) {
		this.codigoTecnicoResponsavel = codigoTecnicoResponsavel;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public boolean isSelecionado() {
		return selecionado;
	}

	public void setSelecionado(boolean selecionado) {
		this.selecionado = selecionado;
	}

	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public Long getQuantidadeDisponivel() {
		return quantidadeDisponivel;
	}

	public void setQuantidadeDisponivel(Long quantidadeDisponivel) {
		this.quantidadeDisponivel = quantidadeDisponivel;
	}

	public Long getQuantidadeRetirada() {
		return quantidadeRetirada;
	}

	public void setQuantidadeRetirada(Long quantidadeRetirada) {
		this.quantidadeRetirada = quantidadeRetirada;
	}

	public boolean isInst() {
		return inst;
	}

	public void setInst(boolean inst) {
		this.inst = inst;
	}

	public Long getSeqItemPatrimonio() {
		return seqItemPatrimonio;
	}

	public void setSeqItemPatrimonio(Long seqItemPatrimonio) {
		this.seqItemPatrimonio = seqItemPatrimonio;
	}

	public Integer getCentroCusto() {
		return centroCusto;
	}

	public void setCentroCusto(Integer centroCusto) {
		this.centroCusto = centroCusto;
	}

	@Override
	public int hashCode() {
		HashCodeBuilder umHashCodeBuilder = new HashCodeBuilder();
        umHashCodeBuilder.append(this.getAf());
        umHashCodeBuilder.append(this.getAreaTecAvaliacao());
        umHashCodeBuilder.append(this.getCentroCustoAutTec());
        umHashCodeBuilder.append(this.getCodigo());
        umHashCodeBuilder.append(this.getCodigoTecnicoResponsavel());
        umHashCodeBuilder.append(this.getComplemento());
        umHashCodeBuilder.append(this.getEsl());
        umHashCodeBuilder.append(this.getItemRecebimento());
        umHashCodeBuilder.append(this.getMaterial());
        umHashCodeBuilder.append(this.getNroSolicCompras());
        umHashCodeBuilder.append(this.getRecebimento());
        umHashCodeBuilder.append(this.getStatus());
        umHashCodeBuilder.append(this.getTecnicoResponsavel());
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
		AceiteTecnicoParaSerRealizadoVO other = (AceiteTecnicoParaSerRealizadoVO) obj;
		EqualsBuilder umEqualsBuilder = new EqualsBuilder();
        umEqualsBuilder.append(this.getAf(), other.getAf());
        umEqualsBuilder.append(this.getAreaTecAvaliacao(), other.getAreaTecAvaliacao());
        umEqualsBuilder.append(this.getCentroCustoAutTec(), other.getCentroCustoAutTec());
        umEqualsBuilder.append(this.getCodigo(), other.getCodigo());
        umEqualsBuilder.append(this.getCodigoTecnicoResponsavel(), other.getCodigoTecnicoResponsavel());
        umEqualsBuilder.append(this.getComplemento(), other.getComplemento());
        umEqualsBuilder.append(this.getEsl(), other.getEsl());
        umEqualsBuilder.append(this.getItemRecebimento(), other.getItemRecebimento());
        umEqualsBuilder.append(this.getMaterial(), other.getMaterial());
        umEqualsBuilder.append(this.getNroSolicCompras(), other.getNroSolicCompras());
        umEqualsBuilder.append(this.getRecebimento(), other.getRecebimento());
        umEqualsBuilder.append(this.getStatus(), other.getStatus());
        umEqualsBuilder.append(this.getTecnicoResponsavel(), other.getTecnicoResponsavel());
        return umEqualsBuilder.isEquals();
	}

	public List<DetalhamentoRetiradaBemPermanenteVO> getItens() {
		return itens;
	}

	public void setItens(
			List<DetalhamentoRetiradaBemPermanenteVO> itens) {
		this.itens = itens;
	}

	public String getRecebItem() {
		return obterRecebItem();
	}

	public String getAfComplemento() {
		return obterAfComplemento();
	}

	public String getFornecedor() {
		return fornecedor;
	}

	public void setFornecedor(String fornecedor) {
		this.fornecedor = fornecedor;
	}

	public AceiteTecnicoParaSerRealizadoVO getAceiteVO() {
		return aceiteVO;
	}

	public void setAceiteVO(AceiteTecnicoParaSerRealizadoVO aceiteVO) {
		this.aceiteVO = aceiteVO;
	}

	public String getNomeTecnicoResp() {
		return nomeTecnicoResp;
	}

	public void setNomeTecnicoResp(String nomeTecnicoResp) {
		this.nomeTecnicoResp = nomeTecnicoResp;
	}

	public String getNomeCentroCusto() {
		return nomeCentroCusto;
	}

	public void setNomeCentroCusto(String nomeCentroCusto) {
		this.nomeCentroCusto = nomeCentroCusto;
	}

	public String getNomeArea() {
		return nomeArea;
	}

	public void setNomeArea(String nomeArea) {
		this.nomeArea = nomeArea;
	}

	public Long getCgc() {
		return cgc;
	}

	public void setCgc(Long cgc) {
		this.cgc = cgc;
	}

	public Integer getTecnicoResponsavelItem() {
		return tecnicoResponsavelItem;
	}

	public void setTecnicoResponsavelItem(Integer tecnicoResponsavelItem) {
		this.tecnicoResponsavelItem = tecnicoResponsavelItem;
	}

	public Short getCodigoTecnicoResponsavelItem() {
		return codigoTecnicoResponsavelItem;
	}

	public void setCodigoTecnicoResponsavelItem(
			Short codigoTecnicoResponsavelItem) {
		this.codigoTecnicoResponsavelItem = codigoTecnicoResponsavelItem;
	}
	
}
