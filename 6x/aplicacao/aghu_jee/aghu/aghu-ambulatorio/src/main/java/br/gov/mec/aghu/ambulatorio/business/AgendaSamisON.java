package br.gov.mec.aghu.ambulatorio.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacConsultasDAO;
import br.gov.mec.aghu.ambulatorio.dao.AacPeriodoReferenciaDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.model.AacConsultas;
import br.gov.mec.aghu.model.AacPeriodoReferencia;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;

@Stateless
public class AgendaSamisON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(AgendaSamisON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@Inject
	private AacPeriodoReferenciaDAO aacPeriodoReferenciaDAO;
	
	@Inject
	private AacConsultasDAO aacConsultasDAO;
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9204915045047444218L;

	private enum AgendaSamisONExceptionCode implements BusinessExceptionCode {
		PACIENTE_SEM_PRONTUARIO_SAMIS, ERRO_PRONTUARIO_VIRTUAL_SAMIS, ERRO_CONSULTA_DIA_OU_DIA_SEGUINTE, PRONTUARIO_JA_SOLICITADO
	}

	public void requererProntuarios(AacConsultas consulta, RapServidores servidorLogado, Boolean exibeMsgProntuarioJaMovimentado) 
			throws ApplicationBusinessException {
		
		List<AacPeriodoReferencia> listaPeriodoReferencia = this.getAacPeriodoReferenciaDAO().pesquisarPeriodoReferencia();

		Date dataReferencia = getDataReferencia(listaPeriodoReferencia);
		Date dataConsultaInicio = getDataConsulta(consulta);
		Date dataReferenciaInicio = getDataInicioReferencia(dataReferencia);
		Date dataAtualInicio = DateUtil.truncaData(new Date());
		Integer pacCodigo = getPacCodigo(consulta);
		
		validaRequerimentoProntuario(consulta, exibeMsgProntuarioJaMovimentado, dataConsultaInicio, dataReferenciaInicio, dataAtualInicio);
		
		validaProntuariosASeremRequeridos(consulta, servidorLogado, pacCodigo);
		
	}

	private void validaProntuariosASeremRequeridos(AacConsultas consulta,
			RapServidores servidorLogado, Integer pacCodigo)
			throws ApplicationBusinessException {
		List<AipMovimentacaoProntuarios> listProntuariosMovimentados = this.getPacienteFacade()
				.pesquisarMovimentacaoPorPacienteEDataConsulta(pacCodigo, consulta.getDtConsulta());
		if(listProntuariosMovimentados == null || listProntuariosMovimentados.size()==0){
			this.getPacienteFacade().gerarMovimentacaoProntuario(consulta.getNumero(), servidorLogado);
		}
		else{
			String siglaUnidadeSolicitante = consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getUnidadeFuncional().getSigla();
			Byte sala = consulta.getGradeAgendamenConsulta().getAacUnidFuncionalSala().getId().getSala();
			requererProntuariosAindaNaoRequeridos(consulta, servidorLogado, siglaUnidadeSolicitante, sala, listProntuariosMovimentados);
		}
	}

	private void requererProntuariosAindaNaoRequeridos(
			AacConsultas consulta, RapServidores servidorLogado,
			String siglaUnidadeSolicitante, Byte sala,
			List<AipMovimentacaoProntuarios> listProntuariosMovimentados) throws ApplicationBusinessException,
			ApplicationBusinessException {
		
		Boolean movimentaProntuario = Boolean.TRUE;
		for (AipMovimentacaoProntuarios aipMovimentacaoProntuarios : listProntuariosMovimentados) {
			String siglaUnidadeSolicitanteMovimentada = aipMovimentacaoProntuarios.getSolicitante().getUnidadesFuncionais().getSigla();
			if(!StringUtils.isBlank(aipMovimentacaoProntuarios.getLocal())){
				Byte salaMovimentada = splitSala(aipMovimentacaoProntuarios.getLocal());
				if(!( !(siglaUnidadeSolicitante.equals(siglaUnidadeSolicitanteMovimentada)) || (siglaUnidadeSolicitante.equals(siglaUnidadeSolicitanteMovimentada) && !(sala.equals(salaMovimentada))) ) ){
					movimentaProntuario = Boolean.FALSE;
				} 
			}
		}
		
		if(movimentaProntuario){
			this.getPacienteFacade().gerarMovimentacaoProntuario(consulta.getNumero(), servidorLogado);
		}
	}

	public Byte splitSala(String local) {
		String[] splitLocal = local.split("/");
		String strSalaMovimentada = splitLocal[splitLocal.length-1].substring(0, 1);
		Byte salaMovimentada = Byte.valueOf(strSalaMovimentada);
		return salaMovimentada;
	}

	private Integer getPacCodigo(AacConsultas consulta) {
		Integer pacCodigo = null;
		if(consulta!=null && consulta.getPaciente()!=null){
			pacCodigo = consulta.getPaciente().getCodigo();
		}
		return pacCodigo;
	}

	private void validaRequerimentoProntuario(AacConsultas consulta,
			Boolean exibeMsgProntuarioJaMovimentado, Date dataConsultaInicio,
			Date dataReferenciaInicio, Date dataAtualInicio)
			throws ApplicationBusinessException {
		validaSePacientePossuiProntuario(consulta);
		validaSePacientePossuiProntuarioVirtual(consulta);
		validaConsultaDiaOuDiaSeguinte(exibeMsgProntuarioJaMovimentado, dataConsultaInicio, dataReferenciaInicio, dataAtualInicio);
	}

	private void validaConsultaDiaOuDiaSeguinte(
			Boolean exibeMsgProntuarioJaMovimentado, Date dataConsultaInicio,
			Date dataReferenciaInicio, Date dataAtualInicio)
			throws ApplicationBusinessException {
		if(exibeMsgProntuarioJaMovimentado && (DateUtil.validaDataMaior(dataConsultaInicio,dataReferenciaInicio) || DateUtil.validaDataMenor(dataConsultaInicio, dataAtualInicio))){
			throw new ApplicationBusinessException(AgendaSamisONExceptionCode.ERRO_CONSULTA_DIA_OU_DIA_SEGUINTE);
		}
	}

	private Date getDataInicioReferencia(Date dataReferencia) {
		Date dataReferenciaInicio = null;
		if(dataReferencia!=null){
			dataReferenciaInicio = DateUtil.truncaData(dataReferencia); 
		}
		return dataReferenciaInicio;
	}

	private Date getDataConsulta(AacConsultas consulta) {
		Date dataConsulta = consulta.getDtConsulta();
		Date dataConsultaInicio = getDataInicioReferencia(dataConsulta);
		return dataConsultaInicio;
	}

	private Date getDataReferencia(List<AacPeriodoReferencia> listaPeriodoReferencia) {
		Date dataReferencia = null;
		if(listaPeriodoReferencia!=null && listaPeriodoReferencia.size()>0){
			AacPeriodoReferencia periodoReferencia = listaPeriodoReferencia.get(0);
			dataReferencia = periodoReferencia.getDtReferencia();
		}
		return dataReferencia;
	}

	private void validaSePacientePossuiProntuarioVirtual(AacConsultas consulta)
			throws ApplicationBusinessException {
		if(consulta.getPaciente()!=null && consulta.getPaciente().getProntuario()!=null && consulta.getPaciente().getProntuario()>VALOR_MAXIMO_PRONTUARIO){
			throw new ApplicationBusinessException(AgendaSamisONExceptionCode.ERRO_PRONTUARIO_VIRTUAL_SAMIS);
		}
	}

	private void validaSePacientePossuiProntuario(AacConsultas consulta)
			throws ApplicationBusinessException {
		if(consulta.getPaciente()==null || consulta.getPaciente().getProntuario()==null){
			throw new ApplicationBusinessException(AgendaSamisONExceptionCode.PACIENTE_SEM_PRONTUARIO_SAMIS);
		}
	}
	
	
	public AacPeriodoReferenciaDAO getAacPeriodoReferenciaDAO(){
		return aacPeriodoReferenciaDAO;
	}
	
	public AacConsultasDAO getAacConsultasDAO() {
		return aacConsultasDAO;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}
}

/*PROCEDURE EVT_WHEN_BUTTON_PRESSED IS
cursor c_mov_prnt(c_pac_codigo aac_consultas.pac_codigo%type,
									c_dt_consulta date)  is
SELECT seq
FROM AIP_MOVIMENTACAO_PRONTUARIOS 
where PAC_CODIGO  = c_pac_codigo
and DATA_RETIRADA = c_dt_consulta;
--
r_mov c_mov_prnt%rowtype;
v_nro_consulta     aac_consultas.numero%type;
v_dt_ref date;
v_data date;
v_dt_hr date;
--
cursor c_per_refer is
select trunc(dt_referencia)
from   aac_periodo_referencias;
--
BEGIN


 if name_in('system.trigger_item') = 'CON.BUT_NUMERO'
 then
 		if name_in('CON.DSP_PRONTUARIO') is null THEN
 			 QMS$HANDLE_OFG45_MESSAGES('E', TRUE, 'Paciente sem prontuário para solicitar ao SAMIS');
 		end if; 
 		if name_in('CON.DSP_PRONTUARIO') > VALOR_MAXIMO_PRONTUARIO THEN
 			 QMS$HANDLE_OFG45_MESSAGES('E', TRUE, 'Prontuário virtual não deve ser solicitado ao SAMIS');
 		end if; 
 		open c_per_refer;
 		fetch c_per_refer into v_dt_ref;
 		close c_per_refer;
 		v_data := trunc(to_date (name_in('CON.DT_CONSULTA'),'dd-MON-yyyy hh24:mi:ss'));
    v_dt_hr := to_date(name_in('CON.DT_CONSULTA'),'dd-mon-yyyy hh24:mi:ss');
    --
 		if v_data > v_dt_ref 
 		or v_data < trunc(sysdate) then
 			 QMS$HANDLE_OFG45_MESSAGES('E', TRUE, 'Para solicitar ao SAMIS a consulta deve ser para o dia ou dia seguinte');
 		end if;

 		open c_mov_prnt(name_in('CON.PAC_CODIGO'),name_in('con.dt_consulta'));
 		fetch c_mov_prnt into r_mov;
 		if c_mov_prnt%notfound then

				 AIPP_GERA_MVOL_EVENT(name_in('CON.NUMERO'));

 		else 

			 QMS$HANDLE_OFG45_MESSAGES('E', TRUE, 'Já foi solicitado prontuário para esta consulta'); 

 		end if;
 		close c_mov_prnt;
 end if;


END;*/
