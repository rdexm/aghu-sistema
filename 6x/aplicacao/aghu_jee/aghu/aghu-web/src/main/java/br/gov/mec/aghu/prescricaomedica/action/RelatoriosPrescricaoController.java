package br.gov.mec.aghu.prescricaomedica.action;

import javax.annotation.PostConstruct;
import javax.ejb.Asynchronous;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.prescricaomedica.constantes.EnumTipoImpressao;
import br.gov.mec.aghu.prescricaomedica.vo.ConfirmacaoPrescricaoVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * @author ehgsilva
 */
public class RelatoriosPrescricaoController extends ActionController {
	
	private static final long serialVersionUID = -2815875383288370634L;
	
	private static final Log LOG = LogFactory.getLog(RelatoriosPrescricaoController.class);
	
	public enum RelatoriosPrescricaoControllerExceptionCode implements	BusinessExceptionCode {
		PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA_SOLICITACAO_HEMOTERAPICA,
		PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA_CONSULTORIA,
		PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA_DISPENSACAO_FARMACIA,
		PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA
	}
	
	@Inject
	private RelatorioSolHemoterapicaController relatorioSolHemoterapicaController;
	
	@Inject
	private RelatorioConsultoriaController relatorioConsultoriaController;
	
	@Inject
	private RelatorioDispensacaoFarmaciaController relatorioDispensacaoFarmaciaController;
	
	@Inject
	private RelatorioPrescricaoMedicaController relatorioPrescricaoMedicaController;
	
	@Inject
	private FormularioDispAntimicrobianosController formularioDispAntimicrobianosController;

	private PrescricaoMedicaVO prescricaoMedicaVO;
	private ConfirmacaoPrescricaoVO confirmacaoPrescricaoVO;
	private EnumTipoImpressao tipoImpressao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	private Boolean relatorioSemAlteracao;
	
	//Simular chamada que ocorria através de @Observer("prescricaoConfirmada")
	//Quando cupsFacade.verificarCupsAtivo() é FALSE, os relatórios são apenas apresentados na tela; A coleção já deve ter sido carregada
	//Para os que são apresentados na tela, não deve ser gerado pendencia de assinatura digital, entao orenderPdf será gerarDocumento(Boolean.TRUE)
	@Asynchronous 
	public void iniciarRelatorios(Boolean bloqueiaGeracaoPendencia) {
		
		relatorioSemAlteracao = Boolean.FALSE;
		
		gerarRelatorioHemoterapica(bloqueiaGeracaoPendencia);
		gerarRelatorioConsultoriaPrescricaoMedica(bloqueiaGeracaoPendencia);
		gerarRelatorioPrescricaoMedica(bloqueiaGeracaoPendencia);
		gerarRelatorioDispensacaoFarmacia(bloqueiaGeracaoPendencia);
		
		formularioDispAntimicrobianosController.init();
		
		validaSeRelatorioHemoterapicaConsultoriaDispensacaoMedicaEvazio();
	}

	/**
	 * 
	 */
	private void validaSeRelatorioHemoterapicaConsultoriaDispensacaoMedicaEvazio() {
		try{
			if(relatorioSolHemoterapicaController.getColecao().isEmpty()
					&& relatorioConsultoriaController.recuperarColecao().isEmpty()
					&& relatorioDispensacaoFarmaciaController.getColecao().isEmpty()
					&& relatorioPrescricaoMedicaController.getItensDaPrescricaoConfirmacao().isEmpty()){
				relatorioSemAlteracao = Boolean.TRUE;
			}
		}catch (Exception e) {
			LOG.error("Problema ao gerar relatório da prescricao médica", e);
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					RelatoriosPrescricaoControllerExceptionCode.PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA));
		}
	}

	/**
	 * @param bloqueiaGeracaoPendencia
	 */
	private void gerarRelatorioPrescricaoMedica(Boolean bloqueiaGeracaoPendencia) {
		try{
			this.relatorioPrescricaoMedicaController.setDataMovimento(confirmacaoPrescricaoVO.getDataMovimento());
			this.relatorioPrescricaoMedicaController.setServidorValido(confirmacaoPrescricaoVO.getServidorValido());
			this.relatorioPrescricaoMedicaController.setItensConfirmados(confirmacaoPrescricaoVO.getItensConfirmados());
			this.relatorioPrescricaoMedicaController.setTipoImpressao(tipoImpressao);
			this.relatorioPrescricaoMedicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
			if(bloqueiaGeracaoPendencia){
				this.relatorioPrescricaoMedicaController.init();
			}else{
				this.relatorioPrescricaoMedicaController.observarEventoImpressaoPrescricaoConfirmada();
			}
		}catch (Exception e) {
			LOG.error("Problema ao gerar relatório da prescricao médica", e);
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					RelatoriosPrescricaoControllerExceptionCode.PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA));
		}
	}

	/**
	 * @param bloqueiaGeracaoPendencia
	 */
	private void gerarRelatorioDispensacaoFarmacia(	Boolean bloqueiaGeracaoPendencia) {
		try{
			this.relatorioDispensacaoFarmaciaController.setServidorValida(confirmacaoPrescricaoVO.getServidorValido());
			this.relatorioDispensacaoFarmaciaController.setSeqAtendimento(prescricaoMedicaVO.getPrescricaoMedica().getId().getAtdSeq());
			this.relatorioDispensacaoFarmaciaController.setSeqPrescricao(prescricaoMedicaVO.getPrescricaoMedica().getId().getSeq());
			this.relatorioDispensacaoFarmaciaController.setTipoImpressao(tipoImpressao);
			this.relatorioDispensacaoFarmaciaController.setDataMovimento(confirmacaoPrescricaoVO.getDataMovimento());
			if(bloqueiaGeracaoPendencia){
				this.relatorioDispensacaoFarmaciaController.init();
			}else{
				this.relatorioDispensacaoFarmaciaController.observarEventoImpressaoPrescricaoConfirmada();
			}
		}catch (Exception e) {
			LOG.error("Problema ao gerar relatório  de dispensação de farmácia da prescricao médica", e);
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					RelatoriosPrescricaoControllerExceptionCode.PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA_DISPENSACAO_FARMACIA));
		}
	}

	/**
	 * @param bloqueiaGeracaoPendencia
	 */
	private void gerarRelatorioConsultoriaPrescricaoMedica(
			Boolean bloqueiaGeracaoPendencia) {
		try{
			this.relatorioConsultoriaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
			this.relatorioConsultoriaController.setTipoImpressao(tipoImpressao);
			if(bloqueiaGeracaoPendencia){
				this.relatorioConsultoriaController.print();
			}else{
				this.relatorioConsultoriaController.observarEventoImpressaoPrescricaoConfirmada();
			}
		}catch (Exception e) {
			LOG.error("Problema ao gerar relatório  de consultoria da prescricao médica", e);
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					RelatoriosPrescricaoControllerExceptionCode.PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA_CONSULTORIA));
		}
	}

	/**
	 * @param bloqueiaGeracaoPendencia
	 */
	private void gerarRelatorioHemoterapica(Boolean bloqueiaGeracaoPendencia) {
		try{
			this.relatorioSolHemoterapicaController.setServidorValida(confirmacaoPrescricaoVO.getServidorValido());
			this.relatorioSolHemoterapicaController.setTipoImpressao(tipoImpressao);
			this.relatorioSolHemoterapicaController.setPrescricaoMedicaVO(prescricaoMedicaVO);
			this.relatorioSolHemoterapicaController.setDataMovimento(confirmacaoPrescricaoVO.getDataMovimento());
			if(bloqueiaGeracaoPendencia){
				this.relatorioSolHemoterapicaController.print();
			}else{
				this.relatorioSolHemoterapicaController.observarEventoImpressaoPrescricaoConfirmada();
			}
		}catch (Exception e) {
			LOG.error("Problema ao gerar relatório  de solicitação hemoterapica da prescricao médica", e);
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					RelatoriosPrescricaoControllerExceptionCode.PROBLEMA_GERAR_RELATORIO_PRESCRICAO_MEDICA_SOLICITACAO_HEMOTERAPICA));
		}
	}
	
	public String voltar(){
		LOG.debug("Voltando prescricaomedica-verificaPrescricaoMedica");
		return "prescricaomedica-verificaPrescricaoMedica";
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}

	public EnumTipoImpressao getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressao tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}

	public void setConfirmacaoPrescricaoVO(
			ConfirmacaoPrescricaoVO confirmacaoPrescricaoVO) {
		this.confirmacaoPrescricaoVO = confirmacaoPrescricaoVO;
	}

	public Boolean getRelatorioSemAlteracao() {
		return relatorioSemAlteracao;
	}

	public void setRelatorioSemAlteracao(Boolean relatorioSemAlteracao) {
		this.relatorioSemAlteracao = relatorioSemAlteracao;
	}
}
