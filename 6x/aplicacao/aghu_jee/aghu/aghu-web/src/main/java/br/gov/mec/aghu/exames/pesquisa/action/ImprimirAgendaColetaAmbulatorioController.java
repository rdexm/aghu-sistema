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
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.cups.cadastrosbasicos.business.ICadastrosBasicosCupsFacade;
import br.gov.mec.aghu.exames.agendamento.business.IAgendamentoExamesFacade;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioMateriaisColetarInternacaoFiltroVO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.SolicitacoesAgendaColetaAmbulatorioVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelGradeAgendaExame;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.cups.ImpImpressora;
import net.sf.jasperreports.engine.JRException;


public class ImprimirAgendaColetaAmbulatorioController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 957435759783799993L;

	private static final String IMPRIMIR_AGENDA_COLETA_AMBULATORIO = "imprimirAgendaColetaAmbulatorio";
	private static final String IMPRIMIR_AGENDA_COLETA_AMBULATORIO_PDF = "imprimirAgendaColetaAmbulatorioPdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IAgendamentoExamesFacade agendamentoExamesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICadastrosBasicosCupsFacade cadastrosBasicosCupsFacade;

	/*	fitro da tela de pesquisa	*/
	private RelatorioMateriaisColetarInternacaoFiltroVO filtro = new RelatorioMateriaisColetarInternacaoFiltroVO();

	/*	Dados que serão impressos em PDF. */
	private List<SolicitacoesAgendaColetaAmbulatorioVO> colecao = new ArrayList<SolicitacoesAgendaColetaAmbulatorioVO>();
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		this.filtro.setUnidadeColeta(solicitacaoExameFacade.buscarAghUnidadesFuncionaisPorParametro(AghuParametrosEnum.P_COD_UNIDADE_COLETA_DEFAULT.toString()));
	
	}

	/**
	 * Metodo que efetua a pesquisa do relatório
	 */
	public void pesquisar() throws BaseException{
		colecao = solicitacaoExameFacade.pesquisarAgendaColetaAmbulatorio(this.filtro);
	}

	public String voltar(){
		return IMPRIMIR_AGENDA_COLETA_AMBULATORIO;
	}
	
	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws DocumentException 
	 */
	public String print() throws BaseException, JRException, SystemException, IOException, DocumentException {
		try{
			this.colecao = solicitacaoExameFacade.pesquisarAgendaColetaAmbulatorio(filtro);

			// #9418 - Contigencia Consular Últimos Arquivos Impressos
			if (this.colecao != null && !this.colecao.isEmpty()){
				this.imprimirRelatorioCopiaSeguranca(true);
			}
			
		} catch (SistemaImpressaoException e){			
			e.setParameters(new Object[] {"Servidor de impressão ou impressora não encontrado."});
			apresentarExcecaoNegocio(e);
			return null;
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return null;
			
		} catch (DocumentException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			return null;
		}
		return IMPRIMIR_AGENDA_COLETA_AMBULATORIO_PDF;
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException {
		try {
			this.colecao = solicitacaoExameFacade.pesquisarAgendaColetaAmbulatorio(this.filtro);
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(),super.getEnderecoIPv4HostRemoto());

			// #9418 - Contigencia Consular Últimos Arquivos Impressos
			if (this.colecao != null && !this.colecao.isEmpty()){
				this.imprimirRelatorioCopiaSeguranca(true);
			}
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
			
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<SolicitacoesAgendaColetaAmbulatorioVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	
	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		params.put("dataAtual", DateFormatUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_AGENDA_COLETA");
		params.put("tituloRelatorio", "Agenda da " + this.filtro.getUnidadeColeta().getDescricao() + " em " + 
				 DateFormatUtil.obterDataFormatada(filtro.getDataAgenda(), DateConstants.DATE_PATTERN_DDMMYYYY_HORA_MINUTO) );
		
		params.put("totalRegistros", colecao.size()-1);

		params.put("subRelatorio",Thread.currentThread()
				.getContextClassLoader().getResource("br/gov/mec/aghu/exames/report/imprimirAgendaColetarAmbulatorio_subreport1.jasper"));
		
		/*
		params.put("subRelatorio","/home/aghu/AGHU/workspace/aghu_contrucao/aghu/web/src/main/jasperreports/br/gov/mec/aghu/exames/report/imprimirAgendaColetarAmbulatorio_subreport1.jasper");
		*/
		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {	
		return	"br/gov/mec/aghu/exames/report/imprimirAgendaColetarAmbulatorio.jasper";		
	}
	
	
	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	// Metodo para Suggestion Box de Unidade Coleta
	public List<AghUnidadesFuncionais> buscarUnidadesColetadoras(String valor){
		return aghuFacade.pesquisarUnidadesFuncionaisColetaPorCodigoDescricao(valor);
	}
	
	public List<AelGradeAgendaExame> pesquisarGradeAgendaExame(String parametro) {
		Short unfSeq = null;
		if (this.filtro.getUnidadeColeta() != null) {
			unfSeq = this.filtro.getUnidadeColeta().getSeq();
		} else {
			unfSeq = -1;
		}
		return this.agendamentoExamesFacade.pesquisarGradeExamePorUnidadeExec(parametro, unfSeq);
	}

	// Metodo para Suggestion Box Unidade Solicitante
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncionalSolicitanteAvisada(Object param) {
		return this.cadastrosBasicosInternacaoFacade.pesquisarUnidadeFuncionalPorCodigoEDescricao(param);
	}

	// Metodo para Suggestion Box de impressoras
	public List<ImpImpressora> pesquisarImpressora(String paramPesquisa) {
		return this.cadastrosBasicosCupsFacade.pesquisarImpressora(paramPesquisa, true);
	}

	public RelatorioMateriaisColetarInternacaoFiltroVO getFiltro() {
		return filtro;
	}

	public void setFiltro(RelatorioMateriaisColetarInternacaoFiltroVO filtro) {
		this.filtro = filtro;
	}
}