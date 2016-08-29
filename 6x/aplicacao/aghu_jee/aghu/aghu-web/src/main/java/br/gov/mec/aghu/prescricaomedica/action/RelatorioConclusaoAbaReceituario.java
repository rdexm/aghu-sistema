package br.gov.mec.aghu.prescricaomedica.action;

import java.io.IOException;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.vo.ReceitaMedicaVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Controller da aba de <b>Receituario</b> da tela de Relatorio de Conclusao do
 * Sumario Alta.<br>
 * 
 * @author rcorvalao
 * 
 */

public class RelatorioConclusaoAbaReceituario extends RelatorioReceitaMedicaController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5813409792635577046L;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private MpmAltaSumario altaSumario;
	private String altaTipoOrigem;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	//TODO:
	//@Observer("sumarioAltaConcluido")
	public void observarEventoImpressaoSumarioAlta(MpmAltaSumario pAltaSumario,
			String pAltaTipoOrigem, RapServidores servidorLogado) throws BaseException {

		/**
		 * Lógica migrada na classe
		 * <code>RelatorioConclusaoSumarioAltaController</code>.
		 */
		this.renderAba(pAltaSumario, pAltaTipoOrigem);

		/**
		 * Regras para impressão ou não do PDF das Receitas, se existir conteúdo
		 * para mesma.
		 */
		List<ReceitaMedicaVO> listaReceitaMedicaVO = ambulatorioFacade.imprimirReceita(
				altaSumario, false);
		if (listaReceitaMedicaVO != null && !listaReceitaMedicaVO.isEmpty()) {

			// Obtem dados do relatório.
			loadRelatorio();

			try {
				DocumentoJasper documento = gerarDocumento();

				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());

				//apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				apresentarMsgNegocio(Severity.ERROR,
						"ERRO_GERAR_RELATORIO");
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
	 */
	public void renderAba(MpmAltaSumario altaSumarioAtual, String pAltaTipoOrigem) {
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
	 */
	protected void loadRelatorio() {
		super.setApaAtdSeq(this.getAltaSumario().getId().getApaAtdSeq());
		super.setApaSeq(this.getAltaSumario().getId().getApaSeq());
		super.setSeqp(this.getAltaSumario().getId().getSeqp());

		super.print();
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws MECBaseException
	 * @throws DocumentException 
	 */
	@Override
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException,
			JRException, SystemException, DocumentException {

		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param receitaSeq, imprimiu
	 */
	//TODO:
	//@Observer("imprimirReceitaMedica")
	public void imprimirReceitaMedica(Long receitaSeq, Boolean imprimiu) throws BaseException,
			JRException, SystemException, IOException {
		setReceitaSeq(receitaSeq);
		setImprimiu(imprimiu);
		directPrint();
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
