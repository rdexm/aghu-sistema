package br.gov.mec.aghu.paciente.cadastrosbasicos.action;

import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AipTipoLogradouros;
import br.gov.mec.aghu.paciente.cadastrosbasicos.business.ICadastrosBasicosPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class TipoLogradouroController extends ActionController{

	private static final long serialVersionUID = -4714455327859945486L;
	private static final Log LOG = LogFactory.getLog(TipoLogradouroController.class);
	private static final String REDIRECT_PESQUISAR_TIPO_LOGRADOURO = "tipoLogradouroList";
	
	@EJB
	private ICadastrosBasicosPacienteFacade cadastrosBasicosPacienteFacade;

	private AipTipoLogradouros tipoLogradouro = new AipTipoLogradouros();
	
	private boolean exibirBotaoIncluirTipoLogradouro = false;

	@Inject
	private TipoLogradouroPaginatorController tipoLogradouroPaginatorController;
	
	public void iniciarEdicao(AipTipoLogradouros tipoLogradouro) {
		if (tipoLogradouro != null) { //Edição
			this.tipoLogradouro = tipoLogradouro;
		}
	}

	public String salvar() {
		try{
			boolean novo = false;
			if (tipoLogradouro == null || tipoLogradouro.getCodigo() == null) {
				novo = true;
			}
			tipoLogradouro.setAbreviatura(tipoLogradouro.getAbreviatura().toUpperCase());
			tipoLogradouro.setDescricao(tipoLogradouro.getDescricao().toUpperCase());
			cadastrosBasicosPacienteFacade.persistirTipoLogradouro(tipoLogradouro);

			this.apresentarMsgNegocio(Severity.INFO,
							novo ? "MENSAGEM_SUCESSO_PERSISTIR_TIPO_LOGRADOURO" : "MENSAGEM_SUCESSO_ALTERAR_TIPO_LOGRADOURO",
									this.tipoLogradouro.getDescricao());

			tipoLogradouro = new AipTipoLogradouros();

			exibirBotaoIncluirTipoLogradouro = true;
			
			tipoLogradouroPaginatorController.reiniciarPaginator();
			
			return REDIRECT_PESQUISAR_TIPO_LOGRADOURO;
		}
		catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);	
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	
	public String cancelar() {
		tipoLogradouro = new AipTipoLogradouros();
		exibirBotaoIncluirTipoLogradouro = true;
		this.tipoLogradouroPaginatorController.reiniciarPaginator();
		return REDIRECT_PESQUISAR_TIPO_LOGRADOURO;
	}
	
	//SET's e GET's
	
	public AipTipoLogradouros getTipoLogradouro() {
		return tipoLogradouro;
	}

	public void setTipoLogradouro(final AipTipoLogradouros tipoLogradouros) {
		this.tipoLogradouro = tipoLogradouros;
	}

	public boolean isExibirBotaoIncluirTipoLogradouro() {
		return exibirBotaoIncluirTipoLogradouro;
	}

	public void setExibirBotaoIncluirTipoLogradouro(
			final boolean exibirBotaoIncluirTipoLogradouro) {
		this.exibirBotaoIncluirTipoLogradouro = exibirBotaoIncluirTipoLogradouro;
	}	
}