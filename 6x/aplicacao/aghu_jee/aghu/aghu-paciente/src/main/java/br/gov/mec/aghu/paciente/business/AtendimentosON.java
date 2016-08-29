package br.gov.mec.aghu.paciente.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.vo.AtualizarLocalAtendimentoVO;
import br.gov.mec.aghu.paciente.vo.InclusaoAtendimentoVO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class AtendimentosON extends BaseBusiness {


@EJB
private AtendimentosRN atendimentosRN;

@EJB
private AtendimentoJournalRN atendimentoJournalRN;

private static final Log LOG = LogFactory.getLog(AtendimentosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IAghuFacade aghuFacade;

@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -8750432450538590023L;

	public void persistirAtendimento(AghAtendimentos atendimento, AghAtendimentos atendimentoOld, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (atendimento.getSeq() == null) {
			this.getAtendimentosRN().inserirAtendimento(atendimento, nomeMicrocomputador);
		} else {
			this.getAtendimentosRN().atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
	}
	
	private AghAtendimentos obterAtendimento(Integer seq) {		
		return this.getAghuFacade().obterAghAtendimentoPorChavePrimaria(seq);
	}
	
	// ORADB: apoio a implementação de AINK_INT_RN.RN_INTP_ATU_ATLZ_ATD (atendimentos para o segundo update)
	// Testes uitários: testObterAtendimentoNacimentoEmAndamento
	public List<AghAtendimentos> obterAtendimentoNacimentoEmAndamento(AipPacientes paciente) {
		return this.getAghuFacade().obterAtendimentoNacimentoEmAndamento(paciente);
	}
	
	public void atualizarProntuarioNoAtendimento(Integer codigoPaciente,
			Integer prontuario, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.getAtendimentosRN().atualizarProntuarioNoAtendimento(codigoPaciente, prontuario, nomeMicrocomputador, dataFimVinculoServidor);
	}
	
	public void atualizarProfissionalResponsavelPeloAtendimento(Integer seqInternacao,
			Integer seqHospitalDia, RapServidores servidor, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		this.getAtendimentosRN().atualizarProfissionalResponsavelPeloAtendimento(seqInternacao, seqHospitalDia, servidor, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public AtualizarLocalAtendimentoVO atualizarLocalAtendimento(
			Integer seqInternacao, Integer seqHospitalDia,
			Integer seqAtendimentoUrgencia, String leitoId, Short numeroQuarto,
			Short seqUnidadeFuncional, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		return this.getAtendimentosRN().atualizarLocalAtendimento(seqInternacao,
				seqHospitalDia, seqAtendimentoUrgencia, leitoId, numeroQuarto,
				seqUnidadeFuncional, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public void atualizarEspecialidadeNoAtendimento(Integer seqInternacao,
			Integer seqHospitalDia, Short seqEspecialidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		this.getAtendimentosRN().atualizarEspecialidadeNoAtendimento(seqInternacao,
				seqHospitalDia, seqEspecialidade, nomeMicrocomputador, dataFimVinculoServidor);
	}

	public InclusaoAtendimentoVO inclusaoAtendimento(Integer codigoPaciente,
			Date dataHoraInicio, Integer seqHospitalDia, Integer seqInternacao,
			Integer seqAtendimentoUrgencia, Date dataHoraFim, String leitoId,
			Short numeroQuarto, Short seqUnidadeFuncional,
			Short seqEspecialidade, RapServidores servidor,
			Integer codigoClinica, RapServidores digitador,
			Integer dcaBolNumero, Short dcaBolBsaCodigo, Date dcaBolData,
			Integer apeSeq, Integer numeroConsulta,
			Integer seqGradeAgendamenConsultas, String nomeMicrocomputador) throws ApplicationBusinessException {
		return this.getAtendimentosRN().inclusaoAtendimento(codigoPaciente,
				dataHoraInicio, seqHospitalDia, seqInternacao,
				seqAtendimentoUrgencia, dataHoraFim, leitoId, numeroQuarto,
				seqUnidadeFuncional, seqEspecialidade, servidor, codigoClinica,
				digitador, dcaBolNumero, dcaBolBsaCodigo, dcaBolData, apeSeq,
				numeroConsulta, seqGradeAgendamenConsultas, nomeMicrocomputador);
	}
	
	/**
	 * Atualiza o identificador de Paciente Pediátrico
	 * @param seq
	 * @param indPacPediatrico
	 * @throws Exception 
	 * @throws BaseException 
	 */
	public void atualizarPacPediatrico(Integer seq, Boolean indPacPediatrico, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {	
		AghAtendimentos atendimento = obterAtendimento(seq);
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		if (atendimento != null && !(atendimento.getIndPacPediatrico().equals(indPacPediatrico))) {
			AghAtendimentos atendimentoOld = this.getAtendimentoJournalRN().clonarAtendimento(atendimento);
			atendimento.setIndPacPediatrico(indPacPediatrico);
			this.getAtendimentosRN().atualizarAtendimento(atendimento, atendimentoOld, nomeMicrocomputador, servidorLogado, dataFimVinculoServidor);
		}
	}

	public List<AghAtendimentos> obterAtendimentoPorSeqAtendimentoUrgencia(Integer seqAtendimentoUrgencia) {
		return this.getAghuFacade().obterAtendimentoPorSeqAtendimentoUrgencia(seqAtendimentoUrgencia);
	}

	public List<AghAtendimentos> obterAtendimentoPorSeqInternacao(Integer seqInternacao) {
		return this.getAghuFacade().obterAtendimentoPorSeqInternacao(seqInternacao);
	}

	public List<AghAtendimentos> obterAtendimentoPorSeqHospitalDia(Integer seqHospitalDia) {
		return this.getAghuFacade().obterAtendimentoPorSeqHospitalDia(seqHospitalDia);
	}
	
	/**
	 * Retorna codigo do paciente
	 * @param altanAtdSeq
	 * @param altanApaSeq
	 * @return
	 */
	public Integer recuperarCodigoPaciente(Integer altanAtdSeq) throws ApplicationBusinessException {
		return this.getAghuFacade().recuperarCodigoPaciente(altanAtdSeq);
	}

	/**
	 * Obtem uma lista de atendimentos de mães com atendimento em andamento (S) e prontuário específico
	 * @param prontuario
	 * @return lista de atendimentos de mães
	 */
	public List<AghAtendimentos> obterAtendimentoPorProntuarioPacienteAtendimento(
			Integer prontuario) {
		return getAghuFacade().obterAtendimentoPorProntuarioPacienteAtendimento(prontuario);
	}

	/**
	 * Obtem uma lista de atendimentos de recém-nascidos de um atendimento de mãe específico 
	 * @param prontuario
	 * @return lista de atendimentos de recém-nascidos
	 */
	public List<AghAtendimentos> obterAtendimentosRecemNascidosPorProntuarioMae(
			AghAtendimentos aghAtendimentos) {
		return getAghuFacade().obterAtendimentosRecemNascidosPorProntuarioMae(aghAtendimentos);
	}
	
	/**
	 * 
	 * @param dominioOrigemAtendimento
	 * @param consultaNumero
	 * @return
	 */
	public List<AghAtendimentos> listarAtendimentosPorConsultaEOrigem(DominioOrigemAtendimento dominioOrigemAtendimento, Integer consultaNumero){
		return getAghuFacade().listarAtendimentosPorConsultaEOrigem(dominioOrigemAtendimento, consultaNumero);
	}

	protected AtendimentoJournalRN getAtendimentoJournalRN() {
		return atendimentoJournalRN;
	}

	protected AtendimentosRN getAtendimentosRN() {
		return atendimentosRN;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
}
