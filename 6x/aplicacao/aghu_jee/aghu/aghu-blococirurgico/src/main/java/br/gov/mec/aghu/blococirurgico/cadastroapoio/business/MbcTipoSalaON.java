package br.gov.mec.aghu.blococirurgico.cadastroapoio.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcTipoSalaDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MbcTipoSala;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de FORMS para #24559 - Manter tipo salas
 * @author fpalma
 *
 */
@Stateless
public class MbcTipoSalaON extends BaseBusiness {
@EJB
private IServidorLogadoFacade servidorLogadoFacade;

	private static final Log LOG = LogFactory.getLog(MbcTipoSalaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcTipoSalaDAO mbcTipoSalaDAO;


	private static final long serialVersionUID = -219954391028505015L;
	
	public enum MbcTipoSalaONExceptionCode implements BusinessExceptionCode {
		TIPO_SALA_DESCRICAO_ATIVA;
	}

	public void gravarMbcTipoSala(MbcTipoSala tpSala) throws ApplicationBusinessException {
RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		validarTipoSala(tpSala);
		if(tpSala.getSeq() == null) {
			tpSala.setServidor(servidorLogado);
			tpSala.setCriadoEm(new Date());
			getMbcTipoSalaDAO().persistir(tpSala);
		} else {
			getMbcTipoSalaDAO().atualizar(tpSala);
		}
	}
	
	//Melhoria #25894
	private void validarTipoSala(MbcTipoSala tpSala) throws ApplicationBusinessException {
		if(DominioSituacao.A.equals(tpSala.getSituacao()) && 
				getMbcTipoSalaDAO().verificarExistenciaSalaAtivaComMesmaDescricao(tpSala.getDescricao())) {
			throw new ApplicationBusinessException(MbcTipoSalaONExceptionCode.TIPO_SALA_DESCRICAO_ATIVA, tpSala.getDescricao());
		}
	}

	/*
	 * Getters Facades, RNs e DAOs
	 */
	protected MbcTipoSalaDAO getMbcTipoSalaDAO() {
		return mbcTipoSalaDAO;
	}
	
}
