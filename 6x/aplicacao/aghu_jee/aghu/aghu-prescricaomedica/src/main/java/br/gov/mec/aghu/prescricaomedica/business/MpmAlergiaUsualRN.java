package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmAlergiaUsual;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAlergiaUsualDAO;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MpmAlergiaUsualRN extends BaseBusiness {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6331773194952277380L;

	private static final Log LOG = LogFactory.getLog(MpmAlergiaUsualRN.class);
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private MpmAlergiaUsualDAO mpmAlergiaUsualDAO;
	
	private MpmAlergiaUsual mpmAlergiaUsual;
	
	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	@SuppressWarnings("unused")
	private enum MpmAlergiaUsualRNExceptionCode implements	BusinessExceptionCode {
		MENSAGEM_SUCESSO_REMOCAO_ALERGIA_USUAL, M01;
	}
	
	public void removerAlergiaUsual(MpmAlergiaUsual obj) {
		mpmAlergiaUsualDAO.removerPorId(obj.getSeq());
	}
	
	public void salvarAlergiaUsual(MpmAlergiaUsual alergiaUsual, Boolean situacao) throws ApplicationBusinessException {
		mpmAlergiaUsual = new MpmAlergiaUsual();
		
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		
		mpmAlergiaUsual.setSerMatricula(servidorLogado.getId().getMatricula());
		mpmAlergiaUsual.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		mpmAlergiaUsual.setDescricao(alergiaUsual.getDescricao().trim());
		mpmAlergiaUsual.setCriadoEm(new Date());
		if(situacao){
			mpmAlergiaUsual.setIndSituacao(DominioSituacao.A.toString());
		}else{
			mpmAlergiaUsual.setIndSituacao(DominioSituacao.I.toString());
		}
		mpmAlergiaUsualDAO.persistir(mpmAlergiaUsual);
	}
	
	public void alterarAlergiaUsual(MpmAlergiaUsual alergiaUsual, Boolean situacao) throws ApplicationBusinessException {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MpmAlergiaUsual novaAlergiaUsual = mpmAlergiaUsualDAO.obterOriginal(alergiaUsual);
		
		novaAlergiaUsual.setSerMatricula(servidorLogado.getId().getMatricula());
		novaAlergiaUsual.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		novaAlergiaUsual.setDescricao(alergiaUsual.getDescricao().trim());
		novaAlergiaUsual.setCriadoEm(new Date());
		if(situacao){
			novaAlergiaUsual.setIndSituacao(DominioSituacao.A.toString());
		}else{
			novaAlergiaUsual.setIndSituacao(DominioSituacao.I.toString());
		}
		mpmAlergiaUsualDAO.merge(novaAlergiaUsual);
	}

	public MpmAlergiaUsual getMpmAlergiaUsual() {
		return mpmAlergiaUsual;
	}

	public void setMpmAlergiaUsual(MpmAlergiaUsual mpmAlergiaUsual) {
		this.mpmAlergiaUsual = mpmAlergiaUsual;
	}
}