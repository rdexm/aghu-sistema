package br.gov.mec.aghu.paciente.prontuarioonline.action;


import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.primefaces.model.StreamedContent;

import br.gov.mec.aghu.action.impressao.SistemaImpressao;
import br.gov.mec.aghu.action.report.ActionReport;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioGrupoProfissionalAnamnese;
import br.gov.mec.aghu.impressao.SistemaImpressaoException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.AtendimentosEmergenciaPOLVO;
//import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.paciente.vo.RelatorioAnaEvoInternacaoVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.SecurityController;
//import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.report.DocumentoJasper;

import com.itextpdf.text.DocumentException;

public class AtendimentoEmergenciaController extends ActionReport {

	private static final Log LOG = LogFactory.getLog(AtendimentoEmergenciaController.class);
	private static final long serialVersionUID = -6695986158190385683L;
	private static final String  PAGE_ATENDIMENTOS_POL = "atendimentosEmergenciaListPOL";


	@Inject
	private SistemaImpressao sistemaImpressao;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB	
	private ICascaFacade cascaFacade;
	
	@Inject
	private SecurityController securityController;	

	private Boolean temPermissaoChamarFiltroReportEmg;
	private String origem;
	private Integer atdSeq;
	private Long trgSeq;
	private Integer conNumero;
	private List<?> dadosRelatorio = null;
	private AtendimentosEmergenciaPOLVO atendimentoPOL;
	
	private DominioGrupoProfissionalAnamnese grupoProf;
	
	private Date dataInicio = new Date();	
	private Date dataFim = new Date();	
	private boolean exibeRelatorio = Boolean.FALSE;	
	private boolean exibeFiltro = Boolean.FALSE;
		
	private AghAtendimentos atendimento;
	
	private Integer prontuario;
	
	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	 temPermissaoChamarFiltroReportEmg = securityController.usuarioTemPermissao("chamarSemFiltroFiltroReportEmergenciaPOL", "habilitar");
	}
	
	public void inicio(){
		
		if(atendimentoPOL != null) {
			atdSeq = atendimentoPOL.getAtdSeq();
			setAtendimento(this.aghuFacade.obterAtendimentoPeloSeq(atendimentoPOL.getAtdSeq()));
			setProntuario(getAtendimento().getProntuario());
			setDataInicio(atendimentoPOL.getInicioTriagem());
			setDataFim(atendimentoPOL.getAlta());
			setConNumero(atendimentoPOL.getNumero());
			setTrgSeq(atendimentoPOL.getTrgSeq());
		}
		
		if(!temPermissaoChamarFiltroReportEmg){
			exibeRelatorio = Boolean.FALSE;
			exibeFiltro = Boolean.TRUE;
		}else{
			exibeRelatorio = Boolean.TRUE;
			exibeFiltro = Boolean.FALSE;			
		}
		pesquisar();	
	}

	public Boolean getTemPermissaoChamarFiltroReportEmg() {
		return temPermissaoChamarFiltroReportEmg;
	}

	public void setTemPermissaoChamarFiltroReportEmg(
			Boolean temPermissaoChamarFiltroReportEmg) {
		this.temPermissaoChamarFiltroReportEmg = temPermissaoChamarFiltroReportEmg;
	}

	@Override
	public String recuperarArquivoRelatorio() {
		return "br/gov/mec/aghu/bancodesangue/report/AnamneseEvolucaoInternacao.jasper";
	}
	
	private enum EnumMessagesRelatorioAtendimentoEmergencia{
		TITULO_RELATORIO_ATENDIMENTO_EMERGENCIA,
		RELATORIO_ATENDIMENTO_EMERGENCIA_VISUALIZACAO;
	}
	
	public StreamedContent getRenderPdf() throws ApplicationBusinessException, IOException, JRException, DocumentException{
		DocumentoJasper documento = gerarDocumento();
		return this.criarStreamedContentPdf(documento.getPdfByteArray(true)); // Protegido? = TRUE
	}
	
	
	public void directPrint() {
		try {
			DocumentoJasper documento = gerarDocumento(Boolean.TRUE);
			this.sistemaImpressao.imprimir(documento.getJasperPrint(), super.getEnderecoIPv4HostRemoto());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_IMPRESSAO");
		} catch (SistemaImpressaoException e) {
			apresentarExcecaoNegocio(e);
		} catch (Exception e) {
			this.apresentarMsgNegocio(Severity.ERROR, "ERRO_GERAR_RELATORIO");
		}
	}
	
	@Override
	public Collection<?> recuperarColecao() throws ApplicationBusinessException {
		return dadosRelatorio;
	}
	
	@Override
	public Map<String, Object> recuperarParametros() {	
		Map<String, Object> params = new HashMap<String, Object>();
		if (dadosRelatorio != null && !dadosRelatorio.isEmpty()) {
			RelatorioAnaEvoInternacaoVO param = (RelatorioAnaEvoInternacaoVO) dadosRelatorio.get(0);
			try {
				params.put("caminhoLogo", recuperarCaminhoLogo());
			} catch (BaseException e) {
				LOG.error("Erro ao tentar recuparar logotipo para o relatório",e);
			}
			params.put("tipoRelatorio", getBundle().getString(EnumMessagesRelatorioAtendimentoEmergencia.TITULO_RELATORIO_ATENDIMENTO_EMERGENCIA.toString()));
			params.put("rodape", param.getRodape());
			params.put("responsavel", param.getResponsavel());
			params.put("agenda", param.getAgenda());
			params.put("paciente", param.getPaciente());
			params.put("prontuario", param.getProntuario());
			params.put("visto", param.getVisto());
		}
		return params;
	}

	public void pesquisar() {
		try {
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado(), new Date());
			CseCategoriaProfissional categoriaProfissional = cascaFacade.primeiraCategoriaProfissional(servidorLogado);
			
			setDadosRelatorio(this.ambulatorioFacade.pesquisarRelatorioAnaEvoEmergencia(getTrgSeq(), getDataInicio(), getDataFim(), getGrupoProf(), getConNumero(), categoriaProfissional));
			if (!getDadosRelatorio().isEmpty()) {
				setExibeRelatorio(Boolean.TRUE);
			} else {
				apresentarMsgNegocio(Severity.WARN, "MSG_PESQUISA_NAO_RETORNOU_DADOS");		
				setExibeRelatorio(Boolean.FALSE);
			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public String voltar(){
		setDataFim(null);
		setDataInicio(null);
		setGrupoProf(null);
		setConNumero(null);		
		
		return PAGE_ATENDIMENTOS_POL ;
	}
	
	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
	
	public DominioGrupoProfissionalAnamnese getGrupoProf() {
		return grupoProf;
	}

	public void setGrupoProf(DominioGrupoProfissionalAnamnese grupoProf) {
		this.grupoProf = grupoProf;
	}
	
	public boolean isExibeRelatorio() {
		return exibeRelatorio;
	}

	public void setExibeRelatorio(boolean exibeRelatorio) {
		this.exibeRelatorio = exibeRelatorio;
	}
		
	public boolean isExibeFiltro() {
		return exibeFiltro;
	}

	public void setExibeFiltro(boolean exibeFiltro) {
		this.exibeFiltro = exibeFiltro;
	}
	
	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public List<?> getDadosRelatorio() {
		return dadosRelatorio;
	}

	public void setDadosRelatorio(List<?> dadosRelatorio) {
		this.dadosRelatorio = dadosRelatorio;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Long getTrgSeq() {
		return trgSeq;
	}

	public void setTrgSeq(Long trgSeq) {
		this.trgSeq = trgSeq;
	}

	public void setConNumero(Integer conNumero) {
		this.conNumero = conNumero;
	}

	public Integer getConNumero() {
		return conNumero;
	}


	public AtendimentosEmergenciaPOLVO getAtendimentoPOL() {
		return atendimentoPOL;
	}


	public void setAtendimentoPOL(AtendimentosEmergenciaPOLVO atendimentoPOL) {
		this.atendimentoPOL = atendimentoPOL;
	}
}
