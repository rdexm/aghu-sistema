package br.gov.mec.aghu.parametrosistema.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.model.SelectItem;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioFiltroParametrosPreenchidos;
import br.gov.mec.aghu.dominio.DominioTipoDadoParametro;
import br.gov.mec.aghu.model.AghModuloAghu;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.parametrosistema.business.IParametroSistemaFacade;
import br.gov.mec.aghu.recursoshumanos.Pessoa;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author bruno.mourao, clayton.bras
 * 
 */

public class ManterParametrosPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<AghParametros> dataModel;

	private static final long serialVersionUID = 6813894736913012550L;

	public enum EnumTargetManterParametros {
		SUCESSO_ALTERACAO_PARAMETRO_SISTEMA;
	}

	@EJB
	private IParametroSistemaFacade parametroSistemaFacade;

	private Pessoa pessoa;

	// Parametro para filtro
	private AghParametros parametro = new AghParametros();
	// Parametro para edicao
	private AghParametros parametroEdicao = new AghParametros();

	// Filtros da pesquisa
	private List<AghModuloAghu> modulos;
	private List<SelectItem> todosModulos;
	private Object valor;
	private DominioFiltroParametrosPreenchidos filtroPreenchidos = DominioFiltroParametrosPreenchidos.TODOS;

	// utilizado para controlar as renderizações dos campos
	private Boolean modoEdicao;

	// armazena o código do parâmetro para edição
	private Integer seqParametro = null;

	// armazena String para redirecionamento à origem
	private String origemPesquisa;

	private Boolean retornaEdicao = Boolean.FALSE;

	private String nomeParametroMensagem;

	public void iniciar() {
	 

	 


		setModoEdicao(Boolean.FALSE);
		// limpa a tela após cancelamento da edição
		if (getRetornaEdicao()) {
			setParametro(new AghParametros());
			setValor(null);
			setModulos(new ArrayList<AghModuloAghu>());
			setFiltroPreenchidos(DominioFiltroParametrosPreenchidos.TODOS);
			// setAtivo(false);
			setRetornaEdicao(Boolean.FALSE);
		}

		if (this.todosModulos == null) {
			this.todosModulos = new ArrayList<SelectItem>();
			for (AghModuloAghu modulo : pesquisarModulosParametroSistemas()) {
				SelectItem item = new SelectItem();
				item.setValue(modulo);
				item.setLabel(modulo.getDescricao());
				this.todosModulos.add(item);
			}
		}

		if (getSeqParametro() != null) { // valor passado pela lista, carregar para edição
			parametro = parametroSistemaFacade.obterParametroPorId(seqParametro);
			parametro.setModulos(parametroSistemaFacade.pesquisarModulosPorParametro(seqParametro));
			seqParametro = null;
			setRetornaEdicao(Boolean.TRUE);
			setNomeParametroMensagem(getParametro().getNome());
		}
	
	}
	

	/**
	 * Retorna a lista de parâmetros encontrados na pesquisa
	 * 
	 * @author clayton.bras
	 */
	@Override
	public List<AghParametros> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<AghParametros> listaParametros = new ArrayList<AghParametros>();

		listaParametros = getParametroSistemaFacade().pesquisarParametrosPorNomeModuloValorFiltro(firstResult, maxResult, orderProperty, asc, getParametro().getNome(), getModulos(), getValor(),
				getFiltroPreenchidos());

		return listaParametros;
	}

	/**
	 * Retorna a quantidade de parâmetros encontrados na pesquisa
	 * 
	 * @author clayton.bras
	 */
	@Override
	public Long recuperarCount() {
		return getParametroSistemaFacade().pesquisarParametrosPorNomeModuloValorFiltroCount(getParametro().getNome(), getModulos(), getValor(), getFiltroPreenchidos());
	}

	/**
	 * Obtém os módulos existentes no sistema
	 * 
	 * @author clayton.bras
	 * @return
	 */
	public List<AghModuloAghu> pesquisarModulosParametroSistemas() {
		return getParametroSistemaFacade().pesquisarModulosParametroSistemas();
	}

	/**
	 * Obtém a quantidade de paâmetros que não possuem valores associados, em qualquer dos campos vlrData, vlrNumerico, vlrTexto
	 * 
	 * @author clayton.bras
	 * @return
	 */
	public Long obterNumeroParametrosSemQualquerValorAssociado() {
		return getParametroSistemaFacade().obterNumeroParametrosSemQualquerValorAssociado();
	}

	/**
	 * Atualiza o campo valor, diretamente da respectiva coluna, no resultado da pesquisa
	 * 
	 * @author clayton.bras
	 * @param seq
	 * @param tipoDado
	 */
	public void atualizarValorParametro(Integer seq, DominioTipoDadoParametro tipoDado) {
		try {
			getParametroEdicao().setTipoDado(tipoDado);
			getParametroSistemaFacade().atualizarValorParametro(seq, getParametroEdicao().getValor(), pessoa.getNome());
			setModoEdicao(Boolean.FALSE);
			// /getParametroSistemaFacade().flush();
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Copia e persiste o conteúdo presente em algum campo de valor padrão para o respectivo campo valor, de acordo com o tipo
	 * 
	 * @author clayton.bras
	 * @param seq
	 */
	public void persistirValorPadraoCampoValor(Integer seq) {
		try {
			getParametroSistemaFacade().copiarValorPadraoCampoValor(seq, pessoa.getNome());
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Verifica a existência d de parâmetros sem valor associado, retornando valor TRUE ou FALSE, a ser utilizado na renderização da mensagem de alerta
	 * 
	 * @author clayton.bras
	 * @return
	 */
	public Boolean verificarExistenciaParametrosSemQualquerValorAssociado() {
		if (obterNumeroParametrosSemQualquerValorAssociado() > 0) {
			return Boolean.TRUE;
		} else {
			return Boolean.FALSE;
		}
	}

	/**
	 * Executa a pesquisa
	 * 
	 * @author clayton.bras
	 */
	public void pesquisar() {
		setModoEdicao(Boolean.FALSE);
		this.dataModel.reiniciarPaginator();
	}

	/**
	 * Limpa os campos da tela
	 * 
	 * @author clayton.bras
	 */
	public void limparCampos() {
		setParametro(new AghParametros());
		setValor(null);
		setModulos(new ArrayList<AghModuloAghu>());
		setFiltroPreenchidos(DominioFiltroParametrosPreenchidos.TODOS);
		this.getDataModel().limparPesquisa();
	}

	/**
	 * Método de atualização do parâmetro a partir da tela de edição
	 * 
	 * @return
	 */
	public String persistir() {
		try {
			getParametroSistemaFacade().atualizarParametroSistema(parametro, pessoa.getNome());
			String mensagemSucessoAlteracaoParametro = EnumTargetManterParametros.SUCESSO_ALTERACAO_PARAMETRO_SISTEMA.toString();
			this.apresentarMsgNegocio(Severity.INFO, mensagemSucessoAlteracaoParametro, nomeParametroMensagem);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return getOrigemPesquisa();
	}

	/**
	 * Copia o valor padrão para o campo valor, de acordo com o tipo do campo
	 */
	public void copiarValorPadrao() {
		if (DominioTipoDadoParametro.N.equals(getParametro().getTipoDado())) {
			getParametro().setVlrNumerico(getParametro().getVlrNumericoPadrao());
		} else if (DominioTipoDadoParametro.D.equals(getParametro().getTipoDado())) {
			getParametro().setVlrData(getParametro().getVlrDataPadrao());
		} else {
			getParametro().setVlrTexto(getParametro().getVlrTextoPadrao());
		}
	}

	public String obterModulosFormatadosParaResultadoPesquisa(Integer seqParametro) {
		List<AghModuloAghu> modulos = parametroSistemaFacade.pesquisarModulosPorParametro(seqParametro);
		StringBuffer modulosFormatados = new StringBuffer();
		if (modulos != null && !modulos.isEmpty()) {
			Iterator<AghModuloAghu> iteratorModulos = modulos.iterator();
			AghModuloAghu modulo = (AghModuloAghu) iteratorModulos.next();
			modulosFormatados.append(modulo.getNome().substring(0, 3));
			while (iteratorModulos.hasNext()) {
				modulo = (AghModuloAghu) iteratorModulos.next();
				modulosFormatados.append(" - ").append(modulo.getNome().substring(0, 3)).append("<br/>");
			}
		}
		return modulosFormatados.toString();
	}
	
	public String cancelar(){
		String retorno = this.origemPesquisa;
		this.origemPesquisa = null;
		this.seqParametro = null;
		this.parametro = new AghParametros();
		return retorno;
	}

	public DominioFiltroParametrosPreenchidos getFiltroPreenchidos() {
		return filtroPreenchidos;
	}

	public void setFiltroPreenchidos(DominioFiltroParametrosPreenchidos filtroPreenchidos) {
		this.filtroPreenchidos = filtroPreenchidos;
	}

	public List<SelectItem> getTodosModulos() {
		return this.todosModulos;
	}

	public void setTodosModulos(List<SelectItem> todosModulos) {
		this.todosModulos = todosModulos;
	}

	public Object getValor() {
		return valor;
	}

	public void setValor(Object valor) {
		this.valor = valor;
	}

	public void setModulos(List<AghModuloAghu> modulos) {
		this.modulos = modulos;
	}

	public List<AghModuloAghu> getModulos() {
		return modulos;
	}

	public void setModoEdicao(Boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public Boolean getModoEdicao() {
		return modoEdicao;
	}

	public IParametroSistemaFacade getParametroSistemaFacade() {
		return parametroSistemaFacade;
	}

	public void setParametroSistemaFacade(IParametroSistemaFacade parametroFacade) {
		this.parametroSistemaFacade = parametroFacade;
	}

	public Integer getSeqParametro() {
		return seqParametro;
	}

	public void setSeqParametro(Integer seqParametro) {
		this.seqParametro = seqParametro;
	}

	public AghParametros getParametro() {
		return parametro;
	}

	public void setParametro(AghParametros parametro) {
		this.parametro = parametro;
	}

	public void setOrigemPesquisa(String origemPesquisa) {
		this.origemPesquisa = origemPesquisa;
	}

	public String getOrigemPesquisa() {
		return origemPesquisa;
	}

	public Boolean getRetornaEdicao() {
		return retornaEdicao;
	}

	public void setRetornaEdicao(Boolean retornaEdicao) {
		this.retornaEdicao = retornaEdicao;
	}

	public void setNomeParametroMensagem(String nomeParametroMensagem) {
		this.nomeParametroMensagem = nomeParametroMensagem;
	}

	public String getNomeParametroMensagem() {
		return nomeParametroMensagem;
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

	public void editarParametro(AghParametros parametro) {
		setModoEdicao(Boolean.TRUE);
		this.parametroEdicao = parametro;
	}

	public AghParametros getParametroEdicao() {
		return parametroEdicao;
	}

	public void setParametroEdicao(AghParametros parametroEdicao) {
		this.parametroEdicao = parametroEdicao;
	}

	public DynamicDataModel<AghParametros> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghParametros> dataModel) {
		this.dataModel = dataModel;
	}
}
