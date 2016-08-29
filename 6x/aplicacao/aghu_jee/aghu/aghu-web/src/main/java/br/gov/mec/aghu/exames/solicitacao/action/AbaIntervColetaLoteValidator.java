package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class AbaIntervColetaLoteValidator extends AbaIntervColetaValidator {
	

	//private static final Log LOG = LogFactory.getLog(AbaIntervColetaLoteValidator.class);

	public AbaIntervColetaLoteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}

	protected String getSbConvenioId() {
		return "sbConvenioLote";
	}
}
