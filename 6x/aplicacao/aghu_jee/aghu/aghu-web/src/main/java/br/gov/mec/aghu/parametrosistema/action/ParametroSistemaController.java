package br.gov.mec.aghu.parametrosistema.action;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.model.AghSistemas;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;

public class ParametroSistemaController extends ActionController {
	
	private static final long serialVersionUID = 8192536105192814958L;

	private static final Log LOG = LogFactory.getLog(ParametroSistemaController.class);
	
	private static final String PAGE_PESQUISAR_SISTEMA = "parametroSistemaList";
	
	@EJB @SuppressWarnings("cdi-ambiguous-dependency")
	private IParametroSistemaFacade parametroSistemaFacade;
		
	private AghSistemas sistema;
	private String sigla;
	
	/**
	 * Informacao que indica se eh uma atualizacao ou uma criacao de sistema.<br>
	 * Deve ser calculado na abertura da tela, pois esta entidade <code>AghSistemas</code>
	 * não tem uma PK gerada pelo sistema. A PK eh o proprio codigo informado pelo usuario.
	 * 
	 */
	private boolean update;
	
	
	@PostConstruct
	protected void init() {
		this.setSistema(new AghSistemas());
	}
	
	public String initForm() {
	 

		if (StringUtils.isNotBlank(getSigla())) {
			sistema = parametroSistemaFacade.buscaAghSistemaPorId(getSigla());
			
			if(sistema == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return cancelar();
			}
		}
		
		return null;
	
	}
	
	public String confirmar() {
		try {
			parametroSistemaFacade.persistirAghSistema(this.getSistema(), this.isUpdate());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PERSISTIR_SISTEMA", this.getSistema().getNome());
			
			return cancelar();
			
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(), e);
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	private void limparController() {
		sistema = new AghSistemas();
		update = false;
		sigla = null;
	}
	
	public String cancelar() {
		limparController();
		return PAGE_PESQUISAR_SISTEMA;
	}
	
	public AghSistemas getSistema() {
		return sistema;
	}
	
	public void setSistema(AghSistemas s) {
		this.sistema = s;
	}
	
	public boolean isUpdate() {
		return update;
	}

	/**
	 * 
	 * @param update
	 */
	public void setUpdate(boolean u) {
		this.update = u;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}
	
}
