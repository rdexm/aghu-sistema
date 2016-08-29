package br.gov.mec.aghu.farmacia.action;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.component.UIComponent;
import javax.faces.component.UIInput;
import javax.faces.context.FacesContext;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.farmacia.vo.CodAtendimentoInformacaoPacienteVO;
import br.gov.mec.aghu.farmacia.vo.InformacoesPacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.MpmPrescricaoMedVO;
import br.gov.mec.aghu.farmacia.vo.PacienteAgendamentoPrescribenteVO;
import br.gov.mec.aghu.farmacia.vo.UnidadeFuncionalVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.VRapServidorConselho;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.action.ModalCentralMensagensController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.vo.RapPessoalServidorVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author pedro.santiago
 *
 */
public class EnviarInformacaoPrescribenteController extends ActionController {

	private static final long serialVersionUID = -6141023364430244341L;	

	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente"; 
	
	@Inject
	IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@Inject
	private IPacienteFacade pacienteFacade;
	
	@Inject
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@Inject
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private ModalCentralMensagensController modalCentralMensagensController;
	
	private AipPacientes paciente;
	private	InformacoesPacienteAgendamentoPrescribenteVO informacoesPacienteAgendamentoPrescribenteVO;
	private PacienteAgendamentoPrescribenteVO pacientePrescribenteVO;
	private MpmPrescricaoMedVO mpmPrescricaoMedica;
	private UnidadeFuncionalVO vAghUnidFuncional;
	private CodAtendimentoInformacaoPacienteVO informacaoPacienteVO;

	private boolean modoEdicao = false;
	private boolean modoVisualizacao = false;
	private boolean transicaoTela = false;
	private Integer codigoRequest;

	private boolean textoBloqueado = false;
	
	private String validadeInicial;
	private String validadeFinal;
	private Integer numeroProntuario;
	private Integer codigoPaciente;
	private String informacaoPrescribente;
	private String nomePaciente;
	private String referenciaPrescricao;
	private String unidadeOrigem;
	private String responsavel;
	private String conselho;
	private String nroRegistro;
	private String responsavelCInformacao;
	private String dtHoraConhecimento;
	private String numeroProntuarioS;
	private String telaOrigem;
	
	private Integer pacCodigo;
	
	private static final Log LOG = LogFactory.getLog(EnviarInformacaoPrescribenteController.class);
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar(){
		if(codigoRequest != null){
			if(modoEdicao){
				informacoesPacienteAgendamentoPrescribenteVO = prescricaoMedicaFacade.obterInformacoesPacienteAgendamentoPrescribenteVO(codigoRequest);
				try {
					farmaciaApoioFacade.validarNulidadeInformacaoPrescribente(informacoesPacienteAgendamentoPrescribenteVO);
				} catch (ApplicationBusinessException e) {
					this.apresentarExcecaoNegocio(e);
					LOG.error(e.getMessage(), e);
				}
				
				if(informacoesPacienteAgendamentoPrescribenteVO != null){
					numeroProntuario = informacoesPacienteAgendamentoPrescribenteVO.getProntuario();
					codigoPaciente = informacoesPacienteAgendamentoPrescribenteVO.getPacCodigo();
					nomePaciente = informacoesPacienteAgendamentoPrescribenteVO.getPacNome();
					informacaoPrescribente = informacoesPacienteAgendamentoPrescribenteVO.getDescricao();
					pacientePrescribenteVO = this.pacienteFacade.obterCodigoAtendimentoInformacoesPaciente(codigoPaciente, numeroProntuario);
					mpmPrescricaoMedica = farmaciaApoioFacade.obterDataReferenciaPrescricaoMedica(informacoesPacienteAgendamentoPrescribenteVO.getPmeAtdSeq(), null).get(0);
					incluirCamposValidade();
					vAghUnidFuncional = farmaciaApoioFacade.obterPorUnfSeq(informacoesPacienteAgendamentoPrescribenteVO.getUnfSeq());
					
					if(informacoesPacienteAgendamentoPrescribenteVO.getIndInfVerificada()){
						textoBloqueado = true;
					}
				}else{
					modoEdicao = false;
				}
			}else if(modoVisualizacao){
				informacaoPacienteVO = prescricaoMedicaFacade.buscarCodigoInformacoesPaciente(codigoRequest);
				if(informacaoPacienteVO != null){
					MpmPrescricaoMedica mpmPrescricaoMedica = prescricaoMedicaFacade.obterValoresPrescricaoMedica(
							informacaoPacienteVO.getAtdSeqPme(), informacaoPacienteVO.getSeqPme());
					AghUnidadesFuncionais aghUnidadesFuncionais = farmaciaApoioFacade.obterValoresPrescricaoMedica(
							informacaoPacienteVO.getSeqUnf());
					VRapServidorConselho vRapServidorConselho = registroColaboradorFacade.obterValoresPrescricaoMedica(
							informacaoPacienteVO.getMatriculaServidor(), informacaoPacienteVO.getVinCodigoServidor());
					RapPessoalServidorVO vRapPessoaServidor = registroColaboradorFacade.obterValoresPrescricaoMedicaPessoaServidor(
							informacaoPacienteVO.getMatriculaServidorVerificado(), 
							informacaoPacienteVO.getVinCodigoServidorVerificado());
					numeroProntuarioS = informacaoPacienteVO.getProntuarioS();
					codigoPaciente = informacaoPacienteVO.getCodigoPac();
					nomePaciente = informacaoPacienteVO.getNomePac();
					informacaoPrescribente = informacaoPacienteVO.getDescricaoIfp();
					if(informacaoPacienteVO.getDthrInfVerificada() != null){
						dtHoraConhecimento = DateUtil.obterDataFormatada(informacaoPacienteVO.getDthrInfVerificada(), "dd/MM/yyyy HH:mm").toString();
					}
					if(mpmPrescricaoMedica != null){
						referenciaPrescricao = DateUtil.obterDataFormatada(mpmPrescricaoMedica.getDtReferencia(),"dd/MM/yyyy");
						validadeInicial = DateUtil.obterDataFormatada(mpmPrescricaoMedica.getDthrInicio(),"dd/MM/yyyy HH:mm");
						validadeFinal = DateUtil.obterDataFormatada(mpmPrescricaoMedica.getDthrFim(),"dd/MM/yyyy HH:mm");
					}
					if(aghUnidadesFuncionais != null){
						unidadeOrigem = aghUnidadesFuncionais.getSeq().toString().concat(" - ").concat(aghUnidadesFuncionais.getDescricao());
					}
					if(vRapServidorConselho != null){
						responsavel = vRapServidorConselho.getNome();
						conselho = vRapServidorConselho.getSigla();
						nroRegistro = vRapServidorConselho.getNroRegConselho();
					}
					if(vRapPessoaServidor != null && vRapPessoaServidor.getNome() != null){
						responsavelCInformacao = vRapPessoaServidor.getNome();
						}
				}else{
					modoVisualizacao = false;
				}
			}
		}else{
			if(pacCodigo!=null){
				pesquisarPacientePesquisaFonetica();
			}
		}
	}
	
	public Boolean validarPaciente(){
		if(paciente == null){
			pacientePrescribenteVO = null;
			return Boolean.FALSE;
		}else{
			return Boolean.TRUE;
		}
	}
	
	public boolean isTextoBloqueado() {
		return textoBloqueado;
	}

	public void setTextoBloqueado(boolean textoBloqueado) {
		this.textoBloqueado = textoBloqueado;
	}
	
	
	public void pesquisarPacientePesquisaFonetica(){
		
        try {
        	paciente = pacienteFacade.obterPacientePorCodigo(this.pacCodigo);
			if(paciente != null){
				pacientePrescribenteVO = this.pacienteFacade.obterCodigoAtendimentoInformacoesPaciente(paciente.getCodigo(), paciente.getProntuario());
				farmaciaApoioFacade.validarRetornoPaciente(pacientePrescribenteVO);
			}
			
		}catch(BaseException e){
			paciente = null;	
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);	
		}
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
	
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if(paciente != null){
				pacientePrescribenteVO = this.pacienteFacade.obterCodigoAtendimentoInformacoesPaciente(paciente.getCodigo(), paciente.getProntuario());
				farmaciaApoioFacade.validarRetornoPaciente(pacientePrescribenteVO);
			}
			
		}catch(BaseException e){
			paciente = null;	
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);	
		}
	}
	
	public String redirecionarPesquisaFonetica(){
		return PESQUISA_FONETICA;
	}
	
	public void gravar(){
		try {
			validarPaciente();
			this.farmaciaApoioFacade.validarEnvioInformacoesPrescribentes(pacientePrescribenteVO);
			apresentarMsgNegocio(Severity.INFO, this.farmaciaApoioFacade.gravarMpmInformacaoPrescribentes(informacoesPacienteAgendamentoPrescribenteVO, 
							mpmPrescricaoMedica, pacientePrescribenteVO, informacaoPrescribente, vAghUnidFuncional));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			LOG.error(e.getMessage(), e);
		}
	}
	
	/**
	 * SB1
	 * @param descricao
	 * @return
	 */
	public List<MpmPrescricaoMedVO> obterDataReferenciaPrescricaoMedicaSB(String descricao){
			
		if(descricao != null && !descricao.trim().isEmpty()){
			if(!farmaciaApoioFacade.validarData(descricao)){
				apresentarMsgNegocio(Severity.ERROR, "Data invalida");
				return new ArrayList<MpmPrescricaoMedVO>();
			}
		}
		
		if(validarPaciente()){
			return this.returnSGWithCount(this.farmaciaApoioFacade.obterDataReferenciaPrescricaoMedica(pacientePrescribenteVO.getSeq(), descricao),
					this.obterDataReferenciaPrescricaoMedicaCount(descricao));
		}else{
			return new ArrayList<MpmPrescricaoMedVO>();
		}
	}	
	
	public Long obterDataReferenciaPrescricaoMedicaCount(String descricao){
		return Long.valueOf(this.farmaciaApoioFacade.obterDataReferenciaPrescricaoMedica(pacientePrescribenteVO.getSeq(), descricao).size());
	}
	
	/**
	 * SB2
	 * @param descricao
	 * @return
	 */
	public List<UnidadeFuncionalVO> obterUnidadeFuncionalOrigemInformacaoPrescribenteSB(String descricao){
			return this.returnSGWithCount(farmaciaApoioFacade.obterUnidadeFuncionalOrigemInformacaoPrescribente(descricao), 
					this.obterUnidadeFuncionalOrigemInformacaoPrescribenteSBCount(descricao));
	}
	
	public Long obterUnidadeFuncionalOrigemInformacaoPrescribenteSBCount(String descricao){
		return Long.valueOf(farmaciaApoioFacade.obterUnidadeFuncionalOrigemInformacaoPrescribente(descricao).size());
	}
	
	public String voltar(){
		if(telaOrigem.equals("/pages/prescricaomedica/manterPrescricaoMedica.xhtml")){
			modalCentralMensagensController.iniciarModal();
			return telaOrigem;
		}
		return null;
	}
	
	public void incluirCamposValidade(){
		
		if(mpmPrescricaoMedica != null && 
				mpmPrescricaoMedica.getDthrInicio() != null){
			this.validadeInicial = mpmPrescricaoMedica.getDataInicioS();
		}
		if(mpmPrescricaoMedica != null && 
				mpmPrescricaoMedica.getDthrFim() != null){
			this.validadeFinal = mpmPrescricaoMedica.getDataFimS();
		}
	}
	
	public void excluirCamposValidade(){
		this.validadeInicial = null;
		this.validadeFinal = null;
	}
	
	/**
	 * Ação do botão limpar.
	 */
	public void limpar() {		
		Iterator<UIComponent> componentes = FacesContext.getCurrentInstance().getViewRoot().getFacetsAndChildren();
		
		while (componentes.hasNext()) {			
			this.limparValoresSubmetidos(componentes.next());
		}
		
		numeroProntuarioS = null;
		codigoPaciente = null;
		nomePaciente = null;
		mpmPrescricaoMedica = null;
		validadeInicial = null;
		validadeFinal = null;
		vAghUnidFuncional = null;
		informacaoPrescribente = null;
		paciente = null;
	}
	

	/**
	 * Percorre o formulário resetando os valores digitados nos campos (inputText, inputNumero, selectOneMenu, ...)
	 * 
	 * @param object {@link Object}
	 */
	private void limparValoresSubmetidos(Object object) {
		
		if (object == null || object instanceof UIComponent == false) {
			return;
		}
		
		Iterator<UIComponent> uiComponent = ((UIComponent) object).getFacetsAndChildren();
		while (uiComponent.hasNext()) {
			limparValoresSubmetidos(uiComponent.next());
		}
		
		if (object instanceof UIInput) {
			((UIInput) object).resetValue();
		}
	}
	
	
	public Integer getNumeroProntuario() {
		return numeroProntuario;
	}
	
	public void setNumeroProntuario(Integer numeroProntuario) {
		this.numeroProntuario = numeroProntuario;
	}
	
	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}
	
	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}
	
	public String getNomePaciente() {
		return nomePaciente;
	}
	
	public void setNomePaciente(String nomePaciente) {
		this.nomePaciente = nomePaciente;
	}

	public String getInformacaoPrescribente() {
		return informacaoPrescribente;
	}

	public void setInformacaoPrescribente(String informacaoPrescribente) {
		this.informacaoPrescribente = informacaoPrescribente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public Integer getCodigoRequest() {
		return codigoRequest;
	}

	public void setCodigoRequest(Integer codigoRequest) {
		this.codigoRequest = codigoRequest;
	}

	public boolean isModoEdicao() {
		return modoEdicao;
	}

	public void setModoEdicao(boolean modoEdicao) {
		this.modoEdicao = modoEdicao;
	}

	public InformacoesPacienteAgendamentoPrescribenteVO getInformacoesPacienteAgendamentoPrescribenteVO() {
		return informacoesPacienteAgendamentoPrescribenteVO;
	}

	public void setInformacoesPacienteAgendamentoPrescribenteVO(
			InformacoesPacienteAgendamentoPrescribenteVO informacoesPacienteAgendamentoPrescribenteVO) {
		this.informacoesPacienteAgendamentoPrescribenteVO = informacoesPacienteAgendamentoPrescribenteVO;
	}
	
	public String getTelaOrigem() {
		return telaOrigem;
	}

	public void setTelaOrigem(String telaOrigem) {
		this.telaOrigem = telaOrigem;
	}
	
	public boolean isModoVisualizacao() {
		return modoVisualizacao;
	}

	public void setModoVisualizacao(boolean modoVisualizacao) {
		this.modoVisualizacao = modoVisualizacao;
	}
	
	public boolean isTransicaoTela() {
		return transicaoTela;
	}

	public void setTransicaoTela(boolean transicaoTela) {
		this.transicaoTela = transicaoTela;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public UnidadeFuncionalVO getvAghUnidFuncional() {
		return vAghUnidFuncional;
	}

	public void setvAghUnidFuncional(UnidadeFuncionalVO vAghUnidFuncional) {
		this.vAghUnidFuncional = vAghUnidFuncional;
	}

	public MpmPrescricaoMedVO getMpmPrescricaoMedica() {
		return mpmPrescricaoMedica;
	}

	public void setMpmPrescricaoMedica(MpmPrescricaoMedVO mpmPrescricaoMedica) {
		this.mpmPrescricaoMedica = mpmPrescricaoMedica;
	}

	public String getValidadeInicial() {
		return validadeInicial;
	}

	public void setValidadeInicial(String validadeInicial) {
		this.validadeInicial = validadeInicial;
	}

	public String getValidadeFinal() {
		return validadeFinal;
	}

	public void setValidadeFinal(String validadeFinal) {
		this.validadeFinal = validadeFinal;
	}

	public CodAtendimentoInformacaoPacienteVO getInformacaoPacienteVO() {
		return informacaoPacienteVO;
	}

	public void setInformacaoPacienteVO(
			CodAtendimentoInformacaoPacienteVO informacaoPacienteVO) {
		this.informacaoPacienteVO = informacaoPacienteVO;
	}

	public String getReferenciaPrescricao() {
		return referenciaPrescricao;
	}

	public void setReferenciaPrescricao(String referenciaPrescricao) {
		this.referenciaPrescricao = referenciaPrescricao;
	}

	public String getUnidadeOrigem() {
		return unidadeOrigem;
	}

	public void setUnidadeOrigem(String unidadeOrigem) {
		this.unidadeOrigem = unidadeOrigem;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public PacienteAgendamentoPrescribenteVO getPacientePrescribenteVO() {
		return pacientePrescribenteVO;
	}

	public void setPacientePrescribenteVO(
			PacienteAgendamentoPrescribenteVO pacientePrescribenteVO) {
		this.pacientePrescribenteVO = pacientePrescribenteVO;
	}

	public String getResponsavel() {
		return responsavel;
	}

	public void setResponsavel(String responsavel) {
		this.responsavel = responsavel;
	}

	public String getNroRegistro() {
		return nroRegistro;
	}

	public void setNroRegistro(String nroRegistro) {
		this.nroRegistro = nroRegistro;
	}

	public String getResponsavelCInformacao() {
		return responsavelCInformacao;
	}

	public void setResponsavelCInformacao(String responsavelCInformacao) {
		this.responsavelCInformacao = responsavelCInformacao;
	}

	public String getConselho() {
		return conselho;
	}

	public void setConselho(String conselho) {
		this.conselho = conselho;
	}

	public String getDtHoraConhecimento() {
		return dtHoraConhecimento;
	}

	public void setDtHoraConhecimento(String dtHoraConhecimento) {
		this.dtHoraConhecimento = dtHoraConhecimento;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getNumeroProntuarioS() {
		return numeroProntuarioS;
	}

	public void setNumeroProntuarioS(String numeroProntuarioS) {
		this.numeroProntuarioS = numeroProntuarioS;
	}
}