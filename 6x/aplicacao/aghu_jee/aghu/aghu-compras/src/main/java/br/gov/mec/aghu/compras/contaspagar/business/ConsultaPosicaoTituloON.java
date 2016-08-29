package br.gov.mec.aghu.compras.contaspagar.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.contaspagar.dao.FcpTituloDAO;
import br.gov.mec.aghu.compras.contaspagar.vo.FiltroPesquisaPosicaoTituloVO;
import br.gov.mec.aghu.compras.contaspagar.vo.PosicaoTituloVO;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceBoletimOcorrencias;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.utils.DateUtil;

@Stateless
public class ConsultaPosicaoTituloON extends BaseBusiness {

	private static final long serialVersionUID = 1080094201843092855L;

	private static final Log LOG = LogFactory.getLog(ConsultaPosicaoTituloON.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	private enum ConsultaPosicaoTituloONException implements BusinessExceptionCode {
		MENSAGEM_PERIODO_MAXIMO_PERMITIDO_PESQUISA;
	}
	
	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private FcpTituloDAO fcpTituloDAO;

	public void validarIntervaloDataGeracaoPesquisaTitulo(final Date dataInicio, final Date dataFim) throws ApplicationBusinessException {
		if (dataInicio != null && dataFim != null) {
			AghParametros parametro = this.parametroFacade.buscarAghParametro(AghuParametrosEnum.P_AGHU_INTERVALO_MAXIMO_PESQUISA_POS_TITULOS);
			if (parametro == null) {
				throw new IllegalArgumentException();
			}
			int periodoMaximo = parametro.getVlrNumerico().intValue();
			if (DateUtil.obterQtdAnosEntreDuasDatas(dataInicio, dataFim) >= periodoMaximo) {
				throw new ApplicationBusinessException(ConsultaPosicaoTituloONException.MENSAGEM_PERIODO_MAXIMO_PERMITIDO_PESQUISA, periodoMaximo);
			}
		}
	}
	
	public List<PosicaoTituloVO> pesquisarPosicaoTitulo(Integer firstResult, Integer maxResult, FiltroPesquisaPosicaoTituloVO filtro) {
		List<PosicaoTituloVO> resultado = fcpTituloDAO.pesquisarPosicaoTitulo(firstResult, maxResult, filtro);
		for (PosicaoTituloVO vo : resultado) {
			if(vo.getNotaRecebimento() != null){
				List<SceBoletimOcorrencias> boletins = estoqueFacade.pesquisarBoletimOcorrenciaNotaRecebimento(vo.getNotaRecebimento());
				if(!boletins.isEmpty()){
					vo.setBo(boletins.get(0).getSeq());
				}
			}
		}
		return resultado;
	}

}
