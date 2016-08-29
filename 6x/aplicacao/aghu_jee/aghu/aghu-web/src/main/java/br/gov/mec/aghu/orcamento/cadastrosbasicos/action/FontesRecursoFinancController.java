package br.gov.mec.aghu.orcamento.cadastrosbasicos.action;

import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.FsoFontesRecursoFinanc;
import br.gov.mec.aghu.orcamento.cadastrosbasicos.business.ICadastrosBasicosOrcamentoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class FontesRecursoFinancController extends ActionController {

	private static final long serialVersionUID = -5860505458653656519L;

	private static final String FONTES_RECURSO_FINANC_LIST = "fontesRecursoFinancList";

	@EJB
	private ICadastrosBasicosOrcamentoFacade cadastrosBasicosOrcamentoFacade;

	private FsoFontesRecursoFinanc fontesRecursoFinanc ;

	private boolean visualizacaoRegistro;
	private boolean alteracaoRegistro;	
	private Boolean isConfirmGravarRequired = false;
	private Boolean permiteEdicaoDescricao;

	public String iniciar() {
	 

		
		if (fontesRecursoFinanc != null && fontesRecursoFinanc.getCodigo() != null) {
			fontesRecursoFinanc = cadastrosBasicosOrcamentoFacade.obterFontesRecursoFinanc(fontesRecursoFinanc.getCodigo());
			
			if(fontesRecursoFinanc == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
			alteracaoRegistro = true;
			permiteEdicaoDescricao = cadastrosBasicosOrcamentoFacade.verificarFonteRecursoFinancUsadaEmVerbaGestao(fontesRecursoFinanc);
			
		} else {
			limpar();
		}
		
		return null;
	
	}
	
	private void limpar() {
		fontesRecursoFinanc = new FsoFontesRecursoFinanc();
		fontesRecursoFinanc.setIndSituacao(DominioSituacao.A);
	    this.alteracaoRegistro = false;
		this.visualizacaoRegistro = false;
		this.setIsConfirmGravarRequired(false);
	}
	
	public String verificarESalvar() {
		if (this.isAlteracaoRegistro() && possuiFonteRecursoFinancUsadaEmVerbaGestao()) {			
			setIsConfirmGravarRequired(true);
            openDialog("confirmGravarModalWG");
			return null;
		} else {
			setIsConfirmGravarRequired(false);
		}
		
		return gravar();
	}
		
	private boolean possuiFonteRecursoFinancUsadaEmVerbaGestao() {
		return cadastrosBasicosOrcamentoFacade.verificarFonteRecursoFinancUsadaEmVerbaGestao(fontesRecursoFinanc);
	}		
	
	public String gravar() {		
		try {
			if (this.fontesRecursoFinanc != null) {
				if (!this.isAlteracaoRegistro()) {
					cadastrosBasicosOrcamentoFacade.incluirFontesRecursoFinanc(fontesRecursoFinanc);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_FONTES_RECURSO_FINANC_M01", fontesRecursoFinanc.getDescricao());
	
				} else {
					cadastrosBasicosOrcamentoFacade.alterarFontesRecursoFinanc(fontesRecursoFinanc);
					apresentarMsgNegocio(Severity.INFO, "MENSAGEM_FONTES_RECURSO_FINANC_M02",fontesRecursoFinanc.getDescricao());
				}
			}
			return cancelar();

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}	

	public String cancelar() {
		limpar();
		return FONTES_RECURSO_FINANC_LIST;
	}
	
	public void setFontesRecursoFinanc(FsoFontesRecursoFinanc fontesRecursoFinanc) {
		this.fontesRecursoFinanc = fontesRecursoFinanc;
	}

	public FsoFontesRecursoFinanc getFontesRecursoFinanc() {
		return fontesRecursoFinanc;
	}

	public Boolean getIsConfirmGravarRequired() {
		return isConfirmGravarRequired;
	}

	public void setIsConfirmGravarRequired(Boolean isConfirmGravarRequired) {
		this.isConfirmGravarRequired = isConfirmGravarRequired;
	}

	public Boolean getPermiteEdicaoDescricao() {
		return permiteEdicaoDescricao;
	}

	public void setPermiteEdicaoDescricao(Boolean permiteEdicaoDescricao) {
		this.permiteEdicaoDescricao = permiteEdicaoDescricao;
	}

	public boolean isVisualizacaoRegistro() {
		return visualizacaoRegistro;
	}

	public void setVisualizacaoRegistro(boolean visualizacaoRegistro) {
		this.visualizacaoRegistro = visualizacaoRegistro;
	}

	public boolean isAlteracaoRegistro() {
		return alteracaoRegistro;
	}

	public void setAlteracaoRegistro(boolean alteracaoRegistro) {
		this.alteracaoRegistro = alteracaoRegistro;
	}

}