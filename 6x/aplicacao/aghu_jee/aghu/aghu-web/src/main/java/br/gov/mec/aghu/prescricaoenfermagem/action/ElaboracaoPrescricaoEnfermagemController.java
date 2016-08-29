package br.gov.mec.aghu.prescricaoenfermagem.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.prescricaoenfermagem.IPrescricaoEnfermagemFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.EpePrescricaoEnfermagem;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.prescricaoenfermagem.vo.DiagnosticoEtiologiaVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.WebUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * Controller para as funcionalidades Elaborar, Atualizar e Consultar uma Prescrição de Enfermagem. 
 * 
 * @author diego.pacheco
 *
 */

public class ElaboracaoPrescricaoEnfermagemController extends ActionController {

	private static final String ENCERRAR_DIAGNOSTICOS = "prescricaoenfermagem-encerrarDiagnosticos";

	private static final String MANTER_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-manterPrescricaoEnfermagem";

	private static final String PACIENTE_PESQUISA_PACIENTE_COMPONENTE = "paciente-pesquisaPacienteComponente";

	private static final String SELECIONAR_PRESCRICAO_ENFERMAGEM = "prescricaoenfermagem-selecionarPrescricaoEnfermagem";

	private static final String PRESCRICAOMEDICA_SELECIONAR_PRESCRICAO_CONSULTAR = "prescricaomedica-selecionarPrescricaoConsultar";

	private static final String BLOCOCIRURGICO_LISTAR_CIRURGIAS = "blococirurgico-listaCirurgias";

	private static final String LISTA_PACIENTES_ENFERMAGEM = "prescricaoenfermagem-listaPacientesEnfermagem";

	private static final Log LOG = LogFactory.getLog(ElaboracaoPrescricaoEnfermagemController.class);
	
	private static final long serialVersionUID = -1691876289762077962L;

	@EJB
	private IPrescricaoEnfermagemFacade prescricaoEnfermagemFacade;	
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB 
	private IPacienteFacade pacienteFacade;	
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFoneticaVO;
	
	@Inject
	private ManutencaoPrescricaoEnfermagemController manutencaoPrescricaoEnfermagemController;
	
	@Inject 
	private EncerramentoDiagnosticoController encerramentoDiagnosticoController;
	private AipPacientes paciente;

	private AinLeitos leito;
	
	private AghAtendimentos atendimento;	
	
	private AghUnidadesFuncionais unidadeFuncional;
	
	private AinQuartos quarto;
	
	private Boolean showModal;
	
	private Integer atdSeq;
	
	private List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem = new ArrayList<EpePrescricaoEnfermagem>();
	
	private Date dtPrescricao;
	
	private Boolean habilitaBotaoCriarPrescricao;
	
	private String mensagemBotaoCriarPrescricao;
	
	private String mensagemModal;
	
	private String voltarPara;
	
	private Integer pacCodigoFonetica;
	
	private Integer codPac;
	
	private EpePrescricaoEnfermagem prescricaoEnfermagem;
	
	private Integer prontuario;
	
	private boolean exibirMensagemCancelamento;


	public enum ElaboracaoPrescricaoEnfermagemControllerExceptionCode implements BusinessExceptionCode {
		ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO
	}
	
	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void iniciar() {
	 

		
		if(exibirMensagemCancelamento){
			this.apresentarMsgNegocio(Severity.INFO, "MSG_CANCELAMENTO_PRESCRICAO_ENFERMAGEM");//Transferido para o iniciar por conta do mec:pageConfig
			exibirMensagemCancelamento = false;
		}
		
		CodPacienteFoneticaVO codPac = codPacienteFoneticaVO.get();
		if (codPac != null && codPac.getCodigo() > 0) { 
			this.pacCodigoFonetica = codPac.getCodigo();
		}	
		
		if (atdSeq != null) {
			this.setAtendimento(this.pacienteFacade.obterAtendimento(atdSeq));
			//this.setAtendimento(this.pacienteFacade.obterAtendimentoPorSeqAtendimentoUrgencia(atdSeq));
			this.carregarAtendimento();
			try {
				this.setDtPrescricao(
						prescricaoEnfermagemFacade.obterDataReferenciaProximaPrescricao(this.getAtendimento(), getDataAtual()));
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
			this.setProntuario(this.getPaciente().getProntuario());
			
		}
		// pac_codigo de um paciente selecionado na pesquisa fonética
		if (pacCodigoFonetica != null) {
			limparCampos();
			this.paciente = this.pacienteFacade.obterPacientePorCodigo(pacCodigoFonetica);
			Integer prontuario = paciente.getProntuario();
			if (prontuario == null) {
				this.apresentarMsgNegocio(Severity.ERROR, 						
						ElaboracaoPrescricaoEnfermagemControllerExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO.toString());
				limparCampos();
			}
			else {
				List<AghAtendimentos> listaAtendimentos = 
					prescricaoEnfermagemFacade.obterAtendimentoAtualPorProntuario(prontuario);
				if (!listaAtendimentos.isEmpty()) {
					this.atendimento = listaAtendimentos.get(0);
					this.carregarAtendimento();
					this.setProntuario(this.getPaciente().getProntuario());
				}
				else {
					this.apresentarMsgNegocio(Severity.ERROR, 						
						ElaboracaoPrescricaoEnfermagemControllerExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO.toString());
				}
			}
		}
	
	}
	
	public void pesquisaPaciente(ValueChangeEvent event){
		try {
			paciente = pacienteFacade.pesquisarPacienteComponente(event.getNewValue(), event.getComponent().getId());
			if (paciente != null) {
				prontuario = paciente.getProntuario();
				codPac = paciente.getCodigo();
				setarDataReferencia();
			}
		}catch(BaseException e){
			apresentarExcecaoNegocio(e);
		}
	}
	
	/**
	 * Método chamado pela suggestion de pesquisa por leito
	 * 
	 * @param objParam
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<AinLeitos> pesquisarLeito(String objParam) throws ApplicationBusinessException {
		List<AinLeitos> leitos = new ArrayList<AinLeitos>();
		
		this.showModal = false;
		String strPesquisa = (String) objParam;
		if (!StringUtils.isBlank(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
		}
		
		leitos = this.internacaoFacade.obterLeitosAtivosPorUnf(strPesquisa, null);
		
		return this.returnSGWithCount(leitos,pesquisarLeitoCount(objParam));
	}
	
	public Long pesquisarLeitoCount(String objParam) throws ApplicationBusinessException {
		Long count = 0L;
		
		String strPesquisa = (String) objParam;
		if (!StringUtils.isBlank(strPesquisa)) {
			strPesquisa = strPesquisa.toUpperCase();
		}
		
		count = this.internacaoFacade.obterLeitosAtivosPorUnfCount(strPesquisa, null);
		
		return count;
	}
	
	private void carregarAtendimento() {
		this.setListaPrescricaoEnfermagem(this.prescricaoEnfermagemFacade
				.pesquisarPrescricaoEnfermagemNaoEncerradaPorAtendimento(getAtendimento().getSeq(), getDataAtual()));
		this.setPaciente(this.getAtendimento().getPaciente());
		this.setProntuario(this.getPaciente().getProntuario());
		this.setCodPac(this.getPaciente().getCodigo());
		this.setLeito(this.getAtendimento().getLeito());
		this.setQuarto(this.getAtendimento().getQuarto());
		this.setUnidadeFuncional(this.getAtendimento().getUnidadeFuncional());
		this.verificarCriarPrescricao();
		this.atdSeq = this.getAtendimento().getSeq();
	}
	
	public void carregarAtendimentoPorLeito() {
		try {	
			if(leito!=null) {		
				AghAtendimentos atendimento = prescricaoEnfermagemFacade.obterAtendimentoPorLeito(leito.getLeitoID());
				this.setAtendimento(atendimento);
				this.carregarAtendimento();
				this.setarDataReferencia();
			}
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
	}
	
	public void setarDataReferencia() {
		if (paciente != null) {
			this.showModal = false;

			AghAtendimentos atendimento = null;
			final List<AghAtendimentos> listaAtendimentos = prescricaoEnfermagemFacade.obterAtendimentoAtualPorProntuario(prontuario);
			if (listaAtendimentos!=null && !listaAtendimentos.isEmpty()) {
				atendimento = listaAtendimentos.get(0);
			}
			
			if (atendimento != null) {
				this.setAtendimento(atendimento);
				this.carregarAtendimento();
				try {
					this.setDtPrescricao(prescricaoEnfermagemFacade.obterDataReferenciaProximaPrescricao(atendimento, getDataAtual()));
				} catch (ApplicationBusinessException e) {
					apresentarExcecaoNegocio(e);
				}
				
			} else {
				this.apresentarMsgNegocio(Severity.ERROR, 						
						ElaboracaoPrescricaoEnfermagemControllerExceptionCode.ERRO_PRESCRICAO_ENFERMAGEM_PACIENTE_NAO_INTERNADO.toString());
				limparCampos();
			}
			
		} else {
			limparCampos();
		}
	}
	
	public void limparCampos() {
		this.prontuario = null;
		this.codPac = null;
		this.atdSeq = null;
		this.showModal = false;
		this.setPaciente(null);
		this.setLeito(null);
		this.setQuarto(null);
		this.setUnidadeFuncional(null);
		this.setAtendimento(null);
		this.listaPrescricaoEnfermagem.clear();
		this.setDtPrescricao(new Date());
		this.verificarCriarPrescricao();
	}
	
	public String cancelar() {
		this.pacCodigoFonetica = null;
		this.limparCampos();
		if(voltarPara != null){
			if(voltarPara.equalsIgnoreCase("listaPacientesEnfermagem") || this.getAtdSeq()!=null){
				return LISTA_PACIENTES_ENFERMAGEM;
			}
			else if(voltarPara.equalsIgnoreCase("blococirurgico-listaCirurgias")){
				return BLOCOCIRURGICO_LISTAR_CIRURGIAS;//TODO MIGRAÇÃO aguardar migração deste módulo
			}
		}
		return voltarPara;
	}
	
	public String selecionarPrescricaoConsultar(){
		return PRESCRICAOMEDICA_SELECIONAR_PRESCRICAO_CONSULTAR;
	}
	
	public String selecionarPrescricaoEnfermagem(){
		return SELECIONAR_PRESCRICAO_ENFERMAGEM;
	}
	
	private Date getDataAtual() {
		return new Date();
	}
	
	public String editarPrescricao(EpePrescricaoEnfermagem prescricaoEnfermagem)
	throws ApplicationBusinessException {
		try {
			EpePrescricaoEnfermagem prescricaoEnfermagemOld = this.prescricaoEnfermagemFacade.clonarPrescricaoEnfermagem(prescricaoEnfermagem);
			this.setPrescricaoEnfermagem(prescricaoEnfermagem);
			this.prescricaoEnfermagemFacade.editarPrescricao(prescricaoEnfermagemOld, prescricaoEnfermagem, false);
			this.showModal=false;
			setPrescrEnfermagemManterPrescr(prescricaoEnfermagem.getId().getSeq(), prescricaoEnfermagem.getId().getAtdSeq());
			return MANTER_PRESCRICAO_ENFERMAGEM;
		}
		catch (BaseException e) {
			if(e.getLocalizedMessage().equals("PRESCRICAO_ATUAL_EM_USO")){
				this.showModal = true;
				this.setMensagemModal( WebUtil.initLocalizedMessage(e.getLocalizedMessage(), null, e.getParameters()));
			}
			else{
				this.showModal = false;
				this.apresentarExcecaoNegocio(e);
			}
		} 
		return null;
	}

	private void setPrescrEnfermagemManterPrescr(Integer penSeq, Integer penSeqAtd) {
		manutencaoPrescricaoEnfermagemController.setPenSeq(penSeq);
		manutencaoPrescricaoEnfermagemController.setPenSeqAtendimento(penSeqAtd);
	}
	
	public String editarPrescricaoEmUso() {
		try{
			EpePrescricaoEnfermagem prescricaoEnfermagemOld = this.prescricaoEnfermagemFacade.clonarPrescricaoEnfermagem(prescricaoEnfermagem);
			this.prescricaoEnfermagemFacade.editarPrescricao(prescricaoEnfermagemOld, this.getPrescricaoEnfermagem(), true);	
			setPrescrEnfermagemManterPrescr(prescricaoEnfermagem.getId().getSeq(), prescricaoEnfermagem.getId().getAtdSeq());
			return MANTER_PRESCRICAO_ENFERMAGEM;
		} catch(BaseException e){
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
		}
		return null;
	}
	
	private void verificarCriarPrescricao() {
		if (this.getAtendimento() != null) {
			try {
				this.prescricaoEnfermagemFacade.verificarCriarPrescricao(this.getAtendimento());
				this.setMensagemBotaoCriarPrescricao(getBundle().getString("LABEL_NOVA_PRESCRICAO"));
				this.setHabilitaBotaoCriarPrescricao(true);				
			} catch (ApplicationBusinessException e) {
				this.setMensagemBotaoCriarPrescricao(e.getLocalizedMessage());
				this.setHabilitaBotaoCriarPrescricao(false);
			}
		}
		else {
			this.setMensagemBotaoCriarPrescricao("");
			this.setHabilitaBotaoCriarPrescricao(false);
		}
	}
	
	public String criarPrescricao() throws ApplicationBusinessException {
		
		try {
			this.prescricaoEnfermagemFacade.validarDataPrescricao(dtPrescricao, getListaPrescricaoEnfermagem());
		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
			return "erro";
		}
		
		try {
			this.prescricaoEnfermagem = this.prescricaoEnfermagemFacade.criarPrescricao(atendimento, this.dtPrescricao);
			
			List<DiagnosticoEtiologiaVO> listaDiagnosticoEtiologiaVO = 
					this.prescricaoEnfermagemFacade.listarPrescCuidDiagnosticoPorAtdSeqDataInicioDataFim(
							this.prescricaoEnfermagem.getAtendimento().getSeq(), 
							this.prescricaoEnfermagem.getDthrInicio(), 
							this.prescricaoEnfermagem.getDthrFim(), 
							this.prescricaoEnfermagem.getDthrMovimento());
			
			if (!listaDiagnosticoEtiologiaVO.isEmpty()) {
				this.encerramentoDiagnosticoController.setCameFrom("elaboracaoPrescricaoEnfermagem");
				this.encerramentoDiagnosticoController.setPenSeq(prescricaoEnfermagem.getId().getSeq());
				this.encerramentoDiagnosticoController.setPenAtdSeq(prescricaoEnfermagem.getId().getAtdSeq());
				return ENCERRAR_DIAGNOSTICOS;			
			} else {
				setPrescrEnfermagemManterPrescr(prescricaoEnfermagem.getId().getSeq(), prescricaoEnfermagem.getId().getAtdSeq());
				return MANTER_PRESCRICAO_ENFERMAGEM;	
			}
		} catch (BaseException e) {
			this.showModal = false;
			this.apresentarExcecaoNegocio(e);
			LOG.error("Erro",e);
			return null;
		}
	}	
	
	public String redirecionarPesquisaFonetica(){
		/*if (this.prontuario!=null){
			return "";
		}*/
		return PACIENTE_PESQUISA_PACIENTE_COMPONENTE;
	}	
	
	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AinLeitos getLeito() {
		return leito;
	}

	public void setLeito(AinLeitos leito) {
		this.leito = leito;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public AinQuartos getQuarto() {
		return quarto;
	}

	public void setQuarto(AinQuartos quarto) {
		this.quarto = quarto;
	}

	public Boolean getShowModal() {
		return showModal;
	}

	public void setShowModal(Boolean showModal) {
		this.showModal = showModal;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}

	public IPrescricaoEnfermagemFacade getPrescricaoEnfermagemFacade() {
		return prescricaoEnfermagemFacade;
	}
	

	public List<EpePrescricaoEnfermagem> getListaPrescricaoEnfermagem() {
		return listaPrescricaoEnfermagem;
	}

	public void setListaPrescricaoEnfermagem(
			List<EpePrescricaoEnfermagem> listaPrescricaoEnfermagem) {
		this.listaPrescricaoEnfermagem = listaPrescricaoEnfermagem;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	public void setPacienteFacade(IPacienteFacade pacienteFacade) {
		this.pacienteFacade = pacienteFacade;
	}

	public Date getDtPrescricao() {
		return dtPrescricao;
	}

	public void setDtPrescricao(Date dtPrescricao) {
		this.dtPrescricao = dtPrescricao;
	}

	public Boolean getHabilitaBotaoCriarPrescricao() {
		return habilitaBotaoCriarPrescricao;
	}

	public void setHabilitaBotaoCriarPrescricao(Boolean habilitaBotaoCriarPrescricao) {
		this.habilitaBotaoCriarPrescricao = habilitaBotaoCriarPrescricao;
	}

	public String getMensagemBotaoCriarPrescricao() {
		return mensagemBotaoCriarPrescricao;
	}

	public void setMensagemBotaoCriarPrescricao(String mensagemBotaoCriarPrescricao) {
		this.mensagemBotaoCriarPrescricao = mensagemBotaoCriarPrescricao;
	}	
	
	public String getMensagemModal() {
		return mensagemModal;
	}

	public void setMensagemModal(String mensagemModal) {
		this.mensagemModal = mensagemModal;
	}

	public Integer getPacCodigoFonetica() {
		return pacCodigoFonetica;
	}

	public void setPacCodigoFonetica(Integer pacCodigoFonetica) {
		this.pacCodigoFonetica = pacCodigoFonetica;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public EpePrescricaoEnfermagem getPrescricaoEnfermagem() {
		return prescricaoEnfermagem;
	}

	public void setPrescricaoEnfermagem(EpePrescricaoEnfermagem prescricaoEnfermagem) {
		this.prescricaoEnfermagem = prescricaoEnfermagem;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}


	public Integer getCodPac() {
		return codPac;
	}


	public void setCodPac(Integer codPac) {
		this.codPac = codPac;
	}
	
	public boolean isExibirMensagemCancelamento() {
		return exibirMensagemCancelamento;
	}

	public void setExibirMensagemCancelamento(boolean exibirMensagemCancelamento) {
		this.exibirMensagemCancelamento = exibirMensagemCancelamento;
	}
	
	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	public void setPrescricaoMedicaFacade(
			IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}
}
