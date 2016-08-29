package br.gov.mec.aghu.blococirurgico.business;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcAgendaHemoterapiaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcCompSangProcCirgDAO;
import br.gov.mec.aghu.business.bancosangue.IBancoDeSangueFacade;
import br.gov.mec.aghu.model.MbcAgendaHemoterapia;
import br.gov.mec.aghu.model.MbcAgendaHemoterapiaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCompSangProcCirg;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MbcAgendaHemoterapiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcAgendaHemoterapiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcCompSangProcCirgDAO mbcCompSangProcCirgDAO;

	@Inject
	private MbcAgendaHemoterapiaDAO mbcAgendaHemoterapiaDAO;


	@EJB
	private IBancoDeSangueFacade iBancoDeSangueFacade;

	@EJB
	private MbcAgendaHemoterapiaRN mbcAgendaHemoterapiaRN;


	private static final long	serialVersionUID	= -2677483108760175357L;

	public enum MbcAgendaHemoterapiaONExceptionCode implements BusinessExceptionCode {
		MBC_00567
	}
	
	
	public Set<MbcAgendaHemoterapia> buscarAgendaHemoterapia(final Integer pciSeq, final Short espSeq) {
		final List<MbcCompSangProcCirg> componentes = this.getMbcCompSangProcCirgDAO().buscarMbcCompSangProcCirg(pciSeq, espSeq);
		boolean especialidade = false;
		
		if (componentes == null || componentes.isEmpty()) {
			return new HashSet<MbcAgendaHemoterapia>(0);
		}
		
		final Set<MbcAgendaHemoterapia> retorno = new HashSet<MbcAgendaHemoterapia>();
		for (MbcCompSangProcCirg componenteSanguineo : componentes) {			
			if ((componenteSanguineo.getAghEspecialidades() == null && especialidade == false)) {
				retorno.add(popularAgendaHemoterapia(componenteSanguineo));
			} else if (componenteSanguineo.getAghEspecialidades() != null) {
				especialidade = true;
				retorno.add(popularAgendaHemoterapia(componenteSanguineo));
			}
		}
		
		return retorno;
	}

	public MbcAgendaHemoterapia popularAgendaHemoterapia(MbcCompSangProcCirg componenteSanguineo) {
		MbcAgendaHemoterapia agdHemoterapia = new MbcAgendaHemoterapia();
		
		agdHemoterapia.setAbsComponenteSanguineo(this.getBancoDeSangueFacade().obterComponeteSanguineoPorCodigo(
				componenteSanguineo.getAbsComponenteSanguineo().getCodigo()));
		agdHemoterapia.setQtdeMl(componenteSanguineo.getQtdeMl());
		agdHemoterapia.setQtdeUnidade(componenteSanguineo.getQtdeUnidade());
		
		return agdHemoterapia;
	}
	
	
	/**
	 * ON4
	 * @ORADB MBC_AGH_CK4 
	 * @param agendaHemoterapias
	 * @throws BaseException
	 */
	public void validarQtde(final Set<MbcAgendaHemoterapia> agendaHemoterapias)throws BaseException{
		if(agendaHemoterapias != null && ! agendaHemoterapias.isEmpty()){
			for (final MbcAgendaHemoterapia mbcAgendaHemoterapia : agendaHemoterapias ) {
				if((mbcAgendaHemoterapia.getQtdeMl() == null || mbcAgendaHemoterapia.getQtdeMl() == 0) && (mbcAgendaHemoterapia.getQtdeUnidade() == null || mbcAgendaHemoterapia.getQtdeUnidade() == 0)
						|| (mbcAgendaHemoterapia.getQtdeMl() != null && mbcAgendaHemoterapia.getQtdeMl() > 0 && mbcAgendaHemoterapia.getQtdeUnidade() != null && mbcAgendaHemoterapia.getQtdeUnidade() > 0 ) ) {
					throw new ApplicationBusinessException(MbcAgendaHemoterapiaONExceptionCode.MBC_00567);
				}
			}
		}
	}


	public void gravarAgendaHemoterapia(MbcAgendas agendaOriginal, final Set<MbcAgendaHemoterapia> agendaHemoterapias, final List<MbcAgendaHemoterapia> agendaHemoterapiasRemovidas,
			final MbcAgendas agenda) throws BaseException {
		
		//ON4
		this.validarQtde(agendaHemoterapias);
		
		if(agendaHemoterapiasRemovidas != null && !agendaHemoterapiasRemovidas.isEmpty()) {
			for (MbcAgendaHemoterapia mbcAgendaHemoterapia : agendaHemoterapiasRemovidas) {
				mbcAgendaHemoterapia = getMbcAgendaHemoterapiaDAO().obterPorChavePrimaria(mbcAgendaHemoterapia.getId());
				this.getMbcAgendaHemoterapiaRN().excluirAgendaHemoterapia(mbcAgendaHemoterapia);
			}
		}
		if(agendaHemoterapias != null && !agendaHemoterapias.isEmpty()){
			for (MbcAgendaHemoterapia mbcAgendaHemoterapia : agendaHemoterapias) {
				mbcAgendaHemoterapia.setMbcAgendas(agenda);
				mbcAgendaHemoterapia.setIndFiltrado(false);
				mbcAgendaHemoterapia.setIndIrradiado(false);
				mbcAgendaHemoterapia.setIndLavado(false);
//				MbcAgendaHemoterapia oldAgendaHemo = null;
//				if ((agendaHemoterapiasRemovidas == null || agendaHemoterapiasRemovidas.isEmpty()) && 
//						agendaOriginal != null && agendaOriginal.getAgendasHemoterapias() != null &&
//						agendaOriginal.getAgendasHemoterapias().contains(mbcAgendaHemoterapia)) {
//					oldAgendaHemo = agendaOriginal.getAgendasHemoterapias().get(agendaOriginal.getAgendasHemoterapias().indexOf((mbcAgendaHemoterapia)));
//					if(CoreUtil.modificados(mbcAgendaHemoterapia.getQtdeUnidadeAdic(), oldAgendaHemo.getQtdeUnidadeAdic())) {
//						Short qtdeUnidadeAdic = mbcAgendaHemoterapia.getQtdeUnidadeAdic();
//						mbcAgendaHemoterapia = getMbcAgendaHemoterapiaDAO().obterOriginal(mbcAgendaHemoterapia.getId()); //Reatacha objeto caso esteja desatachado
//						mbcAgendaHemoterapia.setQtdeUnidadeAdic(qtdeUnidadeAdic);
//					}
//				} else {
//					getMbcAgendaHemoterapiaDAO().desatachar(mbcAgendaHemoterapia);
//					mbcAgendaHemoterapia.setId(null);
//				}
				
				if (mbcAgendaHemoterapia.getId() == null) {
					MbcAgendaHemoterapiaId id = new MbcAgendaHemoterapiaId();
					id.setAgdSeq(mbcAgendaHemoterapia.getMbcAgendas().getSeq());
					id.setCsaCodigo(mbcAgendaHemoterapia.getAbsComponenteSanguineo().getCodigo());
					mbcAgendaHemoterapia.setId(id);
				}
				
				this.getMbcAgendaHemoterapiaRN().persistirAgendaHemoterapia(mbcAgendaHemoterapia);
			}
		}
	}
	
	protected MbcAgendaHemoterapiaDAO getMbcAgendaHemoterapiaDAO() {
		return mbcAgendaHemoterapiaDAO;
	}

	protected MbcCompSangProcCirgDAO getMbcCompSangProcCirgDAO() {
		return mbcCompSangProcCirgDAO;
	}
	
	protected IBancoDeSangueFacade getBancoDeSangueFacade() {
		return iBancoDeSangueFacade;
	}

	protected MbcAgendaHemoterapiaRN getMbcAgendaHemoterapiaRN() {
		return mbcAgendaHemoterapiaRN;
	}
	
}
