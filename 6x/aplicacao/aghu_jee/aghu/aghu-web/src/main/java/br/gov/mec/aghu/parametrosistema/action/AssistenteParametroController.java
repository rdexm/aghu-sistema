package br.gov.mec.aghu.parametrosistema.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.recursoshumanos.Pessoa;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

/**
 * @author clayton.bras
 * 
 */
public class AssistenteParametroController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(AssistenteParametroController.class);

	private static final long serialVersionUID = -642195992326540781L;

	// private AghParametros parametro = new AghParametros();
	private List<AghParametros> parametrosAssistente;
	private List<AghModuloAghu> modulos;

	private boolean update = false;

	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;

	private Pessoa pessoa;

	private Iterator<AghParametros> iterParametroAssistente = null;
	private AghParametros parametroCorrente;
	private Integer paramIndex;

	private List<SelectItem> todosModulos;
	private String origemPesquisa;

	private static final String ASSISTENTE_PREENCHER_PARAMETROS = "assistenteConfiguracaoParametro";
	private static final String ASSISTENTE_FIM = "assistenteConfiguracaoParametroFinal";
	private static final String ASSISTENTE_INICIO = "assistenteConfiguracaoParametroInicial";

	public void iniciar() {
	 


		if (parametrosAssistente != null && !parametrosAssistente.isEmpty() && iterParametroAssistente == null) {
			iterParametroAssistente = parametrosAssistente.iterator();
			setParametroCorrente(iterParametroAssistente.next());
			paramIndex = 0;
		}

		if (todosModulos == null) {
			todosModulos = new ArrayList<SelectItem>();
			for (AghModuloAghu modulo : parametroSistemaFacade.obterTodosModulosAGHU()) {
				SelectItem item = new SelectItem();
				item.setValue(modulo);
				item.setLabel(modulo.getDescricao());
				todosModulos.add(item);
			}
		}
		if (parametroCorrente != null) {
			modulos = getParametroSistemaFacade().pesquisarModulosPorParametro(parametroCorrente.getSeq());
		}
	
	}

	public void inicioPaginaFinal() {
	 

		// Refaz a consulta para ver se ainda existe parametros a serem preenchidos.
		parametrosAssistente = parametroSistemaFacade.obterParametrosSemQualquerValorAssociado();

	
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean isUpdate() {
		return update;
	}

	/**
	 * Inicia o assitente para a configuração dos parâmetros sem valor.
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public String configurarParametroSemValor() {
		String retorno = null;
		// Obtem todos os parâmetros a serem configurados
		parametrosAssistente = parametroSistemaFacade.obterParametrosSemQualquerValorAssociado();

		// Zera iterador
		iterParametroAssistente = null;

		if (parametrosAssistente != null && !parametrosAssistente.isEmpty()) {
			// Redireciona para a tela de preenchimento do parâmetro
			retorno = ASSISTENTE_PREENCHER_PARAMETROS;
		} else {
			retorno = ASSISTENTE_FIM;
		}
		return retorno;
	}

	/**
	 * Inicia o assitente para a configuração dos parâmetros com valor.
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public String configurarParametroComValor() {

		// Obtem todos os parâmetros a serem configurados
		parametrosAssistente = parametroSistemaFacade.obterParametrosComValorAssociado();

		// Zera iterador
		iterParametroAssistente = null;

		// Redireciona para a tela de preenchimento do parâmetro
		return ASSISTENTE_PREENCHER_PARAMETROS;
	}

	public String configurarParametroTodos() {
		// Obtem todos os parâmetros a serem configurados
		parametrosAssistente = parametroSistemaFacade.obterTodosParametros();

		// Zera iterador
		iterParametroAssistente = null;

		// Redireciona para a tela de preenchimento do parâmetro
		return ASSISTENTE_PREENCHER_PARAMETROS;
	}

	public void setParametrosAssitente(List<AghParametros> parametrosAssitente) {
		this.parametrosAssistente = parametrosAssitente;
	}

	public List<AghParametros> getParametrosAssitente() {
		return parametrosAssistente;
	}

	public void setParametroCorrente(AghParametros parametroCorrente) {
		this.parametroCorrente = parametroCorrente;
	}

	public AghParametros getParametroCorrente() {
		return parametroCorrente;
	}

	/**
	 * Avança para o próximo parâmetro a ser configurado, ou para o fim do processo, quando já estiver editando o último parâmetro.
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public String avancar() {
		String navega = null;
		try {
			// Grava o parametro atual, somente se o campo valor foi preenchido
			if (getParametroCorrente().getValor() != null && !"".equals(getParametroCorrente().getValor())) {
				parametroSistemaFacade.atualizarParametroSistema(getParametroCorrente(), this.pessoa.getNome().toUpperCase());
			}
			// Se tiver mais parametros para editar, muda de parametro
			if (iterParametroAssistente.hasNext()) {
				setParametroCorrente(iterParametroAssistente.next());
				navega = ASSISTENTE_PREENCHER_PARAMETROS;
			} else {
				navega = ASSISTENTE_FIM;
			}
			paramIndex++;
		} catch (BaseException e) {
			LOG.error("Erro ao persistir parâmetro no assistente de configuração.", e);
			this.apresentarExcecaoNegocio(e);
		}
		return navega;
	}

	/**
	 * Retorna para o parâmetro configurado anteriormente, ou para a página de inicio do assistente, quando estiver no primeiro parâmetro da lista.
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public String voltar() {
		String navega;
		if (paramIndex <= 0) {
			navega = ASSISTENTE_INICIO;
		} else {
			Integer tmpIndex = 0;
			// Reinicia o iterator
			iterParametroAssistente = parametrosAssistente.iterator();
			while (tmpIndex <= paramIndex - 1 && iterParametroAssistente.hasNext()) {
				setParametroCorrente(iterParametroAssistente.next());
				tmpIndex++;
			}
			navega = ASSISTENTE_PREENCHER_PARAMETROS;
			paramIndex--;
		}

		return navega;
	}

	/**
	 * Retorna navegação quando estiver na página final de configuracao dos parametros
	 * 
	 * @return
	 * @author bruno.mourao
	 * @since 10/11/2011
	 */
	public String voltarPaginaFinal() {
		return ASSISTENTE_FIM;
	}

	/**
	 * Limpa parâmetros da tela
	 */
	// private void limparParametros() {
	// List<AghParametros> parametrosAssistente = null;
	// List<AghModuloAghu> modulos = null;
	// boolean update = false;
	// Pessoa pessoa = null;
	// Iterator<AghParametros> iterParametroAssistente = null;
	// AghParametros parametroCorrente = null;
	// Integer paramIndex = null;
	// List<SelectItem> todosModulos = null;
	// String origemPesquisa = null;
	// }

	/**
	 * Altera o valor corrente para o valor padrão do parametro.
	 * 
	 * @author bruno.mourao
	 * @since 04/11/2011
	 */
	public void copiarValorPadrao() {

		if (getParametroCorrente().getTipoDado().equals(DominioTipoDadoParametro.T)) {
			getParametroCorrente().setVlrTexto(getParametroCorrente().getVlrTextoPadrao());
		} else if (getParametroCorrente().getTipoDado().equals(DominioTipoDadoParametro.N)) {
			getParametroCorrente().setVlrNumerico(getParametroCorrente().getVlrNumericoPadrao());
		} else if (getParametroCorrente().getTipoDado().equals(DominioTipoDadoParametro.D)) {
			getParametroCorrente().setVlrTexto(getParametroCorrente().getVlrTextoPadrao());
			getParametroCorrente().setVlrData(getParametroCorrente().getVlrDataPadrao());
		}
	}

	public IParametroSistemaFacade getParametroSistemaFacade() {
		return parametroSistemaFacade;
	}

	public void setParametroSistemaFacade(IParametroSistemaFacade parametroSistemaFacade) {
		this.parametroSistemaFacade = parametroSistemaFacade;
	}

	public String getDominioTipoDadoParametroTextoAsString() {
		return DominioTipoDadoParametro.T.toString();
	}

	public String getDominioTipoDadoParametroDataAsString() {
		return DominioTipoDadoParametro.D.toString();
	}

	public String getDominioTipoDadoParametroNumeroAsString() {
		return DominioTipoDadoParametro.N.toString();
	}

	public List<AghModuloAghu> getModulos() {
		return modulos;
	}

	public void setModulos(List<AghModuloAghu> modulos) {
		this.modulos = modulos;
	}

	public Integer getTamLista() {
		if (parametrosAssistente != null) {
			return parametrosAssistente.size();
		} else {
			return 0;
		}
	}

	public void setOrigemPesquisa(String origemPesquisa) {
		this.origemPesquisa = origemPesquisa;
	}

	public String getOrigemPesquisa() {
		return origemPesquisa;
	}

	public Integer getParamIndex() {
		return paramIndex;
	}
	
	public List<SelectItem> getTodosModulos() {
		return todosModulos;
	}
	
	public void setTodosModulos(List<SelectItem> todosModulos) {
		this.todosModulos = todosModulos;
	}
}
