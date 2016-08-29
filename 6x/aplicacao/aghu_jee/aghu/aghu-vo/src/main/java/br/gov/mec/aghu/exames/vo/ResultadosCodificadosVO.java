package br.gov.mec.aghu.exames.vo;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AelGrupoResultadoCodificado;

public class ResultadosCodificadosVO {
	
	private Integer seq;
	private String descricao;
	private DominioSituacao situacao;
	private AelGrupoResultadoCodificado grupoResultadoCodificado;
	
	private Boolean bacteriaVirusFungo;
	private Boolean positivoCci;
	
	private DominioSimNao bacteriaVirusFungoAux;
	private DominioSimNao positivoCciAux;
	
	public ResultadosCodificadosVO() {
	}

	public Integer getSeq() {
		return seq;
	}

	public void setSeq(Integer seq) {
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

	public AelGrupoResultadoCodificado getGrupoResultadoCodificado() {
		return grupoResultadoCodificado;
	}

	public void setGrupoResultadoCodificado(
			AelGrupoResultadoCodificado grupoResultadoCodificado) {
		this.grupoResultadoCodificado = grupoResultadoCodificado;
	}

	public Boolean getBacteriaVirusFungo() {
		return bacteriaVirusFungo;
	}

	public void setBacteriaVirusFungo(Boolean bacteriaVirusFungo) {
		this.bacteriaVirusFungo = bacteriaVirusFungo;
	}

	public Boolean getPositivoCci() {
		return positivoCci;
	}

	public void setPositivoCci(Boolean positivoCci) {
		this.positivoCci = positivoCci;
	}

	public DominioSimNao getPositivoCciAux() {
		return positivoCciAux;
	}

	public void setPositivoCciAux(DominioSimNao positivoCciAux) {
		this.positivoCci = (positivoCciAux !=null) ? positivoCciAux.isSim() : null;
		this.positivoCciAux = positivoCciAux;
	}

	public DominioSimNao getBacteriaVirusFungoAux() {
		return bacteriaVirusFungoAux;
	}

	public void setBacteriaVirusFungoAux(DominioSimNao bacteriaVirusFungoAux) {
		this.bacteriaVirusFungo  = (bacteriaVirusFungoAux != null) ? bacteriaVirusFungoAux.isSim() : null;
		this.bacteriaVirusFungoAux = bacteriaVirusFungoAux;
	}	
	
}
