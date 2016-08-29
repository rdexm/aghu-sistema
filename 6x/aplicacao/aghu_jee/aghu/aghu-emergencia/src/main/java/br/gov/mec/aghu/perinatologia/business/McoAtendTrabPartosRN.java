package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioDinamicaUterina;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoAtendTrabPartos;
import br.gov.mec.aghu.model.McoAtendTrabPartosJn;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoAtendTrabPartosJnDAO;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;


@Stateless
public class McoAtendTrabPartosRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1712613170642413556L;
	
	@Inject
	@QualificadorUsuario
	private Usuario usuario;

	
	@Inject
	private McoAtendTrabPartosDAO dao;
	@Inject
	private McoAtendTrabPartosJnDAO daoJn;
	
	private enum McoAtendTrabPartoRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00767, MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00519, MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00518, MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00517;
	}
	

	/**
	 * #32463 - RN01 
	 * @param atendTrabParto
	 * @throws ApplicationBusinessException 
	 */
	public void gravarAtendTrabParto(McoAtendTrabPartos atendTrabParto) throws ApplicationBusinessException {
		// valida regras para não encontrar divergencia no preenchimento
		validarDivergencias(atendTrabParto);
		// fluxo de inserção
		if (atendTrabParto.getId().getSeqp() == null) {
			// atualizar valores
			atendTrabParto	= antesPersistir(atendTrabParto);
			// obtem max gso_seqp
			atendTrabParto.getId().setSeqp(this.dao.obterMaxSeqpMcoAtendTrabParto(atendTrabParto.getId().getGsoSeqp(), atendTrabParto.getId().getGsoPacCodigo()));
			this.dao.persistir(atendTrabParto);
			// fluxo de atualizacao
		} else {
			McoAtendTrabPartos original = this.dao.obterOriginal(atendTrabParto.getId());
			this.dao.merge(atendTrabParto);
			// atualiza jornal
			if (atualizaJornal(original, atendTrabParto)) {
				insereJornal(original, DominioOperacoesJournal.UPD);
			}
		}
	}
	
	/**
	 * #32463 - RN03
	 * @ORADB MCO_ATEND_TRAB_PARTOS.MCOT_TBP_ARD
	 * @param atendTrabParto
	 */
	public void excluirAtendTrabParto(McoAtendTrabPartos atendTrabParto){
		McoAtendTrabPartos original = this.dao.obterOriginal(atendTrabParto.getId());
		this.dao.removerPorId(atendTrabParto.getId());
		insereJornal(original, DominioOperacoesJournal.DEL);
	}
	
	
	/**
	 * Insere jornal
	 * @param original
	 * @param operacao
	 */
	private void insereJornal(McoAtendTrabPartos original, DominioOperacoesJournal operacao){
		McoAtendTrabPartosJn  jn = BaseJournalFactory.getBaseJournal(operacao, McoAtendTrabPartosJn.class,  usuario.getLogin()); 		
		jn.setServidorMatricula(original.getSerMatricula());
		jn.setServidorVinCodigo(original.getSerVinCodigo());		
		jn.setCriadoEm(original.getCriadoEm());
		jn.setSeqp(original.getId().getSeqp());
		jn.setGsoSeqp(original.getId().getGsoSeqp());
		jn.setGsoPacCodigo(original.getId().getGsoPacCodigo());
		jn.setDataHoraAtendimento(original.getDthrAtend());
		jn.setBatimentoCardiacoFetal(original.getBatimentoCardiacoFetal());
		jn.setBatimentoCardiacoFetal2(original.getBatimentoCardiacoFetal2());
		jn.setBatimentoCardiacoFetal3(original.getBatimentoCardiacoFetal3());
		jn.setBatimentoCardiacoFetal4(original.getBatimentoCardiacoFetal4());
		jn.setBatimentoCardiacoFetal5(original.getBatimentoCardiacoFetal5());
		jn.setBatimentoCardiacoFetal6(original.getBatimentoCardiacoFetal6());
		jn.setBossa(original.getBossa());
		jn.setDilatacao(original.getDilatacao());
		
		jn.setDominioCardiotocografiaPartos(original.getCardiotocografia());
		jn.setDominioDinamicaUterina(original.getDinUterina());
		jn.setDominioIntensidadeDinamicaUterina(original.getIntensidadeDinUterina());
		jn.setDominioPosicaoAtendTrabParto(original.getPosicao());
		jn.setDominioPosicaoTrabalhoParto(original.getDominioPosicaoTrabalhoParto());
		jn.setIndicadorAnalgediaBpd(original.getIndicadorAnalgediaBpd());
		jn.setIndicadorAnalgediaBsd(original.getIndicadorAnalgediaBsd());
		jn.setPlanoDelee(original.getPlanoDelee());
		jn.setSemAceleracaoTransitoria(original.getSemAceleracaoTransitoria());
		jn.setVariabilidadeBatidaMenorQueDez(original.getVariabilidadeBatidaMenorQueDez());
		this.daoJn.persistir(jn);
	}
	/**
	 * #32463 -  RN04
	 * @ORADB MCO_ATEND_TRAB_PARTOS.MCOT_TBP_ARU
	 * @param original
	 * @param dominio
	 * @return
	 */
	private boolean atualizaJornal(McoAtendTrabPartos original, McoAtendTrabPartos atendTrabPartos){
			if (CoreUtil.modificados(original.getDthrAtend(), atendTrabPartos.getDthrAtend())
				||CoreUtil.modificados(original.getBatimentoCardiacoFetal(), atendTrabPartos.getBatimentoCardiacoFetal())
				||CoreUtil.modificados(original.getBatimentoCardiacoFetal2(), atendTrabPartos.getBatimentoCardiacoFetal2())
				||CoreUtil.modificados(original.getBatimentoCardiacoFetal3(), atendTrabPartos.getBatimentoCardiacoFetal3())
				||CoreUtil.modificados(original.getBatimentoCardiacoFetal4(), atendTrabPartos.getBatimentoCardiacoFetal4())
				||CoreUtil.modificados(original.getBatimentoCardiacoFetal5(), atendTrabPartos.getBatimentoCardiacoFetal5())
				||CoreUtil.modificados(original.getBatimentoCardiacoFetal6(), atendTrabPartos.getBatimentoCardiacoFetal6())
				||CoreUtil.modificados(original.getBossa(), atendTrabPartos.getBossa())
				||CoreUtil.modificados(original.getDilatacao(), atendTrabPartos.getDilatacao())
				||CoreUtil.modificados(original.getCardiotocografia(), atendTrabPartos.getCardiotocografia())
				||CoreUtil.modificados(original.getDinUterina(), atendTrabPartos.getDinUterina())
				||CoreUtil.modificados(original.getIntensidadeDinUterina(), atendTrabPartos.getIntensidadeDinUterina())
				||CoreUtil.modificados(original.getPosicao(), atendTrabPartos.getPosicao())
				||CoreUtil.modificados(original.getDominioPosicaoTrabalhoParto(), atendTrabPartos.getDominioPosicaoTrabalhoParto())
				||CoreUtil.modificados(original.getIndicadorAnalgediaBpd(), atendTrabPartos.getIndicadorAnalgediaBpd())
				||CoreUtil.modificados(original.getIndicadorAnalgediaBsd(), atendTrabPartos.getIndicadorAnalgediaBsd())
				||CoreUtil.modificados(original.getPlanoDelee(), atendTrabPartos.getPlanoDelee())
				||CoreUtil.modificados(original.getSemAceleracaoTransitoria(), atendTrabPartos.getSemAceleracaoTransitoria())
				||CoreUtil.modificados(original.getVariabilidadeBatidaMenorQueDez(), atendTrabPartos.getVariabilidadeBatidaMenorQueDez())){
				
				return true;
			}
		return false;
	}
	
	/**
	 * #32463 - RN02 
	 * @ORADB MCO_ATEND_TRAB_PARTOS.MCOT_TBP_BRI
	 */
	private McoAtendTrabPartos antesPersistir(McoAtendTrabPartos atendTrabPartos){
		atendTrabPartos.setCriadoEm(new Date());
		
		atendTrabPartos.setSerVinCodigo(usuario.getVinculo());
		atendTrabPartos.setSerMatricula(usuario.getMatricula());
		
		return atendTrabPartos;
	}
	
	
	/**
	 * #32463 - RN05 - Validar regras de divergencia
	 * @throws ApplicationBusinessException
	 */
	private void validarDivergencias(McoAtendTrabPartos atendTrabParto) throws ApplicationBusinessException{
		if (atendTrabParto.getDilatacao() == null && atendTrabParto.getIndicadorAnalgediaBsd() == false && atendTrabParto.getIndicadorAnalgediaBpd() == false) {
			throw new ApplicationBusinessException(McoAtendTrabPartoRNExceptionCode.MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00767);
		}
		
		if(validarErroBatimentoCardiacoFetal(atendTrabParto.getBatimentoCardiacoFetal())
		   || validarErroBatimentoCardiacoFetal(atendTrabParto.getBatimentoCardiacoFetal2())
		   || validarErroBatimentoCardiacoFetal(atendTrabParto.getBatimentoCardiacoFetal3())
		   || validarErroBatimentoCardiacoFetal(atendTrabParto.getBatimentoCardiacoFetal4())
		   || validarErroBatimentoCardiacoFetal(atendTrabParto.getBatimentoCardiacoFetal5())
		   || validarErroBatimentoCardiacoFetal(atendTrabParto.getBatimentoCardiacoFetal6())){
			throw new ApplicationBusinessException(McoAtendTrabPartoRNExceptionCode.MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00519);
		}
		
		if (!validarDilatacao(atendTrabParto)) {
			throw new ApplicationBusinessException(McoAtendTrabPartoRNExceptionCode.MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00517);
		}
		
		if (!validarDinamicaUterina(atendTrabParto)) {
			throw new ApplicationBusinessException(McoAtendTrabPartoRNExceptionCode.MENSAGEM_CRIACAO_PARTOGRAMA_MCO_00518);
			
		}
	}
	
	/**
	 * Valida se o batimento cardiaco não está divergente
	 * @param batimentoCardiaco
	 * @return
	 */
	private boolean validarErroBatimentoCardiacoFetal(Short batimentoCardiaco){
		if (batimentoCardiaco == null || (batimentoCardiaco >= 1 && batimentoCardiaco <= 200)) {
			return false;
		}
		return true;
	}
	
	/**
	 * Valida se a dilatacao não está divergente
	 * @return
	 */
	private boolean validarDilatacao(McoAtendTrabPartos atendTrabParto){
		if (atendTrabParto.getDilatacao() == null || (atendTrabParto.getDilatacao() >= 1 && atendTrabParto.getDilatacao() <= 10)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Valida se a dinamica uterina não está divergente
	 * @return
	 */
	private boolean validarDinamicaUterina(McoAtendTrabPartos atendTrabParto){
		if ((atendTrabParto.getDinUterina() == null && atendTrabParto.getIntensidadeDinUterina() == null)
			|| (atendTrabParto.getDinUterina() == DominioDinamicaUterina.ZERO && atendTrabParto.getIntensidadeDinUterina() == null)
			|| (atendTrabParto.getDinUterina() != null && atendTrabParto.getDinUterina() != DominioDinamicaUterina.ZERO && atendTrabParto.getIntensidadeDinUterina() != null)){
			
			return true;	 
		}
		return false;
	}
	
	@Override
	protected Log getLogger() {
		// TODO Auto-generated method stub
		return null;
	}

}
