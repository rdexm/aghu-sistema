package br.gov.mec.aghu.compras.autfornecimento.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.vo.FiltroProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ProgrGeralEntregaAFVO;
import br.gov.mec.aghu.compras.vo.ValidacaoFiltrosVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioTipoSolitacaoAF;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ProgramacaoGeralEntregaAFON extends BaseBusiness {
	
	private static final long serialVersionUID = -443623130151015613L;

	private static final Log LOG = LogFactory.getLog(ProgramacaoGeralEntregaAFON.class);
	
	@EJB
	private ProgramacaoGeralEntregaAFRN programacaoGeralEntregaAFRN;
	
	private enum ProgramacaoGeralEntregaAFONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CPGAF_DTI;
	}
	
	private ProgramacaoGeralEntregaAFRN getProgramacaoGeralEntregaAFRN() {
		return programacaoGeralEntregaAFRN;
	}

	public List<ProgrGeralEntregaAFVO> pesquisarItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro,Integer firstResult, Integer maxResult) throws ApplicationBusinessException {
		validarDatas(filtro.getPrevisaoDtInicial(), filtro.getPrevisaoDtFinal());
		return getProgramacaoGeralEntregaAFRN().pesquisarItensProgGeralEntregaAF(filtro,firstResult,maxResult);
	}
	
	public Long countItensProgGeralEntregaAF(FiltroProgrGeralEntregaAFVO filtro) throws ApplicationBusinessException {
		return getProgramacaoGeralEntregaAFRN().countItensProgGeralEntregaAF(filtro);
	}
	 
	public ValidacaoFiltrosVO validarVisualizacaoFiltros(FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO camposVO) {		
		if(filtro == null) {
			return camposVO;
		}				
		validarDominioTipoSolicitacaoAF(filtro, camposVO);
		habilitarCP(filtro, camposVO);
		visualizarModalidade(filtro, camposVO);		
		return camposVO;
	}

	private void validarDominioTipoSolicitacaoAF(
			FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO camposVO) {
		DominioTipoSolitacaoAF item = filtro.getTipoItem(); 
		if (item == null) {
			setCamposItemNaoInformado(camposVO);
			limparCamposSolicitacaoFiltro(filtro);
		} else {
			camposVO.setVisualizaCamposSolicitacao(true);
			if (DominioTipoSolitacaoAF.S.getDescricao().equals(item.getDescricao())) {
				setCamposItemServico(filtro, camposVO);
			} else {
				setCamposItemMaterial(camposVO);
			}
		}
	}

	private void limparCamposSolicitacaoFiltro(
			FiltroProgrGeralEntregaAFVO filtro) {
		filtro.setMaterial(null);
		filtro.setGrupoMaterial(null);
		filtro.setServico(null);
		filtro.setGrupoServico(null);
		filtro.setNroSolicitacao(null);
	}

	private void setCamposItemMaterial(ValidacaoFiltrosVO camposVO) {
		camposVO.setHabilitaServico(false);
		camposVO.setDesabilitaCurvaABC(false);
		camposVO.setHabilitaEstocavel(true);
	}

	private void setCamposItemServico(FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO camposVO) {
		camposVO.setHabilitaServico(true);
		camposVO.setDesabilitaCurvaABC(true);
		filtro.setCurvaABC(null);
		camposVO.setHabilitaEstocavel(false);
		filtro.setEstocavel(null);
	}

	private void setCamposItemNaoInformado(ValidacaoFiltrosVO camposVO) {
		camposVO.setVisualizaCamposSolicitacao(false);
		setCamposItemMaterial(camposVO);
	}
	
	private void habilitarCP(FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO campos){
		if(filtro.getNroAF() == null) {
			filtro.setCp(null);
			campos.setHabilitaCP(false);
		} else {
			campos.setHabilitaCP(true);
		}
	}
	
	private void visualizarModalidade(FiltroProgrGeralEntregaAFVO filtro, ValidacaoFiltrosVO campos) {
		DominioModalidadeEmpenho modalidade = filtro.getModalidadeEmpenho();
		if(DominioModalidadeEmpenho.CONTRATO.equals(modalidade)) {			
			campos.setVisualizaVencimento(true);
		} else {
			campos.setVisualizaVencimento(false);
			filtro.setVencimento(null);
		}
	}
	
	public void validarDatas(Date dtInicial, Date dtFinal)
			throws ApplicationBusinessException {

		if (dtInicial != null && dtFinal != null) {
			if (dtInicial.after(dtFinal)) {
				throw new ApplicationBusinessException(
						ProgramacaoGeralEntregaAFONExceptionCode.MENSAGEM_CPGAF_DTI);
			}
		}
	}

	public void pesquisarInfoComplementares(ProgrGeralEntregaAFVO item) {
		getProgramacaoGeralEntregaAFRN().pesquisarInfoComplementares(item) ;
	}

	@Override
	protected Log getLogger() {
		return LOG;
	}
}