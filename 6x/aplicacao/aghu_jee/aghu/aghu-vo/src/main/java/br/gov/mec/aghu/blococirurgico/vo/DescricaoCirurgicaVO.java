package br.gov.mec.aghu.blococirurgico.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.model.AghUnidadesFuncionais;

public class DescricaoCirurgicaVO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1264831120683486076L;
	
	private String descricaoPaciente;
	private String leito;
	private String descricaoEspecialidade;
	private Integer pacCodigo;
	private Integer prontuario;
	private Integer dcgCrgSeq;
	private Short dcgSeqp;
	private AghUnidadesFuncionais unidadeFuncional;
	private Date dataCirurgia;
	
	public DescricaoCirurgicaVO() {

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
	
	public String getDescricaoEspecialidade() {
		return descricaoEspecialidade;
	}
	
	public void setDescricaoEspecialidade(String descricaoEspecialidade) {
		this.descricaoEspecialidade = descricaoEspecialidade;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getDcgCrgSeq() {
		return dcgCrgSeq;
	}

	public void setDcgCrgSeq(Integer dcgCrgSeq) {
		this.dcgCrgSeq = dcgCrgSeq;
	}

	public Short getDcgSeqp() {
		return dcgSeqp;
	}

	public void setDcgSeqp(Short dcgSeqp) {
		this.dcgSeqp = dcgSeqp;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}
}
