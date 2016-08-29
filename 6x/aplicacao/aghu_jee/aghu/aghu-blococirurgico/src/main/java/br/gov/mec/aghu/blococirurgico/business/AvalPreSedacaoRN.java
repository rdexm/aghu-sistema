package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAvalPreSedacaoDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcAvalPreSedacaoJnDAO;
import br.gov.mec.aghu.model.MbcAvalPreSedacao;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoId;
import br.gov.mec.aghu.model.MbcAvalPreSedacaoJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class AvalPreSedacaoRN extends BaseBusiness{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4817794485434767212L;
	
	public enum ProcRNExceptionCode implements BusinessExceptionCode {
		PDT_00124
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MbcAvalPreSedacaoDAO mbcAvalPreSedacaoDAO;
	
	@Inject
	private MbcAvalPreSedacaoJnDAO mbcAvalPreSedacaoJnDAO;
	

	private static final Log LOG = LogFactory.getLog(AvalPreSedacaoRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	public void persistirMbcAvalPreSedacao(final MbcAvalPreSedacao mbcAvalPreSedacao) throws ApplicationBusinessException {
		MbcAvalPreSedacao oldmbcAvalPreSedacao = mbcAvalPreSedacaoDAO.pesquisarMbcAvalPreSedacaoPorDdtSeq(mbcAvalPreSedacao.getId());
		
		if(oldmbcAvalPreSedacao == null){
			inserirAvalPreSedacao(mbcAvalPreSedacao);
		} else {
			inserirJournal(mbcAvalPreSedacao, DominioOperacoesJournal.UPD);
			mbcAvalPreSedacaoDAO.merge(mbcAvalPreSedacao);	
		}
	}


	public void inserirAvalPreSedacao(MbcAvalPreSedacao newAvalPreSedacao) throws ApplicationBusinessException {
		executarAntesInserir(newAvalPreSedacao);
		mbcAvalPreSedacaoDAO.persistir(newAvalPreSedacao);
	}
	
	public void executarAntesInserir(MbcAvalPreSedacao newMbcAvalPreSedacao) throws ApplicationBusinessException {
		
		newMbcAvalPreSedacao.setCriadoEm(new Date());
		
		// Atualiza servidor que incluiu registro
		newMbcAvalPreSedacao.setRapServidores(servidorLogadoFacade.obterServidorLogado());
		//tempo jejum >0
		verificarTempoJejum(newMbcAvalPreSedacao);
	}

	private void verificarTempoJejum(MbcAvalPreSedacao mbcAvalPreSedacao) throws ApplicationBusinessException {
		if(mbcAvalPreSedacao.getTempoJejum()!=null){
			if (mbcAvalPreSedacao.getTempoJejum()==0) {
				throw new ApplicationBusinessException(ProcRNExceptionCode.PDT_00124);
			}
		}
	}
	
	private void inserirJournal(MbcAvalPreSedacao mbcAvalPreSedacao, DominioOperacoesJournal operacao) {

		MbcAvalPreSedacao oldMbcAvalPreSedacao = mbcAvalPreSedacaoDAO.pesquisarMbcAvalPreSedacaoPorDdtSeq(mbcAvalPreSedacao.getId());
		
		if (oldMbcAvalPreSedacao != null) {

			if (CoreUtil.modificados(mbcAvalPreSedacao.getCriadoEm(), oldMbcAvalPreSedacao.getCriadoEm())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getAsa(), oldMbcAvalPreSedacao.getAsa())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getAvaliacaoClinica(), oldMbcAvalPreSedacao.getAvaliacaoClinica())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getComorbidades(), oldMbcAvalPreSedacao.getComorbidades())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getIndParticAvalCli(), oldMbcAvalPreSedacao.getIndParticAvalCli())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getExameFisico(), oldMbcAvalPreSedacao.getExameFisico())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getTempoJejum(), oldMbcAvalPreSedacao.getTempoJejum())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getCriadoEm(), oldMbcAvalPreSedacao.getCriadoEm())
					|| CoreUtil.modificados(mbcAvalPreSedacao.getViaAereas(), oldMbcAvalPreSedacao.getViaAereas())) {

				RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();

				final MbcAvalPreSedacaoJn jn = BaseJournalFactory.getBaseJournal(operacao, MbcAvalPreSedacaoJn.class,
						servidorLogado.getUsuario());

				jn.setDcgCrgSeq(mbcAvalPreSedacao.getId().getDcgCrgSeq());
				jn.setDcgSeqp(mbcAvalPreSedacao.getId().getDcgSeqp());
				jn.setCriadoEm(mbcAvalPreSedacao.getCriadoEm());
				jn.setAsa(mbcAvalPreSedacao.getAsa());
				jn.setAvaliacaoClinica(mbcAvalPreSedacao.getAvaliacaoClinica());
				jn.setComorbidades(mbcAvalPreSedacao.getComorbidades());
				jn.setExameFisico(mbcAvalPreSedacao.getExameFisico());
				jn.setIndParticAvalCli(mbcAvalPreSedacao.getIndParticAvalCli());
				jn.setTempoJejum(mbcAvalPreSedacao.getTempoJejum());
				if (mbcAvalPreSedacao.getViaAereas() != null) {
					jn.setViaAereas(mbcAvalPreSedacao.getViaAereas().getSeq());
				}
				jn.setSerMatricula(mbcAvalPreSedacao.getRapServidores().getId().getMatricula());
				jn.setSerVinCodigo(mbcAvalPreSedacao.getRapServidores().getId().getVinCodigo());
				mbcAvalPreSedacaoJnDAO.persistir(jn);
			}
		}
	}

	public void removerMbcAvalPreSedacao(MbcAvalPreSedacao mbcAvalPreSedacao) {
		
		MbcAvalPreSedacaoId id = new MbcAvalPreSedacaoId(mbcAvalPreSedacao.getId().getDcgCrgSeq(), mbcAvalPreSedacao.getId().getDcgSeqp());
		
		mbcAvalPreSedacao = mbcAvalPreSedacaoDAO.obterPorChavePrimaria(id);
		mbcAvalPreSedacaoDAO.remover(mbcAvalPreSedacao);
		executarAposRemover(mbcAvalPreSedacao);
	}
	
	private void executarAposRemover(MbcAvalPreSedacao mbcAvalPreSedacao) {
		inserirJournal(mbcAvalPreSedacao, DominioOperacoesJournal.DEL);
	}
}
