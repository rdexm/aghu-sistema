package br.gov.mec.aghu.compras.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
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
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.vo.ScoFornecedorVO;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.ScoRefCodes;

import com.itextpdf.text.DocumentException;


public class RelatorioCertificadoRegistroCadastroController extends ActionReport {

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

	private static final long serialVersionUID = 7676544193949867124L;
	private Integer numeroFrn;
	private String voltarPara;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private SistemaImpressao sistemaImpressao;

	private List<ScoFornecedorVO> colecao;
	private static final String PAGE_CERTIFICADO_REGISTRO_CADASTRAL = "certificadoRegistroCadastral";
	private static final Log LOG = LogFactory.getLog(RelatorioCertificadoRegistroCadastroController.class);

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/compras/report/certificadoRegistroCadastro.jasper";
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> param = new HashMap<String, Object>();
		try {
			List<ScoFornecedorVO> listVO = this.comprasFacade.buscarCertificadoDeRegistroDeCadastro(numeroFrn); //TODO resultado da C1 aqui 
			
			ScoFornecedorVO vo = bindValoresLista(listVO);
			if (vo != null) {
				param.put("cgc", vo.getCgc() != null ? vo.getCgc().toString()
						: "");
				param.put("cpf", vo.getCpf() != null ? vo.getCpf().toString()
						: "");
				param.put("crc", vo.getCrc());
				param.put("razaoSocial", vo.getRazaoSocial());
				param.put("dtValidadeCrc", vo.getDtValidadeCrc());
				param.put("dtValidadeFgts", vo.getDtValidadeFgts());
				param.put("dtValidadeInss", vo.getDtValidadeInss());
				param.put("dtValidadeRecEst", vo.getDtValidadeRecEst());
				param.put("dtValidadeBal", vo.getDtValidadeBal());
				param.put("dtValidadeAvs", vo.getDtValidadeAvs());
				param.put("dtValidadeRecMun", vo.getDtValidadeRecMun());
				param.put("dtValidadeCndt", vo.getDtValidadeCndt());// DT_VALIDADE_CNDT
				param.put("dtValidadeRecFed", vo.getDtValidadeRecFed());
				param.put("indAfe", vo.getIndAfe());
				param.put("logradouro", vo.getLogradouro());
				param.put("nroLogradouro", vo.getNroLogradouro());
				param.put("bairro", vo.getBairro());
				param.put("cep", vo.getCep());
				param.put("cidade", vo.getCidade());
				param.put("ufSigla", vo.getUfSigla());
				param.put("rvMeaning", vo.getClassificacaoEconomicaFornecedor());
				param.put("descricao", vo.getDescricao());
				param.put("hospitalCidadeEstado", vo.getHospitalCidadeEstado());
				String cidade = parametroFacade
						.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CIDADE);
				String uf = parametroFacade
						.buscarValorTexto(AghuParametrosEnum.P_AGHU_UF_SEDE_HU);
				param.put("hospitalCidadeEstado", cidade + " - " + uf);
				Calendar cal = Calendar.getInstance();
				String cidadeDataAtualPorExtenso = cidade + ", "
						+ cal.get(Calendar.DAY_OF_MONTH) + " de "
						+ nomeDoMes(cal.get(Calendar.MONTH), 0) + " de "
						+ cal.get(Calendar.YEAR);
				param.put("cidadeDataAtualPorExtenso",
						cidadeDataAtualPorExtenso);
				param.put(
						"comissaoLicitacao",
						parametroFacade
								.buscarValorTexto(AghuParametrosEnum.P_AGHU_NOME_COMISSAO_LICITACOES_PAC));
				param.put(
						"nomeCoordenadorComissaoLicitacao",
						parametroFacade
								.buscarValorTexto(AghuParametrosEnum.P_COMIS_LICT_MEMBRO_1));
				param.put(
						"hospitalCep",
						parametroFacade
								.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_CEP));
				param.put(
						"hospitalLogradouro",
						parametroFacade
								.buscarValorTexto(AghuParametrosEnum.P_HOSPITAL_END_LOGRADOURO));
				param.put("logoHospital", parametroFacade.recuperarCaminhoLogo());
				
				
				List<ScoRefCodes> codes = this.comprasFacade
						.obterClassificacaoEconomica("CATEGORIA_EMP",
								vo.getClassificacaoEconomicaFornecedor());
				if (codes != null && codes.size() > 0) {
					if (codes.get(0) != null){
						param.put("rvMeaning", codes.get(0).getRvMeaning());
					}
				}
			}
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao tentar obter parâmetro...", e);
		}

		return param;
	}	

	private ScoFornecedorVO bindValoresLista(List<ScoFornecedorVO> listVO) {
		return this.comprasFacade.bindValoresLista(listVO);
	}

	public StreamedContent getRenderPdf() throws IOException, JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public void imprimir() throws IOException, BaseException, JRException, SystemException, DocumentException{
		// TODO CHAMAR MÉTODO DE DOWNLOAD internDispararDownload PASSANDO ARQUIVO
	}

	public String getVoltar(){
		return PAGE_CERTIFICADO_REGISTRO_CADASTRAL;
	}

	public void setColecao(List<ScoFornecedorVO> colecao) {
		this.colecao = colecao;
	}

	public List<ScoFornecedorVO> getColecao() {
		return colecao;
	}	

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setNumeroFrn(Integer numeroFrn) {
		this.numeroFrn = numeroFrn;
	}

	public Integer getNumeroFrn() {
		return numeroFrn;
	}

	//METODO OBRIGATORIO PARA GERAR O RELATORIO MAS NAO É OBRIGATÓRIO DE SOBRESCREVER
	@Override
	public Collection<String> recuperarColecao() throws ApplicationBusinessException {
		List<String> lista = new ArrayList<String>();
		lista.add("1");
		return lista;
	}

	private static String nomeDoMes(int i, int tipo) { 
		String mes[] = {"janeiro", "fevereiro", "março", "abril", "maio", "junho", "julho", "agosto", "setembro", "outubro", "novembro", "dezembro"};
		if (tipo == 0) {
			return(mes[i-1]);
		} else{ 
			return(mes[i-1].substring(0, 3));
		}
	}

	public void directPrint(){
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

}
