package br.gov.mec.aghu.ambulatorio.vo;

import java.io.Serializable;
import java.util.Date;

/**
 * Representa o objeto de retorno do resultado da consulta realizada pelo serviço #34380
 * Identifica se teve uma especialidade alterada
 * @author felipe.rocha
 * 
 */
public class ConsultaEspecialidadeAlteradaRetornoVO implements Serializable {

	private static final long serialVersionUID = -4195817103912553934L;

	private Integer grdSeq;
	private Date jnDateTime;
	private Short espSeq;

	public Integer getGrdSeq() {
		return grdSeq;
	}

	public void setGrdSeq(Integer grdSeq) {
		this.grdSeq = grdSeq;
	}

	public Date getJnDateTime() {
		return jnDateTime;
	}

	public void setJnDateTime(Date jnDateTime) {
		this.jnDateTime = jnDateTime;
	}

	public Short getEspSeq() {
		return espSeq;
	}

	public void setEspSeq(Short espSeq) {
		this.espSeq = espSeq;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
}
