package br.gov.mec.aghu.business.bancosangue;

import java.io.Serializable;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.bancosangue.dao.AbsAmostrasPacientesDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesHemoterapicasDAO;
import br.gov.mec.aghu.bancosangue.dao.AbsSolicitacoesPorAmostraDAO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioResponsavelColeta;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoColeta;
import br.gov.mec.aghu.model.AbsAmostrasPacientes;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicas;
import br.gov.mec.aghu.model.AbsSolicitacoesHemoterapicasId;
import br.gov.mec.aghu.model.AbsSolicitacoesPorAmostra;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Implementação da package ABSK_SPA_RN.
 * 
 * Foi migrado apenas a ABST_SPA_BRI. O restante será migrado conforme necessidade.
 */

@Stateless
public class AbsSolicitacoesPorAmostraRN extends BaseBusiness implements Serializable {
	
	private static final Log LOG = LogFactory.getLog(AbsSolicitacoesPorAmostraRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private static final long serialVersionUID = -2528122644679527138L;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IBancoDeSangueFacade bancoDeSangueFacade;
	
	@EJB 
	private IServidorLogadoFacade servidorLogaroFacade;
	
	@EJB
	private AbsAmostrasPacientesRN absAmostraPacienteRN;
	
	@Inject
	private AbsAmostrasPacientesDAO absAmostrasPacientesDAO;
	
	@Inject
	private AbsSolicitacoesHemoterapicasDAO absSolicitacoesHemoterapicasDAO;
	
	@Inject
	private AbsSolicitacoesPorAmostraDAO absSolicitacoesPorAmostraDAO;

	public enum SolicitacaoPorAmostraRNExceptionCode implements
			BusinessExceptionCode {
		ABS_00653;
	}

	public void inserirSolicitacaoPorAmostra(AbsSolicitacoesPorAmostra solicitacaoPorAmostra) throws BaseException {
		// Chamada de trigger "before each row"
		this.preInserir(solicitacaoPorAmostra);
		this.absSolicitacoesPorAmostraDAO.persistir(solicitacaoPorAmostra);
	}
	
	/**
	 * @ORADB ABST_SPA_BRI
	 * @param solicPorAmostra
	 * @throws BaseException
	 */
	private void preInserir(AbsSolicitacoesPorAmostra solicPorAmostra) throws BaseException {
		
		solicPorAmostra.setCriadoEm(new Date());
		
		// verifica paciente da amostra com o do atendimento
		this.verificarPaciente(solicPorAmostra.getAmostraPaciente().getId().getPacCodigo(), solicPorAmostra.getSheAtdSeq());
		
		// atualiza o nro solicitacoes atendidas da amostra
		this.atualizarSolicitacoesAtendidas(solicPorAmostra.getAmostraPaciente().getId().getPacCodigo(),
				solicPorAmostra.getAmostraPaciente().getId().getDthrAmostra(), true);
		
		// atualiza situação da solicitação hemoterápica
		this.atualizarSolicitacaoHemoterapicaRecebida(solicPorAmostra.getSheAtdSeq(), solicPorAmostra.getSheSeq(), 
				solicPorAmostra.getIndSituacao());
		
		// atualiza servidor que registra a informação
		this.atualizarServidor(solicPorAmostra);
	}
	
	/**
	 * @ORADB RN_SPAP_ATU_SERVIDOR
	 * @param solicitacaoHemoterapica
	 */
	private void atualizarServidor(AbsSolicitacoesPorAmostra solicPorAmostra) {
		RapServidores servidorLogado = this.servidorLogaroFacade.obterServidorLogado();
		solicPorAmostra.setRapServidores(servidorLogado);
	}
	
	/**
	 * @throws BaseException 
	 * @throws BaseException 
	 * @ORADB RN_SPAP_ATU_REC_SOL
	 * @param atdSeq
	 * @param seq
	 * @param indSituacao
	 * @throws  
	 */
	private void atualizarSolicitacaoHemoterapicaRecebida(Integer atdSeq, Integer seq, DominioSituacao indSituacao) throws BaseException {
		AbsSolicitacoesHemoterapicasId id = new AbsSolicitacoesHemoterapicasId(atdSeq, seq);
		AbsSolicitacoesHemoterapicas solicitacaoHemoterapica = this.absSolicitacoesHemoterapicasDAO.obterPorChavePrimaria(id);
		
		if (DominioSituacao.A.equals(indSituacao)
				&&  (DominioSituacaoColeta.P.equals(solicitacaoHemoterapica.getIndSituacaoColeta()) 
						|| DominioSituacaoColeta.E.equals(solicitacaoHemoterapica.getIndSituacaoColeta()))
				&& (DominioResponsavelColeta.C.equals(solicitacaoHemoterapica.getIndResponsavelColeta()) 
						|| DominioResponsavelColeta.S.equals(solicitacaoHemoterapica.getIndResponsavelColeta()))){
			solicitacaoHemoterapica.setIndSituacaoColeta(DominioSituacaoColeta.R);
			this.bancoDeSangueFacade.atualizarSolicitacaoHemoterapica(solicitacaoHemoterapica, this.obterLoginUsuarioLogado());			
		}
	}
	  
	/**
	* @ORADB RN_SPAP_ATU_SLTC_ATD
	* @param pacCodigo
	* @param dtHrAmostra
	* @param soma
	*/
	private void atualizarSolicitacoesAtendidas(Integer pacCodigo, Date dtHrAmostra, Boolean soma) {
		
		AbsAmostrasPacientes absAmostrasPacientes = this.absAmostrasPacientesDAO.obterAmostrasPorCodigoDthrAmostra(pacCodigo, dtHrAmostra);
		
		if (soma) {
			Byte nroSolic = absAmostrasPacientes.getNroSolicitacoesAtendidas() == null ? 0 : absAmostrasPacientes.getNroSolicitacoesAtendidas();
			absAmostrasPacientes.setNroSolicitacoesAtendidas((byte) (nroSolic + 1));
		} else {
			Byte nroSolic = null;
			if (absAmostrasPacientes.getNroSolicitacoesAtendidas() == 0
					|| absAmostrasPacientes.getNroSolicitacoesAtendidas() == null) {
				nroSolic = 1;
			}
			absAmostrasPacientes.setNroSolicitacoesAtendidas((byte) (nroSolic - 1));
		}
		this.absAmostraPacienteRN.atualizarAbsAmostrasPacientes(absAmostrasPacientes);
	}

	/**
	 * @ORADB RN_SPAP_VER_PACIENTE
	 * @param pacCodigo
	 * @param atdSeq
	 * @throws ApplicationBusinessException
	 */
	private void verificarPaciente(Integer pacCodigo, Integer atdSeq) throws ApplicationBusinessException {
		AghAtendimentos atendimento = this.aghuFacade.buscarAtendimentoPorSeq(atdSeq);
		
		if (atendimento != null && pacCodigo == atendimento.getPaciente().getCodigo()) {
			throw new ApplicationBusinessException(
					SolicitacaoPorAmostraRNExceptionCode.ABS_00653);
		}
	}		
}
