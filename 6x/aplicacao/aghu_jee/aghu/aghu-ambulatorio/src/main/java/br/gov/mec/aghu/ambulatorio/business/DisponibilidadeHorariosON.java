package br.gov.mec.aghu.ambulatorio.business;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacGradeAgendamenConsultasDAO;
import br.gov.mec.aghu.ambulatorio.vo.ConsultaDisponibilidadeHorarioVO;
import br.gov.mec.aghu.ambulatorio.vo.DataInicioFimVO;
import br.gov.mec.aghu.ambulatorio.vo.DisponibilidadeHorariosVO;
import br.gov.mec.aghu.ambulatorio.vo.VAacSiglaUnfSalaVO;
import br.gov.mec.aghu.dominio.DominioTurno;
import br.gov.mec.aghu.model.AacCondicaoAtendimento;
import br.gov.mec.aghu.model.AacGradeAgendamenConsultas;
import br.gov.mec.aghu.model.AacPagador;
import br.gov.mec.aghu.model.AacTipoAgendamento;
import br.gov.mec.aghu.model.AghEquipes;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.VAacSiglaUnfSala;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioDiaSemana;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.DateValidator;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class DisponibilidadeHorariosON extends BaseBusiness {

    private static final Log LOG = LogFactory.getLog(DisponibilidadeHorariosON.class);

    @Override
    @Deprecated
    protected Log getLogger() {
	return LOG;
    }

    @Inject
    private AacConsultasDAO aacConsultasDAO;

    @EJB
    private IParametroFacade parametroFacade;

    @Inject
    private AacGradeAgendamenConsultasDAO aacGradeAgendamenConsultasDAO;

    /**
	 * 
	 */
    private static final long serialVersionUID = -9173300246606412704L;

    public enum DisponilidadeHorariosONExceptionCode implements BusinessExceptionCode {
	AAC_00226, AAC_00280, AAC_00281, ERRO_PESQUISA_POR_HORARIO_DISPONIBILIDADE, ERRO_FILTROS_OBRIGATORIOS_DISPONIBILIDADE_HORARIO
    }

    class DisponibilidadeHorariosComparator implements Comparator<DisponibilidadeHorariosVO> {

	@Override
	public int compare(DisponibilidadeHorariosVO a1, DisponibilidadeHorariosVO a2) {
	    try {
		int compCons = a1.getConsultasLiberadas().compareTo(a2.getConsultasLiberadas());
		return compCons;
	    } catch (EntityNotFoundException enfe) {
		return -1;
	    }
	}
    }

    private List<DisponibilidadeHorariosVO> ordenarDisponiblidadeEmergencia(List<DisponibilidadeHorariosVO> lista) {
	class OrdenacaoDisponibilidade implements Comparator<DisponibilidadeHorariosVO> {

	    @Override
	    public int compare(DisponibilidadeHorariosVO o1, DisponibilidadeHorariosVO o2) {
		if (o1.getEspecialidade().compareToIgnoreCase(o2.getEspecialidade()) != 0) {
		    return o1.getEspecialidade().compareToIgnoreCase(o2.getEspecialidade());
		} else if (o1.getEquipe() != null && o2.getEquipe() != null) {
		    return o1.getEquipe().compareToIgnoreCase(o2.getEquipe());
		} else {
		    return 0;
		}
	    }
	}
	List<DisponibilidadeHorariosVO> listaDisponibilidades = new ArrayList<DisponibilidadeHorariosVO>();
	if (lista != null) {
	    listaDisponibilidades.addAll(lista);
	}
	Collections.sort(listaDisponibilidades, new OrdenacaoDisponibilidade());
	return listaDisponibilidades;
    }

    public void validarPesquisaDisponibilidadeHorarios(Integer seq, AghEspecialidades especialidade, Date horaConsulta, Date dtConsulta,
	    Date mesInicio, Date mesFim, DominioDiaSemana dia, VAacSiglaUnfSalaVO zona, DominioTurno turno) throws ApplicationBusinessException {
		if (seq == null && (especialidade == null || especialidade.getSeq() == null) &&
				(zona == null || zona.getUnfSeq() == null || turno == null || (dia == null && dtConsulta == null))) {
		    throw new ApplicationBusinessException(DisponilidadeHorariosONExceptionCode.ERRO_FILTROS_OBRIGATORIOS_DISPONIBILIDADE_HORARIO);
		}
		if (dtConsulta != null && (mesInicio != null || mesFim != null)) {
		    throw new ApplicationBusinessException(DisponilidadeHorariosONExceptionCode.AAC_00281);
		}
		if (dtConsulta != null && dia != null) {
		    if (dia != CoreUtil.retornaDiaSemana(dtConsulta)) {
			throw new ApplicationBusinessException(DisponilidadeHorariosONExceptionCode.AAC_00280);
		    }
		}
	
		if (horaConsulta != null && dtConsulta == null && mesInicio == null && dia == null) {
		    throw new ApplicationBusinessException(DisponilidadeHorariosONExceptionCode.ERRO_PESQUISA_POR_HORARIO_DISPONIBILIDADE);
		}

    }

    public Long listarDisponibilidadeHorariosCount(Integer filtroSeq, Short filtroUslUnfSeq, AghEspecialidades filtroEspecialidade,
			AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador, AacTipoAgendamento tipoAgendamento,
			AacCondicaoAtendimento condicaoAtendimento, Date dtConsulta, Date horaConsulta, Date mesInicio, Date mesFim,
			DominioDiaSemana dia, Boolean disponibilidade, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,	DataInicioFimVO turno,
			List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {

	return this.getAacGradeAgendamenConsultasDAO().listarDisponibilidadeHorariosCount(filtroSeq, filtroUslUnfSeq, filtroEspecialidade,
			filtroEquipe, filtroProfissional, pagador, tipoAgendamento, condicaoAtendimento, dtConsulta, horaConsulta, mesInicio,
			mesFim, dia, disponibilidade, zona, zonaSala, turno, listEspecialidade, visualizarPrimeirasConsultasSMS);
    }

    public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorarios(Integer filtroSeq, Short filtroUslUnfSeq,
    	    AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador,
    	    AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicao, Date dtConsulta, Date horaConsulta, Date mesInicio,
    	    Date mesFim, DominioDiaSemana dia, Boolean disponibilidade, VAacSiglaUnfSalaVO zona, VAacSiglaUnfSala zonaSala,
    	    DataInicioFimVO turno, List<AghEspecialidades> listEspecialidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {

    	List<AacGradeAgendamenConsultas> listaGrade = new ArrayList<AacGradeAgendamenConsultas>();

    	listaGrade = this.getAacGradeAgendamenConsultasDAO().listarDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade,
    		filtroEquipe, filtroProfissional, pagador, tipoAgendamento, condicao, dtConsulta, horaConsulta, mesInicio, mesFim, dia,
    		zona, zonaSala, turno, listEspecialidade, visualizarPrimeirasConsultasSMS);
    	
    	List<DisponibilidadeHorariosVO> listaVO = this.popularListaDisponibilidadeHorarios(listaGrade, pagador, tipoAgendamento, condicao,
    		dtConsulta, horaConsulta, mesInicio, mesFim, dia, disponibilidade, visualizarPrimeirasConsultasSMS);

    	return listaVO;
    }

    public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorarios(Integer filtroSeq, Short filtroUslUnfSeq,
	    AghEspecialidades filtroEspecialidade, AghEquipes filtroEquipe, RapServidores filtroProfissional, AacPagador pagador,
	    AacTipoAgendamento tipoAgendamento, AacCondicaoAtendimento condicao, Date dtConsulta, Date horaConsulta, Date mesInicio,
	    Date mesFim, DominioDiaSemana dia, Boolean disponibilidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {

    	return listarDisponibilidadeHorarios(filtroSeq, filtroUslUnfSeq, filtroEspecialidade, filtroEquipe, filtroProfissional, pagador,
        	    tipoAgendamento, condicao, dtConsulta, horaConsulta, mesInicio, mesFim, dia, disponibilidade, null, null, null,  null, visualizarPrimeirasConsultasSMS);
    }

    public List<DisponibilidadeHorariosVO> listarDisponibilidadeHorariosEmergencia(String orderProperty, boolean asc)
	    throws ApplicationBusinessException {
	List<AacGradeAgendamenConsultas> listaGrade = new ArrayList<AacGradeAgendamenConsultas>();
	listaGrade = this.getAacGradeAgendamenConsultasDAO().listarDisponibilidadeHorariosEmergencia(orderProperty, asc);
	List<DisponibilidadeHorariosVO> listaVO = this.popularListaDisponibilidadeHorariosEmergencia(listaGrade);
	return listaVO;
    }

    @SuppressWarnings({ "PMD.ExcessiveMethodLength", "PMD.NPathComplexity" })
    private List<DisponibilidadeHorariosVO> popularListaDisponibilidadeHorarios(List<AacGradeAgendamenConsultas> lista,
	    AacPagador filtroPagador, AacTipoAgendamento filtroTipoAgendamento, AacCondicaoAtendimento filtroCondicao, Date dtConsulta,
	    Date horaConsulta, Date mesInicio, Date mesFim, DominioDiaSemana dia, Boolean disponibilidade, Boolean visualizarPrimeirasConsultasSMS) throws ApplicationBusinessException {

		ArrayList<DisponibilidadeHorariosVO> listaDisponibilidadeHorariosVO = new ArrayList<DisponibilidadeHorariosVO>();
	
		String valorParametro = null;
		try {
		    valorParametro = getParametroFacade()
			    .buscarValorTexto(AghuParametrosEnum.P_AGHU_PERMITE_CONSULTA_HORARIOS_AMBULATORIO_PASSADOS);
		} catch (ApplicationBusinessException e) {
		    LOG.error(e.getMessage(), e);
		}
	
		for (AacGradeAgendamenConsultas grade : lista) {
		    DisponibilidadeHorariosVO disponibilidadeHorariosVO = new DisponibilidadeHorariosVO();
		    Integer consultasBloqueadas = Integer.valueOf(0);
		    Integer consultasLiberadas = Integer.valueOf(0);
	
		    Date dataHorarioComparacao = new Date();
		    if ("S".equalsIgnoreCase(valorParametro)) {
			dataHorarioComparacao = DateUtil.truncaData(dataHorarioComparacao);
		    }
	
		    List<ConsultaDisponibilidadeHorarioVO> listaConsultaDisponibilidadeHorariosVO = this.getAacConsultasDAO()
			    .listarConsultaDisponibilidadeHorariosVO(grade.getSeq(), filtroPagador, filtroTipoAgendamento, filtroCondicao,
				    dtConsulta, mesInicio, mesFim, dia, visualizarPrimeirasConsultasSMS);
		    Date dthrInicio = null;
		    Date dthrFim = null;
	
		    for (ConsultaDisponibilidadeHorarioVO consulta : listaConsultaDisponibilidadeHorariosVO) {
	
				if (consulta.getExcedeProgramacao() != null && consulta.getExcedeProgramacao()
					&& !consulta.getSituacaoConsulta().getSituacao().equals("L")) {
				    continue;
				}
		
				if (consulta.getSituacaoConsulta().getBloqueio().equals(true)
					&& DateValidator.validaDataMaiorQueAtual(consulta.getDataConsulta())) {
				    if (horaConsulta != null) {
					Calendar dataC = Calendar.getInstance();
					dataC.setTime(consulta.getDataConsulta());
					Calendar horaC = Calendar.getInstance();
					horaC.setTime(horaConsulta);
					if (dataC.get(Calendar.HOUR_OF_DAY) == horaC.get(Calendar.HOUR_OF_DAY)
						&& dataC.get(Calendar.MINUTE) == horaC.get(Calendar.MINUTE)) {
					    consultasBloqueadas++;
					}
				    } else {
					consultasBloqueadas++;
				    }
				}
				if (consulta.getSituacaoConsulta().getSituacao().equals("L") && consulta.getDataConsulta().after(dataHorarioComparacao)) {
				    if (horaConsulta != null) {
						Calendar dataC = Calendar.getInstance();
						dataC.setTime(consulta.getDataConsulta());
						Calendar horaC = Calendar.getInstance();
						horaC.setTime(horaConsulta);
						if (dataC.get(Calendar.HOUR_OF_DAY) == horaC.get(Calendar.HOUR_OF_DAY)
							&& dataC.get(Calendar.MINUTE) == horaC.get(Calendar.MINUTE)) {
						    consultasLiberadas++;
						}
					} else {
						consultasLiberadas++;
					}
				}
		
				if (dthrInicio == null && consulta.getDataConsulta() != null) {
				    dthrInicio = consulta.getDataConsulta();
				}
		
				if (dthrFim == null && consulta.getDataConsulta() != null) {
				    dthrFim = consulta.getDataConsulta();
				}
		
				Calendar dataInicio = Calendar.getInstance();
				if (dthrInicio != null) {
				    dataInicio.setTime(dthrInicio);
				}
		
				Calendar dataConsulta = Calendar.getInstance();
				if (consulta.getDataConsulta() != null) {
				    dataConsulta.setTime(consulta.getDataConsulta());
				}
		
				if (dataInicio != null && dataConsulta != null
					&& dataInicio.get(Calendar.HOUR_OF_DAY) > dataConsulta.get(Calendar.HOUR_OF_DAY)) {
				    dthrInicio = consulta.getDataConsulta();
				}
		
				if (dthrFim == null && consulta.getDataConsulta() != null) {
				    dthrFim = consulta.getDataConsulta();
				}
				Calendar dataFim = Calendar.getInstance();
				if (dthrFim != null) {
				    dataFim.setTime(dthrFim);
				}
		
				if (dataInicio != null && dataConsulta != null
					&& dataInicio.get(Calendar.HOUR_OF_DAY) >= dataConsulta.get(Calendar.HOUR_OF_DAY)
					&& dataInicio.get(Calendar.MINUTE) >= dataConsulta.get(Calendar.MINUTE)) {
				    dthrInicio = consulta.getDataConsulta();
				}
		
				if (dataFim != null && dataConsulta != null && dataConsulta.get(Calendar.HOUR_OF_DAY) >= dataFim.get(Calendar.HOUR_OF_DAY)
					&& dataConsulta.get(Calendar.MINUTE) >= dataFim.get(Calendar.MINUTE)) {
				    dthrFim = consulta.getDataConsulta();
				}
		    }
		    disponibilidadeHorariosVO.setGrdSeq(grade.getSeq());
		    if (grade.getSiglaUnfSala() != null && grade.getSiglaUnfSala().getId() != null) {
			disponibilidadeHorariosVO.setZonaSala(grade.getSiglaUnfSala().getSigla() + "-" + grade.getSiglaUnfSala().getId().getSala());
		    }
		    if (grade.getEquipe() != null) {
			disponibilidadeHorariosVO.setEquipe(grade.getEquipe().getNome());
		    }
		    if (grade.getEspecialidade() != null) {
			disponibilidadeHorariosVO.setEspecialidade(grade.getEspecialidade().getSigla() + " - "
				+ grade.getEspecialidade().getNomeEspecialidade());
		    }
		    if (grade.getProfEspecialidade() != null && grade.getProfEspecialidade().getRapServidor() != null
			    && grade.getProfEspecialidade().getRapServidor().getPessoaFisica() != null) {
			disponibilidadeHorariosVO.setProfissional(grade.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
		    }
		    disponibilidadeHorariosVO.setConsultasBloqueadas(consultasBloqueadas);
		    disponibilidadeHorariosVO.setConsultasLiberadas(consultasLiberadas);
		    disponibilidadeHorariosVO.setHoraInicio(dthrInicio);
		    disponibilidadeHorariosVO.setHoraFim(dthrFim);
		    if (!disponibilidade || (disponibilidade && consultasLiberadas > 0)) {
			listaDisponibilidadeHorariosVO.add(disponibilidadeHorariosVO);
		    }
		}
		this.ordenarDisponibilidadeHorariosPorConsultasLiberadas(listaDisponibilidadeHorariosVO);
	
		return listaDisponibilidadeHorariosVO;
    }

    private List<DisponibilidadeHorariosVO> popularListaDisponibilidadeHorariosEmergencia(List<AacGradeAgendamenConsultas> lista) {
	ArrayList<DisponibilidadeHorariosVO> listaDisponibilidadeHorariosVO = new ArrayList<DisponibilidadeHorariosVO>();
	for (AacGradeAgendamenConsultas grade : lista) {
	    DisponibilidadeHorariosVO disponibilidadeHorariosVO = new DisponibilidadeHorariosVO();

	    disponibilidadeHorariosVO.setGrdSeq(grade.getSeq());
	    if (grade.getSiglaUnfSala() != null && grade.getSiglaUnfSala().getId() != null) {
		disponibilidadeHorariosVO.setZonaSala(grade.getSiglaUnfSala().getSigla() + "-" + grade.getSiglaUnfSala().getId().getSala());
	    }
	    if (grade.getEquipe() != null) {
		disponibilidadeHorariosVO.setEquipe(grade.getEquipe().getNome());
	    }
	    if (grade.getEspecialidade() != null) {
		disponibilidadeHorariosVO.setEspecialidade(grade.getEspecialidade().getSigla() + " - "
			+ grade.getEspecialidade().getNomeEspecialidade());
	    }
	    if (grade.getProfEspecialidade() != null && grade.getProfEspecialidade().getRapServidor() != null
		    && grade.getProfEspecialidade().getRapServidor().getPessoaFisica() != null) {
		disponibilidadeHorariosVO.setProfissional(grade.getProfEspecialidade().getRapServidor().getPessoaFisica().getNome());
	    }
	    listaDisponibilidadeHorariosVO.add(disponibilidadeHorariosVO);
	}
	return ordenarDisponiblidadeEmergencia(listaDisponibilidadeHorariosVO);
    }

    private void ordenarDisponibilidadeHorariosPorConsultasLiberadas(List<DisponibilidadeHorariosVO> lista) {
	Collections.sort(lista, Collections.reverseOrder(new DisponibilidadeHorariosComparator()));
    }

    protected AacGradeAgendamenConsultasDAO getAacGradeAgendamenConsultasDAO() {
	return aacGradeAgendamenConsultasDAO;
    }

    protected AacConsultasDAO getAacConsultasDAO() {
	return aacConsultasDAO;
    }

    protected IParametroFacade getParametroFacade() {
	return parametroFacade;
    }
}
