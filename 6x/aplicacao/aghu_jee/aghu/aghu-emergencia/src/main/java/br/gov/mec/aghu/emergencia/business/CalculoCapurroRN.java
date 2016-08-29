package br.gov.mec.aghu.emergencia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.emergencia.vo.CalculoCapurroVO;
import br.gov.mec.aghu.model.McoIddGestCapurros;
import br.gov.mec.aghu.model.McoIddGestCapurrosId;
import br.gov.mec.aghu.model.McoIddGestCapurrosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestCapurrosDAO;
import br.gov.mec.aghu.perinatologia.dao.McoIddGestCapurrosJnDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@Stateless
public class CalculoCapurroRN extends BaseBusiness {

	private static final long serialVersionUID = 1903217821051514576L;

	@Inject
	private McoIddGestCapurrosDAO mcoIddGestCapurrosDAO;
	
	@Inject
	private McoIddGestCapurrosJnDAO mcoIddGestCapurrosJnDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	private enum CalculoCapurroRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_RANGE_VALORES_DIAS_CALCULO_CAPURRO, MENSAGEM_CALCULO_CAPURRO_EXISTENTE
	}
	
	public void persistirMcoIddGestCapurros(CalculoCapurroVO calculoCapurroVO) throws ApplicationBusinessException {
		
		if (calculoCapurroVO.getIgDias() < -99 || calculoCapurroVO.getIgDias() > 99) {
			throw new ApplicationBusinessException(CalculoCapurroRNExceptionCode.MENSAGEM_RANGE_VALORES_DIAS_CALCULO_CAPURRO);
		}
		
		// Se possui elaborador, é edição
		if (calculoCapurroVO.getElaborador() != null) {
			McoIddGestCapurrosId id = new McoIddGestCapurrosId(calculoCapurroVO.getPacCodigo(),
					calculoCapurroVO.getSerMatricula(), calculoCapurroVO.getSerVinCodigo());
			McoIddGestCapurros iddGestCapurros = this.mcoIddGestCapurrosDAO.obterPorChavePrimaria(id);
			
			McoIddGestCapurros original = this.mcoIddGestCapurrosDAO.obterOriginal(id);
			
			iddGestCapurros.setAlteradoEm(new Date());
			this.copiarValores(calculoCapurroVO, iddGestCapurros);
			
			inserirJournalMcoIddGestCapurros(original, DominioOperacoesJournal.UPD);
			
			this.mcoIddGestCapurrosDAO.merge(iddGestCapurros);
			
		} else {
			RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
			
			McoIddGestCapurros iddGestCapurros = new McoIddGestCapurros();
			McoIddGestCapurrosId id = new McoIddGestCapurrosId(calculoCapurroVO.getPacCodigo(),
					servidorLogado.getId().getMatricula(), servidorLogado.getId().getVinCodigo());
			
			McoIddGestCapurros calculoCapurro = this.mcoIddGestCapurrosDAO.obterPorChavePrimaria(id);
			
			if (calculoCapurro != null) {
				throw new ApplicationBusinessException(CalculoCapurroRNExceptionCode.MENSAGEM_CALCULO_CAPURRO_EXISTENTE);
			}
			
			iddGestCapurros.setId(id);
			this.copiarValores(calculoCapurroVO, iddGestCapurros);
			iddGestCapurros.setCriadoEm(new Date());
			
			inserirJournalMcoIddGestCapurros(iddGestCapurros, DominioOperacoesJournal.INS);
			
			this.mcoIddGestCapurrosDAO.persistir(iddGestCapurros);
		}
	}
	
	public void excluirMcoIddGestCapurros(CalculoCapurroVO calculoCapurroVO) {
		McoIddGestCapurrosId id = new McoIddGestCapurrosId(calculoCapurroVO.getPacCodigo(),
				calculoCapurroVO.getSerMatricula(), calculoCapurroVO.getSerVinCodigo());
		McoIddGestCapurros iddGestCapurros = this.mcoIddGestCapurrosDAO.obterPorChavePrimaria(id);
		
		inserirJournalMcoIddGestCapurros(iddGestCapurros, DominioOperacoesJournal.DEL);
		
		this.mcoIddGestCapurrosDAO.remover(iddGestCapurros);
	}

	private void copiarValores(CalculoCapurroVO calculoCapurroVO, McoIddGestCapurros iddGestCapurros) {
		iddGestCapurros.setTexturaPele(calculoCapurroVO.getTexturaPele());
		iddGestCapurros.setFormaOrelha(calculoCapurroVO.getFormaOrelha());
		iddGestCapurros.setGlandulaMamaria(calculoCapurroVO.getGlandulaMamaria());
		iddGestCapurros.setPregasPlantares(calculoCapurroVO.getPregasPlantares());
		iddGestCapurros.setFormacaoMamilo(calculoCapurroVO.getFormacaoMamilo());
		iddGestCapurros.setIgSemanas(calculoCapurroVO.getIgSemanas());
		iddGestCapurros.setIgDias(calculoCapurroVO.getIgDias());
	}
	
	public void inserirJournalMcoIddGestCapurros(McoIddGestCapurros original, DominioOperacoesJournal operacao) {
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		final McoIddGestCapurrosJn jn = BaseJournalFactory.getBaseJournal(operacao, McoIddGestCapurrosJn.class, servidorLogado.getUsuario());
		
		jn.setTexturaPele(original.getTexturaPele());
		jn.setPregasPlantares(original.getPregasPlantares());
		jn.setFormacaoMamilo(original.getFormacaoMamilo());
		jn.setFormaOrelha(original.getFormaOrelha());
		jn.setGlandulaMamaria(original.getGlandulaMamaria());
		jn.setCriadoEm(original.getCriadoEm());
		jn.setAlteradoEm(original.getAlteradoEm());
		jn.setIgSemanas(original.getIgSemanas());
		jn.setIgDias(original.getIgDias());
		jn.setPacCodigo(original.getId().getPacCodigo());
		jn.setSerMatricula(original.getId().getSerMatricula());
		jn.setSerVinCodigo(original.getId().getSerVinCodigo());
		
		this.mcoIddGestCapurrosJnDAO.persistir(jn);
	}

}
