package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * @author amalmeida
 * VO para Unitarização de medicamentos #5892
 *
 */
public class UnitarizacaoVO  implements Serializable{

	private static final long serialVersionUID = 4182754562216041848L;
	
	private String conc;
	private String ummDesc;
	private String concentracao;
	private String nomeMed;
	private Integer nroNf; //Nota fiscal
	private String laboratorio;
	private Integer nroInicial;
	private Integer mcmCodigo;
	private Date dtValidade;
	private String lotCodigo;
	private Integer lotSeq;
	private String siglaApresentacaoMedicamento;
	
	public String getConc() {
		return conc;
	}
	public void setConc(String conc) {
		this.conc = conc;
	}
	public String getUmmDesc() {
		return ummDesc;
	}
	public void setUmmDesc(String ummDesc) {
		this.ummDesc = ummDesc;
	}
	public String getConcentracao() {
		return concentracao;
	}
	public void setConcentracao(String concentracao) {
		this.concentracao = concentracao;
	}
	public String getNomeMed() {
		return nomeMed;
	}
	public void setNomeMed(String nomeMed) {
		this.nomeMed = nomeMed;
	}
	public Integer getNroNf() {
		return nroNf;
	}
	public void setNroNf(Integer nroNf) {
		this.nroNf = nroNf;
	}
	public String getLaboratorio() {
		return laboratorio;
	}
	public void setLaboratorio(String laboratorio) {
		this.laboratorio = laboratorio;
	}
	public Integer getNroInicial() {
		return nroInicial;
	}
	public void setNroInicial(Integer nroInicial) {
		this.nroInicial = nroInicial;
	}
	public Integer getMcmCodigo() {
		return mcmCodigo;
	}
	public void setMcmCodigo(Integer mcmCodigo) {
		this.mcmCodigo = mcmCodigo;
	}
	public Date getDtValidade() {
		return dtValidade;
	}
	public void setDtValidade(Date dtValidade) {
		this.dtValidade = dtValidade;
	}
	public String getLotCodigo() {
		return lotCodigo;
	}
	public void setLotCodigo(String lotCodigo) {
		this.lotCodigo = lotCodigo;
	}
	public Integer getLotSeq() {
		return lotSeq;
	}
	public void setLotSeq(Integer lotSeq) {
		this.lotSeq = lotSeq;
	}
	public String getSiglaApresentacaoMedicamento() {
		return siglaApresentacaoMedicamento;
	}
	public void setSiglaApresentacaoMedicamento(String siglaApresentacaoMedicamento) {
		this.siglaApresentacaoMedicamento = siglaApresentacaoMedicamento;
	}
	
	
	
}
