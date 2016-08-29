package br.gov.mec.aghu.exames.solicitacao.sismama.action;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaSimNaoNaoSabe;
import br.gov.mec.aghu.exames.solicitacao.action.ListarExamesSendoSolicitadosController;
import br.gov.mec.aghu.exames.solicitacao.action.ListarExamesSendoSolicitadosLoteController;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;


/**
 * Controller do slider de ANAMNESE
 *
 * 
 */

public class QuestionarioSismamaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(QuestionarioSismamaController.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = -7273629217906415829L;
	
	private Boolean desabilitaAnoCirurgia = false;
	private Boolean desabilitaNaoCaroco = false;
	private Boolean desabilitaSimCaroco = false;
	private Boolean desabilitaCampoQuando = true;
	private Boolean desabilitaChecksRadioterapia = true;
	private Boolean desabilitaAnoDireita = true;
	private Boolean desabilitaAnoEsquerda = true;
	private Integer currentSliderSismama = 0;

	@Inject
	private ListarExamesSendoSolicitadosController listarExamesSendoSolicitadosController;
	
	@Inject @SelectionQualifier
	private ListarExamesSendoSolicitadosLoteController listarExamesSendoSolicitadosLoteController;
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;

	
	@PostConstruct
	protected void inicializar() {
		LOG.info("QuestionarioSismamaController.inicializar...");
		this.begin(conversation);
	}

	
	public void limpar() {
		desabilitaAnoCirurgia = false;
		desabilitaNaoCaroco = false;
		desabilitaSimCaroco = false;
		desabilitaCampoQuando = true;
		desabilitaChecksRadioterapia = true;
		desabilitaAnoDireita = true;
		desabilitaAnoEsquerda = true;
		currentSliderSismama = 0;
	}
	
	public void desabilitarCamposCirurgia() {
		if(Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_NAOFEZCIRUR.name()))) {
			this.desabilitaAnoCirurgia = true;
			limparCamposAnoCirurgia();
		} else {
			this.desabilitaAnoCirurgia = false;
		}
	}
	
	private void limparCamposAnoCirurgia() {
		//Direita
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_D.name(),null);
		
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_D.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_D.name(),null);
		
		//Esquerda
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_BIOPSIA_LI_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DUTECT_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESVAZIA_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_MASTEC_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_M_PELE_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_IMP_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_PLAST_RED_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_RECON_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_SEGMEN_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_TUMOR_E.name(),null);
		
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_INC_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_BIOPSIA_CIR_EXC_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_CENTRALECTOMIA_E.name(),null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MASTEC_POUP_PE_E.name(),null);
	}
	
	public void desabilitarCamposComCaroco() {
		if(Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANM_NOD.name()))) {
			this.desabilitaSimCaroco = true;
		} else {
			this.desabilitaSimCaroco = false;
		}
	}
	
	public void desabilitarCampoSemCaroco() {
		if(Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANM_NOD_MD.name())) ||
				Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANM_NOD_ME.name()))) {
			this.desabilitaNaoCaroco = true;
		} else {
			this.desabilitaNaoCaroco = false;
		}
	}
	
	public void desabilitarCampoQuando() {
		if(DominioSismamaSimNaoNaoSabe.SIM.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANM_MAMOGRAF.name()))) {
			this.desabilitaCampoQuando = false;
		} else {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANM_MAMO_ANO.name(),null);
			this.desabilitaCampoQuando = true;
		}
	}
	
	public void limparCamposNaoLembraNuncaMenstruou() {
		if(getQuestionario().get(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name()) != null) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MEST_NLEMB.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_NUNCAMEST.name(),null);
		}
	}
	
	public void limparCamposDataMenstruacaoNuncaMenstruou() {
		if(Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_MEST_NLEMB.name()))) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_NUNCAMEST.name(),null);
		}
	}
	
	public void limparCamposNuncaMenstruouNaoLembraMenopausa() {
		if(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.name()) != null) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MENOP_NLEMB.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_NUNCAMEST.name(),null);
		}
	}
	
	public void limparCamposMenopausaNuncaMenstruou() {
		if(Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_MENOP_NLEMB.name()))) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_NUNCAMEST.name(),null);
		}
	}
	
	public void limparHistoriaMenstrual() {
		if(Boolean.TRUE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_NUNCAMEST.name()))) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.D_ANA_MESTRUAC.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MEST_NLEMB.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MENOP_IDADE.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_MENOP_NLEMB.name(),null);
		}
	}
	
	public void desabilitarCamposRadioterapia() {
		if(DominioSismamaSimNaoNaoSabe.SIM.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_RADIO.name()))) {
			this.desabilitaChecksRadioterapia = false;
		} else {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MDIR.name(), false);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MESQ.name(), false);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DIREITA.name(),null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESQUERDA.name(),null);
			this.desabilitaChecksRadioterapia = true;
			this.desabilitaAnoDireita = true;
			this.desabilitaAnoEsquerda = true;
		}
	}
	
	public void desabilitarCampoAnoRadioterapiaDireita() {
		if(Boolean.FALSE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MDIR.name()))) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_DIREITA.name(),null);
			this.desabilitaAnoDireita = true;
		} else {
			this.desabilitaAnoDireita = false;
		}
	}
	
	public void desabilitarCampoAnoRadioterapiaEsquerda() {
		if(Boolean.FALSE.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_ANA_RADIO_MESQ.name()))) {
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_ANA_ANO_ESQUERDA.name(),null);
			this.desabilitaAnoEsquerda = true;
		} else {
			this.desabilitaAnoEsquerda = false;
		}
	}
	
	


	
	public Map<String, Object> getQuestionario() {
		if(solicitacaoExameController.getRenderPorExame()){
			return listarExamesSendoSolicitadosController.getQuestionarioSismama();		
		}else if(solicitacaoExameController.getRenderPorLote()){
			return listarExamesSendoSolicitadosLoteController.getQuestionarioSismama();
		}
		return null;
	}

	public void setQuestionario(Map<String, Object> questionario) {
		if(solicitacaoExameController.getRenderPorExame()){
			listarExamesSendoSolicitadosController.setQuestionarioSismama(questionario);		
		}else if(solicitacaoExameController.getRenderPorLote()){
			listarExamesSendoSolicitadosLoteController.setQuestionarioSismama(questionario);
		}
	}

	public Boolean getDesabilitaAnoCirurgia() {
		return desabilitaAnoCirurgia;
	}

	public void setDesabilitaAnoCirurgia(Boolean desabilitaAnoCirurgia) {
		this.desabilitaAnoCirurgia = desabilitaAnoCirurgia;
	}

	public Boolean getDesabilitaNaoCaroco() {
		return desabilitaNaoCaroco;
	}

	public void setDesabilitaNaoCaroco(Boolean desabilitaNaoCaroco) {
		this.desabilitaNaoCaroco = desabilitaNaoCaroco;
	}

	public Boolean getDesabilitaSimCaroco() {
		return desabilitaSimCaroco;
	}

	public void setDesabilitaSimCaroco(Boolean desabilitaSimCaroco) {
		this.desabilitaSimCaroco = desabilitaSimCaroco;
	}

	public Boolean getDesabilitaCampoQuando() {
		return desabilitaCampoQuando;
	}

	public void setDesabilitaCampoQuando(Boolean desabilitaCampoQuando) {
		this.desabilitaCampoQuando = desabilitaCampoQuando;
	}

	public Boolean getDesabilitaChecksRadioterapia() {
		return desabilitaChecksRadioterapia;
	}

	public void setDesabilitaChecksRadioterapia(Boolean desabilitaChecksRadioterapia) {
		this.desabilitaChecksRadioterapia = desabilitaChecksRadioterapia;
	}

	public Boolean getDesabilitaAnoDireita() {
		return desabilitaAnoDireita;
	}

	public void setDesabilitaAnoDireita(Boolean desabilitaAnoDireita) {
		this.desabilitaAnoDireita = desabilitaAnoDireita;
	}

	public Boolean getDesabilitaAnoEsquerda() {
		return desabilitaAnoEsquerda;
	}

	public void setDesabilitaAnoEsquerda(Boolean desabilitaAnoEsquerda) {
		this.desabilitaAnoEsquerda = desabilitaAnoEsquerda;
	}

	public Integer getCurrentSliderSismama() {
		return currentSliderSismama;
	}

	public void setCurrentSliderSismama(Integer currentSliderSismama) {
		this.currentSliderSismama = currentSliderSismama;
	}
}
