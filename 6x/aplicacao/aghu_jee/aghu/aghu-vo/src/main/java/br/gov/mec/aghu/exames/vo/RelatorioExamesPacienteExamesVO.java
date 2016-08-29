package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.List;

public class RelatorioExamesPacienteExamesVO implements Serializable {

	private static final long serialVersionUID = 7740992269169782839L;
	private String tipoExame;
	private String rodape;
	private List<RelatorioExamesPacientesListaLegendaVO> listExamesPacientesListaLegendaVO;
	private List<RelatorioExamesPacientesListaObservacaoVO> listExamesPacientesListaObservacaoVO;
	private List<RelatorioExamesPacienteExamesDetalhesVO> listPacienteExamesDetalhesVO;

	
	public String getTipoExame() {
		return tipoExame;
	}
	public void setTipoExame(String tipoExame) {
		this.tipoExame = tipoExame;
	}
	public List<RelatorioExamesPacienteExamesDetalhesVO> getListPacienteExamesDetalhesVO() {
		return listPacienteExamesDetalhesVO;
	}
	public void setListPacienteExamesDetalhesVO(
			List<RelatorioExamesPacienteExamesDetalhesVO> listPacienteExamesDetalhesVO) {
		this.listPacienteExamesDetalhesVO = listPacienteExamesDetalhesVO;
	}
	public String getRodape() {
		return rodape;
	}
	public void setRodape(String rodape) {
		this.rodape = rodape;
	}
	public List<RelatorioExamesPacientesListaLegendaVO> getListExamesPacientesListaLegendaVO() {
		return listExamesPacientesListaLegendaVO;
	}
	public void setListExamesPacientesListaLegendaVO(
			List<RelatorioExamesPacientesListaLegendaVO> listExamesPacientesListaLegendaVO) {
		this.listExamesPacientesListaLegendaVO = listExamesPacientesListaLegendaVO;
	}
	public List<RelatorioExamesPacientesListaObservacaoVO> getListExamesPacientesListaObservacaoVO() {
		return listExamesPacientesListaObservacaoVO;
	}
	public void setListExamesPacientesListaObservacaoVO(
			List<RelatorioExamesPacientesListaObservacaoVO> listExamesPacientesListaObservacaoVO) {
		this.listExamesPacientesListaObservacaoVO = listExamesPacientesListaObservacaoVO;
	}

	
	
}
