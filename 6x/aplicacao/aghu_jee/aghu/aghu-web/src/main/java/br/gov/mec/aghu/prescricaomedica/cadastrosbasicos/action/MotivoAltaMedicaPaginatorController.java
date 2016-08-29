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
import br.gov.mec.aghu.model.MpmMotivoAltaMedica;
import br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business.ICadastrosBasicosPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.Severity;

public class MotivoAltaMedicaPaginatorController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -6371919134444530928L;

	private static final String PAGE_MOTIVO_ALTAMEDICA_CRUD = "motivoAltaMedicaCrud";

	@EJB
	private ICadastrosBasicosPrescricaoMedicaFacade cadastrosBasicosPrescricaoMedicaFacade;

	@Inject
	private IParametroFacade parametroFacade;

	@Inject @Paginator
	private DynamicDataModel<MpmMotivoAltaMedica> dataModel;

	private MpmMotivoAltaMedica parametroSelecionado;

	private boolean exibirBotaoIncluirPlano;
	private Integer motivoAltaMedicaCodigo;
	private String descricaoMotivoAltaMedica;
	private String siglaMotivoAltaMedica;
	private DominioSituacao indSituacao;

	@PostConstruct
	public void init() {
		begin(conversation, true);
	}

	public void pesquisar() {
		this.getDataModel().reiniciarPaginator();
		this.exibirBotaoIncluirPlano = true;
	}

	public void limparPesquisa() {
		this.motivoAltaMedicaCodigo = null;
		this.descricaoMotivoAltaMedica = null;
		this.siglaMotivoAltaMedica = null;
		this.exibirBotaoIncluirPlano = false;
		this.indSituacao = null;
		this.getDataModel().limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		return this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarMotivoAltaMedicaCount((this.motivoAltaMedicaCodigo != null) ? this.motivoAltaMedicaCodigo : null,
				this.descricaoMotivoAltaMedica, this.siglaMotivoAltaMedica, indSituacao);
	}

	@Override
	public List<MpmMotivoAltaMedica> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<MpmMotivoAltaMedica> result = this.cadastrosBasicosPrescricaoMedicaFacade.pesquisarMotivoAltaMedica(firstResult, maxResult, orderProperty, true,
				(this.motivoAltaMedicaCodigo != null) ? this.motivoAltaMedicaCodigo : null, this.descricaoMotivoAltaMedica, this.siglaMotivoAltaMedica, this.indSituacao);
		if (result == null) {
			result = new ArrayList<MpmMotivoAltaMedica>();
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

			cadastrosBasicosPrescricaoMedicaFacade.removerMotivoAltaMedica(this.parametroSelecionado.getSeq(), vlrParam);
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_REMOCAO_MOTIVO_ALTA_MEDICA", parametroSelecionado.getDescricao());

			this.motivoAltaMedicaCodigo = null;

		} catch (BaseListException e) {
			super.apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public String inserirEditar() {
		return PAGE_MOTIVO_ALTAMEDICA_CRUD;
	}

	/*
	 * Getters and Setters
	 */
	public boolean isExibirBotaoIncluirPlano() {
		return exibirBotaoIncluirPlano;
	}

	public void setExibirBotaoIncluirPlano(boolean exibirBotaoIncluirPlano) {
		this.exibirBotaoIncluirPlano = exibirBotaoIncluirPlano;
	}

	public Integer getMotivoAltaMedicaCodigo() {
		return motivoAltaMedicaCodigo;
	}

	public void setMotivoAltaMedicaCodigo(Integer motivoAltaMedicaCodigo) {
		this.motivoAltaMedicaCodigo = motivoAltaMedicaCodigo;
	}

	public String getDescricaoMotivoAltaMedica() {
		return descricaoMotivoAltaMedica;
	}

	public void setDescricaoMotivoAltaMedica(String descricaoMotivoAltaMedica) {
		this.descricaoMotivoAltaMedica = descricaoMotivoAltaMedica;
	}

	public String getSiglaMotivoAltaMedica() {
		return siglaMotivoAltaMedica;
	}

	public void setSiglaMotivoAltaMedica(String siglaMotivoAltaMedica) {
		this.siglaMotivoAltaMedica = siglaMotivoAltaMedica;
	}

	public DominioSituacao getIndSituacao() {
		return indSituacao;
	}

	public void setIndSituacao(DominioSituacao indSituacao) {
		this.indSituacao = indSituacao;
	}

	public DynamicDataModel<MpmMotivoAltaMedica> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmMotivoAltaMedica> dataModel) {
		this.dataModel = dataModel;
	}

	public MpmMotivoAltaMedica getParametroSelecionado() {
		return parametroSelecionado;
	}

	public void setParametroSelecionado(MpmMotivoAltaMedica parametroSelecionado) {
		this.parametroSelecionado = parametroSelecionado;
	}
}