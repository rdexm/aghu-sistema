package br.gov.mec.aghu.prescricaomedica.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaReinternacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaReinternacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaReinternacaoON extends BaseBusiness {


@EJB
private ManterAltaReinternacaoRN manterAltaReinternacaoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaReinternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaReinternacaoDAO mpmAltaReinternacaoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -4922783229744559961L;

	/**
	 * Atualiza MpmAltaReinternacao do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarAltaReinternacao(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		MpmAltaReinternacao altaReinternacao = this.getAltaReinternacaoDAO().obterMpmAltaReinternacao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		if (altaReinternacao != null && DateUtil.validaDataTruncadaMaior(altaReinternacao.getData(), new Date())) {
			
			MpmAltaReinternacao novoAltaReinternacao = new MpmAltaReinternacao();
			novoAltaReinternacao.setAltaSumario(altaSumario);
			novoAltaReinternacao.setData(altaReinternacao.getData());
			novoAltaReinternacao.setDescEspecialidade(altaReinternacao.getDescEspecialidade());
			novoAltaReinternacao.setEspecialidade(altaReinternacao.getEspecialidade());
			novoAltaReinternacao.setObservacao(altaReinternacao.getObservacao());
			novoAltaReinternacao.setMotivoReinternacao(altaReinternacao.getMotivoReinternacao());
			this.getManterAltaReinternacaoRN().inserirAltaReinternacao(novoAltaReinternacao);
			
		}
		
	}
	
	/**
	 * Grava as informações de Reinternacao
	 * @param altaReinternacao
	 * @return
	 */
	public void gravarAltaReinternacao(MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {
		if (altaReinternacao.getId() == null) {
			this.getManterAltaReinternacaoRN().inserirAltaReinternacao(altaReinternacao);
		} else {
	
			this.getManterAltaReinternacaoRN().atualizarAltaReinternacao(altaReinternacao);
		}
	}
	
	/**
	 * Remove Reinternacao
	 * @param altaReinternacao
	 */
	public void removerAltaReinternacao(MpmAltaReinternacao altaReinternacao) throws ApplicationBusinessException {
		
		this.getManterAltaReinternacaoRN().removerReinternacao(altaReinternacao);
		
	}
	
	/**
	 * Remover MpmAltaReinternacao do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void removerAltaReinternacao(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		
		MpmAltaReinternacao altaReinternacao = this.getAltaReinternacaoDAO().obterMpmAltaReinternacao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), altaSumario.getId().getSeqp());
		
		if (altaReinternacao != null) {
				
			this.getManterAltaReinternacaoRN().removerReinternacao(altaReinternacao);
			
		}
		
	}
	
	protected MpmAltaReinternacaoDAO getAltaReinternacaoDAO() {
		return mpmAltaReinternacaoDAO;
	}
	
	protected ManterAltaReinternacaoRN getManterAltaReinternacaoRN() {
		return manterAltaReinternacaoRN;
	}
	
}
