package br.gov.mec.aghu.compras.vo;

import java.io.Serializable;
import java.util.Date;

public class HorarioSemanaVO implements Serializable {

	/**
	 * 
	 */

	private static final long serialVersionUID = -7054020593896336204L;

	private Date horario;
	private Integer quantidadeItem;
	
	public HorarioSemanaVO() {
		super();
	}

	public Date getHorario() {
		return horario;
	}

	public void setHorario(Date horario) {
		this.horario = horario;
	}

	public Integer getQuantidadeItem() {
		return quantidadeItem;
	}

	public void setQuantidadeItem(Integer quantidadeItem) {
		this.quantidadeItem = quantidadeItem;
	}
}
