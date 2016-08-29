package br.gov.mec.aghu.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.configuracao.dao.AghProfEspecialidadesDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghProfEspecialidades;
import br.gov.mec.aghu.model.AghProfEspecialidadesId;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;



@Stateless
public class AghProfEspecialidadesRN extends BaseBusiness {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -489593271059572392L;

	protected Log getLogger() {
		return LogFactory.getLog(AghProfEspecialidadesRN.class);
	}
	
	
	@Inject
	private AghProfEspecialidadesDAO aghProfEspecialidadesDAO;

	private enum AghProfEspecialidadesRNExceptionCode implements BusinessExceptionCode {
		ERRO_AGH_PROF_ESP_DUPLICADO;
	}
	
	public void persistirAghProfEspecialidades(AghProfEspecialidades aghProfEspecialidades, RapServidores servidorLogado, Short espSeq) throws ApplicationBusinessException{
		if (aghProfEspecialidades.getId() == null) {
			inserirProfEspecialidades(aghProfEspecialidades, servidorLogado, espSeq);
		} else {
			atualizarProfEspecualidades(aghProfEspecialidades, servidorLogado);
		}
	}

	private void atualizarProfEspecualidades(AghProfEspecialidades aghProfEspecialidades, RapServidores servidorLogado) {
		preAtualizar(aghProfEspecialidades, servidorLogado);
		aghProfEspecialidadesDAO.merge(aghProfEspecialidades);
	}

	private void inserirProfEspecialidades(AghProfEspecialidades aghProfEspecialidades, RapServidores servidorLogado, Short espSeq) throws ApplicationBusinessException {
		preInserir(aghProfEspecialidades, servidorLogado, espSeq);
		aghProfEspecialidadesDAO.persistir(aghProfEspecialidades);
	}

	/**
	 * @ORADB aght_pre_bri
	 * @param aghProfEspecialidades
	 * @throws ApplicationBusinessException 
	 */
	private void preInserir(AghProfEspecialidades aghProfEspecialidades, RapServidores servidorLogado, Short espSeq) throws ApplicationBusinessException {
		AghProfEspecialidadesId id = new AghProfEspecialidadesId();
		id.setSerMatricula(servidorLogado.getId().getMatricula());
		id.setSerVinCodigo(servidorLogado.getId().getVinCodigo());
		id.setEspSeq(espSeq);
		AghProfEspecialidades original = aghProfEspecialidadesDAO.obterPorChavePrimaria(id);
		if (original != null && original.getId() != null) {
			throw new ApplicationBusinessException(AghProfEspecialidadesRNExceptionCode.ERRO_AGH_PROF_ESP_DUPLICADO);
		}
		aghProfEspecialidades.setId(id);
		aghProfEspecialidades.setServidorDigitador(servidorLogado);
		aghProfEspecialidades.setIndAtuaAmbt(Boolean.FALSE);
		aghProfEspecialidades.setIndAtuaInternacao(DominioSimNao.N);
		aghProfEspecialidades.setQuantPacInternados(0);
		if (aghProfEspecialidades.getCapacReferencial() == null) {
			aghProfEspecialidades.setCapacReferencial(0);
		}
		aghProfEspecialidades.setIndAtuaBloco(DominioSimNao.N);
		aghProfEspecialidades.setIndCirurgiaoBloco(DominioSimNao.N);
		aghProfEspecialidades.setIndInterna(DominioSimNao.N);
	}
	
	/**
	 * @ORADB aght_pre_bru
	 * @param aghProfEspecialidades
	 */
	private void preAtualizar(AghProfEspecialidades aghProfEspecialidades, RapServidores servidorLogado) {
		aghProfEspecialidades.setServidorDigitador(servidorLogado);
		aghProfEspecialidades.getId().setSerMatricula(servidorLogado.getId().getMatricula());
		aghProfEspecialidades.getId().setSerVinCodigo(servidorLogado.getId().getVinCodigo());
	}

}
