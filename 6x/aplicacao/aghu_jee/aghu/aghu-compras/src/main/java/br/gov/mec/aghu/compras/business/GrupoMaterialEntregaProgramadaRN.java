package br.gov.mec.aghu.compras.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.compras.dao.ScoItemAutorizacaoFornDAO;
import br.gov.mec.aghu.compras.vo.EntregaProgramadaGrupoMaterialVO;
import br.gov.mec.aghu.compras.vo.FiltroGrupoMaterialEntregaProgramadaVO;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class GrupoMaterialEntregaProgramadaRN extends BaseBusiness {

	private static final long serialVersionUID = -8819518903346077958L;
	private static final Log LOG = LogFactory.getLog(GrupoMaterialEntregaProgramadaRN.class);

	public enum AcessoFornecedorRNExceptionCode implements BusinessExceptionCode {
		MENSAGEM_CODIGO_CADASTRADO, MENSAGEM_NENHUM_CONTATO_ENCONTRADO;
	}

	@EJB
	private IParametroFacade parametroFacade;

	@Inject
	private ScoItemAutorizacaoFornDAO scoItemAutorizacaoFornDAO;

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	public List<EntregaProgramadaGrupoMaterialVO> obtemAutFornecimentosEntregasProgramadas(FiltroGrupoMaterialEntregaProgramadaVO filtro,
			Date dataLiberacao) throws ApplicationBusinessException {

		AghParametros parametroDataInicial = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_INICIAL);
		AghParametros parametroDataFinal = getParametroFacade().buscarAghParametro(
				AghuParametrosEnum.P_AGHU_CONSULTA_PROG_ENTG_GLOBAL_DT_FINAL);

		Boolean materialEstocavel = null;
		if (filtro.getMaterialEstocavel() != null) {
			materialEstocavel = DominioSimNao.S.equals(filtro.getMaterialEstocavel());
		}

		List<EntregaProgramadaGrupoMaterialVO> listagem = getScoItemAutorizacaoFornDAO().obtemEntregaGlobalPorGrupoMaterial(
				filtro.getGrupoMaterial() == null ? null : filtro.getGrupoMaterial().getCodigo(),
				filtro.getFornecedor() == null ? null : filtro.getFornecedor().getNumero(), materialEstocavel,
				filtro.getDataInicioEntrega(), parametroDataInicial.getVlrData(), filtro.getDataFimEntrega(),
				parametroDataFinal.getVlrData(), dataLiberacao);

		return listagem;
	}

	protected ScoItemAutorizacaoFornDAO getScoItemAutorizacaoFornDAO() {
		return this.scoItemAutorizacaoFornDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return this.parametroFacade;
	}

}
