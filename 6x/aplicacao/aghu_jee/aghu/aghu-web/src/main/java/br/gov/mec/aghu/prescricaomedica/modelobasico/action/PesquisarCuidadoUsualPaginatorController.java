package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MpmCuidadoUsual;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action.TiposDietaCrudController;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author mgoulart
 * 
 */
public class PesquisarCuidadoUsualPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -145733189582395257L;

	private static final String PAGE_MANTER_CUIDADO_USUAL = "manterCuidadoUsual";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * Objeto do formulário para edição, inclusão ou exclusão.
	 */
	private MpmCuidadoUsual cuidadoUsual = new MpmCuidadoUsual();

	private List<MpmCuidadoUsual> cuidados;

	private Integer codigoPesquisaCuidadoUsual;

	private String descricaoPesquisaCuidadoUsual;

	private DominioSituacao situacaoPesquisaCuidadoUsual;

	private AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual;

	private Integer codigoCuidadoUsual;

	@Inject @Paginator
	private DynamicDataModel<MpmCuidadoUsual> dataModel;

	private MpmCuidadoUsual parametroSelecionado;

	/**
	 * Atributo utilizado para controlar a exibicao do botao "incluir"
	 */
	private boolean exibirBotaoNovo;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	private List<AghUnidadesFuncionais> listaServUnFuncionais;
	private AghUnidadesFuncionais unidadeFuncional, unidadeFuncionalSelecionada;

	@Override
	public Long recuperarCount() {
		AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual = null;
		if (this.unidadeFuncionalPesquisaCuidadoUsual != null) {
			unidadeFuncionalPesquisaCuidadoUsual = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.unidadeFuncionalPesquisaCuidadoUsual.getSeq(),
					AghUnidadesFuncionais.Fields.ALA);
		}
		return this.modeloBasicoFacade.pesquisarCuidadoUsualCount(codigoPesquisaCuidadoUsual, descricaoPesquisaCuidadoUsual, situacaoPesquisaCuidadoUsual,
				unidadeFuncionalPesquisaCuidadoUsual);

	}

	@Override
	public List<MpmCuidadoUsual> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual = null;
		if (this.unidadeFuncionalPesquisaCuidadoUsual != null) {
			unidadeFuncionalPesquisaCuidadoUsual = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(this.unidadeFuncionalPesquisaCuidadoUsual.getSeq(),
					AghUnidadesFuncionais.Fields.ALA);
		}
		List<MpmCuidadoUsual> result = this.modeloBasicoFacade.pesquisarCuidadoUsual(firstResult, maxResult, orderProperty, asc, codigoPesquisaCuidadoUsual,
				descricaoPesquisaCuidadoUsual, situacaoPesquisaCuidadoUsual, unidadeFuncionalPesquisaCuidadoUsual);
		if (result == null) {
			result = new ArrayList<MpmCuidadoUsual>();
		}
		return result;
	}

	public void carregar() {
	 

		// setServidor(pessoa.getRapServidores());

		// método para carregar a lista de unidades funcionais
		try {
			RapServidores servidor = registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado());
			List<AghUnidadesFuncionais> listaUnfs = getModeloBasicoFacade().getListaUnidadesFuncionais(servidor);
			this.listaServUnFuncionais = new ArrayList<AghUnidadesFuncionais>();
			for (AghUnidadesFuncionais unfProjection : listaUnfs) {
				AghUnidadesFuncionais unf = this.aghuFacade.obterAghUnidadesFuncionaisPorChavePrimaria(unfProjection.getSeq(), AghUnidadesFuncionais.Fields.ALA);
				if (unf != null) {
					AghUnidadesFuncionais vo = new AghUnidadesFuncionais();
					vo.setSeq(unf.getSeq());	
					vo.setDescricao(unf.getDescricao());
					vo.setAndar(unf.getAndar());
					vo.setIndAla(unf.getIndAla());
					this.listaServUnFuncionais.add(vo);
				}
			}
			Collections.sort(listaServUnFuncionais, TiposDietaCrudController.UNIDADE_COMPARATOR);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
		this.setExibirBotaoNovo(true);
	}

	public void excluir() {
		try {
			if (this.parametroSelecionado != null) {
				MpmCuidadoUsual cuidadoUsual = this.modeloBasicoFacade.obterCuidadoUsual(this.parametroSelecionado.getSeq());
				this.modeloBasicoFacade.excluir(this.parametroSelecionado.getSeq());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CUIDADO_USUAL", cuidadoUsual.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Método para limpar o formulário da tela.
	 */
	public void limparCampos() {
		this.setCodigoPesquisaCuidadoUsual(null);
		this.setDescricaoPesquisaCuidadoUsual(null);
		this.setSituacaoPesquisaCuidadoUsual(null);
		this.setUnidadeFuncionalPesquisaCuidadoUsual(null);
		this.setExibirBotaoNovo(false);
		this.dataModel.setPesquisaAtiva(false);
	}

	/**
	 * Método da suggestion box para pesquisa de unidades funcionais a incluir
	 * na lista Exclui da listagem os itens que já estão na tela Ignora a
	 * pesquisa caso o parametro seja o próprio valor selecionado anteriormente
	 * (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String parametro) {
		String paramString = (String) parametro;
		Set<AghUnidadesFuncionais> result = new HashSet<AghUnidadesFuncionais>();
		if ((unidadeFuncionalSelecionada == null)
				|| !(StringUtils.equalsIgnoreCase(paramString, String.valueOf(unidadeFuncionalSelecionada.getSeq())) || StringUtils.equalsIgnoreCase(paramString,
						unidadeFuncionalSelecionada.getLPADAndarAlaDescricao()))) {
				result = new HashSet<AghUnidadesFuncionais>(aghuFacade.pesquisarAghUnidadesFuncionaisAtivasPorSeqOuDescricao(parametro, 0, 100, AghUnidadesFuncionais.Fields.DESCRICAO.toString(), true));
				result.removeAll(getListaServUnFuncionais());
		} else {
			// adiciona a selecionada para nao mostrar mensagens erradas na tela
			result.add(unidadeFuncionalSelecionada);
		}
		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(result);
		Collections.sort(resultReturn, TiposDietaCrudController.UNIDADE_COMPARATOR);
		return resultReturn;
	}

	public String inserirEditar() {
		return PAGE_MANTER_CUIDADO_USUAL;
	}

	// getters & setters

	public IModeloBasicoFacade getModeloBasicoFacade() {
		return modeloBasicoFacade;
	}

	public void setModeloBasicoFacade(IModeloBasicoFacade modeloBasicoFacade) {
		this.modeloBasicoFacade = modeloBasicoFacade;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalSelecionada() {
		return unidadeFuncionalSelecionada;
	}

	public void setUnidadeFuncionalSelecionada(AghUnidadesFuncionais unidadeFuncionalSelecionada) {
		this.unidadeFuncionalSelecionada = unidadeFuncionalSelecionada;
	}

	public void setListaServUnFuncionais(List<AghUnidadesFuncionais> listaServUnFuncionais) {
		this.listaServUnFuncionais = listaServUnFuncionais;
	}

	public List<AghUnidadesFuncionais> getListaServUnFuncionais() {
		return listaServUnFuncionais;
	}

	public MpmCuidadoUsual getCuidadoUsual() {
		return cuidadoUsual;
	}

	public void setCuidadoUsual(MpmCuidadoUsual cuidadoUsual) {
		this.cuidadoUsual = cuidadoUsual;
	}

	public List<MpmCuidadoUsual> getCuidados() {
		return cuidados;
	}

	public void setCuidados(List<MpmCuidadoUsual> cuidados) {
		this.cuidados = cuidados;
	}

	public boolean isExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getCodigoPesquisaCuidadoUsual() {
		return codigoPesquisaCuidadoUsual;
	}

	public void setCodigoPesquisaCuidadoUsual(Integer codigoPesquisaCuidadoUsual) {
		this.codigoPesquisaCuidadoUsual = codigoPesquisaCuidadoUsual;
	}

	public String getDescricaoPesquisaCuidadoUsual() {
		return descricaoPesquisaCuidadoUsual;
	}

	public void setDescricaoPesquisaCuidadoUsual(String descricaoPesquisaCuidadoUsual) {
		this.descricaoPesquisaCuidadoUsual = descricaoPesquisaCuidadoUsual;
	}

	public AghUnidadesFuncionais getUnidadeFuncionalPesquisaCuidadoUsual() {
		return unidadeFuncionalPesquisaCuidadoUsual;
	}

	public void setUnidadeFuncionalPesquisaCuidadoUsual(AghUnidadesFuncionais unidadeFuncionalPesquisaCuidadoUsual) {
		this.unidadeFuncionalPesquisaCuidadoUsual = unidadeFuncionalPesquisaCuidadoUsual;
	}

	public DominioSituacao getSituacaoPesquisaCuidadoUsual() {
		return situacaoPesquisaCuidadoUsual;
	}

	public void setSituacaoPesquisaCuidadoUsual(DominioSituacao situacaoPesquisaCuidadoUsual) {
		this.situacaoPesquisaCuidadoUsual = situacaoPesquisaCuidadoUsual;
	}

	public Integer getCodigoCuidadoUsual() {
		return codigoCuidadoUsual;
	}

	public void setCodigoCuidadoUsual(Integer codigoCuidadoUsual) {
		this.codigoCuidadoUsual = codigoCuidadoUsual;
	}

	private static class UnidadeFuncionalComparator implements Comparator<AghUnidadesFuncionais> {
		@Override
		public int compare(AghUnidadesFuncionais u1, AghUnidadesFuncionais u2) {
			int result = u1.getLPADAndarAlaDescricao().compareToIgnoreCase(u2.getLPADAndarAlaDescricao());
			return result;
		}
	}

	public DynamicDataModel<MpmCuidadoUsual> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmCuidadoUsual> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmCuidadoUsual getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmCuidadoUsual parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}
