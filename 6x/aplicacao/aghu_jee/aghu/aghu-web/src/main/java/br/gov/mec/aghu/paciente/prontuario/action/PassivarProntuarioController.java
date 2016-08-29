package br.gov.mec.aghu.paciente.prontuario.action;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.validator.constraints.Range;

import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;


public class PassivarProntuarioController extends ActionController {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3577106859844307239L;

	private static final Log LOG = LogFactory.getLog(PassivarProntuarioController.class);
	
	private static final String VARIAVEL_CONTEXTO_APLICACAO_DT_ULTIMA_EXEC_PASSIVAR = "ultimaExecucaoPassivarProntuario";
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@Range(min=-99, max=99)
	private Integer secao;
	
	private Date dataReferencia;
	
	private boolean incluirPassivos;
	
	private boolean exibirTabelaResultados = false;
	
	private boolean exibirBotaoNovo = false;
	
	
	@PostConstruct
	public void init() {
		this.begin(conversation);
	}
		
	public String salvar() {
		try {
			Date ultimaExecucaoPassivarProntuario = (Date) FacesContext
					.getCurrentInstance().getExternalContext()
					.getApplicationMap()
					.get(VARIAVEL_CONTEXTO_APLICACAO_DT_ULTIMA_EXEC_PASSIVAR);

			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.getApplicationMap()
					.put(VARIAVEL_CONTEXTO_APLICACAO_DT_ULTIMA_EXEC_PASSIVAR,
							ultimaExecucaoPassivarProntuario);
			
			
			this.pacienteFacade.passivarProntuario(secao.toString(), dataReferencia, incluirPassivos, ultimaExecucaoPassivarProntuario);
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_PASSIVAR_PRONTUARIO");
			
			FacesContext
					.getCurrentInstance()
					.getExternalContext()
					.getApplicationMap()
					.put(VARIAVEL_CONTEXTO_APLICACAO_DT_ULTIMA_EXEC_PASSIVAR,
							ultimaExecucaoPassivarProntuario);
			
		} catch (ApplicationBusinessException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		
		} catch (BaseRuntimeException e) {
			LOG.error(e.getMessage(),e);
			apresentarExcecaoNegocio(e);
		}
			
		return null;
	}
	
	public String cancelar() {
		return "cancelado";
	}
	
	//GETTERS e SETTERS
	public boolean isExibirTabelaResultados() {
		return exibirTabelaResultados;
	}

	public void setExibirTabelaResultados(boolean exibirTabelaResultados) {
		this.exibirTabelaResultados = exibirTabelaResultados;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getSecao() {
		return secao;
	}

	public void setSecao(Integer secao) {
		this.secao = secao;
	}

	public Date getDataReferencia() {
		return dataReferencia;
	}

	public void setDataReferencia(Date dataReferencia) {
		this.dataReferencia = dataReferencia;
	}

	public boolean isIncluirPassivos() {
		return incluirPassivos;
	}

	public void setIncluirPassivos(boolean incluirPassivos) {
		this.incluirPassivos = incluirPassivos;
	}

}
