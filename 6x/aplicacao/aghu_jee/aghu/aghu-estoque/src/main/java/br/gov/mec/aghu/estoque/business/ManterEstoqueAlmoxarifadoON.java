package br.gov.mec.aghu.estoque.business;


import java.math.BigDecimal;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceValidadeDAO;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceValidade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterEstoqueAlmoxarifadoON extends BaseBusiness {


@EJB
private SceEstoqueAlmoxarifadoRN sceEstoqueAlmoxarifadoRN;

private static final Log LOG = LogFactory.getLog(ManterEstoqueAlmoxarifadoON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SceValidadeDAO sceValidadeDAO;

@EJB
private IParametroFacade parametroFacade;

@Inject
private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -9011975512897453931L;
	
	public enum ManterEstoqueAlmoxarifadoONExceptionCode implements BusinessExceptionCode {
		SCE_00706, SCE_00705, SCE_00698, ERRO_ALMOXARIFADO_OBRIGATORIO;
	}

	public void persistirEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador) throws BaseException{
		if(estoqueAlmox.getSeq() != null){
			updateEstoqueAlmox(estoqueAlmox, nomeMicrocomputador);
		}
		else{
			insertEstoqueAlmox(estoqueAlmox);
		}
	}
	
	public void desbloqueioQuantidadesEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{
		validaQuantidadeDesbloqueio(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio);
		updateEstoqueAlmox(estoqueAlmox, nomeMicrocomputador);
	}

	public void bloqueioQuantidadesEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio, String nomeMicrocomputador) throws BaseException{
		validaQuantidadeBloqueio(estoqueAlmox, qtdeAcaoBloqueioDesbloqueio);
		updateEstoqueAlmox(estoqueAlmox, nomeMicrocomputador);
	}

	private void insertEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox) throws BaseException{
		getSceEstoqueAlmoxarifadoRN().inserir(estoqueAlmox);		
	}

	private void updateEstoqueAlmox(SceEstoqueAlmoxarifado estoqueAlmox, String nomeMicrocomputador) throws BaseException{
		getSceEstoqueAlmoxarifadoRN().atualizar(estoqueAlmox, nomeMicrocomputador, true);
	}
	
	private void validaQuantidadeDesbloqueio(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio) throws ApplicationBusinessException{
			
		if(qtdeAcaoBloqueioDesbloqueio > estoqueAlmox.getQtdeBloqueada()){
			throw new ApplicationBusinessException(ManterEstoqueAlmoxarifadoONExceptionCode.SCE_00706);
		}else{
			estoqueAlmox.setQtdeDisponivel(estoqueAlmox.getQtdeDisponivel() + qtdeAcaoBloqueioDesbloqueio);
			estoqueAlmox.setQtdeBloqueada(estoqueAlmox.getQtdeBloqueada() - qtdeAcaoBloqueioDesbloqueio);
		}
	}

	private void validaQuantidadeBloqueio(SceEstoqueAlmoxarifado estoqueAlmox, Integer qtdeAcaoBloqueioDesbloqueio) throws ApplicationBusinessException{
		
		if(qtdeAcaoBloqueioDesbloqueio > estoqueAlmox.getQtdeDisponivel()){
			throw new ApplicationBusinessException(ManterEstoqueAlmoxarifadoONExceptionCode.SCE_00705);
		}else{
			estoqueAlmox.setQtdeDisponivel(estoqueAlmox.getQtdeDisponivel() - qtdeAcaoBloqueioDesbloqueio);
			estoqueAlmox.setQtdeBloqueada(estoqueAlmox.getQtdeBloqueada() + qtdeAcaoBloqueioDesbloqueio);
		}
	}
	
	/**
	 * Verifica ao clicar no botão gravar a obrigatoriedade de inserir validades.
	 * @param estoqueAlmoxarifado
	 * @throws BaseException
	 */
	public void verificaBotaoVoltarBloqueioDesbloqueio(Integer estoqueAlmoxarifado) throws BaseException{
		SceEstoqueAlmoxarifado estAlm = getSceEstoqueAlmoxarifadoDAO().obterEstoqueAlmoxarifadoPorId(estoqueAlmoxarifado);

		if(estAlm != null && estAlm.getIndControleValidade()){
			List<SceValidade> validades = getSceValidadeDAO().pesquisarValidadeEalSeqDataValidadeComQuantidadeDisponivel(estoqueAlmoxarifado, null);
			if(validades!=null && validades.size()==0){
				throw new ApplicationBusinessException(ManterEstoqueAlmoxarifadoONExceptionCode.SCE_00698);
			}
		}
	}

	public SceEstoqueAlmoxarifadoRN getSceEstoqueAlmoxarifadoRN(){
		return sceEstoqueAlmoxarifadoRN;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	private SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}
	
	private SceValidadeDAO getSceValidadeDAO(){
		return sceValidadeDAO;
	}
	
	/**
	 * Pesquisa estoque almoxarifado, com material e demais campos necessários
	 * @param parametro
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public List<SceEstoqueAlmoxarifado> pesquisarMateriaisEstoquePorCodigoDescricao(String parametro, Short seqAlmoxarifado,
			List<Integer> listaGrupos, Boolean somenteEstocaveis, Boolean somenteDiretos) throws ApplicationBusinessException {
		if (seqAlmoxarifado == null) {
			throw new ApplicationBusinessException(ManterEstoqueAlmoxarifadoONExceptionCode.ERRO_ALMOXARIFADO_OBRIGATORIO);
		}
		BigDecimal codigoFornecedor = obterCodigoFornecedorEmParametro(AghuParametrosEnum.P_FORNECEDOR_PADRAO);
		return getSceEstoqueAlmoxarifadoDAO().pesquisarMateriaisEstoquePorCodigoDescricaoAlmoxarifado(parametro, seqAlmoxarifado,
				codigoFornecedor, listaGrupos, somenteEstocaveis, somenteDiretos);
	}
	
	/**
	 * Obtém código do fornecedor, por parâmetro
	 * @param pCodigoAlmoxarifado
	 * @return
	 * @throws ApplicationBusinessException
	 */
	private BigDecimal obterCodigoFornecedorEmParametro(AghuParametrosEnum pCodigoAlmoxarifado) throws ApplicationBusinessException {
		return getParametroFacade().buscarAghParametro(pCodigoAlmoxarifado).getVlrNumerico();
	}
}
