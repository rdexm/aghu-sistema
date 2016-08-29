package br.gov.mec.aghu.bancosangue.vo;

import java.io.Serializable;
import java.util.Date;


public class AtualizaInativacaoSolicitacaoHemoterapicaVO implements
		Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2283585684164233177L;

	private Integer matriculaServidorDesativou;

	private Short vinCodigoServidorDesativou;

	private Date dthrDesativacao;

	public AtualizaInativacaoSolicitacaoHemoterapicaVO() {
	}

	public AtualizaInativacaoSolicitacaoHemoterapicaVO(
			Integer matriculaServidorDesativou,
			Short vinCodigoServidorDesativou, Date dthrDesativacao) {
		this.matriculaServidorDesativou = matriculaServidorDesativou;
		this.vinCodigoServidorDesativou = vinCodigoServidorDesativou;
		this.dthrDesativacao = dthrDesativacao;
	}

	public Integer getMatriculaServidorDesativou() {
		return matriculaServidorDesativou;
	}

	public void setMatriculaServidorDesativou(Integer matriculaServidorDesativou) {
		this.matriculaServidorDesativou = matriculaServidorDesativou;
	}

	public Short getVinCodigoServidorDesativou() {
		return vinCodigoServidorDesativou;
	}

	public void setVinCodigoServidorDesativou(Short vinCodigoServidorDesativou) {
		this.vinCodigoServidorDesativou = vinCodigoServidorDesativou;
	}

	public Date getDthrDesativacao() {
		return dthrDesativacao;
	}

	public void setDthrDesativacao(Date dthrDesativacao) {
		this.dthrDesativacao = dthrDesativacao;
	}

}
