package br.gov.mec.aghu.blococirurgico.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcDescricaoItensDAO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.MbcDescricaoCirurgica;
import br.gov.mec.aghu.model.MbcDescricaoItens;
import br.gov.mec.aghu.model.MbcDescricaoItensId;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas a tela Achados
 * Operatórios - Descrição Cirúrgica.
 * 
 * @author dpacheco
 * 
 */
@Stateless
public class DescricaoCirurgicaAchadosOperatoriosON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(DescricaoCirurgicaAchadosOperatoriosON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcDescricaoItensDAO mbcDescricaoItensDAO;


	@EJB
	private DescricaoItensRN descricaoItensRN;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5648098059569322258L;
	
	public enum DescricaoCirurgicaAchadosOperatoriosONExceptionCode implements BusinessExceptionCode {
		MBC_01835
	}

	public void persistirDescricaoItens(MbcDescricaoItens descricaoItens) 
			throws BaseException {
	
		Boolean ehUpdate = Boolean.FALSE;
		
		MbcDescricaoItensId descricaoItensId = descricaoItens.getId();
		//MbcDescricaoItens descricaoItensExistente = null;
		
		if (descricaoItensId != null && descricaoItensId.getDcgCrgSeq() != null && descricaoItensId.getDcgSeqp() != null) {
			//descricaoItensExistente = getMbcDescricaoItensDAO().obterPorChavePrimaria(descricaoItensId);
			//if (descricaoItensExistente != null) {
				ehUpdate = Boolean.TRUE;	
			//}
		}
		
		if (ehUpdate) {
			getDescricaoItensRN().alterarMbcDescricaoItens(descricaoItens);
		} else {
			getDescricaoItensRN().inserirDescricaoItens(descricaoItens);	
		}
	}
	
	public MbcDescricaoItens montarDescricaoItens(
			MbcDescricaoCirurgica descricaoCirurgica,
			String achadosOperatorios, String observacao,
			DominioSimNao indIntercorrencia, DominioSimNao indPerdaSangue,
			Integer volumePerdaSangue, String intercorrenciaClinica
			) {
		
		MbcDescricaoItensId descricaoItensId = new MbcDescricaoItensId(
				descricaoCirurgica.getId().getCrgSeq(), descricaoCirurgica.getId().getSeqp());
		MbcDescricaoItens descricaoItens = new MbcDescricaoItens();
		descricaoItens.setId(descricaoItensId);
		descricaoItens.setAsa(null);
		descricaoItens.setAchadosOperatorios(achadosOperatorios);
		descricaoItens.setIntercorrenciaClinica(intercorrenciaClinica);
		descricaoItens.setIndPerdaSangue(indPerdaSangue.isSim());
		descricaoItens.setVolumePerdaSangue(volumePerdaSangue);		
		descricaoItens.setIndIntercorrencia(indIntercorrencia.isSim());
		descricaoItens.setObservacao(observacao);
		descricaoItens.setCarater(null);
		descricaoItens.setDthrInicioCirg(null);
		descricaoItens.setDthrFimCirg(null);
		descricaoItens.setCriadoEm(null);
		descricaoItens.setServidor(null);
		descricaoItens.setMbcDescricaoCirurgica(descricaoCirurgica);
		return descricaoItens;
	}
	
	public void validarIntercorrenciaAchadosOperatorios(MbcDescricaoItens descricaoItens)
			throws ApplicationBusinessException {
		
		if (Boolean.TRUE.equals(descricaoItens.getIndIntercorrencia())
				&& StringUtils.isEmpty(descricaoItens.getIntercorrenciaClinica())) {
			throw new ApplicationBusinessException(
					DescricaoCirurgicaAchadosOperatoriosONExceptionCode.MBC_01835);
		}
	}

	protected DescricaoItensRN getDescricaoItensRN() {
		return descricaoItensRN;
	}
	
	protected MbcDescricaoItensDAO getMbcDescricaoItensDAO() {
		return mbcDescricaoItensDAO;
	}

}
