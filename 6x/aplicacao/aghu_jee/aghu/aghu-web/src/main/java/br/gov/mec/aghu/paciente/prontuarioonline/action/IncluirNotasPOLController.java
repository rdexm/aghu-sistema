package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import br.gov.mec.aghu.certificacaodigital.business.ICertificacaoDigitalFacade;
import br.gov.mec.aghu.dominio.DominioTipoDocumento;
import br.gov.mec.aghu.model.AghDocumentoCertificado;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamNotaAdicionalEvolucoes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class IncluirNotasPOLController extends ActionController implements ActionPaginator {
	private static final long serialVersionUID = -4501804791299883377L;
	private static final String PAGE_DIAGNOSTICOS="prescricaomedica-manterDiagnosticosPaciente";
	private static final String PAGE_VISUALIZAR_DOC_ASSINADO="certificacaodigital-visualizarDocumentoAssinado";
	private static final String PAGE_NOTAS_POL_LIST_PDF="pol-incluirNotasPOLListPdf";
	
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;

	@Inject
	private RelatorioNotasController relatorioNotasController;

	@EJB
	private ICertificacaoDigitalFacade certificacaoDigitalFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	
	@Inject
	private SecurityController securityController;
	
	/**
	 * Número do prontuário do paciente, obtido via page parameter.
	 */
	private Integer numeroProntuario;
	
	private MamNotaAdicionalEvolucoes registroSelecionado = new MamNotaAdicionalEvolucoes();	
	private MamNotaAdicionalEvolucoes registroInclusao;
	
	private String textoIncluiNota;

	/**
	 * Paciente consultado.
	 */
	private AipPacientes paciente;	
	private Boolean botaoAdicionar;	
	private Boolean botaoDiagnosticos;
	
	//private Integer atdSeq;

	private Integer seqVersaoDoc;
	private Integer seqMamNotaAdicionalEvolucoes;
	
	private Boolean permiteExcluirNotaAdicionalEvolucao;
	@Inject @Paginator
	private DynamicDataModel<MamNotaAdicionalEvolucoes> dataModel;
	
	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;		
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
		inicio();
	}	

	public void inicio() {
		
		if (itemPOL!=null){
			numeroProntuario=itemPOL.getProntuario();
		}
		
		permiteExcluirNotaAdicionalEvolucao = securityController.usuarioTemPermissao("permiteExcluirNotaAdicionalEvolucao", "excluir");
		paciente = this.pacienteFacade.obterPacientePorProntuario(numeroProntuario);
		
		try {
			botaoAdicionar = this.prontuarioOnlineFacade.validarBotaoAdicionarIncluirNotasPOL();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		try {
			botaoDiagnosticos = this.prontuarioOnlineFacade.habilitarBotaoDiagnostico();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		
		dataModel.reiniciarPaginator();
		registroSelecionado = new MamNotaAdicionalEvolucoes();
		
	}
	
	@Override
	public Long recuperarCount() {
		return this.prontuarioOnlineFacade.pesquisarNotasPorCodigoPacienteCount(paciente.getCodigo());
	}

	@Override
	public List<MamNotaAdicionalEvolucoes> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		try {
			List<MamNotaAdicionalEvolucoes> list = this.prontuarioOnlineFacade.pesquisarNotasPorCodigoPaciente(paciente.getCodigo(), firstResult, maxResult, orderProperty, asc); 
			for(MamNotaAdicionalEvolucoes nota : list){
				getConsProf(nota);
			}
			
			return list;
		} catch ( BaseException e) {
			apresentarExcecaoNegocio(e);
			return new ArrayList<MamNotaAdicionalEvolucoes>();		
		}
	}	
	
	public void visualizar(MamNotaAdicionalEvolucoes registroCorrente) {
		registroSelecionado = registroCorrente;
	}

	
	public String getConsProf(MamNotaAdicionalEvolucoes nota) throws ApplicationBusinessException{
		if (nota.getNomeConsProf()==null){
			nota.setNomeConsProf((String) prescricaoMedicaFacade.buscaConsProf(nota.getServidorValida())[1]);			
		}
		return nota.getNomeConsProf();
	}
	
	
	public void incluir() {
		try {
			validarInclusaoNota();
			MamNotaAdicionalEvolucoes nota = this.prontuarioOnlineFacade.incluirNotaPOL(textoIncluiNota, paciente);
			textoIncluiNota = null;
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_NOTA_ADICIONADA_COM_SUCESSO");
			
			getRelatorioNotasController().gerarDocumentoCertificado(nota);
			/*Boolean usuarioHabilitado = getCertificacaoDigitalFacade().verificarServidorHabilitadoCertificacaoDigitalUsuarioLogado();
			if (usuarioHabilitado) {
				gerarDocumentoCertificado(nota);
			}*/
			closeDialog("modalIncluirWG");
			
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	private void validarInclusaoNota() throws ApplicationBusinessException {
		if(textoIncluiNota == null || "".equals(textoIncluiNota)){
			throw new ApplicationBusinessException("MENSAGEM_TEXTO_NOTA_OBRIGATORIO", Severity.ERROR);
		}
	}

	private ICertificacaoDigitalFacade getCertificacaoDigitalFacade() {
		return certificacaoDigitalFacade;
	}

	/*private void gerarDocumentoCertificado(MamNotaAdicionalEvolucoes nota) throws BaseException {
		getRelatorioNotasController().setNota(nota);
		getRelatorioNotasController().gerarDocumento(DominioTipoDocumento.NPO);
	}*/
	
	private void excluirDocumentoCertificado(Integer seq) throws ApplicationBusinessException {
		AghDocumentoCertificado documento = getCertificacaoDigitalFacade().verificarRelatorioNecessitaAssinatura(getRelatorioNotasController().recuperarArquivoRelatorio(), DominioTipoDocumento.NPO);
		if (documento != null && !documento.equals("")) {
			getProntuarioOnlineFacade().aippInativaDocCertif(seq);
		}
	}

	private RelatorioNotasController getRelatorioNotasController() {
		return relatorioNotasController;
	}

	public void justificarExclusao(MamNotaAdicionalEvolucoes registroCorrente){
		registroSelecionado = registroCorrente;
		textoIncluiNota = null;
	}
	
	
	public void gravarJustificarExclusao(){
		try {
			validaObrigatoriedadeJustificativa();
			this.prontuarioOnlineFacade.gravarJustificarExclusaoIncluirNotasPol(registroSelecionado);
			excluirDocumentoCertificado(registroSelecionado.getSeq());
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_EXCLUSAO_NOTA_SUCESSO");
			closeDialog("modalExcluirWG");
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		registroSelecionado = new MamNotaAdicionalEvolucoes();
		textoIncluiNota = null;
	}
	
	
	private void validaObrigatoriedadeJustificativa() throws ApplicationBusinessException {
		if(registroSelecionado.getJustExclusao() == null || registroSelecionado.getJustExclusao().trim().isEmpty()){
			throw new ApplicationBusinessException("MESSAGE_JUSTIFICATIVA_OBRIGATORIA_NOTAL_POL", Severity.ERROR);
		}	
	}
	
	public void limparJustificativaExclusao(){
		registroSelecionado.setJustExclusao(null);
	}
	
	public void limparIncluirNota(){
		textoIncluiNota = null;
	}
	
	public String visualizaRelatorioIncluirNotasPol(){
		seqVersaoDoc = getProntuarioOnlineFacade().recuperarVersaoDoc(seqMamNotaAdicionalEvolucoes);
		
		if(getProntuarioOnlineFacade().visualizarRelatorio(seqMamNotaAdicionalEvolucoes)){
			return PAGE_VISUALIZAR_DOC_ASSINADO;
		}else{
			getRelatorioNotasController().setSeqMamNotaAdicionalEvolucoes(seqMamNotaAdicionalEvolucoes);		
			getRelatorioNotasController().setPaginaVoltar("incluirNotasPOLList");
			getRelatorioNotasController().setSeqVersaoDoc(seqVersaoDoc);			
			return PAGE_NOTAS_POL_LIST_PDF;
		}
	}
	
	public String abrirDiagnosticos(){
		return PAGE_DIAGNOSTICOS;
	}

	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}

	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}
	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}
	public AipPacientes getPaciente() {
		return paciente;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}
	public IProntuarioOnlineFacade getProntuarioOnlineFacade() {
		return prontuarioOnlineFacade;
	}

	public void setProntuarioOnlineFacade(IProntuarioOnlineFacade prontuarioOnlineFacade) {
		this.prontuarioOnlineFacade = prontuarioOnlineFacade;
	}
	
	public MamNotaAdicionalEvolucoes getRegistroSelecionado() {
		return registroSelecionado;
	}

	public void setRegistroSelecionado(MamNotaAdicionalEvolucoes registroSelecionado) {
		this.registroSelecionado = registroSelecionado;
	}

	public String getTextoIncluiNota() {
		return textoIncluiNota;
	}

	public void setTextoIncluiNota(String textoIncluiNota) {
		this.textoIncluiNota = textoIncluiNota;
	}

	public MamNotaAdicionalEvolucoes getRegistroInclusao() {
		return registroInclusao;
	}

	public void setRegistroInclusao(MamNotaAdicionalEvolucoes registroInclusao) {
		this.registroInclusao = registroInclusao;
	}

	public Boolean getBotaoAdicionar() {
		return botaoAdicionar;
	}

	public void setBotaoAdicionar(Boolean botaoAdicionar) {
		this.botaoAdicionar = botaoAdicionar;
	}

	public Integer getSeqVersaoDoc() {
		return seqVersaoDoc;
	}

	public void setSeqVersaoDoc(Integer seqVersaoDoc) {
		this.seqVersaoDoc = seqVersaoDoc;
	}

	public Integer getSeqMamNotaAdicionalEvolucoes() {
		return seqMamNotaAdicionalEvolucoes;
	}

	public void setSeqMamNotaAdicionalEvolucoes(Integer seqMamNotaAdicionalEvolucoes) {
		this.seqMamNotaAdicionalEvolucoes = seqMamNotaAdicionalEvolucoes;
	}

	public Boolean getBotaoDiagnosticos() {
		return botaoDiagnosticos;
	}

	public void setBotaoDiagnosticos(Boolean botaoDiagnosticos) {
		this.botaoDiagnosticos = botaoDiagnosticos;
	}

	public Boolean getPermiteExcluirNotaAdicionalEvolucao() {
		return permiteExcluirNotaAdicionalEvolucao;
	}

	public void setPermiteExcluirNotaAdicionalEvolucao(
			Boolean permiteExcluirNotaAdicionalEvolucao) {
		this.permiteExcluirNotaAdicionalEvolucao = permiteExcluirNotaAdicionalEvolucao;
	}
 
	public DynamicDataModel<MamNotaAdicionalEvolucoes> getDataModel() {
	 return dataModel;
	}

	public void setDataModel(DynamicDataModel<MamNotaAdicionalEvolucoes> dataModel) {
	 this.dataModel = dataModel;
	}
}
