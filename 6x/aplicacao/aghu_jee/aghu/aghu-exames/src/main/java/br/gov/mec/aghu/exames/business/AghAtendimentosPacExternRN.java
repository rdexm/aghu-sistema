package br.gov.mec.aghu.exames.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.dao.AghAtendimentosPacExternDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.ClienteNfeVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.dao.ConvDAO;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghAtendimentosPacExtern;
import br.gov.mec.aghu.model.AghMedicoExterno;
import br.gov.mec.aghu.model.AghPaisBcb;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.Conv;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.senior.ISeniorService;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AghAtendimentosPacExternRN extends BaseBusiness {

	@EJB
	private ModuloExamesRN moduloExamesRN;
	
	@EJB
	private AghAtendimentosPacExternEnforceRN aghAtendimentosPacExternEnforceRN;
	
	private static final Log LOG = LogFactory.getLog(AghAtendimentosPacExternRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IExamesFacade examesFacade;
	
	@Inject
	private AghAtendimentosPacExternDAO aghAtendimentosPacExternDAO;

	@Inject
	private ConvDAO convDAO;	
	
	@Inject
	private ISeniorService seniorService;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3378800693540342754L;

	public enum AghAtendimentosPacExternRNExceptionCode implements BusinessExceptionCode {
		AGH_00331 // Convênio/plano não encontrado;
		, AEL_00332 // Convênio está inativo;
		, AGH_00337 // Atendimento não encontrado. Atendimento paciente externo: #1;
		, AEL_00444 // Servidor pertence a um conselho profissional que não permite solicitar este exame;
		, AIP_00013 // Paciente com este código não encontrado;
		, AGH_00432 // Paciente especificado para o Atendimento Externo já se encontra internado.
		, AGH_00329 // Não é permitido gerar atendimento, Paciente com óbito informado.
		, AGH_00338 // Não pode excluir atendimento, conta  #1 já possui itens lançados.
		, AEL_03066 // A data da coleta deve ser menor ou igual a data atual.
		, MSG_CBO_DEVE_SER_INFORMADO // Para atendimentos externos com convênio substr(conv.versao_tiss,1,1) >= '3' deve ser infomado o CBO do médico externo  
		;
	}
	
	
	
	public AghAtendimentosPacExtern inserir(AghAtendimentosPacExtern entity, String nomeMicrocomputador, RapServidores servidor) throws BaseException {
		this.preInserir(entity,servidor);
		
	
		this.getAghAtendimentosPacExternDAO().persistir(entity);
		this.getAghAtendimentosPacExternDAO().flush();
		
		this.posInsercao(entity, nomeMicrocomputador);
		
		return entity;
	}
	
	/**
	 * BEFORE INSERT ON AGH_ATENDIMENTOS_PAC_EXTERN FOR EACH ROW.<br>
	 * 
	 * ORADB TRIGGER AGHT_APE_BRI.<br>
	 *  
 	 * @param entity
	 * @throws BaseException 
	 */
	private void preInserir(AghAtendimentosPacExtern entity, RapServidores servidor) throws BaseException {
		entity.setCriadoEm(new Date());
		entity.setServidorDigitado(servidor);
		
		//Paciente não pode ter data de óbito
		this.verificarObitoPaciente(entity);
		
		//Convênio deve estar ativo
		this.verificarConvenioAtivo(entity.getConvenioSaudePlano());
		
		//O servidor responsável, quando houver, deve pertencer
		//a um conselho profissional  que permita solicitar exame.
		this.verificarConselhoProfissional(entity.getServidor());
		
		//Paciente não pode estar internado para abrir um atendimento
		this.verificarPacienteInternadoParaConvenio(entity.getPaciente(), entity.getConvenioSaudePlano());
		
		this.enviarClienteConvenio(entity.getConvenioSaudePlano());
		
		validarObrigatoriedadeCBO(entity);
	}
	
	public void validarObrigatoriedadeCBO(AghAtendimentosPacExtern atendimentoPacExterno) throws ApplicationBusinessException{
		Short convCodigo = atendimentoPacExterno.getConvenioSaudePlano().getId().getCnvCodigo();
		
		Conv convenio = convDAO.obterPorChavePrimaria(convCodigo);
		if (convenio != null){
			String versaoTiss = convenio.getVersaoTiss();
			Integer numInicialVersaoTiss = null;
			if (versaoTiss != null){
				numInicialVersaoTiss = Integer.valueOf(versaoTiss.substring(0, 1));
			}
			
			if (numInicialVersaoTiss != null && numInicialVersaoTiss >= 3){
				AghMedicoExterno medicoExterno = atendimentoPacExterno.getMedicoExterno();
				
				if (medicoExterno == null || medicoExterno.getCbo() == null){
					throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.MSG_CBO_DEVE_SER_INFORMADO);
				}
			}
		}
		
	}
	
	public void enviarClienteConvenio(FatConvenioSaudePlano convenioSaudePlano) throws ApplicationBusinessException {
		if (convenioSaudePlano != null){
			FatConvenioSaude fatConvenioSaude = faturamentoFacade.obterFatConvenioSaudePorId(convenioSaudePlano.getConvenioSaude().getCodigo());
			String conveniosIpe="";
			conveniosIpe = this.parametroFacade.buscarValorTexto(AghuParametrosEnum.P_CODIGOS_CONVENIO_IPE);
			Integer ctaRed = this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_NFSE_CTA_CONT_REDUZIDA);
			AghPaisBcb paisBrasil = this.examesFacade.obterAghPaisBcb(this.parametroFacade.buscarValorInteiro(AghuParametrosEnum.P_SEQ_PAIS_BRASIL));
			
			Conv convenio = faturamentoFacade.obterConvenioPorCodigo(fatConvenioSaude.getCodigo());
			
			if (!DominioGrupoConvenio.P.equals(fatConvenioSaude.getGrupoConvenio()) && 
				!conveniosIpe.contains(fatConvenioSaude.getCodigo().toString()) &&
				convenio != null && convenio.getCodClienteNfe() == null){			
				
				
				ClienteNfeVO clienteNfeVo = new ClienteNfeVO();		
				
				clienteNfeVo.setApeCli(fatConvenioSaude.getDescricao());
				clienteNfeVo.setNomCli(fatConvenioSaude.getDescricao());
				
				if (convenio.getCgc() != null){
				    clienteNfeVo.setDocumento(convenio.getCgc().toString());
				}
				
				clienteNfeVo.setIntNet(convenio.getEmail());
				clienteNfeVo.setEmaNfe(convenio.getEmail());
				clienteNfeVo.setTipCli("J");
				clienteNfeVo.setTipMer("I");
				clienteNfeVo.setCtaRed(ctaRed);
				
				if (paisBrasil != null){
				    clienteNfeVo.setCodPai(paisBrasil.getCodigoBcb().toString());
				}				
				
				if(fatConvenioSaude.getCep()!= null){
				   clienteNfeVo.setCepCli(fatConvenioSaude.getCep().toString());
				}
				
				clienteNfeVo.setEndCli(fatConvenioSaude.getLogradouro());
				
				if(fatConvenioSaude.getNumeroLogradouro() != null){
				   clienteNfeVo.setNenCli(fatConvenioSaude.getNumeroLogradouro().toString());
				}
				
				clienteNfeVo.setBaiCli(fatConvenioSaude.getBairro());
				clienteNfeVo.setCidCli(fatConvenioSaude.getCidade());
				
				if(fatConvenioSaude.getUf() != null){
				   clienteNfeVo.setSigUfs(fatConvenioSaude.getUf().getSigla());
				}
				
				clienteNfeVo.setCplEnd(fatConvenioSaude.getComplementoLogradouro());	
				
				convenio.setCodClienteNfe(seniorService.gravarClienteNota(clienteNfeVo).intValue());
				
				faturamentoFacade.atualizarConvenio(convenio);
			}			
					
		}
		
		
	}
		
	/**
	 * AFTER INSERT ON AGH_ATENDIMENTOS_PAC_EXTERN FOR EACH ROW.<br>
	 * 
	 * ORADB TRIGGER AGHT_APE_ARI.<br>
	 * ORADB TRIGGER AGHT_APE_ASI.<br>
	 *  
	 * @param valorRetorno
	 * @throws BaseException 
	 */
	private void posInsercao(AghAtendimentosPacExtern newEntity, String nomeMicrocomputador) throws BaseException {
		this.getAghAtendimentosPacExternEnforceRN().insert(newEntity, nomeMicrocomputador);
	}
	

	public AghAtendimentosPacExtern alterar(AghAtendimentosPacExtern entity, String nomeMicrocomputador, RapServidores servidor) throws BaseException {
		//TODO MIGRAÇÃO: Verificar o porque de estar retornando null
		AghAtendimentosPacExtern oldEntity = this.getAghAtendimentosPacExternDAO().obterPorChavePrimaria(entity.getSeq());
		
		this.preAlterar(oldEntity, entity);
		
		AghAtendimentosPacExtern newEntity = getAghAtendimentosPacExternDAO().merge(entity);
		
		this.posAlteracao(oldEntity, newEntity, nomeMicrocomputador);
		
		return newEntity;
	}
	
	/**
	 * BEFORE UPDATE ON AGH_ATENDIMENTOS_PAC_EXTERN FOR EACH ROW.<br>
	 * 
	 * ORADB TRIGGER AGHT_APE_BRU.<br>
	 * 
	 * @param oldEntity
	 * @param entity
	 * @throws ApplicationBusinessException 
	 */
	protected void preAlterar(AghAtendimentosPacExtern oldEntity, AghAtendimentosPacExtern entity) throws ApplicationBusinessException {
		if (CoreUtil.modificados(oldEntity, entity)) {
			this.verificarObitoPaciente(entity);
		}
		validarObrigatoriedadeCBO(entity);
		this.enviarClienteConvenio(entity.getConvenioSaudePlano());
	}

	/**
	 * AFTER UPDATE ON AGH_ATENDIMENTOS_PAC_EXTERN FOR EACH ROW.<br>
	 * 
	 * ORADB TRIGGER AGHT_APE_ARU.<br>
	 * ORADB TRIGGER AGHT_APE_ASU.<br>
	 * 
	 * @throws BaseException 
	 *  
	 */
	private void posAlteracao(AghAtendimentosPacExtern oldEntity, AghAtendimentosPacExtern newEntity, String nomeMicrocomputador) throws BaseException {
		this.getAghAtendimentosPacExternEnforceRN().update(oldEntity, newEntity, nomeMicrocomputador);
	}
	
	/**
	 * Verifica se o convênio existe e está ativo.<br>
	 * 
	 * ORADB Package aghk_ape_rn.rn_apep_ver_convenio.<br>
	 * 
	 */
	public void verificarConvenioAtivo(FatConvenioSaudePlano convSaudePlano) throws BaseException {
		FatConvenioSaudePlano umFatConvenioSaudePlano =
		this.getFaturamentoFacade().obterFatConvenioSaudePlanoPorChavePrimaria(convSaudePlano.getId());
		
		if (umFatConvenioSaudePlano == null) {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AGH_00331);			
		}
		
		if (DominioSituacao.A != umFatConvenioSaudePlano.getIndSituacao()) {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AEL_00332);
		}
	}
	
	/**
	 * Ao alterar o convenio/plano no atendimento externo, alterar
	 * também nas solicitações de exames.<br>
	 * 
	 * ORABD Package aghk_ape_rn.rn_apep_atu_convenio.<br>
	 * @throws BaseException 
	 * 
	 */
	public void atualizarConvenio(FatConvenioSaudePlano convenio, AghAtendimentosPacExtern atendimentoPacExterno, String nomeMicrocomputador) throws BaseException {
		List<AghAtendimentos> atendimentos = this.getAtendimentos(atendimentoPacExterno);
		AghAtendimentos atendimento = atendimentos.get(0);
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		//ALTERAR CONVENIO NAS SOLICITACOES DE EXAMES.
		if (atendimento.getAelSolicitacaoExames() != null && !atendimento.getAelSolicitacaoExames().isEmpty()) {
			for (AelSolicitacaoExames solicitacaoExame : atendimento.getAelSolicitacaoExames()) {
				solicitacaoExame.setConvenioSaudePlano(convenio);
				this.getSolicitacaoExameFacade().atualizar(solicitacaoExame, null, nomeMicrocomputador, servidorLogado);
			}
		} else {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AGH_00337, atendimentoPacExterno != null ? atendimentoPacExterno.getSeq() : "0");
		}
	}
	
	private IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	/**
	 * Verifica se o servidor pertence:<br>
	 * - a um conselho profissional com numero de registro;<br>
	 * - e tenha permissao de solicitar exame.<br>
	 * 
	 * ORADB Package aghk_ape_rn.rn_apep_ver_med_resp.<br>
	 * 
	 * @throws ApplicationBusinessException 
	 * 
	 */
	public void verificarConselhoProfissional(RapServidores servidor) throws ApplicationBusinessException {
		if (servidor != null && servidor.getId() != null) {
			List<RapServidores> servidoresPermitidos =
			this.getModuloExamesRN().buscarServidoresSolicitacaoExame(servidor.getId().getVinCodigo(), servidor.getId().getMatricula());
			
			if (servidoresPermitidos == null || servidoresPermitidos.isEmpty()) {
				throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AEL_00444);
			}
		}
	}
	
	/**
	 * Ao alterar um atendimento externo, altera atendimento.<br>
	 * Atualizar todos os atendimentos associados ao Atendimento paciente externo.<br>
	 * 
	 * ORADB Package aghk_ape_rn.rn_apep_atu_upd_aten.<br>
	 * 
	 * @param atdPacExterno
	 * @throws BaseException
	 */
	public void atualizarAtendimento(AghAtendimentosPacExtern atdPacExterno, String nomeMicrocomputador) throws BaseException {
		if (atdPacExterno == null || atdPacExterno.getSeq() == null) {
			throw new IllegalArgumentException("Parametro AghAtendimentosPacExtern nao informado corretamente!!!");
		}
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		this.verificarExistenciaPaciente(atdPacExterno);
		
		List<AghAtendimentos> atendimentos = this.getAtendimentos(atdPacExterno);
		
		//AghAtendimentoDAO atendimentoDAO = getAghAtendimentoDAO();
		AghAtendimentos atendimentoOld;
		final Date dataFimVinculoServidor = new Date();
		for (AghAtendimentos atd : atendimentos) {
			atd.setPaciente(atdPacExterno.getPaciente());
			atd.setProntuario(atdPacExterno.getPaciente().getProntuario());
			atd.setUnidadeFuncional(atdPacExterno.getUnidadeFuncional());
			atd.setServidor(atdPacExterno.getServidor());
			
			atendimentoOld = getAghuFacade().obterAtendimentoOriginal(atd.getSeq());
			this.getPacienteFacade().atualizarAtendimento(atd, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}//FOR atendimentos
	}
	
	/**
	 * Verifica se o AghAtendimentoPacExtern tem um AipPacientes associado.<br>
	 * 
	 * @param atdPacExterno
	 * @throws ApplicationBusinessException
	 */
	protected void verificarExistenciaPaciente(AghAtendimentosPacExtern atdPacExterno) throws ApplicationBusinessException {
		if (atdPacExterno.getPaciente() == null) {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AIP_00013);
		}
	}
	
	/**
	 * Busca os AghAtendimentos associados ao AghAtendimentosPacExtern.<br>
	 * Caso nao encontre AghAtendimentos associado retorna exception com codigo AGH_00337.<br>
	 * 
	 * @param atendimentoPacExterno
	 * @return
	 * @throws ApplicationBusinessException
	 */
	protected List<AghAtendimentos> getAtendimentos(AghAtendimentosPacExtern atendimentoPacExterno) throws ApplicationBusinessException {
		List<AghAtendimentos> atendimentos =
		this.getAghuFacade().listarAtendimentosPorAtendimentoPacienteExterno(atendimentoPacExterno);
		
		if (atendimentos == null || atendimentos.isEmpty()) {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AGH_00337, atendimentoPacExterno.getSeq());
		}
		
		return atendimentos;
	}
	
	/**
	 * Paciente não pode ter data de obito.<br>
	 * 
	 * ORADB Package aghk_ape_rn.rn_apep_ver_paciente.<br>
	 * 
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarObitoPaciente(AghAtendimentosPacExtern atdPacExterno) throws ApplicationBusinessException {
		if (atdPacExterno == null) {
			throw new IllegalArgumentException("Parametro AghAtendimentosPacExtern nao informado corretamente!!!");
		}
		
		this.verificarExistenciaPaciente(atdPacExterno);
		
		if (atdPacExterno.getPaciente().getDtObito() != null) {
			throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AGH_00329);
		}
	}
	
	/**
	 * Verifica se o paciente está internado no convenio. 
	 * Se estiver não permite criar atendimento de paciente externo.<br>
	 *  
	 * ORADB Package AGHK_APE_RN.RN_APEP_VER_PAC_INT.<br>
	 * 
	 * @param paciente
	 * @param convenioSaudePlano
	 * @throws ApplicationBusinessException 
	 */
	protected void verificarPacienteInternadoParaConvenio(AipPacientes paciente, FatConvenioSaudePlano convenioSaudePlano) throws ApplicationBusinessException {
		List<AghAtendimentos> atendimentos = this.getAghuFacade().listarAtendimentosComInternacaoEmAndamentoPorPaciente(paciente.getCodigo());
		
		if (atendimentos != null && !atendimentos.isEmpty()) {
			AghAtendimentos atd = atendimentos.get(0);
			if (atd.getInternacao() != null && CoreUtil.igual(convenioSaudePlano, atd.getInternacao().getConvenioSaudePlano())) {
				throw new ApplicationBusinessException(AghAtendimentosPacExternRNExceptionCode.AGH_00432);
			}//IF internacao
		}//IF lista atendimentos
	}
	
	protected AghAtendimentosPacExternDAO getAghAtendimentosPacExternDAO() {
		return aghAtendimentosPacExternDAO;
	}
	

	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	
	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}
	
	protected ModuloExamesRN getModuloExamesRN() {
		return moduloExamesRN;
	}
	
	protected AghAtendimentosPacExternEnforceRN getAghAtendimentosPacExternEnforceRN() {
		return aghAtendimentosPacExternEnforceRN;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
