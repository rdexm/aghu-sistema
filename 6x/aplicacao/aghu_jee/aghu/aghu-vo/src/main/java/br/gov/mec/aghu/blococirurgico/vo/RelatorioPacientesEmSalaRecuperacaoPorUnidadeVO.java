package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;


public class RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO implements Serializable {
	
	private static final long serialVersionUID = -962601384321968243L;

	private String unidade; 				//2
	private Date dataHora;					//3
	private String pacNome;					//4
	private String quarto; 					//5
	private Date dthrEntrada;				//6
	private String cirurgiao;				//7	
	private String unidCti;		    		
	private String tituloHeader;
	private String prontuario;
	private String nomeFooter;

	
	public RelatorioPacientesEmSalaRecuperacaoPorUnidadeVO() {
		
	}
	
	public enum Fields {

		PAC_NOME("pacNome"),
		CRG_DTHR_ENTRADA_SR("dthrEntrada"),
		UNID_CTI("unidCti");

		private String fields;

		private Fields(String fields) {
			this.fields = fields;
		}

		@Override
		public String toString() {
			return this.fields;
		}

	}

	//Getters and Setters
	
	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}
	
	public String getQuarto() {
		return quarto;
	}

	public void setQuarto(String quarto) {
		this.quarto = quarto;
	}

	public Date getDthrEntrada() {
		return dthrEntrada;
	}

	public void setDthrEntrada(Date dthrEntrada) {
		this.dthrEntrada = dthrEntrada;
	}

	public String getCirurgiao() {
		return cirurgiao;
	}

	public void setCirurgiao(String cirurgiao) {
		this.cirurgiao = cirurgiao;
	}

	public void setUnidade(String unidade) {
		this.unidade = unidade;
	}

	public String getUnidade() {
		return unidade;
	}
	
	public Date getDataHora() {
		return dataHora;
	}

	public void setDataHora(Date dataHora) {
		this.dataHora = dataHora;
	}

	public void setUnidCti(String unidCti) {
		this.unidCti = unidCti;
	}

	public String getUnidCti() {
		return unidCti;
	}
	
	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getTituloHeader() {
		return tituloHeader;
	}

	public String getNomeFooter() {
		return nomeFooter;
	}

	public void setTituloHeader(String tituloHeader) {
		this.tituloHeader = tituloHeader;
	}

	public void setNomeFooter(String nomeFooter) {
		this.nomeFooter = nomeFooter;
	}
}