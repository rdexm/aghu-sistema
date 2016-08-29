package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.model.AfaGrupoUsoMedicamento;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class GrupoUsoMedicamentoController extends ActionController {

	private static final long serialVersionUID = 7030989870521176809L;
	
	private final String REDIRECIONA_PESQUISAR_GRUPO_USO_MDTO = "grupoUsoMedicamentoList";

	@Inject
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	private AfaGrupoUsoMedicamento grupoUsoMedicamento;
	
	@PostConstruct
	public void init(){
		if(grupoUsoMedicamento == null){
			grupoUsoMedicamento = new AfaGrupoUsoMedicamento();
		}
	}
	
	public String confirmar() {
		try {
			boolean inclusaoNovo = grupoUsoMedicamento.getSeq() == null ;
			this.farmaciaApoioFacade.inserirAtualizar(this.grupoUsoMedicamento);
			if (inclusaoNovo) {
				// Inclusão
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_GRUPO_USO_MEDICAMENTOS",
						this.grupoUsoMedicamento.getDescricao());
			} else {
				// Alteração
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_GRUPO_USO_MEDICAMENTOS",
						this.grupoUsoMedicamento.getDescricao());
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}

		return REDIRECIONA_PESQUISAR_GRUPO_USO_MDTO;
	}
	
	public String cancelar() {
		return REDIRECIONA_PESQUISAR_GRUPO_USO_MDTO;
	}
	
	public AfaGrupoUsoMedicamento getGrupoUsoMedicamento() {
		return grupoUsoMedicamento;
	}

	public void setGrupoUsoMedicamento(AfaGrupoUsoMedicamento grupoUsoMedicamento) {
		this.grupoUsoMedicamento = grupoUsoMedicamento;
	}
}