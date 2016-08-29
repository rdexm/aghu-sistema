package br.gov.mec.aghu.internacao.administracao.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.dao.AinInternacaoDAO;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;


@Stateless
public class TrocarPacienteInternacaoON  extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(TrocarPacienteInternacaoON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@Inject
	private AinInternacaoDAO ainInternacaoDAO;

	@EJB
	private ICascaFacade cascaFacade;

	@EJB
	private IInternacaoFacade internacaoFacade;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2141667191392775510L;

	public enum TrocarPacienteInternacaoONExceptionCode implements	BusinessExceptionCode { 
		AIN_00833, ERRO_GENERICO_INTEGRACAO_MODULO
	}

	public void verificaPermissaoTrocarPacienteIntenacao(String usuario) throws ApplicationBusinessException {
		if (!getICascaFacade().usuarioTemPermissao(usuario, "internacao", "trocarPaciente")) {
			throw new ApplicationBusinessException(TrocarPacienteInternacaoONExceptionCode.AIN_00833);
		}
	}
	
	/**
	 * Método responsável pela atualizacao de uma internação.
	 * 
	 * @param internacao
	 * @throws ApplicationBusinessException
	 */
	
	public AinInternacao atualizarInternacao(Integer intSeq, Integer prontuario, String nomeMicrocomputador) throws BaseException {
		AinInternacao internacao = getAinInternacaoDAO().obterPorChavePrimaria(intSeq);
		AinInternacao internacaoOLD = getAinInternacaoDAO().obterOriginal(intSeq);
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		AipPacientes pac = pacienteFacade.pesquisarPacientePorProntuario(prontuario);
		internacao.setPaciente(pac);

		internacao = this.getAinInternacaoDAO().atualizar(internacao);
		
		//CHAMADA DE TRIGGERS DE ATUALIZAÇÃO
		this.getInternacaoFacade().atualizarInternacao(internacao,internacaoOLD, nomeMicrocomputador, servidorLogado, new Date(), false);

		this.getAinInternacaoDAO().flush();
		
		return internacao;
	}
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public String getStringKeyErroGenericoIntegracaoModulo(){
		return TrocarPacienteInternacaoONExceptionCode.ERRO_GENERICO_INTEGRACAO_MODULO.toString();
	}
	
	
	protected ICascaFacade getICascaFacade(){
		return this.cascaFacade;
	}
	
	protected AinInternacaoDAO getAinInternacaoDAO(){
		return ainInternacaoDAO;
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return internacaoFacade;
	}

	public IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
}
