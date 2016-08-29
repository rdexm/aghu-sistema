package br.gov.mec.aghu.prescricaomedica.cadastrosbasicos.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MpmProcedEspecialDiversos;
import br.gov.mec.aghu.model.MpmProcedEspecialRm;
import br.gov.mec.aghu.model.MpmTipoModoUsoProcedimento;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialDiversoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmProcedEspecialRmDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class ProcedEspecialDiversoON extends BaseBusiness {

	private static final long serialVersionUID = -4674544396700048746L;
	
	@EJB
	private ProcedEspecialDiversoRN procedEspecialDiversoRN;
	
	private static final Log LOG = LogFactory.getLog(ProcedEspecialDiversoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@Inject
	private MpmProcedEspecialDiversoDAO mpmProcedEspecialDiversoDAO;

	@Inject
	private MpmProcedEspecialRmDAO mpmProcedEspecialRmDAO;
	

	public List<MpmProcedEspecialDiversos> pesquisar(Integer firstResult, Integer maxResult, String orderProperty, boolean asc, MpmProcedEspecialDiversos elemento) {
		return getMpmProcedEspecialDiversoDAO().pesquisar(firstResult, maxResult, orderProperty, asc, elemento);
	}
	
	public Long pesquisarCount(MpmProcedEspecialDiversos elemento) {
		return getMpmProcedEspecialDiversoDAO().pesquisarCount(elemento);
	}
	
	public MpmProcedEspecialDiversos obterProcedimentoEspecialPeloId(Short codigo) {
		return getMpmProcedEspecialDiversoDAO().obterPeloId(codigo);
	}
	
	public MpmProcedEspecialDiversos persistirProcedimentoEspecial(MpmProcedEspecialDiversos elemento, 
			List<MpmTipoModoUsoProcedimento> modosUsos, List<MpmTipoModoUsoProcedimento> modosUsosExcluidos, 
			List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRm,
			List<MpmProcedEspecialRm> materiaisMpmProcedEspecialRmExcluidos) throws BaseException {
		
		if(elemento.getSeq() == null) {
			//Realiza inserção
			return getProcedEspecialDiversoRN().inserir(elemento, modosUsos, materiaisMpmProcedEspecialRm);
			
		} else {
			//Realiza atualização
			return getProcedEspecialDiversoRN().atualizar(elemento, modosUsos, modosUsosExcluidos, materiaisMpmProcedEspecialRm, materiaisMpmProcedEspecialRmExcluidos);
		}
	}

	public void removerProcedimentoEspecial(Short codigo) throws BaseException {
		//Realiza deleção
		getProcedEspecialDiversoRN().remover(codigo);
	}
	
	
	public List<ScoMaterial> getListaMateriaisRMAutomatica(Integer gmtCodigo, String nome, int maxResults) {
		return getComprasFacade().obterMateriaisRMAutomatica(gmtCodigo, nome, maxResults);
	}
	
	public List<ScoMaterial> getListaMateriaisRMAutomatica(Integer gmtCodigo, String nome){
		return getComprasFacade().obterMateriaisRMAutomatica(gmtCodigo, nome);
	}
	
	
	public void criarFatProcedHospInternos(Short euuSeq, String descricao, DominioSituacao situacao) throws ApplicationBusinessException {
		this.getProcedEspecialDiversoRN().criarFatProcedHospInternos(euuSeq, descricao, situacao);
	}
	
	
	/** GET **/
	protected MpmProcedEspecialDiversoDAO getMpmProcedEspecialDiversoDAO() {
		return mpmProcedEspecialDiversoDAO;
	}
	
	protected MpmProcedEspecialRmDAO getMpmProcedEspecialRmDAO() {
		return mpmProcedEspecialRmDAO;
	}
	
	protected IComprasFacade getComprasFacade() {
		return comprasFacade;
	}
	
	protected ProcedEspecialDiversoRN getProcedEspecialDiversoRN() {
		return procedEspecialDiversoRN;
	}
}