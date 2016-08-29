/**
 * 
 */
package br.gov.mec.aghu.estoque.vo;

import java.util.Date; 

/**
 * @author joao.pan, anderson.zerloti
 *
 *	VO para a est�ria 36185
 */
public class DestinoMaterialRecebidoVO {

	private Integer nroNR;
	private Long notaFiscal;
	private Date dataEntrada;
	private String nroAF;
	private String comprador;
	private Short cv;
	private Integer nroSC;
	private String descricaoSC;
	private Integer ccReq;
	private String ccReqDesc;
	private Integer ccApl;
	private String ccAplDesc;
	private Integer matCodigo;
	private String matNome;
	private String matDescricao;
	private Integer qtde;
	private String umdCodigo;
	private String aplicacao;
	private String justificativaUso;
	
	public enum Fields {
		
		NRO_NR("nroNR"),		
		
		NOTA_FISCAL("notaFiscal"),
		
		DATA_ENTR("dataEntrada"),
		
		NRO_AF("nroAF"),
		
		COMPRADOR("comprador"),
		
		CV("cv"),
		
		NRO_SC("nroSC"),
		
		DESCRICAO_SC("descricaoSC"),
		
		CC_REQ("cctCodigo"),
		
		CC_REQ_DESC("ccReqDesc"),
		
		CC_APL("ccApl"),
		
		CC_APL_DESC("ccAplDesc"),
		
		MAT_CODIGO("matCodigo"),
		
		MAT_NOME("matNome"),
		
		MAT_DESCRICAO("matDescricao"),
		
		QTDE("qtde"),
		
		UMD_CODIGO("umdCodigo"),
		
		APLICACAO("aplicacao"),
		
		JUSTIFICATIVA_USO("justificativaUso");
		
		String field;
		
		private Fields(String field) {
			this.field = field;
		}
		
		@Override
		public String toString() {
			return this.field;
		}
	}

	public Integer getNumeroNR() {
		return nroNR;
	}

	public void setNumeroNR(Integer numeroNR) {
		this.nroNR = numeroNR;
	}

	public Long getNotaFiscal() {
		return notaFiscal;
	}

	public void setNotaFiscal(Long notaFiscal) {
		this.notaFiscal = notaFiscal;
	}

	public Date getDataEntrada() {
		return dataEntrada;
	}

	public void setDataEntrada(Date dataEntrada) {
		this.dataEntrada = dataEntrada;
	}

	public String getNumeroAF() {
		return nroAF;
	}

	public void setNumeroAF(String numeroAF) {
		this.nroAF = numeroAF;
	}

	public String getComprador() {
		return comprador;
	}

	public void setComprador(String comprador) {
		this.comprador = comprador;
	}

	public Short getCv() {
		return cv;
	}

	public void setCv(Short cv) {
		this.cv = cv;
	}

	public Integer getNumeroSC() {
		return nroSC;
	}

	public void setNumeroSC(Integer numeroSC) {
		this.nroSC = numeroSC;
	}

	public String getDescricaoSC() {
		return descricaoSC;
	}

	public void setDescricaoSC(String descricaoSC) {
		this.descricaoSC = descricaoSC;
	}

	public Integer getCcReq() {
		return ccReq;
	}

	public void setCcReq(Integer ccReq) {
		this.ccReq = ccReq;
	}

	public String getCcReqDesc() {
		return ccReqDesc;
	}

	public void setCcReqDesc(String ccReqDesc) {
		this.ccReqDesc = ccReqDesc;
	}

	public Integer getCcApl() {
		return ccApl;
	}

	public void setCcApl(Integer ccApl) {
		this.ccApl = ccApl;
	}

	public String getCcAplDesc() {
		return ccAplDesc;
	}

	public void setCcAplDesc(String ccAplDesc) {
		this.ccAplDesc = ccAplDesc;
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

	public String getAplicacao() {
		return aplicacao;
	}

	public void setAplicacao(String aplicacao) {
		this.aplicacao = aplicacao;
	}

	public String getJustificativaUso() {
		return justificativaUso;
	}

	public void setJustificativaUso(String justificativaUso) {
		this.justificativaUso = justificativaUso;
	}
	
}
