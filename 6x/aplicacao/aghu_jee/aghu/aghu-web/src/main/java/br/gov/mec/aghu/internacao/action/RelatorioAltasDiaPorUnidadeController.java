package br.gov.mec.aghu.internacao.action;

import java.io.IOException;
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
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AltasPorUnidadeVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

/***
 * @author lalegre
 * 
 * */
public class RelatorioAltasDiaPorUnidadeController extends ActionReport {

	private static final long serialVersionUID = 2L;

	private static final Log LOG = LogFactory.getLog(RelatorioAltasDiaPorUnidadeController.class);

	private static final String RELATORIO_ALTAS_DIA_POR_UNIDADE_PDF = "relatorioAltasDiaPorUnidadePdf";
	private static final String RELATORIO_ALTAS_DIA_POR_UNIDADE = "relatorioAltasDiaPorUnidade";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@PostConstruct
	public void inicio() {
		begin(conversation);
	}

	/**
	 * Data de referencia
	 */
	private Date dataDeReferencia = new Date();

	/***
	 * 
	 * Código do Grupo Convênio
	 * 
	 * */
	private DominioGrupoConvenio grupoConvenio;

	/**
	 * Codigo AghUnidadesFuncionais
	 */
	private Integer codigoUnidadesFuncionais;

	/**
	 * Lista de AghUnidadesFuncionais
	 */
	private List<AghUnidadesFuncionais> listaUnidadesFuncionais = new ArrayList<AghUnidadesFuncionais>();

	/**
	 * Descricao AghUnidadesFuncionais
	 */
	private String descricaoUnidadesFuncionais;

	/**
	 * LOV Unidade
	 */
	private AghUnidadesFuncionais unidadesFuncionais = null;

	@Override
	public String recuperarArquivoRelatorio() {
		return "/br/gov/mec/aghu/internacao/report/relatorioAltasPorUnidade.jasper";
	}

	@Override
	public Collection<AltasPorUnidadeVO> recuperarColecao() throws ApplicationBusinessException {

		try {
			return internacaoFacade.pesquisaAltasPorUnidade(
					dataDeReferencia,
					grupoConvenio, 
					unidadesFuncionais != null ? unidadesFuncionais.getSeq().intValue() : null);
			
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		
		return null;
		
	}

	/**
	 * Método responsável por gerar a coleção de dados.
	 * 
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public String print() {
		return "relatorio";
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @param pacienteProntuario
	 * @return
	 * @throws MECBaseException
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	public String imprimirRelatorio() {
		try {
			directPrint();
		} catch (Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
		return null;
	}

	public String visualizarRelatorio() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		return RELATORIO_ALTAS_DIA_POR_UNIDADE_PDF;
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
	public StreamedContent getRenderPdf() throws IOException, ApplicationBusinessException, JRException, SystemException, DocumentException {
		DocumentoJasper documento =  gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		SimpleDateFormat dataHora = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		params.put("nomeRelatorio", "AINR_ALTAS_UNID");

		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		// params.put("nomeHospital", "Hospital de Clínicas de Porto Alegre");

		params.put("dtInicial", "Altas registradas em " + data.format(dataDeReferencia));
		params.put("dtHoje", dataHora.format(new Date()));

		return params;
	}

	/**
	 * Pesquisa AghUnidadesFuncionais para lista de valores.
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String param) {
		this.listaUnidadesFuncionais = this.aghuFacade.pesquisarUnidadeFuncionalPorUnidEmergencia(param, false);
		return this.listaUnidadesFuncionais;
	}

	public String voltar() {
		return RELATORIO_ALTAS_DIA_POR_UNIDADE;
	}

	public DominioGrupoConvenio getGrupoConvenio() {
		return grupoConvenio;
	}

	public void setGrupoConvenio(DominioGrupoConvenio grupoConvenio) {
		this.grupoConvenio = grupoConvenio;
	}

	public Integer getCodigoUnidadesFuncionais() {
		return codigoUnidadesFuncionais;
	}

	public void setCodigoUnidadesFuncionais(Integer codigoUnidadesFuncionais) {
		this.codigoUnidadesFuncionais = codigoUnidadesFuncionais;
	}

	public AghUnidadesFuncionais getUnidadesFuncionais() {
		return unidadesFuncionais;
	}

	public void setUnidadesFuncionais(AghUnidadesFuncionais unidadesFuncionais) {
		this.unidadesFuncionais = unidadesFuncionais;
	}

	public String getDescricaoUnidadesFuncionais() {
		return descricaoUnidadesFuncionais;
	}

	public void setDescricaoUnidadesFuncionais(String descricaoUnidadesFuncionais) {
		this.descricaoUnidadesFuncionais = descricaoUnidadesFuncionais;
	}

	public List<AghUnidadesFuncionais> getListaUnidadesFuncionais() {
		return listaUnidadesFuncionais;
	}

	public void setListaUnidadesFuncionais(List<AghUnidadesFuncionais> listaUnidadesFuncionais) {
		this.listaUnidadesFuncionais = listaUnidadesFuncionais;
	}

	public Date getDataDeReferencia() {
		return dataDeReferencia;
	}

	public void setDataDeReferencia(Date dataDeReferencia) {
		this.dataDeReferencia = dataDeReferencia;
	}
}