package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoForcipesJn;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.perinatologia.dao.McoForcipesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoForcipesJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoNascimentosDAO;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoVO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
/**
 * @author israel.haas
 */
@Stateless
public class McoForcipesRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoForcipesDAO mcoForcipesDAO;
	
	@Inject
	private McoForcipesJnDAO mcoForcipesJnDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum McoForcipesRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUSAO_INSTRUMENTADO
	}
	
	public McoForcipes inserirMcoForcipes(McoForcipes mcoForcipes, DadosNascimentoVO nascimentoSelecionado) {
		this.preInserirMcoForcipes(mcoForcipes);
		
		McoNascimentosId id = new McoNascimentosId();
		id.setGsoPacCodigo(nascimentoSelecionado.getGsoPacCodigo());
		id.setGsoSeqp(nascimentoSelecionado.getGsoSeqp());
		id.setSeqp(nascimentoSelecionado.getSeqp());
		
		mcoForcipes.setId(id);
		
		this.mcoForcipesDAO.persistir(mcoForcipes);
		
		return mcoForcipes;
	}
	
	/**
	 * @ORADB MCO_FORCIPES.MCOT_FCP_BRI – trigger before insert
	 */
	public void preInserirMcoForcipes(McoForcipes mcoForcipes) {
		mcoForcipes.setCriadoEm(new Date());
	}
	
	public void excluirMcoForcipes(McoForcipes mcoForcipes, McoForcipes mcoForcipesOriginal) {
		
		Integer gsoPacCodigo = mcoForcipes.getId().getGsoPacCodigo();
		Short gsoSeqp = mcoForcipes.getId().getGsoSeqp();
		Integer pSeqp = mcoForcipes.getId().getSeqp();
		
		McoForcipes forcipes = this.mcoForcipesDAO.obterMcoForcipes(gsoPacCodigo, gsoSeqp, pSeqp);
		
		this.inserirJournalMcoForcipes(mcoForcipesOriginal, DominioOperacoesJournal.DEL);
		
		this.mcoForcipesDAO.remover(forcipes);
	}
	
	public void atualizarMcoForcipes(McoForcipes mcoForcipes, McoForcipes mcoForcipesOriginal) {
		
		if (this.isMcoForcipesModificado(mcoForcipes, mcoForcipesOriginal)) {
			this.inserirJournalMcoForcipes(mcoForcipesOriginal, DominioOperacoesJournal.UPD);
		}
		this.mcoForcipesDAO.atualizar(mcoForcipes);
	}
	
	public void excluirMcoForcipesPorId(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) throws ApplicationBusinessException {
		if (this.mcoForcipesDAO.verificaExisteForcipe(gsoPacCodigo, gsoSeqp, pSeqp)) {
			McoForcipes forcipes = this.mcoForcipesDAO.obterMcoForcipes(gsoPacCodigo, gsoSeqp, pSeqp);
			McoForcipes forcipesOriginal = this.mcoForcipesDAO.obterOriginal(forcipes);
			
			this.inserirJournalMcoForcipes(forcipesOriginal, DominioOperacoesJournal.DEL);
			
			try {
				McoNascimentosId id = new McoNascimentosId(gsoPacCodigo, gsoSeqp, pSeqp);
				McoNascimentos nascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);
				nascimento.setMcoForcipes(null);				
				this.mcoForcipesDAO.remover(forcipes);
				this.mcoNascimentosDAO.atualizar(nascimento);
				
			} catch(RuntimeException e) {
				throw new ApplicationBusinessException(McoForcipesRNExceptionCode.ERRO_EXCLUSAO_INSTRUMENTADO);
			}
		}
//		McoNascimentosId id = new McoNascimentosId(gsoPacCodigo, gsoSeqp, pSeqp);
//		McoForcipes forcipes = this.mcoForcipesDAO.obterOriginal(id);
//		if (forcipes != null) {
//			this.inserirJournalMcoForcipes(forcipes, DominioOperacoesJournal.DEL);
//			try {
//				McoNascimentos nascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);
//				nascimento.setMcoForcipes(null);				
//				this.mcoNascimentosDAO.atualizar(nascimento);
//			} catch(RuntimeException e) {
//				throw new ApplicationBusinessException(McoForcipesRNExceptionCode.ERRO_EXCLUSAO_INSTRUMENTADO);
//			}
//		}
	}
	
	/**
	 * @ORADB MCO_FORCIPES.MCOT_FCP_ARD
	 * @ORADB MCO_FORCIPES.MCOT_FCP_BRI – trigger after update
	 * @param forcipesOriginal
	 * @param operacao
	 */
	public void inserirJournalMcoForcipes(McoForcipes forcipesOriginal, DominioOperacoesJournal operacao) {
		
		McoForcipesJn mcoForcipesJn = BaseJournalFactory.getBaseJournal(operacao, McoForcipesJn.class, usuario.getLogin());
		
		mcoForcipesJn.setCriadoEm(forcipesOriginal.getCriadoEm());
		mcoForcipesJn.setNasGsoSeqp(forcipesOriginal.getId().getGsoSeqp());
		mcoForcipesJn.setNasGsoPacCodigo(forcipesOriginal.getId().getGsoPacCodigo());
		mcoForcipesJn.setNasSeqp(forcipesOriginal.getId().getSeqp());
		mcoForcipesJn.setTipoForcipe(forcipesOriginal.getTipoForcipe());
		mcoForcipesJn.setTamanhoForcipe(forcipesOriginal.getTamanhoForcipe());
		mcoForcipesJn.setIndForcipeComRotacao(forcipesOriginal.getIndForcipeComRotacao());
		
		this.mcoForcipesJnDAO.persistir(mcoForcipesJn);
	}
	
	private boolean isMcoForcipesModificado(McoForcipes mcoForcipes, McoForcipes mcoForcipesOriginal) {
		return CoreUtil.modificados(mcoForcipes.getTipoForcipe(), mcoForcipesOriginal.getTipoForcipe()) ||
				CoreUtil.modificados(mcoForcipes.getTamanhoForcipe(), mcoForcipesOriginal.getTamanhoForcipe()) ||
				CoreUtil.modificados(mcoForcipes.getIndForcipeComRotacao(), mcoForcipesOriginal.getIndForcipeComRotacao());
	}
}
