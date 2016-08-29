package br.gov.mec.aghu.blococirurgico.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.MbcMaterialPorCirurgiaRN.MbcMaterialPorCirurgiaRNExceptionCode;
import br.gov.mec.aghu.blococirurgico.dao.MbcMaterialPorCirurgiaDAO;
import br.gov.mec.aghu.blococirurgico.vo.MaterialPorCirurgiaVO;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.dominio.DominioIndRespProc;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MbcCirurgias;
import br.gov.mec.aghu.model.MbcMaterialImpNotaSalaUn;
import br.gov.mec.aghu.model.SceConversaoUnidadeConsumos;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;

/**
 * Classe responsável pelas regras de negócio relacionadas à entidade
 * MbcMaterialPorCirurgia.
 * 
 * @author fbraganca
 * 
 */

@Stateless
public class MbcMaterialPorCirurgiaON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MbcMaterialPorCirurgiaON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	

	@Inject
	private MbcMaterialPorCirurgiaDAO mbcMaterialPorCirurgiaDAO;


	@EJB
	private IEstoqueFacade iEstoqueFacade;

	@EJB
	private IComprasFacade iComprasFacade;
	
	@EJB
	private IAghuFacade aghuFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2371957586421626543L;

	/**
	 * 
	 * ORADB 	  MBCF_MAT_CONSUMIDO
	 * PROCEDURE WHEN-NEW-BLOCK-INSTANCE
	 * 
	 * @param crgSeq
	 * @throws ApplicationBusinessException 
	 */
	public List<MaterialPorCirurgiaVO> pesquisarRegraWhenNewBlockInstance(MbcCirurgias cirurgia, AghParametros pGrMatOrtProt,
			AghParametros pDispensario) throws ApplicationBusinessException {
		
		List<MaterialPorCirurgiaVO> listaMbcMaterialPorCirurgia = new ArrayList<MaterialPorCirurgiaVO>();
		listaMbcMaterialPorCirurgia = this.getMbcMaterialPorCirurgiaDAO().pesquisarMaterialConsumidoPorCirurgia(cirurgia.getSeq());
		List<MbcMaterialImpNotaSalaUn> listaMbcMaterialImpNotaSalaUn = null;
		boolean listaGrMatOrtProt = true;
		
		// verificar se todos os incluidos são do mesmo grupo
		if (listaMbcMaterialPorCirurgia != null && !listaMbcMaterialPorCirurgia.isEmpty()) {
			for (MaterialPorCirurgiaVO material : listaMbcMaterialPorCirurgia) {
				if (material.getGmtCodigo().intValue() != pGrMatOrtProt.getVlrNumerico().intValue()) {
					listaGrMatOrtProt = false;
				}
			}
		}

		if (listaGrMatOrtProt) {
			
			listaMbcMaterialImpNotaSalaUn = this.getMbcMaterialPorCirurgiaDAO()
				.pesquisarMateriaisComSubQueryMbcUnidadenotaSala(cirurgia, 
						DominioIndRespProc.AGND, 
						DominioSituacao.A, 
						DominioSimNao.S, 
						pGrMatOrtProt);
			
			//CASO NAO TENHA ENCOTRADO listaScoMaterial
			if(listaMbcMaterialImpNotaSalaUn == null	|| listaMbcMaterialImpNotaSalaUn.size() == 0){
				
				listaMbcMaterialImpNotaSalaUn = this.getMbcMaterialPorCirurgiaDAO()
				.pesquisarMateriaisComSubQueryMbcUnidadenotaSala(cirurgia,
						DominioSituacao.A, pGrMatOrtProt);
			}
			
			//CASO !!AINDA!! NAO TENHA ENCOTRADO listaScoMaterial
			if(listaMbcMaterialImpNotaSalaUn == null || listaMbcMaterialImpNotaSalaUn.size() == 0){
				
				listaMbcMaterialImpNotaSalaUn = this.getMbcMaterialPorCirurgiaDAO()
				.pesquisarMateriaisComSubQueryMbcUnidadenotaSala(cirurgia, pGrMatOrtProt);
			}
			
			
		}
			
		if(listaMbcMaterialImpNotaSalaUn!=null && listaMbcMaterialImpNotaSalaUn.size() > 0){
			for(MbcMaterialImpNotaSalaUn mbcMaterialImpNotaSalaUn : listaMbcMaterialImpNotaSalaUn){
				MaterialPorCirurgiaVO novo = new MaterialPorCirurgiaVO();
				novo.setMatCodigo((mbcMaterialImpNotaSalaUn.getMaterial().getCodigo()));
				novo.setMatNome(mbcMaterialImpNotaSalaUn.getMaterial().getNome());
				novo.setCrgSeq(cirurgia.getSeq());
				ScoUnidadeMedida unidCons = this.getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(mbcMaterialImpNotaSalaUn.getUnidadeMedida().getCodigo());
				novo.setUmdCodigo(unidCons.getCodigo());
				novo.setUnidadeMedidaCons(unidCons.getDescricao());
				ScoUnidadeMedida unidMat = this.getComprasFacade().obterScoUnidadeMedidaPorChavePrimaria(mbcMaterialImpNotaSalaUn.getMaterial().getUnidadeMedida().getCodigo());
				novo.setUnidadeMedidaMat(unidMat.getDescricao());
				
				// #42097
				if (this.aghuFacade.isOracle() && this.aghuFacade.isHCPA() && pDispensario.getVlrTexto().equalsIgnoreCase("S")) {
					Object[] dispensario = this.getMbcMaterialPorCirurgiaDAO().obterDadosDispensarioPorMaterialCirurgico(novo.getMatCodigo(), novo.getCrgSeq());
					
					if (dispensario != null) {
						novo.setQuantidade(((Number) dispensario[0]).doubleValue());
						novo.setStatus(((Number) dispensario[1]).shortValue());
						novo.setOrigem("SS");
					}
				}
				listaMbcMaterialPorCirurgia.add(novo);
			}
		}	
		return listaMbcMaterialPorCirurgia;
	}
	
	public List<ScoUnidadeMedida> pesquisarUnidadeMedida(Integer matCodigo, Object param) {
		
		return montarListaUnidadeMedida(matCodigo, param);
	}
	
	public Long pesquisarUnidadeMedidaCount(Integer matCodigo, Object param) {
		
		List<ScoUnidadeMedida> listaUnidade = montarListaUnidadeMedida(matCodigo, param);
		if(!listaUnidade.isEmpty()){
			return Long.valueOf(listaUnidade.size());
		}
		return null;
	}
	
	private List<ScoUnidadeMedida> montarListaUnidadeMedida(Integer matCodigo, Object param) {
		List<ScoUnidadeMedida> listaUnidade = new ArrayList<ScoUnidadeMedida>();
		
		ScoMaterial material = this.getComprasFacade().obterMaterialPorCodigoDescricaoUnidadeMedida(matCodigo, param);
		if (material != null){
			listaUnidade.add(material.getUnidadeMedida());
			
		}
		
		List<SceConversaoUnidadeConsumos> listaConversaoUnidadeConsumos = this.getEstoqueFacade()
				.pesquisarConversaoPorMaterialCodigoUnidadeMaterialDescricao(matCodigo, param);
		
		for (SceConversaoUnidadeConsumos sceConversaoUnidadeConsumos : listaConversaoUnidadeConsumos) {
			listaUnidade.add(sceConversaoUnidadeConsumos.getUnidadeMedida());
		}
		
		return listaUnidade;
	}
	
	protected IComprasFacade getComprasFacade() {
		return iComprasFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return iEstoqueFacade;
	}
	
	protected String obterMensagemMaterialCadastrado() {
		return MbcMaterialPorCirurgiaRNExceptionCode.MENSAGEM_MATERIAL_JA_CADASTRADO.toString();
	}
	
	private MbcMaterialPorCirurgiaDAO getMbcMaterialPorCirurgiaDAO() {
		return mbcMaterialPorCirurgiaDAO;
	}
	
}
