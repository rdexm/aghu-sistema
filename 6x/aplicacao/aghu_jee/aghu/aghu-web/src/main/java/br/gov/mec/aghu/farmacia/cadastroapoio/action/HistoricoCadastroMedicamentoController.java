package br.gov.mec.aghu.farmacia.cadastroapoio.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.vo.HistoricoCadastroMedicamentoVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Controller da tela de histórico de cadastro de medicamento
 * 
 * 
 */

public class HistoricoCadastroMedicamentoController  extends ActionController {
	
	private static final long serialVersionUID = 7333360694227028403L;

	@Inject
	private IFarmaciaFacade farmaciaFacade;

	private Integer seqJn;	

	private HistoricoCadastroMedicamentoVO historicoMedicamento;
	
	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void pesquisaHistoricoMedicamento(){
		try {
			historicoMedicamento = farmaciaFacade.obterHistoricoCadastroMedicamento(seqJn);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	// GETS E SETS

	public Integer getSeqJn() {
		return seqJn;
	}

	public void setSeqJn(Integer seqJn) {
		this.seqJn = seqJn;
	}

	public HistoricoCadastroMedicamentoVO getHistoricoMedicamento() {
		return historicoMedicamento;
	}

	public void setHistoricoMedicamento(HistoricoCadastroMedicamentoVO historicoMedicamento) {
		this.historicoMedicamento = historicoMedicamento;
	}
	
}