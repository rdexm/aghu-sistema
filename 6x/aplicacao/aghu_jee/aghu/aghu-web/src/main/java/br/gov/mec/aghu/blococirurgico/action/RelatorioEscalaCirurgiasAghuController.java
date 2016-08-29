package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.UnknownHostException;
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

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateConstants;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;

/**
 * Controller de geração do relatório Escala Cirurgias
 * 
 * @author amalmeida
 */

public class RelatorioEscalaCirurgiasAghuController extends ActionReport {

	private static final String IMPRIMIR = "imprimir";

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	
	private static final Log LOG = LogFactory.getLog(RelatorioEscalaCirurgiasAghuController.class);
	
	private static final long serialVersionUID = 657972754252336246L;

	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;
		
	@EJB
	private IParametroFacade parametroFacade;
		
	private AghUnidadesFuncionais unidadesFuncionais; // Unidade funcional

	private AghParametros aghParametro;

	private Date dataCirurgia; // Data da Cirurgia.
	private DominioTipoEscala tipoEscala; // Tipo de Escala
	private String titulo; // Titulo do report

	// Dados que serão impressos em PDF ou etiquetas.
	private List<EscalaCirurgiasVO> colecao = new ArrayList<EscalaCirurgiasVO>(0);
	private String nomeMicrocomputador;
	
	private static final String RELATORIO_ESCALA_CIRURGIAS_PDF = "blococirurgico-relatorioEscalaCirurgiasAghuPdf";
	private static final String RELATORIO_ESCALA_CIRURGIAS = "blococirurgico-relatorioEscalaCirurgiasAghu";
	
	private String voltarPara;
	
	private MbcSalaCirurgica sala;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	
	
	
	/**
	 * Método executado ao abrir a tela
	 */
	public void iniciar() {
	 
		if (unidadesFuncionais == null) {
			try {
				
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e) {
					LOG.error("Exceção capturada:", e);
				}
				
				unidadesFuncionais = blocoCirurgicoFacade.obterUnidadeFuncionalCirurgia(nomeMicrocomputador);
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
				LOG.error("Excecao capturada: ",e);
			}
		}
	
	}
			
	/**
	 * Impressao direta via CUPS.
	 * 
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void impressaoDireta() {
		try {
			this.montaColecaoDados();
			if (this.colecao==null || this.colecao.size()==0) {
				this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_ESCALA_VAZIO");
				return;
			}
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (ApplicationBusinessException e){
			this.apresentarMsgNegocio(Severity.INFO, e.getMessage());
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	/**
	 * Imprime relatório de escala Simples
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print()throws ApplicationBusinessException, JRException, SystemException, IOException, DocumentException {
		try{
			this.montaColecaoDados();
		} catch (ApplicationBusinessException e){
			this.apresentarMsgNegocio(Severity.INFO, e.getMessage());
			return null;
		}
		if (this.colecao==null || this.colecao.size()==0) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_RELATORIO_ESCALA_VAZIO");
			return null;
		}

		DocumentoJasper documento = gerarDocumento();
		
		if (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "escalaCirurgias", IMPRIMIR)
				|| (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "escalaSimplesCirurgias", IMPRIMIR))) {
			
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		} else {
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
		}
		
		return RELATORIO_ESCALA_CIRURGIAS_PDF;
	}

	private void montaColecaoDados() throws ApplicationBusinessException {
		AghParametros aghParametros = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
		Integer grupoMat = aghParametros.getVlrNumerico().intValue();			
		this.colecao = blocoCirurgicoFacade.pesquisarRelatorioEscalaCirurgiasAghu(this.unidadesFuncionais, this.dataCirurgia, this.obterLoginUsuarioLogado(), grupoMat, sala);
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
	
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException {

		DocumentoJasper documento = gerarDocumento();
//		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		if (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "escalaCirurgias", IMPRIMIR)
				|| (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "escalaSimplesCirurgias", IMPRIMIR))) {
			
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} else {
			return this.criarStreamedContentPdf(documento.getPdfByteArray(true));
		}
	}
	
	@Override
	public Collection<EscalaCirurgiasVO> recuperarColecao() throws ApplicationBusinessException {

		return this.colecao;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/blococirurgico/report/relatorioEscalaCirurgiasAghu.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		String descricaoTipoEscala = null;
		
		MbcControleEscalaCirurgica mbcControleEscalaCirurgica = blocoCirurgicoFacade.obterMbcControleEscalaCirurgicaPorUnfSeqDataEscala(unidadesFuncionais.getSeq(), dataCirurgia);

		if (mbcControleEscalaCirurgica!=null){
			descricaoTipoEscala = DominioTipoEscala.D.equals(mbcControleEscalaCirurgica.getTipoEscala()) ? "DEFINITIVA" : "PRÉVIA";
		}
		else {
			descricaoTipoEscala = "AGENDA PREVISTA -";
		}

		params.put("nomeRelatorio", "ESCALA_" + descricaoTipoEscala);
		
		this.titulo = blocoCirurgicoFacade.pesquisarTituloEscala(mbcControleEscalaCirurgica!=null?mbcControleEscalaCirurgica.getTipoEscala():null, dataCirurgia);
		params.put("escala", this.titulo);
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), DateConstants.DATE_PATTERN_DDMMYYYY));
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("unidade", this.unidadesFuncionais.getDescricao());
		params.put("SUBREPORT_DIR", "/br/gov/mec/aghu/blococirurgico/report/");

		return params;
	}
	
	public void limparPesquisa() {
		
		this.unidadesFuncionais = new AghUnidadesFuncionais();
		this.dataCirurgia = null;
	}
	
	public String voltar() {
		if (StringUtils.isBlank(this.voltarPara)) {
			return RELATORIO_ESCALA_CIRURGIAS;
		} else {
			return voltarPara;
		}
	}
	
	public List<MbcSalaCirurgica> buscarSalasCirurgicas(final String filtro){
		return blocoCirurgicoCadastroApoioFacade.buscarSalasCirurgicas( (String) filtro, this.getUnidadesFuncionais().getSeq(), DominioSituacao.A);
	}

	/*
	 * MÉTODOS DA SUGGESTION DE UNIDADES CIRÚRGICAS
	 */
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(final String strPesquisa) {
		return this.returnSGWithCount(this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS,  false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(final String strPesquisa) {
		return this.aghuFacade.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, ConstanteAghCaractUnidFuncionais.UNID_EXECUTORA_CIRURGIAS, false);
	}

	// Getters e Setters

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public boolean isMostrarLinkExcluirUnidades() {
		return this.getUnidadesFuncionais() != null;
	}

	public void limparUnidades() {
		this.unidadesFuncionais = null;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public AghParametros getAghParametro() {
		return aghParametro;
	}

	public void setAghParametro(AghParametros aghParametro) {
		this.aghParametro = aghParametro;
	}

	public DominioTipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(DominioTipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}
	
	public MbcSalaCirurgica getSala() {
		return sala;
	}

	public void setSala(MbcSalaCirurgica sala) {
		this.sala = sala;
	}
	
}
