package br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.RapConselhosProfissionais;
import br.gov.mec.aghu.registrocolaborador.cadastrosbasicos.business.ICadastrosBasicosFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ConselhoProfissionalController extends ActionController {

	private static final long serialVersionUID = 2324087657741480409L;
	
	private static final String PESQUISAR_CONSELHO_PROFISSIONAL = "pesquisarConselhoProfissional";

	@EJB
	private ICadastrosBasicosFacade cadastrosBasicosFacade;

	private RapConselhosProfissionais conselhoProfissional;
	private boolean alterando;
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String cancelarCadastro() {
		conselhoProfissional = null;
		alterando = false;
		return PESQUISAR_CONSELHO_PROFISSIONAL;
	}

	public String iniciar() {
	 


		if (conselhoProfissional != null && conselhoProfissional.getCodigo() != null) {
			try {
				conselhoProfissional = cadastrosBasicosFacade.obterConselhoProfissional(conselhoProfissional.getCodigo());
				alterando = true;

				if(conselhoProfissional == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					return cancelarCadastro();
				}
			} catch (ApplicationBusinessException ex) {
				apresentarExcecaoNegocio(ex);
			}
			
		} else {
			this.conselhoProfissional = new RapConselhosProfissionais();
			this.conselhoProfissional.setIndSituacao(DominioSituacao.A);
		}
		
		return null;
	
	}

	public String salvar() {
		try {
			if (alterando) {
				cadastrosBasicosFacade.alterar(conselhoProfissional);
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_CONSELHO_PROFISSIONAL_ALTERADO_COM_SUCESSO");
				
			} else {
				cadastrosBasicosFacade.salvar(conselhoProfissional);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_CONSELHO_PROFISSIONAL_INCLUIDO_COM_SUCESSO");
			}
			
		} catch (ApplicationBusinessException ex) {
			apresentarExcecaoNegocio(ex);
			return null;
		}
		
		return cancelarCadastro();
	}

	public RapConselhosProfissionais getConselhoProfissional() {
		return conselhoProfissional;
	}

	public void setConselhoProfissional(
			RapConselhosProfissionais conselhoProfissional) {
		this.conselhoProfissional = conselhoProfissional;
	}

	public boolean isAlterando() {
		return alterando;
	}

	public void setAlterando(boolean alterando) {
		this.alterando = alterando;
	}
}