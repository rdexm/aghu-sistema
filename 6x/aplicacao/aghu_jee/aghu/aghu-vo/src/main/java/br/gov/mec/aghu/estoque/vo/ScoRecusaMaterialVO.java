package br.gov.mec.aghu.estoque.vo;

import java.io.Serializable;
import java.util.Date;

public class ScoRecusaMaterialVO implements Serializable {
	
	private static final long serialVersionUID = -7813289724443949845L;

	private Long seq;
	
	private Long dfeNumero;
	
	private String dfeSerie;
	
	private Integer frnNumero;
	
	private Long ctNumero;
	
	private String ctSerie;
	
	private Integer traNumero;
	
	private Integer eslSeq;
	
	private Integer afnNumero;
	
	private Integer iafNumero;
	
	private Short parcela;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private Short numComplemento;
	
	private String frnNome;
	
	private String traNome;
	
	private Integer serMatricula;
	
	private Short serVinCodigo;
	
	private String descricao;
	
	private Date dtInclusao;
	
	private Short jusCodigo;
	
	private String indEnviaEmail;
	
	public enum Fields {

		SEQ("seq"),
		DFE_NUMERO("dfeNumero"),
		DFE_SERIE("dfeSerie"),
		FRN_NUMERO("frnNumero"),
		CT_NUMERO("ctNumero"),
		CT_SERIE("ctSerie"),
		TRA_NUMERO("traNumero"),
		ESL_SEQ("eslSeq"),
		AFN_NUMERO("afnNumero"),
		IAF_NUMERO("iafNumero"),
		PARCELA("parcela"),
		FRN_NOME("frnNome"),
		TRA_NOME("traNome"),
		SER_MATRICULA("serMatricula"),
		SER_VIN_CODIGO("serVinCodigo"),
		DESCRICAO("descricao"),
		DT_INCLUSAO("dtInclusao"),
		JUS_CODIGO("jusCodigo"),
		IND_ENVIA_EMAIL("indEnviaEmail");
		
		private String fields;

		private Fields(final String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}
		
	}

	public Long getSeq() {
		return seq;
	}

	public void setSeq(Long seq) {
		this.seq = seq;
	}

	public Long getDfeNumero() {
		return dfeNumero;
	}

	public void setDfeNumero(Long dfeNumero) {
		this.dfeNumero = dfeNumero;
	}

	public String getDfeSerie() {
		return dfeSerie;
	}

	public void setDfeSerie(String dfeSerie) {
		this.dfeSerie = dfeSerie;
	}

	public Integer getFrnNumero() {
		return frnNumero;
	}

	public void setFrnNumero(Integer frnNumero) {
		this.frnNumero = frnNumero;
	}

	public Long getCtNumero() {
		return ctNumero;
	}

	public void setCtNumero(Long ctNumero) {
		this.ctNumero = ctNumero;
	}

	public String getCtSerie() {
		return ctSerie;
	}

	public void setCtSerie(String ctSerie) {
		this.ctSerie = ctSerie;
	}

	public Integer getTraNumero() {
		return traNumero;
	}

	public void setTraNumero(Integer traNumero) {
		this.traNumero = traNumero;
	}

	public Integer getEslSeq() {
		return eslSeq;
	}

	public void setEslSeq(Integer eslSeq) {
		this.eslSeq = eslSeq;
	}

	public Integer getAfnNumero() {
		return afnNumero;
	}

	public void setAfnNumero(Integer afnNumero) {
		this.afnNumero = afnNumero;
	}

	public Integer getIafNumero() {
		return iafNumero;
	}

	public void setIafNumero(Integer iafNumero) {
		this.iafNumero = iafNumero;
	}

	public Short getParcela() {
		return parcela;
	}

	public void setParcela(Short parcela) {
		this.parcela = parcela;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public Short getNumComplemento() {
		return numComplemento;
	}

	public void setNumComplemento(Short numComplemento) {
		this.numComplemento = numComplemento;
	}

	public String getFrnNome() {
		return frnNome;
	}

	public void setFrnNome(String frnNome) {
		this.frnNome = frnNome;
	}

	public String getTraNome() {
		return traNome;
	}

	public void setTraNome(String traNome) {
		this.traNome = traNome;
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

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public Date getDtInclusao() {
		return dtInclusao;
	}

	public void setDtInclusao(Date dtInclusao) {
		this.dtInclusao = dtInclusao;
	}

	public Short getJusCodigo() {
		return jusCodigo;
	}

	public void setJusCodigo(Short jusCodigo) {
		this.jusCodigo = jusCodigo;
	}

	public String getIndEnviaEmail() {
		return indEnviaEmail;
	}

	public void setIndEnviaEmail(String indEnviaEmail) {
		this.indEnviaEmail = indEnviaEmail;
	}
	
}
