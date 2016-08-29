package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.AnuTipoItemDietaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class TiposDietaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5247563629715737921L;

	private static final String PAGE_TIPOS_DIETA_CRUD = "tiposDietaCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@Inject
	private IParametroFacade parametroFacade;

	// @EJB
	// private INutricaoFacade nutricaoFacade;

	@Inject @Paginator
	private DynamicDataModel<AnuTipoItemDietaVO> dataModel;

	private AnuTipoItemDietaVO parametroSelecionado;

	private boolean exibirBotaoIncluirTipoDieta;
	private Integer tipoDietaCodigo;
	private String descricaoTipoDieta;
	private DominioSituacao indSituacao;
	private Integer codigoDietaExclusao;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.exibirBotaoIncluirTipoDieta = true;
	}

	public void limparPesquisa() {
		this.tipoDietaCodigo = null;
		this.descricaoTipoDieta = null;
		this.exibirBotaoIncluirTipoDieta = false;
		this.getDataModel().limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarTipoItemDietaCount((this.tipoDietaCodigo != null) ? this.tipoDietaCodigo : null, this.descricaoTipoDieta,
				indSituacao);
	}

	@Override
	public List<AnuTipoItemDietaVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AnuTipoItemDietaVO> result = this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarTipoItemDieta(firstResult, maxResult, orderProperty, asc,
				(this.tipoDietaCodigo != null) ? this.tipoDietaCodigo : null, this.descricaoTipoDieta, this.indSituacao);
		if (result == null) {
			result = new ArrayList<AnuTipoItemDietaVO>();
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

			cadastrosBasicosPrescricaoMedicaFacade.removerTipoItemDieta(this.parametroSelecionado.getSeq(), vlrParam);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_TIPO_ITEM_DIETA", this.parametroSelecionado.getDescricao());

			this.tipoDietaCodigo = null;

		} catch (BaseListException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} finally {
			this.codigoDietaExclusao = null;
		}

	}

	public String inserirEditar() {
		return PAGE_TIPOS_DIETA_CRUD;
	}

	/*
	 * Getters and Setters
	 */
	public boolean isExibirBotaoIncluirTipoDieta() {
		return exibirBotaoIncluirTipoDieta;
	}

	public void setExibirBotaoIncluirTipoDieta(boolean exibirBotaoIncluirTipoDieta) {
		this.exibirBotaoIncluirTipoDieta = exibirBotaoIncluirTipoDieta;
	}

	public void setTipoDietaCodigo(Integer tipoDietaCodigo) {
		this.tipoDietaCodigo = tipoDietaCodigo;
	}

	public Integer getTipoDietaCodigo() {
		return tipoDietaCodigo;
	}

	public void setDescricaoTipoDieta(String descricaoTipoDieta) {
		this.descricaoTipoDieta = descricaoTipoDieta;
	}

	public String getDescricaoTipoDieta() {
		return descricaoTipoDieta;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public Integer getCodigoDietaExclusao() {
		return codigoDietaExclusao;
	}

	public void setCodigoDietaExclusao(Integer codigoDietaExclusao) {
		this.codigoDietaExclusao = codigoDietaExclusao;
	}

	public DynamicDataModel<AnuTipoItemDietaVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AnuTipoItemDietaVO> dataModel) {
		this.dataModel = dataModel;
	}

	public AnuTipoItemDietaVO getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(AnuTipoItemDietaVO parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}

}
