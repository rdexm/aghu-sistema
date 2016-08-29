package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.Severity;

public class AbaRegMatAnaliseValidator extends ISECamposObrigatoriosValidator {
	

	private static final Log LOG = LogFactory.getLog(AbaRegMatAnaliseValidator.class);

	public AbaRegMatAnaliseValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}
	
	
	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		
		boolean validou = true;
		
		if(getItemSolicitacaoExameVO().getRegiaoAnatomica() == null
				&& !getItemSolicitacaoExameVO().getIsCadastroRegiaoAnatomica()
				&& getItemSolicitacaoExameVO().getIsExigeRegiaoAnatomica()) {
			statusMessagesAddToControlFromResourceBundle(getSbRegiaoAnatomicaId(), Severity.ERROR, "CAMPO_OBRIGATORIO_MATERIAL_REGIAO", getMessagesBundle("LABEL_REGIAO"));
			validou = false;
		}
		
		if(StringUtils.isEmpty(getItemSolicitacaoExameVO().getDescRegiaoAnatomica()) 
				&& getItemSolicitacaoExameVO().getIsCadastroRegiaoAnatomica()
				&& getItemSolicitacaoExameVO().getIsExigeRegiaoAnatomica()) {
			statusMessagesAddToControlFromResourceBundle(getDescRegiaoAnatomicaId(), Severity.ERROR, "CAMPO_OBRIGATORIO_MATERIAL_REGIAO", getMessagesBundle("LABEL_REGIAO"));
			validou = false;
		}
		
		if(StringUtils.isEmpty(getItemSolicitacaoExameVO().getDescMaterialAnalise()) 
				&& getItemSolicitacaoExameVO().getIsExigeDescMatAnls()) {
			statusMessagesAddToControlFromResourceBundle(getDescMaterialAnaliseId(), Severity.ERROR, "CAMPO_OBRIGATORIO_MATERIAL_REGIAO", getMessagesBundle("LABEL_DESC_MATERIAL_ANALISE"));
			validou = false;
		}
		
		return validou;
	}
	
	protected String getSbRegiaoAnatomicaId() {
		return "sbRegiaoAnatomica";
	}
	protected String getDescRegiaoAnatomicaId() {
		return "descRegiaoAnatomica";
	}
	protected String getDescMaterialAnaliseId() {
		return "descMaterialAnalise";
	}

}
