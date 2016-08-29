package br.gov.mec.aghu.exames.questionario.action;

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
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.questionario.business.IQuestionarioExamesFacade;
import br.gov.mec.aghu.exames.questionario.vo.InformacaoComplementarVO;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;

import com.itextpdf.text.DocumentException;


/**
 * Controller para geração do relatório 'Informações Complementares'.
 * 
 * @author fpalma
 */

public class ImprimeInformacoesComplementaresController extends ActionReport {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}
	private static final Log LOG = LogFactory.getLog(ImprimeInformacoesComplementaresController.class);


	/**
	 * 
	 */
	private static final long serialVersionUID = -6034758665975842999L;

	@Inject
	private SistemaImpressao sistemaImpressao;

	@EJB
	private IQuestionarioExamesFacade questionarioExamesFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	protected IParametroFacade parametroFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	/**
	 *  Filtro de pesquisa
	 */
	private Integer soeSeq;
	private Short seqp;
	private Integer qtnSeq;
	
	private boolean mostrarMensagens = true;
		
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<InformacaoComplementarVO> colecao = new ArrayList<InformacaoComplementarVO>();
	
	
	/**
	 * Impressão direta usando o CUPS.
	 * 
	 * @return
	 * @throws JRException
	 * @throws SystemException
	 * @throws IOException
	 */
	@Override
	public void directPrint() {

		Integer pacCodigo = questionarioExamesFacade.obterPacCodigoPelaSolicitacao(soeSeq);
		
		try {
			this.questionarioExamesFacade.validarEnderecoPaciente(pacCodigo);
			this.colecao = questionarioExamesFacade.pesquisarInformacoesComplementares(pacCodigo, soeSeq, seqp, qtnSeq);
		} catch (ApplicationBusinessException e) {
			LOG.error("Erro ao carregar questionario para impressão SOE_SEQ:"+soeSeq);
			apresentarExcecaoNegocio(e);
			return;
		}
		
		if(colecao.isEmpty() && mostrarMensagens) {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INFORMACAO_COMPLEMENTAR_VAZIA");
			return;
		}

		Byte nroVias = questionarioExamesFacade.obterNumeroViasImpressaoInformacoesComplementares(soeSeq, seqp, qtnSeq);
		if(nroVias != 0) {
			for(int i=0; i < nroVias; i++) {
				try {
					DocumentoJasper documento = gerarDocumento();
					
					this.sistemaImpressao.imprimir(documento.getJasperPrint(),
							super.getEnderecoIPv4HostRemoto());
							
					this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Informação Complementar");
					
				} catch (SistemaImpressaoException e) {
					LOG.warn("Erro ao carregar questionario para impressão SOE_SEQ:"+soeSeq,e);
					apresentarExcecaoNegocio(e);
				} catch (Exception e) {
					LOG.error("Erro ao carregar questionario para impressão SOE_SEQ:"+soeSeq,e);
					this.apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
				}
			}
		} else {
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_INFORMACAO_NRO_VIAS");
		}
		
	}
	
	@Override
	public Collection<InformacaoComplementarVO> recuperarColecao() {
		
		return this.colecao;
	}
	
	public List<InformacaoComplementarVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<InformacaoComplementarVO> colecao) {
		this.colecao = colecao;
	}

	@Override
	public Map<String, Object> recuperarParametros() {

		Map<String, Object> params = new HashMap<String, Object>();
		Date dataAtual = new Date();
		SimpleDateFormat sdf_1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		Integer cnes = aghuFacade.recuperarCnesInstituicaoHospitalarLocal();
		params.put("dataAtual", sdf_1.format(dataAtual));
		params.put("hospitalLocal", hospital);
		params.put("nomeRelatorio", "AELR_INFO_COMPL");
		params.put("cnes", cnes);
		AelItemSolicitacaoExames item = examesFacade.buscaItemSolicitacaoExamePorId(soeSeq, seqp);
		DominioSimNao dominio = aghuFacade.verificarCaracteristicaDaUnidadeFuncional(item.getUnidadeFuncional().getSeq(),
				ConstanteAghCaractUnidFuncionais.HEMODINAMICA);
		params.put("pImpHem", DominioSimNao.S.equals(dominio) ? true : false);

		return params;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/exames/questionario/report/relatorioInformacoesComplementares.jasper";
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
	 * @throws ApplicationBusinessException 
	 */
	public StreamedContent getRenderPdf() throws IOException,
			JRException, SystemException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();

		return this.criarStreamedContentPdf(documento.getPdfByteArray(false));
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getQtnSeq() {
		return qtnSeq;
	}
	
	public void setQtnSeq(Integer qtnSeq) {
		this.qtnSeq = qtnSeq;
	}

	public void setMostrarMensagens(boolean mostrarMensagens) {
		this.mostrarMensagens = mostrarMensagens;
	}

	public boolean isMostrarMensagens() {
		return mostrarMensagens;
	}
	
}
