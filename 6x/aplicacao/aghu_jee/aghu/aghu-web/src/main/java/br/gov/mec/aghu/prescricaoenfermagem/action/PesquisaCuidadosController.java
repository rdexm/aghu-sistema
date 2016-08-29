package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.model.EpeCuidados;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class PesquisaCuidadosController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 4434388823667982572L;

	private static final String PAGE_CADASTRO_CUIDADOS = "prescricaoenfermagem-cadastroCuidados";
	private static final String PAGE_RELATORIO_ROTINA_CUIDADO = "prescricaoenfermagem-relatorioRotinaCuidadoPdf";

	@Inject @Paginator
	private DynamicDataModel<EpeCuidados> dataModel;

	private EpeCuidados epeCuidados;
	private EpeCuidados epeCuidadosDelecao;
	private EpeCuidados epeCuidadosSelecionado;
	private boolean exibirBtnNovo;
	private Short seq;	

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;


	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

		//#36938 rkempfer 23/05/2014
//		this.dataModel.setUserRemovePermission(this.securityController.usuarioTemPermissao("manterCuidadoEnfermagem", "alterar"));
		
		if (!dataModel.getPesquisaAtiva()) {
			epeCuidados = new EpeCuidados();
		}
	
	}

	@Override
	public List<EpeCuidados> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		return prescricaoEnfermagemFacade.pesquisarEpeCuidadosPorCodigoDescricao(getEpeCuidados().getSeq(), getEpeCuidados()
				.getDescricao(), firstResult, maxResult, orderProperty, asc, true);
	}

	@Override
	public Long recuperarCount() {
		return prescricaoEnfermagemFacade.pesquisarEpeCuidadosPorCodigoDescricaoCount(getEpeCuidados().getSeq(), getEpeCuidados()
				.getDescricao());
	}

	public void pesquisar() {
		exibirBtnNovo = true;
		dataModel.reiniciarPaginator();
	}

	public String incluirNovo() {
		epeCuidados = new EpeCuidados();
		return PAGE_CADASTRO_CUIDADOS;
	}
	
	public String editar() {
		return PAGE_CADASTRO_CUIDADOS;
	}

	public String imprimir() {
		return PAGE_RELATORIO_ROTINA_CUIDADO;
	}

	public void excluir() {
		try {
			String msgRetorno = this.prescricaoEnfermagemFacade.excluirEpeCuidados(epeCuidadosSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, msgRetorno);
			this.pesquisar();
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public void limpar() {
		exibirBtnNovo = false;
		dataModel.setPesquisaAtiva(Boolean.FALSE);
		epeCuidados = new EpeCuidados();
	}

	public EpeCuidados getEpeCuidados() {
		if (epeCuidados == null) {
			epeCuidados = new EpeCuidados();
		}
		return epeCuidados;
	}

	public void setEpeCuidados(EpeCuidados epeCuidados) {
		this.epeCuidados = epeCuidados;
	}

	public boolean isExibirBtnNovo() {
		return exibirBtnNovo;
	}

	public void setExibirBtnNovo(boolean exibirBtnNovo) {
		this.exibirBtnNovo = exibirBtnNovo;
	}

	public void setSeq(Short seq) {
		this.seq = seq;
	}

	public Short getSeq() {
		return seq;
	}

	public EpeCuidados getEpeCuidadosDelecao() {
		return epeCuidadosDelecao;
	}

	public void setEpeCuidadosDelecao(EpeCuidados epeCuidadosDelecao) {
		this.epeCuidadosDelecao = epeCuidadosDelecao;
	}

	public DynamicDataModel<EpeCuidados> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<EpeCuidados> dataModel) {
		this.dataModel = dataModel;
	}

	public EpeCuidados getEpeCuidadosSelecionado() {
		return epeCuidadosSelecionado;
	}

	public void setEpeCuidadosSelecionado(EpeCuidados epeCuidadosSelecionado) {
		this.epeCuidadosSelecionado = epeCuidadosSelecionado;
	}
}
