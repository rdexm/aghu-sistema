package br.gov.mec.aghu.transplante.vo;

import java.util.List;

public class RelatorioPermanenciaPacienteListaTransplanteVO  implements Comparable<RelatorioPermanenciaPacienteListaTransplanteVO>{

	private String prontuario;
	private String nome;
	private String tipo;
	private String permanencia;
	private String escore;
	private List<RelatorioPermanenciaFasesVO> fases;
	
	public List<RelatorioPermanenciaFasesVO> getFases() {
		return fases;
	}
	public void setFases(List<RelatorioPermanenciaFasesVO> fases) {
		this.fases = fases;
	}
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
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getPermanencia() {
		return permanencia;
	}
	public void setPermanencia(String permanencia) {
		this.permanencia = permanencia;
	}
	public String getEscore() {
		return escore;
	}
	public void setEscore(String escore) {
		this.escore = escore;
	}
	@Override
	public int compareTo(RelatorioPermanenciaPacienteListaTransplanteVO outro) {
		if(Integer.valueOf(this.permanencia) < Integer.valueOf(outro.getPermanencia())){
			return -1;
		}else if(Integer.valueOf(this.permanencia) > Integer.valueOf(outro.getPermanencia())){
		   return 1;	
		}else{
			return 0;
		}
	}
	
	
	
	
	
	
	
}
