package br.gov.mec.aghu.internacao.business.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioLocalPaciente;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;


public class AtualizaDadosPacienteVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1866034930178119013L;

	private Date dataInternacao;

	private DominioLocalPaciente indLocalPaciente;

	private AinLeitos leito;

	private AinQuartos quarto;

	private AghUnidadesFuncionais unidadeFuncional;

	private Date dthrAltaMedica;

	public AtualizaDadosPacienteVO() {
	}

	public AtualizaDadosPacienteVO(Date dataInternacao,
			DominioLocalPaciente indLocalPaciente, AinLeitos leito,
			AinQuartos quarto, AghUnidadesFuncionais unidadeFuncional,
			Date dthrAltaMedica) {
		this.dataInternacao = dataInternacao;
		this.indLocalPaciente = indLocalPaciente;
		this.leito = leito;
		this.quarto = quarto;
		this.unidadeFuncional = unidadeFuncional;
		this.dthrAltaMedica = dthrAltaMedica;
	}

	public Date getDataInternacao() {
		return dataInternacao;
	}

	public void setDataInternacao(Date dataInternacao) {
		this.dataInternacao = dataInternacao;
	}

	public DominioLocalPaciente getIndLocalPaciente() {
		return indLocalPaciente;
	}

	public void setIndLocalPaciente(DominioLocalPaciente indLocalPaciente) {
		this.indLocalPaciente = indLocalPaciente;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public Date getDthrAltaMedica() {
		return dthrAltaMedica;
	}

	public void setDthrAltaMedica(Date dthrAltaMedica) {
		this.dthrAltaMedica = dthrAltaMedica;
	}

}
