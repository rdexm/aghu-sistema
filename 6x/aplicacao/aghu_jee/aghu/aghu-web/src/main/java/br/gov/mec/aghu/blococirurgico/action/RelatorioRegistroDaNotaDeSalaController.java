package br.gov.mec.aghu.blococirurgico.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.vo.RelatorioRegistroDaNotaDeSalaVO;
import br.gov.mec.aghu.financeiro.centrocusto.business.ICentroCustoFacade;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcProfCirurgias;
import br.gov.mec.aghu.model.RapPessoasFisicas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;
import br.gov.mec.aghu.core.utils.DateUtil;

import com.itextpdf.text.DocumentException;


public class RelatorioRegistroDaNotaDeSalaController extends ActionReport {

	private static final String MENSAGEM_RELATORIO_NOTA_SALA_VAZIA = "MENSAGEM_RELATORIO_NOTA_SALA_VAZIA";

	private StreamedContent media;

	public StreamedContent getMedia() {			
		return media;
	}

	public void setMedia(StreamedContent media) {
		this.media = media;
	}

	private static final long serialVersionUID = -2710416739198005324L;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	@EJB
	private ICentroCustoFacade centroCustoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
		
	@Inject
	private SistemaImpressao sistemaImpressao;
	
	/*
	 * Parâmetros de conversação
	 */
	private Integer crgSeq;	   //Código da cirurgia
	private String voltarPara; //Controla a navegação do botão voltar
	
	/**
	 * Dados que serão impressos em PDF.
	 */
	private List<RelatorioRegistroDaNotaDeSalaVO> colecao = new ArrayList<RelatorioRegistroDaNotaDeSalaVO>();
	
	private static final Log LOG = LogFactory.getLog(RelatorioRegistroDaNotaDeSalaController.class);

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/relatorioRegistroDaNotaDeSala.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		
		String hospital = cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal();
		params.put("hospitalLocal", hospital);
		params.put("dataAtual", DateUtil.obterDataFormatada(new Date(), "dd/MM/yyyy HH:mm"));
		
		MbcCirurgias cirurgia = this.blocoCirurgicoFacade.obterCirurgiaPorSeq(this.crgSeq);
		String codigoCentroCustoStr = "";
		String descCentroCusto = "";
		
		FccCentroCustos centroCusto = null;
		if(cirurgia.getCentroCustos() == null) {
			centroCusto = this.obterCodigoCentroCustoResponsavel(cirurgia);
		} else {
			centroCusto = this.centroCustoFacade.obterCentroCustoPorChavePrimaria(cirurgia.getCentroCustos().getCodigo());
		}
		codigoCentroCustoStr = centroCusto.getCodigo().toString();
		descCentroCusto = centroCusto.getDescricao();
		
		AipPacientes paciente = this.pacienteFacade.obterAipPacientesPorChavePrimaria(cirurgia.getPaciente().getCodigo());
		
		String nome = "";
		String nomeUsual = "";
		if (cirurgia.getServidor() != null && cirurgia.getServidor().getId() !=null){
			RapServidores servidor = this.registroColaboradorFacade.obterRapServidor(cirurgia.getServidor().getId());
			
			if (servidor != null && servidor.getPessoaFisica() != null){
			    RapPessoasFisicas pesFis;
				try {
					pesFis = this.registroColaboradorFacade.obterPessoaFisica(servidor.getPessoaFisica().getCodigo());
					 if(pesFis != null){
					    	nome = pesFis.getNome();
					    	nomeUsual = pesFis.getNomeUsual();
					    }
				} catch (ApplicationBusinessException e) {
					LOG.error("Exceção capturada:", e);
				}
			}
		}
		params.put("cctCodigo", codigoCentroCustoStr);
		params.put("cctDescricao", descCentroCusto);
		params.put("pacNome", paciente.getNome());
		params.put("nroAgenda", cirurgia.getNumeroAgenda().toString());
		params.put("dtCirurgia", DateUtil.obterDataFormatada(cirurgia.getData(), "dd/MM/yyyy"));
		params.put("equipe", StringUtils.isEmpty(nomeUsual)
							? StringUtils.substring(nome, 0, 15)
							: nomeUsual);
		params.put("SUBREPORT_DIR","/br/gov/mec/aghu/blococirurgico/report/");
		
		params.put("pacProntuario", CoreUtil.formataProntuarioRelatorio(paciente.getProntuario()));
		
		return params;
	}
	
	//#34437
	private FccCentroCustos obterCodigoCentroCustoResponsavel(MbcCirurgias cirurgia) {
		MbcProfCirurgias profResponsavel = this.blocoCirurgicoFacade.retornaResponsavelCirurgia(cirurgia);
		if (profResponsavel != null) {
			return profResponsavel.getServidorPuc().getCentroCustoLotacao();
		} else {
			return null;
		}
	}
	
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<RelatorioRegistroDaNotaDeSalaVO> recuperarColecao()  {
		return this.colecao; 
	}
	
	public String visualizarImpressao() {
		try {
			this.colecao = blocoCirurgicoFacade.listarRegistroNotaDeSalaMateriais(this.crgSeq);
		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_NOTA_SALA_VAZIA);	
			return null;
		}
		if(colecao.isEmpty() || colecao.get(0).getSubRelatorioMateriais().isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_NOTA_SALA_VAZIA);
			return this.voltar();
		}
		return null;
	}
	
	
	public void directPrint() {
		try {
			this.colecao = blocoCirurgicoFacade.listarRegistroNotaDeSalaMateriais(this.crgSeq);
				
		} catch (Exception e) {
			e.getMessage();
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_NOTA_SALA_VAZIA);
			return;
		}
		
		if(colecao.isEmpty()) {
			apresentarMsgNegocio(Severity.INFO,MENSAGEM_RELATORIO_NOTA_SALA_VAZIA);
			return;
		}

		try {
			DocumentoJasper documento = gerarDocumento();

			this.sistemaImpressao.imprimir(documento.getJasperPrint(),
					super.getEnderecoIPv4HostRemoto());
		
			apresentarMsgNegocio(Severity.INFO,	"MENSAGEM_SUCESSO_IMPRESSAO_RELATORIO", "Registro da Nota de Sala");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			apresentarMsgNegocio(Severity.ERROR,
					"ERRO_GERAR_RELATORIO");
		}
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));			
	}
	
	/**
	 * Método chamado para o botão voltar
	 */
	public String voltar() {
		return this.voltarPara;
	}
		
	/**
	 * Getters and Setters
	 */
	public Integer getCrgSeq() {
		return crgSeq;
	}

	public void setCrgSeq(Integer crgSeq) {
		this.crgSeq = crgSeq;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public List<RelatorioRegistroDaNotaDeSalaVO> getColecao() {
		return colecao;
	}

	public void setColecao(List<RelatorioRegistroDaNotaDeSalaVO> colecao) {
		this.colecao = colecao;
	}
}
