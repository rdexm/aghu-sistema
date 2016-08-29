package br.gov.mec.aghu.exames.contratualizacao.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.action.HostRemotoCache;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.ApplicationBusinessExceptionCode;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateFormatUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioAgrupamentoTotaisExames;
import br.gov.mec.aghu.dominio.DominioSituacaoItemSolicitacaoExame;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.contratualizacao.business.IContratualizacaoFacade;
import br.gov.mec.aghu.exames.contratualizacao.vo.TotalItemSolicitacaoExamesVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelExames;
import br.gov.mec.aghu.model.AelSitItemSolicitacoes;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import net.sf.jasperreports.engine.JRException;


public class RelatorioTotalExamesPorPeriodoController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 3891101020368889158L;

	private static final String RELATORIO_TOTAL_EXAMES_POR_PERIODO = "relatorioTotalExamesPorPeriodo";
	private static final String RELATORIO_TOTAL_EXAMES_POR_PERIODO_PDF = "relatorioTotalExamesPorPeriodoPdf";

	@EJB
	private IContratualizacaoFacade contratualizacaoFacade;

	@EJB
	private IExamesFacade examesFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@Inject	
	private HostRemotoCache hostRemotoCache;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	private Date dataInicial;
	private Date dataFinal;
	private FatConvenioSaudePlano convenioSaudePlano;
	private AghUnidadesFuncionais unidadeExecutora;
	private AelExames exames;
	private List<AelSitItemSolicitacoes> sitItemSolicitacoesList;
	private Boolean agruparPorDia;
	private Boolean agruparPorSituacao;
	private AelSitItemSolicitacoes situacao;
	private DominioAgrupamentoTotaisExames tipoRelatorio;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 


		this.colecao = new ArrayList<TotalItemSolicitacaoExamesVO>(0);
		
		if (this.getDataInicial() == null) {
			this.populaCamposDatas();
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_AVISO_PARAMETROS_OBRIGATORIOS");
		}				
	
	}

	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<TotalItemSolicitacaoExamesVO> colecao;

	@Override
	public Collection<TotalItemSolicitacaoExamesVO> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();

		String local = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		
		params.put("nomeHospital", (local != null) ? local.toUpperCase() : local);
		params.put("TIPO_RELATORIO", this.tipoRelatorio.toString());
		params.put("DATA_INICIAL", DateFormatUtil.obterDataFormatada(dataInicial, DateConstants.DATE_PATTERN_DDMMYYYY));
		params.put("DATA_FINAL", DateFormatUtil.obterDataFormatada(dataFinal, DateConstants.DATE_PATTERN_DDMMYYYY));
		
		if (this.situacao != null && this.situacao.getDescricao() != null) {
			params.put("SITUACAO_DESCRICAO", this.situacao.getDescricao());
		}
		return params;
	}

	private void populaCamposDatas() {

		Calendar dataAtualMenosUmaSemana = Calendar.getInstance();
		dataAtualMenosUmaSemana.set(Calendar.HOUR_OF_DAY, 0);
		dataAtualMenosUmaSemana.set(Calendar.MINUTE, 0);
		dataAtualMenosUmaSemana.set(Calendar.SECOND, 0);
		dataAtualMenosUmaSemana.set(Calendar.MILLISECOND, 0);

		dataAtualMenosUmaSemana.add(Calendar.DAY_OF_MONTH, -7);

		this.dataInicial = dataAtualMenosUmaSemana.getTime();

		Calendar dataAtual = Calendar.getInstance();

		dataAtual.set(Calendar.HOUR_OF_DAY, 0);
		dataAtual.set(Calendar.MINUTE, 0);
		dataAtual.set(Calendar.SECOND, 0);
		dataAtual.set(Calendar.MILLISECOND, 0);
		this.dataFinal = dataAtual.getTime();
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 */
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/exames/report/relatorioTotaisExamesPorPeriodo.jasper";
	}

	public void obterPagadoresComAgendamento() {
		sitItemSolicitacoesList = this.contratualizacaoFacade.obterListaSituacoesItemSolicitacoes();
	}

	public void limpar() {

		this.populaCamposDatas();
		this.convenioSaudePlano = null;
		this.unidadeExecutora = null;
		this.exames = null;
		this.sitItemSolicitacoesList = null;
		this.agruparPorDia = null;
		this.agruparPorSituacao = null;
		this.situacao = null;
	}

	public void gerarCSV() {

		try {

			String retornoTestes = this.validaCamposFormulario();

			if (!retornoTestes.equals("")) {
				this.apresentarMsgNegocio(Severity.ERROR,
						retornoTestes);
			} else {

				Calendar dataFinalComHora = Calendar.getInstance();
				dataFinalComHora.setTime(this.dataFinal);
				dataFinalComHora.set(Calendar.HOUR_OF_DAY, 23);
				dataFinalComHora.set(Calendar.MINUTE, 59);
				dataFinalComHora.set(Calendar.SECOND, 59);

				this.contratualizacaoFacade
						.gerarCSV(
								this.dataInicial,
								dataFinalComHora.getTime(),
								this.convenioSaudePlano.getId().getCnvCodigo(),
								this.convenioSaudePlano.getId().getSeq(),
								this.buscaDominioSituacaoItemSolicitacaoExame(this.situacao),
								(this.unidadeExecutora != null) ? this.unidadeExecutora.getSeq() : null, 
								(this.exames != null) ? this.exames.getSigla() : null,
								this.defineTipoDeRelatorio());
				
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ARQUIVO_CSV_GERADO_COM_SUCESSO");
			}

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} catch (IOException e) {
			apresentarExcecaoNegocio(new BaseException(ApplicationBusinessExceptionCode.OCORREU_O_SEGUINTE_ERRO, e, e.getLocalizedMessage()));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Impressão direta usando o CUPS.
	 */
	public void directPrint() throws SistemaImpressaoException, ApplicationBusinessException  {
		try {
			String retornoTestes = this.validaCamposFormulario();

			if (!retornoTestes.equals("")) {
				this.apresentarMsgNegocio(Severity.ERROR,
						retornoTestes);
			} else {

				Calendar dataFinalComHora = Calendar.getInstance();
				dataFinalComHora.setTime(this.dataFinal);
				dataFinalComHora.set(Calendar.HOUR_OF_DAY, 23);
				dataFinalComHora.set(Calendar.MINUTE, 59);
				dataFinalComHora.set(Calendar.SECOND, 59);

				this.colecao = this.contratualizacaoFacade
						.buscarTotais(
								this.dataInicial,
								dataFinalComHora.getTime(),
								this.convenioSaudePlano.getId().getCnvCodigo(),
								this.convenioSaudePlano.getId().getSeq(),
								this.buscaDominioSituacaoItemSolicitacaoExame(this.situacao),
								(this.unidadeExecutora != null) ? this.unidadeExecutora.getSeq() : null, 
								(this.exames != null) ? this.exames.getSigla() : null,
								this.defineTipoDeRelatorio());
			}

			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), hostRemotoCache.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			this.limpar();
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.limpar();
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
		}
	}

	/**
	 * Valida o preenchimento dos campos obrigatórios da tela e as datas informadas
	 */
	private String validaCamposFormulario() {

		if (this.dataInicial == null || this.dataFinal == null || this.convenioSaudePlano == null) {
			return "MENSAGEM_PARAMETROS_NULOS";
		}

		if (DateUtil.validaDataMenor(this.dataFinal, this.dataInicial)) {
			return "MENSAGEM_DATA_INICIAL_MAIOR_DATA_FINAL";

		} else if (DateUtil.calcularDiasEntreDatas(this.dataInicial, dataFinal) > 31) {
			return "MENSAGEM_PERIODO_PESQUISA_MAIOR_PERMITIDO";

		} else {
			return "";
		}
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * @throws ApplicationBusinessException 
	 */
	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {

		String retornoTestes = this.validaCamposFormulario();

		if (!retornoTestes.equals("")) {
			this.apresentarMsgNegocio(Severity.ERROR, retornoTestes);
		} else {

			Calendar dataFinalComHora = Calendar.getInstance();
			dataFinalComHora.setTime(this.dataFinal);
			dataFinalComHora.set(Calendar.HOUR_OF_DAY, 23);
			dataFinalComHora.set(Calendar.MINUTE, 59);
			dataFinalComHora.set(Calendar.SECOND, 59);

			this.colecao
					.addAll(this.contratualizacaoFacade.buscarTotais(
							this.dataInicial,
							dataFinalComHora.getTime(),
							this.convenioSaudePlano.getId().getCnvCodigo(),
							this.convenioSaudePlano.getId().getSeq(),
							this.buscaDominioSituacaoItemSolicitacaoExame(this.situacao),
							(this.unidadeExecutora != null) ? this.unidadeExecutora.getSeq() : null, 
							(this.exames != null) ? this.exames.getSigla() : null,
							this.defineTipoDeRelatorio()));
			if (!this.colecao.isEmpty()) {
			
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		return RELATORIO_TOTAL_EXAMES_POR_PERIODO_PDF;
				
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CONSULTA_NAO_RETORNOU_DADOS");
			}
		}
		return null;
	}

	public String voltar(){
		return RELATORIO_TOTAL_EXAMES_POR_PERIODO;
	}
	
	private DominioAgrupamentoTotaisExames defineTipoDeRelatorio() {

		if (this.agruparPorDia == false & this.agruparPorSituacao == false) {
			this.tipoRelatorio = DominioAgrupamentoTotaisExames.X;

		} else if (this.agruparPorDia == true
				& this.agruparPorSituacao == false) {
			this.tipoRelatorio = DominioAgrupamentoTotaisExames.D;

		} else if (this.agruparPorDia == false
				& this.agruparPorSituacao == true) {
			this.tipoRelatorio = DominioAgrupamentoTotaisExames.S;

		} else if (this.agruparPorDia == true & this.agruparPorSituacao == true) {
			this.tipoRelatorio = DominioAgrupamentoTotaisExames.C;
		}

		return this.tipoRelatorio;
	}

	private DominioSituacaoItemSolicitacaoExame buscaDominioSituacaoItemSolicitacaoExame(AelSitItemSolicitacoes situacao) {
		DominioSituacaoItemSolicitacaoExame aux = null;

		if (situacao != null && situacao.getCodigo() != null) {

			if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AC.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.AC;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AX.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.AX;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AG.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.AG;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.AE.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.AE;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.CA.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.CA;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.CO.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.CO;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.CS.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.CS;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.EC.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.EC;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.EO.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.EO;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.EX.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.EX;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.LI.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.LI;
				
			} else if (situacao.getCodigo().equals(DominioSituacaoItemSolicitacaoExame.RE.toString())) {
				aux = DominioSituacaoItemSolicitacaoExame.RE;
				
			} else {
				aux = DominioSituacaoItemSolicitacaoExame.PE;
			}
		}
		return aux;
	}

	// SUGESTIONS BOX//////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////
	public List<FatConvenioSaudePlano> sbObterConvenio(String parametro) {
		return this.contratualizacaoFacade.sbObterConvenio(parametro);
	}

	public Long sbObterConvenioCount(Object parametro) {
		return this.contratualizacaoFacade.sbObterConvenioCount(parametro);
	}

	public List<AghUnidadesFuncionais> sbObterUnidadeExecutora(String parametro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(parametro),sbObterUnidadeExecutoraCount(parametro));
	}

	public Long sbObterUnidadeExecutoraCount(String parametro) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricaoCount(parametro);
	}

	public List<AelExames> sbObterExame(String parametro) {
		return this.returnSGWithCount(this.contratualizacaoFacade.sbObterExame(parametro),sbObterExameCount(parametro));
	}

	public Long sbObterExameCount(String parametro) {
		return this.contratualizacaoFacade.sbObterExameCount(parametro);
	}

	
	
	// FIM SUGGESTIONS/////////////////////////////////////////////
	// ////////////////////////////////////////////////////////////

	public List<AelSitItemSolicitacoes> listarSituacoesItensSolicitacaoAtivos() {
		return examesFacade.listarSituacoesItensSolicitacaoAtivos();
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

	public FatConvenioSaudePlano getConvenioSaudePlano() {
		return convenioSaudePlano;
	}

	public void setConvenioSaudePlano(FatConvenioSaudePlano convenioSaudePlano) {
		this.convenioSaudePlano = convenioSaudePlano;
	}

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public AelExames getExames() {
		return exames;
	}

	public void setExames(AelExames exames) {
		this.exames = exames;
	}

	public List<AelSitItemSolicitacoes> getSitItemSolicitacoesList() {
		return sitItemSolicitacoesList;
	}

	public void setSitItemSolicitacoesList(
			List<AelSitItemSolicitacoes> sitItemSolicitacoesList) {
		this.sitItemSolicitacoesList = sitItemSolicitacoesList;
	}

	public Boolean getAgruparPorSituacao() {
		return agruparPorSituacao;
	}

	public void setAgruparPorSituacao(Boolean agruparPorSituacao) {
		this.agruparPorSituacao = agruparPorSituacao;
	}

	public Boolean getAgruparPorDia() {
		return agruparPorDia;
	}

	public void setAgruparPorDia(Boolean agruparPorDia) {
		this.agruparPorDia = agruparPorDia;
	}

	public AelSitItemSolicitacoes getSituacao() {
		return situacao;
	}

	public void setSituacao(AelSitItemSolicitacoes situacao) {
		this.situacao = situacao;
	}

	public DominioAgrupamentoTotaisExames getTipoRelatorio() {
		return tipoRelatorio;
	}

	public void setTipoRelatorio(DominioAgrupamentoTotaisExames tipoRelatorio) {
		this.tipoRelatorio = tipoRelatorio;
	}

}
