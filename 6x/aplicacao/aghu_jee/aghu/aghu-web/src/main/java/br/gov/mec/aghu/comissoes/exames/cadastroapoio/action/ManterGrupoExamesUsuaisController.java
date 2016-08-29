package br.gov.mec.aghu.comissoes.exames.cadastroapoio.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class ManterGrupoExamesUsuaisController extends ActionController {

	private static final long serialVersionUID = -2301271107376378950L;

	private static final String MANTER_GRUPO_EXAMES_USUAIS_PESQUISA = "manterGrupoExamesUsuaisPesquisa";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private AelGrupoExameUsual grupoExameUsual;
	
	public String iniciar() {
	 

	 

		if(grupoExameUsual != null && grupoExameUsual.getSeq() != null) {
			
			grupoExameUsual = this.examesFacade.obterAelGrupoExameUsualPorId(grupoExameUsual.getSeq());

			if(grupoExameUsual == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			
		} else {
			limparTela();
		}
		
		return null;
	
	}
	
	private void limparTela() {
		grupoExameUsual = new AelGrupoExameUsual();
	}
	
	public String confirmar() {
		try {
			if(this.grupoExameUsual.getSeq() == null) {
				this.grupoExameUsual.setServidor(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
				this.examesFacade.inserirAelGrupoExameUsual(this.grupoExameUsual);
			} else {
				this.examesFacade.alterarAelGrupoExameUsual(this.grupoExameUsual);
			}
			
			apresentarMsgNegocio(Severity.INFO,"LABEL_CONFIRMACAO_GRUPO_EXAMES_USUAIS");
			
			return cancelar();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		limparTela();
		return MANTER_GRUPO_EXAMES_USUAIS_PESQUISA;
	}
	
	public AelGrupoExameUsual getGrupoExameUsual() {
		return grupoExameUsual;
	}

	public void setGrupoExameUsual(AelGrupoExameUsual grupoExameUsual) {
		this.grupoExameUsual = grupoExameUsual;
	}
}