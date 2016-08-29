package br.gov.mec.aghu.paciente.vo;

/**
 * Dados utilizados no relatório re-impressão de etiquetas.
 * @author lalegre
 */
public class ReImpressaoEtiquetasVO { // implements JRDataSource
	
	String prontuario;
	String nome;
	String leito;
	
	String pProntuario;
	String pNome;
	String pLeito;
	
	
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public String getpProntuario() {
		return pProntuario;
	}
	public void setpProntuario(String pProntuario) {
		this.pProntuario = pProntuario;
	}
	public String getpNome() {
		return pNome;
	}
	public void setpNome(String pNome) {
		this.pNome = pNome;
	}
	public String getpLeito() {
		return pLeito;
	}
	public void setpLeito(String pLeito) {
		this.pLeito = pLeito;
	}
}
