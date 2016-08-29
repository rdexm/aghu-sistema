package br.gov.mec.aghu.emergencia.perinatologia.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoTabAdequacaoPeso;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author Rafael Garcia
 */

public class CadastroAdequacaoPesoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3765844896472728206L;

	private static final String REDIRECIONA_LISTAR_CAPACIDADE_ATEND = "cadastroAdequacaoPesoList";

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	private CadastroAdequacaoPesoPaginatorController cadastroAdequacaoPesoPaginatorController;

	private Short seq;

	private McoTabAdequacaoPeso mcoTabAdequacaoPeso;

	@PostConstruct
	public void init() {
		begin(conversation);
	}

	public void inicio() {
		if (this.seq == null) {
			mcoTabAdequacaoPeso = new McoTabAdequacaoPeso();
		} else {
			this.mcoTabAdequacaoPeso = this.emergenciaFacade
					.obterMcoTabAdequacaoPesoPorSeq(seq);
		}
	}

	public String confirmarAdequacaoPeso() {
		try {
			this.emergenciaFacade.persistirMcoTabAdequacaoPeso(mcoTabAdequacaoPeso);
			if (this.seq == null) {
				this.apresentarMsgNegocio(Severity.INFO,
						"CADASTRO_ADEQUACAO_PESO_SUCESSO");
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						"ALTERACAO_ADEQUACAO_PESO_SUCESSO");
			}
			this.mcoTabAdequacaoPeso = null;
			this.seq = null;
			cadastroAdequacaoPesoPaginatorController.reiniciarPaginator();
			return REDIRECIONA_LISTAR_CAPACIDADE_ATEND;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro de
	 * Nacionalidades
	 */
	public String cancelarAdequacaoPeso() {
		this.seq = null;
		this.mcoTabAdequacaoPeso = null;
		return REDIRECIONA_LISTAR_CAPACIDADE_ATEND;
	}

	public Short getSeq() {
		return seq;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public McoTabAdequacaoPeso getMcoTabAdequacaoPeso() {
		return mcoTabAdequacaoPeso;
	}

	public void setMcoTabAdequacaoPeso(McoTabAdequacaoPeso mcoTabAdequacaoPeso) {
		this.mcoTabAdequacaoPeso = mcoTabAdequacaoPeso;
	}

}