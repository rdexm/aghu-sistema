package br.gov.mec.aghu.estoque.pesquisa.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.estoque.action.ManterPacoteMateriaisController;
import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScePacoteMateriais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller responsável pela paginação de pacotes de materiais.
 * 
 * @author guilherme.finotti
 * 
 */

public class PesquisaPacoteMateriaisPaginatorController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = 4045408205041186167L;

	@Inject @Paginator
	private DynamicDataModel<ScePacoteMateriais> dataModel;

	private ScePacoteMateriais parametroSelecionado;

    @Inject
    private ManterPacoteMateriaisController manterPacoteMateriaisController;

	public enum EnumPesquisarPacoteMateriaisMessageCode {
		SUCESSO_REMOVER_PACOTE_MATERIAIS;
	}


	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	protected ICentroCustoFacade centroCustoFacade;

	// filtros de pesquisa
	private FccCentroCustos centroCustoProprietario;
	private FccCentroCustos centroCustoAplicacao;
	private SceAlmoxarifado almoxarifado;
	private Integer numeroPacote;
	private DominioSituacao situacao;
	private Integer codigoCentroCustoProprietarioPesquisa;
	private Integer codigoCentroCustoAplicacaoPesquisa;
	private Short codigoAlmoxarifadoPesquisa;

	// atributos que compõem o id do pacote, para exclusão
	private Integer numero;
	private Integer codigoCentroCustoProprietario;
	private Integer codigoCentroCustoAplicacao;

	// usado quando a pesquisa retornar apenas um registro
	private Integer numeroPacoteMaterial;
	private Integer codCctProprietario;
	private Integer codCctAplicacao;

	// armazenará o pacote para exclusão
	private ScePacoteMateriais pacoteMaterial;

	// controla a exibição do botão NOVO
	private Boolean exibirBotaoNovo = Boolean.FALSE;

	private List<ScePacoteMateriais> listRetorno;

	// public void inicio() {
	// // TODO REVISAR
	// // if (this.ativo) {
	// // this.dirty = true;
	// // this.reiniciarContagem = true;
	// // }
	// }

	/**
	 * Retorna a lista de pacotes de materiais
	 */
	@Override
	public List<ScePacoteMateriais> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		if (getCentroCustoProprietario() != null) {
			setCodigoCentroCustoProprietarioPesquisa(getCentroCustoProprietario().getCodigo());
		}
		if (getCentroCustoAplicacao() != null) {
			setCodigoCentroCustoAplicacaoPesquisa(getCentroCustoAplicacao().getCodigo());
		}
		if (getAlmoxarifado() != null) {
			setCodigoAlmoxarifadoPesquisa(getAlmoxarifado().getSeq());
		}

		listRetorno = estoqueFacade.pesquisarPacoteMateriais(firstResult, maxResult, orderProperty, asc, getCodigoCentroCustoProprietarioPesquisa(), getCodigoCentroCustoAplicacaoPesquisa(),
				getNumeroPacote(), getCodigoAlmoxarifadoPesquisa(), getSituacao());
		return listRetorno;
	}

	/**
	 * Retorna a quantidade de pacotes de materiais obtidos na pesquisa
	 */
	@Override
	public Long recuperarCount() {
		if (getCentroCustoProprietario() != null) {
			setCodigoCentroCustoProprietarioPesquisa(getCentroCustoProprietario().getCodigo());
		} else {
			setCodigoCentroCustoProprietarioPesquisa(null);
		}
		if (getCentroCustoAplicacao() != null) {
			setCodigoCentroCustoAplicacaoPesquisa(getCentroCustoAplicacao().getCodigo());
		} else {
			setCodigoCentroCustoAplicacaoPesquisa(null);
		}
		if (getAlmoxarifado() != null) {
			setCodigoAlmoxarifadoPesquisa(getAlmoxarifado().getSeq());
		} else {
			setCodigoAlmoxarifadoPesquisa(null);
		}
		return estoqueFacade.pesquisarPacoteMateriaisCount(getCodigoCentroCustoProprietarioPesquisa(), getCodigoCentroCustoAplicacaoPesquisa(), getNumeroPacote(), getCodigoAlmoxarifadoPesquisa(),
				getSituacao());
	}

	/**
	 * Executa a pesquisa de pacotes de materiais
	 */
	public String pesquisar() {

		String retorno = null;
		Long count = recuperarCount();
		if (count == 1L && listRetorno != null && listRetorno.size() > 0) {
			ScePacoteMateriais pct = listRetorno.get(0);
			numeroPacoteMaterial = pct.getId().getNumero();
			codCctProprietario = pct.getId().getCodigoCentroCustoProprietario();
			codCctAplicacao = pct.getId().getCodigoCentroCustoAplicacao();
            return manterPacoteMateriaisController.editar(pct);
		}
		this.getDataModel().reiniciarPaginator();
		setExibirBotaoNovo(Boolean.TRUE);
		return retorno;
	}

	/**
	 * Retorna os centros custos ativos para filtro da pesquisa
	 * 
	 * @param parametro
	 * @return
	 */
	public List<FccCentroCustos> pesquisarCentrosCustoAtivos(String parametro) {
		return centroCustoFacade.pesquisarCentroCustosAtivos(parametro);
	}

	/**
	 * Retorna os almoxarifados ativos para filtro da pesquisa
	 * 
	 * @param parametro
	 * @return
	 */
	public List<SceAlmoxarifado> pesquisarAlmoxarifadosAtivosPorCodigoDescricao(String parametro) {
		return estoqueFacade.pesquisarAlmoxarifadosAtivosPorCodigoDescricaoOrdenadosPelaDescricao(parametro);
	}

	/**
	 * Exclui o pacote e os itens desse pacote.
	 */
	public void excluir() {
		if (getPacoteMaterial() != null) {
			try {
				estoqueFacade.removerPacoteMaterial(getPacoteMaterial());
				pesquisar();
				apresentarMsgNegocio(Severity.WARN, EnumPesquisarPacoteMateriaisMessageCode.SUCESSO_REMOVER_PACOTE_MATERIAIS.toString());
			} catch (BaseListException e) {
				apresentarExcecaoNegocio(e);
			}

		}
	}

	/**
	 * Limpa os campos da tela
	 */
	public void limparCampos() {
		setCodigoCentroCustoProprietarioPesquisa(null);
		setCodigoCentroCustoAplicacaoPesquisa(null);
		setCodigoAlmoxarifadoPesquisa(null);
		setCentroCustoProprietario(null);
		setCentroCustoProprietario(null);
		setCentroCustoAplicacao(null);
		setAlmoxarifado(null);
		setNumeroPacote(null);
		setSituacao(null);
		this.getDataModel().limparPesquisa();
		setExibirBotaoNovo(Boolean.FALSE);
	}

	/**
	 * abrevia string (descrição do centro de custo) para apresentação na tela
	 * 
	 * @param str
	 * @param maxWidth
	 * @return
	 */
	public String abreviar(String str, int maxWidth) {
		String abreviado = null;
		if (str != null) {
			abreviado = " " + StringUtils.abbreviate(str, maxWidth);
		}
		return abreviado;
	}

	public ScePacoteMateriais getPacoteMaterial() {
		return pacoteMaterial;
	}

	public void setPacoteMaterial(ScePacoteMateriais pacoteMaterial) {
		this.pacoteMaterial = pacoteMaterial;
	}

	public FccCentroCustos getCentroCustoProprietario() {
		return centroCustoProprietario;
	}

	public void setCentroCustoProprietario(FccCentroCustos centroCustoProprietario) {
		this.centroCustoProprietario = centroCustoProprietario;
	}

	public FccCentroCustos getCentroCustoAplicacao() {
		return centroCustoAplicacao;
	}

	public void setCentroCustoAplicacao(FccCentroCustos centroCustoAplicacao) {
		this.centroCustoAplicacao = centroCustoAplicacao;
	}

	public SceAlmoxarifado getAlmoxarifado() {
		return almoxarifado;
	}

	public void setAlmoxarifado(SceAlmoxarifado almoxarifado) {
		this.almoxarifado = almoxarifado;
	}

	public Integer getNumeroPacote() {
		return numeroPacote;
	}

	public void setNumeroPacote(Integer numeroPacote) {
		this.numeroPacote = numeroPacote;
	}

	public DominioSituacao getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacao situacao) {
		this.situacao = situacao;
	}

	public Boolean getExibirBotaoNovo() {
		return exibirBotaoNovo;
	}

	public void setExibirBotaoNovo(Boolean exibirBotaoNovo) {
		this.exibirBotaoNovo = exibirBotaoNovo;
	}

	public Integer getNumero() {
		return numero;
	}

	public void setNumero(Integer numero) {
		this.numero = numero;
	}

	public Integer getCodigoCentroCustoProprietario() {
		return codigoCentroCustoProprietario;
	}

	public void setCodigoCentroCustoProprietario(Integer codigoCentroCustoProprietario) {
		this.codigoCentroCustoProprietario = codigoCentroCustoProprietario;
	}

	public Integer getCodigoCentroCustoAplicacao() {
		return codigoCentroCustoAplicacao;
	}

	public void setCodigoCentroCustoAplicacao(Integer codigoCentroCustoAplicacao) {
		this.codigoCentroCustoAplicacao = codigoCentroCustoAplicacao;
	}

	public Integer getCodigoCentroCustoProprietarioPesquisa() {
		return codigoCentroCustoProprietarioPesquisa;
	}

	public void setCodigoCentroCustoProprietarioPesquisa(Integer codigoCentroCustoProprietarioPesquisa) {
		this.codigoCentroCustoProprietarioPesquisa = codigoCentroCustoProprietarioPesquisa;
	}

	public Integer getCodigoCentroCustoAplicacaoPesquisa() {
		return codigoCentroCustoAplicacaoPesquisa;
	}

	public void setCodigoCentroCustoAplicacaoPesquisa(Integer codigoCentroCustoAplicacaoPesquisa) {
		this.codigoCentroCustoAplicacaoPesquisa = codigoCentroCustoAplicacaoPesquisa;
	}

	public Short getCodigoAlmoxarifadoPesquisa() {
		return codigoAlmoxarifadoPesquisa;
	}

	public void setCodigoAlmoxarifadoPesquisa(Short codigoAlmoxarifadoPesquisa) {
		this.codigoAlmoxarifadoPesquisa = codigoAlmoxarifadoPesquisa;
	}

	public Integer getNumeroPacoteMaterial() {
		return numeroPacoteMaterial;
	}

	public void setNumeroPacoteMaterial(Integer numeroPacoteMaterial) {
		this.numeroPacoteMaterial = numeroPacoteMaterial;
	}

	public Integer getCodCctProprietario() {
		return codCctProprietario;
	}

	public void setCodCctProprietario(Integer codCctProprietario) {
		this.codCctProprietario = codCctProprietario;
	}

	public Integer getCodCctAplicacao() {
		return codCctAplicacao;
	}

	public void setCodCctAplicacao(Integer codCctAplicacao) {
		this.codCctAplicacao = codCctAplicacao;
	}

	public DynamicDataModel<ScePacoteMateriais> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<ScePacoteMateriais> dataModel) {
		this.dataModel = dataModel;
	}

	public ScePacoteMateriais getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(ScePacoteMateriais parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}