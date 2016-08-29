package br.gov.mec.aghu.ambulatorio.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.dao.AacRadioAcompTomosDAO;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AacRadioAcompTomos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class AacRadioAcompTomosRN extends BaseBusiness {

	private static final long serialVersionUID = -1833246777829683206L;

	private static final Log LOG = LogFactory.getLog(AacRadioAcompTomosRN.class);

	@Inject
	AacRadioAcompTomosDAO aacRadioAcompTomosDAO;

	@EJB
	IServidorLogadoFacade servidorLogadoFacade;
	
	public IServidorLogadoFacade getServidorLogadoFacade() {
		return servidorLogadoFacade;
	}

	public AacRadioAcompTomosDAO getAacRadioAcompTomosDAO() {
		return aacRadioAcompTomosDAO;
	}
	
	/**
	 * ORADB:RN_CONP_ATU_UPD_RAT
	 * @param pacCodigo
	 * @param dtConsulta
	 * @param serVinCodigoAtendido
	 * @param serMatriculaAtendido
	 */
	public void atualizaAacRadioAcompTomos(Integer pacCodigo, Date dtConsulta,
			Short serVinCodigoAtendido, Integer serMatriculaAtendido){
		
		
		List<AacRadioAcompTomos> aacRadioAcompTomos = getAacRadioAcompTomosDAO().pesquisarPorDataPaceienteServidor(pacCodigo,dtConsulta,
					serVinCodigoAtendido, serMatriculaAtendido);

		RapServidores servidor = getServidorLogadoFacade().obterServidorPorChavePrimaria(serMatriculaAtendido, serVinCodigoAtendido);
		
		for(AacRadioAcompTomos aacRadioAcompTomo: aacRadioAcompTomos){
			aacRadioAcompTomo.setServidor(servidor);
			
			atualizarSituacaoAacRadioAcompTomos(aacRadioAcompTomo, getAacRadioAcompTomosDAO().obterOriginal(aacRadioAcompTomo));
			getAacRadioAcompTomosDAO().atualizar(aacRadioAcompTomo);
		}
		
	}
	
	/**
	 * Estória do Usuário #40230
	 * ORADB: Trigger AACT_RAT_BRU
	 * @author marcelo.deus
	 * 
	 */
	 public void atualizarSituacaoAacRadioAcompTomos(AacRadioAcompTomos newAacRadioAcompTomos, AacRadioAcompTomos oldAacRadioAcompTomos){
		 if(newAacRadioAcompTomos.getSeq() != oldAacRadioAcompTomos.getSeq() && 
				 oldAacRadioAcompTomos.getDtBlc() == null &&
				 newAacRadioAcompTomos.getDtRx() != null &&
				 newAacRadioAcompTomos.getDtEfl() != null &&
				 newAacRadioAcompTomos.getDtC3d() != null &&
				 newAacRadioAcompTomos.getDtAm() != null &&
				 newAacRadioAcompTomos.getDtMrc() != null &&
				 newAacRadioAcompTomos.getDtPln() != null &&
				 newAacRadioAcompTomos.getDtAm2() != null &&
				 newAacRadioAcompTomos.getDtLib() != null &&
				 newAacRadioAcompTomos.getSituacao().equals(DominioSituacao.A)){
				 newAacRadioAcompTomos.setSituacao(DominioSituacao.I);
				 getAacRadioAcompTomosDAO().atualizar(newAacRadioAcompTomos);
		 }
	 }
	/*
	 * (non-Javadoc)
	 * @see br.gov.mec.aghu.core.business.BaseBusiness#getLogger()
	 */
	@Override
	protected Log getLogger() {

		return LOG;
	}

}
