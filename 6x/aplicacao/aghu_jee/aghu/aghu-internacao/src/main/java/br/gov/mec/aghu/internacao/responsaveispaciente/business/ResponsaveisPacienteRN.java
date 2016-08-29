package br.gov.mec.aghu.internacao.responsaveispaciente.business;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.dao.AinResponsaveisPacienteDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.AinResponsaveisPaciente;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.TipoOperacaoEnum;


/**
 *
 * Classe de apoio para as Business Facades. Ela em geral agrupa as 	
 * funcionalidades encontradas em packages e procedures do AGHU.
 * 
 * Ela poderia ser uma classe com acesso friendly ou default e não ser um
 * componente seam.
 * 
 * Mas fazendo assim facilita, pois ela também pode receber uma referência para
 * o EntityManager.
 * 
 * Outra forma de fazer é instanciar ela diretamente do ON e passar o entity
 * manager para seus parâmetros. Neste caso os metodos desta classe poderiam ser
 * até estaticos e nao necessitar de instanciação. Ai ela seria apenas um
 * particionamento lógico de código e não um componente que possa ser injetado
 * em qualquer outro contexto.
 *	  	
 * ATENÇÃO, Os metodos desta classe nunca devem ser acessados diretamente por
 * qualquer classe que não a ON correspondente. Por isso sugere-se que todos os
 * métodos desta sejam friendly (default) ou private.
 * 
 */
@SuppressWarnings("deprecation")
@Stateless
public class ResponsaveisPacienteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(ResponsaveisPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@Inject
	private AinResponsaveisPacienteDAO ainResponsaveisPacienteDAO;
	
	@EJB
	private IParametroFacade parametroFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 7073841589015327950L;
	/**
	 * Constante que guarda o nome do atributo do contexto referente ao sequence
	 * DAO
	 */
	

	private enum ResponsaveisPacienteRNExceptionCode implements BusinessExceptionCode {
		AIN_00451, FAT_00107, AIP_USUARIO_NAO_CADASTRADO;
	}
	
	/**
	 * Método responsável por executar os procedimentos necessários para a inclusão de um responsável de paciente
	 * 
	 * @param responsavelPaciente
	 *  
	 */
	public void incluirResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
		TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.INSERT;
		this.processarPersistenciaPaciente(responsavelPaciente, tipoOperacao);
		this.processarTriggerIntegridadeContaInclusao(responsavelPaciente);
	}
	
	
	/**
	 * ORADB PROCEDURE process_rep_rows
	 * Método responsável por executar a enforce para a persistência de um Responsável de Paciente
	 * 
	 * @param responsavelPaciente, tipoOperacao
	 *  
	 * 
	 */
	public void processarPersistenciaPaciente(AinResponsaveisPaciente responsavelPaciente, TipoOperacaoEnum tipoOperacao) throws ApplicationBusinessException{
		
		this.processarEnforceResponsavelPaciente(responsavelPaciente, tipoOperacao);
	}
	
	
	/**
	 * ORADB ENFORCE ainP_enforce_rep_rules
	 * Método responsável por executar a enforce para a persistência de um Responsável de Paciente
	 * 
	 * @param responsavelPaciente, tipoOperacao
	 *  
	 * 
	 */
	public void processarEnforceResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente, TipoOperacaoEnum tipoOperacao) throws ApplicationBusinessException{
		AghParametros aghParametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_COD_MVTO_INT_INTERNACAO);
		BigDecimal idadeLimitePacienteMenor = aghParametro.getVlrNumerico();
		if (tipoOperacao.equals(TipoOperacaoEnum.DELETE)){
			AipPacientes paciente = responsavelPaciente.getInternacao().getPaciente();
			if (paciente != null){
				Integer idadePaciente = paciente.getIdade();
				if (idadePaciente.intValue() <= idadeLimitePacienteMenor.intValue()){
					Integer qtdResponsaveis = pesquisarQtdResponsaveisInternacao(responsavelPaciente.getInternacao().getSeq());
					if (qtdResponsaveis == 0){
						 throw new ApplicationBusinessException(ResponsaveisPacienteRNExceptionCode.AIN_00451);
					}
				}
			}
		}
	}
	
	/**
	 * ORADB Trigger AINT_REP_BASE_BRI
	 * 
	 * Método responsável por solicitar a verificação de integridade da conta do convênio do 
	 * responsável do paciente na inclusão
	 *  
	 */
	public void processarTriggerIntegridadeContaInclusao(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
		if (responsavelPaciente.getCntaConv() != null){
			this.verificarIntegridadeConta(responsavelPaciente.getCntaConv().getNro());			
		}
	}
	
	
	/**
	 * ORADB Procedure ainp_ver_conta_conv
	 * Método que verifica a integridade da conta de um convênio
	 *  
	 * 
	 */
	public void verificarIntegridadeConta(Integer nroConta) throws ApplicationBusinessException{
		if (!temContaConv(nroConta)){
			if (!temBackupContaConv(nroConta)){
				throw new ApplicationBusinessException(ResponsaveisPacienteRNExceptionCode.FAT_00107);
			}
		}
	}
	
	/**
	 * Método que verifica se uma conta existe
	 * 
	 * @param nroConta
	 * @return boolean
	 */
	private boolean temContaConv(Integer nroConta){
		return this.getFaturamentoFacade().temContaConv(nroConta);
	}
	
	private boolean temBackupContaConv(Integer nroConta){
		return this.getFaturamentoFacade().temBackupContaConv(nroConta);
	}
	
	/**
	 * ORADB Trigger AINT_REP_BASE_BRU
	 * 
	 * Método responsável por solicitar a verificação de integridade da conta do convênio 
	 * do responsável do paciente na atualização
	 *  
	 */
	public void processarTriggerIntegridadeContaAtualizacao(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
		if (responsavelPaciente.getCntaConv() != null){
			Integer nroContaAnterior = obterContaConvenioAnterior(responsavelPaciente.getSeq());
			if (nroContaAnterior == null || nroContaAnterior.intValue() != responsavelPaciente.getCntaConv().getNro()){
				this.verificarIntegridadeConta(responsavelPaciente.getCntaConv().getNro());							
			}
		}
	}
	
	

	/**
	 * Método que conta o número de responsáveis de uma internação 
	 * 
	 * @param intSeq
	 * @return Integer
	 */
	private Integer pesquisarQtdResponsaveisInternacao(Integer intSeq){
		return this.getAinResponsaveisPacienteDAO().pesquisarQtdResponsaveisInternacao(intSeq);
	}
	
	
	/**
	 * Método responsável por executar os procedimentos necessários para a atualização de um responsável de paciente
	 * 
	 * @param responsavelPaciente
	 *  
	 */
	public void atualizarResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
		TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.UPDATE;
		this.processarPersistenciaPaciente(responsavelPaciente, tipoOperacao);
		this.processarTriggerIntegridadeContaAtualizacao(responsavelPaciente);
	}
	
	/**
	 * Método responsável por executar os procedimentos necessários para a remoção de um responsável de paciente
	 * 
	 * @param responsavelPaciente
	 *  
	 */
	public void removerResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
		TipoOperacaoEnum tipoOperacao = TipoOperacaoEnum.DELETE;
		this.processarPersistenciaPaciente(responsavelPaciente, tipoOperacao);
	}
	
	
	/**
	 * Método que obtem a conta anterior do responsável
	 * 
	 * @param codigoPaciente
	 * @return
	 */
	private Integer obterContaConvenioAnterior(Integer seqResp) {
		return this.getAinResponsaveisPacienteDAO().obterContaConvenioAnterior(seqResp);
	}
	
	/**
     * ORADB Trigger AINT_REP_BRI
     * Validação do servidor e atribuição do valor da sequence
     * 
     * @param responsavelPaciente
     *  
     */
     public void validarDadosResponsavelPaciente(AinResponsaveisPaciente responsavelPaciente) throws ApplicationBusinessException{
 		 
    	 responsavelPaciente.setCriadoEm(new Date());
	     if(responsavelPaciente.getServidor() == null){
	    	 RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			 if (servidorLogado == null) {
		    	 throw new ApplicationBusinessException(ResponsaveisPacienteRNExceptionCode.AIP_USUARIO_NAO_CADASTRADO);
		     }
			 responsavelPaciente.setServidor(servidorLogado);
	     }
     }

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected AinResponsaveisPacienteDAO getAinResponsaveisPacienteDAO() {
		return ainResponsaveisPacienteDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return this.faturamentoFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
