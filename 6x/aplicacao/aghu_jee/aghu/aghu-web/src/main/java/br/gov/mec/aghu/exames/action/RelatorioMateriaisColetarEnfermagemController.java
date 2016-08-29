package br.gov.mec.aghu.exames.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.vo.MateriaisColetarEnfermagemVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;

/**
 * Controller para geração do relatório 'Materiais a coletar pela enfermagem'.
 * 
 * @author lalegre
 */

public class RelatorioMateriaisColetarEnfermagemController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	private static final long serialVersionUID = -9218861415949602616L;
	
	private static final Log LOG = LogFactory.getLog(RelatorioMateriaisColetarEnfermagemController.class);
	
	private static final String RELATORIO_MATERIAIS_COLETAR_ENFERMAGEM = "relatorioMateriaisColetarEnfermagem";
	private static final String RELATORIO_MATERIAIS_COLETAR_ENFERMAGEM_PDF = "relatorioMateriaisColetarEnfermagemPdf";

	/**
	 * Filtro de pesquisa
	 */
	private AghUnidadesFuncionais unidadeFuncional;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<MateriaisColetarEnfermagemVO> colecao = new ArrayList<MateriaisColetarEnfermagemVO>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		if (unidadeFuncional == null || unidadeFuncional.getSeq() == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_BLANK");
		}
		
		this.colecao = examesFacade.pesquisarRelatorioMateriaisColetaEnfermagem(this.unidadeFuncional);
	    
		if (this.colecao != null && !this.colecao.isEmpty()){
			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
			return RELATORIO_MATERIAIS_COLETAR_ENFERMAGEM_PDF;
		} else {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_DADOS_NAO_ENCONTRADOS");
			return null;
		}
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {
			this.colecao = examesFacade.pesquisarRelatorioMateriaisColetaEnfermagem(this.unidadeFuncional);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			return;
		}

		try {
			if (this.colecao != null && !this.colecao.isEmpty()){
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
				//this.imprimirRelatorioCopiaSeguranca(true);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			}else{
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_DADOS_NAO_ENCONTRADOS");
			}
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	

	public String voltar(){
		return RELATORIO_MATERIAIS_COLETAR_ENFERMAGEM;
	}

	@Override
	public Collection<MateriaisColetarEnfermagemVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_MAT_COLETA_ENF");
		params.put("tituloRelatorio","Materiais a coletar em " + DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY)+ " pelo paciente");
		
		if (colecao != null && !colecao.isEmpty()) {
			params.put("totalRegistros", colecao.size() - 1);
		}
		
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/report/relatorioMateriaisColetarEnfermagem.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	/**
	 * Pesquisa Suggestion Box
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalPorCodigoEDescricao(String param) {
		return this.aghuFacade.pesquisarUnidadeFuncionalPorUnidEmergencia(param, true);
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

}
