package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class TipoUsoMdtoController extends ActionController{

	private static final long serialVersionUID = 7695914839787885851L;

	private final String REDIRECIONA_TIPO_USO_MDTO = "tipoUsoMedicamentoList";
	
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	private AfaTipoUsoMdto afaTipoUsoMdto;
	private Boolean novoRegistro;
	
	public AfaTipoUsoMdto getAfaTipoUsoMdto() {
		return afaTipoUsoMdto;
	}

	public void setAfaTipoUsoMdto(AfaTipoUsoMdto afaTipoUsoMdto) {
		this.afaTipoUsoMdto = afaTipoUsoMdto;
	}

	public String confirmar() {
		try {
			this.farmaciaApoioFacade.inserirAtualizarTipoUsoMdto(this.afaTipoUsoMdto, novoRegistro);
			if (novoRegistro) {
				// Inclusão
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_TIPO_USO_MEDICAMENTOS",
						this.afaTipoUsoMdto.getDescricao());
			} else {
				// Alteração
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_TIPO_USO_MEDICAMENTOS",
						this.afaTipoUsoMdto.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_TIPO_USO_MDTO;
	}
	
	public String cancelar() {
		return REDIRECIONA_TIPO_USO_MDTO;
	}

	public Boolean getNovoRegistro() {
		return novoRegistro;
	}

	public void setNovoRegistro(Boolean novoRegistro) {
		this.novoRegistro = novoRegistro;
	}

}