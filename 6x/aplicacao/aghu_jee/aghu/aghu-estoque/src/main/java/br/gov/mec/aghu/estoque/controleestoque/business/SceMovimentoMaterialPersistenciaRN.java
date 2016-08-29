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
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.estoque.dao.SceEstoqueAlmoxarifadoDAO;
import br.gov.mec.aghu.estoque.dao.SceEstoqueGeralDAO;
import br.gov.mec.aghu.estoque.dao.SceTipoMovimentosDAO;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceEstoqueAlmoxarifado;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.SceMovimentoMaterial;
import br.gov.mec.aghu.model.SceTipoMovimento;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings("ucd")
@Stateless
public class SceMovimentoMaterialPersistenciaRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(SceMovimentoMaterialPersistenciaRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	
	@EJB
	private IEstoqueFacade estoqueFacade;
	
	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	
	@EJB
	private IParametroFacade parametroFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
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

	public void persistir(SceMovimentoMaterial elemento)
			throws BaseException {
		this.inserir(elemento);
		this.flush();
	}

	protected void inserir(SceMovimentoMaterial elemento)
			throws BaseException {
		this.preInserir(elemento);
		this.getEstoqueFacade().persistirSceMovimentoMaterial(elemento);
	}

	// incluir tag ORADB TRIGGER
	protected void preInserir(SceMovimentoMaterial elemento)
			throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		elemento.setServidor(servidorLogado);
		this.atribuirTipoMovimento(elemento);// RN1
		this.verificarEstoqueAlmoxarifadoCadastradoMaterial(elemento);// RN2
		this.verificarCustoMedioMaterialEstoque(elemento);//RN3
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
		
		List<SceEstoqueGeral> estoqueGeralList = this.getSceEstoqueGeralDAO()
			.pesquisarEstoqueGeralPorMaterialDataCompetencia(
					elemento.getMaterial(), dtCompetencia);
		
		if(estoqueGeralList.isEmpty()) {
			throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.SCE_00649);
		} else {
			final SceEstoqueGeral estoqueGeral = estoqueGeralList.get(0);
			if(estoqueGeral.getQtde() != null && estoqueGeral.getQtde() > 0) {
				throw new ApplicationBusinessException(SceMovimentoMaterialRNExceptionCode.MATERIAL_POSSUI_SALDO);
			}
		}
		
	}
	

	/** GET **/
	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected SceTipoMovimentosDAO getSceTipoMovimentosDAO() {
		return sceTipoMovimentosDAO;
	}

	protected SceEstoqueAlmoxarifadoDAO getSceEstoqueAlmoxarifadoDAO() {
		return sceEstoqueAlmoxarifadoDAO;
	}

	protected SceEstoqueGeralDAO getSceEstoqueGeralDAO() {
		return sceEstoqueGeralDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
