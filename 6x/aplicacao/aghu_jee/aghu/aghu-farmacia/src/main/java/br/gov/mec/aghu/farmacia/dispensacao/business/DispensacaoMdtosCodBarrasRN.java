package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioIndExcluidoDispMdtoCbSps;
import br.gov.mec.aghu.dominio.DominioSituacaoDispensacaoMdto;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispMdtoCbSpsDAO;
import br.gov.mec.aghu.farmacia.dispensacao.business.TratarOcorrenciasRN.TratarOcorrenciasRNExceptionCode;
import br.gov.mec.aghu.model.AfaDispMdtoCbSps;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


@Stateless
public class DispensacaoMdtosCodBarrasRN extends BaseBusiness implements Serializable {


@EJB
private AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN;

private static final Log LOG = LogFactory.getLog(DispensacaoMdtosCodBarrasRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IEstoqueFacade estoqueFacade;

@EJB
private IParametroFacade parametroFacade;

@Inject
private AfaDispMdtoCbSpsDAO afaDispMdtoCbSpsDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 2653664746814383114L;

	public enum DispensacaoMdtosCodBarrasRNExceptionCode implements	BusinessExceptionCode {
		AFA_01697,AFA_01489
	}
	
	/**
	 * Procedure do Oracle Forms
	 * 
	 * @ORADB PROCEDURE AFAP_VALIDA_CB
	 * @param nroEtiqueta
	 * @param listaMdtosPrescritos
	 * @throws ApplicationBusinessException
	 */
	public void dispensarMdtoCodBarras(String nroEtiqueta, List<AfaDispensacaoMdtos> listaMdtosPrescritos, String nomeMicrocomputador) throws BaseException {
		
		/**
		 * ***Boolean encontrouRegistro***
		 * 
		 * 	v_sair  VARCHAR(1):='N';
			v_codMed NUMBER(10):=0;
			v_achou VARCHAR(1):='N';
		 */
		
		AfaDispMdtoCbSps dispMdtoCbSps = getAfaDispMdtoCbSpsDAO().getDispMdtoCbSpsByEtiqueta(nroEtiqueta, DominioIndExcluidoDispMdtoCbSps.I);
		if(dispMdtoCbSps != null) {
			throw new ApplicationBusinessException(DispensacaoMdtosCodBarrasRNExceptionCode.AFA_01697);
		}
		
		SceLoteDocImpressao loteDocImp = getEstoqueFacade().getLoteDocImpressaoByNroEtiqueta(nroEtiqueta);
		
		if(loteDocImp == null){
			throw new ApplicationBusinessException(TratarOcorrenciasRNExceptionCode.AFA_01489);
		}
		
		Boolean encontrouRegistro = Boolean.FALSE;
		
		if (listaMdtosPrescritos != null && !listaMdtosPrescritos.isEmpty()) {
			AfaDispMdtoCbSpsRN afaDispMdtoCbSpsRN = this.getAfaDispMdtoCbSpsRN();
			for(AfaDispensacaoMdtos adm: listaMdtosPrescritos){
				if (DominioSituacaoDispensacaoMdto.T.equals(adm.getIndSituacao())
						&& adm.getMedicamento().getMatCodigo().equals(loteDocImp.getMaterial().getCodigo())) {
					afaDispMdtoCbSpsRN.validaAtualizaCodigoDeBarras(nroEtiqueta, adm.getPrescricaoMedica().getId().getAtdSeq(), adm.getPrescricaoMedica().getId().getSeq(), adm.getSeq(), nomeMicrocomputador);
					encontrouRegistro = Boolean.TRUE;
					break;
				}
			}
		}
		
		if(!encontrouRegistro) {
			throw new ApplicationBusinessException(DispensacaoMdtosCodBarrasRNExceptionCode.AFA_01489);
		}
	}
	
	
	//Getters
	
	private AfaDispMdtoCbSpsRN getAfaDispMdtoCbSpsRN() {
		return afaDispMdtoCbSpsRN;
	}
	
	private AfaDispMdtoCbSpsDAO getAfaDispMdtoCbSpsDAO() {
		return afaDispMdtoCbSpsDAO;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
	public String validarCodigoBarrasEtiqueta(final String nroEtiqueta) {
//		int subset = 'A';
//		try {
//			final AghParametros parametros = getParametroFacade().getAghParametro(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO);
//			if (StringUtils.isNotEmpty(parametros.getVlrTexto())) {
//				subset = parametros.getVlrTexto().charAt(0);
//			}
//		} catch (final Exception e) {
//			this.getLogger().info(
//					new StringBuffer("Erro ao buscar paramentro ").append(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO.toString()).append(": ")
//							.append(e.getMessage()), e);
//		}
//		switch (subset) {
//		case 'C':
//		case 'c':
//			
//			break;
//		case 'A':
//		case 'a':
//		default:
//		}
		if (nroEtiqueta == null) {
			return null;
		}
		String[] remocao = new String[] {};
		String nroEtiquetaFormatado = nroEtiqueta;
		try {
			final AghParametros parametros = getParametroFacade().obterAghParametro(AghuParametrosEnum.P_CONJ_CARACT_REMOVER_ETIQ_MDTO);
			if (StringUtils.isNotEmpty(parametros.getVlrTexto())) {
				remocao = parametros.getVlrTexto().split(",");
			}
		} catch (final BaseException e) {
			logError(
					new StringBuffer("Erro ao buscar parametro ").append(AghuParametrosEnum.P_TIPO_ETIQUETA_MEDICAMENTO.toString()).append(": ")
							.append(e.getMessage()), e);
		}
		for (final String rem : remocao) {
			int inicio = nroEtiquetaFormatado.indexOf(rem);
			if (inicio >= 0) {
				nroEtiquetaFormatado = new StringBuffer(nroEtiquetaFormatado.substring(0, inicio))
						.append(nroEtiquetaFormatado.substring(inicio + rem.length())).toString();
			}
		}

		return nroEtiquetaFormatado;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
		
}
