package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class ItemSolicitacaoExameLoteValidator extends ItemSolicitacaoExameValidator {
	
	
	//private static final Log LOG = LogFactory.getLog(ItemSolicitacaoExameLoteValidator.class);

	public ItemSolicitacaoExameLoteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}

	protected String getSbExamesId() {
		return "comboExamesLote";
	}
	protected String getComboDataProgramadaId() {
		return "comboDataProgramadaLote";
	}
	protected String getDataProgramadaId() {
		return "dataProgramadaLote";
	}
	protected String getComboSituacaoItemId() {
		return "comboSituacaoItemLote";
	}
}
