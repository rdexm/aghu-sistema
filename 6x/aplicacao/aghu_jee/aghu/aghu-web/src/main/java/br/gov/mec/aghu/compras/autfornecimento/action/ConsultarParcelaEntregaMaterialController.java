package br.gov.mec.aghu.compras.autfornecimento.action;

import br.gov.mec.aghu.compras.action.ConsultaSCSSPaginatorController;
import br.gov.mec.aghu.compras.autfornecimento.business.IAutFornecimentoFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.action.ConsultarUltimasComprasMaterialPaginatorController;
import br.gov.mec.aghu.compras.solicitacaocompras.action.PlanejamentoSolicitacaoComprasPaginatorController;
import br.gov.mec.aghu.compras.vo.ConsultarParcelasEntregaMateriaisVO;
import br.gov.mec.aghu.estoque.action.EstatisticaConsumoController;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;


public class ConsultarParcelaEntregaMaterialController extends ActionController implements ActionPaginator {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	@Inject @Paginator
	private DynamicDataModel<ConsultarParcelasEntregaMateriaisVO> dataModel;

	
	private static final String PAGE_COMPRAS_CONSULTA_SCSSLIST = "compras-consultaSCSSList";
	private static final String PAGE_ULTIMAS_COMPRAS = "compras-consultarUltimasComprasMaterialList";
	private static final String PAGE_ESTOQUE_ESTATISCTICA_CONSUMO = "estoque-estatisticaConsumo";
	private static final String PAGE_LIB_PARCELAS = "/pages/compras/autfornecimento/consultarParcelaEntregaMaterial.xhtml";
	private static final String PAGE_VISUALIZA_SC = "compras-planejamentoSolicitacaoCompras";
	//private static final String PAGE_SC_MATERIAL = "scMaterial";
	
	private static final long serialVersionUID = -1452646614677343552L;
	
	@EJB
	private IAutFornecimentoFacade autFornecimentoFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasFacade comprasFacade;
	
	private List<ConsultarParcelasEntregaMateriaisVO> listaParcelas;
	private String tipoConsulta;
	private String voltarParaUrl;
	private Integer numeroAf;
	private Short numeroComplemento;
	private Integer codigoMaterial;
	private String observacao;
	private ConsultarParcelasEntregaMateriaisVO vo;
	private Boolean exibirModal = false;
	private Boolean iniciou = false;

	@Inject
	ConsultaSCSSPaginatorController consultaSCSSPaginatorController;

	@Inject
	EstatisticaConsumoController estatisticaConsumoController;

	@Inject
	ConsultarUltimasComprasMaterialPaginatorController consultarUltimasComprasMaterialPaginatorController;

	@Inject
	PlanejamentoSolicitacaoComprasPaginatorController planejamentoSolicitacaoComprasPaginatorController;

	public void iniciar() {

		this.dataModel.setDefaultMaxRow(13);
		if(!iniciou){
			this.dataModel.reiniciarPaginator();
			iniciou = true;
		}

		if (listaParcelas != null && listaParcelas.size() > 0) {
			for (ConsultarParcelasEntregaMateriaisVO parcela : listaParcelas) {
				if (parcela.getIndPlanejamento()) {
					codigoMaterial = parcela.getCodMaterial();
					break;
				}
			}

			if (codigoMaterial == null) {
				codigoMaterial = listaParcelas.get(0).getCodMaterial();
			}
		}

	}

	/*
	 * Botões
	 */
	public void gravar() {
		try {
			getAutFornecimentoFacade().gravarParcelaEntregaMaterial(getListaParcelas(), vo);
			apresentarMsgNegocio(Severity.INFO, "CONSULTAR_PARCELA_ENTREGA_MATERIAL_GRAVADO_SUCESSO");
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void entregasPendentes() {
		setListaParcelas(getAutFornecimentoFacade().buscarEntregasPendentesMaterial(getNumeroAf(), getNumeroComplemento()));
	}
	
	public void atualizarVo(ConsultarParcelasEntregaMateriaisVO vo) {
		if (vo.getIndCancela()) {
			setExibirModal(Boolean.TRUE);
			openDialog("modalObservacaoWG");
		} else {
			setExibirModal(Boolean.FALSE);
		}

		this.vo = vo;
	}
	
	public void gravarObservacao() {
		
		for (ConsultarParcelasEntregaMateriaisVO vol : getListaParcelas()) {
			if (getVo().equals(vol)) {
				vol.setObservacao(getObservacao());
				vol.setIndCancela(Boolean.TRUE);
				gravar();
				break;
			}
		}
		setVo(null);
		setObservacao(null);
		setExibirModal(Boolean.FALSE);
	}
	
	public void fecharModal() {
		for (ConsultarParcelasEntregaMateriaisVO vol : getListaParcelas()) {
			if (getVo().equals(vol)) {
				vol.setIndCancela(Boolean.FALSE);
				break;
			}
		}

		setObservacao(null);
		setExibirModal(Boolean.FALSE);
		setVo(null);
	}
	
	public String scMaterial() {

		if (codigoMaterial == null) {
			codigoMaterial = listaParcelas.get(0).getCodMaterial();
		}

		consultaSCSSPaginatorController.setCodigoMaterial (codigoMaterial);
		consultaSCSSPaginatorController.setVoltarParaUrl(PAGE_LIB_PARCELAS);

		return PAGE_COMPRAS_CONSULTA_SCSSLIST;
	}
	
	public String ultimasCompras() {

		if (codigoMaterial == null) {
			codigoMaterial = listaParcelas.get(0).getCodMaterial();
		}

		consultarUltimasComprasMaterialPaginatorController.setCodigoMaterial (codigoMaterial);
		consultarUltimasComprasMaterialPaginatorController.setVoltarParaUrl(PAGE_LIB_PARCELAS);

		return PAGE_ULTIMAS_COMPRAS;
	}
	
	public String estatisticaConsumo() {

		if (codigoMaterial == null) {
			codigoMaterial = listaParcelas.get(0).getCodMaterial();
		}

		estatisticaConsumoController.setCodigoMaterial (codigoMaterial);
		estatisticaConsumoController.setVoltarPara(PAGE_LIB_PARCELAS);

		return PAGE_ESTOQUE_ESTATISCTICA_CONSUMO;
	}
	
	public String visualizaSc() {

		if (codigoMaterial == null) {
			codigoMaterial = listaParcelas.get(0).getCodMaterial();
		}

		ScoMaterial material = null;
		if (codigoMaterial != null) {
			 material = comprasFacade.obterMaterialPorId(codigoMaterial);
		}

		planejamentoSolicitacaoComprasPaginatorController.setMaterial(material);
		planejamentoSolicitacaoComprasPaginatorController.setVoltarParaUrl(PAGE_LIB_PARCELAS);

		return PAGE_VISUALIZA_SC;
	}
	
	public String cancelar() {
		return "cancelar";
	}

	public String obterStringTruncada(String str, Integer tamanhoMaximo) {
		if (str.length() > tamanhoMaximo) {
			str = str.substring(0, tamanhoMaximo) + "...";
		}
		return str;
	}

	public String obterMaterialTrunc(Integer cod, String mat, Integer tamanhoMaximo) {

		String str = Integer.toString(cod) + " - ";

		if (mat.length() > tamanhoMaximo) {
			mat = mat.substring(0, tamanhoMaximo) + "...";
		}

		StringBuffer result = new StringBuffer(str);
		result.append(mat);
		return result.toString();
	}

	/*
	 * Facade & Controllers
	 */
	
	public IAutFornecimentoFacade getAutFornecimentoFacade() {
		return autFornecimentoFacade;
	}

	/*
	 * Get & Set
	 */
	
	public List<ConsultarParcelasEntregaMateriaisVO> getListaParcelas() {
		return listaParcelas;
	}

	public void setListaParcelas(List<ConsultarParcelasEntregaMateriaisVO> listaParcelas) {
		this.listaParcelas = listaParcelas;
	}

	public String getTipoConsulta() {
		return tipoConsulta;
	}

	public void setTipoConsulta(String tipoConsulta) {
		this.tipoConsulta = tipoConsulta;
	}

	public String getVoltarParaUrl() {
		return voltarParaUrl;
	}

	public void setVoltarParaUrl(String voltarParaUrl) {
		this.voltarParaUrl = voltarParaUrl;
	}

	public Integer getNumeroAf() {
		return numeroAf;
	}

	public void setNumeroAf(Integer numeroAf) {
		this.numeroAf = numeroAf;
	}

	public Short getNumeroComplemento() {
		return numeroComplemento;
	}

	public void setNumeroComplemento(Short numeroComplemento) {
		this.numeroComplemento = numeroComplemento;
	}

	public Integer getCodigoMaterial() {
		return codigoMaterial;
	}

	public void setCodigoMaterial(Integer codigoMaterial) {
		this.codigoMaterial = codigoMaterial;
	}

	public String getObservacao() {
		return observacao;
	}

	public void setObservacao(String observacao) {
		this.observacao = observacao;
	}

	public ConsultarParcelasEntregaMateriaisVO getVo() {
		return vo;
	}

	public void setVo(ConsultarParcelasEntregaMateriaisVO vo) {
		this.vo = vo;
	}

	public Boolean getExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(Boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return registroColaboradorFacade;
	}


	@Override
	public Long recuperarCount() {
		Integer count = 0;
		try {
			if ("liberarEntregasPorItem".equals(getTipoConsulta())) {		
				count = getAutFornecimentoFacade().buscarEntregasPorItem().size();
			} else if ("parcelasLiberar".equals(getTipoConsulta())) {
				count = getAutFornecimentoFacade().buscarEntregasPorItemNumeroAfNumeroComplemento(getNumeroAf(), getNumeroComplemento()).size();
			} else if ("alterarProgramacao".equals(getTipoConsulta())) {
				count = getAutFornecimentoFacade().buscarEntregasPorItemNumeroLctNumeroComplemento(getNumeroAf(), getNumeroComplemento()).size();
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return Long.parseLong(count.toString());
	}

	@Override
	public List<ConsultarParcelasEntregaMateriaisVO> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc)   {
		List<ConsultarParcelasEntregaMateriaisVO> lista = new ArrayList<ConsultarParcelasEntregaMateriaisVO>();
		try {
			if ("liberarEntregasPorItem".equals(getTipoConsulta())) {
				setListaParcelas(getAutFornecimentoFacade().buscarEntregasPorItem());
			} else if ("parcelasLiberar".equals(getTipoConsulta())) {
				setListaParcelas(getAutFornecimentoFacade().buscarEntregasPorItemNumeroAfNumeroComplemento(getNumeroAf(), getNumeroComplemento()));
			} else if ("alterarProgramacao".equals(getTipoConsulta())) {
				setListaParcelas(getAutFornecimentoFacade().buscarEntregasPorItemNumeroLctNumeroComplemento(getNumeroAf(), getNumeroComplemento()));
			}
			Integer indiceFinal = firstResult + maxResults;
			for (int i = firstResult; i < indiceFinal; i++) {
				if(i < getListaParcelas().size()){
					getListaParcelas().get(i).setCorPrevEntrega(getAutFornecimentoFacade().verificaCorFundoPrevEntregas(getListaParcelas().get(i).getDataPrevEntrega()));
					lista.add(getListaParcelas().get(i));
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return lista;
	}

	public Boolean getIniciou() {
		return iniciou;
	}

	public void setIniciou(Boolean iniciou) {
		this.iniciou = iniciou;
	}
	 


	public DynamicDataModel<ConsultarParcelasEntregaMateriaisVO> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<ConsultarParcelasEntregaMateriaisVO> dataModel) {
	 this.dataModel = dataModel;
	}
}

