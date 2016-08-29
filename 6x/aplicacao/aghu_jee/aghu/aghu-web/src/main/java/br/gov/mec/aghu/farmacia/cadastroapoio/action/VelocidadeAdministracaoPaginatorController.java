package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaTipoVelocAdministracoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class VelocidadeAdministracaoPaginatorController extends ActionController implements ActionPaginator{
	
	private static final long serialVersionUID = -5734244852827930036L;

	@EJB
	private IFarmaciaFacade farmaciaFacade;
	
	private Short filtroSeq;
	private String filtroDescricao;
	private BigDecimal filtroFatorConversaoMlH;
	private DominioSimNao filtroTipoUsualNpt;
	private DominioSimNao filtroTipoUsualSoroterapia;
	private DominioSituacao filtroSituacao;
	private AfaTipoVelocAdministracoes tipoVelocAdministracaoSelecionado;
	@Inject @Paginator
	private DynamicDataModel<AfaTipoVelocAdministracoes> dataModel;
	
	@PostConstruct
	public void init(){
		begin(conversation);
	}

	public void pesquisar() {
		dataModel.reiniciarPaginator();
		dataModel.setPesquisaAtiva(Boolean.TRUE);
	}

	public void limparPesquisa() {
		this.filtroSeq = null;
		this.filtroDescricao = null;
		this.filtroFatorConversaoMlH = null;
		this.filtroTipoUsualNpt = null;
		this.filtroTipoUsualSoroterapia = null;
		this.filtroSituacao = null;
		this.getDataModel().limparPesquisa();
	}
	
	public String editar(){
		return "velocidadeAdministracaoCRUD";
	}

	public void excluir() {
		try {
			if (tipoVelocAdministracaoSelecionado != null) {
				this.farmaciaFacade.removerAfaTipoVelocAdministracoes(tipoVelocAdministracaoSelecionado.getSeq());
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_VELOCIDADE_ADMINISTRACAO", tipoVelocAdministracaoSelecionado.getDescricao());
			} else {
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_REMOCAO_VELOCIDADE_ADMINISTRACAO_INVALIDA");
			}
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		dataModel.reiniciarPaginator();
	}
	
	public String iniciarInclusao() {
		return "velocidadeAdministracaoCRUD";
	}

	@Override
	public Long recuperarCount() {
		return this
				.farmaciaFacade
				.pesquisaAfaVelocidadesAdministracaoCount(
						this.filtroSeq,
						this.filtroDescricao,
						this.filtroFatorConversaoMlH,
						this.filtroTipoUsualNpt != null ? this.filtroTipoUsualNpt
								.isSim()
								: null,
						this.filtroTipoUsualSoroterapia != null ? this.filtroTipoUsualSoroterapia
								.isSim()
								: null, this.filtroSituacao);
	}

	@Override
	public List<AfaTipoVelocAdministracoes> recuperarListaPaginada(Integer firstResult, Integer maxResults, String orderProperty, boolean asc) {
		List<AfaTipoVelocAdministracoes> result = this
				.farmaciaFacade.pesquisaAfaVelocidadesAdministracao(firstResult, maxResults, orderProperty, asc,
						filtroSeq, filtroDescricao, filtroFatorConversaoMlH,
						filtroTipoUsualNpt != null ? this.filtroTipoUsualNpt.isSim() : null,
						this.filtroTipoUsualSoroterapia != null ? this.filtroTipoUsualSoroterapia.isSim() : null, 
						this.filtroSituacao);

		if (result == null) {
			result = new ArrayList<AfaTipoVelocAdministracoes>();
		}

		return result;
	}

	public Short getFiltroSeq() {
		return filtroSeq;
	}

	public void setFiltroSeq(Short filtroSeq) {
		this.filtroSeq = filtroSeq;
	}

	public String getFiltroDescricao() {
		return filtroDescricao;
	}

	public void setFiltroDescricao(String filtroDescricao) {
		this.filtroDescricao = filtroDescricao;
	}

	public DominioSimNao getFiltroTipoUsualNpt() {
		return filtroTipoUsualNpt;
	}

	public void setFiltroTipoUsualNpt(DominioSimNao filtroTipoUsualNpt) {
		this.filtroTipoUsualNpt = filtroTipoUsualNpt;
	}

	public DominioSimNao getFiltroTipoUsualSoroterapia() {
		return filtroTipoUsualSoroterapia;
	}

	public void setFiltroTipoUsualSoroterapia(DominioSimNao filtroTipoUsualSoroterapia) {
		this.filtroTipoUsualSoroterapia = filtroTipoUsualSoroterapia;
	}

	public DominioSituacao getFiltroSituacao() {
		return filtroSituacao;
	}

	public void setFiltroSituacao(DominioSituacao filtroSituacao) {
		this.filtroSituacao = filtroSituacao;
	}

	public BigDecimal getFiltroFatorConversaoMlH() {
		return filtroFatorConversaoMlH;
	}

	public void setFiltroFatorConversaoMlH(BigDecimal filtroFatorConversaoMlH) {
		this.filtroFatorConversaoMlH = filtroFatorConversaoMlH;
	}

	public DynamicDataModel<AfaTipoVelocAdministracoes> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AfaTipoVelocAdministracoes> dataModel) {
		this.dataModel = dataModel;
	}

	public AfaTipoVelocAdministracoes getTipoVelocAdministracaoSelecionado() {
		return tipoVelocAdministracaoSelecionado;
	}

	public void setTipoVelocAdministracaoSelecionado(
			AfaTipoVelocAdministracoes tipoVelocAdministracaoSelecionado) {
		this.tipoVelocAdministracaoSelecionado = tipoVelocAdministracaoSelecionado;
	}

}
