package br.gov.mec.aghu.paciente.prontuarioonline.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.administracao.business.IAdministracaoFacade;
import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioCaracteristicaMicrocomputador;
import br.gov.mec.aghu.dominio.DominioOcorrenciaPOL;
import br.gov.mec.aghu.dominio.DominioPacAtendimento;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioSituacaoAtendimento;
import br.gov.mec.aghu.model.AghMicrocomputador;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipGrupoFamiliarPacientes;
import br.gov.mec.aghu.model.AipLogProntOnlines;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.CsePerfilProcessoLocal;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.dao.AipLogProntOnlinesDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseListException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class SegurancaPOLON extends BaseBMTBusiness {

	@EJB
	private SegurancaPOLRN segurancaPOLRN;
	
	private static final Log LOG = LogFactory.getLog(SegurancaPOLON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private AipLogProntOnlinesDAO aipLogProntOnlinesDAO;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IAdministracaoFacade administracaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	private static final long serialVersionUID = -5626155280017978050L;

	protected AipLogProntOnlinesDAO getAipLogProntOnlinesDAO(){
		return aipLogProntOnlinesDAO;
	}
	
	public enum SegurancaPOLONExceptionCode implements BusinessExceptionCode {
		ERRO_AIP_00254, NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR, MICRO_NAO_UBS, PACIENTE_NAO_UBS;
	}
	
	public enum VerPacEspecialTargetCode {
		S, E;
	}
	
	/**
	 * Identifica a tela que originou a requisicao
	 *
	 */
	public enum EnumPaginaOrigem {
		LISTA_ESPERA_PORTAL_CIRURGIAS,
		LISTA_CANCELADOS_PORTAL_CIRURGIAS;
	}
	
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return blocoCirurgicoFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return aghuFacade;
	}
		

	/**
	 * Colocar a referencia para AIPK_POL_VER_ACESSO
	 * Valida as permissões para exibição da árvore de prontuário online.<br> 
	 * Loga as tentativas de acesso aos prontuários.<BR>
	 * Retorna a lista de pacientes que o usuário tem acesso à árvore, lançará uma lista de exceção para os que não tiver acesso.
	 * @param pacientes
	 * @param acessoLivrePOL
	 * @param acessoComissaoPOL
	 * @param acessoEspecialPOL
	 * @param acessoAdminPOL
	 * @param acessoMonitorProjPesqPOL
	 * @param acessoPesquisadorProjPesqPOL
	 * @param paginaOrigem
	 * @return
	 * @throws ApplicationBusinessException
	 * @author bruno.mourao
	 * @throws ApplicationBusinessException 
	 * @since 21/06/2012
	 */
	public List<AipPacientes> validarPermissoesPOL(AipPacientes paciente, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Short processoConsultaPOL) throws ApplicationBusinessException,BaseListException {
		List<AipPacientes> pacAcessoLiberado = new ArrayList<AipPacientes>();
		
		BaseListException listaExcept = new BaseListException();
		validarAcessoPemissaoAcessoLivre(paciente, pacAcessoLiberado, parametros, listaExcept, nomeMicrocomputador, processoConsultaPOL);
		if (listaExcept.hasException()){
			throw listaExcept;
		}
				
		return pacAcessoLiberado;
	}
	
	
	
	public void verificarPerfilUBS(String usuario, String micro, AipPacientes paciente) throws ApplicationBusinessException{
		if (cascaFacade.usuarioTemPerfil(this.obterLoginUsuarioLogado(),
				"ESP08")
				&& !cascaFacade.usuarioTemPerfil(
						this.obterLoginUsuarioLogado(), "ESP03")) {
			if (!verificarComputadorUbs(micro)){
				throw new ApplicationBusinessException(SegurancaPOLONExceptionCode.MICRO_NAO_UBS);
			}
			AipGrupoFamiliarPacientes grupoFamiliarPaciente = pacienteFacade.obterDadosGrupoFamiliarPaciente(paciente);
			if (grupoFamiliarPaciente == null){
				throw new ApplicationBusinessException(SegurancaPOLONExceptionCode.PACIENTE_NAO_UBS);
			}
		}
	}
	
	
	private Boolean verificarComputadorUbs(String strMicro){
    	Boolean retorno = false;
    	AghMicrocomputador micro = administracaoFacade.obterAghMicroComputadorPorNomeOuIP(strMicro,DominioCaracteristicaMicrocomputador.PERFIL_UBS);
		if (micro != null){
			retorno = true;
		}
    	return retorno;
    }


	/**
	 * Item 2
	 * @param paciente
	 * @param pacAcessoLiberado
	 * @param acessoLivrePOL
	 * @param acessoComissaoPOL
	 * @param acessoEspecialPOL
	 * @param acessoAdminPOL
	 * @param acessoMonitorProjPesqPOL
	 * @param acessoPesquisadorProjPesqPOL
	 * @param paginaOrigem
	 * @param listaExcept
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarAcessoPemissaoAcessoLivre(AipPacientes paciente, List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, BaseListException listaExcept, String nomeMicrocomputador, Short paramConsultaPOL) throws ApplicationBusinessException{
		Boolean acessoLivrePOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_LIVRE_POL);
		if (acessoLivrePOL) {
			if(paciente.isVip()){
				//Loga acesso liberado a paciente vip
				gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_VIP, nomeMicrocomputador);
			} else{
				//loga acesso liberado a paciente
				gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
			}
			pacAcessoLiberado.add(paciente);
		}
		else{
			//item 3
			verificarPacienteEspecial(paciente,listaExcept,pacAcessoLiberado,parametros, nomeMicrocomputador, paramConsultaPOL);
		}
	}

	/**
	 * Item 4
	 * @param paciente
	 * @param listaExcept 
	 * @param pacAcessoLiberado
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @param acessoMonitorProjPesqPOL 
	 * @param acessoAdminPOL 
	 * @param acessoEspecialPOL 
	 * @param acessoComissaoPOL 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarAcessoComissaoPOL(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException{
		Boolean acessoComissaoPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_COMISSAO_POL);
		if (acessoComissaoPOL) {
			if(!paciente.isVip()){
				gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
				pacAcessoLiberado.add(paciente);
			}
			else{
				//Item 5
				validarAcessoEspecialPOL(paciente,listaExcept,pacAcessoLiberado,parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
			}
		}
		else{
			//Item 5
			validarAcessoEspecialPOL(paciente,listaExcept,pacAcessoLiberado, parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Item 5
	 * @param paciente
	 * @param listaExcept 
	 * @param pacAcessoLiberado
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @param acessoMonitorProjPesqPOL 
	 * @param acessoAdminPOL 
	 * @param acessoEspecialPOL 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarAcessoEspecialPOL(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException{
		Boolean acessoEspecialPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_ESPECIAL_POL);
		if (acessoEspecialPOL && !paciente.isVip()){
			gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
			pacAcessoLiberado.add(paciente);
		} else{
			//item 6
			validarAcessoAdminPOL(paciente,listaExcept,pacAcessoLiberado, parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Item 6
	 * @param paciente
	 * @param pacAcessoLiberado
	 * @param listaExcept
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @param acessoMonitorProjPesqPOL 
	 * @param acessoAdminPOL 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarAcessoAdminPOL(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		Boolean acessoAdminPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_ADMIN_POL);
		if (acessoAdminPOL) {
			if(paciente.isVip()){
				gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_VIP, nomeMicrocomputador);
				incluirExcessao(paciente, listaExcept);
			} else {
				gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_ADMINISTRATIVO, nomeMicrocomputador);
				pacAcessoLiberado.add(paciente);
			}
		}
		else{
			//item 7
			validarAcessoMonitorProjPesqPOL(paciente,listaExcept,pacAcessoLiberado, parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Item 7
	 * @param paciente
	 * @param pacAcessoLiberado
	 * @param listaExcept
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @param acessoMonitorProjPesqPOL 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarAcessoMonitorProjPesqPOL(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Boolean acessoMonitorProjPesqPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_MONITOR_PROJ_PESQ_POL);
		if (acessoMonitorProjPesqPOL) {
			Integer matricula = servidorLogado.getId().getMatricula();
			Short vinCodigo = servidorLogado.getId().getVinCodigo();
			String funcao = "M";
			boolean participaProjeto = getSegurancaPOLRN().verificarSeUsuarioParticipaDeProjetoDePesquisaComPaciente(paciente.getCodigo(), matricula, vinCodigo, funcao);
			
			if(participaProjeto) {
				gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_PROJETO, nomeMicrocomputador);
				pacAcessoLiberado.add(paciente);
			} else {
				gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_PERFIL, nomeMicrocomputador);
				incluirExcessao(paciente, listaExcept);
			}
		}
		else{
			//item 8
			validarAcessoPesquisadorProjPesqPOL(paciente,listaExcept,pacAcessoLiberado, parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	/**
	 * Item 8
	 * @param paciente
	 * @param pacAcessoLiberado
	 * @param listaExcept
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarAcessoPesquisadorProjPesqPOL(AipPacientes paciente, BaseListException listaExcept,List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		Boolean acessoPesquisadorProjPesqPOL = (Boolean) parametros.get(ParametrosPOLEnum.ACESSO_PESQUISADOR_PROJ_POL);
		if(acessoPesquisadorProjPesqPOL) {
			Integer matricula = servidorLogado.getId().getMatricula();
			Short vinCodigo = servidorLogado.getId().getVinCodigo();
			boolean participaProjeto = getSegurancaPOLRN().verificarSeUsuarioParticipaDeProjetoDePesquisaComPaciente(paciente.getCodigo(), matricula, vinCodigo, null);
			
			if(participaProjeto) {
				gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_PROJETO, nomeMicrocomputador);
				pacAcessoLiberado.add(paciente);
			} else {
				gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_PERFIL, nomeMicrocomputador);
				incluirExcessao(paciente, listaExcept);
			}
		}
		else{
			//Item 9
			validarOrigemListaEsperaPortalCirurgias(paciente,listaExcept,pacAcessoLiberado, parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Item 9
	 * @param paciente
	 * @param pacAcessoLiberado
	 * @param listaExcept
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarOrigemListaEsperaPortalCirurgias(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		String paginaOrigem = (String) parametros.get(ParametrosPOLEnum.PAGINA_ORIGEM);
		Boolean interrompeFluxo = Boolean.FALSE;
		
		if(StringUtils.isNotBlank(paginaOrigem) && StringUtils.equals(EnumPaginaOrigem.LISTA_ESPERA_PORTAL_CIRURGIAS.toString(), paginaOrigem)){
			if(isPacienteEmFilaDeEsperaCirurgiaOuCancelados(paciente, DominioSituacaoAgendas.LE)) {
				if(!paciente.isVip()) {
					gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_LISTA_ESPERA, nomeMicrocomputador);
					pacAcessoLiberado.add(paciente);
					interrompeFluxo = Boolean.TRUE;
				}
			}
		}
		
		if (!interrompeFluxo) {//item 10
			validarOrigemListaCanceladosPacienteNaoVip(paciente,listaExcept, pacAcessoLiberado, paginaOrigem, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Item 10
	 * @param paciente
	 * @param paginaOrigem 
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validarOrigemListaCanceladosPacienteNaoVip(AipPacientes paciente,BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, String paginaOrigem, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		if(StringUtils.isNotBlank(paginaOrigem)
				&& StringUtils.equals(EnumPaginaOrigem.LISTA_CANCELADOS_PORTAL_CIRURGIAS.toString(), paginaOrigem) 
				&& isPacienteEmFilaDeEsperaCirurgiaOuCancelados(paciente, DominioSituacaoAgendas.CA)
				&& !paciente.isVip()){
			gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_LISTA_CANCELADOS, nomeMicrocomputador);
			pacAcessoLiberado.add(paciente);
		} else{
			//item 11
			validacoesDeUsuario(paciente,listaExcept, pacAcessoLiberado, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Item 11
	 * @param paciente
	 * @param listaExcept
	 * @param pacAcessoLiberado
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void validacoesDeUsuario(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//11
		if(servidorLogado.getCentroCustoLotacao() != null) {
			validacoesUsuarioComCCT(paciente, listaExcept, pacAcessoLiberado, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);//11.1
		} else {
			validacoesUsuarioSemCCT(paciente, listaExcept, pacAcessoLiberado, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);//11.2
		}
		
		//12
		if (!listaExcept.hasException() && !pacAcessoLiberado.contains(paciente)) {
			gravarLogOutrosBloqueios(paciente, listaExcept, nomeMicrocomputador);			
		}
	}

	/**
	 * subitem 11.2
	 * @param paciente
	 * @param listaExcept
	 * @param pacAcessoLiberado
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void validacoesUsuarioSemCCT(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, String nomeMicrocomputador, boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		//11.2
		if(getSegurancaPOLRN().verificaSeUsuarioTemPerfilDeConsulta(paramConsultaPOL)) {
			// ?
			if (getSegurancaPOLRN().exigePacienteEmAtendimento(paramConsultaPOL)) { 
				//11.2.1 ?
				if(getSegurancaPOLRN().verificaSeUsuarioEhProfessorPacienteEmAtendimento(paciente, Boolean.FALSE)) {
					//11.2.1.1 ?
					if(paciente.isVip()) {
						//11.2.1.1.1 ?										
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_VIP, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					} else { 
						//11.2.1.1.2 ?
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					}
				//11.2.1.2 ?
				} else if(vRetornoAtendimento) {
					//11.2.1.2.1 ?
					if(paciente.isVip()) {
						gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_VIP, nomeMicrocomputador);		
						incluirExcessao(paciente, listaExcept);
					} else { 
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					}
				//11.2.1.3 ? 
				} else { 
					gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_ATENDIMENTO, nomeMicrocomputador);		
					incluirExcessao(paciente, listaExcept);							
				}
			} else{
				//?
				if(getSegurancaPOLRN().verificaSeUsuarioEhProfessorPacienteEmAtendimento(paciente, Boolean.FALSE)) {
					//?
					if(paciente.isVip()) {
						//?										
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_VIP, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					} else { 
						//?
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					}
				//?
				} else if(getSegurancaPOLRN().verificarPacienteEmAtendimento(paciente.getCodigo(), Boolean.TRUE)) {
					//?
					if(paciente.isVip()) {
						gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_VIP, nomeMicrocomputador);		
						incluirExcessao(paciente, listaExcept);
					} else { 
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					}
				//? 
				} else { 
					gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_ATENDIMENTO, nomeMicrocomputador);		
					incluirExcessao(paciente, listaExcept);							
				}
			}
			
		} else { //11.2.2
			gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_PERFIL, nomeMicrocomputador);		
			incluirExcessao(paciente, listaExcept);							
		}
	}

	/**
	 * subitem 11.1
	 * @param paciente
	 * @param listaExcept
	 * @param pacAcessoLiberado
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private void validacoesUsuarioComCCT(AipPacientes paciente, BaseListException listaExcept, List<AipPacientes> pacAcessoLiberado, String nomeMicrocomputador, Boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException {
		//11.1 
		if(getSegurancaPOLRN().validarSeUsuarioPossuiPerfilProcessoLocal(paramConsultaPOL)) {
			//11.1.1
			if (getSegurancaPOLRN().validarSeUsuarioPossuiPerfilProcessoLocalComCentroDeCusto(paramConsultaPOL)) {
				//11.1.1.1
				if (getSegurancaPOLRN().exigePacienteEmAtendimento(paramConsultaPOL)) { 
					//11.1.1.1.1
					verificarSePermiteConsulta(paciente, listaExcept, pacAcessoLiberado, nomeMicrocomputador, Boolean.FALSE, vRetornoAtendimento, paramConsultaPOL);
				} else{
					//11.1.1.1.2
					verificarSePermiteConsulta(paciente, listaExcept, pacAcessoLiberado, nomeMicrocomputador, Boolean.TRUE, vRetornoAtendimento, paramConsultaPOL);
				}
			//11.1.1.2
			} else {
				gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_PERFIL, nomeMicrocomputador);
				incluirExcessao(paciente, listaExcept);
				//gravarLogOutrosBloqueios(paciente, listaExcept, nomeMicrocomputador);
			}
		//11.1.2 (11.2)
		} else {
			//gravarLogOutrosBloqueios(paciente, listaExcept, nomeMicrocomputador);
			validacoesUsuarioSemCCT(paciente, listaExcept, pacAcessoLiberado, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);//11.2
		}
	}

	private void verificarSePermiteConsulta(AipPacientes paciente,
			BaseListException listaExcept,
			List<AipPacientes> pacAcessoLiberado,
			String nomeMicrocomputador, Boolean comRestricao, boolean vRetornoAtendimento, Short paramConsultaPOL) throws ApplicationBusinessException,
			ApplicationBusinessException {
		
		if(verificaPerfilComPermissaoAoCentroDeCusto(paciente, comRestricao, paramConsultaPOL)) {
			//11.1.1.1.1.1
			if(getSegurancaPOLRN().verificaSeUsuarioEhProfessorPacienteEmAtendimento(paciente, Boolean.FALSE)) {
				//11.1.1.1.1.1.1
				if(paciente.isVip()) {
					//11.1.1.1.1.1.1.1
					gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_VIP, nomeMicrocomputador);
					pacAcessoLiberado.add(paciente);
				} else { 
					//11.1.1.1.1.1.1.2
					gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
					pacAcessoLiberado.add(paciente);
				}
			//11.1.1.1.1.1.2
			} else if(vRetornoAtendimento) {
					//11.1.1.1.1.1.2.1
					if(paciente.isVip()) {
						gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_VIP, nomeMicrocomputador);		
						incluirExcessao(paciente, listaExcept);
					} else { 
						gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
						pacAcessoLiberado.add(paciente);
					}
			//11.1.1.1.1.1.3
			} else { 
				gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_ATENDIMENTO, nomeMicrocomputador);
				incluirExcessao(paciente, listaExcept);
			}
		//11.1.1.1.1.2
		} else { 
			gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_PERFIL, nomeMicrocomputador);
			incluirExcessao(paciente, listaExcept);
			/*
			//11.1.1.1.1.2.1
			if(getSegurancaPOLRN().verificaSeUsuarioEhProfessorPacienteEmAtendimento(paciente, Boolean.TRUE)) {
				//11.1.1.1.1.2.1.1
				if(paciente.isVip()) {
					gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_VIP, nomeMicrocomputador);		
					pacAcessoLiberado.add(paciente);
				}   else {
					gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);
					pacAcessoLiberado.add(paciente);
				}
			//11.1.1.1.1.2.2
			} else if(getSegurancaPOLRN().verificarPacienteEmAtendimento(paciente.getCodigo(), Boolean.FALSE)) {
				//11.1.1.1.1.2.2.1	
				if (paciente.isVip()) {
					gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_VIP, nomeMicrocomputador);		
					incluirExcessao(paciente, listaExcept);	
				} else {
					gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO, nomeMicrocomputador);		
					pacAcessoLiberado.add(paciente);
				}
			} else {
				gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_SEM_ATENDIMENTO, nomeMicrocomputador);		
				incluirExcessao(paciente, listaExcept);	
			}*/
		}
	}

	private void incluirExcessao(AipPacientes paciente, BaseListException listaExcept) {
		String prontuario = CoreUtil.formataProntuario(paciente.getProntuario());		
		BaseException e = new ApplicationBusinessException(SegurancaPOLONExceptionCode.ERRO_AIP_00254, prontuario);
		listaExcept.add(e);
	}

	private void gravarLogOutrosBloqueios(AipPacientes paciente, BaseListException listaExcept, String nomeMicrocomputador) throws ApplicationBusinessException {
		gravarLog(paciente, DominioOcorrenciaPOL.OUTROS_BLOQUEIOS, nomeMicrocomputador);	
		incluirExcessao(paciente, listaExcept);
	}
	
	private void gravarLog(AipPacientes paciente, DominioOcorrenciaPOL ocorrencia, String nomeMicrocomputador) throws ApplicationBusinessException {
		beginTransaction();
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AipLogProntOnlines log = new AipLogProntOnlines();
		log.setPaciente(paciente);
				
		log.setServidor(servidorLogado);
		log.setCriadoEm(new Date());
		
		if(nomeMicrocomputador == null){
			throw new ApplicationBusinessException(SegurancaPOLONExceptionCode.NAO_FOI_POSSIVEL_RECUPERAR_COMPUTADOR);
		}		
		log.setMachine(nomeMicrocomputador);

		if (ocorrencia != null) {
			log.setOcorrencia(ocorrencia);			
		} else {
			log.setOcorrencia(DominioOcorrenciaPOL.LIBERADO);
		}
		getAipLogProntOnlinesDAO().persistir(log);
		commitTransaction();
			
	}
	
	protected SegurancaPOLRN getSegurancaPOLRN() {
		return segurancaPOLRN;
	}

	/**
	 * item 3
	 * @param listaExcept 
	 * @param pacAcessoLiberado 
	 * @param paginaOrigem 
	 * @param acessoPesquisadorProjPesqPOL 
	 * @param acessoMonitorProjPesqPOL 
	 * @param acessoAdminPOL 
	 * @param acessoEspecialPOL 
	 * @param acessoComissaoPOL 
	 * @throws ApplicationBusinessException 
	 *  
	 * @throws BaseListException 
	 */
	private void verificarPacienteEspecial(AipPacientes paciente,
			BaseListException listaExcept,
			List<AipPacientes> pacAcessoLiberado,
			Map<ParametrosPOLEnum, Object> parametros, String nomeMicrocomputador, Short paramConsultaPOL)
			throws ApplicationBusinessException {
		//verifica se o paciente esta em atendimento
		Boolean vRetornoAtendimento = getSegurancaPOLRN().verificarPacienteEmAtendimento(paciente.getCodigo(), Boolean.FALSE);
				
		String str = getSegurancaPOLRN().verificarPacienteEspecial(paciente.getCodigo(), vRetornoAtendimento);
		Boolean retorno = Boolean.TRUE;
		
		if (str.equals(VerPacEspecialTargetCode.S.toString())) {
			retorno = Boolean.FALSE;
			gravarLog(paciente, DominioOcorrenciaPOL.LIBERADO_VIP, nomeMicrocomputador);
			pacAcessoLiberado.add(paciente);
		} else if (str.equals(VerPacEspecialTargetCode.E.toString())) {
			incluirExcessao(paciente, listaExcept);
			retorno = Boolean.FALSE;
			gravarLog(paciente, DominioOcorrenciaPOL.BLOQUEADO_VIP, nomeMicrocomputador);
		}
		
		if(retorno){
			//item 4
			validarAcessoComissaoPOL(paciente,listaExcept,pacAcessoLiberado,parametros, nomeMicrocomputador, vRetornoAtendimento, paramConsultaPOL);
		}
	}
	
	/**
	 * Verifica se paciente está em lista de espera de cirurgia ou na lista de cancelados
	 * @param paciente
	 * @param situacao
	 * @return
	 */
	private boolean isPacienteEmFilaDeEsperaCirurgiaOuCancelados(AipPacientes paciente, DominioSituacaoAgendas situacao) {
		Boolean valida = Boolean.FALSE;
		List<MbcAgendas> cirurgias = getBlocoCirurgicoFacade().pesquisarCirurgiasListaDeEspera(paciente.getCodigo(), situacao);
		if(cirurgias != null && !cirurgias.isEmpty()) {
			valida =  Boolean.TRUE;
		}
		return valida;
	}

	/**
	 * Verifica se usuário possui perfil com permissão de consulta associado ao seu centro de custo
	 * @param paciente
	 * @param comRestricao
	 * @param paramConsultaPOL
	 * @return
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	private Boolean verificaPerfilComPermissaoAoCentroDeCusto(AipPacientes paciente, Boolean comRestricao, Short paramConsultaPOL) throws ApplicationBusinessException {

		CsePerfilProcessoLocal perfilProcessoLocal = getSegurancaPOLRN().getPerfilUsuarioPossuiPerfilProcessoLocalComCentroDeCusto(paramConsultaPOL);
		Integer codigoPerfilProcessoLocal = perfilProcessoLocal.getFccCentroCustosByCctCodigoUnidAtend().getCodigo();
		Boolean permiteConsultaRestricao = Boolean.FALSE;
		
		Integer vNroDiasAacPas = null;
		Integer vNroDiasAacFut = null;
		Integer vNroDiasIntPas = null;
		Integer vNroDiasMbcPas = null;
		Integer vNroDiasMbcFut = null;
		
		if (comRestricao) {
			vNroDiasAacPas = getSegurancaPOLRN().obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_AAC_PAS);
			vNroDiasAacFut = getSegurancaPOLRN().obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_AAC_FUT);
			vNroDiasIntPas = getSegurancaPOLRN().obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_INT_PAS);
			vNroDiasMbcPas = getSegurancaPOLRN().obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_MBC_PAS);
			vNroDiasMbcFut = getSegurancaPOLRN().obterValorNumericoParametro(AghuParametrosEnum.P_NRO_DIAS_MBC_FUT);
		}
		
//		Short paramReteronoConsAgendada = parametroFacade.buscarValorShort(AghuParametrosEnum.P_RETORNO_CONS_AGENDADA);
		
		//Q_CONSULTA_RESTRICAO e Q_CONSULTA
		List<AghUnidadesFuncionais>  listaQConsulta =  getAghuFacade().pesquisarConsultasPaciente(paciente.getCodigo(), 
				vNroDiasAacFut, vNroDiasAacPas, DominioSituacaoAtendimento.PACIENTE_AGENDADO.getCodigo());
		for(AghUnidadesFuncionais unidadeFuncional : listaQConsulta) {
			if(unidadeFuncional.getCentroCusto().getCodigo().equals(codigoPerfilProcessoLocal)){
				permiteConsultaRestricao = Boolean.TRUE;
			}
		}
		//Q_ALTA_RESTRICAO e Q_ALTA
		if(!permiteConsultaRestricao) {
			List<AghUnidadesFuncionais> listaQAlta = getAghuFacade().pesquisarUnidadesFuncionaisPaciente(DominioPacAtendimento.N, vNroDiasIntPas, paciente.getCodigo(), Boolean.TRUE);
			for(AghUnidadesFuncionais aghUnidadesFuncionais : listaQAlta) {
				if(aghUnidadesFuncionais.getCentroCusto().getCodigo().equals(codigoPerfilProcessoLocal)) {
					permiteConsultaRestricao = Boolean.TRUE;
				}
			}
		}
		//Q_INTERNADO
		if(!permiteConsultaRestricao) {
			List<AghUnidadesFuncionais> listaQInternado = getAghuFacade().pesquisarUnidadesFuncionaisPaciente(DominioPacAtendimento.S, null, paciente.getCodigo(), Boolean.TRUE);
			for(AghUnidadesFuncionais aghUnidadesFuncionais : listaQInternado) {
				if(aghUnidadesFuncionais.getCentroCusto().getCodigo().equals(codigoPerfilProcessoLocal)) {
					permiteConsultaRestricao = Boolean.TRUE;
				}
			}
		}
		//Q_CIRURGIA_AMB_RESTRICAO e Q_CIRURGIA_AMB
		if(!permiteConsultaRestricao) {
			List<AghUnidadesFuncionais> listaQCirurgiaAmb = getAghuFacade().pesquisarCirurgiasUnidadesFuncionais(paciente.getCodigo(), vNroDiasMbcFut, vNroDiasMbcPas); 
			for(AghUnidadesFuncionais aghUnidadesFuncionais : listaQCirurgiaAmb) {
				if(aghUnidadesFuncionais.getCentroCusto().getCodigo().equals(codigoPerfilProcessoLocal)) {
					permiteConsultaRestricao = Boolean.TRUE;
				}
			}
		}
		return permiteConsultaRestricao;
	}
	
	protected IAmbulatorioFacade getAmbulatorioFacade() {
		return ambulatorioFacade;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
