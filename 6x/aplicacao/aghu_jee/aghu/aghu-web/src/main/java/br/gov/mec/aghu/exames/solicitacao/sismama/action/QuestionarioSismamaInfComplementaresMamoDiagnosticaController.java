package br.gov.mec.aghu.exames.solicitacao.sismama.action;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamaExaminada;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.core.action.ActionController;


public class QuestionarioSismamaInfComplementaresMamoDiagnosticaController
		extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(QuestionarioSismamaInfComplementaresMamoDiagnosticaController.class);

	private static final long serialVersionUID = 5476000705954222130L;

	@Inject
	private QuestionarioSismamaInfComplementaresController questionarioSismamaInfComplementaresController;

	// Revisao de mamografia com lesao realizada
	protected Boolean cTmpLesaoCatZeroD = false;
	protected Boolean cTmpLesaoCatTresD = false;
	protected Boolean cTmpLesaoCatQuatroD = false;
	protected Boolean cTmpLesaoCatCincoD = false;

	protected Boolean cTmpLesaoCatZeroE = false;
	protected Boolean cTmpLesaoCatTresE = false;
	protected Boolean cTmpLesaoCatQuatroE = false;
	protected Boolean cTmpLesaoCatCincoE = false;

	// Controle lesao apos biopsia de fragmento ou PAAF
	protected Boolean cTmpFrgPaafNodD = false;
	protected Boolean cTmpFrgPaffMicroD = false;
	protected Boolean cTmpFrgPaffAsfocaD = false;
	protected Boolean cTmpFrgPaffAsdifuD = false;
	protected Boolean cTmpFrgPaffAdensaD = false;
	protected Boolean cTmpFrgPaffDisfocaD = false;
	protected Boolean cTmpFrgPaffLinaxiD = false;

	protected Boolean cTmpFrgPaffNodE = false;
	protected Boolean cTmpFrgPaffMicroE = false;
	protected Boolean cTmpFrgPaffAsfocaE = false;
	protected Boolean cTmpFrgPaffAsdifuE = false;
	protected Boolean cTmpFrgPaffAdensaE = false;
	protected Boolean cTmpFrgPaffDisfocaE = false;
	protected Boolean cTmpFrgPaffLinaxE = false;

	// Avaliacao Resposta QT Neoadjuvante
	protected Boolean cTmpQtNeoMamaD = false;
	protected Boolean cTmpQtNeoMamaE = false;

	
	
	public static Log getLog() {
		return LOG;
	}

	/**
	 * Habilita os campos da area de informações de Lesao Apos Biopsia de
	 * fragmento ou PAAF
	 * 
	 * @author rhrosa
	 * @param habilitar
	 */
	protected void chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(
			Boolean habilitar) {

		if (habilitar) {
			
			
			if (DominioSismamaMamaExaminada.DIREITA.equals(questionarioSismamaInfComplementaresController.getQuestionario()
					.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()))
					|| DominioSismamaMamaExaminada.ESQUERDA
							.equals(questionarioSismamaInfComplementaresController.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_CLI_DIAG
											.name()))
					|| DominioSismamaMamaExaminada.AMBAS
							.equals(questionarioSismamaInfComplementaresController.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_CLI_DIAG
											.name()))) {
				this.questionarioSismamaInfComplementaresController
				.setHabilitaCLesaoBiopsiaPaaf(Boolean.TRUE);
			}

			if (questionarioSismamaInfComplementaresController
					.obtemValorDefaultBoolean(questionarioSismamaInfComplementaresController
							.getQuestionario()
							.get(DominioSismamaMamoCadCodigo.C_LESAO_BIOPSIA_PAAF
									.name()))) {
				habilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.TRUE);
			} else {

				zerarCamposControleLesaoAposBiopsiaFragmentoPAAF();
				desHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF();
			}
		} else {

			this.questionarioSismamaInfComplementaresController
					.setHabilitaCLesaoBiopsiaPaaf(Boolean.FALSE); // habilitaCLesaoBiopsiaPaaf
																	// =
																	// Boolean.FALSE;
			questionarioSismamaInfComplementaresController
					.getQuestionario()
					.put(DominioSismamaMamoCadCodigo.C_LESAO_BIOPSIA_PAAF
							.name(),
							Boolean.FALSE);
			habilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
		}
	}

	protected void habilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(
			Boolean habilitar) {
		if ((DominioSismamaMamaExaminada.AMBAS
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.DIREITA
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {

			this.cTmpFrgPaafNodD = habilitar;
			this.cTmpFrgPaffMicroD = habilitar;
			this.cTmpFrgPaffAsfocaD = habilitar;
			this.cTmpFrgPaffAsdifuD = habilitar;
			this.cTmpFrgPaffAdensaD = habilitar;
			this.cTmpFrgPaffDisfocaD = habilitar;
			this.cTmpFrgPaffLinaxiD = habilitar;
		}

		if ((DominioSismamaMamaExaminada.AMBAS
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {

			this.cTmpFrgPaffNodE = habilitar;
			this.cTmpFrgPaffMicroE = habilitar;
			this.cTmpFrgPaffAsfocaE = habilitar;
			this.cTmpFrgPaffAsdifuE = habilitar;
			this.cTmpFrgPaffAdensaE = habilitar;
			this.cTmpFrgPaffDisfocaE = habilitar;
			this.cTmpFrgPaffLinaxE = habilitar;
		}
	}

	protected void desHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF() {

		this.cTmpFrgPaafNodD = Boolean.FALSE;
		this.cTmpFrgPaffMicroD = Boolean.FALSE;
		this.cTmpFrgPaffAsfocaD = Boolean.FALSE;
		this.cTmpFrgPaffAsdifuD = Boolean.FALSE;
		this.cTmpFrgPaffAdensaD = Boolean.FALSE;
		this.cTmpFrgPaffDisfocaD = Boolean.FALSE;
		this.cTmpFrgPaffLinaxiD = Boolean.FALSE;

		this.cTmpFrgPaffNodE = Boolean.FALSE;
		this.cTmpFrgPaffMicroE = Boolean.FALSE;
		this.cTmpFrgPaffAsfocaE = Boolean.FALSE;
		this.cTmpFrgPaffAsdifuE = Boolean.FALSE;
		this.cTmpFrgPaffAdensaE = Boolean.FALSE;
		this.cTmpFrgPaffDisfocaE = Boolean.FALSE;
		this.cTmpFrgPaffLinaxE = Boolean.FALSE;
	}

	/**
	 * Habilita os campos da area de informações de revisao de mamografia com
	 * lesao realizada
	 * 
	 * @author rhrosa
	 * @param habilitar
	 */
	protected void chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(
			Boolean habilitar) {

		if (habilitar) {
			
			
			if (DominioSismamaMamaExaminada.DIREITA.equals(questionarioSismamaInfComplementaresController.getQuestionario()
					.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()))
					|| DominioSismamaMamaExaminada.ESQUERDA
							.equals(questionarioSismamaInfComplementaresController.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_CLI_DIAG
											.name()))
					|| DominioSismamaMamaExaminada.AMBAS
							.equals(questionarioSismamaInfComplementaresController.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_CLI_DIAG
											.name()))) {
				this.questionarioSismamaInfComplementaresController
				.setHabilitaRevMamoRealizada(Boolean.TRUE);
			}
			
			if (questionarioSismamaInfComplementaresController
					.obtemValorDefaultBoolean(questionarioSismamaInfComplementaresController
							.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT
											.name()))) {

				habilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.TRUE);
			} else {

				zerarCamposRevisaoMamografiaLesaoRealizada();
				desHabilitarCamposRevisaoMamografiaLesaoRealizada();
			}
		} else {

			this.questionarioSismamaInfComplementaresController
					.setHabilitaRevMamoRealizada(Boolean.FALSE); // habilitaRevMamoRealizada
																	// =
																	// Boolean.FALSE;
			questionarioSismamaInfComplementaresController.getQuestionario()
					.put(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT.name(),
							Boolean.FALSE);
			habilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
			zerarCamposRevisaoMamografiaLesaoRealizada();
			desHabilitarCamposRevisaoMamografiaLesaoRealizada();
		}
	}

	protected void desHabilitarCamposRevisaoMamografiaLesaoRealizada() {

		this.cTmpLesaoCatZeroD = Boolean.FALSE;
		this.cTmpLesaoCatTresD = Boolean.FALSE;
		this.cTmpLesaoCatQuatroD = Boolean.FALSE;
		this.cTmpLesaoCatCincoD = Boolean.FALSE;

		this.cTmpLesaoCatZeroE = Boolean.FALSE;
		this.cTmpLesaoCatTresE = Boolean.FALSE;
		this.cTmpLesaoCatQuatroE = Boolean.FALSE;
		this.cTmpLesaoCatCincoE = Boolean.FALSE;
	}

	protected void habilitarCamposRevisaoMamografiaLesaoRealizada(
			Boolean habilitar) {

		if ((DominioSismamaMamaExaminada.AMBAS
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.DIREITA
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {

			this.cTmpLesaoCatZeroD = habilitar;
			this.cTmpLesaoCatTresD = habilitar;
			this.cTmpLesaoCatQuatroD = habilitar;
			this.cTmpLesaoCatCincoD = habilitar;
		}

		if ((DominioSismamaMamaExaminada.AMBAS
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {

			this.cTmpLesaoCatZeroE = habilitar;
			this.cTmpLesaoCatTresE = habilitar;
			this.cTmpLesaoCatQuatroE = habilitar;
			this.cTmpLesaoCatCincoE = habilitar;
		}
	}

	protected void chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean habilitar) {

		if (habilitar) {

			if (DominioSismamaMamaExaminada.DIREITA.equals(questionarioSismamaInfComplementaresController.getQuestionario()
					.get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()))
					|| DominioSismamaMamaExaminada.ESQUERDA
							.equals(questionarioSismamaInfComplementaresController.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_CLI_DIAG
											.name()))
					|| DominioSismamaMamaExaminada.AMBAS
							.equals(questionarioSismamaInfComplementaresController.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_CLI_DIAG
											.name()))) {
				this.questionarioSismamaInfComplementaresController
				.setHabilitaCAvaliacaoAdj(Boolean.TRUE);
			}
			
			if (questionarioSismamaInfComplementaresController
					.obtemValorDefaultBoolean(questionarioSismamaInfComplementaresController
							.getQuestionario().get(
									DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ
											.name()))) {

				habilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.TRUE);
			} else {

				zerarCamposAvaliacaoQTNeoAdjuvante();
				desHabilitarCamposAvaliacaoQTNeoAdjuvante();
			}
		} else {

			this.questionarioSismamaInfComplementaresController
					.setHabilitaCAvaliacaoAdj(Boolean.FALSE); // habilitaCAvaliacaoAdj
																// =
																// Boolean.FALSE;
			questionarioSismamaInfComplementaresController.getQuestionario()
					.put(DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ.name(),
							Boolean.FALSE);
			zerarCamposAvaliacaoQTNeoAdjuvante();
			desHabilitarCamposAvaliacaoQTNeoAdjuvante();
		}
	}

	protected void habilitarCamposAvaliacaoQTNeoAdjuvante(Boolean habilitar) {

		if ((DominioSismamaMamaExaminada.AMBAS
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.DIREITA
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {

			this.cTmpQtNeoMamaD = habilitar;
		}

		if ((DominioSismamaMamaExaminada.AMBAS
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA
				.equals(questionarioSismamaInfComplementaresController
						.getQuestionario().get(
								DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {

			this.cTmpQtNeoMamaE = habilitar;
		}
	}

	protected void desHabilitarCamposAvaliacaoQTNeoAdjuvante() {
		this.cTmpQtNeoMamaD = Boolean.FALSE;
		this.cTmpQtNeoMamaE = Boolean.FALSE;
	}

	protected void zerarCamposRevisaoMamografiaLesaoRealizada() {
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_ZERO_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_TRES_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_QUATRO_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_CINCO_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_ZERO_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_TRES_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_QUATRO_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT_CINCO_E.name(),
				Boolean.FALSE);
	}

	public void zerarCamposAvaliacaoQTNeoAdjuvante() {
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_QTNEO_MAMA_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_QTNEO_MAMA_E.name(),
				Boolean.FALSE);
	}

	protected void zerarCamposControleLesaoAposBiopsiaFragmentoPAAF() {
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_NOD_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_MICRO_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASFOCA_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASDIFU_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ADENSA_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_DISFOCA_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_LINAXI_D.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_NOD_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_MICRO_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASFOCA_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ASDIFU_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_ADENSA_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_DISFOCA_E.name(),
				Boolean.FALSE);
		questionarioSismamaInfComplementaresController.getQuestionario().put(
				DominioSismamaMamoCadCodigo.C_TMP_FRG_PAFF_LINAXI_E.name(),
				Boolean.FALSE);
	}

	public void limpar() {
		questionarioSismamaInfComplementaresController.habilitaCAvaliacaoAdj = false;
		// Revisao de mamografia com lesao realizada
		questionarioSismamaInfComplementaresController.habilitaRevMamoRealizada = false;
		cTmpLesaoCatZeroD = false;
		cTmpLesaoCatTresD = false;
		cTmpLesaoCatQuatroD = false;
		cTmpLesaoCatCincoD = false;

		cTmpLesaoCatZeroE = false;
		cTmpLesaoCatTresE = false;
		cTmpLesaoCatQuatroE = false;
		cTmpLesaoCatCincoE = false;

		// Controle lesao apos biopsia de fragmento ou PAAF
		questionarioSismamaInfComplementaresController.habilitaCLesaoBiopsiaPaaf = false;
		cTmpFrgPaafNodD = false;
		cTmpFrgPaffMicroD = false;
		cTmpFrgPaffAsfocaD = false;
		cTmpFrgPaffAsdifuD = false;
		cTmpFrgPaffAdensaD = false;
		cTmpFrgPaffDisfocaD = false;
		cTmpFrgPaffLinaxiD = false;

		cTmpFrgPaffNodE = false;
		cTmpFrgPaffMicroE = false;
		cTmpFrgPaffAsfocaE = false;
		cTmpFrgPaffAsdifuE = false;
		cTmpFrgPaffAdensaE = false;
		cTmpFrgPaffDisfocaE = false;
		cTmpFrgPaffLinaxE = false;

		// Avaliacao de resposta de QT neoadjuvante
		questionarioSismamaInfComplementaresController.habilitaQTNeoAdjuvanteDir = false;
		questionarioSismamaInfComplementaresController.habilitaQTNeoAdjuvanteEsq = false;
		cTmpQtNeoMamaD = false;
		cTmpQtNeoMamaE = false;
	}

	public Boolean getcTmpFrgPaafNodD() {
		return cTmpFrgPaafNodD;
	}

	public void setcTmpFrgPaafNodD(Boolean cTmpFrgPaafNodD) {
		this.cTmpFrgPaafNodD = cTmpFrgPaafNodD;
	}

	public Boolean getcTmpFrgPaffMicroD() {
		return cTmpFrgPaffMicroD;
	}

	public void setcTmpFrgPaffMicroD(Boolean cTmpFrgPaffMicroD) {
		this.cTmpFrgPaffMicroD = cTmpFrgPaffMicroD;
	}

	public Boolean getcTmpFrgPaffAsfocaD() {
		return cTmpFrgPaffAsfocaD;
	}

	public void setcTmpFrgPaffAsfocaD(Boolean cTmpFrgPaffAsfocaD) {
		this.cTmpFrgPaffAsfocaD = cTmpFrgPaffAsfocaD;
	}

	public Boolean getcTmpFrgPaffAsdifuD() {
		return cTmpFrgPaffAsdifuD;
	}

	public void setcTmpFrgPaffAsdifuD(Boolean cTmpFrgPaffAsdifuD) {
		this.cTmpFrgPaffAsdifuD = cTmpFrgPaffAsdifuD;
	}

	public Boolean getcTmpFrgPaffAdensaD() {
		return cTmpFrgPaffAdensaD;
	}

	public void setcTmpFrgPaffAdensaD(Boolean cTmpFrgPaffAdensaD) {
		this.cTmpFrgPaffAdensaD = cTmpFrgPaffAdensaD;
	}

	public Boolean getcTmpFrgPaffDisfocaD() {
		return cTmpFrgPaffDisfocaD;
	}

	public void setcTmpFrgPaffDisfocaD(Boolean cTmpFrgPaffDisfocaD) {
		this.cTmpFrgPaffDisfocaD = cTmpFrgPaffDisfocaD;
	}

	public Boolean getcTmpFrgPaffLinaxiD() {
		return cTmpFrgPaffLinaxiD;
	}

	public void setcTmpFrgPaffLinaxiD(Boolean cTmpFrgPaffLinaxiD) {
		this.cTmpFrgPaffLinaxiD = cTmpFrgPaffLinaxiD;
	}

	public Boolean getcTmpFrgPaffNodE() {
		return cTmpFrgPaffNodE;
	}

	public void setcTmpFrgPaffNodE(Boolean cTmpFrgPaffNodE) {
		this.cTmpFrgPaffNodE = cTmpFrgPaffNodE;
	}

	public Boolean getcTmpFrgPaffMicroE() {
		return cTmpFrgPaffMicroE;
	}

	public void setcTmpFrgPaffMicroE(Boolean cTmpFrgPaffMicroE) {
		this.cTmpFrgPaffMicroE = cTmpFrgPaffMicroE;
	}

	public Boolean getcTmpFrgPaffAsfocaE() {
		return cTmpFrgPaffAsfocaE;
	}

	public void setcTmpFrgPaffAsfocaE(Boolean cTmpFrgPaffAsfocaE) {
		this.cTmpFrgPaffAsfocaE = cTmpFrgPaffAsfocaE;
	}

	public Boolean getcTmpFrgPaffAsdifuE() {
		return cTmpFrgPaffAsdifuE;
	}

	public void setcTmpFrgPaffAsdifuE(Boolean cTmpFrgPaffAsdifuE) {
		this.cTmpFrgPaffAsdifuE = cTmpFrgPaffAsdifuE;
	}

	public Boolean getcTmpFrgPaffAdensaE() {
		return cTmpFrgPaffAdensaE;
	}

	public void setcTmpFrgPaffAdensaE(Boolean cTmpFrgPaffAdensaE) {
		this.cTmpFrgPaffAdensaE = cTmpFrgPaffAdensaE;
	}

	public Boolean getcTmpFrgPaffDisfocaE() {
		return cTmpFrgPaffDisfocaE;
	}

	public void setcTmpFrgPaffDisfocaE(Boolean cTmpFrgPaffDisfocaE) {
		this.cTmpFrgPaffDisfocaE = cTmpFrgPaffDisfocaE;
	}

	public Boolean getcTmpFrgPaffLinaxE() {
		return cTmpFrgPaffLinaxE;
	}

	public void setcTmpFrgPaffLinaxE(Boolean cTmpFrgPaffLinaxE) {
		this.cTmpFrgPaffLinaxE = cTmpFrgPaffLinaxE;
	}

	public Boolean getcTmpLesaoCatZeroD() {
		return cTmpLesaoCatZeroD;
	}

	public void setcTmpLesaoCatZeroD(Boolean cTmpLesaoCatZeroD) {
		this.cTmpLesaoCatZeroD = cTmpLesaoCatZeroD;
	}

	public Boolean getcTmpLesaoCatTresD() {
		return cTmpLesaoCatTresD;
	}

	public void setcTmpLesaoCatTresD(Boolean cTmpLesaoCatTresD) {
		this.cTmpLesaoCatTresD = cTmpLesaoCatTresD;
	}

	public Boolean getcTmpLesaoCatQuatroD() {
		return cTmpLesaoCatQuatroD;
	}

	public void setcTmpLesaoCatQuatroD(Boolean cTmpLesaoCatQuatroD) {
		this.cTmpLesaoCatQuatroD = cTmpLesaoCatQuatroD;
	}

	public Boolean getcTmpLesaoCatCincoD() {
		return cTmpLesaoCatCincoD;
	}

	public void setcTmpLesaoCatCincoD(Boolean cTmpLesaoCatCincoD) {
		this.cTmpLesaoCatCincoD = cTmpLesaoCatCincoD;
	}

	public Boolean getcTmpLesaoCatZeroE() {
		return cTmpLesaoCatZeroE;
	}

	public void setcTmpLesaoCatZeroE(Boolean cTmpLesaoCatZeroE) {
		this.cTmpLesaoCatZeroE = cTmpLesaoCatZeroE;
	}

	public Boolean getcTmpLesaoCatTresE() {
		return cTmpLesaoCatTresE;
	}

	public void setcTmpLesaoCatTresE(Boolean cTmpLesaoCatTresE) {
		this.cTmpLesaoCatTresE = cTmpLesaoCatTresE;
	}

	public Boolean getcTmpLesaoCatQuatroE() {
		return cTmpLesaoCatQuatroE;
	}

	public void setcTmpLesaoCatQuatroE(Boolean cTmpLesaoCatQuatroE) {
		this.cTmpLesaoCatQuatroE = cTmpLesaoCatQuatroE;
	}

	public Boolean getcTmpLesaoCatCincoE() {
		return cTmpLesaoCatCincoE;
	}

	public void setcTmpLesaoCatCincoE(Boolean cTmpLesaoCatCincoE) {
		this.cTmpLesaoCatCincoE = cTmpLesaoCatCincoE;
	}

	public Boolean getcTmpQtNeoMamaD() {
		return cTmpQtNeoMamaD;
	}

	public void setcTmpQtNeoMamaD(Boolean cTmpQtNeoMamaD) {
		this.cTmpQtNeoMamaD = cTmpQtNeoMamaD;
	}

	public Boolean getcTmpQtNeoMamaE() {
		return cTmpQtNeoMamaE;
	}

	public void setcTmpQtNeoMamaE(Boolean cTmpQtNeoMamaE) {
		this.cTmpQtNeoMamaE = cTmpQtNeoMamaE;
	}

}
