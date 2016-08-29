package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaTipoOcorDispensacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class OcorrenciaController extends ActionController {

	private static final long serialVersionUID = -9080940970337693983L;
	
	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	@Inject
	private OcorrenciaPaginatorController ocorrenciaPaginatorController;

	private AfaTipoOcorDispensacao ocorrencia;
	private Boolean ativo;

	/**
	 * Confirma o Cadastro/Edição
	 * 
	 * @return
	 */
	public String confirmar() {
		ocorrenciaPaginatorController.getDataModel().reiniciarPaginator();
		try {
			// Verifica se é edição ou criação
			boolean create = this.ocorrencia.getSeq() == null;
			farmaciaApoioFacade.persistirOcorrencia(this.ocorrencia);
			if (create) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_OCORRENCIA",
						this.ocorrencia.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_OCORRENCIA",
						this.ocorrencia.getDescricao());
			}
			this.ocorrencia = new AfaTipoOcorDispensacao();
			this.ocorrencia.setSeq(null);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return "ocorrenciaList";
	}

	/**
	 * Cancela o Cadastro/Edição
	 * 
	 * @return
	 */
	public String cancelar() {
		this.ocorrencia.setSeq(null);
		return "ocorrenciaList";
	}

	public Boolean getIsUpdate() {
		if (this.ocorrencia.getSeq() == null) {
			return false;
		} else {
			return true;
		}
	}

	public Boolean getAtivo() {
		return ativo;
	}

	public void setAtivo(Boolean ativo) {
		this.ativo = ativo;
	}

	public AfaTipoOcorDispensacao getOcorrencia() {
		return ocorrencia;
	}

	public void setOcorrencia(AfaTipoOcorDispensacao ocorrencia) {
		this.ocorrencia = ocorrencia;
	}
}