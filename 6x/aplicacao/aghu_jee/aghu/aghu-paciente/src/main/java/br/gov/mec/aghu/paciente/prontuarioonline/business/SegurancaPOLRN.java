package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.model.AelProjetoPacientes;
import br.gov.mec.aghu.model.AelProjetoPesquisas;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CsePerfilProcessoLocal;
import br.gov.mec.aghu.model.CsePerfilProcessos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.dao.AipAcessoPacientesDAO;
import br.gov.mec.aghu.paciente.dao.AipPacientesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
//import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class SegurancaPOLRN extends BaseBusiness{

	private static final Log LOG = LogFactory.getLog(SegurancaPOLRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
	return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@Inject
	private AipAcessoPacientesDAO aipAcessoPacientesDAO;
	
	@Inject
	private AipPacientesDAO aipPacientesDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private static final long serialVersionUID = 2727862477426617697L;

	public enum VerPacEspecialTargetCode {
		N, S, E;
	}

	/**
	 * @ORADB c_ver_pac_especial
	 *
	 * @param pacCodigo
	 * @return 
	 * @throws ApplicationBusinessException 
	 *  
	 */
	public String verificarPacienteEspecial(Integer pacCodigo, boolean vRetornoAtendimento) throws ApplicationBusinessException {
		String vRetorno = VerPacEspecialTargetCode.N.toString();
		
		AipPacientes pac = getAipPacientesDAO().obterPorChavePrimaria(pacCodigo);
		
		Boolean vIndAcessoSemAtend = getIndAcessoSemAtend(pacCodigo);
		Boolean vIndAcessoComAtend = getIndAcessoComAtend(pacCodigo);
		Boolean vIndAcessoOutro = getIndAcessoOutro(pacCodigo);
		
		if (pac.isVip()) {
			if ((vRetornoAtendimento && vIndAcessoComAtend) || (!vRetornoAtendimento && vIndAcessoSemAtend)) {
				vRetorno = VerPacEspecialTargetCode.S.toString();
			} else if ((vRetornoAtendimento && vIndAcessoOutro)	|| (!vRetornoAtendimento && !vIndAcessoSemAtend)) {
				vRetorno = VerPacEspecialTargetCode.E.toString();
			}
		} 
		
		return vRetorno;
	}
	
	private Boolean getIndAcessoSemAtend(Integer pacCodigo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Boolean retorno = Boolean.FALSE;
		Long valor = getAcessoPacientesDAO().pesquisarAcessoPacienteSemCount(pacCodigo, servidorLogado != null ? servidorLogado.getUsuario() : null);
		if (valor != null && valor.intValue() > 0) {
			retorno = Boolean.TRUE;
		}
		return retorno;
	}
	
	private Boolean getIndAcessoComAtend(Integer pacCodigo) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Boolean retorno = Boolean.FALSE;
		Long valor = getAcessoPacientesDAO().pesquisarAcessoPacienteComCount(pacCodigo, servidorLogado != null ? servidorLogado.getUsuario() : null);
		if (valor != null && valor.intValue() > 0) {
			retorno = Boolean.TRUE;
		}
		return retorno;
	}
	
	private Boolean getIndAcessoOutro(Integer pacCodigo) {
		Boolean retorno = Boolean.FALSE;
		Long valor = getAcessoPacientesDAO().pesquisarAcessoPacientePacCount(pacCodigo);
		if (valor != null && valor.intValue() > 0) {
			retorno = Boolean.TRUE;
		}
		return retorno;
	}
	
	protected AipAcessoPacientesDAO getAcessoPacientesDAO() {
		return aipAcessoPacientesDAO;
	}
	
	protected AipPacientesDAO getAipPacientesDAO() {
		return aipPacientesDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IExamesFacade getExamesFacade() {
		return examesFacade;
	}
	
	/**
	 * ORADB Function AIPC_VER_MONITOR_PRJ
	 * 
	 * @param codigoPaciente
	 * @param servidorMatricula
	 * @param servidorVinCodigo
	 * @param funcao - se informado('M'), verifica se usuario participa do projeto de pesquisa como monitor
	 * @return
	 *  
	 */
	protected boolean verificarSeUsuarioParticipaDeProjetoDePesquisaComPaciente(Integer codigoPaciente, Integer servidorMatricula, Short servidorVinCodigo, String funcao) throws ApplicationBusinessException {
		
		AghParametros parametro = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_DIAS_POL_PESQ_PROJ);
		Integer quantidadeDias = 0;
		
		if(parametro != null) {
			quantidadeDias = parametro.getVlrNumerico().intValue();
		}

		Date dataInicio = Calendar.getInstance().getTime();
		Date dataFinal = DateUtil.adicionaDias(dataInicio, -quantidadeDias);
		
		List<AelProjetoPesquisas> projetosPesquisa = getExamesFacade().pesquisarProjetosPacientes(codigoPaciente, servidorMatricula, servidorVinCodigo, dataInicio, dataFinal);
		List<AelProjetoPacientes> projetosEquipe = getExamesFacade().pesquisarProjetoEquipesComPaciente(codigoPaciente, servidorMatricula, servidorVinCodigo, funcao);		
		List<AelProjetoPacientes> projetosPaciente = getExamesFacade().buscarProjetosPesquisaComPaciente(codigoPaciente, servidorMatricula, servidorVinCodigo, funcao);
						
		if(!projetosPesquisa.isEmpty()) {
			if(!projetosEquipe.isEmpty()) {
				return Boolean.TRUE;
			}
			if(!projetosPaciente.isEmpty()) {
				return Boolean.TRUE;
			}
		} 
		return Boolean.FALSE;
	}
	
	protected Integer obterValorNumericoParametro(AghuParametrosEnum aghuParametrosEnum) throws ApplicationBusinessException {
		AghParametros param = getParametroFacade().obterAghParametro(aghuParametrosEnum);
		Integer vlr = null;
		if (param != null && param.getVlrNumerico() != null) {
			vlr = param.getVlrNumerico().intValue();
		}
		return vlr;
	}
	
	/**
	 * @ORADB - P_PAC_EM_ATENDIMENTO_COM_RESTRICAO
	 * @ORADB - AIPP_POL_VER_ATEND
	 * 
	 * @see Para atender a AIPP_POL_VER_ATEND informe comRestricao igual a falso
	 * 
	 * @see Para atender a P_PAC_EM_ATENDIMENTO_COM_RESTRICAO informe comRestricao igual a true
	 * 
	 * @param pacCodigo
	 * @param comRestricao
	 * @return
	 * @throws ApplicationBusinessException  
	 */
	public Boolean verificarPacienteEmAtendimento(Integer pacCodigo, Boolean comRestricao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Boolean retorno = Boolean.FALSE;		

		Integer pNroDiasIntPas = null;
		Integer pNroDiasMbcPas = null;
		Integer pNroDiasMbcFut = null;
		Integer pNroDiasAacPas = null;
		Integer pNroDiasAacFut = null;
		Integer pNroDiasAelPas = null;
		Integer pNroDiasAelFut = null;
		
		if (comRestricao) {
			pNroDiasIntPas = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_INT_PAS);
			pNroDiasMbcPas = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_MBC_PAS);
			pNroDiasMbcFut = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_MBC_FUT);
			pNroDiasAacPas = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_AAC_PAS);
			pNroDiasAacFut = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_AAC_FUT);
			pNroDiasAelPas = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_AEL_PAS);
			pNroDiasAelFut = obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_AEL_FUT);
		}
		
//		Short paramReteronoConsAgendada = parametroFacade.buscarValorShort(AghuParametrosEnum.P_RETORNO_CONS_AGENDADA);
		
		String login =servidorLogado.getUsuario();
		Boolean acessoEspecialPOLEmergencia  = getICascaFacade().usuarioTemPermissao(login, "acessoEspecialPOLEmergencia");
		Boolean acessoEspecialPOLFaturamento = getICascaFacade().usuarioTemPermissao(login, "acessoEspecialPOLFaturamento");
		
		if(acessoEspecialPOLEmergencia || acessoEspecialPOLFaturamento){//#23364
			if(!getAghuFacade().buscarPacienteInternado(pacCodigo).isEmpty()){
				retorno = Boolean.TRUE;
			}else {
				Boolean userEmg = processarAtendimentoComCaractEmg(
						pacCodigo, AghuParametrosEnum.P_AGHU_DIAS_CONSULTA_POL_ADM_EMG, ConstanteAghCaractUnidFuncionais.ATEND_EMERG_TERREO);
				if(acessoEspecialPOLEmergencia && userEmg){
					retorno = Boolean.TRUE;
				}else {
					Boolean userFat = processarAtendimentoComCaractEmg(
							pacCodigo, AghuParametrosEnum.P_AGHU_DIAS_CONSULTA_POL_ADM_FAT, null);
					if(acessoEspecialPOLFaturamento && userFat){
					retorno = Boolean.TRUE;
					}
				}
			}
		}else{
			if (!getAghuFacade().buscarPacienteInternado(pacCodigo).isEmpty()) {
				retorno = Boolean.TRUE;
			} else if (!getAghuFacade().buscarPacientesAlta(pacCodigo, pNroDiasIntPas).isEmpty()) {
				retorno = Boolean.TRUE;
			} else if (!getBlocoCirurgicoFacade().verificarSePacienteTemCirurgia(pacCodigo, pNroDiasMbcPas, pNroDiasMbcFut).isEmpty()) {
				retorno = Boolean.TRUE;
			} else if (!getAmbulatorioFacade().verificarSePacienteTemConsulta(pacCodigo, pNroDiasAacPas, pNroDiasAacFut,
					DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo()).isEmpty()) { 
				retorno = Boolean.TRUE;
			} else if (!getBlocoCirurgicoFacade().pesquisarCirurgiaAgendadaPorPaciente(pacCodigo, pNroDiasMbcFut, pNroDiasMbcPas).isEmpty()) {
				retorno = Boolean.TRUE;
			} else if (!getAghuFacade().verificarSePacienteTemAtendimento(pacCodigo, pNroDiasAelPas, pNroDiasAelFut).isEmpty()) {
				retorno = Boolean.TRUE;
			} else if (getAmbulatorioFacade().pacienteEmAtendimentoEmergenciaTerreo(pacCodigo)) {
				retorno = Boolean.TRUE;
			} else if (getAmbulatorioFacade().pacienteEmAtendimentoEmergenciaUltimosDias(pacCodigo, getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_NRO_DIAS_TRG_PAS).getVlrNumerico().intValue())) {
				retorno = Boolean.TRUE;
			}
		}
		return retorno;
	}
	
	private Boolean processarAtendimentoComCaractEmg(Integer pacCodigo,
			AghuParametrosEnum paramEnumDias,
			ConstanteAghCaractUnidFuncionais caractUnf)
			throws ApplicationBusinessException {
		Integer diasConsulta = getParametroFacade().buscarAghParametro(paramEnumDias).getVlrNumerico().intValue();
		Date dataDiasConsulta = DateUtil.adicionaDias(DateUtil.truncaData(new Date()), diasConsulta*-1);
		List listaCaract = getAghuFacade().pesquisarCaracteristicaComAtendimentoPorPaciente(
				pacCodigo,
				dataDiasConsulta,
				Arrays.asList(DominioOrigemAtendimento.I, DominioOrigemAtendimento.U),
				caractUnf);
		
		if(listaCaract == null || listaCaract.isEmpty()){
			return Boolean.FALSE;
		}else{
			return Boolean.TRUE;
		}
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	public Boolean verificaSeUsuarioEhProfessorPacienteEmAtendimento(AipPacientes paciente, Boolean comRestricao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Integer codVinculoProfessor = obterValorNumericoParametro(AghuParametrosEnum.P_COD_VINCULO_PROFESSOR);
		return servidorLogado.getVinculo().getCodigo().intValue() == codVinculoProfessor.intValue()
				&& verificarPacienteEmAtendimento(paciente.getCodigo(), comRestricao);
	}

	/**
	 * Q_PERFIL_CONSULTA
	 * @return
	 *  
	 */
	public boolean verificaSeUsuarioTemPerfilDeConsulta(Short paramSeqProcConsPol) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		boolean usuarioPossuiPerfilProcesso = false;
		
		Set<String> nomePerfisUsuario = getICascaFacade().obterNomePerfisPorUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null);
		List<CsePerfilProcessos> perfisProcesso = getRegistroColaboradorFacade().pesquisarPerfisProcessos(Boolean.TRUE, 
																										  DominioSituacao.A, 
																										  paramSeqProcConsPol);		
		for(String nomePerfil : nomePerfisUsuario) {
			for(CsePerfilProcessos pp : perfisProcesso) {	
				String nomePerfilProcesso = pp.getId().getPerNome();
				if(nomePerfil.equalsIgnoreCase(nomePerfilProcesso)) {
					usuarioPossuiPerfilProcesso = true;	
					break;
				}
			}
		}
		return usuarioPossuiPerfilProcesso;
	}

	/**
	 * Q_VERIF_PERFIL Verifica se usuário possui perfil com permissão de
	 * consulta. 
	 * CSE_PERFIL_PROCESSO_LOCAIS: Armazena informações de onde o
	 * processo pode ser executado, assinado ou consultado por determinado
	 * perfil considerando o local em que o paciente está sendo atendido e o
	 * local em que este usuário está atuando.
	 * 
	 * @return
	 * 
	 */
	public boolean validarSeUsuarioPossuiPerfilProcessoLocal(Short paramSeqProcConsPol) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		boolean usuarioPossuiPerfil = false;
		
		Set<String> nomePerfisUsuario = getICascaFacade().obterNomePerfisPorUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null);
		List<CsePerfilProcessoLocal> perfisProcesso = getRegistroColaboradorFacade().pesquisarPerfisProcessoLocal(DominioSimNao.S, 
																												  DominioSituacao.A, 
																												  paramSeqProcConsPol, 
																												  null);		
		for(String nomePerfil : nomePerfisUsuario) {
			for(CsePerfilProcessoLocal pp : perfisProcesso) {
				if(StringUtils.equals(pp.getPerNome(), nomePerfil)) {
					usuarioPossuiPerfil = true;
					break;
				}
			}
		}
		return usuarioPossuiPerfil;
	}
	
	public boolean validarSeUsuarioPossuiPerfilProcessoLocalComCentroDeCusto(Short paramSeqProcConsPol) throws ApplicationBusinessException {
		
		CsePerfilProcessoLocal perfil = getPerfilUsuarioPossuiPerfilProcessoLocalComCentroDeCusto(paramSeqProcConsPol);
		if(perfil.getSeq()== null){
			return Boolean.FALSE;
		}else{
			return Boolean.TRUE;
		}
	}
	
	/**
	 * Q_PERFIL
	 * Verifica se o usuario tem perfil  com permissão de consulta associado ao seu centro de custo
	 * @return CsePerfilProcessoLocal
	 *  
	 */
	public CsePerfilProcessoLocal getPerfilUsuarioPossuiPerfilProcessoLocalComCentroDeCusto(Short paramSeqProcConsPol) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();
		
		Integer codigoCentroCustoLotacao = null;
		if(servidorLogado != null && servidorLogado.getCentroCustoLotacao() != null) {
			codigoCentroCustoLotacao = servidorLogado.getCentroCustoLotacao().getCodigo();
		}
		
		Set<String> nomePerfisUsuario = getICascaFacade().obterNomePerfisPorUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null);
		List<CsePerfilProcessoLocal> perfisProcesso = getRegistroColaboradorFacade().pesquisarPerfisProcessoLocal(DominioSimNao.S, 
																												  DominioSituacao.A, 
																												  paramSeqProcConsPol,
																												  codigoCentroCustoLotacao);
		for(String nomePerfil : nomePerfisUsuario) {
			for(CsePerfilProcessoLocal pp : perfisProcesso) {
				if(nomePerfil.equalsIgnoreCase(pp.getPerNome())) {
					return pp;				
				}
			}
		}
	
		return new CsePerfilProcessoLocal();
	}
	
	public boolean exigePacienteEmAtendimento(Short paramSeqProcConsPol) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogadoSemCache();

		boolean exigePacienteEmAtendimento = false;
		
		Integer codigoCentroCustoLotacao = null;
		if(servidorLogado != null && servidorLogado.getCentroCustoLotacao() != null) {
			codigoCentroCustoLotacao = servidorLogado.getCentroCustoLotacao().getCodigo();
		}
		
		Set<String> nomePerfisUsuario = getICascaFacade().obterNomePerfisPorUsuario(servidorLogado != null ? servidorLogado.getUsuario() : null);
		List<CsePerfilProcessoLocal> perfisProcesso = getRegistroColaboradorFacade().pesquisarPerfisProcessoLocal(DominioSimNao.S, 
																												  DominioSituacao.A, 
																												  paramSeqProcConsPol,
																												  codigoCentroCustoLotacao);
		for(String nomePerfil : nomePerfisUsuario) {
			for(CsePerfilProcessoLocal pp : perfisProcesso) {
				if(nomePerfil.equalsIgnoreCase(pp.getPerNome())) {
					if(pp.getIndExigeEmAtend().equals(DominioPacAtendimento.S.toString())){ //exigencia do paciente em atendimento
						exigePacienteEmAtendimento = true;	
						break;
					}
				}
			}
		}
		
		return exigePacienteEmAtendimento;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
