package br.gov.mec.aghu.farmacia.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterRotinaFuncionamentoController extends ActionController{
	
	private static final long serialVersionUID = 5292732082797030234L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private AghUnidadesFuncionais aghUnidadesFuncionais;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidades(String param) {
		return  this.aghuFacade.obterUnidadesFuncionais(param);
	}
	
	public void gravar(){
		try{
			this.aghuFacade.atualizarAghUnidadesFuncionais(this.aghUnidadesFuncionais);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_GRAVAR_ROTINA_FUNCIONAMENTO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String limpar() {
		this.aghUnidadesFuncionais = null;
		return "manterRotinaFuncionamento";
	}
	//Getters and Setters

	public AghUnidadesFuncionais getAghUnidadesFuncionais() {
		return aghUnidadesFuncionais;
	}

	public void setAghUnidadesFuncionais(AghUnidadesFuncionais aghUnidadesFuncionais) {
		this.aghUnidadesFuncionais = aghUnidadesFuncionais;
	}
}
