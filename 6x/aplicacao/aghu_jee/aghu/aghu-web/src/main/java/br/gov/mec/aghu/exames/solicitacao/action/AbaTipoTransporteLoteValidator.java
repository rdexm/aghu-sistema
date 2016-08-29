package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class AbaTipoTransporteLoteValidator extends AbaTipoTransporteValidator {
	
	//private static final Log LOG = LogFactory.getLog(AbaTipoTransporteLoteValidator.class);

	public AbaTipoTransporteLoteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	protected String getTipoTransporteId() {
		return "tipoTransporteLote";
	}
	protected String getUsoO2Id() {
		return "usoO2Lote";
	}

}
