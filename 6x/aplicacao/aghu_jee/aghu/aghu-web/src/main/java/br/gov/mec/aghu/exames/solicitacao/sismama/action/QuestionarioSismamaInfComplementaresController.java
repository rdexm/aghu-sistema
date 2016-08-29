package br.gov.mec.aghu.exames.solicitacao.sismama.action;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSismamaMamaExaminada;
import br.gov.mec.aghu.dominio.DominioSismamaMamoCadCodigo;
import br.gov.mec.aghu.dominio.DominioSismamaTipoMamografia;
import br.gov.mec.aghu.exames.solicitacao.action.ListarExamesSendoSolicitadosController;
import br.gov.mec.aghu.exames.solicitacao.action.ListarExamesSendoSolicitadosLoteController;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;


/**
 * Controller do slider de ANAMNESE e INFORMACOES COMPLEMENTARES
 *
 * 
 */
@SuppressWarnings({ "PMD.ExcessiveClassLength"})
public class QuestionarioSismamaInfComplementaresController extends ActionController {
	
	private static final Log LOG = LogFactory.getLog(QuestionarioSismamaInfComplementaresController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -538660858537321981L;
	
	@Inject
	private ListarExamesSendoSolicitadosController listarExamesSendoSolicitadosController;
	
	@Inject @SelectionQualifier
	private ListarExamesSendoSolicitadosLoteController listarExamesSendoSolicitadosLoteController;
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
  
  	@Inject
  	private QuestionarioSismamaInfComplementaresMamoDiagnosticaController questionarioSismamaInfComplementaresMamoDiagnosticaController;
  	
  	@Inject
  	private QuestionarioSismamaInfComplementaresMamoRastreamentoController questionarioSismamaInfComplementaresMamoRastreamentoController;
  	
  	
	
	protected Boolean habilitaCMamoDiag = true;
	protected Boolean habilitaCMamoRastr = true;
	protected Boolean habilitaCCliDiag = false;
	protected Boolean habilitaCAvaliacaoAdj = false;
	protected Boolean habilitaCCliLesPalpa = false;
	protected Boolean habilitaCControleRadiol = false;
	protected Boolean habilitaCLesaoDiag = false;
	
	
	protected Boolean habilitaCRadNoduloDir = false;
	protected Boolean habilitaCRadMicrocalDir = false;
	protected Boolean habilitaCRadAssimFocDir = false;
	protected Boolean habilitaCRadAssimDifDir = false;
	protected Boolean habilitaCRadAreaDensDir = false;
	protected Boolean habilitaCRadDistArqDir = false;
	
	protected Boolean habilitaCRadNoduloEsq = false;
	protected Boolean habilitaCRadMicrocalEsq = false;
	protected Boolean habilitaCRadAssimFocEsq = false;
	protected Boolean habilitaCRadAssimDifEsq = false;
	protected Boolean habilitaCRadAreaDensEsq = false;
	protected Boolean habilitaCRadDistArqEsq = false;
	
	protected Boolean habilitaCDiagNoduloDir = false;
	protected Boolean habilitaCDiagMicrocalDir = false;
	protected Boolean habilitaCDiagAssimFocDir = false;
	protected Boolean habilitaCDiagAssimDifDir = false;
	protected Boolean habilitaCDiagAreaDensDir = false;
	protected Boolean habilitaCDiagDistArqDir = false;
	
	protected Boolean habilitaCDiagNoduloEsq = false;
	protected Boolean habilitaCDiagMicrocalEsq = false;
	protected Boolean habilitaCDiagAssimFocEsq = false;
	protected Boolean habilitaCDiagAssimDifEsq = false;
	protected Boolean habilitaCDiagAreaDensEsq = false;
	protected Boolean habilitaCDiagDistArqEsq = false;
	
	
	protected Boolean habilitaCCliPapilEsq = false;
	protected Boolean habilitaCCliDescEsq = false;
	
	protected Boolean habilitaCCliNodQseEsq = false;
	protected Boolean habilitaCCliNodQieEsq = false;
	protected Boolean habilitaCCliNodQsiEsq = false;
	protected Boolean habilitaCCliNodQiiEsq = false;
	protected Boolean habilitaCCliNodUqextEsq = false;
	
	protected Boolean habilitaCCliNodUqsupEsq = false;
	protected Boolean habilitaCCliNodUqintEsq = false;
	protected Boolean habilitaCCliNodUqinfEsq = false;
	protected Boolean habilitaCCliNodRraEsq = false;
	protected Boolean habilitaCCliNodPaEsq = false;
	
	protected Boolean habilitaCCliEspQseEsq = false;
	protected Boolean habilitaCCliEspQieEsq = false;
	protected Boolean habilitaCCliEspQsiEsq = false;
	protected Boolean habilitaCCliEspQiiEsq = false;
	protected Boolean habilitaCCliEspUqextEsq = false;
	
	protected Boolean habilitaCCliEspUqsupEsq = false;
	protected Boolean habilitaCCliEspUqintEsq = false;
	protected Boolean habilitaCCliEspUqinfEsq = false;
	protected Boolean habilitaCCliEspRraEsq = false;
	protected Boolean habilitaCCliEspPaEsq = false;

	protected Boolean habilitaCCliLinfAxEsq = false;
	protected Boolean habilitaCCliLinfSupraEsq = false;
	
	protected Boolean habilitaCCliPapilDir = false;
	protected Boolean habilitaCCliDescDir = false;
	protected Boolean habilitaCCliNodQseDir = false;
	protected Boolean habilitaCCliNodQieDir = false;
	protected Boolean habilitaCCliNodQsiDir = false;
	protected Boolean habilitaCCliNodQiiDir = false;
	protected Boolean habilitaCCliNodUqextDir = false;
	protected Boolean habilitaCCliNodUqsupDir = false;
	protected Boolean habilitaCCliNodUqintDir = false;
	protected Boolean habilitaCCliNodUqinfDir = false;
	protected Boolean habilitaCCliNodRraDir = false;
	protected Boolean habilitaCCliNodPaDir = false;
	protected Boolean habilitaCCliEspQseDir = false;
	protected Boolean habilitaCCliEspQieDir = false;
	protected Boolean habilitaCCliEspQsiDir = false;
	protected Boolean habilitaCCliEspQiiDir = false;
	protected Boolean habilitaCCliEspUqextDir = false;
	protected Boolean habilitaCCliEspUqsupDir = false;
	protected Boolean habilitaCCliEspUqintDir = false;
	protected Boolean habilitaCCliEspUqinfDir = false;
	protected Boolean habilitaCCliEspRraDir = false;
	protected Boolean habilitaCCliEspPaDir = false;
	protected Boolean habilitaCCliLinfAxDir = false;
	protected Boolean habilitaCCliLinfSupraDir = false;
	
	protected Boolean habilitarCamposInfsMamoDiagnostica = false;
  	protected Boolean habilitarCamposInfsMamoRastreamento = false;
  	protected DominioSismamaTipoMamografia tipoMamografia = DominioSismamaTipoMamografia.C_MAMO_DIAG; 
  	protected Boolean habilitaQTNeoAdjuvanteDir = false;
  	protected Boolean habilitaQTNeoAdjuvanteEsq = false;
  	protected Boolean habilitaCLesaoBiopsiaPaaf = false;
  	protected Boolean habilitaRevMamoRealizada = false;
	

	
	@PostConstruct
	protected void inicializar() {
		LOG.info("QuestionarioSismamaInfComplementaresController.inicializar... ");
		this.begin(conversation);
	}

	
	
	
	public void limpar(){
		habilitaCMamoDiag = true;
		habilitaCMamoRastr = true;
		habilitaCCliDiag = false;
		habilitaCAvaliacaoAdj = false;
		habilitaCCliLesPalpa = false;
		habilitaCControleRadiol = false;
		habilitaCLesaoDiag = false;		
		habilitaCRadNoduloDir = false;
		habilitaCRadMicrocalDir = false;
		habilitaCRadAssimFocDir = false;
		habilitaCRadAssimDifDir = false;
		habilitaCRadAreaDensDir = false;
		
		habilitaCRadNoduloEsq = false;
		habilitaCRadMicrocalEsq = false;
		habilitaCRadAssimFocEsq = false;
		habilitaCRadAssimDifEsq = false;
		habilitaCRadAreaDensEsq = false;
		habilitaCRadDistArqEsq = false;		
		habilitaCDiagNoduloDir = false;
		habilitaCDiagMicrocalDir = false;
		habilitaCDiagAssimFocDir = false;
		habilitaCDiagAssimDifDir = false;
		habilitaCDiagAreaDensDir = false;
		habilitaCDiagDistArqDir = false;		
		habilitaCDiagNoduloEsq = false;
		habilitaCDiagMicrocalEsq = false;
		habilitaCDiagAssimFocEsq = false;
		habilitaCDiagAssimDifEsq = false;
		habilitaCDiagAreaDensEsq = false;
		habilitaCDiagDistArqEsq = false;		
		habilitaCCliPapilEsq = false;
		habilitaCCliDescEsq = false;		
		habilitaCCliNodQseEsq = false;
		habilitaCCliNodQieEsq = false;
		habilitaCCliNodQsiEsq = false;
		habilitaCCliNodQiiEsq = false;
		habilitaCCliNodUqextEsq = false;		
		habilitaCCliNodUqsupEsq = false;
		habilitaCCliNodUqintEsq = false;
		habilitaCCliNodUqinfEsq = false;
		habilitaCCliNodRraEsq = false;
		habilitaCCliNodPaEsq = false;		
		habilitaCCliEspQseEsq = false;
		habilitaCCliEspQieEsq = false;
		habilitaCCliEspQsiEsq = false;
		habilitaCCliEspQiiEsq = false;
		habilitaCCliEspUqextEsq = false;		
		habilitaCCliEspUqsupEsq = false;
		habilitaCCliEspUqintEsq = false;
		habilitaCCliEspUqinfEsq = false;
		habilitaCCliEspRraEsq = false;
		habilitaCCliEspPaEsq = false;
		habilitaCCliLinfAxEsq = false;
		habilitaCCliLinfSupraEsq = false;		
		habilitaCCliPapilDir = false;
		habilitaCCliDescDir = false;
		habilitaCCliNodQseDir = false;
		habilitaCCliNodQieDir = false;
		habilitaCCliNodQsiDir = false;
		habilitaCCliNodQiiDir = false;
		habilitaCCliNodUqextDir = false;
		habilitaCCliNodUqsupDir = false;
		habilitaCCliNodUqintDir = false;
		habilitaCCliNodUqinfDir = false;
		habilitaCCliNodRraDir = false;
		habilitaCCliNodPaDir = false;
		habilitaCCliEspQseDir = false;
		habilitaCCliEspQieDir = false;
		habilitaCCliEspQsiDir = false;
		habilitaCCliEspQiiDir = false;
		habilitaCCliEspUqextDir = false;
		habilitaCCliEspUqsupDir = false;
		habilitaCCliEspUqintDir = false;
		habilitaCCliEspUqinfDir = false;
		habilitaCCliEspRraDir = false;
		habilitaCCliEspPaDir = false;
		habilitaCCliLinfAxDir = false;
		habilitaCCliLinfSupraDir = false;
		habilitaCLesaoDiag = false;
  		habilitaCRadDistArqDir = false;
  		habilitaCRadDistArqEsq = false;
  		habilitaCDiagDistArqDir = false;
  		habilitaCDiagDistArqEsq = false;
  		habilitaCCliDescEsq = false;
  		habilitaCCliNodUqextEsq = false;
  		habilitaCCliNodPaEsq = false;
  		habilitaCCliEspUqextEsq = false;
  		habilitaCCliLinfSupraEsq = false;
  		
  		questionarioSismamaInfComplementaresMamoDiagnosticaController.limpar();
  		questionarioSismamaInfComplementaresMamoRastreamentoController.limpar();
	}

	private void chamarON11(Boolean habilitar) {
		if (habilitar && (DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.DIREITA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {
			habilitarON11(Boolean.TRUE);
		} else {
			habilitarON11(Boolean.FALSE);
			zerarON11();
		}
	}

	
	
	private void chamarON10(Boolean habilitar) {
		if(habilitar && (DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || 
				DominioSismamaMamaExaminada.ESQUERDA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) )){
			habilitarON10(Boolean.TRUE);
		}else{
			habilitarON10(Boolean.FALSE);
			
			zerarON10();
		}
	}
	
	private void chamarON9(Boolean habilitar) {
		if(habilitar){
			if (DominioSismamaMamaExaminada.DIREITA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()))) {
				habilitaCLesaoDiag = true;
			}
			
			if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name()))) {
				if ((DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.DIREITA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {
					habilitaCDiagNoduloDir = true;
					habilitaCDiagMicrocalDir = true;
					habilitaCDiagAssimFocDir = true;
					habilitaCDiagAssimDifDir = true;
					habilitaCDiagAreaDensDir = true;
					habilitaCDiagDistArqDir = true;
				}
				if ((DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {
					habilitaCDiagNoduloEsq = true;
					habilitaCDiagMicrocalEsq = true;
					habilitaCDiagAssimFocEsq = true;
					habilitaCDiagAssimDifEsq = true;
					habilitaCDiagAreaDensEsq = true;
					habilitaCDiagDistArqEsq = true;
				}
				
			}else{
				habilitaON09(Boolean.FALSE);
				zerarON09();
				
			}
		}else{
			habilitaCLesaoDiag = false;
			habilitaON09(Boolean.FALSE);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name(),Boolean.FALSE);
			zerarON09();
		}
	}

	private void chamarON8(Boolean habilitar) {
		if(habilitar){
			if (DominioSismamaMamaExaminada.DIREITA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name()))) {
				habilitaCControleRadiol = true;
			}
			if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name()))) {
				if ((DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.DIREITA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {
					habilitaCRadNoduloDir = true;
					habilitaCRadMicrocalDir = true;
					habilitaCRadAssimFocDir = true;
					habilitaCRadAssimDifDir = true;
					habilitaCRadAreaDensDir = true;
					habilitaCRadDistArqDir = true;
				}
				
				if ((DominioSismamaMamaExaminada.AMBAS.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())) || DominioSismamaMamaExaminada.ESQUERDA.equals(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name())))) {
					habilitaCRadNoduloEsq = true;
					habilitaCRadMicrocalEsq = true;
					habilitaCRadAssimFocEsq = true;
					habilitaCRadAssimDifEsq = true;
					habilitaCRadAreaDensEsq = true;
					habilitaCRadDistArqEsq = true;
				}
				
			}else{
				habilitarON08();
				zerarON08();
				
			}
		}else{
			habilitaCControleRadiol = false;
			habilitarON08();
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name(),Boolean.FALSE);
			zerarON08();
		}
	}



	
	private void chamarON4(Boolean habilitar) {
		if(habilitar){
			habilitaCCliLesPalpa = true;
		}else{
			habilitaCCliLesPalpa = false;
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name(),Boolean.FALSE);
		}
		if(obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name()))){
			chamarON10(Boolean.TRUE);
			chamarON11(Boolean.TRUE);
		}else{
			chamarON10(Boolean.FALSE);
			chamarON11(Boolean.FALSE);
		}			
		
	}

  	 // ON3
  	public void habilitarCamposAvaliacaoRegiao() {

		if (this.tipoMamografia.equals(DominioSismamaTipoMamografia.C_MAMO_DIAG)) {

			if (!obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name())) && !obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name())) && !obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name())) && !obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ.name())) && !obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT.name())) && !obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_LESAO_BIOPSIA_PAAF.name()))) {

				chamarON4(Boolean.TRUE);
				chamarON8(Boolean.TRUE);
				chamarON9(Boolean.TRUE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.TRUE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.TRUE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.TRUE);

			} else if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CLI_LES_PALPA.name()))) {

				chamarON4(Boolean.TRUE);
				chamarON8(Boolean.FALSE);
				chamarON9(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.FALSE);

			} else if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_CONTROLE_RADIOL.name()))) {

				chamarON4(Boolean.FALSE);
				chamarON8(Boolean.TRUE);
				chamarON9(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.FALSE);

			} else if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_LESAO_DIAG.name()))) {

				chamarON4(Boolean.FALSE);
				chamarON8(Boolean.FALSE);
				chamarON9(Boolean.TRUE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.FALSE);

			} else if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ.name()))) {

				chamarON4(Boolean.FALSE);
				chamarON8(Boolean.FALSE);
				chamarON9(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.TRUE);

			} else if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_TMP_LESAO_CAT.name()))) {

				chamarON4(Boolean.FALSE);
				chamarON8(Boolean.FALSE);
				chamarON9(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.TRUE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.FALSE);

			} else if (obtemValorDefaultBoolean(getQuestionario().get(DominioSismamaMamoCadCodigo.C_LESAO_BIOPSIA_PAAF.name()))) {

				chamarON4(Boolean.FALSE);
				chamarON8(Boolean.FALSE);
				chamarON9(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.TRUE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
				questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.FALSE);
			}

			this.questionarioSismamaInfComplementaresMamoRastreamentoController.habilitarCamposMamoRastreamento(Boolean.FALSE);

		} else if (this.tipoMamografia.equals(DominioSismamaTipoMamografia.C_MAMO_RASTR)) {

			chamarON4(Boolean.FALSE);
			chamarON8(Boolean.FALSE);
			chamarON9(Boolean.FALSE);
			questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposControleLesaoAposBiopsiaFragmentoPAAF(Boolean.FALSE);
			questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposRevisaoMamografiaLesaoRealizada(Boolean.FALSE);
			questionarioSismamaInfComplementaresMamoDiagnosticaController.chamaHabilitarCamposAvaliacaoQTNeoAdjuvante(Boolean.FALSE);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_AVALIACAO_ADJ.name(), Boolean.FALSE);

			this.questionarioSismamaInfComplementaresMamoRastreamentoController.habilitarCamposMamoRastreamento(Boolean.TRUE);
		}
	}
  	
	//ON2
	public void habilitarCamposMamoDiag(){
		if (this.tipoMamografia.equals(DominioSismamaTipoMamografia.C_MAMO_DIAG)) {

			getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_DIAG.name(), null);

			getQuestionario().put(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.name(), true);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_MAMO_RASTR.name(), null);
			
			this.habilitaCMamoDiag = Boolean.TRUE;
			this.habilitaCMamoRastr = Boolean.FALSE;
			this.habilitaCCliDiag = Boolean.TRUE;

			this.habilitarCamposInfsMamoDiagnostica = Boolean.TRUE;
			this.habilitarCamposInfsMamoRastreamento = Boolean.FALSE;

		} else if (this.tipoMamografia.equals(DominioSismamaTipoMamografia.C_MAMO_RASTR)) {
			
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_MAMO_DIAG.name(), null);
			getQuestionario().put(DominioSismamaMamoCadCodigo.C_MAMO_RASTR.name(), true);

			this.habilitaCMamoDiag = Boolean.FALSE;
			this.habilitaCMamoRastr = Boolean.TRUE;
			this.habilitaCCliDiag = Boolean.FALSE;

			this.habilitarCamposInfsMamoDiagnostica = Boolean.FALSE;
			this.habilitarCamposInfsMamoRastreamento = Boolean.TRUE;
		}
		habilitarCamposAvaliacaoRegiao();
	}
	
	
	public void habilitarON11(Boolean habilitar) {
		habilitaCCliPapilDir = habilitar;
		habilitaCCliDescDir = habilitar;
		habilitaCCliNodQseDir = habilitar;
		habilitaCCliNodQieDir = habilitar;
		habilitaCCliNodQsiDir = habilitar;
		habilitaCCliNodQiiDir = habilitar;
		habilitaCCliNodUqextDir = habilitar;
		habilitaCCliNodUqsupDir = habilitar;
		habilitaCCliNodUqintDir = habilitar;
		habilitaCCliNodUqinfDir = habilitar;
		habilitaCCliNodRraDir = habilitar;
		habilitaCCliNodPaDir = habilitar;
		habilitaCCliEspQseDir = habilitar;
		habilitaCCliEspQieDir = habilitar;
		habilitaCCliEspQsiDir = habilitar;
		habilitaCCliEspQiiDir = habilitar;
		habilitaCCliEspUqextDir = habilitar;
		habilitaCCliEspUqsupDir = habilitar;
		habilitaCCliEspUqintDir = habilitar;
		habilitaCCliEspUqinfDir = habilitar;
		habilitaCCliEspRraDir = habilitar;
		habilitaCCliEspPaDir = habilitar;
		habilitaCCliLinfAxDir = habilitar;
		habilitaCCliLinfSupraDir = habilitar;
	}
	
	public void zerarON11() {
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_PAPIL_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_DESC_DIR.name(), null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_DIR.name(), Boolean.FALSE);
	}
	

	public void habilitarON10(Boolean habilitar) {
		habilitaCCliPapilEsq = habilitar;
		habilitaCCliDescEsq = habilitar;
		habilitaCCliNodQseEsq = habilitar;
		habilitaCCliNodQieEsq = habilitar;
		habilitaCCliNodQsiEsq = habilitar;
		habilitaCCliNodQiiEsq = habilitar;
		habilitaCCliNodUqextEsq = habilitar;
		habilitaCCliNodUqsupEsq = habilitar;
		habilitaCCliNodUqintEsq = habilitar;
		habilitaCCliNodUqinfEsq = habilitar;
		habilitaCCliNodRraEsq = habilitar;
		habilitaCCliNodPaEsq = habilitar;
		habilitaCCliEspQseEsq = habilitar;
		habilitaCCliEspQieEsq = habilitar;
		habilitaCCliEspQsiEsq = habilitar;
		habilitaCCliEspQiiEsq = habilitar;
		habilitaCCliEspUqextEsq = habilitar;
		habilitaCCliEspUqsupEsq = habilitar;
		habilitaCCliEspUqintEsq = habilitar;
		habilitaCCliEspUqinfEsq = habilitar;
		habilitaCCliEspRraEsq = habilitar;
		habilitaCCliEspPaEsq = habilitar;
		habilitaCCliLinfAxEsq = habilitar;
		habilitaCCliLinfSupraEsq = habilitar;
	}


	public void zerarON10() {
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_PAPIL_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_DESC_ESQ.name(), null);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSE_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QIE_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QSI_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_QII_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQEXT_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQSUP_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINT_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_UQINF_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_RRA_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_NOD_PA_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSE_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QIE_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QSI_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_QII_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQEXT_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQSUP_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINT_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_UQINF_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_RRA_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_ESP_PA_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_LINF_AX_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_CLI_LINF_SUPRA_ESQ.name(), Boolean.FALSE);
	}
	
	public void habilitaON09(Boolean habilita) {
		habilitaCDiagNoduloDir = false;
		habilitaCDiagMicrocalDir = habilita;
		habilitaCDiagAssimFocDir = habilita;
		habilitaCDiagAssimDifDir = habilita;
		habilitaCDiagAreaDensDir = habilita;
		habilitaCDiagDistArqDir = habilita;
		habilitaCDiagNoduloEsq = habilita;
		habilitaCDiagMicrocalEsq = habilita;
		habilitaCDiagAssimFocEsq = habilita;
		habilitaCDiagAssimDifEsq = habilita;
		habilitaCDiagAreaDensEsq = habilita;
		habilitaCDiagDistArqEsq = habilita;
	}



	public void zerarON09() {
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_NODULO_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_NODULO_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_MICROCAL_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_FOC_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_ASSIM_DIF_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_AREA_DENS_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_DIAG_DIST_ARQ_ESQ.name(), Boolean.FALSE);
	}


	public void habilitarON08() {
		habilitaCRadNoduloDir = false;
		habilitaCRadMicrocalDir = false;
		habilitaCRadAssimFocDir = false;
		habilitaCRadAssimDifDir = false;
		habilitaCRadAreaDensDir = false;
		habilitaCRadDistArqDir = false;
		habilitaCRadNoduloEsq = false;
		habilitaCRadMicrocalEsq = false;
		habilitaCRadAssimFocEsq = false;
		habilitaCRadAssimDifEsq = false;
		habilitaCRadAreaDensEsq = false;
		habilitaCRadDistArqEsq = false;
	}
	

	public void zerarON08() {
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_NODULO_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_DIR.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_NODULO_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_MICROCAL_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_FOC_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_ASSIM_DIF_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_AREA_DENS_ESQ.name(), Boolean.FALSE);
		getQuestionario().put(DominioSismamaMamoCadCodigo.C_RAD_DIST_ARQ_ESQ.name(), Boolean.FALSE);
	}
	
	
	public Boolean obtemValorDefaultBoolean(Object obj){
		if(obj==null){
			return Boolean.FALSE;
		}else{
			return (Boolean)obj;
		}
	}
	
	
	//GETS E SETS

	public Boolean getHabilitaCMamoDiag() {
		return habilitaCMamoDiag;
	}

	public void setHabilitaCMamoDiag(Boolean habilitaCMamoDiag) {
		this.habilitaCMamoDiag = habilitaCMamoDiag;
	}

	public Boolean getHabilitaCMamoRastr() {
		return habilitaCMamoRastr;
	}

	public void setHabilitaCMamoRastr(Boolean habilitaCMamoRastr) {
		this.habilitaCMamoRastr = habilitaCMamoRastr;
	}

	public Boolean getHabilitaCCliDiag() {
		return habilitaCCliDiag;
	}

	public void setHabilitaCCliDiag(Boolean habilitaCCliDiag) {
		this.habilitaCCliDiag = habilitaCCliDiag;
	}
	public Boolean getHabilitaCCliLesPalpa() {
		return habilitaCCliLesPalpa;
	}

	public void setHabilitaCCliLesPalpa(Boolean habilitaCCliLesPalpa) {
		this.habilitaCCliLesPalpa = habilitaCCliLesPalpa;
	}

	public Boolean getHabilitaCControleRadiol() {
		return habilitaCControleRadiol;
	}

	public void setHabilitaCControleRadiol(Boolean habilitaCControleRadiol) {
		this.habilitaCControleRadiol = habilitaCControleRadiol;
	}

	public Boolean getHabilitaCRadNoduloDir() {
		return habilitaCRadNoduloDir;
	}

	public void setHabilitaCRadNoduloDir(Boolean habilitaCRadNoduloDir) {
		this.habilitaCRadNoduloDir = habilitaCRadNoduloDir;
	}

	public Boolean getHabilitaCRadMicrocalDir() {
		return habilitaCRadMicrocalDir;
	}

	public void setHabilitaCRadMicrocalDir(Boolean habilitaCRadMicrocalDir) {
		this.habilitaCRadMicrocalDir = habilitaCRadMicrocalDir;
	}

	public Boolean getHabilitaCRadAssimFocDir() {
		return habilitaCRadAssimFocDir;
	}

	public void setHabilitaCRadAssimFocDir(Boolean habilitaCRadAssimFocDir) {
		this.habilitaCRadAssimFocDir = habilitaCRadAssimFocDir;
	}

	public Boolean getHabilitaCRadAssimDifDir() {
		return habilitaCRadAssimDifDir;
	}

	public void setHabilitaCRadAssimDifDir(Boolean habilitaCRadAssimDifDir) {
		this.habilitaCRadAssimDifDir = habilitaCRadAssimDifDir;
	}

	public Boolean getHabilitaCRadAreaDensDir() {
		return habilitaCRadAreaDensDir;
	}

	public void setHabilitaCRadAreaDensDir(Boolean habilitaCRadAreaDensDir) {
		this.habilitaCRadAreaDensDir = habilitaCRadAreaDensDir;
	}

	public Boolean getHabilitaCRadDistArqDir() {
		return habilitaCRadDistArqDir;
	}

	public void setHabilitaCRadDistArqDir(Boolean habilitaCRadDistArqDir) {
		this.habilitaCRadDistArqDir = habilitaCRadDistArqDir;
	}

	public Boolean getHabilitaCRadNoduloEsq() {
		return habilitaCRadNoduloEsq;
	}

	public void setHabilitaCRadNoduloEsq(Boolean habilitaCRadNoduloEsq) {
		this.habilitaCRadNoduloEsq = habilitaCRadNoduloEsq;
	}

	public Boolean getHabilitaCRadMicrocalEsq() {
		return habilitaCRadMicrocalEsq;
	}

	public void setHabilitaCRadMicrocalEsq(Boolean habilitaCRadMicrocalEsq) {
		this.habilitaCRadMicrocalEsq = habilitaCRadMicrocalEsq;
	}

	public Boolean getHabilitaCRadAssimFocEsq() {
		return habilitaCRadAssimFocEsq;
	}

	public void setHabilitaCRadAssimFocEsq(Boolean habilitaCRadAssimFocEsq) {
		this.habilitaCRadAssimFocEsq = habilitaCRadAssimFocEsq;
	}

	public Boolean getHabilitaCRadAssimDifEsq() {
		return habilitaCRadAssimDifEsq;
	}

	public void setHabilitaCRadAssimDifEsq(Boolean habilitaCRadAssimDifEsq) {
		this.habilitaCRadAssimDifEsq = habilitaCRadAssimDifEsq;
	}

	public Boolean getHabilitaCRadAreaDensEsq() {
		return habilitaCRadAreaDensEsq;
	}

	public void setHabilitaCRadAreaDensEsq(Boolean habilitaCRadAreaDensEsq) {
		this.habilitaCRadAreaDensEsq = habilitaCRadAreaDensEsq;
	}

	public Boolean getHabilitaCRadDistArqEsq() {
		return habilitaCRadDistArqEsq;
	}

	public void setHabilitaCRadDistArqEsq(Boolean habilitaCRadDistArqEsq) {
		this.habilitaCRadDistArqEsq = habilitaCRadDistArqEsq;
	}

	public Boolean getHabilitaCDiagNoduloDir() {
		return habilitaCDiagNoduloDir;
	}

	public void setHabilitaCDiagNoduloDir(Boolean habilitaCDiagNoduloDir) {
		this.habilitaCDiagNoduloDir = habilitaCDiagNoduloDir;
	}

	public Boolean getHabilitaCDiagMicrocalDir() {
		return habilitaCDiagMicrocalDir;
	}

	public void setHabilitaCDiagMicrocalDir(Boolean habilitaCDiagMicrocalDir) {
		this.habilitaCDiagMicrocalDir = habilitaCDiagMicrocalDir;
	}

	public Boolean getHabilitaCDiagAssimFocDir() {
		return habilitaCDiagAssimFocDir;
	}

	public void setHabilitaCDiagAssimFocDir(Boolean habilitaCDiagAssimFocDir) {
		this.habilitaCDiagAssimFocDir = habilitaCDiagAssimFocDir;
	}

	public Boolean getHabilitaCDiagAssimDifDir() {
		return habilitaCDiagAssimDifDir;
	}

	public void setHabilitaCDiagAssimDifDir(Boolean habilitaCDiagAssimDifDir) {
		this.habilitaCDiagAssimDifDir = habilitaCDiagAssimDifDir;
	}

	public Boolean getHabilitaCDiagAreaDensDir() {
		return habilitaCDiagAreaDensDir;
	}

	public void setHabilitaCDiagAreaDensDir(Boolean habilitaCDiagAreaDensDir) {
		this.habilitaCDiagAreaDensDir = habilitaCDiagAreaDensDir;
	}

	public Boolean getHabilitaCDiagDistArqDir() {
		return habilitaCDiagDistArqDir;
	}

	public void setHabilitaCDiagDistArqDir(Boolean habilitaCDiagDistArqDir) {
		this.habilitaCDiagDistArqDir = habilitaCDiagDistArqDir;
	}

	public Boolean getHabilitaCDiagNoduloEsq() {
		return habilitaCDiagNoduloEsq;
	}

	public void setHabilitaCDiagNoduloEsq(Boolean habilitaCDiagNoduloEsq) {
		this.habilitaCDiagNoduloEsq = habilitaCDiagNoduloEsq;
	}

	public Boolean getHabilitaCDiagMicrocalEsq() {
		return habilitaCDiagMicrocalEsq;
	}

	public void setHabilitaCDiagMicrocalEsq(Boolean habilitaCDiagMicrocalEsq) {
		this.habilitaCDiagMicrocalEsq = habilitaCDiagMicrocalEsq;
	}

	public Boolean getHabilitaCDiagAssimFocEsq() {
		return habilitaCDiagAssimFocEsq;
	}

	public void setHabilitaCDiagAssimFocEsq(Boolean habilitaCDiagAssimFocEsq) {
		this.habilitaCDiagAssimFocEsq = habilitaCDiagAssimFocEsq;
	}

	public Boolean getHabilitaCDiagAssimDifEsq() {
		return habilitaCDiagAssimDifEsq;
	}

	public void setHabilitaCDiagAssimDifEsq(Boolean habilitaCDiagAssimDifEsq) {
		this.habilitaCDiagAssimDifEsq = habilitaCDiagAssimDifEsq;
	}

	public Boolean getHabilitaCDiagAreaDensEsq() {
		return habilitaCDiagAreaDensEsq;
	}

	public void setHabilitaCDiagAreaDensEsq(Boolean habilitaCDiagAreaDensEsq) {
		this.habilitaCDiagAreaDensEsq = habilitaCDiagAreaDensEsq;
	}

	public Boolean getHabilitaCDiagDistArqEsq() {
		return habilitaCDiagDistArqEsq;
	}

	public void setHabilitaCDiagDistArqEsq(Boolean habilitaCDiagDistArqEsq) {
		this.habilitaCDiagDistArqEsq = habilitaCDiagDistArqEsq;
	}

	public Boolean getHabilitaCLesaoDiag() {
		return habilitaCLesaoDiag;
	}

	public void setHabilitaCLesaoDiag(Boolean habilitaCLesaoDiag) {
		this.habilitaCLesaoDiag = habilitaCLesaoDiag;
	}

	public Boolean getHabilitaCCliPapilEsq() {
		return habilitaCCliPapilEsq;
	}

	public void setHabilitaCCliPapilEsq(Boolean habilitaCCliPapilEsq) {
		this.habilitaCCliPapilEsq = habilitaCCliPapilEsq;
	}

	public Boolean getHabilitaCCliDescEsq() {
		return habilitaCCliDescEsq;
	}

	public void setHabilitaCCliDescEsq(Boolean habilitaCCliDescEsq) {
		this.habilitaCCliDescEsq = habilitaCCliDescEsq;
	}

	public Boolean getHabilitaCCliNodQseEsq() {
		return habilitaCCliNodQseEsq;
	}

	public void setHabilitaCCliNodQseEsq(Boolean habilitaCCliNodQseEsq) {
		this.habilitaCCliNodQseEsq = habilitaCCliNodQseEsq;
	}

	public Boolean getHabilitaCCliNodQieEsq() {
		return habilitaCCliNodQieEsq;
	}

	public void setHabilitaCCliNodQieEsq(Boolean habilitaCCliNodQieEsq) {
		this.habilitaCCliNodQieEsq = habilitaCCliNodQieEsq;
	}

	public Boolean getHabilitaCCliNodQsiEsq() {
		return habilitaCCliNodQsiEsq;
	}

	public void setHabilitaCCliNodQsiEsq(Boolean habilitaCCliNodQsiEsq) {
		this.habilitaCCliNodQsiEsq = habilitaCCliNodQsiEsq;
	}

	public Boolean getHabilitaCCliNodQiiEsq() {
		return habilitaCCliNodQiiEsq;
	}

	public void setHabilitaCCliNodQiiEsq(Boolean habilitaCCliNodQiiEsq) {
		this.habilitaCCliNodQiiEsq = habilitaCCliNodQiiEsq;
	}

	public Boolean getHabilitaCCliNodUqextEsq() {
		return habilitaCCliNodUqextEsq;
	}

	public void setHabilitaCCliNodUqextEsq(Boolean habilitaCCliNodUqextEsq) {
		this.habilitaCCliNodUqextEsq = habilitaCCliNodUqextEsq;
	}

	public Boolean getHabilitaCCliNodUqsupEsq() {
		return habilitaCCliNodUqsupEsq;
	}

	public void setHabilitaCCliNodUqsupEsq(Boolean habilitaCCliNodUqsupEsq) {
		this.habilitaCCliNodUqsupEsq = habilitaCCliNodUqsupEsq;
	}

	public Boolean getHabilitaCCliNodUqintEsq() {
		return habilitaCCliNodUqintEsq;
	}

	public void setHabilitaCCliNodUqintEsq(Boolean habilitaCCliNodUqintEsq) {
		this.habilitaCCliNodUqintEsq = habilitaCCliNodUqintEsq;
	}

	public Boolean getHabilitaCCliNodUqinfEsq() {
		return habilitaCCliNodUqinfEsq;
	}

	public void setHabilitaCCliNodUqinfEsq(Boolean habilitaCCliNodUqinfEsq) {
		this.habilitaCCliNodUqinfEsq = habilitaCCliNodUqinfEsq;
	}

	public Boolean getHabilitaCCliNodRraEsq() {
		return habilitaCCliNodRraEsq;
	}

	public void setHabilitaCCliNodRraEsq(Boolean habilitaCCliNodRraEsq) {
		this.habilitaCCliNodRraEsq = habilitaCCliNodRraEsq;
	}

	public Boolean getHabilitaCCliNodPaEsq() {
		return habilitaCCliNodPaEsq;
	}

	public void setHabilitaCCliNodPaEsq(Boolean habilitaCCliNodPaEsq) {
		this.habilitaCCliNodPaEsq = habilitaCCliNodPaEsq;
	}

	public Boolean getHabilitaCCliEspQseEsq() {
		return habilitaCCliEspQseEsq;
	}

	public void setHabilitaCCliEspQseEsq(Boolean habilitaCCliEspQseEsq) {
		this.habilitaCCliEspQseEsq = habilitaCCliEspQseEsq;
	}

	public Boolean getHabilitaCCliEspQieEsq() {
		return habilitaCCliEspQieEsq;
	}

	public void setHabilitaCCliEspQieEsq(Boolean habilitaCCliEspQieEsq) {
		this.habilitaCCliEspQieEsq = habilitaCCliEspQieEsq;
	}

	public Boolean getHabilitaCCliEspQsiEsq() {
		return habilitaCCliEspQsiEsq;
	}

	public void setHabilitaCCliEspQsiEsq(Boolean habilitaCCliEspQsiEsq) {
		this.habilitaCCliEspQsiEsq = habilitaCCliEspQsiEsq;
	}

	public Boolean getHabilitaCCliEspQiiEsq() {
		return habilitaCCliEspQiiEsq;
	}

	public void setHabilitaCCliEspQiiEsq(Boolean habilitaCCliEspQiiEsq) {
		this.habilitaCCliEspQiiEsq = habilitaCCliEspQiiEsq;
	}

	public Boolean getHabilitaCCliEspUqextEsq() {
		return habilitaCCliEspUqextEsq;
	}

	public void setHabilitaCCliEspUqextEsq(Boolean habilitaCCliEspUqextEsq) {
		this.habilitaCCliEspUqextEsq = habilitaCCliEspUqextEsq;
	}

	public Boolean getHabilitaCCliEspUqsupEsq() {
		return habilitaCCliEspUqsupEsq;
	}

	public void setHabilitaCCliEspUqsupEsq(Boolean habilitaCCliEspUqsupEsq) {
		this.habilitaCCliEspUqsupEsq = habilitaCCliEspUqsupEsq;
	}

	public Boolean getHabilitaCCliEspUqintEsq() {
		return habilitaCCliEspUqintEsq;
	}

	public void setHabilitaCCliEspUqintEsq(Boolean habilitaCCliEspUqintEsq) {
		this.habilitaCCliEspUqintEsq = habilitaCCliEspUqintEsq;
	}

	public Boolean getHabilitaCCliEspUqinfEsq() {
		return habilitaCCliEspUqinfEsq;
	}

	public void setHabilitaCCliEspUqinfEsq(Boolean habilitaCCliEspUqinfEsq) {
		this.habilitaCCliEspUqinfEsq = habilitaCCliEspUqinfEsq;
	}

	public Boolean getHabilitaCCliEspRraEsq() {
		return habilitaCCliEspRraEsq;
	}

	public void setHabilitaCCliEspRraEsq(Boolean habilitaCCliEspRraEsq) {
		this.habilitaCCliEspRraEsq = habilitaCCliEspRraEsq;
	}

	public Boolean getHabilitaCCliEspPaEsq() {
		return habilitaCCliEspPaEsq;
	}

	public void setHabilitaCCliEspPaEsq(Boolean habilitaCCliEspPaEsq) {
		this.habilitaCCliEspPaEsq = habilitaCCliEspPaEsq;
	}

	public Boolean getHabilitaCCliLinfAxEsq() {
		return habilitaCCliLinfAxEsq;
	}

	public void setHabilitaCCliLinfAxEsq(Boolean habilitaCCliLinfAxEsq) {
		this.habilitaCCliLinfAxEsq = habilitaCCliLinfAxEsq;
	}

	public Boolean getHabilitaCCliLinfSupraEsq() {
		return habilitaCCliLinfSupraEsq;
	}

	public void setHabilitaCCliLinfSupraEsq(Boolean habilitaCCliLinfSupraEsq) {
		this.habilitaCCliLinfSupraEsq = habilitaCCliLinfSupraEsq;
	}

	public Boolean getHabilitaCCliPapilDir() {
		return habilitaCCliPapilDir;
	}

	public void setHabilitaCCliPapilDir(Boolean habilitaCCliPapilDir) {
		this.habilitaCCliPapilDir = habilitaCCliPapilDir;
	}

	public Boolean getHabilitaCCliDescDir() {
		return habilitaCCliDescDir;
	}

	public void setHabilitaCCliDescDir(Boolean habilitaCCliDescDir) {
		this.habilitaCCliDescDir = habilitaCCliDescDir;
	}

	public Boolean getHabilitaCCliNodQseDir() {
		return habilitaCCliNodQseDir;
	}

	public void setHabilitaCCliNodQseDir(Boolean habilitaCCliNodQseDir) {
		this.habilitaCCliNodQseDir = habilitaCCliNodQseDir;
	}

	public Boolean getHabilitaCCliNodQieDir() {
		return habilitaCCliNodQieDir;
	}

	public void setHabilitaCCliNodQieDir(Boolean habilitaCCliNodQieDir) {
		this.habilitaCCliNodQieDir = habilitaCCliNodQieDir;
	}

	public Boolean getHabilitaCCliNodQsiDir() {
		return habilitaCCliNodQsiDir;
	}

	public void setHabilitaCCliNodQsiDir(Boolean habilitaCCliNodQsiDir) {
		this.habilitaCCliNodQsiDir = habilitaCCliNodQsiDir;
	}

	public Boolean getHabilitaCCliNodQiiDir() {
		return habilitaCCliNodQiiDir;
	}

	public void setHabilitaCCliNodQiiDir(Boolean habilitaCCliNodQiiDir) {
		this.habilitaCCliNodQiiDir = habilitaCCliNodQiiDir;
	}

	public Boolean getHabilitaCCliNodUqextDir() {
		return habilitaCCliNodUqextDir;
	}

	public void setHabilitaCCliNodUqextDir(Boolean habilitaCCliNodUqextDir) {
		this.habilitaCCliNodUqextDir = habilitaCCliNodUqextDir;
	}

	public Boolean getHabilitaCCliNodUqsupDir() {
		return habilitaCCliNodUqsupDir;
	}

	public void setHabilitaCCliNodUqsupDir(Boolean habilitaCCliNodUqsupDir) {
		this.habilitaCCliNodUqsupDir = habilitaCCliNodUqsupDir;
	}

	public Boolean getHabilitaCCliNodUqintDir() {
		return habilitaCCliNodUqintDir;
	}

	public void setHabilitaCCliNodUqintDir(Boolean habilitaCCliNodUqintDir) {
		this.habilitaCCliNodUqintDir = habilitaCCliNodUqintDir;
	}

	public Boolean getHabilitaCCliNodUqinfDir() {
		return habilitaCCliNodUqinfDir;
	}

	public void setHabilitaCCliNodUqinfDir(Boolean habilitaCCliNodUqinfDir) {
		this.habilitaCCliNodUqinfDir = habilitaCCliNodUqinfDir;
	}

	public Boolean getHabilitaCCliNodRraDir() {
		return habilitaCCliNodRraDir;
	}

	public void setHabilitaCCliNodRraDir(Boolean habilitaCCliNodRraDir) {
		this.habilitaCCliNodRraDir = habilitaCCliNodRraDir;
	}

	public Boolean getHabilitaCCliNodPaDir() {
		return habilitaCCliNodPaDir;
	}

	public void setHabilitaCCliNodPaDir(Boolean habilitaCCliNodPaDir) {
		this.habilitaCCliNodPaDir = habilitaCCliNodPaDir;
	}

	public Boolean getHabilitaCCliEspQseDir() {
		return habilitaCCliEspQseDir;
	}

	public void setHabilitaCCliEspQseDir(Boolean habilitaCCliEspQseDir) {
		this.habilitaCCliEspQseDir = habilitaCCliEspQseDir;
	}

	public Boolean getHabilitaCCliEspQieDir() {
		return habilitaCCliEspQieDir;
	}

	public void setHabilitaCCliEspQieDir(Boolean habilitaCCliEspQieDir) {
		this.habilitaCCliEspQieDir = habilitaCCliEspQieDir;
	}

	public Boolean getHabilitaCCliEspQsiDir() {
		return habilitaCCliEspQsiDir;
	}

	public void setHabilitaCCliEspQsiDir(Boolean habilitaCCliEspQsiDir) {
		this.habilitaCCliEspQsiDir = habilitaCCliEspQsiDir;
	}

	public Boolean getHabilitaCCliEspQiiDir() {
		return habilitaCCliEspQiiDir;
	}

	public void setHabilitaCCliEspQiiDir(Boolean habilitaCCliEspQiiDir) {
		this.habilitaCCliEspQiiDir = habilitaCCliEspQiiDir;
	}

	public Boolean getHabilitaCCliEspUqextDir() {
		return habilitaCCliEspUqextDir;
	}

	public void setHabilitaCCliEspUqextDir(Boolean habilitaCCliEspUqextDir) {
		this.habilitaCCliEspUqextDir = habilitaCCliEspUqextDir;
	}

	public Boolean getHabilitaCCliEspUqsupDir() {
		return habilitaCCliEspUqsupDir;
	}

	public void setHabilitaCCliEspUqsupDir(Boolean habilitaCCliEspUqsupDir) {
		this.habilitaCCliEspUqsupDir = habilitaCCliEspUqsupDir;
	}

	public Boolean getHabilitaCCliEspUqintDir() {
		return habilitaCCliEspUqintDir;
	}

	public void setHabilitaCCliEspUqintDir(Boolean habilitaCCliEspUqintDir) {
		this.habilitaCCliEspUqintDir = habilitaCCliEspUqintDir;
	}

	public Boolean getHabilitaCCliEspUqinfDir() {
		return habilitaCCliEspUqinfDir;
	}

	public void setHabilitaCCliEspUqinfDir(Boolean habilitaCCliEspUqinfDir) {
		this.habilitaCCliEspUqinfDir = habilitaCCliEspUqinfDir;
	}

	public Boolean getHabilitaCCliEspRraDir() {
		return habilitaCCliEspRraDir;
	}

	public void setHabilitaCCliEspRraDir(Boolean habilitaCCliEspRraDir) {
		this.habilitaCCliEspRraDir = habilitaCCliEspRraDir;
	}

	public Boolean getHabilitaCCliEspPaDir() {
		return habilitaCCliEspPaDir;
	}

	public void setHabilitaCCliEspPaDir(Boolean habilitaCCliEspPaDir) {
		this.habilitaCCliEspPaDir = habilitaCCliEspPaDir;
	}

	public Boolean getHabilitaCCliLinfAxDir() {
		return habilitaCCliLinfAxDir;
	}

	public void setHabilitaCCliLinfAxDir(Boolean habilitaCCliLinfAxDir) {
		this.habilitaCCliLinfAxDir = habilitaCCliLinfAxDir;
	}

	public Boolean getHabilitaCCliLinfSupraDir() {
		return habilitaCCliLinfSupraDir;
	}

	public void setHabilitaCCliLinfSupraDir(Boolean habilitaCCliLinfSupraDir) {
		this.habilitaCCliLinfSupraDir = habilitaCCliLinfSupraDir;
	}
	
	public Map<String, Object> getQuestionario() {
		if (solicitacaoExameController.getRenderPorExame()) {
			return listarExamesSendoSolicitadosController.getQuestionarioSismama();
		} else if (solicitacaoExameController.getRenderPorLote()) {
			return listarExamesSendoSolicitadosLoteController.getQuestionarioSismama();
		}
		return null;
	}
	
	public void setQuestionario(Map<String, Object> questionario) {
		  		if (solicitacaoExameController.getRenderPorExame()) {
		  			listarExamesSendoSolicitadosController
		  					.setQuestionarioSismama(questionario);
		  		} else if (solicitacaoExameController.getRenderPorLote()) {
		  			listarExamesSendoSolicitadosLoteController
		  					.setQuestionarioSismama(questionario);
	 		}
	}
	
	public Boolean getHabilitarCamposInfsMamoDiagnostica() {
		return habilitarCamposInfsMamoDiagnostica;
	}
	
	public void setHabilitarCamposInfsMamoDiagnostica(Boolean habilitarCamposInfsMamoDiagnostica) {
		this.habilitarCamposInfsMamoDiagnostica = habilitarCamposInfsMamoDiagnostica;
	}

	public Boolean getHabilitarCamposInfsMamoRastreamento() {
		return habilitarCamposInfsMamoRastreamento;
	}

	public void setHabilitarCamposInfsMamoRastreamento(Boolean habilitarCamposInfsMamoRastreamento) {
		this.habilitarCamposInfsMamoRastreamento = habilitarCamposInfsMamoRastreamento;
	}

	public DominioSismamaTipoMamografia getTipoMamografia() {
		return tipoMamografia;
	}

	public void setTipoMamografia(DominioSismamaTipoMamografia tipoMamografia) {
		this.tipoMamografia = tipoMamografia;
	}

	public Boolean getHabilitaCAvaliacaoAdj() {
		return habilitaCAvaliacaoAdj;
	}

	public void setHabilitaCAvaliacaoAdj(Boolean habilitaCAvaliacaoAdj) {
		this.habilitaCAvaliacaoAdj = habilitaCAvaliacaoAdj;
	}

	public Boolean getHabilitaQTNeoAdjuvanteDir() {
		return habilitaQTNeoAdjuvanteDir;
	}

	public void setHabilitaQTNeoAdjuvanteDir(Boolean habilitaQTNeoAdjuvanteDir) {
		this.habilitaQTNeoAdjuvanteDir = habilitaQTNeoAdjuvanteDir;
	}

	public Boolean getHabilitaQTNeoAdjuvanteEsq() {
		return habilitaQTNeoAdjuvanteEsq;
	}

	public void setHabilitaQTNeoAdjuvanteEsq(Boolean habilitaQTNeoAdjuvanteEsq) {
		this.habilitaQTNeoAdjuvanteEsq = habilitaQTNeoAdjuvanteEsq;
	}

	public Boolean getHabilitaCLesaoBiopsiaPaaf() {
		return habilitaCLesaoBiopsiaPaaf;
	}

	public void setHabilitaCLesaoBiopsiaPaaf(Boolean habilitaCLesaoBiopsiaPaaf) {
		this.habilitaCLesaoBiopsiaPaaf = habilitaCLesaoBiopsiaPaaf;
	}

	public Boolean getHabilitaRevMamoRealizada() {
		return habilitaRevMamoRealizada;
	}

	public void setHabilitaRevMamoRealizada(Boolean habilitaRevMamoRealizada) {
		this.habilitaRevMamoRealizada = habilitaRevMamoRealizada;
	}
											  
		
	
}
