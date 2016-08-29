package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaTipoApresentacaoMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class TipoApresentacaoMedicamentoController extends ActionController {

	private static final long serialVersionUID = 8237961558179814018L;
	
	private final String REDIRECIONA_PESQUISAR_TIPO_APRES_MDTO = "tipoApresentacaoMedicamentoList";

	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;

	private AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento;
	
	private Boolean novoRegistro;

	@PostConstruct
	public void init(){

		if(tipoApresentacaoMedicamento == null){
			tipoApresentacaoMedicamento = new AfaTipoApresentacaoMedicamento();
		}
	}

	public String cancelar() {
		return REDIRECIONA_PESQUISAR_TIPO_APRES_MDTO;
	}

	public String confirmar() {
		try {
			this.farmaciaApoioFacade.inserirAtualizarTipoApresentacaoMedicamento(this.tipoApresentacaoMedicamento, novoRegistro);

			if(novoRegistro) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_TIPO_APRESENTACAO_MEDICAMENTOS",
						this.tipoApresentacaoMedicamento.getDescricao());
			} else {
				// Alteração
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_TIPO_APRESENTACAO_MEDICAMENTOS",
						this.tipoApresentacaoMedicamento.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_TIPO_APRES_MDTO;
	}

	// GETS E SETS
	public AfaTipoApresentacaoMedicamento getTipoApresentacaoMedicamento() {
		return tipoApresentacaoMedicamento;
	}

	public void setTipoApresentacaoMedicamento(
			AfaTipoApresentacaoMedicamento tipoApresentacaoMedicamento) {
		this.tipoApresentacaoMedicamento = tipoApresentacaoMedicamento;
	}

	public Boolean getNovoRegistro() {
		return novoRegistro;
	}

	public void setNovoRegistro(Boolean novoRegistro) {
		this.novoRegistro = novoRegistro;
	}
}