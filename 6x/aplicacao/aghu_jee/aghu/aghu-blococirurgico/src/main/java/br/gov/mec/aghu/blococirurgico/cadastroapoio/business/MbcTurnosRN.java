package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcTurnosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.model.MbcTurnos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * 
 * @author lsamberg
 * 
 */
@Stateless
public class MbcTurnosRN extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcTurnosRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcTurnosDAO mbcTurnosDAO;



	/**
	 * 
	 */
	private static final long serialVersionUID = -7695729174895077055L;
	
	

	public enum MbcTurnosRNExceptionCode implements BusinessExceptionCode {
		MBC_JA_POSSUI_COM_ID, TIPO_TURNO_DESCRICAO_ATIVA
	}

	public void persistirMbcTurnos(MbcTurnos turno) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
	
		validarSiglaTurno(turno);
		validarDescricaoTurno(turno);
		turno.setCriadoEm(new Date());
		turno.setServidor(servidorLogado);
		getMbcTurnosDAO().persistir(turno);
	}

	public void atualizarMbcTurnos(MbcTurnos turno) throws ApplicationBusinessException {
			getMbcTurnosDAO().atualizar(turno);
			getMbcTurnosDAO().flush();
	}

	private void validarSiglaTurno(MbcTurnos turno) throws ApplicationBusinessException {
		MbcTurnos original = getMbcTurnosDAO().obterOriginal(turno.getTurno());

		if(original != null){
			throw new ApplicationBusinessException(MbcTurnosRNExceptionCode.MBC_JA_POSSUI_COM_ID);
		}
	}
	
	//Melhoria #25894
	private void validarDescricaoTurno(MbcTurnos turno) throws ApplicationBusinessException {
		if(getMbcTurnosDAO().verificarExistenciaTurnoAtivoComMesmaDescricao(turno.getDescricao(), turno.getTurno())) {
			throw new ApplicationBusinessException(MbcTurnosRNExceptionCode.TIPO_TURNO_DESCRICAO_ATIVA, turno.getDescricao());
		}
		
	}
	
	public List<MbcTurnos> pesquisarTiposTurno(Object objPesquisa) {
		final List<MbcTurnos> retorno = new ArrayList<MbcTurnos>();
		String pesq = "";
		if(objPesquisa != null) {
			pesq =objPesquisa.toString().toUpperCase(); 
		}
		final MbcTurnos ret = this.getMbcTurnosDAO().obterPorChavePrimaria(pesq);
		if(ret == null){
			return this.getMbcTurnosDAO().buscarPorDescricao(pesq);
		}
		retorno.add(ret);
		return retorno;
	}
	
	public Long pesquisarTiposTurnoCount(Object objPesquisa) {
		MbcTurnos ret = this.getMbcTurnosDAO().obterPorChavePrimaria(objPesquisa);
		if(ret == null) {
			return this.getMbcTurnosDAO().buscarPorDescricaoCount(objPesquisa);
		}
		return Long.valueOf(1);
	}
	
	public MbcTurnosDAO getMbcTurnosDAO(){
		return mbcTurnosDAO;
	}

}
