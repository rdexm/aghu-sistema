package br.gov.mec.aghu.paciente.prontuarioonline.vo;

import java.util.Date;

@SuppressWarnings("ucd")
public class NodoAtendimentoEmergenciaPOLVO {
	
	private Integer atdSeq;
	private Integer pacCodigo;
	private Integer prontuario;
	private Date dtHrInicio;
	private Integer conNumero;
	private String nomeReduzido;
	private String nomeEspecialidade;
	private Short serVinCodigo;
	private Integer serVinMatricula;
	private String dtHrInicioFormatada;
	private String sigla;
	
	private Short seqEspecialidade;
	
	//Atributos de utilizados ao clicar no nodo 
	private String dataInicioNodoNivel3;
	private String dataFinalNodoNivel3;
	//private Integer conNumero;
	
	public enum Fields {
		ATD_SEQ("atdSeq"),
		PAC_CODIGO("pacCodigo"),
		PRONTUARIO("prontuario"),
		DTHR_INICIO("dtHrInicio"),
		CON_NUMERO("conNumero"),
		NOME_REDUZIDO("nomeReduzido"),
		NOME_ESPECIALIDADE("nomeEspecialidade"),
		SER_VIN_CODIGO("serVinCodigo"),
		SER_VIN_MATRICULA("serVinMatricula"),
		DTHT_INICIO_FORMATADA("dtHrInicioFormatada"),
		SIGLA("sigla"),
		SEQ_ESPECIALIDADE("seqEspecialidade");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}
	
	public NodoAtendimentoEmergenciaPOLVO(){}
	
	public NodoAtendimentoEmergenciaPOLVO(Integer atdSeq, Integer pacCodigo,
			Integer prontuario, Date dtHrInicio, Integer conNumero,
			String nomeReduzido, String nomeEspecialidade, Short serVinCodigo,
			Integer serVinMatricula, String dtHrInicioFormatada, String sigla) {
		super();
		this.atdSeq = atdSeq;
		this.pacCodigo = pacCodigo;
		this.prontuario = prontuario;
		this.dtHrInicio = dtHrInicio;
		this.conNumero = conNumero;
		this.nomeReduzido = nomeReduzido;
		this.nomeEspecialidade = nomeEspecialidade;
		this.serVinCodigo = serVinCodigo;
		this.serVinMatricula = serVinMatricula;
		this.dtHrInicioFormatada = dtHrInicioFormatada;
		this.sigla = sigla;
	}
	
	public NodoAtendimentoEmergenciaPOLVO(String dataInicioNodoNivel3,String dataFinalNodoNivel3) {
		super();
		this.dataInicioNodoNivel3 = dataInicioNodoNivel3;
		this.dataFinalNodoNivel3 = dataFinalNodoNivel3;
	}
	
	public NodoAtendimentoEmergenciaPOLVO(String dataInicioNodoNivel3,String dataFinalNodoNivel3,Integer pacCodigo) {
		super();
		this.dataInicioNodoNivel3 = dataInicioNodoNivel3;
		this.dataFinalNodoNivel3 = dataFinalNodoNivel3;
		this.pacCodigo = pacCodigo;
	}
	
	
	public NodoAtendimentoEmergenciaPOLVO(Integer  conNumero) {
		super();
		this.conNumero = conNumero;
	}
	
	public Integer getAtdSeq() {
		return atdSeq;
	}
	public Integer getPacCodigo() {
		return pacCodigo;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public Date getDtHrInicio() {
		return dtHrInicio;
	}
	public Integer getConNumero() {
		return conNumero;
	}
	public String getNomeReduzido() {
		return nomeReduzido;
	}
	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}
	public Integer getSerVinMatricula() {
		return serVinMatricula;
	}
	public String getDtHrInicioFormatada() {
		return dtHrInicioFormatada;
	}
	public String getSigla() {
		return sigla;
	}
	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public void setDtHrInicio(Date dtHrInicio) {
		this.dtHrInicio = dtHrInicio;
	}
	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}
	public void setNomeReduzido(String nomeReduzido) {
		this.nomeReduzido = nomeReduzido;
	}
	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}
	public void setSerVinMatricula(Integer serVinMatricula) {
		this.serVinMatricula = serVinMatricula;
	}
	public void setDtHrInicioFormatada(String dtHrInicioFormatada) {
		this.dtHrInicioFormatada = dtHrInicioFormatada;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public String getDataInicioNodoNivel3() {
		return dataInicioNodoNivel3;
	}

	public String getDataFinalNodoNivel3() {
		return dataFinalNodoNivel3;
	}

	public void setDataInicioNodoNivel3(String dataInicioNodoNivel3) {
		this.dataInicioNodoNivel3 = dataInicioNodoNivel3;
	}

	public void setDataFinalNodoNivel3(String dataFinalNodoNivel3) {
		this.dataFinalNodoNivel3 = dataFinalNodoNivel3;
	}

	public Short getSeqEspecialidade() {
		return seqEspecialidade;
	}

	public void setSeqEspecialidade(Short seqEspecialidade) {
		this.seqEspecialidade = seqEspecialidade;
	}
	
}
