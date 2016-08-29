package br.gov.mec.aghu.internacao.business.vo;

import java.io.Serializable;
import java.util.Date;

import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;


public class AtualizarContaInternacaoVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8630155115498428269L;

	private Integer seqContaHospitalar;

	private Date dataInternacaoAdministrativaContaHospitalar;

	private DominioSituacaoConta indSituacaoContaHospitalar;

	private Integer seqAtendimento;

	private DominioGrupoConvenio grupoConvenioConvenioSaude;

	public AtualizarContaInternacaoVO(Integer seqContaHospitalar,
			Date dataInternacaoAdministrativaContaHospitalar,
			DominioSituacaoConta indSituacaoContaHospitalar,
			Integer seqAtendimento,
			DominioGrupoConvenio grupoConvenioConvenioSaude) {
		this.seqContaHospitalar = seqContaHospitalar;
		this.dataInternacaoAdministrativaContaHospitalar = dataInternacaoAdministrativaContaHospitalar;
		this.indSituacaoContaHospitalar = indSituacaoContaHospitalar;
		this.seqAtendimento = seqAtendimento;
		this.grupoConvenioConvenioSaude = grupoConvenioConvenioSaude;
	}

	public Integer getSeqContaHospitalar() {
		return seqContaHospitalar;
	}

	public void setSeqContaHospitalar(Integer seqContaHospitalar) {
		this.seqContaHospitalar = seqContaHospitalar;
	}

	public Date getDataInternacaoAdministrativaContaHospitalar() {
		return dataInternacaoAdministrativaContaHospitalar;
	}

	public void setDataInternacaoAdministrativaContaHospitalar(
			Date dataInternacaoAdministrativaContaHospitalar) {
		this.dataInternacaoAdministrativaContaHospitalar = dataInternacaoAdministrativaContaHospitalar;
	}

	public DominioSituacaoConta getIndSituacaoContaHospitalar() {
		return indSituacaoContaHospitalar;
	}

	public void setIndSituacaoContaHospitalar(
			DominioSituacaoConta indSituacaoContaHospitalar) {
		this.indSituacaoContaHospitalar = indSituacaoContaHospitalar;
	}

	public Integer getSeqAtendimento() {
		return seqAtendimento;
	}

	public void setSeqAtendimento(Integer seqAtendimento) {
		this.seqAtendimento = seqAtendimento;
	}

	public DominioGrupoConvenio getGrupoConvenioConvenioSaude() {
		return grupoConvenioConvenioSaude;
	}

	public void setGrupoConvenioConvenioSaude(
			DominioGrupoConvenio grupoConvenioConvenioSaude) {
		this.grupoConvenioConvenioSaude = grupoConvenioConvenioSaude;
	}

}
