package br.gov.mec.aghu.paciente.vo;

import java.util.List;

/**
 * Vo utilizado como retorno do método de obter dados da etiqueta.
 * 
 * @author riccosta
 * 
 */
public class ZplVo {
	String zpl;

	String vtznslInicial;

	String vtznslFinal;

	List<PacienteZplVo> listPacZplVo;

	public ZplVo(String zpl, String vtznslInicial, String vtznslFinal,
			List<PacienteZplVo> listPacZplVo) {
		super();
		this.zpl = zpl;
		this.vtznslInicial = vtznslInicial;
		this.vtznslFinal = vtznslFinal;
		this.listPacZplVo = listPacZplVo;
	}

	public String getZpl() {
		return zpl;
	}

	public void setZpl(String zpl) {
		this.zpl = zpl;
	}

	public String getVtznslInicial() {
		return vtznslInicial;
	}

	public void setVtznslInicial(String vtznslInicial) {
		this.vtznslInicial = vtznslInicial;
	}

	public String getVtznslFinal() {
		return vtznslFinal;
	}

	public void setVtznslFinal(String vtznslFinal) {
		this.vtznslFinal = vtznslFinal;
	}

	public List<PacienteZplVo> getListPacZplVo() {
		return listPacZplVo;
	}

	public void setListPacZplVo(List<PacienteZplVo> listPacZplVo) {
		this.listPacZplVo = listPacZplVo;
	}
	
	

}