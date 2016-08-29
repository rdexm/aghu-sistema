package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItemJnDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.model.MbcDescricaoItemJn;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade MbcDescricaoItens.
 * 
 * @author dpacheco
 *
 */
@Stateless
public class DescricaoItensRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(DescricaoItensRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;

	@Inject
	private MbcDescricaoItemJnDAO mbcDescricaoItemJnDAO;


	@EJB
	private ProfDescricoesRN profDescricoesRN;

	@EJB
	private DiagnosticoDescricaoRN diagnosticoDescricaoRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9145338908158413834L;
	
	/**
	 * Insere instância de MbcDescricaoItens.
	 * 
	 * @param descricaoItens
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void inserirDescricaoItens(MbcDescricaoItens descricaoItens
			)
			throws ApplicationBusinessException {
		
		executarAntesDeInserir(descricaoItens);
		getMbcDescricaoItensDAO().persistir(descricaoItens);
	}

	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_DTI_BRI
	 * 
	 * @param descricaoItens
	 * @param servidorLogado
	 * @throws ApplicationBusinessException
	 */
	public void executarAntesDeInserir(MbcDescricaoItens descricaoItens
			)
			throws ApplicationBusinessException {

		Integer dcgCrgSeq = descricaoItens.getId().getDcgCrgSeq();
		Short dcgSeqp = descricaoItens.getId().getDcgSeqp();
		ProfDescricoesRN profDescricoesRN = getProfDescricoesRN();

		/*
		 * Verifica se o usuario que está fazendo o insert é quem criou a
		 * descricao cirurgica
		 */
		profDescricoesRN.verificarServidorLogadoRealizaDescricaoCirurgica(
				dcgCrgSeq, dcgSeqp);

		/*
		 * Não permite inserir o registro desta tabela, se a descricao estiver
		 * concluida
		 */
		profDescricoesRN.verificarDescricaoCirurgicaConcluida(dcgCrgSeq,
				dcgSeqp);

		/* Atualiza servidor que incluiu registro */
		descricaoItens.setServidor(servidorLogadoFacade.obterServidorLogado());

		descricaoItens.setCriadoEm(new Date());
	}

	public void excluirMbcDescricaoItens(MbcDescricaoItens mbcDescricaoItens) throws ApplicationBusinessException, ApplicationBusinessException {
		executarAntesDeExcluir(mbcDescricaoItens);
		getMbcDescricaoItensDAO().remover(mbcDescricaoItens);
		executarDepoisDeExcluir(mbcDescricaoItens);
	}
	
	/**
	 * ORADB: MBCT_DTI_BRD
	 */
	protected void executarAntesDeExcluir(final MbcDescricaoItens mbcDescricaoItem) throws ApplicationBusinessException, ApplicationBusinessException {
		DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuario que exclui foi o mesmo que incluiu
		ddRN.validaServidorExclusao(mbcDescricaoItem.getServidor());
		
		// não permite que se exclua o registro desta tabela se a descricao estiver concluida
		ddRN.verificaDescricaoConcluida(mbcDescricaoItem.getMbcDescricaoCirurgica());
	}

	/**
	 * ORADB: MBCT_DTI_ARD
	 */
	protected void executarDepoisDeExcluir(final MbcDescricaoItens descricaoItem) {
		final MbcDescricaoItemJn journal = createJournal(descricaoItem, DominioOperacoesJournal.DEL);
		getMbcDescricaoItemJnDAO().persistir(journal); 
	}
	
	
	
	
	public void alterarMbcDescricaoItens(MbcDescricaoItens descricaoItem) throws ApplicationBusinessException, ApplicationBusinessException{
		final MbcDescricaoItensDAO dao = getMbcDescricaoItensDAO();
		final MbcDescricaoItens oldDescricaoItem = dao.obterOriginal(descricaoItem);
		
		executarAntesDeAlterar(descricaoItem, oldDescricaoItem);		
		dao.atualizar(descricaoItem);
		executarAposAlterar(descricaoItem, oldDescricaoItem);
	}

	/**
	 * ORADB: MBCT_DTI_BRU
	 */
	protected void executarAntesDeAlterar(final MbcDescricaoItens descricaoItens, final  MbcDescricaoItens oldDescricaoItens) throws ApplicationBusinessException, ApplicationBusinessException{
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		final DiagnosticoDescricaoRN ddRN = getDiagnosticoDescricaoRN();
		
		// verifica se o usuario que altera o registro é o mesmo que incluiu
		ddRN.verificaUsuarioInclusaoAlteracao(oldDescricaoItens.getServidor());

		// não permite alterar o registro desta tabela, se a descricao estiver concluida
		ddRN.verificaDescricaoConcluida(oldDescricaoItens.getMbcDescricaoCirurgica());
		
		// atualiza servidor que incluiu registro
		descricaoItens.setServidor(servidorLogado);
	}
		
	/**
	 * ORADB: MBCT_DTI_ARU
	 */
	protected void executarAposAlterar(final MbcDescricaoItens descricaoItem, final  MbcDescricaoItens oldDescricaoItem){
		if(!CoreUtil.igual(descricaoItem.getServidor(), oldDescricaoItem.getServidor()) ||
				!CoreUtil.igual(descricaoItem.getId().getDcgCrgSeq(), oldDescricaoItem.getId().getDcgCrgSeq()) ||
				!CoreUtil.igual(descricaoItem.getId().getDcgSeqp(), oldDescricaoItem.getId().getDcgSeqp()) ||
				!CoreUtil.igual(descricaoItem.getAsa(), oldDescricaoItem.getAsa()) ||
				!CoreUtil.igual(descricaoItem.getAchadosOperatorios(), oldDescricaoItem.getAchadosOperatorios()) ||
				!CoreUtil.igual(descricaoItem.getIntercorrenciaClinica(), oldDescricaoItem.getIntercorrenciaClinica()) ||
				!CoreUtil.igual(descricaoItem.getObservacao(), oldDescricaoItem.getObservacao()) ||
				!CoreUtil.igual(descricaoItem.getCarater(), oldDescricaoItem.getCarater()) ||
				!CoreUtil.igual(descricaoItem.getDthrInicioCirg(), oldDescricaoItem.getDthrInicioCirg()) ||
				!CoreUtil.igual(descricaoItem.getDthrFimCirg(), oldDescricaoItem.getDthrFimCirg()) ||
				!CoreUtil.igual(descricaoItem.getCriadoEm(), oldDescricaoItem.getCriadoEm()) ||
				!CoreUtil.igual(descricaoItem.getIndIntercorrencia(), oldDescricaoItem.getIndIntercorrencia())
				){
			
			final MbcDescricaoItemJn journal = createJournal(oldDescricaoItem, DominioOperacoesJournal.UPD);
			getMbcDescricaoItemJnDAO().persistir(journal); 
		}
	}

	private MbcDescricaoItemJn createJournal(
			final MbcDescricaoItens descricaoItens, DominioOperacoesJournal operacao) {
		MbcDescricaoItemJn journal =  BaseJournalFactory.getBaseJournal(operacao, MbcDescricaoItemJn.class, servidorLogadoFacade.obterServidorLogado().getUsuario());
		journal.setAchadosOperatorios(descricaoItens.getAchadosOperatorios());
		journal.setAsa(descricaoItens.getAsa());
		journal.setCarater(descricaoItens.getCarater());
		journal.setCriadoEm(descricaoItens.getCriadoEm());
		journal.setDcgCrgSeq(descricaoItens.getId().getDcgCrgSeq());
		journal.setDcgSeqp(descricaoItens.getId().getDcgSeqp());
		journal.setDthrFimCirg(descricaoItens.getDthrFimCirg());
		journal.setDthrInicioCirg(descricaoItens.getDthrInicioCirg());
		journal.setIndIntercorrencia(descricaoItens.getIndIntercorrencia());
		journal.setIntercorrenciaClinica(descricaoItens.getIntercorrenciaClinica());
		journal.setObservacao(descricaoItens.getObservacao());
		journal.setServidor(descricaoItens.getServidor());
		return journal;
	} 
	
	protected MbcDescricaoItemJnDAO getMbcDescricaoItemJnDAO() {
		return mbcDescricaoItemJnDAO;
	}
	
	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}
	
	protected ProfDescricoesRN getProfDescricoesRN() {
		return profDescricoesRN;
	}
	
	protected DiagnosticoDescricaoRN getDiagnosticoDescricaoRN() {
		return diagnosticoDescricaoRN;
	}
}
