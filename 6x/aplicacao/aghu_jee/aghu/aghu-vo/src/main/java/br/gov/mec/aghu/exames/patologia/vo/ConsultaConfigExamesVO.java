package br.gov.mec.aghu.exames.patologia.vo;

import br.gov.mec.aghu.dominio.DominioSituacao;

/**
 * Consulta à Configuração de Exames
 * 
 * @author matheus
 */
public class ConsultaConfigExamesVO {
	private Integer firstResult;
	private Integer maxResult;
	private Integer seq;
	private String nome; 
	private String sigla; 
	private DominioSituacao situacao;
	private Boolean micro;
	private Boolean laudoAnterior;
	
	// Getters/Setters
	
	public Integer getFirstResult() {
		return firstResult;
	}
	
	public void setFirstResult(Integer firstResult) {
		this.firstResult = firstResult;
	}
	
	public Integer getMaxResult() {
		return maxResult;
	}
	
	public void setMaxResult(Integer maxResult) {
		this.maxResult = maxResult;
	}
	
	public Integer getSeq() {
		return seq;
	}
	
	public void setSeq(Integer seq) {
		this.seq = seq;
	}
	
	public String getNome() {
		return nome;
	}
	
	public void setNome(String nome) {
		this.nome = nome;
	}
	
	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}
	
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	
	public Boolean getMicro() {
		return micro;
	}
	
	public void setMicro(Boolean micro) {
		this.micro = micro;
	}
	
	public Boolean getLaudoAnterior() {
		return laudoAnterior;
	}
	
	public void setLaudoAnterior(Boolean laudoAnterior) {
		this.laudoAnterior = laudoAnterior;
	}
}