package br.gov.mec.aghu.exames.pesquisa.action;

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

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.ImprimirExamesRealizadosAtendimentosDiversosVO;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.FatConvenioSaude;
import net.sf.jasperreports.engine.JRException;

public class ImprimirExamesRealizadosAtendimentosDiversosController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 957435759783799993L;

	private static final String IMPRIMIR_EXAMES_REALIZADOS_ATENDIMENTOS_DIVERSOS = "imprimirExamesRealizadosAtendimentosDiversos";
	private static final String IMPRIMIR_EXAMES_REALIZADOS_ATENDIMENTOS_DIVERSOS_PDF = "imprimirExamesRealizadosAtendimentosDiversosPdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	private Date dataInicial;
	
	private Date dataFinal;
	
	private DominioSimNao grupoSus; 
	
	private FatConvenioSaude convenioSaude;

	private List<ImprimirExamesRealizadosAtendimentosDiversosVO> colecao = new ArrayList<ImprimirExamesRealizadosAtendimentosDiversosVO>();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public List<FatConvenioSaude> pesquisarConveniosSaude(String valor){
		return faturamentoApoioFacade.pesquisarConveniosSaudePorCodigoOuDescricao(String.valueOf(valor));
	}

	public String voltar(){
		return IMPRIMIR_EXAMES_REALIZADOS_ATENDIMENTOS_DIVERSOS;
	}
	
	public String print()throws BaseException, JRException, SystemException, IOException, DocumentException {
		try{
			this.colecao = solicitacaoExameFacade.imprimirExamesRealizadosAtendimentosDiversos(dataInicial,dataFinal,grupoSus,convenioSaude);

//			// #9418 - Contigencia Consular Últimos Arquivos Impressos
//			if (this.colecao != null && !this.colecao.isEmpty()){
//				this.imprimirRelatorioCopiaSeguranca(true);
//			}
		
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
		} 
		return IMPRIMIR_EXAMES_REALIZADOS_ATENDIMENTOS_DIVERSOS_PDF;
	}
	
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {

		try {

			this.colecao = solicitacaoExameFacade.imprimirExamesRealizadosAtendimentosDiversos(dataInicial,dataFinal,grupoSus,convenioSaude);

			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());

//			// #9418 - Contigencia Consular Últimos Arquivos Impressos
//			if (this.colecao != null && !this.colecao.isEmpty()){
//				this.imprimirRelatorioCopiaSeguranca(true);
//			}

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<ImprimirExamesRealizadosAtendimentosDiversosVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_FAT_EXAMES");
		params.put("tituloRelatorio", "Faturamento Exames");
		params.put("grupoSus", grupoSus.name());
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		StringBuffer sb = new StringBuffer();
		sb.append(DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY))
			.append(" A ")
			.append(DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("periodo", sb.toString());
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/exames/report/imprimirExamesRealizadosAtendimentosDiversos.jasper";		
	}
	
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}
	
	
	public DominioSimNao getGrupoSus() {
		return grupoSus;
	}

	public void setGrupoSus(DominioSimNao grupoSus) {
		this.grupoSus = grupoSus;
	}

	public FatConvenioSaude getConvenioSaude() {
		return convenioSaude;
	}

	public void setConvenioSaude(FatConvenioSaude convenioSaude) {
		this.convenioSaude = convenioSaude;
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