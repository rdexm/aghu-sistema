package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaGrupoMedicamentoMensagem;
import br.gov.mec.aghu.model.AfaMensagemMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class MensagemMedicamentoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 2517359426280571156L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	@Inject
	private MensagemMedicamentoController mensagemMedicamentoController;
	
	private Integer filtroSeq;
	private String filtroDescricao;
	private DominioSimNao filtroCoexistente;
	private DominioSituacao filtroSituacao;
	private AfaMensagemMedicamento mensagemMedicamentoSelecionado;
	private List<AfaGrupoMedicamentoMensagem> gruposMedicamentosMensagem;

	@Inject @Paginator
	private DynamicDataModel<AfaMensagemMedicamento> dataModel;
		
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	private static final Comparator<AfaGrupoMedicamentoMensagem> COMPARATOR_GRUPO_MENSAGEM_MEDICAMENTO = new Comparator<AfaGrupoMedicamentoMensagem>() {
	@Override
	public int compare(AfaGrupoMedicamentoMensagem o1, AfaGrupoMedicamentoMensagem o2) {
			return o1.getGrupoMedicamento().getDescricao().toUpperCase().compareTo(o2.getGrupoMedicamento().getDescricao().toUpperCase());
		}
	};

	public String iniciarInclusao() {
		mensagemMedicamentoController.setMensagemMedicamento(null);
		mensagemMedicamentoController.setEdicao(false);
		mensagemMedicamentoController.setSeqGrupoMedicamento(null);
		mensagemMedicamentoController.setGrupoMedicamento(null);
		mensagemMedicamentoController.setSituacao(null);

		if (mensagemMedicamentoController.getMensagemMedicamento() == null) {
			mensagemMedicamentoController.setMensagemMedicamento(new AfaMensagemMedicamento());
		}

		if (mensagemMedicamentoController.getMensagemMedicamento().getGruposMedicamentosMensagem() == null) {
			mensagemMedicamentoController.getMensagemMedicamento().setGruposMedicamentosMensagem(new ArrayList<AfaGrupoMedicamentoMensagem>());
		}
		mensagemMedicamentoController.setListaMedicamentosGrupos(mensagemMedicamentoController.getMensagemMedicamento().getGruposMedicamentosMensagem());
		
		return "mensagemMedicamentoCRUD";
	}
	
	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		this.filtroSeq = null;
		this.filtroDescricao = null;
		this.filtroCoexistente = null;
		this.filtroSituacao = null;
		dataModel.limparPesquisa();
	}
	
	public String editar() {
		mensagemMedicamentoController.setMensagemMedicamento(mensagemMedicamentoSelecionado);
		mensagemMedicamentoController.setListaMedicamentosGrupos(mensagemMedicamentoSelecionado.getGruposMedicamentosMensagem());
		
		return "mensagemMedicamentoCRUD";
	}

	public void excluir() {
		dataModel.reiniciarPaginator();
		try {
			if (mensagemMedicamentoSelecionado != null) {
				farmaciaFacade.removerAfaMensagemMedicamento(mensagemMedicamentoSelecionado.getSeq());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MENSAGEM_MEDICAMENTO");
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_MENSAGEM_MEDICAMENTO_INVALIDA");
			}
			mensagemMedicamentoSelecionado = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String obtemDescricaoReduzida(String descricao) {
		if(descricao.length() > 56) {
			return descricao.substring(0, 56).concat("...");
		}
		return descricao;
	}

	public void exibeGruposMedicamentosMensagem(
			AfaMensagemMedicamento _mensagemMedicamento) {
		this.gruposMedicamentosMensagem = _mensagemMedicamento.getGruposMedicamentosMensagem();

		Collections.sort(this.gruposMedicamentosMensagem, COMPARATOR_GRUPO_MENSAGEM_MEDICAMENTO);
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaFacade.pesquisaAfaMensagemMedicamentoCount(this.filtroSeq, this.filtroDescricao,
				this.filtroCoexistente != null ? this.filtroCoexistente.isSim() : null, this.filtroSituacao);
	}

	@Override
	public List<AfaMensagemMedicamento> recuperarListaPaginada(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc) {
		List<AfaMensagemMedicamento> result = this.farmaciaFacade
				.pesquisaAfaMensagemMedicamento(firstResult, maxResults, orderProperty, asc, this.filtroSeq, this.filtroDescricao, 
						this.filtroCoexistente != null ? this.filtroCoexistente .isSim() : null, this.filtroSituacao);

		if (result == null) {
			result = new ArrayList<AfaMensagemMedicamento>();
		}

		return result;
	}

	public Integer getFiltroSeq() {
		return filtroSeq;
	}

	public void setFiltroSeq(Integer filtroSeq) {
		this.filtroSeq = filtroSeq;
	}

	public String getFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(String filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public DominioSimNao getFiltroCoexistente() {
		return filtroCoexistente;
	}

	public void setFiltroCoexistente(DominioSimNao filtroCoexistente) {
		this.filtroCoexistente = filtroCoexistente;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public List<AfaGrupoMedicamentoMensagem> getGruposMedicamentosMensagem() {
		return gruposMedicamentosMensagem;
	}

	public void setGruposMedicamentosMensagem(
			List<AfaGrupoMedicamentoMensagem> gruposMedicamentosMensagem) {
		this.gruposMedicamentosMensagem = gruposMedicamentosMensagem;
	}

	public IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	public void setFarmaciaFacade(IFarmaciaFacade farmaciaFacade) {
		this.farmaciaFacade = farmaciaFacade;
	}

	public DynamicDataModel<AfaMensagemMedicamento> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<AfaMensagemMedicamento> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaMensagemMedicamento getMensagemMedicamentoSelecionado() {
		return mensagemMedicamentoSelecionado;
	}

	public void setMensagemMedicamentoSelecionado(
			AfaMensagemMedicamento mensagemMedicamentoSelecionado) {
		this.mensagemMedicamentoSelecionado = mensagemMedicamentoSelecionado;
	}
}
