package br.gov.mec.aghu.prescricaomedica.vo;

import java.io.Serializable;


public class AtualizaServidorPrescricaoMedicamentoAlteracaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8692657870716536028L;

	private Short vinCodigo;

	private Integer matricula;

	public AtualizaServidorPrescricaoMedicamentoAlteracaoVO() {
	}

	public AtualizaServidorPrescricaoMedicamentoAlteracaoVO(Short vinCodigo,
			Integer matricula) {
		this.vinCodigo = vinCodigo;
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

}
