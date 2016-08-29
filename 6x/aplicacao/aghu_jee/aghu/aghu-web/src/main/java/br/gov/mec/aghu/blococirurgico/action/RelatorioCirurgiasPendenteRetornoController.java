package br.gov.mec.aghu.blococirurgico.action;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Calendar;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.NomeArquivoRelatorioCrgVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiasPendenteRetornoVO;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioTipoPendenciaCirurgia;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioCirurgiasPendenteRetornoController extends ActionReport {

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
	
	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasPendenteRetornoController.class);
	
	private static final long serialVersionUID = -4336151264696326176L;
	
	private static final String EXTENSAO_CSV = ".csv";
	private static final String CONTENT_TYPE = "text/csv";
	private static final int BUFFER_SIZE_EQ_1M = 1024 * 1024;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Boolean gerouArquivo;
	private String fileName;
	private AghUnidadesFuncionais unidadeFuncional;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private Date dataInicial;
	private Date dataFinal;
	private DominioTipoPendenciaCirurgia tipoPendenciaCirurgia;
	private Boolean habilitaProcedimentoCirurgico = Boolean.TRUE;
	
	
	private String nomeMicrocomputador;

	private String nomeRelatorio;
	
	private static final String RELATORIO_CIRURGIAS_PENDENTES_RETORNO_PDF = "relatorioCirurgiasPendenteRetornoPdf";
	private static final String RELATORIO_CIRURGIAS_PENDENTES_RETORNO = "relatorioCirurgiasPendenteRetorno";

		

	@PostConstruct
	protected void init() {
		begin(conversation, true);
		iniciar();
	}
	
	/**
	 * Método executado ao abrir a tela
	 */
	public String iniciar() {
		if (unidadeFuncional == null) {
			try {
				
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção capturada:", e);
				}
				
				unidadeFuncional = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);				
			}
		}
		return "";
	}		
	
	
	public RelatorioCirurgiasPendenteRetornoController() {
		Calendar cal = Calendar.getInstance(); 
		Date dataAtual = cal.getTime();
		
		cal.add(Calendar.DAY_OF_MONTH, -1);
		Date dataOntem = cal.getTime();
		
		dataInicial = dataOntem;
		dataFinal = dataAtual;
	}
	
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, false),pesquisarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long pesquisarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return blocoCirurgicoFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, false);
	}
	
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgico(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro),pesquisarProcedimentoCirurgicoCount(filtro));
	}
	
	public Long pesquisarProcedimentoCirurgicoCount(String filtro) {
		return this.blocoCirurgicoFacade.listarMbcProcedimentoCirurgicosCount(filtro);
	}
	
	public String visualizarRelatorio() {
		if (!validarTipoPendencia()) {
			return null;
		} else {
			return RELATORIO_CIRURGIAS_PENDENTES_RETORNO_PDF;	
		}
	}
	
	public String voltar(){
		return RELATORIO_CIRURGIAS_PENDENTES_RETORNO;
	}
	
	public void limpar() {
		dataInicial = null;
		dataFinal = null;
		gerouArquivo = false;
		fileName = null;
		unidadeFuncional = null;
		procedimentoCirurgico = null;
		tipoPendenciaCirurgia = null;
	}
	
	
	
	private Boolean validarTipoPendencia() {
		try {
			blocoCirurgicoFacade.validarTipoPendenciaRelatorioCirurgiasPendenteRetorno(tipoPendenciaCirurgia);
			return Boolean.TRUE;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		return Boolean.FALSE;
	}
	
	public void gerarCSV() {
		
		if (!validarTipoPendencia()) {
			return;
		}
	
		try {
			Integer pciSeq = null;
			if (procedimentoCirurgico != null) {
				pciSeq = procedimentoCirurgico.getSeq();
			}
			
			NomeArquivoRelatorioCrgVO nomeArquivoRelatorioCrgVO = 
					blocoCirurgicoFacade.gerarRelatorioCirurgiasPendenteRetornoCSV(tipoPendenciaCirurgia, 
							unidadeFuncional.getSeq(), dataInicial, dataFinal, pciSeq, EXTENSAO_CSV);
			
			fileName = nomeArquivoRelatorioCrgVO.getFileName();
			nomeRelatorio = nomeArquivoRelatorioCrgVO.getNomeRelatorio();
			
			gerouArquivo = true;

		} catch (IOException e) {
			apresentarExcecaoNegocio(new ApplicationBusinessException(
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
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
	public Collection<RelatorioCirurgiasPendenteRetornoVO> recuperarColecao() {
		Integer pciSeq = null;

		if (procedimentoCirurgico != null) {
			pciSeq = procedimentoCirurgico.getSeq();
		}

		List<RelatorioCirurgiasPendenteRetornoVO> lista = null;
				
		try {
			lista = blocoCirurgicoFacade.pesquisarCirurgiasPendenteRetorno(
					tipoPendenciaCirurgia, unidadeFuncional.getSeq(), dataInicial, dataFinal, pciSeq);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);			
		}
		
		return lista;
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
	@Override
	public void directPrint() {
		
		if (!validarTipoPendencia()) {
			return;
		}
	
		try {
			DocumentoJasper documento = gerarDocumento();

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
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioCirurgiasPendenteRetorno.jasper";
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
	public StreamedContent getRenderPdf()throws IOException,
			ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		final Map<String, Object> params = new HashMap<String, Object>();

		String nomeHospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		String descricaoUnidadeFuncional = unidadeFuncional.getDescricao();
		
		params.put("nomeHospital", (nomeHospital != null) ? nomeHospital : "");
		params.put("dataInicial", dataInicial);
		params.put("dataFinal", dataFinal);
		params.put("descricaoUnidadeFuncional", descricaoUnidadeFuncional);
		if (procedimentoCirurgico != null) {
			params.put("descricaoProcedimentoCirurgico", procedimentoCirurgico.getDescricao());
		}
	
		return params;
	}
	
	public void verificarProcedimento() {
		if (DominioTipoPendenciaCirurgia.NOTA_CONSUMO.equals(tipoPendenciaCirurgia)) {
			procedimentoCirurgico = null;
			habilitaProcedimentoCirurgico = Boolean.FALSE;
		} else {
			habilitaProcedimentoCirurgico = Boolean.TRUE;
		}
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

	public DominioTipoPendenciaCirurgia getTipoPendenciaCirurgia() {
		return tipoPendenciaCirurgia;
	}

	public void setTipoPendenciaCirurgia(
			DominioTipoPendenciaCirurgia tipoPendenciaCirurgia) {
		this.tipoPendenciaCirurgia = tipoPendenciaCirurgia;
	}

	public Boolean getHabilitaProcedimentoCirurgico() {
		return habilitaProcedimentoCirurgico;
	}

	public void setHabilitaProcedimentoCirurgico(
			Boolean habilitaProcedimentoCirurgico) {
		this.habilitaProcedimentoCirurgico = habilitaProcedimentoCirurgico;
	}
	
}
