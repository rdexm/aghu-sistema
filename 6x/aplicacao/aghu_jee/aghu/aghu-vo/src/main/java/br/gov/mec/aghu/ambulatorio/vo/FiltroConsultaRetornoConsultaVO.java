package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;

import br.gov.mec.aghu.dominio.DominioIndAbsenteismo;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;

public class FiltroConsultaRetornoConsultaVO implements Serializable {

	private static final long serialVersionUID = -4611980402783255431L;
	
	private Short seq;
	private String descricao;
	private DominioSituacao situacao;
	private DominioIndAbsenteismo absenteismo;
	private DominioSimNao dominioSimNao;
	
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
	public DominioSituacao getSituacao() {
		return situacao;
	}
	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}
	public DominioIndAbsenteismo getAbsenteismo() {
		return absenteismo;
	}
	public void setAbsenteismo(DominioIndAbsenteismo absenteismo) {
		this.absenteismo = absenteismo;
	}
	public DominioSimNao getDominioSimNao() {
		return dominioSimNao;
	}
	public void setDominioSimNao(DominioSimNao dominioSimNao) {
		this.dominioSimNao = dominioSimNao;
	}

}
