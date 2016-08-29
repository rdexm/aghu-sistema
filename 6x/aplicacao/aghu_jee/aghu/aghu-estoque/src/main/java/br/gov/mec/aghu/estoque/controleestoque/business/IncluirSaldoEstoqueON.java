package br.gov.mec.aghu.estoque.controleestoque.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;

@Stateless
public class IncluirSaldoEstoqueON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(IncluirSaldoEstoqueON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@Inject
	private SceEstoqueGeralDAO sceEstoqueGeralDAO;
	
	@Inject
	private SceEstoqueAlmoxarifadoDAO sceEstoqueAlmoxarifadoDAO;
	
	@Inject
	private SceTipoMovimentosDAO sceTipoMovimentosDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 790369000092381504L;

	public enum SceMovimentoMaterialRNExceptionCode implements
			BusinessExceptionCode {

		SCE_00648, //
		SCE_00649,//
		MATERIAL_POSSUI_SALDO;//

	}

	public void persistir(SceMovimentoMaterial elemento, String nomeMicrocomputador)
			throws BaseException {
		
		this.atribuirTipoMovimento(elemento);// RN1
		this.verificarEstoqueAlmoxarifadoCadastradoMaterial(elemento);// RN2
		this.verificarCustoMedioMaterialEstoque(elemento);//RN3
		
		elemento.setIndEstorno(Boolean.FALSE);
		this.getEstoqueFacade().inserirMovimentoMaterial(elemento, nomeMicrocomputador);
	}

	

	/**
	 * RN1 Seta a coluna TMV_SEQ com o v_vlr_numerico<br>
	 * do parametro P_TMV_MOV_INSL e a coluna TMV_COMPLEMENTO
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 */
	protected void atribuirTipoMovimento(SceMovimentoMaterial elemento)
			throws ApplicationBusinessException {

		final Short tmvSeq = this.getParametroFacade()
				.buscarAghParametro(AghuParametrosEnum.P_TMV_MOV_INSL)
				.getVlrNumerico().shortValue();

		SceTipoMovimento tipoMovimento = this.getSceTipoMovimentosDAO()
				.obterSceTipoMovimentosAtivoPorSeq(tmvSeq);
		elemento.setTipoMovimento(tipoMovimento);
	}

	/**
	 * TRIGGER scep_ver_eal_ativo Verifica se existe estoque almoxarifado <br>
	 * cadastrado para o material
	 * 
	 * @param elemento
	 * @throws ApplicationBusinessException
	 * @throws ApplicationBusinessException
	 */
	protected void verificarEstoqueAlmoxarifadoCadastradoMaterial(
			SceMovimentoMaterial elemento) throws BaseException {

		List<SceEstoqueAlmoxarifado> estoqueAlmoxarifadoList = this
				.getSceEstoqueAlmoxarifadoDAO()
				.buscarEstoqueAlmoxarifadoPorAlmSeqMatCodigoEFrnNumero(
						elemento.getAlmoxarifado().getSeq(),
						elemento.getMaterial().getCodigo(),
						elemento.getFornecedor().getNumero());

		if (estoqueAlmoxarifadoList.isEmpty()) {
			throw new ApplicationBusinessException(
					SceMovimentoMaterialRNExceptionCode.SCE_00648);
		} else {
			SceEstoqueAlmoxarifado estoqueAlmoxarifado = estoqueAlmoxarifadoList
					.get(0);
			elemento.setUnidadeMedida(estoqueAlmoxarifado.getUnidadeMedida());
		}
	}

	/**
	 * TRIGGER scep_ver_estoque_geral Verifica o custo medio do material no
	 * estoque geral
	 * 
	 * @param elemento
	 * @throws BaseException
	 */
	protected void verificarCustoMedioMaterialEstoque(SceMovimentoMaterial elemento) 
		throws BaseException {
		
		final Date dtCompetencia = this.getParametroFacade()
		.buscarAghParametro(AghuParametrosEnum.P_COMPETENCIA).getVlrData();
		
		//setar a data competencia no objeto
		elemento.setDtCompetencia(dtCompetencia);
		
		List<SceEstoqueGeral> estoqueGeralList = this.getSceEstoqueGeralDAO()
			.pesquisarEstoqueGeralPorMaterialDataCompetencia(
					elemento.getMaterial(), dtCompetencia);
		
		if(estoqueGeralList.isEmpty()) {
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00649);
		} else {
			final SceEstoqueGeral estoqueGeral = estoqueGeralList.get(0);
			if(estoqueGeral.getQtde() != null && estoqueGeral.getQtde() > 0 && isIncluirSaldoEstoque()) {
				throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MATERIAL_POSSUI_SALDO);
			}
		}
		
	}
	
	public boolean isIncluirSaldoEstoque() throws ApplicationBusinessException {
		boolean parametro = false;
	    AghParametros parametroBotaoCancelar = getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_ESTOQUE_INCLUSAO_SALDO_INICIAL);
	    String valorParametroBotaoCancelar = parametroBotaoCancelar.getVlrTexto();
	    parametro = "S".equalsIgnoreCase(valorParametroBotaoCancelar);
		return parametro;		
	}

	

	/** GET **/
	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}
	
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

}
