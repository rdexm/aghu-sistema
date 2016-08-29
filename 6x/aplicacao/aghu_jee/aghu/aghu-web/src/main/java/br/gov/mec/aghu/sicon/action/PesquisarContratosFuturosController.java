package br.gov.mec.aghu.sicon.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.AfFiltroVO;
import br.gov.mec.aghu.compras.vo.AfsContratosFuturosVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.ScoFornecedor;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoGrupoServico;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.vo.LicitacaoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ListaDetalhesItensLicVO;
import br.gov.mec.aghu.sicon.vo.ListaLicitacaoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class PesquisarContratosFuturosController extends ActionController {

	private static final long serialVersionUID = -7170414301840612402L;


	@EJB
	private ISiconFacade siconFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;
	private static final String PAGE_PESQUISAR_CONTRATOSFUTUROS = "pesquisarContratosFuturos";

	private List<ListaLicitacaoVO> listaLicVO = new ArrayList<ListaLicitacaoVO>();
	private List<AfsContratosFuturosVO> listaAfsVO = new ArrayList<AfsContratosFuturosVO>();
	private List<ListaDetalhesItensLicVO> listaDetalhesItensLic = new ArrayList<ListaDetalhesItensLicVO>();
	private ScoLicitacao licitacao;
	private DominioModalidadeEmpenho paramModEmpenho;
	private Boolean novaPesquisa;
	private LicitacaoFiltroVO filtro;
	private AfFiltroVO filtroAf;
	private AfsContratosFuturosVO af;
	private ListaLicitacaoVO lic;
	private String toolTipComSiasg;
	private String toolTipSemSiasg;
	
	private static final Integer TAB_1 = 0; 
	private static final Integer TAB_2 = 1;
	
	/**
	 * Aba default
	 */
	private Integer currentTabIndex;
	

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}


	public void inicio() {

		novaPesquisa = false;
		this.setCurrentTabIndex(TAB_1);

		if (filtro == null) {
			filtro = new LicitacaoFiltroVO(new ScoLicitacao());
			filtro.setLicitacao(null);
		}
		try {
			setParamModEmpenho(DominioModalidadeEmpenho
					.forValue(this.parametroFacade
							.buscarAghParametro(
									AghuParametrosEnum.P_MODL_EMP_CTR_SICON)
							.getVlrNumerico().intValue()));
			popularFiltros(filtro);
			montarListasAbas();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	
	}

	public String pesquisar() {
		novaPesquisa = true;
		if (verificarFiltroOK(this.filtro)) {
			if ((this.filtro.getNumeroAf() != null && this.filtro
					.getNroComplementoAf() == null)
					|| (this.filtro.getNumeroAf() == null && this.filtro
							.getNroComplementoAf() != null)) {
				this.apresentarMsgNegocio(Severity.ERROR,
						getBundle().getString("MENSAGEM_NROCP_OBRIGATORIO"));
				popularFiltros(filtro);
				montarListasAbas();
			}
		}
		novaPesquisa = false;
		return PAGE_PESQUISAR_CONTRATOSFUTUROS;
	}

	/**
	 * Isso foi necessário pois o Filtro(LicitacaoFiltroVO) é do módulo do
	 * SICON e o filtroAf(AfFiltroVO) é do módulo do COMPRAS
	 * 
	 * @param filtro
	 */
	public void popularFiltros(LicitacaoFiltroVO filtro) {
		if (filtroAf == null) {
			filtroAf = new AfFiltroVO(new ScoLicitacao());
			filtroAf.setLicitacao(null);
		}
		
		filtroAf.setLicitacao(filtro.getLicitacao());
		filtroAf.setFornecedor(filtro.getFornecedor());
		filtroAf.setNumeroAf(filtro.getNumeroAf());
		filtroAf.setNroComplementoAf(filtro.getNroComplementoAf());
		filtroAf.setGrupoMaterial(filtro.getGrupoMaterial());
		filtroAf.setGrupoServico(filtro.getGrupoServico());
		filtroAf.setMaterial(filtro.getMaterial());
		filtroAf.setServico(filtro.getServico());
		filtroAf.setTipoItens(filtro.getTipoItens());
		filtroAf.setCodSiasg(filtro.getCodSiasg());
	}

	private void montarListasAbas() {
		try {
			listaLicVO = siconFacade.montarListaLicitacoes(filtro,
					getParamModEmpenho());
			
			listaAfsVO = autFornecimentoFacade.montarListaItemAutorizacaoForn(
					filtroAf, getParamModEmpenho());
			
			
			setarAbas();
			
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					getBundle().getString("ERRO_MONTAR_LISTAS"));
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					getBundle().getString("ERRO_MONTAR_LISTAS"));
		}
	}

	private boolean verificarFiltroOK(LicitacaoFiltroVO input) {
		if (input.getLicitacao() != null || input.getNumeroAf() != null
				|| input.getNroComplementoAf() != null
				|| input.getFornecedor() != null
				|| input.getTipoItens() != null
				|| input.getGrupoServico() != null
				|| input.getServico() != null
				|| input.getGrupoMaterial() != null
				|| input.getMaterial() != null || input.getCodSiasg() != null) {
			return true;
		} else {
			return false;
		}
	}

	public void limpar() {
		filtro = new LicitacaoFiltroVO(new ScoLicitacao());
		filtro.setLicitacao(null);
		popularFiltros(filtro);
		listaLicVO = null;
		novaPesquisa = false;
		listaDetalhesItensLic = null;
		this.setCurrentTabIndex(TAB_1);
	}

	private void setarAbas() {
		if (getListaLicVO().size() > 0 && getListaAfsVO().size() == 0) {
			this.setCurrentTabIndex(TAB_1);
		} else if (getListaLicVO().size() == 0 && getListaAfsVO().size() > 0) {
			this.setCurrentTabIndex(TAB_2);
		}
	}

	/**
	 * Detalhar as Licitacoes.
	 * 
	 * @param item
	 *            Licitacao
	 * @throws BaseException
	 */
	public void detalharLicitacoes(ScoLicitacao lic) {
		licitacao = lic;
		List<Object[]> listaItensLic;
		// A leitura dos detalhes dos itens da licitacao deve ser baseado na
		// QUERY V_SCO_ITENS_LICITACAO
		// Retorna uma lista: List<Object[]> listaItensLic, com os campos da
		// QUERY V_SCO_ITENS_LICITACAO
		// que serão usados para montar a lista dos detalhes dos itens da
		// licitacao.
		listaItensLic = this.pacFacade.montarListaDetalhesLicitacao(lic
				.getNumero());
		try {
			// Montagem da lista dos detalhes dos itens da licitacao.
			listaDetalhesItensLic = this.siconFacade
					.montarlistaDetalhesItensLic(listaItensLic);
		} catch (BaseException e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					getBundle().getString("ERRO_MONTAR_LISTA_ITENS_LICITACOES"));
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR,
					getBundle().getString("ERRO_MONTAR_LISTA_ITENS_LICITACOES"));
		}
	}

	public boolean materialPossuiCodSiasg(ScoMaterial material) {
		return this.cadastrosBasicosSiconFacade.pesquisarMaterialSicon(null,
				material, DominioSituacao.A, null) != null;
	}

	public boolean servicoPossuiCodSiasg(ScoServico servico) {
		return this.cadastrosBasicosSiconFacade.pesquisarServicoSicon(null,
				servico, DominioSituacao.A, null) != null;
	}

	public boolean afPossuiCodSiasg(Integer numeroLicitacao) {
		try {
			return siconFacade.possuiCodSiasg(numeroLicitacao);
		} catch (Exception e) {
			return Boolean.FALSE;
		}
	}

	public String tooltipAfPossuiCodSiasg(Integer numeroLicitacao) {
		try {
			if (siconFacade.possuiCodSiasg(numeroLicitacao)
					.equals(Boolean.TRUE)) {
				// TRUE - pinta de verde MENSAGEM_LIC_POSSUI_SIASG
				setToolTipComSiasg(getBundle().getString(
						"MENSAGEM_LIC_POSSUI_SIASG"));
				return getToolTipComSiasg();
			} else {
				// FALSE - pinta de vermelho MENSAGEM_LIC_NÃO_POSSUI_SIASG
				setToolTipSemSiasg(getBundle().getString(
						"MENSAGEM_LIC_NAO_POSSUI_SIASG"));
				return getToolTipSemSiasg();
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return "";
	}

	public List<ScoFornecedor> listarFornecedoresAtivos(final String pesquisa) {
		return (this.comprasFacade
				.listarFornAtivosComPropostaAceitaAfsSemContratos(pesquisa));
	}

	public List<ScoGrupoMaterial> pesquisarGrupoMateriais(String _input) {
		return this.comprasFacade.pesquisarGrupoMaterialPorFiltro(_input);
	}

	public List<ScoMaterial> pesquisarMateriais(String _input) {

		List<ScoMaterial> listaMaterial = null;
		try {
			listaMaterial = this.comprasFacade.listarScoMateriaisGrupoAtiva(
					_input, filtro.getGrupoMaterial(), true, false);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return listaMaterial;

	}

	public List<ScoServico> listarServicosAtivos(String pesquisa) {

		List<ScoServico> servicos = null;
		try {
			servicos = comprasFacade.listarServicosByNomeOrCodigoGrupoAtivo(
					pesquisa, filtro.getGrupoServico());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return servicos;
	}

	public List<ScoGrupoServico> listarGrupoServico(String pesquisa) {
		List<ScoGrupoServico> grupoServico = comprasFacade
				.listarGrupoServico(pesquisa);
		return grupoServico;
	}

	public List<ScoLicitacao> pesquisarLicitacoesAtivas(String pesquisa) {
		List<ScoLicitacao> licitacoesAtivas = pacFacade.listarLicitacoesAtivas(
				pesquisa, getParamModEmpenho());
		return licitacoesAtivas;
	}

	public List<ScoLicitacao> pesquisarLicitac(Object param) {
		try {
			return siconFacade.listarLicitacoesAtivas(param);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public LicitacaoFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(LicitacaoFiltroVO filtro) {
		this.filtro = filtro;
	}

	public LicitacaoFiltroVO getfiltro() {
		return filtro;
	}

	public void setfitro(LicitacaoFiltroVO filtro) {
		this.filtro = filtro;
	}

	public AfFiltroVO getFiltroAf() {
		return filtroAf;
	}

	public void setFiltroAf(AfFiltroVO filtroAf) {
		this.filtroAf = filtroAf;
	}

	public List<ListaLicitacaoVO> getListaLicVO() {
		return listaLicVO;
	}

	public void setListaLicVO(List<ListaLicitacaoVO> listaLicVO) {
		this.listaLicVO = listaLicVO;
	}

	public Boolean getNovaPesquisa() {
		return novaPesquisa;
	}

	public DominioModalidadeEmpenho getParamModEmpenho() {
		return paramModEmpenho;
	}

	public void setParamModEmpenho(DominioModalidadeEmpenho paramModEmpenho) {
		this.paramModEmpenho = paramModEmpenho;
	}

	public void setNovaPesquisa(Boolean novaPesquisa) {
		this.novaPesquisa = novaPesquisa;
	}

	public List<ListaDetalhesItensLicVO> getListaDetalhesItensLic() {
		return listaDetalhesItensLic;
	}

	public void setListaDetalhesItensLic(
			List<ListaDetalhesItensLicVO> listaDetalhesItensLic) {
		this.listaDetalhesItensLic = listaDetalhesItensLic;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public Integer getCurrentTabIndex() {
		return currentTabIndex;
	}

	public void setCurrentTabIndex(Integer currentTabIndex) {
		this.currentTabIndex = currentTabIndex;
	}

	public List<AfsContratosFuturosVO> getListaAfsVO() {
		return listaAfsVO;
	}

	public void setListaAfsVO(List<AfsContratosFuturosVO> listaAfsVO) {
		this.listaAfsVO = listaAfsVO;
	}

	public AfsContratosFuturosVO getAf() {
		return af;
	}

	public void setAf(AfsContratosFuturosVO af) {
		this.af = af;
	}

	public String getToolTipComSiasg() {
		return toolTipComSiasg;
	}

	public void setToolTipComSiasg(String toolTipComSiasg) {
		this.toolTipComSiasg = toolTipComSiasg;
	}

	public String getToolTipSemSiasg() {
		return toolTipSemSiasg;
	}

	public void setToolTipSemSiasg(String toolTipSemSiasg) {
		this.toolTipSemSiasg = toolTipSemSiasg;
	}

	public ListaLicitacaoVO getLic() {
		return lic;
	}

	public void setLic(ListaLicitacaoVO lic) {
		this.lic = lic;
	}

}
