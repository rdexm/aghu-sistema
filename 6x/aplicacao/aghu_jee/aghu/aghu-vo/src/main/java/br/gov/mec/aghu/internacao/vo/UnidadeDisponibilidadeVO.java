package br.gov.mec.aghu.internacao.vo;

import br.gov.mec.aghu.model.AghAla;

public class UnidadeDisponibilidadeVO {
	
	private Short seq;
	private String descricao;
	private String andar;
	private AghAla indAla;
	private Short capacInternacao;
	private Integer numeroVagasOcupadas = 0;
	private Integer numeroReservas = 0;
	private Integer numeroVagasIndisponiveis = 0;

	//GETTERs e SETTERs
	public Short getSeq() {
		return seq;
	}
	
	public void setSeq(Short seq) {
		this.seq = seq;
	}
	
	public String getDescricao() {
		return descricao;
	}
	
	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}
	
	public String getAndar() {
		return andar;
	}
	
	public void setAndar(String andar) {
		this.andar = andar;
	}
	
	public AghAla getIndAla() {
		return indAla;
	}
	
	public void setIndAla(AghAla indAla) {
		this.indAla = indAla;
	}
	
	public Short getCapacInternacao() {
		if (this.capacInternacao == null) {
			return Short.valueOf("0");
		} else {
			return capacInternacao;
		}
	}
	
	public void setCapacInternacao(Short capacInternacao) {
		this.capacInternacao = capacInternacao;
	}
	
	public Integer getNumeroVagasOcupadas() {
		return numeroVagasOcupadas;
	}
	
	public void setNumeroVagasOcupadas(Integer numeroVagasOcupadas) {
		this.numeroVagasOcupadas = numeroVagasOcupadas;
	}
	
	public Integer getNumeroReservas() {
		return numeroReservas;
	}
	
	public void setNumeroReservas(Integer numeroReservas) {
		this.numeroReservas = numeroReservas;
	}
	
	public Integer getNumeroVagasIndisponiveis() {
		return numeroVagasIndisponiveis;
	}
	
	public void setNumeroVagasIndisponiveis(Integer numeroVagasIndisponiveis) {
		this.numeroVagasIndisponiveis = numeroVagasIndisponiveis;
	}
	
	public Integer getNumeroVagas() {
		Short capacidade = this.capacInternacao == null ? 0 : this.capacInternacao;
		Integer vagasIndisponiveis = this.numeroVagasIndisponiveis == null ? 0 : this.numeroVagasIndisponiveis;
		Integer vagasOcupadas = this.numeroVagasOcupadas == null ? 0 : this.numeroVagasOcupadas;

		return (capacidade - vagasOcupadas - vagasIndisponiveis);
	}
	
	public String getDescricaoCompleta() {
		if (this.getAndar() == null) {
			return null;
		} else {
			
			StringBuffer descricao = new StringBuffer();
			
			if(this.getAndar()!=null){
				descricao.append(this.getAndar().toString()).append(' ');
			}
			if (this.getIndAla() != null){
				descricao.append(this.getIndAla().getDescricao().toString()).append(" - ");
			} else{
				descricao.append(" - ");
			}
			if (this.getDescricao() != null){
				descricao.append(this.getDescricao());	
			}
			
			return descricao.toString();
		}
	}
}
