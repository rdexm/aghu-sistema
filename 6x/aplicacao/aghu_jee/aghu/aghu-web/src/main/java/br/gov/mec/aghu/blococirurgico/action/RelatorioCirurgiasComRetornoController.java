package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioCirurgiaComRetornoVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

public class RelatorioCirurgiasComRetornoController extends	ActionReport {

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
	private static final long serialVersionUID = -1006442888234676013L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IParametroFacade parametroFacade; 
	
	@EJB
	private IAghuFacade aghuFacade;
	
	private Date dataInicio;
	private Date dataFim;
	private AghUnidadesFuncionais unidade;
	private MbcProcedimentoCirurgicos procedimento;
	private FatConvenioSaude convenio;
	
	private Boolean gerouArquivo;
	private String fileName;

	private static final Log LOG = LogFactory.getLog(RelatorioCirurgiasComRetornoController.class);
	
	private static final String RELATORIO = "relatorioCirurgiasComRetorno";
	private static final String RELATORIO_PDF = "relatorioCirurgiasComRetornoPdf";
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioCirurgiasComRetorno.jasper";
	}

	@Override
	public Collection<RelatorioCirurgiaComRetornoVO> recuperarColecao() {
		try {
			return blocoCirurgicoFacade.listarCirurgiasComRetorno(unidade.getSeq(), (convenio!=null)?convenio.getCodigo():null, (procedimento!=null)?procedimento.getSeq():null, dataInicio, dataFim);
		} catch (BaseException e) {
			LOG.error("Excecao Capturada: ",e);
		}
		
		return null;
	}

	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		
		params.put("hospitalLocal", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());

		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yy HH:mm"));
		params.put("dataInicial", DateUtil.obterDataFormatada(dataInicio, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("dataFinal", DateUtil.obterDataFormatada(dataFim, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("observacao", this.getBundle().getString("LABEL_RELATORIO_PDF_OBSERVACAO"));
		params.put("textoData", this.getBundle().getString("LABEL_RELATORIO_PDF_TEXTO_DATA"));
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/report/"); 
		params.put("quantidadeComRetorno", Long.valueOf(0));
		params.put("quantidadeSemRetorno", blocoCirurgicoFacade.quantidadeCirurgiasSemRetorno(unidade.getSeq(), (convenio!=null)?convenio.getCodigo():null, (procedimento!=null)?procedimento.getSeq():null, dataInicio, dataFim));
		params.put("unidade", "BLOCO CIRÚRGICO");		

		try {
			AghParametros convenioSus = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_CONVENIO_SUS);
			params.put("convenio", convenioSus.getVlrNumerico().shortValue());		
		} catch(ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		
		return params;
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));			
	}

	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
		try {
			blocoCirurgicoFacade.validarIntervaloDatasPesquisa(dataInicio, dataFim);
			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));	
			return RELATORIO_PDF;
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public String voltar() {
		return RELATORIO;
	}
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(
					Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(
					Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public void gerarCSV() {
		try {
			
			fileName = blocoCirurgicoFacade.gerarRelatorioCirurgiasComRetornoCSV(unidade.getSeq(), (convenio!=null)?convenio.getCodigo():null, (procedimento!=null)?procedimento.getSeq():null, dataInicio, dataFim);
			gerouArquivo = true;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
		}
	}

	public void dispararDownload(){
		if(fileName != null){
			try {
				download(fileName,"RELATORIO_CIRURGIAS_COM_RETORNO.csv");
				gerouArquivo = false;
				fileName = null;
				
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV, e, e.getLocalizedMessage()));
			}
		}
	}

	
	public void limpar() {
		this.procedimento = null;
		this.unidade = null;
		this.dataInicio = null;
		this.dataFim = null;
		this.convenio = null;
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS,  false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}

	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentoCirurgico(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro),pesquisarProcedimentoCirurgicoCount(filtro));
	}
	
	public Long pesquisarProcedimentoCirurgicoCount(String filtro) {
		return this.blocoCirurgicoFacade.listarMbcProcedimentoCirurgicosCount(filtro);
	}

	public List<FatConvenioSaude> pesquisarConvenioSaude(final String objPesquisa) {
		return this.faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricao((String) objPesquisa);
	}
	
	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public FatConvenioSaude getConvenio() {
		return convenio;
	}

	public void setConvenio(FatConvenioSaude convenio) {
		this.convenio = convenio;
	}

	public MbcProcedimentoCirurgicos getProcedimento() {
		return procedimento;
	}

	public void setProcedimento(MbcProcedimentoCirurgicos procedimento) {
		this.procedimento = procedimento;
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
}
