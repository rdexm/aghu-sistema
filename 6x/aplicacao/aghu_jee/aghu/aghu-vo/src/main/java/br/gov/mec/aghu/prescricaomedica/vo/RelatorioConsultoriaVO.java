package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Contem os dados do relatório de consultoria gerado ao confirmar a prescrição médica.
 * 
 * @author ptneto
 *
 */

public class RelatorioConsultoriaVO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1815292670442716678L;
	private String nomeMedico;
	private String conselho;
	private String codConselho;
	private String nomePaciente;
	private String prontuario;
	private String leito;
	private Date solicitadoEm;
	private String tipoConsultoria;
	

	private List<RelatorioConsultoriaSubRelVO> dadosSubRel;

	public String getNomeMedico() {
		return nomeMedico;
	}

	public void setNomeMedico(String nomeMedico) {
		this.nomeMedico = nomeMedico;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}

	public String getCodConselho() {
		return codConselho;
	}

	public void setCodConselho(String codConselho) {
		this.codConselho = codConselho;
	}

	public String getNomePaciente() {
		return nomePaciente;
	}

	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getProntuario() {
		return prontuario;
	}

	public void setProntuario(String prontuario) {
		this.prontuario = prontuario;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Date getSolicitadoEm() {
		return solicitadoEm;
	}

	public void setSolicitadoEm(Date solicitadoEm) {
		this.solicitadoEm = solicitadoEm;
	}

	public List<RelatorioConsultoriaSubRelVO> getDadosSubRel() {
		return dadosSubRel;
	}

	public void setDadosSubRel(List<RelatorioConsultoriaSubRelVO> dadosSubRel) {
		this.dadosSubRel = dadosSubRel;
	}
	public String getTipoConsultoria() {
		return tipoConsultoria;
	}

	public void setTipoConsultoria(String tipoConsultoria) {
		this.tipoConsultoria = tipoConsultoria;
	}

}
