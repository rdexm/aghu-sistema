package br.gov.mec.aghu.ambulatorio.vo;

import java.util.Date;

import br.gov.mec.aghu.model.AacSituacaoConsultas;

public class ConsultaDisponibilidadeHorarioVO {
	private Date dataConsulta;
	private AacSituacaoConsultas situacaoConsulta;
	private Boolean excedeProgramacao;
	
	public ConsultaDisponibilidadeHorarioVO(Date dataConsulta, AacSituacaoConsultas situacao, Boolean excedeProgramacao) {
		this.dataConsulta = dataConsulta;
		this.situacaoConsulta = situacao;
		this.excedeProgramacao = excedeProgramacao;
	}
	
	
	public Date getDataConsulta() {
		return dataConsulta;
	}


	public void setDataConsulta(Date dataConsulta) {
		this.dataConsulta = dataConsulta;
	}


	public AacSituacaoConsultas getSituacaoConsulta() {
		return situacaoConsulta;
	}
	public void setSituacaoConsulta(AacSituacaoConsultas situacaoConsulta) {
		this.situacaoConsulta = situacaoConsulta;
	}


	public Boolean getExcedeProgramacao() {
		return excedeProgramacao;
	}


	public void setExcedeProgramacao(Boolean excedeProgramacao) {
		this.excedeProgramacao = excedeProgramacao;
	}
	
	
	

}
