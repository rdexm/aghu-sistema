package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcHorarioTurnoCirgDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcProfAtuaUnidCirgsDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasAgendaVO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.PortalPlanejamentoCirurgiasTurno2VO;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.model.MbcBloqSalaCirurgica;
import br.gov.mec.aghu.model.MbcHorarioTurnoCirg;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class PortalPlanejamentoCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(PortalPlanejamentoCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcHorarioTurnoCirgDAO mbcHorarioTurnoCirgDAO;

	@Inject
	private MbcProfAtuaUnidCirgsDAO mbcProfAtuaUnidCirgsDAO;


	@EJB
	private IRegistroColaboradorFacade iRegistroColaboradorFacade;

	private static final long serialVersionUID = 6727355541065849900L;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	public MbcProfAtuaUnidCirgs popularFiltrosPorUsuarioLogado() throws ApplicationBusinessException {
		List<MbcProfAtuaUnidCirgs> equipes = null;
		MbcProfAtuaUnidCirgs equipe = new MbcProfAtuaUnidCirgs();
		
		equipes = getMbcProfAtuaUnidCirgsDAO().buscarEquipesPorUsuarioLogado(servidorLogadoFacade.obterServidorLogado());
		
		if (!equipes.isEmpty()) {
			equipe.setMbcProfAtuaUnidCirgs(equipes.get(0));
			equipe.setUnidadeFuncional(equipes.get(0).getUnidadeFuncional());
			equipe.setRapServidores(equipes.get(0).getRapServidores());
		} else {
			return null;
		}
					
		return equipe;
	}
	
	public Boolean isUltimoTurnoQueEquipePossuiReservaNoDia(MbcProfAtuaUnidCirgs equipe, PortalPlanejamentoCirurgiasTurno2VO turnoVO,
			Date data, Short espSeq, Short unfSeq, MbcSalaCirurgica sala, List<PortalPlanejamentoCirurgiasTurno2VO> listaTurnosVO,
			Integer agdSeq) {
		List<MbcHorarioTurnoCirg> horarioTurnoList = new ArrayList<MbcHorarioTurnoCirg>();
		MbcHorarioTurnoCirg cedInst = getMbcHorarioTurnoCirgDAO().buscarUltimoTurnoQueEquipePossuiCedenciaInstitucional(unfSeq, data, sala, equipe);
		MbcHorarioTurnoCirg cedEntreEquipe = getMbcHorarioTurnoCirgDAO().buscarUltimoTurnoQueEquipePossuiCedencia(unfSeq, data, sala, equipe);
		MbcHorarioTurnoCirg reserva = getMbcHorarioTurnoCirgDAO().buscarUltimoTurnoQueEquipePossuiReserva(unfSeq, data, sala, equipe, espSeq);
		if(cedInst != null) {
			horarioTurnoList.add(cedInst);
		}
		if(cedEntreEquipe != null) {
			horarioTurnoList.add(cedEntreEquipe);
		}
		if(reserva != null) {
			horarioTurnoList.add(reserva);
		}
		
		if(!horarioTurnoList.isEmpty()) {
			Collections.sort(horarioTurnoList, new Comparator<MbcHorarioTurnoCirg>() { 
				@Override
				public int compare(MbcHorarioTurnoCirg o1, MbcHorarioTurnoCirg o2) {      
					return DateUtil.truncaHorario(o2.getHorarioInicial()).compareTo(DateUtil.truncaHorario(o1.getHorarioInicial()));
				}
			});
			
			if(turnoVO.getSiglaTurno().equals(horarioTurnoList.get(0).getMbcTurnos().getTurno())) {
				for(PortalPlanejamentoCirurgiasTurno2VO item : listaTurnosVO) {
					if(!item.getListaAgendas().isEmpty()) {
						for(PortalPlanejamentoCirurgiasAgendaVO agd : item.getListaAgendas()) {
							if(agd.getSeqAgenda().equals(agdSeq)) {
								return Boolean.FALSE;
							}
						}
					}
				}
				return Boolean.TRUE;
			}
		}
		
		return Boolean.FALSE;
	}
	
	public Boolean verificarBloqueioSala(String turnoSala,DominioDiaSemanaSigla diaSigla, MbcProfAtuaUnidCirgs profAtua,List<MbcBloqSalaCirurgica> listaBloqueioSala ){
		for (MbcBloqSalaCirurgica mbcBloqSalaCirurgica : listaBloqueioSala) {
			if(mbcBloqSalaCirurgica.getTurno()!=null && ! mbcBloqSalaCirurgica.getTurno().getTurno().equals(turnoSala)){
				continue;
			}
			if(mbcBloqSalaCirurgica.getDiaSemana()!=null && ! mbcBloqSalaCirurgica.getDiaSemana().equals(diaSigla)){
				continue;
			}
			if(mbcBloqSalaCirurgica.getMbcProfAtuaUnidCirgs()!=null && ! mbcBloqSalaCirurgica.getMbcProfAtuaUnidCirgs().getId().equals(profAtua.getId())){
				continue;
			}
			return Boolean.TRUE;	
		}
		return Boolean.FALSE;
	}
	
	
	
	
	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return iRegistroColaboradorFacade;
	}
	
	protected MbcHorarioTurnoCirgDAO getMbcHorarioTurnoCirgDAO(){
		return mbcHorarioTurnoCirgDAO;
	}
	
	protected MbcProfAtuaUnidCirgsDAO getMbcProfAtuaUnidCirgsDAO() {
		return mbcProfAtuaUnidCirgsDAO;
	}
	
}