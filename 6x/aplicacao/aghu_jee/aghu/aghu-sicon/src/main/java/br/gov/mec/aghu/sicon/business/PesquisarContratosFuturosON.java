package br.gov.mec.aghu.sicon.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.ItensPACVO;
import br.gov.mec.aghu.dominio.DominioModalidadeEmpenho;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoFaseSolicitacao;
import br.gov.mec.aghu.model.ScoItemLicitacao;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoServico;
import br.gov.mec.aghu.sicon.cadastrosbasicos.business.ICadastrosBasicosSiconFacade;
import br.gov.mec.aghu.sicon.dao.ScoContratoDAO;
import br.gov.mec.aghu.sicon.vo.LicitacaoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ListaDetalhesItensLicVO;
import br.gov.mec.aghu.sicon.vo.ListaLicitacaoVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class PesquisarContratosFuturosON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(PesquisarContratosFuturosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPacFacade pacFacade;

@EJB
private ICadastrosBasicosSiconFacade cadastrosBasicosSiconFacade;

@EJB
private IComprasFacade comprasFacade;

@Inject
private ScoContratoDAO scoContratoDAO;

	private static final long serialVersionUID = -4327135150258159174L;

	/**
	 * Param: Licitação Verifica a inexistência de registros Ativos(INATIVOS),
	 * em Compras/Serviço, SE PELO MENOS UM não for, retorna FALSE, caso todos
	 * forem ativos retorna TRUE. FALSE - pinta de vermelho - TRUE - pinta de
	 * verde
	 **/
	public Boolean possuiCodSiasg(Integer numeroLicitacao)
			throws ApplicationBusinessException {
		
		List<ItensPACVO> listaItensPACVOs = getPacFacade().pesquisarRelatorioItensPAC(numeroLicitacao, false);
		Boolean licPossuiSiasg = Boolean.FALSE;

		for (ItensPACVO item : listaItensPACVOs) {
			if (DominioTipoFaseSolicitacao.C.equals(item.getTipoFaseSolicitacao())) {
				ScoMaterial material = getPacFacade().obterMaterialPorChavePrimaria(item.getCodigoMaterial());
				licPossuiSiasg = materialPossuiCodSiasg(material);
				if (!licPossuiSiasg.equals(Boolean.TRUE)) {
					break;
				}
			} else {
				ScoServico servico = getPacFacade().obterServicoPorChavePrimaria(item.getCodigoMaterial());
				licPossuiSiasg = servicoPossuiCodSiasg(servico);
				if (!licPossuiSiasg.equals(Boolean.TRUE)) {
					break;
				}
			}
		}
		return licPossuiSiasg;
	}

	/**
	 * Montar a Lista de itens da licitação na página pesquisarContratosFuturos.xhtml
	 * Verifica a inexistência de registros Ativos(INATIVOS), em Compras/Serviço, SE PELO MENOS UM
	 * não for, retorna FALSE, caso todos forem ativos retorna TRUE.
	 * FALSE - pinta de vermelho - TRUE - pinta de verde
	 **/
	private List<ListaLicitacaoVO> criarListaLicitacoes(List<ScoLicitacao> licitacoes) throws BaseException {
		List<ListaLicitacaoVO> listaLicVO = new ArrayList<ListaLicitacaoVO>();
		
		for (ScoLicitacao licitacao : licitacoes) {
			ListaLicitacaoVO itemListaLicitacaoVO = new ListaLicitacaoVO();
			itemListaLicitacaoVO.setLicitacao(licitacao);
			itemListaLicitacaoVO.setbTemSiasg(possuiCodSiasg(licitacao.getNumero()));
			
			if (possuiCodSiasg(licitacao.getNumero()).equals(Boolean.TRUE)) {
				// TRUE - pinta de verde MENSAGEM_LIC_POSSUI_SIASG
				//itemListaLicitacaoVO.setToolTipComSiasg(super.getResourceBundleValue("MENSAGEM_LIC_POSSUI_SIASG"));
				
				String MENSAGEM_LIC_POSSUI_SIASG = "Item da Licitação em questão possue código SIASG.";
				//itemListaLicitacaoVO.setToolTipComSiasg(this.getResourceBundleValue("MENSAGEM_LIC_POSSUI_SIASG"));
				itemListaLicitacaoVO.setToolTipComSiasg(MENSAGEM_LIC_POSSUI_SIASG);
			} else {
				// FALSE - pinta de vermelho MENSAGEM_LIC_NÃO_POSSUI_SIASG
				String MENSAGEM_LIC_NAO_POSSUI_SIASG = "Item da Licitação em questão não possue código SIASG.";
				//itemListaLicitacaoVO.setToolTipSemSiasg(this.getResourceBundleValue("MENSAGEM_LIC_NAO_POSSUI_SIASG"));
				itemListaLicitacaoVO.setToolTipSemSiasg(MENSAGEM_LIC_NAO_POSSUI_SIASG);
			}
			listaLicVO.add(itemListaLicitacaoVO);
		}
		return listaLicVO;
	}
	/**
	 * Monta a lista de licitações 
	 * @param filtro
	 * @param param
	 * @return
	 * @throws BaseException
	 */
	public List<ListaLicitacaoVO> montarListaLicitacoes(LicitacaoFiltroVO filtro, DominioModalidadeEmpenho param) throws BaseException {
		List<ScoLicitacao> lic = new ArrayList<ScoLicitacao>();
		List<Integer> listaNroLicitacoes = getScoContratoDAO().obterConsultaLicitacoesVO(filtro, param);
		for (Integer item : listaNroLicitacoes) {
			lic.add(getPacFacade().obterLicitacao(item));	
		}
		return criarListaLicitacoes(lic);	
	}
	
	/**
	 * @param listaDetalhesItensLic
	 * @return Lista dos detalhes dos itens da licitação do grid da página modalDetalhesLicitacao.xhtml
	 */
	public List<ListaDetalhesItensLicVO> montarlistaDetalhesItensLic(List<Object[]> listaDetalhesItensLic) throws BaseException {
		List<ListaDetalhesItensLicVO> listaVO = new ArrayList<ListaDetalhesItensLicVO>();
		ScoItemLicitacao itemLicitacao;
		Boolean siasgMat = Boolean.FALSE;
		Boolean siasServ = Boolean.FALSE;
		Boolean ItemLicPossuiSiasg = Boolean.FALSE;
		
		for (Object[] itensList : listaDetalhesItensLic) {
			ListaDetalhesItensLicVO itens = new ListaDetalhesItensLicVO();
			itens.setLicitacao((Integer) itensList[0]);
			itens.setItem((Short) itensList[1]);
			itens.setDescTipo((String) itensList[2]);
			itens.setUnidMedida((String) itensList[3]);
			itens.setQtde((Integer) itensList[4]);
			itens.setValorUnitario((Double) itensList[5]);
			
			itemLicitacao = (getComprasFacade().obterItemLicitacaoPorNumeroLicitacaoENumeroItem(
					(Integer) itensList[0], (Short) itensList[1]));
			
			ScoMaterial material = getPacFacade().obterMaterialPorChavePrimaria(itemLicitacao.getCodMatServ());	
			if(material != null){
				siasgMat = materialPossuiCodSiasg(material);
			}
			
			ScoServico servico = getPacFacade().obterServicoPorChavePrimaria(itemLicitacao.getCodMatServ());
			if(servico!=null){
				siasServ = servicoPossuiCodSiasg(servico);
			}
							
			ItemLicPossuiSiasg = siasgMat || siasServ;
			itens.setbItemTemSiasg(ItemLicPossuiSiasg);
			
			if(ItemLicPossuiSiasg.equals(Boolean.TRUE)){
				// TRUE - pinta de verde MENSAGEM_ITEM_POSSUI_SIASG
				itens.setToolTipItemComSiasg(super.getResourceBundleValue("MENSAGEM_ITEM_POSSUI_SIASG"));
			}else{
				// FALSE - pinta de vermelho MENSAGEM_ITEM_NAO_POSSUI_SIASG
				itens.setToolTipItemSemSiasg(super.getResourceBundleValue("MENSAGEM_ITEM_NAO_POSSUI_SIASG"));
			}

			listaVO.add(itens);
		}
		return listaVO;
	}	
	

	private boolean materialPossuiCodSiasg(ScoMaterial material) {
		return getCadastrosBasicosSiconFacade().pesquisarMaterialSicon(null, material, DominioSituacao.A, null) != null;
	}
	
	private boolean servicoPossuiCodSiasg(ScoServico servico) {
		return getCadastrosBasicosSiconFacade().pesquisarServicoSicon(null, servico, DominioSituacao.A, null) != null;
	}

	protected ICadastrosBasicosSiconFacade getCadastrosBasicosSiconFacade() {
		return this.cadastrosBasicosSiconFacade;
	}
	
	protected IPacFacade getPacFacade() {
		return this.pacFacade;
	}
		
	protected IComprasFacade getComprasFacade() {
		return this.comprasFacade;
	}
	
	protected ScoContratoDAO getScoContratoDAO() {
		return scoContratoDAO;
	}
}
