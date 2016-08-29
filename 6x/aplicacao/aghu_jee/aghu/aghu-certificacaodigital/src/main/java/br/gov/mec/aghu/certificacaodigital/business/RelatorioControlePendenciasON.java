package br.gov.mec.aghu.certificacaodigital.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.certificacaodigital.dao.AghVersaoDocumentoDAO;
import br.gov.mec.aghu.certificacaodigital.vo.RelatorioControlePendenciasVO;
import br.gov.mec.aghu.dominio.DominioOrdenacaoRelatorioControlePendencias;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FccCentroCustos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class RelatorioControlePendenciasON extends BaseBusiness {
	
	private static final Log LOG = LogFactory.getLog(RelatorioControlePendenciasON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private ICascaFacade cascaFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private AghVersaoDocumentoDAO aghVersaoDocumentoDAO;
	
	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 80901124920841494L;

	public enum RelatorioControlePendenciasONExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CERTIFICACAO_DIGITAL_NAO_HABILITADA, MENSAGEM_USUARIO_SEM_PERMISSAO_PARA_PESQUISA;
	}
	
	public List<RelatorioControlePendenciasVO> pesquisaPendenciaAssinaturaDigital(
			RapServidores rapServidores, FccCentroCustos fccCentroCustos,
			DominioOrdenacaoRelatorioControlePendencias ordenacao) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
			//verificar se o HU possui parâmetro para certificação digital
			AghParametros aghParametros = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_AGHU_CERTIFICACAO_DIGITAL);
			if(aghParametros.getVlrTexto().equals("N")){
				throw new ApplicationBusinessException(RelatorioControlePendenciasON.
						RelatorioControlePendenciasONExceptionCode.MENSAGEM_CERTIFICACAO_DIGITAL_NAO_HABILITADA, 
						getCadastrosBasicosInternacaoFacade().recuperarNomeInstituicaoHospitalarLocal());
			}
		
			if (!temTargetPesquisa(servidorLogado.getUsuario())) {				
				throw new ApplicationBusinessException(RelatorioControlePendenciasON.
						RelatorioControlePendenciasONExceptionCode.MENSAGEM_USUARIO_SEM_PERMISSAO_PARA_PESQUISA);
			}														
			
			List<Object[]> lista = getAghVersaoDocumentoDAO().pesquisaPendenciaAssinaturaDigital(rapServidores,fccCentroCustos,ordenacao);
			return getListRelatorioControlePendenciasVO(lista);
			
	}
	
	private List<RelatorioControlePendenciasVO> getListRelatorioControlePendenciasVO(List<Object[]> lista){
		List<RelatorioControlePendenciasVO> listResult = new ArrayList<RelatorioControlePendenciasVO>();
		
		for (Object[] listFileds : lista) {
			RelatorioControlePendenciasVO relatorioControlePendenciasVO = new RelatorioControlePendenciasVO();
			
			relatorioControlePendenciasVO.setNomePessoaFisica((String)listFileds[0]);
			relatorioControlePendenciasVO.setMatriculaId((Integer)listFileds[1]);
			relatorioControlePendenciasVO.setCodigoVinculo((Short)listFileds[2]);
			relatorioControlePendenciasVO.setDescricaoCentroCusto((String)listFileds[3]!=null?(String)listFileds[3]:(String)listFileds[4]);
			relatorioControlePendenciasVO.setCountDocumentos((Long)listFileds[5]);
			
			
			listResult.add(relatorioControlePendenciasVO);
		}
		
		return listResult;
	} 
	
	protected boolean temTargetPesquisa(String loginUsuario) throws ApplicationBusinessException {
		final String componenteAssinaturaDigital = "samisAssinaturaDigital";
		final String metodoPesquisaDocumentos = "pesquisarDocumentosPendentes";

		return getICascaFacade().usuarioTemPermissao(loginUsuario, componenteAssinaturaDigital, metodoPesquisaDocumentos);
	}
	
	protected AghVersaoDocumentoDAO getAghVersaoDocumentoDAO(){
		return aghVersaoDocumentoDAO;
	}
	
	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected ICadastrosBasicosInternacaoFacade getCadastrosBasicosInternacaoFacade() {
		return this.cadastrosBasicosInternacaoFacade;
	}
	
	protected ICascaFacade getICascaFacade() {
		return cascaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
