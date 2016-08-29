package br.gov.mec.aghu.blococirurgico.action;
import java.io.ByteArrayOutputStream;
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
import org.primefaces.model.StreamedContent;

import com.itextpdf.text.DocumentException;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.CirurgiaVO;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioEtiquetasIdentificacaoVO;
import br.gov.mec.aghu.blococirurgico.vo.VAghUnidFuncionalVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioTipoEtiqueta;
import br.gov.mec.aghu.exames.action.ReimprimirEtiquetasController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
//import br.gov.mec.aghu.impressao.OpcoesImpressao;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AelAmostraItemExames;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;


public class ImpressaoEtiquetasIdentificacaoController extends ActionReport {

	private static final String LABEL_NAO_ENCONTROU_REGISTRO_IMPRESSAO_TIPO_ETIQUETA = "LABEL_NAO_ENCONTROU_REGISTRO_IMPRESSAO_TIPO_ETIQUETA";

	private static final String EXCECAO = "excecao";

	private StreamedContent media;

	public StreamedContent getMedia() {	
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(ImpressaoEtiquetasIdentificacaoController.class);

	private static final long serialVersionUID = 433052708282290129L;


	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@Inject
	private ReimprimirEtiquetasController reimprimirEtiquetasController;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	private CirurgiaVO cirurgiaSelecionada;

	private AghUnidadesFuncionais unidade;
	private VAghUnidFuncionalVO unidadeMae;
	private Boolean previsaoAlta = true;
	private Integer prontuario;
	private Date dataCirurgia = new Date();
	private List<RelatorioEtiquetasIdentificacaoVO> colecao;
	private String exibirCamposFixos;
	private boolean desabilitaPct;
	private List<String> listaTipoEtiquetas = new ArrayList<String>();	
	
	private Integer atdSeqSelecionado;
	private Integer pacCodigoSelecionado;
	private Integer prontuarioSelecionado;
	private Date dtProcedimento;
	private List<DocumentoJasper> listPdfs;

	private Map<DominioTipoEtiqueta, Boolean> imprimiuMap;
	
	private final String PAGE_RELATORIO_IMP_EQUI_IDEN_MISTA_PDF= "blococirurgico-relatorio-impressaoEtiquetasIdentificacaoMedItensPacMistaPdfs";
	private final String PAGE_LISTA_CIRURGIAS = "blococirurgico-listaCirurgias";
	
	public void directPrint() throws ApplicationBusinessException {
		blocoCirurgicoFacade
				.preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(
						unidade, pacCodigoSelecionado, unidadeMae, null, null);
		setExibirCamposFixos(DominioTipoEtiqueta.E.name()); // Etiqueta Folha A4 para medicamento
		carregarColecao();
		if (colecao == null || colecao.isEmpty()) {
			if (this.imprimiuMap != null) {
				this.imprimiuMap.put(DominioTipoEtiqueta.E, false);
			}
			
			apresentarMsgNegocio(Severity.INFO,
					LABEL_NAO_ENCONTROU_REGISTRO_IMPRESSAO_TIPO_ETIQUETA, this.getDescricaoDominioTipoEtiqueta("E"));
		} else {
			if (this.imprimiuMap != null) {
				this.imprimiuMap.put(DominioTipoEtiqueta.E, true);
			}
			
			DocumentoJasper documento = gerarDocumento();
			this.listPdfs.add(documento);
		}
	}
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return this.colecao;
	}
	
	public void directPrintEtiquetas() throws BaseException, JRException, SystemException, IOException {

		try{ 
			DocumentoJasper documento = gerarDocumento();
			//OpcoesImpressao noMargins = new OpcoesImpressao(true, false);		
			sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);			
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
	}

	public String directPrintMedicamento() {
		try {
			directPrint();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);			
		}

		return "reload";
	}

	public String directPrintItensPaciente() throws BaseException,
			JRException, SystemException, IOException {
		blocoCirurgicoFacade
				.preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(
						unidade, pacCodigoSelecionado, unidadeMae, null, null);
		setExibirCamposFixos(DominioTipoEtiqueta.I.name()); // Etiqueta Folha A4 identificação itens do paciente
		carregarColecao();
		if (colecao == null || colecao.isEmpty()) {
			if (this.imprimiuMap != null) {
				this.imprimiuMap.put(DominioTipoEtiqueta.I, false);
			}
			
			apresentarMsgNegocio(Severity.INFO,
					LABEL_NAO_ENCONTROU_REGISTRO_IMPRESSAO_TIPO_ETIQUETA, this.getDescricaoDominioTipoEtiqueta("I"));
		} else {
			if (this.imprimiuMap != null) {
				this.imprimiuMap.put(DominioTipoEtiqueta.I, true);
			}
			
			DocumentoJasper documento = gerarDocumento();
			this.listPdfs.add(documento);
		}

		return "reload";
	}

	public void directPrintMista() throws BaseException, JRException,
			SystemException, IOException {
		blocoCirurgicoFacade
				.preImprimirValidarUnidadeFuncionalProntuarioUnidadeFuncionalMae(
						unidade, pacCodigoSelecionado, unidadeMae, null, null);
		setExibirCamposFixos(DominioTipoEtiqueta.M.name()); // Etiqueta Folha A4 mista
		carregarColecao();
		if (colecao == null || colecao.isEmpty()) {
			if (this.imprimiuMap != null) {
				this.imprimiuMap.put(DominioTipoEtiqueta.M, false);
			}
			
			apresentarMsgNegocio(Severity.INFO,
					LABEL_NAO_ENCONTROU_REGISTRO_IMPRESSAO_TIPO_ETIQUETA, this.getDescricaoDominioTipoEtiqueta("M"));
		} else {
			if (this.imprimiuMap != null) {
				this.imprimiuMap.put(DominioTipoEtiqueta.M, true);
			}
			
			DocumentoJasper documento = gerarDocumento();
			this.listPdfs.add(documento);
		}
	}

	public void directPrintPct(Integer atdSeqSelecionado)
			throws BaseException, JRException, SystemException, IOException {
		

		final AghParametros parametroExamePCT = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_EXAME_PROVA_CRUZADA);
		final AghParametros parametroSitAColetar = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_A_COLETAR);
		final AghParametros parametroSitAExecutar = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_NA_AREA_EXECUTORA);
		final AghParametros parametroSitColetado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO);
		final AghParametros parametroSitColetadoSolic = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_COLETADO_SOLIC);
		final AghParametros parametroSitEmColeta = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EM_COLETA);
		final AghParametros parametroSitExecutado = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_SITUACAO_EXECUTANDO);
		
		String[] situacaoIse = {parametroSitAColetar.getVlrTexto(), parametroSitAExecutar.getVlrTexto(),
								parametroSitColetado.getVlrTexto(), parametroSitColetadoSolic.getVlrTexto(),
								parametroSitEmColeta.getVlrTexto(), parametroSitExecutado.getVlrTexto()};
		AelItemSolicitacaoExames ise = solicitacaoExameFacade
				.obterItemSolicitacaoExamePorAtendimento(
						atdSeqSelecionado, parametroExamePCT.getVlrTexto(),	situacaoIse);
		
		if(ise == null || ise.getAelAmostraItemExames() == null || ise.getAelAmostraItemExames().size() == 0){
			apresentarMsgNegocio(Severity.INFO,
					LABEL_NAO_ENCONTROU_REGISTRO_IMPRESSAO_TIPO_ETIQUETA, this.getDescricaoDominioTipoEtiqueta("T"));
			return;
		}
		
		try {
			reimprimirEtiquetasController.setUnidadeExecutora(unidade);
			reimprimirEtiquetasController.setAmostraSoeSeqSelecionada(ise.getSolicitacaoExame().getSeq());
			AelAmostraItemExames exame = ise.getAelAmostraItemExames().get(0);
			reimprimirEtiquetasController.setAmostraSeqpSelecionada(exame.getId().getAmoSeqp().shortValue());

			reimprimirEtiquetasController.reimprimirAmostra();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}

	public void imprimirEtiquetas(AghUnidadesFuncionais unidadesFuncionais,
			Integer prontuarioSelecionado, Date dtProcedimento,
			List<String> listaTipoEtiquetas, Integer atdSeqSelecionado) {
		this.setUnidade(unidadesFuncionais);
		this.setProntuario(prontuarioSelecionado);
		this.setUnidadeMae(null);
		this.setDataCirurgia(dtProcedimento);
		this.listPdfs = new ArrayList<DocumentoJasper>();

		for (String tipoEtiqueta : listaTipoEtiquetas) {

			try {
				if (tipoEtiqueta.equals(DominioTipoEtiqueta.E.toString())) {
					directPrint();
				}

				if (tipoEtiqueta.equals(DominioTipoEtiqueta.I.toString())) {
					directPrintItensPaciente();
				}

				if (tipoEtiqueta.equals(DominioTipoEtiqueta.M.toString())) {
					directPrintMista();
				}

				if (tipoEtiqueta.equals(DominioTipoEtiqueta.T.toString())) {
					directPrintPct(atdSeqSelecionado);
				}

			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);				
			} catch (JRException e) {
				LOG.error(EXCECAO, e);
			} catch (SystemException e) {
				LOG.error(EXCECAO, e);
			} catch (IOException e) {
				LOG.error(EXCECAO, e);
			}

		}

	}

	public boolean verificaPctPendente(Integer atdSeqSelecionado, Integer seqCrgSelecionada) {
		boolean retorno = solicitacaoExameFacade.verificaPctPendente(atdSeqSelecionado, seqCrgSelecionada);	
		return retorno;
	}


	public boolean verificaPctRealizado(Integer pacCodigoSelecionado) {
		boolean retorno = solicitacaoExameFacade.verificaPctRealizado(pacCodigoSelecionado);
		return retorno;
	}
	
	// Concatena os PDFs para exibir juntos.
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException {

		ByteArrayOutputStream outPutStream = new ByteArrayOutputStream();
		List<JasperPrint> jasperPrints = new ArrayList<JasperPrint>();
		
		for (DocumentoJasper documento : this.listPdfs) {
			jasperPrints.add(documento.getJasperPrint());
		}
		
		JRPdfExporter exp = new JRPdfExporter();
		exp.setParameter(JRPdfExporterParameter.JASPER_PRINT_LIST, jasperPrints); 
		exp.setParameter(JRExporterParameter.OUTPUT_STREAM, outPutStream);
		exp.exportReport();	
		
		byte[] byteArray = outPutStream.toByteArray();
		return this.criarStreamedContentPdf(byteArray);
	}
	

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();

		params.put("P_EXIBIR_CAMPOS_FIXOS", exibirCamposFixos);

		return params;
	}

	public void carregarColecao() throws ApplicationBusinessException {
		Short unfSeq = (unidade != null) ? unidade.getSeq() : null;
		Boolean prvAlta = previsaoAlta;
		Short unfSeqMae = (unidadeMae != null) ? unidadeMae.getSeq() : null;
		colecao = blocoCirurgicoFacade.pesquisarRelatorioEtiquetasIdentificacao(unfSeq, unfSeqMae, prvAlta, pacCodigoSelecionado, dataCirurgia);
	}

	public String imprimirEtiquetas() {
		this.imprimiuMap = new HashMap<DominioTipoEtiqueta, Boolean>();
		
		if (listaTipoEtiquetas != null && !listaTipoEtiquetas.isEmpty()) {
			imprimirEtiquetas(
					unidade, prontuarioSelecionado,
					dtProcedimento, listaTipoEtiquetas, atdSeqSelecionado);
			if(redirecionaTelaMostraRelatorioEtiquetas(listaTipoEtiquetas)){
				return PAGE_RELATORIO_IMP_EQUI_IDEN_MISTA_PDF;	
			}
		} else {
			
			apresentarMsgNegocio(Severity.INFO,
					"LABEL_SELECIONE_TIPO_ETIQUETA_PARA_IMPRESSAO");
			openDialog("modalEtiquetasWG");
			return null;
		}
		return PAGE_LISTA_CIRURGIAS ;
		
	}
	
	private boolean redirecionaTelaMostraRelatorioEtiquetas(List<String> listaTipoEtiquetas){
		for (String tipoEtiqueta : listaTipoEtiquetas) {
			boolean existeDados = true;
			
			if (tipoEtiqueta.equals(DominioTipoEtiqueta.E.toString())) {
				if (this.imprimiuMap != null) {
					existeDados = this.imprimiuMap.get(DominioTipoEtiqueta.E);
				}
				
				if (Boolean.TRUE.equals(existeDados)) {
					return true;
				}
			}

			if (tipoEtiqueta.equals(DominioTipoEtiqueta.I.toString())) {
				if (this.imprimiuMap != null) {
					existeDados = this.imprimiuMap.get(DominioTipoEtiqueta.I);
				}
				
				if (Boolean.TRUE.equals(existeDados)) {
					return true;
				}
			}

			if (tipoEtiqueta.equals(DominioTipoEtiqueta.M.toString())) {
				if (this.imprimiuMap != null) {
					existeDados = this.imprimiuMap.get(DominioTipoEtiqueta.M);
				}
				
				if (Boolean.TRUE.equals(existeDados)) {
					return true;
				}
			}
		}
		return false;
	}

	public void habiltaEtiquetaPct() {

		desabilitaPct = true;
		listaTipoEtiquetas.remove("T");

		if (atdSeqSelecionado != null && cirurgiaSelecionada.getCrgSeq() != null) { // marcado
			if (verificaPctPendente(atdSeqSelecionado, cirurgiaSelecionada.getCrgSeq())) {
				listaTipoEtiquetas.add("T");
				desabilitaPct = false;
			}
			
		if (pacCodigoSelecionado != null) {
			if (verificaPctRealizado(pacCodigoSelecionado)) { // deixa selecionar
				desabilitaPct = false;
			}
		}
		}
	}

	public void cancelar() {
		desabilitaPct = true;
		listaTipoEtiquetas = new ArrayList<String>();
	}
	
	public String voltar(){
		this.cancelar();
		return PAGE_LISTA_CIRURGIAS;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioEtiquetasIdentificacao.jasper";
	}
	
	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public void setBlocoCirurgicoFacade(IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public AghUnidadesFuncionais getUnidade() {
		return unidade;
	}

	public void setUnidade(AghUnidadesFuncionais unidade) {
		this.unidade = unidade;
	}

	public VAghUnidFuncionalVO getUnidadeMae() {
		return unidadeMae;
	}

	public void setUnidadeMae(VAghUnidFuncionalVO unidadeMae) {
		this.unidadeMae = unidadeMae;
	}

	public Boolean getPrevisaoAlta() {
		return previsaoAlta;
	}

	public void setPrevisaoAlta(Boolean previsaoAlta) {
		this.previsaoAlta = previsaoAlta;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public List<RelatorioEtiquetasIdentificacaoVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioEtiquetasIdentificacaoVO> colecao) {
		this.colecao = colecao;
	}

	public String getExibirCamposFixos() {
		return exibirCamposFixos;
	}

	public void setExibirCamposFixos(String exibirCamposFixos) {
		this.exibirCamposFixos = exibirCamposFixos;
	}

	public String getDominioTipoEtiqueta(String valor){
		return DominioTipoEtiqueta.getInstance(valor).toString();
	}
	
	public String getDescricaoDominioTipoEtiqueta(String valor) {
		return DominioTipoEtiqueta.getInstance(valor).getDescricao();
	}

	public boolean isDesabilitaPct() {
		return desabilitaPct;
	}

	public void setDesabilitaPct(boolean desabilitaPct) {
		this.desabilitaPct = desabilitaPct;
	}

	public List<String> getListaTipoEtiquetas() {
		return listaTipoEtiquetas;
	}

	public void setListaTipoEtiquetas(List<String> listaTipoEtiquetas) {
		this.listaTipoEtiquetas = listaTipoEtiquetas;
	}

	public Integer getAtdSeqSelecionado() {
		return atdSeqSelecionado;
	}

	public void setAtdSeqSelecionado(Integer atdSeqSelecionado) {
		this.atdSeqSelecionado = atdSeqSelecionado;
	}

	public Integer getPacCodigoSelecionado() {
		return pacCodigoSelecionado;
	}

	public void setPacCodigoSelecionado(Integer pacCodigoSelecionado) {
		this.pacCodigoSelecionado = pacCodigoSelecionado;
	}

	public Integer getProntuarioSelecionado() {
		return prontuarioSelecionado;
	}

	public void setProntuarioSelecionado(Integer prontuarioSelecionado) {
		this.prontuarioSelecionado = prontuarioSelecionado;
	}

	public Date getDtProcedimento() {
		return dtProcedimento;
	}

	public void setDtProcedimento(Date dtProcedimento) {
		this.dtProcedimento = dtProcedimento;
	}

	public List<DocumentoJasper> getListPdfs() {
		return listPdfs;
	}

	public void setListPdfs(List<DocumentoJasper> listPdfs) {
		this.listPdfs = listPdfs;
	}

	public CirurgiaVO getCirurgiaSelecionada() {
		return cirurgiaSelecionada;
	}

	public void setCirurgiaSelecionada(CirurgiaVO cirurgiaSelecionada) {
		this.cirurgiaSelecionada = cirurgiaSelecionada;
	}
	
}