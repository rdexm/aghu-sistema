package br.gov.mec.aghu.estoque.ejb;

import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceItemRmsDAO;
import br.gov.mec.aghu.estoque.dao.SceReqMateriaisDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceItemRms;
import br.gov.mec.aghu.model.SceReqMaterial;
import br.gov.mec.aghu.model.SceTipoMovimentoId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
@SuppressWarnings({"PMD.PackagePrivateBaseBusiness","PMD.AtributoEmSeamContextManager"})
public class GerarRequisicaoMaterialBean extends BaseBusiness implements GerarRequisicaoMaterialBeanLocal {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7171314234902594315L;
	
	private static final Log LOG = LogFactory.getLog(GerarRequisicaoMaterialBean.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IComprasFacade comprasFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private SceReqMateriaisDAO sceReqMateriaisDAO;
	
	@Inject
	private SceItemRmsDAO sceItemRmsDAO;
		
	@Resource
	protected SessionContext ctx;
	
	public enum GerarRequisicaoMaterialBeanExceptionCode implements BusinessExceptionCode {
		ERRO_GRAVAR_REQUISICAO_MATERIAL;
	}

	@Override
	public void gravarRequisicaoMaterial(SceReqMaterial sceReqMateriais, String nomeMicrocomputador) throws BaseException {
		
		try {
			this.popularRequisicaoMaterial(sceReqMateriais);
			getEstoqueFacade().persistirRequisicaoMaterial(sceReqMateriais, nomeMicrocomputador);
			
		} 
		catch (Exception e) {
			if(e instanceof BaseException){
				throw e;	
			}
			throw new BaseException(GerarRequisicaoMaterialBeanExceptionCode.ERRO_GRAVAR_REQUISICAO_MATERIAL);
		}
	}
	
	private void popularRequisicaoMaterial(SceReqMaterial requisicao){
		if(requisicao.getServidor()==null && requisicao.getMatricula()!=null && requisicao.getVinCodigo()!=null){
			requisicao.setServidor(this.servidorLogadoFacade.obterServidorPorChavePrimaria(requisicao.getMatricula(), requisicao.getVinCodigo()));
		}
		if(requisicao.getAlmoxarifado()==null){
			requisicao.setAlmoxarifado(this.estoqueFacade.obterAlmoxarifadoPorId(requisicao.getSeqAlmoxarifado()));
		}
		if(requisicao.getCentroCusto()==null){
			requisicao.setCentroCusto(this.estoqueFacade.obterFccCentroCustos(requisicao.getCodigoCentroCustoRequisitante()));
		}
		if(requisicao.getCentroCustoAplica()==null){
			requisicao.setCentroCustoAplica(this.estoqueFacade.obterFccCentroCustos(requisicao.getCodigoCentroCustoAplicacao()));
		}
		if(requisicao.getGrupoMaterial()==null && requisicao.getCodigoGrupoMaterial()!=null){
			requisicao.setGrupoMaterial(this.comprasFacade.obterGrupoMaterialPorId(requisicao.getCodigoGrupoMaterial()));
		}
		if(requisicao.getTipoMovimento()==null){
			SceTipoMovimentoId id = new SceTipoMovimentoId();
			id.setSeq(requisicao.getTmvSeq());
			id.setComplemento(requisicao.getTmvComplemento());
			requisicao.setTipoMovimento(this.estoqueFacade.obterTipoMovimentoPorChavePrimaria(id));
		}
		if(requisicao.getAtendimento()==null && requisicao.getAtendimentoSeq()!=null){
			requisicao.setAtendimento(this.aghuFacade.obterAghAtendimentoPorChavePrimaria(requisicao.getAtendimentoSeq()));
		}
	}
	
	@Override
	public void gravarItensRequisicaoMaterial(SceItemRms sceItemRms, String nomeMicrocomputador) throws BaseException {
		
		try {
			
			getEstoqueFacade().persistirItensRequisicaoMaterial(sceItemRms, Boolean.FALSE, nomeMicrocomputador);
			getSceItemRmsDAO().flush();
	
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		}
		
	}
	
	@Override
	public void excluirItemRequisicaoMaterial(SceItemRms sceItemRms, Integer countItensLista, Boolean estorno) throws BaseException {
		
		try {
		
			getEstoqueFacade().excluirItemRequisicaoMaterial(sceItemRms, countItensLista, estorno);	
			getSceItemRmsDAO().flush();
	
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		}
	}
	
	@Override
	public void gravarSceEstoqueAlmoxarifado(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador)
			throws BaseException {
		
		try {
			
			getEstoqueFacade().persistirEstoqueAlmox(estoqueAlmox, nomeMicrocomputador);	
			getReqMateriaisDAO().flush();
			
		} catch (BaseException e) {
			LOG.error(e.getMessage(), e);
			this.ctx.setRollbackOnly();
			throw e;
		}
	}
	
	protected SceReqMateriaisDAO getReqMateriaisDAO(){
		return sceReqMateriaisDAO;
	}
	
	protected SceItemRmsDAO getSceItemRmsDAO(){
		return sceItemRmsDAO;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}
	
}
