package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Controller da aba de <b>Laudos SUS</b> da tela de Relatorio de Conclusao do
 * Sumario Alta.<br>
 * 
 * @author rcorvalao
 * 
 */

public class RelatorioConclusaoAbaLaudoSus extends RelatorioLaudosProcSusController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5015996050336897953L;
	

	@Inject
	private SistemaImpressao sistemaImpressao;

	public enum RelatorioConclusaoAbaLaudoSusExceptionCode implements BusinessExceptionCode {
		ERRO_AO_BUSCAR_DADOS_RELATORIO;
	}

	private MpmAltaSumario altaSumario;
	private String altaTipoOrigem;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private RelatorioConclusaoSumarioAltaController relatorioConclusaoSumarioAltaController;

	@PostConstruct
	public void init() {
		begin(conversation);
	}
		
	//TODO:
	//@Observer("sumarioAltaConcluido")
	public void observarEventoImpressaoSumarioAlta(MpmAltaSumario pAltaSumario,
			String pAltaTipoOrigem, RapServidores servidorLogado) throws BaseException, JRException, SystemException, IOException {

		/**
		 * Lógica migrada na classe
		 * <code>RelatorioConclusaoSumarioAltaController</code>.
		 */
		this.renderAba(pAltaSumario, pAltaTipoOrigem);

		/**
		 * Item 17 da estória #5660 Mostrar a aba de Laudo Sus se o grupo
		 * convênio for do tipo SUS e se houver dados para o relatório.
		 */
		if (this.altaSumario.getConvenioSaudePlano() != null
				&& this.altaSumario.getConvenioSaudePlano().getConvenioSaude() != null
				&& DominioGrupoConvenio.S == this.altaSumario.getConvenioSaudePlano()
						.getConvenioSaude().getGrupoConvenio()) {
			if (this.prescricaoMedicaFacade
					.existeProcedimentosComLaudoJustificativaParaImpressao(this.altaSumario
							.getAtendimento())) {

				this.enviarPDFCups(DominioNomeRelatorio.SUMARIO_ALTA);
			}
		}
	}

	/**
	 * Metodo responsavel por inicializar as variaveis e fazer load do
	 * Relatorio.<br>
	 * Chamado toda vez que um click for efetudado na aba.<br>
	 * 
	 * @param altaSumario
	 * @param altaTipoOrigem
	 * @throws ApplicationBusinessException
	 */
	public void renderAba(MpmAltaSumario altaSumarioAtual, String pAltaTipoOrigem)
			throws ApplicationBusinessException {
		if (this.getAltaSumario() == null || (altaSumarioAtual != null && !this.getAltaSumario().getId().equals(altaSumarioAtual.getId()))) {
			this.setAltaSumario(altaSumarioAtual);
			this.setAltaTipoOrigem(pAltaTipoOrigem);
			this.loadRelatorio();
		}

	}

	/**
	 * Metodo responsavel por fazer load do Relatorio.<br>
	 * O metodo de render da Aba deve controlar para chamar apenas uma vez.<br>
	 * 
	 * @throws ApplicationBusinessException
	 * 
	 */
	protected void loadRelatorio() throws ApplicationBusinessException {
		super.setSeqAtendimento(this.getAltaSumario().getId().getApaAtdSeq());
		super.setApaSeq(this.getAltaSumario().getId().getApaSeq());
		super.setSeqp(this.getAltaSumario().getId().getSeqp());
		try {
			super.print();
		} catch (Exception e) {
			ApplicationBusinessException erro = new ApplicationBusinessException(
					RelatorioConclusaoAbaLaudoSusExceptionCode.ERRO_AO_BUSCAR_DADOS_RELATORIO);
			erro.initCause(e);
			throw erro;
		}
	}

	@Override
	public StreamedContent getRenderPdf() throws IOException,
			ApplicationBusinessException, JRException, SystemException, DocumentException {
		relatorioConclusaoSumarioAltaController.setCurrentTabIndex(3);
		relatorioConclusaoSumarioAltaController.renderAbas();
		DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * Envia o PDF para o CUPS.
	 * 
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	private void enviarPDFCups(DominioNomeRelatorio nomeArquivo)
			throws ApplicationBusinessException, JRException, SystemException, IOException {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			//apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * @param altaSumario
	 *            the altaSumario to set
	 */
	public void setAltaSumario(MpmAltaSumario altaSumario) {
		this.altaSumario = altaSumario;
	}

	/**
	 * @return the altaSumario
	 */
	public MpmAltaSumario getAltaSumario() {
		return altaSumario;
	}

	/**
	 * @param altaTipoOrigem
	 *            the altaTipoOrigem to set
	 */
	public void setAltaTipoOrigem(String altaTipoOrigem) {
		this.altaTipoOrigem = altaTipoOrigem;
	}

	/**
	 * @return the altaTipoOrigem
	 */
	public String getAltaTipoOrigem() {
		return altaTipoOrigem;
	}

}
