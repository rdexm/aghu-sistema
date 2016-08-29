package br.gov.mec.aghu.procedimentoterapeutico.vo;

import java.io.Serializable;

public class PrescricaoPacientePTVO implements Serializable {

	
	private static final long serialVersionUID = 1160080431473124388L;

	private Integer sesSeq;
	private Short ciclo;
	private Integer serMatricula;
	private Short serVinCodigo;
	private Integer serMatriculaValida;
	private Short serVinCodigoValida;
	private String nomeResponsavel1;
	private String nomeResponsavel2;
	private Integer cloSeq;
	private Integer lote;
	
	public enum Fields {

		CICLO("ciclo"), 
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_MATRICULA_VALIDA("serMatriculaValida"),
		SER_VIN_CODIGO_VALIDA("serVinCodigoValida"),
		NOME_RESPONSAVEL1("nomeResponsavel1"),
		NOME_RESPONSAVEL2("nomeResponsavel2"),
		CLO_SEQ("cloSeq"),
		LOTE("lote"),
		SES_SEQ("sesSeq"),
		;

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return fields;
		}
	}

	public Short getCiclo() {
		return ciclo;
	}

	public void setCiclo(Short ciclo) {
		this.ciclo = ciclo;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Integer getSerMatriculaValida() {
		return serMatriculaValida;
	}

	public void setSerMatriculaValida(Integer serMatriculaValida) {
		this.serMatriculaValida = serMatriculaValida;
	}

	public Short getSerVinCodigoValida() {
		return serVinCodigoValida;
	}

	public void setSerVinCodigoValida(Short serVinCodigoValida) {
		this.serVinCodigoValida = serVinCodigoValida;
	}

	public String getNomeResponsavel1() {
		return nomeResponsavel1;
	}

	public void setNomeResponsavel1(String nomeResponsavel1) {
		this.nomeResponsavel1 = nomeResponsavel1;
	}

	public String getNomeResponsavel2() {
		return nomeResponsavel2;
	}

	public void setNomeResponsavel2(String nomeResponsavel2) {
		this.nomeResponsavel2 = nomeResponsavel2;
	}

	public Integer getCloSeq() {
		return cloSeq;
	}

	public void setCloSeq(Integer cloSeq) {
		this.cloSeq = cloSeq;
	}

	public Integer getLote() {
		return lote;
	}

	public void setLote(Integer lote) {
		this.lote = lote;
	}

	public Integer getSesSeq() {
		return sesSeq;
	}

	public void setSesSeq(Integer sesSeq) {
		this.sesSeq = sesSeq;
	}
	
}
