package br.gov.mec.aghu.prescricaomedica.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import java.util.Objects;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.MpmLaudo;
import br.gov.mec.aghu.model.MpmLaudoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmLaudoJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class LaudoJournalRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(LaudoJournalRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmLaudoJnDAO mpmLaudoJnDAO;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 3480175164255590896L;

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	
	protected MpmLaudoJnDAO getMpmLaudoJnDAO() {
		return mpmLaudoJnDAO;
	}

	/**
	 * Metodo que compara alguns atributos de um laudo antes de depois de alterado e, conforme a validacao, 
	 * retorna true indicando se deve ser inserido um registro na tabela de journal.
	 * @param laudoNew
	 * @param laudoOld
	 * @return
	 */
	protected Boolean verificarInsercaoLaudoJournal(MpmLaudo laudoNew, MpmLaudo laudoOld){
		boolean inserirJournal = false;
		
		if(
//			((laudoNew.getServidorFeitoManual() != null && laudoOld.getServidorFeitoManual() == null)
//				|| (laudoNew.getServidorFeitoManual() == null && laudoOld.getServidorFeitoManual() != null)	
//				|| (laudoNew.getServidorFeitoManual() != null && laudoOld.getServidorFeitoManual() != null 
//					&& laudoNew.getServidorFeitoManual().getMatriculaVinculo() != laudoOld.getServidorFeitoManual().getMatriculaVinculo()))
			(!Objects.equals(laudoNew.getServidorFeitoManual(),laudoOld.getServidorFeitoManual()))			
			|| (!Objects.equals(laudoNew.getSeq(),laudoOld.getSeq()))
			|| (!Objects.equals(laudoNew.getDthrInicioValidade(), laudoOld.getDthrInicioValidade()))		
			|| (!Objects.equals(laudoNew.getDthrFimValidade(), laudoOld.getDthrFimValidade()))			
			|| (!Objects.equals(laudoNew.getDthrFimPrevisao(), laudoOld.getDthrFimPrevisao()))			
			|| (!Objects.equals(laudoNew.getJustificativa(), laudoOld.getJustificativa()))			
			|| (!Objects.equals(laudoNew.getCriadoEm(), laudoOld.getCriadoEm()))			
			|| (!Objects.equals(laudoNew.getContaDesdobrada(), laudoOld.getContaDesdobrada()))			
			|| (!Objects.equals(laudoNew.getImpresso(), laudoOld.getImpresso()))			
			|| (!Objects.equals(laudoNew.getDuracaoTratSolicitado(), laudoOld.getDuracaoTratSolicitado()))			
			|| (!Objects.equals(laudoNew.getLaudoManual(), laudoOld.getLaudoManual()))			
			|| (!Objects.equals(laudoNew.getProcedimentoHospitalarInterno(), laudoOld.getProcedimentoHospitalarInterno()))			
			|| (!Objects.equals(laudoNew.getAtendimento(), laudoOld.getAtendimento()))			
			|| (!Objects.equals(laudoNew.getServidor(), laudoOld.getServidor()))			
			|| (!Objects.equals(laudoNew.getLaudo(), laudoOld.getLaudo()))			
			|| (!Objects.equals(laudoNew.getTipoLaudo(), laudoOld.getTipoLaudo()))			
			|| (!Objects.equals(laudoNew.getPrescricaoProcedimento(), laudoOld.getPrescricaoProcedimento()))			
			|| (!Objects.equals(laudoNew.getPrescricaoNpts(), laudoOld.getPrescricaoNpts()))			
			|| (!Objects.equals(laudoNew.getItemPrescricaoMdtos(), laudoOld.getItemPrescricaoMdtos()))			
					
		){
			inserirJournal = true;
		}
		
		return inserirJournal;
		
	}
	
	/**
	 * Metodo para setar os atributos de um laudoJn antes de inseri-lo.
	 * @param laudoOld
	 * @return
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	private MpmLaudoJn prepararLaudoJnParaGravar(MpmLaudo laudoOld, DominioOperacoesJournal operacao){
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		MpmLaudoJn jn = BaseJournalFactory.getBaseJournal(operacao, MpmLaudoJn.class, servidorLogado.getUsuario());
		
		jn.setSerVinCodigoFeitoManual(laudoOld.getServidorFeitoManual() != null ? laudoOld.getServidorFeitoManual().getId().getVinCodigo() : null);
		jn.setSeq(laudoOld.getSeq());
		jn.setDthrInicioValidade(laudoOld.getDthrInicioValidade());
		jn.setDthrFimValidade(laudoOld.getDthrFimValidade());
		jn.setDthrFimPrevisao(laudoOld.getDthrFimPrevisao());
		jn.setJustificativa(laudoOld.getJustificativa());
		jn.setCriadoEm(laudoOld.getCriadoEm());
		jn.setContaDesdobrada(laudoOld.getContaDesdobrada());
		jn.setImpresso(laudoOld.getImpresso());
		jn.setDuracaoTratSolicitado(laudoOld.getDuracaoTratSolicitado());
		jn.setLaudoManual(laudoOld.getLaudoManual());
		jn.setPhiSeq(laudoOld.getProcedimentoHospitalarInterno() != null ? laudoOld.getProcedimentoHospitalarInterno().getSeq() : null);
		jn.setAtdSeq(laudoOld.getAtendimento() != null ? laudoOld.getAtendimento().getSeq() : null);
		jn.setSerMatricula(laudoOld.getServidor() != null ? laudoOld.getServidor().getId().getMatricula() : null);
		jn.setSerVinCodigo(laudoOld.getServidor() != null ? laudoOld.getServidor().getId().getVinCodigo() : null);
		jn.setLadSeq(laudoOld.getLaudo() != null ? laudoOld.getLaudo().getSeq() : null);
		jn.setTuoSeq(laudoOld.getTipoLaudo() != null ? laudoOld.getTipoLaudo().getSeq() : null);
		jn.setSerMatriculaFeitoManual(laudoOld.getServidorFeitoManual() != null ? laudoOld.getServidorFeitoManual().getId().getMatricula() : null);
		jn.setPprAtdSeq(laudoOld.getPrescricaoProcedimento() != null ?  laudoOld.getPrescricaoProcedimento().getId().getAtdSeq() : null);
		jn.setPprSeq(laudoOld.getPrescricaoProcedimento() != null ?  laudoOld.getPrescricaoProcedimento().getId().getSeq().intValue() : null);
		jn.setPnpAtdSeq(laudoOld.getPrescricaoNpts() != null ? laudoOld.getPrescricaoNpts().getId().getAtdSeq() : null);
		jn.setPnpSeq(laudoOld.getPrescricaoNpts() != null ? laudoOld.getPrescricaoNpts().getId().getSeq() : null);
		if(laudoOld.getItemPrescricaoMdtos() != null){
			jn.setImePmdAtdSeq(laudoOld.getItemPrescricaoMdtos().getId().getPmdAtdSeq());
			jn.setImePmdSeq(laudoOld.getItemPrescricaoMdtos().getId().getPmdSeq().intValue());
			jn.setImeMedMatCodigo(laudoOld.getItemPrescricaoMdtos().getId().getMedMatCodigo());
			jn.setImeSeqp(laudoOld.getItemPrescricaoMdtos().getId().getSeqp());
		}else{
			jn.setImePmdAtdSeq(null);
			jn.setImePmdSeq(null);
			jn.setImeMedMatCodigo(null);
			jn.setImeSeqp(null);
		}
		return jn;
	}
	
	/**
	 * Metodo para realizar a inclusao de journal de laudo, conforme a operacao, caso necessario.
	 * @param laudoNew
	 * @param laudoOld
	 * @param operacao
	 * @throws ApplicationBusinessException
	 */
	public void realizarLaudoJournal(MpmLaudo laudoNew, MpmLaudo laudoOld, DominioOperacoesJournal operacao) throws ApplicationBusinessException {
			
			if(DominioOperacoesJournal.UPD.equals(operacao) && verificarInsercaoLaudoJournal(laudoNew, laudoOld)){
				getMpmLaudoJnDAO().persistirLaudoJn(prepararLaudoJnParaGravar(laudoOld, operacao));
			}
			
			if(DominioOperacoesJournal.DEL.equals(operacao)){
				getMpmLaudoJnDAO().persistirLaudoJn(prepararLaudoJnParaGravar(laudoOld, operacao));
			}
			
		
			//TODO Implementar aqui outros casos de operacoes de journal (delete, etc) conforme necessidade.
			
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

}
