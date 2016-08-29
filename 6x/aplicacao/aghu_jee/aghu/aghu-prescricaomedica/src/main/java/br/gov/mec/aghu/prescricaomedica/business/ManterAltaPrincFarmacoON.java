package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmAltaPrincFarmaco;
import br.gov.mec.aghu.model.MpmAltaPrincFarmacoId;
import br.gov.mec.aghu.model.MpmAltaSumario;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaPrincFarmacoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaSumarioDAO;
import br.gov.mec.aghu.prescricaomedica.vo.AfaMedicamentoPrescricaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * 
 * @author lalegre
 *
 */
@Stateless
public class ManterAltaPrincFarmacoON extends BaseBusiness {


@EJB
private ManterAltaPrincFarmacoRN manterAltaPrincFarmacoRN;

private static final Log LOG = LogFactory.getLog(ManterAltaPrincFarmacoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaSumarioDAO mpmAltaSumarioDAO;

@Inject
private MpmAltaPrincFarmacoDAO mpmAltaPrincFarmacoDAO;

@EJB
private IFarmaciaFacade farmaciaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 8841828924619731816L;
	/**
	 * Atualiza MpmAltaPrincFarmaco do sumário ativo
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void versionarAltaPrincFarmaco(MpmAltaSumario altaSumario, Short antigoAsuSeqp) throws ApplicationBusinessException {
		
		List<MpmAltaPrincFarmaco> lista = this.getMpmAltaPrincFarmacoDAO().obterMpmAltaPrincFarmaco(altaSumario.getId().getApaAtdSeq(), altaSumario.getId().getApaSeq(), antigoAsuSeqp);
		
		for (MpmAltaPrincFarmaco altaPrincFarmaco : lista) {
		    
			MpmAltaPrincFarmaco novoAltaPrincFarmaco = new MpmAltaPrincFarmaco();
			novoAltaPrincFarmaco.setAltaSumario(altaSumario);
			novoAltaPrincFarmaco.setDescMedicamento(altaPrincFarmaco.getDescMedicamento());
			novoAltaPrincFarmaco.setIndCarga(altaPrincFarmaco.getIndCarga());
			novoAltaPrincFarmaco.setIndSituacao(altaPrincFarmaco.getIndSituacao());
			novoAltaPrincFarmaco.setMedicamento(altaPrincFarmaco.getMedicamento());
			this.getManterAltaPrincFarmacoRN().inserirAltaPrincFarmaco(novoAltaPrincFarmaco);
			
		}
		
	}
	
	
	
	public void inserirMpmAltaPrincFarmaco(AfaMedicamentoPrescricaoVO vo)throws ApplicationBusinessException {
	    
	    MpmAltaSumarioDAO dao1 = this.getMpmAltaSumarioDAO();
	    MpmAltaSumario altaSumario = dao1.obterAltaSumarioPeloId(vo.getAsuApaAtdSeq(), vo.getAsuApaSeq(), vo.getAsuSeqp());
	    	    
	    IFarmaciaFacade farmaciaFacade = this.getFarmaciaFacade();
	    //AfaMedicamento afaMedicamento = dao2.obterPorChavePrimaria(vo.getMedMatCodigo());
	    AfaMedicamento afaMedicamento = farmaciaFacade.obterMedicamento(vo.getMedMatCodigo());
	    
	    MpmAltaPrincFarmaco altaPrincFarmaco = new MpmAltaPrincFarmaco();
	    altaPrincFarmaco.setAltaSumario(altaSumario);
	    altaPrincFarmaco.setDescMedicamento(CoreUtil.capitalizaTextoFormatoAghu(vo.getDescricao()));
	    altaPrincFarmaco.setIndSituacao(DominioSituacao.A);
	    altaPrincFarmaco.setMedicamento(afaMedicamento);
	    altaPrincFarmaco.setIndCarga(true);
	    
	    this.getManterAltaPrincFarmacoRN().inserirAltaPrincFarmaco(altaPrincFarmaco);
	    
	}
	
	public void removerMpmAltaPrincFarmaco(AfaMedicamentoPrescricaoVO vo)throws ApplicationBusinessException {
	    
	    MpmAltaPrincFarmacoId id = new MpmAltaPrincFarmacoId(vo.getAsuApaAtdSeq(),vo.getAsuApaSeq(),vo.getAsuSeqp(),vo.getSeqp());
	    
	   MpmAltaPrincFarmacoDAO dao = this.getMpmAltaPrincFarmacoDAO();
	   MpmAltaPrincFarmaco altaPrincFarmaco = dao.obterPorChavePrimaria(id);
	    
	   this.getManterAltaPrincFarmacoRN().removerAltaPrincFarmaco(altaPrincFarmaco);
	    
	}
	
	/**
	 * Remove MpmAltaPrincFarmaco do sumário.
	 * 
	 * @param altaSumario
	 * @param antigoAsuSeqp
	 */
	public void removerAltaPrincFarmaco(MpmAltaSumario altaSumario) throws ApplicationBusinessException {
		List<MpmAltaPrincFarmaco> lista = this.getMpmAltaPrincFarmacoDAO().obterMpmAltaPrincFarmaco(
				altaSumario.getId().getApaAtdSeq(), 
				altaSumario.getId().getApaSeq(), 
				altaSumario.getId().getSeqp(),
				false
		);
		
		for (MpmAltaPrincFarmaco altaPrincFarmaco : lista) {
			this.getManterAltaPrincFarmacoRN().removerAltaPrincFarmaco(altaPrincFarmaco);
		}
	}
	
	
	protected MpmAltaPrincFarmacoDAO getMpmAltaPrincFarmacoDAO() {
		return mpmAltaPrincFarmacoDAO;
	}
	
	protected ManterAltaPrincFarmacoRN getManterAltaPrincFarmacoRN() {
		return manterAltaPrincFarmacoRN;
	}
	protected MpmAltaSumarioDAO getMpmAltaSumarioDAO() {
		return mpmAltaSumarioDAO;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

}
