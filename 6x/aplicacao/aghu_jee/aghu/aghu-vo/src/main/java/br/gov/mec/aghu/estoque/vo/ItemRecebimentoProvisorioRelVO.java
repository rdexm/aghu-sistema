package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioTipoDocumentoEntrada;
import br.gov.mec.aghu.core.commons.CoreUtil;

public class ItemRecebimentoProvisorioRelVO implements Serializable {

	private static final String HIFEN_ESPACADO = " - ";

	private static final long serialVersionUID = 8919020356375252002L;

	private Integer seqNotaRecProv;
	private Integer dfeSeqNotaRecProv;
	private Integer numeroFornecedor;
	private Long    cgcFornecedor;
	private Long    cpfFornecedor;
	private String  razaoSocialFornecedor;
	private Date    dtGeracaoNotaRecProv;
	private Integer lctNumeroAutorizacaoForn;
	private Short   nroComplementoAutorizacaoForn;
	private Integer matriculaServidor;
	private String  nomePessoaFisica;
	private Long    numeroDocFiscalEntrada;
	private String  serieDocFiscalEntrada;
	private DominioTipoDocumentoEntrada tipoDocFiscalEntrada;
	private Double  valorTotalNfDocFiscalEntrada;
	private Date    dtEmissaoDocFiscalEntrada;
	private Date    dtEntradaDocFiscalEntrada;
	private Short   numeroItemLicItemProp;
	private Integer parcelaProgEntrItemRecProv;
	private Integer numeroAutorizacaoFornPedido;	
	private String  descricaoMarcaComercialItemPropForn;
	private String  codigoUnidadeMedItemAF;
	private Integer quantidadeItemRecProvisorio;
	private Double  valorItemRecProvisorio;
	private Integer codigoMaterial;
	private String  descricaoMaterial;
	private String  nomeMaterial;
	private Integer codigoServico;
	private String nomeServico;
	private String descricaoServico;
	
		
	public Integer getSeqNotaRecProv() {
		return seqNotaRecProv;
	}
	public void setSeqNotaRecProv(Integer seqNotaRecProv) {
		this.seqNotaRecProv = seqNotaRecProv;
	}
	
	public Integer getDfeSeqNotaRecProv() {
		return dfeSeqNotaRecProv;
	}
	public void setDfeSeqNotaRecProv(Integer dfeSeqNotaRecProv) {
		this.dfeSeqNotaRecProv = dfeSeqNotaRecProv;
	}
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
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
	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}
	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}
	public Date getDtGeracaoNotaRecProv() {
		return dtGeracaoNotaRecProv;
	}
	public void setDtGeracaoNotaRecProv(Date dtGeracaoNotaRecProv) {
		this.dtGeracaoNotaRecProv = dtGeracaoNotaRecProv;
	}
	public Integer getLctNumeroAutorizacaoForn() {
		return lctNumeroAutorizacaoForn;
	}
	public void setLctNumeroAutorizacaoForn(Integer lctNumeroAutorizacaoForn) {
		this.lctNumeroAutorizacaoForn = lctNumeroAutorizacaoForn;
	}
	public Short getNroComplementoAutorizacaoForn() {
		return nroComplementoAutorizacaoForn;
	}
	public void setNroComplementoAutorizacaoForn(Short nroComplementoAutorizacaoForn) {
		this.nroComplementoAutorizacaoForn = nroComplementoAutorizacaoForn;
	}
	public Integer getMatriculaServidor() {
		return matriculaServidor;
	}
	public void setMatriculaServidor(Integer matriculaServidor) {
		this.matriculaServidor = matriculaServidor;
	}
	public String getNomePessoaFisica() {
		return nomePessoaFisica;
	}
	public void setNomePessoaFisica(String nomePessoaFisica) {
		this.nomePessoaFisica = nomePessoaFisica;
	}
	public Long getNumeroDocFiscalEntrada() {
		return numeroDocFiscalEntrada;
	}
	public void setNumeroDocFiscalEntrada(Long numeroDocFiscalEntrada) {
		this.numeroDocFiscalEntrada = numeroDocFiscalEntrada;
	}
	public String getSerieDocFiscalEntrada() {
		return serieDocFiscalEntrada;
	}
	public void setSerieDocFiscalEntrada(String serieDocFiscalEntrada) {
		this.serieDocFiscalEntrada = serieDocFiscalEntrada;
	}
	public DominioTipoDocumentoEntrada getTipoDocFiscalEntrada() {
		return tipoDocFiscalEntrada;
	}
	public void setTipoDocFiscalEntrada(
			DominioTipoDocumentoEntrada tipoDocFiscalEntrada) {
		this.tipoDocFiscalEntrada = tipoDocFiscalEntrada;
	}
	public Double getValorTotalNfDocFiscalEntrada() {
		return valorTotalNfDocFiscalEntrada;
	}
	public void setValorTotalNfDocFiscalEntrada(Double valorTotalNfDocFiscalEntrada) {
		this.valorTotalNfDocFiscalEntrada = valorTotalNfDocFiscalEntrada;
	}
	public Date getDtEmissaoDocFiscalEntrada() {
		return dtEmissaoDocFiscalEntrada;
	}
	public void setDtEmissaoDocFiscalEntrada(Date dtEmissaoDocFiscalEntrada) {
		this.dtEmissaoDocFiscalEntrada = dtEmissaoDocFiscalEntrada;
	}
	public Date getDtEntradaDocFiscalEntrada() {
		return dtEntradaDocFiscalEntrada;
	}
	public void setDtEntradaDocFiscalEntrada(Date dtEntradaDocFiscalEntrada) {
		this.dtEntradaDocFiscalEntrada = dtEntradaDocFiscalEntrada;
	}
	public Short getNumeroItemLicItemProp() {
		return numeroItemLicItemProp;
	}
	public void setNumeroItemLicItemProp(Short numeroItemLicItemProp) {
		this.numeroItemLicItemProp = numeroItemLicItemProp;
	}
	public Integer getParcelaProgEntrItemRecProv() {
		return parcelaProgEntrItemRecProv;
	}
	public void setParcelaProgEntrItemRecProv(Integer parcelaProgEntrItemRecProv) {
		this.parcelaProgEntrItemRecProv = parcelaProgEntrItemRecProv;
	}
	public Integer getNumeroAutorizacaoFornPedido() {
		return numeroAutorizacaoFornPedido;
	}
	public void setNumeroAutorizacaoFornPedido(Integer numeroAutorizacaoFornPedido) {
		this.numeroAutorizacaoFornPedido = numeroAutorizacaoFornPedido;
	}
	public String getDescricaoMarcaComercialItemPropForn() {
		return descricaoMarcaComercialItemPropForn;
	}
	public void setDescricaoMarcaComercialItemPropForn(
			String descricaoMarcaComercialItemPropForn) {
		this.descricaoMarcaComercialItemPropForn = descricaoMarcaComercialItemPropForn;
	}
	public String getCodigoUnidadeMedItemAF() {
		return codigoUnidadeMedItemAF;
	}
	public void setCodigoUnidadeMedItemAF(String codigoUnidadeMedItemAF) {
		this.codigoUnidadeMedItemAF = codigoUnidadeMedItemAF;
	}
	public Integer getQuantidadeItemRecProvisorio() {
		return quantidadeItemRecProvisorio;
	}
	public void setQuantidadeItemRecProvisorio(Integer quantidadeItemRecProvisorio) {
		this.quantidadeItemRecProvisorio = quantidadeItemRecProvisorio;
	}
	public Double getValorItemRecProvisorio() {
		return valorItemRecProvisorio / this.getQuantidadeItemRecProvisorio();
	}
	public void setValorItemRecProvisorio(Double valorItemRecProvisorio) {
		this.valorItemRecProvisorio = valorItemRecProvisorio;
	}
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}
	public String getDescricaoMaterial() {
		return descricaoMaterial;
	}
	public void setDescricaoMaterial(String descricaoMaterial) {
		this.descricaoMaterial = descricaoMaterial;
	}
	public String getNomeMaterial() {
		return nomeMaterial;
	}
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}
	public Integer getCodigoServico() {
		return codigoServico;
	}
	public void setCodigoServico(Integer codigoServico) {
		this.codigoServico = codigoServico;
	}
	public String getNomeServico() {
		return nomeServico;
	}
	public void setNomeServico(String nomeServico) {
		this.nomeServico = nomeServico;
	}
	public String getDescricaoServico() {
		return descricaoServico;
	}
	public void setDescricaoServico(String descricaoServico) {
		this.descricaoServico = descricaoServico;
	}
	

	public enum Fields {
		
		SEQ_NOTA_RECEB_PROV("seqNotaRecProv"),
		DFE_SEQ_NOTA_RECEB_PROV("dfeSeqNotaRecProv"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		CGC_FORNECEDOR("cgcFornecedor"),
		CPF_FORNECEDOR("cpfFornecedor"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor"),
		DT_RECEBIMENTO_NOTA_REB_PROV("dtGeracaoNotaRecProv"),
		LCT_NUMERO_AF("lctNumeroAutorizacaoForn"),
		NUMERO_COMPLEMENTO_AF("nroComplementoAutorizacaoForn"),
		MATRICULA_SERVIDOR("matriculaServidor"),
		NOME_SERV_PESSOA_FISICA("nomePessoaFisica"),
		NUMERO_DOC_FISCAL_ENTRADA("numeroDocFiscalEntrada"),
		SERIE_DOC_FISCAL_ENTRADA("serieDocFiscalEntrada"),
		TIPO_DOC_FISCAL_ENTRADA("tipoDocFiscalEntrada"),
		VALOR_TOTAL_NF_DOC_FISCAL_ENTRADA("valorTotalNfDocFiscalEntrada"),
		DT_EMIS_DOC_FISCAL_ENTRADA("dtEmissaoDocFiscalEntrada"),
		DT_ENTR_DOC_FISCAL_ENTRADA("dtEntradaDocFiscalEntrada"),
		NUMERO_ITEM_LIC_IPF("numeroItemLicItemProp"),
		PARCELA_PEA_ITEM_NOTA_REC_PROV("parcelaProgEntrItemRecProv"),
		NUMERO_AF_PEDIDO("numeroAutorizacaoFornPedido"),
		DESCRICAO_MARCA_IPF("descricaoMarcaComercialItemPropForn"),
		CODIGO_UMD_IAF("codigoUnidadeMedItemAF"),
		QUANT_ITEM_NOTA_REC_PROV("quantidadeItemRecProvisorio"),
		VALOR_ITEM_NOTA_REC_PROV("valorItemRecProvisorio"),
		CODIGO_MATERIAL("codigoMaterial"),
		DESCRICAO_MATERIAL("descricaoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		CODIGO_SERVICO("codigoServico"),
		NOME_SERVICO("nomeServico"),
		DESCRICAO_SERVICO("descricaoServico");		
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}

    public String getFornecedorFormatado(){
    	StringBuffer fornecedorFormatado = new StringBuffer();    	
    	if (this.getNumeroFornecedor() != null){
    		fornecedorFormatado.append(this.getNumeroFornecedor().toString());
    		
    		if (this.cgcFornecedor != null) {
    			fornecedorFormatado.append(HIFEN_ESPACADO);
    			fornecedorFormatado.append(CoreUtil.formatarCNPJ(this.cgcFornecedor));
    		}
    		else {
    			if (this.cpfFornecedor != null) {
    				fornecedorFormatado.append(HIFEN_ESPACADO);
    				fornecedorFormatado.append(CoreUtil.formataCPF(this.cpfFornecedor));    				
    			}
    		}
    		fornecedorFormatado.append(HIFEN_ESPACADO);
    		fornecedorFormatado.append(this.razaoSocialFornecedor);
    		
    	}
		
    	return fornecedorFormatado.toString();
	}
	
       
    public String getAutorizacaoFornecimentoFormatado() {
    	if (this.lctNumeroAutorizacaoForn != null) {
    		return this.lctNumeroAutorizacaoForn + "/" + this.nroComplementoAutorizacaoForn;
    	}
		return "";
	}

    public Integer getCodigoMaterialServico() {
    	return (this.codigoMaterial != null ? this.codigoMaterial : this.codigoServico);
    }
    
    public String getNomeDescricaoMaterialServico() {
    	
    	return (this.codigoMaterial != null ? this.nomeMaterial + ' ' + CoreUtil.nvl(this.descricaoMaterial,"") : this.nomeServico + ' ' +  CoreUtil.nvl(this.descricaoServico,""));
    	
    }
    
    public String getUnidadeMedidaMaterialServico() {
    	return (this.codigoMaterial != null ? this.getCodigoUnidadeMedItemAF() : "UN");
    	
    }
    
    public Integer getQuantidadeMaterialServico() {
    	return (this.codigoMaterial != null ? this.getQuantidadeItemRecProvisorio() : 1);
    	
    }
    
    public String getResponsavelRecebimento() {
    	StringBuffer responsavel = new StringBuffer() ;
    	responsavel.append(this.matriculaServidor != null ? this.matriculaServidor.toString() : "");
        
    	if (this.nomePessoaFisica != null) {
    		responsavel.append(HIFEN_ESPACADO);
    		responsavel.append(this.nomePessoaFisica); 
    	}
    	
    	return responsavel.toString();
    	
    }
	
    public Double  getValorTotalNota(){
    	return (this.getValorTotalNfDocFiscalEntrada() != null ? this.getValorTotalNfDocFiscalEntrada(): 0);
    }
}
