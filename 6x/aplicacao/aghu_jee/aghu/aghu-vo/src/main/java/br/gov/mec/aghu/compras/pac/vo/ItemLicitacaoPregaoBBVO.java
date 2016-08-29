package br.gov.mec.aghu.compras.pac.vo;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioMotivoCancelamentoComissaoLicitacao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;

public class ItemLicitacaoPregaoBBVO implements Serializable {
	
	private static final long serialVersionUID = -7550942587513416207L;
	private Integer itlLctNumero;
	private Short itlNumero;
	private Short itlClassifItem;
	private BigDecimal itlValorUnitario;
	private Boolean itlExclusao;
	private String itlMotivoExclusao;
	private Date itlDtExclusao;
	private DominioMotivoCancelamentoComissaoLicitacao itlMotivoCancel;
	private Boolean itlPropostaEscolhida;
	private Boolean itlEmAf;
	private Short llcNumero;
	private String llcDescricao;
	private Long slcQtdeAprovada;
	private String slcDescricao;
	private Integer slsQtdeSolicitada;
	private String slsDescricao;
	private Integer matCodigo;
	private String matNome;
	private String matDescricao;
	private Integer srvCodigo;
	private DominioTipoFaseSolicitacao fscTipo;
	private Integer gmtCodigoMercadoriaBB;
	
	public String retornarQtdeAprovada(){
		return StringUtils.join(new Object[]{slcQtdeAprovada, slsQtdeSolicitada}); 
	}
	
	public String retornarMatCodigo(){
		return StringUtils.join(new Integer[]{matCodigo, srvCodigo}); 
	}
	
	public Integer retornarGmtCodigoMercadoriaBB(){
		if(fscTipo == DominioTipoFaseSolicitacao.C){
			if(gmtCodigoMercadoriaBB == null){
				return 0;
			} else {
				return gmtCodigoMercadoriaBB;
			}
		}
		return 112222;
	}
	
	private String translateString(String variavel){
		return StringUtils.replaceChars(variavel.replace((char)10, (char)32).replace((char)9, (char)32)
				, "ãõáéíóúçäëïöüâêîôûàèìòùçÃÕÁÉÍÓÚÇÄËÏÖÜÂÊÎÔÛÀÈÌÒÙÇ"
				, "aoaeioucaeiouaeiouaeiouçAOAEIOUCAEIOUAEIOUAEIOUC");
	}
	
	public String retornarDescricaoItem(){
		return translateString(StringUtils.join(new String[]{StringUtils.stripStart(matNome," ")
				, StringUtils.stripStart(matDescricao, " ")
				, StringUtils.stripStart(slcDescricao," ")
				, StringUtils.stripStart(slsDescricao, " ")}, " "));
	}
	
	public String retornarLlcDescricao() {
		return llcDescricao != null ? llcDescricao : StringUtils.EMPTY;
	}
	
	//getters and setters
	public Integer getItlLctNumero() {
		return itlLctNumero;
	}
	public void setItlLctNumero(Integer itlLctNumero) {
		this.itlLctNumero = itlLctNumero;
	}
	public Short getItlNumero() {
		return itlNumero;
	}
	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}
	public Short getItlClassifItem() {
		return itlClassifItem;
	}
	public void setItlClassifItem(Short itlClassifItem) {
		this.itlClassifItem = itlClassifItem;
	}
	public BigDecimal getItlValorUnitario() {
		return itlValorUnitario;
	}
	public void setItlValorUnitario(BigDecimal itlValorUnitario) {
		this.itlValorUnitario = itlValorUnitario;
	}
	public Boolean getItlExclusao() {
		return itlExclusao;
	}
	public void setItlExclusao(Boolean itlExclusao) {
		this.itlExclusao = itlExclusao;
	}
	public String getItlMotivoExclusao() {
		return itlMotivoExclusao;
	}
	public void setItlMotivoExclusao(String itlMotivoExclusao) {
		this.itlMotivoExclusao = itlMotivoExclusao;
	}
	public Date getItlDtExclusao() {
		return itlDtExclusao;
	}
	public void setItlDtExclusao(Date itlDtExclusao) {
		this.itlDtExclusao = itlDtExclusao;
	}
	public DominioMotivoCancelamentoComissaoLicitacao getItlMotivoCancel() {
		return itlMotivoCancel;
	}
	public void setItlMotivoCancel(
			DominioMotivoCancelamentoComissaoLicitacao itlMotivoCancel) {
		this.itlMotivoCancel = itlMotivoCancel;
	}
	public Boolean getItlPropostaEscolhida() {
		return itlPropostaEscolhida;
	}
	public void setItlPropostaEscolhida(Boolean itlPropostaEscolhida) {
		this.itlPropostaEscolhida = itlPropostaEscolhida;
	}
	public Boolean getItlEmAf() {
		return itlEmAf;
	}
	public void setItlEmAf(Boolean itlEmAf) {
		this.itlEmAf = itlEmAf;
	}
	public Short getLlcNumero() {
		return llcNumero;
	}
	public void setLlcNumero(Short llcNumero) {
		this.llcNumero = llcNumero;
	}
	public String getLlcDescricao() {
		return llcDescricao;
	}
	public void setLlcDescricao(String llcDescricao) {
		this.llcDescricao = llcDescricao;
	}
	public Long getSlcQtdeAprovada() {
		return slcQtdeAprovada;
	}
	public void setSlcQtdeAprovada(Long slcQtdeAprovada) {
		this.slcQtdeAprovada = slcQtdeAprovada;
	}
	public String getSlcDescricao() {
		return slcDescricao;
	}
	public void setSlcDescricao(String slcDescricao) {
		this.slcDescricao = slcDescricao;
	}
	public Integer getSlsQtdeSolicitada() {
		return slsQtdeSolicitada;
	}
	public void setSlsQtdeSolicitada(Integer slsQtdeSolicitada) {
		this.slsQtdeSolicitada = slsQtdeSolicitada;
	}
	public String getSlsDescricao() {
		return slsDescricao;
	}
	public void setSlsDescricao(String slsDescricao) {
		this.slsDescricao = slsDescricao;
	}
	public Integer getMatCodigo() {
		return matCodigo;
	}
	public void setMatCodigo(Integer matCodigo) {
		this.matCodigo = matCodigo;
	}
	public String getMatNome() {
		return matNome;
	}
	public void setMatNome(String matNome) {
		this.matNome = matNome;
	}
	public String getMatDescricao() {
		return matDescricao;
	}
	public void setMatDescricao(String matDescricao) {
		this.matDescricao = matDescricao;
	}
	public Integer getSrvCodigo() {
		return srvCodigo;
	}
	public void setSrvCodigo(Integer srvCodigo) {
		this.srvCodigo = srvCodigo;
	}
	public DominioTipoFaseSolicitacao getFscTipo() {
		return fscTipo;
	}
	public void setFscTipo(DominioTipoFaseSolicitacao fscTipo) {
		this.fscTipo = fscTipo;
	}
	public Integer getGmtCodigoMercadoriaBB() {
		return gmtCodigoMercadoriaBB;
	}
	public void setGmtCodigoMercadoriaBB(Integer gmtCodigoMercadoriaBB) {
		this.gmtCodigoMercadoriaBB = gmtCodigoMercadoriaBB;
	}
	
	public enum Fields {
		ITL_LCT_NUMERO("itlLctNumero"),
		ITL_NUMERO("itlNumero"),
		ITL_CLASSIF_ITEM("itlClassifItem"),
		ITL_VALOR_UNITARIO("itlValorUnitario"),
		ITL_EXCLUSAO("itlExclusao"),
		ITL_MOTIVO_EXCLUSAO("itlMotivoExclusao"),
		ITL_DT_EXCLUSAO("itlDtExclusao"),
		ITL_MOTIVO_CANCEL("itlMotivoCancel"),
		ITL_PROPOSTA_ESCOLHIDA("itlPropostaEscolhida"),
		ITL_EM_AF("itlEmAf"),
		LLC_NUMERO("llcNumero"),
		LLC_DESCRICAO("llcDescricao"),
		SLC_QTDE_APROVADA("slcQtdeAprovada"),
		SLC_DESCRICAO("slcDescricao"),
		SLS_QTD_SOLICITADA("slsQtdeSolicitada"),
		SLS_DESCRICAO("slsDescricao"),
		MAT_CODIGO("matCodigo"),
		MAT_NOME("matNome"),
		MAT_DESCRICAO("matDescricao"),
		SRV_CODIGO("srvCodigo"),
		FSC_TIPO("fscTipo"),
		GMT_CODIGO_MERCADORIA_BB("gmtCodigoMercadoriaBB");

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
