package br.gov.mec.aghu.blococirurgico.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaOrtProteseDAO;
import br.gov.mec.aghu.model.MbcAgendaOrtProtese;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 *  Classe responsável pelas regras de FORMS para #22446
 *  @author rpanassolo
 */
@Stateless
public class MbcAgendaOrtProteseON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcAgendaOrtProteseON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendaOrtProteseDAO mbcAgendaOrtProteseDAO;


	@EJB
	private MbcAgendaOrtProteseRN mbcAgendaOrtProteseRN;

	private static final long serialVersionUID = 4918335814295466329L;

	public enum MbcAgendaOrteseproteseONExceptionCode implements BusinessExceptionCode {
		MBC_00809_1, AGENDA_ORTPROT_QTDE_INVALIDA
	}

	/**
	 * ON0
	 * valida se já foi adicionado uma ortese protese com o mesmo material
	 * @throws ApplicationBusinessException
	 */
	
	public void validarAgendaOrteseproteseAdicionadoExistente(List<MbcAgendaOrtProtese> agendaOrteseProteseList,MbcAgendaOrtProtese agendaOrteseProtese)throws ApplicationBusinessException {
		for (MbcAgendaOrtProtese mbcAgendaOrteseProtese : agendaOrteseProteseList) {
			if(agendaOrteseProtese.getScoMaterial().getCodigo().equals(mbcAgendaOrteseProtese.getScoMaterial().getCodigo())){
				throw new ApplicationBusinessException(MbcAgendaOrteseproteseONExceptionCode.MBC_00809_1);
			}
		}
	}
		
	/**
	 * ON1
	 * Qtd deve existir e ser maior que zero 
	 * @throws ApplicationBusinessException
	 *  
	 */	
	public void validarQtdeOrtProtese(MbcAgendaOrtProtese agendaOrtProtese)throws ApplicationBusinessException {
		if(agendaOrtProtese.getQtde()== null || agendaOrtProtese.getQtde()==0 ){
			throw new ApplicationBusinessException(MbcAgendaOrteseproteseONExceptionCode.AGENDA_ORTPROT_QTDE_INVALIDA,agendaOrtProtese.getScoMaterial().getNome());	
		}
		
	}
	
	
	public void gravarAgendaOrtProtese(MbcAgendas agendaOriginal,final List<MbcAgendaOrtProtese> agendaOrtProteses, final List<MbcAgendaOrtProtese> agendaOrtProtesesRemovidas,
			final MbcAgendas agenda) throws BaseException {
		
		if(agendaOrtProtesesRemovidas != null && !agendaOrtProtesesRemovidas.isEmpty()) {
			for (MbcAgendaOrtProtese agendaOrtProtese : agendaOrtProtesesRemovidas) {
				agendaOrtProtese = getMbcAgendaOrtProteseDAO().obterPorChavePrimaria(agendaOrtProtese.getId());
				this.getMbcAgendaOrtProteseRN().removerAgendasOrteseProtese(agendaOrtProtese);
			}
		}

		if(agendaOrtProteses != null && ! agendaOrtProteses.isEmpty()){
			for (final MbcAgendaOrtProtese agendaOrtProtese : agendaOrtProteses ) {
				agendaOrtProtese.setMbcAgendas(agenda);
				this.validarQtdeOrtProtese(agendaOrtProtese);
				MbcAgendaOrtProtese oldAgendaOrtProtese = null;
				if (agendaOriginal != null && agendaOriginal.getAgendasOrtProteses() != null && agendaOriginal.getAgendasOrtProteses().contains(agendaOrtProtese)) { //alteracao
					oldAgendaOrtProtese = agendaOriginal.getAgendasOrtProteses().get(agendaOriginal.getAgendasOrtProteses().indexOf((agendaOrtProtese)));
					if(CoreUtil.modificados(agendaOrtProtese.getQtde(), oldAgendaOrtProtese.getQtde())){
						//reatacha
						MbcAgendaOrtProtese orteseProtese = getMbcAgendaOrtProteseDAO().obterOriginal(agendaOrtProtese);
						orteseProtese.setQtde(agendaOrtProtese.getQtde());
						orteseProtese.setScoMaterial(agendaOrtProtese.getScoMaterial());
						this.getMbcAgendaOrtProteseRN().alterarAgendasOrteseProtese(oldAgendaOrtProtese, agendaOrtProtese);
					}
				} else { //inclusao
					getMbcAgendaOrtProteseDAO().desatachar(agendaOrtProtese);
					agendaOrtProtese.setId(null);
					this.getMbcAgendaOrtProteseRN().inserirAgendasOrteseProtese(agendaOrtProtese);
				}
			}
		}
	}
	
	public MbcAgendaOrtProteseRN getMbcAgendaOrtProteseRN(){
		return mbcAgendaOrtProteseRN;
	}
	
	protected MbcAgendaOrtProteseDAO getMbcAgendaOrtProteseDAO() {
		return mbcAgendaOrtProteseDAO;
	}
	
}
