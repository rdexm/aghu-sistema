package br.gov.mec.aghu.estoque.pesquisa.action;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
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
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.vo.RelatorioDetalhesAjusteEstoqueVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class ImprimirDetalhesAjusteEstoqueController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(ImprimirDetalhesAjusteEstoqueController.class);

	private static final long serialVersionUID = 6205690148405047866L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	private Integer mmtSeq;

	/* Dados que serão impressos em PDF. */
	private List<Object> colecao = new ArrayList<Object>();

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @return
	 * @throws JRException
	 */
	public void directPrint() {
		try {
			colecao.add(estoqueFacade.obterDetalhesAjusteEstoque(this.mmtSeq));
			
			if (colecao.isEmpty()) {
				apresentarMsgNegocio(Severity.INFO,"MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
				return;
			}
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

			apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");

		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}

	@Override
	public Collection<Object> recuperarColecao() {
		return this.colecao;

	}

	@Override
	public Map<String, Object> recuperarParametros() {
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm", new Locale("pt", "BR"));
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();

		Map<String, Object> params = new HashMap<String, Object>();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "SCER_IMP_AJUSTE");
		params.put("tituloRelatorio", "Ajuste de Estoque");
		params.put("totalRegistros", colecao.size() - 1);

		final RelatorioDetalhesAjusteEstoqueVO detalhesAjuste = estoqueFacade.obterDetalhesAjusteEstoque(this.mmtSeq);

		params.put("almoxarifadoSeq", detalhesAjuste.getAlmoxarifadoSeq());
		params.put("almoxarifadoDesc", detalhesAjuste.getAlmoxarifadoDesc());
		params.put("materialCodigo", detalhesAjuste.getMaterialCodigo());
		params.put("materialDesc", detalhesAjuste.getMaterialDesc());
		params.put("fornecedorNumero", detalhesAjuste.getFornecedorNumero());
		params.put("fornecedorDesc", detalhesAjuste.getFornecedorDesc());
		params.put("quantidade", detalhesAjuste.getQuantidade());
		params.put("unidade", detalhesAjuste.getUnidade());
		params.put("tipoMovimentoSeq", detalhesAjuste.getTipoMovimentoSeq());
		params.put("tipoMovimentoCompl", detalhesAjuste.getTipoMovimentoCompl());
		params.put("tipoMovimentoDesc", detalhesAjuste.getTipoMovimentoDesc());
		params.put("motivoMovimentoSeq", detalhesAjuste.getMotivoMovimentoSeq());
		params.put("motivoMovimentoDesc", detalhesAjuste.getMotivoMovimentoDesc());
		params.put("geradoEm", detalhesAjuste.getGeradoEm());
		params.put("geradoPor", detalhesAjuste.getGeradoPor());

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/estoque/report/imprimirDetalhesAjusteEstoque.jasper";
	}

	/**
	 * Método invocado pelo a:mediaOutput para geração de PDF dentro de XHTML.
	 * 
	 * @param out
	 * @param data
	 * @throws IOException
	 * @throws SystemException
	 * @throws JRException
	 * @throws DocumentException
	 */
	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException {

		try {
			DocumentoJasper documento = gerarDocumento();
			return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return null;
		}
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}

	public Integer getMmtSeq() {
		return mmtSeq;
	}

	public void setMmtSeq(Integer mmtSeq) {
		this.mmtSeq = mmtSeq;
	}
}