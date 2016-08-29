package br.gov.mec.aghu.ambulatorio.vo;


public class GerarDiariasProntuariosSamisVO implements Comparable<GerarDiariasProntuariosSamisVO>{
	private String ordem;
	private String arquivista;
	private String prontuario;
	private String prontuario1;
	private String nome;
	private String dtNascimento;
	private String sigla;
	private String sala;
	private String dtConsulta;
	private String obs;
	
	public int compareTo(GerarDiariasProntuariosSamisVO other) {
		int result = this.getProntuario1().compareTo(other.getProntuario1());
        if (result == 0) {
                if(this.getArquivista() != null && other.getArquivista() != null){
                	result = this.getArquivista().compareTo(other.getArquivista());
                    if (result == 0) {
                            if(this.getProntuario() != null && other.getProntuario() != null){
                            	result = this.getProntuario().compareTo(other.getProntuario());
                            }                                
                    }
                }                                
        }
        return result;
	}
	
	public String getOrdem() {
		return ordem;
	}
	public void setOrdem(String ordem) {
		this.ordem = ordem;
	}
	public String getArquivista() {
		return arquivista;
	}
	public void setArquivista(String arquivista) {
		this.arquivista = arquivista;
	}
	public String getProntuario() {
		return prontuario;
	}
	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}
	public String getProntuario1() {
		return prontuario1;
	}
	public void setProntuario1(String prontuario1) {
		this.prontuario1 = prontuario1;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getDtNascimento() {
		return dtNascimento;
	}
	public void setDtNascimento(String dtNascimento) {
		this.dtNascimento = dtNascimento;
	}
	public String getSigla() {
		return sigla;
	}
	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	public String getSala() {
		return sala;
	}
	public void setSala(String sala) {
		this.sala = sala;
	}
	public String getDtConsulta() {
		return dtConsulta;
	}
	public void setDtConsulta(String dtConsulta) {
		this.dtConsulta = dtConsulta;
	}
	public String getObs() {
		return obs;
	}
	public void setObs(String obs) {
		this.obs = obs;
	}
}
