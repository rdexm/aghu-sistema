package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import java.util.Date;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AfaFormaDosagem;
import br.gov.mec.aghu.model.MpmUnidadeMedidaMedica;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

@SuppressWarnings("PMD.HierarquiaControllerIncorreta")
public class FormaDosagemController extends AbstractCrudMedicamentoController<AfaFormaDosagem> {
	
	private static final long serialVersionUID = -3205717349096887645L;
	
	@Inject
	private FormaDosagemPaginatorController formaDosagemPaginatorController;

	@Override
	protected void instanciarEntidade() {
		AfaFormaDosagem result = new AfaFormaDosagem();
		result.setSeq(null);
		result.setAfaMedicamentos(getMedicamento());
		result.setIndUsualPrescricao(Boolean.FALSE);
		result.setIndUsualNpt(Boolean.FALSE);
		result.setIndSituacao(DominioSituacao.A);
		setEntidade(result);
	}

	@Override
	protected String getPaginaInclusao() {
		return "formaDosagemCRUD";
	}

	@Override
	protected String getPaginaConfirmado() {
		return "formaDosagemList";
	}

	@Override
	protected String getPaginaCancelado() {
		return "formaDosagemList";
	}

	@Override
	protected String getPaginaErro() {
		return null;
	}

	@Override
	protected List<String> obterRazoesExcecao() {
		return this.getFarmaciaApoioFacade().obterRazoesExcessaoFormaDosagem();
	}

	@Override
	protected AfaFormaDosagem obterEntidadeOriginalViaEntidade(
			AfaFormaDosagem entidade) {
		return this.getFarmaciaFacade().obterAfaFormaDosagem(entidade.getSeq());
	}

	@Override
	protected void efetuarInclusao(AfaFormaDosagem entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		this.getFarmaciaApoioFacade().efetuarInclusao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarAlteracao(AfaFormaDosagem entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		this.getFarmaciaApoioFacade().efetuarAlteracao(entidade, nomeMicrocomputador, new Date());
	}

	@Override
	protected void efetuarRemocao(AfaFormaDosagem entidade)
			throws IllegalStateException, ApplicationBusinessException,
			BaseException {
		String nomeMicrocomputador = getNomeMicroComputador();
		this.getFarmaciaApoioFacade().efetuarRemocao(entidade, nomeMicrocomputador, new Date());
	}
	
	@Override
	protected void informarInclusaoSucesso(AfaFormaDosagem entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_CRIACAO_FORMA_DOSAGEM",
				entidade.getFatorConversaoUp().toPlainString(), 
				getMedicamento().getTipoApresentacaoMedicamento().getDescricao());
	}

	@Override
	protected void informarAlteracaoSucesso(AfaFormaDosagem entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_EDICAO_FORMA_DOSAGEM",
				entidade.getFatorConversaoUp().toPlainString(), 
				getMedicamento().getTipoApresentacaoMedicamento().getDescricao());
	}

	@Override
	protected void informarExclusaoSucesso(AfaFormaDosagem entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_SUCESSO_REMOCAO_FORMA_DOSAGEM",
				entidade.getFatorConversaoUp().toPlainString(), 
				getMedicamento().getTipoApresentacaoMedicamento().getDescricao());		
	}

	@Override
	protected void informarExclusaoErro(AfaFormaDosagem entidade) {
		this.apresentarMsgNegocio(Severity.INFO,
				"MENSAGEM_ERRO_REMOCAO_FORMA_DOSAGEM_INVALIDA");
	}

	@Override
	protected void reiniciarPaginatorController() {
		formaDosagemPaginatorController.getDataModel().reiniciarPaginator();
	}
	
	public Boolean getObrigaUMM() {
		
		boolean result = false;
		List<AfaFormaDosagem> fdUsadas = null;

		result = getEdicao();//this.getIsUpdate().booleanValue();
		if (!result) {
			fdUsadas = this.getFarmaciaApoioFacade().listaFormaDosagemMedicamento(getMedicamento().getCodigo());
			for (AfaFormaDosagem fd : fdUsadas) {
				if ((fd.getUnidadeMedidaMedicas() == null) || (fd.getUnidadeMedidaMedicas().getSeq() == null)) {// caso especial da apresentacao
					result = true;
					break;
				}
			}			
		}
		
		return result;		
	}

	public List<MpmUnidadeMedidaMedica> getListaUnidadeMedidaMedica() {
		return this.getPrescricaoMedicaFacade().getListaUnidadeMedidaMedica();			
	}
	
	public String getDescricaoUMM() {
		
		String result = null;
		MpmUnidadeMedidaMedica umm = null;
		
		if (getEntidade() != null) {
			umm = getEntidade().getUnidadeMedidaMedicas();
			if ((umm == null) && (this.getMedicamento().getTipoApresentacaoMedicamento() != null)) {
				result = this.getMedicamento().getTipoApresentacaoMedicamento().getDescricao();
			} else if (umm != null) {
				result = umm.getDescricao();
			}
		}
		
		return result;
	}
}
