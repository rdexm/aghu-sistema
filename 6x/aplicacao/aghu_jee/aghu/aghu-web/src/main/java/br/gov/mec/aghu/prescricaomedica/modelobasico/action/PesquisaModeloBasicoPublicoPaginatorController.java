package br.gov.mec.aghu.prescricaomedica.modelobasico.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MpmModeloBasicoPrescricao;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.modelobasico.business.IModeloBasicoFacade;
import br.gov.mec.aghu.prescricaomedica.modelobasico.vo.ItensModeloBasicoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaModeloBasicoPublicoPaginatorController extends ActionController implements ActionPaginator {
	
	private static final long serialVersionUID = 3243083763889665706L;
	
	private static final String PAGE_MANTER_MODELO_BASICO = "manterModeloBasico";

	@EJB
	private IModeloBasicoFacade modeloBasicoFacade;

	@Inject
	protected ICentroCustoFacade centroCustoFacade;
	
	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	/**
	 * Servidor logado
	 */
	private RapServidores servidor;
	
	@Inject @Paginator
	private DynamicDataModel<MpmModeloBasicoPrescricao> dataModel;
	
	/**
	 * Lista de Modelos Básicos Públicos
	 */
	private List<MpmModeloBasicoPrescricao> modelosPublicos;
	
	/**
	 * Lista de itens de Modelos Básicos Públicos
	 */
	private List<ItensModeloBasicoVO> itensModeloBasicoVO;
	
	/**
	 * Filtro de pesquisa pela descrição
	 */
	private String descricao;
	
	/**
	 * Descricao do modelo selecionado
	 */
	private String descricaoModeloSelecionado;
	
	/**
	 * Modelo Básico selecionado
	 */
	private MpmModeloBasicoPrescricao modeloBasico;
	
	/**
	 * Filtro de pesquisa por FccCentroCustos
	 */
	private FccCentroCustos centroCustos;
	
	@PostConstruct
	public void init() {
		begin(conversation, true);
	}
	
	public String iniciar() {
	 

		try {
			this.servidor = registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		this.getDataModel().reiniciarPaginator();
		
		return null;
	
	}
	
	/**
	 * Pesquisa
	 */
	public void pesquisar() {
		
		this.getDataModel().reiniciarPaginator();
	}
	
	public void limparPesquisa() {
		
		this.descricao = null;
		this.centroCustos = null;
		this.getDataModel().limparPesquisa();
	}
	
	/**
	 * Cancelar a ação de copiar modelo.
	 * @return
	 */
	public String cancelar() {
		
		return PAGE_MANTER_MODELO_BASICO;
		
	}
	
	@Override
	public Long recuperarCount() {
		
		String descricaoCentroCusto = null;
		
		if (this.centroCustos != null) {
			descricaoCentroCusto = this.centroCustos.getDescricao();
		}
		
		return this.modeloBasicoFacade.pequisarModelosPublicosCount(this.descricao, descricaoCentroCusto);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<MpmModeloBasicoPrescricao> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
	
		String descricaoCentroCusto = null;
		
		if (this.centroCustos != null) {
			descricaoCentroCusto = this.centroCustos.getDescricao();
		}
		
		return this.modeloBasicoFacade.pequisarModelosPublicos(this.descricao, descricaoCentroCusto, firstResult, maxResult, orderProperty, asc);
	}
	
	/**
	 * Carrega a lista de itens do modelo basico
	 * @param seqModelo
	 */
	public void pesquisarItens(Integer seqModelo) {
		
		if (seqModelo != null) {
			
			modeloBasico = this.modeloBasicoFacade.obterModeloBasico(seqModelo);

			if (modeloBasico != null) {

				itensModeloBasicoVO = this.modeloBasicoFacade.obterListaItensModelo(seqModelo);
				descricaoModeloSelecionado = modeloBasico.getDescricao();
			}

		} else {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SEQ_MODELO_NAO_INFORMADA");
		}
	}
	
	/**
	 * Copia o modelo público selecionado para o usuário logado
	 * @param seqModelo
	 */
	public void copiarModelo(Integer seqModelo) {
		
		try {
			
			this.modeloBasicoFacade.copiarModeloBasico(seqModelo);
			this.apresentarMsgNegocio(Severity.INFO, "Modelo copiado com sucesso");
			
		} catch (BaseException e) {
			
			this.apresentarMsgNegocio(Severity.ERROR, "Erro ao copiar Modelo.");
		}
	}
	
	/**
	 * Método de Pesquisa da SB
	 * @param objPesquisa
	 * @return
	 */
	public List<FccCentroCustos> obterCentroCustos(String objPesquisa) {
		return this.centroCustoFacade.pesquisarCentroCustos(objPesquisa);
	}

	/*
	 * Getters and Setters
	 */
	public DynamicDataModel<MpmModeloBasicoPrescricao> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmModeloBasicoPrescricao> dataModel) {
		this.dataModel = dataModel;
	}
	
	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public String getDescricao() {
		return descricao;
	}

	public void setDescricao(String descricao) {
		this.descricao = descricao;
	}

	public List<MpmModeloBasicoPrescricao> getModelosPublicos() {
		return modelosPublicos;
	}

	public void setModelosPublicos(List<MpmModeloBasicoPrescricao> modelosPublicos) {
		this.modelosPublicos = modelosPublicos;
	}

	public FccCentroCustos getCentroCustos() {
		return centroCustos;
	}

	public void setCentroCustos(FccCentroCustos centroCustos) {
		this.centroCustos = centroCustos;
	}

	public List<ItensModeloBasicoVO> getItensModeloBasicoVO() {
		return itensModeloBasicoVO;
	}

	public void setItensModeloBasicoVO(List<ItensModeloBasicoVO> itensModeloBasicoVO) {
		this.itensModeloBasicoVO = itensModeloBasicoVO;
	}

	public MpmModeloBasicoPrescricao getModeloBasico() {
		return modeloBasico;
	}

	public void setModeloBasico(MpmModeloBasicoPrescricao modeloBasico) {
		this.modeloBasico = modeloBasico;
	}

	public String getDescricaoModeloSelecionado() {
		return descricaoModeloSelecionado;
	}

	public void setDescricaoModeloSelecionado(String descricaoModeloSelecionado) {
		this.descricaoModeloSelecionado = descricaoModeloSelecionado;
	}
	
}
