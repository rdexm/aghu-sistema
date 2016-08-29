package br.gov.mec.aghu.paciente.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghAtendimentoJn;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class AtendimentoJournalRN extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AtendimentoJournalRN.class);
	
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
	private static final long serialVersionUID = 7884293051080339521L;

	private enum AtendimentoJournalRNExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_OBJ_ATENDIMENTO;
	}
	
	public void gerarJournalAtendimento(
			DominioOperacoesJournal operacaoJournal,
			AghAtendimentos atendimento, AghAtendimentos atendimentoOld) {
		if (operacaoJournal == DominioOperacoesJournal.UPD) {
			this.gerarJournalAtualizacao(atendimento, atendimentoOld);
		} else if (operacaoJournal == DominioOperacoesJournal.DEL) {
			this.gerarJournalRemocao(atendimento);
		}
	}
	
	/**
	 * Método para gerar journal de DELETE
	 * 
	 * @param atendimento
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void gerarJournalRemocao(AghAtendimentos atendimento) {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		AghAtendimentoJn atendimentoJournal = BaseJournalFactory
				.getBaseJournal(DominioOperacoesJournal.DEL,
						AghAtendimentoJn.class, servidorLogado.getUsuario());

		atendimentoJournal.setSeq(atendimento.getSeq());
		atendimentoJournal.setPacCodigo(atendimento.getPaciente() == null ? null : atendimento.getPaciente().getCodigo());
		atendimentoJournal.setDthrInicio(atendimento.getDthrInicio());
		atendimentoJournal.setIndPacPediatrico(atendimento.getIndPacPediatrico());
		atendimentoJournal.setIndPacPrematuro(atendimento.getIndPacPrematuro());
		atendimentoJournal.setIndPacAtendimento(atendimento.getIndPacAtendimento());
		atendimentoJournal.setHodSeq(atendimento.getHospitalDia() == null ? null : atendimento.getHospitalDia().getSeq());
		atendimentoJournal.setIntSeq(atendimento.getInternacao() == null ? null : atendimento.getInternacao().getSeq());
		atendimentoJournal.setAtuSeq(atendimento.getAtendimentoUrgencia() == null ? null : atendimento.getAtendimentoUrgencia().getSeq());
		atendimentoJournal.setDthrFim(atendimento.getDthrFim());
		atendimentoJournal.setDthrUltImprSumrPrescr(atendimento.getDthrUltImprSumrPrescr());
		atendimentoJournal.setTipoLaminaLaringo(atendimento.getTipoLaminaLaringo());
		
		if (atendimento.getServidorMovimento() != null) {
			atendimentoJournal.setSerMatriculaMovimento(atendimento.getServidorMovimento().getId().getMatricula());
			atendimentoJournal.setSerVinCodigoMovimento(atendimento.getServidorMovimento().getId().getVinCodigo());
		}
		
		atendimentoJournal.setDthrIngressoUnidade(atendimento.getDthrIngressoUnidade());
		
		if (atendimento.getServidor() != null) {
			atendimentoJournal.setPreSerMatricula(atendimento.getServidor().getId().getMatricula());
			atendimentoJournal.setPreSerVinCodigo(atendimento.getServidor().getId().getVinCodigo());
		}
		
		if(atendimento.getProfissionalEspecialidade() != null){
			atendimentoJournal.setPreEspSeq(atendimento.getProfissionalEspecialidade().getId().getEspSeq());
		}
		atendimentoJournal.setConNumero(atendimento.getConsulta() == null ? null : atendimento.getConsulta().getNumero());
		
		//TODO: Tarefa #14529 - Troca de uso do atributo mcoGestacoes pelos gsoPacCodigo e gsoSeqp
		if (atendimento.getGsoPacCodigo() != null) {
			atendimentoJournal.setGsoPacCodigo(atendimento.getGsoPacCodigo());
			atendimentoJournal.setGsoSeqp(atendimento.getGsoSeqp());
		}

		atendimentoJournal.setAtdSeqMae(atendimento.getAtendimentoMae() == null ? null : atendimento.getAtendimentoMae().getSeq());
		
		this.getAghuFacade().inserirAghAtendimentoJn(atendimentoJournal);
	}
	
	/**
	 * Método para gerar journal de UPDATE
	 * 
	 * @param atendimento
	 * @param atendimentoOld
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private void gerarJournalAtualizacao(AghAtendimentos atendimento, AghAtendimentos atendimentoOld) {
		// Existem situações onde os dados alterados não geram journal, assim
		// podem enviar o parametro atendimentoOld nulo
		if (atendimentoOld == null) {
			return;
		}
		
		if (!atendimento.getSeq().equals(atendimentoOld.getSeq())
				|| (atendimento.getPaciente() != null && atendimentoOld.getPaciente() != null 
						&& (int)atendimento.getPaciente().getCodigo() != (int)atendimentoOld.getPaciente().getCodigo())
				|| (atendimento.getDthrInicio() != null && atendimentoOld.getDthrInicio() != null 
						&& atendimento.getDthrInicio().compareTo(atendimentoOld.getDthrInicio()) != 0)
				|| atendimento.getIndPacPediatrico() != atendimentoOld.getIndPacPediatrico()
				|| atendimento.getIndPacPrematuro() != atendimentoOld.getIndPacPrematuro()
				|| atendimento.getIndPacAtendimento() != atendimentoOld.getIndPacAtendimento()
				|| (atendimento.getHospitalDia() != null && atendimentoOld.getHospitalDia() != null 
						&& (int)atendimento.getHospitalDia().getSeq() != (int)atendimentoOld.getHospitalDia().getSeq())
				|| (atendimento.getInternacao() != null && atendimentoOld.getInternacao() != null 
						&& (int)atendimento.getInternacao().getSeq() != (int)atendimentoOld.getInternacao().getSeq())
				|| (atendimento.getAtendimentoUrgencia() != null && atendimentoOld.getAtendimentoUrgencia() != null 
						&& (int)atendimento.getAtendimentoUrgencia().getSeq() != (int)atendimentoOld.getAtendimentoUrgencia().getSeq())
				|| (atendimento.getDthrFim() != null && atendimentoOld.getDthrFim() != null 
						&& atendimento.getDthrFim().compareTo(atendimentoOld.getDthrFim()) != 0)
				|| (atendimento.getDthrUltImprSumrPrescr() != null && atendimentoOld.getDthrUltImprSumrPrescr() != null 
						&& atendimento.getDthrUltImprSumrPrescr().compareTo(atendimentoOld.getDthrUltImprSumrPrescr()) != 0)
				|| atendimento.getTipoLaminaLaringo() != atendimentoOld.getTipoLaminaLaringo()
				|| (atendimento.getServidorMovimento() != null && atendimentoOld.getServidorMovimento() != null 
						&&  (int)atendimento.getServidorMovimento().getId().getMatricula() != 
							(int)atendimentoOld.getServidorMovimento().getId().getMatricula())
				|| (atendimento.getServidorMovimento() != null && atendimentoOld.getServidorMovimento() != null
						&&  (short)atendimento.getServidorMovimento().getId().getVinCodigo() !=
							(short)atendimentoOld.getServidorMovimento().getId().getVinCodigo())
				|| (atendimento.getDthrIngressoUnidade() != null && atendimentoOld.getDthrIngressoUnidade() != null 
						&& atendimento.getDthrIngressoUnidade().compareTo(atendimentoOld.getDthrIngressoUnidade()) != 0)
				|| (atendimento.getServidor() != null && atendimentoOld.getServidor() != null 
						&&  (int)atendimento.getServidor().getId().getMatricula() != 
							(int)atendimentoOld.getServidor().getId().getMatricula())
				|| (atendimento.getServidor() != null && atendimentoOld.getServidor() != null
						&&  (short)atendimento.getServidor().getId().getVinCodigo() !=
							(short)atendimentoOld.getServidor().getId().getVinCodigo())
				|| (atendimento.getEspecialidade() != null && atendimentoOld.getEspecialidade() != null 
						&& (short)atendimento.getEspecialidade().getSeq() != (short)atendimentoOld.getEspecialidade().getSeq())
				|| (atendimento.getConsulta() != null && atendimentoOld.getConsulta() != null 
						&& (int)atendimento.getConsulta().getNumero() != (int)atendimentoOld.getConsulta().getNumero())
				|| (atendimento.getAtendimentoMae() != null && atendimentoOld.getAtendimentoMae() != null 
						&& (int)atendimento.getAtendimentoMae().getSeq() != (int)atendimento.getAtendimentoMae().getSeq())
				//TODO: Tarefa #14529 - Troca de uso do atributo mcoGestacoes pelo gsoPacCodigo
				|| (atendimento.getGsoPacCodigo() != null && atendimentoOld.getGsoPacCodigo() != null 
						&& (int)atendimento.getGsoPacCodigo() != (int)atendimento.getGsoPacCodigo())
		) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

			AghAtendimentoJn atendimentoJournal = BaseJournalFactory
					.getBaseJournal(DominioOperacoesJournal.UPD,
							AghAtendimentoJn.class, servidorLogado.getUsuario());

			atendimentoJournal.setSeq(atendimentoOld.getSeq());

			atendimentoJournal.setPacCodigo(atendimentoOld.getPaciente() == null ? null : atendimentoOld.getPaciente().getCodigo());
			atendimentoJournal.setDthrInicio(atendimentoOld.getDthrInicio());
			atendimentoJournal.setIndPacPediatrico(atendimentoOld.getIndPacPediatrico());
			atendimentoJournal.setIndPacPrematuro(atendimentoOld.getIndPacPrematuro());
			atendimentoJournal.setIndPacAtendimento(atendimentoOld.getIndPacAtendimento());
			atendimentoJournal.setHodSeq(atendimentoOld.getHospitalDia() == null ? null : atendimentoOld.getHospitalDia().getSeq());
			atendimentoJournal.setIntSeq(atendimentoOld.getInternacao() == null ? null : atendimentoOld.getInternacao().getSeq());
			atendimentoJournal.setAtuSeq(atendimentoOld.getAtendimentoUrgencia() == null ? null : atendimentoOld.getAtendimentoUrgencia().getSeq());
			atendimentoJournal.setDthrFim(atendimentoOld.getDthrFim());
			atendimentoJournal.setDthrUltImprSumrPrescr(atendimentoOld.getDthrUltImprSumrPrescr());
			atendimentoJournal.setTipoLaminaLaringo(atendimentoOld.getTipoLaminaLaringo());
			if (atendimentoOld.getServidorMovimento() != null) {
				atendimentoJournal.setSerMatriculaMovimento(atendimentoOld.getServidorMovimento().getId().getMatricula());
				atendimentoJournal.setSerVinCodigoMovimento(atendimentoOld.getServidorMovimento().getId().getVinCodigo());
			}
			atendimentoJournal.setDthrIngressoUnidade(atendimentoOld.getDthrIngressoUnidade());

			if(atendimentoOld.getProfissionalEspecialidade() != null) {
				atendimentoJournal.setPreSerMatricula(atendimentoOld.getServidor().getId().getMatricula());
				atendimentoJournal.setPreSerVinCodigo(atendimentoOld.getServidor().getId().getVinCodigo());
				atendimentoJournal.setPreEspSeq(atendimentoOld.getProfissionalEspecialidade().getId().getEspSeq());
			}
			
			atendimentoJournal.setConNumero(atendimentoOld.getConsulta() == null ? null : atendimentoOld.getConsulta().getNumero());
			
			//TODO: Tarefa #14529 - Troca de uso do atributo mcoGestacoes pelos gsoPacCodigo e gsoSeqp
			if (atendimentoOld.getGsoPacCodigo() != null) {
				atendimentoJournal.setGsoPacCodigo(atendimentoOld.getGsoPacCodigo());
				atendimentoJournal.setGsoSeqp(atendimentoOld.getGsoSeqp());
			}
			atendimentoJournal.setAtdSeqMae(atendimentoOld.getAtendimentoMae() == null ? null : atendimentoOld.getAtendimentoMae().getSeq());
			
			this.getAghuFacade().inserirAghAtendimentoJn(atendimentoJournal, true);
		}
	}
	
	/**
	 * Método para clonar o atendimento sem ser "shalow". Esse método retorna os
	 * relacionamentos do objeto (ex.: atendimento.paciente), ao invés de trazer
	 * somente as propriedades do 1º nível
	 * 
	 * @param atendimento
	 * @return AghAtendimentos
	 */
	public AghAtendimentos clonarAtendimento(AghAtendimentos atendimento) throws ApplicationBusinessException {
		AghAtendimentos cloneAtendimento = new AghAtendimentos();
		
		try {
			cloneAtendimento = (AghAtendimentos) BeanUtils.cloneBean(atendimento);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(AtendimentoJournalRNExceptionCode.ERRO_CLONE_OBJ_ATENDIMENTO);
		}
		cloneAtendimento.setPaciente(atendimento.getPaciente());
		cloneAtendimento.setInternacao(atendimento.getInternacao());
		cloneAtendimento.setAtendimentoUrgencia(atendimento.getAtendimentoUrgencia());
		cloneAtendimento.setServidorMovimento(atendimento.getServidorMovimento());
		cloneAtendimento.setConsulta(atendimento.getConsulta());
		cloneAtendimento.setGsoPacCodigo(atendimento.getGsoPacCodigo());
		cloneAtendimento.setGsoSeqp(atendimento.getGsoSeqp());
		cloneAtendimento.setAtendimentoMae(atendimento.getAtendimentoMae());
		return cloneAtendimento;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
