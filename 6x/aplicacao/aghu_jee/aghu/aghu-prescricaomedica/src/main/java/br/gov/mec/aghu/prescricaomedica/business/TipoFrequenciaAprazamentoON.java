package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAprazamentoFrequencia;
import br.gov.mec.aghu.model.MpmAprazamentoFrequenciaId;
import br.gov.mec.aghu.model.MpmTipoFrequenciaAprazamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAprazamentoFrequenciasDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmTipoFrequenciaAprazamentoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class TipoFrequenciaAprazamentoON extends BaseBusiness {

	private static final String TIPO_FREQ_APRAZAMENTOS = "Tipo Freq Aprazamentos";

	@EJB
	private TipoFrequenciaAprazamentoRN tipoFrequenciaAprazamentoRN;
	
	@EJB
	private AprazamentoFrequenciaRN aprazamentoFrequenciaRN;
	
	private static final Log LOG = LogFactory.getLog(TipoFrequenciaAprazamentoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO;
	
	@Inject
	private MpmAprazamentoFrequenciasDAO mpmAprazamentoFrequenciasDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8106641279768036866L;

	public void excluirTipoAprazamentoFrequencia(final Short seq) throws ApplicationBusinessException{
		final MpmTipoFrequenciaAprazamentoDAO mpmTipoFrequenciaAprazamentoDAO = getTipoFrequenciaAprazamentoDAO();
		final MpmTipoFrequenciaAprazamento  entity = mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(seq);
		
		validaExclusaoTipoFrequenciaAprazamento(entity, true);
		getTipoFrequenciaAprazamentoRN().preDeleteTipoFrequenciaAprazamento(entity);
		mpmTipoFrequenciaAprazamentoDAO.remover(entity);
		mpmTipoFrequenciaAprazamentoDAO.flush();
	}	

	public void salvar(MpmTipoFrequenciaAprazamento entity, List<MpmAprazamentoFrequencia> aprazamentos, List<MpmAprazamentoFrequencia> excluidosList) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		final boolean isInsert = (entity.getSeq()==null);
		
		if (getTipoFrequenciaAprazamentoDAO().siglaJaExiste(entity)){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.MPM_01252);
		}
		
		try {
			entity.setServidor(servidorLogado);		
			getTipoFrequenciaAprazamentoRN().verificaMatricula(entity);
			
			if (isInsert){
				this.validarFatConvHorasDigitaFrequencia(entity);
				entity.setCriadoEm(new Date());
				getTipoFrequenciaAprazamentoDAO().persistir(entity);
				getTipoFrequenciaAprazamentoDAO().flush();
			}else{
				getTipoFrequenciaAprazamentoDAO().atualizar(entity);
				getTipoFrequenciaAprazamentoDAO().flush();
			}
			mergeAprazamentos(entity,aprazamentos,excluidosList);
		} catch (BaseRuntimeException e) {
			if(isInsert){
				entity.setSeq(null);
			}
			throw new ApplicationBusinessException(e.getCode());
		} catch (BaseException e) {
			if(isInsert){
				entity.setSeq(null);
			}
			throw new ApplicationBusinessException(e.getCode());
		}
	}	
	
	/**
	 * Método que incluí a seguinte regra de negócio solicitada pelo Vacaro em 08/10/2012
	 * Se o fator de conversão de horas for maior que zero, o campo digitaFrequencia deve ser marcado
	 * Caso contrário o campo não pode estar marcado
	 * @param entity
	 * @throws ApplicationBusinessException
	 */
	private void validarFatConvHorasDigitaFrequencia(
			MpmTipoFrequenciaAprazamento entity)
			throws ApplicationBusinessException {
		if (entity.getFatorConversaoHoras().floatValue() > 0.0) {
			if (!entity.getIndDigitaFrequencia()) {
				throw new ApplicationBusinessException(
						TipoFrequenciaAprazamentoExceptionCode.FAT_CONV_HORAS_FREQ_DEVE_DIGITAR);
			}
		} else {
			if (entity.getIndDigitaFrequencia()) {
				throw new ApplicationBusinessException(
						TipoFrequenciaAprazamentoExceptionCode.FAT_CONV_HORAS_FREQ_NAO_DEVE_DIGITAR);
			}
		}
	}
	
	
	public void excluirAprazamentoFrequencia(final MpmAprazamentoFrequenciaId id) throws ApplicationBusinessException{
		
		final MpmAprazamentoFrequenciasDAO dao = getAprazamentoFrequenciaDAO();
		final MpmAprazamentoFrequencia entity = dao.obterPorChavePrimaria(id);
		
		validaExclusaoTipoFrequenciaAprazamento(entity.getTipoFreqAprazamento(), false);
		dao.remover(entity);
		dao.flush();
	}	

	public List<MpmTipoFrequenciaAprazamento> paginator(MpmTipoFrequenciaAprazamento entity,int firstResult, int maxResults,String orderProperty, boolean asc){
		return getTipoFrequenciaAprazamentoDAO().listarAprazamentosFrequenciaPaginator(entity, firstResult, maxResults,orderProperty,asc);
	}
	
	public Long count(MpmTipoFrequenciaAprazamento entity){
		return getTipoFrequenciaAprazamentoDAO().countAprazamentosFrequenciaPaginator(entity);
	}	
	
	
	private MpmTipoFrequenciaAprazamentoDAO getTipoFrequenciaAprazamentoDAO(){
		return mpmTipoFrequenciaAprazamentoDAO;
	}	

	protected MpmAprazamentoFrequenciasDAO getAprazamentoFrequenciaDAO(){
		return mpmAprazamentoFrequenciasDAO;
	}	
	
	public void validaExclusaoTipoFrequenciaAprazamento(MpmTipoFrequenciaAprazamento entity, boolean validaAprazamentos) throws ApplicationBusinessException {
		entity = this.mpmTipoFrequenciaAprazamentoDAO.obterPorChavePrimaria(entity.getSeq());
		if (validaAprazamentos && entity.getAprazamentoFrequencias()!=null && !entity.getAprazamentoFrequencias().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Aprazamentos Frequências");
			
		}else if (entity.getCuidadoUsuais()!=null && !entity.getCuidadoUsuais().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Cuidados Usuais");
			
		}else if (entity.getHorarioInicioAprazamentos()!=null && !entity.getHorarioInicioAprazamentos().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Horário Inic Aprazamentos");
			
		}else if (entity.getItensCuidadoSumarios()!=null && !entity.getItensCuidadoSumarios().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Cuidados Sumários");
			
		}else if (entity.getItensDietaSumarios()!=null && !entity.getItensDietaSumarios().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Dietas Sumários");

		}else if (entity.getItensHemoterapiaSumarios()!=null && !entity.getItensHemoterapiaSumarios().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Hemoterapias Sumários");

		}else if (entity.getItensMedicamentoSumarios()!=null && !entity.getItensMedicamentoSumarios().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Medicamentos Sumários");

		}else if (entity.getItensModeloBasicoDietas()!=null && !entity.getItensModeloBasicoDietas().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Modelos Sumários");

		}else if (entity.getItensPrescricaoDietas()!=null && !entity.getItensPrescricaoDietas().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Prescrições Sumários");

		}else if (entity.getModeloBasicoCuidados()!=null && !entity.getModeloBasicoCuidados().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Modelos Básicos Cuidados");

		}else if (entity.getModeloBasicoMedicamentos()!=null && !entity.getModeloBasicoMedicamentos().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Modelos Básicos Medicamentos");

		}else if (entity.getPrescricaoCuidados()!=null && !entity.getPrescricaoCuidados().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Prescrições Cuidados");

		}else if (entity.getPrescricaoMedicamentos()!=null && !entity.getPrescricaoMedicamentos().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Prescrições Medicamentos");
			
		}else if (entity.getUsoOrdCuidados()!=null && !entity.getUsoOrdCuidados().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Uso Ord Cuidados");

		}else if (entity.getItensSolHemoterapicas()!=null && !entity.getItensSolHemoterapicas().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Itens Sol Hemoterápicas");
			
		}else if (entity.getCuidados()!=null && !entity.getCuidados().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Cuidados");
			
		}else if (entity.getEpePrescricoesCuidados()!=null && !entity.getEpePrescricoesCuidados().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Prescrições Cuidados(EPE)");
			
		}else if (entity.getUsoOrdItemHemoters()!=null && !entity.getUsoOrdItemHemoters().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Uso Ord Item Hemoters");
			
		}else if (entity.getViewMpaUsoOrdMdtos()!=null && !entity.getViewMpaUsoOrdMdtos().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Uso Ord Item Medicamentos (view)");
			
		}else if (entity.getViewMpmCuidado()!=null && !entity.getViewMpmCuidado().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Cuidado MPM (view)");

		}else if (entity.getViewMpmMdtos()!=null && !entity.getViewMpmMdtos().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Medicamentos MPM (view)");
			
		}else if (entity.getViewMpmPrescrMdtos()!=null && !entity.getViewMpmPrescrMdtos().isEmpty()){
			throw new ApplicationBusinessException(TipoFrequenciaAprazamentoExceptionCode.OFG_00005, TIPO_FREQ_APRAZAMENTOS, "Prescrição Medicamentos MPM (view)");
		}
	}
	
	private void mergeAprazamentos(MpmTipoFrequenciaAprazamento entity, List<MpmAprazamentoFrequencia> aprazamentos, List<MpmAprazamentoFrequencia> excluidosList) throws ApplicationBusinessException{
		final MpmAprazamentoFrequenciasDAO dao = getAprazamentoFrequenciaDAO();
		
		entity = getTipoFrequenciaAprazamentoDAO().obterTipoFrequenciaAprazamentoPeloId(entity.getSeq());
		
		for (MpmAprazamentoFrequencia item : aprazamentos){
			if (item.getId()==null){
				getAprazamentoFrequenciaRN().verificaMatricula(item);
				getAprazamentoFrequenciaRN().verificaTipoFrequenciaAprazamento(item);
				item.setId(dao.criaId(entity.getSeq()));
				dao.persistir(item);
				dao.flush();
			}else{
				RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
				
				item.setServidor(servidorLogado);			
				dao.merge(item);
				dao.flush();
			}
		}
		
		if(excluidosList != null){//Estava dando problema de transaction
			for (MpmAprazamentoFrequencia item : excluidosList){
				if (item.getId()!=null){
					// Para reatachar objeto na sessao
					MpmAprazamentoFrequencia maf = dao.obterPorChavePrimaria(item.getId());
					
					dao.remover(maf);
					dao.flush();
				}
			}
		}	
		getTipoFrequenciaAprazamentoDAO().refresh(entity);
	}
	
	
	public void criaAprazamentoFrequencia(MpmAprazamentoFrequencia item, MpmTipoFrequenciaAprazamento entity) throws ApplicationBusinessException{
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		item.setServidor(servidorLogado);
		item.setTipoFreqAprazamento(entity);
		item.setCriadoEm(new Date());				
	}
	
	private enum TipoFrequenciaAprazamentoExceptionCode implements
			BusinessExceptionCode {
		OFG_00005, MPM_01252, FAT_CONV_HORAS_FREQ_DEVE_DIGITAR, FAT_CONV_HORAS_FREQ_NAO_DEVE_DIGITAR
	}
	
	private TipoFrequenciaAprazamentoRN getTipoFrequenciaAprazamentoRN(){
		return tipoFrequenciaAprazamentoRN;
	}
	
	private AprazamentoFrequenciaRN getAprazamentoFrequenciaRN(){
		return aprazamentoFrequenciaRN;
	}	

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
