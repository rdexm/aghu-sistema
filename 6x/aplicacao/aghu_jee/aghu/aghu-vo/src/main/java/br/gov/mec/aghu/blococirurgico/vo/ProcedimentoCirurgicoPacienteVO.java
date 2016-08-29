package br.gov.mec.aghu.blococirurgico.vo;

import java.util.Date;

public class ProcedimentoCirurgicoPacienteVO {
	
	private Integer crgSeq;
	private Integer pciSeq;
	private Integer phiSeq;
	private Boolean indPrincipal;
	private Date dataEntradaSala;
	private Date dataSaidaSala;
	private Date dataInicioAnestesia;
	private Date dataFimAnestesia;
	private Date dataInicioCirurgia;
	private Date dataFimCirurgia;
	private Date dataEntradaSr;
	private Date dataSaidaSr;
	private Short espSeq;
	private Integer cctCodigo;
	private Short pucSerVinCodigo;
	private Integer pucSerMatricula;
	
	
	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public Integer getPciSeq() {
		return pciSeq;
	}

	public void setPciSeq(Integer pciSeq) {
		this.pciSeq = pciSeq;
	}

	public Integer getPhiSeq() {
		return phiSeq;
	}

	public void setPhiSeq(Integer phiSeq) {
		this.phiSeq = phiSeq;
	}

	public Boolean getIndPrincipal() {
		return indPrincipal;
	}

	public void setIndPrincipal(Boolean indPrincipal) {
		this.indPrincipal = indPrincipal;
	}

	public Date getDataEntradaSala() {
		return dataEntradaSala;
	}

	public void setDataEntradaSala(Date dataEntradaSala) {
		this.dataEntradaSala = dataEntradaSala;
	}

	public Date getDataSaidaSala() {
		return dataSaidaSala;
	}

	public void setDataSaidaSala(Date dataSaidaSala) {
		this.dataSaidaSala = dataSaidaSala;
	}

	public Date getDataInicioAnestesia() {
		return dataInicioAnestesia;
	}

	public void setDataInicioAnestesia(Date dataInicioAnestesia) {
		this.dataInicioAnestesia = dataInicioAnestesia;
	}

	public Date getDataFimAnestesia() {
		return dataFimAnestesia;
	}

	public void setDataFimAnestesia(Date dataFimAnestesia) {
		this.dataFimAnestesia = dataFimAnestesia;
	}

	public Date getDataInicioCirurgia() {
		return dataInicioCirurgia;
	}

	public void setDataInicioCirurgia(Date dataInicioCirurgia) {
		this.dataInicioCirurgia = dataInicioCirurgia;
	}

	public Date getDataFimCirurgia() {
		return dataFimCirurgia;
	}

	public void setDataFimCirurgia(Date dataFimCirurgia) {
		this.dataFimCirurgia = dataFimCirurgia;
	}

	public Date getDataEntradaSr() {
		return dataEntradaSr;
	}

	public void setDataEntradaSr(Date dataEntradaSr) {
		this.dataEntradaSr = dataEntradaSr;
	}

	public Date getDataSaidaSr() {
		return dataSaidaSr;
	}

	public void setDataSaidaSr(Date dataSaidaSr) {
		this.dataSaidaSr = dataSaidaSr;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public Integer getCctCodigo() {
		return cctCodigo;
	}

	public void setCctCodigo(Integer cctCodigo) {
		this.cctCodigo = cctCodigo;
	}

	public Short getPucSerVinCodigo() {
		return pucSerVinCodigo;
	}

	public void setPucSerVinCodigo(Short pucSerVinCodigo) {
		this.pucSerVinCodigo = pucSerVinCodigo;
	}

	public Integer getPucSerMatricula() {
		return pucSerMatricula;
	}

	public void setPucSerMatricula(Integer pucSerMatricula) {
		this.pucSerMatricula = pucSerMatricula;
	}
	
	public enum Fields {
		
		CRG_SEQ("crgSeq"),
		PCI_SEQ("pciSeq"),
		PHI_SEQ("phiSeq"),
		IND_PRINCIPAL("indPrincipal"),
		DATA_ENTRADA_SALA("dataEntradaSala"),
		DATA_SAIDA_SALA("dataSaidaSala"),
		DATA_INICIO_ANESTESIA("dataInicioAnestesia"),
		DATA_FIM_ANESTESIA("dataFimAnestesia"),
		DATA_INICIO_CIRURGIA("dataInicioCirurgia"),
		DATA_FIM_CIRURGIA("dataFimCirurgia"),
		DATA_ENTRADA_SR("dataEntradaSr"),
		DATA_SAIDA_SR("dataSaidaSr"),
		ESP_SEQ("espSeq"),
		CCT_CODIGO("cctCodigo"),
		PUC_SER_VIN_CODIGO("pucSerVinCodigo"),
		PUC_SER_MATRICULA("pucSerMatricula")
		;
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}	
	}
}
