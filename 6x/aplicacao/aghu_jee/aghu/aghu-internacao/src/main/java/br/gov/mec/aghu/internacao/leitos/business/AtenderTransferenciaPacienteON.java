package br.gov.mec.aghu.internacao.leitos.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoInternacao;
import br.gov.mec.aghu.dominio.DominioSituacaoSolicitacaoTransferencia;
import br.gov.mec.aghu.internacao.dao.AinLeitosDAO;
import br.gov.mec.aghu.internacao.dao.AinSolicTransfPacientesDAO;
import br.gov.mec.aghu.internacao.vo.ServidoresCRMVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCaractUnidFuncionais;
import br.gov.mec.aghu.model.AghCaractUnidFuncionaisId;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinSolicTransfPacientes;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


/**
 * Classe responsável por prover os métodos de negócio para a tela de
 * atendimento de solicitações de transferencia de pacientes
 * 
 * @author tfelini
 */

@Stateless
public class AtenderTransferenciaPacienteON extends BaseBusiness {


@EJB
private SolicTransfPacienteRN solicTransfPacienteRN;

private static final Log LOG = LogFactory.getLog(AtenderTransferenciaPacienteON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AinLeitosDAO ainLeitosDAO;

@Inject
private AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO;

@EJB
private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -5072107243296815119L;

	private enum AtenderTransferenciaPacienteONExceptionCode implements
			BusinessExceptionCode {
		LEITO_OBRIGATORIO, TRANSF_PACIENTE_POSSUI_ALTA_OBITO;
	}

	public AinSolicTransfPacientes obterSolicitacaoPorSeq(Integer seq) {
		AinSolicTransfPacientes solicitacao = this.getAinSolicTransfPacientesDAO().obterPorChavePrimaria(seq);
		return solicitacao;
	}

	public List<AinLeitos> pesquisarLeitosDisponiveis(String pesquisa, AinSolicTransfPacientes solicitacao) {
		return getAinLeitosDAO().pesquisarLeitosDisponiveis(pesquisa, solicitacao);
	}

	public Boolean verificaSolicitacoesComAlerta(AinSolicTransfPacientes solicitacao) {

		Boolean result = false;

		if (solicitacao.getUnfSolicitante() != null && solicitacao.getUnfSolicitante().getSeq() != null) {
			AghCaractUnidFuncionaisId idCuf = new AghCaractUnidFuncionaisId(solicitacao.getUnfSolicitante().getSeq(),
					ConstanteAghCaractUnidFuncionais.ALERTA_ATENDER_LEITO_TRANSF);
			AghCaractUnidFuncionais cuf = getAghuFacade().obterAghCaractUnidFuncionais(idCuf);
			if (cuf != null) {
				result = true;
			}

		}
		return result;
	}

	public Long pesquisarSolicitacaoTransferenciaPacienteCount(
			DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao,
			Date criadoEm, Integer prontuario,
			AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm,
			AghEspecialidades especialidade) {

		return getAinSolicTransfPacientesDAO().pesquisarSolicitacaoTransferenciaPacienteCount(indSolicitacaoInternacao, criadoEm,
				prontuario, unidadeFuncional, crm, especialidade);
	}

	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPaciente(
			Integer firstResult, Integer maxResults, String orderProperty,
			boolean asc,
			DominioSituacaoSolicitacaoTransferencia indSolicitacaoInternacao,
			Date criadoEm, Integer prontuario,
			AghUnidadesFuncionais unidadeFuncional, ServidoresCRMVO crm,
			AghEspecialidades especialidade) {

		return getAinSolicTransfPacientesDAO().pesquisarSolicitacaoTransferenciaPaciente(firstResult, maxResults, orderProperty, asc,
				indSolicitacaoInternacao, criadoEm, prontuario, unidadeFuncional, crm, especialidade);

	}
	
	
	public void cancelarSolicitacao(AinSolicTransfPacientes solicitacao)
			throws ApplicationBusinessException {
		AinSolicTransfPacientes solicitacaoOriginal = this.getAinSolicTransfPacientesDAO().obterPorChavePrimaria(solicitacao.getSeq());
		if (solicitacaoOriginal.getIndSitSolicLeito() == DominioSituacaoSolicitacaoInternacao.P) {
			solicitacaoOriginal
					.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.C);
			solicitacaoOriginal.setDthrAtendimentoSolicitacao(new Date());

			AinSolicTransfPacientesDAO ainSolicTransfPacientesDAO = this.getAinSolicTransfPacientesDAO();
			ainSolicTransfPacientesDAO.persistir(solicitacaoOriginal);
			ainSolicTransfPacientesDAO.flush();
		}
	}

	
	public void atenderSolicitacaoTransferencia(
			AinSolicTransfPacientes solicitacao, AinLeitos leitoConcedido)
			throws ApplicationBusinessException {
		
		solicitacao =  this.getAinSolicTransfPacientesDAO().obterPorChavePrimaria(solicitacao.getSeq());
		leitoConcedido = this.getAinLeitosDAO().obterPorChavePrimaria(leitoConcedido.getLeitoID());

		// Valida se Leito está nulo. Campo obrigatório.
		if (leitoConcedido == null
				|| StringUtils.isBlank(leitoConcedido.getLeitoID())) {
			throw new ApplicationBusinessException(
					AtenderTransferenciaPacienteONExceptionCode.LEITO_OBRIGATORIO);
		}

		this.validarPacienteJaPossuiAlta(solicitacao.getInternacao().getAtendimento());

		if (solicitacao.getIndSitSolicLeito() == DominioSituacaoSolicitacaoInternacao.P) {
			solicitacao
					.setIndSitSolicLeito(DominioSituacaoSolicitacaoInternacao.A);
			solicitacao.setDthrAtendimentoSolicitacao(new Date());
			this.getSolicTransfPacienteRN().alteraLeitoSolicTransfPaciente(solicitacao,
					leitoConcedido);
		}
	}
	
	/**
	 * Verifica se o paciente já possui alta
	 * @param atendimento
	 * @throws ApplicationBusinessException
	 */
	public void validarPacienteJaPossuiAlta(AghAtendimentos atendimento) throws ApplicationBusinessException{
		MpmAltaSumario altaSumario = getPrescricaoMedicaFacade().obterAltaSumarioPorAtendimento(atendimento);
		if (altaSumario != null){
			throw new ApplicationBusinessException(AtenderTransferenciaPacienteONExceptionCode.TRANSF_PACIENTE_POSSUI_ALTA_OBITO);
		}
	}
	
	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return this.prescricaoMedicaFacade;
	}
	
	public String recuperaUnidadeFuncionalInternacao(Integer seqInternacao) {
		return this.getSolicTransfPacienteRN().recuperaUnidadeFuncionalInternacao(seqInternacao);
	}

	protected SolicTransfPacienteRN getSolicTransfPacienteRN(){
		return solicTransfPacienteRN;
	}
	
	protected AinSolicTransfPacientesDAO getAinSolicTransfPacientesDAO(){
		return ainSolicTransfPacientesDAO;
	}
	
	protected AinLeitosDAO getAinLeitosDAO(){
		return ainLeitosDAO;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	public List<AinSolicTransfPacientes> pesquisarSolicitacaoTransferenciaPorProntuario(Integer prontuario) {
		return getAinSolicTransfPacientesDAO().pesquisarSolicitacaoTransferenciaPorProntuario(prontuario);
	}
	
}
