package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class AbaRegMatAnaliseLoteValidator extends AbaRegMatAnaliseValidator {
	
	
	//private static final Log LOG = LogFactory.getLog(AbaRegMatAnaliseLoteValidator.class);

	public AbaRegMatAnaliseLoteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	protected String getSbRegiaoAnatomicaId() {
		return "sbRegiaoAnatomicaLote";
	}
	protected String getDescRegiaoAnatomicaId() {
		return "descRegiaoAnatomicaLote";
	}
	protected String getDescMaterialAnaliseId() {
		return "descMaterialAnaliseLote";
	}

}
