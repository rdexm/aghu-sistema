package br.gov.mec.aghu.indicadores.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Inject;

import br.gov.mec.aghu.core.business.BaseFacade;
import br.gov.mec.aghu.core.business.moduleintegration.Modulo;
import br.gov.mec.aghu.core.business.moduleintegration.ModuloEnum;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioTipoIndicador;
import br.gov.mec.aghu.dominio.DominioTipoUnidade;
import br.gov.mec.aghu.indicadores.dao.AghDatasIgDAO;
import br.gov.mec.aghu.indicadores.vo.IndHospClinicaEspVO;
import br.gov.mec.aghu.indicadores.vo.ReferencialClinicaEspecialidadeVO;
import br.gov.mec.aghu.indicadores.vo.UnidadeIndicadoresVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinIndicadorHospitalarResumido;

/**
 * Porta de entrada do módulo de Indicadores.
 */
@Stateless
@Modulo(ModuloEnum.INDICADORES)
public class IndicadoresFacade extends BaseFacade implements IIndicadoresFacade  {

	
	@EJB
	private IndicadoresHospitalaresUtilRN indicadoresHospitalaresUtilRN;
	
	@EJB
	private IndicadoresON indicadoresON;
	
	@EJB
	private IndicadoresResumidosRN indicadoresResumidosRN;
	
	@EJB
	private IndicadoresRN indicadoresRN;
	
	@EJB
	private RelatorioIndicadoresON relatorioIndicadoresON;

	@Inject
	private AghDatasIgDAO aghDatasIgDAO;

	private static final long serialVersionUID = -3749530512291422468L;

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void gravarIndicadoresResumidos(Date anoMesCompetencia) throws ApplicationBusinessException {
		this.getIndicadoresON().gravarIndicadoresResumidos(anoMesCompetencia);
	}

	@Override
	@TransactionAttribute(TransactionAttributeType.NEVER)
	public void gerarIndicadoresHospitalares(Date anoMesCompetencia) throws ApplicationBusinessException {
		this.getIndicadoresRN().gerarIndicadoresHospitalares(anoMesCompetencia);
	}

	@Override
	public List<AinIndicadorHospitalarResumido> obterTotaisIndicadoresUnidade(Date mes,
			Date mesFim, DominioTipoIndicador tipoIndicador, AghUnidadesFuncionais unidadeFuncional) {
		return getIndicadoresResumidosRN().obterTotaisIndicadoresUnidade(mes, mesFim,
				tipoIndicador, unidadeFuncional);
	}

	@Secure
	public List<ReferencialClinicaEspecialidadeVO> pesquisarReferencialClinicaEspecialidade(
			Integer codigoClinica, Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		return getIndicadoresON().pesquisarReferencialClinicaEspecialidade(codigoClinica, firstResult,
				maxResult, orderProperty, asc);
	}

	@Override
	public Integer obterNumeroOcorrenciasIndicadoresGerais(Date mes) {
		return getRelatorioIndicadoresON().obterNumeroOcorrenciasIndicadoresGerais(mes);
	}

	@Override
	public List<IndHospClinicaEspVO> gerarRelatorioClinicaEspecialidade(Date mes) {
		return getRelatorioIndicadoresON().gerarRelatorioClinicaEspecialidade(mes);
	}

	@Override
	public Integer obterNumeroOcorrenciasIndicadoresUnidade(DominioTipoUnidade tipoUnidade, Date mes) {
		return getRelatorioIndicadoresON().obterNumeroOcorrenciasIndicadoresUnidade(tipoUnidade, mes);
	}

	@Override
	public List<UnidadeIndicadoresVO> pesquisarIndicadoresUnidade(DominioTipoUnidade tipoUnidade, Date mes) {
		return getRelatorioIndicadoresON().pesquisarIndicadoresUnidade(tipoUnidade, mes);
	}

	/**
	 * Método para servir de fachada entre o controller e a classe
	 * IndicadoresRN.
	 * 
	 * @param anoMesCompetencia
	 * @throws ApplicationBusinessException
	 */
	@Override
	public void gerarIndicadoresHospitalares(Date date, String cron) throws ApplicationBusinessException {
		this.getIndicadoresON().gerarIndicadoresHospitalares(date, cron);
	}

	@Override
	public void gravarIndicadoresResumidos(Date date, String cron) throws ApplicationBusinessException {
		this.getIndicadoresON().gravarIndicadoresResumidos(date, cron);
	}
	
	
	protected IndicadoresHospitalaresUtilRN getIndicadoresHospitalaresUtilRN() {
		return indicadoresHospitalaresUtilRN;
	}

	protected IndicadoresResumidosRN getIndicadoresResumidosRN() {
		return indicadoresResumidosRN;
	}

	protected RelatorioIndicadoresON getRelatorioIndicadoresON() {
		return relatorioIndicadoresON;
	}

	protected IndicadoresON getIndicadoresON(){
		return indicadoresON;
	}
	
	protected IndicadoresRN getIndicadoresRN(){
		return indicadoresRN;
	}
	
	protected AghDatasIgDAO getAghDatasIgDAO() {
		return aghDatasIgDAO;
	}

	@Override
	public Date obterUltimaDataInicial() {
		return this.getAghDatasIgDAO().obterUltimaDataInicial();
	}	
}