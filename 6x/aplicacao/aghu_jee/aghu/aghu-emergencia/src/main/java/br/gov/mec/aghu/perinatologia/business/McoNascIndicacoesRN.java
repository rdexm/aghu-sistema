package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoNascIndicacaoJn;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoNascIndicacaoJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascIndicacoesDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
/**
 * @author israel.haas
 */
@Stateless
public class McoNascIndicacoesRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoNascIndicacoesDAO mcoNascIndicacoesDAO;
	
	@Inject McoNascIndicacaoJnDAO mcoNascIndicacaoJnDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum McoNascIndicacoesRNExceptionCode implements BusinessExceptionCode {
		ERRO_ALTERACAO_INDICACAO_CESARIANA, ERRO_EXCLUSAO_INDICACAO_CESARIANA, ERRO_ALTERACAO_INDICACAO_INSTRUMENTADO,
		ERRO_EXCLUSAO_INDICACAO_INSTRUMENTADO
	}
	
	public void excluirMcoNascIndicacoesCesarea(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) throws ApplicationBusinessException {
		if (this.mcoNascIndicacoesDAO.verificaExisteMcoNascIndicacoesCesarea(gsoPacCodigo, gsoSeqp, pSeqp)) {
			List<McoNascIndicacoes> listNascIndicacoes = this.mcoNascIndicacoesDAO.pesquisarIndicacoesCesariana(gsoPacCodigo, gsoSeqp, pSeqp);
			
			for (McoNascIndicacoes nascIndicacoes : listNascIndicacoes) {
				McoNascIndicacoes nascIndicacoesOriginal = this.mcoNascIndicacoesDAO.obterOriginal(nascIndicacoes);
				
				this.inserirJournalMcoNascIndicacoes(nascIndicacoesOriginal, DominioOperacoesJournal.DEL);
				
				try {
					this.mcoNascIndicacoesDAO.remover(nascIndicacoes);
					this.mcoNascIndicacoesDAO.flush();
				} catch(RuntimeException e) {
					throw new ApplicationBusinessException(McoNascIndicacoesRNExceptionCode.ERRO_EXCLUSAO_INDICACAO_CESARIANA);
				}
			}
		}
	}
	
	public void excluirMcoNascIndicacoesForcipe(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) throws ApplicationBusinessException {
		if (this.mcoNascIndicacoesDAO.verificaExisteMcoNascIndicacoesForcipe(gsoPacCodigo, gsoSeqp, pSeqp)) {
			List<McoNascIndicacoes> listNascIndicacoes = this.mcoNascIndicacoesDAO
					.pesquisarIndicacoesPartoInstrumentado(gsoPacCodigo, gsoSeqp, pSeqp);
			
			for (McoNascIndicacoes nascIndicacoes : listNascIndicacoes) {
				McoNascIndicacoes nascIndicacoesOriginal = this.mcoNascIndicacoesDAO.obterOriginal(nascIndicacoes);
				
				this.inserirJournalMcoNascIndicacoes(nascIndicacoesOriginal, DominioOperacoesJournal.DEL);
				
				try {
					this.mcoNascIndicacoesDAO.remover(nascIndicacoes);
					this.mcoNascIndicacoesDAO.flush();
				} catch(RuntimeException e) {
					throw new ApplicationBusinessException(McoNascIndicacoesRNExceptionCode.ERRO_EXCLUSAO_INDICACAO_INSTRUMENTADO);
				}
			}
		}
	}
	
	public void excluirNascIndicacoesForcipe(McoNascIndicacoes mcoNascIndicacoes) {
		
		McoNascIndicacoes nascIndicacoes = this.mcoNascIndicacoesDAO.obterPorChavePrimaria(mcoNascIndicacoes.getSeq());
		
		McoNascIndicacoes nascIndicacoesOriginal = this.mcoNascIndicacoesDAO.obterOriginal(nascIndicacoes);
		
		this.inserirJournalMcoNascIndicacoes(nascIndicacoesOriginal, DominioOperacoesJournal.DEL);
		
		this.mcoNascIndicacoesDAO.remover(nascIndicacoes);
	}
	
	public void excluirNascIndicacoesCesariana(McoNascIndicacoes mcoNascIndicacoes) {
		
		McoNascIndicacoes nascIndicacoes = this.mcoNascIndicacoesDAO.obterPorChavePrimaria(mcoNascIndicacoes.getSeq());
		
		McoNascIndicacoes nascIndicacoesOriginal = this.mcoNascIndicacoesDAO.obterOriginal(nascIndicacoes);
		
		this.inserirJournalMcoNascIndicacoes(nascIndicacoesOriginal, DominioOperacoesJournal.DEL);
		
		this.mcoNascIndicacoesDAO.remover(nascIndicacoes);
	}
	
	/**
	 * @ORADB MCO_NASC_INDICACOES.MCOT_NAI_ARD
	 * @param nascIndicacoesOriginal
	 * @param operacao
	 */
	public void inserirJournalMcoNascIndicacoes(McoNascIndicacoes nascIndicacoesOriginal, DominioOperacoesJournal operacao) {
		
		McoNascIndicacaoJn mcoNascIndicacaoJn = new McoNascIndicacaoJn();
		
		mcoNascIndicacaoJn.setNomeUsuario(usuario.getLogin());
		mcoNascIndicacaoJn.setOperacao(operacao);
		mcoNascIndicacaoJn.setCriadoEm(nascIndicacoesOriginal.getCriadoEm());
		mcoNascIndicacaoJn.setSeq(nascIndicacoesOriginal.getSeq());
		mcoNascIndicacaoJn.setInaSeq(nascIndicacoesOriginal.getIndicacaoNascimento().getSeq());
		mcoNascIndicacaoJn.setSerMatricula(nascIndicacoesOriginal.getServidor().getId().getMatricula());
		mcoNascIndicacaoJn.setSerVinCodigo(nascIndicacoesOriginal.getServidor().getId().getVinCodigo());
		if (nascIndicacoesOriginal.getMcoForcipes() != null) {
			mcoNascIndicacaoJn.setFcpNasGsoSeqp(nascIndicacoesOriginal.getMcoForcipes().getId().getGsoSeqp());
			mcoNascIndicacaoJn.setFcpNasGsoPacCodigo(nascIndicacoesOriginal.getMcoForcipes().getId().getGsoPacCodigo());
			mcoNascIndicacaoJn.setFcpNasSeqp(nascIndicacoesOriginal.getMcoForcipes().getId().getSeqp());
		}
		if (nascIndicacoesOriginal.getMcoCesarianas() != null) {
			mcoNascIndicacaoJn.setCsrNasGsoSeqp(nascIndicacoesOriginal.getMcoCesarianas().getId().getGsoSeqp());
			mcoNascIndicacaoJn.setCsrNasGsoPacCodigo(nascIndicacoesOriginal.getMcoCesarianas().getId().getGsoPacCodigo());
			mcoNascIndicacaoJn.setCsrNasSeqp(nascIndicacoesOriginal.getMcoCesarianas().getId().getSeqp());
		}
		
		this.mcoNascIndicacaoJnDAO.persistir(mcoNascIndicacaoJn);
	}
	
	public boolean isMcoNascIndicacoesAlterada(McoNascIndicacoes nascIndicacoes, McoNascIndicacoes nascIndicacoesOriginal) {
		return CoreUtil.modificados(nascIndicacoes.getMcoForcipes(), nascIndicacoesOriginal.getMcoForcipes()) ||
				CoreUtil.modificados(nascIndicacoes.getMcoCesarianas(), nascIndicacoesOriginal.getMcoCesarianas()) ||
				CoreUtil.modificados(nascIndicacoes.getIndicacaoNascimento(), nascIndicacoesOriginal.getIndicacaoNascimento()) ||
				CoreUtil.modificados(nascIndicacoes.getServidor() , nascIndicacoesOriginal.getServidor()) ||				
				CoreUtil.modificados(nascIndicacoes.getCriadoEm(), nascIndicacoesOriginal.getCriadoEm());
	}
	
	public void inserirMcoNascIndicacoesForcipes(McoNascIndicacoes mcoNascIndicacoes, McoForcipes mcoForcipes) {
		this.preInsert(mcoNascIndicacoes);
		
		mcoNascIndicacoes.setMcoForcipes(mcoForcipes);
		mcoNascIndicacoes.setMcoCesarianas(null);
		
		this.mcoNascIndicacoesDAO.persistir(mcoNascIndicacoes);
	}
	
	public void inserirMcoNascIndicacoesCesarianas(McoNascIndicacoes mcoNascIndicacoes, McoCesarianas mcoCesarianas) {
		this.preInsert(mcoNascIndicacoes);
		
		mcoNascIndicacoes.setMcoCesarianas(mcoCesarianas);
		mcoNascIndicacoes.setMcoForcipes(null);
		
		this.mcoNascIndicacoesDAO.persistir(mcoNascIndicacoes);
	}
	
	/**
	 * @MCOT_NAI_BRI
	 * 
	 * @param mcoNascIndicacoes
	 */
	public void preInsert(McoNascIndicacoes mcoNascIndicacoes) {
		mcoNascIndicacoes.setCriadoEm(new Date());		
		mcoNascIndicacoes.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
	}
}
