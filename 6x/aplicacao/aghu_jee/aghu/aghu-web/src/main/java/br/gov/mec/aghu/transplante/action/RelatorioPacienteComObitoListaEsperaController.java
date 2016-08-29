package br.gov.mec.aghu.transplante.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.event.ActionEvent;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.exception.AghuCSVBaseException.AghuCSVBaseExceptionExceptionCode;
import br.gov.mec.aghu.dominio.DominioNomeRelatorio;
import br.gov.mec.aghu.dominio.DominioOrdenacaoPesquisaFilaTransplante;
import br.gov.mec.aghu.dominio.DominioSituacaoTmo;
import br.gov.mec.aghu.dominio.DominioTipoAlogenico;
import br.gov.mec.aghu.dominio.DominioTipoOrgao;
import br.gov.mec.aghu.dominio.DominioTipoTransplanteCombo;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.transplante.business.ITransplanteFacade;
import br.gov.mec.aghu.transplante.vo.FiltroTempoPermanenciaListVO;
import br.gov.mec.aghu.transplante.vo.RelatorioExtratoTransplantesPacienteVO;
import br.gov.mec.aghu.core.dominio.DominioMimeType;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

/**
 * 
 * @author vandre.sandes
 *
 */

public class RelatorioPacienteComObitoListaEsperaController extends ActionReport {



	/**
	 * 
	 */
	private static final long serialVersionUID = -1282771232768050819L;
	private static final Log LOG = LogFactory.getLog(RelatorioPacienteComObitoListaEsperaController.class);
	private DominioTipoOrgao tipoOrgao;
	private DominioTipoTransplanteCombo tipoTransplante;
	private DominioOrdenacaoPesquisaFilaTransplante ordenacao;
	private DominioSituacaoTmo tipoTMO;
	private DominioTipoAlogenico tipoAlogenico;
	


	private static final String REDIRECT_RELATORIO_PACIENTE_COM_OBTO_PDF = "transplante-relatorioPacienteComObitoListaEsperaPdf";
	private static final String REDIRECT_RELATORIO_PACIENTE_COM_OBTO = "transplante-relatorioPacienteComObitoListaEspera";
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente";

	private boolean tipoTransplanteTMO;
	private boolean tipoTransplanteOrgaos;
	private boolean tipoTmoAlogenico;
	private boolean gerouArquivo;

	private StreamedContent media;
	
	private List<RelatorioExtratoTransplantesPacienteVO> colecao = null;

	@EJB
	private ITransplanteFacade transplanteFacade;

	@EJB
	private IParametroFacade parametroFacade;

	
	private FiltroTempoPermanenciaListVO filtro = new FiltroTempoPermanenciaListVO();

	
	public String redirecionarPesquisaFonetica() {
		return PESQUISA_FONETICA;
	}

	@PostConstruct
	public void inicializar() {
		this.begin(conversation);
		setTipoTransplanteTMO(true);
		setTipoTransplanteOrgaos(true);
		setTipoTmoAlogenico(true);
		fileName = null;
		gerouArquivo = false;
		filtro.setOrdenacao(DominioOrdenacaoPesquisaFilaTransplante.permanencia);
	}

	public String limpar() {
		this.filtro = new FiltroTempoPermanenciaListVO();
		filtro.setOrdenacao(DominioOrdenacaoPesquisaFilaTransplante.permanencia);
		this.gerouArquivo = false;
		this.fileName = null;
		//Desabilita combos
		this.tipoTransplanteTMO = true;
		this.tipoTransplanteOrgaos = true;
		this.tipoTmoAlogenico = true;
		

		return REDIRECT_RELATORIO_PACIENTE_COM_OBTO;
	}

	/*
	*//**
	 * Dispara o download para o arquivo CSV do relatório.
	 */
	private String fileName;
	
	private enum RelatorioPacienteComObitoListaEsperaControllerExceptionCode implements BusinessExceptionCode{
		PESQUISA_VAZIA,TIPO_TRANSPLANTE_OBRIGATORIO,MESSAGE_VALIDA_DATA_INICIAL,MESSAGE_VALIDA_DATA_FINAL;
	}

	public void dispararDownload() {
		this.gerouArquivo = false;
		if (fileName != null) {
			try {
				this.download(
						fileName,
						DominioNomeRelatorio.MTXR_OBITO_ESPERA_TRANSPLANTES.toString()+"_"+DateUtil.dataToString(new Date(), "yyyyMMddHHmm") + DominioNomeRelatorio.EXTENSAO_CSV,
						DominioMimeType.CSV.getContentType());
				download(fileName);
				fileName = null;
				this.gerouArquivo = false;
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
						e, e.getLocalizedMessage()));
			}
		} else {
			apresentarMsgNegocio(Severity.ERROR,
					AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV
							.toString());
		}

	}
	
	
	public StreamedContent getRenderPdf() throws JRException, IOException, DocumentException{
		try {
			DocumentoJasper documento = gerarDocumento();
			media =  this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
			return media;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}
	

	public boolean gerarDadosRelatorio() {
		
		AghParametros valorMaximo;
		AghParametros valorMasSeq;
		try {
			valorMaximo = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_REL_OBT_ESP_TRANSP_DIAS);
						
			if(validaCamposTela()){
				String valida = this.transplanteFacade.validarDatas(this.filtro,valorMaximo.getVlrNumerico().intValue());
				valorMasSeq = parametroFacade.obterAghParametro(AghuParametrosEnum.P_AGHU_COD_MAS_OBITO);
	
				if (!"".equals(valida)) {
					apresentarMsgNegocio(Severity.ERROR, valida);
					return false;
				}
	//			valorMasSeq.getVlrNumerico().intValue();
				this.colecao = this.transplanteFacade.pesquisarPacienteComObtitoListaTranplante(filtro, valorMasSeq.getVlrNumerico().intValue());
			}
			else{
				return false;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		boolean flagOrdemPermanencia = false;
		flagOrdemPermanencia = verificarOrdenacaoPermanencia(flagOrdemPermanencia);
		
		if (this.colecao == null || this.colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,RelatorioPacienteComObitoListaEsperaControllerExceptionCode.PESQUISA_VAZIA.toString());
			return false;
		}

		this.colecao= transplanteFacade.formatarExtratoPacienteTransplante(colecao);
		
		this.colecao = transplanteFacade.removerRepetidosLista(colecao);

		return true;
	}

	private boolean validaCamposTela() {
		boolean pesquisar=true;
		if (filtro.getDataInicio()==null) {
			apresentarMsgNegocio(Severity.ERROR,RelatorioPacienteComObitoListaEsperaControllerExceptionCode.MESSAGE_VALIDA_DATA_INICIAL.toString());
			pesquisar= false;
		}
		if (filtro.getDataFim()==null) {
			apresentarMsgNegocio(Severity.ERROR,RelatorioPacienteComObitoListaEsperaControllerExceptionCode.MESSAGE_VALIDA_DATA_FINAL.toString());
			pesquisar= false;
		}
		if (filtro.getTipoTransplante()==null) {
			apresentarMsgNegocio(Severity.ERROR,RelatorioPacienteComObitoListaEsperaControllerExceptionCode.TIPO_TRANSPLANTE_OBRIGATORIO.toString());
			pesquisar= false;
		}
		return pesquisar;
	}

	public String print() throws JRException, IOException, DocumentException,
			ApplicationBusinessException {
		try {

			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(
					documento.getPdfByteArray(Boolean.TRUE)));
			return REDIRECT_RELATORIO_PACIENTE_COM_OBTO_PDF;
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public void directPrint() {
		try {
			if(this.gerarDadosRelatorio()){
				DocumentoJasper documento = gerarDocumento();
					
				this.sistemaImpressao.imprimir(documento.getJasperPrint(),
				super.getEnderecoIPv4HostRemoto());

				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
			}
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public void gerarCSV() {
		this.gerouArquivo = false;
			
		if(this.gerarDadosRelatorio()){

			try {
				this.fileName = this.transplanteFacade
						.gerarRelatorioPacientesComObitoListaEsperaTranplenteCSV(colecao);
				this.gerouArquivo = true;
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			} catch (IOException e) {
				apresentarExcecaoNegocio(new BaseException(
						AghuCSVBaseExceptionExceptionCode.ERRO_GERACAO_ARQUIVO_CSV,
						e, e.getLocalizedMessage()));
			}
		}
	}


	public String voltar() {
		fileName = null;
		gerouArquivo = false;
		filtro.setOrdenacao(DominioOrdenacaoPesquisaFilaTransplante.permanencia);
		return REDIRECT_RELATORIO_PACIENTE_COM_OBTO;
	}

	public String visualizarImpressao() {
		if(this.gerarDadosRelatorio()){
			return REDIRECT_RELATORIO_PACIENTE_COM_OBTO_PDF;
		}else{
			return REDIRECT_RELATORIO_PACIENTE_COM_OBTO;
		}
	}

	private boolean verificarOrdenacaoPermanencia(boolean flagOrdemPermanencia) {
		if (this.filtro.getOrdenacao() != null
				&& this.filtro.getOrdenacao().equals(
						DominioOrdenacaoPesquisaFilaTransplante.permanencia)) {
			this.filtro
					.setOrdenacao(DominioOrdenacaoPesquisaFilaTransplante.dataIngresso);
			flagOrdemPermanencia = true;
		}
		return flagOrdemPermanencia;
	}

	@Override
	protected Collection<?> recuperarColecao()
			throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	protected String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/transplante/report/relatorioPacienteComObitoListaEspera.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		AghParametros hospital = null;
		try {
			hospital = this.parametroFacade
					.obterAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		Map<String, Object> params = new HashMap<String, Object>();
		if (hospital != null) {
			params.put("hospitalLocal", hospital.getVlrTexto());
		}
		params.put("nomeRelatorio", DominioNomeRelatorio.MTXR_OBITO_ESPERA_TRANSPLANTES);
		params.put("tituloRelatorio", DominioNomeRelatorio.MTXR_OBITO_ESPERA_TRANSPLANTES.getDescricao());
		params.put("quantidade", colecao.size());
		//params.put("SUBREPORT_DIR", "br/gov/mec/aghu/transplante/report/");
		return params;
	}

	public void verificaTipoTransplante() {
		boolean tipoTrans = this.transplanteFacade
				.verificaTipoTransplanteRelatorioFila(this.filtro);

		if (filtro.getTipoTransplante() == null) {
			setTipoTransplanteTMO(true);
			setTipoTransplanteOrgaos(true);
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoTMO(null);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoOrgao(null);
		} else {
			setTipoTransplanteTMO(tipoTrans);
			setTipoTransplanteOrgaos(!tipoTrans);
			if(filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.T)){
				this.filtro.setTipoOrgao(null);
			}else if(filtro.getTipoTransplante().equals(DominioTipoTransplanteCombo.O)){
				this.filtro.setTipoOrgao(null);
				this.filtro.setTipoAlogenico(null);
				this.filtro.setTipoTMO(null);
			}
			
		}
	}

	public void verificaTipoTMO() {
		boolean tipoTmo = this.transplanteFacade
				.verificaTipoTMOFilaTransplante(this.filtro);
		if ((this.filtro.getTipoTransplante() == null)
				|| (this.filtro.getTipoTransplante()
						.equals(DominioTipoTransplanteCombo.O))) {
			setTipoTmoAlogenico(true);
			this.filtro.setTipoAlogenico(null);
			this.filtro.setTipoTMO(null);
			this.filtro.setTipoAlogenico(null);
		} else {
			if((this.filtro.getTipoTMO()==null)||(this.filtro.getTipoTransplante()
					.equals(DominioTipoTransplanteCombo.T) && this.filtro.getTipoTMO() !=null && this.filtro.getTipoTMO().equals(DominioSituacaoTmo.U) )){
				this.filtro.setTipoAlogenico(null);
			}
			setTipoTmoAlogenico(tipoTmo);
		}
	}

	public void actionPerformed(ActionEvent e) {
		verificaTipoTransplante();
		verificaTipoTMO();
	}

	public boolean isTipoTransplanteTMO() {
		return tipoTransplanteTMO;
	}

	public void setTipoTransplanteTMO(boolean tipoTransplanteTMO) {
		this.tipoTransplanteTMO = tipoTransplanteTMO;
	}

	public boolean isTipoTransplanteOrgaos() {
		return tipoTransplanteOrgaos;
	}

	public void setTipoTransplanteOrgaos(boolean tipoTransplanteOrgaos) {
		this.tipoTransplanteOrgaos = tipoTransplanteOrgaos;
	}

	public boolean isTipoTmoAlogenico() {
		return tipoTmoAlogenico;
	}

	public void setTipoTmoAlogenico(boolean tipoTmoAlogenico) {
		this.tipoTmoAlogenico = tipoTmoAlogenico;
	}

	public FiltroTempoPermanenciaListVO getFiltro() {
		return filtro;
	}

	public void setFiltro(FiltroTempoPermanenciaListVO filtro) {
		this.filtro = filtro;
	}

	public boolean isGerouArquivo() {
		return gerouArquivo;
	}

	public void setGerouArquivo(boolean gerouArquivo) {
		this.gerouArquivo = gerouArquivo;
	}

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}
	
	public DominioTipoOrgao getTipoOrgao() {
		return tipoOrgao;
	}

	public void setTipoOrgao(DominioTipoOrgao tipoOrgao) {
		this.tipoOrgao = tipoOrgao;
	}

	public DominioTipoTransplanteCombo getTipoTransplante() {
		return tipoTransplante;
	}

	public void setTipoTransplante(DominioTipoTransplanteCombo tipoTransplante) {
		this.tipoTransplante = tipoTransplante;
	}

	public DominioOrdenacaoPesquisaFilaTransplante getOrdenacao() {
		return ordenacao;
	}

	public void setOrdenacao(DominioOrdenacaoPesquisaFilaTransplante ordenacao) {
		this.ordenacao = ordenacao;
	}

	public DominioSituacaoTmo getTipoTMO() {
		return tipoTMO;
	}

	public void setTipoTMO(DominioSituacaoTmo tipoTMO) {
		this.tipoTMO = tipoTMO;
	}

	public DominioTipoAlogenico getTipoAlogenico() {
		return tipoAlogenico;
	}

	public void setTipoAlogenico(DominioTipoAlogenico tipoAlogenico) {
		this.tipoAlogenico = tipoAlogenico;
	}

	public String downloadCSV() {
		return REDIRECT_RELATORIO_PACIENTE_COM_OBTO;
	}

	
}
