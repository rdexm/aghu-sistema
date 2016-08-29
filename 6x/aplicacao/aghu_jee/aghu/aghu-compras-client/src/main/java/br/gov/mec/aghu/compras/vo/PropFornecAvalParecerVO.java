package br.gov.mec.aghu.compras.vo;

import java.math.BigDecimal;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioMotivoDesclassificacaoItemProposta;
import br.gov.mec.aghu.core.commons.BaseBean;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class PropFornecAvalParecerVO implements BaseBean {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = -6419486566991736380L;
	
	
	private Integer numeroLicitacao;
	private Short numeroItemLicitacao;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private Long cgcFornecedor;
	private Long cpfFornecedor;
	private Boolean itemLicitacaoPropostaEscolhida;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private String descricaoMaterial;
	private String codigoUnidadeMedida;
	private Long qtdeAprovadaSC;
	private BigDecimal valorUnitarioItemPropostaFornecedor;
	private Integer fatorConversaoItemPropostaFornecedor;
	private Integer codigoMarcaItemPropostaFornecedor;
	private String descricaoMarcaItemPropostaFornecedor;
	private Integer codigoModeloItemPropostaFornecedor;
	private String descricaoModeloItemPropostaFornecedor;
	private Date dtEntregaAmostraItemPropostaFornecedor;
	private Integer pfrLctNumeroItemProposta;
	private Integer pfrFrnNumeroItemPropostaFornecedor;
	private Short numeroItemPropostaFornecedor;	
	private Boolean indAutorizUsrItemPropostaFornecedor;
	private String justifAutorizUsrItemPropostaFornecedor;
	private Boolean indDesclassificadoItemPropostaFornecedor;
	private DominioMotivoDesclassificacaoItemProposta motDesclassifItemPropostaFornecedor;
	private String situacaoParecerDescricao;
	private Integer codigoParecerMaterial;
    
			
	
	public Integer getNumeroLicitacao() {
		return numeroLicitacao;
	}




	public void setNumeroLicitacao(Integer numeroLicitacao) {
		this.numeroLicitacao = numeroLicitacao;
	}




	public Short getNumeroItemLicitacao() {
		return numeroItemLicitacao;
	}




	public void setNumeroItemLicitacao(Short numeroItemLicitacao) {
		this.numeroItemLicitacao = numeroItemLicitacao;
	}




	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}




	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}




	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}




	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}




	public Long getCgcFornecedor() {
		return cgcFornecedor;
	}




	public void setCgcFornecedor(Long cgcFornecedor) {
		this.cgcFornecedor = cgcFornecedor;
	}




	public Long getCpfFornecedor() {
		return cpfFornecedor;
	}




	public void setCpfFornecedor(Long cpfFornecedor) {
		this.cpfFornecedor = cpfFornecedor;
	}




	public Boolean getItemLicitacaoPropostaEscolhida() {
		return itemLicitacaoPropostaEscolhida;
	}




	public void setItemLicitacaoPropostaEscolhida(
			Boolean itemLicitacaoPropostaEscolhida) {
		this.itemLicitacaoPropostaEscolhida = itemLicitacaoPropostaEscolhida;
	}




	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}




	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}




	public String getNomeMaterial() {
		return nomeMaterial;
	}




	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}




	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}




	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}




	public String getCodigoUnidadeMedida() {
		return codigoUnidadeMedida;
	}




	public void setCodigoUnidadeMedida(String codigoUnidadeMedida) {
		this.codigoUnidadeMedida = codigoUnidadeMedida;
	}




	public Long getQtdeAprovadaSC() {
		return qtdeAprovadaSC;
	}




	public void setQtdeAprovadaSC(Long qtdeAprovadaSC) {
		this.qtdeAprovadaSC = qtdeAprovadaSC;
	}




	public BigDecimal getValorUnitarioItemPropostaFornecedor() {
		return valorUnitarioItemPropostaFornecedor;
	}




	public void setValorUnitarioItemPropostaFornecedor(
			BigDecimal valorUnitarioItemPropostaFornecedor) {
		this.valorUnitarioItemPropostaFornecedor = valorUnitarioItemPropostaFornecedor;
	}




	public Integer getFatorConversaoItemPropostaFornecedor() {
		return fatorConversaoItemPropostaFornecedor;
	}




	public void setFatorConversaoItemPropostaFornecedor(
			Integer fatorConversaoItemPropostaFornecedor) {
		this.fatorConversaoItemPropostaFornecedor = fatorConversaoItemPropostaFornecedor;
	}




	public Integer getCodigoMarcaItemPropostaFornecedor() {
		return codigoMarcaItemPropostaFornecedor;
	}




	public void setCodigoMarcaItemPropostaFornecedor(
			Integer codigoMarcaItemPropostaFornecedor) {
		this.codigoMarcaItemPropostaFornecedor = codigoMarcaItemPropostaFornecedor;
	}




	public String getDescricaoMarcaItemPropostaFornecedor() {
		return descricaoMarcaItemPropostaFornecedor;
	}




	public void setDescricaoMarcaItemPropostaFornecedor(
			String descricaoMarcaItemPropostaFornecedor) {
		this.descricaoMarcaItemPropostaFornecedor = descricaoMarcaItemPropostaFornecedor;
	}




	public Integer getCodigoModeloItemPropostaFornecedor() {
		return codigoModeloItemPropostaFornecedor;
	}




	public void setCodigoModeloItemPropostaFornecedor(
			Integer codigoModeloItemPropostaFornecedor) {
		this.codigoModeloItemPropostaFornecedor = codigoModeloItemPropostaFornecedor;
	}




	public String getDescricaoModeloItemPropostaFornecedor() {
		return descricaoModeloItemPropostaFornecedor;
	}




	public void setDescricaoModeloItemPropostaFornecedor(
			String descricaoModeloItemPropostaFornecedor) {
		this.descricaoModeloItemPropostaFornecedor = descricaoModeloItemPropostaFornecedor;
	}




	public Date getDtEntregaAmostraItemPropostaFornecedor() {
		return dtEntregaAmostraItemPropostaFornecedor;
	}




	public void setDtEntregaAmostraItemPropostaFornecedor(
			Date dtEntregaAmostraItemPropostaFornecedor) {
		this.dtEntregaAmostraItemPropostaFornecedor = dtEntregaAmostraItemPropostaFornecedor;
	}	

	public Integer getPfrLctNumeroItemProposta() {
		return pfrLctNumeroItemProposta;
	}




	public void setPfrLctNumeroItemProposta(Integer pfrLctNumeroItemProposta) {
		this.pfrLctNumeroItemProposta = pfrLctNumeroItemProposta;
	}




	public Integer getPfrFrnNumeroItemPropostaFornecedor() {
		return pfrFrnNumeroItemPropostaFornecedor;
	}




	public void setPfrFrnNumeroItemPropostaFornecedor(
			Integer pfrFrnNumeroItemPropostaFornecedor) {
		this.pfrFrnNumeroItemPropostaFornecedor = pfrFrnNumeroItemPropostaFornecedor;
	}




	public Short getNumeroItemPropostaFornecedor() {
		return numeroItemPropostaFornecedor;
	}




	public void setNumeroItemPropostaFornecedor(Short numeroItemPropostaFornecedor) {
		this.numeroItemPropostaFornecedor = numeroItemPropostaFornecedor;
	}




	public Boolean getIndAutorizUsrItemPropostaFornecedor() {
		return indAutorizUsrItemPropostaFornecedor;
	}




	public void setIndAutorizUsrItemPropostaFornecedor(
			Boolean indAutorizUsrItemPropostaFornecedor) {
		this.indAutorizUsrItemPropostaFornecedor = indAutorizUsrItemPropostaFornecedor;
	}




	public String getJustifAutorizUsrItemPropostaFornecedor() {
		return justifAutorizUsrItemPropostaFornecedor;
	}




	public void setJustifAutorizUsrItemPropostaFornecedor(
			String justifAutorizUsrItemPropostaFornecedor) {
		this.justifAutorizUsrItemPropostaFornecedor = justifAutorizUsrItemPropostaFornecedor;
	}




	public Boolean getIndDesclassificadoItemPropostaFornecedor() {
		return indDesclassificadoItemPropostaFornecedor;
	}




	public void setIndDesclassificadoItemPropostaFornecedor(
			Boolean indDesclassificadoItemPropostaFornecedor) {
		this.indDesclassificadoItemPropostaFornecedor = indDesclassificadoItemPropostaFornecedor;
	}




	public DominioMotivoDesclassificacaoItemProposta getMotDesclassifItemPropostaFornecedor() {
		return motDesclassifItemPropostaFornecedor;
	}




	public void setMotDesclassifItemPropostaFornecedor(
			DominioMotivoDesclassificacaoItemProposta motDesclassifItemPropostaFornecedor) {
		this.motDesclassifItemPropostaFornecedor = motDesclassifItemPropostaFornecedor;
	}




	public String getSituacaoParecerDescricao() {
		return situacaoParecerDescricao;
	}




	public void setSituacaoParecerDescricao(String situacaoParecerDescricao) {
		this.situacaoParecerDescricao = situacaoParecerDescricao;
	}

	public Integer getCodigoParecerMaterial() {
		return codigoParecerMaterial;
	}




	public void setCodigoParecerMaterial(Integer codigoParecerMaterial) {
		this.codigoParecerMaterial = codigoParecerMaterial;
	}




	private String formatCpf(Long cpf) {
		StringBuilder cpfSb = new StringBuilder(String.format("%011d", cpf));
		
		cpfSb.insert(3, '.');
		cpfSb.insert(7, '.');
		cpfSb.insert(11, '-');
		
		return cpfSb.toString();
	}
	
	public String getCnpjCpfFormatado() {
		return this.getCgcFornecedor() == null ? formatCpf(this.getCpfFornecedor()) : CoreUtil.formatarCNPJ(this.getCgcFornecedor());
	}



	public enum Fields {
		NUMERO_LICITACAO("numeroLicitacao"),
		NUMERO_ITEM_LICITACAO("numeroItemLicitacao"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor"),
		CGC_FORNECEDOR("cgcFornecedor"),
		CPF_FORNECEDOR("cpfFornecedor"),
		PROPOSTA_ESCOLHIDA_ITEM_LICITACAO("itemLicitacaoPropostaEscolhida"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		CODIGO_UNIDADE_MEDIDA_MATERIAL("codigoUnidadeMedida"),
		QTDE_APROVADA_SC("qtdeAprovadaSC"),
		VALOR_UNITARIO_ITEM_PROPOS_FORNEC("valorUnitarioItemPropostaFornecedor"),
		FATOR_CONVERSAO_ITEM_PROPOS_FORNEC("fatorConversaoItemPropostaFornecedor"),
		COD_MARCA_ITEM_PROPOS_FORNEC("codigoMarcaItemPropostaFornecedor"),
		DESCR_MARCA_ITEM_PROPOS_FORNEC("descricaoMarcaItemPropostaFornecedor"),
		COD_MODELO_ITEM_PROPOS_FORNEC("codigoModeloItemPropostaFornecedor"),
		DESCR_MODELO_ITEM_PROPOS_FORNEC("descricaoModeloItemPropostaFornecedor"),
		DT_ENTREGA_AMOSTRA("dtEntregaAmostraItemPropostaFornecedor"),
		NUMERO_LICITACAO_ITEM_PROPOS_FORNEC("pfrLctNumeroItemProposta"),
		FORNEC_NUMERO_ITEM_PROPOS_FORNEC("pfrFrnNumeroItemPropostaFornecedor"),
		NUMERO_ITEM_PROPOS_FORNEC("numeroItemPropostaFornecedor"),
		IND_AUT_USR_ITEM_PROPOS_FORNEC("indAutorizUsrItemPropostaFornecedor"),
		JUSR_AUT_USR_ITEM_PROPOS_FORNEC("justifAutorizUsrItemPropostaFornecedor"),
		IND_DESCLASSIF_ITEM_PROPOS_FORNEC("indDesclassificadoItemPropostaFornecedor"),
		MOT_DESCLASSIF_ITEM_PROPOS_FORNEC ("motDesclassifItemPropostaFornecedor");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}	

	
			
}
