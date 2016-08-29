package br.gov.mec.aghu.blococirurgico.portalplanejamento.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.text.WordUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.blococirurgico.dao.MbcAgendasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcControleEscalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcSalaCirurgicaDAO;
import br.gov.mec.aghu.blococirurgico.portalplanejamento.vo.ResumoAgendaCirurgiaVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.dominio.DominioDiaSemanaSigla;
import br.gov.mec.aghu.dominio.DominioSituacaoAgendas;
import br.gov.mec.aghu.dominio.DominioTipoAgendaJustificativa;
import br.gov.mec.aghu.dominio.DominioTipoEscala;
import br.gov.mec.aghu.model.MbcAgendaJustificativa;
import br.gov.mec.aghu.model.MbcAgendaJustificativaId;
import br.gov.mec.aghu.model.MbcAgendas;
import br.gov.mec.aghu.model.MbcCaracteristicaSalaCirg;
import br.gov.mec.aghu.model.MbcControleEscalaCirurgica;
import br.gov.mec.aghu.model.MbcProfAtuaUnidCirgs;
import br.gov.mec.aghu.model.MbcSalaCirurgica;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

@Stateless
public class RemarcarPacienteAgendaON  extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(RemarcarPacienteAgendaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcAgendasDAO mbcAgendasDAO;

	@Inject
	private MbcSalaCirurgicaDAO mbcSalaCirurgicaDAO;

	@Inject
	private MbcControleEscalaCirurgicaDAO mbcControleEscalaCirurgicaDAO;


	@EJB
	private EscalaPortalPlanejamentoCirurgiaRN escalaPortalPlanejamentoCirurgiaRN;

	@EJB
	private RelatorioPortalPlanejamentoCirurgiasRN relatorioPortalPlanejamentoCirurgiasRN;

	@EJB
	private IBlocoCirurgicoFacade iBlocoCirurgicoFacade;

	@EJB
	private EscalaPortalPlanejamentoCirurgiaON escalaPortalPlanejamentoCirurgiaON;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4627147909746044951L;
	
	public enum RemarcarPacienteAgendaONExceptionCode implements BusinessExceptionCode {
		MBC_01016, MBC_01017, MBC_01019, MBC_01014, MBC_01014_1, MBC_01015, MBC_01018;
	}
	
	public void remarcarPacienteAgenda(Date dtReagendamento, MbcAgendas agenda, String justificativa, DominioTipoAgendaJustificativa dominio,
			MbcSalaCirurgica salaCirurgica) throws BaseException {
		if(!validarDataReagendamento(dtReagendamento, agenda.getProfAtuaUnidCirgs(),
				agenda.getEspecialidade().getSeq(), agenda.getUnidadeFuncional().getSeq())) {
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01016);
		}
		if(verificarEscalaDefinitivaFoiExecutada(dtReagendamento, agenda.getUnidadeFuncional().getSeq())) {
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01017);
		}
		//se agendamento veio da lista de agendas em escala
		if (agenda.getIndSituacao().equals(DominioSituacaoAgendas.ES)) {
			//@ORADB p_cancela_cirgs
			getEscalaPortalPlanejamentoCirurgiaRN().cancelaCirurgiasEletivas(agenda.getSeq());
			
			getEscalaPortalPlanejamentoCirurgiaON().verificaDiasConfigurados(agenda.getUnidadeFuncional().getSeq(), dtReagendamento);
		}
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		validarDataReagendamentoComDataAtual(dtReagendamento);
		//@ORADB p_volta_agenda
		agenda.setSalaCirurgica(salaCirurgica);
		agenda.setIndSituacao(DominioSituacaoAgendas.AG);
		agenda.setIndExclusao(false);
		
		agenda.setDtAgenda(dtReagendamento);
		getBlocoCirurgicoFacade().persistirAgenda(agenda,servidorLogado);
		flush();
		
		agenda.setDthrPrevInicio(null);
		agenda.setDthrPrevFim(null);
		agenda.setOrdemOverbooking(null);
		getBlocoCirurgicoFacade().gerarPrevisaoInicioFimCirurgiaOrdemOverbooking(agenda.getPucServidor(), 
				agenda.getProfAtuaUnidCirgs(), agenda.getEspecialidade(), agenda.getUnidadeFuncional(), agenda.getDtAgenda(), agenda,
				null, agenda.getSalaCirurgica().getId().getSeqp(), null, null, true);
				
		//Cria justificativa
		if (justificativa != null) {
			MbcAgendaJustificativaId id = new MbcAgendaJustificativaId();
			id.setAgdSeq(agenda.getSeq());
			MbcAgendaJustificativa agendaJustif = new MbcAgendaJustificativa();
			agendaJustif.setId(id);
			agendaJustif.setJustificativa(justificativa);
			agendaJustif.setTipo(dominio);
			getBlocoCirurgicoFacade().persistirMbcAgendaJustificativa(agendaJustif);
		}
	}
	
	/**
	 * Melhoria 29025 - ON8 @ORADB MBCF_AGENDA
	 * 
	 * @param dtAgendamento
	 * @param agenda
	 * @throws ApplicationBusinessException 
	 */
	public void validarInclusaoPacienteAgenda(Date dtAgendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq, Short salaSeqp, String descricaoTurno) throws ApplicationBusinessException {
		if(!validarDataReagendamento(dtAgendamento, prof, espSeq, unfSeq)) {
			if(validarDataReagendamentoCaracteristicaSala(dtAgendamento, prof, espSeq, unfSeq, salaSeqp, descricaoTurno)) {
				throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01014_1);
			}	
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01014);
		} else if(validarDataReagendamentoCaracteristicaSala(dtAgendamento, prof, espSeq, unfSeq, salaSeqp, descricaoTurno)) {
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01014_1);
		} else if(verificarEscalaDefinitivaFoiExecutada(dtAgendamento, unfSeq)) {
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01015);
		} else if(dtAgendamento.compareTo(DateUtil.truncaData(new Date())) <= 0) {
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01018);
		}
	}
	
	/**
	 * ON2 @ORADB mbcc_valida_dtagenda
	 * 
	 * @return true para data invalida e false para data valida
	 * 
	 */
	public Boolean validarDataReagendamento(Date dtReagendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq) {
		List<MbcSalaCirurgica> salas = getMbcSalaCirurgicaDAO().validarDataRemarcacaoAgendaEquipe(prof,
				espSeq, unfSeq, dtReagendamento);
		if(salas.size() == 0) {
			return false;
		}
		return true;
	}
	
	/**
	 * infelizmente não podemos unificar com o metodo 
	 * validarDataReagendamento, porque este é usado 
	 * em diverso lugares para proposito diferente
	 * 
	 * @param dtReagendamento
	 * @param prof
	 * @param espSeq
	 * @param unfSeq
	 * @param salaSeqp
	 * @return
	 */
	public Boolean validarDataReagendamentoCaracteristicaSala(Date dtReagendamento, MbcProfAtuaUnidCirgs prof, Short espSeq, Short unfSeq, Short salaSeqp, String descricaoTurno) {
		List<MbcSalaCirurgica> salas = getMbcSalaCirurgicaDAO().validarDataRemarcacaoAgendaEquipeCaracteristica(prof,
				espSeq, unfSeq, dtReagendamento, salaSeqp);
		if(salas.size() == 0) {
			return false;
		}else{
			return verificaCaracteristicaSalas(salas, dtReagendamento, descricaoTurno);
		}
	}
	
	
	private Boolean verificaCaracteristicaSalas(List<MbcSalaCirurgica> salas, Date dtReagendamento, String descricaoTurno){
		Calendar cal = Calendar.getInstance();
		cal.setTime(dtReagendamento);
		Integer diaSem = cal.get(Calendar.DAY_OF_WEEK);
		for (MbcSalaCirurgica mbcSalaCirurgica : salas) {
			Set<MbcCaracteristicaSalaCirg> caracs = mbcSalaCirurgica.getMbcCaracteristicaSalaCirgs();
			for (MbcCaracteristicaSalaCirg mbcCaracteristicaSalaCirg : caracs) {
				if(mbcCaracteristicaSalaCirg.getDiaSemana().toString().equals(DominioDiaSemanaSigla.getDiaSemanaSigla(diaSem).toString())){
					if(mbcCaracteristicaSalaCirg.getMbcTurnos().getDescricao().equals(descricaoTurno)){
						if (mbcCaracteristicaSalaCirg.getIndUrgencia() || mbcCaracteristicaSalaCirg.getCirurgiaParticular()){
							return true;
						}
					}
				}
			}
		}
		return false;
	}
	
	
	/**
	 * ON3 @ORADB mbcc_ver_escala_data
	 * 
	 * @return true para data invalida e false para data valida
	 * 
	 */
	public Boolean verificarEscalaDefinitivaFoiExecutada(Date dtReagendamento, Short unfSeq) {
		MbcControleEscalaCirurgica controleEscala = getMbcControleEscalaCirurgicaDAO().obterControleEscalaCirgPorUnidadeDataAgendaTruncadaTipoEscala(
				unfSeq, dtReagendamento, DominioTipoEscala.D);
		if(controleEscala != null) {
			return true;
		}
		return false;
	}
	
	public void validarDataReagendamentoComDataAtual(Date dtReagendamento) throws ApplicationBusinessException {
		Date dataAtual = DateUtil.truncaData(new Date());
		if(dtReagendamento.compareTo(dataAtual) <= 0) {
			throw new ApplicationBusinessException(RemarcarPacienteAgendaONExceptionCode.MBC_01019);
		}
	}
	
	//ON4 c_texto_calendar_agenda
	public String obterResumoAgendamento(Date dtAgenda, MbcAgendas agenda) {
		List<ResumoAgendaCirurgiaVO> resumos = getMbcAgendasDAO().pesquisarResumoAgendamentos(dtAgenda, agenda.getProfAtuaUnidCirgs(),
				agenda.getUnidadeFuncional().getSeq(), agenda.getEspecialidade().getSeq());
		Integer tempoTotalMinutos = 0;
		String texto = "";
		for(ResumoAgendaCirurgiaVO item : resumos) {
			tempoTotalMinutos += calcularTempoEmMinutos(item.getTempoSala());
			texto = texto.concat("*").concat(WordUtils.capitalizeFully(WordUtils.capitalizeFully(item.getPacNome())))
					.concat(" - ").concat(CoreUtil.capitalizaTextoFormatoAghu(item.getDescricaoProcedimento())).concat("\n");
		}
		
		return "Total de Horas: ".concat(calcularMinutosEmHoraMin(tempoTotalMinutos)).concat("\n").concat(texto);
	}
	
	private Integer calcularTempoEmMinutos(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal.get(Calendar.HOUR_OF_DAY)*60+cal.get(Calendar.MINUTE);
	}
	
	private String calcularMinutosEmHoraMin(Integer totalMinutos) {
		Integer hora = totalMinutos/60;
		Integer min = totalMinutos%60;
		
		return String.format("%02d:%02d", hora, min);
	}
	
	protected MbcSalaCirurgicaDAO getMbcSalaCirurgicaDAO(){
		return mbcSalaCirurgicaDAO;
	}
	
	protected MbcControleEscalaCirurgicaDAO getMbcControleEscalaCirurgicaDAO(){
		return mbcControleEscalaCirurgicaDAO;
	}
	
	protected MbcAgendasDAO getMbcAgendasDAO(){
		return mbcAgendasDAO;
	}
		
	protected RelatorioPortalPlanejamentoCirurgiasRN getPortalPlanejamentoCirurgiasRN(){
		return relatorioPortalPlanejamentoCirurgiasRN;
	}
	
	protected EscalaPortalPlanejamentoCirurgiaRN getEscalaPortalPlanejamentoCirurgiaRN(){
		return escalaPortalPlanejamentoCirurgiaRN;
	}
	
	protected EscalaPortalPlanejamentoCirurgiaON getEscalaPortalPlanejamentoCirurgiaON(){
		return escalaPortalPlanejamentoCirurgiaON;
	}
		
	protected IBlocoCirurgicoFacade getBlocoCirurgicoFacade() {
		return iBlocoCirurgicoFacade;
	}	
}
