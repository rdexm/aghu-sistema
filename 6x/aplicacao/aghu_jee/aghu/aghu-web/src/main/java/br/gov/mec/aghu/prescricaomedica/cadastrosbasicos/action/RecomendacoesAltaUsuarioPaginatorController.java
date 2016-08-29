package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmServRecomendacaoAlta;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class RecomendacoesAltaUsuarioPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -336206804782911502L;

	private static final String PAGE_RECOMENDACOES_ALTA_POR_USUARIO_CRUD = "recomendacoesAltaPorUsuarioCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Inject @Paginator
	private DynamicDataModel<MpmServRecomendacaoAlta> dataModel;

	private MpmServRecomendacaoAlta parametroSelecionado;

	private String voltarPara = "cancelar";

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
	 

		this.getDataModel().reiniciarPaginator();
	
	}

	public void limparPesquisa() {
		this.getDataModel().limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		Long count;
		try {
			count = cadastrosBasicosPrescricaoMedicaFacade
					.pesquisarRecomendacoesUsuarioCount(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			count = 0L;
		}
		return count;
	}

	@Override
	public List<MpmServRecomendacaoAlta> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		List<MpmServRecomendacaoAlta> result;
		try {
			result = cadastrosBasicosPrescricaoMedicaFacade.pesquisarRecomendacoesUsuario(firstResult, maxResult, orderProperty, asc,
					registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()));
		} catch (ApplicationBusinessException e) {
			result = null;
		}

		if (result == null) {
			result = new ArrayList<MpmServRecomendacaoAlta>();
		}

		return result;
	}

	public void excluir() {
		try {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_DIAS_PERM_DEL_MPM);

			Integer vlrParam = 0;

			if (parametro.getVlrNumerico() != null) {
				vlrParam = parametro.getVlrNumerico().intValue();
			}

			this.cadastrosBasicosPrescricaoMedicaFacade.removerRecomendacao(this.parametroSelecionado.getId(), vlrParam);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_RECOMENDACAO_POR_USUARIO", parametroSelecionado.getDescricao());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String cancelar() {
		return this.voltarPara;
	}

	public String inserirEditar() {
		return PAGE_RECOMENDACOES_ALTA_POR_USUARIO_CRUD;
	}

	/*
	 * Getters and Setters
	 */
	public RapServidores getUsuario() {
		try {
			return registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(this.obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			return null;
		}
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public DynamicDataModel<MpmServRecomendacaoAlta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmServRecomendacaoAlta> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmServRecomendacaoAlta getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmServRecomendacaoAlta parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}