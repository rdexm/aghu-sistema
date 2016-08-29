package br.gov.mec.aghu.exames.action;

import java.net.UnknownHostException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.solicitacao.action.SolicitacaoExameController;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.AtendimentoExternoVO;
import br.gov.mec.aghu.model.AelLaboratorioExternos;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.action.VerificarPrescricaoMedicaController;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;


public class AtendimentoExternoController extends ActionController {


	private static final long serialVersionUID = 2971514088024436214L;

	private static final Log LOG = LogFactory.getLog(AtendimentoExternoController.class);

	private static final String PESQUISA_PACIENTE 				= "paciente-pesquisaPaciente";
	private static final String CADASTRO_PACIENTE 				= "paciente-cadastroPaciente";
	private static final String PRESCRICAO_MEDICA 				= "prescricaomedica-verificaPrescricaoMedica";
	private static final String SOLICITACAO_EXAME_CRUD 			= "exames-solicitacaoExameCRUD";
	private static final String MEDICO_ATENDIMENTO_EXTERNO_CRUD = "exames-medicoAtendimentoExternoCRUD";
	private static final String LABORATORIO_HEMOCENTRO_CRUD 	= "exames-laboratorioHemocentroCRUD";


	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController;
	
	@Inject
	private VerificarPrescricaoMedicaController verificarPrescricaoMedicaController; 
	
	@Inject
	private SolicitacaoExameController solicitacaoExameController;
	
	private AipPacientes paciente;
	
	private AtendimentoExternoVO atendimentoExternoVo;
	private AtendimentoExternoVO atendimentoExternoVoEmEdicao;
	
	private List<AtendimentoExternoVO> listaAtendimentoExterno;
	
	private Integer atendimentoSeq;
	
	private String origemChamada;
	
	private FatConvenioSaudePlano convenioPlanoselecionado;
	
	private Boolean manterAtendimentoExternoVo = false;



	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}
	
	/**
	 * Metodo configurado no page.xml para ser chamado ao entrar na tela. 
	 */
	public void iniciar() {
	 
		initController();
		this.listaAtendimentoExterno = this.examesFacade.obterAtendimentoExternoList(paciente.getCodigo());
		
		
		if (this.atendimentoExternoVoEmEdicao != null) {
			if (this.listaAtendimentoExterno.contains(this.atendimentoExternoVoEmEdicao)) {
				int index = this.listaAtendimentoExterno.indexOf(this.atendimentoExternoVoEmEdicao);
				AtendimentoExternoVO vo = this.listaAtendimentoExterno.get(index);
				vo.setEmEdicao(Boolean.TRUE);
				this.atendimentoExternoVoEmEdicao = vo;
			}
		}
		
	}
	
	protected void initController() {
		this.listaAtendimentoExterno = this.examesFacade.obterAtendimentoExternoList(paciente.getCodigo());
					
		if(!manterAtendimentoExternoVo){
			this.atendimentoExternoVo = new AtendimentoExternoVO(); 
			this.atendimentoExternoVo.setPaciente(this.getPaciente());		
		}
		
		
		if (convenioPlanoselecionado != null){
			this.atendimentoExternoVo.setConvenioPlano(convenioPlanoselecionado);
		}
		
	}
	
	public String solicitarExame(AtendimentoExternoVO item) {
		AghAtendimentos atendimento = null;
		try {
			atendimento  = solicitacaoExameFacade.verificarPermissoesParaSolicitarExame(item.getAtendimentoSeq());
		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
			return null;
		}
		solicitacaoExameController.setAtendimento(atendimento);
		solicitacaoExameController.setAtendimentoSeq(item.getAtendimento().getSeq());
		solicitacaoExameController.setPaginaChamadora("exames-atendimentoExternoCRUD");
		return SOLICITACAO_EXAME_CRUD;
	}
	
	public void posSelectionConvenio() {
		FatConvenioSaudePlano convenioPlano = this.atendimentoExternoVo.getConvenioPlano() ;
		if (convenioPlano != null){
			this.setConvenioPlanoselecionado(convenioPlano);
		}
	}
	
	public void posDeleteConvenio() {
		this.setConvenioPlanoselecionado(null);
		
	}
	
	public List<FatConvenioSaudePlano> sbObterConvenio(String parametro) {
		return this.returnSGWithCount(examesFacade.listarConvenioSaudePlanos((String) parametro),sbObterConvenioCount(parametro));
	}
	
	public Integer sbObterConvenioCount(String parametro) {
		return examesFacade.listarConvenioSaudePlanosCount((String) parametro);
	}

	
	public List<AghMedicoExterno> sbObterMedicoExterno(String parametro) {
		return this.returnSGWithCount(this.examesFacade.obterMedicoExternoList((String)parametro),sbObterMedicoExternoCount(parametro));
	}
	
	public Long sbObterMedicoExternoCount(String parametro) {
		return this.examesFacade.obterMedicoExternoListCount((String)parametro);		
	}

	
	public List<AelLaboratorioExternos> sbObterLaboratorioHemocentro(String parametro) {
		return this.returnSGWithCount(this.examesFacade.obterLaboratorioExternoList((String)parametro),sbObterLaboratorioHemocentroCount(parametro));
	}
	
	public Integer sbObterLaboratorioHemocentroCount(String parametro) {
		return this.examesFacade.obterLaboratorioExternoList((String)parametro).size();
	}

	/**
	 * Finaliza e cancela Edicao.<br>
	 */
	public void cancelarEdicao() {
		this.manterAtendimentoExternoVo = false;
		this.atendimentoExternoVoEmEdicao = null;
		this.convenioPlanoselecionado = null;		
		this.initController();
	}
	
	public String cancelar(){
		this.manterAtendimentoExternoVo = false;
		atendimentoExternoVoEmEdicao = null;
		atendimentoExternoVo = null;
		this.convenioPlanoselecionado = null;
		
		if("cadastroPaciente".equals(origemChamada)){
			return CADASTRO_PACIENTE;
			
		} else {
			return PESQUISA_PACIENTE;
			
		}
	}
	
	/**
	 * Finalizar e confirma Edicao.<br>
	 */
	public void atualizar() {
		this.atendimentoExternoVoEmEdicao = null;	
		this.gravar();
	}
	
	/**
	 * Inicia Edicao.<br>
	 */
	public void editar(AtendimentoExternoVO vo) {
		if (this.atendimentoExternoVoEmEdicao != null) {
			this.atendimentoExternoVoEmEdicao.setEmEdicao(Boolean.FALSE);
		}
		vo.setEmEdicao(Boolean.TRUE);				
		this.atendimentoExternoVo = vo;
		this.atendimentoExternoVoEmEdicao = vo;
	}
	
	/**
	 * Grava e recarrega lista.<br>
	 */
	public String gravar() {
		final String medicoExternoMessages = super.getBundle().getString("LABEL_ATENDIMENTO_EXTERNO_MEDICO_EXTERNO_NOME");
		final String convenioPlanoMessages = super.getBundle().getString("LABEL_CONVENIO");
		Boolean isRequiredField = Boolean.FALSE;
		
		try {
			if(this.atendimentoExternoVo.getMedicoExterno() == null) {
				apresentarMsgNegocio("sbMedicoExterno",Severity.ERROR, "CAMPO_OBRIGATORIO", medicoExternoMessages);
				isRequiredField = Boolean.TRUE;
			}
			
			if(this.atendimentoExternoVo.getConvenioPlano() == null) {
				apresentarMsgNegocio("sbConvenio", Severity.ERROR, "CAMPO_OBRIGATORIO", convenioPlanoMessages);
				isRequiredField = Boolean.TRUE;
			}
						
			if(isRequiredField) {
				return null;
			}
			
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			
			AghAtendimentosPacExtern model = this.atendimentoExternoVo.getModel();
			model.setUnidadeFuncional(this.unidadeExecutoraController.getUnidadeExecutora());
			RapServidores servidorLogado = registroColaboradorFacade.obterServidorAtivoPorUsuario(obterLoginUsuarioLogado());
			AghAtendimentosPacExtern atdPacExtern = this.examesFacade.gravarAghAtendimentoPacExtern(model, nomeMicrocomputador, servidorLogado);
			List<AghAtendimentos> atendimentos = aghuFacade.listarAtendimentosPorAtendimentoPacienteExterno(atdPacExtern);
			this.convenioPlanoselecionado = null;
			this.manterAtendimentoExternoVo = false;
			this.initController();
			
			this.atendimentoExternoVo.setAtendimento( !atendimentos.isEmpty() ? atendimentos.get(0) : null );
			
			return this.solicitarExame(atendimentoExternoVo);
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			super.apresentarExcecaoNegocio(e);
			return null;
		} catch (BaseRuntimeException modelEx) {
			LOG.error(modelEx.getMessage(), modelEx);
			super.apresentarExcecaoNegocio(modelEx);
			return null;
		}
	}
	
	
	
	public void limpar() {
		this.manterAtendimentoExternoVo = false;
		this.initController();		
	}
	
	
	public String linkParaAddMedicoExterno() {
		this.manterAtendimentoExternoVo = true;
		return MEDICO_ATENDIMENTO_EXTERNO_CRUD;
	}

	public String adicionarLaboratorioHemocentro() {
		this.manterAtendimentoExternoVo = true;
		return LABORATORIO_HEMOCENTRO_CRUD;
	}
	
   /** get/set **/
	public String getProntuarioNomePaciente() {
		StringBuilder prontuarioNome = new StringBuilder();
		
		if (this.paciente != null) {
			if(this.paciente.getProntuario() != null) {
				prontuarioNome.append(CoreUtil.formataProntuario(
						this.paciente.getProntuario())).append(" - ");
			}
			prontuarioNome.append(this.paciente.getNome());
		}
		return prontuarioNome.toString();
	}
	public String redirecionaPrescricaoAmbulatorial(AtendimentoExternoVO atdExtSelec){
		List<AghAtendimentos> atendimentoPmeInform = aghuFacade
				.obterAtendimentosPorPaciente(null, atdExtSelec.getAtendimento().getSeq(), null, ConstanteAghCaractUnidFuncionais.PME_INFORMATIZADA);
		
		if(!atendimentoPmeInform.isEmpty()){
			this.setAtendimentoSeq(atdExtSelec.getAtendimento().getSeq());
			verificarPrescricaoMedicaController.setAtendimentoSeq(atdExtSelec.getAtendimento().getSeq());
			return PRESCRICAO_MEDICA;
		}
		
		apresentarMsgNegocio(Severity.ERROR,"ATENDIMENTOS_DIVERSOS_ERROR_UNIDADE_NAO_POSSUI_CARACTERISTICA_PARA_PRESCRICAO");
		return null;
	}
		
	public AtendimentoExternoVO getAtendimentoExternoVo() {
		return atendimentoExternoVo;
	}

	public void setAtendimentoExternoVo(
			AtendimentoExternoVO atendimentoExternoVo) {
		this.atendimentoExternoVo = atendimentoExternoVo;
	}

	public List<AtendimentoExternoVO> getListaAtendimentoExterno() {
		return listaAtendimentoExterno;
	}

	public void setListaAtendimentoExterno(
			List<AtendimentoExternoVO> listaAtendimentoExterno) {
		this.listaAtendimentoExterno = listaAtendimentoExterno;
	}

	public IdentificarUnidadeExecutoraController getUnidadeExecutoraController() {
		return unidadeExecutoraController;
	}

	public void setUnidadeExecutoraController(
			IdentificarUnidadeExecutoraController unidadeExecutoraController) {
		this.unidadeExecutoraController = unidadeExecutoraController;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getAtendimentoSeq() {
		return atendimentoSeq;
	}
	
	public String getOrigemChamada() {
		return origemChamada;
	}

	public void setOrigemChamada(String origemChamada) {
		this.origemChamada = origemChamada;
	}

	public FatConvenioSaudePlano getConvenioPlanoselecionado() {
		return convenioPlanoselecionado;
	}

	public void setConvenioPlanoselecionado(
			FatConvenioSaudePlano convenioPlanoselecionado) {
		this.convenioPlanoselecionado = convenioPlanoselecionado;
	}

}