package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;

public class AbaConcentO2LoteValidator extends AbaConcentO2Validator {
	
	//private static final Log LOG = LogFactory.getLog(AbaConcentO2LoteValidator.class);

	public AbaConcentO2LoteValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO,
			ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	protected String getFormaRespiracaoId() {
		return "formaRespiracaoLote";
	}
	protected String getLitrosOxigenioObrigId() {
		return "litrosOxigenioObrigLote";	
	}
	protected String getPercOxigenioObrigId() {
		return "percOxigenioObrigLote";
	}
}
