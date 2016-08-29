package br.gov.mec.aghu.internacao.administracao.business;

import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoDataObito;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * ON para a estoria de usuario 'Atualizar Data de Óbito'.
 */
@Stateless
public class AtualizarDataObitoON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(AtualizarDataObitoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacienteFacade pacienteFacade;

@EJB
private ICadastroPacienteFacade cadastroPacienteFacade;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3954366415967588665L;

	private enum AtualizarDataObitoONExceptionCode implements
			BusinessExceptionCode {
		AIN_00531_CAMPO_OBRIGATORIO, DATA_OBITO_OBRIGATORIO, PESQUISA_NAO_ENCONTRADA, AIP_00120, AIN_00528, DATA_OBITO_MAIOR_QUE_DATA_ATUAL;
	}

	/**
	 * Valida campos de entrada da tela.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public void validaCampos(Integer prontuario, Integer codigo)
			throws ApplicationBusinessException {
		if (prontuario == null && codigo == null) {
			throw new ApplicationBusinessException(
					AtualizarDataObitoONExceptionCode.AIN_00531_CAMPO_OBRIGATORIO);
		}
	}

	/**
	 * ORADB PROCEDURE EVT_PRE_COMMIT -> AINF_ATLZ_DT_OBT_PAC.FMB
	 * 
	 * @param dataObito
	 * @param tipoDataObito
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	// TODO: Verificar esse método.
	public void validarDataObito(Date dataObito,
			DominioTipoDataObito tipoDataObito, AipPacientes paciente)
			throws ApplicationBusinessException {

		if (paciente.getDtObito() != null) {
			throw new ApplicationBusinessException(AtualizarDataObitoONExceptionCode.AIP_00120);
			
		} else if(tipoDataObito != null ){
			
			// Quando for diferente de Data Ignorada, deve-se ter a data de óbito
			if (!DominioTipoDataObito.IGN.equals(tipoDataObito) && dataObito == null){
				throw new ApplicationBusinessException(AtualizarDataObitoONExceptionCode.DATA_OBITO_OBRIGATORIO);
				
			} else if (!tipoDataObito.equals(DominioTipoDataObito.IGN)) {
				
				if (dataObito != null && dataObito.after(new Date())) {
					throw new ApplicationBusinessException(
							AtualizarDataObitoONExceptionCode.DATA_OBITO_MAIOR_QUE_DATA_ATUAL,
							tipoDataObito.getDescricao());
				}
				
				Date dt = getDataHoraFim(paciente.getCodigo());
				if (dt != null) {
					if (tipoDataObito.equals(DominioTipoDataObito.ANO)) {
						dt = org.apache.commons.lang3.time.DateUtils.truncate(dt,
								Calendar.YEAR);
					} else if (tipoDataObito.equals(DominioTipoDataObito.MES)) {
						dt = org.apache.commons.lang3.time.DateUtils.truncate(dt,
								Calendar.MONTH);
					}
					if (paciente.getDtObitoExterno() != null && dt.after(paciente.getDtObitoExterno())) {
						throw new ApplicationBusinessException(
								AtualizarDataObitoONExceptionCode.AIN_00528);
					}
				}
			}
		}
	}

	/**
	 * Metodo auxiliar para validacao de datas.
	 * 
	 * @param pacCodigo
	 * @return Date
	 */
	private Date getDataHoraFim(Integer pacCodigo) {		
		return getAghuFacade().getDataHoraFim(pacCodigo);
	}

	/**
	 * Metodo que busca os dados do paciente a partir de parametros.
	 * 
	 * @param prontuario
	 * @param codigo
	 * @throws ApplicationBusinessException
	 */
	public AipPacientes buscarDadosPaciente(Integer prontuario, Integer codigo, Long cpf)
			throws ApplicationBusinessException {
		AipPacientes pac = getPacienteFacade().obterPacientePorCodigoEProntuario(
				prontuario, codigo, null);

		if (pac == null) {
			throw new ApplicationBusinessException(
					AtualizarDataObitoONExceptionCode.PESQUISA_NAO_ENCONTRADA);
		}

		return pac;
	}
	
	/**
	 * Persiste as alterações em paciente, salvando a data de óbito do mesmo.
	 * 
	 * @param paciente
	 * @throws ApplicationBusinessException
	 */
	public void atualizarDataObito(AipPacientes paciente, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws ApplicationBusinessException {
		this.getCadastroPacienteFacade().atualizarPacienteParcial(paciente, nomeMicrocomputador, dataFimVinculoServidor);
	}

	protected ICadastroPacienteFacade getCadastroPacienteFacade() {
		return (ICadastroPacienteFacade) cadastroPacienteFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}
	
	protected IPacienteFacade getPacienteFacade() {
		return (IPacienteFacade) pacienteFacade;
	}

}
