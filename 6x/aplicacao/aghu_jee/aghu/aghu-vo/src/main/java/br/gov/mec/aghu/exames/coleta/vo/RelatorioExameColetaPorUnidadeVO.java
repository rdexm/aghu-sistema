package br.gov.mec.aghu.exames.coleta.vo;

import java.io.Serializable;
import java.util.List;

import br.gov.mec.aghu.core.commons.CoreUtil;

public class RelatorioExameColetaPorUnidadeVO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2349952462196429602L;
	private String leito;
	private Integer prontuario;
	private String nomePaciente;
	private String prontuarioFormatado;
	private List<SubRelatorioExameColetaPorUnidadeVO> subRelatorio;
	
	
	public RelatorioExameColetaPorUnidadeVO() {
		super();
	}
	
	public RelatorioExameColetaPorUnidadeVO(String leito, Integer prontuario,
			String nomePaciente) {
		super();
		this.leito = leito;
		this.prontuario = prontuario;
		this.nomePaciente = nomePaciente;
		this.prontuarioFormatado = CoreUtil.formataProntuarioRelatorio(prontuario);
	}


	public String getLeito() {
		return leito;
	}
	public void setLeito(String leito) {
		this.leito = leito;
	}
	public Integer getProntuario() {
		return prontuario;
	}
	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}
	public String getNomePaciente() {
		return nomePaciente;
	}
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}
	public String getProntuarioFormatado() {
		return prontuarioFormatado;
	}
	public void setProntuarioFormatado(String prontuarioFormatado) {
		this.prontuarioFormatado = prontuarioFormatado;
	}
	public List<SubRelatorioExameColetaPorUnidadeVO> getSubRelatorio() {
		return subRelatorio;
	}
	public void setSubRelatorio(List<SubRelatorioExameColetaPorUnidadeVO> subRelatorio) {
		this.subRelatorio = subRelatorio;
	}
	
}