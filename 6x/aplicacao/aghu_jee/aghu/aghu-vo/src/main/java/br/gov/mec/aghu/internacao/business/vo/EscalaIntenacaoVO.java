package br.gov.mec.aghu.internacao.business.vo;

import java.util.Date;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.core.commons.BaseBean;

public class EscalaIntenacaoVO implements BaseBean{

	private static final long serialVersionUID = 822341529810846326L;

	private Short vinculoServidorSubstituto;
	private Integer matriculaServidorSubstituto;
	private String nomeServidorSubstituto;
	private String numeroRegistroSubstituto;
	private Date dataInicio;
	private Date dataFim;
	private DominioSimNao indCTI;

	// getters and setters
	public Short getVinculoServidorSubstituto() {
		return vinculoServidorSubstituto;
	}

	public void setVinculoServidorSubstituto(Short vinculoServidorSubstituto) {
		this.vinculoServidorSubstituto = vinculoServidorSubstituto;
	}

	public Integer getMatriculaServidorSubstituto() {
		return matriculaServidorSubstituto;
	}

	public void setMatriculaServidorSubstituto(
			Integer matriculaServidorSubstituto) {
		this.matriculaServidorSubstituto = matriculaServidorSubstituto;
	}

	public String getNomeServidorSubstituto() {
		return nomeServidorSubstituto;
	}

	public void setNomeServidorSubstituto(String nomeServidorSubstituto) {
		this.nomeServidorSubstituto = nomeServidorSubstituto;
	}

	public String getNumeroRegistroSubstituto() {
		return numeroRegistroSubstituto;
	}

	public void setNumeroRegistroSubstituto(String numeroRegistroSubstituto) {
		this.numeroRegistroSubstituto = numeroRegistroSubstituto;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date datafim) {
		this.dataFim = datafim;
	}

	public DominioSimNao getIndCTI() {
		return indCTI;
	}

	public void setIndCTI(DominioSimNao indCTI) {
		this.indCTI = indCTI;
	}
}
