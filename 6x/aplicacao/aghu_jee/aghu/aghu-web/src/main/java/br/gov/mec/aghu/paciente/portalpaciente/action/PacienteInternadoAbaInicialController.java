package br.gov.mec.aghu.paciente.portalpaciente.action;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.component.tabview.Tab;
import org.primefaces.context.RequestContext;
import org.primefaces.event.TabChangeEvent;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioCategoriaProfissionalPortalPaciente;
import br.gov.mec.aghu.dominio.DominioIndPendenteDiagnosticos;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CseCategoriaProfissional;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.action.DiagnosticosPacienteController;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class PacienteInternadoAbaInicialController extends ActionController {
	private static final long serialVersionUID = -8114543590837400136L;

	private final String TAB_1 = "aba1";
	private final String TAB_2 = "aba2";
	private final String TAB_3 = "aba3";
	private final String TAB_4 = "aba4";
	private final String TAB_5 = "aba5";
	private final String PAGE_MANTER_DIAGNOSTICO_PACIENTE = "prescricaomedica-manterDiagnosticosPaciente";
	private final String PAGE_PORTAL_PACIENTE_PESQUISAR = "paciente-portalPacienteInternado";
	
	@EJB 
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade; 
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private PacienteInternadoAbaAnamneseController abaAnamneseController;
	
	@Inject
	private DiagnosticosPacienteController diagnosticosPacienteController;

	private Integer selectedTab;
	private Tab selecionarAbas;
	private Boolean fromBack = Boolean.FALSE;
	private AghAtendimentos atendimento;
	private Integer prontuario;
	private Date data;
	private DominioCategoriaProfissionalPortalPaciente categoria;
	private Integer paramCategoria;
	private String leito;
	private Boolean exibeInfoPaciente;
	private String textoTextArea;
	private String textoServicoMedico;
	private List<AipPacientes> listaPaciente;
	private AipPacientes pacienteSelecionado;
	private List<MamDiagnostico> listaDiagnostico;
	private MamDiagnostico diagnosticoPersistencia= new MamDiagnostico();

	@PostConstruct
	public void init() {
		this.begin(conversation, true);
	}

	public void iniciar() {
		if (!fromBack) {
			selecionarAbas = new Tab();
			selecionarAbas.setId(TAB_1);
			selectedTab = 0;
			data = new Date();
		}

		// Refazer a consulta quando retornar à tela
		if (!this.isPostBack() && this.atendimento != null){
			if (selecionarAbas.getId().equals(TAB_1)) {
				this.carregarDiagnostico();
			} else if (selecionarAbas.getId().equals(TAB_2)) {
				this.carregarAnamnese();
			}
		}
		
		fromBack = Boolean.FALSE;
	}
	
	private String capitalizarTexto(String texto){
		return ambulatorioFacade.mpmcMinusculo(texto, 2);
	}

	public void pesquisarAbas() throws ApplicationBusinessException {
		this.pesquisarGeral();
		this.abaAnamneseController.limpar();

		if (selecionarAbas.getId().equals(TAB_1)) {
			this.carregarDiagnostico();
		} else if (selecionarAbas.getId().equals(TAB_2)) {
			this.carregarAnamnese();
		} else if (selecionarAbas.getId().equals(TAB_3)) {
			// todo: implementar nas outras abas
			this.carregarDiagnostico();
		} else if (selecionarAbas.getId().equals(TAB_4)) {
			// todo: implementar nas outras abas
			this.carregarDiagnostico();
		} else if (selecionarAbas.getId().equals(TAB_5)) {
			// todo: implementar nas outras abas
			this.carregarDiagnostico();
		}
	}

	private void pesquisarGeral() throws ApplicationBusinessException {
		if (this.atendimento == null) {
			if (this.prontuario != null){
				this.carregarAtendimento(this.prontuario);
			}else if (!StringUtils.isBlank(this.leito)) {
				this.exibirSelecaoPaciente();
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, "MSG_PRONTUARIO_LEITO_OBRIGATORIO");
			}
		} 
	}
	
	public String obterDescricaoDiagnostico(MamDiagnostico mamDiagnostico){
		if (mamDiagnostico != null){
			if (mamDiagnostico.getCid() == null){
				return mamDiagnostico.getDescricao().toUpperCase();
			} else {
				return mamDiagnostico.getCid().getDescricao();
			}
		}
		
		return StringUtils.EMPTY;
	}
	
	private void carregarDiagnostico() {
		if (this.atendimento != null) {
			this.listaDiagnostico = ambulatorioFacade.listarDiagnosticosPortalPacienteInternado(this.atendimento.getPacCodigo(), this.data);
		}
	}

	private void exibirSelecaoPaciente() {
		this.pacienteSelecionado = null;
		this.listaPaciente = pacienteFacade.obterPacienteInternadoPorLeito(this.leito);
		RequestContext.getCurrentInstance().execute("PF('modalSelecionaPacienteWG').show();"); 
		RequestContext.getCurrentInstance().execute("PF('gridPacientesWG').unselectAllRows();");
	}
	
	public void selecionarPaciente() throws ApplicationBusinessException{
		if (this.pacienteSelecionado != null){
			this.carregarAtendimento(this.pacienteSelecionado.getProntuario());
			this.carregarDiagnostico();			
			this.fecharSelecaoPaciente();
		}
		else {
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_SELECIONAR_PACIENTE");
		}
	}
	
	public void fecharSelecaoPaciente(){
		this.pacienteSelecionado = null;
		RequestContext.getCurrentInstance().execute("PF('modalSelecionaPacienteWG').hide();"); 
	}

	private void carregarAtendimento(Integer nroProntuario) throws ApplicationBusinessException {
		this.atendimento = this.pacienteFacade.obterAtendimentoPacienteInternadoPorProntuario(nroProntuario);
		if (this.atendimento != null){
			this.textoServicoMedico = this.prescricaoMedicaFacade.obterServicoMedicoDoAtendimento(this.atendimento.getSeq());
			this.exibeInfoPaciente = Boolean.TRUE;
		} else {
			this.exibeInfoPaciente = Boolean.FALSE;
			this.apresentarMsgNegocio(Severity.ERROR, "MSG_NENHUM_PACIENTE_ENCONTRADO");
		}
	}

	public void limpar() {
		this.limparAbaDiagnostico();
		abaAnamneseController.limpar();
		// todo: cada aba implementar seu método limpar
	}

	private void limparAbaDiagnostico() {
		this.fromBack = Boolean.FALSE;
		this.atendimento  = null;
		this.prontuario = null;
		this.data = null;
		this.categoria = null;
		this.paramCategoria = null;
		this.exibeInfoPaciente = Boolean.FALSE;
		this.textoServicoMedico = null;
		this.pacienteSelecionado = null;
		this.leito = null;
		this.diagnosticoPersistencia = new MamDiagnostico();
		this.listaDiagnostico = null;
		this.data = new Date();
	}
	
	public List<AghCid> obterListaAghCid(String filtro) {
		return this.returnSGWithCount(aghuFacade.obterCids(filtro, false), aghuFacade.obterCidsCount(filtro, false));
	}	
	
	public String editar(MamDiagnostico mamDiagnostico){
		this.diagnosticosPacienteController.setPacCodigo(this.atendimento.getPacCodigo());
		this.diagnosticosPacienteController.setVoltarPara(PAGE_PORTAL_PACIENTE_PESQUISAR);
		
		return PAGE_MANTER_DIAGNOSTICO_PACIENTE;
	}
	
	public void excluir(MamDiagnostico mamDiagnostico) throws ApplicationBusinessException{
		this.ambulatorioFacade.excluirDiagnostico(mamDiagnostico);
		this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO");
		this.carregarDiagnostico();
	}
	
	public void selecionarCategoria() throws ApplicationBusinessException{
		this.paramCategoria = null;
		
		if (this.categoria != null){
			AghParametros param = parametroFacade.obterAghParametroPorNome(this.categoria.obterNomeParam());
			if (param != null){
				this.paramCategoria = param.getVlrNumerico().intValue();
			}
		}
	}
	
	public void persistirDiagnostico() {
		Calendar data = Calendar.getInstance();
		data.setTime(new Date());
		data.set(Calendar.HOUR, 0);
		data.set(Calendar.MINUTE, 0);
		data.set(Calendar.SECOND, 0);
		this.diagnosticoPersistencia.setData(data.getTime());
		this.diagnosticoPersistencia.setDthrCriacao(new Date());
		this.diagnosticoPersistencia.setDthrValida(new Date());
		this.diagnosticoPersistencia.setPaciente(this.atendimento.getPaciente());
		this.diagnosticoPersistencia.setIndSituacao(DominioSituacao.A);
		this.diagnosticoPersistencia.setIndPendente(DominioIndPendenteDiagnosticos.V);
		this.diagnosticoPersistencia.setServidor(servidorLogadoFacade.obterServidorLogado());
		this.diagnosticoPersistencia.setServidorValida(this.diagnosticoPersistencia.getServidor());
		
		if (this.paramCategoria != null) {
			CseCategoriaProfissional categoria = ambulatorioFacade.obterCategoriaPorSeq(this.paramCategoria);
			this.diagnosticoPersistencia.setCategoriaProfissional(categoria);
		}

		// this.diagnosticoPersistencia.setRegistro(registro);
		this.diagnosticoPersistencia.setAtendimento(this.atendimento);

		try {
			ambulatorioFacade.inserirDiagnostico(this.diagnosticoPersistencia);

			this.diagnosticoPersistencia = new MamDiagnostico();
			this.carregarDiagnostico();
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}
	
	public void tabChange(TabChangeEvent event) throws ApplicationBusinessException {
		this.selecionarAbas = event.getTab();
		
		if (TAB_1.equals(event.getTab().getId())) {
			this.selectedTab = 0;
			this.carregarDiagnostico();
		}
		else if (TAB_2.equals(event.getTab().getId())) {
			this.selectedTab = 1;
			this.carregarAnamnese();
		}
	}

	private void carregarAnamnese() {
		this.selectedTab = 1;
		abaAnamneseController.setAtendimento(this.atendimento);
		abaAnamneseController.setData(this.data);
		abaAnamneseController.setParamCategoria(this.paramCategoria);
		abaAnamneseController.iniciar();
	}
	
	public Integer getSelectedTab() {
		return selectedTab;
	}

	public void setSelectedTab(Integer selectedTab) {
		this.selectedTab = selectedTab;
	}

	public Tab getSelecionarAbas() {
		return selecionarAbas;
	}

	public void setSelecionarAbas(Tab selecionarAbas) {
		this.selecionarAbas = selecionarAbas;
	}

	public boolean isFromBack() {
		return fromBack;
	}

	public void setFromBack(boolean fromBack) {
		this.fromBack = fromBack;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Date getData() {
		return data;
	}

	public void setData(Date data) {
		this.data = data;
	}

	public DominioCategoriaProfissionalPortalPaciente getCategoria() {
		return categoria;
	}

	public void setCategoria(DominioCategoriaProfissionalPortalPaciente categoria) {
		this.categoria = categoria;
	}

	public Integer getParamCategoria() {
		return paramCategoria;
	}

	public void setParamCategoria(Integer paramCategoria) {
		this.paramCategoria = paramCategoria;
	}

	public String getLeito() {
		return leito;
	}

	public void setLeito(String leito) {
		this.leito = leito;
	}

	public Boolean getExibeInfoPaciente() {
		return exibeInfoPaciente;
	}

	public void setExibeInfoPaciente(Boolean exibeInfoPaciente) {
		this.exibeInfoPaciente = exibeInfoPaciente;
	}

	/**
	 * @return the textoTextArea
	 */
	public String getTextoTextArea() {
		return textoTextArea;
	}

	/**
	 * @param textoTextArea the textoTextArea to set
	 */
	public void setTextoTextArea(String textoTextArea) {
		this.textoTextArea = textoTextArea;
	}
	
	public String getTextoServicoMedico() {
		return textoServicoMedico;
	}

	public void setTextoServicoMedico(String textoServicoMedico) {
		this.textoServicoMedico = textoServicoMedico;
	}

	public String getNomePaciente(){
		if (this.atendimento != null){
			return this.capitalizarTexto(this.atendimento.getPaciente().getNome());
		}
		
		return null;
	}

	public List<AipPacientes> getListaPaciente() {
		return listaPaciente;
	}

	public void setListaPaciente(List<AipPacientes> listaPaciente) {
		this.listaPaciente = listaPaciente;
	}

	public AipPacientes getPacienteSelecionado() {
		return pacienteSelecionado;
	}

	public void setPacienteSelecionado(AipPacientes pacienteSelecionado) {
		this.pacienteSelecionado = pacienteSelecionado;
	}

	public List<MamDiagnostico> getListaDiagnostico() {
		return listaDiagnostico;
	}

	public void setListaDiagnostico(List<MamDiagnostico> listaDiagnostico) {
		this.listaDiagnostico = listaDiagnostico;
	}

	public MamDiagnostico getDiagnosticoPersistencia() {
		return diagnosticoPersistencia;
	}

	public void setDiagnosticoPersistencia(MamDiagnostico diagnosticoPersistencia) {
		this.diagnosticoPersistencia = diagnosticoPersistencia;
	}
}
