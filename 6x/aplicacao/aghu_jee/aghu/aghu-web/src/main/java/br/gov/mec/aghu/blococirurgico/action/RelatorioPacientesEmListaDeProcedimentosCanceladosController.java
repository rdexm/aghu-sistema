package br.gov.mec.aghu.blococirurgico.action;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.action.PesquisaAgendaCirurgiaController;
import br.gov.mec.aghu.blococirurgico.vo.PacientesEmListaDeProcedimentosCanceladosVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;


public class RelatorioPacientesEmListaDeProcedimentosCanceladosController extends ActionReport {

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

	private static final Log LOG = LogFactory.getLog(RelatorioPacientesEmListaDeProcedimentosCanceladosController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -1006442448234636014L;

	private static final String PESQUISA_AGENDA = "blococirurgico-pesquisaAgendaCirurgia";
	private static final String RELATORIO_PDF = "blococirurgico-relatorioPacientesEmListaDeProcedimentosCanceladosPdf";
	
	@Inject
	private SistemaImpressao sistemaImpressao;
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	@EJB
	private IAghuFacade aghuFacade;
	@Inject
	private PesquisaAgendaCirurgiaController pesquisaAgendaCirurgiaController;
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	private AghUnidadesFuncionais unidadeCirurgica;
	private AghEspecialidades especialidade;
	private MbcProfAtuaUnidCirgs equipe;
	private Integer pacCodigo;
	private Date dataCirurgia;
	private Boolean pacienteInternado;
	private Collection<PacientesEmListaDeProcedimentosCanceladosVO> colecao;
	
	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/blococirurgico/report/pacientesEmListaDeProcedimentosCancelados.jasper";
	}
	
	/**
	 * Retorna os parametros utilizados no relatorio
	 */
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("hospitalLocal", cadastrosBasicosInternacaoFacade.recuperarNomeInstituicaoHospitalarLocal());
		
		try {
			params.put("caminhoLogo", recuperarCaminhoLogo2());
		} catch (BaseException e) {
			LOG.error("Erro ao tentar recuperar logotipo para o relatório", e);
		}
		
		RapServidores nomeEquipe = this.registroColaboradorFacade.buscaServidor(equipe.getRapServidores().getId());
		
		if (nomeEquipe != null) {
			params.put("equipe", "Equipe: " + nomeEquipe.getPessoaFisica().getNomeUsualOuNome());	
		}
		
		Date dataAtual = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		params.put("dataAtual", sdf.format(dataAtual));
		
		params.put("nomeRelatorio", getBundle().getString("NOME_RELATORIO_PACIENTES_EM_LISTA_DE_PROCEDIMENTOS_CANCELADOS"));		
		params.put("unidadeEspecialidade", obterUnidadeEspecialidade());
		params.put("idRelatorio", "MBCR_LIST_CANCELADAS");
		
		return params;
	}
	
	private String obterUnidadeEspecialidade() {
		StringBuilder retorno = new StringBuilder();
		
		if (this.aghuFacade.verificarCaracteristicaUnidadeFuncional(unidadeCirurgica.getSeq(),
				ConstanteAghCaractUnidFuncionais.BLOCO)) {
			retorno.append("Bloco");
		} else if (this.aghuFacade.verificarCaracteristicaUnidadeFuncional(unidadeCirurgica.getSeq(),
				ConstanteAghCaractUnidFuncionais.CCA)) {
			retorno.append("CCA");
		} else if (this.aghuFacade.verificarCaracteristicaUnidadeFuncional(unidadeCirurgica.getSeq(),
				ConstanteAghCaractUnidFuncionais.HEMODINAMICA)) {
			retorno.append("Hemodinâmica");
		}
		
		retorno.append(" - " ).append( especialidade.getNomeEspecialidade());
		
		return retorno.toString();
	}
		
	/**
	 * Recupera a coleção utilizada no relatório
	 */
	@Override
	public Collection<PacientesEmListaDeProcedimentosCanceladosVO> recuperarColecao() throws ApplicationBusinessException {
		return colecao; 
	}
	
	public String print()throws JRException, IOException, DocumentException, ApplicationBusinessException {
		carregarColecao();
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		}
	
		DocumentoJasper documento = gerarDocumento();
		media = new DefaultStreamedContent(new ByteArrayInputStream(documento.getPdfByteArray(Boolean.FALSE)));			
		return RELATORIO_PDF;
	}

	public void directPrint() {
		carregarColecao();
		if(colecao.isEmpty()) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NENHUM_REGISTRO_ENCONTRADO_PESQUISA");
		}else{
			try {
				DocumentoJasper documento = gerarDocumento();
				this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
				
			} catch (SistemaImpressaoException e) {
				apresentarExcecaoNegocio(e);
			} catch (Exception e) {
				this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
			}
		}
	}

	public String voltar() {
		return PESQUISA_AGENDA;
	}
	
	public StreamedContent getRenderPdf() throws IOException, JRException, DocumentException, ApplicationBusinessException {
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(Boolean.FALSE));			
	}
	
	public void carregarColecao() {
		carregarParametros();
		try {
			colecao = blocoCirurgicoFacade.obterPacientesEmListaDeProcedimentosCancelados(equipe, especialidade.getSeq(), unidadeCirurgica.getSeq(), pacCodigo);
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	public void carregarParametros() {
		this.unidadeCirurgica = pesquisaAgendaCirurgiaController.getUnidadeFuncional();
		this.equipe = pesquisaAgendaCirurgiaController.getEquipe();
		this.especialidade = pesquisaAgendaCirurgiaController.getEspecialidade();
		this.pacCodigo = pesquisaAgendaCirurgiaController.getPacCodigo();
	}
	
	//Getters and Setters
	public void setUnidadeCirurgica(AghUnidadesFuncionais unidadeCirurgica) {
		this.unidadeCirurgica = unidadeCirurgica;
	}

	public AghUnidadesFuncionais getUnidadeCirurgica() {
		return unidadeCirurgica;
	}

	public void setDataCirurgia(Date dataCirurgia) {
		this.dataCirurgia = dataCirurgia;
	}

	public Date getDataCirurgia() {
		return dataCirurgia;
	}

	public void setPacienteInternado(Boolean pacienteInternado) {
		this.pacienteInternado = pacienteInternado;
	}

	public Boolean getPacienteInternado() {
		return pacienteInternado;
	}

	public SistemaImpressao getSistemaImpressao() {
		return sistemaImpressao;
	}

	public void setSistemaImpressao(SistemaImpressao sistemaImpressao) {
		this.sistemaImpressao = sistemaImpressao;
	}	

	public Collection<PacientesEmListaDeProcedimentosCanceladosVO> getColecao() {
		return colecao;
	}

	public void setColecao(
			Collection<PacientesEmListaDeProcedimentosCanceladosVO> colecao) {
		this.colecao = colecao;
	}

	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	public MbcProfAtuaUnidCirgs getEquipe() {
		return equipe;
	}

	public void setEquipe(MbcProfAtuaUnidCirgs equipe) {
		this.equipe = equipe;
	}
	
	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}
}
