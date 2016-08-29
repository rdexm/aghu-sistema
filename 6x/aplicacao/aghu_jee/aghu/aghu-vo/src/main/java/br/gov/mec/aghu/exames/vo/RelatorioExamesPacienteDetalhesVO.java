package br.gov.mec.aghu.exames.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author aghu
 *
 */
public class RelatorioExamesPacienteDetalhesVO implements Serializable {

	private static final long serialVersionUID = -9092215235502657200L;
	private String dataHoraEvento; // 17 - DTHR_EVENTO_LIB de Q_SEM
	private String unfDescricao; // 18 - unf_descricao de Q_SEM
	private String manDescricao; // 19 - man_descricao de Q_SEM
	private String exaDescricao; // 20 - exa_descricao de Q_SEM
	private String descricaoMascLinha;  // 21 - DESCRICAO1 de Q_MASC_LINHA
	private String descricaoMascCampoEdit;  // 22 - descricao6 de Q_MASC_CAMPO_EDIT
	private List<RelatorioExamesPacienteDetalhesLinhaVO> listLinhas;
	private List<RelatorioExamesPacienteDetalhesCampoEditVO> listCamposEdit;
	
	//CAMPOS AUXILIARES
	private Integer ordemRelatorio;
	private Integer ordemAgrupamento;
	private Integer nroLinha;
	

	/**
	 * 
	 */
	public RelatorioExamesPacienteDetalhesVO() {
	}

	public String getDataHoraEvento() {
		return dataHoraEvento;
	}

	public void setDataHoraEvento(String dataHoraEvento) {
		this.dataHoraEvento = dataHoraEvento;
	}

	public String getUnfDescricao() {
		return unfDescricao;
	}

	public void setUnfDescricao(String unfDescricao) {
		this.unfDescricao = unfDescricao;
	}

	public String getManDescricao() {
		return manDescricao;
	}

	public void setManDescricao(String manDescricao) {
		this.manDescricao = manDescricao;
	}

	public String getExaDescricao() {
		return exaDescricao;
	}

	public void setExaDescricao(String exaDescricao) {
		this.exaDescricao = exaDescricao;
	}

	public String getDescricaoMascLinha() {
		return descricaoMascLinha;
	}

	public void setDescricaoMascLinha(String descricaoMascLinha) {
		this.descricaoMascLinha = descricaoMascLinha;
	}

	public String getDescricaoMascCampoEdit() {
		return descricaoMascCampoEdit;
	}

	public void setDescricaoMascCampoEdit(String descricaoMascCampoEdit) {
		this.descricaoMascCampoEdit = descricaoMascCampoEdit;
	}

	public Integer getOrdemRelatorio() {
		return ordemRelatorio;
	}

	public void setOrdemRelatorio(Integer ordemRelatorio) {
		this.ordemRelatorio = ordemRelatorio;
	}

	public Integer getNroLinha() {
		return nroLinha;
	}

	public void setNroLinha(Integer nroLinha) {
		this.nroLinha = nroLinha;
	}

	public Integer getOrdemAgrupamento() {
		return ordemAgrupamento;
	}

	public void setOrdemAgrupamento(Integer ordemAgrupamento) {
		this.ordemAgrupamento = ordemAgrupamento;
	}

	public List<RelatorioExamesPacienteDetalhesLinhaVO> getListLinhas() {
		return listLinhas;
	}

	public void setListLinhas(
			List<RelatorioExamesPacienteDetalhesLinhaVO> listLinhas) {
		this.listLinhas = listLinhas;
	}

	public List<RelatorioExamesPacienteDetalhesCampoEditVO> getListCamposEdit() {
		return listCamposEdit;
	}

	public void setListCamposEdit(
			List<RelatorioExamesPacienteDetalhesCampoEditVO> listCamposEdit) {
		this.listCamposEdit = listCamposEdit;
	}
	

}
