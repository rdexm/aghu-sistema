package br.gov.mec.aghu.ambulatorio.action;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoProntuario;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.faturamento.cadastrosapoio.business.IFaturamentoApoioFacade;
import br.gov.mec.aghu.internacao.vo.EspCrmVO;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacFormaAgendamento;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.VAacConvenioPlano;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.vo.CodPacienteFoneticaVO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;

public class AtualizarConsultaController extends ActionController {

	private static final Log LOG = LogFactory.getLog(AtualizarConsultaController.class);

	private static final long serialVersionUID = 1487164847720931521L;
	
	private static final String PESQUISA_FONETICA = "paciente-pesquisaPacienteComponente"; 

	@EJB
	private IFaturamentoApoioFacade faturamentoApoioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPacienteFacade pacienteFacade;

	private Integer nroConsulta = null;
	private AacConsultas consulta = null;
	private AacFormaAgendamento formaAgendamento = null;
	private AelProjetoPesquisas projetoPesquisa = null;
	private RapServidores servidorConsultado = null;
	
	private EspCrmVO espCrmVO;
	private RapServidores servidorAtendimento = null;
	
	private VAacConvenioPlano convenioPlano = null;
	private AipPacientes paciente = null;
	private Integer codigoPaciente = null;
	
	private boolean exibirModalExame = false;
	private boolean exibirModalAtendimento = false;
	private boolean ignorarSolExame = false;
	private boolean ignorarAtendimento = false;
	private Boolean exibirModalAlteracaoPendente = Boolean.FALSE;

	private Long oldCodCentral;
	private String oldJustificativa;
	private AacFormaAgendamento oldFormaAgendamento = null;
	private RapServidores oldServidorAtendimento = null;
	private VAacConvenioPlano oldConvenioPlano = null;
	
	private boolean pesquisou = false;

	@Inject
	@SelectionQualifier
	private Instance<CodPacienteFoneticaVO> codPacienteFonetica;
	
	public enum AtualizarConsultaControllerExceptionCode implements BusinessExceptionCode {
		ERRO_ATUALIZAR_CONSULTA, CONSULTA_ATUALIZADA_SUCESSO, AAC_00166;
	}


	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	public void inicio() {
	 

	 

		CodPacienteFoneticaVO codPac = codPacienteFonetica.get();
		if (codPac != null && codPac.getCodigo() > 0 && consulta != null) { 
			paciente = pacienteFacade.obterAipPacientesPorChavePrimaria(codPac.getCodigo());
			consulta.setPaciente(paciente);
		}
		this.ocultarModalModificacaoPendente();
	
	}
	
	
	public void limparVar() {
		paciente = null;
		consulta = null;
		formaAgendamento = null;
		projetoPesquisa = null;
		servidorConsultado = null;
		espCrmVO = null;
		convenioPlano = null;
		paciente = null;
		codigoPaciente = null;
		exibirModalExame = false;
		exibirModalAtendimento = false;
		ignorarSolExame = false;
		ignorarAtendimento = false;
		servidorAtendimento = null;
		this.ocultarModalModificacaoPendente();
	}
	
	public void limpar() {
		nroConsulta = null;
		pesquisou = false;
		limparVar();
	}
	
	public void pesquisar() {
		try {
			limparVar();
			consulta = ambulatorioFacade.obterConsultasMarcada(this.nroConsulta, false);
			if(consulta != null) {
				if(consulta.getFormaAgendamento() != null){
					Enum[] innerJoins = {AacFormaAgendamento.Fields.PAGADOR,AacFormaAgendamento.Fields.TIPO_AGENDAMENTO, AacFormaAgendamento.Fields.CONDICAO_ATENDIMENTO};
					formaAgendamento = ambulatorioFacade.obterAacFormaAgendamentoPorChavePrimaria(consulta.getFormaAgendamento().getId(), innerJoins, null );
				}
				projetoPesquisa = consulta.getProjetoPesquisa();
				servidorConsultado = consulta.getServidorConsultado();
				
				if(servidorAtendimento != null) {
					espCrmVO = new EspCrmVO();
					espCrmVO.setNomeMedico(servidorAtendimento.getPessoaFisica().getNome());
					espCrmVO.setMatricula(servidorAtendimento.getId().getMatricula());
					espCrmVO.setVinCodigo(servidorAtendimento.getId().getVinCodigo());
					//				servidorAtendimento = registroColaboradorFacade.obterRapServidor(consulta.getServidorAtendido().getId());
				}
				servidorAtendimento = consulta.getServidorAtendido();
				
				if(consulta.getConvenioSaudePlano() != null) {
					convenioPlano = ambulatorioFacade.obterVAacConvenioPlanoAtivoPorId(consulta.getConvenioSaudePlano().getId().getCnvCodigo(), consulta.getConvenioSaudePlano().getId().getSeq());
				}
				paciente = consulta.getPaciente();
				
				this.setarValorAlteracaoPendente();
			}
			this.setPesquisou(true);
		} catch(Exception e) {
			LOG.error("Exceção capturada: ", e);
			apresentarMsgNegocio(Severity.ERROR,  AtualizarConsultaControllerExceptionCode.ERRO_ATUALIZAR_CONSULTA.toString());
		}
	}
	
	
	private void setarValorAlteracaoPendente() {
		this.oldCodCentral = consulta.getCodCentral();
		this.oldJustificativa = consulta.getJustificativa();
		this.oldFormaAgendamento = this.formaAgendamento;
		this.oldServidorAtendimento = this.servidorAtendimento;
		this.oldConvenioPlano =  this.convenioPlano;
	}
	
	private void setarValorAlteracaoPendenteAnterior() {
		this.consulta.setCodCentral(this.oldCodCentral);
		this.consulta.setJustificativa(this.oldJustificativa);
		this.formaAgendamento = this.oldFormaAgendamento;
		this.servidorAtendimento = this.oldServidorAtendimento;
		this.convenioPlano = this.oldConvenioPlano;
	}
	
	
	public void atualizar() {
		try {
			AacConsultas consultaOriginal = ambulatorioFacade.obterConsultasMarcada(consulta.getNumero(), true);
			ambulatorioFacade.desatacharConsulta(consultaOriginal);
			
			consulta.setFormaAgendamento(formaAgendamento);
			if(formaAgendamento!=null){
				consulta.setCondicaoAtendimento(formaAgendamento.getCondicaoAtendimento());
				consulta.setTipoAgendamento(formaAgendamento.getTipoAgendamento());
				consulta.setPagador(formaAgendamento.getPagador());
			}
			consulta.setProjetoPesquisa(projetoPesquisa);
			consulta.setServidorConsultado(servidorConsultado);
			
			consulta.setServidorAtendido(null);
			if(espCrmVO != null) {
				servidorAtendimento = registroColaboradorFacade.obterRapServidor(new RapServidoresId(espCrmVO.getMatricula(),espCrmVO.getVinCodigo()));
				consulta.setServidorAtendido(registroColaboradorFacade.obterRapServidor(new RapServidoresId(servidorAtendimento.getId().getMatricula(), servidorAtendimento.getId().getVinCodigo())));
			}
			
			consulta.setConvenioSaudePlano(null);
			if(convenioPlano != null) {
				consulta.setConvenioSaudePlano(faturamentoApoioFacade.obterPlanoPorId(convenioPlano.getId().getPlano(), convenioPlano.getId().getCnvCodigo()));
			}
			
			
			if(!ignorarSolExame && ambulatorioFacade.existeSolicitacaoExame(consulta)) {
				this.openDialog("modalAvisoSolExameWG");
				exibirModalExame = true;
			}
			
			if(!ignorarAtendimento && ambulatorioFacade.existeAtendimentoEmAndamento(consulta)) {
				this.openDialog("modalAvisoAtendimentoWG");
				exibirModalAtendimento = true;
			}
			
			if(!exibirModalExame && !exibirModalAtendimento){						
				ambulatorioFacade.preAtualizar(consulta);
				
				String nomeMicrocomputador = null;
				try {
					nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
				} catch (UnknownHostException e1) {
					LOG.error("Exceção capturada:", e1);
				}
				
				consulta = ambulatorioFacade.atualizarConsulta(consultaOriginal, consulta, false, nomeMicrocomputador, new Date(), false);
				nroConsulta = consulta.getNumero();
				pesquisar();
				
				if(consulta.getPaciente() != null && DominioTipoProntuario.R.equals(consulta.getPaciente().getPrntAtivo())) {
					apresentarMsgNegocio(Severity.INFO, AtualizarConsultaControllerExceptionCode.AAC_00166.toString());
				}
				apresentarMsgNegocio(Severity.INFO,  AtualizarConsultaControllerExceptionCode.CONSULTA_ATUALIZADA_SUCESSO.toString());
				
			}
		} catch(BaseException mbe) {
			apresentarExcecaoNegocio(mbe);
			
		} catch(NumberFormatException nfe) {
			apresentarMsgNegocio(Severity.ERROR,  AtualizarConsultaControllerExceptionCode.ERRO_ATUALIZAR_CONSULTA.toString());
			LOG.error(nfe.getMessage(), nfe);
		}
	}
	
	public void ignorarSolExame() {
		this.ignorarSolExame = true;
		exibirModalExame = false;
		this.atualizar();
	}
	
	public void ignorarAtendimento() {
		this.ignorarAtendimento = true;
		exibirModalAtendimento = false;
		this.atualizar();
	}
	
	/**
	 * Verifica se houve modificacao em um dos campos 
	 */
	public String verificarModificacao() {
		
		if(CoreUtil.modificados(this.consulta.getCodCentral(), this.oldCodCentral) 
				|| CoreUtil.modificados(this.consulta.getJustificativa(), this.oldJustificativa)
				|| CoreUtil.modificados(this.formaAgendamento, this.oldFormaAgendamento)
				|| CoreUtil.modificados(this.servidorAtendimento, this.oldServidorAtendimento)
				|| CoreUtil.modificados(this.convenioPlano, this.oldConvenioPlano)) {
			
			this.exibirModalAlteracaoPendente = Boolean.TRUE;
			return null;
		} else {
			
			return PESQUISA_FONETICA;
		}
	}
	
	public String chamarTelaPesquisaFonetica() {
		this.setarValorAlteracaoPendenteAnterior();
		this.exibirModalAlteracaoPendente = Boolean.FALSE;
		return PESQUISA_FONETICA;
	}
	
	public void ocultarModalModificacaoPendente() {
		this.exibirModalAlteracaoPendente = Boolean.FALSE;
	}

	public List<VAacConvenioPlano> obterConvenios(String parametro) {
		List<VAacConvenioPlano> resultList = ambulatorioFacade.getListaConvenios((String) parametro);
		return resultList;
	}
	
	public List<AacFormaAgendamento> obterFormasAgendamento(String parametro) {
		List<AacFormaAgendamento> resultList = ambulatorioFacade.pesquisaFormaAgendamentoPorStringEFormaAgendamento((String) parametro, null, null, null);
		return this.returnSGWithCount(resultList,obterFormasAgendamentoCount(parametro));
	}

	public Long obterFormasAgendamentoCount(String parametro) {
		return ambulatorioFacade.pesquisaFormaAgendamentoPorStringEFormaAgendamentoCount((String) parametro, null, null, null);
	}

	public List<AelProjetoPesquisas> obterProjetosPesquisa(String strPesquisa) {
		return this.returnSGWithCount(examesFacade.pesquisarTodosProjetosPesquisa((strPesquisa!=null)?((String)strPesquisa).toString():null),obterProjetosPesquisaCount(strPesquisa));
	}
	
	public Long obterProjetosPesquisaCount(String strPesquisa) {
		return examesFacade.pesquisarTodosProjetosPesquisaCount((strPesquisa!=null)?((String)strPesquisa).toString():null);
	}

	public List<RapServidores> obterServidores(Object strPesquisa) {
		return registroColaboradorFacade.pesquisarServidores(strPesquisa);
	}

	public Long obterServidoresCount(Object strPesquisa) {
		return registroColaboradorFacade.pesquisarServidoresCount(strPesquisa);
	}
	
	public List<EspCrmVO> obterMedicos(String parametro) throws ApplicationBusinessException {
		List<Object[]> listaObject = new ArrayList<Object[]>();
		if (consulta.getGradeAgendamenConsulta().getEspecialidade() != null){
			listaObject =  aghuFacade.pesquisarEspCrmVOAmbulatorioEspecialidade(parametro, consulta.getGradeAgendamenConsulta().getEspecialidade());
		}
		Set<EspCrmVO> setEspCrmVO = new HashSet<EspCrmVO>();
		for (Object[] objeto : listaObject){
			EspCrmVO espCrmVO = new EspCrmVO();
			espCrmVO.setNomeMedico((String) objeto[0]);
			espCrmVO.setMatricula((Integer) objeto[1]);
			espCrmVO.setVinCodigo((Short) objeto[2]);
			espCrmVO.setEspSeq((Short)objeto[3]);
			setEspCrmVO.add(espCrmVO);
		}
		List<EspCrmVO> listaEspCrmVO = new ArrayList<EspCrmVO>(setEspCrmVO);
		return listaEspCrmVO;
	}

	public Integer getNroConsulta() {
		return nroConsulta;
	}

	public void setNroConsulta(Integer nroConsulta) {
		this.nroConsulta = nroConsulta;
	}
	
	public AacConsultas getConsulta() {
		return consulta;
	}

	public void setConsulta(AacConsultas consulta) {
		this.consulta = consulta;
	}

	public AacFormaAgendamento getFormaAgendamento() {
		return formaAgendamento;
	}

	public void setFormaAgendamento(AacFormaAgendamento formaAgendamento) {
		this.formaAgendamento = formaAgendamento;
	}

	public VAacConvenioPlano getConvenioPlano() {
		return convenioPlano;
	}

	public void setConvenioPlano(VAacConvenioPlano convenioPlano) {
		this.convenioPlano = convenioPlano;
	}

	public AelProjetoPesquisas getProjetoPesquisa() {
		return projetoPesquisa;
	}

	public void setProjetoPesquisa(AelProjetoPesquisas projetoPesquisa) {
		this.projetoPesquisa = projetoPesquisa;
	}

	public RapServidores getServidorConsultado() {
		return servidorConsultado;
	}

	public void setServidorConsultado(RapServidores servidorConsultado) {
		this.servidorConsultado = servidorConsultado;
	}

	public Integer getCodigoPaciente() {
		return codigoPaciente;
	}

	public void setCodigoPaciente(Integer codigoPaciente) {
		this.codigoPaciente = codigoPaciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public boolean isExibirModalExame() {
		return exibirModalExame;
	}

	public void setExibirModalExame(boolean exibirModalExame) {
		this.exibirModalExame = exibirModalExame;
	}
	
	public void setExibirModalExame(String exibirModalExame) {
		this.exibirModalExame = Boolean.valueOf(exibirModalExame);
	}
	
	public boolean isExibirModalAtendimento() {
		return exibirModalAtendimento;
	}

	public void setExibirModalAtendimento(boolean exibirModalAtendimento) {
		this.exibirModalAtendimento = exibirModalAtendimento;
	}

	public void setExibirModalAtendimento(String exibirModalAtendimento) {
		this.exibirModalAtendimento = Boolean.valueOf(exibirModalAtendimento);
	}

	public boolean isIgnorarSolExame() {
		return ignorarSolExame;
	}

	public void setIgnorarSolExame(boolean ignorarSolExame) {
		this.ignorarSolExame = ignorarSolExame;
	}

	public void setIgnorarSolExame(String ignorarSolExame) {
		this.ignorarSolExame = Boolean.valueOf(ignorarSolExame);
	}

	public boolean isIgnorarAtendimento() {
		return ignorarAtendimento;
	}

	public void setIgnorarAtendimento(boolean ignorarAtendimento) {
		this.ignorarAtendimento = ignorarAtendimento;
	}
	
	public void setIgnorarAtendimento(String ignorarAtendimento) {
		this.ignorarAtendimento = Boolean.valueOf(ignorarAtendimento);
	}
	
	public void ocultarModal(){
		exibirModalExame = false;
		exibirModalAtendimento = false;
		ignorarSolExame = false;
		ignorarAtendimento = false;
	}

	public Boolean getExibirModalAlteracaoPendente() {
		return exibirModalAlteracaoPendente;
	}

	public void setExibirModalAlteracaoPendente(Boolean exibirModalAlteracaoPendente) {
		this.exibirModalAlteracaoPendente = exibirModalAlteracaoPendente;
	}
	
	public void setEspCrmVO(EspCrmVO espCrmVO) {
		this.espCrmVO = espCrmVO;
	}

	public EspCrmVO getEspCrmVO() {
		return espCrmVO;
	}

	public boolean isPesquisou() {
		return pesquisou;
	}

	public void setPesquisou(boolean pesquisou) {
		this.pesquisou = pesquisou;
	}

}