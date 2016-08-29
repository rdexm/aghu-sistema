package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

/**
 Classe para carregar dados para ConsultasDeOutrosConvenios
 * #8684 
 * @author romario.caldeira
 *
 */
public class ConsultasDeOutrosConveniosVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2782595946747407317L;
	
	private Date dtConsulta;
	
	private Integer numero;
	private Integer pacCodigo;
	private Integer pacProntuario;
	private Integer serMatricula;
	
	private String espSigla;
	private String retDescricao;
	private String pacNome;
	private String cnvDescricao;
	private String cspDescricao;
	private String pesNome;
	
	private Short cspCnvCodigo;
	private Short serVinCodigo;
	
	private Byte cspSeq;
	
	
	public enum Fields {

		DATA_CONSULTA("dtConsulta"), 
		NUMERO("numero"),
		PAC_CODIGO("pacCodigo"),
		PAC_PRONTUARIO("pacProntuario"), 
		SER_MATRICULA("serMatricula"),
		ESP_SIGLA("espSigla"),
		RET_DESCRICAO("retDescricao"), 
		PAC_NOME("pacNome"),
		CNV_DESCRICAO("cnvDescricao"),
		CSP_DESCRICAO("cspDescricao"),
		PEC_NOME("pesNome"),
		CSP_CNV_CODIGO("cspCnvCodigo"),
		SER_VIN_CODIGO("serVinCodigo"),
		CSP_SEQ("cspSeq");
		
		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
	}


	/**
	 * @return the dtConsulta
	 */
	public Date getDtConsulta() {
		return dtConsulta;
	}


	/**
	 * @param dtConsulta the dtConsulta to set
	 */
	public void setDtConsulta(Date dtConsulta) {
		this.dtConsulta = dtConsulta;
	}


	/**
	 * @return the numero
	 */
	public Integer getNumero() {
		return numero;
	}


	/**
	 * @param numero the numero to set
	 */
	public void setNumero(Integer numero) {
		this.numero = numero;
	}


	/**
	 * @return the pacCodigo
	 */
	public Integer getPacCodigo() {
		return pacCodigo;
	}


	/**
	 * @param pacCodigo the pacCodigo to set
	 */
	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}


	/**
	 * @return the pacProntuario
	 */
	public Integer getPacProntuario() {
		return pacProntuario;
	}


	/**
	 * @param pacProntuario the pacProntuario to set
	 */
	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}


	/**
	 * @return the serMatricula
	 */
	public Integer getSerMatricula() {
		return serMatricula;
	}


	/**
	 * @param serMatricula the serMatricula to set
	 */
	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}


	/**
	 * @return the espSigla
	 */
	public String getEspSigla() {
		return espSigla;
	}


	/**
	 * @param espSigla the espSigla to set
	 */
	public void setEspSigla(String espSigla) {
		this.espSigla = espSigla;
	}


	/**
	 * @return the retDescricao
	 */
	public String getRetDescricao() {
		return retDescricao;
	}


	/**
	 * @param retDescricao the retDescricao to set
	 */
	public void setRetDescricao(String retDescricao) {
		this.retDescricao = retDescricao;
	}


	/**
	 * @return the pacNome
	 */
	public String getPacNome() {
		return pacNome;
	}


	/**
	 * @param pacNome the pacNome to set
	 */
	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}


	/**
	 * @return the cnvDescricao
	 */
	public String getCnvDescricao() {
		return cnvDescricao;
	}


	/**
	 * @param cnvDescricao the cnvDescricao to set
	 */
	public void setCnvDescricao(String cnvDescricao) {
		this.cnvDescricao = cnvDescricao;
	}


	/**
	 * @return the cspDescricao
	 */
	public String getCspDescricao() {
		return cspDescricao;
	}


	/**
	 * @param cspDescricao the cspDescricao to set
	 */
	public void setCspDescricao(String cspDescricao) {
		this.cspDescricao = cspDescricao;
	}


	/**
	 * @return the pesNome
	 */
	public String getPesNome() {
		return pesNome;
	}


	/**
	 * @param pesNome the pesNome to set
	 */
	public void setPesNome(String pesNome) {
		this.pesNome = pesNome;
	}


	/**
	 * @return the cspCnvCodigo
	 */
	public Short getCspCnvCodigo() {
		return cspCnvCodigo;
	}


	/**
	 * @param cspCnvCodigo the cspCnvCodigo to set
	 */
	public void setCspCnvCodigo(Short cspCnvCodigo) {
		this.cspCnvCodigo = cspCnvCodigo;
	}


	/**
	 * @return the serVinCodigo
	 */
	public Short getSerVinCodigo() {
		return serVinCodigo;
	}


	/**
	 * @param serVinCodigo the serVinCodigo to set
	 */
	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}


	/**
	 * @return the cspSeq
	 */
	public Byte getCspSeq() {
		return cspSeq;
	}


	/**
	 * @param cspSeq the cspSeq to set
	 */
	public void setCspSeq(Byte cspSeq) {
		this.cspSeq = cspSeq;
	}

	

	
}
