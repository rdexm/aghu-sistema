package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.Date;
import java.util.ResourceBundle;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class AbaNoAmostrasLoteValidator extends AbaNoAmostrasValidator {

	

	//private static final Log LOG = LogFactory.getLog(AbaNoAmostrasLoteValidator.class);

	public AbaNoAmostrasLoteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	protected String getNumeroAmostraId() {
		return "numeroAmostraLote";
	}

	protected String getIntervaloHoraObrigId() {
		return "intervaloHoraObrigLote";
	}

	protected String getIntervaloDiasObrigId() {
		return "intervaloDiasObrigLote";
	}

	protected Integer getNumeroAmostraValue() {
		return getItemSolicitacaoExameVO().getUnfExecutaExame().getNumeroAmostra();
	}

	protected Date getIntervaloHorasValue() {
		return getItemSolicitacaoExameVO().getUnfExecutaExame().getIntervaloHoras();
	}

	protected Integer getIntervaloDiasValue() {
		return getItemSolicitacaoExameVO().getUnfExecutaExame().getIntervaloDias();
	}
}
