package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.prescricaoenfermagem.action.ManutencaoPrescricaoEnfermagemController.EnumTipoImpressaoEnfermagem;
import br.gov.mec.aghu.prescricaoenfermagem.vo.PrescricaoEnfermagemVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


/**
 * Controller para geração do 'Relatório Prescricão Enfermagem'.
 * 
 * @author gzapalaglio
 */

public class RelatorioPrescricaoEnfermagemController extends ActionReport {

	private static final String ELABORACAO_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-elaboracaoPrescricaoEnfermagem";

	private static final String PATH_RELATORIOS = "br/gov/mec/aghu/prescricaoenfermagem/report/";

	private static final Log LOG = LogFactory.getLog(RelatorioPrescricaoEnfermagemController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -6384845357208890049L;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;
	
	private PrescricaoEnfermagemVO prescricaoEnfermagemVO;
	
	

	private EnumTipoImpressaoEnfermagem tipoImpressao;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<PrescricaoEnfermagemVO> colecao;
	
	
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() throws BaseException, JRException, SystemException, IOException {		
		return "relatorioPrescricaoEnfermagem";
	}
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws JRException
	 */
	//@Observer("relatorioPrescricaoEnfermagem")
	public void directPrint(){
		
		// Quando tipo for SEM_IMPRESSAO não imprime o relatório fisicamente
		if (!EnumTipoImpressaoEnfermagem.SEM_IMPRESSAO.equals(tipoImpressao)) {
			try {
				DocumentoJasper documento = gerarDocumento();
	
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
						super.getEnderecoIPv4HostRemoto());
	
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_IMPRESSAO");
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				this.apresentarMsgNegocio(Severity.ERROR,
						"ERRO_GERAR_RELATORIO");
			}
		}

	}
	
	@Override
	public Collection<PrescricaoEnfermagemVO> recuperarColecao() {
		
		try {
			this.colecao = prescricaoEnfermagemFacade.obterRelatorioPrescricaoEnfermagem(prescricaoEnfermagemVO);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
		return colecao;
	}

	
	/**
	 * Método responsável por retornar os parâmetros utilizados no relatório.
	 * 
	 * @return o relatorio Jasper.
	 * @throws SystemException
	 */
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String caminhoComAprazamento = PATH_RELATORIOS + "ItensPrescricaoEnfermagemConfirmadosComAprazamento.jasper";
		String caminhoComAprazamentoPaisagem = PATH_RELATORIOS + "ItensPrescricaoEnfermagemConfirmadosComAprazamentoPaisagem.jasper";
		String titulo = "PRESCRIÇÃO DO PACIENTE - ENFERMAGEM";

		params.put("subRelatorioComAprazamento", recuperaRelatorioStream(caminhoComAprazamento));
		params.put("subRelatorioComAprazamentoPaisagem", recuperaRelatorioStream(caminhoComAprazamentoPaisagem));

		params.put("titulo", titulo);
		try {
			params.put("imagemLogoHospital", recuperarCaminhoLogo());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
		}

		return params;
	}
	
	private InputStream recuperaRelatorioStream(String caminhoComAprazamento) {
		return Thread.currentThread().getContextClassLoader().getResourceAsStream(caminhoComAprazamento);
	}
	
	@Override
	public String recuperarArquivoRelatorio() {

		try {
			AghParametros aghParamRelatorioPrescricaoEnfermagem = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_RELATORIO_PRESCRICAO_ENFERMAGEM_PAISAGEM);
			String RelatorioPrescricaoEnfermagemPaisagem = aghParamRelatorioPrescricaoEnfermagem.getVlrTexto();
			if ("S".equalsIgnoreCase(RelatorioPrescricaoEnfermagemPaisagem)) {
				return PATH_RELATORIOS + "ItensPrescricaoEnfermagemConfirmadosPaisagem.jasper";
			} else {
				return PATH_RELATORIOS + "ItensPrescricaoEnfermagemConfirmados.jasper";
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return "retorno";

	}
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws DocumentException 
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));
	}
	
	public String voltar(){
		return ELABORACAO_PRESCRICAO_ENFERMAGEM;
	}
	
	public PrescricaoEnfermagemVO getPrescricaoEnfermagemVO() {
		return prescricaoEnfermagemVO;
	}

	public void setPrescricaoEnfermagemVO(
			PrescricaoEnfermagemVO prescricaoEnfermagemVO) {
		this.prescricaoEnfermagemVO = prescricaoEnfermagemVO;
	}

	public EnumTipoImpressaoEnfermagem getTipoImpressao() {
		return tipoImpressao;
	}

	public void setTipoImpressao(EnumTipoImpressaoEnfermagem tipoImpressao) {
		this.tipoImpressao = tipoImpressao;
	}
}


