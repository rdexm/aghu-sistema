package br.gov.mec.aghu.paciente.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentoPacientes;
import br.gov.mec.aghu.model.AghAtendimentoPacientesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class AtendimentoPacienteRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(AtendimentoPacienteRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 550438254802820151L;
	
	public enum AtendimentoPacienteRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_AGH_ATENDIMENTO_PACIENTES;

	}

	/**
	 * ORADB AGHT_APA_BRI
	 * 
	 * @param atendimentoPacientes
	 *  
	 */
	public void incluirAtendimentoPaciente(
			AghAtendimentoPacientes atendimentoPacientes) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		try {
			atendimentoPacientes.setCriadoEm(new Date());
			atendimentoPacientes.setServidor(servidorLogado);
			
			if (atendimentoPacientes.getId() != null && atendimentoPacientes.getId().getSeq() == null) {
				atendimentoPacientes.getId().setSeq(this.getAghuFacade().obterValorSequencialIdAghAtendimentoPacientes());
			}
			
			this.getAghuFacade().inserirAghAtendimentoPacientes(atendimentoPacientes);
		} catch (Exception e) {
			logError(e.getMessage(), e);
			throw new ApplicationBusinessException (
					AtendimentoPacienteRNExceptionCode.ERRO_INSERIR_AGH_ATENDIMENTO_PACIENTES);
		}

	}

	/**
	 * ORADB AGHC_GERA_ATD_PAC
	 * 
	 * Metodo que gera ou recupera o apa_seq de um atendimento.
	 * 
	 * @param atdSeq
	 * @return atdPacSeq
	 */
	public Integer gerarAtendimentoPaciente(Integer atdSeq)throws ApplicationBusinessException{
		Integer atdPacSeq = null;
		
		atdPacSeq = this.getAghuFacade().recuperarAtendimentoPaciente(atdSeq);
		if(atdPacSeq == null){
			AghAtendimentoPacientes atdPac = new AghAtendimentoPacientes();
			AghAtendimentoPacientesId atdPacId = new AghAtendimentoPacientesId();
			atdPacId.setAtdSeq(atdSeq);
			atdPac.setId(atdPacId);
			atdPac.setNumeroRn((short)0);
			atdPac.setIndRn(false);
			
			this.incluirAtendimentoPaciente(atdPac);
			
			atdPacSeq = atdPac.getId().getSeq();
		}
		
		return atdPacSeq;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
