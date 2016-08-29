package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.Severity;

public class AbaConcentO2Validator extends ISECamposObrigatoriosValidator {

	private static final Log LOG = LogFactory.getLog(AbaConcentO2Validator.class);

	public AbaConcentO2Validator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages) {
		super(itemSolicitacaoExameVO, messages);
	}

	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		boolean validou = true;
		if (getItemSolicitacaoExameVO().getFormaRespiracao() == null) {
			statusMessagesAddToControlFromResourceBundle(getFormaRespiracaoId(), Severity.ERROR, "CAMPO_OBRIGATORIO",
					getMessagesBundle("LABEL_CONCENTRACAO_OXIGENIO"));
			validou = false;
		}
		if (!getItemSolicitacaoExameVO().getReadOnlyLitroOxigenios() && getItemSolicitacaoExameVO().getLitrosOxigenio() == null) {
			statusMessagesAddToControlFromResourceBundle(getLitrosOxigenioObrigId(), Severity.ERROR, "CAMPO_OBRIGATORIO",
					getMessagesBundle("LABEL_LITRO_OXIGENIO"));
			validou = false;
		}
		return validou;
	}

	boolean validatePercOxigenios(int intervaloInicioPercOxigenios, int intervaloFimPercOxigenios) {
		boolean validou = true;
		if (!getItemSolicitacaoExameVO().getReadOnlyPercOxigenios()) {
			if (getItemSolicitacaoExameVO().getPercOxigenio() == null) {
				statusMessagesAddToControlFromResourceBundle(getPercOxigenioObrigId(), Severity.ERROR, "CAMPO_OBRIGATORIO",
						getMessagesBundle("LABEL_PERC_OXIGENIO"));
				validou = false;
			} else {
				if (!CoreUtil.isBetweenRange(getItemSolicitacaoExameVO().getPercOxigenio(), intervaloInicioPercOxigenios, intervaloFimPercOxigenios)) {
					this.statusMessagesAddToControlFromResourceBundle(getPercOxigenioObrigId(), Severity.ERROR, "MESSAGE_INTERVALO",
							intervaloInicioPercOxigenios, intervaloFimPercOxigenios);
					validou = false;
				}
			}
		}
		return validou;
	}

	protected String getFormaRespiracaoId() {
		return "formaRespiracao";
	}

	protected String getLitrosOxigenioObrigId() {
		return "litrosOxigenioObrig";
	}

	protected String getPercOxigenioObrigId() {
		return "percOxigenioObrig";
	}
}
