package br.gov.mec.aghu.faturamento.vo;

public class FatTabProcedRegistroVO {

	private String keyProcedRegistro;
	
	private Long codigoProcedimento;
	
	private String codigoRegistro;

	private String dtCompetencia;

	private TipoProcessado processado;
	
	public FatTabProcedRegistroVO() {}
	
	

	public FatTabProcedRegistroVO(Long codigoProcedimento, String codigoRegistro, String dtCompetencia,
			 TipoProcessado processado) {
		super();
		this.codigoProcedimento = codigoProcedimento;
		this.codigoRegistro = codigoRegistro;
		this.dtCompetencia = dtCompetencia;
		this.processado = processado;
	}



	public enum TipoProcessado {

		INCLUI("I"),
		ALTERA("A"),
		PROCESSADO("S"),
		NAO_PROCESSADO("N"),
		DESPREZADO("D");
		
		private String fields;

		private TipoProcessado(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}



	public String getKeyProcedRegistro() {
		return keyProcedRegistro;
	}



	public void setKeyProcedRegistro(String keyProcedRegistro) {
		this.keyProcedRegistro = keyProcedRegistro;
	}



	public Long getCodigoProcedimento() {
		return codigoProcedimento;
	}



	public void setCodigoProcedimento(Long codigoProcedimento) {
		this.codigoProcedimento = codigoProcedimento;
	}



	public String getCodigoRegistro() {
		return codigoRegistro;
	}



	public void setCodigoRegistro(String codigoRegistro) {
		this.codigoRegistro = codigoRegistro;
	}



	public String getDtCompetencia() {
		return dtCompetencia;
	}

	public void setDtCompetencia(String dtCompetencia) {
		this.dtCompetencia = dtCompetencia;
	}

	public TipoProcessado getProcessado() {
		return processado;
	}



	public void setProcessado(TipoProcessado processado) {
		this.processado = processado;
	}
	



}