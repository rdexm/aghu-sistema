package br.gov.mec.aghu.compras.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSituacaoAutorizacaoFornecimento;
import br.gov.mec.aghu.core.commons.BaseBean;

/**
 * #5561 - Consultar Parcelas de Entrega de Materiais
 * @author fabrica-ctis
 *
 */
public class ConsultarParcelasEntregaMateriaisVO implements BaseBean{
	
	
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3127155924284972010L;
	
	private Integer pfrLctNumero;
	private Short nroComplemento;
	private Short itlNumero;
	private DominioSituacaoAutorizacaoFornecimento indSituacao;
	private Boolean indEstocavel;
	private Integer parcela;
	private Integer qtde;
	private String umdCodigo;
	private Date dataPrevEntrega;
	private Date dataGeracao;
	private Integer qtdeSolicitada;
	private Integer fatorConversao;
	private Double valorUnitario;
	private Boolean indContrato;
	private Boolean indRecalcManual;
	private Boolean indCancela;
	private Boolean indTramiteInterno;
	private Boolean indEntregaImediata;
	private Boolean indEntregaUrgente;
	private String Material; // MAT.CODIGO || ' - ' || SUBSTR(MAT.NOME,1,20) AS "MATERIAL",
	private Boolean indPlanejamento;
	private Date dataVencContrato;
	private Integer iafNumero;
	private Integer iafAfnNumero;
	private Double valorTotal;
	private Integer codMaterial;
	private Integer seq;
	private String observacao;
	private String corPrevEntrega;
	
	
	
	/** Campos **/
	public enum Fields {
		PFR_LCT_NUMERO("pfrLctNumero"),
		NRO_COMPLEMENTO("nroComplemento"),
		ITL_NUMERO("itlNumero"),		
		IND_SITUACAO("indSituacao"),
		IND_ESTOCAVEL("indEstocavel"),
		PARCELA("parcela"),
		QTDE("qtde"),
		UMD_CODIGO("umdCodigo"),
		DATA_PREV_ENTREGA("dataPrevEntrega"),
		DATA_GERACAO("dataGeracao"),
		QTDE_SOLICITADA("qtdeSolicitada"),
		FATOR_CONVERSAO("fatorConversao"),
		VALOR_UNITARIO("valorUnitario"),
		IND_CONTRATO("indContrato"),
		IND_RECALC_MANUAL("indRecalcManual"),
		IND_CANCELA("indCancela"),
		IND_TRAMITE_INTERNO("indTramiteInterno"),
		IND_ENTREGA_IMEDIATA("indEntregaImediata"),
		IND_ENTREGA_URGENTE("indEntregaUrgente"),
		MATERIAL("Material"),
		MAT_CODIGO("codMaterial"),
		IND_PLANEJAMENTO("indPlanejamento"),
		DATA_VENC_CONTRATO("dataVencContrato"),
		IAF_NUMERO("iafNumero"),
		IAF_AFN_NUMERO("iafAfnNumero"),
		SEQ("seq"),
		VALOR_TOTAL("valorTotal"),
		COR_PREV_ENTREGA("corPrevEntrega");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}


	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}


	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	public Short getNroComplemento() {
		return nroComplemento;
	}

	public void setNroComplemento(Short nroComplemento) {
		this.nroComplemento = nroComplemento;
	}

	public DominioSituacaoAutorizacaoFornecimento getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacaoAutorizacaoFornecimento indSituacao) {
		this.indSituacao = indSituacao;
	}


	public Boolean getIndEstocavel() {
		return indEstocavel;
	}

	public void setIndEstocavel(Boolean indEstocavel) {
		this.indEstocavel = indEstocavel;
	}

	public Integer getParcela() {
		return parcela;
	}

	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	public Integer getQtde() {
		return qtde;
	}

	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	public String getUmdCodigo() {
		return umdCodigo;
	}

	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	public Date getDataPrevEntrega() {
		return dataPrevEntrega;
	}

	public void setDataPrevEntrega(Date dataPrevEntrega) {
		this.dataPrevEntrega = dataPrevEntrega;
	}

	public Date getDataGeracao() {
		return dataGeracao;
	}

	public void setDataGeracao(Date dataGeracao) {
		this.dataGeracao = dataGeracao;
	}

	public Integer getQtdeSolicitada() {
		return qtdeSolicitada;
	}

	public void setQtdeSolicitada(Integer qtdeSolicitada) {
		this.qtdeSolicitada = qtdeSolicitada;
	}

	public Integer getFatorConversao() {
		return fatorConversao;
	}

	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	public Double getValorUnitario() {
		return valorUnitario;
	}

	public void setValorUnitario(Double valorUnitario) {
		this.valorUnitario = valorUnitario;
	}

	public Boolean getIndContrato() {
		return indContrato;
	}

	public void setIndContrato(Boolean indContrato) {
		this.indContrato = indContrato;
	}

	public Boolean getIndRecalcManual() {
		return indRecalcManual;
	}

	public void setIndRecalcManual(Boolean indRecalcManual) {
		this.indRecalcManual = indRecalcManual;
	}

	public Boolean getIndCancela() {
		return indCancela;
	}

	public void setIndCancela(Boolean indCancela) {
		this.indCancela = indCancela;
	}

	public Boolean getIndTramiteInterno() {
		return indTramiteInterno;
	}

	public void setIndTramiteInterno(Boolean indTramiteInterno) {
		this.indTramiteInterno = indTramiteInterno;
	}

	public Boolean getIndEntregaImediata() {
		return indEntregaImediata;
	}

	public void setIndEntregaImediata(Boolean indEntregaImediata) {
		this.indEntregaImediata = indEntregaImediata;
	}

	public Boolean getIndEntregaUrgente() {
		return indEntregaUrgente;
	}

	public void setIndEntregaUrgente(Boolean indEntregaUrgente) {
		this.indEntregaUrgente = indEntregaUrgente;
	}

	public String getMaterial() {
		return Material;
	}

	public void setMaterial(String material) {
		Material = material;
	}

	public Boolean getIndPlanejamento() {
		return indPlanejamento;
	}

	public void setIndPlanejamento(Boolean indPlanejamento) {
		this.indPlanejamento = indPlanejamento;
	}

	public Date getDataVencContrato() {
		return dataVencContrato;
	}

	public void setDataVencContrato(Date dataVencContrato) {
		this.dataVencContrato = dataVencContrato;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}

	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Double getValorTotal() {
		return valorTotal;
	}

	public void setValorTotal(Double valorTotal) {
		this.valorTotal = valorTotal;
	}

	public Short getItlNumero() {
		return itlNumero;
	}

	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	public Integer getCodMaterial() {
		return codMaterial;
	}

	public void setCodMaterial(Integer codMaterial) {
		this.codMaterial = codMaterial;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	public String getCorPrevEntrega() {
		return corPrevEntrega;
	}

	public void setCorPrevEntrega(String corPrevEntrega) {
		this.corPrevEntrega = corPrevEntrega;
	}
}