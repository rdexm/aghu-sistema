package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoCesarianaJn;
import br.gov.mec.aghu.model.McoCesarianas;
import br.gov.mec.aghu.model.McoNascimentos;
import br.gov.mec.aghu.model.McoNascimentosId;
import br.gov.mec.aghu.perinatologia.dao.McoCesarianaJnDAO;
import br.gov.mec.aghu.perinatologia.dao.McoCesarianasDAO;
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
public class McoCesarianasRN extends BaseBusiness {

	private static final long serialVersionUID = -3423984755101821178L;

	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoNascimentosDAO mcoNascimentosDAO;
	
	@Inject
	private McoCesarianasDAO mcoCesarianasDAO;
	
	@Inject
	private McoCesarianaJnDAO mcoCesarianaJnDAO;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum McoCesarianasRNExceptionCode implements BusinessExceptionCode {
		ERRO_EXCLUSAO_CESARIANA
	}
	
	public void excluirMcoCesarianas(McoCesarianas mcoCesarianas, McoCesarianas mcoCesarianasOriginal) {
		
		Integer gsoPacCodigo = mcoCesarianas.getId().getGsoPacCodigo();
		Short gsoSeqp = mcoCesarianas.getId().getGsoSeqp();
		Integer pSeqp = mcoCesarianas.getId().getSeqp();
		
		McoCesarianas cesarianas = this.mcoCesarianasDAO.obterMcoCesarianas(gsoPacCodigo, gsoSeqp, pSeqp);
		
		this.inserirJournalMcoCesarianas(mcoCesarianasOriginal, DominioOperacoesJournal.DEL);
		
		this.mcoCesarianasDAO.remover(cesarianas);
	}
	
	public void excluirMcoCesarianasPorId(Integer gsoPacCodigo, Short gsoSeqp, Integer pSeqp) throws ApplicationBusinessException {
		if (this.mcoCesarianasDAO.verificaExisteCesariana(gsoPacCodigo, gsoSeqp, pSeqp)) {
			McoCesarianas cesarianas = this.mcoCesarianasDAO.obterMcoCesarianas(gsoPacCodigo, gsoSeqp, pSeqp);
			McoCesarianas cesarianasOriginal = this.mcoCesarianasDAO.obterOriginal(cesarianas);
			
			this.inserirJournalMcoCesarianas(cesarianasOriginal, DominioOperacoesJournal.DEL);
			
			try {
				McoNascimentosId id = new McoNascimentosId(gsoPacCodigo, gsoSeqp, pSeqp);
				McoNascimentos nascimento = this.mcoNascimentosDAO.obterPorChavePrimaria(id);
				nascimento.setMcoCesarianas(null);				
				this.mcoCesarianasDAO.remover(cesarianas);
				this.mcoNascimentosDAO.atualizar(nascimento);
				
			} catch(RuntimeException e) {
				throw new ApplicationBusinessException(McoCesarianasRNExceptionCode.ERRO_EXCLUSAO_CESARIANA);
			}
		}		
	}
	
	/**
	 * @ORADB MCO_CESARIANAS.MCOT_CSR_ARD
	 * @ORADB MCO_CESARIANAS.MCOT_CSR_ARU – trigger after update
	 * @param cesarianasOriginal
	 * @param operacao
	 */
	public void inserirJournalMcoCesarianas(McoCesarianas cesarianasOriginal, DominioOperacoesJournal operacao) {
		
		McoCesarianaJn mcoCesarianaJn = BaseJournalFactory.getBaseJournal(operacao, McoCesarianaJn.class, usuario.getLogin());
		
		mcoCesarianaJn.setCriadoEm(cesarianasOriginal.getCriadoEm());
		mcoCesarianaJn.setHrDuracao(cesarianasOriginal.getHrDuracao());
		mcoCesarianaJn.setContaminacao(cesarianasOriginal.getContaminacao());
		mcoCesarianaJn.setLaparotomia(cesarianasOriginal.getLaparotomia());
		
		mcoCesarianaJn.setIndLaqueaduraTubaria(cesarianasOriginal.getIndLaqueaduraTubaria());
		mcoCesarianaJn.setIndRafiaPeritonial(cesarianasOriginal.getIndRafiaPeritonial());
		mcoCesarianaJn.setIndLavagemCavidade(cesarianasOriginal.getIndLavagemCavidade());
		mcoCesarianaJn.setIndDrenos(cesarianasOriginal.getIndDrenos());
		mcoCesarianaJn.setNasGsoSeqp(cesarianasOriginal.getId().getGsoSeqp());
		mcoCesarianaJn.setNasGsoPacCodigo(cesarianasOriginal.getId().getGsoPacCodigo());
		mcoCesarianaJn.setNasSeqp(cesarianasOriginal.getId().getSeqp());
		mcoCesarianaJn.setDthrPrevInicio(cesarianasOriginal.getDthrPrevInicio());
		mcoCesarianaJn.setSciUnfSeq(cesarianasOriginal.getSciUnfSeq());
		mcoCesarianaJn.setSciSeqp(cesarianasOriginal.getSciSeqp());
		
		this.mcoCesarianaJnDAO.persistir(mcoCesarianaJn);
	}
	
	public McoCesarianas inserirMcoCesarianas(McoCesarianas mcoCesarianas, McoNascimentos mcoNascimentos,
			DadosNascimentoVO nascimentoSelecionado) {
		mcoCesarianas.setSciUnfSeq(mcoNascimentos.getSciUnfSeq());
		mcoCesarianas.setSciSeqp(mcoNascimentos.getSciSeqp());
		
		this.preInserirMcoCesarianas(mcoCesarianas);
		
		McoNascimentosId id = new McoNascimentosId();
		id.setGsoPacCodigo(nascimentoSelecionado.getGsoPacCodigo());
		id.setGsoSeqp(nascimentoSelecionado.getGsoSeqp());
		id.setSeqp(nascimentoSelecionado.getSeqp());
		
		mcoCesarianas.setId(id);
		
		this.mcoCesarianasDAO.persistir(mcoCesarianas);
		
		return mcoCesarianas;
	}
	
	/**
	 * @ORADB MCO_CESARIANAS.MCOT_CSR_BRI – trigger before insert
	 * @param mcoCesarianas
	 */
	public void preInserirMcoCesarianas(McoCesarianas mcoCesarianas) {
		mcoCesarianas.setCriadoEm(new Date());
	}
	
	public void atualizarMcoCesarianas(McoCesarianas mcoCesarianas, McoCesarianas mcoCesarianasOriginal,
			McoNascimentos mcoNascimentos) {
		
		mcoCesarianas.setSciUnfSeq(mcoNascimentos.getSciUnfSeq());
		mcoCesarianas.setSciSeqp(mcoNascimentos.getSciSeqp());
		
		if (this.isMcoCesarianaModificado(mcoCesarianas, mcoCesarianasOriginal)) {
			this.inserirJournalMcoCesarianas(mcoCesarianasOriginal, DominioOperacoesJournal.UPD);
		}
		this.mcoCesarianasDAO.atualizar(mcoCesarianas);
	}
	
	private boolean isMcoCesarianaModificado(McoCesarianas mcoCesarianas, McoCesarianas mcoCesarianasOriginal) {
		return CoreUtil.modificados(mcoCesarianas.getDthrIndicacao(), mcoCesarianasOriginal.getDthrIndicacao()) ||
				CoreUtil.modificados(mcoCesarianas.getDthrPrevInicio(), mcoCesarianasOriginal.getDthrPrevInicio()) ||
				CoreUtil.modificados(mcoCesarianas.getDthrIncisao(), mcoCesarianasOriginal.getDthrIncisao()) ||
				CoreUtil.modificados(mcoCesarianas.getHrDuracao(), mcoCesarianasOriginal.getHrDuracao()) ||
				CoreUtil.modificados(mcoCesarianas.getContaminacao(), mcoCesarianasOriginal.getContaminacao()) ||
				CoreUtil.modificados(mcoCesarianas.getLaparotomia(), mcoCesarianasOriginal.getLaparotomia()) ||
				CoreUtil.modificados(mcoCesarianas.getHisterotomia(), mcoCesarianasOriginal.getHisterotomia()) ||
				CoreUtil.modificados(mcoCesarianas.getHisterorrafia(), mcoCesarianasOriginal.getHisterorrafia()) ||
				CoreUtil.modificados(mcoCesarianas.getIndLaqueaduraTubaria(), mcoCesarianasOriginal.getIndLaqueaduraTubaria()) ||
				CoreUtil.modificados(mcoCesarianas.getIndRafiaPeritonial(), mcoCesarianasOriginal.getIndRafiaPeritonial()) ||
				CoreUtil.modificados(mcoCesarianas.getIndLavagemCavidade(), mcoCesarianasOriginal.getIndLavagemCavidade()) ||
				CoreUtil.modificados(mcoCesarianas.getIndDrenos(), mcoCesarianasOriginal.getIndDrenos()) ||
				CoreUtil.modificados(mcoCesarianas.getSciUnfSeq(), mcoCesarianasOriginal.getSciUnfSeq()) ||
				CoreUtil.modificados(mcoCesarianas.getSciSeqp(), mcoCesarianasOriginal.getSciSeqp());
				
	}
}
