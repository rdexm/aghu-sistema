package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmPim2;
import br.gov.mec.aghu.model.MpmPim2Jn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmPim2JnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class Pim2JournalRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(Pim2JournalRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmPim2JnDAO mpmPim2JnDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1257988891194713106L;

	private enum Pim2JournalRNExceptionCode implements BusinessExceptionCode {
		ERRO_CLONE_PIM2;
	}
	
	public void gerarJournalPim2(DominioOperacoesJournal operacaoJournal, MpmPim2 pim2, 
			MpmPim2 pim2Old) throws ApplicationBusinessException {
		if (operacaoJournal == DominioOperacoesJournal.UPD) {
			this.gerarJournalAtualizacao(pim2, pim2Old);
		} else if (operacaoJournal == DominioOperacoesJournal.DEL) {
			this.gerarJournalRemocao(pim2);
		}
	}
	
	/**
	 * Método para gerar journal de DELETE
	 * 
	 * @param pim2
	 */
	private void gerarJournalRemocao(MpmPim2 pim2) throws ApplicationBusinessException {
		MpmPim2Jn pim2Journal = this.criarPim2Journal(
				pim2, DominioOperacoesJournal.DEL);
		
		this.getPim2JnDAO().persistir(pim2Journal);
		this.getPim2JnDAO().flush();
	}
	
	/**
	 * Método para gerar journal de UPDATE
	 * 
	 * @param pim2
	 * @param pim2Old
	 */
	private void gerarJournalAtualizacao(MpmPim2 pim2, MpmPim2 pim2Old) throws ApplicationBusinessException {

		if (pim2 == null || pim2Old == null) {
			return;
		}
		
		if (!pim2.getSeq().equals(pim2Old.getSeq())
				|| CoreUtil.modificados(pim2.getAdmissaoEletiva(), pim2Old.getAdmissaoEletiva())
				|| CoreUtil.modificados(pim2.getAdmissaoRecuperaCirProc(), pim2Old.getAdmissaoRecuperaCirProc())
				|| CoreUtil.modificados(pim2.getAdmissaoPosBypass(), pim2Old.getAdmissaoPosBypass())
				|| CoreUtil.modificados(pim2.getDiagAltoRisco(), pim2Old.getDiagAltoRisco())
				|| CoreUtil.modificados(pim2.getDiagBaixoRisco(), pim2Old.getDiagBaixoRisco())
				|| CoreUtil.modificados(pim2.getFaltaRespostaPupilar(), pim2Old.getFaltaRespostaPupilar())
				|| CoreUtil.modificados(pim2.getExcessoBase(), pim2Old.getExcessoBase())
				|| CoreUtil.modificados(pim2.getPao2(), pim2Old.getPao2())
				|| CoreUtil.modificados(pim2.getFio2(), pim2Old.getFio2())
				|| CoreUtil.modificados(pim2.getPressaoSistolica(), pim2Old.getPressaoSistolica())
				|| CoreUtil.modificados(pim2.getVentilacaoMecanica(), pim2Old.getVentilacaoMecanica())
				|| (pim2.getDthrRealizacao() != null && pim2Old.getDthrRealizacao() != null 
						&& pim2.getDthrRealizacao().compareTo(pim2Old.getDthrRealizacao()) != 0)
				|| (pim2.getCriadoEm() != null && pim2Old.getCriadoEm() != null 
						&& pim2.getCriadoEm().compareTo(pim2Old.getCriadoEm()) != 0)
				|| CoreUtil.modificados(pim2.getAtendimento().getSeq(), pim2Old.getAtendimento().getSeq())
				|| (pim2.getDthrIngressoUnidade() != null && pim2Old.getDthrIngressoUnidade() != null 
						&& pim2.getDthrIngressoUnidade().compareTo(pim2Old.getDthrIngressoUnidade()) != 0)
				|| CoreUtil.modificados(pim2.getSituacao(), pim2Old.getSituacao())
				|| CoreUtil.modificados(pim2.getEscorePim2(), pim2Old.getEscorePim2())
				|| CoreUtil.modificados(pim2.getProbabilidadeMorte(), pim2Old.getProbabilidadeMorte())
				|| CoreUtil.modificados(pim2.getServidor(), pim2Old.getServidor())
		) {
			MpmPim2Jn pim2Journal = this.criarPim2Journal(pim2Old, DominioOperacoesJournal.UPD);
			this.getPim2JnDAO().persistir(pim2Journal);
			this.getPim2JnDAO().flush();
		}
	}
	
	private MpmPim2Jn criarPim2Journal(MpmPim2 pim2, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmPim2Jn jn = BaseJournalFactory.getBaseJournal(operacao, MpmPim2Jn.class, servidorLogado.getUsuario());

		jn.setSeq(pim2.getSeq());
		jn.setAdmissaoEletiva(pim2.getAdmissaoEletiva());
		jn.setAdmissaoRecuperaCirProc(pim2.getAdmissaoRecuperaCirProc());
		jn.setAdmissaoPosBypass(pim2.getAdmissaoPosBypass());
		jn.setDiagAltoRisco(pim2.getDiagAltoRisco());
		jn.setDiagBaixoRisco(pim2.getDiagBaixoRisco());
		jn.setFaltaRespostaPupilar(pim2.getFaltaRespostaPupilar());
		jn.setExcessoBase(pim2.getExcessoBase());
		jn.setPao2(pim2.getPao2());
		jn.setFio2(pim2.getFio2());
		jn.setPressaoSistolica(pim2.getPressaoSistolica());
		jn.setVentilacaoMecanica(pim2.getVentilacaoMecanica());
		jn.setDthrRealizacao(pim2.getDthrRealizacao());
		jn.setCriadoEm(pim2.getCriadoEm());
		jn.setAtdSeq(pim2.getAtendimento().getSeq());
		jn.setDthrIngressoUnidade(pim2.getDthrIngressoUnidade());
		jn.setSituacao(pim2.getSituacao());
		jn.setEscorePim2(pim2.getEscorePim2());
		jn.setProbabilidadeMorte(pim2.getProbabilidadeMorte());
		jn.setSerMatricula(pim2.getServidor() != null ? pim2.getServidor().getId().getMatricula() : null);
		jn.setSerVinCodigo(pim2.getServidor() != null ? pim2.getServidor().getId().getVinCodigo() : null);
		
		return jn;
	}
	
	/**
	 * Método para clonar o pim2.
	 * 
	 * @param pim2
	 * @return MpmPim2
	 */
	public MpmPim2 clonarPim2(MpmPim2 pim2) throws ApplicationBusinessException {
		MpmPim2 clonePim2 = new MpmPim2();
		
		try {
			// clona o objeto (faz um shalow clone), trazendo os objetos do 1º nível
			// Pim2 não está mapeado com os relacionamentos, assim não é feito
			// atribuição dos mesmos.
			clonePim2 = (MpmPim2) BeanUtils.cloneBean(pim2);
		} catch (Exception e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(Pim2JournalRNExceptionCode.ERRO_CLONE_PIM2);
		}
		return clonePim2;
	}
	
	/**
	 * Retorna o DAO da journal de atendimento
	 * 
	 * @return
	 */
	protected MpmPim2JnDAO getPim2JnDAO() {
		return mpmPim2JnDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
