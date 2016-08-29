package br.gov.mec.aghu.blococirurgico.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.dao.MbcCirurgiasDAO;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialPorCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialPorCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcMaterialPorCirurgia;
import br.gov.mec.aghu.model.MbcMaterialPorCirurgiaId;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade
 * MbcMaterialPorCirurgia.
 * 
 * @author fbraganca
 * 
 */

@Stateless
public class MbcMaterialPorCirurgiaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcMaterialPorCirurgiaRN.class);
	private static final long serialVersionUID = -6454932636690343540L;

	@Inject
	private MbcMaterialPorCirurgiaDAO mbcMaterialPorCirurgiaDAO;
	@Inject
	private MbcCirurgiasDAO mbcCirurgiasDAO;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IComprasFacade comprasFacade;
	@EJB
	private IAghuFacade aghuFacade;

	
	protected enum MbcMaterialPorCirurgiaRNExceptionCode implements BusinessExceptionCode {
		MBC_00542, MENSAGEM_MATERIAL_JA_CADASTRADO
	}
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	/**
	 * Persiste lista de MbcMaterialPorCirurgia
	 * 
	 * @param materiais
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException
	 */
	public void persistirMaterialPorCirurgia(List<MaterialPorCirurgiaVO> materiais, AghParametros pDispensario) throws ApplicationBusinessException {
		
		for (MaterialPorCirurgiaVO vo : materiais) {
			
			MbcMaterialPorCirurgia mbcMaterialPorCirurgia = new MbcMaterialPorCirurgia();
			if (vo.getCrgSeqId() != null) {
				mbcMaterialPorCirurgia = this.mbcMaterialPorCirurgiaDAO.obterPorChavePrimaria(new MbcMaterialPorCirurgiaId(vo.getCrgSeqId(), vo.getMatCodigo()));
			}
			mbcMaterialPorCirurgia.setMbcCirurgias(this.mbcCirurgiasDAO.obterCirurgiaPorSeq(vo.getCrgSeq()));
			mbcMaterialPorCirurgia.setScoMaterial(this.comprasFacade.obterMaterialPorId(vo.getMatCodigo()));
			mbcMaterialPorCirurgia.setScoUnidadeMedida(this.comprasFacade.obterPorCodigo(vo.getUmdCodigo()));
			mbcMaterialPorCirurgia.setQuantidade(vo.getQuantidade());
			
			if (CoreUtil.maior(mbcMaterialPorCirurgia.getQuantidade(), 0)) {
				
				this.executarAntesDePersistir(mbcMaterialPorCirurgia);
				atualizarMateriaisPorCirurgia(mbcMaterialPorCirurgia);
				
				if (mbcMaterialPorCirurgia.getId() == null) {
					MbcMaterialPorCirurgiaId id = new MbcMaterialPorCirurgiaId();
					id.setCrgSeq(mbcMaterialPorCirurgia.getMbcCirurgias().getSeq());
					id.setMatCodigo(mbcMaterialPorCirurgia.getScoMaterial().getCodigo());
					
					mbcMaterialPorCirurgia.setId(id);
					this.mbcMaterialPorCirurgiaDAO.persistir(mbcMaterialPorCirurgia);
					
				} else {
					mbcMaterialPorCirurgia = mbcMaterialPorCirurgiaDAO.merge(mbcMaterialPorCirurgia);
				}
				
				// #42097
				if (this.aghuFacade.isOracle() && this.aghuFacade.isHCPA() && pDispensario.getVlrTexto().equalsIgnoreCase("S")) {
					if (vo.getStatus() != null) {
						switch (vo.getStatus()) {
						case 1:
							this.mbcMaterialPorCirurgiaDAO.atualizarDispensarioPorMaterialCirurgico((short) 3, vo.getMatCodigo(), vo.getCrgSeq());
							break;
							
						case 3:
							this.mbcMaterialPorCirurgiaDAO.atualizarDispensarioPorMaterialCirurgico((short) 2, vo.getMatCodigo(), vo.getCrgSeq());
							break;

						default:
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * Trigger
	 * 
	 * ORADB: MBCT_MCI_BRI, MBCT_MCI_BRU
	 * 
	 * @param materialPorCirurgia
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException 
	 */
	private void executarAntesDePersistir(MbcMaterialPorCirurgia materialPorCirurgia) throws ApplicationBusinessException, ApplicationBusinessException {
		//RN1
		materialPorCirurgia.setCriadoEm(new Date());
		//RN2
		materialPorCirurgia.setRapServidores(servidorLogadoFacade.obterServidorLogado());			
	}
	
	public void validarMaterialNovo(MbcMaterialPorCirurgia materialPorCirurgia, List<MbcMaterialPorCirurgia> listaMateriais) throws ApplicationBusinessException {
		this.verificarMaterialUnico(materialPorCirurgia, listaMateriais);
	}
	
	private void verificarMaterialUnico(MbcMaterialPorCirurgia materialPorCirurgia, List<MbcMaterialPorCirurgia> listaMateriais) throws ApplicationBusinessException {
		
		for (MbcMaterialPorCirurgia mbcMaterialPorCirurgia : listaMateriais) {
			if (mbcMaterialPorCirurgia.getScoMaterial().getCodigo().equals(materialPorCirurgia.getScoMaterial().getCodigo())) {
				throw new ApplicationBusinessException(MbcMaterialPorCirurgiaRNExceptionCode.MENSAGEM_MATERIAL_JA_CADASTRADO);
			}
		}
	}
	
	/**
	 * ORADB: FFC_INTERFACE_MBC
	 * 
	 * @param materialPorCirurgia
	 * @throws ApplicationBusinessException
	 */
	private void atualizarMateriaisPorCirurgia(MbcMaterialPorCirurgia materialPorCirurgia) throws ApplicationBusinessException {
		
		if (materialPorCirurgia.getId() == null) {
			
			mbcMaterialPorCirurgiaDAO.atualizarMateriaisPorCirurgiaCallableStatement(
					materialPorCirurgia.getMbcCirurgias().getSeq(), 
					materialPorCirurgia.getScoMaterial().getCodigo(), 
					materialPorCirurgia.getQuantidade(), 
					MbcMaterialPorCirurgiaDAO.TipoOperacao.I);
			
		} else {
			
			MbcMaterialPorCirurgia mpc = mbcMaterialPorCirurgiaDAO.obterOriginal(materialPorCirurgia.getId());
			
			if (mpc != null && materialPorCirurgia != null) {
				if (!mpc.getQuantidade().equals(materialPorCirurgia.getQuantidade())) {					
					mbcMaterialPorCirurgiaDAO.atualizarMateriaisPorCirurgiaCallableStatement(
							materialPorCirurgia.getMbcCirurgias().getSeq(), 
							materialPorCirurgia.getScoMaterial().getCodigo(), 
							materialPorCirurgia.getQuantidade(), MbcMaterialPorCirurgiaDAO.TipoOperacao.A);				
				}
			}
		}
	}
	
	/**
	 * Exclui instância de MbcMaterialPorCirurgia
	 * 
	 * @param materialPorCirurgia
	 * @param obterLoginUsuarioLogado
	 * @throws ApplicationBusinessException 
	 */
	public void excluirMaterialPorCirurgia(MaterialPorCirurgiaVO materialPorCirurgia) throws ApplicationBusinessException {
		
		mbcMaterialPorCirurgiaDAO.atualizarMateriaisPorCirurgiaCallableStatement(materialPorCirurgia.getCrgSeq(), 
				materialPorCirurgia.getMatCodigo(), materialPorCirurgia.getQuantidade(), MbcMaterialPorCirurgiaDAO.TipoOperacao.E);
		MbcMaterialPorCirurgia materialPorCirurgiaOriginal = null;  
		if(materialPorCirurgia.getCrgSeqId() != null) {
			materialPorCirurgiaOriginal = mbcMaterialPorCirurgiaDAO.obterPorChavePrimaria(
					new MbcMaterialPorCirurgiaId(materialPorCirurgia.getCrgSeqId(), materialPorCirurgia.getMatCodigo()));
		}
		mbcMaterialPorCirurgiaDAO.remover(materialPorCirurgiaOriginal);
	}
	
}
