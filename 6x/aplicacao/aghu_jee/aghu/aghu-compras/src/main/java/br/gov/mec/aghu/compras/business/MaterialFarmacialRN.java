package br.gov.mec.aghu.compras.business;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMedicamento;
import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.cadastroapoio.business.IFarmaciaApoioFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceAlmoxarifado;
import br.gov.mec.aghu.model.ScoGrupoMaterial;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.ScoMaterialJN;
import br.gov.mec.aghu.model.ScoUnidadeMedida;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;

/**
 * Triggers de:<br/>
 * @ORADB: <code>SCO_MATERIAIS</code>
 * @author gandriotti
 *
 */
@SuppressWarnings({"PMD.CyclomaticComplexity","PMD.HierarquiaONRNIncorreta"})
@Stateless
public class MaterialFarmacialRN extends AbstractAGHUCrudRn<ScoMaterial> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8597291518482102574L;
	public static final String UNIDADE = "UN";
	public static final Short LOCAL = 1;
	private static final Log LOG = LogFactory.getLog(MaterialFarmacialRN.class);

	@EJB
	private IFarmaciaApoioFacade farmaciaApoioFacade;
	
	@EJB
	private IFarmaciaFacade farmaciaFacade;

	@EJB	
	private IFaturamentoFacade faturamentoFacade;

	@EJB	
	private IParametroFacade parametroFacade;

	@EJB	
	private IEstoqueFacade estoqueFacade;
	
	@EJB	
	private IComprasFacade comprasFacade;	

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	private enum MaterialFarmaciaRNExceptionCode implements BusinessExceptionCode {
		AFA_00169;
	}

	public Date getDataCriacao() {
		return Calendar.getInstance().getTime(); 
	}
	
	protected void inicializarBRIeBRU(ScoMaterial material) throws BaseException {
		setServidor(material);
		material.setServidorAlteracao(material.getServidor());
		material.setServidorDesativado(material.getServidor());
		material.setServidorCadSapiens(material.getServidor());
		String nomeMaterial = AbstractAGHUCrudRn.adequarTextoObrigatorio(material.getNome(), true);
		material.setNome(nomeMaterial);
	}

	@Override
	public boolean briPreInsercaoRow(ScoMaterial material,
			String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws BaseException {
		inicializarBRIeBRU(material);
		material.setDtDigitacao(this.getDataCriacao());
		material.setIndEnvioProdInterna(DominioSimNao.N);
		return true;
	}

	@Override
	public boolean bruPreAtualizacaoRow(ScoMaterial materialOld,
			ScoMaterial material, String nomeMicrocomputador,
			Date dataFimVinculoServidor)
			throws BaseException {
		inicializarBRIeBRU(material);		
		
		if (DominioSituacao.A.equals(materialOld.getIndSituacao()) 
				&& DominioSituacao.I.equals(material.getIndSituacao())) {
			material.setDtDesativacao(this.getDataCriacao());

		} else if (DominioSituacao.I.equals(materialOld.getIndSituacao())
				&& DominioSituacao.A.equals(material.getIndSituacao())) {
			material.setDtDesativacao(null);
		}

		if (!material.getNome().equals(materialOld.getNome()) 
				&& material.getAfaMedicamento() != null) {
	
			material.getAfaMedicamento().setIndRevisaoCadastro(true);
		}
		return true;
	}

	protected BigDecimal getValNumAghuParamMatMed() throws ApplicationBusinessException {

		AghParametros parametro = null;
		BigDecimal valNumMatMed = null;

		parametro = this.getParametroFacade().buscarAghParametro(AghuParametrosEnum.P_GR_MAT_MEDIC);
		valNumMatMed = parametro.getVlrNumerico();

		return valNumMatMed;
	}

	@Override
	public boolean bsiPreInsercaoStatement(ScoMaterial material, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		BigDecimal valNumMatMed = getValNumAghuParamMatMed();		
		if (valNumMatMed == null) {
			throw new IllegalArgumentException();
		}

		if (material.getGrupoMaterial() == null) {
			ScoGrupoMaterial grupoMaterial = getComprasFacade().obterGrupoMaterialPorId(valNumMatMed.intValue());
			material.setGrupoMaterial(grupoMaterial);
		}
		
		ScoUnidadeMedida unidMedida = getComprasFacade().obterUnidadeMedidaPorId(MaterialFarmacialRN.UNIDADE);
		material.setUnidadeMedida(unidMedida);
		
		if (material.getAlmoxarifado() == null) {
			SceAlmoxarifado almoxarifado = getEstoqueFacade().obterAlmoxarifadoPorId(MaterialFarmacialRN.LOCAL);
			material.setAlmoxarifado(almoxarifado);
		}

		return true;
	}

	@Override
	public boolean aruPosAtualizacaoRow(ScoMaterial materialOld,
			ScoMaterial material, String nomeMicrocomputador,
			Date dataFimVinculoServidor)
			throws BaseException {
		if (this.verificarDiferenca(materialOld, material)) {
			RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
			
			//ScoMaterialJN materialJN = new ScoMaterialJN(materialOld);
			ScoMaterialJN materialJN = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.UPD, ScoMaterialJN.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
			materialJN.doSetPropriedades(materialOld);
			
			//materialJN.setOperacao(DominioOperacoesJournal.UPD);
			//materialJN.setJnUser("");
			//materialJN.setJnDateTime(new Date());

			if (materialOld.getNcmCodigo() != null) {
				materialJN.setCodNcm(Integer.parseInt(materialOld.getNcmCodigo()));
			} else {
				materialJN.setCodNcm(null);
			}
			materialJN.setNomeEstacao("");
			this.insertMaterialJN(materialJN);

			AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamentoPorMatCodigo(material.getAfaMedicamento().getCodigo());
			if (medicamento != null) {
				medicamento.setIndRevisaoCadastro(Boolean.TRUE);
				this.getFarmaciaApoioFacade().efetuarAlteracao(medicamento, nomeMicrocomputador, new Date());
			}

			return true;
		}

		return true;
	}

	@Override
	public boolean ariPosInsercaoRow(ScoMaterial material, String nomeMicrocomputador, final Date dataFimVinculoServidor) throws BaseException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		//ScoMaterialJN materialJN = new ScoMaterialJN(material);
		ScoMaterialJN materialJN = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.INS, ScoMaterialJN.class, servidorLogado != null ? servidorLogado.getUsuario() : null);
		materialJN.doSetPropriedades(material);
		
		//materialJN.setOperacao(DominioOperacoesJournal.INS);
		//materialJN.setJnUser("");
		//materialJN.setJnDateTime(new Date());
		materialJN.setCodNcm(null);
		materialJN.setNomeEstacao("");
		this.insertMaterialJN(materialJN);
		return true;
	}

	private String obterDescricaoEtiqueta(final ScoMaterial material) {

		if (material.getNome().length() > 18) {
			return material.getNome().substring(0, 18);
		}
		return material.getNome();
	}

	protected AfaMedicamento inicializarMedicamento(final ScoMaterial material) {

		AfaMedicamento medicamento = new AfaMedicamento();

		medicamento.setMatCodigo(material.getCodigo());
		medicamento.setDescricao(material.getNome());
		medicamento.setCriadoEm(new Date());
		medicamento.setIndSituacao(DominioSituacaoMedicamento.P);
		medicamento.setScoMaterial(material);
		medicamento.setDescricaoEtiqueta(this.obterDescricaoEtiqueta(material));

		medicamento.setIndCalcDispensacaoFracionad(Boolean.FALSE);
		medicamento.setIndPadronizacao(Boolean.FALSE);
		medicamento.setIndPermiteDoseFracionada(Boolean.FALSE);
		medicamento.setIndSobraReaproveitavel(Boolean.FALSE);
		medicamento.setIndExigeObservacao(Boolean.FALSE);
		medicamento.setIndRevisaoCadastro(Boolean.FALSE);
		medicamento.setIndDiluente(Boolean.FALSE);
		medicamento.setIndGeraDispensacao(Boolean.TRUE);
		medicamento.setIndUnitariza(Boolean.FALSE);
		medicamento.setIndInteresseCcih(Boolean.FALSE);
		medicamento.setIndGeladeira(Boolean.FALSE);
		medicamento.setIndControlado(Boolean.FALSE);
		medicamento.setPermitePrescricaoAmbulatorial(Boolean.TRUE);
		medicamento.setPermitePrescricaoEnfermeiroObstetra(Boolean.FALSE);
		
		return medicamento;
	}
	
	protected FatProcedHospInternos inicializarProcedimentoHospitalarInterno(
			final ScoMaterial material) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		FatProcedHospInternos fatProcedHospInterno = new FatProcedHospInternos();
		
		fatProcedHospInterno.setMaterial(material);
		fatProcedHospInterno.setDescricao(material.getNome());
		fatProcedHospInterno.setSituacao(material.getIndSituacao());
		fatProcedHospInterno.setServidor(servidorLogado);
		fatProcedHospInterno.setCriadoEm(new Date());
		fatProcedHospInterno.setIndOrigemPresc(true);
		fatProcedHospInterno.setIndUtilizaKit(true);
		
		return fatProcedHospInterno;
		
	}

	@Override
	public boolean asiPosInsercaoStatement(ScoMaterial material,
			String nomeMicrocomputador,
			Date dataFimVinculoServidor) throws BaseException {
		
		BigDecimal valMatMed = null;
		AfaMedicamento medicamento = null;

		valMatMed = this.getValNumAghuParamMatMed();

		if (material.getGrupoMaterial().getCodigo().equals(valMatMed.intValue())) {
			//TODO: ESTE CÓDIGO DEVERÁ SER MOVIDO DAQUI QUANDO O RESTO DO CÓDIGO
			//DESTA FUNCIONALIDADE FOR MIGRADO.
			medicamento = this.inicializarMedicamento(material);
			this.getFarmaciaApoioFacade().efetuarInclusao(medicamento, nomeMicrocomputador, new Date());
			
			FatProcedHospInternos fatProcedHospInterno = inicializarProcedimentoHospitalarInterno(material);
			this.getFaturamentoFacade().inserirFatProcedHospInternos(fatProcedHospInterno);
		}	

		return true;
	}
	
	

	/**
	 * Insere uma via de administracao de medicamento na sua respectiva
	 * "journal"
	 * 
	 * @param materialJN
	 * @return
	 * @throws ApplicationBusinessException
	 */
	public ScoMaterialJN insertMaterialJN(final ScoMaterialJN materialJN) throws ApplicationBusinessException {

		return getComprasFacade().inserirScoMaterialJn(materialJN);
	}
	
	private IComprasFacade getComprasFacade() {
		return comprasFacade;
	}

	protected void setServidor(final ScoMaterial material) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		if (servidorLogado == null) {
			throw new ApplicationBusinessException(MaterialFarmaciaRNExceptionCode.AFA_00169);
		}
		material.setServidor(servidorLogado);
	}

	private boolean verificarDiferenca(final ScoMaterial materialOld, final ScoMaterial material) {

		if (!CoreUtil.igual(materialOld.getServidor(), material.getServidor())
				|| !CoreUtil.igual(materialOld.getNome(), material.getNome())
				|| !CoreUtil.igual(materialOld.getIndSituacao(), material.getIndSituacao())
				|| !CoreUtil.igual(materialOld.getIndSituacao(), material.getIndSituacao())
				|| !CoreUtil.igual(materialOld.getIndSituacao(), material.getIndSituacao())
				|| !CoreUtil.igual(materialOld.getIndSituacao(), material.getIndSituacao())
				|| !CoreUtil.igual(materialOld.getCodigo(), material.getCodigo())
				|| !CoreUtil.igual(materialOld.getUmdCodigo(), material.getUmdCodigo())
				|| !CoreUtil.igual(materialOld.getServidor(), material.getServidor())
				|| !CoreUtil.igual(materialOld.getServidorDesativado(), material.getServidorDesativado())
				|| !CoreUtil.igual(materialOld.getDescricao(), material.getDescricao())
				|| !CoreUtil.igual(materialOld.getDtDigitacao(), material.getDtDigitacao())
				|| !CoreUtil.igual(materialOld.getIndSituacao(), material.getIndSituacao())
				|| !CoreUtil.igual(materialOld.getIndEstocavel(), material.getIndEstocavel())
				|| !CoreUtil.igual(materialOld.getIndGenerico(), material.getIndGenerico())
				|| !CoreUtil.igual(materialOld.getIndMenorPreco(), material.getIndMenorPreco())
				|| !CoreUtil.igual(materialOld.getIndProducaoInterna(), material.getIndProducaoInterna())
				|| !CoreUtil.igual(materialOld.getIndAtuQtdeDisponivel(), material.getIndAtuQtdeDisponivel())
				|| !CoreUtil.igual(materialOld.getOrigemParecerTecnico() == null ? null : materialOld.getOrigemParecerTecnico().getCodigo(), material.getOrigemParecerTecnico() == null ? null : material.getOrigemParecerTecnico().getCodigo())
				|| !CoreUtil.igual(materialOld.getClassifXyz(), material.getClassifXyz())
				|| !CoreUtil.igual(materialOld.getSazonalidade(), material.getSazonalidade())
				|| !CoreUtil.igual(materialOld.getNome(), material.getNome())
				|| !CoreUtil.igual(materialOld.getObservacao(), material.getObservacao())
				|| !CoreUtil.igual(materialOld.getDtAlteracao(), material.getDtAlteracao())
				|| !CoreUtil.igual(materialOld.getDtDesativacao(), material.getDtDesativacao())
				|| !CoreUtil.igual(materialOld.getGrupoMaterial(), material.getGrupoMaterial())
				|| !CoreUtil.igual(materialOld.getIndControleValidade(), material.getIndControleValidade())
				|| !CoreUtil.igual(materialOld.getAlmoxarifado() == null ? null : materialOld.getAlmoxarifado().getSeq(), material.getAlmoxarifado() == null ? null : material.getAlmoxarifado().getSeq())
				|| !CoreUtil.igual(materialOld.getIndFaturavel(), material.getIndFaturavel())
				|| !CoreUtil.igual(materialOld.getIndEnvioProdInterna(), material.getIndEnvioProdInterna())
				|| !CoreUtil.igual(materialOld.getAlmLocalEstq(), material.getAlmLocalEstq())
				|| !CoreUtil.igual(materialOld.getServidorAlteracao(), material.getServidorAlteracao())
				|| !CoreUtil.igual(materialOld.getIndPadronizado(), material.getIndPadronizado())
				|| !CoreUtil.igual(materialOld.getIndCcih(), material.getIndCcih())
				|| !CoreUtil.igual(materialOld.getIndControleLote(), material.getIndControleLote())
				|| !CoreUtil.igual(materialOld.getIndFotosensivel(), material.getIndFotosensivel())
				|| !CoreUtil.igual(materialOld.getIndTipoUso(), material.getIndTipoUso())
				|| !CoreUtil.igual(materialOld.getNumero(), material.getNumero())
				|| !CoreUtil.igual(materialOld.getIndCadSapiens(), material.getIndCadSapiens())
				|| !CoreUtil.igual(materialOld.getDtCadSapiens(), material.getDtCadSapiens())
				|| !CoreUtil.igual(materialOld.getServidorCadSapiens(), material.getServidorCadSapiens())
				|| !CoreUtil.igual(materialOld.getCodRetencao(), material.getCodRetencao())
				|| !CoreUtil.igual(materialOld.getCodTransacaoSapiens(), material.getCodTransacaoSapiens())
				|| !CoreUtil.igual(materialOld.getCodSiasg(), material.getCodSiasg())
				|| !CoreUtil.igual(materialOld.getCodSitTribSapiens(), material.getCodSitTribSapiens())
				|| !CoreUtil.igual(materialOld.getNcmCodigo(), material.getNcmCodigo())) {

			return true;
		}

		return false;
	}

	protected IFarmaciaApoioFacade getFarmaciaApoioFacade() {
		return farmaciaApoioFacade;
	}
	
	protected IFarmaciaFacade getFarmaciaFacade() {
		return farmaciaFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IParametroFacade getParametroFacade() {
		return parametroFacade;
	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
}
