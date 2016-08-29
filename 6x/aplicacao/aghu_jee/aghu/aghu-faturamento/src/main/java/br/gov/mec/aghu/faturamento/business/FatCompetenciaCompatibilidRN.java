package br.gov.mec.aghu.faturamento.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCompetenciaCompatibilidDAO;
import br.gov.mec.aghu.model.FatCompetenciaCompatibilid;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class FatCompetenciaCompatibilidRN extends BaseBusiness {

	@Inject
	private FatCompetenciaCompatibilidDAO fatCompetenciaCompatibilidDAO;
	
	private static final Log LOG = LogFactory.getLog(FatCompetenciaCompatibilidRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private static final long serialVersionUID = 4066243285874443836L;

	protected enum FatCompetenciaCompatibilidRNExceptionCode implements BusinessExceptionCode {
		RAP_00175
	}
	
	public void atualizarFatCompetenciaCompatibilid(final Long cpxSeq, final Date dtEncerra) throws ApplicationBusinessException{
		final FatCompetenciaCompatibilid comp = getFatCompetenciaCompatibilidDAO().obterPorChavePrimaria(cpxSeq);
		
		// UPDATE FAT_COMPETENCIAS_COMPATIBILID  SET dt_fim_validade = greatest(v_dt_encerra,dt_inicio_validade)  WHERE seq           = p_cpx_seq;
		if(DateUtil.validaDataMaior(dtEncerra, comp.getDtInicioValidade())){
			comp.setDtFimValidade(dtEncerra);
			
		} else {
			comp.setDtFimValidade(comp.getDtInicioValidade());
		}
		
		persistirFatCompetenciaCompatibilid(comp);
	}
	
	public void persistirFatCompetenciaCompatibilid(final FatCompetenciaCompatibilid fatCompetenciaCompatibilid) throws ApplicationBusinessException{
		if(fatCompetenciaCompatibilid.getSeq() == null){
			inserir(fatCompetenciaCompatibilid);
		} else {
			alterar(fatCompetenciaCompatibilid);
		}
	}


	private void alterar(FatCompetenciaCompatibilid fatCompetenciaCompatibilid) throws ApplicationBusinessException {
		antesDeAlterar(fatCompetenciaCompatibilid);
		getFatCompetenciaCompatibilidDAO().merge(fatCompetenciaCompatibilid);
	}


	/**
	 * ORADB: FATT_CPX_BRU
	 */
	private void antesDeAlterar(final FatCompetenciaCompatibilid fatCompetenciaCompatibilid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		
		fatCompetenciaCompatibilid.setAlteradoEm(new Date());
		fatCompetenciaCompatibilid.setServidorAltera(servidorLogado);
	}


	private void inserir(FatCompetenciaCompatibilid fatCompetenciaCompatibilid) throws ApplicationBusinessException {
		antesDeInserir(fatCompetenciaCompatibilid);
		getFatCompetenciaCompatibilidDAO().persistir(fatCompetenciaCompatibilid);
	}
	
	
	/**
	 * ORADB: FATT_CPX_BRI
	 */
	private void antesDeInserir(final FatCompetenciaCompatibilid fatCompetenciaCompatibilid) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if(servidorLogado == null){
			throw new ApplicationBusinessException(FatCompetenciaCompatibilidRNExceptionCode.RAP_00175);
		}
		fatCompetenciaCompatibilid.setCriadoEm(new Date());
		fatCompetenciaCompatibilid.setServidorInsere(servidorLogado);
	}


	protected FatCompetenciaCompatibilidDAO getFatCompetenciaCompatibilidDAO() {
		return fatCompetenciaCompatibilidDAO;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
