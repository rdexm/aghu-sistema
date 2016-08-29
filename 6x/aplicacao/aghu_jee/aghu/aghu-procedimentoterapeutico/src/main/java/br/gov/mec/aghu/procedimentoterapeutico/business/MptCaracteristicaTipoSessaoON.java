package br.gov.mec.aghu.procedimentoterapeutico.business;

import java.util.Date;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;
import org.apache.commons.logging.Log;
import br.gov.mec.aghu.model.MptCaracteristica;
import br.gov.mec.aghu.model.MptCaracteristicaTipoSessao;
import br.gov.mec.aghu.model.MptTipoSessao;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptCaracteristicaTipoSessaoDAO;
import br.gov.mec.aghu.procedimentoterapeutico.dao.MptTipoSessaoDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

@Stateless
public class MptCaracteristicaTipoSessaoON extends BaseBusiness{

	private static final long serialVersionUID = 7739569608494474388L;
	
	@Inject
	private MptCaracteristicaTipoSessaoDAO mptCaracteristicaTipoSessaoDAO;
	
	@Inject
	private MptTipoSessaoDAO mptTipoSessaoDAO; 
	
	@Inject
	private MptCaracteristicaDAO mptCaracteristicaDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Override
	protected Log getLogger() {
		return null;
	}
	
	
	public void adicionarMptCaracteristicaTipoSessao(MptTipoSessao mptTipoSessao, MptCaracteristica mptCaracteristica) throws ApplicationBusinessException{
		MptCaracteristica caracEntity = this.mptCaracteristicaDAO.obterPorChavePrimaria(mptCaracteristica.getSeq());
		MptTipoSessao tipoSesEntity = this.mptTipoSessaoDAO.obterPorChavePrimaria(mptTipoSessao.getSeq());
		MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao = new MptCaracteristicaTipoSessao();
		mptCaracteristicaTipoSessao.setServidor(this.servidorLogadoFacade.obterServidorLogado());
		mptCaracteristicaTipoSessao.setCriadoEm(new Date());
		mptCaracteristicaTipoSessao.setMptTipoSessao(tipoSesEntity);
		mptCaracteristicaTipoSessao.setMptCaracteristica(caracEntity);
		this.mptCaracteristicaTipoSessaoDAO.persistir(mptCaracteristicaTipoSessao);
	}
	
	public void excluirMptCaracteristicaTipoSessao(MptCaracteristicaTipoSessao mptCaracteristicTSExcluir){
		MptCaracteristicaTipoSessao mptCaracteristicaTipoSessao = this.mptCaracteristicaTipoSessaoDAO.obterPorChavePrimaria(
				mptCaracteristicTSExcluir.getSeq());
		this.mptCaracteristicaTipoSessaoDAO.remover(mptCaracteristicaTipoSessao);
		this.mptCaracteristicaTipoSessaoDAO.flush();
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
