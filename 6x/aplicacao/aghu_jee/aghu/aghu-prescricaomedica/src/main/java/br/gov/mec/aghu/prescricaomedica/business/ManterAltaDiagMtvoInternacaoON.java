package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmAltaDiagMtvoInternacao;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaDiagMtvoInternacaoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaDiagMtvoInternacaoON extends BaseBusiness {


@EJB
private ManterAltaDiagMtvoInternacaoRN manterAltaDiagMtvoInternacaoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaDiagMtvoInternacaoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaDiagMtvoInternacaoDAO mpmAltaDiagMtvoInternacaoDAO;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 8806151926246373595L;

	/**
	 * Cria uma cópia de MpmAltaDiagMtvoInternacao
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 * @throws ApplicationBusinessException
	 */
	public void versionarAltaDiagMtvoInternacao(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaDiagMtvoInternacao> lista = this.getAltaDiagMtvoInternacaoDAO().obterMpmAltaDiagMtvoInternacao(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao : lista) {
			
			MpmAltaDiagMtvoInternacao novoAltaDiagMtvoInternacao = new MpmAltaDiagMtvoInternacao();
			novoAltaDiagMtvoInternacao.setCid(altaDiagMtvoInternacao.getCid());
			novoAltaDiagMtvoInternacao.setCidAtendimento(altaDiagMtvoInternacao.getCidAtendimento());
			novoAltaDiagMtvoInternacao.setComplCid(altaDiagMtvoInternacao.getComplCid());
			novoAltaDiagMtvoInternacao.setDescCid(altaDiagMtvoInternacao.getDescCid());
			novoAltaDiagMtvoInternacao.setDiagnostico(altaDiagMtvoInternacao.getDiagnostico());
			novoAltaDiagMtvoInternacao.setIndCarga(altaDiagMtvoInternacao.getIndCarga());
			novoAltaDiagMtvoInternacao.setIndSituacao(altaDiagMtvoInternacao.getIndSituacao());
			novoAltaDiagMtvoInternacao.setAltaSumario(altaSumario);
			this.getManterAltaDiagMtvoInternacaoRN().inserirAltaDiagMtvoInternacao(novoAltaDiagMtvoInternacao);
			
		}
		
	}
	
	/**
	 * Remove MpmAltaDiagMtvoInternacao
	 * @param altaSumario
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaDiagMtvoInternacao(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmAltaDiagMtvoInternacao> lista = this.getAltaDiagMtvoInternacaoDAO().obterMpmAltaDiagMtvoInternacao(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(), 
				false
		);
		
		for (MpmAltaDiagMtvoInternacao altaDiagMtvoInternacao : lista) {
			this.getManterAltaDiagMtvoInternacaoRN().removerAltaDiagMtvoInternacao(altaDiagMtvoInternacao);
		}
	}
	
	protected MpmAltaDiagMtvoInternacaoDAO getAltaDiagMtvoInternacaoDAO() {
		return mpmAltaDiagMtvoInternacaoDAO;
	}
	
	protected ManterAltaDiagMtvoInternacaoRN getManterAltaDiagMtvoInternacaoRN() {
		return manterAltaDiagMtvoInternacaoRN;
	}

}
