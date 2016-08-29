package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.model.RapGrupoFuncional;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class GrupoFuncionalController extends ActionController {

	private static final long serialVersionUID = 5429273193945585877L;

	private static final String GRUPO_FUNCIONAL_PESQUISA = "pesquisarGrupoFuncional";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapGrupoFuncional rapGrupoFuncional;

	private boolean altera;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 


		if (rapGrupoFuncional != null && rapGrupoFuncional.getCodigo() != null) {
			rapGrupoFuncional = cadastrosBasicosFacade.obterRapGrupoFuncional(rapGrupoFuncional.getCodigo());
			altera = true;
			
			if(rapGrupoFuncional == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}

		} else {
			limparCampos();
		}
		
		return null;
	
	}

	public String salvar() {

		try {
			if (altera) {
				this.cadastrosBasicosFacade.editar(this.rapGrupoFuncional);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_GRUPO_FUNCIONAL");

			} else {

				this.cadastrosBasicosFacade.gravar(this.rapGrupoFuncional);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_GRUPO_FUNCIONAL");
			}
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		return null;
	}
	
	private void limparCampos(){
		this.rapGrupoFuncional = new RapGrupoFuncional();
		this.altera = false;
	}

	public String cancelar() {
		limparCampos();
		return GRUPO_FUNCIONAL_PESQUISA;
	}

	public RapGrupoFuncional getRapGrupoFuncional() {
		return rapGrupoFuncional;
	}

	public void setRapGrupoFuncional(RapGrupoFuncional rapGrupoFuncional) {
		this.rapGrupoFuncional = rapGrupoFuncional;
	}

	public boolean isAltera() {
		return altera;
	}

	public void setAltera(boolean altera) {
		this.altera = altera;
	}
}
