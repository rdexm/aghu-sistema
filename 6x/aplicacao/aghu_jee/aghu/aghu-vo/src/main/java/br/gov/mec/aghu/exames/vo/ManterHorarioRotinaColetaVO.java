package br.gov.mec.aghu.exames.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioDiaSemanaFeriado;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class ManterHorarioRotinaColetaVO{

	private AghUnidadesFuncionais unidadeColeta;
	private AghUnidadesFuncionais unidadeSolic;
	private DominioSituacao situacaoHorario;
	private Date horario;
	private DominioDiaSemanaFeriado diaSemana;

	public AghUnidadesFuncionais getUnidadeColeta() {
		return unidadeColeta;
	}
	public void setUnidadeColeta(AghUnidadesFuncionais unidadeColeta) {
		this.unidadeColeta = unidadeColeta;
	}
	public AghUnidadesFuncionais getUnidadeSolic() {
		return unidadeSolic;
	}
	public void setUnidadeSolic(AghUnidadesFuncionais unidadeSolic) {
		this.unidadeSolic = unidadeSolic;
	}
	public DominioSituacao getSituacaoHorario() {
		return situacaoHorario;
	}
	public void setSituacaoHorario(DominioSituacao situacaoHorario) {
		this.situacaoHorario = situacaoHorario;
	}
	public Date getHorario() {
		return horario;
	}
	public void setHorario(Date horario) {
		this.horario = horario;
	}
	public DominioDiaSemanaFeriado getDiaSemana() {
		return diaSemana;
	}
	public void setDiaSemana(DominioDiaSemanaFeriado diaSemana) {
		this.diaSemana = diaSemana;
	} 
	
	
		
	
	
}
