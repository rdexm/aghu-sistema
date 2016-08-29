package br.gov.mec.aghu.compras.autfornecimento.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioAfEmpenhada;
import br.gov.mec.aghu.dominio.DominioClassifABC;

public class ConsultaProgramacaoEntregaItemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1866998685942800874L;
	private Integer seq; 
	private Integer pfrLctNumero;
	private Short numeroComplemento;
	private Short itlNumero;
	private Integer parcela;
	private Date dtPrevEntrega;
	private Integer qtde;
	private Integer qtdeEntregue;
	private String umdCodigo;
	private Integer fatorConversao;
	private String matUmdCodigo;
	private Integer gmtCodigo;
	private Boolean indCancelada;
	private Boolean indPlanejada;
	private Boolean indAssinatura;
	private DominioAfEmpenhada indEmpenhada;
	private Boolean indEnvioFornecedor;
	private Boolean indRecalculoAutomatico;
	private Boolean indRecalculoManual;
	private Date dtNecessidadeHCPA;
	private Date dtLibPlanejamento;
	private Date dtAssinatura;
	private Integer codigoMaterial;
	private String nomeMaterial;
	private Integer numeroFornecedor;
	private String razaoSocialFornecedor;
	private Integer AfeAfnNumero;
	private Integer afnNumero;
	private Integer iafNumero;
	private Integer iafAfnNumero;
	private DominioClassifABC  curvaAbc;
	private boolean parcelasSeremLiberadasAssinatura;
	private boolean parcelasEmpenhadas;
	private boolean parcelasPrevisaoEntregaVencida;
	private boolean parcelasAssinadasNaoEmpenhada;
	private boolean parcelasEmpenhadasNaoEntregue;
	private boolean parcelasEmpenhadasQtdeEntregueMenor;
	private boolean parcelasEmpenhadasQtdeEntregueMenorAtrasada;
	private String corDtPrevEntrega;
	private Integer qtdEstoque;
	private boolean parcelasNaoEmpenhadasEntregue;
	

	/**
	 * @return the pfrLctNumero
	 */
	public Integer getPfrLctNumero() {
		return pfrLctNumero;
	}

	/**
	 * @param pfrLctNumero
	 *            the pfrLctNumero to set
	 */
	public void setPfrLctNumero(Integer pfrLctNumero) {
		this.pfrLctNumero = pfrLctNumero;
	}

	/**
	 * @return the numeroComplemento
	 */
	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	/**
	 * @param numeroComplemento
	 *            the numeroComplemento to set
	 */
	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	/**
	 * @return the itlNumero
	 */
	public Short getItlNumero() {
		return itlNumero;
	}

	/**
	 * @param itlNumero
	 *            the itlNumero to set
	 */
	public void setItlNumero(Short itlNumero) {
		this.itlNumero = itlNumero;
	}

	/**
	 * @return the parcela
	 */
	public Integer getParcela() {
		return parcela;
	}

	/**
	 * @param parcela
	 *            the parcela to set
	 */
	public void setParcela(Integer parcela) {
		this.parcela = parcela;
	}

	/**
	 * @return the dtPrevEntrega
	 */
	public Date getDtPrevEntrega() {
		return dtPrevEntrega;
	}

	/**
	 * @param dtPrevEntrega
	 *            the dtPrevEntrega to set
	 */
	public void setDtPrevEntrega(Date dtPrevEntrega) {
		this.dtPrevEntrega = dtPrevEntrega;
	}

	/**
	 * @return the qtde
	 */
	public Integer getQtde() {
		return qtde;
	}

	/**
	 * @param qtde
	 *            the qtde to set
	 */
	public void setQtde(Integer qtde) {
		this.qtde = qtde;
	}

	/**
	 * @return the qtdeEntregue
	 */
	public Integer getQtdeEntregue() {
		return qtdeEntregue;
	}

	/**
	 * @param qtdeEntregue
	 *            the qtdeEntregue to set
	 */
	public void setQtdeEntregue(Integer qtdeEntregue) {
		this.qtdeEntregue = qtdeEntregue;
	}


	/**
	 * @return the fatorConversao
	 */
	public Integer getFatorConversao() {
		return fatorConversao;
	}

	/**
	 * @param fatorConversao
	 *            the fatorConversao to set
	 */
	public void setFatorConversao(Integer fatorConversao) {
		this.fatorConversao = fatorConversao;
	}

	/**
	 * @return the matUmdCodigo
	 */
	public String getMatUmdCodigo() {
		return matUmdCodigo;
	}

	/**
	 * @param matUmdCodigo
	 *            the matUmdCodigo to set
	 */
	public void setMatUmdCodigo(String matUmdCodigo) {
		this.matUmdCodigo = matUmdCodigo;
	}

	/**
	 * @return the gmtCodigo
	 */
	public Integer getGmtCodigo() {
		return gmtCodigo;
	}

	/**
	 * @param gmtCodigo
	 *            the gmtCodigo to set
	 */
	public void setGmtCodigo(Integer gmtCodigo) {
		this.gmtCodigo = gmtCodigo;
	}

	/**
	 * @return the indCancelada
	 */
	public Boolean getIndCancelada() {
		return indCancelada;
	}

	/**
	 * @param indCancelada
	 *            the indCancelada to set
	 */
	public void setIndCancelada(Boolean indCancelada) {
		this.indCancelada = indCancelada;
	}

	/**
	 * @return the indPlanejada
	 */
	public Boolean getIndPlanejada() {
		return indPlanejada;
	}

	/**
	 * @param indPlanejada
	 *            the indPlanejada to set
	 */
	public void setIndPlanejada(Boolean indPlanejada) {
		this.indPlanejada = indPlanejada;
	}

	/**
	 * @return the indAssinatura
	 */
	public Boolean getIndAssinatura() {
		return indAssinatura;
	}

	/**
	 * @param indAssinatura
	 *            the indAssinatura to set
	 */
	public void setIndAssinatura(Boolean indAssinatura) {
		this.indAssinatura = indAssinatura;
	}

	/**
	 * @return the indEmpenhada
	 */
	public DominioAfEmpenhada getIndEmpenhada() {
		return indEmpenhada;
	}

	/**
	 * @param indEmpenhada
	 *            the indEmpenhada to set
	 */
	public void setIndEmpenhada(DominioAfEmpenhada indEmpenhada) {
		this.indEmpenhada = indEmpenhada;
	}

	/**
	 * @return the indEnvioFornecedor
	 */
	public Boolean getIndEnvioFornecedor() {
		return indEnvioFornecedor;
	}

	/**
	 * @param indEnvioFornecedor
	 *            the indEnvioFornecedor to set
	 */
	public void setIndEnvioFornecedor(Boolean indEnvioFornecedor) {
		this.indEnvioFornecedor = indEnvioFornecedor;
	}

	/**
	 * @return the indRecalculoAutomatico
	 */
	public Boolean getIndRecalculoAutomatico() {
		return indRecalculoAutomatico;
	}

	/**
	 * @param indRecalculoAutomatico
	 *            the indRecalculoAutomatico to set
	 */
	public void setIndRecalculoAutomatico(Boolean indRecalculoAutomatico) {
		this.indRecalculoAutomatico = indRecalculoAutomatico;
	}

	/**
	 * @return the indRecalculoManual
	 */
	public Boolean getIndRecalculoManual() {
		return indRecalculoManual;
	}

	/**
	 * @param indRecalculoManual
	 *            the indRecalculoManual to set
	 */
	public void setIndRecalculoManual(Boolean indRecalculoManual) {
		this.indRecalculoManual = indRecalculoManual;
	}

	/**
	 * @return the dtNecessidadeHCPA
	 */
	public Date getDtNecessidadeHCPA() {
		return dtNecessidadeHCPA;
	}

	/**
	 * @param dtNecessidadeHCPA
	 *            the dtNecessidadeHCPA to set
	 */
	public void setDtNecessidadeHCPA(Date dtNecessidadeHCPA) {
		this.dtNecessidadeHCPA = dtNecessidadeHCPA;
	}

	/**
	 * @return the dtLibPlanejamento
	 */
	public Date getDtLibPlanejamento() {
		return dtLibPlanejamento;
	}

	/**
	 * @param dtLibPlanejamento
	 *            the dtLibPlanejamento to set
	 */
	public void setDtLibPlanejamento(Date dtLibPlanejamento) {
		this.dtLibPlanejamento = dtLibPlanejamento;
	}

	/**
	 * @return the dtAssinatura
	 */
	public Date getDtAssinatura() {
		return dtAssinatura;
	}

	/**
	 * @param dtAssinatura
	 *            the dtAssinatura to set
	 */
	public void setDtAssinatura(Date dtAssinatura) {
		this.dtAssinatura = dtAssinatura;
	}

	/**
	 * @return the codigoMaterial
	 */
	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	/**
	 * @param codigoMaterial
	 *            the codigoMaterial to set
	 */
	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	/**
	 * @return the nomeMaterial
	 */
	public String getNomeMaterial() {
		return nomeMaterial;
	}

	/**
	 * @param nomeMaterial
	 *            the nomeMaterial to set
	 */
	public void setNomeMaterial(String nomeMaterial) {
		this.nomeMaterial = nomeMaterial;
	}

	/**
	 * @return the numeroFornecedor
	 */
	public Integer getNumeroFornecedor() {
		return numeroFornecedor;
	}

	/**
	 * @param numeroFornecedor
	 *            the numeroFornecedor to set
	 */
	public void setNumeroFornecedor(Integer numeroFornecedor) {
		this.numeroFornecedor = numeroFornecedor;
	}

	/**
	 * @return the razaoSocialFornecedor
	 */
	public String getRazaoSocialFornecedor() {
		return razaoSocialFornecedor;
	}

	/**
	 * @param razaoSocialFornecedor
	 *            the razaoSocialFornecedor to set
	 */
	public void setRazaoSocialFornecedor(String razaoSocialFornecedor) {
		this.razaoSocialFornecedor = razaoSocialFornecedor;
	}

	/**
	 * @return the afnNumero
	 */
	public Integer getAfnNumero() {
		return afnNumero;
	}

	/**
	 * @param afnNumero
	 *            the afnNumero to set
	 */
	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	/**
	 * @return the iafNumero
	 */
	public Integer getIafNumero() {
		return iafNumero;
	}

	/**
	 * @param iafNumero
	 *            the iafNumero to set
	 */
	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	/**
	 * @param afeAfnNumero the afeAfnNumero to set
	 */
	public void setAfeAfnNumero(Integer afeAfnNumero) {
		AfeAfnNumero = afeAfnNumero;
	}

	/**
	 * @return the afeAfnNumero
	 */
	public Integer getAfeAfnNumero() {
		return AfeAfnNumero;
	}




	/**
	 * @param curvaAbc the curvaAbc to set
	 */
	public void setCurvaAbc(DominioClassifABC curvaAbc) {
		this.curvaAbc = curvaAbc;
	}

	/**
	 * @return the curvaAbc
	 */
	public DominioClassifABC getCurvaAbc() {
		return curvaAbc;
	}



	/**
	 * @return the parcelasSeremLiberadasAssinatura
	 */
	public boolean isParcelasSeremLiberadasAssinatura() {
		return parcelasSeremLiberadasAssinatura;
	}

	/**
	 * @param parcelasSeremLiberadasAssinatura the parcelasSeremLiberadasAssinatura to set
	 */
	public void setParcelasSeremLiberadasAssinatura(
			boolean parcelasSeremLiberadasAssinatura) {
		this.parcelasSeremLiberadasAssinatura = parcelasSeremLiberadasAssinatura;
	}

	/**
	 * @return the parcelasEmpenhadas
	 */
	public boolean isParcelasEmpenhadas() {
		return parcelasEmpenhadas;
	}

	/**
	 * @param parcelasEmpenhadas the parcelasEmpenhadas to set
	 */
	public void setParcelasEmpenhadas(boolean parcelasEmpenhadas) {
		this.parcelasEmpenhadas = parcelasEmpenhadas;
	}

	/**
	 * @return the parcelasPrevisaoEntregaVencida
	 */
	public boolean isParcelasPrevisaoEntregaVencida() {
		return parcelasPrevisaoEntregaVencida;
	}

	/**
	 * @param parcelasPrevisaoEntregaVencida the parcelasPrevisaoEntregaVencida to set
	 */
	public void setParcelasPrevisaoEntregaVencida(
			boolean parcelasPrevisaoEntregaVencida) {
		this.parcelasPrevisaoEntregaVencida = parcelasPrevisaoEntregaVencida;
	}

	/**
	 * @return the parcelasAssinadasNaoEmpenhada
	 */
	public boolean isParcelasAssinadasNaoEmpenhada() {
		return parcelasAssinadasNaoEmpenhada;
	}

	/**
	 * @param parcelasAssinadasNaoEmpenhada the parcelasAssinadasNaoEmpenhada to set
	 */
	public void setParcelasAssinadasNaoEmpenhada(
			boolean parcelasAssinadasNaoEmpenhada) {
		this.parcelasAssinadasNaoEmpenhada = parcelasAssinadasNaoEmpenhada;
	}

	/**
	 * @return the parcelasEmpenhadasNaoEntregue
	 */
	public boolean isParcelasEmpenhadasNaoEntregue() {
		return parcelasEmpenhadasNaoEntregue;
	}

	/**
	 * @param parcelasEmpenhadasNaoEntregue the parcelasEmpenhadasNaoEntregue to set
	 */
	public void setParcelasEmpenhadasNaoEntregue(
			boolean parcelasEmpenhadasNaoEntregue) {
		this.parcelasEmpenhadasNaoEntregue = parcelasEmpenhadasNaoEntregue;
	}

	/**
	 * @return the parcelasEmpenhadasQtdeEntregueMenor
	 */
	public boolean isParcelasEmpenhadasQtdeEntregueMenor() {
		return parcelasEmpenhadasQtdeEntregueMenor;
	}

	/**
	 * @param parcelasEmpenhadasQtdeEntregueMenor the parcelasEmpenhadasQtdeEntregueMenor to set
	 */
	public void setParcelasEmpenhadasQtdeEntregueMenor(
			boolean parcelasEmpenhadasQtdeEntregueMenor) {
		this.parcelasEmpenhadasQtdeEntregueMenor = parcelasEmpenhadasQtdeEntregueMenor;
	}

	/**
	 * @return the parcelasEmpenhadasQtdeEntregueMenorAtrasada
	 */
	public boolean isParcelasEmpenhadasQtdeEntregueMenorAtrasada() {
		return parcelasEmpenhadasQtdeEntregueMenorAtrasada;
	}

	/**
	 * @param parcelasEmpenhadasQtdeEntregueMenorAtrasada the parcelasEmpenhadasQtdeEntregueMenorAtrasada to set
	 */
	public void setParcelasEmpenhadasQtdeEntregueMenorAtrasada(
			boolean parcelasEmpenhadasQtdeEntregueMenorAtrasada) {
		this.parcelasEmpenhadasQtdeEntregueMenorAtrasada = parcelasEmpenhadasQtdeEntregueMenorAtrasada;
	}



	/**
	 * @param corDtPrevEntrega the corDtPrevEntrega to set
	 */
	public void setCorDtPrevEntrega(String corDtPrevEntrega) {
		this.corDtPrevEntrega = corDtPrevEntrega;
	}

	/**
	 * @return the corDtPrevEntrega
	 */
	public String getCorDtPrevEntrega() {
		return corDtPrevEntrega;
	}



	/**
	 * @param umdCodigo the umdCodigo to set
	 */
	public void setUmdCodigo(String umdCodigo) {
		this.umdCodigo = umdCodigo;
	}

	/**
	 * @return the umdCodigo
	 */
	public String getUmdCodigo() {
		return umdCodigo;
	}


	/**
	 * @return the qtdEstoque
	 */
	public Integer getQtdEstoque() {
		qtdEstoque = qtde * fatorConversao;
		return qtdEstoque;
	}
	


	/**
	 * @param seq the seq to set
	 */
	public void setSeq(Integer seq) {
		this.seq = seq;
	}

	/**
	 * @return the seq
	 */
	public Integer getSeq() {
		return seq;
	}



	public void setQtdEstoque(Integer qtdEstoque) {
		this.qtdEstoque = qtdEstoque;
	}

	public boolean isParcelasNaoEmpenhadasEntregue() {
		return parcelasNaoEmpenhadasEntregue;
	}

	public void setParcelasNaoEmpenhadasEntregue(
			boolean parcelasNaoEmpenhadasEntregue) {
		this.parcelasNaoEmpenhadasEntregue = parcelasNaoEmpenhadasEntregue;
	}



	public void setIafAfnNumero(Integer iafAfnNumero) {
		this.iafAfnNumero = iafAfnNumero;
	}

	public Integer getIafAfnNumero() {
		return iafAfnNumero;
	}



	public enum Fields {
		PFR_LCT_NUMERO("pfrLctNumero"),
		NUMERO_COMPLEMENTO("numeroComplemento"),
		ITL_NUMERO("itlNumero"),
		PARCELA("parcela"),
		DT_PREV_ENTREGA("dtPrevEntrega"),
		QTDE("qtde"),
		QTDE_ENTREGUE("qtdeEntregue"),
		UMD_CODIGO("umdCodigo"),
		FATOR_CONVERSAO("fatorConversao"),
		MAT_UMD_CODIGO("matUmdCodigo"),
		GMT_CODIGO("gmtCodigo"),
		IND_CANCELADA("indCancelada"),
		IND_PLANEJADA("indPlanejada"),
		IND_ASSINATURA("indAssinatura"),
		IND_EMPENHADA("indEmpenhada"),
		IND_ENVIO_FORNECEDOR("indEnvioFornecedor"), 
		IND_RECALCULO_AUTOMATICO("indRecalculoAutomatico"),
		IND_RECALCULO_MANUAL("indRecalculoManual"),
		DT_NECESSIDADE_HCPA("dtNecessidadeHCPA"),
		DT_LIB_PLANEJAMENTO("dtLibPlanejamento"), 
		DT_ASSINATURA("dtAssinatura"),
		CODIGO_MATERIAL("codigoMaterial"),
		NOME_MATERIAL("nomeMaterial"),
		NUMERO_FORNECEDOR("numeroFornecedor"),
		RAZAO_SOCIAL_FORNECEDOR("razaoSocialFornecedor"),
		AFN_NUMERO("afnNumero"),
		IAF_NUMERO("iafNumero"),
		IAF_AFN_NUMERO("iafAfnNumero"),
		AFNE_AFN_NUMERO("AfeAfnNumero"),
		SEQ("seq"),
		PARCELAS_SEREM_LIBERADAS_ASSINATURA("parcelasSeremLiberadasAssinatura"),
		PARCELAS_EMPENHADAS("parcelasEmpenhadas"),
		PARCELAS_PREVISAO_ENTREGA_VENCIDA("parcelasPrevisaoEntregaVencida"),
		PARCELAS_ASSINADAS_NAO_EMPENHADA("parcelasAssinadasNaoEmpenhada"),
		PARCELAS_ASSINADAS_NAO_ENTEGUE("parcelasAssinadasNaoEntregue"),
		PARCELAS_EMPENHADAS_QTE_ENTREGUE_MENOR("parcelasEmpenhadasQtdeEntregueMenor"),
		PARCELAS_EMPENHADAS_QTDE_ENTREGUE_MENOR_ATRASADA("parcelasEmpenhadasQtdeEntregueMenorAtrasada");
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