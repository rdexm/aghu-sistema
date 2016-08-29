package br.gov.mec.aghu.blococirurgico.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProcedAgendPorUnidCirurgicaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioProcedAgendPorUnidCirurgicaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 2937720133332887718L;
	
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE = "text/csv";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Boolean gerouArquivo;
	private String fileName;
	private AghUnidadesFuncionais unidadeFuncional;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private Date dataInicial;
	private Date dataFinal;

	private String nomeRelatorio;
	
	
	private static final String RELATORIO_PROC_AGEND_UNID_CIR_PDF = "relatorioProcedAgendPorUnidCirurgicaPdf";
	private static final String RELATORIO_PROC_AGEND_UNID_CIR = "relatorioProcedAgendPorUnidCirurgica";

		

	@PostConstruct
	protected void init() {
		begin(conversation, true);		
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false),pesquisarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long pesquisarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgico(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro),pesquisarProcedimentoCirurgicoCount(filtro));
	}
	
	public Long pesquisarProcedimentoCirurgicoCount(String filtro) {
		return this.blocoCirurgicoFacade.listarMbcProcedimentoCirurgicosCount(filtro);
	}
	
	public String visualizarRelatorio() {
		if (validarIntervaloEntreDatas(dataInicial, dataFinal)) {
			return RELATORIO_PROC_AGEND_UNID_CIR_PDF;
		} else {
			return null;
		}
	}
	
	public void limpar() {
		dataInicial = null;
		dataFinal = null;
		gerouArquivo = false;
		fileName = null;
		unidadeFuncional = null;
		procedimentoCirurgico = null;
	}
	
	private Boolean validarIntervaloEntreDatas(Date dataInicial, Date dataFinal) {
		try {
			blocoCirurgicoFacade.validarIntervaloEntreDatasRelatorioProcedAgendPorUnidCirurgica(dataInicial, dataFinal);
			return Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return Boolean.FALSE;
		}
	}
	
	public void gerarCSV() {
		if (!validarIntervaloEntreDatas(dataInicial, dataFinal)) {
			gerouArquivo = false;
			return;
		}
			
		try {
			Integer pciSeq = null;
			if (procedimentoCirurgico != null) {
				pciSeq = procedimentoCirurgico.getSeq();
			}
			
			NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = 
					blocoCirurgicoFacade.gerarRelatorioProcedAgendPorUnidCirurgicaCSV(unidadeFuncional.getSeq(), 
					pciSeq, dataInicial, dataFinal, EXTENSAO_CSV);
			
			fileName = nomeArquivoRelatorioCrgVO.getFileName();
			nomeRelatorio = nomeArquivoRelatorioCrgVO.getNomeRelatorio();
			
			gerouArquivo = true;

		} catch (IOException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
		
	}
	
	public void dispararDownload() {
        FacesContext facesContext = null;
        HttpServletResponse response = null;
        ServletOutputStream out = null;
        FileInputStream in = null;
        byte[] buffer = null;
        int len = 0;

        facesContext = FacesContext.getCurrentInstance();
        response = (HttpServletResponse) facesContext.getExternalContext().getResponse();
        response.reset();
        response.setContentType(CONTENT_TYPE);
        response.setContentLength((int) (new File(fileName)).length());
        response.addHeader("Content-disposition", "attachment; filename=\"" + nomeRelatorio + "\"");
        response.addHeader("Cache-Control", "no-cache");             
        buffer = new byte[BUFFER_SIZE_EQ_1M];
        // committing status and headers
        try {
            response.flushBuffer();
            out = response.getOutputStream();
            in = new FileInputStream(new File(fileName));
            while ((len = in.read(buffer)) >= 0) {
                out.write(buffer, 0, len);            	
            }
    		out.flush();
    		out.close();
    		in.close();
    		facesContext.responseComplete();        	
    	} catch (IOException e) {
    		apresentarExcecaoNegocio(new ApplicationBusinessException(
    				AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
    	}
        
        gerouArquivo = false;
	}
	
	@Override
	public Collection<RelatorioProcedAgendPorUnidCirurgicaVO> recuperarColecao() {
		Integer pciSeq = null;

		if (procedimentoCirurgico != null) {
			pciSeq = procedimentoCirurgico.getSeq();
		}

		return blocoCirurgicoFacade
				.pesquisarCirurgiaAgendadaProcedPrincipalAtivoPorUnfSeqPciSeqDtInicioDtFim(
						unidadeFuncional.getSeq(), pciSeq, dataInicial, dataFinal);
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() {

		if (!validarIntervaloEntreDatas(dataInicial, dataFinal)) {
			return;
		}
		
		try {
			DocumentoJasper	 documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);			
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public String recuperarArquivoRelatorio()  {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioProcedAgendPorUnidCirurgica.jasper";
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
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	public Map<String, Object> recuperarParametros() {

		final Map<String, Object> params = new HashMap<String, Object>();

		String nomeHospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		String descricaoUnidadeFuncional = unidadeFuncional.getDescricao();
		
		params.put("nomeHospital", (nomeHospital != null) ? nomeHospital : "");
		params.put("dataInicial", dataInicial);
		params.put("dataFinal", dataFinal);
		params.put("descricaoUnidadeFuncional", descricaoUnidadeFuncional);
	
		return params;
	}	
	
	public String voltar(){
		return RELATORIO_PROC_AGEND_UNID_CIR;
	}
	
	public Boolean getGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(Boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(
			MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
