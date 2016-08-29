package br.gov.mec.aghu.exames.agendamento.vo;

import br.gov.mec.aghu.model.AelHorarioExameDisp;
import br.gov.mec.aghu.core.commons.BaseBean;


/**
 * 
 * @author diego.pacheco
 *
 */
public class HorarioExameVO implements BaseBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3722842439934499759L;
	
	private AelHorarioExameDisp horarioExame;
	private Boolean selecionado;
	
	public HorarioExameVO() {
		
	}
	
	public HorarioExameVO(AelHorarioExameDisp horarioExame) {
		this.horarioExame = horarioExame;
		this.selecionado = Boolean.FALSE;
	}
	
	public AelHorarioExameDisp getHorarioExame() {
		return horarioExame;
	}
	
	public void setHorarioExame(AelHorarioExameDisp horarioExame) {
		this.horarioExame = horarioExame;
	}
	
	public Boolean getSelecionado() {
		return selecionado;
	}
	
	public void setSelecionado(Boolean selecionado) {
		this.selecionado = selecionado;
	}

}
