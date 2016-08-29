package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Conversation;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.model.AfaTipoUsoMdto;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class TipoUsoMdtoPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4228784920259694424L;
	
	private final String REDIRECIONA_CADASTRAR_EDITAR_TIPO_USO_MDTO = "tipoUsoMedicamentoCRUD";
	
	private AfaTipoUsoMdto tipoUsoMedicamentoPesquisa;
	private AfaTipoUsoMdto tipoUsoMedicamentoSelecionado;
	
	@Inject
	private TipoUsoMdtoController tipoUsoMdtoController;
	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	@Inject
	private IFarmaciaFacade farmaciaFacade;
	@Inject
	private Conversation conversation;
	
	@Inject @Paginator
	private DynamicDataModel<AfaTipoUsoMdto> dataModel;
	
	private DominioSimNao indAntimicrobiano;
	private DominioSimNao indExigeJustificativa;
	private DominioSimNao indAvaliacao;
	private DominioSimNao indExigeDuracaoSolicitada;
	private DominioSimNao indControlado;
	private DominioSimNao indQuimioterapico;
	
	@PostConstruct
	public void init(){
		begin(conversation);
		tipoUsoMedicamentoPesquisa = new AfaTipoUsoMdto();
	}
	
	public void limparPesquisa(){
		tipoUsoMedicamentoPesquisa = new AfaTipoUsoMdto();
		indAntimicrobiano = null;
		indExigeJustificativa = null;
		indAvaliacao = null;
		indExigeDuracaoSolicitada = null;
		indControlado = null;
		indQuimioterapico = null;
		this.dataModel.limparPesquisa();
	}
	
	public void efetuarPesquisa(){
		dominioSimNaoToTipoUsoMdto();
		this.dataModel.reiniciarPaginator();
	}

	public String novo() {
		AfaTipoUsoMdto atum = new AfaTipoUsoMdto();
		atum.setIndSituacao(DominioSituacao.A);
		tipoUsoMdtoController.setAfaTipoUsoMdto(atum);
		tipoUsoMdtoController.setNovoRegistro(Boolean.TRUE);
		return REDIRECIONA_CADASTRAR_EDITAR_TIPO_USO_MDTO;
	}
	
	public void excluir() {
		try {
			farmaciaApoioFacade.removerTipoUsoMdto(tipoUsoMedicamentoSelecionado.getSigla());			
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_TIPO_USO_MEDICAMENTOS", tipoUsoMedicamentoSelecionado.getDescricao());			
			this.dataModel.reiniciarPaginator();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String editar(){
		tipoUsoMdtoController.setAfaTipoUsoMdto(tipoUsoMedicamentoSelecionado);
		tipoUsoMdtoController.setNovoRegistro(Boolean.FALSE);
		return REDIRECIONA_CADASTRAR_EDITAR_TIPO_USO_MDTO;
	}

	@Override
	public Long recuperarCount() {
		return this.farmaciaApoioFacade.pesquisarTipoUsoMdtoCount(tipoUsoMedicamentoPesquisa);
	}

	@Override
	public List<AfaTipoUsoMdto> recuperarListaPaginada(Integer firstResult,
			Integer maxResult, String order, boolean asc) {
		return this.farmaciaApoioFacade.pesquisarTipoUsoMdto(firstResult, maxResult,
				AfaTipoUsoMdto.Fields.DESCRICAO.toString(), true,	tipoUsoMedicamentoPesquisa);
	}

	private void dominioSimNaoToTipoUsoMdto(){
		tipoUsoMedicamentoPesquisa.setIndAntimicrobiano(null);
		tipoUsoMedicamentoPesquisa.setIndExigeJustificativa(null);
		tipoUsoMedicamentoPesquisa.setIndAvaliacao(null);
		tipoUsoMedicamentoPesquisa.setIndExigeDuracaoSolicitada(null);
		tipoUsoMedicamentoPesquisa.setIndControlado(null);
		tipoUsoMedicamentoPesquisa.setIndQuimioterapico(null);
		
		if(indAntimicrobiano != null){
			tipoUsoMedicamentoPesquisa.setIndAntimicrobiano(indAntimicrobiano.isSim());
		}
		if(indExigeJustificativa != null){
			tipoUsoMedicamentoPesquisa.setIndExigeJustificativa(indExigeJustificativa.isSim());		
		}
		if(indAvaliacao != null){
			tipoUsoMedicamentoPesquisa.setIndAvaliacao(indAvaliacao.isSim());
		}
		if(indExigeDuracaoSolicitada != null){
			tipoUsoMedicamentoPesquisa.setIndExigeDuracaoSolicitada(indExigeDuracaoSolicitada.isSim());
		}
		if(indControlado != null){
			tipoUsoMedicamentoPesquisa.setIndControlado(indControlado.isSim());
		}
		if(indQuimioterapico != null){
			tipoUsoMedicamentoPesquisa.setIndQuimioterapico(indQuimioterapico.isSim());
		}
	}
	public AfaTipoUsoMdto getTipoUsoMedicamentoPesquisa() {
		return tipoUsoMedicamentoPesquisa;
	}

	public void setTipoUsoMedicamentoPesquisa(
			AfaTipoUsoMdto tipoUsoMedicamentoPesquisa) {
		this.tipoUsoMedicamentoPesquisa = tipoUsoMedicamentoPesquisa;
	}

	public AfaTipoUsoMdto getTipoUsoMedicamentoSelecionado() {
		return tipoUsoMedicamentoSelecionado;
	}

	public void setTipoUsoMedicamentoSelecionado(
			AfaTipoUsoMdto tipoUsoMedicamentoSelecionado) {
		this.tipoUsoMedicamentoSelecionado = tipoUsoMedicamentoSelecionado;
	}
	
	public List<AfaGrupoUsoMedicamento> getListGruposUsoMedicamentos() {
		return this.farmaciaFacade.obterTodosGruposUsoMedicamento();
	}

	public DynamicDataModel<AfaTipoUsoMdto> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaTipoUsoMdto> dataModel) {
		this.dataModel = dataModel;
	}

	public DominioSimNao getIndAntimicrobiano() {
		return indAntimicrobiano;
	}

	public void setIndAntimicrobiano(DominioSimNao indAntimicrobiano) {
		this.indAntimicrobiano = indAntimicrobiano;
	}

	public DominioSimNao getIndExigeJustificativa() {
		return indExigeJustificativa;
	}

	public void setIndExigeJustificativa(DominioSimNao indExigeJustificativa) {
		this.indExigeJustificativa = indExigeJustificativa;
	}

	public DominioSimNao getIndAvaliacao() {
		return indAvaliacao;
	}

	public void setIndAvaliacao(DominioSimNao indAvaliacao) {
		this.indAvaliacao = indAvaliacao;
	}

	public DominioSimNao getIndExigeDuracaoSolicitada() {
		return indExigeDuracaoSolicitada;
	}

	public void setIndExigeDuracaoSolicitada(DominioSimNao indExigeDuracaoSolicitada) {
		this.indExigeDuracaoSolicitada = indExigeDuracaoSolicitada;
	}

	public DominioSimNao getIndControlado() {
		return indControlado;
	}

	public void setIndControlado(DominioSimNao indControlado) {
		this.indControlado = indControlado;
	}

	public DominioSimNao getIndQuimioterapico() {
		return indQuimioterapico;
	}

	public void setIndQuimioterapico(DominioSimNao indQuimioterapico) {
		this.indQuimioterapico = indQuimioterapico;
	}

}