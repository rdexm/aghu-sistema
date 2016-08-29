package br.gov.mec.aghu.exames.solicitacao.action;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.exames.solicitacao.vo.ItemSolicitacaoExameVO;
import br.gov.mec.aghu.core.exception.Severity;

public class AbaQuestionarioSismamaValidator extends ISECamposObrigatoriosValidator {

	

	private static final Log LOG = LogFactory.getLog(AbaQuestionarioSismamaValidator.class);
	
	public AbaQuestionarioSismamaValidator(ItemSolicitacaoExameVO itemSolicitacaoExameVO, ResourceBundle messages, String tabIndex) {
		super(itemSolicitacaoExameVO, messages);
		this.tabIndex = tabIndex;
	}
	
	private boolean validou = true;
	private String tabIndex;
	
	public void validarAno(Integer ano, String campo) {
		if(ano != null) {
			Integer anoAtual = Calendar.getInstance().get(Calendar.YEAR);
			if(ano > anoAtual) {
				statusMessagesAddToControlFromResourceBundle(campo + this.tabIndex, Severity.ERROR, "AEL_03342");
				validou = false;
			} else if(ano < anoAtual - 100) {
				statusMessagesAddToControlFromResourceBundle(campo + this.tabIndex, Severity.ERROR, "AEL_03343");
				validou = false;
			}
		}
	}

	@Override
	boolean validate() {
		LOG.info("Class: " + getClass().getSimpleName());
		
		Map<String, Object> map = getItemSolicitacaoExameVO().getQuestionarioSismama();
		
		List<String> listaCamposValidaAno = new ArrayList<String>();
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANM_MAMO_ANO.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_DIREITA.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESQUERDA.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_E.name());
		
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_E.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_D.name());
		listaCamposValidaAno.add(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_E.name());
		
		for(String campo : listaCamposValidaAno) {
			if(map.get(campo) != null) {
				validarAno(Integer.valueOf(map.get(campo).toString()), campo);
			}
		}
		
		if(DominioSismamaSimNaoNaoSabe.SIM.equals(map.get(DominioSismamaMamoCadCodigo.C_ANM_MAMOGRAF.name())) &&
				map.get(DominioSismamaMamoCadCodigo.C_ANM_MAMO_ANO.name()) == null) {
			statusMessagesAddToControlFromResourceBundle(DominioSismamaMamoCadCodigo.C_ANM_MAMO_ANO.name() + this.tabIndex,
					Severity.ERROR, "ERRO_QUESTIONARIO_SISMAMA_QUANDO_OBRIGATORIO");
			validou = false;
		}
		
		return validou;
	}

}
