package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioProdutividadePorAnestesistaVO;
import br.gov.mec.aghu.dominio.DominioFuncaoProfissional;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class RelatorioProdutividadePorAnestesistaController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = 4809694453702618502L;

	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;

	private AghUnidadesFuncionais unidadeCirurgica;
	private DominioFuncaoProfissional funcaoProfissional;
	private Date dataInicial;
	private Date dataFinal;
	
	private static final String RELATORIO = "relatorioProdutividadePorAnestesista";
	private static final String RELATORIO_PDF = "relatorioProdutividadePorAnestesistaPdf";
	
	private Collection<RelatorioProdutividadePorAnestesistaVO> colecao;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	public List<AghUnidadesFuncionais> listarUnidadesFuncionaisPorUnidadeExecutora(
			final String strPesquisa) {
		return this.returnSGWithCount(this.blocoCirurgicoFacade
				.listarUnidadesFuncionaisPorUnidadeExecutora((String) strPesquisa, false),listarUnidadesFuncionaisPorUnidadeExecutoraCount(strPesquisa));
	}

	public Long listarUnidadesFuncionaisPorUnidadeExecutoraCount(
			final String strPesquisa) {
		return this.blocoCirurgicoFacade
				.listarUnidadesFuncionaisPorUnidadeExecutoraCount((String) strPesquisa, false);
	}

	public String voltar() {
		return RELATORIO;
	}
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioProdutividadePorAnestesista.jasper";
	}

	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
//		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
//		params.put("hospitalLocal", hospital);
		try {			
			AghParametros parametroRazaoSocial = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL);
			params.put("hospitalLocal", parametroRazaoSocial.getVlrTexto());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		params.put("SUBREPORT_DIR","br/gov/mec/aghu/blococirurgico/report/");
		params.put("unidadeCirurgica", unidadeCirurgica.getDescricao());
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yy HH:mm", new Locale("pt", "BR"));
		SimpleDateFormat sdf_2 = new SimpleDateFormat("dd/MM/yyyy", new Locale("pt", "BR"));
		params.put("dataAtual", sdf_1.format(dataAtual));
		StringBuffer sb = new StringBuffer();
		sb.append(sdf_2.format(dataInicial)).append(" A ").append(sdf_2.format(dataFinal));
		params.put("periodo", "PRODUTIVIDADE POR ANESTESISTA NO PERÍODO DE : "+sb.toString());
		return params;
	}

	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioProdutividadePorAnestesistaVO> recuperarColecao() {
		return colecao;
	}
	
	public void carregarColecao() throws ApplicationBusinessException {
		colecao = blocoCirurgicoFacade.listarProdutividadePorAnestesista(unidadeCirurgica, funcaoProfissional, dataInicial, dataFinal);
	}

	public String print()throws JRException, IOException, DocumentException{
		String retorno = null;
		try {
			blocoCirurgicoFacade.validarIntervaloDatasPesquisa(dataInicial, dataFinal, AghuParametrosEnum.P_AGHU_INTERVALO_REL_PROD_ANESTESISTAS);
			carregarColecao();
			if(colecao == null || colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			} else {
				DocumentoJasper documento = gerarDocumento();
				media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE)));
				retorno = RELATORIO_PDF;
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	
		
		return retorno;
	}

	public void directPrint() {
		try {
			blocoCirurgicoFacade.validarIntervaloDatasPesquisa(dataInicial, dataFinal, AghuParametrosEnum.P_AGHU_INTERVALO_REL_PROD_ANESTESISTAS);
			carregarColecao();
			if(colecao == null || colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
			} else {
				try {
					DocumentoJasper documento = gerarDocumento();
					this.sistemaImpressao.imprimir(documento.getJasperPrint(),
							super.getEnderecoIPv4HostRemoto());
					apresentarMsgNegocio(Severity.INFO,
							"MENSAGEM_SUCESSO_IMPRESSAO");
	
				} catch (SistemaImpressaoException e) {
					apresentarExcecaoNegocio(e);
				} catch (Exception e) {
					apresentarMsgNegocio(Severity.ERROR,
							"ERRO_GERAR_RELATORIO");
				}
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public StreamedContent getRenderPdf() throws IOException,
			JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE));
	}

	public void limparCampos() {
		unidadeCirurgica = null;
		funcaoProfissional = null;
		dataInicial = null;
		dataFinal = null;
	}
	
	public DominioFuncaoProfissional[] listarSituacoes(){
        DominioFuncaoProfissional tipos[] = {DominioFuncaoProfissional.ANP, DominioFuncaoProfissional.ANR, DominioFuncaoProfissional.ANC};
        return tipos;
	}

	// Getters and Setters
	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}
	
	public void setBlocoCirurgicoFacade(
			IBlocoCirurgicoFacade blocoCirurgicoFacade) {
		this.blocoCirurgicoFacade = blocoCirurgicoFacade;
	}

	public IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	public ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return cadastrosBasicosInternacaoFacade;
	}

	public void setCadastrosBasicosInternacaoFacade(
			ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade) {
		this.cadastrosBasicosInternacaoFacade = cadastrosBasicosInternacaoFacade;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Collection<RelatorioProdutividadePorAnestesistaVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			Collection<RelatorioProdutividadePorAnestesistaVO> colecao) {
		this.colecao = colecao;
	}

	public DominioFuncaoProfissional getFuncaoProfissional() {
		return funcaoProfissional;
	}

	public void setFuncaoProfissional(DominioFuncaoProfissional funcaoProfissional) {
		this.funcaoProfissional = funcaoProfissional;
	}
}
