package br.gov.mec.aghu.indicadores.business;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.indicadores.vo.ReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.core.business.BaseBMTBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * ON Geral para indicadores.
 * 
 * @author riccosta
 * 
 */
@Stateless
@TransactionManagement(TransactionManagementType.BEAN)
public class IndicadoresON extends BaseBMTBusiness {

	
	@EJB
	private IndicadoresResumidosRN indicadoresResumidosRN;
	
	@EJB
	private IndicadoresRN indicadoresRN;
	
	private static final Log LOG = LogFactory.getLog(IndicadoresON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IInternacaoFacade internacaoFacade;
	
	private static final long serialVersionUID = 937491728767521261L;

	protected IndicadoresResumidosRN getIndicadoresResumidosRN() {
		return indicadoresResumidosRN;
	}

	public List<ReferencialClinicaEspecialidadeVO> pesquisarReferencialClinicaEspecialidade(
			Integer codigoClinica, Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		return getInternacaoFacade().pesquisarReferencialClinicaEspecialidade(codigoClinica, firstResult, maxResult,
				orderProperty, asc);
	}

	/**
	 * Método para servir de fachada entre o controller e a classe
	 * IndicadoresRN.
	 * 
	 * @param anoMesCompetencia
	 * @throws AGHUNegocioException
	 */
	public void gerarIndicadoresHospitalares(Date date, String cron) throws ApplicationBusinessException {

		logInfo("Rotina IndicadoresON.gerarIndicadoresHospitalares iniciada em: "
				+ Calendar.getInstance().getTime());

		// Mes de Competência = Mês anterior ao atual.
		Calendar mesCompetencia = Calendar.getInstance();
		mesCompetencia.set(Calendar.MONTH, mesCompetencia.get(Calendar.MONTH) - 1);
		mesCompetencia.set(Calendar.DAY_OF_MONTH, 1);

		getIndicadoresRN().gerarIndicadoresHospitalares(mesCompetencia.getTime());

		logInfo("Rotina IndicadoresON.gerarIndicadoresHospitalares finalizada em: "
				+ Calendar.getInstance().getTime());		
	}

	public void gravarIndicadoresResumidos(Date date, String cron) throws ApplicationBusinessException {

		logInfo("Rotina IndicadoresON.gravarIndicadoresResumidos iniciada em: "
				+ Calendar.getInstance().getTime());

		// Mes de Competência = Mês anterior ao atual.
		Calendar mesCompetencia = Calendar.getInstance();
		mesCompetencia.set(Calendar.MONTH, mesCompetencia.get(Calendar.MONTH) - 1);
		mesCompetencia.set(Calendar.DAY_OF_MONTH, 1);

		for (DominioTipoIndicador tipoIndicador : DominioTipoIndicador.values()) {
			getIndicadoresResumidosRN().gravarIndicadoresPorTipo(mesCompetencia.getTime(),
					tipoIndicador);
		}
		
		getIndicadoresResumidosRN().atualizarDataInicialIg(mesCompetencia);

		logInfo("Rotina IndicadoresON.gravarIndicadoresResumidos finalizada em: "
				+ Calendar.getInstance().getTime());
	}


	/**
	 * Gravação dos indicadores a partir de um tela.
	 * 
	 * @param anoMesCompetencia
	 * @throws ApplicationBusinessException
	 */
	public void gravarIndicadoresResumidos(Date anoMesCompetencia) throws ApplicationBusinessException {
		try{
			this.beginTransaction(3*60*60);	// 3 horas
			
			for (DominioTipoIndicador tipoIndicador : DominioTipoIndicador.values()) {
				getIndicadoresResumidosRN().gravarIndicadoresPorTipo(anoMesCompetencia, tipoIndicador);
			}
			
			Date dataUltimoIndicadorGerado = this.getInternacaoFacade().obterUltimoIndicadorHospitalarGerado();
			
			// Verifica se ultima data gerada com indicadores é maior que a data para a qual está sendo gerado na tela
			Date dataAtualizacao = null;
			if (dataUltimoIndicadorGerado != null && dataUltimoIndicadorGerado.compareTo(anoMesCompetencia) > 0) {
				dataAtualizacao = dataUltimoIndicadorGerado;
			} else {
				dataAtualizacao = anoMesCompetencia;
			}
			
			// Mes de Competência = Mês anterior ao atual.
			Calendar mesCompetencia = Calendar.getInstance();
			mesCompetencia.setTime(dataAtualizacao);
			
			// Atualiza a tabela AGH_DATAS_IG que é necessária para as pesquisas referencias
			this.getIndicadoresResumidosRN().atualizarDataInicialIg(mesCompetencia);
			this.commitTransaction();
			
		}catch(ApplicationBusinessException e){
			super.rollbackTransaction();
			throw e;
		}
	}
	
	protected IInternacaoFacade getInternacaoFacade() {
		return this.internacaoFacade;
	}

	protected IndicadoresRN getIndicadoresRN(){
		return indicadoresRN;
	}

}