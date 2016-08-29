package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AltasPorPeriodoVO;
import br.gov.mec.aghu.model.AghParametros;
import net.sf.jasperreports.engine.JRException;


/**
 * @author felipe_marinho
 */
public class RelatorioAltasPorPeriodoController  extends ActionReport {

	private static final long serialVersionUID = 3967522972046913795L;	
	private static final Log LOG = LogFactory.getLog(RelatorioAltasPorPeriodoController.class);
	private static final String RELATORIO_ALTAS_POR_PERIODO = "relatorioAltasPorPeriodo";
	
	private static final String RELATORIO_ALTAS_POR_PERIODO_PDF = "relatorioAltasPorPeriodoPdf";

	private Date dataAltaInicial;
	
	private Date dataAltaFinal;
	
	private Date hoje = new Date();
	
	SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
	SimpleDateFormat sdf_2 = new SimpleDateFormat("dd/MM/yyyy");
	
	@EJB
	private IInternacaoFacade internacaoFacade; 
	
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IParametroFacade parametroFacade;

	private List<AltasPorPeriodoVO> listaAltasPorPeriodoVO = new ArrayList<>();
	
	@PostConstruct
	public void inicio() {
		begin(conversation);
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {

		try {
			if(validarPesquisa()){
				if (this.listaAltasPorPeriodoVO.isEmpty()){
					recuperarDadosParaORelatorio();					
				}
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());
				apresentarMsgNegocio("MENSAGEM_SUCESSO_IMPRESSAO");
			}
			
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (UnknownHostException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		} catch (JRException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public String imprimirRelatorio(){
		
		try {
			this.directPrint();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (SistemaImpressaoException e) {
			LOG.error(e.getMessage(),e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return null;
	}
	
	public String visualizarRelatorio() throws ApplicationBusinessException {
		if (validarPesquisa()) {
			recuperarDadosParaORelatorio();
			if(!this.listaAltasPorPeriodoVO.isEmpty()){
				return RELATORIO_ALTAS_POR_PERIODO_PDF;				
			}
		}		
		return null;
	}

	private void recuperarDadosParaORelatorio() {
		try {
			listaAltasPorPeriodoVO = internacaoFacade.pesquisarAltasNoPeriodo(this.dataAltaInicial, this.dataAltaFinal);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_RELATORIO_VAZIO_ALTAS_PELO_PERIODO");
		}
	}

	
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {	
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));		
		
	}	
	
	private boolean validarPesquisa() throws ApplicationBusinessException {
		return validarDatas();
	}
	
	private boolean validarDatas() {
		if(DateUtil.validaDataMaior(dataAltaInicial, dataAltaFinal)) {
			this.apresentarMsgNegocio(Severity.ERROR, "AAC_00145");
			return Boolean.FALSE;
		}
		return true;
	}
	
	@Override
	public List<AltasPorPeriodoVO> recuperarColecao() throws ApplicationBusinessException {
		return listaAltasPorPeriodoVO;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/internacao/report/relatorioAltasPorPeriodo.jasper";
	}
	
	public boolean isDataInicialPreenchida() {
		if (dataAltaInicial != null) {
			return true;
		}
		return false;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		
		try {
			AghParametros hospital = parametroFacade.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", hospital.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("nomeRelatorio", "AINR_ALTAS_PERIODO");
		params.put("tituloRelatorio", "Altas registradas entre ".concat(sdf_2.format(this.dataAltaInicial).concat(" à ").concat(sdf_2.format(this.dataAltaFinal))));
		
		return params;
	}
	
	public Date getDataAltaInicial() {
		return dataAltaInicial;
	}

	public void setDataAltaInicial(Date dataAltaInicial) {
		this.dataAltaInicial = dataAltaInicial;
	}

	public Date getDataAltaFinal() {
		return dataAltaFinal;
	}

	public void setDataAltaFinal(Date dataAltaFinal) {
		dataAltaFinal = DateUtil.adicionaHoras(dataAltaFinal, 23);
		dataAltaFinal = DateUtil.adicionaMinutos(dataAltaFinal, 59);
		dataAltaFinal = DateUtil.adicionaSegundos(dataAltaFinal, 59);
		this.dataAltaFinal = dataAltaFinal;
	}

	public String getHoje() {
		return sdf_2.format(this.hoje);
	}

	public void setHoje(Date hoje) {
		this.hoje = hoje;
	}

	public String voltar() {
		listaAltasPorPeriodoVO = new ArrayList<>();
		return RELATORIO_ALTAS_POR_PERIODO;
	}
	
}