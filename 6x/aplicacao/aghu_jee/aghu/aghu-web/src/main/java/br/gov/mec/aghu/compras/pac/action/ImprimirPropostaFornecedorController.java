package br.gov.mec.aghu.compras.pac.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
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
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.CotacaoPrecoVO;
import br.gov.mec.aghu.compras.vo.RelatorioCotacaoPrecoVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class ImprimirPropostaFornecedorController extends ActionReport {

	private StreamedContent media;

	public StreamedContent getMedia() {
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	

	private static final String COTAR_PRECO = "cotarPreco";

	private static final Log LOG = LogFactory.getLog(ImprimirPropostaFornecedorController.class);
	
	private static final long serialVersionUID = -1006442888234676013L;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IPacFacade pacFacade;
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	private List<CotacaoPrecoVO> listaVO = new ArrayList<CotacaoPrecoVO>();
	
	private List<RelatorioCotacaoPrecoVO> colecao = new LinkedList<RelatorioCotacaoPrecoVO>();

	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/relatorioModeloPropostaFornecedor.jasper";
	}

	public Collection<RelatorioCotacaoPrecoVO> recuperarColecao() throws ApplicationBusinessException {
		this.colecao = recuperaRelatorio();
		return this.colecao;
	}

	public void print() throws BaseException, JRException, SystemException,
			IOException, DocumentException {
		try {
			recuperarColecao();

			DocumentoJasper documento = gerarDocumento();
			media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.TRUE.booleanValue())));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Map<String, Object> recuperarParametros() {
		Map<String, Object> params = new HashMap<String, Object>();
		try {
			params.put("SUBREPORT_DIR", "br/gov/mec/aghu/compras/report/");
			params.put("hospitalCnpj","CNPJ "+ this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_CGC));
			params.put("hospitalLogradouro", this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO)+ " "+ this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_SALA_COMPRAS));
			params.put("hospitalEnderecoComplemento",this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_COMPLETO_LINHA2));
			params.put("nomeHospital",this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_RAZAO_SOCIAL));
			params.put("foneHospital","Fone: "+  this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_FONE));
			params.put("faxHospital","Fax: "+ this.parametroFacade.buscarValorTexto(AghuParametrosEnum.FAX_GRUM));
			params.put("caminhoLogoRodape", this.parametroFacade.recuperarCaminhoLogo());
			params.put("caminhoLogo", this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_AGHU_LOGO_HOSPITAL3_JEE7));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}	
		return params;
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.TRUE.booleanValue()));
	}

	private List<RelatorioCotacaoPrecoVO> recuperaRelatorio()
			throws ApplicationBusinessException {
		RapServidores colaborador = null;
		List<RelatorioCotacaoPrecoVO> lista = new LinkedList<RelatorioCotacaoPrecoVO>();
		RelatorioCotacaoPrecoVO relatorio = new RelatorioCotacaoPrecoVO();
		List<CotacaoPrecoVO> listaCotacoes = new ArrayList<CotacaoPrecoVO>();
		listaCotacoes.addAll(getListaVO());
		relatorio.setNumeroCotacao(this.pacFacade.gerarIdentificacaoCotacao());
		relatorio.setHospitalEnderecoCidadeData(montarEndereco());
		relatorio.setListaCotacoes(listaCotacoes);
		try {
			colaborador = this.registroColaboradorFacade.obterServidorAtivoPorUsuarioSemCache(obterLoginUsuarioLogado());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		if (colaborador != null) {
			relatorio.setUsuario(colaborador.getPessoaFisica().getNome());
		}
		lista.add(relatorio);
		return lista;
	}

	private String montarEndereco() {
		Calendar cal = Calendar.getInstance();

		String cidadeDataAtualPorExtenso = null;
		try {
			String cidade = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
			cidadeDataAtualPorExtenso = cidade + ", " + cal.get(Calendar.DAY_OF_MONTH) + " de "+ nomeDoMes(cal.get(Calendar.MONTH)) + " de " + cal.get(Calendar.YEAR);
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		return cidadeDataAtualPorExtenso;
	}

	private String nomeDoMes(int i) {
		String[] mes = { "Janeiro", "Fevereiro", "Março", "Abril", "Maio",
				"Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro",
				"Dezembro" };
		return mes[i];
	}


	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());

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

	public String voltar() {
		return COTAR_PRECO;
	}

	public List<CotacaoPrecoVO> getListaVO() {
		return this.listaVO;
	}

	public void setListaVO(List<CotacaoPrecoVO> listaVO) {
		this.listaVO = listaVO;
	}
}