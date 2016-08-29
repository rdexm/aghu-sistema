package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.paciente.vo.EscalaCirurgiasVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/**
 * Controller de geração do relatório Escala Cirurgias
 * 
 * @author amalmeida
 */


public class RelatorioSolicitacaoEscalaCirurgiasController extends ActionReport {

	private static final String IMPRIMIR = "imprimir";

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

	private static final Log LOG = LogFactory.getLog(RelatorioSolicitacaoEscalaCirurgiasController.class);

	private static final long serialVersionUID = 657972754252336246L;
	
	private static final String ESCALA_SIMPLES = "blococirurgico-relatorioEscalaSimplesPdf";
	private static final String ESCALA_CIRURGICA = "blococirurgico-relatorioEscalaCirurgicaPdf";
	private static final String SOLICITAR_ESCALA_CIRURGICA = "blococirurgico-solicitarEscalaCirurgica";
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	private AghUnidadesFuncionais unidadesFuncionais; // Unidade funcional

	private AghParametros aghParametro;

	private Date dataCirurgia; // Data da Cirurgia.
	private DominioTipoEscala tipoEscala; // Tipo de Escala
	private String titulo; // Titulo do report
	private Boolean relatorioSimples; // Verifica se imprime relatório de escala cirurgica ou escala simples

	// Dados que serão impressos em PDF ou etiquetas.
	private List<EscalaCirurgiasVO> colecao = new ArrayList<EscalaCirurgiasVO>(0);

	public String voltar() {
		return SOLICITAR_ESCALA_CIRURGICA;
	}
	
	/**
	 * Impressao direta via CUPS.
	 * 
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public void impressaoDireta() throws BaseException, JRException, SystemException, IOException, ParseException, DocumentException {

		this.print();

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}

	}

	/**
	 * Imprime relatório de escala Simples
	 * 
	 * @return
	 * @throws BaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 * @throws DocumentException 
	 */
	public String print()throws BaseException, JRException, SystemException, IOException, DocumentException {

		String retorno = null;
		try {

			if (this.relatorioSimples) {
				this.colecao = blocoCirurgicoFacade.pesquisarEscalaSimples(this.unidadesFuncionais, this.dataCirurgia);
				
				retorno = ESCALA_SIMPLES;
			} else {
				
				AghParametros aghParametros = this.parametroFacade
						.buscarAghParametro(AghuParametrosEnum.GRPO_MAT_ORT_PROT);
				Integer grupoMat = aghParametros.getVlrNumerico().intValue();
				this.colecao = blocoCirurgicoFacade.pesquisarEscalaCirurgica(this.unidadesFuncionais, this.dataCirurgia, grupoMat);
			
				retorno = ESCALA_CIRURGICA; 
			}

		} catch (ApplicationBusinessException e) {
			apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
		}

	

		DocumentoJasper documento = gerarDocumento();

		// Validação foi feita desta forma pois as permissões "CI" e "C" são iguais em todos os perfis.
		
		if (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "escalaCirurgias", IMPRIMIR)
				|| (cascaFacade.usuarioTemPermissao(servidorLogadoFacade.obterServidorLogado().getUsuario(), "escalaSimplesCirurgias", IMPRIMIR))) {
			
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(false)));
		} else {
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(true)));
		}
	

		
		return retorno;
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws BaseException
	 * @throws DocumentException
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {

		DocumentoJasper documento = gerarDocumento();

		// Validação foi feita desta forma pois as permissões "CI" e "C" são iguais em todos os perfis.
		
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

		String retorno = "";

		if (this.relatorioSimples) {
			retorno = "/br/gov/mec/aghu/blococirurgico/report/relatorioEscalaSimplesCirurgia.jasper";
		} else {
			retorno = "br/gov/mec/aghu/blococirurgico/report/relatorioEscalaCirurgiaComLinhas.jasper";
		}

		return retorno;

	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		if (this.relatorioSimples) {
			params.put("nomeRelatorio", "MBCR_ESCALA_SIMPLES");
		} else {
			params.put("nomeRelatorio", "MBCR_ESCALA_CIRURGIA");
		}

		this.titulo = blocoCirurgicoFacade.pesquisarTituloEscala(this.tipoEscala, dataCirurgia);
		params.put("escala", this.titulo);

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("unidade", this.unidadesFuncionais.getDescricao());
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/report/");

		return params;
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

	public String getTitulo() {
		return titulo;
	}

	public void setTitulo(String titulo) {
		this.titulo = titulo;
	}

	public DominioTipoEscala getTipoEscala() {
		return tipoEscala;
	}

	public void setTipoEscala(DominioTipoEscala tipoEscala) {
		this.tipoEscala = tipoEscala;
	}

	public Boolean getRelatorioSimples() {
		return relatorioSimples;
	}

	public void setRelatorioSimples(Boolean relatorioSimples) {
		this.relatorioSimples = relatorioSimples;
	}
}
