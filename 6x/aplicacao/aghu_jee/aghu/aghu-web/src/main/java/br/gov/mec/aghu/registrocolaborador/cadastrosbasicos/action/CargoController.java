package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapCargos;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CargoController extends ActionController {

	private static final long serialVersionUID = 9141706383085814000L;
	
	private static final String PESQUISAR_CARGO = "pesquisarCargo";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapCargos rapCargos;

	private boolean altera;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String iniciar() {
	 

		if (rapCargos != null && rapCargos.getCodigo() != null) {
			try {
				altera = true;
				this.rapCargos = this.cadastrosBasicosFacade.obterCargo(rapCargos.getCodigo());
				
				if(rapCargos == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelar();
				}

			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		} else {
			this.rapCargos = new RapCargos();
			this.rapCargos.setSituacao(DominioSituacao.A);
		}
		return null;
	
	}

	public String salvar() {
		try {

			if (altera) {
				this.cadastrosBasicosFacade.alterar(this.rapCargos);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_CARGO", this.rapCargos.getDescricao());
				
			} else {
				this.cadastrosBasicosFacade.salvar(this.rapCargos);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_CARGO", this.rapCargos.getDescricao());
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return cancelar();
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de cadastro
	 */
	public String cancelar() {
		rapCargos = null;
		altera = false;
		return PESQUISAR_CARGO;
	}

	public RapCargos getRapCargos() {
		return rapCargos;
	}

	public void setRapCargos(RapCargos rapCargos) {
		this.rapCargos = rapCargos;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}
}