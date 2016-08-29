package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmPlanoPosAlta;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class PlanosPosAltaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -8836529165530157177L;

	private static final String PAGE_PLANOS_POSALTACRUD = "planosPosAltaCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@Inject @Paginator
	private DynamicDataModel<MpmPlanoPosAlta> dataModel;

	@Inject
	private IParametroFacade parametroFacade;

	private boolean exibirBotaoIncluirPlano;
	private Integer ainPlanoPosAltaCodigo;
	private String descricaoPlano;
	private DominioSimNao indExigeCidSecundario;
	private DominioSituacao indSituacao;
	private Integer codigoPlanoPosAltaExclusao;

	private MpmPlanoPosAlta parametroSelecionado;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.exibirBotaoIncluirPlano = true;
	}

	public void limparPesquisa() {
		this.ainPlanoPosAltaCodigo = null;
		this.descricaoPlano = null;
		this.exibirBotaoIncluirPlano = false;
		this.indSituacao = null;
		this.getDataModel().limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarPlanosPosAltaCount((this.ainPlanoPosAltaCodigo != null) ? this.ainPlanoPosAltaCodigo : null,
				this.descricaoPlano, indSituacao);
	}

	@SuppressWarnings("rawtypes")
	@Override
	public List<MpmPlanoPosAlta> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		if (this.descricaoPlano != null) {
			this.descricaoPlano = this.descricaoPlano.trim();
		}
		orderProperty = "descricao";
		List<MpmPlanoPosAlta> result = this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarPlanosPosAlta(firstResult, maxResult, orderProperty, true,
				(this.ainPlanoPosAltaCodigo != null) ? this.ainPlanoPosAltaCodigo : null, this.descricaoPlano, this.indSituacao);
		if (result == null) {
			result = new ArrayList<MpmPlanoPosAlta>();
		}
		return result;
	}

	public void excluir() throws ApplicationBusinessException {
		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);

			Integer vlrParam = 0;

			if (parametro.getVlrNumerico() != null) {
				vlrParam = parametro.getVlrNumerico().intValue();
			}

			this.cadastrosBasicosPrescricaoMedicaFacade.removerPlano(this.parametroSelecionado.getSeq(), vlrParam);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_PLANO_POS_ALTA", parametroSelecionado.getDescricao());

			this.ainPlanoPosAltaCodigo = null;
		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.codigoPlanoPosAltaExclusao = null;
		}
	}

	public String inserirEditar() {
		return PAGE_PLANOS_POSALTACRUD;
	}

	/*
	 * Getters and Setters
	 */

	public boolean isExibirBotaoIncluirAcomodacao() {
		return exibirBotaoIncluirPlano;
	}

	public void setExibirBotaoIncluirAcomodacao(boolean exibirBotaoIncluirAcomodacao) {
		this.exibirBotaoIncluirPlano = exibirBotaoIncluirAcomodacao;
	}

	public String getDescricaoPesquisaAcomodacao() {
		return descricaoPlano;
	}

	public void setDescricaoPesquisaAcomodacao(String descricaoPesquisaAcomodacao) {
		this.descricaoPlano = descricaoPesquisaAcomodacao;
	}

	public Integer getAinPlanoPosAltaCodigo() {
		return ainPlanoPosAltaCodigo;
	}

	public void setAinPlanoPosAltaCodigo(Integer ainPlanoPosAltaCodigo) {
		this.ainPlanoPosAltaCodigo = ainPlanoPosAltaCodigo;
	}

	public boolean isExibirBotaoIncluirPlano() {
		return exibirBotaoIncluirPlano;
	}

	public void setExibirBotaoIncluirPlano(boolean exibirBotaoIncluirPlano) {
		this.exibirBotaoIncluirPlano = exibirBotaoIncluirPlano;
	}

	public String getDescricaoPlano() {
		return descricaoPlano;
	}

	public void setDescricaoPlano(String descricaoPlano) {
		this.descricaoPlano = descricaoPlano;
	}

	public DominioSimNao getIndExigeCidSecundario() {
		return indExigeCidSecundario;
	}

	public void setIndExigeCidSecundario(DominioSimNao indExigeCidSecundario) {
		this.indExigeCidSecundario = indExigeCidSecundario;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getCodigoPlanoPosAltaExclusao() {
		return codigoPlanoPosAltaExclusao;
	}

	public void setCodigoPlanoPosAltaExclusao(Integer codigoPlanoPosAltaExclusao) {
		this.codigoPlanoPosAltaExclusao = codigoPlanoPosAltaExclusao;
	}

	public DynamicDataModel<MpmPlanoPosAlta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmPlanoPosAlta> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmPlanoPosAlta getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmPlanoPosAlta parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}