package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;

import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class DescricaoProcDiagTerapVO implements Serializable  {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5811566816823021846L;
	
	private String descricaoPaciente;
	private String leito;
	private AghEspecialidades especialidade;
	private Integer ddtSeq;
	private Integer ddtCrgSeq;
	private AghUnidadesFuncionais unidadeFuncional;
	private Short espSeqCirurgia;
	
	public DescricaoProcDiagTerapVO() {
	
	}
	
	public String getDescricaoPaciente() {
		return descricaoPaciente;
	}
	
	public void setDescricaoPaciente(String descricaoPaciente) {
		this.descricaoPaciente = descricaoPaciente;
	}
	
	public String getLeito() {
		return leito;
	}
	
	public void setLeito(String leito) {
		this.leito = leito;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public Integer getDdtSeq() {
		return ddtSeq;
	}

	public void setDdtSeq(Integer ddtSeq) {
		this.ddtSeq = ddtSeq;
	}

	public Integer getDdtCrgSeq() {
		return ddtCrgSeq;
	}

	public void setDdtCrgSeq(Integer ddtCrgSeq) {
		this.ddtCrgSeq = ddtCrgSeq;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Short getEspSeqCirurgia() {
		return espSeqCirurgia;
	}

	public void setEspSeqCirurgia(Short espSeqCirurgia) {
		this.espSeqCirurgia = espSeqCirurgia;
	}

}
